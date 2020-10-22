namespace loon.geom
{
   public class PointI
{
        public int x;

        public int y;
        public PointI(int x, int y)
        {
            this.Set(x, y);
        }

        public PointI Set(int x, int y)
        {
            this.x = x;
            this.y = y;
            return this;
        }
    }
}
