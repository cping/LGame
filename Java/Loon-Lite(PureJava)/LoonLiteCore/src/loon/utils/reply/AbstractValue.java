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

public abstract class AbstractValue<T> extends Bypass implements VarView<T> {
	@Override
	public <M> VarView<M> map(final Function<T, M> func) {
		final AbstractValue<T> outer = this;
		return new MappedValue<M>() {
			@Override
			public M get() {
				return func.apply(outer.get());
			}

			@Override
			public String toString() {
				
				return outer + ".map(" + func + ")";
			}

			@Override
			protected Connection connect() {
				return outer.connect(new VarViewListener<T>() {
					@Override
					public void onChange(T value, T ovalue) {
						notifyChange(func.apply(value), func.apply(ovalue));
					}
				});
			}
		};
	}

	@Override
	public Connection connect(VarViewListener<? super T> listener) {
		return addConnection(listener);
	}

	@Override
	public Connection connect(ActViewListener<? super T> listener) {
		return connect(wrap(listener));
	}

	private static <T> VarViewListener<T> wrap(final ActViewListener<? super T> listener) {
		return new VarViewListener<T>() {
			public void onChange(T newValue, T oldValue) {
				listener.onEmit(newValue);
			}
		};
	}

	@Override
	public Connection connect(Port<? super T> port) {
		return connect((VarViewListener<? super T>) port);
	}

	@Override
	public void disconnect(VarViewListener<? super T> listener) {
		removeConnection(listener);
	}

	@Override
	public int hashCode() {
		T value = get();
		return (value == null) ? 0 : value.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other.getClass() != getClass())
			return false;
		T value = get();
		@SuppressWarnings("unchecked")
		T ovalue = ((AbstractValue<T>) other).get();
		return areEqual(value, ovalue);
	}

	@Override
	public String toString() {
		String cname = getClass().getName();
		return cname.substring(cname.lastIndexOf(".") + 1) + "(" + get() + ")";
	}

	@Override
	public GoListener defaultListener() {
		@SuppressWarnings("unchecked")
		VarViewListener<T> p = (VarViewListener<T>) AbstractAct.DEF;
		return p;
	}

	protected T updateAndNotifyIf(T value) {
		return updateAndNotify(value, false);
	}

	protected T updateAndNotify(T value) {
		return updateAndNotify(value, true);
	}

	protected T updateAndNotify(T value, boolean force) {
		checkMutate();
		T ovalue = updateLocal(value);
		if (force || !areEqual(value, ovalue)) {
			emitChange(value, ovalue);
		}
		return ovalue;
	}

	protected void emitChange(T value, T oldValue) {
		notifyChange(value, oldValue);
	}

	protected void notifyChange(T value, T oldValue) {
		notify(CHANGE, value, oldValue, null);
	}

	protected T updateLocal(T value) {
		throw new UnsupportedOperationException();
	}

	protected final Notifier<T> CHANGE = new Notifier<T>() {
		@SuppressWarnings("unchecked")
		@Override
		public void notify(Bypass.GoListener lner, T value, T oldValue, T ignored) {
			((VarViewListener<T>) lner).onChange(value, oldValue);
		}
	};
}
