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
 * 一个方便频繁数值位置替换的Array
 */
public class SwappableArray<T> {

	public static final <T> SwappableArray<T> create() {
		return new SwappableArray<T>();
	}

	private int currentIndex = 0;

	private final TArray<T> data;

	public SwappableArray() {
		this(CollectionUtils.INITIAL_CAPACITY);
	}

	public SwappableArray(int size) {
		this.data = new TArray<T>(size);
	}

	public SwappableArray(T[] array) {
		this.data = new TArray<T>(array);
	}

	public SwappableArray(TArray<T> array) {
		this.data = array;
	}

	public T previousLoop() {
		if (this.currentIndex > 0) {
			this.currentIndex--;
		} else {
			this.currentIndex = this.data.size() - 1;
		}
		return this.data.get(this.currentIndex);
	}

	public T nextLoop() {
		if (this.currentIndex + 1 < this.data.size()) {
			this.currentIndex++;
		} else {
			this.currentIndex = 0;
		}
		return this.data.get(this.currentIndex);
	}

	public T get(int idx) {
		return this.data.get(idx);
	}

	public SwappableArray<T> add(T o) {
		this.data.add(o);
		this.currentIndex = this.data.size;
		return this;
	}

	public SwappableArray<T> remove(T o) {
		this.data.remove(o);
		this.currentIndex = this.data.size;
		return this;
	}

	public boolean moveBack(T o) {
		int loc = getLocation(o);
		if (loc > 0) {
			T previous = this.data.get(loc - 1);
			this.data.set(loc - 1, o);
			this.data.set(loc, previous);
			return true;
		}
		return false;
	}

	public boolean moveForward(T o) {
		int loc = getLocation(o);
		if (loc < this.data.size()) {
			T forward = this.data.get(loc + 1);
			this.data.set(loc, forward);
			this.data.set(loc + 1, o);
			return true;
		}
		return false;
	}

	public boolean isFront(T o) {
		if (this.data.get(this.data.size() - 1) == o) {
			return true;
		}
		return false;
	}

	public boolean isBack(T o) {
		if (this.data.get(0) == o) {
			return true;
		}
		return false;
	}

	public SwappableArray<T> moveToFront(T o) {
		while (!isFront(o)) {
			moveForward(o);
		}
		return this;
	}

	public SwappableArray<T> moveToBack(T o) {
		while (!isBack(o)) {
			moveBack(o);
		}
		return this;
	}

	public int getLocation(T o) {
		for (int i = 0; i < this.data.size(); i++) {
			if (this.data.get(i) == o) {
				return i;
			}
		}
		return -1;
	}

	public T getAt(int loc) {
		return this.data.get(loc);
	}

	public TArray<T> getElements() {
		return this.data;
	}

	public SwappableArray<T> clearLoopIndex() {
		return setLoopIndex(0);
	}

	public int loopIndex() {
		return this.currentIndex;
	}

	public SwappableArray<T> setLoopIndex(int idx) {
		this.currentIndex = idx;
		return this;
	}

	@Override
	public String toString() {
		return this.data.toString();
	}

}
