package org.loon.framework.android.game;

import java.util.LinkedList;
import java.util.List;

import org.loon.framework.android.game.core.EmulatorListener;
import org.loon.framework.android.game.core.LHandler;
import org.loon.framework.android.game.core.LSystem;
import org.loon.framework.android.game.core.LInput.ClickListener;
import org.loon.framework.android.game.core.LInput.SelectListener;
import org.loon.framework.android.game.core.LInput.TextListener;
import org.loon.framework.android.game.core.graphics.LImage;
import org.loon.framework.android.game.core.graphics.Screen;

import android.app.Activity;
import android.content.Context;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
 * @project loonframework
 * @author chenpeng
 * @email ceponline@yahoo.com.cn
 * @version 0.1.2
 */
public abstract class LGameAndroid2DActivity extends Activity implements
		android.hardware.SensorEventListener {

	private android.hardware.SensorManager sensorManager;

	private android.hardware.Sensor sensorAccelerometer;

	private boolean setupSensors, keyboardOpen, isLandscape, isBackLocked,
			isDestroy;

	private int orientation;

	private long keyTimeMillis;

	private LGameAndroid2DView gameView;

	private LHandler gameHandler;

	private FrameLayout frameLayout;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		LSystem.gc();
		Log.i("Android2DActivity", "LGame 2D Engine Start");
		// 构建整个游戏使用的最底层FrameLayout
		this.frameLayout = new FrameLayout(LGameAndroid2DActivity.this);
		// 当此项为True时，Back键无法退出游戏(默认为False)
		this.isBackLocked = false;
		// 当此项为False时，Activity在onDestroy仅关闭当前Activity，而不关闭整个程序(默认为True)
		this.isDestroy = true;
		this.onMain();
	}

	public void setupGravity() {
		this.setupSensors = true;
	}

	private void initSensors() {
		try {
			android.hardware.SensorManager sensorService = (android.hardware.SensorManager) getSystemService(Context.SENSOR_SERVICE);
			this.sensorManager = sensorService;
			if (sensorService == null) {
				return;
			}

			List<android.hardware.Sensor> sensors = sensorManager
					.getSensorList(android.hardware.Sensor.TYPE_ACCELEROMETER);
			if (sensors.size() > 0) {
				sensorAccelerometer = sensors.get(0);
			}

			boolean accelSupported = sensorManager.registerListener(this,
					sensorAccelerometer,
					android.hardware.SensorManager.SENSOR_DELAY_GAME);

			// 当不支持Accelerometer时卸载此监听
			if (!accelSupported) {
				sensorManager.unregisterListener(this, sensorAccelerometer);
			}

		} catch (Exception ex) {
		}
	}

	private void stopSensors() {
		try {
			if (sensorManager != null) {
				this.sensorManager.unregisterListener(this);
				this.sensorManager = null;
			}
		} catch (Exception ex) {

		}
	}

	public abstract void onMain();

	/**
	 * 以指定倾斜方式显示游戏画面
	 * 
	 * @param landscape
	 */
	public void initialization(final boolean landscape) {
		initialization(LSystem.MAX_SCREEN_WIDTH, LSystem.MAX_SCREEN_HEIGHT,
				landscape, LMode.Fill);
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
		initialization(width, height, landscape, LMode.Fill);
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

	/**
	 * 以指定倾斜方式，指定模式显示游戏画面
	 * 
	 * @param landscape
	 * @param mode
	 */
	public void initialization(final boolean landscape, final LMode mode) {
		// 如果错误的设置了最大屏幕值则自动矫正(LGame默认按横屏计算)
		if (landscape == false) {
			if (LSystem.MAX_SCREEN_HEIGHT > LSystem.MAX_SCREEN_WIDTH) {
				int tmp_height = LSystem.MAX_SCREEN_HEIGHT;
				LSystem.MAX_SCREEN_HEIGHT = LSystem.MAX_SCREEN_WIDTH;
				LSystem.MAX_SCREEN_WIDTH = tmp_height;
			}
		}
		// 创建游戏View
		this.gameView = new LGameAndroid2DView(LGameAndroid2DActivity.this,
				landscape, mode);

		// 获得当前游戏操作句柄
		this.gameHandler = gameView.getGameHandler();

		if (mode == LMode.Defalut) {
			// 添加游戏View，显示为指定大小，并居中
			this.addView(gameView, gameHandler.getWidth(), gameHandler
					.getHeight(), Location.CENTER);
		} else if (mode == LMode.Ratio) {
			// 添加游戏View，显示为屏幕许可范围，并居中
			this.addView(gameView, gameHandler.getMaxWidth(), gameHandler
					.getMaxHeight(), Location.CENTER);
		} else if (mode == LMode.MaxRatio) {
			// 添加游戏View，显示为屏幕许可的最大范围(可能比单纯的Ratio失真)，并居中
			this.addView(gameView, gameHandler.getMaxWidth(), gameHandler
					.getMaxHeight(), Location.CENTER);
		} else if (mode == LMode.Max) {
			// 添加游戏View，显示为最大范围值，并居中
			this.addView(gameView, gameHandler.getMaxWidth(), gameHandler
					.getMaxHeight(), Location.CENTER);
		} else if (mode == LMode.Fill) {
			// 添加游戏View，显示为全屏，并居中
			this.addView(gameView,0xffffffff,
					0xffffffff,
					Location.CENTER);
		} else if (mode == LMode.FitFill) {
			// 添加游戏View，显示为按比例缩放情况下的最大值，并居中
			this.addView(gameView, gameHandler.getMaxWidth(), gameHandler
					.getMaxHeight(), Location.CENTER);
		}

		if (setupSensors) {
			// 启动重力感应
			this.initSensors();
		}

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
		if (gameHandler != null) {
			gameHandler.runFirstScreen();
		}
	}

	/**
	 * 取出最后一个Screen并执行
	 */
	public void runLastScreen() {
		if (gameHandler != null) {
			gameHandler.runLastScreen();
		}
	}

	/**
	 * 运行指定位置的Screen
	 * 
	 * @param index
	 */
	public void runIndexScreen(int index) {
		if (gameHandler != null) {
			gameHandler.runIndexScreen(index);
		}
	}

	/**
	 * 运行自当前Screen起的上一个Screen
	 */
	public void runPreviousScreen() {
		if (gameHandler != null) {
			gameHandler.runPreviousScreen();
		}
	}

	/**
	 * 运行自当前Screen起的下一个Screen
	 */
	public void runNextScreen() {
		if (gameHandler != null) {
			gameHandler.runNextScreen();
		}
	}

	/**
	 * 向缓存中添加Screen数据，但是不立即执行
	 * 
	 * @param screen
	 */
	public void addScreen(Screen screen) {
		if (gameHandler != null) {
			gameHandler.addScreen(screen);
		}
	}

	/**
	 * 切换当前窗体为指定Screen
	 * 
	 * @param screen
	 */
	public void setScreen(Screen screen) {
		if (gameHandler != null) {
			this.gameHandler.setScreen(screen);
		}
	}

	/**
	 * 获得保存的Screen列表
	 * 
	 * @return
	 */
	public LinkedList<Screen> getScreens() {
		if (gameHandler != null) {
			return gameHandler.getScreens();
		}
		return null;
	}

	/**
	 * 获得缓存的Screen总数
	 */
	public int getScreenCount() {
		if (gameHandler != null) {
			return gameHandler.getScreenCount();
		}
		return 0;
	}

	/**
	 * 输入框弹出
	 * 
	 * @param listener
	 * @param title
	 * @param text
	 */
	public void showAndroidTextInput(final TextListener listener,
			final String title, final String message) {
		if (listener == null) {
			return;
		}
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				LGameAndroid2DActivity.this);
		builder.setTitle(title);
		final android.widget.EditText input = new android.widget.EditText(
				LGameAndroid2DActivity.this);
		input.setText(message);
		input.setSingleLine();
		builder.setView(input);
		builder.setPositiveButton("Ok",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(android.content.DialogInterface dialog,
							int whichButton) {
						listener.input(input.getText().toString());
					}
				});
		builder
				.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {
					public void onCancel(android.content.DialogInterface dialog) {
						listener.cancled();
					}
				});
		builder.show();
	}

	/**
	 * Alter弹出
	 * 
	 * @param message
	 */
	public void showAndroidAlert(final ClickListener listener,
			final String title, final String message) {
		if (listener == null) {
			return;
		}

		final android.app.AlertDialog alert = new android.app.AlertDialog.Builder(
				LGameAndroid2DActivity.this).create();
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setButton("OK",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(android.content.DialogInterface dialog,
							int whichButton) {
						listener.clicked();
					}
				});
		alert.show();
	}

	/**
	 * HTML文件显示
	 * 
	 * @param title
	 * @param assetsFileName
	 */
	public void showAndroidOpenHTML(final ClickListener listener,
			final String title, final String url) {
		final LGameWeb web = new LGameWeb(LGameAndroid2DActivity.this, url);
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				LGameAndroid2DActivity.this);
		builder.setCancelable(true);
		builder.setTitle(title);
		builder.setView(web);
		builder.setPositiveButton("Ok",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(android.content.DialogInterface dialog,
							int whichButton) {
						listener.clicked();
					}
				}).setNegativeButton("Cancel",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(android.content.DialogInterface dialog,
							int whichButton) {
						listener.cancled();
					}
				});
		builder.show();
	}

	/**
	 * Select弹出
	 * 
	 * @param title
	 * @param text
	 * @return
	 */
	public void showAndroidSelect(final SelectListener listener,
			final String title, final String text[]) {
		if (listener == null) {
			return;
		}

		final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				LGameAndroid2DActivity.this);
		builder.setTitle(title);
		builder.setItems(text,
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(android.content.DialogInterface dialog,
							int item) {
						listener.item(item);
					}
				});
		builder
				.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {
					public void onCancel(android.content.DialogInterface dialog) {
						listener.cancled();
					}
				});
		android.app.AlertDialog alert = builder.create();
		alert.show();

	}

	/**
	 * 加载指定资源索引的View到当前窗体
	 * 
	 * @param layoutID
	 * @return
	 */
	public View inflate(final int layoutID) {
		final android.view.LayoutInflater inflater = android.view.LayoutInflater
				.from(this);
		return inflater.inflate(layoutID, null);
	}

	/**
	 * 添加指定的View到游戏界面的指定位置
	 * 
	 * @param view
	 * @param location
	 */
	public void addView(final View view, Location location) {
		addView(view, android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, location);
	}

	/**
	 * 添加指定的View到游戏界面的指定位置，并将View设置为指定大小
	 * 
	 * @param view
	 * @param location
	 */
	public void addView(final View view, int w, int h, Location location) {
		android.widget.RelativeLayout viewLayout = new android.widget.RelativeLayout(
				LGameAndroid2DActivity.this);
		android.widget.RelativeLayout.LayoutParams relativeParams = LSystem
				.createRelativeLayout(location, w, h);
		viewLayout.addView(view, relativeParams);
		addView(viewLayout);
	}

	/**
	 * 添加指定的View到游戏界面
	 * 
	 * @param view
	 */
	public void addView(final View view) {
		frameLayout.addView(view, LSystem.createFillLayoutParams());
	}

	/**
	 * 从游戏界面中删除指定的View
	 * 
	 * @param view
	 */
	public void removeView(final View view) {
		frameLayout.removeView(view);
	}

	/**
	 * 假广告ID注入地址之一，用以蒙蔽不懂编程的工具破解者，对实际开发无意义。
	 * 
	 * @param ad
	 * @return
	 */
	public int setAD(String ad) {
		int result = 0;
		try {
			Class<LGameAndroid2DActivity> clazz = LGameAndroid2DActivity.class;
			java.lang.reflect.Field[] field = clazz.getDeclaredFields();
			if (field != null) {
				result = field.length;
			}
		} catch (Exception e) {
		}
		return result + ad.length();
	}

	/**
	 * 设定游戏窗体最大值
	 */
	public void maxScreen(int w, int h) {
		LSystem.MAX_SCREEN_WIDTH = w;
		LSystem.MAX_SCREEN_HEIGHT = h;
	}

	/**
	 * 显示游戏窗体
	 */
	public void showScreen() {
		setContentView(frameLayout);
		try {
			getWindow().setBackgroundDrawable(null);
		} catch (Exception e) {

		}
	}

	/**
	 * 返回布局器
	 * 
	 * @return
	 */
	public FrameLayout getFrameLayout() {
		return frameLayout;
	}

	/**
	 * 获得当前环境版本信息
	 * 
	 * @return
	 */
	public android.content.pm.PackageInfo getPackageInfo() {
		try {
			String packName = getPackageName();
			return getPackageManager().getPackageInfo(packName, 0);
		} catch (Exception ex) {

		}
		return null;
	}

	/**
	 * 获得当前版本名
	 * 
	 * @return
	 */
	public String getVersionName() {
		android.content.pm.PackageInfo info = getPackageInfo();
		if (info != null) {
			return info.versionName;
		}
		return null;
	}

	/**
	 * 获得当前程序版本号
	 * 
	 * @return
	 */
	public int getVersionCode() {
		android.content.pm.PackageInfo info = getPackageInfo();
		if (info != null) {
			return info.versionCode;
		}
		return -1;
	}

	public void onConfigurationChanged(android.content.res.Configuration config) {
		super.onConfigurationChanged(config);
		orientation = config.orientation;
		keyboardOpen = config.keyboardHidden == android.content.res.Configuration.KEYBOARDHIDDEN_NO;
		isLandscape = config.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE;
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

	public void setEmulatorListener(EmulatorListener emulator) {
		if (gameView != null) {
			gameView.setEmulatorListener(emulator);
		}
	}

	public void setLogo(LImage img) {
		if (gameView != null) {
			this.gameView.setLogo(img);
		}
	}

	public LGameAndroid2DView gameView() {
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
	 * 是否使用了横屏
	 * 
	 * @return
	 */
	public boolean isLandscape() {
		return isLandscape;
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

	public boolean onTouchEvent(MotionEvent e) {
		if (gameHandler == null) {
			return false;
		}
		gameHandler.onTouchEvent(e);
		return false;
	}

	public boolean onTrackballEvent(MotionEvent e) {
		if (gameHandler != null) {
			synchronized (gameHandler) {
				return gameHandler.onTrackballEvent(e);
			}
		}
		return super.onTrackballEvent(e);
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
		if (isDestroy == false) {
			this.isBackLocked = true;
		}
	}

	public boolean isBackLocked() {
		return isBackLocked;
	}

	/**
	 * 设定锁死BACK事件不处理
	 * 
	 * @param isBackLocked
	 */
	public void setBackLocked(boolean isBackLocked) {
		this.isBackLocked = isBackLocked;
	}

	public boolean onKeyDown(int keyCode, KeyEvent e) {
		long curTime = System.currentTimeMillis();
		// 让每次执行键盘事件，至少间隔1/5秒
		if ((curTime - keyTimeMillis) > LSystem.SECOND / 5) {
			keyTimeMillis = curTime;
			if (gameHandler != null) {
				synchronized (gameHandler) {
					if (!isBackLocked) {
						if (keyCode == KeyEvent.KEYCODE_BACK) {
							LSystem.exit();
							return true;
						}
					}
					if (keyCode == KeyEvent.KEYCODE_MENU) {
						return super.onKeyDown(keyCode, e);
					}
					if (gameHandler.onKeyDown(keyCode, e)) {
						return true;
					}
					// 在事件提交给Android前再次间隔，防止连发
					try {
						Thread.sleep(16);
					} catch (Exception ex) {
					}
					return super.onKeyDown(keyCode, e);
				}
			}
		}
		return true;
	}

	public boolean onKeyUp(int keyCode, KeyEvent e) {
		if (gameHandler != null) {
			synchronized (gameHandler) {
				if (keyCode == KeyEvent.KEYCODE_MENU) {
					return super.onKeyUp(keyCode, e);
				}
				if (gameHandler.onKeyUp(keyCode, e)) {
					return true;
				}
				return super.onKeyUp(keyCode, e);
			}
		}
		return true;
	}

	public abstract void onGameResumed();

	public abstract void onGamePaused();

	final protected void onPause() {
		if (gameHandler != null) {
			gameHandler.onPause();
		}
		if (gameView != null) {
			gameView.setPaused(true);
		}
		super.onPause();
		if (setupSensors) {
			// 停止重力感应
			stopSensors();
		}
		onGamePaused();
	}

	final protected void onResume() {
		if (gameHandler != null) {
			gameHandler.onResume();
		}
		if (gameView != null) {
			gameView.setPaused(false);
		}
		super.onResume();
		if (setupSensors) {
			// 恢复重力感应
			initSensors();
		}
		onGameResumed();
	}

	protected void onDestroy() {
		try {
			if (gameView != null) {
				gameView.setRunning(false);
				Thread.sleep(16);
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

	protected void onStop() {
		try {
			if (gameView != null) {
				this.gameView.setPaused(true);
			}
			super.onStop();
		} catch (Exception e) {

		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		if (gameHandler != null) {
			if (gameHandler.onCreateOptionsMenu(menu)) {
				return true;
			}
		}
		return result;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = super.onOptionsItemSelected(item);
		if (gameHandler != null) {
			if (gameHandler.onOptionsItemSelected(item)) {
				return true;
			}
		}
		return result;
	}

	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
		if (gameHandler != null) {
			gameHandler.onOptionsMenuClosed(menu);
		}
	}

	public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
		if (gameHandler != null) {
			gameHandler.onAccuracyChanged(sensor, accuracy);
		}
	}

	public void onSensorChanged(android.hardware.SensorEvent event) {
		if (gameHandler != null) {
			gameHandler.onSensorChanged(event);
		}
	}

}