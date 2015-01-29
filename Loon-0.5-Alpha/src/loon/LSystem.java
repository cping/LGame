/**
 * Copyright 2008 - 2011
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
 * @email javachenpeng@yahoo.com.cn
 * @version 0.3.3
 */
package loon;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Random;

import loon.core.CallQueue;
import loon.core.event.Drawable;
import loon.core.event.Updateable;
import loon.core.geom.RectBox;
import loon.core.graphics.Screen;
import loon.core.graphics.opengl.FrameBuffer;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTexture.Format;
import loon.core.resource.Resources;
import loon.core.timer.SystemTimer;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

public final class LSystem {

	public static FrameBuffer newFrameBuffer(LTexture texture) {
		return new FrameBuffer(texture);
	}

	public static FrameBuffer newFrameBuffer(int width, int height,
			Format format) {
		return new FrameBuffer(format, width, height, false);
	}

	public static FrameBuffer newFrameBuffer(int width, int height) {
		return new FrameBuffer(Format.LINEAR, width, height, false);
	}

	public static Files files = null;

	public static Files files() {
		if (files == null) {
			files = new JavaSEFiles();
		}
		return files;
	}

	public static String getFileName(String name) {
		if (name == null) {
			return "";
		}
		int length = name.length();
		int size = name.lastIndexOf(LSystem.FS) + 1;
		if (size < length) {
			return name.substring(size, length);
		} else {
			return "";
		}
	}

	public static String getExtension(String name) {
		if (name == null) {
			return "";
		}
		int index = name.lastIndexOf(".");
		if (index == -1) {
			return "";
		} else {
			return name.substring(index + 1);
		}
	}

	public String getLanguage() {
		return java.util.Locale.getDefault().getLanguage();
	}

	public enum ApplicationType {
		Android, JavaSE, XNA, IOS, HTML5, PSM
	}

	public static CallQueue global_queue;

	public static ApplicationType type = ApplicationType.JavaSE;

	public static float EMULATOR_BUTTIN_SCALE = 1f;

	public final static int RESOLUTION_LOW = 0;

	public final static int RESOLUTION_MEDIUM = 1;

	public final static int RESOLUTION_HIGH = 2;

	public static int getResolutionType() {
		final int max = MathUtils.max(screenRect.width, screenRect.height);
		if (max < 480) {
			return RESOLUTION_LOW;
		} else if (max <= 800 && max >= 480) {
			return RESOLUTION_MEDIUM;
		} else {
			return RESOLUTION_HIGH;
		}
	}

	public final static void post(Updateable update) {
		if (global_queue != null) {
			global_queue.invokeLater(update);
		} else {
			LSystem.load(update);
		}
	}

	public final static int TRANSPARENT = 0xff000000;

	public static final int DEFAULT_BUFFER_SIZE = 8192;
	public static final byte[] EMPTY_BYTES = new byte[0];

	public static void copyStream(InputStream input, OutputStream output)
			throws IOException {
		copyStream(input, output, DEFAULT_BUFFER_SIZE);
	}

	public static void copyStream(InputStream input, OutputStream output,
			int bufferSize) throws IOException {
		byte[] buffer = new byte[bufferSize];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}

	public static byte[] copyStreamToByteArray(InputStream input)
			throws IOException {
		return copyStreamToByteArray(input, input.available());
	}

	public static byte[] copyStreamToByteArray(InputStream input,
			int estimatedSize) throws IOException {
		ByteArrayOutputStream baos = new OptimizedByteArrayOutputStream(
				Math.max(0, estimatedSize));
		copyStream(input, baos);
		return baos.toByteArray();
	}

	public static String copyStreamToString(InputStream input)
			throws IOException {
		return copyStreamToString(input, input.available());
	}

	public static String copyStreamToString(InputStream input,
			int approxStringLength) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		StringWriter w = new StringWriter(Math.max(0, approxStringLength));
		char[] buffer = new char[DEFAULT_BUFFER_SIZE];

		int charsRead;
		while ((charsRead = reader.read(buffer)) != -1) {
			w.write(buffer, 0, charsRead);
		}

		return w.toString();
	}

	static public class OptimizedByteArrayOutputStream extends
			ByteArrayOutputStream {

		public OptimizedByteArrayOutputStream(int initialSize) {
			super(initialSize);
		}

		@Override
		public synchronized byte[] toByteArray() {
			if (count == buf.length) {
				return buf;
			}
			return super.toByteArray();
		}

		public byte[] getBuffer() {
			return buf;
		}
	}

	public final static void close(LTexture tex2d) {
		if (tex2d != null) {
			try {
				tex2d.destroy();
				tex2d = null;
			} catch (Exception e) {
			}
		}
	}

	public final static void close(Closeable in) {
		if (in != null) {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 执行一个位于Screen线程中的Runnable
	 * 
	 * @param runnable
	 */
	public final static void callScreenRunnable(Runnable runnable) {
		LProcess process = LSystem.screenProcess;
		if (process != null) {
			Screen screen = process.getScreen();
			if (screen != null) {
				synchronized (screen) {
					screen.callEvent(runnable);
				}
			}
		}
	}

	public final static void load(Updateable u) {
			LProcess process = LSystem.screenProcess;
			if (process != null) {
				process.addLoad(u);
			}
	}

	public final static void unload(Updateable u) {
			LProcess process = LSystem.screenProcess;
			if (process != null) {
				process.addUnLoad(u);
			}
	}

	public final static void clearUpdate() {
		LProcess process = LSystem.screenProcess;
		if (process != null) {
			process.removeAllDrawing();
		}
	}

	public final static void drawing(Drawable d) {
		LProcess process = LSystem.screenProcess;
		if (process != null) {
			process.addDrawing(d);
		}
	}

	public final static void clearDrawing() {
		LProcess process = LSystem.screenProcess;
		if (process != null) {
			process.removeAllDrawing();
		}
	}

	// 框架名
	final static public String FRAMEWORK = "loon";

	// 包内默认的图片路径
	final static public String FRAMEWORK_IMG_NAME = "assets/loon_";

	// 框架版本信息
	final static public String VERSION = "0.5";

	// 秒
	final static public long SECOND = 1000;

	// 分
	final static public long MINUTE = SECOND * 60;

	// 小时
	final static public long HOUR = MINUTE * 60;

	// 天
	final static public long DAY = HOUR * 24;

	// 周
	final static public long WEEK = DAY * 7;

	// 理论上一年
	final static public long YEAR = DAY * 365;

	// 随机数
	final static public Random random = new Random();

	// 默认编码格式
	final static public String encoding = "UTF-8";

	// 行分隔符
	final static public String LS = System.getProperty("line.separator", "\n");

	// 文件分割符
	final static public String FS = System.getProperty("file.separator", "\\");

	// 默认的最大窗体宽（横屏）
	public static int MAX_SCREEN_WIDTH = 480;

	// 默认的最大窗体高（横屏）
	public static int MAX_SCREEN_HEIGHT = 320;

	public static RectBox screenRect = new RectBox(0, 0, MAX_SCREEN_WIDTH,
			MAX_SCREEN_HEIGHT);

	final private static Runtime systemRuntime = Runtime.getRuntime();

	final static private boolean osIsLinux;

	final static private boolean osIsUnix;

	final static private boolean osIsMacOs;

	final static private boolean osIsWindows;

	final static private boolean osIsWindowsXP;

	final static private boolean osIsWindows2003;

	final static private boolean osBit64;

	final static public String OS_NAME;

	final static public int JAVA_13 = 0;

	final static public int JAVA_14 = 1;

	final static public int JAVA_15 = 2;

	final static public int JAVA_16 = 3;

	final static public int JAVA_17 = 4;

	final static public int JAVA_18 = 5;

	final static public int JAVA_19 = 6;

	public static LProcess screenProcess;

	public static float scaleWidth = 1, scaleHeight = 1;

	public static int FONT_TYPE = 15;

	public static int FONT_SIZE = 1;

	public static String FONT_NAME = "黑体";

	public static String LOG_FILE = "log.txt";

	public static boolean DEFAULT_ROTATE_CACHE = true;

	public static int DEFAULT_MAX_CACHE_SIZE = 30;

	public static int DEFAULT_MAX_FPS = 60;

	public static boolean AUTO_REPAINT = true;

	public static boolean isApplet, isPaused, isLogo;

	public static boolean isStringTexture = false, isBackLocked = false;

	private static String javaVersion;

	private static HashMap<String, Object> settings = new HashMap<String, Object>(
			5);

	/**
	 * 设定一组键值对到缓存当中
	 * 
	 * @param key
	 * @param value
	 */
	public static void set(String key, Object value) {
		if (key == null || "".equals(key)) {
			return;
		}
		settings.put(key, value);
	}

	/**
	 * 获得指定键所对应的数值
	 * 
	 * @param key
	 * @return
	 */
	public static Object get(String key) {
		if (key == null || "".equals(key)) {
			return null;
		}
		return settings.get(key);
	}

	/**
	 * 获得计时器
	 * 
	 * @return
	 */
	public static SystemTimer getSystemTimer() {
		return new SystemTimer();
	}

	static {
		OS_NAME = System.getProperty("os.name").toLowerCase();
		osIsLinux = OS_NAME.indexOf("linux") != -1;
		osIsUnix = OS_NAME.indexOf("nix") != -1 || OS_NAME.indexOf("nux") != 1;
		osIsMacOs = OS_NAME.indexOf("mac") != -1;
		osIsWindows = OS_NAME.indexOf("windows") != -1;
		osIsWindowsXP = OS_NAME.startsWith("Windows")
				&& (OS_NAME.compareTo("5.1") >= 0);
		osIsWindows2003 = "windows 2003".equals(OS_NAME);
		osBit64 = System.getProperty("os.arch").equals("amd64");
		javaVersion = System.getProperty("java.version");
	}

	public static void exit() {
		if (LSystem.screenProcess != null
				&& LSystem.screenProcess.getScene() != null) {
			LSystem.screenProcess.getScene().exit();
		} else {
			System.exit(0);
		}
	}

	public static void stopRepaint() {
		LSystem.AUTO_REPAINT = false;
		LSystem.isPaused = true;
	}

	public static void startRepaint() {
		LSystem.AUTO_REPAINT = true;
		LSystem.isPaused = false;
	}

	/**
	 * 清空系统缓存资源
	 * 
	 */
	public static void destroy() {
		JavaSEGraphicsUtils.destroyImages();
		Resources.destroy();
	}

	/**
	 * 申请回收系统资源
	 * 
	 */
	final public static void gc() {
		try {
			long maxRestoreLoops = 299;
			long pauseTime = 10;
			long UsedMemoryNow = nowMemory();
			long UsedMemoryPrev = Long.MAX_VALUE;
			for (int i = 0; i < maxRestoreLoops; i++) {
				systemRuntime.runFinalization();
				systemRuntime.gc();
				try {
					Thread.sleep(pauseTime);
				} catch (InterruptedException e) {
				}
				if (UsedMemoryPrev > UsedMemoryNow) {
					UsedMemoryPrev = UsedMemoryNow;
					UsedMemoryNow = nowMemory();
				} else {
					break;
				}
			}
		} catch (Throwable e) {
			System.gc();
		}
	}

	/**
	 * 以指定范围内的指定概率执行gc
	 * 
	 * @param size
	 * @param rand
	 */
	final public static void gc(final int size, final long rand) {
		if (rand > size) {
			throw new RuntimeException(
					("GC random probability " + rand + " > " + size).intern());
		}
		if (LSystem.random.nextInt(size) <= rand) {
			LSystem.gc();
		}
	}

	/**
	 * 以指定概率使用gc回收系统资源
	 * 
	 * @param rand
	 */
	final public static void gc(final long rand) {
		gc(100, rand);
	}

	/**
	 * 打开当前系统浏览器
	 * 
	 * @param url
	 */
	public static void openURL(String url) {
		try {
			java.net.URI uri = new java.net.URI(url);
			java.awt.Desktop.getDesktop().browse(uri);
		} catch (Throwable e) {
			try {
				browse(url);
			} catch (Exception err) {
				try {
					if (LSystem.isWindows()) {
						File iexplore = new File(
								"C:\\Program Files\\Internet Explorer\\iexplore.exe");
						if (iexplore.exists()) {
							systemRuntime.exec(iexplore.getAbsolutePath()
									+ " \"" + url + "\"");
						} else {
							systemRuntime
									.exec("rundll32 url.dll,FileProtocolHandler "
											+ url);
						}
					} else if (LSystem.isMacOS()) {
						systemRuntime.exec("open " + url);
					} else if (LSystem.isUnix()) {
						String[] browsers = { "epiphany", "firefox", "mozilla",
								"konqueror", "netscape", "opera", "links",
								"lynx" };
						StringBuffer cmd = new StringBuffer();
						for (int i = 0; i < browsers.length; i++) {
							cmd.append((i == 0 ? "" : " || ") + browsers[i]
									+ " \"" + url + "\" ");
						}
						systemRuntime.exec(new String[] { "sh", "-c",
								cmd.toString() });
					}
				} catch (IOException ex) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void browse(String url) throws Exception {
		String osName = System.getProperty("os.name", "");
		if (osName.startsWith("Mac OS")) {
			Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
			Method openURL = fileMgr.getDeclaredMethod("openURL",
					new Class[] { String.class });
			openURL.invoke(null, new Object[] { url });
		} else if (osName.startsWith("Windows")) {
			Runtime.getRuntime().exec(
					"rundll32 url.dll,FileProtocolHandler " + url);
		} else {
			String[] browsers = { "firefox", "opera", "konqueror", "epiphany",
					"mozilla", "netscape" };
			String browser = null;
			for (int count = 0; count < browsers.length && browser == null; count++) {
				if (Runtime.getRuntime()
						.exec(new String[] { "which", browsers[count] })
						.waitFor() == 0) {
					browser = browsers[count];
				}
			}
			if (browser == null) {
				throw new Exception("Could not find web browser");
			} else {
				Runtime.getRuntime().exec(new String[] { browser, url });
			}
		}
	}

	final private static long nowMemory() {
		return systemRuntime.totalMemory() - systemRuntime.freeMemory();
	}

	public static boolean isLinux() {
		return osIsLinux;
	}

	public static boolean isMacOS() {
		return osIsMacOs;
	}

	public static boolean isUnix() {
		return osIsUnix;
	}

	public static boolean isWindows() {
		return osIsWindows;
	}

	public static boolean isWindowsXP() {
		return osIsWindowsXP;
	}

	public static boolean isWindows2003() {
		return osIsWindows2003;
	}

	public static boolean isBit64() {
		return osBit64;
	}

	public static boolean isSun() {
		return System.getProperty("java.vm.vendor").indexOf("Sun") != -1
				|| System.getProperty("java.vm.vendor").indexOf("Oracle") != -1;
	}

	public static boolean isApple() {
		return System.getProperty("java.vm.vendor").indexOf("Apple") != -1;
	}

	public static boolean isHPUX() {
		return System.getProperty("java.vm.vendor").indexOf(
				"Hewlett-Packard Company") != -1;
	}

	public static boolean isIBM() {
		return System.getProperty("java.vm.vendor").indexOf("IBM") != -1;
	}

	public static boolean isBlackdown() {
		return System.getProperty("java.vm.vendor").indexOf("Blackdown") != -1;
	}

	public static boolean isBEAWithUnsafeSupport() {
		if (System.getProperty("java.vm.vendor").indexOf("BEA") != -1) {
			String vmVersion = System.getProperty("java.vm.version");
			if (StringUtils.startsWith(vmVersion, 'R')) {
				return true;
			}
			String vmInfo = System.getProperty("java.vm.info");
			if (vmInfo != null) {
				return (vmInfo.startsWith("R25.1") || vmInfo
						.startsWith("R25.2"));
			}
		}

		return false;
	}

	public static String getJavaVersion() {
		return javaVersion;
	}

	/**
	 * 写入整型数据到OutputStream
	 * 
	 * @param out
	 * @param number
	 */
	public final static void writeInt(final OutputStream out, final int number) {
		byte[] bytes = new byte[4];
		try {
			for (int i = 0; i < 4; i++) {
				bytes[i] = (byte) ((number >> (i * 8)) & 0xff);
			}
			out.write(bytes);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 从InputStream中获得整型数据
	 * 
	 * @param in
	 * @return
	 */
	final static public int readInt(final InputStream in) {
		int data = -1;
		try {
			data = (in.read() & 0xff);
			data |= ((in.read() & 0xff) << 8);
			data |= ((in.read() & 0xff) << 16);
			data |= ((in.read() & 0xff) << 24);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		return data;
	}

	/**
	 * 合并hashCode和指定类型的数值生成新的Code值(以下同)
	 * 
	 * @param hashCode
	 * @param value
	 * @return
	 */
	public static int unite(int hashCode, boolean value) {
		int v = value ? 1231 : 1237;
		return unite(hashCode, v);
	}

	public static int unite(int hashCode, long value) {
		int v = (int) (value ^ (value >>> 32));
		return unite(hashCode, v);
	}

	public static int unite(int hashCode, float value) {
		int v = Float.floatToIntBits(value);
		return unite(hashCode, v);
	}

	public static int unite(int hashCode, double value) {
		long v = Double.doubleToLongBits(value);
		return unite(hashCode, v);
	}

	public static int unite(int hashCode, Object value) {
		return unite(hashCode, value.hashCode());
	}

	public static int unite(int hashCode, int value) {
		return 31 * hashCode + value;
	}
}
