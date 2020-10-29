using java.lang;

namespace loon.canvas
{
	public class Alpha
	{

		public static readonly Alpha ZERO = new Alpha(0.0f);
		public static readonly Alpha FULL = new Alpha(1.0f);

		private static readonly int SCALE_SHORT_MODE = 0x11;
		private static readonly float MAX_INT_VALUE = 255.0f;
		private static readonly int HEX_BASE = 16;

		private float alpha = 0.0f;

		public Alpha(string color)
		{
			this.alpha = GetString(color);
		}

		public Alpha(float a)
		{
			this.alpha = a;
		}

		public Alpha Linear(Alpha end, float t)
		{
			return new Alpha(this.alpha + t * (end.alpha - this.alpha));
		}
		public float GetAlpha()
		{
			return alpha;
		}

		private float GetString(string color)
		{
			if (IsShortMode(color))
			{
				return (Integer.ParseInt(color.Substring(1, 2), HEX_BASE) * SCALE_SHORT_MODE)
						/ MAX_INT_VALUE;
			}
			else
			{
				return Integer.ParseInt(color.Substring(1, 3), HEX_BASE)
						/ MAX_INT_VALUE;
			}
		}

		public Alpha SetAlpha(float newColorAlpha)
		{
			alpha = newColorAlpha;
			return this;
		}

		private bool IsShortMode(string color)
		{
			return color.Length() == 2;
		}

		public Alpha Mutiply(float factor)
		{
			return new Alpha(alpha * factor);
		}

		
		public override string ToString()
		{
			return "(" + alpha + ")";
		}

	}

}
