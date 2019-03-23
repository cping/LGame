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

public abstract class Try<T> {

	public static final class Success<T> extends Try<T> {
		public final T value;

		public Success(T value) {
			this.value = value;
		}

		@Override
		public T get() {
			return value;
		}

		@Override
		public Throwable getFailure() {
			throw new IllegalStateException();
		}

		@Override
		public boolean isSuccess() {
			return true;
		}

		@Override
		public <R> Try<R> map(Function<? super T, R> func) {
			try {
				return success(func.apply(value));
			} catch (Throwable t) {
				return failure(t);
			}
		}

		@Override
		public <R> Try<R> flatMap(Function<? super T, Try<R>> func) {
			try {
				return func.apply(value);
			} catch (Throwable t) {
				return failure(t);
			}
		}

		@Override
		public String toString() {
			return "Success(" + value + ")";
		}
	}

	public static final class Failure<T> extends Try<T> {
		public final Throwable cause;

		public Failure(Throwable cause) {
			this.cause = cause;
		}

		@Override
		public T get() {
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			} else if (cause instanceof Error) {
				throw (Error) cause;
			} else {
				throw (RuntimeException) new RuntimeException().initCause(cause);
			}
		}

		@Override
		public Throwable getFailure() {
			return cause;
		}

		@Override
		public boolean isSuccess() {
			return false;
		}

		@Override
		public <R> Try<R> map(Function<? super T, R> func) {
			return this.<R>casted();
		}

		@Override
		public <R> Try<R> flatMap(Function<? super T, Try<R>> func) {
			return this.<R>casted();
		}

		@Override
		public String toString() {
			return "Failure(" + cause + ")";
		}

		@SuppressWarnings("unchecked")
		private <R> Try<R> casted() {
			return (Try<R>) this;
		}
	}

	public static <T> Try<T> success(T value) {
		return new Success<T>(value);
	}

	public static <T> Try<T> failure(Throwable cause) {
		return new Failure<T>(cause);
	}

	public static <T, R> Function<Try<T>, Try<R>> lift(final Function<? super T, R> func) {
		return new Function<Try<T>, Try<R>>() {
			public Try<R> apply(Try<T> result) {
				return result.map(func);
			}
		};
	}

	public abstract T get();

	public abstract Throwable getFailure();

	public abstract boolean isSuccess();

	public boolean isFailure() {
		return !isSuccess();
	}

	public abstract <R> Try<R> map(Function<? super T, R> func);

	public abstract <R> Try<R> flatMap(Function<? super T, Try<R>> func);

	private Try() {
	}
}
