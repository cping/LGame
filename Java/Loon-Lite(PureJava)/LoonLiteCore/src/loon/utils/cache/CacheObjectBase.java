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

import loon.LSysException;
import loon.LSystem;
import loon.utils.StringUtils;

/**
 * 缓存池的抽象,可以根据此类自行扩展自己需要的缓存池
 */
public abstract class CacheObjectBase {

	private final String _name;

	private int _capacity;

	private long _expireTime;

	public CacheObjectBase(String name, int cap, long time) {
		if (StringUtils.isEmpty(name)) {
			name = LSystem.UNKNOWN;
		}
		this._name = name;
		this._capacity = cap;
		this._expireTime = time;
	}

	public abstract CacheType getObjectType();

	public abstract int getCount();

	public abstract int canReleaseCount();

	public abstract boolean isAllowMultiSpawn();

	public abstract float getAutoReleaseInterval();

	public abstract void setAutoReleaseInterval(long interval);

	public int getCapacity() {
		return _capacity;
	}

	public void setCapacity(int c) {
		if (c < 0) {
			throw new LSysException("Capacity is invalid");
		}

		if (_capacity == c) {
			return;
		}
		this._capacity = c;
		release();
	}

	public long getExpireTime() {
		return _expireTime;
	}

	public void setExpireTime(long time) {
		if (time < 0) {
			throw new LSysException("ExpireTime is invalid");
		}

		if (_expireTime == time) {
			return;
		}
		this._expireTime = time;
		release();
	}

	public abstract int getPriority();

	public abstract void setPriority(int priority);

	public abstract void release();

	public abstract void release(int releaseCount);

	public abstract void releaseAll();

	public abstract CacheObjectInfo[] getAllObjectInfos();

	public abstract void update(long elapsedTime);

	public abstract void shutdown();

	public abstract boolean canSpawn();

	public abstract boolean canSpawn(String name);

	public abstract void unspawn(Object target);

	public abstract void setLocked(Object target, boolean locked);

	public abstract void setPriority(Object target, int priority);

	public String getName() {
		return _name;
	}
}
