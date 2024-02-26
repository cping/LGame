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
package loon.action.map.battle;

import loon.geom.BooleanValue;
import loon.utils.timer.Duration;

public abstract class BattleTurnProcessEvent extends BattleTurnEvent {

	private BattleTimerListener _timerListener;

	public BattleTurnProcessEvent(BattleState state) {
		this(state, true);
	}

	public BattleTurnProcessEvent(BattleState state, boolean skipStartEnd) {
		this(state, skipStartEnd, -1l);
	}

	public BattleTurnProcessEvent(BattleState state, boolean skipStartEnd, float sec) {
		this(state, skipStartEnd, Duration.ofS(sec));
	}

	public BattleTurnProcessEvent(BattleState state, boolean skipStartEnd, long outTime) {
		super(state, skipStartEnd, outTime);
		set(skipStartEnd, false, skipStartEnd);
	}

	@Override
	public void onStart(long elapsedTime, BooleanValue start) {
		if (_timerListener != null) {
			_timerListener.onStart(elapsedTime, start);
		}
	}

	@Override
	public void onEnd(long elapsedTime, BooleanValue end) {
		if (_timerListener != null) {
			_timerListener.onEnd(elapsedTime, end);
		}
	}

	@Override
	public void onTimeOut(long elapsedTime, BooleanValue process) {
		if (_timerListener != null) {
			_timerListener.onTimeOut(elapsedTime, process);
		}
	}

	@Override
	public void onCompleted() {
		if (_timerListener != null) {
			_timerListener.onCompleted();
		}
	}

	@Override
	public void onReset() {
		if (_timerListener != null) {
			_timerListener.onReset();
		}
	}

	public BattleTimerListener getTimerListener() {
		return _timerListener;
	}

	public BattleTimerListener setTimerListener(BattleTimerListener t) {
		this._timerListener = t;
		return this;
	}

	@Override
	public void close() {
		super.close();
		this._timerListener = null;
	}
}
