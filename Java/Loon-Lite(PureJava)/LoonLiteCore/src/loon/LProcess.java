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

import java.util.Iterator;

import loon.action.camera.Viewport;
import loon.canvas.LColor;
import loon.component.layout.SplitLayout;
import loon.events.EventActionT;
import loon.events.GameKey;
import loon.events.GameTouch;
import loon.events.InputMake;
import loon.events.KeyMake;
import loon.events.MouseMake;
import loon.events.SysInputFactory;
import loon.events.SysInputFactoryImpl;
import loon.events.TouchMake;
import loon.events.Updateable;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.GLEx;
import loon.utils.HelperUtils;
import loon.utils.ListMap;
import loon.utils.MathUtils;
import loon.utils.ObjectBundle;
import loon.utils.Resolution;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.processes.GameProcessType;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.reply.Port;
import loon.utils.timer.LTimerContext;

/**
 * Screen的上级类,用于切换与存储多个Screen,并集中管理Screen功能
 */
public final class LProcess implements LRelease {

	private final class ScreenProcess extends RealtimeProcess {

		private final LGame _game;

		private final LProcess _process;

		private final Screen _newScreen;

		private final boolean _put;

		public ScreenProcess(final LGame g, final LProcess p, final Screen ns, final boolean put) {
			super("ScreenProcess", 0);
			this._game = g;
			this._process = p;
			this._newScreen = ns;
			this._put = put;
			this.setProcessType(GameProcessType.Initialize);
		}

		@Override
		public void run(final LTimerContext time) {
			if (_game != null && !_game.displayImpl.showLogo) {
				try {
					if (_newScreen != null) {
						_process.updateScreen(_newScreen);
						_process.startTransition();
						_newScreen.restart();
						_process.endTransition();
						_process.setCurrentScreen(false, _newScreen, false, _put);
					}
				} catch (Throwable cause) {
					LSystem.error("The New Screen onLoad dispatch failed: " + _newScreen, cause);
				} finally {
					_process.beginProcess();
					kill();
				}
			}
		}
	}

	protected TArray<Updateable> resumes;

	protected TArray<Updateable> loads;

	protected TArray<Updateable> unloads;

	protected EmulatorListener emulatorListener;

	private EmulatorButtons _emulatorButtons;

	private final ListMap<CharSequence, Screen> _screenMap;

	private final ScreenExitEffect _exitEffect = new ScreenExitEffect();

	private final Vector2f _pointLocaltion = new Vector2f();

	private final Vector2f _offsetTouch = new Vector2f();

	private final ObjectBundle _bundle;

	private final LGame _game;

	private TArray<Updateable> _loadcaches;

	private ScreenProcess _screenProcess;

	private boolean _isInstance;

	private boolean _waitTransition;

	private boolean _screenLoading;

	private boolean _screenAllowSyncTouch;

	private int _curId;

	private float _touchScaleX = 1f, _touchScaleY = 1f;

	private Screen _currentScreen, _loadingScreen;

	private LTransition _transition;

	private SysInputFactory _currentInput;

	public LProcess(final LGame game) {
		this._game = game;
		this._screenMap = new ListMap<CharSequence, Screen>();
		this._bundle = new ObjectBundle();
		this.initSetting();
	}

	public LProcess initSetting() {
		if (_game != null) {
			LSetting setting = _game.setting;
			setting.updateScale();
			setInputFactory(null);
			clearProcess();
		}
		this._isInstance = false;
		this._waitTransition = false;
		this._screenLoading = false;
		this._screenAllowSyncTouch = true;
		return this;
	}

	public boolean isScreenAllowSyncTouch() {
		return this._screenAllowSyncTouch;
	}

	public LProcess setScreenAllowSyncTouch(final boolean t) {
		this._screenAllowSyncTouch = t;
		return this;
	}

	public LProcess setInputFactory(final SysInputFactory factory) {
		if (factory == null) {
			this._currentInput = new SysInputFactoryImpl();
		} else {
			this._currentInput = factory;
		}
		InputMake input = _game.input();
		if (input != null) {
			if (input.mouseEvents.hasConnections()) {
				input.mouseEvents.clearConnections();
			}
			if (input.touchEvents.hasConnections()) {
				input.touchEvents.clearConnections();
			}
			if (input.keyboardEvents.hasConnections()) {
				input.keyboardEvents.clearConnections();
			}
			if (input != null) {

				if (!_game.setting.emulateTouch && !_game.isMobile() && !_game.input().hasTouch()) {
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
		}
		return this;
	}

	public LProcess setFilterTouch(final EventActionT<GameTouch> t) {
		if (this._currentInput != null) {
			this._currentInput.setFilterTouch(t);
		}
		return this;
	}

	public SysInputFactory getSysInputFactory() {
		return this._currentInput;
	}

	private final void callUpdateable(final TArray<Updateable> list) {
		synchronized (LProcess.class) {
			synchronized (list) {
				if (_loadcaches == null) {
					_loadcaches = new TArray<Updateable>(list);
				} else if (_loadcaches.size == list.size) {
					_loadcaches.fill(list);
				} else {
					_loadcaches.clear();
					_loadcaches.addAll(list);
				}
			}
			list.clear();
		}
		for (int i = 0, size = _loadcaches.size; i < size; i++) {
			final Updateable r = _loadcaches.get(i);
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
	}

	public final SysInputFactory getCurrentSysInput() {
		return _currentInput;
	}

	public boolean addResume(final Updateable u) {
		if (u == null) {
			return false;
		}
		synchronized (resumes) {
			return resumes.add(u);
		}
	}

	public boolean removeResume(final Updateable u) {
		if (u == null) {
			return false;
		}
		synchronized (resumes) {
			return resumes.remove(u);
		}
	}

	// --- Load start ---//

	public boolean addLoad(final Updateable u) {
		if (u == null) {
			return false;
		}
		synchronized (loads) {
			return loads.add(u);
		}
	}

	public boolean containsLoad(final Updateable u) {
		if (u == null) {
			return false;
		}
		synchronized (loads) {
			return loads.contains(u);
		}
	}

	public boolean removeLoad(final Updateable u) {
		if (u == null) {
			return false;
		}
		synchronized (loads) {
			return loads.remove(u);
		}
	}

	public LProcess removeAllLoad() {
		synchronized (loads) {
			loads.clear();
		}
		return this;
	}

	public LProcess load() {
		if (_isInstance) {
			final int count = loads.size;
			if (count > 0) {
				callUpdateable(loads);
			}
		}
		return this;
	}

	// --- Load end ---//

	// --- UnLoad start ---//

	public boolean addUnLoad(final Updateable u) {
		if (u == null) {
			return false;
		}
		synchronized (unloads) {
			return unloads.add(u);
		}
	}

	public boolean containsUnLoad(final Updateable u) {
		if (u == null) {
			return false;
		}
		synchronized (unloads) {
			return unloads.contains(u);
		}
	}

	public boolean removeUnLoad(final Updateable u) {
		if (u == null) {
			return false;
		}
		synchronized (unloads) {
			return unloads.remove(u);
		}
	}

	public LProcess removeAllUnLoad() {
		synchronized (unloads) {
			unloads.clear();
		}
		return this;
	}

	public LProcess unload() {
		if (_isInstance) {
			final int count = unloads.size;
			if (count > 0) {
				callUpdateable(unloads);
			}
		}
		return this;
	}

	// --- UnLoad end ---//

	private void setScreen(final Screen newScreen, final boolean put) {
		if (checkWaiting()) {
			return;
		}
		if (_loadingScreen != null && _loadingScreen.isOnLoadComplete()) {
			return;
		}
		try {
			synchronized (this) {
				if (newScreen == null) {
					this._isInstance = false;
					throw new LSysException("Cannot create a [Screen] instance !");
				}
				if (!_game.displayImpl.showLogo) {
					if (_currentScreen != null) {
						setTransition(newScreen.onTransition());
					} else {
						// * 为了防止画面单调,Loon默认为未设定Transition时,让首个Screen随机使用一次渐变
						// * 不想使用,或者需要自行设定的话，请重载Screen的onTransition函数。
						// * 不使用,返回: LTransition.newEmpty()
						// * 使用,返回: 设定或者自定义一个LTransition对象.
						LTransition randTransition = newScreen.onTransition();
						if (randTransition == null) {
							int rad = MathUtils.random(0, 14);
							switch (rad) {
							case 0:
								randTransition = LTransition.newFadeIn();
								break;
							case 1:
								randTransition = LTransition.newFadeArcIn();
								break;
							case 2:
								randTransition = LTransition.newSplitRandom(LColor.black);
								break;
							case 3:
								randTransition = LTransition.newCrossRandom(LColor.black);
								break;
							case 4:
								randTransition = LTransition.newFadeOvalIn(LColor.black);
								break;
							case 5:
								randTransition = LTransition.newPixelWind(LColor.white);
								break;
							case 6:
								randTransition = LTransition.newPixelDarkOut(LColor.black);
								break;
							case 7:
								randTransition = LTransition.newPixelThunder(LColor.black);
								break;
							case 8:
								randTransition = LTransition.newFadeDotIn(LColor.black);
								break;
							case 9:
								randTransition = LTransition.newFadeTileIn(LColor.black);
								break;
							case 10:
								randTransition = LTransition.newFadeSpiralIn(LColor.black);
								break;
							case 11:
								randTransition = LTransition.newFadeSwipeIn(LColor.black);
								break;
							case 12:
								randTransition = LTransition.newFadeBoardIn(LColor.black);
								break;
							case 13:
								randTransition = LTransition.newOvalHollowIn(LColor.black);
								break;
							case 14:
								randTransition = LTransition.newFadeDoorIrregularIn(LColor.black);
								break;
							}
						}
						setTransition(randTransition);
					}
				}
				_game.displayImpl.clearLog();
				if (_screenProcess != null) {
					_screenProcess.kill();
					if (RealtimeProcessManager.get().containsProcess(_screenProcess)) {
						RealtimeProcessManager.get().delete(_screenProcess);
					}
				}
				endProcess();
				RealtimeProcessManager.get()
						.addProcess((_screenProcess = new ScreenProcess(_game, this, newScreen, put)));
				_loadingScreen = null;
			}
		} catch (Throwable cause) {
			LSystem.error("Update New Screen failed: " + newScreen, cause);
		}
	}

	protected void beginProcess() {
		_isInstance = true;
	}

	protected void endProcess() {
		_isInstance = false;
	}

	protected void updateScreen(final Screen newScreen) {
		this.closedScreen(_currentScreen, newScreen);
		this._currentScreen = newScreen;
	}

	protected void closedScreen(final Screen newScreen) {
		closedScreen(_currentScreen, newScreen);
	}

	protected void closedScreen(final Screen oldScreen, final Screen newScreen) {
		try {
			if (oldScreen != null && oldScreen != newScreen) {
				oldScreen.destroy();
			} else if (oldScreen != null) {
				oldScreen.setLock(true);
				oldScreen.pause();
			} else if (_currentScreen == null && oldScreen == null) {
				_currentScreen = newScreen;
			}
		} catch (Throwable cause) {
			LSystem.error("Destroy screen failure", cause);
		}
	}

	public LProcess start() {
		if (!_screenLoading) {
			if (_loadingScreen != null) {
				setScreen(_loadingScreen);
			}
			_screenLoading = true;
		}
		return this;
	}

	public LProcess resize(final int w, final int h) {
		if (_isInstance) {
			if (_emulatorButtons != null) {
				_emulatorButtons.updateSize(w, h);
			}
			_currentInput.reset();
			_currentScreen.resetSize(w, h);
		}
		return this;
	}

	public LProcess resume() {
		if (_isInstance) {
			final int count = resumes.size;
			if (count > 0) {
				callUpdateable(resumes);
			}
			_currentInput.reset();
			_currentScreen.resume();
		}
		return this;
	}

	public LProcess pause() {
		if (_isInstance) {
			_currentInput.reset();
			_currentScreen.pause();
		}
		return this;
	}

	public LProcess resetTouch() {
		_currentInput.resetSysTouch();
		return this;
	}

	public LProcess clearProcess() {
		if (resumes == null) {
			resumes = new TArray<Updateable>();
		} else {
			resumes.clear();
		}
		if (loads == null) {
			loads = new TArray<Updateable>();
		} else {
			loads.clear();
		}
		if (unloads == null) {
			unloads = new TArray<Updateable>();
		} else {
			unloads.clear();
		}
		clearScreens();
		return this;
	}

	public boolean next() {
		if (_isInstance) {
			if (_currentScreen.next() && !LSystem.PAUSED) {
				return true;
			}
		}
		return false;
	}

	public void runTimer(final LTimerContext context) {
		if (_isInstance) {
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
							if (_currentScreen != null) {
								_currentScreen.setLock(true);
							}
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

	public void draw(final GLEx g) {
		if (_isInstance) {
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
			}
		}
	}

	public void drawFrist(final GLEx g) {
		if (_isInstance && !_waitTransition) {
			_currentScreen.drawFrist(g);
		}
	}

	public void drawLast(final GLEx g) {
		if (_isInstance && !_waitTransition) {
			_currentScreen.drawLast(g);
		}
	}

	public void drawEmulator(final GLEx gl) {
		if (_emulatorButtons != null) {
			_emulatorButtons.draw(gl);
		}
	}

	public LColor getBackgroundColor() {
		if (_isInstance) {
			return _currentScreen.getBackgroundColor();
		}
		return null;
	}

	public float getScaleX() {
		if (_isInstance) {
			return _currentScreen.getScaleX();
		}
		return 1f;
	}

	public float getScaleY() {
		if (_isInstance) {
			return _currentScreen.getScaleY();
		}
		return 1f;
	}

	public boolean isFlipX() {
		if (_isInstance) {
			return _currentScreen.isFlipX();
		}
		return false;
	}

	public boolean isFlipY() {
		if (_isInstance) {
			return _currentScreen.isFlipY();
		}
		return false;
	}

	public float getRotation() {
		if (_isInstance) {
			return _currentScreen.getRotation();
		}
		return 0;
	}

	public LTexture getBackground() {
		if (_isInstance || _currentScreen != null) {
			return _currentScreen.getBackground();
		}
		return null;
	}

	public int getRepaintMode() {
		if (_isInstance) {
			return _currentScreen.getRepaintMode();
		}
		return Screen.SCREEN_NOT_REPAINT;
	}

	/**
	 * 设定模拟按钮监听器
	 * 
	 * @param emulatorListener
	 */
	public LProcess setEmulatorListener(final EmulatorListener emulator) {
		this.emulatorListener = emulator;
		if (emulatorListener != null) {
			if (_emulatorButtons == null) {
				_emulatorButtons = new EmulatorButtons(emulatorListener, LSystem.viewSize.getWidth(),
						LSystem.viewSize.getHeight());
			} else {
				_emulatorButtons.setEmulatorListener(emulator);
			}
		} else {
			_emulatorButtons = null;
		}
		return this;
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
		return _emulatorButtons;
	}

	public LProcess setScreenID(final int curId) {
		if (_isInstance) {
			_currentScreen.setID(curId);
		}
		return this;
	}

	public int getScreenID() {
		return _isInstance ? -1 : _currentScreen.getID();
	}

	public LProcess setID(int i) {
		this._curId = i;
		return this;
	}

	public int getID() {
		return _curId;
	}

	public final LProcess setTransition(final LTransition t) {
		this._transition = t;
		return this;
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
			if (_isInstance) {
				_currentScreen.setLock(true);
			}
		} else if (_currentScreen != null) {
			_currentScreen.setLock(true);
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
			if (_isInstance) {
				_currentScreen.setLock(false);
			}
		} else {
			_waitTransition = false;
			if (_currentScreen != null) {
				_currentScreen.setLock(false);
			}
		}
	}

	public LColor getColor() {
		if (_isInstance) {
			return _currentScreen.getColor();
		}
		return LColor.white;
	}

	public float getX() {
		if (_isInstance) {
			return _currentScreen.getX();
		}
		return 0;
	}

	public float getY() {
		if (_isInstance) {
			return _currentScreen.getY();
		}
		return 0;
	}

	protected Vector2f convertYX(final Viewport view, final float fx, final float fy, float x, float y, float w,
			float h, Vector2f o) {
		final float newX = MathUtils.ifloor((x - (fx + view.getX())) / view.getScaleX());
		final float newY = MathUtils.ifloor((y - (fy + view.getY())) / view.getScaleY());
		float oldW = w;
		float oldH = h;
		float newW = view.getDisplayWidth();
		float newH = view.getDisplayHeight();
		float offX = fx - view.getX() + (oldW - newW);
		float offY = fy - view.getY() + (oldH - newH);
		final int r = MathUtils.ifloor(view.getAngle());
		switch (r) {
		case 0:
		case 360:
			break;
		case -90:
		case 90:
			view.getView().transformPoint(o, o);
			if (!view.isScaled()) {
				o.set(MathUtils.abs(view.getWidth() - o.x), MathUtils.abs(view.getHeight() - o.y));
			}
			break;
		case -180:
		case 180:
			view.getView().transformPoint(o, o);
			break;
		default: // 原则上不处理非水平角度的触点
			float rad = MathUtils.toRadians(view.getAngle());
			float sin = MathUtils.sin(rad);
			float cos = MathUtils.cos(rad);
			float dx = offX * view.getScaleX();
			float dy = offY * view.getScaleY();
			float dx2 = cos * dx - sin * dy;
			float dy2 = sin * dx + cos * dy;
			o.x = view.getWidth() - (newX - dx2);
			o.y = view.getHeight() - (newY - dy2);
			break;
		}
		if (view.isScaled() && !view.isRotated()) {
			o.x = MathUtils.abs(offX + o.x);
			o.y = MathUtils.abs(offY + o.y);
		} else if (view.isScaled() && view.isRotated()) {
			RectBox rect = view.getViewWorld();
			newW = rect.getWidth();
			newH = rect.getHeight();
			offX = rect.getX() + ((newW) / 2f);
			offY = rect.getY() + ((newH) / 2f);
			o.set(MathUtils.abs(offX + o.x), MathUtils.abs(offY + o.y));
		}
		if (r == 0 || r == 360) {
			o.addSelf(-view.getX(), -view.getY());
		}
		return o;
	}

	public Vector2f convertXY(final float fx, final float fy, float x, float y) {
		final float newX = MathUtils.ifloor(((x - fx) / (LSystem.getScaleWidth())));
		final float newY = MathUtils.ifloor(((y - fy) / (LSystem.getScaleHeight())));
		if (_isInstance && _currentScreen.isTxUpdate()) {
			if (_currentScreen.isPosOffsetUpdate()) {
				float posX = x / LSystem.getScaleWidth();
				float posY = y / LSystem.getScaleHeight();
				_pointLocaltion.set((posX - fx) - fx, (posY - fy) - fy);
			} else {
				float oldW = getWidth();
				float oldH = getHeight();
				float newW = getWidth() * getScaleX();
				float newH = getHeight() * getScaleY();
				float offX = fx + (oldW - newW) / 2f;
				float offY = fy + (oldH - newH) / 2f;
				float posX = (newX - offX);
				float posY = (newY - offY);
				final int r = MathUtils.ifloor(getRotation());
				switch (r) {
				case -90:
					offX = fx + (oldH - newW) / 2f;
					offY = fy + (oldW - newH) / 2f;
					posX = (newX - offY);
					posY = (newY - offX);
					_pointLocaltion.set(posX / getScaleX(), posY / getScaleY()).rotateSelf(-90);
					_pointLocaltion.set(-(_pointLocaltion.x - getWidth()), MathUtils.abs(_pointLocaltion.y));
					break;
				case 0:
				case 360:
					_pointLocaltion.set(posX / getScaleX(), posY / getScaleY());
					break;
				case 90:
					offX = fx + (oldH - newW) / 2f;
					offY = fy + (oldW - newH) / 2f;
					posX = (newX - offY);
					posY = (newY - offX);
					_pointLocaltion.set(posX / getScaleX(), posY / getScaleY()).rotateSelf(90);
					_pointLocaltion.set(-_pointLocaltion.x, MathUtils.abs(_pointLocaltion.y - getHeight()));
					break;
				case -180:
				case 180:
					_pointLocaltion.set(posX / getScaleX(), posY / getScaleY()).rotateSelf(getRotation()).addSelf(
							getWidth() - fx / getScaleX() - fx / LSystem.getScaleWidth(),
							getHeight() - fy / getScaleY() - fy / LSystem.getScaleHeight());
					break;
				default: // 原则上不处理非水平角度的触点
					float rad = MathUtils.toRadians(getRotation());
					float sin = MathUtils.sin(rad);
					float cos = MathUtils.cos(rad);
					float dx = offX / getScaleX();
					float dy = offY / getScaleY();
					float dx2 = cos * dx - sin * dy;
					float dy2 = sin * dx + cos * dy;
					_pointLocaltion.x = getWidth() - (newX - dx2);
					_pointLocaltion.y = getHeight() - (newY - dy2);
					break;
				}
			}
		} else {
			_pointLocaltion.set(newX, newY);
		}
		_pointLocaltion.mulSelf(_touchScaleX, _touchScaleY).addSelf(_offsetTouch);
		if (isFlipX() || isFlipY()) {
			HelperUtils.local2Global(isFlipX(), isFlipY(), fx + getWidth() / 2, fy + getHeight() / 2, _pointLocaltion.x,
					_pointLocaltion.y, _pointLocaltion);
			if (_isInstance) {
				Viewport viewport = _currentScreen.getViewport();
				if (viewport != null) {
					convertYX(viewport, getX(), getY(), getWidth(), getHeight(), _pointLocaltion.x, _pointLocaltion.y,
							_pointLocaltion);
				}
			}
			return _pointLocaltion;
		}
		if (_isInstance) {
			Viewport viewport = _currentScreen.getViewport();
			if (viewport != null) {
				convertYX(viewport, getX(), getY(), getWidth(), getHeight(), _pointLocaltion.x, _pointLocaltion.y,
						_pointLocaltion);
			}
		}
		return _pointLocaltion;
	}

	public Vector2f convertXY(final float x, final float y) {
		if (_screenAllowSyncTouch) {
			return convertXY(getX(), getY(), x, y);
		}
		return _pointLocaltion.set(x, y);
	}

	public Screen getRootScreen() {
		return _screenMap.size == 0 ? null : _screenMap.getValueAt(0);
	}

	public String getCurrentScreenName() {
		return _currentScreen == null ? LSystem.UNKNOWN : _currentScreen.getScreenName();
	}

	public Screen getScreen() {
		return _currentScreen;
	}

	public boolean isCurrentScreen(final Screen src) {
		return _currentScreen == src;
	}

	public LProcess clearScreens() {
		for (Iterator<Screen> it = _screenMap.iterator(); it.hasNext();) {
			final Screen screen = it.next();
			if (screen != null) {
				screen.destroy();
			}
		}
		_screenMap.clear();
		return this;
	}

	public LProcess addScreen(final CharSequence name,final Screen screen) {
		if (screen == null) {
			throw new LSysException("Cannot create a Screen instance !");
		}
		final CharSequence key = StringUtils.isEmpty(name) ? LSystem.UNKNOWN : name;
		if (!_screenMap.containsKey(key)) {
			_screenMap.put(key, screen);
		}
		return this;
	}

	public LProcess addScreen(final Screen screen) {
		if (screen == null) {
			throw new LSysException("Cannot create a Screen instance !");
		}
		if (!_screenMap.containsValue(screen)) {
			addScreen(screen.getName(), screen);
		}
		return this;
	}

	public boolean containsScreen(final CharSequence name) {
		return _screenMap.containsKey(name);
	}

	public boolean containsScreenValue(final Screen screen) {
		return _screenMap.containsValue(screen);
	}

	public Screen getScreen(final CharSequence name) {
		return _screenMap.get(name);
	}

	public Screen runScreenClassName(final CharSequence name) {
		for (Iterator<Screen> it = _screenMap.iterator(); it.hasNext();) {
			final Screen screen = it.next();
			if (screen != null) {
				if (name.equals(screen.getName())) {
					setScreen(screen, false);
					return screen;
				}
			}
		}
		return _currentScreen;
	}

	public Screen runScreenName(final CharSequence name) {
		for (Iterator<Screen> it = _screenMap.iterator(); it.hasNext();) {
			final Screen screen = it.next();
			if (screen != null) {
				if (name.equals(screen.getScreenName())) {
					setScreen(screen, false);
					return screen;
				}
			}
		}
		return _currentScreen;
	}

	public Screen toggleScreen(final CharSequence name) {
		final Screen screen = getScreen(name);
		if (screen != null && screen != _currentScreen) {
			setScreen(screen, false);
			return screen;
		}
		return _currentScreen;
	}

	public Screen runScreen(final CharSequence name) {
		final Screen screen = getScreen(name);
		if (screen != null) {
			setScreen(screen, false);
			return screen;
		}
		return _currentScreen;
	}

	public Screen runIndexScreen(final int index) {
		final int size = _screenMap.size;
		if (size > 0 && index > -1 && index < size) {
			final Screen screen = _screenMap.getValueAt(index);
			if (_currentScreen != screen) {
				setScreen(screen, false);
				return screen;
			}
		}
		return _currentScreen;
	}

	public LProcess runPopScreen() {
		final int size = _screenMap.size;
		if (size > 0) {
			Screen o = _screenMap.pop();
			if (o != _currentScreen) {
				setScreen(o, false);
			}
		}
		return this;
	}

	public LProcess runPeekScreen() {
		return runLastScreen();
	}

	public LProcess runFirstScreen() {
		final int size = _screenMap.size;
		if (size > 0) {
			final Screen o = _screenMap.first();
			if (o != _currentScreen) {
				setScreen(o, false);
			}
		}
		return this;
	}

	public LProcess runLastScreen() {
		final int size = _screenMap.size;
		if (size > 0) {
			final Screen o = _screenMap.last();
			if (o != _currentScreen) {
				setScreen(o, false);
			}
		}
		return this;
	}

	public LProcess runPreviousScreen() {
		final int size = _screenMap.size;
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (_currentScreen == _screenMap.getValueAt(i)) {
					if (i - 1 > -1) {
						setScreen(_screenMap.getValueAt(i - 1), false);
						return this;
					}
				}
			}
		}
		return this;
	}

	public LProcess runNextScreen() {
		final int size = _screenMap.size;
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				if (_currentScreen == _screenMap.getValueAt(i)) {
					if (i + 1 < size) {
						setScreen(_screenMap.getValueAt(i + 1), false);
						return this;
					}
				}
			}
		}
		return this;
	}

	public boolean containsScreen(final Screen screen) {
		if (screen == null) {
			throw new LSysException("Cannot create a Screen instance !");
		}
		return _screenMap.containsValue(screen);
	}

	public TArray<Screen> getScreens() {
		return _screenMap.valuesToArray();
	}

	public int getScreenCount() {
		return _screenMap.size;
	}

	/**
	 * 以指定布局同时加载多个Screen到画面中
	 * 
	 * @param layout
	 * @param screens
	 * @return
	 */
	public SplitScreen setScreen(final SplitLayout layout, final Screen... screens) {
		return new SplitScreen(layout, screens);
	}

	public LProcess setScreen(final Screen screen) {
		if (checkWaiting()) {
			return this;
		}
		if (screen == null) {
			return this;
		}
		if (screen == _currentScreen) {
			return this;
		}
		if (screen._processHandler == null) {
			screen.resetOrder();
			screen.resetSize();
		}
		if (_game.setting.isLogo && _game.displayImpl.showLogo) {
			_loadingScreen = screen;
		} else {
			setScreen(screen, true);
		}
		return this;
	}

	public ScreenExitEffect getExitEffect() {
		return _exitEffect;
	}

	public LProcess gotoEffectExit(final Screen dst) {
		if (checkWaiting()) {
			return this;
		}
		if (_currentScreen == null && dst != null) {
			return setScreen(dst);
		}
		if (dst == _currentScreen) {
			return this;
		}
		_exitEffect.gotoEffectExit(_currentScreen, dst);
		return this;
	}

	public LProcess gotoEffectExit(final int index, final Screen dst) {
		if (checkWaiting()) {
			return this;
		}
		if (_currentScreen == null && dst != null) {
			return setScreen(dst);
		}
		if (dst == _currentScreen) {
			return this;
		}
		_exitEffect.gotoEffectExit(index, _currentScreen, dst);
		return this;
	}

	public LProcess gotoEffectExit(final String name, final Screen dst) {
		return gotoEffectExit(ScreenExitEffect.toEffectTypeIndex(name), dst);
	}

	public LProcess gotoEffectExit(final LColor color, final Screen dst) {
		if (checkWaiting()) {
			return this;
		}
		if (_currentScreen == null && dst != null) {
			return setScreen(dst);
		}
		if (dst == _currentScreen) {
			return this;
		}
		_exitEffect.gotoEffectExit(color, _currentScreen, dst);
		return this;
	}

	public LProcess gotoEffectExit(final int index, final LColor color, final Screen dst) {
		if (checkWaiting()) {
			return this;
		}
		if (_currentScreen == null && dst != null) {
			return setScreen(dst);
		}
		if (dst == _currentScreen) {
			return this;
		}
		_exitEffect.gotoEffectExit(index, color, _currentScreen, dst);
		return this;
	}

	public LProcess gotoEffectExit(final String name, final LColor color, final Screen dst) {
		return gotoEffectExit(ScreenExitEffect.toEffectTypeIndex(name), color, dst);
	}

	public LProcess gotoEffectExitRand(final Screen dst) {
		if (checkWaiting()) {
			return this;
		}
		if (_currentScreen == null && dst != null) {
			return setScreen(dst);
		}
		if (dst == _currentScreen) {
			return this;
		}
		_exitEffect.gotoEffectExitRand(_currentScreen, dst);
		return this;
	}

	public LProcess gotoEffectExitRand(final LColor color, final Screen dst) {
		if (checkWaiting()) {
			return this;
		}
		if (_currentScreen == null && dst != null) {
			return setScreen(dst);
		}
		if (dst == _currentScreen) {
			return this;
		}
		_exitEffect.gotoEffectExitRand(color, _currentScreen, dst);
		return this;
	}

	public LProcess gotoEffectExit(final int index, final LColor color, final int dstIndex) {
		int size = _screenMap.size;
		if (size > 0 && dstIndex > -1 && dstIndex < size) {
			Screen screen = _screenMap.getValueAt(dstIndex);
			if (_currentScreen != screen) {
				return gotoEffectExit(index, color, screen);
			}
		}
		return this;
	}

	public LProcess gotoEffectExit(final String name, final LColor color, final int dstIndex) {
		return gotoEffectExit(ScreenExitEffect.toEffectTypeIndex(name), color, dstIndex);
	}

	public LProcess gotoEffectExitName(final int index, final LColor color, final CharSequence name) {
		for (Iterator<Screen> it = _screenMap.iterator(); it.hasNext();) {
			Screen screen = it.next();
			if (screen != null) {
				if (name.equals(screen.getScreenName())) {
					return gotoEffectExit(index, color, screen);
				}
			}
		}
		return this;
	}

	public LProcess gotoEffectExitName(final String name, final LColor color, final CharSequence ch) {
		return gotoEffectExitName(ScreenExitEffect.toEffectTypeIndex(name), color, ch);
	}

	public LProcess gotoEffectExit(final int index, final LColor color, final CharSequence name) {
		final Screen screen = getScreen(name);
		if (screen != null) {
			return gotoEffectExit(index, color, screen);
		}
		return this;
	}

	public LProcess gotoEffectExit(final String name, final LColor color, final CharSequence ch) {
		return gotoEffectExit(ScreenExitEffect.toEffectTypeIndex(name), color, ch);
	}

	public LProcess gotoEffectExitRand(final LColor color, final CharSequence name) {
		final Screen screen = getScreen(name);
		if (screen != null) {
			return gotoEffectExitRand(color, screen);
		}
		return this;
	}

	public boolean checkWaiting() {
		return _waitTransition;
	}

	public int getHeight() {
		if (_isInstance) {
			return _currentScreen.getHeight();
		}
		return 0;
	}

	public int getWidth() {
		if (_isInstance) {
			return _currentScreen.getWidth();
		}
		return 0;
	}

	public LProcess setCurrentScreen(final Screen screen) {
		return setCurrentScreen(screen, true);
	}

	public LProcess setCurrentScreen(final Screen screen, boolean closed) {
		return setCurrentScreen(screen, closed, true);
	}

	public LProcess setCurrentScreen(final Screen screen, boolean closed, boolean put) {
		return setCurrentScreen(true, screen, closed, put);
	}

	public LProcess setCurrentScreen(final boolean waiting, final Screen screen, boolean closed) {
		return setCurrentScreen(waiting, screen, closed, true);
	}

	public LProcess setCurrentScreen(final boolean waiting, final Screen screen, boolean closed, boolean put) {
		if (checkWaiting() && waiting) {
			return this;
		}
		if (screen != null) {
			this._isInstance = false;
			if (closed) {
				closedScreen(_currentScreen, screen);
			}
			_currentScreen = null;
			_currentScreen = screen;
			_currentScreen.setLock(false);
			_currentScreen.setClose(false);
			_currentScreen.setOnLoadState(true);
			if (screen.getBackground() != null) {
				_currentScreen.setRepaintMode(Screen.SCREEN_TEXTURE_REPAINT);
			}
			beginProcess();
			if (screen instanceof EmulatorListener) {
				setEmulatorListener((EmulatorListener) screen);
			} else {
				setEmulatorListener(null);
			}
			if (put) {
				addScreen(screen);
			}
		}
		return this;
	}

	public void keyDown(GameKey e) {
		if (_isInstance) {
			_currentScreen.keyPressed(e);
		}
	}

	public void keyUp(GameKey e) {
		if (_isInstance) {
			_currentScreen.keyReleased(e);
		}
	}

	public void keyTyped(GameKey e) {
		if (_isInstance) {
			_currentScreen.keyTyped(e);
		}
	}

	public void mousePressed(GameTouch e) {
		if (_isInstance) {
			_currentScreen.mousePressed(e);
		}
	}

	public void mouseReleased(GameTouch e) {
		if (_isInstance) {
			_currentScreen.mouseReleased(e);
		}
	}

	public void mouseMoved(GameTouch e) {
		if (_isInstance) {
			_currentScreen.mouseMoved(e);
		}
	}

	public void mouseDragged(GameTouch e) {
		if (_isInstance) {
			_currentScreen.mouseDragged(e);
		}
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

	public RectBox getWorldBounds() {
		return LSystem.viewSize.getRect();
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

	public LProcess setOffsetTouch(XY pos) {
		_offsetTouch.set(pos);
		return this;
	}

	public LProcess setOffsetTouchX(float x) {
		_offsetTouch.x = x;
		return this;
	}

	public LProcess setOffsetTouchY(float y) {
		_offsetTouch.y = y;
		return this;
	}

	public LProcess setScaleTouch(XY pos) {
		if (pos == null) {
			return this;
		}
		return setScaleTouch(pos.getX(), pos.getY());
	}

	public LProcess setScaleTouch(float sx, float sy) {
		this._touchScaleX = sx;
		this._touchScaleY = sy;
		return this;
	}

	public LProcess setScaleTouchX(float sx) {
		this._touchScaleX = sx;
		return this;
	}

	public LProcess setScaleTouchY(float sy) {
		this._touchScaleY = sy;
		return this;
	}

	public Vector2f getOffsetTouch() {
		return _offsetTouch;
	}

	public Vector2f getPointTouch() {
		return _pointLocaltion;
	}

	public float getTouchScaleX() {
		return _touchScaleX;
	}

	public float getTouchScaleY() {
		return _touchScaleY;
	}

	public LGame getGame() {
		return _game;
	}

	@Override
	public void close() {
		_screenLoading = false;
		if (_isInstance && _currentScreen != null) {
			_currentScreen.stop();
		}
		endTransition();
		if (_isInstance) {
			_isInstance = false;
			if (loads != null) {
				loads.clear();
			}
			if (unloads != null) {
				unloads.clear();
			}
			if (resumes != null) {
				resumes.clear();
			}
			if (_loadcaches != null) {
				_loadcaches.clear();
			}
			if (_currentScreen != null) {
				_currentScreen.destroy();
				_currentScreen = null;
			}
		}
	}

}
