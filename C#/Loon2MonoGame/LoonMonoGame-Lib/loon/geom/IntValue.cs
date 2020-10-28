using java.lang;

namespace loon.geom
{
    public class IntValue
    {

        private int value;

        public IntValue() : this(0)
        {

        }

        public IntValue(int v)
        {
            this.Set(v);
        }

        public IntValue Set(int v)
        {
            this.value = v;
            return this;
        }

        public int Get()
        {
            return Result();
        }

        public int Result()
        {
            return value;
        }


        public override string ToString()
        {
            return JavaSystem.Str(value);
        }

    }

}
