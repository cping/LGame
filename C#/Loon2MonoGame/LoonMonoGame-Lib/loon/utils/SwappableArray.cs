namespace loon.utils
{
    public class SwappableArray<T>
    {

        private int currentIndex = 0;

        private readonly TArray<T> data;

        public SwappableArray() : this(CollectionUtils.INITIAL_CAPACITY)
        {

        }

        public SwappableArray(int size)
        {
            this.data = new TArray<T>(size);
        }

        public SwappableArray(T[] array)
        {
            this.data = new TArray<T>(array);
        }

        public SwappableArray(TArray<T> array)
        {
            this.data = array;
        }

        public T PreviousLoop()
        {
            if (this.currentIndex > 0)
            {
                this.currentIndex--;
            }
            else
            {
                this.currentIndex = this.data.Size() - 1;
            }
            return this.data.Get(this.currentIndex);
        }

        public T NextLoop()
        {
            if (this.currentIndex + 1 < this.data.Size())
            {
                this.currentIndex++;
            }
            else
            {
                this.currentIndex = 0;
            }
            return this.data.Get(this.currentIndex);
        }

        public T Get(int idx)
        {
            return this.data.Get(idx);
        }

        public SwappableArray<T> Add(T o)
        {
            this.data.Add(o);
            this.currentIndex = this.data.size;
            return this;
        }

        public SwappableArray<T> Remove(T o)
        {
            this.data.Remove(o);
            this.currentIndex = this.data.size;
            return this;
        }

        public bool MoveBack(T o)
        {
            int loc = GetLocation(o);
            if (loc > 0)
            {
                T previous = this.data.Get(loc - 1);
                this.data.Set(loc - 1, o);
                this.data.Set(loc, previous);
                return true;
            }
            return false;
        }

        public bool MoveForward(T o)
        {
            int loc = GetLocation(o);
            if (loc < this.data.Size())
            {
                T forward = this.data.Get(loc + 1);
                this.data.Set(loc, forward);
                this.data.Set(loc + 1, o);
                return true;
            }
            return false;
        }

        public bool IsFront(T o)
        {
            if ((object)this.data.Get(this.data.Size() - 1) == (object)o)
            {
                return true;
            }
            return false;
        }

        public bool IsBack(T o)
        {
            if ((object)this.data.Get(0) == (object)o)
            {
                return true;
            }
            return false;
        }

        public SwappableArray<T> MoveToFront(T o)
        {
            while (!IsFront(o))
            {
                MoveForward(o);
            }
            return this;
        }

        public SwappableArray<T> MoveToBack(T o)
        {
            while (!IsBack(o))
            {
                MoveBack(o);
            }
            return this;
        }

        public int GetLocation(T o)
        {
            for (int i = 0; i < this.data.Size(); i++)
            {
                if ((object)this.data.Get(i) == (object)o)
                {
                    return i;
                }
            }
            return -1;
        }

        public T GetAt(int loc)
        {
            return this.data.Get(loc);
        }

        public TArray<T> GetElements()
        {
            return this.data;
        }

        public SwappableArray<T> ClearLoopIndex()
        {
            return SetLoopIndex(0);
        }

        public int LoopIndex()
        {
            return this.currentIndex;
        }

        public SwappableArray<T> SetLoopIndex(int idx)
        {
            this.currentIndex = idx;
            return this;
        }


        public override string ToString()
        {
            return this.data.ToString();
        }

    }
}
