package loon.core.input;

import java.util.ArrayList;
import java.util.LinkedList;

import loon.core.Director;
import loon.core.EmulatorButtons;
import loon.core.EmulatorListener;
import loon.core.LSystem;
import loon.core.event.Drawable;
import loon.core.event.Updateable;
import loon.core.graphics.LColor;
import loon.core.graphics.LImage;
import loon.core.graphics.Screen;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.GLLoader;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextureBatch;
import loon.core.graphics.opengl.LTextures;
import loon.core.graphics.opengl.ScreenUtils;
import loon.core.timer.LTimerContext;
import loon.utils.MathUtils;

import android.view.View;

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
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
public class LProcess extends Director{

	ArrayList<Updateable> loads;

	ArrayList<Updateable> unloads;

	ArrayList<Drawable> drawings;

	EmulatorListener emulatorListener;

	EmulatorButtons emulatorButtons;

	private LinkedList<Screen> screens;

	private Screen currentControl, loading_Control;

	private boolean running, waitTransition, loading_complete = false;

	private boolean isInstance;

	private int id, width, height;

	private LInputFactory currentInput;

	private LTransition transition;

	public LProcess(View view, int width, int height) {
		this.width = width;
		this.height = height;
		this.screens = new LinkedList<Screen>();
		this.currentInput = new LInputFactory(this);
		view.setOnKeyListener(currentInput);
		view.setOnTouchListener(currentInput);
		clear();
	}

	public void clear() {
		if (loads == null) {
			loads = new ArrayList<Updateable>(10);
		} else {
			loads.clear();
		}
		if (unloads == null) {
			unloads = new ArrayList<Updateable>(10);
		} else {
			unloads.clear();
		}
		if (drawings == null) {
			drawings = new ArrayList<Drawable>(10);
		} else {
			drawings.clear();
		}
	}

	public void begin() {
		if (!running) {
			running = true;
		}
	}

	public void resize(int w, int h) {
		if (isInstance) {
			currentControl.resize(w, h);
		}
	}

	public void end() {
		if (running) {
			running = false;
		}
	}

	public LColor getColor() {
		if (isInstance) {
			return currentControl.getColor();
		}
		return null;
	}

	public void calls() {
		if (isInstance) {
			LTextureBatch.clearBatchCaches();
			currentControl.callEvents(true);
		}
	}

	public void onResume() {
		if (isInstance) {
			currentControl.onResume();
		}
	}

	public void onPause() {
		if (isInstance) {
			currentControl.onPause();
		}
	}

	public boolean next() {
		if (isInstance) {
			if (currentControl.next()) {
				return true;
			}
		} else {
			if (loading_complete && !LSystem.isLogo) {
				if (loading_Control != null) {
					loading_complete = false;
					setScreen(loading_Control);
				}
			}
			return false;
		}
		return false;
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

	private final static void callUpdateable(final ArrayList<Updateable> list) {
		ArrayList<Updateable> loadCache;
		synchronized (list) {
			loadCache = new ArrayList<Updateable>(list);
			list.clear();
		}
		for (int i = 0; i < loadCache.size(); i++) {
			Updateable running = loadCache.get(i);
			synchronized (running) {
				running.action();
			}
		}
		loadCache = null;
	}

	// --- Load start ---//

	public void addLoad(Updateable u) {
		synchronized (loads) {
			loads.add(u);
		}
	}

	public void removeLoad(Updateable u) {
		synchronized (loads) {
			loads.remove(u);
		}
	}

	public void removeAllLoad() {
		synchronized (loads) {
			loads.clear();
		}
	}

	public void load() {
		if (isInstance) {
			final int count = loads.size();
			if (count > 0) {
				callUpdateable(loads);
			}
		}
	}

	// --- Load end ---//

	// --- UnLoad start ---//

	public void addUnLoad(Updateable u) {
		synchronized (unloads) {
			unloads.add(u);
		}
	}

	public void removeUnLoad(Updateable u) {
		synchronized (unloads) {
			unloads.remove(u);
		}
	}

	public void removeAllUnLoad() {
		synchronized (unloads) {
			unloads.clear();
		}
	}

	public void unload() {
		if (isInstance) {
			final int count = unloads.size();
			if (count > 0) {
				callUpdateable(unloads);
			}
		}
	}

	// --- UnLoad end ---//

	// --- Drawable start ---//

	public void addDrawing(Drawable d) {
		synchronized (drawings) {
			drawings.add(d);
		}
	}

	public void removeDrawing(Drawable d) {
		synchronized (drawings) {
			drawings.remove(d);
		}
	}

	public void removeAllDrawing() {
		synchronized (drawings) {
			drawings.clear();
		}
	}

	public void drawable(long elapsedTime) {
		if (isInstance) {
			final int count = drawings.size();
			if (count > 0) {
				for (int i = 0; i < count; i++) {
					drawings.get(i).action(elapsedTime);
				}
				// not delete
				// drawings.clear();
			}
		}
	}

	// --- Drawable end ---//

	public void draw(GLEx g) {
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

	public void drawEmulator(GLEx gl) {
		if (emulatorButtons != null) {
			emulatorButtons.draw(gl);
		}
	}

	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		if (isInstance) {
			return currentControl.onCreateOptionsMenu(menu);
		}
		return true;
	}

	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		if (isInstance) {
			return currentControl.onOptionsItemSelected(item);
		}
		return true;
	}

	public void onOptionsMenuClosed(android.view.Menu menu) {
		if (isInstance) {
			currentControl.onOptionsMenuClosed(menu);
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

	public LTexture getBackground() {
		if (isInstance) {
			return currentControl.getBackground();
		}
		return null;
	}

	public int getRepaintMode() {
		if (isInstance) {
			return currentControl.getRepaintMode();
		}
		return Screen.SCREEN_NOT_REPAINT;
	}

	/**
	 * 设定模拟按钮监听器
	 * 
	 * @param emulatorListener
	 */
	public void setEmulatorListener(EmulatorListener emulator) {
		this.emulatorListener = emulator;
		if (emulatorListener != null) {
			if (emulatorButtons == null) {
				emulatorButtons = new EmulatorButtons(emulatorListener,
						LSystem.screenRect.width, LSystem.screenRect.height);
			} else {
				emulatorButtons.setEmulatorListener(emulator);
			}
		} else {
			emulatorButtons = null;
		}
	}

	/**
	 * 获得模拟器监听
	 * 
	 * @return
	 */
	public EmulatorListener getEmulatorListener() {
		return emulatorListener;
	}

	/**
	 * 获得模拟器按钮
	 * 
	 * @return
	 */
	public EmulatorButtons getEmulatorButtons() {
		return emulatorButtons;
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
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
			if (currentControl != null) {
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
			if (currentControl != null) {
				currentControl.setLock(false);
			}
		} else {
			waitTransition = false;
		}
	}

	public synchronized Screen getScreen() {
		return currentControl;
	}

	public void runFirstScreen() {
		int size = screens.size();
		if (size > 0) {
			Screen o = screens.getFirst();
			if (o != currentControl) {
				setScreen( o, false);
			}
		}
	}

	public void runLastScreen() {
		int size = screens.size();
		if (size > 0) {
			Screen o = screens.getLast();
			if (o != currentControl) {
				setScreen( o, false);
			}
		}
	}

	public void runPreviousScreen() {
		int size = screens.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (currentControl == screens.get(i)) {
					if (i - 1 > -1) {
						setScreen( screens.get(i - 1), false);
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
						setScreen( screens.get(i + 1), false);
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
				setScreen( screens.get(index), false);
			}
		}
	}

	public void addScreen(final Screen screen) {
		if (screen == null) {
			throw new RuntimeException("Cannot create a [IScreen] instance !");
		}
		screens.add(screen);
	}

	public LinkedList<Screen> getScreens() {
		return screens;
	}

	public int getScreenCount() {
		return screens.size();
	}

	public void setScreen(final Screen screen) {
		if (GLEx.gl == null) {
			loading_Control = screen;
			loading_complete = true;
		} else {
			setScreen(screen, true);
		}
	}

	private void setScreen(final Screen screen, final boolean put) {
		if (currentControl != null && currentControl.isOnLoadComplete()) {
			return;
		}
		synchronized (this) {
			if (screen == null) {
				this.isInstance = false;
				throw new RuntimeException(
						"Cannot create a [Screen] instance !");
			}
			GLLoader.destory();
			if (!LSystem.isLogo) {
				if (currentControl != null) {
					setTransition(screen.onTransition());
				} else {
					LTransition transition = screen.onTransition();
					if (transition == null) {
						switch (MathUtils.random(0, 3)) {
						case 0:
							transition = LTransition.newFadeIn();
							break;
						case 1:
							transition = LTransition.newArc();
							break;
						case 2:
							transition = LTransition
									.newSplitRandom(LColor.black);
							break;
						case 3:
							transition = LTransition
									.newCrossRandom(LColor.black);
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
				setEmulatorListener((EmulatorListener) screen);
			} else {
				setEmulatorListener(null);
			}

			screen.onCreate(LSystem.screenRect.width, LSystem.screenRect.height);

			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					for (; LSystem.isLogo;) {
						try {
							Thread.sleep(60);
						} catch (InterruptedException e) {
						}
					}
					startTransition();
					screen.setClose(false);
					screen.onLoad();
					screen.onLoaded();
					screen.setOnLoadState(true);
					endTransition();

				}
			};

			LSystem.callScreenRunnable(new Thread(runnable,"ProcessThread"));

			if (put) {
				screens.add(screen);
			}
			loading_Control = null;
		}
	}

	public void keyDown(LKey e) {
		if (isInstance) {
			currentControl.keyPressed(e);
		}
	}

	public void keyUp(LKey e) {
		if (isInstance) {
			currentControl.keyReleased(e);
		}
	}

	public void mousePressed(LTouch e) {
		if (isInstance) {
			currentControl.mousePressed(e);
		}
	}

	public void mouseReleased(LTouch e) {
		if (isInstance) {
			currentControl.mouseReleased(e);
		}
	}

	public void mouseMoved(LTouch e) {
		if (isInstance) {
			currentControl.mouseMoved(e);
		}
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public void onDestroy() {
		endTransition();
		if (isInstance) {
			isInstance = false;
			loads.clear();
			unloads.clear();
			drawings.clear();
			if (currentControl != null) {
				currentControl.destroy();
				currentControl = null;
			}
			LTextures.disposeAll();
			LImage.disposeAll();
			ScreenUtils.disposeAll();
		}
	}

	public void setCurrentScreen(final Screen screen) {
		if (screen != null) {
			this.isInstance = false;
			if (currentControl != null) {
				currentControl.destroy();
			}
			this.currentControl = screen;
			currentControl.setLock(false);
			currentControl.setLocation(0, 0);
			currentControl.setClose(false);
			currentControl.setOnLoadState(true);
			if (screen.getBackground() != null) {
				currentControl.setRepaintMode(Screen.SCREEN_BITMAP_REPAINT);
			}
			this.isInstance = true;
			if (screen instanceof EmulatorListener) {
				setEmulatorListener((EmulatorListener) screen);
			} else {
				setEmulatorListener(null);
			}
			this.screens.add(screen);
		}
	}

	public LInputFactory getInput() {
		return currentInput;
	}

}
