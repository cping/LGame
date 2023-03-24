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

import loon.events.Updateable;
import loon.geom.BooleanValue;
import loon.utils.processes.WaitProcess;

public abstract class BattleTurnEvent implements BattleEvent {

	private final BattleState _state;

	private final BooleanValue _start = new BooleanValue();

	private final BooleanValue _process = new BooleanValue();

	private final BooleanValue _end = new BooleanValue();

	private BattleProcess _battleProcess;

	private boolean _updateFlag;

	public BattleTurnEvent(BattleState state) {
		this(state, true);
	}

	public BattleTurnEvent(BattleState state, boolean updateFlag) {
		this._state = state;
		this._updateFlag = updateFlag;
	}

	@Override
	public BattleTurnEvent reset() {
		if (_updateFlag) {
			set(false);
		} else {
			set(false, false, false);
		}
		onReset();
		return this;
	}

	public abstract void onReset();

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

	public BattleTurnEvent freeStart() {
		_start.set(false);
		return this;
	}

	public BattleTurnEvent freeProcess() {
		_process.set(false);
		return this;
	}

	public BattleTurnEvent freeEnd() {
		_end.set(false);
		return this;
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

	public BattleTurnEvent undone() {
		_start.set(false);
		_process.set(false);
		_end.set(false);
		return this;
	}

	public BattleTurnEvent done() {
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

	public abstract void onCompleted();

	public WaitProcess wait(Updateable update) {
		if (_battleProcess != null) {
			return _battleProcess.wait(update);
		}
		return null;
	}

	public WaitProcess wait(Updateable update, float s) {
		if (_battleProcess != null) {
			return _battleProcess.wait(update, s);
		}
		return null;
	}

	public WaitProcess wait(WaitProcess waitProcess) {
		if (_battleProcess != null) {
			return _battleProcess.wait(waitProcess);
		}
		return null;
	}

	public BattleProcess getMainProcess() {
		return _battleProcess;
	}

	public BattleTurnEvent setMainProcess(BattleProcess battleProcess) {
		this._battleProcess = battleProcess;
		return this;
	}

	@Override
	public boolean isLocked() {
		if (_battleProcess != null) {
			return _battleProcess.get();
		}
		return false;
	}

	@Override
	public BattleEvent lock(boolean lock) {
		if (_battleProcess == null) {
			return this;
		}
		_battleProcess.set(lock);
		return this;
	}

	@Override
	public boolean completed() {
		onCompleted();
		return isDone();
	}

}
