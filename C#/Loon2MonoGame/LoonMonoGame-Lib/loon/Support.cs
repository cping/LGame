namespace loon
{
    public abstract class Support
{

		public const int M00 = 0;
		public const int M01 = 4;
		public const int M02 = 8;
		public const int M03 = 12;
		public const int M10 = 1;
		public const int M11 = 5;
		public const int M12 = 9;
		public const int M13 = 13;
		public const int M20 = 2;
		public const int M21 = 6;
		public const int M22 = 10;
		public const int M23 = 14;
		public const int M30 = 3;
		public const int M31 = 7;
		public const int M32 = 11;
		public const int M33 = 15;

		public abstract void FilterColor(int maxPixel, int pixelStart, int pixelEnd,
				int[] src, int[] dst, int[] colors, int c1, int c2);

		public abstract void FilterFractions(int size, float[] fractions, int width,
				int height, int[] pixels, int numElements);

		public abstract void Mul(float[] mata, float[] matb);

		public abstract void MulVec(float[] mat, float[] vec);

		public abstract void MulVec(float[] mat, float[] vecs, int offset, int numVecs,
				int stride);

		public abstract void Prj(float[] mat, float[] vec);

		public abstract void Prj(float[] mat, float[] vecs, int offset, int numVecs,
				int stride);

		public abstract void Rot(float[] mat, float[] vec);

		public abstract void Rot(float[] mat, float[] vecs, int offset, int numVecs,
				int stride);

		public abstract bool Inv(float[] values);

		public abstract float Det(float[] values);

		public abstract int[] ToColorKey(int[] buffer, int colorKey);

		public abstract int[] ToColorKeys(int[] buffer, int[] colors);

		public abstract int[] ToColorKeyLimit(int[] buffer, int start, int end);

		public abstract int[] ToGray(int[] buffer, int w, int h);
	}
}
