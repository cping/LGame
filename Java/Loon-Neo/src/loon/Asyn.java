/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon;

import loon.utils.TArray;
import loon.utils.reply.Act;
import loon.utils.reply.GoPromise;
import loon.utils.reply.Port;

public abstract class Asyn
{

    public static class Default extends Asyn
    {

        /**
         * 为了语法转换到C#和C++，只能忍痛放弃匿名构造类了……
         **/
        private class CallDefaultPort<T> extends Port<T>
        {

            private Default _def;

            CallDefaultPort(Default d)
            {
                this._def = d;
            }

            @Override
            public void onEmit(Object event)
            {
                _def.dispatch();
            }

        }

        private final TArray<Runnable> pending = new TArray<>();
        private final TArray<Runnable> running = new TArray<>();
        protected final Log log;

        public Default(Log log, Act<? extends Object> frame)
        {
            this.log = log;
            frame.connect(new CallDefaultPort<Object>(this)).setPriority(Short.MAX_VALUE);
        }

        @Override
        public boolean isAsyncSupported()
        {
            return false;
        }

        @Override
        public void invokeAsync(Runnable action)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized void invokeLater(Runnable action)
        {
            pending.add(action);
        }

        private void dispatch()
        {
            synchronized(this)
            {
                running.addAll(pending);
                pending.clear();
            }

            for(int ii = 0, ll = running.size; ii < ll; ii++)
            {
                Runnable action = running.get(ii);
                try
                {
                    action.run();
                }
                catch(Exception e)
                {
                    log.warn("invokeLater Runnable failed: " + action, e);
                }
            }
            running.clear();
        }
    }

    public abstract void invokeLater(Runnable action);

    /**
     * 为了语法转换到C#和C++，只能忍痛放弃匿名构造类了……
     **/
    private static class DeferredPromiseRunnable<T> implements Runnable
    {

        private CallDeferredPromise<T> _promise;

        private int _mode = 0;

        private T _value;

        private Throwable _cause;

        public DeferredPromiseRunnable(int m, CallDeferredPromise<T> p, T val, Throwable c)
        {
            this._mode = m;
            this._promise = p;
            this._value = val;
            this._cause = c;
        }

        @Override
        public void run()
        {
            switch(_mode)
            {
                case 0:
                    _promise.invokeSucceed(_value);
                    break;
                default:
                    _promise.invokeFail(_cause);
                    break;
            }
        }
    }

    /**
     * 为了语法转换到C#和C++，只能忍痛放弃匿名构造类了……
     **/
    private class CallDeferredPromise<T> extends GoPromise<T>
    {

        private Asyn _asyn;

        public CallDeferredPromise(Asyn a)
        {
            this._asyn = a;
        }

        @Override
        public void succeed(final T value)
        {
            _asyn.invokeLater(new DeferredPromiseRunnable<T>(0, this, value, null));
        }

        @Override
        public void fail(final Throwable cause)
        {
            _asyn.invokeLater(new DeferredPromiseRunnable<T>(1, this, null, cause));
        }

        public void invokeSucceed(final T value)
        {
            super.succeed(value);
        }

        public void invokeFail(final Throwable cause)
        {
            super.fail(cause);
        }
    }

    public <T> GoPromise<T> deferredPromise()
    {
        return new CallDeferredPromise<T>(this);
    }

    public abstract boolean isAsyncSupported();

    public void invokeAsync(Runnable action)
    {
        throw new UnsupportedOperationException();
    }
}
