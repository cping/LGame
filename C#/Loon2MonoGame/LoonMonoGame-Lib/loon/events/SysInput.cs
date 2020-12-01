using loon.geom;

namespace loon.events
{
	public interface SysInput
	{

		void SetKeyDown(int code);

		void SetKeyUp(int code);

		bool IsMoving();

		int GetRepaintMode();

		void SetRepaintMode(int mode);

		PointI GetTouch();

		int GetWidth();

		int GetHeight();

		void Refresh();

		int GetTouchX();

		int GetTouchY();

		int GetTouchDX();

		int GetTouchDY();

		int GetTouchReleased();

		bool IsTouchReleased(int i);

		int GetTouchPressed();

		bool IsTouchPressed(int i);

		bool IsTouchType(int i);

		int GetKeyReleased();

		bool IsKeyReleased(int i);

		int GetKeyPressed();

		bool IsKeyPressed(int i);

		bool IsKeyType(int i);

	}

	public static class SysInput_Item
	{
		public const int NO_BUTTON = -1;
		public const int NO_KEY = -1;
		public const int UPPER_LEFT = 0;
		public const int UPPER_RIGHT = 1;
		public const int LOWER_LEFT = 2;
		public const int LOWER_RIGHT = 3;
	}

	public interface SysInput_TextEvent
	{

		void Input(string text);

		void Cancel();

	}

	public interface SysInput_SelectEvent
	{

		void Item(int index);

		void Cancel();

	}

	public interface SysInput_ClickEvent
	{

		void Clicked();

		void Cancel();

	}
}
