namespace loon.events
{
    public class SysInputFactory
{
		private float _offsetTouchX, _offsetMoveX, _offsetTouchY, _offsetMoveY;

		private static OnscreenKeyboard defkeyboard = new DefaultOnscreenKeyboard();

		public interface OnscreenKeyboard
		{
			void show(bool visible);
		}

		public class DefaultOnscreenKeyboard : OnscreenKeyboard
		{
			public virtual void show(bool visible)
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
			//update();
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
			//this.update();
		}

		protected internal virtual void Update()
		{
			/*LProcess process = LSystem.GetProcess();
			if (process != null)
			{
				this._halfWidth = process.GetWidth() / 2;
				this._halfHeight = process.GetHeight() / 2;
			}
			else if (LSystem.viewSize != null)
			{
				this._halfWidth = LSystem.viewSize.Width / 2;
				this._halfHeight = LSystem.viewSize.Height / 2;
			}*/
		}

		public virtual void callKey(KeyMake.KeyEvent e)
		{
			/*LProcess process = LSystem.Process;
			if (process != null)
			{
				if (e.down)
				{
					finalKey.timer = e.time;
					finalKey.keyChar = e.keyChar;
					finalKey.keyCode = e.keyCode;
					finalKey.type = SysKey.DOWN;
					SysKey.only_key.press();
					SysKey.addKey(finalKey.keyCode);
					process.keyDown(finalKey);
				}
				else
				{
					finalKey.timer = e.time;
					// finalKey.keyChar = e.keyChar;
					// finalKey.keyCode = e.keyCode;
					finalKey.type = SysKey.UP;
					SysKey.removeKey(finalKey.keyCode);
					process.keyUp(finalKey);
				}
			}*/
		}

		private int buttons;

		private EmulatorButtons ebuttons;

		
		

	
}
}
