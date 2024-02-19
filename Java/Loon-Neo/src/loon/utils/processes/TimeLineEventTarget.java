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
package loon.utils.processes;

import loon.LRelease;

public abstract class TimeLineEventTarget implements LRelease {

	final String _eventName;

	float _elapsed;

	int _priority;

	int _age;

	boolean _actived = false;

	boolean _paused = false;

	boolean _started = false;

	boolean _stoped = false;

	boolean _completed = false;

	boolean _cancelled = false;

	public TimeLineEventTarget(String eveName) {
		this(eveName, 0);
	}

	public TimeLineEventTarget(String eveName, int priority) {
		this._eventName = eveName;
		this._priority = priority;
	}

	public int getPriority() {
		return _priority;
	}

	public final boolean isNotInit() {
		return !(_started && _stoped && _completed && _cancelled);
	}

	public final boolean isStarted() {
		return _started;
	}

	public final boolean isStoped() {
		return _stoped;
	}

	public final boolean isPaused() {
		return _paused;
	}

	public final boolean isCompleted() {
		return _completed;
	}

	public final TimeLineEventTarget setComplete() {
		_completed = true;
		return this;
	}

	public final boolean isCancelled() {
		return _cancelled;
	}

	public final boolean isActived() {
		return _actived;
	}

	public final TimeLineEventTarget setActived(boolean a) {
		this._actived = a;
		return this;
	}

	public final TimeLineEventTarget resetAll() {
		this.reset();
		this._age = 0;
		return this;
	}

	public final TimeLineEventTarget reset() {
		this._actived = false;
		this._paused = false;
		this._started = false;
		this._stoped = false;
		this._completed = false;
		this._cancelled = false;
		return this;
	}

	public final TimeLineEventTarget complete() {
		if (_completed) {
			return this;
		}
		_completed = true;
		onCompleted();
		stop();
		return this;
	}

	public final TimeLineEventTarget pause() {
		if (_paused) {
			return this;
		}
		_paused = true;
		_actived = false;
		onPause();
		return this;
	}

	public final TimeLineEventTarget resume() {
		if (!_paused) {
			return this;
		}
		_paused = false;
		_actived = true;
		onResume();
		return this;
	}

	public final TimeLineEventTarget cancel() {
		if (_cancelled) {
			return this;
		}
		_cancelled = true;
		onCancelled();
		stop();
		return this;
	}

	public final TimeLineEventTarget start() {
		if (_started) {
			return this;
		}
		_started = true;
		_stoped = false;
		_actived = true;
		_paused = false;
		onStart();
		return this;
	}

	public final TimeLineEventTarget stop() {
		if (_stoped) {
			return this;
		}
		_stoped = true;
		_started = false;
		_actived = false;
		_paused = false;
		onStop();
		onExit();
		return this;
	}

	public final boolean isRunning() {
		return _actived;
	}

	public final boolean isOver() {
		return _started && (_completed || _cancelled || _stoped);
	}

	public final float getElapsed() {
		return _elapsed;
	}

	public final int getAge() {
		return _age;
	}

	public abstract void onUpdate(float dt);

	public abstract void onStart();

	public abstract void onPause();

	public abstract void onResume();

	public abstract void onStop();

	public abstract void onExit();

	public abstract void onCompleted();

	public abstract void onCancelled();

	@Override
	public void close() {
		stop();
	}

}
