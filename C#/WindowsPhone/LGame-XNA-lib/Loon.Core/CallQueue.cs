using Loon.Core.Event;
using System.Runtime.CompilerServices;
using System;
namespace Loon.Core
{

    public class RunQueue
    {

        private int _count;

        private Entry _head;

        private class Entry
        {
            public readonly Updateable update;
            public Entry next;

            public Entry(Updateable update)
            {
                this.update = update;
            }
        }

        public RunQueue()
        {
        }

        public void Execute()
        {
            if (_count == 0)
            {
                return;
            }
            Entry head;
            lock (this)
            {
                head = this._head;
                this._head = null;
            }
            for (; head != null; )
            {
                try
                {
                    head.update.Action();
                }
                catch (System.Exception)
                {
                }
                head = head.next;
            }
            _count = 0;
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public void Add(Updateable update)
        {
            if (_head == null)
            {
                _head = new Entry(update);
            }
            else
            {
                Entry parent = _head;
                while (parent.next != null)
                {
                    parent = parent.next;
                }
                parent.next = new Entry(update);
            }
            _count++;
        }
    }
    public abstract class CallQueue
    {

        protected readonly RunQueue _queue;

        protected CallQueue()
        {
            this._queue = new RunQueue();
        }

        public void InvokeLater(Updateable update)
        {
            _queue.Add(update);
        }

        private class update_call : Updateable
        {
            Callback<object> callback;
            object result;
            internal update_call(Callback<object> callback, object result)
            {
                this.callback = callback;
                this.result = result;
            }

            public void Action()
            {
                callback.OnSuccess(result);
            }
        }

        public void NotifySuccess(Callback<object> callback, object result)
        {
            InvokeLater(new CallQueue.update_call(callback, result));
        }

        private class update_fail : Updateable
        {
            Callback<object> callback;
            Exception result;
            internal update_fail(Callback<object> callback, Exception result)
            {
                this.callback = callback;
                this.result = result;
            }

            public void Action()
            {
                callback.OnFailure(result);
            }
        }

        public void NotifyFailure(Callback<object> callback, Exception error)
        {
            InvokeLater(new CallQueue.update_fail(callback, error));
        }

        public abstract void InvokeAsync(Updateable action);

    }
}
