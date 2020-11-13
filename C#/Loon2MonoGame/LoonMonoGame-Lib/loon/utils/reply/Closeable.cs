using java.lang;

namespace loon.utils.reply
{

    public abstract class Closeable : LRelease
    {
        internal class Set : Closeable
        {
            protected ObjectSet<LRelease> _set;

            protected bool _closed = false;

            public bool IsClosed()
            {
                return _closed;
            }

            public override void Close()
            {
                if (_set != null)
                {
                    foreach (LRelease c in _set)
                    {
                        try
                        {
                            c.Close();
                        }
                        catch (Throwable)
                        {
                        }
                    }
                    _set.Clear();
                }
                _closed = true;
            }

            public T Add<T>(T c) where T : LRelease
            {
               if (_set == null)
                {
                    _set = new ObjectSet<LRelease>();
                }
                _set.Add(c);
                return c;
            }

            public void Remove(LRelease c)
            {
                if (_set != null)
                {
                    _set.Remove(c);
                }
            }
        }


        public abstract void Close();
    }

}
