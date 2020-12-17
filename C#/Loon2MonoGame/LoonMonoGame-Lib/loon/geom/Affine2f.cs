using loon.utils;

namespace loon.geom
{
    public class Affine2f : LTrans
    {

        public static Affine2f Transform(Affine2f tx, float x, float y, int Transform)
        {
            switch (Transform)
            {
                case TRANS_ROT90:
                    {
                        tx.Translate(x, y);
                        tx.Rotate(ANGLE_90);
                        tx.Translate(-x, -y);
                        break;
                    }
                case TRANS_ROT180:
                    {
                        tx.Translate(x, y);
                        tx.Rotate(MathUtils.PI);
                        tx.Translate(-x, -y);
                        break;
                    }
                case TRANS_ROT270:
                    {
                        tx.Translate(x, y);
                        tx.Rotate(ANGLE_270);
                        tx.Translate(-x, -y);
                        break;
                    }
                case TRANS_MIRROR:
                    {
                        tx.Translate(x, y);
                        tx.Scale(-1, 1);
                        tx.Translate(-x, -y);
                        break;
                    }
                case TRANS_MIRROR_ROT90:
                    {
                        tx.Translate(x, y);
                        tx.Rotate(ANGLE_90);
                        tx.Translate(-x, -y);
                        tx.Scale(-1, 1);
                        break;
                    }
                case TRANS_MIRROR_ROT180:
                    {
                        tx.Translate(x, y);
                        tx.Scale(-1, 1);
                        tx.Translate(-x, -y);
                        tx.Translate(x, y);
                        tx.Rotate(MathUtils.PI);
                        tx.Translate(-x, -y);
                        break;
                    }
                case TRANS_MIRROR_ROT270:
                    {
                        tx.Translate(x, y);
                        tx.Rotate(ANGLE_270);
                        tx.Translate(-x, -y);
                        tx.Scale(-1, 1);
                        break;
                    }
            }
            return tx;
        }

        public static Affine2f Transform(Affine2f tx, float x, float y, int Transform, float width, float height)
        {
            switch (Transform)
            {
                case TRANS_ROT90:
                    {
                        float w = x + width / 2;
                        float h = y + height / 2;
                        tx.Translate(w, h);
                        tx.Rotate(ANGLE_90);
                        tx.Translate(-w, -h);
                        break;
                    }
                case TRANS_ROT180:
                    {
                        float w = x + width / 2;
                        float h = y + height / 2;
                        tx.Translate(w, h);
                        tx.Rotate(MathUtils.PI);
                        tx.Translate(-w, -h);
                        break;
                    }
                case TRANS_ROT270:
                    {
                        float w = x + width / 2;
                        float h = y + height / 2;
                        tx.Translate(w, h);
                        tx.Rotate(ANGLE_270);
                        tx.Translate(-w, -h);
                        break;
                    }
                case TRANS_MIRROR:
                    {
                        float w = x + width / 2;
                        float h = y + height / 2;
                        tx.Translate(w, h);
                        tx.Scale(-1, 1);
                        tx.Translate(-w, -h);
                        break;
                    }
                case TRANS_MIRROR_ROT90:
                    {
                        float w = x + width / 2;
                        float h = y + height / 2;
                        tx.Translate(w, h);
                        tx.Rotate(ANGLE_90);
                        tx.Translate(-w, -h);
                        tx.Scale(-1, 1);
                        break;
                    }
                case TRANS_MIRROR_ROT180:
                    {
                        float w = x + width / 2;
                        float h = y + height / 2;
                        tx.Translate(w, h);
                        tx.Scale(-1, 1);
                        tx.Translate(-w, -h);
                        w = x + width / 2;
                        h = y + height / 2;
                        tx.Translate(w, h);
                        tx.Rotate(MathUtils.PI);
                        tx.Translate(-w, -h);
                        break;
                    }
                case TRANS_MIRROR_ROT270:
                    {
                        float w = x + width / 2;
                        float h = y + height / 2;
                        tx.Translate(w, h);
                        tx.Rotate(ANGLE_270);
                        tx.Translate(-w, -h);
                        tx.Scale(-1, 1);
                        break;
                    }
            }
            return tx;
        }

        public static Affine2f TransformRegion(Affine2f tx, float x, float y, int Transform, float width,
                float height)
        {
            switch (Transform)
            {
                case TRANS_ROT90:
                    {
                        float w = height;
                        float h = y;
                        tx.Translate(w, h);
                        tx.Rotate(ANGLE_90);
                        tx.Translate(-w, -h);
                        break;
                    }
                case TRANS_ROT180:
                    {
                        float w = x + width;
                        float h = y + height;
                        tx.Translate(w, h);
                        tx.Rotate(MathUtils.PI);
                        tx.Translate(-w, -h);
                        break;
                    }
                case TRANS_ROT270:
                    {
                        float w = x;
                        float h = y + width;
                        tx.Translate(w, h);
                        tx.Rotate(ANGLE_270);
                        tx.Translate(-w, -h);
                        break;
                    }
                case TRANS_MIRROR:
                    {
                        float w = x + width;
                        float h = y;
                        tx.Translate(w, h);
                        tx.Scale(-1, 1);
                        tx.Translate(-w, -h);
                        break;
                    }
                case TRANS_MIRROR_ROT90:
                    {
                        float w = x + height;
                        float h = y;
                        tx.Translate(w, h);
                        tx.Rotate(ANGLE_90);
                        tx.Translate(-w, -h);
                        tx.Scale(-1, 1);
                        break;
                    }
                case TRANS_MIRROR_ROT180:
                    {
                        float w = x + width;
                        float h = y;
                        tx.Translate(w, h);
                        tx.Scale(-1, 1);
                        tx.Translate(-w, -h);
                        w = x + width;
                        h = y + height;
                        tx.Translate(w, h);
                        tx.Rotate(MathUtils.PI);
                        tx.Translate(-w, -h);
                        break;
                    }
                case TRANS_MIRROR_ROT270:
                    {
                        tx.Rotate(ANGLE_270);
                        tx.Scale(-1, 1);
                        break;
                    }
            }
            return tx;
        }

        public static Affine2f Multiply(Affine2f a, Affine2f b, Affine2f into)
        {
            return Multiply(a.m00, a.m01, a.m10, a.m11, a.tx, a.ty, b.m00, b.m01, b.m10, b.m11, b.tx, b.ty, into);
        }

        public static Affine2f Multiply(Affine2f a, float m00, float m01, float m10, float m11, float tx, float ty,
                Affine2f into)
        {
            return Multiply(a.m00, a.m01, a.m10, a.m11, a.tx, a.ty, m00, m01, m10, m11, tx, ty, into);
        }

        public static Affine2f Multiply(float m00, float m01, float m10, float m11, float tx, float ty, Affine2f b,
                Affine2f into)
        {
            return Multiply(m00, m01, m10, m11, tx, ty, b.m00, b.m01, b.m10, b.m11, b.tx, b.ty, into);
        }

        public static Affine2f Multiply(float am00, float am01, float am10, float am11, float atx, float aty,
                float bm00, float bm01, float bm10, float bm11, float btx, float bty, Affine2f into)
        {
            into.SetTransform(am00 * bm00 + am10 * bm01, am01 * bm00 + am11 * bm01, am00 * bm10 + am10 * bm11,
                    am01 * bm10 + am11 * bm11, am00 * btx + am10 * bty + atx, am01 * btx + am11 * bty + aty);
            return into;
        }

        /* default generality */
        protected int GENERALITY = 4;
        /* x scale */
        public float m00 = 1.0f;
        /* y skew */
        public float m01 = 0.0f;
        /* x skew */
        public float m10 = 0.0f;
        /* y scale */
        public float m11 = 1.0f;
        /* x translation */
        public float tx = 0.0f;
        /* y translation */
        public float ty = 0.0f;
        /* convert Affine to Matrix3 */
        private float[] matrix3f = new float[9];
        /* one 4x4 matrix temp object */
        private Matrix4 projectionMatrix = null;

        protected Affine2f(Affine2f other) : this(other.ScaleX(), other.ScaleY(), other.Rotation(), other.Tx(), other.Ty())
        {

        }

        public Affine2f() : this(1, 0, 0, 1, 0, 0)
        {

        }

        public Affine2f(float scale, float angle, float tx, float ty) : this(scale, scale, angle, tx, ty)
        {

        }

        public Affine2f(float scaleX, float scaleY, float angle, float tx, float ty)
        {
            float sina = MathUtils.Sin(angle), cosa = MathUtils.Cos(angle);
            this.m00 = cosa * scaleX;
            this.m01 = sina * scaleY;
            this.m10 = -sina * scaleX;
            this.m11 = cosa * scaleY;
            this.tx = tx;
            this.ty = ty;
        }

        public Affine2f(float m00, float m01, float m10, float m11, float tx, float ty)
        {
            this.m00 = m00;
            this.m01 = m01;
            this.m10 = m10;
            this.m11 = m11;
            this.tx = tx;
            this.ty = ty;
        }

        public Affine2f Combined(Affine2f aff)
        {

            float a = aff.m00 * this.m00 + aff.m01 * this.m10;
            float b = aff.m00 * this.m11 + aff.m11 * this.m10;
            float tx = aff.m00 * this.m01 + aff.m01 * this.m11;
            float c = aff.m00 * this.tx + aff.m01 * this.ty + aff.tx;
            float d = aff.m10 * this.tx + aff.m11 * this.ty + aff.ty;

            this.m00 = a;
            this.m01 = b;
            this.tx = tx;
            this.m10 = c;
            this.m11 = d;
            return this;
        }

        public Affine2f Combined(Matrix4 mat)
        {
            float[] m = mat.val;

            float m00 = m[Matrix4.M00] * this.m00 + m[Matrix4.M01] * this.m10;
            float m01 = m[Matrix4.M00] * this.m11 + m[Matrix4.M11] * this.m10;
            float tx = m[Matrix4.M00] * this.m01 + m[Matrix4.M01] * this.m11;
            float m10 = m[Matrix4.M00] * this.tx + m[Matrix4.M01] * this.ty + m[Matrix4.M02];
            float m11 = m[Matrix4.M10] * this.tx + m[Matrix4.M11] * this.ty + m[Matrix4.M12];

            m[Matrix4.M00] = m00;
            m[Matrix4.M01] = m01;
            m[Matrix4.M03] = tx;
            m[Matrix4.M10] = m10;
            m[Matrix4.M11] = m11;
            m[Matrix4.M13] = ty;

            return this;
        }

        public Affine2f Combined4x4(float[] vals)
        {
            float[] m = vals;

            float m00 = m[Matrix4.M00] * this.m00 + m[Matrix4.M01] * this.m10;
            float m01 = m[Matrix4.M00] * this.m11 + m[Matrix4.M11] * this.m10;
            float tx = m[Matrix4.M00] * this.m01 + m[Matrix4.M01] * this.m11;
            float m10 = m[Matrix4.M00] * this.tx + m[Matrix4.M01] * this.ty + m[Matrix4.M02];
            float m11 = m[Matrix4.M10] * this.tx + m[Matrix4.M11] * this.ty + m[Matrix4.M12];

            m[Matrix4.M00] = m00;
            m[Matrix4.M01] = m01;
            m[Matrix4.M03] = tx;
            m[Matrix4.M10] = m10;
            m[Matrix4.M11] = m11;
            m[Matrix4.M13] = ty;
            return this;
        }
        public Affine2f Idt()
        {
            this.m00 = 1;
            this.m01 = 0;
            this.tx = 0;
            this.m10 = 0;
            this.m11 = 1;
            this.ty = 0;
            return this;
        }
        public Affine2f Reset()
        {
            return this.Idt();
        }
        public bool CheckBaseTransform()
        {
            return (m00 != 1 || m01 != 0 || m10 != 0 || m11 != 1 || tx != 0 || ty != 0);
        }


        public override bool Equals(object o)
        {
            if (null == o)
            {
                return false;
            }
            if (o == this)
            {
                return true;
            }
            if (o is Affine2f)
            {
                Affine2f a2f = (Affine2f)o;
                if (a2f.m00 == m00 && a2f.m01 == m01 && a2f.tx == tx && a2f.ty == ty && a2f.m10 == m10 && a2f.m11 == m11)
                {
                    return true;
                }
            }
            return false;
        }

        public bool Equals(Affine2f a2f)
        {
            if (a2f == null)
            {
                return false;
            }
            if (a2f == this)
            {
                return true;
            }
            return a2f.m00 == m00 && a2f.m01 == m01 && a2f.tx == tx && a2f.ty == ty && a2f.m10 == m10 && a2f.m11 == m11;
        }
        public Affine2f Set(Matrix3 matrix)
        {
            float[] other = matrix.val;
            m00 = other[Matrix3.M00];
            m01 = other[Matrix3.M01];
            tx = other[Matrix3.M02];
            m10 = other[Matrix3.M10];
            m11 = other[Matrix3.M11];
            ty = other[Matrix3.M12];
            return this;
        }
        public Affine2f SetValue3x3(float[] vals)
        {
            m00 = vals[Matrix3.M00];
            m01 = vals[Matrix3.M01];
            tx = vals[Matrix3.M02];
            m10 = vals[Matrix3.M10];
            m11 = vals[Matrix3.M11];
            ty = vals[Matrix3.M12];
            return this;
        }

        public Affine2f Set(Matrix4 matrix)
        {
            float[] other = matrix.val;
            m00 = other[Matrix4.M00];
            m01 = other[Matrix4.M01];
            tx = other[Matrix4.M03];
            m10 = other[Matrix4.M10];
            m11 = other[Matrix4.M11];
            ty = other[Matrix4.M13];
            return this;
        }
        public Affine2f setValue4x4(float[] vals)
        {
            m00 = vals[Matrix4.M00];
            m01 = vals[Matrix4.M01];
            tx = vals[Matrix4.M03];
            m10 = vals[Matrix4.M10];
            m11 = vals[Matrix4.M11];
            ty = vals[Matrix4.M13];
            return this;
        }

        public Affine2f SetThis(Affine2f aff)
        {
            this.m00 = aff.m00;
            this.m11 = aff.m11;
            this.m01 = aff.m01;
            this.m10 = aff.m10;
            this.tx = aff.tx;
            this.ty = aff.ty;
            return this;
        }

        public Affine2f Set(Affine2f other)
        {
            return SetTransform(other.m00, other.m01, other.m10, other.m11, other.tx, other.ty);
        }

        public float UniformScale()
        {
            float cp = m00 * m11 - m01 * m10;
            return (cp < 0f) ? -MathUtils.Sqrt(-cp) : MathUtils.Sqrt(cp);
        }
        public float ScaleX()
        {
            return m01 == 0 ? m00 : MathUtils.Sqrt(m00 * m00 + m01 * m01);
        }
        public float ScaleY()
        {
            return m10 == 0 ? m11 : MathUtils.Sqrt(m10 * m10 + m11 * m11);
        }

        public float Rotation()
        {
            float n00 = m00, n10 = m10;
            float n01 = m01, n11 = m11;
            for (int ii = 0; ii < 10; ii++)
            {
                float o00 = n00, o10 = n10;
                float o01 = n01, o11 = n11;
                float det = o00 * o11 - o10 * o01;
                if (MathUtils.Abs(det) == 0f)
                {
                    throw new LSysException("Affine2f exception " + this.ToString());
                }
                float hrdet = 0.5f / det;
                n00 = +o11 * hrdet + o00 * 0.5f;
                n10 = -o01 * hrdet + o10 * 0.5f;

                n01 = -o10 * hrdet + o01 * 0.5f;
                n11 = +o00 * hrdet + o11 * 0.5f;

                float d00 = n00 - o00, d10 = n10 - o10;
                float d01 = n01 - o01, d11 = n11 - o11;
                if (d00 * d00 + d10 * d10 + d01 * d01 + d11 * d11 < MathUtils.EPSILON)
                {
                    break;
                }
            }
            return MathUtils.Atan2(n01, n00);
        }

        public float GetAngle()
        {
            return MathUtils.ToRadians(Rotation());
        }

        public float Tx()
        {
            return this.tx;
        }

        public float Ty()
        {
            return this.ty;
        }

        public float[] GetMartix3f()
        {
            matrix3f[0] = m00;
            matrix3f[1] = m10;
            matrix3f[2] = 0;
            matrix3f[3] = m01;
            matrix3f[4] = m11;
            matrix3f[5] = 0;
            matrix3f[6] = tx;
            matrix3f[7] = ty;
            matrix3f[8] = 1;
            return matrix3f;
        }

        public float[] Get(float[] matrix)
        {
            if (matrix == null)
            {
                return GetMartix3f();
            }
            int len = matrix.Length;
            if (len == 6)
            {
                matrix[0] = m00;
                matrix[1] = m01;
                matrix[2] = m10;
                matrix[3] = m11;
                matrix[4] = tx;
                matrix[5] = ty;
            }
            else if (len == 9)
            {
                matrix[0] = m00;
                matrix[1] = m10;
                matrix[3] = m01;
                matrix[4] = m11;
                matrix[6] = tx;
                matrix[7] = ty;
            }
            else if (len == 16)
            {
                matrix[0] = m00;
                matrix[1] = m10;
                matrix[4] = m01;
                matrix[5] = m11;
                matrix[12] = tx;
                matrix[13] = ty;
            }
            return matrix;
        }

        public Affine2f SetUniformScale(float scale)
        {
            return SetScale(scale, scale);
        }

        public Affine2f SetScaleX(float scaleX)
        {
            // 计算新的X轴缩放
            float mult = scaleX / ScaleX();
            m00 *= mult;
            m01 *= mult;
            return this;
        }

        public Affine2f SetScaleY(float scaleY)
        {
            // 计算新的Y轴缩放
            float mult = scaleY / ScaleY();
            m10 *= mult;
            m11 *= mult;
            return this;
        }

        public Affine2f SetRotation(float angle)
        {
            // 提取比例，然后重新应用旋转和缩放在一起
            float sx = ScaleX(), sy = ScaleY();
            float sina = MathUtils.Sin(angle), cosa = MathUtils.Cos(angle);
            m00 = cosa * sx;
            m01 = sina * sx;
            m10 = -sina * sy;
            m11 = cosa * sy;
            return this;
        }

        public Affine2f Skew(float x, float y)
        {
            float tanX = MathUtils.Tan(x);
            float tanY = MathUtils.Tan(y);
            float a1 = m00;
            float b1 = m01;
            m00 += tanY * m10;
            m01 += tanY * m11;
            m10 += tanX * a1;
            m11 += tanX * b1;
            return this;
        }

        public float GetX(float x, float y)
        {
            return x * this.m00 + y * this.m01 + this.tx;
        }

        public float GetY(float x, float y)
        {
            return x * this.m10 + y * this.m11 + this.ty;
        }

        public Affine2f Rotate(float angle)
        {
            float sina = MathUtils.Sin(angle), cosa = MathUtils.Cos(angle);
            return Multiply(this, cosa, sina, -sina, cosa, 0, 0, this);
        }

        public Affine2f Rotate(float angle, float x, float y)
        {
            float sina = MathUtils.Sin(angle), cosa = MathUtils.Cos(angle);
            return Multiply(this, cosa, sina, -sina, cosa, x, y, this);
        }

        public Affine2f PreRotate(float angle)
        {
            float angleRad = MathUtils.DEG_TO_RAD * angle;
            float sin = MathUtils.Sin(angleRad);
            float cos = MathUtils.Cos(angleRad);
            float m00 = this.m00;
            float m01 = this.m01;
            float m10 = this.m10;
            float m11 = this.m11;
            this.m00 = cos * m00 + sin * m10;
            this.m01 = cos * m01 + sin * m11;
            this.m10 = cos * m10 - sin * m00;
            this.m11 = cos * m11 - sin * m01;
            return this;
        }

        public Affine2f PostRotate(float angle)
        {
            float angleRad = MathUtils.DEG_TO_RAD * angle;

            float sin = MathUtils.Sin(angleRad);
            float cos = MathUtils.Cos(angleRad);

            float m00 = this.m00;
            float m01 = this.m01;
            float m10 = this.m10;
            float m11 = this.m11;
            float tx = this.tx;
            float ty = this.ty;

            this.m00 = m00 * cos - m01 * sin;
            this.m01 = m00 * sin + m01 * cos;
            this.m10 = m10 * cos - m11 * sin;
            this.m11 = m10 * sin + m11 * cos;
            this.tx = tx * cos - ty * sin;
            this.ty = tx * sin + ty * cos;
            return this;
        }

        public Affine2f SetToRotate(float angle)
        {
            float angleRad = MathUtils.DEG_TO_RAD * angle;

            float sin = MathUtils.Sin(angleRad);
            float cos = MathUtils.Cos(angleRad);

            this.m00 = cos;
            this.m01 = sin;
            this.m10 = -sin;
            this.m11 = cos;
            this.tx = 0.0f;
            this.ty = 0.0f;

            return this;
        }

        public Affine2f SetTranslation(float tx, float ty)
        {
            this.tx = tx;
            this.ty = ty;
            return this;
        }

        public Affine2f SetTx(float tx)
        {
            this.tx = tx;
            return this;
        }

        public Affine2f SetTy(float ty)
        {
            this.ty = ty;
            return this;
        }

        public Affine2f SetTo(float m00, float m01, float m10, float m11, float tx, float ty)
        {
            return SetTransform(m00, m01, m10, m11, tx, ty);
        }

        public Affine2f SetTransform(float m00, float m01, float m10, float m11, float tx, float ty)
        {
            this.m00 = m00;
            this.m01 = m01;
            this.m10 = m10;
            this.m11 = m11;
            this.tx = tx;
            this.ty = ty;
            return this;
        }

        public Affine2f UniformScale(float scale)
        {
            return Scale(scale, scale);
        }

        public Affine2f PreScale(float sx, float sy)
        {
            return Scale(sx, sy);
        }

        public Affine2f PostScale(float sx, float sy)
        {
            this.m00 = this.m00 * sx;
            this.m01 = this.m01 * sy;
            this.m10 = this.m10 * sx;
            this.m11 = this.m11 * sy;
            this.tx = this.tx * sx;
            this.ty = this.ty * sy;
            return this;
        }

        public Affine2f SetToScale(float sx, float sy)
        {
            this.m00 = sx;
            this.m01 = 0.0f;
            this.m10 = 0.0f;
            this.m11 = sy;
            this.tx = 0.0f;
            this.ty = 0.0f;
            return this;
        }

        public Affine2f ScaleAll(float scaleX, float scaleY)
        {
            this.m00 *= scaleX;
            this.m01 *= scaleY;
            this.m10 *= scaleX;
            this.m11 *= scaleY;
            this.tx *= scaleX;
            this.ty *= scaleY;
            return this;
        }

        public Affine2f Scale(float scaleX, float scaleY)
        {
            this.m00 *= scaleX;
            this.m01 *= scaleX;
            this.m10 *= scaleY;
            this.m11 *= scaleY;
            return this;
        }

        public Affine2f ScaleX(float scaleX)
        {
            return Multiply(this, scaleX, 0, 0, 1, 0, 0, this);
        }

        public Affine2f ScaleY(float scaleY)
        {
            return Multiply(this, 1, 0, 0, scaleY, 0, 0, this);
        }

        public Affine2f Translate(float tx, float ty)
        {
            this.tx += m00 * tx + m10 * ty;
            this.ty += m11 * ty + m01 * tx;
            return this;
        }

        public Affine2f PreTranslate(float tx, float ty)
        {
            return Translate(tx, ty);
        }

        public Affine2f PostTranslate(float tx, float ty)
        {
            this.tx += tx;
            this.ty += ty;
            return this;
        }

        public Affine2f SetToTranslate(float tx, float ty)
        {
            this.m00 = 1.0f;
            this.m01 = 0.0f;
            this.m10 = 0.0f;
            this.m11 = 1.0f;
            this.tx = tx;
            this.ty = ty;
            return this;
        }

        public Affine2f SetTranslate(float x, float y)
        {
            this.tx = x;
            this.ty = y;
            return this;
        }

        public Affine2f TranslateX(float tx)
        {
            return Multiply(this, 1, 0, 0, 1, tx, 0, this);
        }

        public Affine2f TranslateY(float ty)
        {
            return Multiply(this, 1, 0, 0, 1, 0, ty, this);
        }

        public Affine2f Shear(float sx, float sy)
        {
            return Multiply(this, 1, sy, sx, 1, 0, 0, this);
        }

        public Affine2f ShearX(float sx)
        {
            return Multiply(this, 1, 0, sx, 1, 0, 0, this);
        }

        public Affine2f ShearY(float sy)
        {
            return Multiply(this, 1, sy, 0, 1, 0, 0, this);
        }

        public Affine2f PreShear(float sx, float sy)
        {
            float tanX = MathUtils.Tan(-MathUtils.DEG_TO_RAD * sx);
            float tanY = MathUtils.Tan(-MathUtils.DEG_TO_RAD * sy);

            float m00 = this.m00;
            float m01 = this.m01;
            float m10 = this.m10;
            float m11 = this.m11;
            float tx = this.tx;
            float ty = this.ty;

            this.m00 = m00 + tanY * m10;
            this.m01 = m01 + tanY * m11;
            this.m10 = tanX * m00 + m10;
            this.m11 = tanX * m01 + m11;
            this.tx = tx;
            this.ty = ty;
            return this;
        }

        public void PostShear(float sx, float sy)
        {
            float tanX = MathUtils.Tan(-MathUtils.DEG_TO_RAD * sx);
            float tanY = MathUtils.Tan(-MathUtils.DEG_TO_RAD * sy);

            float m00 = this.m00;
            float m01 = this.m01;
            float m10 = this.m10;
            float m11 = this.m11;
            float tx = this.tx;
            float ty = this.ty;

            this.m00 = m00 + m01 * tanX;
            this.m01 = m00 * tanY + m01;
            this.m10 = m10 + m11 * tanX;
            this.m11 = m10 * tanY + m11;
            this.tx = tx + ty * tanX;
            this.ty = tx * tanY + ty;
        }

        public Affine2f SetToShear(float sx, float sy)
        {
            this.m00 = 1.0f;
            this.m01 = MathUtils.Tan(-MathUtils.DEG_TO_RAD * sy);
            this.m10 = MathUtils.Tan(-MathUtils.DEG_TO_RAD * sx);
            this.m11 = 1.0f;
            this.tx = 0.0f;
            this.ty = 0.0f;

            return this;
        }

        public Affine2f Invert()
        {
            // 计算行列式，并临时存储数值
            float det = m00 * m11 - m10 * m01;
            if (MathUtils.Abs(det) == 0f)
            {
                // 行列式为零时，矩阵将不可逆，无法还原所以报错
                throw new LSysException(this.ToString());
            }
            float rdet = 1f / det;
            return new Affine2f(+m11 * rdet, -m10 * rdet, -m01 * rdet, +m00 * rdet, (m10 * ty - m11 * tx) * rdet,
                    (m01 * tx - m00 * ty) * rdet);
        }

        public Affine2f InvertSelf()
        {
            float a1 = this.m00;
            float b1 = this.m01;
            float c1 = this.m10;
            float d1 = this.m11;
            float tx1 = this.tx;
            float n = a1 * d1 - b1 * c1;
            this.m00 = d1 / n;
            this.m01 = -b1 / n;
            this.m10 = -c1 / n;
            this.m11 = a1 / n;
            this.tx = (c1 * this.ty - d1 * tx1) / n;
            this.ty = -(a1 * this.ty - b1 * tx1) / n;
            return this;
        }

        public Affine2f ApplyITRS(float x, float y, float rotation, float scaleX, float scaleY)
        {
            float radianSin = MathUtils.Sin(rotation);
            float radianCos = MathUtils.Cos(rotation);
            this.m00 = radianCos * scaleX;
            this.m01 = radianSin * scaleX;
            this.m10 = -radianSin * scaleY;
            this.m11 = radianCos * scaleY;
            this.tx = x;
            this.ty = y;
            return this;
        }

        public Vector2f ApplyInverse(float x, float y)
        {
            Vector2f output = new Vector2f();

            float a = this.m00;
            float b = this.m01;
            float c = this.m10;
            float d = this.m11;

            float id = 1f / ((a * d) + (c * -b));

            output.x = (d * id * x) + (-c * id * y) + (((ty * c) - (tx * d)) * id);
            output.y = (a * id * y) + (-b * id * x) + (((-ty * a) + (tx * b)) * id);

            return output;
        }

        public Affine2f Concat(Affine2f other)
        {
            float a = this.m00 * other.m00;
            float b = 0f;
            float c = 0f;
            float d = this.m11 * other.m11;
            float tx = this.tx * other.m00 + other.tx;
            float ty = this.ty * other.m11 + other.ty;

            if (this.m10 != 0f || this.m01 != 0f || other.m10 != 0f || other.m01 != 0f)
            {
                a += this.m10 * other.m01;
                d += this.m01 * other.m10;
                b += this.m00 * other.m10 + this.m10 * other.m11;
                c += this.m01 * other.m00 + this.m11 * other.m01;
                tx += this.ty * other.m01;
                ty += this.tx * other.m10;
            }

            this.m00 = a;
            this.m01 = b;
            this.m10 = c;
            this.m11 = d;
            this.tx = tx;
            this.ty = ty;
            return this;
        }

        public Affine2f Concatenate(Affine2f other)
        {
            if (Generality() < other.Generality())
            {
                return other.PreConcatenate(this);
            }
            if (other is Affine2f)
            {
                return Multiply(this, (Affine2f)other, new Affine2f());
            }
            else
            {
                Affine2f oaff = new Affine2f(other);
                return Multiply(this, oaff, oaff);
            }
        }

        public Affine2f PreConcatenate(Affine2f other)
        {
            if (Generality() < other.Generality())
            {
                return other.Concatenate(this);
            }
            if (other is Affine2f)
            {
                return Multiply((Affine2f)other, this, new Affine2f());
            }
            else
            {
                Affine2f oaff = new Affine2f(other);
                return Multiply(oaff, this, oaff);
            }
        }

        public Affine2f PostConcatenate(Affine2f t)
        {
            return PostConcatenate(t.m00, t.m01, t.m10, t.m11, t.tx, t.ty);
        }

        public Affine2f PostConcatenate(float ma, float mb, float mc, float md, float mx,
                 float my)
        {
            float m00 = this.m00;
            float m01 = this.m01;
            float m10 = this.m10;
            float m11 = this.m11;
            float tx = this.tx;
            float ty = this.ty;

            this.m00 = m00 * ma + m01 * mc;
            this.m01 = m00 * mb + m01 * md;
            this.m10 = m10 * ma + m11 * mc;
            this.m11 = m10 * mb + m11 * md;
            this.tx = tx * ma + ty * mc + mx;
            this.ty = tx * mb + ty * md + my;
            return this;
        }

        public Affine2f Prepend(float a, float b, float c, float d, float tx, float ty)
        {
            float tx1 = this.tx;
            if (this.m00 != 1 || b != 0 || c != 0 || d != 1)
            {
                float a1 = this.m00;
                float c1 = this.m01;
                this.m00 = a1 * a + this.m10 * c;
                this.m10 = a1 * b + this.m10 * d;
                this.m01 = c1 * a + this.m11 * c;
                this.m11 = c1 * b + this.m11 * d;
            }
            this.tx = tx1 * a + this.ty * c + tx;
            this.ty = tx1 * b + this.ty * d + ty;
            return this;
        }

        public Affine2f Prepend(Affine2f other)
        {
            return Prepend(other.m00, other.m10, other.m01, other.m11, other.tx, other.ty);
        }

        public Affine2f Append(float a, float b, float c, float d, float tx, float ty)
        {
            float a1 = this.m00;
            float b1 = this.m10;
            float c1 = this.m01;
            float d1 = this.m11;
            if (a != 1 || b != 0 || c != 0 || d != 1)
            {
                this.m00 = a * a1 + b * c1;
                this.m10 = a * b1 + b * d1;
                this.m01 = c * a1 + d * c1;
                this.m11 = c * b1 + d * d1;
            }
            this.tx = tx * a1 + ty * c1 + this.tx;
            this.ty = tx * b1 + ty * d1 + this.ty;
            return this;
        }

        public Affine2f Append(Affine2f other)
        {
            return Append(other.m00, other.m10, other.m01, other.m11, other.tx, other.ty);
        }

        public Affine2f Lerp(Affine2f other, float t)
        {
            if (Generality() < other.Generality())
            {
                return other.Lerp(this, -t);
            }

            Affine2f ot = (other is Affine2f) ? (Affine2f)other : new Affine2f(other);
            return new Affine2f(m00 + t * (ot.m00 - m00), m01 + t * (ot.m01 - m01), m10 + t * (ot.m10 - m10),
                    m11 + t * (ot.m11 - m11), tx + t * (ot.tx - tx), ty + t * (ot.ty - ty));
        }

        public void Transform(Vector2f[] src, int srcOff, Vector2f[] dst, int dstOff, int count)
        {
            for (int ii = 0; ii < count; ii++)
            {
                Transform(src[srcOff++], dst[dstOff++]);
            }
        }

        public void Transform(float[] src, int srcOff, float[] dst, int dstOff, int count)
        {
            for (int ii = 0; ii < count; ii++)
            {
                float x = src[srcOff++], y = src[srcOff++];
                dst[dstOff++] = m00 * x + m10 * y + tx;
                dst[dstOff++] = m01 * x + m11 * y + ty;
            }
        }

        public void Transform(float[] vertices)
        {
            int count = vertices.Length >> 1;
            int i = 0;
            int j = 0;
            while (--count >= 0)
            {
                float x = vertices[i++];
                float y = vertices[i++];
                vertices[j++] = x * this.m00 + y * this.m10 + this.tx;
                vertices[j++] = x * this.m01 + y * this.m11 + this.ty;
            }
        }

        public PointI TransformPoint(int pointX, int pointY, PointI resultPoint)
        {
            int x = (int)(this.m00 * pointX + this.m01 * pointY + this.tx);
            int y = (int)(this.m10 * pointX + this.m11 * pointY + this.ty);
            if (resultPoint != null)
            {
                resultPoint.Set(x, y);
                return resultPoint;
            }
            return new PointI(x, y);
        }

        public PointF TransformPoint(float pointX, float pointY, PointF resultPoint)
        {
            float x = this.m00 * pointX + this.m01 * pointY + this.tx;
            float y = this.m10 * pointX + this.m11 * pointY + this.ty;
            if (resultPoint != null)
            {
                resultPoint.Set(x, y);
                return resultPoint;
            }
            return new PointF(x, y);
        }

        public Vector2f TransformPoint(float pointX, float pointY, Vector2f resultPoint)
        {
            float x = this.m00 * pointX + this.m01 * pointY + this.tx;
            float y = this.m10 * pointX + this.m11 * pointY + this.ty;
            if (resultPoint != null)
            {
                resultPoint.Set(x, y);
                return resultPoint;
            }
            return new Vector2f(x, y);
        }

        public Vector2f TransformPoint(Vector2f v, Vector2f into)
        {
            float x = v.X(), y = v.Y();
            return into.Set(m00 * x + m10 * y + tx, m01 * x + m11 * y + ty);
        }

        public Vector2f Transform(Vector2f v, Vector2f into)
        {
            float x = v.X(), y = v.Y();
            return into.Set(m00 * x + m10 * y, m01 * x + m11 * y);
        }

        public Vector2f InverseTransform(Vector2f v, Vector2f into)
        {
            float x = v.X(), y = v.Y();
            float det = m00 * m11 - m01 * m10;
            if (MathUtils.Abs(det) == 0f)
            {
                // 行列式为零时，矩阵将不可逆，无法还原所以报错
                throw new LSysException("Affine2f exception " + this.ToString());
            }
            float rdet = 1 / det;
            return into.Set((x * m11 - y * m10) * rdet, (y * m00 - x * m01) * rdet);
        }

        public Affine2f SetToOrtho2D(float x, float y, float width, float height)
        {
            SetToOrtho(x, x + width, y + height, y, 1f, -1f);
            return this;
        }

        public Affine2f SetToOrtho2D(float x, float y, float width, float height, float near, float far)
        {
            SetToOrtho(x, x + width, y + height, y, near, far);
            return this;
        }

        public Affine2f SetToOrtho(float left, float right, float bottom, float top, float near, float far)
        {
            float x_orth = 2 / (right - left);
            float y_orth = 2 / (top - bottom);

            float m03 = -(right + left) / (right - left);
            float m13 = -(top + bottom) / (top - bottom);

            this.m00 = x_orth;
            this.m11 = y_orth;
            this.tx = m03;
            this.ty = m13;

            return this;
        }

        public Affine2f Cpy(Affine2f other)
        {
            this.m00 = other.m00;
            this.m01 = other.m01;
            this.m10 = other.m10;
            this.m11 = other.m11;
            this.tx = other.tx;
            this.ty = other.ty;
            return this;
        }

        public Affine2f CpyTo(Affine2f other)
        {
            other.m00 = this.m00;
            other.m01 = this.m01;
            other.m10 = this.m10;
            other.m11 = this.m11;
            other.tx = this.tx;
            other.ty = this.ty;
            return this;
        }

        public Affine2f Cpy()
        {
            return new Affine2f(m00, m01, m10, m11, tx, ty);
        }

        public int Generality()
        {
            return GENERALITY;
        }

        public Vector2f Scale()
        {
            return new Vector2f(ScaleX(), ScaleY());
        }

        public Vector2f Translation()
        {
            return new Vector2f(Tx(), Ty());
        }

        public Affine2f SetScale(float scaleX, float scaleY)
        {
            SetScaleX(scaleX);
            SetScaleY(scaleY);
            return this;
        }


        public override int GetHashCode()
        {
            uint prime = 31;
            uint result = 17;
            result = prime * result + NumberUtils.FloatToIntBits(m00);
            result = prime * result + NumberUtils.FloatToIntBits(m11);
            result = prime * result + NumberUtils.FloatToIntBits(m01);
            result = prime * result + NumberUtils.FloatToIntBits(m10);
            result = prime * result + NumberUtils.FloatToIntBits(tx);
            result = prime * result + NumberUtils.FloatToIntBits(ty);
            return (int)result;
        }


        public float GetX()
        {
            return Tx();
        }


        public float GetY()
        {
            return Ty();
        }

        public virtual Matrix4 ToViewMatrix4()
        {
            Dimension dim = LSystem.viewSize;
            if (projectionMatrix == null)
            {
                projectionMatrix = new Matrix4();
            }
            projectionMatrix.SetToOrtho2D(0, 0, dim.width * LSystem.GetScaleWidth(), dim.height * LSystem.GetScaleHeight());
            projectionMatrix.ThisCombine(this);
            return projectionMatrix;
        }
        public override string ToString()
        {
            StringKeyValue builder = new StringKeyValue("Affine");
            builder.NewLine().PushBracket().AddValue(MathUtils.ToString((m00))).Comma().AddValue(MathUtils.ToString(m10)).Comma().AddValue(MathUtils.ToString(tx)).PopBracket().NewLine().PushBracket().AddValue(MathUtils.ToString(m01)).Comma().AddValue(MathUtils.ToString(m11)).Comma().AddValue(MathUtils.ToString(ty)).PopBracket().NewLine().AddValue("[0.0,0.0,1.0]").NewLine();
            return builder.ToString();
        }

    }
}
