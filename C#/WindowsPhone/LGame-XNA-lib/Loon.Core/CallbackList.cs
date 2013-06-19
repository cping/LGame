/// <summary>
/// Copyright 2008 - 2012
/// Licensed under the Apache License, Version 2.0 (the "License"); you may not
/// use this file except in compliance with the License. You may obtain a copy of
/// the License at
/// http://www.apache.org/licenses/LICENSE-2.0
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
/// WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
/// License for the specific language governing permissions and limitations under
/// the License.
/// </summary>
///
/// @project loon
/// @email javachenpeng@yahoo.com
/// 
/// 
using System;
using System.Collections.Generic;
namespace Loon.Core
{
    public class CallbackList<T> : Callback<T>
    {

        private IList<Callback<T>> callbacks;

        public CallbackList()
        {
            this.callbacks = new List<Callback<T>>();
        }

        public static IList<Callback<T>> CreateAdd(
                IList<Callback<T>> list, Callback<T> callback)
        {
            if (list == null)
            {
                list = new List<Callback<T>>();
            }
            list.Add(callback);
            return list;
        }

        public static IList<Callback<T>> DispatchSuccessClear(
                IList<Callback<T>> list, T result)
        {
            if (list != null)
            {
                for (int ii = 0, ll = list.Count; ii < ll; ii++)
                {
                    list[ii].OnSuccess(result);
                }
            }
            return null;
        }

        public static IList<Callback<T>> DispatchFailureClear(
                IList<Callback<T>> list, Exception cause)
        {
            if (list != null)
            {
                for (int ii = 0, ll = list.Count; ii < ll; ii++)
                    list[ii].OnFailure(cause);
            }
            return null;
        }


        protected internal void CheckState()
        {
            if (callbacks == null)
            {
                throw new InvalidOperationException("CallbackList has already fired !");
            }
        }

        public static CallbackList<T> Create(Callback<T> callback)
        {
            CallbackList<T> list = new CallbackList<T>();
            list.Add(callback);
            return list;
        }

        public CallbackList<T> Add(Callback<T> callback)
        {
            CheckState();
            if (callbacks != null)
            {
                callbacks.Add(callback);
            }
            return this;
        }

        public void Remove(Callback<T> callback)
        {
            CheckState();
            if (callbacks != null)
            {
                callbacks.Add(callback);
            }
        }

        public virtual void OnSuccess(T result)
        {
            CheckState();
            foreach (Callback<T> cb in callbacks)
            {
                cb.OnSuccess(result);
            }
            callbacks = null;
        }

        public virtual void OnFailure(Exception t)
        {
            CheckState();
            foreach (Callback<T> cb in callbacks)
            {
                cb.OnFailure(t);
            }
            callbacks = null;
        }
    }
}
