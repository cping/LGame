namespace Loon.Core.Geom
{

    using System;
    using System.Collections.Generic;
    using Loon.Utils;

    public sealed class Matrix
    {

        public class Transform2i
        {

            public Transform2i()
            {
                this.matrixs = Empty();
            }

            public int[][] matrixs;

            public void Idt()
            {
                this.matrixs = Empty();
            }

            public int Get(int x, int y)
            {
                return matrixs[x][y];
            }

            public void Set(int[][] matrixs_0)
            {
                this.matrixs = matrixs_0;
            }

            public void Mul(int[][] matrixs_0)
            {
                this.matrixs = Mul(matrixs_0, matrixs_0);
            }

            public int[] Mul(int[] fpV)
            {
                return Mul(matrixs, fpV);
            }

            public void Rotate(float alpha, float x, float y)
            {
                if (alpha != 1f)
                {
                    int[][] angle = RotationMatrix(alpha, x, y);
                    this.matrixs = Mul(matrixs, angle);
                }
            }

            public void Zoom(float scale, float x, float y)
            {
                if (scale != 1f)
                {
                    int[][] zoom = ZoomMatrix(scale, x, y);
                    this.matrixs = Mul(matrixs, zoom);
                }
            }

            public static int[][] Empty()
            {
                int[][] id = {
						new int[] {
								Loon.Utils.MathUtils.ONE_FIXED,
								0, 0 },
						new int[] {
								0,
								Loon.Utils.MathUtils.ONE_FIXED,
								0 },
						new int[] {
								0,
								0,
								Loon.Utils.MathUtils.ONE_FIXED } };
                return id;
            }

            public static int[][] Def()
            {
                int[][] id = { new int[] { 0, 0, 0 }, new int[] { 0, 0, 0 },
						new int[] { 0, 0, 0 } };
                return id;
            }

            public static int[][] Mul(int[][] a, int[][] b)
            {
                int[][] matrixs_0 = Def();
                for (int i = 0; i < 3; ++i)
                {
                    for (int j = 0; j < 3; ++j)
                    {
                        for (int n = 0; n < 3; ++n)
                        {
                            matrixs_0[i][j] += Loon.Utils.MathUtils.Mul(a[i][n], b[n][j]);
                        }
                    }
                }
                return matrixs_0;
            }

            public static int[] Mul(int[][] a, int[] b)
            {
                int[] matrixs_0 = { 0, 0, 0 };
                for (int i = 0; i < 3; ++i)
                {
                    for (int j = 0; j < 3; ++j)
                    {
                        matrixs_0[i] += Loon.Utils.MathUtils.Mul(a[i][j], b[j]);
                    }
                }
                return matrixs_0;
            }

            public static int[][] ZoomMatrix(float scale,
                    float x, float y)
            {
                int mu = (0 == scale) ? Int32.MaxValue : Loon.Utils.MathUtils
                        .FromFloat(1 / scale);
                if (Loon.Utils.MathUtils.ONE_FIXED == mu)
                {
                    return Def();
                }
                int x_c = Loon.Utils.MathUtils.FromFloat(x);
                int y_c = Loon.Utils.MathUtils.FromFloat(y);
                int transX = x_c - Loon.Utils.MathUtils.Mul(x_c, mu);
                int transY = y_c - Loon.Utils.MathUtils.Mul(y_c, mu);
                int[][] zoom = {
						new int[] { mu, 0, transX },
						new int[] { 0, mu, transY },
						new int[] {
								0,
								0,
								Loon.Utils.MathUtils.ONE_FIXED } };
                return zoom;
            }

            public static int[][] RotationMatrix(float alpha,
                    float x, float y)
            {
                if (0 == alpha % (2 * Loon.Utils.MathUtils.PI))
                {
                    return Empty();
                }
                int cosAlpha = Loon.Utils.MathUtils.FromDouble(Loon.Utils.MathUtils.Cos(alpha));
                int sinAlpha = Loon.Utils.MathUtils.FromDouble(Loon.Utils.MathUtils.Sin(alpha));
                int x_c = Loon.Utils.MathUtils.FromFloat(x);
                int y_c = Loon.Utils.MathUtils.FromFloat(y);
                int transX = Loon.Utils.MathUtils.Mul(x_c, Loon.Utils.MathUtils.ONE_FIXED
                        - cosAlpha)
                        + Loon.Utils.MathUtils.Mul(y_c, sinAlpha);
                int transY = Loon.Utils.MathUtils.Mul(y_c, Loon.Utils.MathUtils.ONE_FIXED
                        - cosAlpha)
                        - Loon.Utils.MathUtils.Mul(x_c, sinAlpha);
                int[][] angle = {
						new int[] { cosAlpha, -sinAlpha, transX },
						new int[] { sinAlpha, cosAlpha, transY },
						new int[] {
								0,
								0,
								Loon.Utils.MathUtils.ONE_FIXED } };
                return angle;
            }

        }

        internal float[] matrixs;

        public Matrix()
        {
            this.result = new float[16];
            this.Idt();
        }

        public Matrix(Matrix m)
        {
            this.result = new float[16];
            matrixs = new float[9];
            for (int i = 0; i < 9; i++)
            {
                matrixs[i] = m.matrixs[i];
            }
        }

        public Matrix(Matrix t1, Matrix t2): this(t1)
        {
           
            Concatenate(t2);
        }

        public Matrix(float[] matrixs_0)
        {
            this.result = new float[16];
            if (matrixs_0.Length != 9)
            {
                throw new Exception("matrixs.length != 9");
            }
            this.matrixs = new float[] { matrixs_0[0], matrixs_0[1], matrixs_0[2],
					matrixs_0[3], matrixs_0[4], matrixs_0[5], matrixs_0[6], matrixs_0[7],
					matrixs_0[8] };
        }

        public void Set(float x1, float y1, float x2, float y2)
        {
            Set(x1, y1, 1, x2, y2, 1);
        }

        public Matrix(float a1, float a2, float a3, float b1, float b2, float b3)
        {
            this.result = new float[16];
            Set(a1, a2, a3, b1, b2, b3);
        }

        public void Set(float a1, float a2, float a3, float b1, float b2, float b3)
        {
            Set(a1, a2, a3, b1, b2, b3, 0, 0, 1);
        }

        public void Set(float a1, float a2, float a3, float b1, float b2, float b3,
                float c1, float c2, float c3)
        {
            matrixs = new float[] { a1, a2, a3, b1, b2, b3, c1, c2, c3 };
        }

        public void Transform(float[] source, int sourceOffset,
                float[] destination, int destOffset, int numberOfPoints)
        {

            float[] result = (source == destination) ? new float[numberOfPoints * 2]
                    : destination;

            for (int i = 0; i < numberOfPoints * 2; i += 2)
            {
                for (int j = 0; j < 6; j += 3)
                {
                    result[i + (j / 3)] = source[i + sourceOffset] * matrixs[j]
                            + source[i + sourceOffset + 1] * matrixs[j + 1] + 1
                            * matrixs[j + 2];
                }
            }

            if (source == destination)
            {
                for (int i_0 = 0; i_0 < numberOfPoints * 2; i_0 += 2)
                {
                    destination[i_0 + destOffset] = result[i_0];
                    destination[i_0 + destOffset + 1] = result[i_0 + 1];
                }
            }
        }

        public Matrix Concatenate(Matrix m)
        {
            float[] mp = new float[9];
            float n00 = matrixs[0] * m.matrixs[0] + matrixs[1] * m.matrixs[3];
            float n01 = matrixs[0] * m.matrixs[1] + matrixs[1] * m.matrixs[4];
            float n02 = matrixs[0] * m.matrixs[2] + matrixs[1] * m.matrixs[5]
                    + matrixs[2];
            float n10 = matrixs[3] * m.matrixs[0] + matrixs[4] * m.matrixs[3];
            float n11 = matrixs[3] * m.matrixs[1] + matrixs[4] * m.matrixs[4];
            float n12 = matrixs[3] * m.matrixs[2] + matrixs[4] * m.matrixs[5]
                    + matrixs[5];
            mp[0] = n00;
            mp[1] = n01;
            mp[2] = n02;
            mp[3] = n10;
            mp[4] = n11;
            mp[5] = n12;

            matrixs = mp;
            return this;
        }

        public static Matrix CreateRotateTransform(float angle)
        {
            return new Matrix(MathUtils.Cos(angle), -MathUtils.Sin(angle), 0,
                    MathUtils.Sin(angle), MathUtils.Cos(angle), 0);
        }

        public static Matrix CreateRotateTransform(float angle, float x, float y)
        {
            Matrix temp = Matrix.CreateRotateTransform(angle);
            float sinAngle = temp.matrixs[3];
            float oneMinusCosAngle = 1.0f - temp.matrixs[4];
            temp.matrixs[2] = x * oneMinusCosAngle + y * sinAngle;
            temp.matrixs[5] = y * oneMinusCosAngle - x * sinAngle;
            return temp;
        }

        public static Matrix CreateTranslateTransform(float xOffset, float yOffset)
        {
            return new Matrix(1, 0, xOffset, 0, 1, yOffset);
        }

        public static Matrix CreateScaleTransform(float scalex, float scaley)
        {
            return new Matrix(scalex, 0, 0, 0, scaley, 0);
        }

        public float Get(int x, int y)
        {
            try
            {
                return matrixs[x * 3 + y];
            }
            catch (Exception)
            {
                throw new ArgumentException("Invalid indices into matrix !");
            }
        }

        public void Set(int x, int y, float v)
        {
            try
            {
                this.matrixs[x * 3 + y] = v;
            }
            catch (Exception)
            {
                throw new ArgumentException("Invalid indices into matrix !");
            }
        }

        public Matrix Set(Matrix m)
        {
            matrixs[0] = m.matrixs[0];
            matrixs[1] = m.matrixs[1];
            matrixs[2] = m.matrixs[2];
            matrixs[3] = m.matrixs[3];
            matrixs[4] = m.matrixs[4];
            matrixs[5] = m.matrixs[5];
            matrixs[6] = m.matrixs[6];
            matrixs[7] = m.matrixs[7];
            matrixs[8] = m.matrixs[8];
            return this;
        }

        public Matrix From(float[] source, bool rowMajor)
        {
            Matrix m = new Matrix();
            if (rowMajor)
            {
                for (int i = 0; i < 3; i++)
                {
                    for (int j = 0; j < 3; j++)
                    {
                        m.Set(i, j, source[i * 3 + j]);
                    }
                }
            }
            else
            {
                for (int j_0 = 0; j_0 < 3; j_0++)
                {
                    for (int i_1 = 0; i_1 < 3; i_1++)
                    {
                        m.Set(i_1, j_0, source[j_0 * 3 + i_1]);
                    }
                }
            }
            return this;
        }

        public void Translation(float x, float y)
        {
            this.matrixs[0] = 1;
            this.matrixs[1] = 0;
            this.matrixs[2] = 0;
            this.matrixs[3] = 0;
            this.matrixs[4] = 1;
            this.matrixs[5] = 0;
            this.matrixs[6] = x;
            this.matrixs[7] = y;
            this.matrixs[8] = 1;
        }

        public void Rotation(float angle)
        {
            angle = MathUtils.DEG_TO_RAD * angle;
            float cos = MathUtils.Cos(angle);
            float sin = MathUtils.Sin(angle);
            this.matrixs[0] = cos;
            this.matrixs[1] = sin;
            this.matrixs[2] = 0;
            this.matrixs[3] = -sin;
            this.matrixs[4] = cos;
            this.matrixs[5] = 0;
            this.matrixs[6] = 0;
            this.matrixs[7] = 0;
            this.matrixs[8] = 1;
        }

        private float[] result;

        public float[] Get()
        {
            result[0] = matrixs[0];
            result[1] = matrixs[1];
            result[2] = matrixs[2];
            result[3] = 0;
            result[4] = matrixs[3];
            result[5] = matrixs[4];
            result[6] = matrixs[5];
            result[7] = 0;
            result[8] = 0;
            result[9] = 0;
            result[10] = 1;
            result[11] = 0;
            result[12] = matrixs[6];
            result[13] = matrixs[7];
            result[14] = 0;
            result[15] = matrixs[8];
            return result;
        }

        public void RotationX(float angleX)
        {
            angleX = MathUtils.PI / 180 * angleX;
            Set(1f, 0f, 0f, 0f, MathUtils.Cos(angleX), -MathUtils.Sin(angleX), 0f,
                    MathUtils.Sin(angleX), MathUtils.Cos(angleX));
        }

        public void RotationY(float angleY)
        {
            angleY = MathUtils.PI / 180 * angleY;
            Set(MathUtils.Cos(angleY), 0f, MathUtils.Sin(angleY), 0f, 1f, 0f,
                    -MathUtils.Sin(angleY), 0f, MathUtils.Cos(angleY));
        }

        public void RotationZ(float angleZ)
        {
            angleZ = MathUtils.PI / 180 * angleZ;
            Set(MathUtils.Cos(angleZ), -MathUtils.Sin(angleZ), 0f,
                    MathUtils.Sin(angleZ), MathUtils.Cos(angleZ), 0f, 0f, 0f, 1f);
        }

        public void Scale(float sx, float sy)
        {
            this.matrixs[0] = sx;
            this.matrixs[1] = 0;
            this.matrixs[2] = 0;
            this.matrixs[3] = 0;
            this.matrixs[4] = sy;
            this.matrixs[5] = 0;
            this.matrixs[6] = 0;
            this.matrixs[7] = 0;
            this.matrixs[8] = 1;
        }

        public void Idt()
        {
            if (matrixs == null)
            {
                matrixs = new float[] { 1, 0, 0, 0, 1, 0, 0, 0, 1 };
            }
            else
            {
                this.matrixs[0] = 1;
                this.matrixs[1] = 0;
                this.matrixs[2] = 0;
                this.matrixs[3] = 0;
                this.matrixs[4] = 1;
                this.matrixs[5] = 0;
                this.matrixs[6] = 0;
                this.matrixs[7] = 0;
                this.matrixs[8] = 1;
            }
        }

        public bool IsIdt()
        {
            return (matrixs[0] == 1 && matrixs[1] == 0 && matrixs[2] == 0)
                    && (matrixs[3] == 0 && matrixs[4] == 1 && matrixs[5] == 0)
                    && (matrixs[6] == 0 && matrixs[7] == 0 && matrixs[8] == 1);
        }

        public float Det()
        {
            return matrixs[0] * matrixs[4] * matrixs[8] + matrixs[3] * matrixs[7]
                    * matrixs[2] + matrixs[6] * matrixs[1] * matrixs[5]
                    - matrixs[0] * matrixs[7] * matrixs[5] - matrixs[3]
                    * matrixs[1] * matrixs[8] - matrixs[6] * matrixs[4]
                    * matrixs[2];
        }

        private static float Detd(float a, float b, float c, float d)
        {
            return (a * d) - (b * c);
        }

        public void Adj()
        {

            float a11 = this.matrixs[0];
            float a12 = this.matrixs[1];
            float a13 = this.matrixs[2];

            float a21 = this.matrixs[3];
            float a22 = this.matrixs[4];
            float a23 = this.matrixs[5];

            float a31 = this.matrixs[6];
            float a32 = this.matrixs[7];
            float a33 = this.matrixs[8];

            this.matrixs[0] = Detd(a22, a23, a32, a33);
            this.matrixs[1] = Detd(a13, a12, a33, a32);
            this.matrixs[2] = Detd(a12, a13, a22, a23);

            this.matrixs[3] = Detd(a23, a21, a33, a31);
            this.matrixs[4] = Detd(a11, a13, a31, a33);
            this.matrixs[5] = Detd(a13, a11, a23, a21);

            this.matrixs[6] = Detd(a21, a22, a31, a32);
            this.matrixs[7] = Detd(a12, a11, a32, a31);
            this.matrixs[8] = Detd(a11, a12, a21, a22);
        }

        public void Add(Matrix m)
        {
            float a1 = this.matrixs[0];
            float a2 = this.matrixs[1];
            float a3 = this.matrixs[2];

            float b1 = this.matrixs[3];
            float b2 = this.matrixs[4];
            float b3 = this.matrixs[5];

            float c1 = this.matrixs[6];
            float c2 = this.matrixs[7];
            float c3 = this.matrixs[8];

            a1 += m.matrixs[0];
            a2 += m.matrixs[1];
            a3 += m.matrixs[2];

            b1 += m.matrixs[3];
            b2 += m.matrixs[4];
            b3 += m.matrixs[5];

            c1 += m.matrixs[6];
            c2 += m.matrixs[7];
            c3 += m.matrixs[8];

            this.matrixs[0] = a1;
            this.matrixs[1] = a2;
            this.matrixs[2] = a3;
            this.matrixs[3] = b1;
            this.matrixs[4] = b2;
            this.matrixs[5] = b3;
            this.matrixs[6] = c1;
            this.matrixs[7] = c2;
            this.matrixs[8] = c3;
        }

        public Matrix AddEqual(Matrix m)
        {
            Matrix newMatrix = new Matrix(this.matrixs);
            newMatrix.Add(m);
            return newMatrix;
        }

        public void Mul(float c)
        {
            float a1 = this.matrixs[0];
            float a2 = this.matrixs[1];
            float a3 = this.matrixs[2];

            float b1 = this.matrixs[3];
            float b2 = this.matrixs[4];
            float b3 = this.matrixs[5];

            float c1 = this.matrixs[6];
            float c2 = this.matrixs[7];
            float c3 = this.matrixs[8];

            this.matrixs[0] = a1 * c;
            this.matrixs[1] = a2 * c;
            this.matrixs[2] = a3 * c;
            this.matrixs[3] = b1 * c;
            this.matrixs[4] = b2 * c;
            this.matrixs[5] = b3 * c;
            this.matrixs[6] = c1 * c;
            this.matrixs[7] = c2 * c;
            this.matrixs[8] = c3 * c;
        }

        public void Mul(Matrix m)
        {
            float a1 = matrixs[0] * m.matrixs[0] + matrixs[3] * m.matrixs[1]
                    + matrixs[6] * m.matrixs[2];
            float a2 = matrixs[0] * m.matrixs[3] + matrixs[3] * m.matrixs[4]
                    + matrixs[6] * m.matrixs[5];
            float a3 = matrixs[0] * m.matrixs[6] + matrixs[3] * m.matrixs[7]
                    + matrixs[6] * m.matrixs[8];

            float b1 = matrixs[1] * m.matrixs[0] + matrixs[4] * m.matrixs[1]
                    + matrixs[7] * m.matrixs[2];
            float b2 = matrixs[1] * m.matrixs[3] + matrixs[4] * m.matrixs[4]
                    + matrixs[7] * m.matrixs[5];
            float b3 = matrixs[1] * m.matrixs[6] + matrixs[4] * m.matrixs[7]
                    + matrixs[7] * m.matrixs[8];

            float c1 = matrixs[2] * m.matrixs[0] + matrixs[5] * m.matrixs[1]
                    + matrixs[8] * m.matrixs[2];
            float c2 = matrixs[2] * m.matrixs[3] + matrixs[5] * m.matrixs[4]
                    + matrixs[8] * m.matrixs[5];
            float c3 = matrixs[2] * m.matrixs[6] + matrixs[5] * m.matrixs[7]
                    + matrixs[8] * m.matrixs[8];

            this.matrixs[0] = a1;
            this.matrixs[1] = a2;
            this.matrixs[2] = a3;

            this.matrixs[3] = b1;
            this.matrixs[4] = b2;
            this.matrixs[5] = b3;

            this.matrixs[6] = c1;
            this.matrixs[7] = c2;
            this.matrixs[8] = c3;

        }

        public Matrix MulEqual(Matrix m)
        {
            if (m == null)
            {
                m = new Matrix();
            }
            Matrix result_0 = new Matrix(this.matrixs);
            result_0.Mul(m);
            return result_0;
        }

        public Matrix Invert(Matrix m)
        {
            Matrix result_0 = m;
            if (result_0 == null)
            {
                result_0 = new Matrix();
            }

            float det = Det();
            if (Math.Abs(det) <= MathUtils.EPSILON)
            {
                throw new ArithmeticException("This matrix cannot be inverted !");
            }

            float temp00 = matrixs[4] * matrixs[8] - matrixs[5] * matrixs[7];
            float temp01 = matrixs[2] * matrixs[7] - matrixs[1] * matrixs[8];
            float temp02 = matrixs[1] * matrixs[5] - matrixs[2] * matrixs[4];
            float temp10 = matrixs[5] * matrixs[6] - matrixs[3] * matrixs[8];
            float temp11 = matrixs[0] * matrixs[8] - matrixs[2] * matrixs[6];
            float temp12 = matrixs[2] * matrixs[3] - matrixs[0] * matrixs[5];
            float temp20 = matrixs[3] * matrixs[7] - matrixs[4] * matrixs[6];
            float temp21 = matrixs[1] * matrixs[6] - matrixs[0] * matrixs[7];
            float temp22 = matrixs[0] * matrixs[4] - matrixs[1] * matrixs[3];
            result_0.Set(temp00, temp01, temp02, temp10, temp11, temp12, temp20,
                    temp21, temp22);
            result_0.Mul(1.0f / det);
            return result_0;
        }

        public bool IsFloatValid()
        {

            bool valid = true;

            valid &= !Single.IsNaN(matrixs[0]);
            valid &= !Single.IsNaN(matrixs[1]);
            valid &= !Single.IsNaN(matrixs[2]);

            valid &= !Single.IsNaN(matrixs[3]);
            valid &= !Single.IsNaN(matrixs[4]);
            valid &= !Single.IsNaN(matrixs[5]);

            valid &= !Single.IsNaN(matrixs[6]);
            valid &= !Single.IsNaN(matrixs[7]);
            valid &= !Single.IsNaN(matrixs[8]);

            return valid;
        }

        public static Matrix Avg(ICollection<Matrix> set)
        {
            Matrix average = new Matrix();
            average.Set(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f);
            float hist = 0;
            foreach (Matrix matrix3d in set)
            {
                if (matrix3d.IsFloatValid())
                {
                    average.Add(matrix3d);
                    hist++;
                }
            }
            average.Mul(1f / hist);
            return average;
        }

        public void Copy(Matrix m)
        {
            if (m == null)
            {
                Idt();
            }
            else
            {
                Set(m);
            }
        }

        public override bool Equals(object o)
        {
            if (!(o is Matrix) || o == null)
            {
                return false;
            }

            if ((object)this == o)
            {
                return true;
            }

            Matrix comp = (Matrix)o;

            if (matrixs[0].CompareTo(comp.matrixs[0]) != 0)
            {
                return false;
            }
            if (matrixs[1].CompareTo(comp.matrixs[1]) != 0)
            {
                return false;
            }
            if (matrixs[2].CompareTo(comp.matrixs[2]) != 0)
            {
                return false;
            }

            if (matrixs[3].CompareTo(comp.matrixs[3]) != 0)
            {
                return false;
            }
            if (matrixs[4].CompareTo(comp.matrixs[4]) != 0)
            {
                return false;
            }
            if (matrixs[5].CompareTo(comp.matrixs[5]) != 0)
            {
                return false;
            }

            if (matrixs[6].CompareTo(comp.matrixs[6]) != 0)
            {
                return false;
            }
            if (matrixs[7].CompareTo(comp.matrixs[7]) != 0)
            {
                return false;
            }
            if (matrixs[8].CompareTo(comp.matrixs[8]) != 0)
            {
                return false;
            }

            return true;
        }

        public override int GetHashCode()
        {
            int result = 17;
            for (int j = 0; j < 9; j++)
            {
                long val = (long)matrixs[j];
                result += 31 * result + (int)(val ^ ((long)(((ulong)val) >> 32)));
            }
            return result;
        }

        public Vector2f Transform(Vector2f pt)
        {
            float[] ins0 = new float[] { pt.x, pt.y };
            float[] xout = new float[2];

            Transform(ins0, 0, xout, 0, 1);

            return new Vector2f(xout[0], xout[1]);
        }

        public Matrix Clone()
        {
            return new Matrix(this.matrixs);
        }

        public float[] GetValues()
        {
            return matrixs;
        }
    }
}
