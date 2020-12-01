using loon.utils;
using loon.utils.reply;
using static loon.events.Event;

namespace loon.events
{
	public class MouseMake
	{

		public class Event : XYEvent
		{

			protected internal Event(int flags, double time, float x, float y) : base(flags, time, x, y)
			{
			}
		}

		public class ButtonEvent : Event
		{

			public readonly int button;

			public bool down;

			public ButtonEvent(int flags, double time, float x, float y, int button, bool down) : base(flags, time, x, y)
			{
				this.button = button;
				this.down = down;
			}

			protected internal override string Name()
			{
				return "Button";
			}

			protected internal override void AddFields(StrBuilder builder)
			{
				base.AddFields(builder);
				builder.Append(", id=").Append(button).Append(", down=").Append(down);
			}
		}

		public abstract class ButtonSlot : Port<Event>
		{
			public override void OnEmit(Event e)
			{
				if (e is ButtonEvent eve)
				{
					OnEmit(eve);
				}
			}
			public abstract void OnEmit(ButtonEvent e);
		}

	}
}
