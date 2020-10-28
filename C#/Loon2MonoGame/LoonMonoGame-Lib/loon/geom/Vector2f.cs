namespace loon.geom
{
    public class Vector2f : XY
    {
        public float x;

        public float y;
        public Vector2f() : this(0, 0)
        {

        }

        public Vector2f(float x, float y)
        {
            this.x = x;
            this.y = y;
        }

        public float GetX()
        {
            return this.x;
        }

        public float GetY()
        {
            return this.y;
        }
        public int X()
        {
            return (int)x;
        }

        public int Y()
        {
            return (int)y;
        }

        public Vector2f Set(float v)
        {
            return Set(v, v);
        }

        public Vector2f Set(float x, float y)
        {
            this.x = x;
            this.y = y;
            return this;
        }

        public Vector2f Set(XY v)
        {
            this.x = v.GetX();
            this.y = v.GetY();
            return this;
        }

        public Vector2f Set(Vector2f v)
        {
            this.x = v.x;
            this.y = v.y;
            return this;
        }
    }
}
