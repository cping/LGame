package loon.an;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import loon.Assets;
import loon.LRelease;
import loon.LSystem;
import loon.Sound;
import loon.canvas.Image;
import loon.canvas.ImageImpl;
import loon.opengl.TextureSource;
import loon.utils.Scale;
import loon.utils.StringUtils;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;

public class JavaANAssets extends Assets {

	public static final URL convertURL(String url) throws Exception {
		return convertURI(url).toURL();
	}

	public static final URI convertURI(String url) {
		return URI.create(url);
	}

	private final static String DEF_RES = "assets/";

	public static interface Resource extends LRelease {

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

	public static class ClassRes extends DataRes implements Resource {

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
					return (in = getMainClass().getResourceAsStream(path));
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

	public static class FileRes extends DataRes implements Resource {

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

	public static class RemoteRes extends DataRes implements Resource {

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

	public static class SDRes extends DataRes implements Resource {

		@SuppressWarnings("deprecation")
		public SDRes(String path) {
			if (isMoutedSD()) {
				File file = null;
				try {
					file = android.os.Environment.getExternalStorageDirectory();
				} catch (Exception e) {
					file = (((JavaANGame) LSystem.base())).mainPlatform.getContext().getExternalFilesDir(null);
				}
				if (file == null) {
					file = (((JavaANGame) LSystem.base())).mainPlatform.getContext().getExternalFilesDir(null);
				}
				String tmp = file.getPath();
				if (StringUtils.startsWith(path, '/')) {
					path = path.substring(1);
				}
				if (!StringUtils.endsWith(tmp, '/')) {
					path = tmp + "/" + path;
				} else {
					path = tmp + path;
				}
			} else {
				path = (((JavaANGame) LSystem.base())).mainPlatform.getDir().getAbsolutePath();
				path = StringUtils.replaceIgnoreCase(path, "\\", "/");
				if (StringUtils.startsWith(path, '/') || StringUtils.startsWith(path, '\\')) {
					path = path.substring(1, path.length());
				}
			}
			this.path = path;
			this.name = "sdcard://" + path;
		}

		public final static boolean isMoutedSD() {
			String sdState = android.os.Environment.getExternalStorageState();
			return sdState.equals(android.os.Environment.MEDIA_MOUNTED);
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

	public static Resource classRes(String path) {
		return new ClassRes(path);
	}

	public static Resource fileRes(String path) {
		return new FileRes(path);
	}

	public static Resource remoteRes(String path) {
		return new RemoteRes(path);
	}

	public static Resource sdRes(String path) {
		return new SDRes(path);
	}

	private InputStream filestream(final String path) throws IOException {
		try {
			File file = new File(path);
			if (file.exists()) {
				return new FileInputStream(file);
			} else {
				file = new File(StringUtils.replaceIgnoreCase(getPath(path), DEF_RES, ""));
				if (file.exists()) {
					return new FileInputStream(file);
				} else {
					return getMainClass().getResourceAsStream(path);
				}
			}
		} catch (Throwable t) {
			return openAsset(path);
		}
	}

	public InputStream strRes(final String path) {
		if (path == null) {
			return null;
		}
		InputStream in = null;
		try {
			in = filestream(path);
		} catch (Exception ex) {
		}
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

	public class BitmapOptions extends BitmapFactory.Options {

		public Scale scale;
	}

	public interface BitmapOptionsAdjuster {

		void adjustOptions(String path, BitmapOptions options);
	}

	private final JavaANGame game;
	private final AssetManager assetMgr;

	private Scale assetScale = null;

	private BitmapOptionsAdjuster optionsAdjuster = new BitmapOptionsAdjuster() {
		public void adjustOptions(String path, BitmapOptions options) {
		}
	};

	public JavaANAssets(JavaANGame game) {
		super(game.asyn());
		this.game = game;
		this.assetMgr = game.mainPlatform.getResAssets();
		this.setPathPrefix("");
	}

	public void setAssetScale(float scaleFactor) {
		this.assetScale = new Scale(scaleFactor);
	}

	public void setBitmapOptionsAdjuster(BitmapOptionsAdjuster optionsAdjuster) {
		this.optionsAdjuster = optionsAdjuster;
	}

	@Override
	public Image getRemoteImage(final String url, int width, int height) {
		final ImageImpl image = createImage(true, width, height, url);
		asyn.invokeAsync(new Runnable() {
			public void run() {
				try {
					BitmapOptions options = createOptions(url, Scale.ONE);
					Bitmap bmp = downloadBitmap(url, options);
					image.succeed(new ImageImpl.Data(options.scale, bmp, bmp.getWidth(), bmp.getHeight()));
				} catch (Exception error) {
					image.fail(error);
				}
			}
		});
		return image;
	}

	protected JavaANAudio _audio;

	@Override
	public Sound getSound(String path) {
		if (_audio == null) {
			_audio = new JavaANAudio(game);
		}
		return _audio.createSound(path);
	}

	@Override
	public Sound getMusic(String path) {
		if (_audio == null) {
			_audio = new JavaANAudio(game);
		}
		return _audio.createMusic(path);
	}

	@Override
	public String getTextSync(String path) throws Exception {
		InputStream is = openAsset(path);
		try {
			StringBuilder fileData = new StringBuilder(1000);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
			}
			reader.close();
			return fileData.toString();
		} finally {
			is.close();
		}
	}

	@Override
	public byte[] getBytesSync(String path) throws Exception {
		InputStream is = openAsset(path);
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			while (true) {
				int r = is.read(buf);
				if (r == -1) {
					break;
				}
				out.write(buf, 0, r);
			}
			return out.toByteArray();
		} finally {
			is.close();
		}
	}

	@Override
	protected ImageImpl createImage(boolean async, int rwid, int rhei, String source) {
		return new JavaANImage(game, async, rwid, rhei, source);
	}

	@Override
	protected ImageImpl.Data load(String path) throws Exception {
		if (path == null || TextureSource.RenderCanvas.equals(path)) {
			return null;
		}
		Exception error = null;
		for (Scale.ScaledResource rsrc : assetScale().getScaledResources(path)) {
			try {
				InputStream is = openAsset(rsrc.path);
				try {
					BitmapOptions options = createOptions(path, rsrc.scale);
					Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
					return new ImageImpl.Data(options.scale, bitmap, bitmap.getWidth(), bitmap.getHeight());
				} finally {
					is.close();
				}
			} catch (FileNotFoundException ex) {
				error = ex;
			} catch (Exception e) {
				error = e;
				break;
			}
		}
		game.log().warn("Could not load image: " + pathPrefix + path, error);
		throw error != null ? error : new FileNotFoundException(path);
	}

	Typeface getTypeface(String path) {
		return Typeface.createFromAsset(assetMgr, getPath(path));
	}

	protected AssetFileDescriptor openAssetFd(String path) throws IOException {
		String fullPath = getPath(path);
		return assetMgr.openFd(fullPath);
	}

	protected Scale assetScale() {
		return (assetScale != null) ? assetScale : game.graphics().scale();
	}

	protected InputStream openAsset(String path) throws IOException {
		String newPath = getPath(path);
		InputStream is = openResource(newPath);
		if (is == null) {
			is = assetMgr.open(newPath, AssetManager.ACCESS_STREAMING);
		}
		if (is == null) {
			throw new FileNotFoundException("not found resource: " + newPath);
		}
		return is;
	}

	public InputStream openResource(String resName) throws IOException {
		InputStream resource = strRes(resName);
		if (resource != null) {
			return resource;
		}
		if (resName.indexOf('\\') != -1) {
			resName = resName.replace('\\', '/');
		}
		String fileName = resName.toLowerCase();
		if (fileName.startsWith(DEF_RES) || fileName.startsWith('/' + DEF_RES)) {
			boolean flag = resName.startsWith("/");
			String file;
			if (flag) {
				file = resName.substring(1);
			} else {
				file = resName;
			}
			int index = file.indexOf('/') + 1;
			if (index != -1) {
				file = resName.substring(index);
			} else {
				int length = file.length();
				int size = file.lastIndexOf('/', 0) + 1;
				if (size < length) {
					file = file.substring(size, length);
				}
			}
			return this.assetMgr.open(file);
		}
		if (getMainClass() != null) {
			InputStream in = null;
			try {
				in = getMainClass().getResourceAsStream(resName);
			} catch (Exception e) {
			}
			return in;
		} else {
			return this.assetMgr.open(resName);
		}
	}

	private final static Class<?> getMainClass() {
		return LSystem.base() == null ? JavaANApplication.class : LSystem.base().setting.mainClass;
	}

	protected BitmapOptions createOptions(String path, Scale scale) {
		BitmapOptions options = new BitmapOptions();
		options.inScaled = false;
		options.inMutable = true;
		options.inPreferredConfig = ((JavaANGraphics) game.graphics()).preferredBitmapConfig;
		options.scale = scale;
		optionsAdjuster.adjustOptions(path, options);
		return options;
	}

	protected Bitmap downloadBitmap(String url, BitmapOptions options) throws Exception {
		try {
			URL imageurl = convertURL(url);
			HttpURLConnection connection = (HttpURLConnection) imageurl.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(false);
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			connection.setUseCaches(true);
			connection.connect();
			int responseCode = connection.getResponseCode();
			if (responseCode == 200) {
				InputStream in = connection.getInputStream();
				try {
					return BitmapFactory.decodeStream(in, null, options);
				} finally {
					if (in != null) {
						in.close();
					}
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		} catch (Exception e) {
			game.reportError("bitmap from " + url, e);
			throw e;
		}
		return null;
	}
}
