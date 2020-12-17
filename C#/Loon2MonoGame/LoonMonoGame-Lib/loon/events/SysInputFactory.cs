using loon.geom;
using loon.utils;

namespace loon.events
{
    public class SysInputFactory
{
		private float _offsetTouchX, _offsetMoveX, _offsetTouchY, _offsetMoveY;

		private static OnscreenKeyboard defkeyboard = new DefaultOnscreenKeyboard();

		public interface OnscreenKeyboard
		{
			void Show(bool visible);
		}

		public class DefaultOnscreenKeyboard : OnscreenKeyboard
		{
			public virtual void Show(bool visible)
			{
				// todo
			}
		}

		internal static readonly GameTouch finalTouch = new GameTouch();

		internal static readonly GameKey finalKey = new GameKey();

		private int _halfWidth, _halfHeight;

		internal static bool _isDraging;

		private static bool useTouchCollection = false;

		public static void StartTouchCollection()
		{
			useTouchCollection = true;
		}

		public static void StopTouchCollection()
		{
			useTouchCollection = false;
		}

		private static LTouchCollection touchCollection = new LTouchCollection();

		public static LTouchCollection GetTouchState()
		{
				LTouchCollection result = new LTouchCollection(touchCollection);
				touchCollection.Update();
				return result;
		}

		public virtual void Reset()
		{
			_offsetTouchX = 0;
			_offsetTouchY = 0;
			_offsetMoveX = 0;
			_offsetMoveY = 0;
			_isDraging = false;
			finalTouch.Reset();
			finalKey.Reset();
			touchCollection.Clear();
			Update();
		}

		protected internal virtual void Update()
		{
			LProcess process = LSystem.GetProcess();
			if (process != null)
			{
				this._halfWidth = process.GetWidth() / 2;
				this._halfHeight = process.GetHeight() / 2;
			}
			else if (LSystem.viewSize != null)
			{
				this._halfWidth = LSystem.viewSize.GetWidth() / 2;
				this._halfHeight = LSystem.viewSize.GetHeight() / 2;
			}
		}


		public static void ResetTouch()
		{
			touchCollection.Clear();
		}

		public virtual void ResetSysTouch()
		{
			if (finalTouch.button == SysTouch.TOUCH_UP)
			{
				finalTouch.id = -1;
				finalTouch.button = -1;
			}
		}

		public SysInputFactory()
		{
			this.Update();
		}

		public virtual void CallKey(KeyMake.KeyEvent e)
		{
			LProcess process = LSystem.GetProcess();
			if (process != null)
			{
				if (e.down)
				{
					finalKey.timer = e.time;
					finalKey.keyChar = e.keyChar;
					finalKey.keyCode = e.keyCode;
					finalKey.type = SysKey.DOWN;
					SysKey.only_key.Press();
					SysKey.AddKey(finalKey.keyCode);
					//process.KeyDown(finalKey);
				}
				else
				{
					finalKey.timer = e.time;
					//finalKey.keyChar = e.keyChar;
					//finalKey.keyCode = e.keyCode;
					finalKey.type = SysKey.UP;
					SysKey.RemoveKey(finalKey.keyCode);
					//process.keyUp(finalKey);
				}
			}
		}

		private int buttons;

		private EmulatorButtons ebuttons;

		public virtual void CallMouse(MouseMake.ButtonEvent e)
		{

			if (LSystem.IsLockAllTouchEvent())
			{
				return;
			}
			LProcess process = LSystem.GetProcess();
			if (process == null)
			{
				return;
			}
			bool stopMoveDrag = LSystem.IsNotAllowDragAndMove();
		
			Vector2f pos = process.ConvertXY(e.x, e.y);

			float touchX = pos.x;
		
			float touchY = pos.y;

			int button = e.button;
			finalTouch.isDraging = _isDraging;
			finalTouch.x = touchX;
			finalTouch.y = touchY;
			finalTouch.button = e.button;
			finalTouch.pointer = 0;
			finalTouch.id = 0;
			ebuttons = process.GetEmulatorButtons();
			if (button == -1)
			{
				if (buttons > 0)
				{
					finalTouch.type = SysTouch.TOUCH_DRAG;
				}
				else
				{
					finalTouch.type = SysTouch.TOUCH_MOVE;
				}
			}
			else
			{
				if (e.down)
				{
					finalTouch.type = SysTouch.TOUCH_DOWN;
				}
				else
				{
					if (finalTouch.type == SysTouch.TOUCH_DOWN || finalTouch.type == SysTouch.TOUCH_DRAG)
					{
						finalTouch.type = SysTouch.TOUCH_UP;
					}
				}
			}

			switch (finalTouch.type)
			{
				case SysTouch.TOUCH_DOWN:
					finalTouch.button = SysTouch.TOUCH_DOWN;
					finalTouch.duration = 0;
					finalTouch.timeDown = TimeUtils.Millis();
					if (useTouchCollection)
					{
						touchCollection.Add(finalTouch.id, finalTouch.x, finalTouch.y);
					}
					process.MousePressed(finalTouch);
					buttons++;
					_isDraging = false;
					/*if (ebuttons != null && ebuttons.Visible)
					{
						ebuttons.hit(0, touchX, touchY, false);
					}*/
					break;
				case SysTouch.TOUCH_UP:
					finalTouch.button = SysTouch.TOUCH_UP;
					finalTouch.timeUp = TimeUtils.Millis();
					finalTouch.duration = finalTouch.timeUp - finalTouch.timeDown;
					if (useTouchCollection)
					{
						touchCollection.Update(finalTouch.id, LTouchLocationState.Released, finalTouch.x, finalTouch.y);
					}
					process.MouseReleased(finalTouch);
					buttons = 0;
					_isDraging = false;
					/*if (ebuttons != null && ebuttons.Visible)
					{
						ebuttons.unhit(0, touchX, touchY);
					}*/
					break;
				case SysTouch.TOUCH_MOVE:
					_offsetMoveX = touchX;
					_offsetMoveY = touchY;
					finalTouch.dx = _offsetTouchX - _offsetMoveX;
					finalTouch.dy = _offsetTouchY - _offsetMoveY;
					finalTouch.button = SysTouch.TOUCH_MOVE;
					finalTouch.duration = TimeUtils.Millis() - finalTouch.timeDown;
					if (!_isDraging)
					{
						if (useTouchCollection)
						{
							touchCollection.Update(finalTouch.id, LTouchLocationState.Dragged, finalTouch.x, finalTouch.y);
						}
						if (!stopMoveDrag)
						{
							process.MouseMoved(finalTouch);
						}
					}
					/*if (ebuttons != null && ebuttons.Visible)
					{
						ebuttons.unhit(0, touchX, touchY);
					}*/
					break;
				case SysTouch.TOUCH_DRAG:
					_offsetMoveX = touchX;
					_offsetMoveY = touchY;
					finalTouch.dx = _offsetTouchX - _offsetMoveX;
					finalTouch.dy = _offsetTouchY - _offsetMoveY;
					finalTouch.button = SysTouch.TOUCH_DRAG;
					finalTouch.duration = TimeUtils.Millis() - finalTouch.timeDown;
					ebuttons = process.GetEmulatorButtons();
					/*if (ebuttons != null && ebuttons.Visible)
					{
						ebuttons.hit(0, touchX, touchY, true);
					}*/
					if (useTouchCollection)
					{
						touchCollection.Update(finalTouch.id, LTouchLocationState.Dragged, finalTouch.x, finalTouch.y);
					}
					if (!stopMoveDrag)
					{
						process.MouseDragged(finalTouch);
					}
					/*if (ebuttons != null && ebuttons.Visible)
					{
						ebuttons.hit(0, touchX, touchY, false);
					}*/
					_isDraging = true;
					break;
				default:
					finalTouch.duration = 0;
					if (useTouchCollection)
					{
						touchCollection.Update(finalTouch.id, LTouchLocationState.Invalid, finalTouch.x, finalTouch.y);
					}
					/*if (ebuttons != null && ebuttons.Visible)
					{
						ebuttons.release();
					}*/
					break;
			}
		}
		public virtual void CallTouch(TouchMake.Event[] events)
		{

			if (LSystem.IsLockAllTouchEvent())
			{
				return;
			}
			LProcess process = LSystem.GetProcess();
			if (process == null)
			{
				return;
			}

			bool stopMoveDrag = LSystem.IsNotAllowDragAndMove();

			int size = events.Length;

			ebuttons = process.GetEmulatorButtons();

			for (int i = 0; i < size; i++)
			{
				TouchMake.Event e = events[i];
				Vector2f pos = process.ConvertXY(e.x, e.y);
				float touchX = pos.x;
				float touchY = pos.y;

				finalTouch.isDraging = _isDraging;
				finalTouch.x = touchX;
				finalTouch.y = touchY;
				finalTouch.pointer = i;
				finalTouch.id = e.id;

				switch (e.kind.innerEnumValue)
				{
					case loon.events.TouchMake.Event.Kind.InnerEnum.START:
						if (useTouchCollection)
						{
							touchCollection.Add(finalTouch.id, finalTouch.x, finalTouch.y);
						}
						_offsetTouchX = touchX;
						_offsetTouchY = touchY;
						if ((touchX < _halfWidth) && (touchY < _halfHeight))
						{
							finalTouch.type = SysTouch.UPPER_LEFT;
						}
						else if ((touchX >= _halfWidth) && (touchY < _halfHeight))
						{
							finalTouch.type = SysTouch.UPPER_RIGHT;
						}
						else if ((touchX < _halfWidth) && (touchY >= _halfHeight))
						{
							finalTouch.type = SysTouch.LOWER_LEFT;
						}
						else
						{
							finalTouch.type = SysTouch.LOWER_RIGHT;
						}
						finalTouch.duration = 0;
						finalTouch.button = SysTouch.TOUCH_DOWN;
						finalTouch.timeDown = TimeUtils.Millis();
						process.MousePressed(finalTouch);
						_isDraging = false;
						/*if (ebuttons != null && ebuttons.Visible)
						{
							ebuttons.hit(i, touchX, touchY, false);
						}*/
						break;
					case loon.events.TouchMake.Event.Kind.InnerEnum.MOVE:
						_offsetMoveX = touchX;
						_offsetMoveY = touchY;
						finalTouch.dx = _offsetTouchX - _offsetMoveX;
						finalTouch.dy = _offsetTouchY - _offsetMoveY;
						finalTouch.duration = TimeUtils.Millis() - finalTouch.timeDown;
						if (MathUtils.Abs(finalTouch.dx) > 0.1f || MathUtils.Abs(finalTouch.dy) > 0.1f)
						{
							if (useTouchCollection)
							{
								touchCollection.Update(finalTouch.id, LTouchLocationState.Dragged, finalTouch.x, finalTouch.y);
							}
							if (!stopMoveDrag)
							{
								process.MouseMoved(finalTouch);
							}
							if (!stopMoveDrag)
							{
								process.MouseDragged(finalTouch);
							}
							_isDraging = true;
						}
						ebuttons = process.GetEmulatorButtons();
						/*if (ebuttons != null && ebuttons.Visible)
						{
							ebuttons.hit(i, touchX, touchY, false);
						}*/
						break;
					case TouchMake.Event.Kind.InnerEnum.END:
						if (useTouchCollection)
						{
							touchCollection.Update(finalTouch.id, LTouchLocationState.Released, finalTouch.x, finalTouch.y);
						}
						if (finalTouch.button == SysTouch.TOUCH_DOWN || finalTouch.button == SysTouch.TOUCH_MOVE)
						{
							finalTouch.button = SysTouch.TOUCH_UP;
						}
						finalTouch.timeUp = TimeUtils.Millis();
						finalTouch.duration = finalTouch.timeUp - finalTouch.timeDown;
						process.MouseReleased(finalTouch);
						_isDraging = false;
						/*if (ebuttons != null && ebuttons.Visible)
						{
							ebuttons.unhit(i, touchX, touchY);
						}*/
						break;
					case TouchMake.Event.Kind.InnerEnum.CANCEL:
					default:
						if (finalTouch.button == SysTouch.TOUCH_DOWN || finalTouch.button == SysTouch.TOUCH_MOVE)
						{
							finalTouch.button = SysTouch.TOUCH_UP;
						}
						finalTouch.duration = 0;
						if (useTouchCollection)
						{
							touchCollection.Update(finalTouch.id, LTouchLocationState.Invalid, finalTouch.x, finalTouch.y);
						}
						/*if (ebuttons != null && ebuttons.Visible)
						{
							ebuttons.release();
						}*/
						break;
				}

			}
		}



	}
}
