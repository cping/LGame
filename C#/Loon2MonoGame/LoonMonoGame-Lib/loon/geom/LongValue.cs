using java.lang;

namespace loon.geom
{
	public class LongValue
	{

		private long value;

		public LongValue(): this(0)
		{
			
		}

		public LongValue(long v)
		{
			this.Set(v);
		}

		public LongValue Set(long v)
		{
			this.value = v;
			return this;
		}

		public long Get()
		{
			return Result();
		}

		public long Result()
		{
			return value;
		}

		public override string ToString()
		{
			return JavaSystem.Str(value);
		}

	}

}
