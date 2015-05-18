namespace Loon
{
	public class UnityMeasureSpec
	{
		private static readonly int MODE_SHIFT = 30;
		
		private static readonly int MODE_MASK = 0x3 << MODE_SHIFT;
		
		public static readonly int UNSPECIFIED = 0 << MODE_SHIFT;
		
		public static readonly int EXACTLY = 1 << MODE_SHIFT;
		
		public static readonly int AT_MOST = 2 << MODE_SHIFT;
		
		public static int MakeMeasureSpec(int size, int mode)
		{
			return size + mode;
		}
		
		public static int GetMode(int measureSpec)
		{
			return (measureSpec & MODE_MASK);
		}
		
		public static int GetSize(int measureSpec)
		{
			return (measureSpec & ~MODE_MASK);
		}
	}
}
