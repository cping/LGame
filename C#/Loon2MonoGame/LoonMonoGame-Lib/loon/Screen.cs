using java.lang;
using loon.action;
using loon.canvas;
using loon.events;
using loon.geom;
using loon.opengl;
using loon.utils;
using loon.utils.timer;

namespace loon
{
	public abstract class Screen
	{

		public enum MoveMethod
		{
			FROM_LEFT, FROM_UP, FROM_DOWN, FROM_RIGHT, FROM_UPPER_LEFT, FROM_UPPER_RIGHT, FROM_LOWER_LEFT, FROM_LOWER_RIGHT,
			OUT_LEFT, OUT_UP, OUT_DOWN, OUT_RIGHT, OUT_UPPER_LEFT, OUT_UPPER_RIGHT, OUT_LOWER_LEFT, OUT_LOWER_RIGHT
		}

		public enum PageMethod
		{
			Unknown, Accordion, BackToFore, CubeIn, Depth, Fade, Rotate, RotateDown, RotateUp, Stack, ZoomIn, ZoomOut
		}

		public interface ReplaceEvent
		{

			Screen GetScreen(int idx);

		}

		public enum DrawOrder
		{
			SPRITE, DESKTOP, USER
		}

		public const int SCREEN_NOT_REPAINT = 0;

		public const int SCREEN_TEXTURE_REPAINT = 1;

		public const int SCREEN_COLOR_REPAINT = 2;

		public const sbyte DRAW_EMPTY = -1;

		public const sbyte DRAW_USER = 0;

		public const sbyte DRAW_SPRITE = 1;

		public const sbyte DRAW_DESKTOP = 2;

		/// <summary>
		/// 通用碰撞管理器(需要用户自行初始化(getCollisionManager或initializeCollision),不实例化默认不存在)
		/// </summary>
		// private CollisionManager _collisionManager;

		private ResizeListener<Screen> _resizeListener;

		private bool _collisionClosed;

		private ArrayMap _keyActions = new ArrayMap();

		private Updateable _closeUpdate;

		private TouchedClick _touchListener;

		private string _screenName;

		//private BaseCamera _baseCamera;

		//private ScreenAction _screenAction = null;

		private TArray<LTouchArea> _touchAreas = new TArray<LTouchArea>();

		private loon.utils.reply.Closeable.Set _conns = new loon.utils.reply.Closeable.Set();

		private LTransition _transition;

		//private DrawListener<Screen> _drawListener;

		private bool spriteRun, desktopRun, stageRun;

		private bool fristPaintFlag;

		private bool secondPaintFlag;

		private bool lastPaintFlag;

		//private GravityHandler gravityHandler;

	//	private Viewport _baseViewport;

		private LColor _backgroundColor;

		private RectBox _rectBox;

		private LColor _baseColor;

		private float _alpha = 1f;

		private float _rotation = 0;

		private float _pivotX = -1f, _pivotY = -1f;

		private float _scaleX = 1f, _scaleY = 1f;

		private bool _flipX = false, _flipY = false;

		private bool _visible = true;

		private TArray<RectBox> _rect_limits = new TArray<RectBox>(10);

		private TArray<ActionBind> _action_limits = new TArray<ActionBind>(10);

		//private TArray<FrameLoopEvent> _loopEvents;

		private bool _initLoopEvents = false;

		private bool _isExistCamera = false;

		private bool _isExistViewport = false;

		//private Accelerometer.SensorDirection direction = Accelerometer_SensorDirection.EMPTY;

		private int mode, frame;

		private bool processing = true;

		private LTexture currentScreenBackground;

		protected internal LProcess handler;

		private int width, height, halfWidth, halfHeight;
		// 精灵集合
		//private Sprites sprites;

		// 桌面集合
		//private Desktop desktop;

		private readonly Disposes _disposes;

		private readonly PointF _lastTocuh = new PointF();

		private readonly PointI _touch = new PointI();

		private bool _desktopPenetrate = false;

		private bool isLoad, isLock, isClose, isTranslate, isGravity;

		private float tx, ty;

		private float lastTouchX, lastTouchY, touchDX, touchDY;

		public long elapsedTime;

		private readonly IntMap<bool> keyType = new IntMap<bool>();

		private readonly IntMap<bool> touchType = new IntMap<bool>();

		private int touchButtonPressed = SysInput_Item.NO_BUTTON, touchButtonReleased = SysInput_Item.NO_BUTTON;

		private int keyButtonPressed = SysInput_Item.NO_KEY, keyButtonReleased = SysInput_Item.NO_KEY;

		protected internal bool isNext;

		// 首先绘制的对象
		private PaintOrder fristOrder;

		// 其次绘制的对象
		private PaintOrder secondOrder;

		// 最后绘制的对象
		private PaintOrder lastOrder;

		private PaintOrder userOrder, spriteOrder, desktopOrder;

		private bool replaceLoading;

		private int replaceScreenSpeed = 8;

		private LTimer replaceDelay = new LTimer(0);

		private Screen replaceDstScreen;

		//private ScreenSwitch screenSwitch;

		//private EmptyObject dstPos = new EmptyObject();

		private MoveMethod replaceMethod = MoveMethod.FROM_LEFT;

		private bool isScreenFrom = false;

		// 每次screen处理事件循环的额外间隔时间
		private LTimer delayTimer = new LTimer(0);
		// 希望Screen中所有组件的update暂停的时间
		private LTimer pauseTimer = new LTimer(LSystem.SECOND);
		// 是否已经暂停
		private bool isTimerPaused = false;

		private int _screenIndex = 0;

		public sealed class PaintOrder
		{

			internal sbyte type;

			internal Screen screen;

			public PaintOrder(sbyte t, Screen s)
			{
				this.type = t;
				this.screen = s;
			}

			internal void Paint(GLEx g)
			{
				switch (type)
				{
					case DRAW_USER:
						/*DrawListener<Screen> drawing = screen._drawListener;
						if (drawing != null)
						{
							drawing.draw(g, screen.X, screen.Y);
						}*/
						screen.Draw(g);
						break;
					case DRAW_SPRITE:
						/*if (screen.spriteRun)
						{
							screen.sprites.createUI(g);
						}
						else if (screen.spriteRun = (screen.sprites != null && screen.sprites.size() > 0))
						{
							screen.sprites.createUI(g);
						}*/
						break;
					case DRAW_DESKTOP:
						/*if (screen.desktopRun)
						{
							screen.desktop.createUI(g);
						}
						else if (screen.desktopRun = (screen.desktop != null && screen.desktop.size() > 0))
						{
							screen.desktop.createUI(g);
						}*/
						break;
					case DRAW_EMPTY:
					default:
						break;
				}
			}

			internal void Update(LTimerContext c)
			{
				try
				{
					switch (type)
					{
						case DRAW_USER:
							/*DrawListener<Screen> drawing = screen._drawListener;
							if (drawing != null)
							{
								drawing.update(c.timeSinceLastUpdate);
							}*/
							screen.Alter(c);
							break;
						case DRAW_SPRITE:
							/*screen.spriteRun = (screen.sprites != null && screen.sprites.size() > 0);
							if (screen.spriteRun)
							{
								screen.sprites.update(c.timeSinceLastUpdate);
							}*/
							break;
						case DRAW_DESKTOP:
						/*	screen.desktopRun = (screen.desktop != null && screen.desktop.size() > 0);
							if (screen.desktopRun)
							{
								screen.desktop.update(c.timeSinceLastUpdate);
							}*/
							break;
						case DRAW_EMPTY:
						default:
							break;
					}
				}
				catch (System.Exception cause)
				{
					LSystem.Error("Screen update() dispatch failure", cause);
				}
			}
		}

		public abstract void Alter(LTimerContext context);


		public int GetScreenWidth()
		{
			return (int)(width * this._scaleX);
		}

		public int GetScreenHeight()
		{
			return (int)(height * this._scaleY);
		}

	public int GetWidth()
		{
			return width;
		}

	public int GetHeight()
		{
			return height;
		}

		public string GetName()
        {
			 return this.GetType().Name ;
        }

		public string GetScreenName()
        {
			return null;
        }

		public virtual void OnCreate(int width, int height)
        {
			this.mode = SCREEN_NOT_REPAINT;
			this.stageRun = true;
			this.width = width;
			this.height = height;
			this.halfWidth = width / 2;
			this.halfHeight = height / 2;
			this.lastTouchX = lastTouchY = touchDX = touchDY = 0;
			this.isLoad = isLock = isClose = isTranslate = isGravity = false;
			/*if (sprites != null)
			{
				sprites.close();
				sprites.removeAll();
				sprites = null;
			}
			this.sprites = new Sprites("ScreenSprites", this, width, height);
			if (desktop != null)
			{
				desktop.close();
				desktop.clear();
				desktop = null;
			}
			this.desktop = new Desktop("ScreenDesktop", this, width, height);*/
			this.isNext = true;
			this.tx = ty = 0;
			this.isTranslate = false;
			this._screenIndex = 0;
			this._lastTocuh.Empty();
			this._keyActions.Clear();
			this._visible = true;
			this._rotation = 0;
			this._scaleX = _scaleY = _alpha = 1f;
			this._baseColor = null;
			this._isExistCamera = false;
			this._initLoopEvents = false;
			this._desktopPenetrate = false;
			if (this.delayTimer == null)
			{
				this.delayTimer = new LTimer(0);
			}
			if (this.pauseTimer == null)
			{
				this.pauseTimer = new LTimer(LSystem.SECOND);
			}
			if (this._rect_limits == null)
			{
				this._rect_limits = new TArray<RectBox>(10);
			}
			if (this._action_limits == null)
			{
				this._action_limits = new TArray<ActionBind>(10);
			}
			if (this._touchAreas == null)
			{
				this._touchAreas = new TArray<LTouchArea>();
			}
			if (this._conns == null)
			{
				this._conns = new loon.utils.reply.Closeable.Set();
			}
			if (this._keyActions == null)
			{
				this._keyActions = new ArrayMap();
			}
		}

		public bool IsOnLoadComplete()
        {
			return true;
        }
		public float GetRotation()
        {
			return 0;
        }

		public float GetScaleX()
		{
			return 1f;
		}

		public float GetScaleY()
		{
			return 1f;
		}

		public virtual bool IsTxUpdate()
        {
			return false;
        }
		public bool IsFlipX()
		{
			return false;
		}

		public bool IsFlipY()
		{
			return false;
		}

		public abstract void Draw(GLEx g);

		public abstract void Resize(int width, int height);

		public int GetTouchPressed()
		{
			return touchButtonPressed > SysInput_Item.NO_BUTTON ? touchButtonPressed : SysInput_Item.NO_BUTTON;
		}

		public int GetTouchReleased()
		{
			return touchButtonReleased > SysInput_Item.NO_BUTTON ? touchButtonReleased : SysInput_Item.NO_BUTTON;
		}

		public bool IsTouchPressed(int button)
		{
			return touchButtonPressed == button;
		}

		public bool IsTouchReleased(int button)
		{
			return touchButtonReleased == button;
		}


		public int GetKeyPressed()
		{
			return keyButtonPressed > SysInput_Item.NO_KEY ? keyButtonPressed : SysInput_Item.NO_KEY;
		}


		public bool IsKeyPressed(int keyCode)
		{
			return keyButtonPressed == keyCode;
		}

		public int GetKeyReleased()
		{
			return keyButtonReleased > SysInput_Item.NO_KEY ? keyButtonReleased : SysInput_Item.NO_KEY;
		}


		public bool IsKeyReleased(int keyCode)
		{
			return keyButtonReleased == keyCode;
		}


		public virtual void KeyPressed(GameKey e)
		{

		}

		public virtual void KeyReleased(GameKey e)
		{

		}


		public virtual void KeyTyped(GameKey e)
		{

		}


		public virtual void MousePressed(GameTouch e)
		{
			/*if (isLock || isClose || !isLoad)
			{
				return;
			}*/
			if (isTranslate)
			{
				e.Offset(tx, ty);
			}
			int type = e.Type;
			int button = e.GetButton();

			try
			{
				touchType.Put(type, true);
				touchButtonPressed = button;
				touchButtonReleased = SysInput_Item.NO_BUTTON;
				TouchDown(e);
				/*if (!isClickLimit(e))
				{
					updateTouchArea(LTouchArea_Event.DOWN, e.X, e.Y);
					touchDown(e);
					if (_touchListener != null && desktop != null)
					{
						_touchListener.DownClick(desktop.SelectedComponent, e.X, e.Y);
					}
				}*/
				_lastTocuh.Set(e.X(), e.Y());
			}
			catch (System.Exception ex)
			{
				touchButtonPressed = SysInput_Item.NO_BUTTON;
				touchButtonReleased = SysInput_Item.NO_BUTTON;
				//error("Screen mousePressed() exception", ex);
			}
		}

		public abstract void TouchDown(GameTouch e);

		public virtual void MouseReleased(GameTouch e)
		{
			/*if (isLock || isClose || !isLoad)
			{
				return;
			}*/
			if (isTranslate)
			{
				e.Offset(tx, ty);
			}
			int type = e.Type;
			int button = e.GetButton();

			try
			{
				touchType.Put(type, false);
				touchButtonReleased = button;
				touchButtonPressed = SysInput_Item.NO_BUTTON;
				TouchUp(e);
				/*if (!isClickLimit(e))
				{
					updateTouchArea(LTouchArea_Event.UP, e.X, e.Y);
					touchUp(e);
					if (_touchListener != null && desktop != null)
					{
						_touchListener.UpClick(desktop.SelectedComponent, e.X, e.Y);
					}
				}*/
				_lastTocuh.Set(e.X(), e.Y());
			}
			catch (System.Exception ex)
			{
				touchButtonPressed = SysInput_Item.NO_BUTTON;
				touchButtonReleased = SysInput_Item.NO_BUTTON;
				//Error("Screen mouseReleased() exception", ex);
			}
		}

		public abstract void TouchUp(GameTouch e);

		public virtual void MouseMoved(GameTouch e)
		{
			/*if (isLock || isClose || !isLoad)
			{
				return;
			}*/
			if (isTranslate)
			{
				e.Offset(tx, ty);
			}
			TouchMove(e);
			/*if (!isClickLimit(e))
			{
				updateTouchArea(LTouchArea_Event.MOVE, e.X, e.Y);
				touchMove(e);
			}*/
		}

		public abstract void TouchMove(GameTouch e);

		public virtual void MouseDragged(GameTouch e)
		{
			/*if (isLock || isClose || !isLoad)
			{
				return;
			}*/
			if (isTranslate)
			{
				e.Offset(tx, ty);
			}
			TouchDrag(e);
			/*if (!isClickLimit(e))
			{
				updateTouchArea(LTouchArea_Event.DRAG, e.X, e.Y);
				touchDrag(e);
				if (_touchListener != null && desktop != null)
				{
					_touchListener.DragClick(desktop.SelectedComponent, e.X, e.Y);
				}
			}*/
			_lastTocuh.Set(e.X(), e.Y());
		}

		public abstract void TouchDrag(GameTouch e);

		public virtual void Resume()
		{

		}
		public virtual void Pause()
		{

		}

		public virtual void Destroy()
        {

        }
	}
}
