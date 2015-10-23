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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GoSet<E> extends GoCollection<E> implements Set<E>
{

    protected Set<E> _impl;

    protected static final Listener<Object> DEF = new Listener<Object>() {};

    public static abstract class Listener<E> implements Bypass.GoListener
    {
        public void onAdd (E elem) {
            
        }

        public void onRemove (E elem) {
            
        }
    }

    public static <E> GoSet<E> create () {
        return create(new HashSet<E>());
    }

    public static <E> GoSet<E> create (Set<E> impl) {
        return new GoSet<E>(impl);
    }

    public GoSet (Set<E> impl) {
        _impl = impl;
    }

    public Connection connect (Listener<? super E> listener) {
        return addConnection(listener);
    }

    public Connection connectNotify (Listener<? super E> listener) {
        for (E elem : this) listener.onAdd(elem);
        return connect(listener);
    }

    public void disconnect (Listener<? super E> listener) {
        removeConnection(listener);
    }

    public boolean addForce (E elem) {
        checkMutate();
        boolean added = _impl.add(elem);
        emitAdd(elem);
        return added;
    }
    
    public boolean removeForce (E elem) {
        checkMutate();
        boolean removed = _impl.remove(elem);
        emitRemove(elem);
        return removed;
    }

    public VarView<Boolean> containsView (final E elem) {
        if (elem == null) throw new NullPointerException("Must supply non-null 'elem'.");
        return new MappedValue<Boolean>() {
            @Override public Boolean get () {
                return contains(elem);
            }
            @Override protected Connection connect () {
                return GoSet.this.connect(new GoSet.Listener<E>() {
                    @Override public void onAdd (E aelem) {
                        if (elem.equals(aelem)) notifyChange(true, false);
                    }
                    @Override public void onRemove (E relem) {
                        if (elem.equals(relem)) notifyChange(false, true);
                    }
                });
            }
        };
    }

    
    public int size () {
        return _impl.size();
    }

    
    public boolean isEmpty () {
        return _impl.isEmpty();
    }

    
    public boolean contains (Object key) {
        return _impl.contains(key);
    }

    
    public boolean add (E elem) {
        checkMutate();
        if (!_impl.add(elem)) return false;
        emitAdd(elem);
        return true;
    }

    
    public boolean remove (Object rawElem) {
        checkMutate();
        if (!_impl.remove(rawElem)) return false;
        @SuppressWarnings("unchecked") E elem = (E)rawElem;
        emitRemove(elem);
        return true;
    }

    
    public boolean containsAll (Collection<?> coll) {
        return _impl.containsAll(coll);
    }

    
    public boolean addAll (Collection<? extends E> coll) {
        boolean modified = false;
        for (E elem : coll) {
            modified |= add(elem);
        }
        return modified;
    }

    
    public boolean retainAll (Collection<?> coll) {
        boolean modified = false;
        for (Iterator<E> iter = iterator(); iter.hasNext(); ) {
            if (!coll.contains(iter.next())) {
                iter.remove();
                modified = true;
            }
        }
        return modified;
    }

    
    public boolean removeAll (Collection<?> coll) {
        boolean modified = false;
        for (Iterator<?> iter = coll.iterator(); iter.hasNext(); ) {
            modified |= remove(iter.next());
        }
        return modified;
    }

    
    public void clear () {
        checkMutate();
        // generate removed events for our elemens (do so on a copy of our set so that we can clear
        // our underlying set before any of the published events are processed)
        List<E> elems = new ArrayList<E>(_impl);
        _impl.clear();
        for (E elem : elems) emitRemove(elem);
    }

    
    public Iterator<E> iterator () {
        final Iterator<E> iiter = _impl.iterator();
        return new Iterator<E>() {
            public boolean hasNext () {
                return iiter.hasNext();
            }
            public E next () {
                return (_current = iiter.next());
            }
            public void remove () {
                checkMutate();
                iiter.remove();
                emitRemove(_current);
            }
            protected E _current;
        };
    }

    
    public Object[] toArray () {
        return _impl.toArray();
    }

    
    public <T> T[] toArray (T[] array) {
        return _impl.toArray(array);
    }

    @Override public int hashCode () {
        return _impl.hashCode();
    }

    @Override public boolean equals (Object other) {
        return other == this || _impl.equals(other);
    }

    @Override public String toString () {
        return "RSet" + _impl;
    }

    @Override Listener<E> placeholderListener () {
		@SuppressWarnings("unchecked")
		Listener<E> p = (Listener<E>) DEF;
        return p;
    }

    protected void emitAdd (E elem) {
        notifyAdd(elem);
    }

    protected void notifyAdd (E elem) {
        notify(ADD, elem, null, null);
    }

    protected void emitRemove (E elem) {
        notifyRemove(elem);
    }

    protected void notifyRemove (E elem) {
        notify(REMOVE, elem, null, null);
    }

    @SuppressWarnings("unchecked") protected static final Notifier ADD = new Notifier() {
        public void notify (Object lner, Object elem, Object _1, Object _2) {
            ((Listener<Object>)lner).onAdd(elem);
        }
    };

    @SuppressWarnings("unchecked") protected static final Notifier REMOVE = new Notifier() {
        public void notify (Object lner, Object elem, Object _1, Object _2) {
            ((Listener<Object>)lner).onRemove(elem);
        }
    };
}
