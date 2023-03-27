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
package loon.utils.cache;

import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.events.QueryEvent;
import loon.utils.Array;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 一个简单的缓存池对象抽象,没有生命周期也没有引用计数, 其中对象都需要手动释放,只能单纯的提取和存储缓存对象,<br>
 * 只适合实现简单的缓存逻辑.复杂缓存池存储,请使用CacheObjectManger实现.
 * 
 * @param <T>
 */
public abstract class Pool<T> {

	protected final int max;

	protected int peak;

	protected final TArray<T> freeObjects;

	protected boolean allowFree;

	public Pool() {
		this(LSystem.DEFAULT_MAX_CACHE_SIZE);
	}

	public Pool(int max) {
		this.freeObjects = new TArray<T>();
		this.max = max;
		this.allowFree = true;
		peak = 0;
	}

	abstract protected T newObject();

	public T obtain() {
		return freeObjects.size() == 0 ? newObject() : freeObjects.pop();
	}

	public void push(T o) {
		free(o);
	}

	public T pop() {
		return freeObjects.pop();
	}

	public TArray<T> select(QueryEvent<T> event) {
		TArray<T> result = new TArray<T>();
		for (int i = freeObjects.size - 1; i > -1; i--) {
			T v = result.get(i);
			if (event.hit(v)) {
				result.add(v);
			}
		}
		return result;
	}

	public void delete(QueryEvent<T> event) {
		for (int i = freeObjects.size - 1; i > -1; i--) {
			T v = freeObjects.get(i);
			if (event.hit(v)) {
				freeObjects.remove(v);
			}
		}
	}

	public abstract boolean isLimit(T src, T dst);

	public void free(T o) {
		if (o == null)
			throw new LSysException("Object cannot be null.");
		if (freeObjects.size() < max) {
			for (int i = 0; i < freeObjects.size; i++) {
				T obj = freeObjects.get(i);
				if (isLimit(o, obj)) {
					return;
				}
			}
			if (!freeObjects.contains(o)) {
				freeObjects.add(o);
			}
			peak = MathUtils.max(peak, freeObjects.size());
		} else if (allowFree) {
			for (int i = 0; i < freeObjects.size; i++) {
				T obj = freeObjects.get(i);
				if (obj instanceof LRelease)
					((LRelease) obj).close();
			}
			freeObjects.clear();
		}
		if (o instanceof Poolable)
			((Poolable) o).reset();
	}

	public void freeAll(TArray<T> objects) {
		if (objects == null) {
			throw new LSysException("Object cannot be null.");
		}
		for (int i = objects.size - 1; i > -1; i--) {
			T o = objects.get(i);
			if (o == null) {
				continue;
			}
			if (freeObjects.size() < max) {
				freeObjects.add(o);
			}
			if (o instanceof Poolable) {
				((Poolable) o).reset();
			}
		}
		peak = MathUtils.max(peak, freeObjects.size());
	}

	public void freeAll(Array<T> objects) {
		if (objects == null) {
			throw new LSysException("Object cannot be null.");
		}
		for (; objects.hashNext();) {
			T o = objects.next();
			if (o == null) {
				continue;
			}
			if (freeObjects.size() < max) {
				freeObjects.add(o);
			}
			if (o instanceof Poolable) {
				((Poolable) o).reset();
			}
		}
		objects.stopNext();
		peak = MathUtils.max(peak, freeObjects.size());
	}

	public void clear() {
		freeObjects.clear();
		peak = 0;
	}

	public int getPeak() {
		return peak;
	}

	public int getFree() {
		return freeObjects.size();
	}

	public boolean isAllowFree() {
		return allowFree;
	}

	public Pool<T> setAllowFree(boolean allowFree) {
		this.allowFree = allowFree;
		return this;
	}

	static public interface Poolable {
		public void reset();
	}

}
