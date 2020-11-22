namespace loon.geom
{
	public class LongTuple
	{

		public long val1;

		public long val2;

		public LongTuple()
		{
		}

		public LongTuple(long val)
		{
			val1 = val;
			val2 = val;
		}

		public LongTuple(long val1, long val2)
		{
			this.val1 = val1;
			this.val2 = val2;
		}

		public LongTuple Set(long val1, long val2)
		{
			this.val1 = val1;
			this.val2 = val2;
			return this;
		}

		public LongTuple Reverse()
		{
			long swap = val1;
			val1 = val2;
			val2 = swap;
			return this;
		}

		
		public override string ToString()
		{
			return "(" + val1 + ',' + val2 + ")";
		}

	}
}
