using loon.utils;
using loon.utils.reply;
using static loon.events.Event;

namespace loon.events
{
   public abstract class KeyMake
{

		public enum TextType
		{
			DEFAULT,
			NUMBER,
			EMAIL,
			URL
		}

		public class Event : InputEvent
		{
			public char keyChar;

			protected internal Event(int flags, char ch, double time) : base(flags, time)
			{
				this.keyChar = ch;
			}
		}

		public class KeyEvent : Event
		{

			public readonly int keyCode;

			public readonly bool down;

			public KeyEvent(int flags, double time, char keyChar, int keyCode, bool down) : base(flags, keyChar, time)
			{
				this.keyCode = keyCode;
				this.down = down;
			}

			protected internal override string Name()
			{
				return "Key";
			}

			protected internal override void AddFields(StrBuilder builder)
			{
				base.AddFields(builder);
				builder.Append(", keyCode=").Append(keyCode).Append(", down=").Append(down);
			}
		}

		public abstract class KeyPort : Port<object>
		{
			public override void OnEmit(object e)
			{
				if (e is KeyEvent eve)
				{
					OnEmit(eve);
				}
			}

			public abstract void OnEmit(KeyEvent e);
		}

	}
}
