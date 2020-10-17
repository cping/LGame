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
package loon.action;

import loon.LSystem;
import loon.Screen;
import loon.events.FrameLoopEvent;
import loon.utils.StringKeyValue;

/**
 * 执行一个FrameLoopEvent事件
 *
 */
public class EventTo extends ActionEvent {

	private final FrameLoopEvent _event;

	public EventTo(FrameLoopEvent e) {
		this._event = e;
	}

	@Override
	public void update(long elapsedTime) {
		Screen screen = null;
		if (LSystem.getProcess() != null
				&& LSystem.getProcess().getScreen() != null) {
			screen = LSystem.getProcess().getScreen();
		}
		_event.call(elapsedTime, screen);
		if (_event.isDead()) {
			_event.completed();
		}
		_isCompleted = _event.isDead();
	}

	@Override
	public void onLoad() {
		_event.setDelay(getDelay());
	}

	@Override
	public ActionEvent kill() {
		super.kill();
		_event.kill();
		return this;
	}

	public FrameLoopEvent getLoopEvent() {
		return _event;
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public ActionEvent cpy() {
		EventTo event = new EventTo(_event);
		event.set(this);
		return event;
	}

	@Override
	public ActionEvent reverse() {
		_event.reset();
		return cpy();
	}

	@Override
	public String getName() {
		return "event";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("frameLoopEvent", _event);
		return builder.toString();
	}
}
