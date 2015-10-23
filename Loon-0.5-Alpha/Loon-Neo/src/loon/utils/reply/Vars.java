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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class Vars
{
    public static class T2<A,B> {
        public final A a;
        public final B b;
        public T2 (A a, B b) {
            this.a = a;
            this.b = b;
        }

        @Override public String toString () {
            return "T2(" + a + ", " + b + ")";
        }
        @Override public int hashCode () {
            return Objects.hashCode(a) ^ Objects.hashCode(b);
        }
        @Override public boolean equals (Object other) {
            if (!(other instanceof T2<?,?>)) return false;
            T2<?,?> ot = (T2<?,?>)other;
            return Objects.equals(a, ot.a) && Objects.equals(b, ot.b);
        }
    }

    public static class T3<A,B,C> {
        public final A a;
        public final B b;
        public final C c;
        public T3 (A a, B b, C c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        @Override public String toString () {
            return "T3(" + a + ", " + b + ", " + c + ")";
        }
        @Override public int hashCode () {
            return Objects.hashCode(a) ^ Objects.hashCode(b);
        }
        @Override public boolean equals (Object other) {
            if (!(other instanceof T3<?,?,?>)) return false;
            T3<?,?,?> ot = (T3<?,?,?>)other;
            return Objects.equals(a, ot.a) && Objects.equals(b, ot.b) && Objects.equals(c, ot.c);
        }
    }

    public static <A,B> VarView<T2<A,B>> join (final VarView<A> a, final VarView<B> b) {
        return new MappedValue<T2<A,B>>() {
            @Override public T2<A,B> get () {
                return _current;
            }
            @Override protected Connection connect () {
                return Connection.join(a.connect(_trigger), b.connect(_trigger));
            }
            protected final UnitPort _trigger = new UnitPort() {
                public void onEmit () {
                    T2<A,B> ovalue = _current;
                    _current = new T2<A,B>(a.get(), b.get());
                    notifyChange(_current, ovalue);
                }
            };
            protected T2<A,B> _current = new T2<A,B>(a.get(), b.get());
        };
    }

    public static <A,B,C> VarView<T3<A,B,C>> join (final VarView<A> a, final VarView<B> b,
                                                     final VarView<C> c) {
        return new MappedValue<T3<A,B,C>>() {
            @Override public T3<A,B,C> get () {
                return _current;
            }
            @Override protected Connection connect () {
                return Connection.join(
                    a.connect(_trigger), b.connect(_trigger), c.connect(_trigger));
            }
            protected final UnitPort _trigger = new UnitPort() {
                public void onEmit () {
                    T3<A,B,C> ovalue = _current;
                    _current = new T3<A,B,C>(a.get(), b.get(), c.get());
                    notifyChange(_current, ovalue);
                }
            };
            protected T3<A,B,C> _current = new T3<A,B,C>(a.get(), b.get(), c.get());
        };
    }

    public static VarView<Boolean> toggler (final ActView<?> signal, final boolean initial) {
        return new MappedValue<Boolean>() {
            @Override public Boolean get () {
                return _current;
            }
            @Override protected Connection connect () {
                return signal.connect(new UnitPort() {
                    @Override public void onEmit () {
                        boolean old = _current;
                        notifyChange(_current = !old, old);
                    }
                });
            }
            protected boolean _current = initial;
        };
    }

    public static VarView<Boolean> not (VarView<Boolean> value) {
        return value.map(Functions.NOT);
    }

    public static VarView<Boolean> and (VarView<Boolean> one, VarView<Boolean> two) {
        return and(Arrays.asList(one, two));
    }

    @SafeVarargs
    public static VarView<Boolean> and (VarView<Boolean>... values) {
        return and(Arrays.asList(values));
    }

    public static VarView<Boolean> and (final Collection<? extends VarView<Boolean>> values) {
        return aggValue(values, COMPUTE_AND);
    }

    public static VarView<Boolean> or (VarView<Boolean> one, VarView<Boolean> two) {
        return or(Arrays.asList(one, two));
    }

    @SafeVarargs 
    public static VarView<Boolean> or (VarView<Boolean>... values) {
        return or(Arrays.asList(values));
    }

    public static VarView<Boolean> or (final Collection<? extends VarView<Boolean>> values) {
        return aggValue(values, COMPUTE_OR);
    }

    public static <T> VarView<T> asValue (final ActView<T> signal, final T initial) {
        return new MappedValue<T>() {
            @Override public T get () {
                return _value;
            }
            @Override protected T updateLocal (T value) {
                T ovalue = _value;
                _value = value;
                return ovalue;
            }
            @Override protected Connection connect () {
                return signal.connect(new Port<T>() {
                    public void onEmit (T value) {
                        updateAndNotifyIf(value);
                    }
                });
            }
            protected T _value = initial;
        };
    }

    protected static final VarView<Boolean> aggValue (
        final Collection<? extends VarView<Boolean>> values,
        final Function<Iterable<? extends VarView<Boolean>>,Boolean> aggOp) {

        return new MappedValue<Boolean>() {
            @Override public Boolean get () {
                return aggOp.apply(values);
            }

            @Override protected Connection connect () {
                Connection[] conns = new Connection[values.size()];
                Iterator<? extends VarView<Boolean>> iter = values.iterator();
                for (int ii = 0; ii < conns.length; ii++) conns[ii] = iter.next().connect(_trigger);
                return Connection.join(conns);
            }

            protected final UnitPort _trigger = new UnitPort() {
                public void onEmit () {
                    boolean ovalue = _current;
                    _current = aggOp.apply(values);
                    notifyChange(_current, ovalue);
                }
                protected boolean _current = aggOp.apply(values);
            };
        };
    }

    protected static final Function<Iterable<? extends VarView<Boolean>>,Boolean> COMPUTE_AND =
        new Function<Iterable<? extends VarView<Boolean>>,Boolean>() {
            public Boolean apply (Iterable<? extends VarView<Boolean>> values) {
                for (VarView<Boolean> value : values) {
                    if (!value.get()) return false;
                }
                return true;
            }
        };

    protected static final Function<Iterable<? extends VarView<Boolean>>,Boolean> COMPUTE_OR =
        new Function<Iterable<? extends VarView<Boolean>>,Boolean>() {
            public Boolean apply (Iterable<? extends VarView<Boolean>> values) {
                for (VarView<Boolean> value : values) {
                    if (value.get()) return true;
                }
                return false;
            }
        };

    private Vars () {} 
}
