using java.lang;

namespace loon.geom
{
    public class BooleanValue
    {
        private bool value = false;

        public BooleanValue() : this(false)
        {

        }

        public BooleanValue(bool v)
        {
            this.Set(v);
        }

        public BooleanValue Set(bool res)
        {
            this.value = res;
            return this;
        }

        public bool Get()
        {
            return Result();
        }

        public bool Result()
        {
            return value;
        }

        public override string ToString()
        {
            return JavaSystem.Str(value);
        }
    }
}
