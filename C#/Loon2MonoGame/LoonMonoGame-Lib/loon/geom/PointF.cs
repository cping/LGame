namespace loon.geom
{
    public class PointF
    {
        public float x;

        public float y;

        public PointF(float x, float y)
        {
            this.Set(x, y);
        }

        public PointF Set(float x, float y)
        {
            this.x = x;
            this.y = y;
            return this;
        }
    }
}
