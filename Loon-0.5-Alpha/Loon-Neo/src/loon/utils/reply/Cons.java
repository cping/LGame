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

import java.lang.ref.WeakReference;

import loon.utils.reply.Bypass.GoListener;

class Cons extends Connection
{

    private Bypass _owner;
    private ListenerRef _ref;
    private boolean _oneShot; 
    private int _priority; 
    
    public Cons next;

    public final boolean oneShot () { return _oneShot; }

    public GoListener listener () {
        return _ref.get(this);
    }

    @Override public void close () {
        if (_owner != null) {
            _ref.defang(_owner.placeholderListener());
            _owner.disconnect(this);
            _owner = null;
        }
    }

    @Override public Connection once () {
        _oneShot = true;
        return this;
    }

    @Override public Connection setPriority (int priority) {
        if (_owner == null){ throw new IllegalStateException(
            "Cannot change priority of disconnected connection.");
        }
        _owner.disconnect(this);
        next = null;
        _priority = priority;
        _owner.addCons(this);
        return this;
    }

    @Override public Connection holdWeakly () {
        if (_owner == null){ throw new IllegalStateException(
            "Cannot change disconnected connection to weak.");
        }
        if (!_ref.isWeak()) {
        	_ref = new WeakRef(_ref.get(this));
        }
        return this;
    }

    @Override public String toString () {
        return "[owner=" + _owner + ", pri=" + _priority + ", lner=" + listener() +
            ", hasNext=" + (next != null) + ", oneShot=" + oneShot() + "]";
    }

    protected Cons (Bypass owner, GoListener listener) {
        _owner = owner;
        _ref = new StrongRef(listener);
    }

    private static abstract class ListenerRef {
        abstract boolean isWeak ();
        abstract void defang (GoListener def);
        abstract GoListener get (Cons cons);
    }

    private static class StrongRef extends ListenerRef {
        private GoListener _lner;
        public StrongRef (GoListener lner) { _lner = lner; }
        public boolean isWeak () { return false; }
        public void defang (GoListener def) { _lner = def; }
        public GoListener get (Cons cons) { return _lner; }
    }

    private static class WeakRef extends ListenerRef {
        private WeakReference<GoListener> _wref;
        private GoListener _def;
        public WeakRef (GoListener lner) { _wref = new WeakReference<GoListener>(lner); }
        public boolean isWeak () { return true; }
        public void defang (GoListener def) { _def = def; _wref = null; }
        public GoListener get (Cons cons) {
            if (_wref != null) {
                GoListener listener = _wref.get();
                if (listener != null){
                	return listener;
                }
                cons.close(); 
            }
            return _def;
        }
    }

    static Cons insert (Cons head, Cons cons) {
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

    static Cons remove (Cons head, Cons cons) {
        if (head == null) {
        	return head;
        }
        if (head == cons) {
        	return head.next;
        }
        head.next = remove(head.next, cons);
        return head;
    }

    static Cons removeAll (Cons head, GoListener listener) {
        if (head == null) {
        	return null;
        }
        if (head.listener() == listener) {
        	return removeAll(head.next, listener);
        }
        head.next = removeAll(head.next, listener);
        return head;
    }

}
