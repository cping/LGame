using loon.utils.reply;
using static loon.events.TouchMake.Event;

namespace loon.events
{
	public abstract class InputMake
	{
		public LObject<object> tag;

		public bool mouseEnabled = true;

		public bool touchEnabled = true;

		public bool keyboardEnabled = true;

		public Act<object> mouseEvents = Act<object>.Create<object>();

		public Act<object> touchEvents = Act<object>.Create<object>();

		public Act<object> keyboardEvents = Act<object>.Create<object>();

		public virtual bool HasMouse()
		{
			return false;
		}

		public virtual bool HasTouch()
		{
			return false;
		}

		public virtual bool HasHardwareKeyboard()
		{
			return false;
		}

		public virtual bool HsMouseLock()
		{
			return false;
		}

		protected internal virtual int ModifierFlags(bool altP, bool ctrlP, bool metaP, bool shiftP)
		{
			return Event.InputEvent.ModifierFlags(altP, ctrlP, metaP, shiftP);
		}

		protected internal virtual void EmitKeyPress(double time, int keyCode, char keyChar, bool down, int flags)
		{
			KeyMake.KeyEvent e = new KeyMake.KeyEvent(0, time, keyChar, keyCode, down);
			e.SetFlag(flags);
			keyboardEvents.Emit(e);
		}

		protected internal virtual void EmitMouseButton(double time, float x, float y, int btnid, bool down, int flags)
		{
			MouseMake.ButtonEvent e = new MouseMake.ButtonEvent(0, time, x, y, btnid, down);
			e.SetFlag(flags);
			mouseEvents.Emit(e);
		}

		public abstract void Callback<T1>(LObject<T1> o);


	}
}
