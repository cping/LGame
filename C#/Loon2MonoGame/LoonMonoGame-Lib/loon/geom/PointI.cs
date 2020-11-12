using loon.utils;

namespace loon.geom
{
    public class PointI : XY
    {

        public static bool PointEquals(int x1, int y1, int x2, int y2, int tolerance)
        {
            int dx = x2 - x1;
            int dy = y2 - y1;
            return dx * dx + dy * dy < tolerance * tolerance;
        }

        public int x = 0;
        public int y = 0;

        public PointI() : this(0, 0)
        {

        }

        public PointI(int size)
        {
            Set(size, size);
        }

        public PointI(int x1, int y1)
        {
            Set(x1, y1);
        }

        public PointI(PointI p)
        {
            this.x = p.x;
            this.y = p.y;
        }

        public PointI Set(int v)
        {
            return Set(v, v);
        }

        public PointI Set(int x1, int y1)
        {
            this.x = x1;
            this.y = y1;
            return this;
        }

        public PointF GetF()
        {
            return new PointF(this.x, this.y);
        }

        public PointI ToRoundPoint()
        {
            return new PointI(MathUtils.Floor(this.x), MathUtils.Floor(this.y));
        }

        public PointI Empty()
        {
            return this.Set(0, 0);
        }

        public override bool Equals(object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (GetType() != obj.GetType())
                return false;
            PointI other = (PointI)obj;
            return Equals(other);
        }

        public bool Equals(PointI point)
        {
            return Equals(point.x, point.y);
        }

        public bool Equals(int x, int y)
        {
            return MathUtils.Equal(x, this.x) && MathUtils.Equal(y, this.y);
        }

        public int Length()
        {
            return (int)MathUtils.Sqrt(MathUtils.Mul(x, x) + MathUtils.Mul(y, y));
        }

        public PointI Negate()
        {
            x = -x;
            y = -y;
            return this;
        }

        public PointI Offset(int x, int y)
        {
            this.x += x;
            this.y += y;
            return this;
        }

        public PointI Set(PointI p)
        {
            this.x = p.x;
            this.y = p.y;
            return this;
        }

        public int DistanceTo(PointI p)
        {
            int tx = this.x - p.x;
            int ty = this.y - p.y;
            return (int)MathUtils.Sqrt(MathUtils.Mul(tx, tx) + MathUtils.Mul(ty, ty));
        }

        public int DistanceTo(int x, int y)
        {
            int tx = this.x - x;
            int ty = this.y - y;
            return (int)MathUtils.Sqrt(MathUtils.Mul(tx, tx) + MathUtils.Mul(ty, ty));
        }

        public int DistanceTo(PointI p1, PointI p2)
        {
            int tx = p2.x - p1.x;
            int ty = p2.y - p1.y;
            int u = MathUtils.Div(MathUtils.Mul(x - p1.x, tx) + MathUtils.Mul(y - p1.y, ty),
                   MathUtils.Mul(tx, tx) + MathUtils.Mul(ty, ty));
            int ix = p1.x + MathUtils.Mul(u, tx);
            int iy = p1.y + MathUtils.Mul(u, ty);
            int dx = ix - x;
            int dy = iy - y;
            return (int)MathUtils.Sqrt(MathUtils.Mul(dx, dx) + MathUtils.Mul(dy, dy));
        }

        public PointI Cpy(PointI p)
        {
            return new PointI(p.x, p.y);
        }

        public PointI Cpy()
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

        public string ToCSS()
        {
            return this.x + "px " + this.y + "px";
        }

        public PointI Random()
        {
            this.x = MathUtils.Random(0, LSystem.viewSize.GetWidth());
            this.y = MathUtils.Random(0, LSystem.viewSize.GetHeight());
            return this;
        }

        public float[] ToArray()
        {
            return new float[] { x, y };
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
