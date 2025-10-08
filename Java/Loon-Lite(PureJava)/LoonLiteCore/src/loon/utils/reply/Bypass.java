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

import loon.LSysException;
import loon.events.Updateable;

public abstract class Bypass {

	protected static abstract class Runs implements Updateable {

		public Runs next;

		@Override
		public abstract void action(final Object a);
	}

	protected static abstract class Notifier<T> {

		public abstract void notify(final Bypass.GoListener listener, final T a1, final T a2, final T a3);

	}

	public interface GoListener {
	}

	protected static final Cons DISPATCHING = new Cons(null, null);

	protected Cons _listeners;
	protected Runs _pendingRuns;

	public abstract GoListener defaultListener();

	protected static <T> boolean areEqual(final T o1, final T o2) {
		return (o1 == o2 || (o1 != null && o1.equals(o2)));
	}

	protected static Runs append(final Runs head, final Runs action) {
		if (head == null)
			return action;
		head.next = append(head.next, action);
		return head;
	}

	protected void checkMutate() {
	}

	protected void connectionAdded() {
	}

	protected void connectionRemoved() {
	}

	public boolean hasConnections() {
		return _listeners != null;
	}

	public synchronized void clearConnections() {
		if (isDispatching()) {
			throw new LSysException("system dispatching");
		}
		_listeners = null;
	}

	protected synchronized Cons addConnection(final GoListener listener) {
		if (listener == null)
			throw new LSysException("null listener");
		return addCons(new Cons(this, listener));
	}

	protected synchronized Cons addCons(final Cons cons) {
		if (isDispatching()) {
			_pendingRuns = append(_pendingRuns, new Runs() {
				@Override
				public void action(final Object o) {
					_listeners = Cons.insert(_listeners, cons);
					connectionAdded();
				}
			});
		} else {
			_listeners = Cons.insert(_listeners, cons);
			connectionAdded();
		}
		return cons;
	}

	protected synchronized void disconnect(final Cons cons) {
		if (isDispatching()) {
			_pendingRuns = append(_pendingRuns, new Runs() {
				@Override
				public void action(final Object o) {
					_listeners = Cons.remove(_listeners, cons);
					connectionRemoved();
				}
			});
		} else {
			_listeners = Cons.remove(_listeners, cons);
			connectionRemoved();
		}
	}

	protected synchronized void removeConnection(final GoListener listener) {
		if (isDispatching()) {
			_pendingRuns = append(_pendingRuns, new Runs() {
				@Override
				public void action(final Object o) {
					_listeners = Cons.removeAll(_listeners, listener);
					connectionRemoved();
				}
			});
		} else {
			_listeners = Cons.removeAll(_listeners, listener);
			connectionRemoved();
		}
	}

	protected <T> void notify(final Notifier<T> notifier, final T a1, final T a2, final T a3) {
		Cons lners;
		synchronized (this) {
			if (_listeners == DISPATCHING) {
				_pendingRuns = append(_pendingRuns, new Runs() {
					@Override
					public void action(final Object o) {
						Bypass.this.notify(notifier, a1, a2, a3);
					}
				});
				return;
			}
			lners = _listeners;
			Cons sentinel = DISPATCHING;
			_listeners = sentinel;
		}
		RuntimeException exn = null;
		try {
			for (Cons cons = lners; cons != null; cons = cons.next) {
				try {
					notifier.notify(cons.listener(), a1, a2, a3);
				} catch (RuntimeException ex) {
					exn = ex;
				}
				if (cons.oneShot()) {
					cons.close();
				}
			}

		} finally {
			synchronized (this) {
				_listeners = lners;
			}
			Runs run;
			for (; (run = nextRun()) != null;) {
				try {
					run.action(this);
				} catch (RuntimeException ex) {
					exn = ex;
				}
			}
		}
		if (exn != null) {
			throw exn;
		}
	}

	private synchronized Runs nextRun() {
		Runs run = _pendingRuns;
		if (run != null) {
			_pendingRuns = run.next;
		}
		return run;
	}

	private final boolean isDispatching() {
		return _listeners == DISPATCHING;
	}

	public boolean isClosed() {
		return isDispatching();
	}

}
