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
using System;
namespace Loon.Core
{

    public interface Callback<T>
    {

        void OnSuccess(T result);

        void OnFailure(System.Exception t);
    }

    public abstract class Chain<T> : Callback<T>
    {

        private Callback<T> _failure;

        public Chain(Callback<T> f)
        {
            this._failure = f;
        }

        public virtual void OnFailure(Exception t)
        {
            if (_failure != null)
            {
                _failure.OnFailure(t);
            }
        }

        public abstract void OnSuccess(T result);
    }
}
