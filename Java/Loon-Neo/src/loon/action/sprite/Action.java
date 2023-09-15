/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
package loon.action.sprite;

public abstract class Action {

	protected Entity entity;

	private boolean _actived = false;

	private boolean _started = false;

	private boolean _stoped = false;

	private boolean _completed = false;

	private boolean _cancelled = false;

	public final Entity getEntity() {
		return entity;
	}

	public final Action setEntity(Entity entity) {
		if (this.entity != null && this.entity != entity) {
			return this;
		}
		this.entity = entity;
		return this;
	}

	public final boolean isStarted() {
		return _started;
	}

	public final boolean isStoped() {
		return _stoped;
	}

	public final boolean isComplete() {
		return _completed;
	}

	public final Action setComplete() {
		_completed = true;
		return this;
	}

	public final boolean isCancelled() {
		return _cancelled;
	}

	public final Action complete() {
		if (_completed) {
			return this;
		}
		_completed = true;
		onStop();
		onCompleted();
		return this;
	}

	public final Action cancel() {
		if (_cancelled) {
			return this;
		}
		_cancelled = true;
		onCancelled();
		return this;
	}

	public final Action start() {
		if (_started) {
			return this;
		}
		_started = true;
		_stoped = false;
		_actived = true;
		onStart();
		return this;
	}

	public final Action stop() {
		if (_stoped) {
			return this;
		}
		_stoped = true;
		_started = false;
		_actived = false;
		onStop();
		return this;
	}

	protected abstract void onQueued();

	protected abstract void onStart();

	protected abstract void onStop();

	protected abstract void onUpdate(float dt);

	protected abstract void onCompleted();

	protected abstract void onCancelled();

	public boolean isActived() {
		return _actived;
	}

	public Action setActived(boolean a) {
		this._actived = a;
		return this;
	}

}
