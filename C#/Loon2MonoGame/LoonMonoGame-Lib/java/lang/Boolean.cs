namespace java.lang
{
    public class Boolean
    {
        private readonly bool value;

        public static readonly Boolean FALSE_JAVA = new Boolean(false);
        public static readonly Boolean TRUE_JAVA = new Boolean(true);
        public Boolean(bool v)
        {
            value = v;
        }

        public bool BooleanValue()
        {
            return value;
        }

        public override bool Equals(object o)
        {
            if (o == null || !(o is Boolean)) return false;
            return ((Boolean)o).value == value;
        }

        public override int GetHashCode()
        {
            return value ? 1231 : 1237;
        }

        public override string ToString()
        {
            return Boolean.ToString(value);
        }

        public static string ToString(bool b)
        {
            return JavaSystem.Str(b);
        }

        public static Boolean ValueOf(bool b)
        {
            return b ? TRUE_JAVA : FALSE_JAVA;
        }

    }
}
