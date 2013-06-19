package loon;

import java.lang.reflect.Constructor;
import java.util.LinkedList;

import loon.LSetting.Listener;
import loon.core.EmulatorListener;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.graphics.Screen;
import loon.core.graphics.opengl.LTexture;
import loon.core.input.LInput.ClickEvent;
import loon.core.input.LInput.SelectEvent;
import loon.core.input.LInput.TextEvent;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 
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
 * @email javachenpeng@yahoo.com
 * @version 0.1.3
 */
public abstract class LGame extends Activity {

	private static Class<?> getType(Object o) {
		if (o instanceof Integer) {
			return Integer.TYPE;
		} else if (o instanceof Float) {
			return Float.TYPE;
		} else if (o instanceof Double) {
			return Double.TYPE;
		} else if (o instanceof Long) {
			return Long.TYPE;
		} else if (o instanceof Short) {
			return Short.TYPE;
		} else if (o instanceof Short) {
			return Short.TYPE;
		} else if (o instanceof Boolean) {
			return Boolean.TYPE;
		} else {
			return o.getClass();
		}
	}

	public void register(LSetting setting, Class<? extends Screen> clazz,
			Object... args) {
		this._listener = setting.listener;
		this.maxScreen(setting.width, setting.height);
		this.initialization(setting.landscape, setting.mode);
		this.setShowFPS(setting.showFPS);
		this.setShowMemory(setting.showMemory);
		this.setShowLogo(setting.showLogo);
		this.setFPS(setting.fps);
		if (clazz != null) {
			if (args != null) {
				try {
					final int funs = args.length;
					if (funs == 0) {
						setScreen(clazz.newInstance());
						showScreen();
					} else {
						Class<?>[] functions = new Class<?>[funs];
						for (int i = 0; i < funs; i++) {
							functions[i] = getType(args[i]);
						}
						Constructor<?> constructor = Class.forName(
								clazz.getName()).getConstructor(functions);
						Object o = constructor.newInstance(args);
						if (o != null && (o instanceof Screen)) {
							setScreen((Screen) o);
							showScreen();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static enum LMode {

		Defalut, Max, Fill, FitFill, Ratio, MaxRatio

	}

	public static enum Location {

		LEFT, RIGHT, TOP, BOTTOM, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER, ALIGN_BASELINE, ALIGN_LEFT, ALIGN_TOP, ALIGN_RIGHT, ALIGN_BOTTOM, ALIGN_PARENT_LEFT, ALIGN_PARENT_TOP, ALIGN_PARENT_RIGHT, ALIGN_PARENT_BOTTOM, CENTER_IN_PARENT, CENTER_HORIZONTAL, CENTER_VERTICAL;

	}

	private boolean keyboardOpen, isDestroy;

	private int orientation;

	private LGameView gameView;

	private FrameLayout frameLayout;

	private Listener _listener;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Log.i("Android2DActivity", "LGame 2D Engine Start");
				LSystem.screenActivity = LGame.this;
				LGame.this.frameLayout = new FrameLayout(LGame.this);
				LGame.this.isDestroy = true;
				LGame.this.onMain();
			}
		};
		runOnUiThread(runnable);
	}

	public void setActionBarVisibility(boolean visible) {
		if (LSystem.isAndroidVersionHigher(11)) {
			try {
				java.lang.reflect.Method getBarMethod = Activity.class
						.getMethod("getActionBar");
				Object actionBar = getBarMethod.invoke(this);
				if (actionBar != null) {
					java.lang.reflect.Method showHideMethod = actionBar
							.getClass().getMethod((visible) ? "show" : "hide");
					showHideMethod.invoke(actionBar);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void initialization(final boolean landscape) {
		initialization(landscape, LMode.Ratio);
	}

	public void initialization(final boolean landscape, final LMode mode) {
		initialization(landscape, true, mode);
	}

	/**
	 * 以指定倾斜方式显示游戏画面
	 * 
	 * @param width
	 * @param height
	 * @param landscape
	 */
	public void initialization(final int width, final int height,
			final boolean landscape) {
		initialization(width, height, landscape, LMode.Ratio);
	}

	/**
	 * 以指定倾斜方式显示游戏画面
	 * 
	 * @param width
	 * @param height
	 * @param landscape
	 * @param mode
	 */
	public void initialization(final int width, final int height,
			final boolean landscape, final LMode mode) {
		maxScreen(width, height);
		initialization(landscape, mode);
	}

	public void initialization(final boolean landscape,
			final boolean fullScreen, final LMode mode) {
		if (!landscape) {
			if (LSystem.MAX_SCREEN_HEIGHT > LSystem.MAX_SCREEN_WIDTH) {
				int tmp_height = LSystem.MAX_SCREEN_HEIGHT;
				LSystem.MAX_SCREEN_HEIGHT = LSystem.MAX_SCREEN_WIDTH;
				LSystem.MAX_SCREEN_WIDTH = tmp_height;
			}
		}
		this.gameView = new LGameView(LGame.this, mode, fullScreen, landscape);
		if (mode == LMode.Defalut) {
			// 添加游戏View，显示为指定大小，并居中
			this.addView(gameView.getView(), gameView.getWidth(),
					gameView.getHeight(), Location.CENTER);
		} else if (mode == LMode.Ratio) {
			// 添加游戏View，显示为屏幕许可范围，并居中
			this.addView(gameView.getView(), gameView.getMaxWidth(),
					gameView.getMaxHeight(), Location.CENTER);
		} else if (mode == LMode.MaxRatio) {
			// 添加游戏View，显示为屏幕许可的最大范围(可能比单纯的Ratio失真)，并居中
			this.addView(gameView.getView(), gameView.getMaxWidth(),
					gameView.getMaxHeight(), Location.CENTER);
		} else if (mode == LMode.Max) {
			// 添加游戏View，显示为最大范围值，并居中
			this.addView(gameView.getView(), gameView.getMaxWidth(),
					gameView.getMaxHeight(), Location.CENTER);
		} else if (mode == LMode.Fill) {
			// 添加游戏View，显示为全屏，并居中
			this.addView(gameView.getView(), 0xffffffff, 0xffffffff,
					Location.CENTER);
		} else if (mode == LMode.FitFill) {
			// 添加游戏View，显示为按比例缩放情况下的最大值，并居中
			this.addView(gameView.getView(), gameView.getMaxWidth(),
					gameView.getMaxHeight(), Location.CENTER);
		}
		if (LSystem.isAndroidVersionHigher(11)) {
			View rootView = getWindow().getDecorView();
			try {
				java.lang.reflect.Method m = View.class.getMethod(
						"setSystemUiVisibility", int.class);
				m.invoke(rootView, 0x0);
				m.invoke(rootView, 0x1);
			} catch (Exception ex) {

			}
		}
	}

	public abstract void onMain();

	/**
	 * 弹出输入框
	 * 
	 * @param listener
	 * @param title
	 * @param message
	 */
	public void showAndroidTextInput(final TextEvent listener,
			final String title, final String message) {
		if (listener == null) {
			return;
		}
		final LGameTools.ClickAndroid OK = new LGameTools.ClickAndroid(
				listener, 0);
		final LGameTools.ClickAndroid CANCEL = new LGameTools.ClickAndroid(
				listener, 1);
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				LGame.this);
		builder.setTitle(title);
		final android.widget.EditText input = new android.widget.EditText(
				LGame.this);
		input.setText(message);
		input.setSingleLine();
		OK.setInput(input);
		builder.setView(input);
		builder.setPositiveButton("Ok", OK);
		builder.setOnCancelListener(CANCEL);
		builder.show();
	}

	/**
	 * 弹出指定的HTML页面
	 * 
	 * @param listener
	 * @param title
	 * @param url
	 */
	public void showAndroidOpenHTML(final ClickEvent listener,
			final String title, final String url) {
		if (listener == null) {
			return;
		}
		final LGameTools.ClickAndroid OK = new LGameTools.ClickAndroid(
				listener, 0);
		final LGameTools.ClickAndroid CANCEL = new LGameTools.ClickAndroid(
				listener, 1);
		final LGameTools.Web web = new LGameTools.Web(LGame.this, url);
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				LGame.this);
		builder.setCancelable(true);
		builder.setTitle(title);
		builder.setView(web);
		builder.setPositiveButton("Ok", OK).setNegativeButton("Cancel", CANCEL);
		builder.show();
	}

	/**
	 * 弹出选择框
	 * 
	 * @param listener
	 * @param title
	 * @param text
	 */
	public void showAndroidSelect(final SelectEvent listener,
			final String title, final String text[]) {
		if (listener == null) {
			return;
		}
		final LGameTools.ClickAndroid ITEM = new LGameTools.ClickAndroid(
				listener, 0);
		final LGameTools.ClickAndroid CANCEL = new LGameTools.ClickAndroid(
				listener, 1);
		final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				LGame.this);
		builder.setTitle(title);
		builder.setItems(text, ITEM);
		builder.setOnCancelListener(CANCEL);
		android.app.AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * 弹出Yes or No判定
	 * 
	 * @param title
	 * @param message
	 * @param cancelable
	 * @param yes
	 * @param no
	 * @param onYesClick
	 * @param onNoClick
	 */
	public void showAndroidYesOrNo(String title, String message,
			boolean cancelable, String yes, String no,
			android.content.DialogInterface.OnClickListener onYesClick,
			android.content.DialogInterface.OnClickListener onNoClick) {
		final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				LGame.this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(yes, onYesClick);
		builder.setNegativeButton(no, onNoClick);
		builder.setCancelable(cancelable);
		builder.create();
		builder.show();
	}

	public boolean isGamePadBackExit() {
		return !LSystem.isBackLocked;
	}

	public void setGamePadBackExit(boolean flag) {
		LSystem.isBackLocked = !flag;
	}

	public View inflate(final int layoutID) {
		final android.view.LayoutInflater inflater = android.view.LayoutInflater
				.from(this);
		return inflater.inflate(layoutID, null);
	}

	public void addView(final View view, Location location) {
		if (view == null) {
			return;
		}
		addView(view, android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, location);
	}

	public void addView(final View view, int w, int h, Location location) {
		if (view == null) {
			return;
		}
		android.widget.RelativeLayout viewLayout = new android.widget.RelativeLayout(
				LGame.this);
		android.widget.RelativeLayout.LayoutParams relativeParams = LSystem
				.createRelativeLayout(location, w, h);
		viewLayout.addView(view, relativeParams);
		addView(viewLayout);
	}

	public void addView(final View view) {
		if (view == null) {
			return;
		}
		frameLayout.addView(view, createLayoutParams());
		try {
			if (view.getVisibility() != View.VISIBLE) {
				view.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
		}
	}

	public void removeView(final View view) {
		if (view == null) {
			return;
		}
		frameLayout.removeView(view);
		try {
			if (view.getVisibility() != View.GONE) {
				view.setVisibility(View.GONE);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 假的，糊弄反编译者中小白用……
	 * 
	 * @param ad
	 * @return
	 */
	public int setAD(String ad) {
		int result = 0;
		try {
			Class<LGame> clazz = LGame.class;
			java.lang.reflect.Field[] field = clazz.getDeclaredFields();
			if (field != null) {
				result = field.length;
			}
		} catch (Exception e) {
		}
		return result + ad.length();
	}

	public void maxScreen() {
		RectBox rect = getScreenDimension();
		maxScreen(rect.width, rect.height);
	}

	public void maxScreen(int w, int h) {
		LSystem.MAX_SCREEN_WIDTH = w;
		LSystem.MAX_SCREEN_HEIGHT = h;
	}

	public void showScreen() {
		setContentView(frameLayout);
		try {
			getWindow().setBackgroundDrawable(null);
		} catch (Exception e) {
		}
	}

	public void checkConfigChanges(android.content.Context context) {
		try {
			final int REQUIRED_CONFIG_CHANGES = android.content.pm.ActivityInfo.CONFIG_ORIENTATION
					| android.content.pm.ActivityInfo.CONFIG_KEYBOARD_HIDDEN;
			android.content.pm.ActivityInfo info = this.getPackageManager()
					.getActivityInfo(
							new android.content.ComponentName(context,
									this.getPackageName() + "."
											+ this.getLocalClassName()), 0);
			if ((info.configChanges & REQUIRED_CONFIG_CHANGES) != REQUIRED_CONFIG_CHANGES) {
				new android.app.AlertDialog.Builder(this)
						.setMessage(
								"LGame Tip : Please add the following line to the Activity manifest .\n[configChanges=\"keyboardHidden|orientation\"]")
						.show();
			}
		} catch (Exception e) {
			Log.w("Android2DView",
					"Cannot access game AndroidManifest.xml file !");
		}
	}

	public FrameLayout getFrameLayout() {
		return frameLayout;
	}

	public android.content.pm.PackageInfo getPackageInfo() {
		try {
			String packName = getPackageName();
			return getPackageManager().getPackageInfo(packName, 0);
		} catch (Exception ex) {

		}
		return null;
	}

	public String getVersionName() {
		android.content.pm.PackageInfo info = getPackageInfo();
		if (info != null) {
			return info.versionName;
		}
		return null;
	}

	public int getVersionCode() {
		android.content.pm.PackageInfo info = getPackageInfo();
		if (info != null) {
			return info.versionCode;
		}
		return -1;
	}

	@Override
	public void onConfigurationChanged(android.content.res.Configuration config) {
		super.onConfigurationChanged(config);
		orientation = config.orientation;
		keyboardOpen = config.keyboardHidden == android.content.res.Configuration.KEYBOARDHIDDEN_NO;
	}

	protected FrameLayout.LayoutParams createLayoutParams() {
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
				0xffffffff, 0xffffffff);
		layoutParams.gravity = Gravity.CENTER;
		return layoutParams;
	}

	/**
	 * 设定常规图像加载方法的扩大值
	 * 
	 * @param sampleSize
	 */
	public void setSizeImage(int sampleSize) {
		LSystem.setPoorImage(sampleSize);
	}

	/**
	 * 取出第一个Screen并执行
	 * 
	 */
	public void runFirstScreen() {
		if (LSystem.screenProcess != null) {
			LSystem.screenProcess.runFirstScreen();
		}
	}

	/**
	 * 取出最后一个Screen并执行
	 */
	public void runLastScreen() {
		if (LSystem.screenProcess != null) {
			LSystem.screenProcess.runLastScreen();
		}
	}

	/**
	 * 运行指定位置的Screen
	 * 
	 * @param index
	 */
	public void runIndexScreen(int index) {
		if (LSystem.screenProcess != null) {
			LSystem.screenProcess.runIndexScreen(index);
		}
	}

	/**
	 * 运行自当前Screen起的上一个Screen
	 */
	public void runPreviousScreen() {
		if (LSystem.screenProcess != null) {
			LSystem.screenProcess.runPreviousScreen();
		}
	}

	/**
	 * 运行自当前Screen起的下一个Screen
	 */
	public void runNextScreen() {
		if (LSystem.screenProcess != null) {
			LSystem.screenProcess.runNextScreen();
		}
	}

	/**
	 * 向缓存中添加Screen数据，但是不立即执行
	 * 
	 * @param screen
	 */
	public void addScreen(Screen screen) {
		if (LSystem.screenProcess != null) {
			LSystem.screenProcess.addScreen(screen);
		}
	}

	/**
	 * 切换当前窗体为指定Screen
	 * 
	 * @param screen
	 */
	public void setScreen(Screen screen) {
		if (LSystem.screenProcess != null) {
			LSystem.screenProcess.setScreen(screen);
		}
	}

	/**
	 * 获得保存的Screen列表
	 * 
	 * @return
	 */
	public LinkedList<Screen> getScreens() {
		if (LSystem.screenProcess != null) {
			return LSystem.screenProcess.getScreens();
		}
		return null;
	}

	/**
	 * 获得缓存的Screen总数
	 */
	public int getScreenCount() {
		if (LSystem.screenProcess != null) {
			return LSystem.screenProcess.getScreenCount();
		}
		return 0;
	}

	public void setEmulatorListener(EmulatorListener emulator) {
		if (LSystem.screenProcess != null) {
			LSystem.screenProcess.setEmulatorListener(emulator);
		}
	}

	public void setShowFPS(boolean flag) {
		if (gameView != null) {
			this.gameView.setShowFPS(flag);
		}
	}

	public void setShowMemory(boolean flag) {
		if (gameView != null) {
			this.gameView.setShowMemory(flag);
		}
	}

	public void setFPS(long frames) {
		if (gameView != null) {
			this.gameView.setFPS(frames);
		}
	}

	public void setShowLogo(boolean showLogo) {
		if (gameView != null) {
			gameView.setShowLogo(showLogo);
		}
	}

	public void setLogo(LTexture img) {
		if (gameView != null) {
			gameView.setLogo(img);
		}
	}

	public RectBox getScreenDimension() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		return new RectBox(dm.xdpi, dm.ydpi, dm.widthPixels, dm.heightPixels);
	}

	public LGameView gameView() {
		return gameView;
	}

	/**
	 * 键盘是否已显示
	 * 
	 * @return
	 */
	public boolean isKeyboardOpen() {
		return keyboardOpen;
	}

	/**
	 * 当前窗体方向
	 * 
	 * @return
	 */
	public int getOrientation() {
		return orientation;
	}

	/**
	 * 退出当前应用
	 */
	public void close() {
		finish();
	}

	public boolean isDestroy() {
		return isDestroy;
	}

	/**
	 * 设定是否在Activity注销时强制关闭整个程序
	 * 
	 * @param isDestroy
	 */
	public void setDestroy(boolean isDestroy) {
		this.isDestroy = isDestroy;
		if (!isDestroy) {
			LSystem.isBackLocked = true;
		}
	}

	public boolean isBackLocked() {
		return LSystem.isBackLocked;
	}

	/**
	 * 设定锁死BACK事件不处理
	 * 
	 * @param isBackLocked
	 */
	public void setBackLocked(boolean isBackLocked) {
		LSystem.isBackLocked = isBackLocked;
	}

	@Override
	protected void onPause() {
		if (gameView == null) {
			return;
		}
		if (_listener != null) {
			_listener.onPause();
		}
		gameView.pause();
		onGamePaused();
		if (isFinishing()) {
			gameView.destroy();
		}
		if (gameView != null && gameView.getView() != null) {
			((GLSurfaceViewCupcake) gameView.getView()).onPause();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (gameView == null) {
			return;
		}
		if (LSystem.SCREEN_LANDSCAPE) {
			if (getRequestedOrientation() != android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		} else {
			if (getRequestedOrientation() != android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
				setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		}
		if (_listener != null) {
			_listener.onResume();
		}
		gameView.resume();
		onGameResumed();
		if (gameView != null && gameView.getView() != null) {
			((GLSurfaceViewCupcake) gameView.getView()).onResume();
		}
		super.onResume();
	}

	public abstract void onGameResumed();

	public abstract void onGamePaused();

	@Override
	protected void onDestroy() {
		try {
			LSystem.isRunning = false;
			if (_listener != null) {
				_listener.onExit();
			}
			super.onDestroy();
			// 当此项为True时，强制关闭整个程序
			if (isDestroy) {
				Log.i("Android2DActivity", "LGame 2D Engine Shutdown");
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		} catch (Exception e) {

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		if (LSystem.screenProcess != null) {
			if (LSystem.screenProcess.onCreateOptionsMenu(menu)) {
				return true;
			}
		}
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = super.onOptionsItemSelected(item);
		if (LSystem.screenProcess != null) {
			if (LSystem.screenProcess.onOptionsItemSelected(item)) {
				return true;
			}
		}
		return result;
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
		if (LSystem.screenProcess != null) {
			LSystem.screenProcess.onOptionsMenuClosed(menu);
		}
	}

	// 检查ADView状态，如果ADView上附着有其它View则删除，
	// 从而起到屏蔽-广告屏蔽组件的作用。
	public void safeguardAndroidADView(android.view.View view) {
		try {
			final android.view.ViewGroup vgp = (android.view.ViewGroup) view
					.getParent().getParent();
			if (vgp.getChildAt(1) != null) {
				vgp.removeViewAt(1);
			}
		} catch (Exception ex) {
		}
	}

}
