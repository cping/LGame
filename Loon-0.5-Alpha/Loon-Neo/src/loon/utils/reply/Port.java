/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
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
package loon.utils.reply;

public abstract class Port<T> implements VarView.Listener<T>, ActView.Listener<T>
{

    public <P> Port<P> compose (final Function<P,T> f) {
        final Port<T> outer = this;
        return new Port<P>() {
            public void onEmit (P value) {
                outer.onEmit(f.apply(value));
            }
        };
    }

    public <P extends T> Port<P> filtered (final Function<? super P,Boolean> pred) {
        final Port<T> outer = this;
        return new Port<P>() {
            public void onEmit (P value) {
                if (pred.apply(value)) outer.onEmit(value);
            }
        };
    }

    public <P extends T> Port<P> andThen (final Port<? super P> after) {
        final Port<T> before = this;
        return new Port<P>() {
            public void onEmit (P event) {
                before.onEmit(event);
                after.onEmit(event);
            }
        };
    }

    @Override public final void onChange (T value, T oldValue) {
        onEmit(value);
    }
}
