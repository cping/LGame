/**
 * Copyright 2013 The Loon Authors
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
 */
package loon.core;

import loon.core.event.Updateable;

public abstract class CallQueue {

	public class RunQueue {

		private int _count;
		
		private Entry _head;

		private class Entry {
			public final Updateable update;
			public Entry next;

			public Entry(Updateable update) {
				this.update = update;
			}
		}

		public RunQueue() {
		}

		public void execute() {
			if(_count == 0){
				return;
			}
			Entry head;
			synchronized (this) {
				head = this._head;
				this._head = null;
			}
			for (;head != null;) {
				try {
					head.update.action();
				} catch (Throwable t) {
				}
				head = head.next;
			}
			_count = 0;
		}

		public synchronized void add(Updateable update) {
			if (_head == null) {
				_head = new Entry(update);
			} else {
				Entry parent = _head;
				while (parent.next != null) {
					parent = parent.next;
				}
				parent.next = new Entry(update);
			}
			_count++;
		}
	}

	protected final RunQueue _queue;

	protected CallQueue() {
		this._queue = new RunQueue();
	}

	public void invokeLater(Updateable update) {
		_queue.add(update);
	}

	public <T> void notifySuccess(final Callback<T> callback, final T result) {
		invokeLater(new Updateable() {
			@Override
			public void action() {
				callback.onSuccess(result);
			}
		});
	}

	public void notifyFailure(final Callback<?> callback, final Throwable error) {
		invokeLater(new Updateable() {
			@Override
			public void action() {
				callback.onFailure(error);
			}
		});
	}

	public abstract void invokeAsync(Updateable action);
}
