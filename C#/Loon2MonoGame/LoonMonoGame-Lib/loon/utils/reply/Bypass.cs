using loon.events;

namespace loon.utils.reply
{
   public class Bypass
{

		protected internal abstract class Runs : Updateable
		{
		    public Runs next;
			public abstract void Action(object a);
		}

	protected internal abstract class Notifier
	{
		public abstract void Notify(object listener, object a1, object a2, object a3);
	}

		public interface GoListener
		{
		}

	}
}
