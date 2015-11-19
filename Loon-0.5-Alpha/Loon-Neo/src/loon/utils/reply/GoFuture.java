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

public class GoFuture<T> {

    protected final VarView<Try<T>> _result;
    protected VarView<Boolean> _isComplete;


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
