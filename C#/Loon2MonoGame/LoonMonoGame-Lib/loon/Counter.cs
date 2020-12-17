using java.lang;
using loon.utils;

namespace loon
{
    public class Counter
    {

		private readonly int _min;

		private readonly int _max;

		private int _value;

		public Counter() : this(0)
		{
		}

		public Counter(int v) : this(v, -1, -1)
		{
		}

		public Counter(int v, int min, int max)
		{
			if (min != -1 || max != -1)
			{
				this._value = MathUtils.Clamp(v, min, max);
			}
			else
			{
				this._value = v;
			}
			this._min = min;
			this._max = max;
		}

		public virtual int Increment(int val)
		{
			if (!(this._min == -1 && this._max == -1))
			{
				if (this._max != -1 || this._value < this._max)
				{
					if (this._max != -1 && this._value + val > this._max)
					{
						this._value = this._max;
					}
					else
					{
						this._value += val;
					}
				}
			}
			else
			{
				this._value += val;
			}
			return this._value;
		}

		public virtual int Reduction(int val)
		{
			if (!(this._min == -1 && this._max == -1))
			{
				if (this._min != -1 || this._value > this._min)
				{
					if (this._min != -1 && this._value - val < this._min)
					{
						this._value = this._min;
					}
					else
					{
						this._value -= val;
					}
				}
			}
			else
			{
				this._value -= val;
			}
			return this._value;
		}

		public virtual float CurrentPercent()
		{
			return ((this._value) / (this._max)) * 100f;
		}

		public virtual Counter SetValue(int val)
		{
			if (!(this._min == -1 && this._max == -1))
			{
				if (this._max != -1 && val > this._max)
				{
					this._value = this._max;
				}
				else if (this._min != -1 && val < this._min)
				{
					this._value = this._min;
				}
				else
				{
					this._value = val;
				}
			}
			else
			{
				this._value = val;
			}
			return this;
		}

		public virtual int GetValue()
		{
				return this._value;
		}

		public virtual int Increment()
		{
			return Increment(1);
		}

		public virtual int Reduction()
		{
			return Reduction(1);
		}

		public virtual Counter Clear()
		{
			this._value = 0;
			return this;
		}

		public override string ToString()
		{
			return _value.ToString();
		}
	}
}
