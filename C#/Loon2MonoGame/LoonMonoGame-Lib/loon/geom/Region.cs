using loon.utils;

namespace loon.geom
{
   public class Region
{

		private int start;
		private int end;

		public Region(int start, int end)
		{
			this.start = start;
			this.end = end;
		}

		public Region SetStart(int start)
		{
			this.start = start;
			return this;
		}

		public Region SetEnd(int end)
		{
			this.end = end;
			return this;
		}

		public int GetStart()
		{
			return start;
		}

		public int GetEnd()
		{
			return end;
		}

		public int Random()
		{
			return (int)(start + (MathUtils.Random() * (end - start)));
		}

	
	}
}
