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

import loon.action.ActionBind;
import loon.events.TimeLineEnterListener;
import loon.events.TimeLineListener;
import loon.utils.timer.Duration;
import loon.utils.timer.LTimer;

public class TimeLineEvent extends TimeLineEventTarget {

	private TimeLineEnterListener _timelineEnterListener;

	private TimeLineListener _timelineListener;

	private LTimer _timer;

	protected ActionBind _bind;

	public TimeLineEvent(String eveName) {
		this(eveName, 0f);
	}

	public TimeLineEvent(String eveName, float delay) {
		this(eveName, delay, null);
	}

	public TimeLineEvent(String eveName, float delay, TimeLineEnterListener lineEnterListener) {
		this(eveName, delay, 0, lineEnterListener);
	}

	public TimeLineEvent(String eveName, float delay, int priority, TimeLineEnterListener lineEnterListener) {
		super(eveName, priority);
		this._timer = new LTimer(Duration.ofS(delay));
		this._timelineEnterListener = lineEnterListener;
	}

	public ActionBind getActionBind() {
		return _bind;
	}

	public TimeLineEvent setActionBind(ActionBind v) {
		this._bind = v;
		return this;
	}

	@Override
	public void onUpdate(float dt) {
		if (_timer.action(dt)) {
			if (_timelineListener != null) {
				_timelineListener.onUpdate(dt);
			}
			if (_timelineEnterListener != null) {
				_timelineEnterListener.onEnter(this);
			}
			this._age++;
		}
	}

	@Override
	public void onStart() {
		if (_timelineListener != null) {
			_timelineListener.onStart();
		}
	}

	@Override
	public void onPause() {
		if (_timelineListener != null) {
			_timelineListener.onPause();
		}
	}

	@Override
	public void onResume() {
		if (_timelineListener != null) {
			_timelineListener.onResume();
		}
	}

	@Override
	public void onStop() {
		if (_timelineListener != null) {
			_timelineListener.onStop();
		}
	}

	@Override
	public void onExit() {
		if (_timelineListener != null) {
			_timelineListener.onExit();
		}
	}

	@Override
	public void onCompleted() {
		if (_timelineListener != null) {
			_timelineListener.onCompleted();
		}
	}

	@Override
	public void onCancelled() {
		if (_timelineListener != null) {
			_timelineListener.onCancelled();
		}
	}

	public TimeLineListener getTimeLineListener() {
		return _timelineListener;
	}

	public TimeLineEvent setTimeLineListener(TimeLineListener tl) {
		this._timelineListener = tl;
		return this;
	}

	public LTimer getTimer() {
		return _timer;
	}

}
