/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.utils;

/**
 * 存储单独value的线性数据集合,内部数据无序排列,不允许重复
 *
 * @param <E>
 */
public class ObjectSet<E> implements Iterable<E>, IArray {

	private ObjectMap<E, Object> _map;

	public ObjectSet() {
		_map = new ObjectMap<>(false);
	}

	public ObjectSet(ObjectSet<? extends E> c) {
		_map = new ObjectMap<>(false);
		addAll(c);
	}

	public ObjectSet(int initialCapacity, float loadFactor) {
		_map = new ObjectMap<>(initialCapacity, loadFactor, false);
	}

	public ObjectSet(int initialCapacity) {
		_map = new ObjectMap<>(initialCapacity, false);
	}

	ObjectSet(int initialCapacity, float loadFactor, boolean dummy) {
		_map = new OrderedMap<>(initialCapacity, loadFactor, false, false);
	}

	public void addAll(ObjectSet<? extends E> c){
		for (E key : c) {
			add(key);
		}
	}

	@Override
	public LIterator<E> iterator() {
		return _map.keys();
	}

	@Override
	public int size() {
		return _map.size();
	}

	@Override
	public boolean isEmpty() {
		return _map.isEmpty();
	}

	public boolean contains(Object o) {
		return _map.containsKey(o);
	}

	public boolean add(E e) {
		return _map.put(e, null) == null;
	}

	public boolean remove(Object o) {
		return _map.remove(o) == ObjectMap.FINAL_VALUE;
	}

	@Override
	public int hashCode() {
		if (_map == null) {
			return super.hashCode();
		}
		int hashCode = 1;
		for (LIterator<E> it = _map.keys(); it.hasNext();) {
			E e = it.next();
			hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
		}
		return hashCode;
	}

	@Override
	public void clear() {
		_map.clear();
	}

	@Override
    public String toString() {
		LIterator<E> it = iterator();
        if (! it.hasNext()) {
            return "[]";
        }
        StrBuilder sbr = new StrBuilder();
        sbr.append('[');
        for (it = _map.keys(); it.hasNext();) {
            E e = it.next();
            sbr.append(e == this ? "(this ObjectSet)" : e);
            if (! it.hasNext()) {
                return sbr.append(']').toString();
            }
            sbr.append(',').append(' ');
        }
        return sbr.toString();
    }

}
