namespace loon.geom
{
    public class XYValue
    {

        private XY value = null;

        public XYValue(XY v)
        {
            this.Set(v);
        }

        public XYValue Set(XY v)
        {
            this.value = v;
            return this;
        }

        public XY Get()
        {
            return Result();
        }

        public XY Result()
        {
            return value;
        }

        public override string ToString()
        {
            return value.ToString();
        }

    }
}
