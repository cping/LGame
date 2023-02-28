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
import loon.utils.StringKeyValue;
import loon.utils.reply.Bypass.GoListener;

public class Cons extends Connection {

	private Bypass _owner;
	private ListenerRef _ref;
	private boolean _oneShot = false;
	private boolean _closed = false;
	private int _priority;

	public Cons next;

	public final boolean oneShot() {
		return _oneShot;
	}

	public GoListener listener() {
		return _ref.get(this);
	}

	@Override
	public void close() {
		if (_owner != null) {
			_ref.defang(_owner.defaultListener());
			_owner.disconnect(this);
			_owner = null;
		}
		_closed = true;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public Connection once() {
		_oneShot = true;
		return this;
	}

	@Override
	public Connection setPriority(int priority) {
		if (_owner == null) {
			throw new LSysException("cannot change priority of disconnected connection.");
		}
		_owner.disconnect(this);
		next = null;
		_priority = priority;
		_owner.addCons(this);
		return this;
	}

	protected Cons(Bypass owner, GoListener listener) {
		_owner = owner;
		_ref = new StrongRef(listener);
	}

	private static abstract class ListenerRef {

		public abstract boolean isWeak();

		public abstract void defang(GoListener def);

		public abstract GoListener get(Cons cons);
	}

	private static class StrongRef extends ListenerRef {

		private GoListener _lner;

		public StrongRef(GoListener lner) {
			_lner = lner;
		}

		@Override
		public boolean isWeak() {
			return false;
		}

		@Override
		public void defang(GoListener def) {
			_lner = def;
		}

		@Override
		public GoListener get(Cons cons) {
			return _lner;
		}
	}

	protected static Cons insert(Cons head, Cons cons) {
		if (head == null) {
			return cons;
		} else if (cons._priority > head._priority) {
			cons.next = head;
			return cons;
		} else {
			head.next = insert(head.next, cons);
			return head;
		}
	}

	protected static Cons remove(Cons head, Cons cons) {
		if (head == null) {
			return head;
		}
		if (head == cons) {
			return head.next;
		}
		head.next = remove(head.next, cons);
		return head;
	}

	protected static Cons removeAll(Cons head, GoListener listener) {
		if (head == null) {
			return null;
		}
		if (head.listener() == listener) {
			return removeAll(head.next, listener);
		}
		head.next = removeAll(head.next, listener);
		return head;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("Cons");
		builder.kv("owner", _owner).comma().kv("priority", _priority).comma().kv("listener", listener()).comma()
				.kv("hasNext", (next != null)).comma().kv("oneShot", oneShot());
		return builder.toString();
	}

}
