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
    }
}
