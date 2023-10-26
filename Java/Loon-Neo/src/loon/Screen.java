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

import loon.action.ActionBind;
import loon.action.ActionControl;
import loon.action.ActionTween;
import loon.action.camera.BaseCamera;
import loon.action.camera.EmptyCamera;
import loon.action.camera.Viewport;
import loon.action.collision.CollisionHelper;
import loon.action.collision.CollisionManager;
import loon.action.collision.CollisionObject;
import loon.action.collision.Gravity;
import loon.action.collision.GravityHandler;
import loon.action.collision.GravityResult;
import loon.action.map.Config;
import loon.action.map.Field2D;
import loon.action.page.ScreenSwitch;
import loon.action.sprite.IEntity;
import loon.action.sprite.ISprite;
import loon.action.sprite.Sprite;
import loon.action.sprite.SpriteControls;
import loon.action.sprite.SpriteLabel;
import loon.action.sprite.Sprites;
import loon.action.sprite.Sprites.Created;
import loon.action.sprite.Sprites.SpriteListener;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.canvas.Pixmap;
import loon.component.Desktop;
import loon.component.LClickButton;
import loon.component.LComponent;
import loon.component.LDragging;
import loon.component.LLabel;
import loon.component.LLayer;
import loon.component.LMenuSelect;
import loon.component.LPaper;
import loon.component.LSpriteUI;
import loon.component.UIControls;
import loon.component.layout.HorizontalAlign;
import loon.component.layout.LayoutConstraints;
import loon.component.layout.LayoutManager;
import loon.component.layout.LayoutPort;
import loon.component.skin.SkinManager;
import loon.events.ActionKey;
import loon.events.ClickListener;
import loon.events.DrawListener;
import loon.events.DrawLoop;
import loon.events.EventAction;
import loon.events.FrameLoopEvent;
import loon.events.GameKey;
import loon.events.GameTouch;
import loon.events.LTouchArea;
import loon.events.LTouchLocation;
import loon.events.QueryEvent;
import loon.events.ResizeListener;
import loon.events.SysInput;
import loon.events.SysTouch;
import loon.events.Touched;
import loon.events.TouchedClick;
import loon.events.Updateable;
import loon.events.LTouchArea.Event;
import loon.font.Font.Style;
import loon.font.IFont;
import loon.geom.BoxSize;
import loon.geom.Circle;
import loon.geom.Line;
import loon.geom.PointF;
import loon.geom.PointI;
import loon.geom.RectBox;
import loon.geom.SetXY;
import loon.geom.Triangle2f;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.GLEx;
import loon.opengl.LTextureImage;
import loon.opengl.ShaderSource;
import loon.utils.ArrayByte;
import loon.utils.Calculator;
import loon.utils.ConfigReader;
import loon.utils.Disposes;
import loon.utils.Easing.EasingMode;
import loon.utils.GLUtils;
import loon.utils.IArray;
import loon.utils.IntMap;
import loon.utils.MathUtils;
import loon.utils.ObjectBundle;
import loon.utils.Resolution;
import loon.utils.StringKeyValue;
import loon.utils.TArray;
import loon.utils.TimeUtils;
import loon.utils.processes.Coroutine;
import loon.utils.processes.CoroutineProcess;
import loon.utils.processes.GameProcess;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.processes.YieldExecute;
import loon.utils.processes.Yielderable;
import loon.utils.reply.Callback;
import loon.utils.reply.Closeable;
import loon.utils.reply.Port;
import loon.utils.res.ResourceLocal;
import loon.utils.timer.Duration;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;
import loon.utils.timer.StopwatchTimer;

/**
 * LGame游戏的运行与显示主体，用来显示与操作游戏基础画布，精灵，UI以及其他组件.
 * 
 * 此类默认以DrawOrder.SPRITE, DrawOrder.DESKTOP, DrawOrder.USER
 * 
 * 顺序渲染,即精灵最下,桌面组件中间,用户的GLEx接口渲染最上为绘制顺序
 * 
 * 可以使用@see setDrawOrder() 类调整渲染顺序,也可以使用@see defaultDraw(),@see lastSpriteDraw()
 * 之类函数改变默认组件显示顺序.精灵和组件的setZ函数只在同类排序时生效,不能改变整个Screen的默认显示顺序.
 * 
 */
public abstract class Screen extends PlayerUtils implements SysInput, IArray, LRelease, SetXY, XY {

	public final static int NO_BUTTON = -1;

	public final static int NO_KEY = -1;

	public final static int UPPER_LEFT = 0;

	public final static int UPPER_RIGHT = 1;

	public final static int LOWER_LEFT = 2;

	public final static int LOWER_RIGHT = 3;

	/**
	 * Screen切换方式(单纯移动)
	 *
	 */
	public static enum MoveMethod {
		FROM_LEFT, FROM_UP, FROM_DOWN, FROM_RIGHT, FROM_UPPER_LEFT, FROM_UPPER_RIGHT, FROM_LOWER_LEFT, FROM_LOWER_RIGHT,
		OUT_LEFT, OUT_UP, OUT_DOWN, OUT_RIGHT, OUT_UPPER_LEFT, OUT_UPPER_RIGHT, OUT_LOWER_LEFT, OUT_LOWER_RIGHT;
	}

	/**
	 * Screen切换方式(渐变效果)
	 *
	 */
	public static enum PageMethod {
		Unknown, Accordion, BackToFore, CubeIn, Depth, Fade, Rotate, RotateDown, RotateUp, Stack, ZoomIn, ZoomOut;
	}

	/**
	 * Screen中组件替换选择器,用switch之类分支结构选择指定Screen进行替换
	 */
	public static interface ReplaceEvent {

		public Screen getScreen(int idx);

	}

	/**
	 * Screen中组件渲染顺序选择用枚举类型,Loon的Screen允许桌面组件(UI),精灵,以及用户渲染(Draw接口中的实现)<br>
	 * 自行设置渲染顺序.默认条件下精灵最下,桌面在后,用户渲染最上.
	 */
	public static enum DrawOrder {
		SPRITE, DESKTOP, USER
	}

	public final static int SCREEN_NOT_REPAINT = 0;

	public final static int SCREEN_TEXTURE_REPAINT = 1;

	public final static int SCREEN_COLOR_REPAINT = 2;

	public final static byte DRAW_EMPTY = -1;

	public final static byte DRAW_USER = 0;

	public final static byte DRAW_SPRITE = 1;

	public final static byte DRAW_DESKTOP = 2;

	/**
	 * 通用碰撞管理器(需要用户自行初始化(getCollisionManager或initializeCollision),不实例化默认不存在)
	 */
	private CollisionManager _collisionManager;

	private ResizeListener<Screen> _resizeListener;

	private boolean _collisionClosed;

	private final IntMap<ActionKey> _keyActions = new IntMap<ActionKey>();

	private Updateable _closeUpdate;

	private TouchedClick _touchListener;

	private String _screenName;

	private BaseCamera _baseCamera;

	private ScreenAction _screenAction = null;

	private final CoroutineProcess _coroutineProcess = new CoroutineProcess();

	private final TArray<LTouchArea> _touchAreas = new TArray<LTouchArea>();

	private final Closeable.Set _conns = new Closeable.Set();

	private LTransition _transition;

	private DrawListener<Screen> _drawListener;

	private boolean _curSpriteRun, _curDesktopRun, _curStageRun;

	private boolean _curFristPaintFlag;

	private boolean _curSecondPaintFlag;

	private boolean _curLastPaintFlag;

	// 0.3.2版新增的简易重力控制接口
	private GravityHandler _gravityHandler;

	private Viewport _baseViewport;

	private LColor _backgroundColor;

	private RectBox _rectBox;

	private LColor _baseColor;

	private float _alpha = 1f;

	private float _rotation = 0;

	private float _pivotX = -1f, _pivotY = -1f;

	private float _scaleX = 1f, _scaleY = 1f;

	private boolean _flipX = false, _flipY = false;

	private boolean _visible = true;

	private final TArray<RectBox> _rectLimits = new TArray<RectBox>(10);

	private final TArray<ActionBind> _actionLimits = new TArray<ActionBind>(10);

	private TArray<FrameLoopEvent> _loopEvents;

	private boolean _initLoopEvents = false;

	private boolean _isExistCamera = false;

	private boolean _isExistViewport = false;

	private Accelerometer.SensorDirection _direction = Accelerometer.SensorDirection.EMPTY;

	private int _currentMode, _currentFrame;

	private LTexture _currentScreenBackground;

	protected LProcess _processHandler;

	private int _currentWidth, _currentHeight, _halfWidth, _halfHeight;
	// 精灵集合
	private Sprites _currentSprites;

	// 桌面集合
	private Desktop _currentDesktop;

	private final Disposes _disposes = new Disposes();

	private final PointF _lastTocuh = new PointF();

	private final PointI _touch = new PointI();

	private boolean _desktopPenetrate = false;

	// 是否允许穿透UI组件点击在Screen上
	private boolean _isAllowThroughUItoScreenTouch = false;

	private boolean _isLoad, _isLock, _isClose;

	private boolean _isTranslate, _isGravity;

	private boolean _isProcessing = true;

	protected boolean _isNext;

	private float _currentX, _currentY;

	private float _lastTouchX, _lastTouchY, _touchDX, _touchDY;

	public long elapsedTime;

	private final IntMap<Boolean> _keyTypes = new IntMap<Boolean>();

	private final IntMap<Boolean> _touchTypes = new IntMap<Boolean>();

	private int _touchButtonPressed = NO_BUTTON, _touchButtonReleased = NO_BUTTON;

	private int _keyButtonPressed = NO_KEY, _keyButtonReleased = NO_KEY;

	// 首先绘制的对象
	private PaintOrder _curFristOrder;

	// 其次绘制的对象
	private PaintOrder _curSecondOrder;

	// 最后绘制的对象
	private PaintOrder _curLastOrder;

	private PaintOrder _curUserOrder, _curSpriteOrder, _curDesktopOrder;

	private boolean _replaceLoading;

	private int _replaceScreenSpeed = 8;

	private final LTimer _replaceDelay = new LTimer(0);

	private Screen _replaceDstScreen;

	private ScreenSwitch _screenSwitch;

	private final EmptyObject _curDstPos = new EmptyObject();

	private MoveMethod _curReplaceMethod = MoveMethod.FROM_LEFT;

	private boolean _isScreenFrom = false;

	private final StopwatchTimer _downUpTimer = new StopwatchTimer();
	// 每次screen处理事件循环的额外间隔时间
	private final LTimer _delayTimer = new LTimer(0);
	// 希望Screen中所有组件的update暂停的时间
	private final LTimer _pauseTimer = new LTimer(LSystem.SECOND);
	// 是否已经暂停
	private boolean _isTimerPaused = false;

	private int _screenIndex = 0;

	public static final class PaintOrder {

		private byte _orderType;

		private Screen _orderScreen;

		public PaintOrder(byte t, Screen s) {
			this._orderType = t;
			this._orderScreen = s;
		}

		void paint(GLEx g) {
			switch (_orderType) {
			case DRAW_USER:
				DrawListener<Screen> drawing = _orderScreen._drawListener;
				if (drawing != null) {
					drawing.draw(g, _orderScreen.getX(), _orderScreen.getY());
				}
				_orderScreen.draw(g);
				break;
			case DRAW_SPRITE:
				if (_orderScreen._curSpriteRun) {
					_orderScreen._currentSprites.createUI(g);
				} else if (_orderScreen._curSpriteRun = (_orderScreen._currentSprites != null
						&& _orderScreen._currentSprites.size() > 0)) {
					_orderScreen._currentSprites.createUI(g);
				}
				break;
			case DRAW_DESKTOP:
				if (_orderScreen._curDesktopRun) {
					_orderScreen._currentDesktop.createUI(g);
				} else if (_orderScreen._curDesktopRun = (_orderScreen._currentDesktop != null
						&& _orderScreen._currentDesktop.size() > 0)) {
					_orderScreen._currentDesktop.createUI(g);
				}
				break;
			case DRAW_EMPTY:
			default:
				break;
			}
		}

		void update(LTimerContext c) {
			try {
				switch (_orderType) {
				case DRAW_USER:
					DrawListener<Screen> drawing = _orderScreen._drawListener;
					if (drawing != null) {
						drawing.update(c.timeSinceLastUpdate);
					}
					_orderScreen.alter(c);
					break;
				case DRAW_SPRITE:
					_orderScreen._curSpriteRun = (_orderScreen._currentSprites != null
							&& _orderScreen._currentSprites.size() > 0);
					if (_orderScreen._curSpriteRun) {
						_orderScreen._currentSprites.update(c.timeSinceLastUpdate);
					}
					break;
				case DRAW_DESKTOP:
					_orderScreen._curDesktopRun = (_orderScreen._currentDesktop != null
							&& _orderScreen._currentDesktop.size() > 0);
					if (_orderScreen._curDesktopRun) {
						_orderScreen._currentDesktop.update(c.timeSinceLastUpdate);
					}
					break;
				case DRAW_EMPTY:
				default:
					break;
				}
			} catch (Throwable cause) {
				LSystem.error("Screen update() dispatch failure", cause);
			}
		}
	}

	public Screen(String name, int w, int h) {
		init(name, w, h);
	}

	public Screen(String name) {
		this(name, 0, 0);
	}

	public Screen(int w, int h) {
		this(LSystem.UNKNOWN, w, h);
	}

	public Screen() {
		this(LSystem.UNKNOWN, 0, 0);
	}

	protected void init(String name, int w, int h) {
		this._screenName = name;
		resetSize(w, h);
	}

	public Screen setID(int id) {
		this._screenIndex = id;
		return this;
	}

	public int getID() {
		return this._screenIndex;
	}

	/**
	 * 转化DrawOrder为PaintOrder
	 * 
	 * @param tree
	 * @return
	 */
	public final PaintOrder toPaintOrder(DrawOrder tree) {
		PaintOrder order = null;
		switch (tree) {
		case SPRITE:
			order = DRAW_SPRITE_PAINT();
			break;
		case DESKTOP:
			order = DRAW_DESKTOP_PAINT();
			break;
		case USER:
		default:
			order = DRAW_USER_PAINT();
			break;
		}
		return order;
	}

	protected final PaintOrder DRAW_USER_PAINT() {
		if (_curUserOrder == null) {
			_curUserOrder = new PaintOrder(DRAW_USER, this);
		}
		return _curUserOrder;
	}

	protected final PaintOrder DRAW_SPRITE_PAINT() {
		if (_curSpriteOrder == null) {
			_curSpriteOrder = new PaintOrder(DRAW_SPRITE, this);
		}
		return _curSpriteOrder;
	}

	protected final PaintOrder DRAW_DESKTOP_PAINT() {
		if (_curDesktopOrder == null) {
			_curDesktopOrder = new PaintOrder(DRAW_DESKTOP, this);
		}
		return _curDesktopOrder;
	}

	/**
	 * 设置Screen中组件渲染顺序
	 * 
	 * @param one
	 * @param two
	 * @param three
	 */
	public Screen setDrawOrder(DrawOrder one, DrawOrder two, DrawOrder three) {
		this.setFristOrder(toPaintOrder(one));
		this.setSecondOrder(toPaintOrder(two));
		this.setLastOrder(toPaintOrder(three));
		return this;
	}

	/**
	 * 设置为默认渲染顺序
	 */
	public Screen defaultDraw() {
		return setDrawOrder(DrawOrder.SPRITE, DrawOrder.DESKTOP, DrawOrder.USER);
	}

	/**
	 * 把精灵渲染置于桌面与桌面之间
	 */
	public Screen centerSpriteDraw() {
		setFristOrder(DRAW_DESKTOP_PAINT());
		setSecondOrder(DRAW_SPRITE_PAINT());
		setLastOrder(DRAW_USER_PAINT());
		return this;
	}

	/**
	 * 最后绘制精灵
	 */
	public Screen lastSpriteDraw() {
		setFristOrder(DRAW_USER_PAINT());
		setSecondOrder(DRAW_DESKTOP_PAINT());
		setLastOrder(DRAW_SPRITE_PAINT());
		return this;
	}

	/**
	 * 只绘制精灵
	 */
	public Screen onlySpriteDraw() {
		setFristOrder(null);
		setSecondOrder(null);
		setLastOrder(DRAW_SPRITE_PAINT());
		return this;
	}

	/**
	 * 把桌面渲染置于精灵与桌面之间
	 */
	public Screen centerDesktopDraw() {
		setFristOrder(DRAW_SPRITE_PAINT());
		setSecondOrder(DRAW_DESKTOP_PAINT());
		setLastOrder(DRAW_USER_PAINT());
		return this;
	}

	/**
	 * 最后绘制组件
	 */
	public Screen lastDesktopDraw() {
		setFristOrder(DRAW_USER_PAINT());
		setSecondOrder(DRAW_SPRITE_PAINT());
		setLastOrder(DRAW_DESKTOP_PAINT());
		return this;
	}

	/**
	 * 只绘制组件
	 */
	public Screen onlyDesktopDraw() {
		setFristOrder(null);
		setSecondOrder(null);
		setLastOrder(DRAW_DESKTOP_PAINT());
		return this;
	}

	/**
	 * 最后绘制用户界面
	 */
	public Screen lastUserDraw() {
		setFristOrder(DRAW_SPRITE_PAINT());
		setSecondOrder(DRAW_DESKTOP_PAINT());
		setLastOrder(DRAW_USER_PAINT());
		return this;
	}

	/**
	 * 优先绘制用户界面
	 */
	public Screen fristUserDraw() {
		setFristOrder(DRAW_USER_PAINT());
		setSecondOrder(DRAW_SPRITE_PAINT());
		setLastOrder(DRAW_DESKTOP_PAINT());
		return this;
	}

	/**
	 * 把用户渲染置于精灵与桌面之间
	 */
	public Screen centerUserDraw() {
		setFristOrder(DRAW_SPRITE_PAINT());
		setSecondOrder(DRAW_USER_PAINT());
		setLastOrder(DRAW_DESKTOP_PAINT());
		return this;
	}

	/**
	 * 只保留一个用户渲染接口（即无组件会被渲染出来）
	 */
	public Screen onlyUserDraw() {
		setFristOrder(null);
		setSecondOrder(null);
		setLastOrder(DRAW_USER_PAINT());
		return this;
	}

	public boolean containsActionKey(int keyCode) {
		return _keyActions.containsKey(keyCode);
	}

	public Screen addActionKey(int keyCode, ActionKey e) {
		_keyActions.put(keyCode, e);
		return this;
	}

	public ActionKey removeActionKey(int keyCode) {
		return _keyActions.remove(keyCode);
	}

	public Screen pressActionKey(int keyCode) {
		ActionKey key = _keyActions.get(keyCode);
		if (key != null) {
			key.press();
		}
		return this;
	}

	public Screen releaseActionKey(int keyCode) {
		ActionKey key = _keyActions.get(keyCode);
		if (key != null) {
			key.release();
		}
		return this;
	}

	public Screen clearActionKey() {
		_keyActions.clear();
		return this;
	}

	public Screen releaseActionKeys() {
		int keySize = _keyActions.size();
		if (keySize > 0) {
			for (Iterator<ActionKey> it = _keyActions.iterator(); it.hasNext();) {
				ActionKey act = it.next();
				if (act != null) {
					act.release();
				}
			}
		}
		return this;
	}

	public SkinManager skin() {
		return SkinManager.get();
	}

	private final TouchedClick makeTouched() {
		if (_touchListener == null) {
			_touchListener = new TouchedClick();
		}
		TouchedClick click = new TouchedClick();
		_touchListener.addClickListener(click);
		return click;
	}

	public Screen addClickListener(ClickListener c) {
		makeTouched().addClickListener(c);
		return this;
	}

	public Screen clearTouched() {
		this._touchButtonPressed = NO_BUTTON;
		this._touchButtonReleased = NO_BUTTON;
		this._touchDX = -1;
		this._touchDY = -1;
		this._lastTouchX = -1;
		this._lastTouchY = -1;
		if (_touchTypes != null) {
			_touchTypes.clear();
		}
		if (_touchListener != null) {
			_touchListener.clear();
			_touchListener = null;
		}
		return this;
	}

	public boolean isTouchedEnabled() {
		if (_touchListener != null) {
			return _touchListener.isEnabled();
		}
		return false;
	}

	public Screen setTouchedEnabled(boolean e) {
		if (_touchListener != null) {
			_touchListener.setEnabled(e);
		}
		return this;
	}

	/**
	 * 监听所有触屏事件(可以添加多个)
	 * 
	 * @param t
	 * @return
	 */
	public Screen all(Touched t) {
		makeTouched().setAllTouch(t);
		return this;
	}

	/**
	 * 添加所有点击事件(可以添加多个)
	 * 
	 * @param t
	 * @return
	 */
	public Screen down(Touched t) {
		makeTouched().setDownTouch(t);
		return this;
	}

	/**
	 * 监听所有触屏离开事件(可以添加多个)
	 * 
	 * @param t
	 * @return
	 */
	public Screen up(Touched t) {
		makeTouched().setUpTouch(t);
		return this;
	}

	/**
	 * 监听所有触屏拖拽事件(可以添加多个)
	 * 
	 * @param t
	 * @return
	 */
	public Screen drag(Touched t) {
		makeTouched().setDragTouch(t);
		return this;
	}

	/** 受限函数,关系到线程的同步与异步，使用此部分函数实现的功能，将无法在GWT编译的HTML5环境运行，所以默认注释掉. **/

	/**
	 * 但是，TeaVM之类的Bytecode to JS转码器是支持的.因此视情况有恢复可能性，但千万注意，恢复此部分函数的话。[不保证完整的跨平台性]
	 **/
	/*
	 * private boolean isDrawing;
	 * 
	 * @Deprecated public void yieldDraw() { notifyDraw(); waitUpdate(); }
	 * 
	 * @Deprecated public void yieldUpdate() { notifyUpdate(); waitDraw(); }
	 * 
	 * @Deprecated public synchronized void notifyDraw() { this.isDrawing = true;
	 * this.notifyAll(); }
	 * 
	 * @Deprecated public synchronized void notifyUpdate() { this.isDrawing = false;
	 * this.notifyAll(); }
	 * 
	 * @Deprecated public synchronized void waitDraw() { for (; !isDrawing;) { try {
	 * this.wait(); } catch (InterruptedException ex) { } } }
	 * 
	 * @Deprecated public synchronized void waitUpdate() { for (; isDrawing;) { try
	 * { this.wait(); } catch (InterruptedException ex) { } } }
	 * 
	 * @Deprecated public synchronized void waitFrame(int i) { for (int wait = frame
	 * + i; frame < wait;) { try { super.wait(); } catch (Throwable ex) { } } }
	 * 
	 * @Deprecated public synchronized void waitTime(long i) { for (long time =
	 * System.currentTimeMillis() + i; System .currentTimeMillis() < time;) try {
	 * super.wait(time - System.currentTimeMillis()); } catch (Throwable ex) { } }
	 */
	/** 受限函数结束 **/

	public LayoutConstraints getRootConstraints() {
		if (_currentDesktop != null) {
			return _currentDesktop.getRootConstraints();
		}
		return null;
	}

	public LayoutPort getLayoutPort() {
		if (_currentDesktop != null) {
			return _currentDesktop.getLayoutPort();
		}
		return null;
	}

	public LayoutPort getLayoutPort(final RectBox newBox, final LayoutConstraints newBoxConstraints) {
		if (_currentDesktop != null) {
			return _currentDesktop.getLayoutPort(newBox, newBoxConstraints);
		}
		return null;
	}

	public LayoutPort getLayoutPort(final LayoutPort src) {
		if (_currentDesktop != null) {
			return _currentDesktop.getLayoutPort(src);
		}
		return null;
	}

	public Screen layoutElements(final LayoutManager manager, final LComponent... comps) {
		if (_currentDesktop != null) {
			_currentDesktop.layoutElements(manager, comps);
		}
		return this;
	}

	public Screen layoutElements(final LayoutManager manager, final LayoutPort... ports) {
		if (_currentDesktop != null) {
			_currentDesktop.layoutElements(manager, ports);
		}
		return this;
	}

	public Screen packLayout(final LayoutManager manager) {
		if (_currentDesktop != null) {
			_currentDesktop.packLayout(manager);
		}
		return this;
	}

	public Screen packLayout(final LayoutManager manager, final float spacex, final float spacey,
			final float spaceWidth, final float spaceHeight) {
		if (_currentDesktop != null) {
			_currentDesktop.packLayout(manager, spacex, spacey, spaceHeight, spaceHeight);
		}
		return this;
	}

	public Screen setCamera(BaseCamera came) {
		_isExistCamera = (came != null);
		if (_isExistCamera) {
			_baseCamera = came;
			_baseCamera.setup();
		}
		return this;
	}

	public BaseCamera getCamera() {
		if (_baseCamera == null) {
			_baseCamera = new EmptyCamera();
		}
		return _baseCamera;
	}

	public Screen stopRepaint() {
		LSystem.stopRepaint();
		return this;
	}

	public Screen startRepaint() {
		LSystem.startRepaint();
		return this;
	}

	public Screen stopProcess() {
		this._isProcessing = false;
		return this;
	}

	public Screen startProcess() {
		this._isProcessing = true;
		return this;
	}

	public Screen registerTouchArea(final LTouchArea touchArea) {
		this._touchAreas.add(touchArea);
		return this;
	}

	public boolean unregisterTouchArea(final LTouchArea touchArea) {
		return this._touchAreas.remove(touchArea);
	}

	public Screen clearTouchAreas() {
		this._touchAreas.clear();
		return this;
	}

	public TArray<LTouchArea> getTouchAreas() {
		return this._touchAreas;
	}

	/**
	 * 私有函数,如果设置(@see registerTouchArea)的LTouchArea区域被点击,则会调用相关函数
	 * 
	 * @param e
	 * @param touchX
	 * @param touchY
	 */
	private final void updateTouchArea(final LTouchArea.Event e, final float touchX, final float touchY) {
		if (this._touchAreas.size == 0) {
			return;
		}
		final TArray<LTouchArea> touchAreas = this._touchAreas;
		final int touchAreaCount = touchAreas.size;
		if (touchAreaCount > 0) {
			for (int i = 0; i < touchAreaCount; i++) {
				final LTouchArea touchArea = touchAreas.get(i);
				if (touchArea.contains(touchX, touchY)) {
					touchArea.onAreaTouched(e, touchX, touchY);
				}
			}
		}
	}

	/**
	 * 作用近似于js的同名函数,以指定的延迟执行Updateable
	 * 
	 * @param update
	 * @param delay
	 * @return
	 */
	public Screen setTimeout(final EventAction update, final long delay) {
		return add(new Port<LTimerContext>() {

			final LTimer timer = new LTimer(delay > 0l ? delay / 60l : 0l);

			@Override
			public void onEmit(LTimerContext event) {
				if (timer.action(event) && update != null) {
					callEventAction(update, this, event.getTimeSinceLastUpdate());
				}
			}
		}, true);
	}

	/**
	 * 作用近似于js的同名函数,以指定的延迟执行Updateable
	 * 
	 * @param update
	 * @param second
	 * @return
	 */
	public Screen setTimeout(final EventAction update, final float second) {
		return setTimeout(update, Duration.ofS(second));
	}

	/**
	 * 作用近似于js的同名函数,以指定的延迟执行Updateable
	 * 
	 * @param update
	 * @return
	 */
	public Screen setTimeout(final EventAction update) {
		return setTimeout(update, 0l);
	}

	/**
	 * 添加一个监听LTimerContext的Port
	 * 
	 * @param timer
	 * @return
	 */
	public Screen add(Port<LTimerContext> timer) {
		return add(timer, false);
	}

	/**
	 * 添加一个监听LTimerContext的Port(若paint为true,则监听同步画布刷新,否则异步监听,默认为false)
	 * 
	 * @param timer
	 * @param paint
	 * @return
	 */
	public Screen add(Port<LTimerContext> timer, boolean paint) {
		LGame game = LSystem.base();
		if (game != null && game.display() != null) {
			if (paint) {
				_conns.add(game.display().paint.connect(timer));
			} else {
				_conns.add(game.display().update.connect(timer));
			}
		}
		return this;
	}

	/**
	 * 删除一个Port的LTimerContext监听
	 * 
	 * @param timer
	 * @return
	 */
	public Screen remove(Port<LTimerContext> timer) {
		return remove(timer, false);
	}

	/**
	 * 删除一个Port的LTimerContext监听(为true时,删除绑定到画布刷新中的,为false删除绑定到异步的update监听上的,默认为false)
	 * 
	 * @param timer
	 * @param paint
	 * @return
	 */
	public Screen remove(Port<LTimerContext> timer, boolean paint) {
		LGame game = LSystem.base();
		if (game != null && game.display() != null) {
			if (paint) {
				_conns.remove(game.display().paint.connect(timer));
			} else {
				_conns.remove(game.display().update.connect(timer));
			}
		}
		return this;
	}

	/**
	 * 要求缓动动画计算与游戏画布刷新同步或异步(默认异步)
	 * 
	 * @param sync
	 * @return
	 */
	public Screen syncTween(boolean sync) {
		LGame game = LSystem.base();
		if (game != null && game.display() != null) {
			game.display().updateSyncTween(sync);
		}
		return this;
	}

	/**
	 * 设定缓动动画延迟时间
	 * 
	 * @param delay
	 * @return
	 */
	public Screen delayTween(long delay) {
		ActionControl.setDelay(delay);
		return this;
	}

	public Screen putRelease(LRelease r) {
		_disposes.put(r);
		return this;
	}

	public boolean containsRelease(LRelease r) {
		return _disposes.contains(r);
	}

	public Screen removeRelease(LRelease r) {
		_disposes.remove(r);
		return this;
	}

	public Screen putReleases(LRelease... rs) {
		_disposes.put(rs);
		return this;
	}

	public float getDeltaTime() {
		return MathUtils.max(Duration.toS(elapsedTime), LSystem.MIN_SECONE_SPEED_FIXED);
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public LGame getGame() {
		return LSystem.base();
	}

	public final boolean isSpriteRunning() {
		return _curSpriteRun;
	}

	public final boolean isDesktopRunning() {
		return _curDesktopRun;
	}

	public final boolean isStageRunning() {
		return _curStageRun;
	}

	public abstract void draw(GLEx g);

	/**
	 * 替换当前Screen为其它Screen(替换效果随机)
	 * 
	 * @param screen
	 * @return
	 */
	public Screen replaceScreen(final Screen screen) {
		if (_replaceLoading) {
			return this;
		}
		Screen tmp = null;
		int rnd = MathUtils.random(0, 11);
		switch (rnd) {
		default:
		case 0:
			tmp = replaceScreen(screen, PageMethod.ZoomOut);
			break;
		case 1:
			tmp = replaceScreen(screen, PageMethod.ZoomIn);
			break;
		case 2:
			tmp = replaceScreen(screen, PageMethod.Accordion);
			break;
		case 3:
			tmp = replaceScreen(screen, PageMethod.BackToFore);
			break;
		case 4:
			tmp = replaceScreen(screen, PageMethod.CubeIn);
			break;
		case 5:
			tmp = replaceScreen(screen, PageMethod.Depth);
			break;
		case 6:
			tmp = replaceScreen(screen, PageMethod.Fade);
			break;
		case 7:
			tmp = replaceScreen(screen, PageMethod.RotateDown);
			break;
		case 8:
			tmp = replaceScreen(screen, PageMethod.RotateUp);
			break;
		case 9:
			tmp = replaceScreen(screen, PageMethod.Stack);
			break;
		case 10:
			tmp = replaceScreen(screen, PageMethod.Rotate);
			break;
		case 11:
			int random = MathUtils.random(0, 15);
			if (random == 0) {
				tmp = replaceScreen(screen, MoveMethod.FROM_LEFT);
			} else if (random == 1) {
				tmp = replaceScreen(screen, MoveMethod.FROM_UP);
			} else if (random == 2) {
				tmp = replaceScreen(screen, MoveMethod.FROM_DOWN);
			} else if (random == 3) {
				tmp = replaceScreen(screen, MoveMethod.FROM_RIGHT);
			} else if (random == 4) {
				tmp = replaceScreen(screen, MoveMethod.FROM_UPPER_LEFT);
			} else if (random == 5) {
				tmp = replaceScreen(screen, MoveMethod.FROM_UPPER_RIGHT);
			} else if (random == 6) {
				tmp = replaceScreen(screen, MoveMethod.FROM_LOWER_LEFT);
			} else if (random == 7) {
				tmp = replaceScreen(screen, MoveMethod.FROM_LOWER_RIGHT);
			} else if (random == 8) {
				tmp = replaceScreen(screen, MoveMethod.OUT_LEFT);
			} else if (random == 9) {
				tmp = replaceScreen(screen, MoveMethod.OUT_UP);
			} else if (random == 10) {
				tmp = replaceScreen(screen, MoveMethod.OUT_DOWN);
			} else if (random == 11) {
				tmp = replaceScreen(screen, MoveMethod.OUT_RIGHT);
			} else if (random == 12) {
				tmp = replaceScreen(screen, MoveMethod.OUT_UPPER_LEFT);
			} else if (random == 13) {
				tmp = replaceScreen(screen, MoveMethod.OUT_UPPER_RIGHT);
			} else if (random == 14) {
				tmp = replaceScreen(screen, MoveMethod.OUT_LOWER_LEFT);
			} else if (random == 15) {
				tmp = replaceScreen(screen, MoveMethod.OUT_LOWER_RIGHT);
			} else {
				tmp = replaceScreen(screen, MoveMethod.FROM_LEFT);
			}
			break;
		}
		return tmp;
	}

	/**
	 * 替换当前Screen为其它Screen,替换效果指定
	 * 
	 * @param screen
	 * @param _screenSwitch
	 * @return
	 */
	public Screen replaceScreen(final Screen screen, ScreenSwitch _screenSwitch) {
		if (_replaceLoading) {
			return this;
		}
		if (screen != null && screen != this) {
			_replaceLoading = true;
			screen.setOnLoadState(false);
			setLock(true);
			screen.setLock(true);
			this._replaceDstScreen = screen;
			this._screenSwitch = _screenSwitch;
			screen.setRepaintMode(SCREEN_NOT_REPAINT);

			RealtimeProcessManager.get().addProcess(new RealtimeProcess() {

				@Override
				public void run(LTimerContext time) {
					try {
						screen.onCreate(LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
						screen.setClose(false);
						screen.onLoad();
						screen.setRepaintMode(SCREEN_NOT_REPAINT);
						screen.onLoaded();
						screen.setOnLoadState(true);
					} catch (Throwable cause) {
						LSystem.error("Replace screen dispatch failure", cause);
					} finally {
						kill();
					}
				}
			});

		}
		return this;
	}

	public Screen replaceScreen(final Screen screen, PageMethod m) {
		return replaceScreen(screen, new ScreenSwitch(m, this, screen));
	}

	/**
	 * 替换当前Screen为其它Screen,并指定页面交替的移动效果
	 * 
	 * @param screen
	 * @param m
	 * @return
	 */
	public Screen replaceScreen(final Screen screen, MoveMethod m) {
		if (_replaceLoading) {
			return this;
		}
		if (screen != null && screen != this) {
			_replaceLoading = true;
			screen.setOnLoadState(false);
			setLock(true);
			screen.setLock(true);
			this._curReplaceMethod = m;
			this._replaceDstScreen = screen;
			screen.setRepaintMode(SCREEN_NOT_REPAINT);
			switch (m) {
			case FROM_LEFT:
				_curDstPos.setLocation(-getWidth(), 0);
				_isScreenFrom = true;
				break;
			case FROM_RIGHT:
				_curDstPos.setLocation(getWidth(), 0);
				_isScreenFrom = true;
				break;
			case FROM_UP:
				_curDstPos.setLocation(0, -getHeight());
				_isScreenFrom = true;
				break;
			case FROM_DOWN:
				_curDstPos.setLocation(0, getHeight());
				_isScreenFrom = true;
				break;
			case FROM_UPPER_LEFT:
				_curDstPos.setLocation(-getWidth(), -getHeight());
				_isScreenFrom = true;
				break;
			case FROM_UPPER_RIGHT:
				_curDstPos.setLocation(getWidth(), -getHeight());
				_isScreenFrom = true;
				break;
			case FROM_LOWER_LEFT:
				_curDstPos.setLocation(-getWidth(), getHeight());
				_isScreenFrom = true;
				break;
			case FROM_LOWER_RIGHT:
				_curDstPos.setLocation(getWidth(), getHeight());
				_isScreenFrom = true;
				break;
			default:
				_curDstPos.setLocation(0, 0);
				_isScreenFrom = false;
				break;
			}

			RealtimeProcessManager.get().addProcess(new RealtimeProcess() {

				@Override
				public void run(LTimerContext time) {
					try {
						screen.onCreate(LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
						screen.setClose(false);
						screen.onLoad();
						screen.setRepaintMode(SCREEN_NOT_REPAINT);
						screen.onLoaded();
						screen.setOnLoadState(true);
					} catch (Throwable cause) {
						LSystem.error("Replace screen dispatch failure", cause);
					} finally {
						kill();
					}
				}
			});

		}

		return this;
	}

	public int getReplaceScreenSpeed() {
		return _replaceScreenSpeed;
	}

	public Screen setReplaceScreenSpeed(int s) {
		this._replaceScreenSpeed = s;
		return this;
	}

	public Screen setReplaceScreenDelay(long d) {
		_replaceDelay.setDelay(d);
		return this;
	}

	public long getReplaceScreenDelay() {
		return _replaceDelay.getDelay();
	}

	private void submitReplaceScreen() {
		if (_processHandler != null) {
			_processHandler.setCurrentScreen(_replaceDstScreen, false);
			_replaceDstScreen._closeUpdate = new Updateable() {

				@Override
				public void action(Object a) {
					destroy();
				}
			};
		}
		_replaceLoading = false;
	}

	/**
	 * 删除所有添加的TouchLimit
	 * 
	 * @return
	 */
	public Screen removeTouchLimit() {
		_rectLimits.clear();
		_actionLimits.clear();
		return this;
	}

	/**
	 * 删除ActionBind的TouchLimit
	 * 
	 * @return
	 */
	public Screen removeTouchLimit(ActionBind act) {
		if (act != null) {
			_actionLimits.remove(act);
		}
		return this;
	}

	/**
	 * 删除RectBox的TouchLimit
	 * 
	 * @param rect
	 * @return
	 */
	public Screen removeTouchLimit(RectBox rect) {
		if (rect != null) {
			_rectLimits.remove(rect);
		}
		return this;
	}

	/**
	 * 添加一个针对ActionBind区域的触屏限制
	 * 
	 * @param act
	 * @return
	 */
	public Screen addTouchLimit(ActionBind act) {
		if (act != null && !_actionLimits.contains(act) && !(act instanceof LDragging)) {
			_actionLimits.add(act);
		}
		return this;
	}

	/**
	 * 添加一个针对RectBox区域的触屏限制
	 * 
	 * @param rect
	 * @return
	 */
	public Screen addTouchLimit(RectBox rect) {
		if (rect != null && !_rectLimits.contains(rect)) {
			_rectLimits.add(rect);
		}
		return this;
	}

	/**
	 * 检查指定点击区域是否有被限制(@see addTouchLimit() 函数添加限制区域)
	 * 
	 * @return
	 */
	public boolean isClickLimit() {
		return isClickLimit(SysTouch.x(), SysTouch.y());
	}

	/**
	 * 检查指定点击区域是否有被限制(@see addTouchLimit() 函数添加限制区域)
	 * 
	 * @param e
	 * @return
	 */
	public boolean isClickLimit(GameTouch e) {
		return isClickLimit(e.x(), e.y());
	}

	/**
	 * 检查指定点击区域是否有被限制(@see addTouchLimit() 函数添加限制区域)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isClickLimit(int x, int y) {
		if (_isAllowThroughUItoScreenTouch) {
			return false;
		}
		if (_rectLimits.size == 0 && _actionLimits.size == 0) {
			return false;
		}
		for (RectBox rect : _rectLimits) {
			if (rect.contains(x, y)) {
				return true;
			}
		}
		for (ActionBind act : _actionLimits) {
			final boolean show = (act.isVisible() && act.getAlpha() > 0f);
			if (show && act.getRectBox().contains(x, y)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否允许穿透UI组件直接触发Screen点击(此项默认为false,不能穿透已有UI组件触发Screen的touch事件)
	 * 
	 * @return
	 */
	public boolean isAllowThroughUItoScreenTouch() {
		return _isAllowThroughUItoScreenTouch;
	}

	public Screen setAllowThroughUItoScreenTouch(boolean a) {
		this._isAllowThroughUItoScreenTouch = a;
		return this;
	}

	final public void resetSize() {
		resetSize(0, 0);
	}

	final public void resetSize(int w, int h) {
		this._processHandler = LSystem.getProcess();
		this._currentWidth = (w <= 0 ? LSystem.viewSize.getWidth() : w);
		this._currentHeight = (h <= 0 ? LSystem.viewSize.getHeight() : h);
		this._halfWidth = _currentWidth / 2;
		this._halfHeight = _currentHeight / 2;
		this.setSize(_currentWidth, _currentHeight);
		if (_resizeListener != null) {
			_resizeListener.onResize(this);
		}
		if (_curSpriteRun && _currentSprites != null) {
			_currentSprites.setSize(_currentWidth, _currentHeight);
		}
		if (_curDesktopRun && _currentDesktop != null) {
			_currentDesktop.setSize(_currentWidth, _currentHeight);
		}
		if (_isGravity && _gravityHandler != null) {
			_gravityHandler.setLimit(_currentWidth, _currentHeight);
		}
		this.resize(_currentWidth, _currentHeight);
	}

	final public void resetOrder() {
		// 最先精灵
		this._curFristOrder = DRAW_SPRITE_PAINT();
		// 其次桌面
		this._curSecondOrder = DRAW_DESKTOP_PAINT();
		// 最后用户
		this._curLastOrder = DRAW_USER_PAINT();
		this._curFristPaintFlag = true;
		this._curSecondPaintFlag = true;
		this._curLastPaintFlag = true;
	}

	public boolean contains(float x, float y) {
		return getRectBox().contains(x, y);
	}

	public boolean contains(float x, float y, float w, float h) {
		return getRectBox().contains(x, y, w, h);
	}

	public boolean intersects(float x, float y) {
		return getRectBox().intersects(x, y);
	}

	public boolean intersects(float x, float y, float w, float h) {
		return getRectBox().intersects(x, y, w, h);
	}

	/**
	 * 当Screen被创建(或再次加载)时将调用此函数
	 * 
	 * @param width
	 * @param height
	 */
	public void onCreate(int width, int height) {
		this._currentMode = SCREEN_NOT_REPAINT;
		this._curStageRun = true;
		this._currentWidth = width;
		this._currentHeight = height;
		this._halfWidth = this._currentWidth / 2;
		this._halfHeight = this._currentHeight / 2;
		this._lastTouchX = _lastTouchY = _touchDX = _touchDY = 0;
		this._isScreenFrom = _isTimerPaused = _isAllowThroughUItoScreenTouch = false;
		this._isLoad = _isLock = _isClose = _isTranslate = _isGravity = false;
		this._isProcessing = true;
		if (_currentSprites != null) {
			_currentSprites.close();
			_currentSprites.removeAll();
			_currentSprites = null;
		}
		this._currentSprites = new Sprites("ScreenSprites", this, _currentWidth, _currentHeight);
		if (_currentDesktop != null) {
			_currentDesktop.close();
			_currentDesktop.clear();
			_currentDesktop = null;
		}
		this._currentDesktop = new Desktop("ScreenDesktop", this, _currentWidth, _currentHeight);
		this._currentX = _currentY = 0;
		this._isNext = true;
		this._isTranslate = false;
		this._screenIndex = 0;
		this._lastTocuh.empty();
		this._keyActions.clear();
		this._visible = true;
		this._rotation = 0;
		this._scaleX = _scaleY = _alpha = 1f;
		this._baseColor = null;
		this._isExistCamera = false;
		this._initLoopEvents = false;
		this._desktopPenetrate = false;
		this._rectLimits.clear();
		this._actionLimits.clear();
		this._touchAreas.clear();
		this._keyActions.clear();
		this._conns.reset();
		this._delayTimer.setDelay(0);
		this._pauseTimer.setDelay(LSystem.SECOND);
	}

	public Screen invokeAsync(Runnable runnable) {
		LSystem.invokeAsync(runnable);
		return this;
	}

	public Screen invokeLater(Runnable runnable) {
		LSystem.invokeLater(runnable);
		return this;
	}

	public Screen addResume(Updateable u) {
		if (_processHandler != null) {
			_processHandler.addResume(u);
		}
		return this;
	}

	public Screen removeResume(Updateable u) {
		if (_processHandler != null) {
			_processHandler.removeResume(u);
		}
		return this;
	}

	public Screen addLoad(Updateable u) {
		if (_processHandler != null) {
			_processHandler.addLoad(u);
		}
		return this;
	}

	public boolean containsLoad(Updateable u) {
		if (_processHandler != null) {
			return _processHandler.containsLoad(u);
		}
		return false;
	}

	public Screen removeLoad(Updateable u) {
		if (_processHandler != null) {
			_processHandler.removeLoad(u);
		}
		return this;
	}

	public Screen removeAllLoad() {
		if (_processHandler != null) {
			_processHandler.removeAllLoad();
		}
		return this;
	}

	public Screen addUnLoad(Updateable u) {
		if (_processHandler != null) {
			_processHandler.addUnLoad(u);
		}
		return this;
	}

	public boolean containsUnLoad(Updateable u) {
		if (_processHandler != null) {
			return _processHandler.containsUnLoad(u);
		}
		return false;
	}

	public Screen removeUnLoad(Updateable u) {
		if (_processHandler != null) {
			_processHandler.removeUnLoad(u);
		}
		return this;
	}

	public Screen removeAllUnLoad() {
		if (_processHandler != null) {
			_processHandler.removeAllUnLoad();
		}
		return this;
	}

	/**
	 * 设置游戏中使用的默认IFont
	 * 
	 * @param font
	 * @return
	 */
	public Screen setSystemGameFont(IFont font) {
		LSystem.setSystemGameFont(font);
		return this;
	}

	/**
	 * 获得游戏中使用的IFont
	 * 
	 * @return
	 */
	public IFont getSystemGameFont() {
		return LSystem.getSystemGameFont();
	}

	/**
	 * 设置系统log使用的默认IFont
	 * 
	 * @param font
	 * @return
	 */
	public Screen setSystemLogFont(IFont font) {
		LSystem.setSystemLogFont(font);
		return this;
	}

	/**
	 * 设置系统log使用的IFont
	 * 
	 * @return
	 */
	public IFont getSystemLogFont() {
		return LSystem.getSystemLogFont();
	}

	/**
	 * 当执行Screen转换时将调用此函数(如果返回的LTransition不为null，则渐变效果会被执行)
	 * 
	 * @return
	 */
	public LTransition onTransition() {
		return _transition;
	}

	/**
	 * 设置一个空操作的过渡效果
	 */
	public void noopTransition() {
		this._transition = LTransition.newEmpty();
	}

	/**
	 * 注入一个渐变特效，在引入Screen时将使用此特效进行渐变.
	 * 
	 * @param t
	 * @return
	 */
	public Screen setTransition(LTransition t) {
		this._transition = t;
		return this;
	}

	/**
	 * 注入一个渐变特效的名称，以及渐变使用的颜色，在引入Screen时将使用此特效进行渐变.
	 * 
	 * @param transName
	 * @param c
	 * @return
	 */
	public Screen setTransition(String transName, LColor c) {
		this._transition = LTransition.newTransition(transName, c);
		return this;
	}

	/**
	 * @see onTransition
	 * 
	 * @return
	 */
	public LTransition getTransition() {
		return this._transition;
	}

	/**
	 * 设定重力系统是否启动,并返回控制器
	 * 
	 * @param g
	 * @return
	 */
	public GravityHandler setGravity(boolean g) {
		return setGravity(EasingMode.Linear, g);
	}

	/**
	 * 设定重力系统是否启动,并返回控制器
	 * 
	 * @param ease
	 * @param g
	 * @return
	 */
	public GravityHandler setGravity(EasingMode ease, boolean g) {
		return setGravity(getWidth(), getHeight(), ease, 1f, g);
	}

	/**
	 * 设定重力系统是否启动,并返回控制器
	 * 
	 * @param ease
	 * @param d
	 * @param g
	 * @return
	 */
	public GravityHandler setGravity(EasingMode ease, float d, boolean g) {
		return setGravity(getWidth(), getHeight(), ease, d, g);
	}

	/**
	 * 设定重力系统是否启动,并返回控制器
	 * 
	 * @param w
	 * @param h
	 * @param ease
	 * @param g
	 * @return
	 */
	public GravityHandler setGravity(int w, int h, EasingMode ease, float d, boolean g) {
		if (g && (_gravityHandler == null || _gravityHandler.isClosed())) {
			_gravityHandler = new GravityHandler(w, h, ease, d);
		}
		this._isGravity = g;
		return _gravityHandler;
	}

	/**
	 * 返回重力系统是否启动
	 * 
	 * @param g
	 * @return
	 */
	public GravityHandler getGravity() {
		return _gravityHandler;
	}

	/**
	 * 返回一个对象和当前其它重力对象的碰撞关系
	 * 
	 * @param g
	 * @return
	 */
	public GravityResult getCollisionBetweenObjects(Gravity g) {
		if (_isGravity) {
			return _gravityHandler.getCollisionBetweenObjects(g);
		}
		return null;
	}

	/**
	 * 判断重力系统是否启动
	 * 
	 * @return
	 */
	public boolean isGravity() {
		return this._isGravity;
	}

	/**
	 * 获得当前重力器句柄
	 * 
	 * @return
	 */
	public GravityHandler getGravityHandler() {
		return setGravity(true);
	}

	/**
	 * 获得当前游戏事务运算时间是否被锁定
	 * 
	 * @return
	 */
	public boolean isLock() {
		return _isLock;
	}

	/**
	 * 锁定游戏事务运算时间
	 * 
	 * @param lock
	 */
	public Screen setLock(boolean lock) {
		this._isLock = lock;
		return this;
	}

	/**
	 * 关闭游戏
	 * 
	 * @param close
	 */
	public Screen setClose(boolean close) {
		this._isClose = close;
		return this;
	}

	/**
	 * 判断游戏是否被关闭
	 * 
	 * @return
	 */
	public boolean isClosed() {
		return _isClose;
	}

	/**
	 * 设定当前帧
	 * 
	 * @param frame
	 */
	public Screen setFrame(int frame) {
		this._currentFrame = frame;
		return this;
	}

	/**
	 * 返回当前帧
	 * 
	 * @return
	 */
	public int getFrame() {
		return _currentFrame;
	}

	/**
	 * 移动当前帧
	 * 
	 * @return
	 */
	public synchronized boolean next() {
		this._currentFrame++;
		return _isNext;
	}

	/**
	 * 初始化时加载的数据
	 */
	public abstract void onLoad();

	/**
	 * 初始化加载完毕
	 * 
	 */
	public void onLoaded() {

	}

	/**
	 * 改变资源加载状态
	 */
	public Screen setOnLoadState(boolean flag) {
		this._isLoad = flag;
		return this;
	}

	/**
	 * 是否处于过渡中
	 * 
	 * @return
	 */
	public boolean isTransitioning() {
		if (_processHandler != null) {
			return _processHandler.isTransitioning();
		}
		// 如果过渡效果不存在，则返回是否加载完毕
		return _isLoad;
	}

	/**
	 * 过度是否完成
	 */
	public boolean isTransitionCompleted() {
		if (_processHandler != null) {
			return _processHandler.isTransitionCompleted();
		}
		// 如果过渡效果不存在，则返回是否加载完毕
		return _isLoad;
	}

	/**
	 * 获得当前资源加载是否完成
	 */
	public boolean isOnLoadComplete() {
		return _isLoad;
	}

	private ReplaceEvent revent;

	/**
	 * 设置一个Screen替换事件的默认布局
	 * 
	 * @param re
	 */
	public void setReplaceEvent(ReplaceEvent re) {
		this.revent = re;
	}

	/**
	 * 返回Screen替换事件
	 * 
	 * @return
	 */
	public ReplaceEvent getReplaceEvent() {
		return this.revent;
	}

	/**
	 * 返回指定Screen替换事件中的对应索引的Screen
	 * 
	 * @param idx
	 * @return
	 */
	public Screen getReplaceScreen(int idx) {
		if (revent == null) {
			return null;
		}
		return this.revent.getScreen(idx);
	}

	/**
	 * 判断指定名称的Screen是否存在
	 * 
	 * @param name
	 * @return
	 */
	public boolean containsScreen(CharSequence name) {
		return _processHandler != null ? _processHandler.containsScreen(name) : false;
	}

	/**
	 * 顺序运行并删除一个Screen
	 * 
	 * @return
	 */
	public Screen runPopScreen() {
		if (_processHandler != null) {
			_processHandler.runPopScreen();
		}
		return this;
	}

	/**
	 * 运行最后一个Screen
	 * 
	 * @return
	 */
	public Screen runPeekScreen() {
		if (_processHandler != null) {
			_processHandler.runPeekScreen();
		}
		return this;
	}

	/**
	 * 取出第一个Screen并执行
	 * 
	 */
	public Screen runFirstScreen() {
		if (_processHandler != null) {
			_processHandler.runFirstScreen();
		}
		return this;
	}

	/**
	 * 取出最后一个Screen并执行
	 */
	public Screen runLastScreen() {
		if (_processHandler != null) {
			_processHandler.runLastScreen();
		}
		return this;
	}

	/**
	 * 运行指定位置的Screen
	 * 
	 * @param index
	 */
	public Screen runIndexScreen(int index) {
		if (_processHandler != null) {
			_processHandler.runIndexScreen(index);
		}
		return this;
	}

	/**
	 * 运行自当前Screen起的上一个Screen
	 */
	public Screen runPreviousScreen() {
		if (_processHandler != null) {
			_processHandler.runPreviousScreen();
		}
		return this;
	}

	/**
	 * 运行自当前Screen起的下一个Screen
	 */
	public Screen runNextScreen() {
		if (_processHandler != null) {
			_processHandler.runNextScreen();
		}
		return this;
	}

	/**
	 * 添加指定名称的Screen到当前Screen，但不立刻执行
	 * 
	 * @param name
	 * @param screen
	 * @return
	 */
	public Screen addScreen(CharSequence name, Screen screen) {
		if (_processHandler != null) {
			_processHandler.addScreen(name, screen);
		}
		return this;
	}

	/**
	 * 获得指定名称的Screen
	 * 
	 * @param name
	 * @return
	 */
	public Screen getScreen(CharSequence name) {
		if (_processHandler != null) {
			return _processHandler.getScreen(name);
		}
		return this;
	}

	/**
	 * 执行指定替换事件索引的Screen
	 * 
	 * @param idx
	 * @return
	 */
	public Screen runEventScreen(int idx) {
		Screen screen = getReplaceScreen(idx);
		if (screen != null && _processHandler != null) {
			_processHandler.setScreen(screen);
		}
		return screen;
	}

	/**
	 * 执行指定名称的Screen
	 * 
	 * @param name
	 * @return
	 */
	public Screen runScreen(CharSequence name) {
		if (_processHandler != null) {
			return _processHandler.runScreen(name);
		}
		return this;
	}

	public Screen clearScreen() {
		if (_processHandler != null) {
			_processHandler.clearScreens();
		}
		return this;
	}

	/**
	 * 向缓存中添加Screen数据，但是不立即执行
	 * 
	 * @param screen
	 */
	public Screen addScreen(Screen screen) {
		if (_processHandler != null) {
			_processHandler.addScreen(screen);
		}
		return this;
	}

	/**
	 * 获得保存的Screen列表
	 * 
	 * @return
	 */
	public TArray<Screen> getScreens() {
		if (_processHandler != null) {
			return _processHandler.getScreens();
		}
		return null;
	}

	/**
	 * 获得缓存的Screen总数
	 */
	public int getScreenCount() {
		if (_processHandler != null) {
			return _processHandler.getScreenCount();
		}
		return 0;
	}

	/**
	 * 判断当前Screen是否能后退一页
	 * 
	 * @return
	 */
	public boolean canScreenBack() {
		return this._screenIndex > 0;
	}

	/**
	 * 判断当前Screen是否能前进一页
	 * 
	 * @return
	 */
	public boolean canScreenNext() {
		return this._screenIndex < getScreenCount() - 1;
	}

	/**
	 * 返回精灵监听
	 * 
	 * @return
	 */

	public SpriteListener getSprListerner() {
		if (_currentSprites == null) {
			return null;
		}
		return _currentSprites.getSprListerner();
	}

	/**
	 * 监听Screen中精灵
	 * 
	 * @param sprListerner
	 */

	public Screen setSprListerner(SpriteListener sprListerner) {
		if (_currentSprites == null) {
			return this;
		}
		_currentSprites.setSprListerner(sprListerner);
		return this;
	}

	/**
	 * 获得当前Screen类名
	 */
	public String getName() {
		return getClass().getSimpleName();
	}

	/**
	 * 设定模拟按钮监听器
	 */

	public Screen setEmulatorListener(EmulatorListener emulator) {
		if (LSystem.getProcess() != null) {
			LSystem.getProcess().setEmulatorListener(emulator);
		}
		return this;
	}

	/**
	 * 返回模拟按钮集合
	 * 
	 * @return
	 */

	public EmulatorButtons getEmulatorButtons() {
		if (LSystem.getProcess() != null) {
			return LSystem.getProcess().getEmulatorButtons();
		}
		return null;
	}

	/**
	 * 设定模拟按钮组是否显示
	 * 
	 * @param visible
	 */

	public Screen emulatorButtonsVisible(boolean v) {
		if (LSystem.getProcess() != null) {
			try {
				EmulatorButtons es = LSystem.getProcess().getEmulatorButtons();
				es.setVisible(v);
			} catch (Throwable e) {
			}
		}
		return this;
	}

	/**
	 * 设定背景图像
	 * 
	 * @param screen
	 */
	public Screen setBackground(final LTexture background) {
		if (background != null) {
			setRepaintMode(SCREEN_TEXTURE_REPAINT);
			_currentScreenBackground = background;
		} else {
			setRepaintMode(SCREEN_NOT_REPAINT);
		}
		return this;
	}

	/**
	 * 设定背景图像
	 */
	public Screen setBackground(String fileName) {
		return this.setBackground(LSystem.loadTexture(fileName));
	}

	/**
	 * 设定背景颜色
	 * 
	 * @param c
	 */
	public Screen setBackground(LColor c) {
		setRepaintMode(SCREEN_COLOR_REPAINT);
		if (_backgroundColor == null) {
			_backgroundColor = new LColor(c);
		} else {
			_backgroundColor.setColor(c.r, c.g, c.b, c.a);
		}
		return this;
	}

	/**
	 * 设定背景颜色
	 * 
	 * @param colorString
	 * @return
	 */
	public Screen setBackgroundValue(String colorString) {
		return setBackground(LColor.decode(colorString));
	}

	/**
	 * 设定背景颜色
	 * 
	 * @param c
	 * @return
	 */
	public Screen setBackgroundString(String c) {
		return setBackground(new LColor(c));
	}

	public LColor getBackgroundColor() {
		return _backgroundColor;
	}

	/**
	 * 返回背景图像
	 * 
	 * @return
	 */
	public LTexture getBackground() {
		return _currentScreenBackground;
	}

	/**
	 * 获得桌面组件（即UI）管理器
	 */
	public Desktop getDesktop() {
		return _currentDesktop;
	}

	/**
	 * @see getDesktop
	 * 
	 * @return
	 */
	public Desktop UI() {
		return getDesktop();
	}

	/**
	 * 获得精灵组件管理器
	 * 
	 * @return
	 */
	public Sprites getSprites() {
		return _currentSprites;
	}

	/**
	 * @see getSprites
	 * 
	 * @return
	 */
	public Sprites ELF() {
		return getSprites();
	}

	public Screen addSpriteGroup(LTexture tex, int count) {
		if (_currentSprites == null) {
			return this;
		}
		_currentSprites.addSpriteGroup(tex, count);
		return this;
	}

	public Screen addEntityGroup(LTexture tex, int count) {
		if (_currentSprites == null) {
			return this;
		}
		_currentSprites.addEntityGroup(tex, count);
		return this;
	}

	public Screen addSpriteGroup(String path, int count) {
		if (_currentSprites == null) {
			return this;
		}
		_currentSprites.addSpriteGroup(path, count);
		return this;
	}

	public Screen addEntityGroup(String path, int count) {
		if (_currentSprites == null) {
			return this;
		}
		_currentSprites.addEntityGroup(path, count);
		return this;
	}

	public Screen addEntityGroup(Created<? extends IEntity> e, int count) {
		if (_currentSprites == null) {
			return this;
		}
		_currentSprites.addEntityGroup(e, count);
		return this;
	}

	/**
	 * 返回位于屏幕顶部的组件
	 * 
	 * @return
	 */

	public LComponent getTopComponent() {
		if (_currentDesktop != null) {
			return _currentDesktop.getTopComponent();
		}
		return null;
	}

	/**
	 * 返回位于屏幕底部的组件
	 * 
	 * @return
	 */

	public LComponent getBottomComponent() {
		if (_currentDesktop != null) {
			return _currentDesktop.getBottomComponent();
		}
		return null;
	}

	public LLayer getTopLayer() {
		if (_currentDesktop != null) {
			return _currentDesktop.getTopLayer();
		}
		return null;
	}

	public LLayer getBottomLayer() {
		if (_currentDesktop != null) {
			return _currentDesktop.getBottomLayer();
		}
		return null;
	}

	/**
	 * 返回位于数据顶部的精灵
	 * 
	 */

	public ISprite getTopSprite() {
		if (_currentSprites != null) {
			return _currentSprites.getTopSprite();
		}
		return null;
	}

	/**
	 * 返回位于数据底部的精灵
	 * 
	 */

	public ISprite getBottomSprite() {
		if (_currentSprites != null) {
			return _currentSprites.getBottomSprite();
		}
		return null;
	}

	public Screen add(Object... obj) {
		for (int i = 0; i < obj.length; i++) {
			add(obj[i]);
		}
		return this;
	}

	/**
	 * 添加游戏对象
	 * 
	 * @param obj
	 * @return
	 */
	public Screen add(Object obj) {
		if (obj instanceof ISprite) {
			add((ISprite) obj);
		} else if (obj instanceof LComponent) {
			add((LComponent) obj);
		} else if (obj instanceof LTexture) {
			addPaper((LTexture) obj, 0, 0);
		} else if (obj instanceof Updateable) {
			addLoad((Updateable) obj);
		} else if (obj instanceof GameProcess) {
			addProcess((GameProcess) obj);
		} else if (obj instanceof LRelease) {
			putRelease((LRelease) obj);
		}
		return this;
	}

	/**
	 * 按照上一个精灵的x,y位置,另起一行添加精灵,并偏移指定位置
	 * 
	 * @param spr
	 * @param offX
	 * @param offY
	 * @return
	 */
	public ISprite addPadding(ISprite spr, float offX, float offY) {
		if (_isClose || _currentSprites == null) {
			return spr;
		}
		return _currentSprites.addPadding(spr, offX, offY);
	}

	/**
	 * 按照上一个精灵的y轴,另起一行添加精灵
	 * 
	 * @param spr
	 * @return
	 */
	public ISprite addCol(ISprite spr) {
		if (_isClose || _currentSprites == null) {
			return spr;
		}
		return _currentSprites.addCol(spr);
	}

	/**
	 * 按照上一个精灵的y轴,另起一行添加精灵,并让y轴偏移指定位置
	 * 
	 * @param spr
	 * @param offY
	 * @return
	 */
	public ISprite addCol(ISprite spr, float offY) {
		if (_isClose || _currentSprites == null) {
			return spr;
		}
		return _currentSprites.addCol(spr, offY);
	}

	/**
	 * 按照上一个精灵的x轴,另起一行添加精灵
	 * 
	 * @param spr
	 * @return
	 */
	public Screen addRow(ISprite spr) {
		if (_isClose || _currentSprites == null) {
			return this;
		}
		_currentSprites.addRow(spr);
		return this;
	}

	/**
	 * 按照上一个精灵的x轴,另起一行添加精灵,并将x轴偏移指定位置
	 * 
	 * @param spr
	 * @param offX
	 * @return
	 */
	public Screen addRow(ISprite spr, float offX) {
		if (_isClose || _currentSprites == null) {
			return this;
		}
		_currentSprites.addRow(spr, offX);
		return this;
	}

	/**
	 * 按照上一个组件的x,y位置,另起一行添加组件,并偏移指定位置
	 * 
	 * @param comp
	 * @param offX
	 * @param offY
	 * @return
	 */
	public Screen addPadding(LComponent comp, float offX, float offY) {
		if (_isClose || _currentDesktop == null) {
			return this;
		}
		_currentDesktop.addPadding(comp, offX, offY);
		if (comp instanceof LTouchArea) {
			registerTouchArea((LTouchArea) comp);
		}
		if (!_desktopPenetrate) {
			addTouchLimit(comp);
		}
		return this;
	}

	/**
	 * 按照上一个组件的y轴,另起一行添加组件
	 * 
	 * @param comp
	 * @return
	 */
	public Screen addCol(LComponent comp) {
		if (_isClose || _currentDesktop == null) {
			return this;
		}
		_currentDesktop.addCol(comp);
		if (comp instanceof LTouchArea) {
			registerTouchArea((LTouchArea) comp);
		}
		if (!_desktopPenetrate) {
			addTouchLimit(comp);
		}
		return this;
	}

	/**
	 * 按照上一个组件的y轴,另起一行添加组件,并偏移指定y轴坐标
	 * 
	 * @param comp
	 * @param offY
	 * @return
	 */
	public Screen addCol(LComponent comp, float offY) {
		if (_isClose || _currentDesktop == null) {
			return this;
		}
		_currentDesktop.addCol(comp, offY);
		if (comp instanceof LTouchArea) {
			registerTouchArea((LTouchArea) comp);
		}
		if (!_desktopPenetrate) {
			addTouchLimit(comp);
		}
		return this;
	}

	/**
	 * 按照上一个组件的x轴,在同一行添加组件
	 * 
	 * @param comp
	 * @return
	 */
	public Screen addRow(LComponent comp) {
		if (_isClose || _currentDesktop == null) {
			return this;
		}
		_currentDesktop.addRow(comp);
		if (comp instanceof LTouchArea) {
			registerTouchArea((LTouchArea) comp);
		}
		if (!_desktopPenetrate) {
			addTouchLimit(comp);
		}
		return this;
	}

	/**
	 * 按照上一个组件的x轴,在同一行添加组件,并偏移指定x轴
	 * 
	 * @param comp
	 * @param offX
	 * @return
	 */
	public Screen addRow(LComponent comp, float offX) {
		if (_isClose || _currentDesktop == null) {
			return this;
		}
		_currentDesktop.addRow(comp, offX);
		if (comp instanceof LTouchArea) {
			registerTouchArea((LTouchArea) comp);
		}
		if (!_desktopPenetrate) {
			addTouchLimit(comp);
		}
		return this;
	}

	/**
	 * 删除指定对象
	 * 
	 * @param obj
	 * @return
	 */
	public Screen remove(Object obj) {
		if (obj instanceof ISprite) {
			remove((ISprite) obj);
		} else if (obj instanceof LComponent) {
			remove((LComponent) obj);
		} else if (obj instanceof LTexture) {
			remove((LTexture) obj);
		} else if (obj instanceof Updateable) {
			removeLoad((Updateable) obj);
		} else if (obj instanceof GameProcess) {
			removeProcess((GameProcess) obj);
		} else if (obj instanceof LRelease) {
			removeRelease((LRelease) obj);
		}
		return this;
	}

	public Screen remove(Object... obj) {
		for (int i = 0; i < obj.length; i++) {
			remove(obj[i]);
		}
		return this;
	}

	/**
	 * 添加游戏精灵
	 * 
	 * @param sprite
	 */

	public Screen add(ISprite sprite) {
		if (_currentSprites != null) {
			_currentSprites.add(sprite);
			if (sprite instanceof LTouchArea) {
				registerTouchArea((LTouchArea) sprite);
			}
		}
		return this;
	}

	/**
	 * 添加游戏精灵
	 * 
	 * @param sprite
	 * @param x
	 * @param y
	 * @return
	 */
	public Screen addAt(ISprite sprite, float x, float y) {
		if (_currentSprites != null) {
			_currentSprites.addAt(sprite, x, y);
			if (sprite instanceof LTouchArea) {
				registerTouchArea((LTouchArea) sprite);
			}
		}
		return this;
	}

	public boolean isDesktopPenetrate() {
		return _desktopPenetrate;
	}

	/**
	 * 是否允许桌面组件触碰事件传导到Screen(若此项为true,则当组件占据屏幕位置时,触发的Touch相关事件Screen的也
	 * Touch监听默认也会收到,反之则无法接收)
	 * 
	 * @param dp
	 * @return
	 */
	public Screen setDesktopPenetrate(boolean dp) {
		if (this._desktopPenetrate == dp) {
			return this;
		}
		this._desktopPenetrate = dp;
		if (this._desktopPenetrate) {
			final TArray<ActionBind> limits = this._actionLimits;
			if (limits != null) {
				final int len = this._actionLimits.size;
				if (len > 0) {
					for (int i = len - 1; i > -1; i--) {
						ActionBind bind = limits.get(i);
						if (bind != null && bind instanceof LComponent) {
							limits.remove(bind);
						}
					}
				}
			}
		} else {
			if (_currentDesktop != null) {
				final LComponent[] comps = _currentDesktop.getComponents();
				if (comps != null) {
					final int size = comps.length;
					for (int i = 0; i < size; i++) {
						LComponent comp = comps[i];
						if (comp != null) {
							addTouchLimit(comp);
						}
					}
				}
			}
		}
		return this;
	}

	/**
	 * 添加游戏组件
	 * 
	 * @param s
	 */

	public Screen addSpriteToUI(ISprite s) {
		if (_currentDesktop != null) {
			LSpriteUI ui = _currentDesktop.addSprite(s);
			if (s instanceof LTouchArea) {
				registerTouchArea((LTouchArea) s);
			}
			if (!_desktopPenetrate) {
				addTouchLimit(ui);
			}
		}
		return this;
	}

	/**
	 * 添加游戏组件
	 * 
	 * @param s
	 * @param x
	 * @param y
	 * @return
	 */
	public Screen addSpriteToUIAt(ISprite s, float x, float y) {
		if (_currentDesktop != null) {
			LSpriteUI ui = _currentDesktop.addSpriteAt(s, x, y);
			if (s instanceof LTouchArea) {
				registerTouchArea((LTouchArea) s);
			}
			if (!_desktopPenetrate) {
				addTouchLimit(ui);
			}
		}
		return this;
	}

	/**
	 * 添加游戏组件
	 * 
	 * @param comp
	 */
	public Screen add(LComponent comp) {
		if (_currentDesktop != null) {
			_currentDesktop.add(comp);
			if (comp instanceof LTouchArea) {
				registerTouchArea((LTouchArea) comp);
			}
			if (!_desktopPenetrate) {
				addTouchLimit(comp);
			}
		}
		return this;
	}

	/**
	 * 添加游戏组件
	 * 
	 * @param comp
	 * @param x
	 * @param y
	 * @return
	 */
	public Screen addAt(LComponent comp, float x, float y) {
		if (_currentDesktop != null) {
			_currentDesktop.addAt(comp, x, y);
			if (comp instanceof LTouchArea) {
				registerTouchArea((LTouchArea) comp);
			}
			if (!_desktopPenetrate) {
				addTouchLimit(comp);
			}
		}
		return this;
	}

	public LMenuSelect addMenu(String labels, int x, int y) {
		LMenuSelect menu = LMenuSelect.make(labels, x, y);
		add(menu);
		return menu;
	}

	public LMenuSelect addMenu(String[] labels, int x, int y) {
		LMenuSelect menu = LMenuSelect.make(labels, x, y);
		add(menu);
		return menu;
	}

	public LMenuSelect addMenu(IFont font, String[] labels, int x, int y) {
		LMenuSelect menu = LMenuSelect.make(font, labels, x, y);
		add(menu);
		return menu;
	}

	public LMenuSelect addMenu(IFont font, String[] labels, String path, int x, int y) {
		LMenuSelect menu = LMenuSelect.make(font, labels, path, x, y);
		add(menu);
		return menu;
	}

	public LMenuSelect addMenu(IFont font, String[] labels, LTexture bg, int x, int y) {
		LMenuSelect menu = LMenuSelect.make(font, labels, bg, x, y);
		add(menu);
		return menu;
	}

	public LClickButton addButton(IFont font, String text, int x, int y, int w, int h) {
		LClickButton click = LClickButton.make(font, text, x, y, w, h);
		add(click);
		return click;
	}

	public LClickButton addButton(String text, int x, int y, int w, int h) {
		LClickButton click = LClickButton.make(text, x, y, w, h);
		add(click);
		return click;
	}

	public LClickButton addButton(String text, int x, int y, int w, int h, Touched touched) {
		LClickButton click = LClickButton.make(text, x, y, w, h);
		add(click);
		return (LClickButton) click.up(touched);
	}

	public LLabel addLabel(String text) {
		return addLabel(text, 0, 0);
	}

	public LLabel addLabel(String text, float x, float y) {
		return addLabel(text, Vector2f.at(x, y));
	}

	public LLabel addLabel(String text, Vector2f pos) {
		LLabel label = LLabel.make(text, pos.x(), pos.y());
		add(label);
		return label;
	}

	public LLabel addLabel(IFont font, String text, float x, float y) {
		return addLabel(font, text, Vector2f.at(x, y));
	}

	public LLabel addLabel(IFont font, String text, Vector2f pos) {
		return addLabel(font, text, pos, LColor.white);
	}

	public LLabel addLabel(IFont font, String text, float x, float y, LColor color) {
		return addLabel(font, text, Vector2f.at(x, y), color);
	}

	public LLabel addLabel(IFont font, String text, Vector2f pos, LColor color) {
		LLabel label = LLabel.make(text, font, pos.x(), pos.y(), color);
		add(label);
		return label;
	}

	public LLabel addLabel(HorizontalAlign alignment, String text, float x, float y, LColor color) {
		return addLabel(alignment, LSystem.getSystemGameFont(), text, Vector2f.at(x, y), color);
	}

	public LLabel addLabel(HorizontalAlign alignment, IFont font, String text, float x, float y, LColor color) {
		return addLabel(alignment, font, text, Vector2f.at(x, y), color);
	}

	public LLabel addLabel(HorizontalAlign alignment, IFont font, String text, Vector2f pos, LColor color) {
		LLabel label = LLabel.make(alignment, text, font, pos.x(), pos.y(), color);
		add(label);
		return label;
	}

	public Screen addSprites(ISprite... spr) {
		for (int i = 0; i < spr.length; i++) {
			add(spr[i]);
		}
		return this;
	}

	public Sprite addSprite(LTexture tex) {
		return addSprite(tex, 0, 0);
	}

	public Sprite addSprite(LTexture tex, Vector2f pos) {
		return addSprite(tex, pos.x, pos.y);
	}

	public Sprite addSprite(LTexture tex, float x, float y) {
		Sprite sprite = new Sprite(tex, x, y);
		add(sprite);
		return sprite;
	}

	public LPaper addPaper(LTexture tex, Vector2f pos) {
		return addPaper(tex, pos.x, pos.y);
	}

	public LPaper addPaper(LTexture tex, float x, float y) {
		LPaper paper = new LPaper(tex, (int) x, (int) y);
		add(paper);
		return paper;
	}

	public SpriteLabel addSpriteLabel(String text, float x, float y) {
		return addSpriteLabel(text, Vector2f.at(x, y));
	}

	public SpriteLabel addSpriteLabel(String text, Vector2f pos) {
		SpriteLabel label = new SpriteLabel(text, pos.x(), pos.y());
		add(label);
		return label;
	}

	public SpriteLabel addSpriteLabel(String text, float x, float y, LColor color) {
		return addSpriteLabel(text, Vector2f.at(x, y), color);
	}

	public SpriteLabel addSpriteLabel(String text, Vector2f pos, LColor color) {
		SpriteLabel label = new SpriteLabel(text, pos.x(), pos.y());
		label.setColor(color);
		add(label);
		return label;
	}

	public SpriteLabel addSpriteLabel(IFont font, String text, float x, float y, LColor color) {
		return addSpriteLabel(font, text, Vector2f.at(x, y), color);
	}

	public SpriteLabel addSpriteLabel(IFont font, String text, Vector2f pos, LColor color) {
		SpriteLabel label = new SpriteLabel(font, text, pos.x(), pos.y());
		label.setColor(color);
		add(label);
		return label;
	}

	public SpriteLabel addSpriteLabel(String fontName, Style type, int size, int x, int y, Style style, String text,
			Vector2f pos, LColor color) {
		SpriteLabel label = new SpriteLabel(text, fontName, style, size, pos.x(), pos.y());
		label.setColor(color);
		add(label);
		return label;
	}

	public boolean contains(ISprite sprite) {
		return contains(sprite, false);
	}

	public boolean contains(ISprite sprite, boolean canView) {
		boolean can = false;
		if (_currentSprites != null) {
			can = _currentSprites.contains(sprite);
		}
		if (canView) {
			return can && contains(sprite.x(), sprite.y(), sprite.getWidth(), sprite.getHeight());
		} else {
			return can;
		}
	}

	public boolean intersects(ISprite sprite) {
		return intersects(sprite, false);
	}

	public boolean intersects(ISprite sprite, boolean canView) {
		boolean can = false;
		if (_currentSprites != null) {
			can = _currentSprites.contains(sprite);
		}
		if (canView) {
			return can && intersects(sprite.x(), sprite.y(), sprite.getWidth(), sprite.getHeight());
		} else {
			return can;
		}
	}

	public Screen remove(ISprite sprite) {
		if (_currentSprites != null) {
			_currentSprites.remove(sprite);
			removeTouchLimit(sprite);
			if (sprite instanceof LTouchArea) {
				unregisterTouchArea((LTouchArea) sprite);
			}
		}
		return this;
	}

	public boolean contains(LComponent comp) {
		return contains(comp, false);
	}

	public boolean contains(LComponent comp, boolean canView) {
		boolean can = false;
		if (_currentDesktop != null) {
			can = _currentDesktop.contains(comp);
		}
		if (canView) {
			return can && getRectBox().contains(comp.getX(), comp.getY(), comp.getWidth(), comp.getHeight());
		} else {
			return can;
		}
	}

	public boolean intersects(LComponent comp) {
		return intersects(comp, false);
	}

	public boolean intersects(LComponent comp, boolean canView) {
		boolean can = false;
		if (_currentDesktop != null) {
			can = _currentDesktop.contains(comp);
		}
		if (canView) {
			return can && getRectBox().intersects(comp.getX(), comp.getY(), comp.getWidth(), comp.getHeight());
		} else {
			return can;
		}
	}

	public Screen remove(LTexture tex) {
		if (_currentDesktop != null) {
			LComponent textureObject = null;
			LComponent[] components = _currentDesktop.getComponents();
			for (int i = 0; i < components.length; i++) {
				LComponent comp = components[i];
				if (comp != null && comp.getBackground() != null && tex.equals(comp.getBackground())) {
					textureObject = comp;
					break;
				}
			}
			if (textureObject != null) {
				remove(textureObject);
			}
		}
		return this;
	}

	public Screen remove(LComponent comp) {
		if (_currentDesktop != null) {
			_currentDesktop.remove(comp);
			removeTouchLimit(comp);
			if (comp instanceof LTouchArea) {
				unregisterTouchArea((LTouchArea) comp);
			}
		}
		return this;
	}

	public boolean containsView(ActionBind obj) {
		return getRectBox().contains(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
	}

	public boolean contains(Object obj) {
		if (obj instanceof ISprite) {
			return contains((ISprite) obj, false);
		} else if (obj instanceof LComponent) {
			return contains((LComponent) obj, false);
		} else if (obj instanceof Updateable) {
			Updateable update = (Updateable) obj;
			boolean can = containsLoad(update);
			if (!can) {
				can = containsUnLoad(update);
			}
			return can;
		} else if (obj instanceof GameProcess) {
			return containsProcess((GameProcess) obj);
		} else if (obj instanceof LRelease) {
			return containsRelease((LRelease) obj);
		}
		return false;
	}

	public Screen removeAll() {
		if (_currentSprites != null) {
			_currentSprites.removeAll();
		}
		if (_currentDesktop != null) {
			_currentDesktop.removeAll();
		}
		ActionControl.get().clear();
		removeAllLoad();
		removeAllUnLoad();
		removeTouchLimit();
		clearTouchAreas();
		clearTouched();
		clearFrameLoop();
		return this;
	}

	/**
	 * 判断是否点中指定精灵
	 * 
	 * @param sprite
	 * @return
	 */
	public boolean onClick(ISprite sprite, float x, float y) {
		if (sprite == null) {
			return false;
		}
		if (sprite.isVisible()) {
			RectBox rect = sprite.getCollisionBox();
			if (rect.contains(x, y) || rect.intersects(x, y)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否点中指定精灵
	 * 
	 * @param sprite
	 * @return
	 */
	public boolean onClick(ISprite sprite) {
		return onClick(sprite, SysTouch.getX(), SysTouch.getY());
	}

	/**
	 * 判断是否点中指定组件
	 * 
	 * @param component
	 * @return
	 */
	public boolean onClick(LComponent component, float x, float y) {
		if (component == null) {
			return false;
		}
		if (component.isVisible() && component.getAlpha() > 0f) {
			RectBox rect = component.getCollisionBox();
			if (rect.contains(x, y) || rect.intersects(x, y)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否点中指定组件
	 * 
	 * @param component
	 * @return
	 */
	public boolean onClick(LComponent component) {
		return onClick(component, SysTouch.getX(), SysTouch.getY());
	}

	public Screen centerOn(final LObject<?> object) {
		LObject.centerOn(object, getWidth(), getHeight());
		return this;
	}

	public Screen centerTopOn(final LObject<?> object) {
		LObject.centerTopOn(object, getWidth(), getHeight());
		return this;
	}

	public Screen centerBottomOn(final LObject<?> object) {
		LObject.centerBottomOn(object, getWidth(), getHeight());
		return this;
	}

	public Screen topOn(final LObject<?> object) {
		LObject.topOn(object, getWidth(), getHeight());
		return this;
	}

	public Screen topLeftOn(final LObject<?> object) {
		LObject.topLeftOn(object, getWidth(), getHeight());
		return this;
	}

	public Screen topRightOn(final LObject<?> object) {
		LObject.topRightOn(object, getWidth(), getHeight());
		return this;
	}

	public Screen leftOn(final LObject<?> object) {
		LObject.leftOn(object, getWidth(), getHeight());
		return this;
	}

	public Screen rightOn(final LObject<?> object) {
		LObject.rightOn(object, getWidth(), getHeight());
		return this;
	}

	public Screen bottomOn(final LObject<?> object) {
		LObject.bottomOn(object, getWidth(), getHeight());
		return this;
	}

	public Screen bottomLeftOn(final LObject<?> object) {
		LObject.bottomLeftOn(object, getWidth(), getHeight());
		return this;
	}

	public Screen bottomRightOn(final LObject<?> object) {
		LObject.bottomRightOn(object, getWidth(), getHeight());
		return this;
	}

	/**
	 * 获得背景显示模式
	 */
	@Override
	public int getRepaintMode() {
		return _currentMode;
	}

	/**
	 * 设定背景刷新模式
	 * 
	 * @param mode
	 */
	@Override
	public void setRepaintMode(int mode) {
		this._currentMode = mode;
	}

	public Screen setLocation(float x, float y) {
		return pos(x, y);
	}

	public Screen pos(float x, float y) {
		this._currentX = x;
		this._currentY = y;
		this._isTranslate = (_currentX != 0 || _currentY != 0);
		return this;
	}

	@Override
	public void setX(float x) {
		this.posX(x);
	}

	public Screen posX(float x) {
		setLocation(x, _currentY);
		return this;
	}

	@Override
	public void setY(float y) {
		this.posY(y);
	}

	public Screen posY(float y) {
		setLocation(_currentX, y);
		return this;
	}

	@Override
	public float getX() {
		return this._currentX;
	}

	@Override
	public float getY() {
		return this._currentY;
	}

	protected LTextureImage createTextureImage(float w, float h) {
		if (LSystem.base() == null) {
			return null;
		}
		return new LTextureImage(LSystem.base().graphics(), LSystem.base().display().GL().batch(), w, h, true);
	}

	/**
	 * 重载此函数,可以自定义渲染Screen的最下层图像
	 * 
	 * @param g
	 */
	protected void afterUI(GLEx g) {
	}

	/**
	 * 添加一组事件到循环开始前
	 * 
	 * @param update
	 * @return
	 */
	public Screen after(Updateable update) {
		return addLoad(update);
	}

	/**
	 * 重载此函数,可以自定义渲染Screen的最上层图像
	 * 
	 * @param g
	 */
	protected void beforeUI(GLEx g) {
	}

	/**
	 * 添加一组事件到循环开始后
	 * 
	 * @param update
	 * @return
	 */
	public Screen before(Updateable update) {
		return addUnLoad(update);
	}

	private final void repaint(GLEx g) {
		if (!_visible) {
			return;
		}
		if (!_isClose) {
			try {
				// 记录屏幕矩阵以及画笔
				g.save();
				if (_baseColor != null) {
					g.setColor(_baseColor);
				}
				if (_alpha != 1f) {
					g.setAlpha(_alpha);
				}
				if (_rotation != 0) {
					g.rotate(getX() + (_pivotX == -1 ? getHalfWidth() : _pivotX),
							getY() + (_pivotY == -1 ? getHalfHeight() : _pivotY), _rotation);
				}
				if (_flipX || _flipY) {
					g.flip(getX(), getY(), getWidth(), getHeight(), _flipX, _flipY);
				}
				if (_scaleX != 1f || _scaleY != 1f) {
					g.scale(_scaleX, _scaleY, getX() + (_pivotX == -1 ? getHalfWidth() : _pivotX),
							getY() + (_pivotY == -1 ? getHalfHeight() : _pivotY));
				}
				// 偏移屏幕
				if (_isTranslate) {
					g.translate(_currentX, _currentY);
				}
				if (_isExistCamera) {
					g.setCamera(_baseCamera);
				}
				if (_isExistViewport) {
					_baseViewport.apply(g);
				}
				int repaintMode = getRepaintMode();
				switch (repaintMode) {
				case Screen.SCREEN_NOT_REPAINT:
					// 默认将background设置为和窗口一样大小
					if (getBackground() != null) {
						g.draw(getBackground(), 0, 0, getWidth(), getHeight());
					}
					break;
				case Screen.SCREEN_TEXTURE_REPAINT:
					g.draw(getBackground(), 0, 0, getWidth(), getHeight());
					break;
				case Screen.SCREEN_COLOR_REPAINT:
					if (getBackground() != null) {
						g.draw(getBackground(), 0, 0, getWidth(), getHeight());
					} else {
						LColor c = getBackgroundColor();
						if (c != null) {
							g.clear(c);
						}
					}
					break;
				default:
					g.draw(getBackground(), repaintMode / 2 - MathUtils.random(repaintMode),
							repaintMode / 2 - MathUtils.random(repaintMode), getWidth(), getHeight());
					break;
				}
				// 最下一层渲染，可重载
				afterUI(g);
				// PS:下列项允许用户调整顺序
				// 精灵
				if (_curFristPaintFlag) {
					_curFristOrder.paint(g);
				}
				// 其次，桌面
				if (_curSecondPaintFlag) {
					_curSecondOrder.paint(g);
				}
				// 最后，用户渲染
				if (_curLastPaintFlag) {
					_curLastOrder.paint(g);
				}
				// 最前一层渲染，可重载
				beforeUI(g);
			} finally {
				if (_isExistViewport) {
					_baseViewport.unapply(g);
				}
				// 若存在摄影机,则还原camera坐标
				if (_isExistCamera) {
					g.restoreTx();
				}
				// 还原屏幕矩阵以及画笔
				g.restore();
			}
		}
	}

	protected void drawFrist(GLEx g) {

	}

	protected void drawLast(GLEx g) {

	}

	public synchronized void createUI(GLEx g) {
		if (_isClose) {
			return;
		}
		if (_replaceLoading) {
			if (_replaceDstScreen == null || !_replaceDstScreen.isOnLoadComplete()) {
				repaint(g);
			} else if (_screenSwitch != null) {
				_replaceDstScreen.createUI(g);
				repaint(g);
			} else if (_replaceDstScreen.isOnLoadComplete()) {
				if (_isScreenFrom) {
					repaint(g);
					if (_curDstPos.x() != 0 || _curDstPos.y() != 0) {
						g.setClip(_curDstPos.x(), _curDstPos.y(), getWidth(), getHeight());
						g.translate(_curDstPos.x(), _curDstPos.y());
					}
					_replaceDstScreen.createUI(g);
					if (_curDstPos.x() != 0 || _curDstPos.y() != 0) {
						g.translate(-_curDstPos.x(), -_curDstPos.y());
						g.clearClip();
					}
				} else {
					_replaceDstScreen.createUI(g);
					if (_curDstPos.x() != 0 || _curDstPos.y() != 0) {
						g.setClip(_curDstPos.x(), _curDstPos.y(), getWidth(), getHeight());
						g.translate(_curDstPos.x(), _curDstPos.y());
					}
					repaint(g);
					if (_curDstPos.x() != 0 || _curDstPos.y() != 0) {
						g.translate(-_curDstPos.x(), -_curDstPos.y());
						g.clearClip();
					}
				}
			}
		} else {
			repaint(g);
		}
	}

	public Screen setScreenDelay(long delay) {
		this._delayTimer.setDelay(delay);
		return this;
	}

	public long getScreenDelay() {
		return this._delayTimer.getDelay();
	}

	/**
	 * 暂停进程处理指定时间
	 * 
	 * @param delay
	 */
	public Screen processSleep(long delay) {
		_pauseTimer.setDelay(delay);
		_isTimerPaused = true;
		return this;
	}

	private void allocateLoopEvents() {
		if (_loopEvents == null) {
			_loopEvents = new TArray<FrameLoopEvent>();
		}
	}

	public Screen loop(FrameLoopEvent event) {
		addFrameLoop(event);
		return this;
	}

	public Screen loop(float second, FrameLoopEvent event) {
		return addFrameLoop(second, event);
	}

	public Screen addFrameLoop(TArray<FrameLoopEvent> events) {
		allocateLoopEvents();
		_loopEvents.addAll(events);
		_initLoopEvents = true;
		return this;
	}

	public Screen addFrameLoop(float second, FrameLoopEvent event) {
		allocateLoopEvents();
		if (event != null) {
			event.setSecond(second);
		}
		_loopEvents.add(event);
		_initLoopEvents = true;
		return this;
	}

	public Screen addFrameLoop(FrameLoopEvent event) {
		allocateLoopEvents();
		_loopEvents.add(event);
		_initLoopEvents = true;
		return this;
	}

	public Screen removeFrameLoop(FrameLoopEvent event) {
		allocateLoopEvents();
		_loopEvents.remove(event);
		_initLoopEvents = (_loopEvents.size <= 0);
		return this;
	}

	public Screen clearFrameLoop() {
		if (_loopEvents == null) {
			_initLoopEvents = false;
			return this;
		}
		_loopEvents.clear();
		_initLoopEvents = false;
		return this;
	}

	public Screen disableFrameBuffer() {
		LSystem.disableFrameBuffer();
		return this;
	}

	public Screen enableFrameBuffer() {
		LSystem.enableFrameBuffer();
		return this;
	}

	public Screen setShaderSource(ShaderSource src) {
		LSystem.setShaderSource(src);
		return this;
	}

	public ShaderSource getShaderSource() {
		return LSystem.getShaderSource();
	}

	private final void process(final LTimerContext timer) {
		this.elapsedTime = timer.timeSinceLastUpdate;
		for (Iterator<ActionKey> it = _keyActions.iterator(); it.hasNext();) {
			ActionKey act = it.next();
			if (act != null && act.isPressed()) {
				act.act(elapsedTime);
				if (act.isInterrupt()) {
					return;
				}
			}
		}
		// 如果Screen设置了计时器暂停
		if (_isTimerPaused) {
			// 开始累加时间
			_pauseTimer.addPercentage(timer);
			// 当还在暂停中，则不处理所有update事件，直接退出此进程
			if (!_pauseTimer.isCompleted()) {
				return;
			} else {
				// 还原计时器暂停
				_isTimerPaused = false;
				_pauseTimer.refresh();
			}
		}
		if (_delayTimer.action(elapsedTime)) {
			if (_isProcessing && !_isClose) {
				if (_isGravity) {
					_gravityHandler.update(elapsedTime);
				}
				if (_curFristPaintFlag) {
					_curFristOrder.update(timer);
				}
				if (_curSecondPaintFlag) {
					_curSecondOrder.update(timer);
				}
				if (_curLastPaintFlag) {
					_curLastOrder.update(timer);
				}
			}
		}
		// 处理直接加入screen中的循环
		if (_initLoopEvents) {
			if (_loopEvents != null && _loopEvents.size > 0) {
				final TArray<FrameLoopEvent> toUpdated;
				synchronized (this._loopEvents) {
					toUpdated = new TArray<FrameLoopEvent>(this._loopEvents);
				}
				final TArray<FrameLoopEvent> deadEvents = new TArray<FrameLoopEvent>();
				try {
					for (FrameLoopEvent eve : toUpdated) {
						eve.call(elapsedTime, this);
						if (eve.isDead()) {
							deadEvents.add(eve);
						}
					}
					if (deadEvents.size > 0) {
						for (FrameLoopEvent dead : deadEvents) {
							dead.completed();
						}
						synchronized (this._loopEvents) {
							this._loopEvents.removeAll(deadEvents);
						}
					}
				} catch (Throwable cause) {
					LSystem.error("FrameLoopEvent dispatch failure", cause);
				}
			}
		}
		this._touchDX = SysTouch.getX() - _lastTouchX;
		this._touchDY = SysTouch.getY() - _lastTouchY;
		this._lastTouchX = SysTouch.getX();
		this._lastTouchY = SysTouch.getY();
		this._touchButtonReleased = NO_BUTTON;
	}

	public PointF getLastTouch() {
		return _lastTocuh;
	}

	public float getLastTouchX() {
		return _lastTocuh.x;
	}

	public float getLastTouchY() {
		return _lastTocuh.y;
	}

	public void runTimer(final LTimerContext timer) {
		if (_isClose) {
			return;
		}
		_coroutineProcess.run(timer);
		if (_replaceLoading) {
			// 无替换对象
			if (_replaceDstScreen == null || !_replaceDstScreen.isOnLoadComplete()) {
				process(timer);
				// 渐进效果替换
			} else if (_screenSwitch != null) {
				process(timer);
				if (_replaceDelay.action(timer)) {
					_screenSwitch.update(timer.timeSinceLastUpdate);
				}
				if (_screenSwitch.isCompleted()) {
					submitReplaceScreen();
				}
				// 位移替换
			} else if (_replaceDstScreen.isOnLoadComplete()) {
				process(timer);
				if (_replaceDelay.action(timer)) {
					switch (_curReplaceMethod) {
					case FROM_LEFT:
						_curDstPos.move_right(_replaceScreenSpeed);
						if (_curDstPos.x() >= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case FROM_RIGHT:
						_curDstPos.move_left(_replaceScreenSpeed);
						if (_curDstPos.x() <= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case FROM_UP:
						_curDstPos.move_down(_replaceScreenSpeed);
						if (_curDstPos.y() >= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case FROM_DOWN:
						_curDstPos.move_up(_replaceScreenSpeed);
						if (_curDstPos.y() <= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_LEFT:
						_curDstPos.move_left(_replaceScreenSpeed);
						if (_curDstPos.x() < -getWidth()) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_RIGHT:
						_curDstPos.move_right(_replaceScreenSpeed);
						if (_curDstPos.x() > getWidth()) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_UP:
						_curDstPos.move_up(_replaceScreenSpeed);
						if (_curDstPos.y() < -getHeight()) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_DOWN:
						_curDstPos.move_down(_replaceScreenSpeed);
						if (_curDstPos.y() > getHeight()) {
							submitReplaceScreen();
							return;
						}
						break;
					case FROM_UPPER_LEFT:
						if (_curDstPos.y() < 0) {
							_curDstPos.move_45D_right(_replaceScreenSpeed);
						} else {
							_curDstPos.move_right(_replaceScreenSpeed);
						}
						if (_curDstPos.y() >= 0 && _curDstPos.x() >= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case FROM_UPPER_RIGHT:
						if (_curDstPos.y() < 0) {
							_curDstPos.move_45D_down(_replaceScreenSpeed);
						} else {
							_curDstPos.move_left(_replaceScreenSpeed);
						}
						if (_curDstPos.y() >= 0 && _curDstPos.x() <= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case FROM_LOWER_LEFT:
						if (_curDstPos.y() > 0) {
							_curDstPos.move_45D_up(_replaceScreenSpeed);
						} else {
							_curDstPos.move_right(_replaceScreenSpeed);
						}
						if (_curDstPos.y() <= 0 && _curDstPos.x() >= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case FROM_LOWER_RIGHT:
						if (_curDstPos.y() > 0) {
							_curDstPos.move_45D_left(_replaceScreenSpeed);
						} else {
							_curDstPos.move_left(_replaceScreenSpeed);
						}
						if (_curDstPos.y() <= 0 && _curDstPos.x() <= 0) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_UPPER_LEFT:
						_curDstPos.move_45D_left(_replaceScreenSpeed);
						if (_curDstPos.x() < -getWidth() || _curDstPos.y() <= -getHeight()) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_UPPER_RIGHT:
						_curDstPos.move_45D_up(_replaceScreenSpeed);
						if (_curDstPos.x() > getWidth() || _curDstPos.y() < -getHeight()) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_LOWER_LEFT:
						_curDstPos.move_45D_down(_replaceScreenSpeed);
						if (_curDstPos.x() < -getWidth() || _curDstPos.y() > getHeight()) {
							submitReplaceScreen();
							return;
						}
						break;
					case OUT_LOWER_RIGHT:
						_curDstPos.move_45D_right(_replaceScreenSpeed);
						if (_curDstPos.x() > getWidth() || _curDstPos.y() > getHeight()) {
							submitReplaceScreen();
							return;
						}
						break;
					default:
						break;
					}
					_replaceDstScreen.runTimer(timer);
				}
			}
		} else {
			process(timer);
		}
	}

	public SysInput getInput() {
		return this;
	}

	public Screen setNext(boolean next) {
		this._isNext = next;
		return this;
	}

	public abstract void alter(LTimerContext context);

	/**
	 * 设定游戏窗体
	 */
	public Screen setScreen(Screen screen) {
		if (_processHandler != null) {
			this._processHandler.setScreen(screen);
		}
		return this;
	}

	public int getScreenWidth() {
		return (int) (_currentWidth * this._scaleX);
	}

	public int getScreenHeight() {
		return (int) (_currentHeight * this._scaleY);
	}

	@Override
	public int getWidth() {
		return _currentWidth;
	}

	@Override
	public int getHeight() {
		return _currentHeight;
	}

	/**
	 * 刷新基础设置
	 */
	@Override
	public void refresh() {
		_touchTypes.clear();
		_keyTypes.clear();
		_touchDX = _touchDY = 0;
	}

	public abstract void resize(int w, int h);

	@Override
	public PointI getTouch() {
		_touch.set((int) SysTouch.getX(), (int) SysTouch.getY());
		return _touch;
	}

	public boolean isPaused() {
		return LSystem.PAUSED;
	}

	@Override
	public int getTouchPressed() {
		return _touchButtonPressed > NO_BUTTON ? _touchButtonPressed : NO_BUTTON;
	}

	@Override
	public int getTouchReleased() {
		return _touchButtonReleased > NO_BUTTON ? _touchButtonReleased : NO_BUTTON;
	}

	@Override
	public boolean isTouchPressed(int button) {
		return _touchButtonPressed == button;
	}

	@Override
	public boolean isTouchReleased(int button) {
		return _touchButtonReleased == button;
	}

	public boolean isTouchPressed() {
		return isTouchPressed(SysTouch.TOUCH_DOWN);
	}

	public boolean isTouchReleased() {
		return isTouchReleased(SysTouch.TOUCH_UP);
	}

	@Override
	public boolean isLongPressed() {
		return isLongPressed(LSystem.LONG_PRESSED_TIME);
	}

	@Override
	public boolean isLongPressed(float seconds) {
		if (isTouchReleased()) {
			return false;
		}
		if (_downUpTimer.completed()) {
			return false;
		}
		if (!isTouchPressed()) {
			final long timer = getDownUpTimer();
			if (timer >= seconds * LSystem.SECOND) {
				return true;
			}
		} else {
			long endTimer = 0;
			if (!_downUpTimer.completed()) {
				endTimer = _downUpTimer.getTimestamp();
			}
			endTimer = endTimer - getDownUpStartTimer();
			if (endTimer >= seconds * LSystem.SECOND) {
				return true;
			}
		}
		return false;
	}

	public long getDownUpStartTimer() {
		return _downUpTimer.getStartTime();
	}

	public long getDownUpEndTimer() {
		return _downUpTimer.getEndTime();
	}

	public long getDownUpTimer() {
		return _downUpTimer.getDuration();
	}

	public long getDownUpLastTimer() {
		return _downUpTimer.getLastDuration();
	}

	public float getDownUpStartTimerSeconds() {
		return Duration.toS(getDownUpStartTimer());
	}

	public float getDownUpEndTimerSeconds() {
		return Duration.toS(getDownUpEndTimer());
	}

	public float getDownUpTimerSeconds() {
		return Duration.toS(getDownUpTimer());
	}

	public float getDownUpLastTimerSeconds() {
		return Duration.toS(getDownUpLastTimer());
	}

	@Override
	public int getTouchX() {
		return (int) SysTouch.getX();
	}

	@Override
	public int getTouchY() {
		return (int) SysTouch.getY();
	}

	@Override
	public int getTouchDX() {
		return (int) _touchDX;
	}

	@Override
	public int getTouchDY() {
		return (int) _touchDY;
	}

	@Override
	public boolean isTouchType(int type) {
		Boolean bn = _touchTypes.get(type);
		if (bn == null) {
			return false;
		}
		return bn.booleanValue();
	}

	@Override
	public int getKeyPressed() {
		return _keyButtonPressed > NO_KEY ? _keyButtonPressed : NO_KEY;
	}

	@Override
	public boolean isKeyPressed(int keyCode) {
		return _keyButtonPressed == keyCode;
	}

	@Override
	public int getKeyReleased() {
		return _keyButtonReleased > NO_KEY ? _keyButtonReleased : NO_KEY;
	}

	@Override
	public boolean isKeyReleased(int keyCode) {
		return _keyButtonReleased == keyCode;
	}

	@Override
	public boolean isKeyType(int type) {
		Boolean bn = _keyTypes.get(type);
		if (bn == null) {
			return false;
		}
		return bn.booleanValue();
	}

	public boolean isNotAllowTouch() {
		return _isLock || _isClose || !_isLoad;
	}

	public boolean isAllowTouch() {
		return !isNotAllowTouch();
	}

	public final void keyPressed(GameKey e) {
		if (isNotAllowTouch()) {
			return;
		}
		int type = e.getTypeCode();
		int code = e.getKeyCode();
		try {
			int keySize = _keyActions.size();
			if (keySize > 0) {
				int keyCode = e.getKeyCode();
				ActionKey act = _keyActions.get(keyCode);
				if (act != null) {
					act.press();
				}
			}
			this.onKeyDown(e);
			if (_currentDesktop != null) {
				_currentDesktop.keyPressed(e);
			}
			_keyTypes.put(type, Boolean.valueOf(true));
			_keyButtonPressed = code;
			_keyButtonReleased = NO_KEY;
		} catch (Throwable ex) {
			_keyButtonPressed = NO_KEY;
			_keyButtonReleased = NO_KEY;
			error("Screen keyPressed() exception", ex);
		}
	}

	/**
	 * 设置键盘按下事件
	 * 
	 * @param code
	 */
	public void setKeyDown(int button) {
		_keyButtonPressed = button;
		_keyButtonReleased = NO_KEY;
	}

	public final void keyReleased(GameKey e) {
		if (isNotAllowTouch()) {
			return;
		}
		int type = e.getTypeCode();
		int code = e.getKeyCode();
		try {
			int keySize = _keyActions.size();
			if (keySize > 0) {
				int keyCode = e.getKeyCode();
				ActionKey act = _keyActions.get(keyCode);
				if (act != null) {
					act.release();
				}
			}
			this.onKeyUp(e);
			if (_currentDesktop != null) {
				_currentDesktop.keyReleased(e);
			}
			this.releaseActionKeys();
			_keyTypes.put(type, Boolean.valueOf(false));
			_keyButtonReleased = code;
			_keyButtonPressed = NO_KEY;
		} catch (

		Throwable ex) {
			_keyButtonPressed = NO_KEY;
			_keyButtonReleased = NO_KEY;
			error("Screen keyReleased() exception", ex);
		}
	}

	@Override
	public void setKeyUp(int button) {
		_keyButtonReleased = button;
		_keyButtonPressed = NO_KEY;
	}

	public void keyTyped(GameKey e) {
		if (isNotAllowTouch()) {
			return;
		}
		onKeyTyped(e);
	}

	public void onKeyDown(GameKey e) {

	}

	public void onKeyUp(GameKey e) {

	}

	public void onKeyTyped(GameKey e) {

	}

	public final void mousePressed(GameTouch e) {
		if (isNotAllowTouch()) {
			return;
		}
		if (_isTranslate) {
			e.offset(_currentX, _currentY);
		}
		int type = e.getTypeCode();
		int button = e.getButton();
		try {
			if (!isTouchPressed()) {
				_downUpTimer.start();
			}
			_touchTypes.put(type, Boolean.TRUE);
			_touchButtonPressed = button;
			_touchButtonReleased = NO_BUTTON;
			if (!isClickLimit(e)) {
				updateTouchArea(Event.DOWN, e.getX(), e.getY());
				touchDown(e);
				if (_touchListener != null && _currentDesktop != null) {
					_touchListener.DownClick(_currentDesktop.getSelectedComponent(), e.getX(), e.getY());
				}
			}
			_lastTocuh.set(e.getX(), e.getY());
		} catch (Throwable ex) {
			_touchButtonPressed = NO_BUTTON;
			_touchButtonReleased = NO_BUTTON;
			error("Screen mousePressed() exception", ex);
		}
	}

	public abstract void touchDown(GameTouch e);

	public void mouseReleased(GameTouch e) {
		if (isNotAllowTouch()) {
			return;
		}
		if (_isTranslate) {
			e.offset(_currentX, _currentY);
		}
		int type = e.getTypeCode();
		int button = e.getButton();
		try {
			if (isTouchPressed()) {
				_downUpTimer.stop();
			}
			_touchTypes.put(type, Boolean.FALSE);
			_touchButtonReleased = button;
			_touchButtonPressed = NO_BUTTON;
			if (!isClickLimit(e)) {
				updateTouchArea(Event.UP, e.getX(), e.getY());
				touchUp(e);
				if (_touchListener != null && _currentDesktop != null) {
					_touchListener.UpClick(_currentDesktop.getSelectedComponent(), e.getX(), e.getY());
				}
			}
			_lastTocuh.set(e.getX(), e.getY());
		} catch (Throwable ex) {
			_touchButtonPressed = NO_BUTTON;
			_touchButtonReleased = NO_BUTTON;
			error("Screen mouseReleased() exception", ex);
		}
	}

	public abstract void touchUp(GameTouch e);

	public void mouseMoved(GameTouch e) {
		if (isNotAllowTouch()) {
			return;
		}
		if (_isTranslate) {
			e.offset(_currentX, _currentY);
		}
		if (!isClickLimit(e)) {
			updateTouchArea(Event.MOVE, e.getX(), e.getY());
			touchMove(e);
		}
	}

	public abstract void touchMove(GameTouch e);

	public void mouseDragged(GameTouch e) {
		if (isNotAllowTouch()) {
			return;
		}
		if (_isTranslate) {
			e.offset(_currentX, _currentY);
		}
		if (!isClickLimit(e)) {
			updateTouchArea(Event.DRAG, e.getX(), e.getY());
			touchDrag(e);
			if (_touchListener != null && _currentDesktop != null) {
				_touchListener.DragClick(_currentDesktop.getSelectedComponent(), e.getX(), e.getY());
			}
		}
		_lastTocuh.set(e.getX(), e.getY());
	}

	public abstract void touchDrag(GameTouch e);

	public LComponent getSelectedComponent() {
		return getDesktop().getSelectedComponent();
	}

	/**
	 * 判定是否点击了指定位置
	 * 
	 * @param event
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public boolean inBounds(GameTouch event, float x, float y, float w, float h) {
		return (event.x() > x && event.x() < x + w - 1 && event.y() > y && event.y() < y + h - 1);
	}

	/**
	 * 判定是否点击了指定位置
	 * 
	 * @param event
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public boolean inBounds(LTouchLocation event, float x, float y, float w, float h) {
		return (event.x() > x && event.x() < x + w - 1 && event.y() > y && event.y() < y + h - 1);
	}

	/**
	 * 判定是否点击了指定位置
	 * 
	 * @param event
	 * @param o
	 * @return
	 */
	public boolean inBounds(GameTouch event, LObject<?> o) {
		RectBox rect = o.getCollisionArea();
		if (rect != null) {
			return inBounds(event, rect);
		} else {
			return false;
		}
	}

	/**
	 * 判定是否点击了指定位置
	 * 
	 * @param event
	 * @param o
	 * @return
	 */
	public boolean inBounds(GameTouch event, ISprite o) {
		return inBounds(event, o.x(), o.y(), o.getWidth(), o.getHeight());
	}

	/**
	 * 判定是否点击了指定位置
	 * 
	 * @param event
	 * @param o
	 * @return
	 */
	public boolean inBounds(GameTouch event, LComponent o) {
		return inBounds(event, o.x(), o.y(), o.getWidth(), o.getHeight());
	}

	/**
	 * 判定是否点击了指定位置
	 * 
	 * @param event
	 * @param rect
	 * @return
	 */
	public boolean inBounds(GameTouch event, RectBox rect) {
		return inBounds(event, rect.x, rect.y, rect.width, rect.height);
	}

	@Override
	public boolean isMoving() {
		return SysTouch.isDrag();
	}

	public int getHalfWidth() {
		return _halfWidth;
	}

	public int getHalfHeight() {
		return _halfHeight;
	}

	public Accelerometer.SensorDirection setSensorDirection(Accelerometer.SensorDirection dir) {
		return (this._direction = dir);
	}

	public Accelerometer.SensorDirection getSensorDirection() {
		return this._direction;
	}

	public PaintOrder getFristOrder() {
		return _curFristOrder;
	}

	public Screen setFristOrder(PaintOrder fristOrder) {
		if (fristOrder == null) {
			this._curFristPaintFlag = false;
		} else {
			this._curFristPaintFlag = true;
			this._curFristOrder = fristOrder;
		}
		return this;
	}

	public PaintOrder getSecondOrder() {
		return _curSecondOrder;
	}

	public Screen setSecondOrder(PaintOrder secondOrder) {
		if (secondOrder == null) {
			this._curSecondPaintFlag = false;
		} else {
			this._curSecondPaintFlag = true;
			this._curSecondOrder = secondOrder;
		}
		return this;
	}

	public PaintOrder getLastOrder() {
		return _curLastOrder;
	}

	public Screen setLastOrder(PaintOrder lastOrder) {
		if (lastOrder == null) {
			this._curLastPaintFlag = false;
		} else {
			this._curLastPaintFlag = true;
			this._curLastOrder = lastOrder;
		}
		return this;
	}

	public Screen clearListener() {
		_touchListener = null;
		_drawListener = null;
		_loopEvents = null;
		_touchAreas.clear();
		return this;
	}

	public Display getDisplay() {
		return LSystem.base().display();
	}

	/**
	 * 设定是否允许自行释放桌面组件(UI)资源
	 * 
	 * @param a
	 * @return
	 */
	public Screen setAutoDestory(final boolean a) {
		if (_currentDesktop != null) {
			_currentDesktop.setAutoDestory(a);
		}
		return this;
	}

	public boolean isAutoDestory() {
		if (_currentDesktop != null) {
			return _currentDesktop.isAutoDestory();
		}
		return false;
	}

	/**
	 * 检查指定组件是否显示于_currentDesktop当中
	 * 
	 * @param comp
	 * @return
	 */
	public boolean isVisibleInParents(LComponent comp) {
		if (_currentDesktop != null) {
			return _currentDesktop.isVisibleInParents(comp);
		}
		return false;
	}

	/**
	 * 获得指定名称的资源管理器
	 */
	public ResourceLocal RES(String path) {
		return getResourceConfig(path);
	}

	/**
	 * 获得指定名称的资源管理器
	 * 
	 * @param path
	 * @return
	 */
	public ResourceLocal getResourceConfig(String path) {
		if (LSystem.base() == null) {
			return new ResourceLocal(path);
		}
		return LSystem.base().assets().getJsonResource(path);
	}

	/**
	 * 当前Screen是否旋转
	 * 
	 * @return
	 */
	public boolean isRotated() {
		return this._rotation != 0;
	}

	/**
	 * 设定Screen旋转角度
	 * 
	 * @param r
	 */
	public Screen setRotation(float r) {
		this._rotation = r;
		if (_rotation > 360f) {
			_rotation = 0f;
		}
		return this;
	}

	public float getRotation() {
		return _rotation;
	}

	public Screen setPivotX(float pX) {
		this._pivotX = pX;
		return this;
	}

	public Screen setPivotY(float pY) {
		this._pivotY = pY;
		return this;
	}

	public float getPivotX() {
		return _pivotX;
	}

	public float getPivotY() {
		return _pivotY;
	}

	public Screen setPivot(float pX, float pY) {
		setPivotX(pX);
		setPivotY(pY);
		return this;
	}

	public boolean isScaled() {
		return (this._scaleX != 1) || (this._scaleY != 1);
	}

	public float getScaleX() {
		return this._scaleX;
	}

	public float getScaleY() {
		return this._scaleY;
	}

	public Screen setScaleX(final float sx) {
		this._scaleX = sx;
		return this;
	}

	public Screen setScaleY(final float sy) {
		this._scaleY = sy;
		return this;
	}

	public Screen setScale(final float pScale) {
		return setScale(pScale, pScale);
	}

	public Screen setScale(final float sx, final float sy) {
		this._scaleX = sx;
		this._scaleY = sy;
		return this;
	}

	public boolean isVisible() {
		return this._visible;
	}

	public Screen setVisible(final boolean v) {
		this._visible = v;
		return this;
	}

	public boolean isTxUpdate() {
		return _scaleX != 1f || _scaleY != 1f || _rotation != 0 || _flipX || _flipY || _currentX != 0 || _currentY != 0;
	}

	public Screen setAlpha(float a) {
		this._alpha = a;
		return this;
	}

	public float getAlpha() {
		return this._alpha;
	}

	public Screen setColor(LColor color) {
		this._baseColor = color;
		return this;
	}

	public LColor getColor() {
		return this._baseColor;
	}

	public Session getSession(String name) {
		return new Session(name);
	}

	public ConfigReader getConfigFile(String path) {
		return new ConfigReader(path);
	}

	public IFont getGameFont() {
		return LSystem.getSystemGameFont();
	}

	public Screen setGameFont(IFont font) {
		LSystem.setSystemGameFont(font);
		return this;
	}

	public IFont getLogFont() {
		return LSystem.getSystemLogFont();
	}

	public Screen setLogFont(IFont font) {
		LSystem.setSystemLogFont(font);
		return this;
	}

	public RectBox getBox() {
		return getRectBox();
	}

	public RectBox getRectBox() {
		if (_rectBox != null) {
			_rectBox.setBounds(MathUtils.getBounds(getScalePixelX(), getScalePixelY(), getScreenWidth(),
					getScreenHeight(), _rotation, _rectBox));
		} else {
			_rectBox = MathUtils.getBounds(getScalePixelX(), getScalePixelY(), getScreenWidth(), getScreenHeight(),
					_rotation, _rectBox);
		}
		return _rectBox;
	}

	public float getScalePixelX() {
		if (_pivotX != -1f) {
			return getX() + _pivotX;
		}
		return ((_scaleX == 1f) ? getX() : (getX() + getScreenWidth() / 2));
	}

	public float getScalePixelY() {
		if (_pivotY != -1f) {
			return getY() + _pivotY;
		}
		return ((_scaleY == 1f) ? getY() : (getY() + getScreenHeight() / 2));
	}

	public ScreenAction getScreenAction() {
		synchronized (this) {
			if (_screenAction == null) {
				_screenAction = new ScreenAction(this);
			} else {
				_screenAction.set(this);
			}
			return _screenAction;
		}
	}

	public boolean isFlipX() {
		return _flipX;
	}

	public Screen setFlipX(boolean flipX) {
		this._flipX = flipX;
		return this;
	}

	public boolean isFlipY() {
		return _flipY;
	}

	public Screen setFlipY(boolean flipY) {
		this._flipY = flipY;
		return this;
	}

	public Screen setFlipXY(boolean flipX, boolean flpiY) {
		setFlipX(flipX);
		setFlipY(flpiY);
		return this;
	}

	public boolean isActionCompleted() {
		return _screenAction == null || isActionCompleted(getScreenAction());
	}

	/**
	 * 返回Screen的动作事件
	 * 
	 * @return
	 */
	public ActionTween selfAction() {
		return set(getScreenAction(), false);
	}

	/**
	 * 创建一个针对此Screen的组件控制器
	 * 
	 * @return
	 */
	public UIControls createUIControls() {
		if (_currentDesktop != null) {
			return _currentDesktop.createUIControls();
		}
		return new UIControls();
	}

	public UIControls findUINames(String... uiName) {
		if (_currentDesktop != null) {
			return _currentDesktop.findUINamesToUIControls(uiName);
		}
		return new UIControls();
	}

	public UIControls findNotUINames(String... uiName) {
		if (_currentDesktop != null) {
			return _currentDesktop.findNotUINamesToUIControls(uiName);
		}
		return new UIControls();
	}

	/**
	 * 插找桌面组件名等于此序列中的组件控制器
	 * 
	 * @param name
	 * @return
	 */
	public UIControls findNames(String... name) {
		if (_currentDesktop != null) {
			return _currentDesktop.findNamesToUIControls(name);
		}
		return new UIControls();
	}

	/**
	 * 插找桌面组件名包含在此序列中的组件控制器
	 * 
	 * @param name
	 * @return
	 */
	public UIControls findNameContains(String... name) {
		if (_currentDesktop != null) {
			return _currentDesktop.findNameContainsToUIControls(name);
		}
		return new UIControls();
	}

	/**
	 * 插找桌面组件名不包含在此序列中的组件控制器
	 * 
	 * @param name
	 * @return
	 */
	public UIControls findNotNames(String... name) {
		if (_currentDesktop != null) {
			return _currentDesktop.findNotNamesToUIControls(name);
		}
		return new UIControls();
	}

	/**
	 * 插找桌面组件Tag包含在此序列中的组件控制器
	 * 
	 * @param o
	 * @return
	 */
	public UIControls findTags(Object... o) {
		if (_currentDesktop != null) {
			return _currentDesktop.findTagsToUIControls(o);
		}
		return new UIControls();
	}

	/**
	 * 插找桌面组件Tag不包含在此序列中的组件控制器
	 * 
	 * @param o
	 * @return
	 */
	public UIControls findNotTags(Object... o) {
		if (_currentDesktop != null) {
			return _currentDesktop.findNotTagsToUIControls(o);
		}
		return new UIControls();
	}

	/**
	 * 创建一个针对当前Screen的精灵控制器
	 * 
	 * @return
	 */
	public SpriteControls createSpriteControls() {
		if (_currentSprites != null) {
			return _currentSprites.createSpriteControls();
		}
		return new SpriteControls();
	}

	/**
	 * 查找等于指定名的精灵控制器
	 * 
	 * @param names
	 * @return
	 */
	public SpriteControls findSpriteNames(String... names) {
		if (_currentSprites != null) {
			return _currentSprites.findNamesToSpriteControls(names);
		}
		return new SpriteControls();
	}

	/**
	 * 查找包含在指定名中的精灵控制器
	 * 
	 * @param names
	 * @return
	 */
	public SpriteControls findSpriteNameContains(String... names) {
		if (_currentSprites != null) {
			return _currentSprites.findNameContainsToSpriteControls(names);
		}
		return new SpriteControls();
	}

	/**
	 * 查找不在此序列中的精灵控制器
	 * 
	 * @param names
	 * @return
	 */
	public SpriteControls findSpriteNotNames(String... names) {
		if (_currentSprites != null) {
			return _currentSprites.findNotNamesToSpriteControls(names);
		}
		return new SpriteControls();
	}

	/**
	 * 查找所有【包含】指定tag的精灵控制器
	 * 
	 * @param o
	 * @return
	 */
	public SpriteControls findSpriteTags(Object... o) {
		if (_currentSprites != null) {
			return _currentSprites.findTagsToSpriteControls(o);
		}
		return new SpriteControls();
	}

	/**
	 * 查找所有【不包含】指定tag的精灵控制器
	 * 
	 * @param o
	 * @return
	 */
	public SpriteControls findSpriteNotTags(Object... o) {
		if (_currentSprites != null) {
			return _currentSprites.findNotTagsToSpriteControls(o);
		}
		return new SpriteControls();
	}

	/**
	 * 打开一个指定web页面
	 * 
	 * @param url
	 * @return
	 */
	public Screen openURL(final String url) {
		LSystem.base().openURL(url);
		return this;
	}

	/**
	 * 截屏并保存在texture
	 * 
	 * @return
	 */
	public LTexture screenshotToTexture() {
		return screenshotToImage().onHaveToClose(true).texture();
	}

	/**
	 * 返回全局通用的Bundle对象(作用类似Android中同名类,内部为key-value键值对形式的值,用来跨screen传递数据)
	 * 
	 * @return
	 */
	public ObjectBundle getBundle() {
		if (LSystem.getProcess() != null) {
			return LSystem.getProcess().getBundle();
		}
		return new ObjectBundle();
	}

	/**
	 * 添加全局通用的Bundle对象
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public ObjectBundle addBundle(String key, Object value) {
		if (LSystem.getProcess() != null) {
			LSystem.getProcess().addBundle(key, value);
		}
		return new ObjectBundle();
	}

	/**
	 * 删除全局通用的Bundle对象
	 * 
	 * @param key
	 * @return
	 */
	public ObjectBundle removeBundle(String key) {
		if (LSystem.getProcess() != null) {
			LSystem.getProcess().removeBundle(key);
		}
		return new ObjectBundle();
	}

	/**
	 * 全局通用的Bundle中指定数据做加法
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public ObjectBundle incBundle(String key, Object value) {
		return getBundle().inc(key, value);
	}

	/**
	 * 全局通用的Bundle中指定数据做减法
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public ObjectBundle subBundle(String key, Object value) {
		return getBundle().sub(key, value);
	}

	/**
	 * 全局通用的Bundle中指定数据做乘法
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public ObjectBundle mulBundle(String key, Object value) {
		return getBundle().mul(key, value);
	}

	/**
	 * 全局通用的Bundle中指定数据做除法
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public ObjectBundle divBundle(String key, Object value) {
		return getBundle().div(key, value);
	}

	/**
	 * 获得一个针对全局通用的Bundle中指定数据的四则运算器
	 * 
	 * @param key
	 * @return
	 */
	public Calculator calcBundle(String key) {
		return getBundle().calc(key);
	}

	/**
	 * 截屏screen并转化为base64字符串
	 * 
	 * @return
	 */
	public String screenshotToBase64() {
		Image tmp = screenshotToImage();
		String base64 = tmp.getBase64();
		tmp.close();
		tmp = null;
		return base64;
	}

	/**
	 * 截屏screen并保存在image(image存在系统依赖,为系统本地image类组件的封装,所以效果可能存在差异)
	 * 
	 * @return
	 */
	public Image screenshotToImage() {
		Image tmp = GLUtils.getScreenshot();
		Image image = Image.getResize(tmp, getWidth(), getHeight());
		tmp.close();
		tmp = null;
		return image;
	}

	/**
	 * 截屏screen并保存在pixmap(pixmap本质上是一个无系统依赖的，仅存在于内存中的像素数组)
	 * 
	 * @return
	 */
	public Pixmap screenshotToPixmap() {
		Pixmap pixmap = GLUtils.getFrameBufferRGBPixmap();
		Pixmap image = Pixmap.getResize(pixmap, getWidth(), getHeight());
		pixmap.close();
		pixmap = null;
		return image;
	}

	/**
	 * 退出游戏
	 * 
	 * @return
	 */
	public Screen exitGame() {
		LSystem.exit();
		return this;
	}

	/**
	 * 返回video的缓存结果(不设置out对象时才会有效)
	 * 
	 * @return
	 */
	public ArrayByte getVideoCache() {
		if (LSystem.base() != null && LSystem.base().display() != null) {
			return LSystem.base().display().getVideoCache();
		}
		return new ArrayByte();
	}

	/**
	 * 开始录像(默认使用ArrayByte缓存录像结果到内存中)
	 * 
	 * @return
	 */
	public Screen startVideo() {
		if (LSystem.base() != null && LSystem.base().display() != null) {
			LSystem.base().display().startVideo();
		}
		return this;
	}

	/**
	 * 开始录像(指定一个OutputStream对象,比如FileOutputStream 输出录像结果到指定硬盘位置)
	 * 
	 * @param output
	 * @return
	 */
	public Screen startVideo(java.io.OutputStream output) {
		if (LSystem.base() != null && LSystem.base().display() != null) {
			LSystem.base().display().startVideo(output);
		}
		return this;
	}

	/**
	 * 开始录像(指定一个OutputStream对象,比如FileOutputStream 输出录像结果到指定硬盘位置)
	 * 
	 * @param output
	 * @param delay
	 * @return
	 */
	public Screen startVideo(java.io.OutputStream output, long delay) {
		if (LSystem.base() != null && LSystem.base().display() != null) {
			LSystem.base().display().startVideo(output, delay);
		}
		return this;
	}

	/**
	 * 结束录像
	 * 
	 * @return
	 */
	public Screen stopVideo() {
		if (LSystem.base() != null && LSystem.base().display() != null) {
			LSystem.base().display().startVideo();
		}
		return this;
	}

	/**
	 * 当前Screen名称
	 * 
	 * @return
	 */
	public String getScreenName() {
		return _screenName;
	}

	/**
	 * 全局延迟缓动动画指定时间
	 * 
	 * @param delay
	 */
	public void setTweenDelay(long delay) {
		ActionControl.setDelay(delay);
	}

	public Resolution getOriginResolution() {
		if (LSystem.getProcess() != null) {
			return LSystem.getProcess().getOriginResolution();
		}
		return new Resolution(LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public Resolution getDisplayResolution() {
		if (LSystem.getProcess() != null) {
			return LSystem.getProcess().getDisplayResolution();
		}
		return new Resolution(LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public String getOriginResolutionMode() {
		return getOriginResolution().matchMode();
	}

	public String getDisplayResolutionMode() {
		return getDisplayResolution().matchMode();
	}

	/**
	 * 调用Loon虚拟的Yield实现
	 * 
	 * @param es
	 * @return
	 */
	public Coroutine call(YieldExecute... es) {
		return _coroutineProcess.call(es);
	}

	public Coroutine startCoroutine(Yielderable y) {
		return _coroutineProcess.startCoroutine(y);
	}

	public Screen clearCoroutine() {
		_coroutineProcess.clearCoroutine();
		return this;
	}

	/**
	 * 判断当前Touch行为与上次是否在屏幕中指定间距内存在移动
	 * 
	 * @param distance
	 * @return
	 */
	public boolean isTouchMoved(float distance) {
		return CollisionHelper.isMoved(distance, getLastTouchX(), getLastTouchY(), getTouchX(), getTouchY());
	}

	/**
	 * 判断当前Touch行为与上次是否存在一定程度的移动(默认间距2个像素判定移动)
	 * 
	 * @return
	 */
	public boolean isTouchMoved() {
		return isTouchMoved(2f);
	}

	/**
	 * 返回当前Touch行为的移动方向(返回int为map组件包中 @see Config 中设置的整型方向参数)
	 * 
	 * @param distance
	 * @return
	 */
	public int getTouchDirection(float distance) {
		if (isTouchMoved(distance)) {
			return Field2D.getDirection(Vector2f.at(getLastTouchX(), getLastTouchY()),
					Vector2f.at(getTouchX(), getTouchY()));
		}
		return Config.EMPTY;
	}

	/**
	 * 返回当前Touch行为的移动方向(返回map组件包中的Config整型方向)
	 * 
	 * @return
	 */
	public int getTouchDirection() {
		return getTouchDirection(2f);
	}

	/**
	 * 获得碰撞器实例对象(默认不启用,需要用户手动调用)
	 * 
	 * @return
	 */
	public CollisionManager getCollisionManager() {
		if (_collisionClosed || _collisionManager == null) {
			_collisionManager = new CollisionManager();
			_collisionClosed = false;
		}
		return _collisionManager;
	}

	/**
	 * 初始碰撞器检测的地图瓦片范围(也就是实际像素/瓦片大小后缩放进行碰撞),瓦片数值越小,精确度越高,但是计算时间也越长
	 * 
	 * @param tileSize
	 */
	public void initializeCollision(int tileSize) {
		getCollisionManager().initialize(tileSize);
	}

	/**
	 * 初始碰撞器检测的地图瓦片范围(也就是实际像素/瓦片大小后缩放进行碰撞),瓦片数值越小,精确度越高,但是计算时间也越长
	 * 
	 * @param tileSizeX
	 * @param tileSizeY
	 */
	public void initializeCollision(int tileSizeX, int tileSizeY) {
		getCollisionManager().initialize(tileSizeX, tileSizeY);
	}

	/**
	 * 若此项为true(默认为false),则碰撞查询涉及具体碰撞对象的碰撞关系时,只会返回与查询对象同一层(layer值,z值)的对象
	 * 
	 * @param itlayer
	 */
	public void setCollisionInTheLayer(boolean itlayer) {
		if (_collisionClosed) {
			return;
		}
		_collisionManager.setInTheLayer(itlayer);
	}

	/**
	 * 是否设定了同层(layer值,z值)限制(默认为false)
	 * 
	 * @param itlayer
	 */
	public boolean getCollisionInTheLayer() {
		if (_collisionClosed) {
			return false;
		}
		return _collisionManager.getInTheLayer();
	}

	/**
	 * 让碰撞器偏移指定坐标后产生碰撞
	 * 
	 * @param x
	 * @param y
	 */
	public void setCollisionOffsetPos(float x, float y) {
		if (_collisionClosed) {
			return;
		}
		_collisionManager.setOffsetPos(x, y);
	}

	/**
	 * 让碰撞器偏移指定坐标后产生碰撞
	 * 
	 * @param x
	 */
	public void setCollisionOffsetX(float x) {
		if (_collisionClosed) {
			return;
		}
		_collisionManager.setOffsetX(x);
	}

	/**
	 * 让碰撞器偏移指定坐标后产生碰撞
	 * 
	 * @param y
	 */
	public void setCollisionOffsetY(float y) {
		if (_collisionClosed) {
			return;
		}
		_collisionManager.setOffsetY(y);
	}

	/**
	 * 获得碰撞器当前的偏移坐标
	 * 
	 * @return
	 */
	public Vector2f getCollisionOffsetPos() {
		if (_collisionClosed) {
			return Vector2f.ZERO();
		}
		return _collisionManager.getOffsetPos();
	}

	/**
	 * 注入一个碰撞对象
	 * 
	 * @param obj
	 */
	public void putCollision(CollisionObject obj) {
		if (_collisionClosed) {
			return;
		}
		_collisionManager.addObject(obj);
	}

	/**
	 * 删除一个碰撞对象
	 * 
	 * @param obj
	 */
	public void removeCollision(CollisionObject obj) {
		if (_collisionClosed) {
			return;
		}
		_collisionManager.removeObject(obj);
	}

	/**
	 * 删除一个指定对象标记的碰撞对象
	 * 
	 * @param objFlag
	 */
	public void removeCollision(String objFlag) {
		if (_collisionClosed) {
			return;
		}
		_collisionManager.removeObject(objFlag);
	}

	/**
	 * 获得当前存在的碰撞对象总数
	 * 
	 * @return
	 */
	public int getCollisionSize() {
		if (_collisionClosed) {
			return 0;
		}
		return _collisionManager.numberActors();
	}

	/**
	 * 返回当前碰撞管理器中存在的碰撞对象集合
	 * 
	 * @return
	 */
	public TArray<CollisionObject> getCollisionObjects() {
		if (_collisionClosed) {
			return null;
		}
		return _collisionManager.getActorsList();
	}

	/**
	 * 获得所有指定对象标记的碰撞对象
	 * 
	 * @param objFlag
	 * @return
	 */
	public TArray<CollisionObject> getCollisionObjects(String objFlag) {
		if (_collisionClosed) {
			return null;
		}
		return _collisionManager.getObjects(objFlag);
	}

	/**
	 * 获得与指定坐标碰撞并且有指定对象标记的对象
	 * 
	 * @param x
	 * @param y
	 * @param objFlag
	 * @return
	 */
	public TArray<CollisionObject> getCollisionObjectsAt(float x, float y, String objFlag) {
		if (_collisionClosed) {
			return null;
		}
		return _collisionManager.getObjectsAt(x, y, objFlag);
	}

	/**
	 * 获得有指定标记并与指定对象相交的集合
	 * 
	 * @param obj
	 * @param objFlag
	 * @return
	 */
	public TArray<CollisionObject> getIntersectingObjects(CollisionObject obj, String objFlag) {
		if (_collisionClosed) {
			return null;
		}
		return _collisionManager.getIntersectingObjects(obj, objFlag);
	}

	/**
	 * 获得一个有指定标记并与指定对象相交的单独对象
	 * 
	 * @param obj
	 * @param objFlag
	 * @return
	 */
	public CollisionObject getOnlyIntersectingObject(CollisionObject obj, String objFlag) {
		if (_collisionClosed) {
			return null;
		}
		return _collisionManager.getOnlyIntersectingObject(obj, objFlag);
	}

	/**
	 * 获得在指定位置指定大小圆轴内有指定标记的对象集合
	 * 
	 * @param x
	 * @param y
	 * @param r
	 * @param objFlag
	 * @return
	 */
	public TArray<CollisionObject> getObjectsInRange(float x, float y, float r, String objFlag) {
		if (_collisionClosed) {
			return null;
		}
		return _collisionManager.getObjectsInRange(x, y, r, objFlag);
	}

	/**
	 * 获得与指定对象相邻的全部对象
	 * 
	 * @param obj
	 * @param distance
	 * @param d
	 * @param objFlag
	 * @return
	 */
	public TArray<CollisionObject> getNeighbours(CollisionObject obj, float distance, boolean d, String objFlag) {
		if (_collisionClosed) {
			return null;
		}
		if (distance < 0) {
			throw new LSysException("distance < 0");
		} else {
			return _collisionManager.getNeighbours(obj, distance, d, objFlag);
		}
	}

	/**
	 * 注销碰撞器
	 * 
	 */
	public void disposeCollision() {
		_collisionClosed = true;
		if (_collisionManager != null) {
			_collisionManager.dispose();
			_collisionManager = null;
		}
	}

	/**
	 * 构建一个三角区域,让集合中的动作元素尽可能填充这一三角区域
	 * 
	 * @param objs
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public final Screen elementsTriangle(final TArray<ActionBind> objs, float x, float y, float w, float h) {
		LayoutManager.elementsTriangle(this, objs, x, y, w, h);
		return this;
	}

	/**
	 * 构建一个三角区域,让集合中的动作元素尽可能填充这一三角区域
	 * 
	 * @param objs
	 * @param triangle
	 * @param stepRate
	 * @return
	 */
	public final Screen elementsTriangle(final TArray<ActionBind> objs, Triangle2f triangle, int stepRate) {
		LayoutManager.elementsTriangle(this, objs, triangle, stepRate);
		return this;
	}

	/**
	 * 构建一个三角区域,让集合中的动作元素尽可能填充这一三角区域
	 * 
	 * @param objs
	 * @param triangle
	 * @param stepRate
	 * @param offsetX
	 * @param offsetY
	 */
	public final Screen elementsTriangle(final TArray<ActionBind> objs, Triangle2f triangle, int stepRate,
			float offsetX, float offsetY) {
		LayoutManager.elementsTriangle(this, objs, triangle, stepRate, offsetX, offsetY);
		return this;
	}

	/**
	 * 构建一个线性区域,让集合中的动作元素延续这一线性对象按照指定的初始坐标到完结坐标线性排序
	 * 
	 * @param objs
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public final Screen elementsLine(final TArray<ActionBind> objs, float x1, float y1, float x2, float y2) {
		LayoutManager.elementsLine(this, objs, x1, y1, x2, y2);
		return this;
	}

	/**
	 * 构建一个线性区域,让集合中的动作元素延续这一线性对象按照指定的初始坐标到完结坐标线性排序
	 * 
	 * @param objs
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param offsetX
	 * @param offsetY
	 * @return
	 */
	public final Screen elementsLine(final TArray<ActionBind> objs, float x1, float y1, float x2, float y2,
			float offsetX, float offsetY) {
		LayoutManager.elementsLine(this, objs, x1, y1, x2, y2, offsetX, offsetY);
		return this;
	}

	/**
	 * 构建一个线性区域,让集合中的动作元素延续这一线性对象按照指定的初始坐标到完结坐标线性排序
	 * 
	 * @param objs
	 * @param line
	 * @param offsetX
	 * @param offsetY
	 */
	public final Screen elementsLine(final TArray<ActionBind> objs, Line line, float offsetX, float offsetY) {
		LayoutManager.elementsLine(this, objs, line, offsetX, offsetY);
		return this;
	}

	/**
	 * 以圆形环绕部署动作对象，构建一个圆形区域,让集合中的动作元素延续这一圆形对象按照指定的startAngle到endAngle范围环绕
	 * 
	 * @param objs
	 * @param x
	 * @param y
	 * @param radius
	 * @return
	 */
	public final Screen elementsCircle(final TArray<ActionBind> objs, float x, float y, float radius) {
		LayoutManager.elementsCircle(this, objs, x, y, radius);
		return this;
	}

	/**
	 * 以圆形环绕部署动作对象，构建一个圆形区域,让集合中的动作元素围绕这一圆形对象按照指定的startAngle到endAngle范围环绕
	 * 
	 * @param root
	 * @param objs
	 * @param circle
	 * @param startAngle
	 * @param endAngle
	 * @return
	 */
	public final Screen elementsCircle(final TArray<ActionBind> objs, Circle circle, float startAngle, float endAngle) {
		LayoutManager.elementsCircle(this, objs, circle, startAngle, endAngle);
		return this;
	}

	/**
	 * 以圆形环绕部署动作对象，构建一个圆形区域,让集合中的动作元素围绕这一圆形对象按照指定的startAngle到endAngle范围环绕
	 * 
	 * @param root
	 * @param objs
	 * @param circle
	 * @param startAngle
	 * @param endAngle
	 * @param offsetX
	 * @param offsetY
	 */
	public final Screen elementsCircle(final TArray<ActionBind> objs, Circle circle, float startAngle, float endAngle,
			float offsetX, float offsetY) {
		LayoutManager.elementsCircle(this, objs, circle, startAngle, endAngle, offsetX, offsetY);
		return this;
	}

	/**
	 * 把指定对象布局,在指定的RectBox范围内部署,并注入Screen
	 * 
	 * @param objs
	 * @param rectView
	 * @return
	 */
	public final Screen elements(final TArray<ActionBind> objs, BoxSize rectView) {
		LayoutManager.elements(this, objs, rectView);
		return this;
	}

	/**
	 * 把指定动作对象进行布局在指定的RectBox范围内部署,并注入Screen
	 * 
	 * @param objs
	 * @param rectView
	 * @param cellWidth
	 * @param cellHeight
	 * @return
	 */
	public final Screen elements(final TArray<ActionBind> objs, BoxSize rectView, float cellWidth, float cellHeight) {
		LayoutManager.elements(this, objs, rectView, cellWidth, cellHeight);
		return this;
	}

	/**
	 * 把指定对象布局,在指定的RectBox范围内部署,并注入Screen
	 * 
	 * @param objs       要布局的对象集合
	 * @param rectView   显示范围
	 * @param cellWidth  单独对象的默认width(如果对象有width,并且比cellWidth大,则以对象自己的为主)
	 * @param cellHeight 单独对象的默认height(如果对象有width,并且比cellWidth大,则以对象自己的为主)
	 * @param offsetX    显示坐标偏移x轴
	 * @param offsetY    显示坐标偏移y轴
	 */
	public final Screen elements(final TArray<ActionBind> objs, BoxSize rectView, float cellWidth, float cellHeight,
			float offsetX, float offsetY) {
		LayoutManager.elements(this, objs, rectView, cellWidth, cellHeight, offsetX, offsetY);
		return this;
	}

	/**
	 * 以指定名称指定坐标指定大小开始,在Screen中生成一组LClickButton,并返回Click对象集合
	 * 
	 * @param names
	 * @param sx
	 * @param sy
	 * @param cellWidth
	 * @param cellHeight
	 * @param offsetX
	 * @param offsetY
	 * @param listener
	 * @param maxHeight
	 * @return
	 */
	public final TArray<LClickButton> elementButtons(final String[] names, int sx, int sy, int cellWidth,
			int cellHeight, int offsetX, int offsetY, ClickListener listener, int maxHeight) {
		return LayoutManager.elementButtons(this, names, sx, sy, cellWidth, cellHeight, listener, maxHeight);
	}

	/**
	 * 删除符合指定条件的精灵并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public TArray<ISprite> removeSprite(QueryEvent<ISprite> query) {
		if (_currentSprites != null) {
			return _currentSprites.remove(query);
		}
		return new TArray<ISprite>();
	}

	/**
	 * 查找符合指定条件的精灵并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public TArray<ISprite> findSprite(QueryEvent<ISprite> query) {
		if (_currentSprites != null) {
			return _currentSprites.find(query);
		}
		return new TArray<ISprite>();
	}

	/**
	 * 删除指定条件的精灵并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public TArray<ISprite> deleteSprite(QueryEvent<ISprite> query) {
		if (_currentSprites != null) {
			return _currentSprites.delete(query);
		}
		return new TArray<ISprite>();
	}

	/**
	 * 查找符合指定条件的精灵并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public TArray<ISprite> selectSprite(QueryEvent<ISprite> query) {
		if (_currentSprites != null) {
			return _currentSprites.select(query);
		}
		return new TArray<ISprite>();
	}

	/**
	 * 删除符合指定条件的组件并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public TArray<LComponent> removeComponent(QueryEvent<LComponent> query) {
		if (_currentDesktop == null) {
			return new TArray<LComponent>();
		}
		return _currentDesktop.remove(query);
	}

	/**
	 * 查找符合指定条件的组件并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public TArray<LComponent> findComponent(QueryEvent<LComponent> query) {
		if (_currentDesktop == null) {
			return new TArray<LComponent>();
		}
		return _currentDesktop.find(query);
	}

	/**
	 * 删除指定条件的组件并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public TArray<LComponent> deleteComponent(QueryEvent<LComponent> query) {
		if (_currentDesktop == null) {
			return new TArray<LComponent>();
		}
		return _currentDesktop.delete(query);
	}

	/**
	 * 查找符合指定条件的组件并返回操作的集合
	 * 
	 * @param query
	 * @return
	 */
	public TArray<LComponent> selectComponent(QueryEvent<LComponent> query) {
		if (_currentDesktop == null) {
			return new TArray<LComponent>();
		}
		return _currentDesktop.select(query);
	}

	/**
	 * 返回当前Screen的渲染监听器
	 * 
	 * @return
	 */
	public DrawListener<Screen> getDrawListener() {
		return _drawListener;
	}

	/**
	 * 设定当前Screen的渲染监听器
	 * 
	 * @param drawListener
	 */
	public Screen setDrawListener(DrawListener<Screen> drawListener) {
		this._drawListener = drawListener;
		return this;
	}

	/**
	 * 设定当前Screen的渲染监听器
	 * 
	 * @param draw
	 * @return
	 */
	public Screen drawable(DrawLoop.Drawable draw) {
		return setDrawListener(new DrawLoop<Screen>(this, draw));
	}

	/**
	 * 遍历所有注入Screen的精灵
	 * 
	 * @param callback
	 * @return
	 */
	public Screen forSpriteChildren(Callback<ISprite> callback) {
		if (this._currentSprites != null) {
			this._currentSprites.forChildren(callback);
		}
		return this;
	}

	/**
	 * 遍历所有注入Screen的组件
	 * 
	 * @param callback
	 * @return
	 */
	public Screen forComponentChildren(Callback<LComponent> callback) {
		if (this._currentDesktop != null) {
			this._currentDesktop.forChildren(callback);
		}
		return this;
	}

	/**
	 * 排序桌面和精灵组件
	 * 
	 * @return
	 */
	public Screen sort() {
		if (this._currentDesktop != null) {
			this._currentDesktop.sortDesktop();
		}
		if (this._currentSprites != null) {
			this._currentSprites.sortSprites();
		}
		return this;
	}

	/**
	 * 是否允许自动排序桌面和精灵组件
	 * 
	 * @param v
	 * @return
	 */
	public Screen setSortableChildren(boolean v) {
		if (this._currentSprites != null) {
			this._currentSprites.setSortableChildren(v);
		}
		if (this._currentDesktop != null) {
			this._currentDesktop.setSortableChildren(v);
		}
		return this;
	}

	public boolean isSpriteSortableChildren() {
		return this._currentSprites != null ? this._currentSprites.isSortableChildren() : false;
	}

	public boolean isDesktopSortableChildren() {
		return this._currentDesktop != null ? this._currentDesktop.isSortableChildren() : false;
	}

	@Override
	public float getCurrentTimer() {
		return TimeUtils.currentTime();
	}

	@Override
	public String toString() {
		StringKeyValue sbr = new StringKeyValue(getClass().getName());
		sbr.newLine().kv("Sprites", _currentSprites).newLine().kv("Desktop", _currentDesktop).newLine();
		return sbr.toString();
	}

	/**
	 * Screen挂起时调用
	 */
	public abstract void resume();

	/**
	 * Screen暂停时调用
	 */
	public abstract void pause();

	/**
	 * Screen停止时调用
	 */
	public void stop() {
	}// noop

	public ResizeListener<Screen> getResizeListener() {
		return _resizeListener;
	}

	public Screen setResizeListener(ResizeListener<Screen> listener) {
		this._resizeListener = listener;
		return this;
	}

	@Override
	public int size() {
		if (_currentSprites != null && _currentDesktop != null) {
			return _currentSprites.size() + _currentDesktop.size();
		}
		if (_currentSprites != null) {
			return _currentSprites.size();
		}
		if (_currentDesktop != null) {
			return _currentDesktop.size();
		}
		return 0;
	}

	@Override
	public void clear() {
		if (_currentSprites != null) {
			_currentSprites.clear();
		}
		if (_currentDesktop != null) {
			_currentDesktop.clear();
		}
	}

	@Override
	public boolean isEmpty() {
		if (_currentSprites != null && _currentDesktop != null) {
			return _currentSprites.isEmpty() && _currentDesktop.isEmpty();
		}
		if (_currentSprites != null) {
			return _currentSprites.isEmpty();
		}
		if (_currentDesktop != null) {
			return _currentDesktop.isEmpty();
		}
		return true;
	}

	public Viewport getViewport() {
		return _baseViewport;
	}

	public Screen setViewport(Viewport v) {
		this._isExistViewport = (v != null);
		if (this._isExistViewport) {
			this._baseViewport = v;
		}
		return this;
	}

	public void println(Object msg) {
		LSystem.info(toStr(msg));
	}

	public void println(String msg, Object... args) {
		LSystem.info(msg, args);
	}

	public void println(String msg, Throwable throwable) {
		LSystem.info(msg, throwable);
	}

	/**
	 * 释放函数内资源
	 * 
	 */
	@Override
	public abstract void close();

	/**
	 * 注销Screen
	 */
	public final void destroy() {
		synchronized (Screen.class) {
			try {
				_screenIndex = 0;
				_rotation = 0;
				_scaleX = _scaleY = _alpha = 1f;
				_baseColor = null;
				_visible = false;
				_drawListener = null;
				_touchListener = null;
				if (_rectLimits != null) {
					_rectLimits.clear();
				}
				if (_actionLimits != null) {
					_actionLimits.clear();
				}
				if (_touchAreas != null) {
					_touchAreas.clear();
				}
				_touchButtonPressed = NO_BUTTON;
				_touchButtonReleased = NO_BUTTON;
				_keyButtonPressed = NO_KEY;
				_keyButtonReleased = NO_KEY;
				_replaceLoading = false;
				if (_replaceDelay != null) {
					_replaceDelay.setDelay(10);
				}
				_currentX = _currentY = _currentFrame = 0;
				_isClose = true;
				_isTranslate = false;
				_isNext = false;
				_isGravity = false;
				_isLock = true;
				_isTimerPaused = false;
				_isProcessing = false;
				_isExistCamera = false;
				_isExistViewport = false;
				_isAllowThroughUItoScreenTouch = false;
				_desktopPenetrate = false;
				if (_currentSprites != null) {
					_curSpriteRun = false;
					_currentSprites.close();
					_currentSprites = null;
				}
				if (_currentDesktop != null) {
					_curDesktopRun = false;
					_currentDesktop.close();
					_currentDesktop = null;
				}
				if (_gravityHandler != null) {
					_gravityHandler.close();
					_gravityHandler = null;
				}
				clearTouched();
				clearFrameLoop();
				if (_screenAction != null) {
					removeAllActions(_screenAction);
				}
				_coroutineProcess.close();
				_disposes.close();
				_conns.close();
				release();
				if (_keyActions != null) {
					_keyActions.clear();
				}
				if (_currentScreenBackground != null) {
					_currentScreenBackground.close();
					_currentScreenBackground = null;
				}
				if (_closeUpdate != null) {
					_closeUpdate.action(this);
				}
				disposeCollision();
				_initLoopEvents = false;
				if (_loopEvents != null) {
					_loopEvents.clear();
				}
				_closeUpdate = null;
				_resizeListener = null;
				this._screenSwitch = null;
				this._curStageRun = false;
				LSystem.closeTemp();
			} catch (Throwable cause) {
				LSystem.error("Screen destroy() dispatch exception", cause);
			} finally {
				close();
			}
		}
	}

}
