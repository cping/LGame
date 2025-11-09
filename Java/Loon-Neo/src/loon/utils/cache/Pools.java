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
package loon.utils.cache;

import loon.events.Created;
import loon.events.DefaultCreated;
import loon.utils.ObjectMap;

/**
 * 用于同时存储多个简单缓存池Pool类
 * 
 * @param <T>
 */
public final class Pools<T> {

	private static Pools<Object> _instance;

	public static void freeStatic() {
		_instance = null;
	}

	public static final Pools<Object> get() {
		synchronized (Pools.class) {
			if (_instance == null) {
				_instance = new Pools<Object>();
			}
			return _instance;
		}
	}
	
	public static final <T> Pools<T> create() {
		return new Pools<T>();
	}

	private final ObjectMap<String, Pool<T>> _inPoolDic;

	public Pools() {
		_inPoolDic = new ObjectMap<String, Pool<T>>();
	}

	public T create(String sign, T item) {
		return create(sign, new DefaultCreated<T>(item));
	}

	public T create(String sign, Created<T> c) {
		Pool<T> p = getBySign(sign);
		if (p != null) {
			T v = p.pop();
			if (v == null) {
				v = c.make();
				p.push(v);
			}
			return v;
		} else {
			p = new DefaultPool<T>(c);
			_inPoolDic.put(sign, p);
			return c.make();
		}
	}

	public void recover(String sign, Created<T> c) {
		Pool<T> p = getBySign(sign);
		if (p != null) {
			p.push(c.make());
		} else {
			p = new DefaultPool<T>(c);
			_inPoolDic.put(sign, p);
		}
	}

	public void recover(String sign, T item) {
		Pool<T> p = getBySign(sign);
		if (p != null) {
			p.push(item);
		} else {
			p = new DefaultPool<T>();
			p.push(item);
			_inPoolDic.put(sign, p);
		}
	}

	public Pools<T> recover(String sign, Pool<T> item) {
		_inPoolDic.put(sign, item);
		return this;
	}

	public Pool<T> getBySign(String sign) {
		return _inPoolDic.get(sign);
	}

	public boolean clearBySign(String sign) {
		return _inPoolDic.remove(sign) != null;
	}

	public void clear() {
		_inPoolDic.clear();
	}

	public int getFreeAll() {
		int count = 0;
		for (Pool<T> p : _inPoolDic.values()) {
			count += p.getFree();
		}
		return count;
	}
}
