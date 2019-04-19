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
import loon.utils.StringUtils;
import loon.utils.TimeUtils;

public abstract class CacheObject<T> {

	protected final String _name;

	protected final Object _target;

	protected boolean _locked;

	protected int _priority;

	protected long _lastUseTime;

	protected boolean _customFlag;

	public CacheObject(Object target) {
		this(null, target, false, 0);
	}

	public CacheObject(String name, Object target, boolean locked, int priority) {
		if (target == null) {
			throw new LSysException("Target is null");
		}
		_name = StringUtils.isEmpty(name) ? "unkown" : name;
		_target = target;
		_locked = locked;
		_priority = priority;
		_lastUseTime = TimeUtils.millis();
		_customFlag = false;
	}

	protected abstract boolean isUse();

	protected abstract T onSpawn();

	protected abstract T onUnspawn();

	protected abstract T disposed(boolean isShutdown);

	public boolean isLocked() {
		return _locked;
	}

	public void setLocked(boolean locked) {
		this._locked = locked;
	}

	public boolean getCustomFlag() {
		return _customFlag;
	}

	public void setCustomFlag(boolean flag) {
		this._customFlag = flag;
	}

	public int getPriority() {
		return _priority;
	}

	public void setPriority(int priority) {
		this._priority = priority;
	}

	public long getLastUseTime() {
		return _lastUseTime;
	}

	public String getName() {
		return _name;
	}

	public Object getTarget() {
		return _target;
	}
}
