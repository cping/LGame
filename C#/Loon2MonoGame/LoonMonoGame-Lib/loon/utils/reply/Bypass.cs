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

        protected internal virtual void CheckMutate()
        {
        }

        protected internal virtual void ConnectionAdded()
        {
        }

        protected internal virtual void ConnectionRemoved()
        {
        }

        public virtual bool HasConnections()
        {
            return _listeners != null;
        }

        public virtual void ClearConnections()
        {
            lock (this)
            {
                if (IsDispatching())
                {
                    throw new LSysException("system dispatching");
                }
                _listeners = null;
            }
        }


        protected internal virtual Cons AddConnection(GoListener listener)
        {
            lock (this)
            {
                if (listener == null)
                    throw new LSysException("null listener");
                return AddCons(new Cons(this, listener));
            }
        }

        protected internal virtual Cons AddCons(Cons cons)
        {
            lock (this)
            {
                if (IsDispatching())
                {
                    _pendingRuns = Append(_pendingRuns, new AddConsImpl(this, cons));
                }
                else
                {
                    _listeners = Cons.Insert(_listeners, cons);
                    ConnectionAdded();
                }
                return cons;
            }
        }

        private class AddConsImpl : Runs
        {
            private readonly Bypass outerInstance;

            private readonly loon.utils.reply.Cons cons;

            public AddConsImpl(Bypass outerInstance, loon.utils.reply.Cons cons)
            {
                this.outerInstance = outerInstance;
                this.cons = cons;
            }

            public override void Action(object o)
            {
                outerInstance._listeners = Cons.Insert(outerInstance._listeners, cons);
                outerInstance.ConnectionAdded();
            }
        }
        protected internal virtual void Disconnect(Cons cons)
        {
            lock (this)
            {
                if (IsDispatching())
                {
                    _pendingRuns = Append(_pendingRuns, new DisconnectImpl(this, cons));
                }
                else
                {
                    _listeners = Cons.Remove(_listeners, cons);
                    ConnectionRemoved();
                }
            }
        }

        private class DisconnectImpl : Runs
        {
            private readonly Bypass outerInstance;

            private readonly loon.utils.reply.Cons cons;

            public DisconnectImpl(Bypass outerInstance, loon.utils.reply.Cons cons)
            {
                this.outerInstance = outerInstance;
                this.cons = cons;
            }

            public override void Action(object o)
            {
                outerInstance._listeners = Cons.Remove(outerInstance._listeners, cons);
                outerInstance.ConnectionRemoved();
            }
        }
        protected internal virtual void RemoveConnection(GoListener listener)
        {
            lock (this)
            {
                if (IsDispatching())
                {
                    _pendingRuns = Append(_pendingRuns, new RemoveConnectionImpl(this, listener));
                }
                else
                {
                    _listeners = Cons.RemoveAll(_listeners, listener);
                    ConnectionRemoved();
                }
            }
        }

        private class RemoveConnectionImpl : Runs
        {
            private readonly Bypass outerInstance;

            private readonly loon.utils.reply.Bypass.GoListener listener;

            public RemoveConnectionImpl(Bypass outerInstance, loon.utils.reply.Bypass.GoListener listener)
            {
                this.outerInstance = outerInstance;
                this.listener = listener;
            }

            public override void Action(object o)
            {
                outerInstance._listeners = Cons.RemoveAll(outerInstance._listeners, listener);
                outerInstance.ConnectionRemoved();
            }
        }
        protected internal virtual void Notify(Notifier notifier, object a1, object a2, object a3)
        {
            Cons lners;
            lock (this)
            {
                if (_listeners == DISPATCHING)
                {
                    _pendingRuns = Append(_pendingRuns, new RunsNotifyImpl(this, notifier, a1, a2, a3));
                    return;
                }
                lners = _listeners;
                Cons sentinel = DISPATCHING;
                _listeners = sentinel;
            }
            System.Exception exn = null;
            try
            {
                for (Cons cons = lners; cons != null; cons = cons.next)
                {
                    try
                    {
                        notifier.Notify(cons.Listener(), a1, a2, a3);
                    }
                    catch (System.Exception ex)
                    {
                        exn = ex;
                    }
                    if (cons.OneShot())
                    {
                        cons.Close();
                    }
                }

            }
            finally
            {
                lock (this)
                {
                    _listeners = lners;
                }
                Runs run;
                for (; (run = NextRun()) != null;)
                {
                    try
                    {
                        run.Action(this);
                    }
                    catch (System.Exception ex)
                    {
                        exn = ex;
                    }
                }
            }
            if (exn != null)
            {
                throw exn;
            }
        }

        private class RunsNotifyImpl : Runs
        {
            private readonly Bypass outerInstance;

            private loon.utils.reply.Bypass.Notifier notifier;
            private readonly object a1;
            private readonly object a2;
            private readonly object a3;

            public RunsNotifyImpl(Bypass outerInstance, loon.utils.reply.Bypass.Notifier notifier, object a1, object a2, object a3)
            {
                this.outerInstance = outerInstance;
                this.notifier = notifier;
                this.a1 = a1;
                this.a2 = a2;
                this.a3 = a3;
            }

            public override void Action(object o)
            {
                outerInstance.Notify(notifier, a1, a2, a3);
            }
        }

        private Runs NextRun()
        {
            lock (this)
            {
                Runs run = _pendingRuns;
                if (run != null)
                {
                    _pendingRuns = run.next;
                }
                return run;
            }
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
