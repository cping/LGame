using java.lang;

namespace loon.geom
{
    public class Matrix4
    {
        public static Matrix4 TMP()
        {
            return new Matrix4();
        }

        public static Matrix4 ZERO()
        {
            return new Matrix4();
        }

        public readonly static int M00 = 0;

        public readonly static int M01 = 4;

        public readonly static int M02 = 8;

        public readonly static int M03 = 12;

        public readonly static int M10 = 1;

        public readonly static int M11 = 5;

        public readonly static int M12 = 9;

        public readonly static int M13 = 13;

        public readonly static int M20 = 2;

        public readonly static int M21 = 6;

        public readonly static int M22 = 10;

        public readonly static int M23 = 14;

        public readonly static int M30 = 3;

        public readonly static int M31 = 7;

        public readonly static int M32 = 11;

        public readonly static int M33 = 15;

        public readonly float[] tmp = new float[16];

        public readonly float[] val = new float[16];

        private void init()
        {

        }

        public Matrix4()
        {
            val[M00] = 1f;
            val[M11] = 1f;
            val[M22] = 1f;
            val[M33] = 1f;
            init();
        }

        public Matrix4(Matrix4 matrix)
        {
            init();
            this.Set(matrix);
        }

        public Matrix4(float[] values)
        {
            init();
            this.Set(values);
        }


        public Matrix4 Set(Matrix4 matrix)
        {
            return this.Set(matrix.val);
        }

        public Matrix4 Set(float[] values)
        {
            JavaSystem.Arraycopy(values, 0, val, 0, val.Length);
            return this;
        }
    }
}
