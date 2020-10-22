using java.lang;

namespace java.nio
{
    public abstract class Buffer
{
        protected int capacity;
        protected int position;
        protected int limit;
        protected int mark = -1;

        Buffer(int c)
        {
            this.capacity = c;
            limit = c;
        }

        public int Capacity()
        {
            return capacity;
        }

        public int Position()
        {
            return position;
        }

        public Buffer Position(int newPosition)
        {
            if (newPosition < 0 || newPosition > limit)
            {
                throw new IllegalArgumentException("New position " + newPosition + " is outside of range [0;"
                        + limit + "]");
            }
            position = newPosition;
            if (newPosition < mark)
            {
                mark = 0;
            }
            return this;
        }

        public int Limit()
        {
            return this.limit;
        }

        public Buffer Limit(int newLimit)
        {
            if (newLimit < 0 || newLimit > capacity)
            {
                throw new IllegalArgumentException("New limit " + newLimit + " is outside of range [0;"
                        + capacity + "]");
            }
            if (mark > newLimit)
            {
                mark = -1;
            }
            limit = newLimit;
            if (position > limit)
            {
                position = limit;
            }
            return this;
        }

        public Buffer Mark()
        {
            mark = position;
            return this;
        }

        public Buffer Reset()
        {
            if (mark < 0)
            {
                throw new RuntimeException();
            }
            position = mark;
            return this;
        }

        public Buffer Clear()
        {
            position = 0;
            limit = capacity;
            mark = -1;
            return this;
        }

        public Buffer Flip()
        {
            limit = position;
            position = 0;
            mark = -1;
            return this;
        }

        public Buffer Rewind()
        {
            mark = -1;
            position = 0;
            return this;
        }

        public int Remaining()
        {
            return limit - position;
        }

        public bool HasRemaining()
        {
            return position < limit;
        }

        public abstract bool IsReadOnly();

        public abstract bool HasArray();

        public abstract object Array();

        public abstract int ArrayOffset();

        public abstract bool IsDirect();
    }
}
