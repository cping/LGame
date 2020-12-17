
using loon.events;

namespace loon
{

	public class ActionCounter : LimitedCounter
	{

		private Updateable actListener;

		public ActionCounter(int limit, Updateable actListener) : base(limit)
		{
		}

		public ActionCounter(int limit) : base(limit)
		{
		}


		public virtual ActionCounter SetActionListener(Updateable u)
		{
			this.actListener = u;
			return this;
		}

		public virtual Updateable GetActionListener()
		{
			return this.actListener;
		}
		public override int Increment(int v)
		{
			bool isLimitReachedBefore = IsLimitReached();
			int result = base.Increment(v);
			if (actListener != null && IsLimitReached() && !isLimitReachedBefore)
			{
				actListener.Action(this);
			}
			return result;
		}

		public override int Increment()
		{
			bool isLimitReachedBefore = IsLimitReached();
			int result = base.Increment();
			if (actListener != null && IsLimitReached() && !isLimitReachedBefore)
			{
				actListener.Action(this);
			}
			return result;
		}

		public override int Reduction(int v)
		{
			bool isLimitReachedBefore = IsLimitReached();
			int result = base.Reduction(v);
			if (actListener != null && IsLimitReached() && !isLimitReachedBefore)
			{
				actListener.Action(this);
			}
			return result;
		}

		public override int Reduction()
		{
			bool isLimitReachedBefore = IsLimitReached();
			int result = base.Reduction();
			if (actListener != null && IsLimitReached() && !isLimitReachedBefore)
			{
				actListener.Action(this);
			}
			return result;
		}
	}
}
