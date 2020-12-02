using loon.events;

namespace loon.utils.reply
{

    public class Emitter<T> : LRelease
    {

        protected internal OrderedMap<T, SortedList<Updateable>> _emitterTable;

        protected internal int _maxFrameTask;

        protected internal bool _active;

        public Emitter(int max)
        {
            this._emitterTable = new OrderedMap<T, SortedList<Updateable>>();
            this._active = true;
            _maxFrameTask = max;
        }

        public Emitter() : this(32)
        {

        }

        public virtual bool Contains(T eventType)
        {
            return _emitterTable.ContainsKey(eventType);
        }

        public virtual Updateable GetObserverFirst(T eventType)
        {
            SortedList<Updateable> list = _emitterTable.Get(eventType);
            if (list != null)
            {
                return list.GetFirst();
            }
            return null;
        }

        public virtual Updateable GetObserverLast(T eventType)
        {
            SortedList<Updateable> list = _emitterTable.Get(eventType);
            if (list != null)
            {
                return list.GetLast();
            }
            return null;
        }

        public virtual Updateable RemoveObserverFirst(T eventType)
        {
            SortedList<Updateable> list = _emitterTable.Get(eventType);
            if (list != null)
            {
                return list.RemoveFirst();
            }
            return null;
        }

        public virtual Updateable RemoveObserverLast(T eventType)
        {
            SortedList<Updateable> list = _emitterTable.Get(eventType);
            if (list != null)
            {
                return list.RemoveLast();
            }
            return null;
        }

        public virtual Emitter<T> AddObserver(T eventType, Updateable handler)
        {
            SortedList<Updateable> list = _emitterTable.Get(eventType);
            if (list == null)
            {
                list = new SortedList<Updateable>();
            }
            if (!list.Contains(handler))
            {
                list.Add(handler);
            }
            _emitterTable.Put(eventType, list);
            return this;
        }

        public virtual bool RemoveObserver(T eventType, Updateable handler)
        {
            SortedList<Updateable> list = _emitterTable.Get(eventType);
            if (list != null)
            {
                return list.Remove(handler);
            }
            return false;
        }

        public virtual Emitter<T> ClearObserver(T eventType)
        {
            SortedList<Updateable> list = _emitterTable.Get(eventType);
            if (list != null)
            {
                list.Clear();
                return this;
            }
            return this;
        }

        public virtual Emitter<T> OnEmits()
        {
            for (ObjectMap<T, SortedList<Updateable>>.Keys<T> it = _emitterTable.KEYS(); it.HasNext();)
            {
                T key = it.Next();
                if (null != key)
                {
                    OnEmit(key);
                }
            }
            return this;
        }

        public virtual Emitter<T> OnEmit(T eventType)
        {
            if (!_active)
            {
                return this;
            }
            int taskCount = 0;
            SortedList<Updateable> list = _emitterTable.Get(eventType);
            if (null != list)
            {
                for (LIterator<Updateable> it = list.ListIterator(); it.HasNext();)
                {
                    Updateable update = it.Next();
                    if (update != null)
                    {
                        update.Action(eventType);
                        if (update is ActionUpdate)
                        {
                            ActionUpdate au = (ActionUpdate)update;
                            if (au.Completed())
                            {
                                list.Remove(au);
                            }
                        }
                    }
                    taskCount++;
                    if (taskCount >= _maxFrameTask)
                    {
                        break;
                    }
                }
            }
            return this;
        }

        public virtual Emitter<T> SetMaxFrameTasks(int i)
        {
            this._maxFrameTask = i;
            return this;
        }

        public virtual int GetMaxFrameTasks()
        {
            return this._maxFrameTask;
        }

        public virtual Emitter<T> Start()
        {
            this._active = true;
            return this;
        }

        public virtual Emitter<T> Stop()
        {
            this._active = false;
            return this;
        }

        public virtual Emitter<T> Pause()
        {
            return Stop();
        }

        public virtual Emitter<T> Unpause()
        {
            return Start();
        }

        public virtual bool IsActive()
        {
            return this._active;
        }

        public virtual int Size()
        {
            return _emitterTable.size;
        }

        public virtual int GetObserverSize(T eventType)
        {
            SortedList<Updateable> list = _emitterTable.Get(eventType);
            return list == null ? 0 : list.size;
        }


        public virtual void Close()
        {
            _active = false;
            if (_emitterTable != null)
            {
                _emitterTable.Clear();
            }
        }
    }

}
