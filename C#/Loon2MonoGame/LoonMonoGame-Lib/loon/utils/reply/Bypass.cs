using loon.events;

namespace loon.utils.reply
{
    public abstract class Bypass
    {

        protected internal abstract class Runs : Updateable
        {
            public Runs next;
            public abstract void Action(object a);
        }

        protected internal abstract class Notifier
        {
            public abstract void Notify(object listener, object a1, object a2, object a3);
        }

        public interface GoListener
        {
        }

        public Cons DISPATCHING = new Cons(null, null);

        protected Cons _listeners;

        protected Runs _pendingRuns;

        protected static bool AreEqual<T>(T o1, T o2)
        {
            return ((object)o1 == (object)o2 || (o1 != null && o1.Equals(o2)));
        }

        protected static Runs Append(Runs head, Runs action)
        {
            if (head == null)
                return action;
            head.next = Append(head.next, action);
            return head;
        }

        public abstract GoListener DefaultListener();

        protected virtual void CheckMutate()
        {
        }

        protected virtual void ConnectionAdded()
        {
        }

        protected virtual void ConnectionRemoved()
        {
        }

        public virtual bool HasConnections()
        {
            return _listeners != null;
        }

        public virtual void ClearConnections()
        {
            lock (typeof(Bypass))
            {
                if (IsDispatching())
                {
                    throw new LSysException("system dispatching");
                }
                _listeners = null;
            }
        }


        protected Cons AddConnection(GoListener listener)
        {
            lock (typeof(Bypass))
            {
                if (listener == null)
                    throw new LSysException("null listener");
                return AddCons(new Cons(this, listener));
            }
        }

        protected Cons AddCons(Cons cons)
        {
            lock (typeof(Bypass))
            {
                if (IsDispatching())
                {
                }
            }
            return cons;
        }

        private bool IsDispatching()
        {
            return _listeners == DISPATCHING;
        }

        public bool IsClosed()
        {
            return IsDispatching();
        }

    }




}
