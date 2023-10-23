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
package loon;

import loon.geom.IV;
import loon.utils.StrBuilder;
import loon.utils.TimeUtils;

public class FramerateCounter implements IV<Long> {

	private long _elapsedTime = 0;

	private long _lastTickTime = 0;

	private int _framerateCounter = 0;

	private int _updateCounter = 0;

	private int _framerateBuffer = 0;

	private int _updateBuffer = 0;

	private boolean _canUpdateFps = false;

	private final StrBuilder _buffer;

	public FramerateCounter() {
		this._buffer = new StrBuilder();
		this.updateDisplay();
	}

	protected void updateDisplay() {
		clear();
		_buffer.append(_framerateCounter);
		_buffer.append(" FPS / ");
		_buffer.append(_updateCounter);
		_buffer.append(" UPS");
	}

	public boolean renderTick() {
		return this.renderTick(getTimer());
	}

	public boolean renderTick(long elapsedTime) {
		if (_elapsedTime - _lastTickTime <= LSystem.SECOND) {
			_framerateBuffer++;
			return true;
		}
		return false;
	}

	public boolean updateTick() {
		return this.updateTick(getTimer());
	}

	public boolean updateTick(long elapsedTime) {
		if (elapsedTime - _lastTickTime <= LSystem.SECOND) {
			_updateBuffer++;
			return true;
		}
		_updateCounter = _updateBuffer;
		_updateBuffer = 0;
		_lastTickTime = elapsedTime;
		_framerateCounter = _framerateBuffer;
		_framerateBuffer = 0;
		_canUpdateFps = true;
		return false;
	}

	private long getTimer() {
		return _elapsedTime == 0 ? TimeUtils.millis() : _elapsedTime;
	}

	public long getElapsedTime() {
		return _elapsedTime;
	}

	public FramerateCounter setElapsedTime(long e) {
		this._elapsedTime = e;
		return this;
	}

	public long getLastTickTime() {
		return _lastTickTime;
	}

	public int getFramerateCounter() {
		return _framerateCounter;
	}

	public int getUpdateCounter() {
		return _updateCounter;
	}

	public int getFramerateBuffer() {
		return _framerateBuffer;
	}

	public int getUpdateBuffer() {
		return _updateBuffer;
	}

	public boolean isCanUpdateFps() {
		return _canUpdateFps;
	}

	public FramerateCounter clear() {
		_buffer.setLength(0);
		_canUpdateFps = true;
		return this;
	}

	@Override
	public Long get() {
		return getTimer();
	}

	@Override
	public String toString() {
		if (_canUpdateFps) {
			updateDisplay();
			_canUpdateFps = false;
		}
		return _buffer.toString();
	}

}
