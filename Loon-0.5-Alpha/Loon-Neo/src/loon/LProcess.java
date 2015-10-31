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
package loon;

import java.util.ArrayList;
import java.util.LinkedList;

import loon.LGame.Status;
import loon.canvas.LColor;
import loon.event.Drawable;
import loon.event.GameKey;
import loon.event.GameTouch;
import loon.event.InputMake;
import loon.event.KeyMake;
import loon.event.MouseMake;
import loon.event.SysInputFactory;
import loon.event.TouchMake;
import loon.event.Updateable;
import loon.opengl.GLEx;
import loon.stage.PlayerUtils;
import loon.stage.RootPlayer;
import loon.stage.StageSystem;
import loon.stage.StageTransition;
import loon.utils.MathUtils;
import loon.utils.Scale;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.reply.Port;
import loon.utils.timer.LTimerContext;

public class LProcess extends PlayerUtils {

	ArrayList<Updateable> loads;

	ArrayList<Updateable> unloads;

	ArrayList<Drawable> drawings;

	EmulatorListener emulatorListener;

	private EmulatorButtons emulatorButtons;

	private final LinkedList<Screen> screens;

	private boolean isInstance;

	private int id, width, height;

	private boolean waitTransition;

	private boolean running;

	private Screen currentScreen, loadingScreen;

	private LTransition transition;

	private final SysInputFactory currentInput;

	protected final StageSystem stageSystem;

	protected final RootPlayer rootPlayer;

	private final LGame game;

	public LProcess(LGame game) {
		super();
		this.game = game;
		this.currentInput = new SysInputFactory(this);
		this.screens = new LinkedList<Screen>();
		this.clear();
		InputMake input = game.input();
		if (input != null) {
			if (!game.setting.emulateTouch && !game.isMobile()) {
				input.mouseEvents.connect(new MouseMake.ButtonSlot() {
					public void onEmit(MouseMake.ButtonEvent event) {
						currentInput.callMouse(event);
					}
				});
			} else {
				input.touchEvents.connect(new Port<TouchMake.Event[]>() {
					public void onEmit(TouchMake.Event[] events) {
						currentInput.callTouch(events);
					}
				});
			}
			input.keyboardEvents.connect(new KeyMake.KeyPort() {
				public void onEmit(KeyMake.KeyEvent e) {
					currentInput.callKey(e);
				}
			});
		}
		this.rootPlayer = new RootPlayer();
		this.stageSystem = new StageSystem(rootPlayer) {
			@Override
			protected StageTransition defaultPushTransition() {
				return newSlide();
			}

			@Override
			protected StageTransition defaultPopTransition() {
				return newSlide().right();
			}
		};
		game.status.connect(new Port<LGame.Status>() {

			@Override
			public void onEmit(Status event) {
				switch (event) {
				case EXIT:
					stop();
					break;
				case RESUME:
					LSystem.PAUSED = false;
					resume();
					break;
				case PAUSE:
					LSystem.PAUSED = true;
					pause();
					break;
				default:
					break;
				}
			}
		});
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
				running.action(null);
			}
		}
		loadCache = null;
	}

	public final SysInputFactory getCurrentSysInput() {
		return currentInput;
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
				// the date is not to delete
				// drawings.clear();
			}
		}
	}

	private void setScreen(final Screen screen, boolean put) {
		if (currentScreen != null && currentScreen.isOnLoadComplete()) {
			return;
		}
		synchronized (this) {
			if (screen == null) {
				this.isInstance = false;
				throw new RuntimeException(
						"Cannot create a [Screen] instance !");
			}
			if (!game.display().showLogo) {
				if (currentScreen != null) {
					setTransition(screen.onTransition());
				} else {
					LTransition transition = screen.onTransition();
					if (transition == null) {
						int rad = MathUtils.random(0, 4);
						switch (rad) {
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
						case 4:
							transition = LTransition
									.newFadeOvalIn(LColor.black);
							break;
						}
					}
					setTransition(transition);
				}
			}

			screen.setOnLoadState(false);
			if (currentScreen == null) {
				currentScreen = screen;
			} else {
				synchronized (currentScreen) {
					currentScreen.destroy();
					currentScreen = screen;
				}
			}
			this.isInstance = true;
			if (screen instanceof EmulatorListener) {
				setEmulatorListener((EmulatorListener) screen);
			} else {
				setEmulatorListener(null);
			}

			screen.onCreate(LSystem.viewSize.getWidth(),
					LSystem.viewSize.getHeight());

			RealtimeProcess process = new RealtimeProcess() {

				@Override
				public void run(long time) {
					if (!LSystem._base.display().showLogo) {
						startTransition();
						screen.setClose(false);
						screen.onLoad();
						screen.onLoaded();
						screen.setOnLoadState(true);
						endTransition();
						kill();
					}
				}
			};
			process.setDelay(0);

			RealtimeProcessManager.get().addProcess(process);

			if (put) {
				screens.add(screen);
			}
			loadingScreen = null;
		}
	}

	public void start() {
		if (!running) {
			if (loadingScreen != null) {
				setScreen(loadingScreen);
			}
			running = true;
		}
	}

	public void resize(Scale scale, int w, int h) {
		if (isInstance) {
			currentScreen.resize(scale, w, h);
		}
	}

	public void resume() {
		if (isInstance) {
			currentScreen.resume();
		}
	}

	public void pause() {
		if (isInstance) {
			currentScreen.pause();
		}
	}

	public void stop() {
		running = false;
		if (isInstance) {
			currentScreen.stop();
		}
		endTransition();
		if (isInstance) {
			isInstance = false;
			unloads.clear();
			drawings.clear();
			if (currentScreen != null) {
				currentScreen.destroy();
				currentScreen = null;
			}
			RealtimeProcessManager.get().close();
			LTextures.destroyAll();
			// LImage.disposeAll();
			// ScreenUtils.disposeAll();
			// GLMesh.disposeAll();
		}
		LSystem._base.log().debug("The Loon Game Engine is End");
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

	public void calls() {
		if (isInstance) {
			LTextureBatch.clearBatchCaches();
		}
	}

	public boolean next() {
		if (isInstance) {
			if (currentScreen.next()) {
				return true;
			}
		}
		return false;
	}

	public void runTimer(LTimerContext context) {
		if (isInstance) {
			if (waitTransition) {
				if (transition != null) {
					switch (transition.code) {
					default:
						if (!currentScreen.isOnLoadComplete()) {
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
				currentScreen.runTimer(context);
				return;
			}
		}
	}

	public void draw(GLEx g) {
		if (isInstance) {
			if (waitTransition) {
				if (transition != null) {
					if (transition.isDisplayGameUI) {
						currentScreen.createUI(g);
					}
					switch (transition.code) {
					default:
						if (!currentScreen.isOnLoadComplete()) {
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
				currentScreen.createUI(g);
				return;
			}
		}
	}

	public void drawEmulator(GLEx gl) {
		if (emulatorButtons != null) {
			emulatorButtons.draw(gl);
		}
	}

	public LColor getColor() {
		if (isInstance) {
			return currentScreen.getColor();
		}
		return null;
	}

	public LTexture getBackground() {
		if (isInstance) {
			return currentScreen.getBackground();
		}
		return null;
	}

	public int getRepaintMode() {
		if (isInstance) {
			return currentScreen.getRepaintMode();
		}
		return Screen.SCREEN_CANVAS_REPAINT;
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
						LSystem.viewSize.getWidth(),
						LSystem.viewSize.getHeight());
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
			if (isInstance) {
				currentScreen.setLock(true);
			}
		}
	}

	private final void endTransition() {
		if (transition != null) {
			switch (transition.code) {
			default:
				waitTransition = false;
				transition.close();
				break;
			case 1:
				if (transition.completed()) {
					waitTransition = false;
					transition.close();
				}
				break;
			}
			if (isInstance) {
				currentScreen.setLock(false);
			}
		} else {
			waitTransition = false;
		}
	}

	public float getX() {
		if (isInstance) {
			return currentScreen.getX();
		}
		return 0;
	}

	public float getY() {
		if (isInstance) {
			return currentScreen.getY();
		}
		return 0;
	}

	public synchronized Screen getScreen() {
		return currentScreen;
	}

	public void runFirstScreen() {
		int size = screens.size();
		if (size > 0) {
			Object o = screens.getFirst();
			if (o != currentScreen) {
				setScreen((Screen) o, false);
			}
		}
	}

	public void runLastScreen() {
		int size = screens.size();
		if (size > 0) {
			Object o = screens.getLast();
			if (o != currentScreen) {
				setScreen((Screen) o, false);
			}
		}
	}

	public void runPreviousScreen() {
		int size = screens.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (currentScreen == screens.get(i)) {
					if (i - 1 > -1) {
						setScreen(screens.get(i - 1), false);
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
				if (currentScreen == screens.get(i)) {
					if (i + 1 < size) {
						setScreen(screens.get(i + 1), false);
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
			if (currentScreen != o) {
				setScreen(screens.get(index), false);
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
		if (screen.handler == null) {
			screen.resetBase();
		}
		if (game.setting.isLogo && game.display().showLogo) {
			loadingScreen = screen;
		} else {
			setScreen(screen, true);
		}
	}

	public boolean isScreenTransitionCompleted() {
		return !waitTransition;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public void setCurrentScreen(final Screen screen) {
		if (screen != null) {
			this.isInstance = false;
			if (currentScreen != null) {
				currentScreen.destroy();
			}
			this.currentScreen = screen;
			currentScreen.setLock(false);
			currentScreen.setLocation(0, 0);
			currentScreen.setClose(false);
			currentScreen.setOnLoadState(true);
			if (screen.getBackground() != null) {
				currentScreen.setRepaintMode(Screen.SCREEN_BITMAP_REPAINT);
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

	public void keyDown(GameKey e) {
		if (isInstance) {
			currentScreen.keyPressed(e);
		}
	}

	public void keyUp(GameKey e) {
		if (isInstance) {
			currentScreen.keyReleased(e);
		}
	}

	public void keyTyped(GameKey e) {
		if (isInstance) {
			currentScreen.keyTyped(e);
		}
	}

	public void mousePressed(GameTouch e) {
		if (isInstance) {
			currentScreen.mousePressed(e);
		}
	}

	public void mouseReleased(GameTouch e) {
		if (isInstance) {
			currentScreen.mouseReleased(e);
		}
	}

	public void mouseMoved(GameTouch e) {
		if (isInstance) {
			currentScreen.mouseMoved(e);
		}
	}

	public void mouseDragged(GameTouch e) {
		if (isInstance) {
			currentScreen.mouseDragged(e);
		}
	}

	public RootPlayer getRootPlayer() {
		return rootPlayer;
	}

	public StageSystem getStageSystem() {
		return stageSystem;
	}

	public LGame getGame() {
		return game;
	}
}
