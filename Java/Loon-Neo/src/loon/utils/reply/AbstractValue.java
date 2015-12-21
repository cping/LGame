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

public abstract class AbstractValue<T> extends Bypass implements VarView<T>
{
    @Override public <M> VarView<M> map (final Function<? super T, M> func) {
        final AbstractValue<T> outer = this;
        return new MappedValue<M>() {
            @Override public M get () {
                return func.apply(outer.get());
            }
            @Override public String toString () {
                return outer + ".map("  + func + ")";
            }
            @Override protected Connection connect () {
                return outer.connect(new Listener<T>() {
                    @Override public void onChange (T value, T ovalue) {
                        notifyChange(func.apply(value), func.apply(ovalue));
                    }
                });
            }
        };
    }

    @Override public <M> VarView<M> flatMap (
        final Function<? super T, ? extends VarView<M>> func) {
        final AbstractValue<T> outer = this;
        final VarView<? extends VarView<M>> mapped = map(func);
        return new MappedValue<M>() {
            private Connection conn;

            @Override public M get () {
                return mapped.get().get();
            }
            @Override public String toString () {
                return outer + ".flatMap("  + func + ")";
            }
            @Override protected Connection connect () {
                conn = mapped.connect(new UnitPort() {
                    public void onEmit () { reconnect(); }
                });
                return mapped.get().connect(new Listener<M>() {
                    @Override public void onChange (M value, M ovalue) {
                        notifyChange(value, ovalue);
                    }
                });
            }
            @Override protected void disconnect () {
                super.disconnect();
                if (conn != null) conn.close();
            }
        };
    }

    @Override public Connection connect (Listener<? super T> listener) {
        return addConnection(listener);
    }
    @Override public Connection connectNotify (Listener<? super T> listener) {
        Connection conn = connect(listener);
        try {
            listener.onChange(get(), null);
            return conn;
        } catch (RuntimeException re) {
            conn.close();
            throw re;
        } catch (Error e) {
            conn.close();
            throw e;
        }
    }

    @Override public Connection connect (ActView.Listener<? super T> listener) {
        return connect(wrap(listener));
    }
    @Override public Connection connectNotify (ActView.Listener<? super T> listener) {
        return connectNotify(wrap(listener));
    }
    private static <T> Listener<T> wrap (final ActView.Listener<? super T> listener) {
        return new Listener<T>() {
            public void onChange (T newValue, T oldValue) {
                listener.onEmit(newValue);
            }
        };
    }

    @Override public Connection connect (Port<? super T> port) {
        return connect((Listener<? super T>)port);
    }
    @Override public Connection connectNotify (Port<? super T> port) {
        return connectNotify((Listener<? super T>)port);
    }

    @Override public void disconnect (Listener<? super T> listener) {
        removeConnection(listener);
    }

    @Override public int hashCode () {
        T value = get();
        return (value == null) ? 0 : value.hashCode();
    }

    @Override public boolean equals (Object other) {
        if (other == null) return false;
        if (other.getClass() != getClass()) return false;
        T value = get();
        @SuppressWarnings("unchecked") T ovalue = ((AbstractValue<T>)other).get();
        return areEqual(value, ovalue);
    }

    @Override public String toString () {
        String cname = getClass().getName();
        return cname.substring(cname.lastIndexOf(".")+1) + "(" + get() + ")";
    }

    @Override Listener<T> placeholderListener () {
        @SuppressWarnings("unchecked") Listener<T> p = (Listener<T>)Ports.DEF;
        return p;
    }

    protected T updateAndNotifyIf (T value) {
        return updateAndNotify(value, false);
    }

    protected T updateAndNotify (T value) {
        return updateAndNotify(value, true);
    }

    protected T updateAndNotify (T value, boolean force) {
        checkMutate();
        T ovalue = updateLocal(value);
        if (force || !areEqual(value, ovalue)) {
            emitChange(value, ovalue);
        }
        return ovalue;
    }

    protected void emitChange (T value, T oldValue) {
        notifyChange(value, oldValue);
    }

    protected void notifyChange (T value, T oldValue) {
        notify(CHANGE, value, oldValue, null);
    }

    protected T updateLocal (T value) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked") protected static final Notifier CHANGE = new Notifier() {
        public void notify (Object lner, Object value, Object oldValue, Object ignored) {
            ((Listener<Object>)lner).onChange(value, oldValue);
        }
    };
}
