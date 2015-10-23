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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class GoFuture<T> {

    protected final VarView<Try<T>> _result;
    protected VarView<Boolean> _isComplete;
    
    public static class T2<A,B> {
        public final A a;
        public final B b;
        public T2 (A a, B b) {
            this.a = a;
            this.b = b;
        }

        @Override public int hashCode () {
            return Objects.hashCode(a) ^ Objects.hashCode(b);
        }
        @Override public boolean equals (Object other) {
            if (!(other instanceof T2<?,?>)) return false;
            T2<?,?> ot = (T2<?,?>)other;
            return Objects.equals(a, ot.a) && Objects.equals(a, ot.b);
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

        @Override public int hashCode () {
            return Objects.hashCode(a) ^ Objects.hashCode(b);
        }
        @Override public boolean equals (Object other) {
            if (!(other instanceof T3<?,?,?>)) {
            	return false;
            }
            T3<?,?,?> ot = (T3<?,?,?>)other;
            return Objects.equals(a, ot.a) && Objects.equals(a, ot.b) && Objects.equals(c, ot.c);
        }
    }

    public static <T> GoFuture<T> success (T value) {
        return result(Try.success(value));
    }

    public static GoFuture<Void> success () {
        return success(null);
    }

    public static <T> GoFuture<T> failure (Throwable cause) {
        return result(Try.<T>failure(cause));
    }

    public static <T> GoFuture<T> result (Try<T> result) {
        return new GoFuture<T>(Var.create(result));
    }

    public static <T> GoFuture<List<T>> sequence (Collection<? extends GoFuture<T>> futures) {
        if (futures.isEmpty()){
        	return GoFuture.success(Collections.<T>emptyList());
        }
        final GoPromise<List<T>> pseq = GoPromise.create();
        final int count = futures.size();
        class Sequencer {
            public synchronized void onResult (int idx, Try<T> result) {
                if (result.isSuccess()) {
                    _results[idx] = result.get();
                } else {
                    if (_error == null) _error = new ManyFailure();
                    _error.addFailure(result.getFailure());
                }
                if (--_remain == 0) {
                    if (_error != null) {
                    	pseq.fail(_error);
                    }
                    else {
                        @SuppressWarnings("unchecked") T[] results = (T[])_results;
                        pseq.succeed(Arrays.asList(results));
                    }
                }
            }
            protected final Object[] _results = new Object[count];
            protected int _remain = count;
            protected ManyFailure _error;
        }
        final Sequencer seq = new Sequencer();
        Iterator<? extends GoFuture<T>> iter = futures.iterator();
        for (int ii = 0; iter.hasNext(); ii++) {
            final int idx = ii;
            iter.next().onComplete(new ActView.Listener<Try<T>>() {
                public void onEmit (Try<T> result) { seq.onResult(idx, result); }
            });
        }
        return pseq;
    }

    public static <A,B> GoFuture<T2<A,B>> sequence (GoFuture<A> a, GoFuture<B> b) {
        @SuppressWarnings("unchecked") GoFuture<Object> oa = (GoFuture<Object>)a;
        @SuppressWarnings("unchecked") GoFuture<Object> ob = (GoFuture<Object>)b;
        return sequence(Arrays.asList(oa, ob)).map(new Function<List<Object>,T2<A,B>>() {
            public T2<A,B> apply (List<Object> results) {
                @SuppressWarnings("unchecked") A a = (A)results.get(0);
                @SuppressWarnings("unchecked") B b = (B)results.get(1);
                return new T2<A,B>(a, b);
            }
        });
    }

    public static <A,B,C> GoFuture<T3<A,B,C>> sequence (GoFuture<A> a, GoFuture<B> b, GoFuture<B> c) {
        @SuppressWarnings("unchecked") GoFuture<Object> oa = (GoFuture<Object>)a;
        @SuppressWarnings("unchecked") GoFuture<Object> ob = (GoFuture<Object>)b;
        @SuppressWarnings("unchecked") GoFuture<Object> oc = (GoFuture<Object>)c;
        return sequence(Arrays.asList(oa, ob, oc)).map(new Function<List<Object>,T3<A,B,C>>() {
            public T3<A,B,C> apply (List<Object> results) {
                @SuppressWarnings("unchecked") A a = (A)results.get(0);
                @SuppressWarnings("unchecked") B b = (B)results.get(1);
                @SuppressWarnings("unchecked") C c = (C)results.get(2);
                return new T3<A,B,C>(a, b, c);
            }
        });
    }

    public static <T> GoFuture<Collection<T>> collect (Collection<? extends GoFuture<T>> futures) {
        if (futures.isEmpty()) {
        	return GoFuture.<Collection<T>>success(Collections.<T>emptyList());
        }
        final GoPromise<Collection<T>> pseq = GoPromise.create();
        final int count = futures.size();
        ActView.Listener<Try<T>> collector = new ActView.Listener<Try<T>>() {
            protected final List<T> _results = new ArrayList<T>();
            protected int _remain = count;
            public synchronized void onEmit (Try<T> result) {
                if (result.isSuccess()) {
                	_results.add(result.get());
                }
                if (--_remain == 0) {
                	pseq.succeed(_results);
                }
            }
        };
        for (GoFuture<T> future : futures){
        	future.onComplete(collector);
        }
        return pseq;
    }

    public GoFuture<T> onSuccess (final ActView.Listener<? super T> slot) {
        Try<T> result = _result.get();
        if (result == null) _result.connect(new ActView.Listener<Try<T>>() {
            public void onEmit (Try<T> result) {
                if (result.isSuccess()) slot.onEmit(result.get());
            }
        });
        else if (result.isSuccess()) slot.onEmit(result.get());
        return this;
    }

    public GoFuture<T> onFailure (final ActView.Listener<? super Throwable> slot) {
        Try<T> result = _result.get();
        if (result == null) _result.connect(new ActView.Listener<Try<T>>() {
            public void onEmit (Try<T> result) {
                if (result.isFailure()) slot.onEmit(result.getFailure());
            }
        });
        else if (result.isFailure()) slot.onEmit(result.getFailure());
        return this;
    }

    public GoFuture<T> onComplete (final ActView.Listener<? super Try<T>> slot) {
        Try<T> result = _result.get();
        if (result == null) {
        	_result.connect(slot);
        }
        else slot.onEmit(result);
        return this;
    }

    public VarView<Boolean> isComplete () {
        if (_isComplete == null){
        	_isComplete = _result.map(Functions.NON_NULL);
        }
        return _isComplete;
    }

    public boolean isCompleteNow () {
        return _result.get() != null;
    }

    public GoFuture<T> bindComplete (ActView.Listener<Boolean> slot) {
        isComplete().connectNotify(slot);
        return this;
    }

    public <R> GoFuture<R> map (final Function<? super T, R> func) {
        return new GoFuture<R>(_result.map(new Function<Try<T>,Try<R>>() {
            public Try<R> apply (Try<T> result) {
                return result == null ? null : result.map(func);
            }
        }));
    }

    public <R> GoFuture<R> flatMap (final Function<? super T, GoFuture<R>> func) {
        final Var<Try<R>> mapped = Var.create(null);
        _result.connectNotify(new ActView.Listener<Try<T>>() {
            public void onEmit (Try<T> result) {
                if (result == null){
                	return;
                }
                if (result.isFailure()) {
                	mapped.update(Try.<R>failure(result.getFailure()));
                }
                else{
                	func.apply(result.get()).onComplete(mapped.port());
                }
            }
        });
        return new GoFuture<R>(mapped);
    }

    public Try<T> result () {
        return _result.get();
    }

    protected GoFuture (VarView<Try<T>> result) {
        _result = result;
    }

}
