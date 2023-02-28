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

	public final static UnitPort DEF = new UnitPort() {
		public void onEmit() {
		}
	};

	@Override
	public <M> ActView<M> map(final Function<T, M> func) {
		final AbstractAct<T> outer = this;
		return new MappedAct<M>() {
			@Override
			protected Connection connect() {
				return outer.connect(new ActViewListener<T>() {
					@Override
					public void onEmit(T value) {
						notifyEmit(func.apply(value));
					}
				});
			}
		};
	}

	@Override
	public ActView<T> filter(final Function<T, Boolean> pred) {
		final AbstractAct<T> outer = this;
		return new MappedAct<T>() {
			@Override
			protected Connection connect() {
				return outer.connect(new ActViewListener<T>() {
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
	public Connection connect(ActViewListener<? super T> port) {
		return addConnection(port);
	}

	@Override
	public void disconnect(ActViewListener<? super T> port) {
		removeConnection(port);
	}

	@Override
	public GoListener defaultListener() {
		@SuppressWarnings("unchecked")
		ActViewListener<T> p = (ActViewListener<T>) AbstractAct.DEF;
		return p;
	}

	protected void notifyEmit(T e) {
		notify(EMIT, e, null, null);
	}

	protected final Notifier<T> EMIT = new Notifier<T>() {
		@SuppressWarnings("unchecked")
		@Override
		public void notify(Bypass.GoListener port, T a1, T a2, T a3) {
			((ActViewListener<T>) port).onEmit(a1);
		}
	};
}
