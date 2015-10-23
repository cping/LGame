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

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class GoMap<K, V> extends GoCollection<Map.Entry<K, V>> implements
		Map<K, V> {
	protected Map<K, V> _impl;

	protected static final Listener<Object, Object> DEF = new Listener<Object, Object>() {
	};

	public static abstract class Listener<K, V> implements Bypass.GoListener {

		public void onPut(K key, V value, V oldValue) {
			onPut(key, value);
		}

		public void onPut(K key, V value) {

		}

		public void onRemove(K key, V oldValue) {
			onRemove(key);
		}

		public void onRemove(K key) {
		}
	}

	public static <K, V> GoMap<K, V> create() {
		return create(new HashMap<K, V>());
	}

	public static <K, V> GoMap<K, V> create(Map<K, V> impl) {
		return new GoMap<K, V>(impl);
	}

	public GoMap(Map<K, V> impl) {
		_impl = impl;
	}

	public Connection connect(Listener<? super K, ? super V> listener) {
		return addConnection(listener);
	}

	public Connection connectNotify(Listener<? super K, ? super V> listener) {
		for (Map.Entry<K, V> entry : entrySet()) {
			listener.onPut(entry.getKey(), entry.getValue(), null);
		}
		return connect(listener);
	}

	public void disconnect(Listener<? super K, ? super V> listener) {
		removeConnection(listener);
	}

	public V getOrElse(K key, V defaultValue) {
		V value = _impl.get(key);
		return (value == null) ? defaultValue : value;
	}

	public V putForce(K key, V value) {
		checkMutate();
		V ovalue = _impl.put(key, value);
		emitPut(key, value, ovalue);
		return ovalue;
	}

	public V removeForce(K key) {
		checkMutate();
		V ovalue = _impl.remove(key);
		emitRemove(key, ovalue);
		return ovalue;
	}

	public VarView<Boolean> containsKeyView(final K key) {
		if (key == null)
			throw new NullPointerException("Must supply non-null 'key'.");
		return new MappedValue<Boolean>() {
			@Override
			public Boolean get() {
				return containsKey(key);
			}

			@Override
			protected Connection connect() {
				return GoMap.this.connect(new GoMap.Listener<K, V>() {
					@Override
					public void onPut(K pkey, V value, V ovalue) {
						if (key.equals(pkey) && ovalue == null)
							notifyChange(true, false);
					}

					@Override
					public void onRemove(K rkey, V ovalue) {
						if (key.equals(rkey))
							notifyChange(false, true);
					}
				});
			}
		};
	}

	public VarView<V> getView(final K key) {
		if (key == null)
			throw new NullPointerException("Must supply non-null 'key'.");
		return new MappedValue<V>() {
			@Override
			public V get() {
				return GoMap.this.get(key);
			}

			@Override
			protected Connection connect() {
				return GoMap.this.connect(new GoMap.Listener<K, V>() {
					@Override
					public void onPut(K pkey, V value, V ovalue) {
						if (key.equals(pkey))
							notifyChange(value, ovalue);
					}

					@Override
					public void onRemove(K pkey, V ovalue) {
						if (key.equals(pkey))
							notifyChange(null, ovalue);
					}
				});
			}
		};
	}

	public int size() {
		return _impl.size();
	}

	public boolean isEmpty() {
		return _impl.isEmpty();
	}

	public boolean containsKey(Object key) {
		return _impl.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return _impl.containsValue(value);
	}

	@Override
	public int hashCode() {
		return _impl.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return other == this || _impl.equals(other);
	}

	@Override
	public String toString() {
		return "RMap" + _impl;
	}

	public V get(Object key) {
		return _impl.get(key);
	}

	public V put(K key, V value) {
		checkMutate();
		V ovalue = _impl.put(key, value);
		if (!areEqual(value, ovalue)) {
			emitPut(key, value, ovalue);
		}
		return ovalue;
	}

	public V remove(Object rawKey) {
		checkMutate();

		// avoid generating an event if no mapping exists for the supplied key
		if (!_impl.containsKey(rawKey)) {
			return null;
		}

		@SuppressWarnings("unchecked")
		K key = (K) rawKey;
		V ovalue = _impl.remove(key);
		emitRemove(key, ovalue);

		return ovalue;
	}

	public void putAll(Map<? extends K, ? extends V> map) {
		for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	public void clear() {
		checkMutate();
		// generate removed events for our keys (do so on a copy of our set so
		// that we can clear
		// our underlying map before any of the published events are processed)
		Set<Map.Entry<K, V>> entries = new HashSet<Map.Entry<K, V>>(
				_impl.entrySet());
		_impl.clear();
		for (Map.Entry<K, V> entry : entries)
			emitRemove(entry.getKey(), entry.getValue());
	}

	public Set<K> keySet() {
		final Set<K> iset = _impl.keySet();
		return new AbstractSet<K>() {
			public Iterator<K> iterator() {
				final Iterator<K> iiter = iset.iterator();
				return new Iterator<K>() {
					public boolean hasNext() {
						return iiter.hasNext();
					}

					public K next() {
						return (_current = iiter.next());
					}

					public void remove() {
						checkMutate();
						if (_current == null)
							throw new IllegalStateException();
						V ovalue = GoMap.this.get(_current);
						iiter.remove();
						emitRemove(_current, ovalue);
						_current = null;
					}

					protected K _current;
				};
			}

			public int size() {
				return GoMap.this.size();
			}

			public boolean remove(Object o) {
				checkMutate();
				V ovalue = GoMap.this.get(o);
				boolean modified = iset.remove(o);
				if (modified) {
					@SuppressWarnings("unchecked")
					K key = (K) o;
					emitRemove(key, ovalue);
				}
				return modified;
			}

			public void clear() {
				GoMap.this.clear();
			}
		};
	}

	public Collection<V> values() {
		final Collection<Map.Entry<K, V>> iset = _impl.entrySet();
		return new AbstractCollection<V>() {
			public Iterator<V> iterator() {
				final Iterator<Map.Entry<K, V>> iiter = iset.iterator();
				return new Iterator<V>() {
					public boolean hasNext() {
						return iiter.hasNext();
					}

					public V next() {
						return (_current = iiter.next()).getValue();
					}

					public void remove() {
						checkMutate();
						iiter.remove();
						emitRemove(_current.getKey(), _current.getValue());
						_current = null;
					}

					protected Map.Entry<K, V> _current;
				};
			}

			public int size() {
				return GoMap.this.size();
			}

			public boolean contains(Object o) {
				return GoMap.this.containsValue(o);
			}

			public void clear() {
				GoMap.this.clear();
			}
		};
	}

	public Set<Map.Entry<K, V>> entrySet() {
		final Set<Map.Entry<K, V>> iset = _impl.entrySet();
		return new AbstractSet<Map.Entry<K, V>>() {
			public Iterator<Map.Entry<K, V>> iterator() {
				final Iterator<Map.Entry<K, V>> iiter = iset.iterator();
				return new Iterator<Map.Entry<K, V>>() {
					public boolean hasNext() {
						return iiter.hasNext();
					}

					public Map.Entry<K, V> next() {
						_current = iiter.next();
						return new Map.Entry<K, V>() {
							public K getKey() {
								return _ientry.getKey();
							}

							public V getValue() {
								return _ientry.getValue();
							}

							public V setValue(V value) {
								checkMutate();
								if (!iset.contains(this))
									throw new IllegalStateException(
											"Cannot update removed map entry.");
								V ovalue = _ientry.setValue(value);
								if (!areEqual(value, ovalue)) {
									emitPut(_ientry.getKey(), value, ovalue);
								}
								return ovalue;
							}

							public boolean equals(Object o) {
								return _ientry.equals(o);
							}

							public int hashCode() {
								return _ientry.hashCode();
							}

							protected Map.Entry<K, V> _ientry = _current;
						};
					}

					public void remove() {
						checkMutate();
						iiter.remove();
						emitRemove(_current.getKey(), _current.getValue());
						_current = null;
					}

					protected Map.Entry<K, V> _current;
				};
			}

			public boolean contains(Object o) {
				return iset.contains(o);
			}

			public boolean remove(Object o) {
				checkMutate();
				boolean modified = iset.remove(o);
				if (modified) {
					@SuppressWarnings("unchecked")
					Map.Entry<K, V> entry = (Map.Entry<K, V>) o;
					emitRemove(entry.getKey(), entry.getValue());
				}
				return modified;
			}

			public int size() {
				return GoMap.this.size();
			}

			public void clear() {
				GoMap.this.clear();
			}
		};
	}

	@Override
	Listener<K, V> placeholderListener() {
		@SuppressWarnings("unchecked")
		Listener<K, V> p = (Listener<K, V>) DEF;
		return p;
	}

	protected void emitPut(K key, V value, V oldValue) {
		notifyPut(key, value, oldValue);
	}

	protected void notifyPut(K key, V value, V oldValue) {
		notify(PUT, key, value, oldValue);
	}

	protected void emitRemove(K key, V oldValue) {
		notifyRemove(key, oldValue);
	}

	protected void notifyRemove(K key, V oldValue) {
		notify(REMOVE, key, oldValue, null);
	}

	@SuppressWarnings("unchecked")
	protected static final Notifier PUT = new Notifier() {
		public void notify(Object lner, Object key, Object value,
				Object oldValue) {
			((Listener<Object, Object>) lner).onPut(key, value, oldValue);
		}
	};

	@SuppressWarnings("unchecked")
	protected static final Notifier REMOVE = new Notifier() {
		public void notify(Object lner, Object key, Object oldValue,
				Object ignored) {
			((Listener<Object, Object>) lner).onRemove(key, oldValue);
		}
	};
}
