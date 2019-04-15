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

import loon.LSystem;

public class GoPromise<T> extends GoFuture<T> {

	protected final Var<Try<T>> _result;

	public static <T> GoPromise<T> create() {
		return new GoPromise<T>();
	}

	public void succeed(T value) {
		_result.update(Try.success(value));
	}

	public void fail(Throwable cause) {
		_result.update(Try.<T> failure(cause));
	}

	public Port<Try<T>> completer() {
		return _result.port();
	}

	public Port<T> succeeder() {
		return new Port<T>() {
			public void onEmit(T result) {
				succeed(result);
			}
		};
	}

	public Port<Throwable> failer() {
		return new Port<Throwable>() {
			public void onEmit(Throwable cause) {
				fail(cause);
			}
		};
	}

	public boolean hasConnections() {
		return _result.hasConnections();
	}

	protected GoPromise() {
		this(new Var<Try<T>>(null) {
			@Override
			protected synchronized Try<T> updateAndNotify(Try<T> value,
					boolean force) {
				if (_value != null){
					throw LSystem.runThrow("already completed");
				}
				try {
					return super.updateAndNotify(value, force);
				} finally {
					_listeners = null;
				}
			}
		});
	}

	private GoPromise(Var<Try<T>> result) {
		super(result);
		_result = result;
	}

}
