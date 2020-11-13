namespace loon.utils.reply
{

    public class Triple<T1, T2, T3>
    {

        private readonly T1 o1;

        private readonly T2 o2;

        private readonly T3 o3;

        private Triple(T1 o1, T2 o2, T3 o3)
        {
            this.o1 = o1;
            this.o2 = o2;
            this.o3 = o3;
        }

        public static Triple<T1, T2, T3> Get(T1 o1, T2 o2, T3 o3)
        {
            return new Triple<T1, T2, T3>(o1, o2, o3);
        }

        public override int GetHashCode()
        {
            if (o1 == null && o2 != null && o3 != null)
            {
                return base.GetHashCode() ^ o2.GetHashCode() ^ o3.GetHashCode();
            }
            if (o2 == null && o1 != null && o3 != null)
            {
                return base.GetHashCode() ^ o1.GetHashCode() ^ o3.GetHashCode();
            }
            if (o3 == null && o1 != null && o2 != null)
            {
                return base.GetHashCode() ^ o1.GetHashCode() ^ o2.GetHashCode();
            }
            if (o1 == null && o2 == null && o3 != null)
            {
                return base.GetHashCode() ^ o3.GetHashCode();
            }
            if (o2 == null && o3 == null && o1 != null)
            {
                return base.GetHashCode() ^ o1.GetHashCode();
            }
            if (o3 == null && o1 == null && o2 != null)
            {
                return base.GetHashCode() ^ o2.GetHashCode();
            }
            if (o1 == null && o2 == null && o3 == null)
            {
                return base.GetHashCode();
            }
            return o1.GetHashCode() ^ o2.GetHashCode() ^ o3.GetHashCode();
        }

        public override bool Equals(object o)
        {
            if (!(o is Triple<T1, T2, T3>))
            {
                return false;
            }
            Triple<T1, T2, T3> p = (Triple<T1, T2, T3>)o;
            return LSystem.Equals(o1, p.o1) && LSystem.Equals(o2, p.o2) && LSystem.Equals(o3, p.o3);
        }

        public T1 Get1()
        {
            return o1;
        }

        public T2 Get2()
        {
            return o2;
        }

        public T3 Get3()
        {
            return o3;
        }

        public override string ToString()
        {
            return "[" + StringUtils.ToString(o1) + ", " + StringUtils.ToString(o2) + ", " + StringUtils.ToString(o3) + "]";
        }

    }

}
