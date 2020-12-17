using loon.events;
using loon.opengl;
using loon.utils;

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

		private readonly IntMap<bool> keyType = new IntMap<bool>();

		private readonly IntMap<bool> touchType = new IntMap<bool>();

		private int touchButtonPressed = SysInput_Item.NO_BUTTON, touchButtonReleased = SysInput_Item.NO_BUTTON;

		private int keyButtonPressed = SysInput_Item.NO_KEY, keyButtonReleased = SysInput_Item.NO_KEY;

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

		}

		public virtual void MouseReleased(GameTouch e)
		{

		}


		public virtual void MouseMoved(GameTouch e)
		{

		}


		public virtual void MouseDragged(GameTouch e)
		{

		}
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
