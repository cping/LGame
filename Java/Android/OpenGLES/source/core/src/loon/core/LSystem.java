package loon.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Random;

import loon.LGame;
import loon.LGame.Location;
import loon.core.event.Drawable;
import loon.core.event.Updateable;
import loon.core.geom.RectBox;
import loon.core.graphics.GraphicsUtils;
import loon.core.graphics.Screen;
import loon.core.graphics.opengl.LTexture;
import loon.core.input.LProcess;
import loon.core.resource.Resources;
import loon.core.timer.SystemTimer;
import loon.utils.MathUtils;

import android.os.Build;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

/**
 * 
 * Copyright 2008 - 2012
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
public final class LSystem {

	public String getLanguage() {
		return java.util.Locale.getDefault().getDisplayName();
	}

	public enum ApplicationType {
		Android, JavaSE, XNA, IOS, HTML5, PSM
	}

	public static CallQueue global_queue;

	public static ApplicationType type = ApplicationType.Android;

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

	public final static int TRANSPARENT = 0xff000000;

	public final static boolean isThreadDrawing() {
		Thread thread = Thread.currentThread();
		if (thread != null) {
			String name = thread.getName();
			if (name != null && name.toLowerCase().startsWith("glthread")) {
				return true;
			}
		}
		return false;
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

	public final static void close(InputStream in) {
		if (in != null) {
			try {
				in.close();
				in = null;
			} catch (Exception e) {
			}
		}
	}

	/***
	 * 获得指定名称资源的数据流
	 * 
	 * @param resName
	 * @return
	 * @throws FileNotFoundException
	 */
	public final static InputStream getResourceAsStream(String resName)
			throws FileNotFoundException {
		try {
			return Resources.class.getClassLoader()
					.getResourceAsStream(resName);
		} catch (Exception ex) {
			try {
				return getActivity().getAssets().open(resName);
			} catch (IOException e1) {
				try {
					return getActivity().openFileInput(resName);
				} catch (IOException e2) {
					return new FileInputStream(new File(resName));
				}
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
		if (LSystem.isThreadDrawing()) {
			u.action();
		} else {
			LProcess process = LSystem.screenProcess;
			if (process != null) {
				process.addLoad(u);
			}
		}
	}

	public final static void unload(Updateable u) {
		if (LSystem.isThreadDrawing()) {
			u.action();
		} else {
			LProcess process = LSystem.screenProcess;
			if (process != null) {
				process.addUnLoad(u);
			}
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
	final static public String VERSION = "0.4.0";

	// 默认的最大窗体宽（横屏）
	public static int MAX_SCREEN_WIDTH = 480;

	// 默认的最大窗体高（横屏）
	public static int MAX_SCREEN_HEIGHT = 320;

	public static RectBox screenRect = new RectBox(0, 0, MAX_SCREEN_WIDTH,
			MAX_SCREEN_HEIGHT);

	// 图像缩放值
	public static int IMAGE_SIZE = 0;

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

	// 行分隔符
	final static public String LS = System.getProperty("line.separator", "\n");

	// 文件分割符
	final static public String FS = System.getProperty("file.separator", "\\");

	// 随机数
	final static public Random random = new Random();

	// 屏幕是否横屏
	public static boolean SCREEN_LANDSCAPE;

	// 最大缓存数量
	final static public int DEFAULT_MAX_CACHE_SIZE = 30;

	final static public String encoding = "UTF-8";

	final static public String FONT_NAME = "Monospaced";

	final static public int DEFAULT_MAX_FPS = 60;

	public static LGame screenActivity;

	public static LProcess screenProcess;

	public static boolean isStringTexture = false, isBackLocked = false;

	public static float scaleWidth = 1, scaleHeight = 1;

	public static boolean isCreated, isLogo, isRunning, isResume, isDestroy,
			isPaused, AUTO_REPAINT;

	private static boolean EMULATOR;

	private static android.os.Handler OS_HANDLER;

	final private static String BULID_BRAND, BULID_MODEL, BULIDM_PRODUCT,
			BULIDM_RELEASE, BULIDM_DEVICE;

	private static int BULIDM_SDK;

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

	static {
		BULID_BRAND = Build.BRAND.toLowerCase();
		BULID_MODEL = Build.MODEL.toLowerCase();
		BULIDM_PRODUCT = Build.PRODUCT.toLowerCase();
		BULIDM_RELEASE = Build.VERSION.RELEASE;
		try {
			BULIDM_SDK = Integer.parseInt(String.valueOf(Build.VERSION.class
					.getDeclaredField("SDK").get(null)));
		} catch (Exception ex) {
			try {
				BULIDM_SDK = Build.VERSION.class.getDeclaredField("SDK_INT")
						.getInt(null);
			} catch (Exception e) {
			}
		}
		BULIDM_DEVICE = Build.DEVICE;
		EMULATOR = BULID_BRAND.indexOf("generic") != -1
				&& BULID_MODEL.indexOf("sdk") != -1;
	}

	/**
	 * 设定常规图像加载方法（非全部）的默认劣化值
	 * 
	 * @param sampleSize
	 */
	public static void setPoorImage(int sampleSize) {
		if (sampleSize > 0) {
			LSystem.IMAGE_SIZE = sampleSize;
		} else {
			LSystem.IMAGE_SIZE = 0;
		}
	}

	/**
	 * 判定是否使用了“奇异”的三星机型
	 * 
	 * @return
	 */
	public static boolean isSamsung7500() {
		return isDevice("GT-I7500");
	}

	/**
	 * 判断手机驱动
	 * 
	 * @param d
	 * @return
	 */
	public static boolean isDevice(String d) {
		return BULIDM_DEVICE.equalsIgnoreCase(d);
	}

	/**
	 * 设定LayoutParams为全屏模式
	 * 
	 * @return
	 */
	public static LayoutParams createFillLayoutParams() {
		return new LayoutParams(0xffffffff, 0xffffffff);
	}

	/**
	 * 生成一个对应指定位置的RelativeLayout
	 * 
	 * @param location
	 * @return
	 */
	public static RelativeLayout.LayoutParams createRelativeLayout(
			Location location) {
		return createRelativeLayout(location, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
	}

	/**
	 * 生成一个对应指定位置的RelativeLayout
	 * 
	 * @param location
	 * @return
	 */
	public static RelativeLayout.LayoutParams createRelativeLayout(
			Location location, int w, int h) {
		RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
				w, h);
		if (location == Location.LEFT) {
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
					RelativeLayout.TRUE);
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
					RelativeLayout.TRUE);
		} else if (location == Location.RIGHT) {
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
					RelativeLayout.TRUE);
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
					RelativeLayout.TRUE);
		} else if (location == Location.TOP) {
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
					RelativeLayout.TRUE);
		} else if (location == Location.BOTTOM) {
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
					RelativeLayout.TRUE);
		} else if (location == Location.BOTTOM_LEFT) {
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
					RelativeLayout.TRUE);
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
					RelativeLayout.TRUE);
		} else if (location == Location.BOTTOM_RIGHT) {
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
					RelativeLayout.TRUE);
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
					RelativeLayout.TRUE);
		} else if (location == Location.CENTER) {
			relativeParams.addRule(RelativeLayout.CENTER_VERTICAL,
					RelativeLayout.TRUE);
			relativeParams.addRule(RelativeLayout.CENTER_IN_PARENT,
					RelativeLayout.TRUE);
		} else if (location == Location.ALIGN_BASELINE) {
			relativeParams.addRule(RelativeLayout.ALIGN_BASELINE,
					RelativeLayout.TRUE);
		} else if (location == Location.ALIGN_LEFT) {
			relativeParams.addRule(RelativeLayout.ALIGN_LEFT,
					RelativeLayout.TRUE);
		} else if (location == Location.ALIGN_TOP) {
			relativeParams.addRule(RelativeLayout.ALIGN_TOP,
					RelativeLayout.TRUE);
		} else if (location == Location.ALIGN_RIGHT) {
			relativeParams.addRule(RelativeLayout.ALIGN_RIGHT,
					RelativeLayout.TRUE);
		} else if (location == Location.ALIGN_BOTTOM) {
			relativeParams.addRule(RelativeLayout.ALIGN_BOTTOM,
					RelativeLayout.TRUE);
		} else if (location == Location.ALIGN_PARENT_LEFT) {
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
					RelativeLayout.TRUE);
		} else if (location == Location.ALIGN_PARENT_TOP) {
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
					RelativeLayout.TRUE);
		} else if (location == Location.ALIGN_PARENT_RIGHT) {
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
					RelativeLayout.TRUE);
		} else if (location == Location.ALIGN_PARENT_BOTTOM) {
			relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
					RelativeLayout.TRUE);
		} else if (location == Location.CENTER_IN_PARENT) {
			relativeParams.addRule(RelativeLayout.CENTER_IN_PARENT,
					RelativeLayout.TRUE);
		} else if (location == Location.CENTER_HORIZONTAL) {
			relativeParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
					RelativeLayout.TRUE);
		} else if (location == Location.CENTER_VERTICAL) {
			relativeParams.addRule(RelativeLayout.CENTER_VERTICAL,
					RelativeLayout.TRUE);
		}

		return relativeParams;
	}

	/**
	 * 跳转到指定的Activity
	 * 
	 * @param from
	 */
	public static void action(android.app.Activity from, Class<?> clazz) {

		screenActivity.setDestroy(false);

		android.content.Intent intent = new android.content.Intent(from, clazz);
		from.startActivity(intent);
	}

	/**
	 * 跳转到指定的Activity,并将其设定为最初的Activity
	 * 
	 * @param from
	 * @param clazz
	 */
	public static void go(android.app.Activity from, Class<?> clazz) {
		LSystem.getActivity().setDestroy(false);
		android.content.Intent intent = new android.content.Intent(from, clazz);
		intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
		from.startActivity(intent);
	}

	public static android.os.Handler getOSHandler() {
		if (OS_HANDLER == null) {
			OS_HANDLER = new android.os.Handler();
		}
		return OS_HANDLER;
	}

	public final static void post(final Updateable update) {
		if (LSystem.isPaused) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					update.action();
				}
			});
		} else {
			if (global_queue != null) {
				global_queue.invokeLater(update);
			} else {
				LSystem.load(update);
			}
		}
	}

	public static void runOnUiThread(final Runnable runnable) {
		LSystem.getActivity().runOnUiThread(runnable);
	}

	public static void dispose(final Runnable runnable) {
		getOSHandler().removeCallbacks(runnable);
		getOSHandler().removeMessages(0);
	}

	public static void stopRepaint() {
		LSystem.AUTO_REPAINT = false;
		LSystem.isPaused = true;
	}

	public static void startRepaint() {
		LSystem.AUTO_REPAINT = true;
		LSystem.isPaused = false;
	}

	public static long getJavaHeap() {
		return Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
	}

	/**
	 * 判定当前Android系统版本是否高于指定的版本
	 * 
	 * @param ver
	 * @return
	 */
	public static boolean isAndroidVersionHigher(final int ver) {
		return BULIDM_SDK >= ver;
	}

	public static String getModel() {
		return BULID_MODEL;
	}

	public static String getProductName() {
		return BULIDM_PRODUCT;
	}

	public static String getOSVersion() {
		return BULIDM_RELEASE;
	}

	public static int getSDKVersion() {
		return BULIDM_SDK;
	}

	public static boolean isPaused() {
		return isPaused;
	}

	public static String getBRANDName() {
		return BULID_BRAND;
	}

	public static boolean isEmulator() {
		return EMULATOR;
	}

	public static boolean isHTC() {
		return BULID_BRAND.indexOf("htc") != -1;
	}

	public static boolean isSamsungGalaxy() {
		final boolean isSamsung = BULID_BRAND.indexOf("samsung") != -1;
		final boolean isGalaxy = BULID_MODEL.indexOf("galaxy") != -1;
		return isSamsung && isGalaxy;
	}

	public static boolean isDroidOrMilestone() {
		final boolean isMotorola = BULID_BRAND.indexOf("moto") != -1;
		final boolean isDroid = BULID_MODEL.indexOf("droid") != -1;
		final boolean isMilestone = BULID_MODEL.indexOf("milestone") != -1;
		return isMotorola && (isDroid || isMilestone);
	}

	/**
	 * 清空框架临时资源
	 */
	public static void destroy() {
		GraphicsUtils.destroy();
		Resources.destroy();
		LSystem.gc();
	}

	/**
	 * 退出当前应用
	 * 
	 */
	public static void exit() {
		if (screenProcess == null) {
			return;
		}
		synchronized (screenProcess) {
			if (screenProcess != null) {
				LSystem.isRunning = false;
				if (screenActivity != null) {
					if (screenActivity.isDestroy()) {
						screenActivity.finish();
					}
				}
			}
		}
	}

	/**
	 * 返回当前的Activity
	 * 
	 * @return
	 */
	public static LGame getActivity() {
		return screenActivity;
	}

	public static SystemTimer getSystemTimer() {
		return new SystemTimer();
	}

	/**
	 * 申请回收系统资源
	 * 
	 */
	final public static void gc() {
		System.gc();
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
		} catch (Exception ex) {
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
