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

import loon.utils.reply.ActView.ActViewListener;

public class GoFuture<T> {

	protected final VarView<Try<T>> _result;
	protected VarView<Boolean> _isComplete;

	public static <T> GoFuture<T> success(final T value) {
		return result(Try.createSuccess(value));
	}

	public static GoFuture<Object> success() {
		return success(null);
	}

	public static <T> GoFuture<T> failure(final Throwable cause) {
		return result(Try.<T>createFailure(cause));
	}

	public static <T> GoFuture<T> result(final Try<T> result) {
		return new GoFuture<T>(Var.create(result));
	}

	public GoFuture<T> onSuccess(final ActViewListener<? super T> slot) {
		Try<T> result = _result.get();
		if (result == null)
			_result.connect(new ActViewListener<Try<T>>() {
				@Override
				public void onEmit(final Try<T> result) {
					if (result.isSuccess()) {
						slot.onEmit(result.get());
					}
				}
			});
		else if (result.isSuccess()) {
			slot.onEmit(result.get());
		}
		return this;
	}

	public GoFuture<T> onFailure(final ActViewListener<? super Throwable> slot) {
		Try<T> result = _result.get();
		if (result == null)
			_result.connect(new ActViewListener<Try<T>>() {
				public void onEmit(final Try<T> result) {
					if (result.isFailure()) {
						slot.onEmit(result.getFailure());
					}
				}
			});
		else if (result.isFailure()) {
			slot.onEmit(result.getFailure());
		}
		return this;
	}

	public GoFuture<T> onComplete(final ActViewListener<? super Try<T>> slot) {
		Try<T> result = _result.get();
		if (result == null) {
			_result.connect(slot);
		} else {
			slot.onEmit(result);
		}
		return this;
	}

	public boolean isCompleteNow() {
		return _result.get() != null;
	}

	public <R> GoFuture<R> map(final Function<? super T, R> func) {
		return new GoFuture<R>(_result.map(new Function<Try<T>, Try<R>>() {
			@Override
			public Try<R> apply(final Try<T> result) {
				return result == null ? null : result.map(func);
			}
		}));
	}

	public Try<T> result() {
		return _result.get();
	}

	protected GoFuture(final VarView<Try<T>> result) {
		_result = result;
	}

}
