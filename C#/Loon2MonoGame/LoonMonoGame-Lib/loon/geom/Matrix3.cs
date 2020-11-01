using java.lang;
using java.util;
using loon.utils;
using System.Collections;
using System.Collections.Generic;

namespace loon.geom
{
    public class Matrix3 : XY
    {

        public static Matrix3 TMP()
        {
            return new Matrix3();
        }

        public static Matrix3 ZERO()
        {
            return new Matrix3();
        }

        public readonly static int M00 = 0;
        public readonly static int M01 = 3;
        public readonly static int M02 = 6;
        public readonly static int M10 = 1;
        public readonly static int M11 = 4;
        public readonly static int M12 = 7;
        public readonly static int M20 = 2;
        public readonly static int M21 = 5;
        public readonly static int M22 = 8;

        public float[] val = new float[9];
        private float[] tmp = new float[9];

        public Matrix3()
        {
            Idt();
        }

        public Matrix3(Matrix3 matrix)
        {
            Set(matrix);
        }

        public Matrix3(float[] values)
        {
            this.Set(values);
        }

        public Matrix3(float[] mat, int offset)
        {
            this.val = new float[9];
            for (int i = 0; i < 9; i++)
            {
                this.val[i] = mat[i + offset];
            }
        }

        public Matrix3(Matrix3 t1, Matrix3 t2) : this(t1)
        {

            Concatenate(t2);
        }

        public Matrix3 Set(Affine2f affine)
        {
            float[] val = this.val;

            val[M00] = affine.m00;
            val[M10] = affine.m10;
            val[M20] = 0;
            val[M01] = affine.m01;
            val[M11] = affine.m11;
            val[M21] = 0;
            val[M02] = affine.tx;
            val[M12] = affine.ty;
            val[M22] = 1;

            return this;
        }

        public Matrix3 SetToOrtho2D(float x, float y, float width, float height)
        {
            SetToOrtho(x, x + width, y + height, y, 1f, -1f);
            return this;
        }

        public Matrix3 SetToOrtho2D(float x, float y, float width, float height, float near, float far)
        {
            SetToOrtho(x, x + width, y + height, y, near, far);
            return this;
        }

        public Matrix3 SetToOrtho(float left, float right, float bottom, float top, float near, float far)
        {

            float x_orth = 2 / (right - left);
            float y_orth = 2 / (top - bottom);
            float z_orth = -2 / (far - near);

            float tx = -(right + left) / (right - left);
            float ty = -(top + bottom) / (top - bottom);
            float tz = -(far + near) / (far - near);

            val[M00] = x_orth;
            val[M10] = 0;
            val[M20] = z_orth;
            val[M01] = y_orth;
            val[M11] = 0;
            val[M21] = z_orth;
            val[M02] = tx;
            val[M12] = ty;
            val[M22] = tz;

            return this;
        }

        public Matrix3 Idt()
        {
            val[M00] = 1;
            val[M10] = 0;
            val[M20] = 0;
            val[M01] = 0;
            val[M11] = 1;
            val[M21] = 0;
            val[M02] = 0;
            val[M12] = 0;
            val[M22] = 1;
            return this;
        }

        public Matrix3 Mul(Matrix3 m)
        {
            float v00 = val[M00] * m.val[M00] + val[M01] * m.val[M10] + val[M02] * m.val[M20];
            float v01 = val[M00] * m.val[M01] + val[M01] * m.val[M11] + val[M02] * m.val[M21];
            float v02 = val[M00] * m.val[M02] + val[M01] * m.val[M12] + val[M02] * m.val[M22];

            float v10 = val[M10] * m.val[M00] + val[M11] * m.val[M10] + val[M12] * m.val[M20];
            float v11 = val[M10] * m.val[M01] + val[M11] * m.val[M11] + val[M12] * m.val[M21];
            float v12 = val[M10] * m.val[M02] + val[M11] * m.val[M12] + val[M12] * m.val[M22];

            float v20 = val[M20] * m.val[M00] + val[M21] * m.val[M10] + val[M22] * m.val[M20];
            float v21 = val[M20] * m.val[M01] + val[M21] * m.val[M11] + val[M22] * m.val[M21];
            float v22 = val[M20] * m.val[M02] + val[M21] * m.val[M12] + val[M22] * m.val[M22];

            val[M00] = v00;
            val[M10] = v10;
            val[M20] = v20;
            val[M01] = v01;
            val[M11] = v11;
            val[M21] = v21;
            val[M02] = v02;
            val[M12] = v12;
            val[M22] = v22;

            return this;
        }

        public Matrix3 MulLeft(Matrix3 m)
        {
            float v00 = m.val[M00] * val[M00] + m.val[M01] * val[M10] + m.val[M02] * val[M20];
            float v01 = m.val[M00] * val[M01] + m.val[M01] * val[M11] + m.val[M02] * val[M21];
            float v02 = m.val[M00] * val[M02] + m.val[M01] * val[M12] + m.val[M02] * val[M22];

            float v10 = m.val[M10] * val[M00] + m.val[M11] * val[M10] + m.val[M12] * val[M20];
            float v11 = m.val[M10] * val[M01] + m.val[M11] * val[M11] + m.val[M12] * val[M21];
            float v12 = m.val[M10] * val[M02] + m.val[M11] * val[M12] + m.val[M12] * val[M22];

            float v20 = m.val[M20] * val[M00] + m.val[M21] * val[M10] + m.val[M22] * val[M20];
            float v21 = m.val[M20] * val[M01] + m.val[M21] * val[M11] + m.val[M22] * val[M21];
            float v22 = m.val[M20] * val[M02] + m.val[M21] * val[M12] + m.val[M22] * val[M22];

            val[M00] = v00;
            val[M10] = v10;
            val[M20] = v20;
            val[M01] = v01;
            val[M11] = v11;
            val[M21] = v21;
            val[M02] = v02;
            val[M12] = v12;
            val[M22] = v22;

            return this;
        }

        public Matrix3 SetToRotation(float degrees)
        {
            return SetToRotationRad(MathUtils.DEG_TO_RAD * degrees);
        }

        public Matrix3 SetToRotationRad(float radians)
        {
            float Cos = MathUtils.Cos(radians);
            float Sin = MathUtils.Sin(radians);

            this.val[M00] = Cos;
            this.val[M10] = Sin;
            this.val[M20] = 0;

            this.val[M01] = -Sin;
            this.val[M11] = Cos;
            this.val[M21] = 0;

            this.val[M02] = 0;
            this.val[M12] = 0;
            this.val[M22] = 1;

            return this;
        }

        public Matrix3 SetToRotation(Vector3f axis, float degrees)
        {
            return SetToRotation(axis, MathUtils.CosDeg(degrees), MathUtils.SinDeg(degrees));
        }

        public Matrix3 SetToRotation(Vector3f axis, float Cos, float Sin)
        {
            float oc = 1.0f - Cos;
            val[M00] = oc * axis.x * axis.x + Cos;
            val[M10] = oc * axis.x * axis.y - axis.z * Sin;
            val[M20] = oc * axis.z * axis.x + axis.y * Sin;
            val[M01] = oc * axis.x * axis.y + axis.z * Sin;
            val[M11] = oc * axis.y * axis.y + Cos;
            val[M21] = oc * axis.y * axis.z - axis.x * Sin;
            val[M02] = oc * axis.z * axis.x - axis.y * Sin;
            val[M12] = oc * axis.y * axis.z + axis.x * Sin;
            val[M22] = oc * axis.z * axis.z + Cos;
            return this;
        }

        public Matrix3 SetToTranslation(float x, float y)
        {
            this.val[M00] = 1;
            this.val[M10] = 0;
            this.val[M20] = 0;

            this.val[M01] = 0;
            this.val[M11] = 1;
            this.val[M21] = 0;

            this.val[M02] = x;
            this.val[M12] = y;
            this.val[M22] = 1;

            return this;
        }

        public Matrix3 SetToTranslation(Vector2f translation)
        {
            this.val[M00] = 1;
            this.val[M10] = 0;
            this.val[M20] = 0;

            this.val[M01] = 0;
            this.val[M11] = 1;
            this.val[M21] = 0;

            this.val[M02] = translation.x;
            this.val[M12] = translation.y;
            this.val[M22] = 1;

            return this;
        }

        public Matrix3 SetToScaling(float scaleX, float scaleY)
        {
            val[M00] = scaleX;
            val[M10] = 0;
            val[M20] = 0;
            val[M01] = 0;
            val[M11] = scaleY;
            val[M21] = 0;
            val[M02] = 0;
            val[M12] = 0;
            val[M22] = 1;
            return this;
        }

        public Matrix3 SetToScaling(Vector2f scale)
        {
            val[M00] = scale.x;
            val[M10] = 0;
            val[M20] = 0;
            val[M01] = 0;
            val[M11] = scale.y;
            val[M21] = 0;
            val[M02] = 0;
            val[M12] = 0;
            val[M22] = 1;
            return this;
        }

        public float Det()
        {
            return val[M00] * val[M11] * val[M22] + val[M01] * val[M12] * val[M20] + val[M02] * val[M10] * val[M21]
                    - val[M00] * val[M12] * val[M21] - val[M01] * val[M10] * val[M22] - val[M02] * val[M11] * val[M20];
        }

        public Matrix3 Inv()
        {
            float det = Det();
            if (det == 0)
            {
                throw new LSysException("Can't invert a singular matrix");
            }

            float inv_det = 1.0f / det;

            tmp[M00] = val[M11] * val[M22] - val[M21] * val[M12];
            tmp[M10] = val[M20] * val[M12] - val[M10] * val[M22];
            tmp[M20] = val[M10] * val[M21] - val[M20] * val[M11];
            tmp[M01] = val[M21] * val[M02] - val[M01] * val[M22];
            tmp[M11] = val[M00] * val[M22] - val[M20] * val[M02];
            tmp[M21] = val[M20] * val[M01] - val[M00] * val[M21];
            tmp[M02] = val[M01] * val[M12] - val[M11] * val[M02];
            tmp[M12] = val[M10] * val[M02] - val[M00] * val[M12];
            tmp[M22] = val[M00] * val[M11] - val[M10] * val[M01];

            val[M00] = inv_det * tmp[M00];
            val[M10] = inv_det * tmp[M10];
            val[M20] = inv_det * tmp[M20];
            val[M01] = inv_det * tmp[M01];
            val[M11] = inv_det * tmp[M11];
            val[M21] = inv_det * tmp[M21];
            val[M02] = inv_det * tmp[M02];
            val[M12] = inv_det * tmp[M12];
            val[M22] = inv_det * tmp[M22];

            return this;
        }

        public Matrix3 Set(Matrix3 mat)
        {
            JavaSystem.Arraycopy(mat.val, 0, val, 0, val.Length);
            return this;
        }

        public Matrix3 Set(Matrix4 mat)
        {
            val[M00] = mat.val[Matrix4.M00];
            val[M10] = mat.val[Matrix4.M10];
            val[M20] = mat.val[Matrix4.M20];
            val[M01] = mat.val[Matrix4.M01];
            val[M11] = mat.val[Matrix4.M11];
            val[M21] = mat.val[Matrix4.M21];
            val[M02] = mat.val[Matrix4.M02];
            val[M12] = mat.val[Matrix4.M12];
            val[M22] = mat.val[Matrix4.M22];
            return this;
        }

        public Matrix3 Set(float[] values)
        {
            JavaSystem.Arraycopy(values, 0, val, 0, val.Length);
            return this;
        }

        public Matrix3 Set(int x, int y, float v)
        {
            val[y + x * 3] = v;
            return this;
        }

        public float Get(int x, int y)
        {
            return val[y + x * 3];
        }

        public Matrix3 Izero()
        {
            val[M00] = 0;
            val[M01] = 0;
            val[M02] = 0;
            val[M10] = 0;
            val[M11] = 0;
            val[M12] = 0;
            val[M20] = 0;
            val[M21] = 0;
            val[M22] = 0;
            return this;
        }

        public Matrix3 Trn(Vector2f vector)
        {
            val[M02] += vector.x;
            val[M12] += vector.y;
            return this;
        }

        public Matrix3 Trn(float x, float y)
        {
            val[M02] += x;
            val[M12] += y;
            return this;
        }

        public Matrix3 Trn(Vector3f vector)
        {
            val[M02] += vector.x;
            val[M12] += vector.y;
            return this;
        }

        public Matrix3 Translate(float x, float y)
        {
            tmp[M00] = 1;
            tmp[M10] = 0;
            tmp[M20] = 0;

            tmp[M01] = 0;
            tmp[M11] = 1;
            tmp[M21] = 0;

            tmp[M02] = x;
            tmp[M12] = y;
            tmp[M22] = 1;
            Mul(val, tmp);
            return this;
        }

        public Matrix3 Translate(Vector2f translation)
        {
            tmp[M00] = 1;
            tmp[M10] = 0;
            tmp[M20] = 0;

            tmp[M01] = 0;
            tmp[M11] = 1;
            tmp[M21] = 0;

            tmp[M02] = translation.x;
            tmp[M12] = translation.y;
            tmp[M22] = 1;
            Mul(val, tmp);
            return this;
        }

        public Matrix3 Rotate(float degrees)
        {
            return RotateRad(MathUtils.DEG_TO_RAD * degrees);
        }

        public Matrix3 RotateRad(float radians)
        {
            if (radians == 0)
            {
                return this;
            }
            float Cos = MathUtils.Cos(radians);
            float Sin = MathUtils.Sin(radians);

            tmp[M00] = Cos;
            tmp[M10] = Sin;
            tmp[M20] = 0;

            tmp[M01] = -Sin;
            tmp[M11] = Cos;
            tmp[M21] = 0;

            tmp[M02] = 0;
            tmp[M12] = 0;
            tmp[M22] = 1;
            Mul(val, tmp);
            return this;
        }

        public Matrix3 Scale(float scaleX, float scaleY)
        {
            tmp[M00] = scaleX;
            tmp[M10] = 0;
            tmp[M20] = 0;
            tmp[M01] = 0;
            tmp[M11] = scaleY;
            tmp[M21] = 0;
            tmp[M02] = 0;
            tmp[M12] = 0;
            tmp[M22] = 1;
            Mul(val, tmp);
            return this;
        }

        public Matrix3 Scale(Vector2f scale)
        {
            tmp[M00] = scale.x;
            tmp[M10] = 0;
            tmp[M20] = 0;
            tmp[M01] = 0;
            tmp[M11] = scale.y;
            tmp[M21] = 0;
            tmp[M02] = 0;
            tmp[M12] = 0;
            tmp[M22] = 1;
            Mul(val, tmp);
            return this;
        }

        public float[] GetValues()
        {
            return val;
        }

        public Vector2f GetTranslation(Vector2f position)
        {
            position.x = val[M02];
            position.y = val[M12];
            return position;
        }

        public Vector2f GetScale(Vector2f scale)
        {
            scale.x = MathUtils.Sqrt(val[M00] * val[M00] + val[M01] * val[M01]);
            scale.y = MathUtils.Sqrt(val[M10] * val[M10] + val[M11] * val[M11]);
            return scale;
        }

        public float GetRotation()
        {
            return MathUtils.DEG_TO_RAD * MathUtils.Atan2(val[M10], val[M00]);
        }

        public float GetRotationRad()
        {
            return MathUtils.Atan2(val[M10], val[M00]);
        }

        public Matrix3 Scl(float scale)
        {
            val[M00] *= scale;
            val[M11] *= scale;
            return this;
        }

        public Matrix3 Scl(Vector2f scale)
        {
            val[M00] *= scale.x;
            val[M11] *= scale.y;
            return this;
        }

        public Matrix3 Scl(Vector3f scale)
        {
            val[M00] *= scale.x;
            val[M11] *= scale.y;
            return this;
        }

        public Matrix3 Transpose()
        {
            float v01 = val[M10];
            float v02 = val[M20];
            float v10 = val[M01];
            float v12 = val[M21];
            float v20 = val[M02];
            float v21 = val[M12];
            val[M01] = v01;
            val[M02] = v02;
            val[M10] = v10;
            val[M12] = v12;
            val[M20] = v20;
            val[M21] = v21;
            return this;
        }

        private static void Mul(float[] mata, float[] matb)
        {
            float v00 = mata[M00] * matb[M00] + mata[M01] * matb[M10] + mata[M02] * matb[M20];
            float v01 = mata[M00] * matb[M01] + mata[M01] * matb[M11] + mata[M02] * matb[M21];
            float v02 = mata[M00] * matb[M02] + mata[M01] * matb[M12] + mata[M02] * matb[M22];

            float v10 = mata[M10] * matb[M00] + mata[M11] * matb[M10] + mata[M12] * matb[M20];
            float v11 = mata[M10] * matb[M01] + mata[M11] * matb[M11] + mata[M12] * matb[M21];
            float v12 = mata[M10] * matb[M02] + mata[M11] * matb[M12] + mata[M12] * matb[M22];

            float v20 = mata[M20] * matb[M00] + mata[M21] * matb[M10] + mata[M22] * matb[M20];
            float v21 = mata[M20] * matb[M01] + mata[M21] * matb[M11] + mata[M22] * matb[M21];
            float v22 = mata[M20] * matb[M02] + mata[M21] * matb[M12] + mata[M22] * matb[M22];

            mata[M00] = v00;
            mata[M10] = v10;
            mata[M20] = v20;
            mata[M01] = v01;
            mata[M11] = v11;
            mata[M21] = v21;
            mata[M02] = v02;
            mata[M12] = v12;
            mata[M22] = v22;
        }

        public void Set(float x1, float y1, float x2, float y2)
        {
            Set(x1, y1, 1, x2, y2, 1);
        }

        public Matrix3(float a1, float a2, float a3, float b1, float b2, float b3)
        {
            Set(a1, a2, a3, b1, b2, b3);
        }

        public void Set(float a1, float a2, float a3, float b1, float b2, float b3)
        {
            Set(a1, a2, a3, b1, b2, b3, 0, 0, 1);
        }

        public void Set(float a1, float a2, float a3, float b1, float b2, float b3, float c1, float c2, float c3)
        {
            this.val = new float[] { a1, a2, a3, b1, b2, b3, c1, c2, c3 };
        }

        public void Transform(float[] source, int sourceOffset, float[] destination, int destOffset, int numberOfPoints)
        {

            float[] result = source == destination ? new float[numberOfPoints * 2] : destination;

            for (int i = 0; i < numberOfPoints * 2; i += 2)
            {
                for (int j = 0; j < 6; j += 3)
                {
                    result[i + (j / 3)] = source[i + sourceOffset] * this.val[j]
                            + source[i + sourceOffset + 1] * this.val[j + 1] + 1 * this.val[j + 2];
                }
            }

            if (source == destination)
            {
                for (int i = 0; i < numberOfPoints * 2; i += 2)
                {
                    destination[i + destOffset] = result[i];
                    destination[i + destOffset + 1] = result[i + 1];
                }
            }
        }

        public Matrix3 Concatenate(Matrix3 m)
        {
            float[] mp = new float[9];
            float n00 = this.val[0] * m.val[0] + this.val[1] * m.val[3];
            float n01 = this.val[0] * m.val[1] + this.val[1] * m.val[4];
            float n02 = this.val[0] * m.val[2] + this.val[1] * m.val[5] + this.val[2];
            float n10 = this.val[3] * m.val[0] + this.val[4] * m.val[3];
            float n11 = this.val[3] * m.val[1] + this.val[4] * m.val[4];
            float n12 = this.val[3] * m.val[2] + this.val[4] * m.val[5] + this.val[5];
            mp[0] = n00;
            mp[1] = n01;
            mp[2] = n02;
            mp[3] = n10;
            mp[4] = n11;
            mp[5] = n12;

            this.val = mp;
            return this;
        }

        public static Matrix3 CreateRotateTransform(float angle)
        {
            return new Matrix3(MathUtils.Cos(angle), -MathUtils.Sin(angle), 0, MathUtils.Sin(angle), MathUtils.Cos(angle),
                    0);
        }

        public static Matrix3 CreateRotateTransform(float angle, float x, float y)
        {
            Matrix3 temp = Matrix3.CreateRotateTransform(angle);
            float sinAngle = temp.val[3];
            float oneMinusCosAngle = 1.0f - temp.val[4];
            temp.val[2] = x * oneMinusCosAngle + y * sinAngle;
            temp.val[5] = y * oneMinusCosAngle - x * sinAngle;
            return temp;
        }

        public static Matrix3 CreateTranslateTransform(float xOffset, float yOffset)
        {
            return new Matrix3(1, 0, xOffset, 0, 1, yOffset);
        }

        public static Matrix3 CreateScaleTransform(float scalex, float scaley)
        {
            return new Matrix3(scalex, 0, 0, 0, scaley, 0);
        }

        public float Get(int i)
        {
            return this.val[i];
        }

        public Matrix3 From(float[] source, bool rowMajor)
        {
            Matrix3 m = new Matrix3();
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
                for (int j = 0; j < 3; j++)
                {
                    for (int i = 0; i < 3; i++)
                    {
                        m.Set(i, j, source[j * 3 + i]);
                    }
                }
            }
            return this;
        }

        public float GetTranslationX()
        {
            return this.val[M02];
        }

        public float GetTranslationY()
        {
            return this.val[M12];
        }

        public float GetScaleX()
        {
            return this.val[M00];
        }

        public float GetScaleY()
        {
            return this.val[M11];
        }

        public void Translation(float x, float y)
        {
            this.val[0] = 1;
            this.val[1] = 0;
            this.val[2] = 0;
            this.val[3] = 0;
            this.val[4] = 1;
            this.val[5] = 0;
            this.val[6] = x;
            this.val[7] = y;
            this.val[8] = 1;
        }
        public void Rotation(float angle)
        {
            angle = MathUtils.DEG_TO_RAD * angle;
            float Cos = MathUtils.Cos(angle);
            float Sin = MathUtils.Sin(angle);
            this.val[0] = Cos;
            this.val[1] = Sin;
            this.val[2] = 0;
            this.val[3] = -Sin;
            this.val[4] = Cos;
            this.val[5] = 0;
            this.val[6] = 0;
            this.val[7] = 0;
            this.val[8] = 1;
        }

        private readonly float[] result = new float[16];

        public float[] Get()
        {
            result[0] = this.val[0];
            result[1] = this.val[1];
            result[2] = this.val[2];
            result[3] = 0;
            result[4] = this.val[3];
            result[5] = this.val[4];
            result[6] = this.val[5];
            result[7] = 0;
            result[8] = 0;
            result[9] = 0;
            result[10] = 1;
            result[11] = 0;
            result[12] = this.val[6];
            result[13] = this.val[7];
            result[14] = 0;
            result[15] = this.val[8];
            return result;
        }

        public void RotationX(float angleX)
        {
            angleX = MathUtils.PI / 180 * angleX;
            Set(1f, 0f, 0f, 0f, MathUtils.Cos(angleX), -MathUtils.Sin(angleX), 0f, MathUtils.Sin(angleX),
                    MathUtils.Cos(angleX));
        }

        public void RotationY(float angleY)
        {
            angleY = MathUtils.PI / 180 * angleY;
            Set(MathUtils.Cos(angleY), 0f, MathUtils.Sin(angleY), 0f, 1f, 0f, -MathUtils.Sin(angleY), 0f,
                    MathUtils.Cos(angleY));
        }

        public void RotationZ(float angleZ)
        {
            angleZ = MathUtils.PI / 180 * angleZ;
            Set(MathUtils.Cos(angleZ), -MathUtils.Sin(angleZ), 0f, MathUtils.Sin(angleZ), MathUtils.Cos(angleZ), 0f, 0f, 0f,
                    1f);
        }

        public bool IsIdt()
        {
            return (this.val[0] == 1 && this.val[1] == 0 && this.val[2] == 0)
                    && (this.val[3] == 0 && this.val[4] == 1 && this.val[5] == 0)
                    && (this.val[6] == 0 && this.val[7] == 0 && this.val[8] == 1);
        }

        private static float Detd(float a, float b, float c, float d)
        {
            return (a * d) - (b * c);
        }

        public void Adj()
        {

            float a11 = this.val[0];
            float a12 = this.val[1];
            float a13 = this.val[2];

            float a21 = this.val[3];
            float a22 = this.val[4];
            float a23 = this.val[5];

            float a31 = this.val[6];
            float a32 = this.val[7];
            float a33 = this.val[8];

            this.val[0] = Detd(a22, a23, a32, a33);
            this.val[1] = Detd(a13, a12, a33, a32);
            this.val[2] = Detd(a12, a13, a22, a23);

            this.val[3] = Detd(a23, a21, a33, a31);
            this.val[4] = Detd(a11, a13, a31, a33);
            this.val[5] = Detd(a13, a11, a23, a21);

            this.val[6] = Detd(a21, a22, a31, a32);
            this.val[7] = Detd(a12, a11, a32, a31);
            this.val[8] = Detd(a11, a12, a21, a22);
        }

        public void Add(Matrix3 m)
        {
            float a1 = this.val[0];
            float a2 = this.val[1];
            float a3 = this.val[2];

            float b1 = this.val[3];
            float b2 = this.val[4];
            float b3 = this.val[5];

            float c1 = this.val[6];
            float c2 = this.val[7];
            float c3 = this.val[8];

            a1 += m.val[0];
            a2 += m.val[1];
            a3 += m.val[2];

            b1 += m.val[3];
            b2 += m.val[4];
            b3 += m.val[5];

            c1 += m.val[6];
            c2 += m.val[7];
            c3 += m.val[8];

            this.val[0] = a1;
            this.val[1] = a2;
            this.val[2] = a3;
            this.val[3] = b1;
            this.val[4] = b2;
            this.val[5] = b3;
            this.val[6] = c1;
            this.val[7] = c2;
            this.val[8] = c3;
        }

        public Matrix3 AddEqual(Matrix3 m)
        {
            Matrix3 newMatrix = new Matrix3(this.val);
            newMatrix.Add(m);
            return newMatrix;
        }

        public void Mul(float c)
        {
            float a1 = this.val[0];
            float a2 = this.val[1];
            float a3 = this.val[2];

            float b1 = this.val[3];
            float b2 = this.val[4];
            float b3 = this.val[5];

            float c1 = this.val[6];
            float c2 = this.val[7];
            float c3 = this.val[8];

            this.val[0] = a1 * c;
            this.val[1] = a2 * c;
            this.val[2] = a3 * c;
            this.val[3] = b1 * c;
            this.val[4] = b2 * c;
            this.val[5] = b3 * c;
            this.val[6] = c1 * c;
            this.val[7] = c2 * c;
            this.val[8] = c3 * c;
        }

        public Matrix3 MulEqual(Matrix3 m)
        {
            if (m == null)
            {
                m = new Matrix3();
            }
            Matrix3 result = new Matrix3(this.val);
            result.Mul(m);
            return result;
        }

        public Matrix3 Invert(Matrix3 m)
        {
            Matrix3 result = m;
            if (result == null)
            {
                result = new Matrix3();
            }

            float det = Det();
            if (MathUtils.Abs(det) <= MathUtils.EPSILON)
            {
                throw new LSysException("This matrix cannot be inverted !");
            }

            float temp00 = this.val[4] * this.val[8] - this.val[5] * this.val[7];
            float temp01 = this.val[2] * this.val[7] - this.val[1] * this.val[8];
            float temp02 = this.val[1] * this.val[5] - this.val[2] * this.val[4];
            float temp10 = this.val[5] * this.val[6] - this.val[3] * this.val[8];
            float temp11 = this.val[0] * this.val[8] - this.val[2] * this.val[6];
            float temp12 = this.val[2] * this.val[3] - this.val[0] * this.val[5];
            float temp20 = this.val[3] * this.val[7] - this.val[4] * this.val[6];
            float temp21 = this.val[1] * this.val[6] - this.val[0] * this.val[7];
            float temp22 = this.val[0] * this.val[4] - this.val[1] * this.val[3];
            result.Set(temp00, temp01, temp02, temp10, temp11, temp12, temp20, temp21, temp22);
            result.Mul(1.0f / det);
            return result;
        }

        public bool IsFloatValid()
        {

            bool valid = true;

            valid &= !Float.IsNaN(this.val[0]);
            valid &= !Float.IsNaN(this.val[1]);
            valid &= !Float.IsNaN(this.val[2]);

            valid &= !Float.IsNaN(this.val[3]);
            valid &= !Float.IsNaN(this.val[4]);
            valid &= !Float.IsNaN(this.val[5]);

            valid &= !Float.IsNaN(this.val[6]);
            valid &= !Float.IsNaN(this.val[7]);
            valid &= !Float.IsNaN(this.val[8]);

            return valid;
        }

        public static Matrix3 Avg(ICollection<Matrix3> Set)
        {
            Matrix3 average = new Matrix3();
			average.Set(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f);
			float hist = 0;
			foreach (Matrix3 matrix3d in Set)
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

        public void Cpy(Matrix3 m)
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

        public Vector2f Transform(Vector2f pt)
        {
            float[] ins = new float[] { pt.x, pt.y };
            float[] outs = new float[2];

            Transform(ins, 0, outs, 0, 1);

            return new Vector2f(outs[0], outs[1]);
        }

        public Matrix3 Cpy()
        {
            return new Matrix3(this.val);
        }

        public void Set(int i, float value)
        {
            this.val[i] = value;
        }

        public static void Add(Matrix3 result, Matrix3 m1, Matrix3 m2)
        {
            for (int i = 0; i < 9; i++)
            {
                result.Set(i, m1.Get(i) + m2.Get(i));
            }
        }

        public static void Sub(Matrix3 result, Matrix3 m1, Matrix3 m2)
        {
            for (int i = 0; i < 9; i++)
            {
                result.Set(i, m1.Get(i) - m2.Get(i));
            }
        }

        public static void Mul(Matrix3 result, Matrix3 m1, Matrix3 m2)
        {
            for (int i = 0; i < 3; i++)
            {
                for (int j = 0; j < 3; j++)
                {
                    result.Set(i, j,
                            m1.Get(i, 0) * m2.Get(0, j) + m1.Get(i, 1) * m2.Get(1, j) + m1.Get(i, 2) * m2.Get(2, j));
                }
            }
        }

        public static void Mul(float[] result, Matrix3 m, float[] v)
        {
            float a, b, c;
            a = m.Get(0, 0) * v[0] + m.Get(1, 0) * v[1] + m.Get(2, 0) * v[2];
            b = m.Get(0, 1) * v[0] + m.Get(1, 1) * v[1] + m.Get(2, 1) * v[2];
            c = m.Get(0, 2) * v[0] + m.Get(1, 2) * v[1] + m.Get(2, 2) * v[2];
            result[0] = a;
            result[1] = b;
            result[2] = c;
        }

        public static Matrix3 GetRotationMatrixExact(float ax, float ay, float az)
        {
            float cosax = MathUtils.Cos(MathUtils.ToRadians(ax));
            float sinax = MathUtils.Sin(MathUtils.ToRadians(ax));
            float cosay = MathUtils.Cos(MathUtils.ToRadians(ay));
            float sinay = MathUtils.Sin(MathUtils.ToRadians(ay));
            float cosaz = MathUtils.Cos(MathUtils.ToRadians(az));
            float sinaz = MathUtils.Sin(MathUtils.ToRadians(az));
            float[] tx = { 1, 0, 0, 0, cosax, -sinax, 0, sinax, cosax };
            float[] ty = { cosay, 0, sinay, 0, 1f, 0f, -sinay, 0, cosay };
            float[] tz = { cosaz, -sinaz, 0, sinaz, cosaz, 0, 0, 0, 1 };
            Matrix3 Rx = new Matrix3(tx);
            Matrix3 Ry = new Matrix3(ty);
            Matrix3 Rz = new Matrix3(tz);
            Matrix3 result = new Matrix3();
            Matrix3 tmpresult = new Matrix3();
            Matrix3.Mul(tmpresult, Rx, Ry);
            Matrix3.Mul(result, tmpresult, Rz);
            return result;
        }

        public static float Distance2d(float x1, float y1, float x2, float y2)
        {
            return Distance(x1, y1, 0f, x2, y2, 0f);
        }

        public static float Distance(float x1, float y1, float z1, float x2, float y2, float z2)
        {
            return MathUtils.Sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1) + (z2 - z1) * (z2 - z1));
        }

        public static bool IsOnTriange(float x1, float y1, float x2, float y2, float x3, float y3, float x, float y)
        {
            float a;
            float b;
            bool s;
            bool s2;

            if (x2 - x1 != 0f)
            {
                a = (y2 - y1) / (x2 - x1);
                b = y1 - a * x1;

                if (a * x3 + b > y3)
                {
                    s = true;
                }
                else
                {
                    s = false;
                }
                if (a * x + b > y)
                {
                    s2 = true;
                }
                else
                {
                    s2 = false;
                }

                if ((s != s2) && (a * x + b != y))
                {
                    return false;
                }

            }
            else
            {
                if (x1 > x3)
                {
                    s = true;
                }
                else
                {
                    s = false;
                }
                if (x1 > x)
                {
                    s2 = true;
                }
                else
                {
                    s2 = false;
                }
                if ((s != s2) && (x1 != x))
                {
                    return false;
                }
            }

            if (x3 - x2 != 0f)
            {
                a = (y3 - y2) / (x3 - x2);
                b = y2 - a * x2;

                if (a * x1 + b > y1)
                {
                    s = true;
                }
                else
                {
                    s = false;
                }
                if (a * x + b > y)
                {
                    s2 = true;
                }
                else
                {
                    s2 = false;
                }
                if ((s != s2) && (a * x + b != y))
                {
                    return false;
                }

            }
            else
            {
                if (x2 > x1)
                {
                    s = true;
                }
                else
                {
                    s = false;
                }
                if (x2 > x)
                {
                    s2 = true;
                }
                else
                {
                    s2 = false;
                }
                if ((s != s2) && (x1 != x))
                {
                    return false;
                }
            }

            if (x1 - x3 != 0f)
            {
                a = (y1 - y3) / (x1 - x3);
                b = y3 - a * x3;

                if (a * x2 + b > y2)
                {
                    s = true;
                }
                else
                {
                    s = false;
                }

                if (a * x + b > y)
                {
                    s2 = true;
                }
                else
                {
                    s2 = false;
                }

                if ((s != s2) && (a * x + b != y))
                {
                    return false;
                }

            }
            else
            {
                if (x1 > x2)
                {
                    s = true;
                }
                else
                {
                    s = false;
                }

                if (x1 > x)
                {
                    s2 = true;
                }
                else
                {
                    s2 = false;
                }

                if ((s != s2) && (x1 != x))
                {
                    return false;
                }
            }
            return true;

        }

        public static float[] Convert33to44(float[] m33, int offset)
        {
            float[] m44 = new float[16];

            m44[0] = m33[0 + offset];
            m44[1] = m33[1 + offset];
            m44[2] = m33[2 + offset];

            m44[4] = m33[3 + offset];
            m44[5] = m33[4 + offset];
            m44[6] = m33[5 + offset];

            m44[8] = m33[6 + offset];
            m44[9] = m33[7 + offset];
            m44[10] = m33[8 + offset];

            m44[15] = 1.0f;
            return m44;
        }

        public static void SetRotateEulerM(float[] rm, int rmOffset, float x, float y, float z)
        {
            x *= 0.01745329f;
            y *= 0.01745329f;
            z *= 0.01745329f;
            float sx = MathUtils.Sin(x);
            float sy = MathUtils.Sin(y);
            float sz = MathUtils.Sin(z);
            float cx = MathUtils.Cos(x);
            float cy = MathUtils.Cos(y);
            float cz = MathUtils.Cos(z);
            float cxsy = cx * sy;
            float sxsy = sx * sy;

            rm[rmOffset + 0] = cy * cz;
            rm[rmOffset + 1] = -cy * sz;
            rm[rmOffset + 2] = sy;
            rm[rmOffset + 3] = 0.0f;

            rm[rmOffset + 4] = sxsy * cz + cx * sz;
            rm[rmOffset + 5] = -sxsy * sz + cx * cz;
            rm[rmOffset + 6] = -sx * cy;
            rm[rmOffset + 7] = 0.0f;

            rm[rmOffset + 8] = -cxsy * cz + sx * sz;
            rm[rmOffset + 9] = cxsy * sz + sx * cz;
            rm[rmOffset + 10] = cx * cy;
            rm[rmOffset + 11] = 0.0f;

            rm[rmOffset + 12] = 0.0f;
            rm[rmOffset + 13] = 0.0f;
            rm[rmOffset + 14] = 0.0f;
            rm[rmOffset + 15] = 1.0f;
        }


        public override bool Equals(object o)
        {
            if (!(o is Matrix3) || o == null)
            {
                return false;
            }
            if (this == o)
            {
                return true;
            }
            Matrix3 comp = (Matrix3)o;
            for (int i = 0; i < 9; i++)
            {
                if (NumberUtils.Compare(this.val[i], comp.val[i]) != 0)
                {
                    return false;
                }
            }
            return true;
        }

        public float GetX(float x, float y)
        {
            return x * val[M00] + y * val[M01] + val[M02];
        }

        public float GetY(float x, float y)
        {
            return x * val[M10] + y * val[M11] + val[M12];
        }




        public override int GetHashCode()
        {
            int result = 17;
            for (int j = 0; j < 9; j++)
            {
                long val = NumberUtils.FloatToIntBits(this.val[j]);
                result += 31 * result + (int)(val ^ (int)((uint)val >> 32));
            }
            return result;
        }
        public float GetX()
        {
            return val[M02];
        }
        public float GetY()
        {
            return val[M12];
        }
    }
}
