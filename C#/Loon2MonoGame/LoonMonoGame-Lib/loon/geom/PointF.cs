using loon.utils;

namespace loon.geom
{
    public class PointF : XY
    {

        public static bool PointEquals(float x1, float y1, float x2, float y2, float tolerance)
        {
            float dx = x2 - x1;
            float dy = y2 - y1;
            return dx * dx + dy * dy < tolerance * tolerance;
        }

        public float x = 0;
        public float y = 0;

        public PointF() : this(0, 0)
        {

        }

        public PointF(float size)
        {
            Set(size, size);
        }

        public PointF(float x1, float y1)
        {
            Set(x1, y1);
        }

        public PointF(PointF p)
        {
            this.x = p.x;
            this.y = p.y;
        }

        public PointF Set(float v)
        {
            return Set(v, v);
        }

        public PointF Set(float x1, float y1)
        {
            this.x = x1;
            this.y = y1;
            return this;
        }

        public PointI GetI()
        {
            return new PointI((int)this.x, (int)this.y);
        }

        public PointF ToRoundPoint()
        {
            return new PointF(MathUtils.Floor(this.x), MathUtils.Floor(this.y));
        }

        public PointF Empty()
        {
            return Set(0f, 0f);
        }

        public override bool Equals(object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (GetType() != obj.GetType())
                return false;
            PointF other = (PointF)obj;
            return Equals(other);
        }

        public bool Equals(PointF point)
        {
            return Equals(point.x, point.y);
        }

        public bool Equals(float x, float y)
        {
            return MathUtils.Equal(x, this.x) && MathUtils.Equal(y, this.y);
        }

        public float Length()
        {
            return MathUtils.Sqrt(MathUtils.Mul(x, x) + MathUtils.Mul(y, y));
        }

        public PointF Negate()
        {
            x = -x;
            y = -y;
            return this;
        }

        public PointF Offset(float x, float y)
        {
            this.x += x;
            this.y += y;
            return this;
        }

        public PointF Set(PointF p)
        {
            this.x = p.x;
            this.y = p.y;
            return this;
        }

        public float DistanceTo(PointF p)
        {
            float tx = this.x - p.x;
            float ty = this.y - p.y;
            return MathUtils.Sqrt(MathUtils.Mul(tx, tx) + MathUtils.Mul(ty, ty));
        }

        public float DistanceTo(float x, float y)
        {
            float tx = this.x - x;
            float ty = this.y - y;
            return MathUtils.Sqrt(MathUtils.Mul(tx, tx) + MathUtils.Mul(ty, ty));
        }

        public float DistanceTo(PointF p1, PointF p2)
        {
            float tx = p2.x - p1.x;
            float ty = p2.y - p1.y;
            float u = MathUtils.Div(MathUtils.Mul(x - p1.x, tx) + MathUtils.Mul(y - p1.y, ty),
                   MathUtils.Mul(tx, tx) + MathUtils.Mul(ty, ty));
            float ix = p1.x + MathUtils.Mul(u, tx);
            float iy = p1.y + MathUtils.Mul(u, ty);
            float dx = ix - x;
            float dy = iy - y;
            return MathUtils.Sqrt(MathUtils.Mul(dx, dx) + MathUtils.Mul(dy, dy));
        }

        public PointF Cpy(PointF p)
        {
            return new PointF(p.x, p.y);
        }

        public PointF Cpy()
        {
            return Cpy(this);
        }

        public float GetX()
        {
            return x;
        }

        public float GetY()
        {
            return y;
        }

        public PointF Random()
        {
            this.x = MathUtils.Random(0f, LSystem.viewSize.GetWidth());
            this.y = MathUtils.Random(0f, LSystem.viewSize.GetHeight());
            return this;
        }

        public float[] ToArray()
        {
            return new float[] { x, y };
        }

        public string ToCSS()
        {
            return this.x + "px " + this.y + "px";
        }

        public override int GetHashCode()
        {
            uint prime = 31;
            uint result = 1;
            result = prime * result + NumberUtils.FloatToIntBits(x);
            result = prime * result + NumberUtils.FloatToIntBits(y);
            return (int)result;
        }

        public override string ToString()
        {
            return "(" + x + "," + y + ")";
        }
    }

}
