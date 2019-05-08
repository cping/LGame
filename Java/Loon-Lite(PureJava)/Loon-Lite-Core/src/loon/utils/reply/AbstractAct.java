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

/**
 * 处理传递过来的具体数据，将其依照规则过滤后，触发指定的事件反馈.
 */
public class AbstractAct<T> extends Bypass implements ActView<T> {
	
    public static UnitPort DEF = new UnitPort() {
        public void onEmit () {} 
    };
	
	@Override
	public <M> ActView<M> map(final Function<? super T, M> func) {
		final AbstractAct<T> outer = this;
		return new MappedAct<M>() {
			@Override
			protected Connection connect() {
				return outer.connect(new Listener<T>() {
					@Override
					public void onEmit(T value) {
						notifyEmit(func.apply(value));
					}
				});
			}
		};
	}

	@Override
	public ActView<T> filter(final Function<? super T, Boolean> pred) {
		final AbstractAct<T> outer = this;
		return new MappedAct<T>() {
			@Override
			protected Connection connect() {
				return outer.connect(new Listener<T>() {
					@Override
					public void onEmit(T value) {
						if (pred.apply(value)) {
							notifyEmit(value);
						}
					}
				});
			}
		};
	}

	@Override
	public Connection connect(Listener<? super T> port) {
		return addConnection(port);
	}

	@Override
	public void disconnect(Listener<? super T> port) {
		removeConnection(port);
	}

	@Override
	Listener<T> defaultListener() {
		@SuppressWarnings("unchecked")
		Listener<T> p = (Listener<T>) AbstractAct.DEF;
		return p;
	}

	protected void notifyEmit(T event) {
		notify(EMIT, event, null, null);
	}

	@SuppressWarnings("unchecked")
	protected static final Notifier EMIT = new Notifier() {
		public void notify(Object port, Object event, Object _1, Object _2) {
			((Listener<Object>) port).onEmit(event);
		}
	};
}
