using java.lang;

namespace java.util
{
    public interface Comparator<T>
    {
        int Compare(T o1, T o2);
        Comparator<T> Reversed();
        Comparator<T> ThenComparing(Comparator<T> other);
    }

    public static class Comparator_Java<T>
    {
        public static Comparator<T> Reversed(Comparator<T> @this)
        {
            if (@this == null) { throw new NullPointerException(); }
            return new ComparatorReversed<T>(@this);
        }
        public static Comparator<T> ThenComparing(Comparator<T> @this, Comparator<T> other)
        {
            if (@this == null || other == null) { throw new NullPointerException(); }
            return new ComparatorThenComparing<T>(@this, other);
        }
    }

    internal class ComparatorReversed<T> : Comparator<T>
    {
        protected readonly Comparator<T> other;

        public ComparatorReversed(Comparator<T> other)
        {
            this.other = other;
        }
        public virtual int Compare(T o1, T o2)
        {
            return other.Compare(o2, o1);
        }
        public virtual Comparator<T> ThenComparing(Comparator<T> other)
        {
            return Comparator_Java<T>.ThenComparing(this, other);
        }
        public virtual Comparator<T> Reversed()
        {
            return other;
        }
    }

    internal class ComparatorThenComparing<T> : ComparatorReversed<T>
    {
        private readonly Comparator<T> second;
        public ComparatorThenComparing(Comparator<T> other, Comparator<T> second) : base(other)
        {
            this.second = second;
        }
        public override int Compare(T o1, T o2)
        {
            int v = other.Compare(o1, o2);
            if (v != 0) { return v; }
            return second.Compare(o1, o2);
        }
    }
}
