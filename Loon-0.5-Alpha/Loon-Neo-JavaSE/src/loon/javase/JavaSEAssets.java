/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.javase;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import loon.Assets;
import loon.LSystem;
import loon.Sound;
import loon.canvas.Image;
import loon.canvas.ImageImpl;
import loon.utils.MathUtils;
import loon.utils.Scale;

public class JavaSEAssets extends Assets {

	
	private final JavaSEGame game;
	private File[] directories = {};

	private Scale assetScale = null;

	public JavaSEAssets(JavaSEGame game) {
		super(game.asyn());
		this.game = game;
	}


	public void addDirectory(File dir) {
		File[] ndirs = new File[directories.length + 1];
		System.arraycopy(directories, 0, ndirs, 0, directories.length);
		ndirs[ndirs.length - 1] = dir;
		directories = ndirs;
	}

	public void setAssetScale(float scaleFactor) {
		this.assetScale = new Scale(scaleFactor);
	}

	@Override
	public Image getRemoteImage(final String url, int width, int height) {
		final JavaSEImage image = new JavaSEImage(game, true, width, height, url);
		asyn.invokeAsync(new Runnable() {
			public void run() {
				try {
					BufferedImage bmp = ImageIO.read(new URL(url));
					image.succeed(new ImageImpl.Data(Scale.ONE, bmp, bmp
							.getWidth(), bmp.getHeight()));
				} catch (Exception error) {
					image.fail(error);
				}
			}
		});
		return image;
	}

	@Override
	public Sound getSound(String path) {
		return getSound(path, false);
	}

	@Override
	public Sound getMusic(String path) {
		return getSound(path, true);
	}

	@Override
	public String getTextSync(String path) throws Exception {
		return requireResource(path).readString();
	}

	@Override
	public byte[] getBytesSync(String path) throws Exception {
		return requireResource(path).readBytes();
	}

	private static JavaSEAudio _audio;
	
	protected Sound getSound(String path, boolean music) {
		if (_audio == null) {
			_audio = new JavaSEAudio();
		}
		Exception err = null;
		String ext = LSystem.getExtension(path);
		if (ext == null || ext.length() == 0) {
			for (String suff : SUFFIXES) {
				final String soundPath = path + suff;
				try {
					return _audio.createSound(
							new ByteArrayInputStream(
									getBytesSync(soundPath)), music);
				} catch (Exception e) {
					e.printStackTrace();
					err = e;
				}
			}
		} else {
			try {
				return _audio.createSound(
						new ByteArrayInputStream(getBytesSync(path)),
						music);
			} catch (Exception e) {
				e.printStackTrace();
				err = e;
			}
		}
		return new Sound.Error(err);
	}


	static ClassLoader classLoader;

	static {
		try {
			classLoader = JavaSEAssets.class.getClassLoader();
		} catch (Exception e) {
			classLoader = Thread.currentThread().getContextClassLoader();
		}
	}

	protected Resource requireResource(String path) throws IOException {
		final String serachPath = getPath(path);
		URL url = classLoader.getResource(serachPath);
		if (url != null) {
			return url.getProtocol().equals("file") ? new FileResource(
					new File(url.getPath())) : new URLResource(url);
		} else {
			File file = new File(serachPath);
			if (file.exists()) {
				return new FileResource(file);
			}
		}
		for (File dir : directories) {
			File f = new File(dir, path).getCanonicalFile();
			if (f.exists()) {
				return new FileResource(f);
			}
		}
		throw new FileNotFoundException(path);
	}

	static byte[] toByteArray(InputStream in) throws IOException {
		try {
			byte[] buffer = new byte[512];
			int size = 0, read = 0;
			while ((read = in.read(buffer, size, buffer.length - size)) > 0) {
				size += read;
				if (size == buffer.length)
					buffer = Arrays.copyOf(buffer, size * 2);
			}
			if (size < buffer.length) {
				buffer = Arrays.copyOf(buffer, size);
			}
			return buffer;
		} finally {
			in.close();
		}
	}

	protected BufferedImage scaleImage(BufferedImage image, float viewImageRatio) {
		int swidth = MathUtils.iceil(viewImageRatio * image.getWidth());
		int sheight = MathUtils.iceil(viewImageRatio * image.getHeight());
		BufferedImage scaled = new BufferedImage(swidth, sheight,
				BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics2D gfx = scaled.createGraphics();
		gfx.drawImage(image.getScaledInstance(swidth, sheight,
				java.awt.Image.SCALE_SMOOTH), 0, 0, null);
		gfx.dispose();
		return scaled;
	}

	protected Scale assetScale() {
		return (assetScale != null) ? assetScale : game.graphics().scale();
	}

	abstract static class Resource {
		public abstract BufferedImage readImage() throws IOException;

		public abstract InputStream openStream() throws IOException;

		public AudioInputStream openAudioStream() throws Exception {
			return AudioSystem.getAudioInputStream(openStream());
		}

		public Font createFont() throws Exception {
			return Font.createFont(Font.TRUETYPE_FONT, openStream());
		}

		public byte[] readBytes() throws IOException {
			return toByteArray(openStream());
		}

		public String readString() throws Exception {
			return new String(readBytes(), "UTF-8");
		}
	}

	protected static class URLResource extends Resource {
		public final URL url;

		public URLResource(URL url) {
			this.url = url;
		}

		public InputStream openStream() throws IOException {
			return url.openStream();
		}

		public BufferedImage readImage() throws IOException {
			return ImageIO.read(url);
		}
	}

	protected static class FileResource extends Resource {
		public final File file;

		public FileResource(File file) {
			this.file = file;
		}

		public InputStream openStream() throws IOException {
			return new FileInputStream(file);
		}

		public BufferedImage readImage() throws IOException {
			return ImageIO.read(file);
		}

		@Override
		public AudioInputStream openAudioStream() throws Exception {
			return AudioSystem.getAudioInputStream(file);
		}

		@Override
		public Font createFont() throws Exception {
			return Font.createFont(Font.TRUETYPE_FONT, file);
		}

		@Override
		public byte[] readBytes() throws IOException {
			InputStream in = openStream();
			try {
				byte[] buffer = new byte[(int) file.length()]; 
				in.read(buffer);
				return buffer;
			} finally {
				in.close();
			}
		}
	}

	@Override
	protected ImageImpl.Data load(String path) throws Exception {
		Exception error = null;

		for (Scale.ScaledResource rsrc : assetScale().getScaledResources(path)) {
			try {
				BufferedImage image = requireResource(rsrc.path).readImage();
				Scale viewScale = game.graphics().scale(), imageScale = rsrc.scale;
				float viewImageRatio = viewScale.factor / imageScale.factor;
				if (viewImageRatio < 1) {
					image = scaleImage(image, viewImageRatio);
					imageScale = viewScale;
				}
				if (game.setting.convertImagesOnLoad) {
					BufferedImage convertedImage = JavaSEGraphics
							.convertImage(image);
					if (convertedImage != image) {
						game.log().debug(
								"Converted image: " + path + " [type="
										+ image.getType() + "]");
						image = convertedImage;
					}
				}
				return new ImageImpl.Data(imageScale, image, image.getWidth(),
						image.getHeight());
			} catch (FileNotFoundException ex) {
				error = ex;
			}
		}
		game.log().warn(
				"Could not load image: " + path + " [error=" + error + "]");
		throw error != null ? error : new FileNotFoundException(path);
	}

	@Override
	protected ImageImpl createImage(boolean async, int rwid, int rhei,
			String source) {
		return new JavaSEImage(game, async, rwid, rhei, source);
	}

}
