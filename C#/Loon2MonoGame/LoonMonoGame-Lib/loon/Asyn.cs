using java.lang;
using loon.utils;
using loon.utils.reply;

namespace loon
{
    public abstract class Asyn
    {
        public class CallDefaultPort<T> : Port<T>
        {

            public Default<T> _def;

            public CallDefaultPort(Default<T> d)
            {
                this._def = d;
            }

            public override void OnEmit(T e)
            {
                _def.Dispatch();
            }

        }

        public class Default<T> : Asyn
        {
            internal readonly TArray<Runnable> pending = new TArray<Runnable>();
            internal readonly TArray<Runnable> running = new TArray<Runnable>();
            protected internal readonly Log log;

            public Default(Log log, Act<T> frame)
            {
                this.log = log;
                frame.Connect(new CallDefaultPort<T>(this)).SetPriority(Short.MAX_VALUE_JAVA);
            }

            public override bool IsAsyncSupported()
            {
                return false;
            }

            public override void InvokeAsync(Runnable action)
            {
                throw new System.NotSupportedException();
            }

            public override void InvokeLater(Runnable action)
            {
                lock (this)
                {
                    pending.Add(action);
                }
            }

            internal virtual void Dispatch()
            {
                lock (this)
                {
                    running.AddAll(pending);
                    pending.Clear();
                }
                for (int ii = 0, ll = running.size; ii < ll; ii++)
                {
                    Runnable action = running.Get(ii);
                    try
                    {
                        action.Run();
                    }
                    catch (System.Exception e)
                    {
                        log.Warn("invokeLater Runnable failed: " + action, e);
                    }
                }
                running.Clear();
            }

        }

        public abstract void InvokeLater(Runnable action);
        private class DeferredPromiseRunnable<T> : Runnable
        {

            private GoPromise<T> _promise;

            private int _mode = 0;

            private T _value;

            private System.Exception _cause;

            public DeferredPromiseRunnable(int m, GoPromise<T> p, T val, System.Exception c)
            {
                this._mode = m;
                this._promise = p;
                this._value = val;
                this._cause = c;
            }

            public void Run()
            {
                switch (_mode)
                {
                    case 0:
                        _promise.Succeed(_value);
                        break;
                    default:
                        _promise.Fail(_cause);
                        break;
                }
            }
        }
        private class CallDeferredPromise<T> : GoPromise<T>
        {

            internal Asyn _asyn;

            public CallDeferredPromise(Asyn a)
            {
                this._asyn = a;
            }

            public override void Succeed(T value)
            {
                _asyn.InvokeLater(new DeferredPromiseRunnable<T>(0, this, value, null));
            }

            public override void Fail(System.Exception cause)
            {
                _asyn.InvokeLater(new DeferredPromiseRunnable<T>(1, this, default, cause));
            }
        }

        public virtual GoPromise<T> DeferredPromise<T>()
        {
            return new CallDeferredPromise<T>(this);
        }

        public abstract bool IsAsyncSupported();

        public virtual void InvokeAsync(Runnable action)
        {
            throw new System.NotSupportedException();
        }
    }


}
