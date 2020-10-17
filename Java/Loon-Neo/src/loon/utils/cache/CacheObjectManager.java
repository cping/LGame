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

import java.util.Comparator;

import loon.LSysException;
import loon.LSystem;
import loon.events.QueryEvent;
import loon.utils.ObjectMap;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.TimeUtils;
import loon.utils.processes.GameProcessType;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.timer.Duration;
import loon.utils.timer.LTimerContext;

/**
 * 缓存池管理器
 */
public class CacheObjectManager {

	private static class SortCacheComparator implements Comparator<CacheObjectBase> {

		@Override
		public int compare(CacheObjectBase o1, CacheObjectBase o2) {
			int a = o1.getPriority();
			int b = o2.getPriority();
			if (a > b) {
				return -1;
			}
			if (a < b) {
				return 1;
			}
			return 0;
		}

	}

	private int defCapacity;

	private long defExpireTime;

	private int defPriority;

	private Comparator<CacheObjectBase> sortCacheComparator;

	private final ObjectMap<String, CacheObjectBase> objectPools;

	private final RealtimeProcess process;

	public CacheObjectManager() {
		this(0);
	}

	public CacheObjectManager(long delay) {
		this.objectPools = new ObjectMap<String, CacheObjectBase>();
		this.sortCacheComparator = new SortCacheComparator();
		this.defCapacity = Integer.MAX_VALUE;
		this.defExpireTime = LSystem.YEAR;
		this.defPriority = 0;
		process = new RealtimeProcess() {

			@Override
			public void run(LTimerContext time) {
				update(time.timeSinceLastUpdate);
			}
		};
		process.setProcessType(GameProcessType.Progress);
		process.setDelay(delay);
	}

	public void subimit() {
		for (CacheObjectBase cob : objectPools.values()) {
			cob.setExpireTime(TimeUtils.millis());
		}
		if (!RealtimeProcessManager.get().containsProcess(process)) {
			RealtimeProcessManager.get().addProcess(process);
		}
	}

	public void update(long elapsedTime) {
		for (CacheObjectBase cob : objectPools.values()) {
			cob.update(elapsedTime);
		}
	}

	public boolean hasObjectPool(CacheType cacheType, String name) {
		if (name == null) {
			throw new LSysException("Name is null");
		}
		if (cacheType == null) {
			throw new LSysException("Object type is null");
		}
		return hasObjectPool(getTypeName(cacheType, name));
	}

	public boolean hasObjectPool(CacheType cacheType) {
		if (cacheType == null) {
			throw new LSysException("Object type is null");
		}
		return hasObjectPool(cacheType.getName());
	}

	public boolean hasObjectPool(String name) {
		if (StringUtils.isEmpty(name)) {
			throw new LSysException("Name is null");
		}
		return objectPools.containsKey(name);
	}

	public CacheObjectBase getOnlyObjectPool(String name) {
		if (name == null) {
			throw new LSysException("Name is null");
		}
		for (CacheObjectBase v : objectPools.values()) {
			if (v.getName().equals(name)) {
				return v;
			}
		}
		return null;
	}

	public CacheObjectBase getOnlyObjectPool(QueryEvent<CacheObjectBase> query) {
		if (query == null) {
			throw new LSysException("Query is null");
		}
		for (CacheObjectBase v : objectPools.values()) {
			if (query.hit(v)) {
				return v;
			}
		}
		return null;
	}

	public TArray<CacheObjectBase> getObjectPools(QueryEvent<CacheObjectBase> query) {
		if (query == null) {
			throw new LSysException("Query is null");
		}
		TArray<CacheObjectBase> results = new TArray<CacheObjectBase>();
		for (CacheObjectBase v : objectPools.values()) {
			if (query.hit(v)) {
				results.add(v);
			}
		}
		return results;
	}

	public TArray<CacheObjectBase> getAllObjectPools(boolean sort) {
		TArray<CacheObjectBase> results = new TArray<CacheObjectBase>();
		for (CacheObjectBase v : objectPools.values()) {
			results.add(v);
		}
		if (sort) {
			results.sort(sortCacheComparator);
		}
		return results;
	}

	private final static String getTypeName(CacheType cacheType, String name) {
		return cacheType.getName() + "." + name;
	}

	public boolean destroyObjectPool(CacheObjectBase objectPool) {
		if (objectPool == null) {
			throw new LSysException("Object pool is null");
		}

		return destroyObjectPool(getTypeName(objectPool.getObjectType(), objectPool.getName()));
	}

	public <T extends CacheObject> CacheObjectPool<T> createObjectPool(String name, boolean allowMultiSpawn) {
		return createObjectPool(name, allowMultiSpawn, defExpireTime, defCapacity, defExpireTime, defPriority);
	}

	public <T extends CacheObject> CacheObjectPool<T> createObjectPool(String name, Duration expireTime,
			boolean allowMultiSpawn) {
		long timer = expireTime.toMillisLong();
		return createObjectPool(name, allowMultiSpawn, timer, defCapacity, timer, defPriority);
	}

	public <T extends CacheObject> CacheObjectPool<T> createObjectPool(String name, Duration autoReleaseInterval,
			Duration expireTime, boolean allowMultiSpawn) {
		long autoTimer = autoReleaseInterval.toMillisLong();
		long timer = expireTime.toMillisLong();
		return createObjectPool(name, allowMultiSpawn, autoTimer, defCapacity, timer, defPriority);
	}

	public <T extends CacheObject> CacheObjectPool<T> createObjectPool(String name, long expireTime,
			boolean allowMultiSpawn) {
		return createObjectPool(name, allowMultiSpawn, expireTime, defCapacity, expireTime, defPriority);
	}

	public <T extends CacheObject> CacheObjectPool<T> createObjectPool(CacheType cacheType, String name,
			boolean allowMultiSpawn, long autoReleaseInterval, int capacity, long expireTime, int priority) {
		if (cacheType == null) {
			throw new LSysException("Object type is null");
		}
		if (name == null) {
			throw new LSysException("Name is null");
		}
		if (hasObjectPool(cacheType, name)) {
			throw new LSysException("Already exist object pool " + getTypeName(cacheType, name));
		}
		CacheObjectPool<T> objectPool = new CacheObjectPool<T>(name, allowMultiSpawn, autoReleaseInterval, capacity,
				expireTime, priority);
		objectPools.put(cacheType.getName() + "." + name, objectPool);
		return objectPool;
	}

	public <T extends CacheObject> CacheObjectPool<T> createObjectPool(String name, boolean allowMultiSpawn,
			long autoReleaseInterval, int capacity, long expireTime, int priority) {
		if (hasObjectPool(name)) {
			throw new LSysException("Already exist object pool " + name);
		}
		CacheObjectPool<T> objectPool = new CacheObjectPool<T>(name, allowMultiSpawn, autoReleaseInterval, capacity,
				expireTime, priority);
		objectPools.put(name, objectPool);
		return objectPool;
	}

	public void setSortCacheComparator(Comparator<CacheObjectBase> sortCache) {
		this.sortCacheComparator = sortCache;
	}

	public void shutdown() {
		for (CacheObjectBase cob : objectPools.values()) {
			cob.shutdown();
		}
		objectPools.clear();
	}

	public boolean destroyObjectPool(String name) {
		for (CacheObjectBase v : objectPools.values()) {
			if (v.getName().equals(name)) {
				v.shutdown();
				objectPools.remove(v);
			}
		}
		return false;
	}

	public void release() {
		TArray<CacheObjectBase> objectPools = getAllObjectPools(true);
		for (int i = 0; i < objectPools.size; i++) {
			CacheObjectBase obj = objectPools.get(i);
			if (obj != null) {
				obj.release();
			}
		}
	}

	public void releaseAll() {
		TArray<CacheObjectBase> objectPools = getAllObjectPools(true);
		for (int i = 0; i < objectPools.size; i++) {
			CacheObjectBase obj = objectPools.get(i);
			if (obj != null) {
				obj.releaseAll();
			}
		}
	}
}
