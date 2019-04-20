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
import loon.utils.TimeUtils;

/**
 * 一个缓存对象的二次封装,用于统一注入缓存池中的对象形式
 * 
 * @param <T>
 */
public class GCCache<T extends CacheObject> extends CacheObject {

	private T _gc_object;

	private int _gc_spawnCount;

	public GCCache(T obj) {
		this(obj, false);
	}

	public GCCache(T obj, boolean spawned) {
		super(obj);
		if (obj == null) {
			throw new LSysException("Object is null");
		}
		_gc_object = obj;
		_gc_spawnCount = spawned ? 1 : 0;
		if (spawned) {
			_gc_object.onSpawn();
		}
	}

	@Override
	public String getName() {
		return _gc_object._name;
	}

	@Override
	public boolean isLocked() {
		return _gc_object._locked;
	}

	@Override
	public void setLocked(boolean locked) {
		_gc_object._locked = locked;
	}

	@Override
	public int getPriority() {
		return _gc_object._priority;
	}

	@Override
	public void setPriority(int priority) {
		_gc_object._priority = priority;
	}

	@Override
	public long getLastUseTime() {
		return _gc_object._lastUseTime;
	}

	@Override
	public boolean isUse() {
		return _gc_spawnCount > 0;
	}

	public int getSpawnCount() {
		return _gc_spawnCount;
	}

	public T peek() {
		return _gc_object;
	}

	@Override
	public boolean getCustomFlag() {
		return _gc_object._customFlag;
	}

	@Override
	public void setCustomFlag(boolean flag) {
		_gc_object._customFlag = flag;
	}

	public T spawn() {
		_gc_spawnCount++;
		_gc_object._lastUseTime = TimeUtils.millis();
		_gc_object.onSpawn();
		return _gc_object;
	}

	public T unspawn() {
		_gc_object.onUnspawn();
		_gc_object._lastUseTime = TimeUtils.millis();
		_gc_spawnCount--;
		if (_gc_spawnCount < 0) {
			throw new LSysException("Spawn count is less than 0.");
		}
		return _gc_object;
	}

	public T close(boolean isShutdown) {
		_gc_object.disposed(isShutdown);
		return _gc_object;
	}

	@Override
	public void onSpawn() {
		spawn();
	}

	@Override
	public void onUnspawn() {
		unspawn();
	}

	@Override
	public void disposed(boolean isShutdown) {
		close(isShutdown);
	}

}
