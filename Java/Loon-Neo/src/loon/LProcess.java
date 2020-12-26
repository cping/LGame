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

import loon.LGame.Status;
import loon.canvas.LColor;
import loon.events.GameKey;
import loon.events.GameTouch;
import loon.events.InputMake;
import loon.events.KeyMake;
import loon.events.MouseMake;
import loon.events.SysInputFactory;
import loon.events.TouchMake;
import loon.events.Updateable;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.opengl.ShaderSource;
import loon.utils.HelperUtils;
import loon.utils.ListMap;
import loon.utils.MathUtils;
import loon.utils.ObjectBundle;
import loon.utils.Resolution;
import loon.utils.TArray;
import loon.utils.processes.GameProcessType;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.reply.Port;
import loon.utils.timer.LTimerContext;

public class LProcess {

	protected TArray<Updateable> resumes;

	protected TArray<Updateable> loads;

	protected TArray<Updateable> unloads;

	protected EmulatorListener emulatorListener;

	private EmulatorButtons emulatorButtons;

	private final ListMap<CharSequence, Screen> _screenMap;

	private final TArray<Screen> _screens;

	private boolean isInstance;

	private int id;

	private boolean _waitTransition;

	private boolean _running;

	private Screen _currentScreen, _loadingScreen;

	private LTransition _transition;

	private LogDisplay _logDisplay;

	private final ObjectBundle _bundle;

	private final SysInputFactory _currentInput;

	private final LGame _game;

	public LProcess(LGame game) {
		super();
		this._game = game;
		LSetting setting = _game.setting;
		setting.updateScale();
		LSystem.viewSize.setSize(setting.width, setting.height);
		this._bundle = new ObjectBundle();
		this._currentInput = new SysInputFactory();
		this._screens = new TArray<Screen>();
		this._screenMap = new ListMap<CharSequence, Screen>();
		this.clear();
		InputMake input = game.input();
		if (input != null) {
			if (!game.setting.emulateTouch && !game.isMobile() && !game.input().hasTouch()) {
				input.mouseEvents.connect(new MouseMake.ButtonSlot() {
					@Override
					public void onEmit(MouseMake.ButtonEvent event) {
						_currentInput.callMouse(event);
					}
				});
			} else {
				input.touchEvents.connect(new Port<TouchMake.Event[]>() {
					@Override
					public void onEmit(TouchMake.Event[] events) {
						_currentInput.callTouch(events);
					}
				});
			}
			input.keyboardEvents.connect(new KeyMake.KeyPort() {
				@Override
				public void onEmit(KeyMake.KeyEvent e) {
					_currentInput.callKey(e);
				}
			});
		}
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

	public LProcess setShaderSource(ShaderSource src) {
		LSystem.setShaderSource(src);
		return this;
	}

	public ShaderSource getShaderSource() {
		return LSystem.getShaderSource();
	}

	private final static void callUpdateable(final TArray<Updateable> list) {
		synchronized (LProcess.class) {
			TArray<Updateable> loadCache;
			synchronized (list) {
				loadCache = new TArray<Updateable>(list);
				list.clear();
			}
			for (int i = 0, size = loadCache.size; i < size; i++) {
				Updateable r = loadCache.get(i);
				if (r == null) {
					continue;
				}
				synchronized (r) {
					try {
						r.action(null);
					} catch (Throwable cause) {
						LSystem.error("Updateable dispatch failure", cause);
					}
				}
			}
			loadCache = null;
		}
	}

	public final SysInputFactory getCurrentSysInput() {
		return _currentInput;
	}

	public void addResume(Updateable u) {
		synchronized (resumes) {
			resumes.add(u);
		}
	}

	public void removeResume(Updateable u) {
		synchronized (resumes) {
			resumes.remove(u);
		}
	}

	// --- Load start ---//

	public void addLoad(Updateable u) {
		synchronized (loads) {
			loads.add(u);
		}
	}

	public boolean containsLoad(Updateable u) {
		synchronized (loads) {
			return loads.contains(u);
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
			final int count = loads.size;
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

	public boolean containsUnLoad(Updateable u) {
		synchronized (unloads) {
			return unloads.contains(u);
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
			final int count = unloads.size;
			if (count > 0) {
				callUpdateable(unloads);
			}
		}
	}

	// --- UnLoad end ---//

	private void setScreen(final Screen screen, boolean put) {
		if (_loadingScreen != null && _loadingScreen.isOnLoadComplete()) {
			return;
		}
		try {
			synchronized (this) {
				if (screen == null) {
					this.isInstance = false;
					throw new LSysException("Cannot create a [Screen] instance !");
				}
				if (!_game.display().showLogo) {
					if (_currentScreen != null) {
						setTransition(screen.onTransition());
					} else {
						// * 为了防止画面单调,Loon默认为未设定Transition时,让首个Screen随机使用一次渐变
						// * 不想使用,或者需要自行设定的话，请重载Screen的onTransition函数。
						// * 不使用,返回: LTransition.newEmpty()
						// * 使用,返回: 设定或者自定义一个LTransition对象.
						LTransition _transition = screen.onTransition();
						if (_transition == null) {
							int rad = MathUtils.random(0, 12);
							switch (rad) {
							case 0:
								_transition = LTransition.newFadeIn();
								break;
							case 1:
								_transition = LTransition.newArc();
								break;
							case 2:
								_transition = LTransition.newSplitRandom(LColor.black);
								break;
							case 3:
								_transition = LTransition.newCrossRandom(LColor.black);
								break;
							case 4:
								_transition = LTransition.newFadeOvalIn(LColor.black);
								break;
							case 5:
								_transition = LTransition.newPixelWind(LColor.white);
								break;
							case 6:
								_transition = LTransition.newPixelDarkOut(LColor.black);
								break;
							case 7:
								_transition = LTransition.newPixelThunder(LColor.black);
								break;
							case 8:
								_transition = LTransition.newFadeDotIn(LColor.black);
								break;
							case 9:
								_transition = LTransition.newFadeTileIn(LColor.black);
								break;
							case 10:
								_transition = LTransition.newFadeSpiralIn(LColor.black);
								break;
							case 11:
								_transition = LTransition.newFadeSwipeIn(LColor.black);
								break;
							case 12:
								_transition = LTransition.newFadeBoardIn(LColor.black);
								break;
							}
						}
						setTransition(_transition);
					}
				}
				clearLog();
				screen.setOnLoadState(false);
				if (_currentScreen == null) {
					_currentScreen = screen;
				} else {
					killScreen(screen);
				}
				this.isInstance = true;

				if (screen instanceof EmulatorListener) {
					setEmulatorListener((EmulatorListener) screen);
				} else {
					setEmulatorListener(null);
				}

				screen.onCreate(LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());

				RealtimeProcess process = new RealtimeProcess() {

					@Override
					public void run(LTimerContext time) {
						if (_game != null && !_game.display().showLogo) {
							try {
								startTransition();
								screen.setClose(false);
								screen.resetOrder();
								screen.resetSize();
								screen.onLoad();
								screen.onLoaded();
								screen.setOnLoadState(true);
								screen.resume();
								endTransition();
							} catch (Throwable cause) {
								LSystem.error("Screen onLoad dispatch failed: " + screen, cause);
							} finally {
								kill();
							}
						}
					}
				};
				process.setProcessType(GameProcessType.Initialize);
				process.setDelay(0);

				RealtimeProcessManager.get().addProcess(process);

				if (put) {
					_screens.add(screen);
				}
				_loadingScreen = null;
			}
		} catch (Throwable cause) {
			LSystem.error("Update Screen failed: " + screen, cause);
		}
	}

	private void killScreen(Screen screen) {
		try {
			synchronized (_currentScreen) {
				if (_currentScreen != null) {
					_currentScreen.destroy();
				}
				if (screen == _currentScreen) {
					screen.pause();
				}
				screen.destroy();
				_currentScreen = screen;
			}
		} catch (Throwable cause) {
			LSystem.error("Destroy screen failure", cause);
		}
	}

	public void start() {
		if (!_running) {
			if (_loadingScreen != null) {
				setScreen(_loadingScreen);
			}
			_running = true;
		}
	}

	public void resize(int w, int h) {
		if (isInstance) {
			_currentInput.reset();
			_currentScreen.resetSize(w, h);
		}
	}

	public void resume() {
		if (isInstance) {
			final int count = resumes.size;
			if (count > 0) {
				callUpdateable(resumes);
			}
			_currentInput.reset();
			_currentScreen.resume();
		}
	}

	public void pause() {
		if (isInstance) {
			_currentInput.reset();
			_currentScreen.pause();
		}
	}

	public void stop() {
		try {
			synchronized (LProcess.class) {
				_running = false;
				if (isInstance) {
					_currentScreen.stop();
				}
				endTransition();
				if (isInstance) {
					isInstance = false;
					unloads.clear();
					if (_currentScreen != null) {
						_currentScreen.destroy();
						_currentScreen = null;
					}
					if (_game != null && _game.display() != null) {
						_game.assets().close();
						_game.display().close();
					}
					RealtimeProcessManager.get().dispose();
					LSTRDictionary.get().dispose();
					LSystem.disposeTextureAll();
					LSystem.stopRepaint();
					LSystem.freeStaticObject();
					if (_game != null) {
						_game.close();
					}
				}
			}
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
		LSystem.debug("The Loon Game Engine is End");
	}

	public void resetTouch() {
		_currentInput.resetSysTouch();
	}

	public void clear() {
		if (resumes == null) {
			resumes = new TArray<Updateable>(10);
		} else {
			resumes.clear();
		}
		if (loads == null) {
			loads = new TArray<Updateable>(10);
		} else {
			loads.clear();
		}
		if (unloads == null) {
			unloads = new TArray<Updateable>(10);
		} else {
			unloads.clear();
		}
		clearScreens();
	}

	public boolean next() {
		if (isInstance) {
			if (_currentScreen.next() && !LSystem.PAUSED) {
				return true;
			}
		}
		return false;
	}

	public void runTimer(LTimerContext context) {
		if (isInstance) {
			if (_waitTransition) {
				if (_transition != null) {
					switch (_transition.code) {
					default:
						if (!_currentScreen.isOnLoadComplete()) {
							_transition.update(context.timeSinceLastUpdate);
						}
						break;
					case 1:
						if (!_transition.completed()) {
							_transition.update(context.timeSinceLastUpdate);
						} else {
							endTransition();
						}
						break;
					}
				}
			} else {
				_currentScreen.runTimer(context);
				return;
			}
		}
	}

	public void draw(GLEx g) {
		if (isInstance) {
			if (_waitTransition) {
				if (_transition != null) {
					if (_transition.isDisplayGameUI) {
						_currentScreen.createUI(g);
					}
					switch (_transition.code) {
					default:
						if (!_currentScreen.isOnLoadComplete()) {
							_transition.draw(g);
						}
						break;
					case 1:
						if (!_transition.completed()) {
							_transition.draw(g);
						}
						break;
					}
				}
			} else {
				_currentScreen.createUI(g);
				return;
			}
		}
	}

	public void drawFrist(GLEx g) {
		if (isInstance && !_waitTransition) {
			_currentScreen.drawFrist(g);
		}
	}

	public void drawLast(GLEx g) {
		if (isInstance && !_waitTransition) {
			_currentScreen.drawLast(g);
		}
	}

	public void drawEmulator(GLEx gl) {
		if (emulatorButtons != null) {
			emulatorButtons.draw(gl);
		}
	}

	public LColor getBackgroundColor() {
		if (isInstance) {
			return _currentScreen.getBackgroundColor();
		}
		return null;
	}

	public float getScaleX() {
		if (isInstance) {
			return _currentScreen.getScaleX();
		}
		return 1f;
	}

	public float getScaleY() {
		if (isInstance) {
			return _currentScreen.getScaleY();
		}
		return 1f;
	}

	public boolean isFlipX() {
		if (isInstance) {
			return _currentScreen.isFlipX();
		}
		return false;
	}

	public boolean isFlipY() {
		if (isInstance) {
			return _currentScreen.isFlipY();
		}
		return false;
	}

	public float getRotation() {
		if (isInstance) {
			return _currentScreen.getRotation();
		}
		return 0;
	}

	public LTexture getBackground() {
		if (isInstance || _currentScreen != null) {
			return _currentScreen.getBackground();
		}
		return null;
	}

	public int getRepaintMode() {
		if (isInstance) {
			return _currentScreen.getRepaintMode();
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
				emulatorButtons = new EmulatorButtons(emulatorListener, LSystem.viewSize.getWidth(),
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

	public void setScreenID(int id) {
		if (isInstance) {
			_currentScreen.setID(id);
		}
	}

	public int getScreenID() {
		return isInstance ? -1 : _currentScreen.getID();
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	public final void setTransition(LTransition t) {
		this._transition = t;
	}

	public final boolean isTransitioning() {
		return _waitTransition;
	}

	public boolean isTransitionCompleted() {
		return !_waitTransition;
	}

	public final LTransition getTransition() {
		return this._transition;
	}

	private final void startTransition() {
		if (_transition != null) {
			_waitTransition = true;
			if (isInstance) {
				_currentScreen.setLock(true);
			}
		}
	}

	private final void endTransition() {
		if (_transition != null) {
			switch (_transition.code) {
			default:
				_waitTransition = false;
				_transition.close();
				break;
			case 1:
				if (_transition.completed()) {
					_waitTransition = false;
					_transition.close();
				}
				break;
			}
			if (isInstance) {
				_currentScreen.setLock(false);
			}
		} else {
			_waitTransition = false;
		}
	}

	public LColor getColor() {
		if (isInstance) {
			return _currentScreen.getColor();
		}
		return LColor.white;
	}

	public float getX() {
		if (isInstance) {
			return _currentScreen.getX();
		}
		return 0;
	}

	public float getY() {
		if (isInstance) {
			return _currentScreen.getY();
		}
		return 0;
	}

	private final static Vector2f _tmpLocaltion = new Vector2f();

	public Vector2f convertXY(float x, float y) {
		float newX = ((x - getX()) / (LSystem.getScaleWidth()));
		float newY = ((y - getY()) / (LSystem.getScaleHeight()));
		if (isInstance && _currentScreen.isTxUpdate()) {
			float oldW = getWidth();
			float oldH = getHeight();
			float newW = getWidth() * getScaleX();
			float newH = getHeight() * getScaleY();
			float offX = oldW / 2f - newW / 2f;
			float offY = oldH / 2f - newH / 2f;
			float nx = (newX - offX);
			float ny = (newY - offY);
			final int r = (int) getRotation();
			switch (r) {
			case -90:
				offX = oldH / 2f - newW / 2f;
				offY = oldW / 2f - newH / 2f;
				nx = (newX - offY);
				ny = (newY - offX);
				_tmpLocaltion.set(nx / getScaleX(), ny / getScaleY()).rotateSelf(-90);
				_tmpLocaltion.set(-(_tmpLocaltion.x - getWidth()), MathUtils.abs(_tmpLocaltion.y));
				break;
			case 0:
			case 360:
				_tmpLocaltion.set(nx / getScaleX(), ny / getScaleY());
				break;
			case 90:
				offX = oldH / 2f - newW / 2f;
				offY = oldW / 2f - newH / 2f;
				nx = (newX - offY);
				ny = (newY - offX);
				_tmpLocaltion.set(nx / getScaleX(), ny / getScaleY()).rotateSelf(90);
				_tmpLocaltion.set(-_tmpLocaltion.x, MathUtils.abs(_tmpLocaltion.y - getHeight()));
				break;
			case -180:
			case 180:
				_tmpLocaltion.set(nx / getScaleX(), ny / getScaleY()).rotateSelf(getRotation()).addSelf(getWidth(),
						getHeight());
				break;
			default: // 原则上不处理非水平角度的触点
				_tmpLocaltion.set(newX, newY);
				break;
			}
		} else {
			_tmpLocaltion.set(newX, newY);
		}
		if (isFlipX() || isFlipY()) {
			HelperUtils.local2Global(isFlipX(), isFlipY(), getWidth() / 2, getHeight() / 2, _tmpLocaltion.x,
					_tmpLocaltion.y, _tmpLocaltion);
			return _tmpLocaltion;
		}
		return _tmpLocaltion;
	}

	public Screen getScreen() {
		return _currentScreen;
	}

	public void clearScreens() {
		_screenMap.clear();
		for (Screen screen : _screens) {
			if (screen != null) {
				screen.destroy();
			}
		}
		_screens.clear();
	}

	public void clearScreenMaps() {
		_screenMap.clear();
	}

	public void addScreen(CharSequence name, Screen screen) {
		if (!_screenMap.containsKey(name)) {
			_screenMap.put(name, screen);
			addScreen(screen);
		}
	}

	public boolean containsScreen(CharSequence name) {
		return _screenMap.containsKey(name);
	}

	public Screen getScreen(CharSequence name) {
		Screen screen = _screenMap.get(name);
		if (screen != null) {
			return screen;
		}
		return null;
	}

	public Screen runScreenClassName(CharSequence name) {
		for (Screen screen : _screens) {
			if (screen != null) {
				if (name.equals(screen.getName())) {
					setScreen(screen);
					return screen;
				}
			}
		}
		return null;
	}

	public Screen runScreenName(CharSequence name) {
		for (Screen screen : _screens) {
			if (screen != null) {
				if (name.equals(screen.getScreenName())) {
					setScreen(screen);
					return screen;
				}
			}
		}
		return null;
	}

	public Screen runScreen(CharSequence name) {
		Screen screen = getScreen(name);
		if (screen != null) {
			setScreen(screen);
			return screen;
		}
		return null;
	}

	public void runPopScreen() {
		int size = _screens.size;
		if (size > 0) {
			Screen o = _screens.pop();
			if (o != _currentScreen) {
				setScreen(o, false);
			}
		}
	}

	public void runPeekScreen() {
		runLastScreen();
	}

	public void runFirstScreen() {
		int size = _screens.size;
		if (size > 0) {
			Screen o = _screens.first();
			if (o != _currentScreen) {
				setScreen(o, false);
			}
		}
	}

	public void runLastScreen() {
		int size = _screens.size;
		if (size > 0) {
			Screen o = _screens.last();
			if (o != _currentScreen) {
				setScreen(o, false);
			}
		}
	}

	public void runPreviousScreen() {
		int size = _screens.size;
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (_currentScreen == _screens.get(i)) {
					if (i - 1 > -1) {
						setScreen(_screens.get(i - 1), false);
						return;
					}
				}
			}
		}
	}

	public void runNextScreen() {
		int size = _screens.size;
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (_currentScreen == _screens.get(i)) {
					if (i + 1 < size) {
						setScreen(_screens.get(i + 1), false);
						return;
					}
				}
			}
		}
	}

	public void runIndexScreen(int index) {
		int size = _screens.size;
		if (size > 0 && index > -1 && index < size) {
			Object o = _screens.get(index);
			if (_currentScreen != o) {
				setScreen(_screens.get(index), false);
			}
		}
	}

	public boolean containsScreen(final Screen screen) {
		if (screen == null) {
			throw new LSysException("Cannot create a [IScreen] instance !");
		}
		return _screens.contains(screen);
	}

	public void addScreen(final Screen screen) {
		if (screen == null) {
			throw new LSysException("Cannot create a [IScreen] instance !");
		}
		if (!_screens.contains(screen)) {
			_screens.add(screen);
		}
	}

	public TArray<Screen> getScreens() {
		return _screens.cpy();
	}

	public int getScreenCount() {
		return _screens.size;
	}

	public void setScreen(final Screen screen) {
		if (screen.handler == null) {
			screen.resetOrder();
			screen.resetSize();
		}
		if (_game.setting.isLogo && _game.display().showLogo) {
			_loadingScreen = screen;
		} else {
			setScreen(screen, true);
		}
	}

	public int getHeight() {
		if (isInstance) {
			return _currentScreen.getHeight();
		}
		return 0;
	}

	public int getWidth() {
		if (isInstance) {
			return _currentScreen.getWidth();
		}
		return 0;
	}

	public void setCurrentScreen(final Screen screen) {
		setCurrentScreen(screen, true);
	}

	public void setCurrentScreen(final Screen screen, boolean closed) {
		if (screen != null) {
			this.isInstance = false;
			if (closed && _currentScreen != null) {
				_currentScreen.destroy();
			}
			this._currentScreen = screen;
			_currentScreen.setLock(false);
			_currentScreen.setLocation(0, 0);
			_currentScreen.setClose(false);
			_currentScreen.setOnLoadState(true);
			if (screen.getBackground() != null) {
				_currentScreen.setRepaintMode(Screen.SCREEN_TEXTURE_REPAINT);
			}
			this.isInstance = true;
			if (screen instanceof EmulatorListener) {
				setEmulatorListener((EmulatorListener) screen);
			} else {
				setEmulatorListener(null);
			}
			this._screens.add(screen);
		}
	}

	public void keyDown(GameKey e) {
		if (isInstance) {
			_currentScreen.keyPressed(e);
		}
	}

	public void keyUp(GameKey e) {
		if (isInstance) {
			_currentScreen.keyReleased(e);
		}
	}

	public void keyTyped(GameKey e) {
		if (isInstance) {
			_currentScreen.keyTyped(e);
		}
	}

	public void mousePressed(GameTouch e) {
		if (isInstance) {
			_currentScreen.mousePressed(e);
		}
	}

	public void mouseReleased(GameTouch e) {
		if (isInstance) {
			_currentScreen.mouseReleased(e);
		}
	}

	public void mouseMoved(GameTouch e) {
		if (isInstance) {
			_currentScreen.mouseMoved(e);
		}
	}

	public void mouseDragged(GameTouch e) {
		if (isInstance) {
			_currentScreen.mouseDragged(e);
		}
	}

	public void clearLog() {
		if (_logDisplay != null) {
			_logDisplay.clear();
		}
	}

	public void addLog(String mes, LColor col) {
		if (_logDisplay == null) {
			_logDisplay = new LogDisplay();
		}
		_logDisplay.addText(mes, col);
	}

	public void addLog(String mes) {
		if (_logDisplay == null) {
			_logDisplay = new LogDisplay();
		}
		_logDisplay.addText(mes);
	}

	public LogDisplay getLogDisplay() {
		return _logDisplay;
	}

	protected void paintLog(final GLEx g, int x, int y) {
		if (_logDisplay == null) {
			_logDisplay = new LogDisplay();
		}
		_logDisplay.paint(g, x, y);
	}

	public LProcess addBundle(String key, Object val) {
		_bundle.put(key, val);
		return this;
	}

	public LProcess removeBundle(String key) {
		_bundle.remove(key);
		return this;
	}

	public ObjectBundle getBundle() {
		return _bundle;
	}

	public Screen getCurrentScreen() {
		return _currentScreen;
	}

	public Resolution getOriginResolution() {
		if (_game != null && _game.setting != null) {
			return new Resolution(_game.setting.width, _game.setting.height);
		}
		return new Resolution(LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public Resolution getDisplayResolution() {
		if (_game != null && _game.setting != null) {
			return new Resolution(_game.setting.width_zoom, _game.setting.height_zoom);
		}
		return new Resolution(LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public String getOriginResolutionMode() {
		return getOriginResolution().matchMode();
	}

	public String getDisplayResolutionMode() {
		return getDisplayResolution().matchMode();
	}

	public LGame getGame() {
		return _game;
	}
}
