package org.loon.framework.android.game.core;

import java.util.LinkedList;

import org.loon.framework.android.game.LGameAndroid2DActivity;
import org.loon.framework.android.game.LGameAndroid2DView;
import org.loon.framework.android.game.LMode;
import org.loon.framework.android.game.core.geom.RectBox;
import org.loon.framework.android.game.core.graphics.LColor;
import org.loon.framework.android.game.core.graphics.LImage;
import org.loon.framework.android.game.core.graphics.Screen;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.core.timer.LTimerContext;
import org.loon.framework.android.game.media.AssetsSoundManager;
import org.loon.framework.android.game.media.PlaySoundManager;
import org.loon.framework.android.game.utils.GraphicsUtils;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

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
public class LHandler {

	private int width, height, maxWidth, maxHeight;

	private LGameAndroid2DActivity activity;

	private Context context;

	private Window window;

	private WindowManager windowManager;

	private AssetsSoundManager asm;

	private PlaySoundManager psm;

	private LGameAndroid2DView view;

	private Screen currentControl;

	private LinkedList<Screen> screens;

	private boolean isInstance;

	private LTransition transition;

	private boolean waitTransition;

	public LHandler(LGameAndroid2DActivity activity, LGameAndroid2DView view,
			boolean landscape, LMode mode) {
		try {

			this.activity = activity;

			if (landscape) {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			} else {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}

			this.context = activity.getApplicationContext();
			this.window = activity.getWindow();
			this.windowManager = activity.getWindowManager();
			this.view = view;
			this.screens = new LinkedList<Screen>();

			RectBox d = getScreenDimension();

			LSystem.SCREEN_LANDSCAPE = landscape;

			this.maxWidth = d.getWidth();
			this.maxHeight = d.getHeight();

			if (landscape && (d.getWidth() > d.getHeight())) {
				maxWidth = d.getWidth();
				maxHeight = d.getHeight();
			} else if (landscape && (d.getWidth() < d.getHeight())) {
				maxHeight = d.getWidth();
				maxWidth = d.getHeight();
			} else if (!landscape && (d.getWidth() < d.getHeight())) {
				maxWidth = d.getWidth();
				maxHeight = d.getHeight();
			} else if (!landscape && (d.getWidth() > d.getHeight())) {
				maxHeight = d.getWidth();
				maxWidth = d.getHeight();
			}

			if (mode != LMode.Max) {
				if (landscape) {
					this.width = LSystem.MAX_SCREEN_WIDTH;
					this.height = LSystem.MAX_SCREEN_HEIGHT;
				} else {
					this.width = LSystem.MAX_SCREEN_HEIGHT;
					this.height = LSystem.MAX_SCREEN_WIDTH;
				}
			} else {
				if (landscape) {
					this.width = maxWidth >= LSystem.MAX_SCREEN_WIDTH ? LSystem.MAX_SCREEN_WIDTH
							: maxWidth;
					this.height = maxHeight >= LSystem.MAX_SCREEN_HEIGHT ? LSystem.MAX_SCREEN_HEIGHT
							: maxHeight;
				} else {
					this.width = maxWidth >= LSystem.MAX_SCREEN_HEIGHT ? LSystem.MAX_SCREEN_HEIGHT
							: maxWidth;
					this.height = maxHeight >= LSystem.MAX_SCREEN_WIDTH ? LSystem.MAX_SCREEN_WIDTH
							: maxHeight;
				}
			}

			if (mode == LMode.Fill) {

				LSystem.scaleWidth = ((float) maxWidth) / width;
				LSystem.scaleHeight = ((float) maxHeight) / height;

			} else if (mode == LMode.FitFill) {

				RectBox res = GraphicsUtils.fitLimitSize(width, height,
						maxWidth, maxHeight);
				maxWidth = res.width;
				maxHeight = res.height;
				LSystem.scaleWidth = ((float) maxWidth) / width;
				LSystem.scaleHeight = ((float) maxHeight) / height;

			} else if (mode == LMode.Ratio) {

				maxWidth = View.MeasureSpec.getSize(maxWidth);
				maxHeight = View.MeasureSpec.getSize(maxHeight);

				float userAspect = (float) width / (float) height;
				float realAspect = (float) maxWidth / (float) maxHeight;

				if (realAspect < userAspect) {
					maxHeight = Math.round(maxWidth / userAspect);
				} else {
					maxWidth = Math.round(maxHeight * userAspect);
				}

				LSystem.scaleWidth = ((float) maxWidth) / width;
				LSystem.scaleHeight = ((float) maxHeight) / height;

			} else if (mode == LMode.MaxRatio) {

				maxWidth = View.MeasureSpec.getSize(maxWidth);
				maxHeight = View.MeasureSpec.getSize(maxHeight);

				float userAspect = (float) width / (float) height;
				float realAspect = (float) maxWidth / (float) maxHeight;

				if ((realAspect < 1 && userAspect > 1)
						|| (realAspect > 1 && userAspect < 1)) {
					userAspect = (float) height / (float) width;
				}

				if (realAspect < userAspect) {
					maxHeight = Math.round(maxWidth / userAspect);
				} else {
					maxWidth = Math.round(maxHeight * userAspect);
				}

				LSystem.scaleWidth = ((float) maxWidth) / width;
				LSystem.scaleHeight = ((float) maxHeight) / height;

			} else {

				LSystem.scaleWidth = 1;
				LSystem.scaleHeight = 1;

			}

			LSystem.screenRect = new RectBox(0, 0, width, height);

			StringBuffer sbr = new StringBuffer();
			sbr.append("Mode:").append(mode);
			sbr.append("\nWidth:").append(width).append(",Height:" + height);
			sbr.append("\nMaxWidth:").append(maxWidth)
					.append(",MaxWidth:" + maxHeight);
			sbr.append("\nScale:").append(isScale());
			Log.i("Android2DSize", sbr.toString());

		} catch (Exception ex) {

			Log.e("Android2DHandler", ex.getMessage());

		}
	}

	/**
	 * 判断当前游戏屏幕是否需要拉伸
	 * 
	 * @return
	 */
	public boolean isScale() {
		return LSystem.scaleWidth != 1 || LSystem.scaleHeight != 1;
	}

	/**
	 * 返回AssetsSoundManager
	 * 
	 * @return
	 */
	public AssetsSoundManager getAssetsSound() {
		if (this.asm == null) {
			this.asm = AssetsSoundManager.getInstance();
		}
		return asm;
	}

	/**
	 * 返回PlaySoundManager
	 * 
	 * @return
	 */
	public PlaySoundManager getPlaySound() {
		if (this.psm == null) {
			this.psm = new PlaySoundManager(context);
		}
		return psm;
	}

	/**
	 * 获得窗体实际坐标
	 * 
	 * @return
	 */
	public RectBox getScreenDimension() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		return new RectBox((int) dm.xdpi, (int) dm.ydpi, (int) dm.widthPixels,
				(int) dm.heightPixels);

	}

	public int getRepaintMode() {
		if (isInstance) {
			return currentControl.getRepaintMode();
		}
		return Screen.SCREEN_CANVAS_REPAINT;
	}

	public Bitmap getBackground() {
		if (isInstance) {
			return currentControl.getBackground();
		}
		return null;
	}

	public boolean next() {
		if (isInstance) {
			if (currentControl.next()) {
				return true;
			}
		}
		return false;
	}

	public void calls() {
		if (isInstance) {
			currentControl.callEvents();
		}
	}

	public void runTimer(LTimerContext context) {
		if (isInstance) {
			if (waitTransition) {
				if (transition != null) {

					switch (transition.code) {
					default:
						if (!currentControl.isOnLoadComplete()) {
							transition.update(context.timeSinceLastUpdate);
						}
						break;
					case 1:
						if (!transition.completed()) {
							transition.update(context.timeSinceLastUpdate);
						} else {
							endTransition();
						}
						break;
					}
				}
			} else {
				currentControl.runTimer(context);
				return;
			}
		}
	}

	public void draw(LGraphics g) {
		if (isInstance) {
			if (waitTransition) {
				if (transition != null) {
					if (transition.isDisplayGameUI) {
						currentControl.createUI(g);
					}
					switch (transition.code) {
					default:
						if (!currentControl.isOnLoadComplete()) {
							transition.draw(g);
						}
						break;
					case 1:
						if (!transition.completed()) {
							transition.draw(g);
						}
						break;
					}
				}
			} else {
				currentControl.createUI(g);
				return;
			}
		}
	}

	public final void setTransition(LTransition t) {
		this.transition = t;
	}

	public final LTransition getTransition() {
		return this.transition;
	}

	private final void startTransition() {
		if (transition != null) {
			waitTransition = true;
			if (isInstance) {
				currentControl.setLock(true);
			}
		}
	}

	private final void endTransition() {
		if (transition != null) {
			switch (transition.code) {
			default:
				waitTransition = false;
				transition.dispose();
				break;
			case 1:
				if (transition.completed()) {
					waitTransition = false;
					transition.dispose();
				}
				break;
			}
			if (isInstance) {
				currentControl.setLock(false);
			}
		} else {
			waitTransition = false;
		}
	}

	public float getX() {
		if (isInstance) {
			return currentControl.getX();
		}
		return 0;
	}

	public float getY() {
		if (isInstance) {
			return currentControl.getY();
		}
		return 0;
	}

	public synchronized Screen getScreen() {
		return currentControl;
	}

	public void runFirstScreen() {
		int size = screens.size();
		if (size > 0) {
			Object o = screens.getFirst();
			if (o != currentControl) {
				setScreen((Screen) o, false);
			}
		}
	}

	public void runLastScreen() {
		int size = screens.size();
		if (size > 0) {
			Object o = screens.getLast();
			if (o != currentControl) {
				setScreen((Screen) o, false);
			}
		}
	}

	public void runPreviousScreen() {
		int size = screens.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (currentControl == screens.get(i)) {
					if (i - 1 > -1) {
						setScreen((Screen) screens.get(i - 1), false);
						return;
					}
				}
			}
		}
	}

	public void runNextScreen() {
		int size = screens.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (currentControl == screens.get(i)) {
					if (i + 1 < size) {
						setScreen((Screen) screens.get(i + 1), false);
						return;
					}
				}
			}
		}
	}

	public void runIndexScreen(int index) {
		int size = screens.size();
		if (size > 0 && index > -1 && index < size) {
			Object o = screens.get(index);
			if (currentControl != o) {
				setScreen((Screen) screens.get(index), false);
			}
		}
	}

	public void addScreen(final Screen screen) {
		if (screen == null) {
			throw new RuntimeException("Cannot create a [IScreen] instance !");
		}
		screens.add(screen);
	}

	public void initScreen() {
		window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		window.requestFeature(android.view.Window.FEATURE_NO_TITLE);
		try {
			window.setBackgroundDrawable(null);
		} catch (Exception e) {
		}
	}

	public LinkedList<Screen> getScreens() {
		return screens;
	}

	public int getScreenCount() {
		return screens.size();
	}

	public void setScreen(final Screen screen) {
		setScreen(screen, true);
	}

	public void setScreen(final Screen screen, boolean put) {
		synchronized (this) {
			if (screen == null) {
				this.isInstance = false;
				throw new RuntimeException(
						"Cannot create a [Screen] instance !");
			}
			if (currentControl != null) {
				setTransition(screen.onTransition());
			} else {
				LTransition transition = screen.onTransition();
				if (transition == null) {
					switch (LSystem.getRandomBetWeen(0, 3)) {
					case 0:
						transition = LTransition.newFadeIn();
						break;
					case 1:
						transition = LTransition.newArc();
						break;
					case 2:
						transition = LTransition.newSplitRandom(LColor.black);
						break;
					case 3:
						transition = LTransition.newCrossRandom(LColor.black);
						break;
					}
				}
				setTransition(transition);
			}
		}
		screen.setOnLoadState(false);
		if (currentControl == null) {
			currentControl = screen;
		} else {
			synchronized (currentControl) {
				currentControl.destroy();
				currentControl = screen;
			}
		}
		this.isInstance = true;
		if (screen instanceof EmulatorListener) {
			view.update();
			view.setEmulatorListener((EmulatorListener) screen);
		} else {
			view.setEmulatorListener(null);
		}
		startTransition();
		screen.onCreate(LSystem.screenRect.width, LSystem.screenRect.height);
		Thread load = null;
		try {
			load = new Thread() {
				public void run() {

					screen.setClose(false);
					screen.onLoad();
					screen.setOnLoadState(true);
					screen.onLoaded();
					endTransition();
				}
			};
			load.setPriority(Thread.NORM_PRIORITY);
			load.start();
		} catch (Exception ex) {
			throw new RuntimeException(currentControl.getName() + " onLoad:"
					+ ex.getMessage());
		} finally {
			load = null;
		}
		if (screen != null) {
			if (screen instanceof LFlickerListener) {
				setFlicker((LFlickerListener) screen);
			}
		}
		if (put) {
			screens.add(screen);
		}
		Thread.yield();

	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public View getView() {
		return view;
	}

	public RectBox getScreenBox() {
		return LSystem.screenRect;
	}

	public LGameAndroid2DActivity getLGameActivity() {
		return activity;
	}

	public Context getContext() {
		return context;
	}

	public Window getWindow() {
		return window;
	}

	public WindowManager getWindowManager() {
		return windowManager;
	}

	private LFlicker flicker;

	private void setFlicker(LFlickerListener listener) {
		if (listener == null) {
			flicker = null;
			return;
		}
		if (flicker == null) {
			flicker = new LFlicker(listener);
		} else {
			flicker.setListener(listener);
		}
	}

	public boolean onTouchEvent(MotionEvent e) {
		if (isInstance) {
			try {
				if (flicker != null) {
					flicker.onTouchEvent(e);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				return currentControl.onTouchEvent(e);
			} catch (Exception ex) {

			}
		}
		return false;

	}

	public boolean onKeyDown(int keyCode, KeyEvent e) {
		if (isInstance) {
			try {
				return currentControl.onKeyDownEvent(keyCode, e);
			} catch (Exception ex) {

			}
		}
		return false;
	}

	public boolean onKeyUp(int keyCode, KeyEvent e) {
		if (isInstance) {
			try {
				return currentControl.onKeyUpEvent(keyCode, e);
			} catch (Exception ex) {

			}
		}
		return false;
	}

	public boolean onTrackballEvent(MotionEvent e) {
		if (isInstance) {
			try {
				return currentControl.onTrackballEvent(e);
			} catch (Exception ex) {

			}
		}
		return false;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		if (isInstance) {
			currentControl.onAccuracyChanged(sensor, accuracy);
		}
	}

	public void onSensorChanged(SensorEvent event) {
		if (isInstance) {
			currentControl.onSensorChanged(event);
		}
	}

	public void onPause() {
		if (isInstance) {
			currentControl.onPause();
		}
	}

	public void onResume() {
		if (isInstance) {
			currentControl.onResume();
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		if (isInstance) {
			return currentControl.onCreateOptionsMenu(menu);
		}
		return false;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (isInstance) {
			return currentControl.onOptionsItemSelected(item);
		}
		return false;
	}

	public void onOptionsMenuClosed(Menu menu) {
		if (isInstance) {
			currentControl.onOptionsMenuClosed(menu);
		}
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public Bitmap getImage() {
		if (view != null) {
			return view.getImage();
		}
		return null;
	}

	public void destroy() {
		if (isInstance) {
			isInstance = false;
			if (currentControl != null) {
				currentControl.destroy();
				currentControl = null;
			}
			LImage.disposeAll();
		}
	}
}
