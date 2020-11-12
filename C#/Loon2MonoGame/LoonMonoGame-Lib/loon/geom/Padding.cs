using loon.utils;

namespace loon.geom
{

	public class Padding
	{

		private int left;
		private int top;
		private int right;
		private int bottom;

		public Padding(): this(0, 0, 0, 0)
		{
			
		}

		public Padding(int left, int top, int right, int bottom)
		{
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
		}

		public int GetLeft()
		{
			return left;
		}

		public void SetLeft(int left)
		{
			this.left = left;
		}

		public int GetTop()
		{
			return top;
		}

		public void SetTop(int top)
		{
			this.top = top;
		}

		public int GetRight()
		{
			return right;
		}

		public void SetRight(int right)
		{
			this.right = right;
		}

		public int GetBottom()
		{
			return bottom;
		}

		public void SetBottom(int bottom)
		{
			this.bottom = bottom;
		}

		
		public override string ToString()
		{
			StringKeyValue builder = new StringKeyValue("Padding");
			builder.Kv("left", left).Comma().Kv("top", top).Comma().Kv("right", right).Comma().Kv("bottom", bottom);
			return builder.ToString();
		}
	}

}
