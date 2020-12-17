namespace loon
{
	public class LimitedCounter : Counter
	{

		private readonly int _limit;

		public LimitedCounter(int limit) : this(limit, 0)
		{
		}

		public LimitedCounter(int limit, int v) : this(limit, v, -1, -1)
		{
		}

		public LimitedCounter(int limit, int v, int min, int max) : base(v, min, max)
		{
			this._limit = limit;
		}

		public virtual int GetLimit()
		{
				return _limit;
		}

		public override int Increment(int val)
		{
			if (!IsLimitReached())
			{
				return base.Increment(val);
			}
			return GetValue();
		}

		public override int Increment()
		{
			if (!IsLimitReached())
			{
				return base.Increment();
			}
			return GetValue();
		}

		public override int Reduction(int val)
		{
			if (!IsLimitReached())
			{
				return base.Reduction(val);
			}
			return GetValue();
		}

		public override int Reduction()
		{
			if (!IsLimitReached())
			{
				return base.Reduction();
			}
			return GetValue();
		}

		public virtual int ValuesUntilLimitRemains()
		{
			return _limit - GetValue();
		}

		public virtual bool IsLimitReached()
		{
			
				return _limit > 0 ? (GetValue() >= _limit) : (GetValue() <= _limit);
			
		}

	}

}
