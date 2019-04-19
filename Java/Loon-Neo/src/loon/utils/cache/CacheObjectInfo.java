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

public class CacheObjectInfo {

	protected final String _name;

	protected final boolean _locked;

	protected final int _priority;

	protected final long _lastUseTime;

	protected final int _spawnCount;

	protected final boolean _customFlag;

	public CacheObjectInfo(String name, boolean locked, boolean customflag, int priority, long lastUseTime,
			int spawnCount) {
		_name = name;
		_locked = locked;
		_customFlag = customflag;
		_priority = priority;
		_lastUseTime = lastUseTime;
		_spawnCount = spawnCount;
	}

	public String getName() {
		return _name;
	}

	public boolean isUse() {
		return _spawnCount > 0;
	}

	public boolean isLocked() {
		return _locked;
	}

	public int getPriority() {
		return _priority;
	}

	public long getLastUseTime() {
		return _lastUseTime;
	}

	public int getSpawnCount() {
		return _spawnCount;
	}

}
