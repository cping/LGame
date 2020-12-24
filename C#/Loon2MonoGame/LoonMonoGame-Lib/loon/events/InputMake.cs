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

		public Act<MouseMake.Event> mouseEvents = Act<MouseMake.Event>.Create<MouseMake.Event>();

		public Act<TouchMake.Event[]> touchEvents = Act<TouchMake.Event[]>.Create<TouchMake.Event[]>();

		public Act<KeyMake.Event> keyboardEvents = Act<KeyMake.Event>.Create<KeyMake.Event>();

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
