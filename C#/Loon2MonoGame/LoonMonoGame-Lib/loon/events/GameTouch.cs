using loon.geom;
using loon.utils;

namespace loon.events
{

	public class GameTouch
	{

		 internal Orientation _orientation;

		 internal bool _active;

		protected internal int type;

		protected internal float x, y;

		protected internal float dx, dy;

		protected internal int button;

		protected internal int pointer;

		protected internal int id;

		protected internal long timeDown;

		protected internal long timeUp;

		protected internal long duration;

		internal GameTouch()
		{
			Reset();
		}

		public GameTouch Reset()
		{
			this._orientation = Orientation.Portrait;
			this._active = true;
			this.type = -1;
			this.x = 0;
			this.y = 0;
			this.dx = dy = 0;
			this.timeDown = this.timeUp = 0;
			this.duration = 0;
			this.button = -1;
			this.pointer = -1;
			this.id = -1;
			return this;
		}

		public GameTouch(float x, float y, int pointer, int id)
		{
			this.Set(x, y, pointer, id);
		}

		public GameTouch Set(float x, float y, int pointer, int id)
		{
			this.x = x;
			this.y = y;
			this.pointer = pointer;
			this.id = id;
			return this;
		}

		internal GameTouch(GameTouch touch)
		{
			if (touch == null)
			{
				this.Reset();
			}
			this._orientation = touch._orientation;
			this._active = touch._active;
			this.type = touch.type;
			this.x = touch.x;
			this.y = touch.y;
			this.dx = touch.dx;
			this.dy = touch.dy;
			this.button = touch.button;
			this.pointer = touch.pointer;
			this.duration = touch.duration;
			this.id = touch.id;
			this.timeUp = touch.timeUp;
			this.timeDown = touch.timeDown;
		}

		public GameTouch ConvertOrientation()
		{
			return ConvertOrientation(this.x, this.y);
		}

		public GameTouch ConvertOrientation(float posX, float posY)
		{
			float tmpX;
			switch (_orientation)
			{
				case Orientation.Portrait:
					this.x = posX;
					this.y = posY;
					return this;
				case Orientation.PortraitUpsideDown:
					this.x = LSystem.viewSize.GetWidth() - this.x;
					return this;
				case Orientation.LandscapeRight:
					tmpX = this.x;
					this.x = this.y;
					this.y = tmpX;
					return this;
				case Orientation.LandscapeLeft:
					tmpX = this.x;
					this.x = this.y;
					this.y = tmpX;
					this.x = LSystem.viewSize.GetWidth() - this.x;
					this.y = LSystem.viewSize.GetHeight() - this.y;
					return this;
			}
			this.x = posX;
			this.y = posY;
			return this;
		}

		public GameTouch Offset(float posX, float posY)
		{
			this.x += posX;
			this.y += posY;
			return this;
		}

		public GameTouch OffsetX(float posX)
		{
			this.x += posX;
			return this;
		}

		public GameTouch OffsetY(float posY)
		{
			this.y += posY;
			return this;
		}

		public bool Equals(GameTouch e)
		{
			if (e == null)
			{
				return false;
			}
			if (e == this)
			{
				return true;
			}
			if (e.type == type && e.x == x && e.y == y && e.button == button && e.pointer == pointer && e.id == id)
			{
				return true;
			}
			return false;
		}

		public int GetButton()
		{
			return button;
		}

		public int GetPointer()
		{
			return pointer;
		}

		public int Type
		{
			get
			{
				return type;
			}
		}

		public int GetID()
		{
			return id;
		}

		public int X()
		{
			return (int)x;
		}

		public int Y()
		{
			return (int)y;
		}

		public float GetX()
		{
			return x;
		}

		public int GetTileX(int tileX)
		{
			return (int)(x / tileX);
		}

		public float GetY()
		{
			return y;
		}

		public int GetTileY(int tileY)
		{
			return (int)(y / tileY);
		}

		public float GetDX()
		{
			return dx;
		}

		public float GetDY()
		{
			return dy;
		}

		internal bool isDraging;

		public bool IsLeft()
		{
			return button == SysTouch.LEFT;
		}

		public bool IsMiddle()
		{
			return button == SysTouch.MIDDLE;
		}

		public bool IsRight()
		{
			return button == SysTouch.RIGHT;
		}

		public bool IsDown()
		{
			return button == SysTouch.TOUCH_DOWN;
		}

		public bool IsUp()
		{
			return button == SysTouch.TOUCH_UP;
		}

		public bool IsMove()
		{
			return button == SysTouch.TOUCH_MOVE;
		}

		public GameTouch SetState(int s)
		{
			this.button = s;
			return this;
		}

		public int GetState()
		{
			return this.button;
		}

		public bool IsDrag()
		{
			return isDraging;
		}

		public Vector2f Get()
		{
			return new Vector2f((int)x, (int)y);
		}

		public bool LowerLeft()
		{
			return type == SysTouch.LOWER_LEFT;
		}

		public bool LowerRight()
		{
			return type == SysTouch.LOWER_RIGHT;
		}

		public bool UpperLeft()
		{
			return type == SysTouch.UPPER_LEFT;
		}

		public bool UpperRight()
		{
			return type == SysTouch.UPPER_RIGHT;
		}

		public bool JustPressed()
		{
			return JustPressed(TimeUtils.Millis());
		}

		public bool JustPressed(long time)
		{
			return (this.IsDown() && (this.timeDown + duration) > time);
		}

		public bool JustReleased()
		{
			return JustReleased(TimeUtils.Millis());
		}

		public bool JustReleased(long time)
		{
			return (this.IsUp() && (this.timeUp + duration) > time);
		}

		public long GetTimeDown()
		{
			return timeDown;
		}

		public long GetTimeUp()
		{
			return timeUp;
		}

		public long GetDuration()
		{
			return duration;
		}

		public GameTouch Cpy()
		{
			return new GameTouch(this);
		}

		public bool IsActive()
		{
			return _active;
		}

		public GameTouch SetActive(bool active)
		{
			this._active = active;
			return this;
		}

		public Orientation GetOrientation()
		{
			return _orientation;
		}

		public GameTouch SetOrientation(Orientation ori)
		{
			if (ori == default)
			{
				this._orientation = Orientation.Portrait;
				return this;
			}
			this._orientation = ori;
			return this;
		}

		public override string ToString()
		{
			StringKeyValue builder = new StringKeyValue("GameTouch");
			builder.Kv("id", id).Comma()
			.Kv("point", pointer).Comma()
			.Kv("button", button).Comma()
			.Kv("timeDown", timeDown).Comma()
			.Kv("timeUp", timeUp).Comma()
			.Kv("duration", duration).Comma()
			.Kv("active", _active).Comma()
			.Kv("orientation", _orientation);
			return builder.ToString();
		}

	}

}
