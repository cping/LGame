
namespace loon.events
{
  public  class SysTouch
	{
		public const int TOUCH_UNKNOWN = -1;

		public const int TOUCH_DOWN = 0;

		public const int TOUCH_UP = 1;

		public const int TOUCH_MOVE = 2;

		public const int TOUCH_DRAG = 3;

		public const int LEFT = 0;

		public const int RIGHT = 1;

		public const int MIDDLE = 2;

		public const int UPPER_LEFT = 0;

		public const int UPPER_RIGHT = 1;

		public const int LOWER_LEFT = 2;

		public const int LOWER_RIGHT = 3;

		public static int X()
		{
			return SysInputFactory.finalTouch.X();
		}

		public static int Y()
		{
			return SysInputFactory.finalTouch.Y();
		}

		public static float GetX()
        {
			return SysInputFactory.finalTouch.x;
        }

		public static float GetY()
		{
			return SysInputFactory.finalTouch.y;
		}

		public static float GetDX()
		{
			return SysInputFactory.finalTouch.dx;
		}

		public static float GetDY()
		{
			return SysInputFactory.finalTouch.dy;
		}

		public static bool IsLeft()
		{
			return SysInputFactory.finalTouch.IsLeft();
		}

		public static bool IsMiddle()
		{
			return SysInputFactory.finalTouch.IsMiddle();
		}

		public static bool IsRight()
		{
			return SysInputFactory.finalTouch.IsRight();
		}

		public static bool IsDown()
		{
			return SysInputFactory.finalTouch.IsDown();
		}

		public static bool IsUp()
		{
			return SysInputFactory.finalTouch.IsUp();
		}

		public static bool IsMove()
		{
			return SysInputFactory.finalTouch.IsMove();
		}

		public static bool LowerLeft()
		{
			return SysInputFactory.finalTouch.LowerLeft();
		}

		public static bool LowerRight()
		{
			return SysInputFactory.finalTouch.LowerRight();
		}

		public static bool UpperLeft()
		{
			return SysInputFactory.finalTouch.UpperLeft();
		}

		public static bool UpperRight()
		{
			return SysInputFactory.finalTouch.UpperRight();
		}

		public static long GetDuration()
		{
			return SysInputFactory.finalTouch.duration;
		}

		public static long GetTimeDown()
		{
			return SysInputFactory.finalTouch.timeDown;
		}

		public static long GetTimeUp()
		{
			return SysInputFactory.finalTouch.timeUp;
		}

		public static bool IsDrag()
		{
			return SysInputFactory._isDraging;
		}
	}
}
