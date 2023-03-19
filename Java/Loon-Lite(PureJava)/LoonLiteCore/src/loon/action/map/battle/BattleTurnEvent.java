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

public abstract class BattleTurnEvent implements BattleEvent {

	private final BattleState _state;

	private final BooleanValue _start = new BooleanValue();

	private final BooleanValue _process = new BooleanValue();

	private final BooleanValue _end = new BooleanValue();

	public BattleTurnEvent(BattleState state) {
		this._state = state;
	}

	public BattleTurnEvent reset() {
		set(false);
		return this;
	}

	public BattleTurnEvent set(boolean s, boolean p, boolean e) {
		_start.set(s);
		_process.set(p);
		_end.set(e);
		return this;
	}

	public BattleTurnEvent set(boolean s, boolean e) {
		return set(s, true, e);
	}

	public BattleTurnEvent set(boolean p) {
		return set(true, p, true);
	}

	public BooleanValue getStart() {
		return _start;
	}

	public BooleanValue getProcess() {
		return _process;
	}

	public BooleanValue getEnd() {
		return _end;
	}

	public BattleTurnEvent startEndUnDone() {
		_start.set(false);
		_end.set(false);
		return this;
	}

	public BattleTurnEvent startEndDone() {
		_start.set(true);
		_end.set(true);
		return this;
	}

	public BattleTurnEvent allUndone() {
		_start.set(false);
		_process.set(false);
		_end.set(false);
		return this;
	}

	public BattleTurnEvent allDone() {
		_start.set(true);
		_process.set(true);
		_end.set(true);
		return this;
	}

	@Override
	public boolean start(long elapsedTime) {
		if (!_start.get()) {
			onStart(elapsedTime, _start);
		}
		return _start.get();
	}

	public abstract void onStart(long elapsedTime, BooleanValue start);

	@Override
	public boolean process(long elapsedTime) {
		if (!_process.get()) {
			onProcess(elapsedTime, _process);
		}
		return _process.get();
	}

	public abstract void onProcess(long elapsedTime, BooleanValue process);

	@Override
	public boolean end(long elapsedTime) {
		if (!_end.get()) {
			onEnd(elapsedTime, _end);
		}
		return _end.get();
	}

	public abstract void onEnd(long elapsedTime, BooleanValue end);

	public boolean isDone() {
		return _start.get() && _process.get() && _end.get();
	}

	@Override
	public BattleState getState() {
		return _state;
	}

	@Override
	public boolean completed() {
		return isDone();
	}

}
