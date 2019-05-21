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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import loon.Assets;
import loon.LRelease;
import loon.LSystem;
import loon.Sound;
import loon.canvas.Image;
import loon.canvas.ImageImpl;
import loon.utils.MathUtils;
import loon.utils.Scale;
import loon.utils.StringUtils;

public class JavaSEAssets extends Assets {

	private final static String DEF_RES = "assets/";

	public static interface JavaSEResource extends LRelease {

		InputStream getInputStream();

		String getResourceName();

		URI getURI();
	}

	public static abstract class DataRes {

		String path;

		String name;

		InputStream in;

		URI uri;

		@Override
		public int hashCode() {
			return (name == null) ? super.hashCode() : name.hashCode();
		}

		public void close() {
			if (in != null) {
				try {
					in.close();
					in = null;
				} catch (IOException e) {
				}
			}
			if (uri != null) {
				uri = null;
			}
		}
	}

	public static class ClassRes extends DataRes implements JavaSEResource {

		private ClassLoader classLoader;

		public ClassRes(String path) {
			this(path, null);
		}

		public ClassRes(String path, ClassLoader classLoader) {
			this.path = path;
			this.name = "classpath://" + path;
			this.classLoader = classLoader;
		}

		@Override
		public InputStream getInputStream() {
			try {
				if (classLoader == null) {
					return (in = JavaSEAssets.classLoader
							.getResourceAsStream(path));
				} else {
					return (in = classLoader.getResourceAsStream(path));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public String getResourceName() {
			return name;
		}

		@Override
		public URI getURI() {
			try {
				if (uri != null) {
					return uri;
				}
				return (uri = classLoader.getResource(path).toURI());
			} catch (URISyntaxException ex) {
				throw new RuntimeException(ex);
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ClassRes other = (ClassRes) obj;
			if (name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!name.equals(other.name)) {
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

	}

	public static class FileRes extends DataRes implements JavaSEResource {

		public FileRes(String path) {
			this.path = path;
			this.name = "file://" + path;
		}

		@Override
		public InputStream getInputStream() {
			try {
				if (in != null) {
					return in;
				}
				File file = new File(path);
				return (in = new FileInputStream(file));
			} catch (FileNotFoundException e) {
				throw new RuntimeException("file " + name + " not found !", e);
			}
		}

		@Override
		public String getResourceName() {
			return name;
		}

		@Override
		public URI getURI() {
			try {
				if (uri != null) {
					return uri;
				}
				return (uri = new URL(path).toURI());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			FileRes other = (FileRes) obj;
			if (name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!name.equals(other.name)) {
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}
	}

	public static class RemoteRes extends DataRes implements JavaSEResource {

		public RemoteRes(String url) {
			this.path = url;
			this.name = url;
		}

		@Override
		public InputStream getInputStream() {
			try {
				if (in != null) {
					return in;
				}
				return in = new URL(path).openStream();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public String getResourceName() {
			return name;
		}

		@Override
		public URI getURI() {
			try {
				return new URL(path).toURI();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			RemoteRes other = (RemoteRes) obj;
			if (name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!name.equals(other.name)) {
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}
	}

	public static class SDRes extends DataRes implements JavaSEResource {

		public SDRes(String path) {
			path = StringUtils.replaceIgnoreCase(path, "\\", "/");
			if (StringUtils.startsWith(path, '/')
					|| StringUtils.startsWith(path, '\\')) {
				path = path.substring(1, path.length());
			}
			this.path = path;
			this.name = "sdcard://" + path;
		}

		@Override
		public InputStream getInputStream() {
			try {
				if (in != null) {
					return in;
				}
				return (in = new FileInputStream(new File(path)));
			} catch (FileNotFoundException e) {
				throw new RuntimeException("file " + name + " not found !", e);
			}
		}

		@Override
		public String getResourceName() {
			return name;
		}

		@Override
		public URI getURI() {
			try {
				if (uri != null) {
					return uri;
				}
				return (uri = new URL(path).toURI());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			SDRes other = (SDRes) obj;
			if (name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!name.equals(other.name)) {
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}
	}

	public static JavaSEResource classRes(String path) {
		return new ClassRes(path);
	}

	public static JavaSEResource fileRes(String path) {
		return new FileRes(path);
	}

	public static JavaSEResource remoteRes(String path) {
		return new RemoteRes(path);
	}

	public static JavaSEResource sdRes(String path) {
		return new SDRes(path);
	}

	private InputStream filestream(String path) {
		try {
			File file = new File(path);
			if (file.exists()) {
				return new FileInputStream(file);
			} else {
				file = new File(StringUtils.replaceIgnoreCase(getPath(path),
						DEF_RES, ""));
				if (file.exists()) {
					return new FileInputStream(file);
				} else {
					return classLoader.getResourceAsStream(path);
				}
			}
		} catch (Throwable t) {
			return null;
		}
	}

	public InputStream strRes(final String path) {
		if (path == null) {
			return null;
		}
		InputStream in = filestream(path);
		if (in != null) {
			return in;
		}
		if (path.indexOf("->") == -1) {
			if (path.startsWith("sd:")) {
				in = sdRes(path.substring(3, path.length())).getInputStream();
			} else if (path.startsWith("class:")) {
				in = classRes(path.substring(6, path.length()))
						.getInputStream();
			} else if (path.startsWith("path:")) {
				in = fileRes(path.substring(5, path.length())).getInputStream();
			} else if (path.startsWith("url:")) {
				in = remoteRes(path.substring(4, path.length()))
						.getInputStream();
			}
		}
		return in;
	}

	private final JavaSEGame game;
	private File[] directories = {};

	private Scale assetScale = null;

	public JavaSEAssets(JavaSEGame game) {
		super(game.asyn());
		this.game = game;
		JavaSEAssets.pathPrefix = "assets/";
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
		final JavaSEImage image = new JavaSEImage(game, true, width, height,
				url);
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
					return _audio.createSound(path, new ByteArrayInputStream(
							getBytesSync(soundPath)), music);
				} catch (Exception e) {
					e.printStackTrace();
					err = e;
				}
			}
		} else {
			try {
				return _audio.createSound(path, new ByteArrayInputStream(
						getBytesSync(path)), music);
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
			boolean isFile = url.getProtocol().equals("file");
			if (isFile) {
				File file = new File(URLDecoder.decode(url.getPath(),
						LSystem.ENCODING));
				if (file.exists()) {
					return new FileResource(this, file);
				} else {
					return new URLResource(this, serachPath);
				}
			} else {
				return new URLResource(this, serachPath);
			}
		} else {
			File file = resolvePath(serachPath);
			if (file.exists()) {
				return new FileResource(this, new File(serachPath));
			}
		}
		for (File dir : directories) {
			File f = new File(dir, path).getCanonicalFile();
			if (f.exists()) {
				return new FileResource(this, f);
			}
		}
		throw new FileNotFoundException(path);
	}

	protected final static File resolvePath(String path) {
		File file = new File(path);
		if (!file.exists()) {
			path = getPath(path);
			if (path.startsWith(LSystem.getSystemImagePath())) {
				path = DEF_RES + path;
			}
			file = new File(path);
			if (!file.exists()
					&& (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
				file = new File(path.substring(path.indexOf('/') + 1,
						path.length()));
			}
			if (!file.exists()
					&& (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
				file = new File(LSystem.getFileName(path = file
						.getAbsolutePath()));
			}
			if (!file.exists()) {
				file = new File(LSystem.getFileName(path = (DEF_RES + path)));
			}
		}
		return file;
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
			return new String(readBytes(), LSystem.ENCODING);
		}
	}

	protected static class URLResource extends Resource {
		public final String url;

		private JavaSEAssets assets;

		public URLResource(JavaSEAssets assets, String url) {
			this.url = url;
			this.assets = assets;
		}

		@Override
		public InputStream openStream() throws IOException {
			return assets.strRes(url);
		}

		@Override
		public BufferedImage readImage() throws IOException {
			return ImageIO.read(assets.strRes(url));
		}
	}

	protected static class FileResource extends Resource {

		public final File file;

		private JavaSEAssets assets;

		public FileResource(JavaSEAssets assets, File file) {
			this.assets = assets;
			this.file = file;
		}
		@Override
		public InputStream openStream() throws IOException {
			return assets.strRes(file.getPath());
		}
		@Override
		public BufferedImage readImage() throws IOException {
			return ImageIO.read(assets.strRes(file.getPath()));
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
		if (path == null || "<canvas>".equals(path)) {
			return null;
		}
		
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
