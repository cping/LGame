namespace loon.geom
{
	public class IntTuple
	{

		public int val1;

		public int val2;

		public IntTuple()
		{
		}

		public IntTuple(int val)
		{
			val1 = val;
			val2 = val;
		}

		public IntTuple(int val1, int val2)
		{
			this.val1 = val1;
			this.val2 = val2;
		}

		public IntTuple Set(int val1, int val2)
		{
			this.val1 = val1;
			this.val2 = val2;
			return this;
		}

		public IntTuple Reverse()
		{
			int swap = val1;
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
