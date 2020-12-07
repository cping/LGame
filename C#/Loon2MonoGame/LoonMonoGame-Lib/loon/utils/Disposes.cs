
namespace loon.utils
{
    public class Disposes : LRelease
    {

        private readonly object _lock = new object();

        private readonly SortedList<LRelease> _disposeSelf;

        public Disposes()
        {
            this._disposeSelf = new SortedList<LRelease>();
        }

        public Disposes Put(params LRelease[] rs)
        {
            int size = rs.Length;
            lock (_lock)
            {
                for (int i = 0; i < size; i++)
                {
                    _disposeSelf.Add(rs[i]);
                }
            }
            return this;
        }

        public Disposes Put(LRelease release)
        {
            lock (_lock)
            {
                _disposeSelf.Add(release);
            }
            return this;
        }

        public bool Contains(LRelease release)
        {
            lock (_lock)
            {
                return _disposeSelf.Contains(release);
            }
        }

        public Disposes Remove(LRelease release)
        {
            lock (_lock)
            {
                _disposeSelf.Remove(release);
            }
            return this;
        }


        public void Close()
        {
            lock (_lock)
            {
                for (LIterator<LRelease> it = _disposeSelf.ListIterator(); it.HasNext();)
                {
                    LRelease release = it.Next();
                    if (release != null)
                    {
                        release.Close();
                    }
                }
                _disposeSelf.Clear();
            }
        }
    }
}
