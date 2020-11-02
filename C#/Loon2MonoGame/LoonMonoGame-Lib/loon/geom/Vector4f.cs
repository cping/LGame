using loon.utils;

namespace loon.geom
{
    public class Vector4f : XYZW
    {

        private static readonly Array<Vector4f> _VEC4_CACHE = new Array<Vector4f>();

        public static Vector4f TMP()
        {
            Vector4f temp = _VEC4_CACHE.Pop();
            if (temp == null)
            {
                _VEC4_CACHE.Add(temp = new Vector4f(0, 0, 0, 0));
            }
            return temp;
        }

        public static Vector4f ZERO()
        {
            return new Vector4f(0);
        }

        public static Vector4f ONE()
        {
            return new Vector4f(1);
        }

        public static Vector4f AXIS_X()
        {
            return new Vector4f(1, 0, 0, 0);
        }

        public static Vector4f AXIS_Y()
        {
            return new Vector4f(0, 1, 0, 0);
        }

        public static Vector4f AXIS_Z()
        {
            return new Vector4f(0, 0, 1, 0);
        }

        public static Vector4f AXIS_W()
        {
            return new Vector4f(0, 0, 0, 1);
        }

        public static Vector4f At(float x, float y, float z, float w)
        {
            return new Vector4f(x, y, z, w);
        }

        public static Vector4f SmoothStep(Vector4f a, Vector4f b, float amount)
        {
            return new Vector4f(MathUtils.SmoothStep(a.x, b.x, amount), MathUtils.SmoothStep(a.y, b.y, amount),
                    MathUtils.SmoothStep(a.z, b.z, amount), MathUtils.SmoothStep(a.w, b.w, amount));
        }

        public float x, y, z, w;

        public Vector4f() : this(0, 0, 0, 0)
        {

        }

        public Vector4f(float x, float y, float z, float w)
        {
            Set(x, y, z, w);
        }

        public Vector4f(float v) : this(v, v, v, v)
        {

        }

        public Vector4f(Vector2f v, float z, float w) : this(v.GetX(), v.GetY(), z, w)
        {

        }

        public Vector4f(float x, Vector2f v, float w) : this(x, v.GetX(), v.GetY(), w)
        {

        }

        public Vector4f(float x, float y, Vector2f v) : this(x, y, v.GetX(), v.GetY())
        {

        }

        public Vector4f(Vector3f v, float w) : this(v.GetX(), v.GetY(), v.GetZ(), w)
        {

        }

        public Vector4f(float x, Vector3f v) : this(x, v.GetX(), v.GetY(), v.GetZ())
        {

        }

        public Vector4f(Vector4f v) : this(v.x, v.y, v.z, v.w)
        {

        }

        public Vector4f Set(float x, float y, float z, float w)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;

            return this;
        }

        public Vector4f Add(Vector4f v)
        {
            return Add(v.x, v.y, v.z, v.w);
        }

        public Vector4f Add(float x, float y, float z, float w)
        {
            return Cpy().AddSelf(x, y, z, w);
        }

        public Vector4f AddSelf(float x, float y, float z, float w)
        {
            return Set(this.x + x, this.y + y, this.z + z, this.w + w);
        }

        public Vector4f Cpy()
        {
            return new Vector4f(this);
        }

        public Vector4f Add(Vector3f v, float w)
        {
            return Add(v.x, v.y, v.z, w);
        }

        public Vector4f AddSelf(Vector3f v, float w)
        {
            return AddSelf(v.x, v.y, v.z, w);
        }

        public Vector4f Add(float x, Vector3f v)
        {
            return Add(x, v.x, v.y, v.z);
        }

        public Vector4f AddSelf(float x, Vector3f v)
        {
            return AddSelf(x, v.x, v.y, v.z);
        }

        public Vector4f Add(Vector2f v, float z, float w)
        {
            return Add(v.x, v.y, z, w);
        }

        public Vector4f AddSelf(Vector2f v, float z, float w)
        {
            return AddSelf(v.x, v.y, z, w);
        }

        public Vector4f Add(Vector2f v1, Vector2f v2)
        {
            return Add(v1.x, v1.y, v2.x, v2.y);
        }

        public Vector4f AddSelf(Vector2f v1, Vector2f v2)
        {
            return AddSelf(v1.x, v1.y, v2.x, v2.y);
        }

        public Vector4f Add(float x, float y, Vector2f v)
        {
            return Add(x, y, v.x, v.y);
        }

        public Vector4f AddSelf(float x, float y, Vector2f v)
        {
            return AddSelf(x, y, v.x, v.y);
        }

        public Vector4f SmoothStep(Vector4f v, float amount)
        {
            return SmoothStep(this, v, amount);
        }

        public Vector4f Subtract(Vector4f v)
        {
            return Add(-v.x, -v.y, -v.z, -v.w);
        }

        public Vector4f SubtractSelf(Vector4f v)
        {
            return AddSelf(-v.x, -v.y, -v.z, -v.w);
        }

        public Vector4f Subtract(Vector3f v, float w)
        {
            return Subtract(v.x, v.y, v.z, w);
        }

        public Vector4f Subtract(float x, float y, float z, float w)
        {
            return Add(-x, -y, -z, -w);
        }

        public Vector4f SubtractSelf(Vector3f v, float w)
        {
            return SubtractSelf(v.x, v.y, v.z, w);
        }

        public Vector4f SubtractSelf(float x, float y, float z, float w)
        {
            return AddSelf(-x, -y, -z, -w);
        }

        public Vector4f Subtract(float x, Vector3f v)
        {
            return Subtract(x, v.x, v.y, v.z);
        }

        public Vector4f SubtractSelf(float x, Vector3f v)
        {
            return SubtractSelf(x, v.x, v.y, v.z);
        }

        public Vector4f Subtract(Vector2f v, float z, float w)
        {
            return Subtract(v.x, v.y, z, w);
        }

        public Vector4f SubtractSelf(Vector2f v, float z, float w)
        {
            return SubtractSelf(v.x, v.y, z, w);
        }

        public Vector4f Subtract(Vector2f v1, Vector2f v2)
        {
            return Subtract(v1.x, v1.y, v2.x, v2.y);
        }

        public Vector4f SubtractSelf(Vector2f v1, Vector2f v2)
        {
            return SubtractSelf(v1.x, v1.y, v2.x, v2.y);
        }

        public Vector4f Subtract(float x, float y, Vector2f v)
        {
            return Subtract(x, y, v.x, v.y);
        }

        public Vector4f SubtractSelf(float x, float y, Vector2f v)
        {
            return SubtractSelf(x, y, v.x, v.y);
        }

        public Vector4f Scale(float s)
        {
            return Scale(s, s, s, s);
        }

        public Vector4f Scale(float sx, float sy, float sz, float sw)
        {
            return new Vector4f(x * sx, y * sy, z * sz, w * sw);
        }

        public float Dot(Vector4f v)
        {
            return x * v.x + y * v.y + z * v.z + w * v.w;
        }

        public Vector4f Normalize()
        {
            return Cpy().NormalizeSelf();
        }

        public Vector4f NormalizeSelf()
        {
            float l = Length();

            if (l == 0 || l == 1)
                return this;

            return Set(x / l, y / l, z / l, w / l);
        }

        public Vector4f Normalize3()
        {
            return Cpy().Normalize3Self();
        }

        public Vector4f Normalize3Self()
        {
            float l = MathUtils.Sqrt(x * x + y * y + z * z);

            if (l == 0 || l == 1)
            {
                return this;
            }

            return Set(x / l, y / l, z / l, w / l);
        }

        public float Length()
        {
            return MathUtils.Sqrt(LengthSquared());
        }

        public float LengthSquared()
        {
            return x * x + y * y + z * z + w * w;
        }

        public Vector4f Negate()
        {
            return new Vector4f(-x, -y, -z, -w);
        }

        public Vector4f NegateSelf()
        {
            return Set(-x, -y, -z, -w);
        }

        public Vector4f multiply(Vector4f v)
        {
            return Scale(v.x, v.y, v.z, v.w);
        }

        public Vector4f multiplySelf(Vector4f v)
        {
            return ScaleSelf(v.x, v.y, v.z, v.w);
        }

        public Vector4f ScaleSelf(float sx, float sy, float sz, float sw)
        {
            return Set(x * sx, y * sy, z * sz, w * sw);
        }

        public Vector4f translate(float dx, float dy, float dz, float dw)
        {
            return cpy().translateSelf(dx, dy, dz, dw);
        }

        public Vector4f translateSelf(float dx, float dy, float dz, float dw)
        {
            return Set(this.x + dx, this.y + dy, this.z + dz, this.w + dw);
        }

        public Vector4f Lerp(Vector4f tarGet, float alpha)
        {
            return Cpy().LerpSelf(tarGet, alpha);
        }

        public Vector4f LerpSelf(Vector4f tarGet, float alpha)
        {
            Vector4f temp = Vector4f.TMP();
            ScaleSelf(1f - alpha).AddSelf(temp.Set(tarGet).ScaleSelf(alpha));
            return this;
        }

        public Vector4f AddSelf(Vector4f v)
        {
            return AddSelf(v.x, v.y, v.z, v.w);
        }

        public Vector4f ScaleSelf(float s)
        {
            return ScaleSelf(s, s, s, s);
        }

        public Vector4f Set(Vector4f v)
        {
            return Set(v.x, v.y, v.z, v.w);
        }

        public float GetX()
        {
            return x;
        }

        public Vector4f SetX(float x)
        {
            this.x = x;
            return this;
        }

        public float GetY()
        {
            return y;
        }

        public Vector4f SetY(float y)
        {
            this.y = y;
            return this;
        }

        public float GetZ()
        {
            return z;
        }

        public Vector4f SetZ(float z)
        {
            this.z = z;
            return this;
        }

        public float GetW()
        {
            return w;
        }

        public Vector4f SetW(float w)
        {
            this.w = w;
            return this;
        }

        public float GetR()
        {
            return x;
        }

        public Vector4f SetR(float r)
        {
            x = r;
            return this;
        }

        public float GetG()
        {
            return y;
        }

        public Vector4f SetG(float g)
        {
            y = g;
            return this;
        }

        public float GetB()
        {
            return z;
        }

        public Vector4f SetB(float b)
        {
            z = b;
            return this;
        }

        public float GetA()
        {
            return w;
        }

        public Vector4f SetA(float a)
        {
            w = a;
            return this;
        }

        public Vector4f Set(float v)
        {
            return Set(v, v, v, v);
        }

        public Vector4f Set(Vector2f v, float z, float w)
        {
            return Set(v.x, v.y, z, w);
        }

        public Vector4f Set(float x, Vector2f v, float w)
        {
            return Set(x, v.x, v.y, w);
        }

        public Vector4f Set(float x, float y, Vector2f v)
        {
            return Set(x, y, v.x, v.y);
        }

        public Vector4f Set(Vector3f v, float w)
        {
            return Set(v.x, v.y, v.z, w);
        }

        public Vector4f Set(float x, Vector3f v)
        {
            return Set(x, v.x, v.y, v.z);
        }

        public Vector4f cpy()
        {
            return new Vector4f(this);
        }

        public Vector2f GetXX()
        {
            return new Vector2f(x, x);
        }

        public Vector2f GetXY()
        {
            return new Vector2f(x, y);
        }

        public Vector2f GetXZ()
        {
            return new Vector2f(x, z);
        }

        public Vector2f GetXW()
        {
            return new Vector2f(x, w);
        }

        public Vector2f GetYX()
        {
            return new Vector2f(y, x);
        }

        public Vector2f GetYY()
        {
            return new Vector2f(y, y);
        }

        public Vector2f GetYZ()
        {
            return new Vector2f(y, z);
        }

        public Vector2f GetYW()
        {
            return new Vector2f(y, w);
        }

        public Vector2f GetZX()
        {
            return new Vector2f(z, x);
        }

        public Vector2f GetZY()
        {
            return new Vector2f(z, y);
        }

        public Vector2f GetZZ()
        {
            return new Vector2f(z, z);
        }

        public Vector2f GetZW()
        {
            return new Vector2f(z, w);
        }

        public Vector2f GetWX()
        {
            return new Vector2f(w, x);
        }

        public Vector2f GetWY()
        {
            return new Vector2f(w, y);
        }

        public Vector2f GetWZ()
        {
            return new Vector2f(w, z);
        }

        public Vector2f GetWW()
        {
            return new Vector2f(w, w);
        }

        public Vector3f GetXXX()
        {
            return new Vector3f(x, x, x);
        }

        public Vector3f GetXXY()
        {
            return new Vector3f(x, x, y);
        }

        public Vector3f GetXXZ()
        {
            return new Vector3f(x, x, z);
        }

        public Vector3f GetXXW()
        {
            return new Vector3f(x, x, w);
        }

        public Vector3f GetXYX()
        {
            return new Vector3f(x, y, x);
        }

        public Vector3f GetXYY()
        {
            return new Vector3f(x, y, y);
        }

        public Vector3f GetXYZ()
        {
            return new Vector3f(x, y, z);
        }

        public Vector3f GetXYW()
        {
            return new Vector3f(x, y, w);
        }

        public Vector3f GetXZX()
        {
            return new Vector3f(x, z, x);
        }

        public Vector3f GetXZY()
        {
            return new Vector3f(x, z, y);
        }

        public Vector3f GetXZZ()
        {
            return new Vector3f(x, z, z);
        }

        public Vector3f GetXZW()
        {
            return new Vector3f(x, z, w);
        }

        public Vector3f GetXWX()
        {
            return new Vector3f(x, w, x);
        }

        public Vector3f GetXWY()
        {
            return new Vector3f(x, w, y);
        }

        public Vector3f GetXWZ()
        {
            return new Vector3f(x, w, z);
        }

        public Vector3f GetXWW()
        {
            return new Vector3f(x, w, w);
        }

        public Vector3f GetYXX()
        {
            return new Vector3f(y, x, x);
        }

        public Vector3f GetYXY()
        {
            return new Vector3f(y, x, y);
        }

        public Vector3f GetYXZ()
        {
            return new Vector3f(y, x, z);
        }

        public Vector3f GetYXW()
        {
            return new Vector3f(y, x, w);
        }

        public Vector3f GetYYX()
        {
            return new Vector3f(y, y, x);
        }

        public Vector3f GetYYY()
        {
            return new Vector3f(y, y, y);
        }

        public Vector3f GetYYZ()
        {
            return new Vector3f(y, y, z);
        }

        public Vector3f GetYYW()
        {
            return new Vector3f(y, y, w);
        }

        public Vector3f GetYZX()
        {
            return new Vector3f(y, z, x);
        }

        public Vector3f GetYZY()
        {
            return new Vector3f(y, z, y);
        }

        public Vector3f GetYZZ()
        {
            return new Vector3f(y, z, z);
        }

        public Vector3f GetYZW()
        {
            return new Vector3f(y, z, w);
        }

        public Vector3f GetYWX()
        {
            return new Vector3f(y, w, x);
        }

        public Vector3f GetYWY()
        {
            return new Vector3f(y, w, y);
        }

        public Vector3f GetYWZ()
        {
            return new Vector3f(y, w, z);
        }

        public Vector3f GetYWW()
        {
            return new Vector3f(y, w, w);
        }

        public Vector3f GetZXX()
        {
            return new Vector3f(z, x, x);
        }

        public Vector3f GetZXY()
        {
            return new Vector3f(z, x, y);
        }

        public Vector3f GetZXZ()
        {
            return new Vector3f(z, x, z);
        }

        public Vector3f GetZXW()
        {
            return new Vector3f(z, x, w);
        }

        public Vector3f GetZYX()
        {
            return new Vector3f(z, y, x);
        }

        public Vector3f GetZYY()
        {
            return new Vector3f(z, y, y);
        }

        public Vector3f GetZYZ()
        {
            return new Vector3f(z, y, z);
        }

        public Vector3f GetZYW()
        {
            return new Vector3f(z, y, w);
        }

        public Vector3f GetZZX()
        {
            return new Vector3f(z, z, x);
        }

        public Vector3f GetZZY()
        {
            return new Vector3f(z, z, y);
        }

        public Vector3f GetZZZ()
        {
            return new Vector3f(z, z, z);
        }

        public Vector3f GetZZW()
        {
            return new Vector3f(z, z, w);
        }

        public Vector3f GetZWX()
        {
            return new Vector3f(z, w, x);
        }

        public Vector3f GetZWY()
        {
            return new Vector3f(z, w, y);
        }

        public Vector3f GetZWZ()
        {
            return new Vector3f(z, w, z);
        }

        public Vector3f GetZWW()
        {
            return new Vector3f(z, w, w);
        }

        public Vector3f GetWXX()
        {
            return new Vector3f(w, x, x);
        }

        public Vector3f GetWXY()
        {
            return new Vector3f(w, x, y);
        }

        public Vector3f GetWXZ()
        {
            return new Vector3f(w, x, z);
        }

        public Vector3f GetWXW()
        {
            return new Vector3f(w, x, w);
        }

        public Vector3f GetWYX()
        {
            return new Vector3f(w, y, x);
        }

        public Vector3f GetWYY()
        {
            return new Vector3f(w, y, y);
        }

        public Vector3f GetWYZ()
        {
            return new Vector3f(w, y, z);
        }

        public Vector3f GetWYW()
        {
            return new Vector3f(w, y, w);
        }

        public Vector3f GetWZX()
        {
            return new Vector3f(w, z, x);
        }

        public Vector3f GetWZY()
        {
            return new Vector3f(w, z, y);
        }

        public Vector3f GetWZZ()
        {
            return new Vector3f(w, z, z);
        }

        public Vector3f GetWZW()
        {
            return new Vector3f(w, z, w);
        }

        public Vector3f GetWWX()
        {
            return new Vector3f(w, w, x);
        }

        public Vector3f GetWWY()
        {
            return new Vector3f(w, w, y);
        }

        public Vector3f GetWWZ()
        {
            return new Vector3f(w, w, z);
        }

        public Vector3f GetWWW()
        {
            return new Vector3f(w, w, w);
        }

        public Vector2f GetRR()
        {
            return new Vector2f(x, x);
        }

        public Vector2f GetRG()
        {
            return new Vector2f(x, y);
        }

        public Vector2f GetRB()
        {
            return new Vector2f(x, z);
        }

        public Vector2f GetRA()
        {
            return new Vector2f(x, w);
        }

        public Vector2f GetGR()
        {
            return new Vector2f(y, x);
        }

        public Vector2f GetGG()
        {
            return new Vector2f(y, y);
        }

        public Vector2f GetGB()
        {
            return new Vector2f(y, z);
        }

        public Vector2f GetGA()
        {
            return new Vector2f(y, w);
        }

        public Vector2f GetBR()
        {
            return new Vector2f(z, x);
        }

        public Vector2f GetBG()
        {
            return new Vector2f(z, y);
        }

        public Vector2f GetBB()
        {
            return new Vector2f(z, z);
        }

        public Vector2f GetBA()
        {
            return new Vector2f(z, w);
        }

        public Vector2f GetAR()
        {
            return new Vector2f(w, x);
        }

        public Vector2f GetAG()
        {
            return new Vector2f(w, y);
        }

        public Vector2f GetAB()
        {
            return new Vector2f(w, z);
        }

        public Vector2f GetAA()
        {
            return new Vector2f(w, w);
        }

        public Vector3f GetRRR()
        {
            return new Vector3f(x, x, x);
        }

        public Vector3f GetRRG()
        {
            return new Vector3f(x, x, y);
        }

        public Vector3f GetRRB()
        {
            return new Vector3f(x, x, z);
        }

        public Vector3f GetRRA()
        {
            return new Vector3f(x, x, w);
        }

        public Vector3f GetRGR()
        {
            return new Vector3f(x, y, x);
        }

        public Vector3f GetRGG()
        {
            return new Vector3f(x, y, y);
        }

        public Vector3f GetRGB()
        {
            return new Vector3f(x, y, z);
        }

        public Vector3f GetRGA()
        {
            return new Vector3f(x, y, w);
        }

        public Vector3f GetRBR()
        {
            return new Vector3f(x, z, x);
        }

        public Vector3f GetRBG()
        {
            return new Vector3f(x, z, y);
        }

        public Vector3f GetRBB()
        {
            return new Vector3f(x, z, z);
        }

        public Vector3f GetRBA()
        {
            return new Vector3f(x, z, w);
        }

        public Vector3f GetRAR()
        {
            return new Vector3f(x, w, x);
        }

        public Vector3f GetRAG()
        {
            return new Vector3f(x, w, y);
        }

        public Vector3f GetRAB()
        {
            return new Vector3f(x, w, z);
        }

        public Vector3f GetRAA()
        {
            return new Vector3f(x, w, w);
        }

        public Vector3f GetGRR()
        {
            return new Vector3f(y, x, x);
        }

        public Vector3f GetGRG()
        {
            return new Vector3f(y, x, y);
        }

        public Vector3f GetGRB()
        {
            return new Vector3f(y, x, z);
        }

        public Vector3f GetGRA()
        {
            return new Vector3f(y, x, w);
        }

        public Vector3f GetGGR()
        {
            return new Vector3f(y, y, x);
        }

        public Vector3f GetGGG()
        {
            return new Vector3f(y, y, y);
        }

        public Vector3f GetGGB()
        {
            return new Vector3f(y, y, z);
        }

        public Vector3f GetGGA()
        {
            return new Vector3f(y, y, w);
        }

        public Vector3f GetGBR()
        {
            return new Vector3f(y, z, x);
        }

        public Vector3f GetGBG()
        {
            return new Vector3f(y, z, y);
        }

        public Vector3f GetGBB()
        {
            return new Vector3f(y, z, z);
        }

        public Vector3f GetGBA()
        {
            return new Vector3f(y, z, w);
        }

        public Vector3f GetGAR()
        {
            return new Vector3f(y, w, x);
        }

        public Vector3f GetGAG()
        {
            return new Vector3f(y, w, y);
        }

        public Vector3f GetGAB()
        {
            return new Vector3f(y, w, z);
        }

        public Vector3f GetGAA()
        {
            return new Vector3f(y, w, w);
        }

        public Vector3f GetBRR()
        {
            return new Vector3f(z, x, x);
        }

        public Vector3f GetBRG()
        {
            return new Vector3f(z, x, y);
        }

        public Vector3f GetBRB()
        {
            return new Vector3f(z, x, z);
        }

        public Vector3f GetBRA()
        {
            return new Vector3f(z, x, w);
        }

        public Vector3f GetBGR()
        {
            return new Vector3f(z, y, x);
        }

        public Vector3f GetBGG()
        {
            return new Vector3f(z, y, y);
        }

        public Vector3f GetBGB()
        {
            return new Vector3f(z, y, z);
        }

        public Vector3f GetBGA()
        {
            return new Vector3f(z, y, w);
        }

        public Vector3f GetBBR()
        {
            return new Vector3f(z, z, x);
        }

        public Vector3f GetBBG()
        {
            return new Vector3f(z, z, y);
        }

        public Vector3f GetBBB()
        {
            return new Vector3f(z, z, z);
        }

        public Vector3f GetBBA()
        {
            return new Vector3f(z, z, w);
        }

        public Vector3f GetBAR()
        {
            return new Vector3f(z, w, x);
        }

        public Vector3f GetBAG()
        {
            return new Vector3f(z, w, y);
        }

        public Vector3f GetBAB()
        {
            return new Vector3f(z, w, z);
        }

        public Vector3f GetBAA()
        {
            return new Vector3f(z, w, w);
        }

        public Vector3f GetARR()
        {
            return new Vector3f(w, x, x);
        }

        public Vector3f GetARG()
        {
            return new Vector3f(w, x, y);
        }

        public Vector3f GetARB()
        {
            return new Vector3f(w, x, z);
        }

        public Vector3f GetARA()
        {
            return new Vector3f(w, x, w);
        }

        public Vector3f GetAGR()
        {
            return new Vector3f(w, y, x);
        }

        public Vector3f GetAGG()
        {
            return new Vector3f(w, y, y);
        }

        public Vector3f GetAGB()
        {
            return new Vector3f(w, y, z);
        }

        public Vector3f GetAGA()
        {
            return new Vector3f(w, y, w);
        }

        public Vector3f GetABR()
        {
            return new Vector3f(w, z, x);
        }

        public Vector3f GetABG()
        {
            return new Vector3f(w, z, y);
        }

        public Vector3f GetABB()
        {
            return new Vector3f(w, z, z);
        }

        public Vector3f GetABA()
        {
            return new Vector3f(w, z, w);
        }

        public Vector3f GetAAR()
        {
            return new Vector3f(w, w, x);
        }

        public Vector3f GetAAG()
        {
            return new Vector3f(w, w, y);
        }

        public Vector3f GetAAB()
        {
            return new Vector3f(w, w, z);
        }

        public Vector3f GetAAA()
        {
            return new Vector3f(w, w, w);
        }

        public Vector4f Random()
        {
            this.x = MathUtils.Random(0f, LSystem.viewSize.GetWidth());
            this.y = MathUtils.Random(0f, LSystem.viewSize.GetHeight());
            this.z = MathUtils.Random();
            this.w = MathUtils.Random();
            return this;
        }

        public override int GetHashCode()
        {
            uint prime = 31;
            uint result = 1;
            result = prime * result + NumberUtils.FloatToIntBits(x);
            result = prime * result + NumberUtils.FloatToIntBits(y);
            result = prime * result + NumberUtils.FloatToIntBits(z);
            result = prime * result + NumberUtils.FloatToIntBits(w);
            return (int)result;
        }


        public override string ToString()
        {
            return "(" + x + ", " + y + ", " + z + ", " + w + ")";
        }

    }
}
