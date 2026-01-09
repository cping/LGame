/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.cport;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import loon.Assets;
import loon.LRelease;
import loon.LSystem;
import loon.Sound;
import loon.canvas.Image;
import loon.canvas.ImageImpl;
import loon.canvas.ImageImpl.Data;
import loon.canvas.Pixmap;
import loon.cport.bridge.SDLCall;
import loon.cport.bridge.STBImage;
import loon.opengl.TextureSource;
import loon.utils.MathUtils;
import loon.utils.PathUtils;
import loon.utils.Scale;
import loon.utils.StringUtils;

public class CAssets extends Assets {

	public static final URL convertURL(String url) throws Exception {
		return convertURI(url).toURL();
	}

	public static final URI convertURI(String url) {
		return URI.create(url);
	}

	public static interface CResource extends LRelease {

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

	public static class ClassRes extends DataRes implements CResource {

		public ClassRes(String path) {
			this.path = path;
			this.name = "classpath://" + path;
		}

		@Override
		public InputStream getInputStream() {
			try {
				return in = new ByteArrayInputStream(SDLCall.LoadRWFileToBytes(path));
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
				return (uri = convertURI(path));
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if ((obj == null) || (getClass() != obj.getClass())) {
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

	public static class FileRes extends DataRes implements CResource {

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
				return in = new ByteArrayInputStream(SDLCall.LoadRWFileToBytes(path));
			} catch (Exception e) {
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
				return (uri = convertURI(path));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if ((obj == null) || (getClass() != obj.getClass())) {
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

	public static class RemoteRes extends DataRes implements CResource {

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
				return in = convertURL(path).openStream();
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
				return convertURI(path);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if ((obj == null) || (getClass() != obj.getClass())) {
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

	public static class SDRes extends DataRes implements CResource {

		public SDRes(String path) {
			path = StringUtils.replaceIgnoreCase(path, "\\", "/");
			if (StringUtils.startsWith(path, '/') || StringUtils.startsWith(path, '\\')) {
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
				String org = "loon";
				String app = LSystem.getSystemAppName();
				String prefPath = SDLCall.getPrefPath(org, app);
				return in = new ByteArrayInputStream(
						SDLCall.LoadRWFileToBytes(PathUtils.getCombinePaths(prefPath, path)));
			} catch (Exception e) {
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
				return (uri = convertURI(path));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if ((obj == null) || (getClass() != obj.getClass())) {
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

	public static CResource classRes(String path) {
		return new ClassRes(path);
	}

	public static CResource fileRes(String path) {
		return new FileRes(path);
	}

	public static CResource remoteRes(String path) {
		return new RemoteRes(path);
	}

	public static CResource sdRes(String path) {
		return new SDRes(path);
	}

	private InputStream filestream(String path) {
		try {
			File file = new File(path);
			if (file.exists()) {
				return new FileInputStream(file);
			} else {
				file = new File(StringUtils.replaceIgnoreCase(getPath(path), LSystem.getPathPrefix(), ""));
				if (file.exists()) {
					return new FileInputStream(file);
				} else {
					return new ByteArrayInputStream(SDLCall.LoadRWFileToBytes(path));
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
				in = classRes(path.substring(6, path.length())).getInputStream();
			} else if (path.startsWith("path:")) {
				in = fileRes(path.substring(5, path.length())).getInputStream();
			} else if (path.startsWith("url:")) {
				in = remoteRes(path.substring(4, path.length())).getInputStream();
			}
		}
		return in;
	}

	private final CGame game;
	private File[] directories = {};

	private Scale assetScale = null;

	public CAssets(CGame game) {
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

	protected Scale assetScale() {
		return (assetScale != null) ? assetScale : game.graphics().scale();
	}

	@Override
	public Image getRemoteImage(final String url, int width, int height) {
		final CImage image = new CImage(game, true, width, height, url);
		asyn.invokeAsync(new Runnable() {
			@Override
			public void run() {
				try {
					STBImage stbimage = STBImage.createImage(SDLCall.downloadURL(url));
					Pixmap pixmap = new Pixmap(stbimage.getImagePixels32(), stbimage.getWidth(), stbimage.getHeight(),
							stbimage.getFormat() >= 4);
					image.succeed(new ImageImpl.Data(Scale.ONE, pixmap, pixmap.getWidth(), pixmap.getHeight()));
				} catch (Exception error) {
					image.fail(error);
				}
			}
		});
		return image;
	}

	protected final static boolean existsPath(final String path) {
		return SDLCall.fileExists(path) || SDLCall.rwFileExists(path);
	}

	protected final static File resolvePath(String path) {
		File file = new File(path);
		if (!file.exists()) {
			path = getPath(path);
			if (path.startsWith(LSystem.getSystemImagePath())) {
				path = LSystem.getPathPrefix() + path;
			}
			file = new File(path);
			if (!file.exists() && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
				file = new File(path.substring(path.indexOf('/') + 1, path.length()));
			}
			if (!file.exists() && (path.indexOf('\\') != -1 || path.indexOf('/') != -1)) {
				file = new File(LSystem.getFileName(path = file.getAbsolutePath()));
			}
			if (!file.exists()) {
				file = new File(LSystem.getFileName(path = (LSystem.getPathPrefix() + path)));
			}
		}
		return file;
	}

	protected String requirePath(final String path) throws IOException {
		if (existsPath(path)) {
			return path;
		}
		String serachPath = getPath(path);
		boolean notExists = !existsPath(serachPath);
		if (notExists && !path.startsWith("/")) {
			serachPath = "/" + getPath(path);
			notExists = existsPath(serachPath);
		}
		if (notExists && !path.startsWith("\\")) {
			serachPath = "\\" + getPath(path);
			notExists = existsPath(serachPath);
		}
		if (notExists) {
			serachPath = getPath(path);
			notExists = existsPath(serachPath);
		}
		if (!notExists) {
			return serachPath;
		} else {
			File file = resolvePath(serachPath);
			if (file.exists() || existsPath(serachPath)) {
				return serachPath;
			}
		}
		for (File dir : directories) {
			File f = new File(dir, path).getCanonicalFile();
			if (f.exists() || existsPath(serachPath)) {
				return serachPath;
			}
		}
		return path;
	}

	public Sound getSound(byte[] buffer) {
		return CAudio.createSound(buffer);
	}

	public Sound getMusic(byte[] buffer) {
		return CAudio.createMusic(buffer);
	}

	@Override
	public Sound getSound(String path) {
		return CAudio.createSound(path);
	}

	@Override
	public Sound getMusic(String path) {
		return CAudio.createMusic(path);
	}

	@Override
	public String getTextSync(String path) throws Exception {
		String newPath = requirePath(path);
		return SDLCall.loadRWFileToChars(newPath);
	}

	@Override
	public byte[] getBytesSync(String path) throws Exception {
		String newPath = requirePath(path);
		return SDLCall.LoadRWFileToBytes(newPath);
	}

	protected Pixmap scaleImage(Pixmap image, float viewImageRatio) {
		int swidth = MathUtils.iceil(viewImageRatio * image.getWidth());
		int sheight = MathUtils.iceil(viewImageRatio * image.getHeight());
		return Pixmap.getResize(image, swidth, sheight).scaleBicubic();
	}

	@Override
	protected Data load(String path) throws Exception {
		if (path == null || TextureSource.RenderCanvas.equals(path)) {
			return null;
		}
		Exception error = null;
		for (Scale.ScaledResource rsrc : assetScale().getScaledResources(path)) {
			try {
				STBImage stbImage = STBImage.createImage(path);
				Pixmap image = new Pixmap(stbImage.getImagePixels32(), stbImage.getWidth(), stbImage.getHeight());
				Scale viewScale = game.graphics().scale(), imageScale = rsrc.scale;
				float viewImageRatio = viewScale.factor / imageScale.factor;
				if (viewImageRatio < 1) {
					image = scaleImage(image, viewImageRatio);
					imageScale = viewScale;
				}
				return new ImageImpl.Data(imageScale, image, image.getWidth(), image.getHeight());
			} catch (Exception ex) {
				error = ex;
			}
		}
		game.log().warn("Could not load image: " + path + " [error=" + error + "]");
		throw error != null ? error : new FileNotFoundException(path);
	}

	@Override
	protected ImageImpl createImage(boolean async, int rawWidth, int rawHeight, String source) {
		return new CImage(game, async, rawWidth, rawHeight, source);
	}

}
