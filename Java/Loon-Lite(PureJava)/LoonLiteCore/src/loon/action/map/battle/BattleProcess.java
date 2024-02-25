/**
 * Copyright 2008 - 2023 The Loon Game Engine Authors
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

import java.util.Comparator;
import java.util.Iterator;

import loon.LSystem;
import loon.action.map.items.Role;
import loon.action.map.items.Teams;
import loon.events.EventActionN;
import loon.events.Updateable;
import loon.geom.BooleanValue;
import loon.utils.HelperUtils;
import loon.utils.MathUtils;
import loon.utils.ObjectBundle;
import loon.utils.ObjectMap;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.processes.CoroutineProcess;
import loon.utils.processes.GameProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.processes.WaitProcess;
import loon.utils.reply.RollbackVar;
import loon.utils.timer.LTimerContext;

/**
 * loon提供的战斗进程管理用类,以内嵌多事件模块顺序(或非顺序)循环的方式进行有序(或无序)的战斗进程管理
 */
public class BattleProcess extends CoroutineProcess {

	public static abstract class TurnBeginEvent extends BattleTurnProcessEvent {

		public TurnBeginEvent() {
			this(true);
		}

		public TurnBeginEvent(boolean skipAndResetFlag) {
			super(BattleState.TurnBeginState, skipAndResetFlag);
		}

	}

	public static abstract class TurnPlayerEvent extends BattleTurnProcessEvent {

		public TurnPlayerEvent() {
			this(true);
		}

		public TurnPlayerEvent(boolean skipAndResetFlag) {
			super(BattleState.TurnPlayerState, skipAndResetFlag);
		}

	}

	public static abstract class TurnEnemyEvent extends BattleTurnProcessEvent {

		public TurnEnemyEvent() {
			this(true);
		}

		public TurnEnemyEvent(boolean skipAndResetFlag) {
			super(BattleState.TurnEnemyState, skipAndResetFlag);
		}

	}

	public static abstract class TurnNpcEvent extends BattleTurnProcessEvent {

		public TurnNpcEvent() {
			this(true);
		}

		public TurnNpcEvent(boolean skipAndResetFlag) {
			super(BattleState.TurnNpcState, skipAndResetFlag);
		}

	}

	public static abstract class TurnOtherEvent extends BattleTurnProcessEvent {

		public TurnOtherEvent() {
			this(true);
		}

		public TurnOtherEvent(boolean skipAndResetFlag) {
			super(BattleState.TurnOtherState, skipAndResetFlag);
		}

	}

	public static abstract class TurnEndEvent extends BattleTurnProcessEvent {

		public TurnEndEvent() {
			this(true);
		}

		public TurnEndEvent(boolean skipAndResetFlag) {
			super(BattleState.TurnEndState, skipAndResetFlag);
		}

	}

	public static abstract class TurnDoneEvent extends BattleTurnProcessEvent {

		public TurnDoneEvent() {
			this(true);
		}

		public TurnDoneEvent(boolean skipAndResetFlag) {
			super(BattleState.TurnDoneState, skipAndResetFlag);
		}

	}

	static class EventComparator implements Comparator<BattleEvent> {

		@Override
		public int compare(BattleEvent o1, BattleEvent o2) {
			if (o1 == null || o2 == null) {
				return 0;
			}
			return o2.getState().getPriority() - o1.getState().getPriority();
		}

	}

	public static BattleAction createBattleAction(Role src, Role dst) {
		return new BattleAction(src, dst);
	}

	public static BattleAction createBattleAction(Role src, Role dst, float cost) {
		return new BattleAction(src, dst, cost);
	}

	private final static EventComparator _sortEvents = new EventComparator();

	private RollbackVar<ObjectBundle> _battleTurnHistory = new RollbackVar<ObjectBundle>();

	private ObjectMap<String, Object> _battleVars = new ObjectMap<String, Object>();

	private Teams _mainTeams;

	private float _minBattleWaitSeconds = 0.1f;

	private float _maxBattleWaitSeconds = 5f;

	private float _battleWaitSeconds = 1f;

	private float _battleSpeed;

	private final BooleanValue _actioning = new BooleanValue();

	private TArray<BattleState> _states;

	private TArray<BattleEvent> _events;

	private BattleEvent _currentBattleEvent;

	private BattleEvent _enforceEvent;

	private BattleState _stateCurrent;

	private BattleState _stateCompleted;

	private BattleResults _result;

	private TArray<GameProcess> _waitProcess;

	private int _roundAmount;

	private boolean _battleEventLocked;

	private boolean _pause;

	private boolean _enforce;

	private boolean _loop;

	private boolean _waiting;

	public BattleProcess() {
		this("Battle", 0);
	}

	public BattleProcess(String name) {
		this(name, 0);
	}

	public BattleProcess(String name, long delay) {
		this._waitProcess = new TArray<GameProcess>();
		this._states = new TArray<BattleState>();
		this._events = new TArray<BattleEvent>();
		this._result = BattleResults.Running;
		this._mainTeams = new Teams(name);
		this._minBattleWaitSeconds = 0.1f;
		this._maxBattleWaitSeconds = 5f;
		this._battleWaitSeconds = 1f;
		this.setLoop(true);
		this.setDelay(delay);
	}

	public BattleProcess setVar(String name, Object v) {
		_battleVars.put(name, v);
		return this;
	}

	public String getVar(String name) {
		Object v = _battleVars.get(name);
		if (v == null) {
			return null;
		}
		return HelperUtils.toStr(v);
	}

	public String removeVar(String name) {
		Object v = _battleVars.remove(name);
		if (v == null) {
			return null;
		}
		return HelperUtils.toStr(v);
	}

	public BattleProcess clearVars() {
		_battleVars.clear();
		return this;
	}

	public boolean isBool(String name) {
		return StringUtils.toBoolean(getVar(name));
	}

	/**
	 * 创建指定战斗回合的历史数据(记录特定回合状态用的,比如有些游戏有回合触发剧情,这时候这个功能就有用了,记录状态才能后续触发)
	 * 
	 * @param turn
	 * @return
	 */
	public ObjectBundle createTurnHistory(int turn) {
		final int tick = getTurnToRoundAmount(turn);
		ObjectBundle data = _battleTurnHistory.get(turn);
		if (data != null) {
			data.clear();
			return data;
		} else {
			data = new ObjectBundle();
		}
		_battleTurnHistory.add(tick, data);
		return data;
	}

	/**
	 * 指定回合历史数据是否存在
	 * 
	 * @param turn
	 * @return
	 */
	public boolean hasTurnHistory(int turn) {
		return _battleTurnHistory.has(getTurnToRoundAmount(turn));
	}

	/**
	 * 获得指定回合的上一回合历史数据
	 * 
	 * @param turn
	 * @return
	 */
	public ObjectBundle getTurnLastHistory(int turn) {
		return _battleTurnHistory.last(getTurnToRoundAmount(turn));
	}

	/**
	 * 获得指定回合历史数据
	 * 
	 * @param turn
	 * @return
	 */
	public ObjectBundle getTurnHistory(int turn) {
		return _battleTurnHistory.get(getTurnToRoundAmount(turn));
	}

	/**
	 * 删除指定回合历史数据
	 * 
	 * @param turn
	 * @return
	 */
	public ObjectBundle removeTurnHistory(int turn) {
		return _battleTurnHistory.remove(getTurnToRoundAmount(turn));
	}

	/**
	 * 获得所有大于指定回合的历史数据
	 * 
	 * @param turn
	 * @return
	 */
	public TArray<ObjectBundle> getTurnHistoryBigger(int turn) {
		return _battleTurnHistory.getTicksBigger(getTurnToRoundAmount(turn));
	}

	/**
	 * 获得所有小于指定回合的历史数据
	 * 
	 * @param turn
	 * @return
	 */
	public TArray<ObjectBundle> getTurnHistorySmaller(int turn) {
		return _battleTurnHistory.getTicksSmaller(getTurnToRoundAmount(turn));
	}

	/**
	 * 删除所有大于指定回合的历史数据
	 * 
	 * @param turn
	 * @return
	 */
	public BattleProcess removeTurnHistoryBigger(int turn) {
		_battleTurnHistory.removeTickBigger(getTurnToRoundAmount(turn));
		return this;
	}

	/**
	 * 删除所有小于指定回合的历史数据
	 * 
	 * @param turn
	 * @return
	 */
	public BattleProcess removeTurnHistorySmaller(int turn) {
		_battleTurnHistory.removeTickSmaller(getTurnToRoundAmount(turn));
		return this;
	}

	public ObjectBundle createCurrentTurnHistory() {
		return createTurnHistory(getTurnCount());
	}

	public boolean hasCurrentTurnHistory() {
		return hasTurnHistory(getTurnCount());
	}

	public ObjectBundle getCurrentTurnLastHistory() {
		return getTurnLastHistory(getTurnCount());
	}

	public ObjectBundle getCurrentTurnHistory() {
		return getTurnHistory(getTurnCount());
	}

	public TArray<ObjectBundle> getCurrentTurnHistoryBigger() {
		return getTurnHistoryBigger(getTurnCount());
	}

	public TArray<ObjectBundle> getCurrentTurnHistorySmaller() {
		return getTurnHistorySmaller(getTurnCount());
	}

	public ObjectBundle removeCurrentTurnHistory() {
		return removeTurnHistory(getTurnCount());
	}

	public BattleProcess removeCurrentTurnHistoryBigger() {
		return removeTurnHistoryBigger(getTurnCount());
	}

	public BattleProcess removeCurrentTurnHistorySmaller() {
		return removeTurnHistorySmaller(getTurnCount());
	}

	/**
	 * 清空全部回合历史数据
	 * 
	 * @return
	 */
	public BattleProcess clearTurnHistory() {
		_battleTurnHistory.clear();
		return this;
	}

	protected boolean runBattleEvent(final BattleEvent turnEvent, final long elapsedTime) {
		if (checkProcessWait()) {
			return false;
		}
		if (turnEvent == null) {
			return false;
		}
		final BattleState state = turnEvent.getState();
		this._stateCurrent = state;
		if (_stateCompleted != state) {
			if (turnEvent != null) {
				this._currentBattleEvent = turnEvent;
				if (!turnEvent.start(elapsedTime) || _waiting) {
					return false;
				}
				if (!turnEvent.process(elapsedTime) || _waiting) {
					return false;
				}
				if (!turnEvent.end(elapsedTime) || _waiting) {
					return false;
				}
				if (!turnEvent.completed() || _waiting) {
					return false;
				}
				if (_waiting) {
					return false;
				}
				turnEvent.reset();
			}
			_stateCompleted = state;
		}
		return true;
	}

	protected boolean updateBattleEvent(final BattleEvent turnEvent, final long elapsedTime) {
		if (checkProcessWait()) {
			return false;
		}
		if (turnEvent == null) {
			return false;
		}
		final BattleState state = turnEvent.getState();
		if (!_states.contains(state)) {
			this._stateCurrent = state;
			if (_stateCompleted != state) {
				if (turnEvent != null) {
					this._currentBattleEvent = turnEvent;
					if (!turnEvent.start(elapsedTime) || _waiting) {
						return false;
					}
					if (!turnEvent.process(elapsedTime) || _waiting) {
						return false;
					}
					if (!turnEvent.end(elapsedTime) || _waiting) {
						return false;
					}
					if (!turnEvent.completed() || _waiting) {
						return false;
					}
					if (_waiting) {
						return false;
					}
					turnEvent.reset();
				}
				_stateCompleted = state;
				_states.add(state);
			}
		}
		return true;
	}

	public BattleEvent getCurrentBattleEvent() {
		return this._currentBattleEvent;
	}

	public Teams getTeams() {
		return _mainTeams;
	}

	/**
	 * 强制跳去指定事件
	 * 
	 * @param name
	 * @return
	 */
	public BattleProcess jumpTo(String name) {
		return jumpTo(get(name));
	}

	public BattleProcess jumpTo(BattleState state) {
		return jumpTo(get(state));
	}

	public BattleProcess jumpTo(BattleEvent eve) {
		if (eve != null) {
			eve.reset();
		}
		_enforceEvent = eve;
		_stateCurrent = null;
		_stateCompleted = null;
		_enforce = true;
		return this;
	}

	public BattleEvent get(String name) {
		BattleEvent eve = null;
		for (Iterator<BattleEvent> it = _events.iterator(); it.hasNext();) {
			BattleEvent e = it.next();
			if (e != null && name.equalsIgnoreCase(e.getState().getName())) {
				eve = e;
			}
		}
		return eve;
	}

	public BattleEvent get(BattleState state) {
		BattleEvent eve = null;
		for (Iterator<BattleEvent> it = _events.iterator(); it.hasNext();) {
			BattleEvent e = it.next();
			if (e != null && e.getState().equals(state)) {
				eve = e;
			}
		}
		return eve;
	}

	public int getTotalTeams() {
		return _mainTeams.getSize();
	}

	protected int getTurnToRoundAmount(int turn) {
		return MathUtils.max(0, turn - 1);
	}

	public int getTurnCount() {
		return getTurnCount(_roundAmount);
	}

	public int getTurnCount(int count) {
		return MathUtils.max(0, count + 1);
	}

	public int getRoundAmount() {
		return _roundAmount;
	}

	@Override
	public void run(LTimerContext time) {
		if (isCoroutineRunning()) {
			super.run(time);
			update(time.timeSinceLastUpdate);
		}
	}

	protected boolean checkProcessWait() {
		if (_waiting) {
			if (_waitProcess.size > 0) {
				synchronized (_waitProcess) {
					for (Iterator<GameProcess> it = _waitProcess.iterator(); it.hasNext();) {
						GameProcess process = it.next();
						if (process != null && !process.isDead()) {
							return (_waiting = true);
						}
					}
					_waitProcess.clear();
					this._waiting = false;
				}
			} else {
				this._waiting = false;
			}
		}
		return this._waiting;
	}

	public void update(long elapsedTime) {
		if (_pause) {
			return;
		}
		if (checkProcessWait()) {
			return;
		}
		if (_result != BattleResults.Running) {
			return;
		}
		if (_enforce) {
			if (!runBattleEvent(_enforceEvent, elapsedTime)) {
				return;
			}
			this._enforce = false;
			this._enforceEvent = null;
			this._roundAmount++;
		} else {
			if (!_loop) {
				return;
			}
			for (Iterator<BattleEvent> it = _events.iterator(); it.hasNext();) {
				BattleEvent e = it.next();
				if (e != null) {
					if (!updateBattleEvent(e, elapsedTime)) {
						return;
					}
				}
			}
			_actioning.set(false);
			_roundAmount++;
		}
		_states.clear();
		_stateCurrent = null;
		_stateCompleted = null;
	}

	public BattleProcess noLoop() {
		this._loop = false;
		return this;
	}

	public boolean isloop() {
		return _loop;
	}

	public BattleProcess setLoop(boolean l) {
		this._loop = l;
		return this;
	}

	public BattleProcess removeEvent(BattleEvent e) {
		if (e == null) {
			return this;
		}
		_events.remove(e);
		_events.sort(_sortEvents);
		e.setMainProcess(null);
		return this;
	}

	public BattleProcess addEvent(BattleEvent e) {
		if (e == null) {
			return this;
		}
		e.setMainProcess(this);
		_events.add(e);
		_events.sort(_sortEvents);
		return this;
	}

	@Override
	public BattleProcess reset() {
		super.reset();
		return clean();
	}

	public void callBattleEvent(EventActionN event) {
		if (_battleEventLocked || get()) {
			return;
		}
		lockBattle();
		if (event != null) {
			event.update();
		}
	}

	public boolean isBattleLocked() {
		return _battleEventLocked;
	}

	public BattleProcess lockBattle() {
		_battleEventLocked = true;
		return this;
	}

	public BattleProcess unlockBattle() {
		_battleEventLocked = false;
		return this;
	}

	public BattleProcess clearEventMainProcess() {
		final int size = _events.size;
		for (int i = size - 1; i > -1; i--) {
			BattleEvent e = _events.get(i);
			if (e != null) {
				e.setMainProcess(null);
			}
		}
		return this;
	}

	public BattleProcess clean() {
		this._waitProcess.clear();
		this._states.clear();
		this.clearEventMainProcess();
		this.clearVars();
		this.clearTurnHistory();
		this._events.clear();
		this._result = BattleResults.Running;
		this._actioning.set(false);
		this._enforce = false;
		this._enforceEvent = null;
		this._stateCurrent = null;
		this._stateCompleted = null;
		this._battleEventLocked = false;
		this._waiting = false;
		this._pause = false;
		this._loop = true;
		this._roundAmount = 0;
		this._minBattleWaitSeconds = 0.1f;
		this._maxBattleWaitSeconds = 5f;
		this._battleWaitSeconds = 1f;
		this._battleSpeed = 0f;
		this.setDelay(0);
		return this;
	}

	public boolean isCurrentBegin() {
		return isCurrentState(BattleState.TurnBeginState);
	}

	public boolean isCurrentPlayer() {
		return isCurrentState(BattleState.TurnPlayerState);
	}

	public boolean isCurrentEnemy() {
		return isCurrentState(BattleState.TurnEnemyState);
	}

	public boolean isCurrentNpc() {
		return isCurrentState(BattleState.TurnNpcState);
	}

	public boolean isCurrentOther() {
		return isCurrentState(BattleState.TurnOtherState);
	}

	public boolean isCurrentEnd() {
		return isCurrentState(BattleState.TurnEndState);
	}

	public boolean isCurrentDone() {
		return isCurrentState(BattleState.TurnDoneState);
	}

	public boolean isCurrentState(BattleState state) {
		if (state == null) {
			return false;
		}
		return state.equals(currentState());
	}

	public BattleState currentState() {
		if (_stateCurrent == null) {
			if (_events.size > 0) {
				return (_stateCurrent = _events.first().getState());
			}
		}
		return _stateCurrent;
	}

	public BattleState previousState() {
		return _stateCompleted;
	}

	public boolean isPaused() {
		return _pause;
	}

	public BattleProcess setPause(boolean p) {
		this._pause = p;
		return this;
	}

	public TArray<BattleState> getStates() {
		return _states;
	}

	public BattleResults getResult() {
		return _result;
	}

	public BattleProcess setResult(BattleResults result) {
		this._result = result;
		return this;
	}

	public float getBattleWaitSeconds() {
		if (_battleSpeed != 0f) {
			_battleWaitSeconds = MathUtils.lerp(_maxBattleWaitSeconds, _minBattleWaitSeconds, _battleSpeed);
		}
		return _battleWaitSeconds * (1f + MathUtils.random());
	}

	public WaitProcess getWaitProcess(Updateable update) {
		return getWaitProcess(update, getBattleWaitSeconds());
	}

	public WaitProcess getWaitProcess(Updateable update, float s) {
		return new WaitProcess(MathUtils.floor(s * LSystem.SECOND), update);
	}

	public WaitProcess wait(Updateable update) {
		if (update != null) {
			return wait(getWaitProcess(update));
		}
		return null;
	}

	public WaitProcess wait(Updateable update, float s) {
		if (update != null) {
			return wait(getWaitProcess(update, s));
		}
		return null;
	}

	public WaitProcess wait(WaitProcess waitProcess) {
		if (waitProcess != null) {
			synchronized (_waitProcess) {
				_waitProcess.add(waitProcess);
				_waiting = true;
				RealtimeProcessManager.get().addProcess(waitProcess);
				return waitProcess;
			}
		}
		return null;
	}

	public BattleProcess dontWait() {
		_waitProcess.clear();
		_waiting = false;
		return this;
	}

	public boolean isWaiting() {
		return _waiting;
	}

	public BattleProcess playNext() {
		_actioning.set(true);
		return this;
	}

	public BattleProcess stopNext() {
		_actioning.set(false);
		return this;
	}

	public BattleProcess setActioning(boolean a) {
		_actioning.set(a);
		return this;
	}

	public BattleProcess set(boolean a) {
		_actioning.set(a);
		return this;
	}

	public boolean get() {
		return _actioning.get();
	}

	public boolean isFighting() {
		return _actioning.get();
	}

	public boolean isActioning() {
		return _actioning.get();
	}

	public float getMinBattleWaitSeconds() {
		return _minBattleWaitSeconds;
	}

	public BattleProcess setMinBattleWaitSeconds(float s) {
		this._minBattleWaitSeconds = s;
		return this;
	}

	public float getMaxBattleWaitSeconds() {
		return _maxBattleWaitSeconds;
	}

	public BattleProcess setMaxBattleWaitSeconds(float s) {
		this._maxBattleWaitSeconds = s;
		return this;
	}

	public BattleProcess setBattleWaitSeconds(float s) {
		this._battleWaitSeconds = s;
		return this;
	}

	public float getBattleSpeed() {
		return _battleSpeed;
	}

	public BattleProcess setBattleSpeed(float s) {
		this._battleSpeed = s;
		return this;
	}

	public BattleProcess lockProcess(EventActionN e) {
		return lockProcess(null, e);
	}

	public BattleProcess lockProcess(BooleanValue process, EventActionN e) {
		if (get()) {
			set(false);
			if (process != null) {
				process.set(false);
			}
			lockBattle();
			if (e != null) {
				HelperUtils.callEventAction(e, process);
			}
		}
		return this;
	}

	public BattleProcess unlockProcess() {
		return unlockProcess(null);
	}

	public BattleProcess unlockProcess(BooleanValue process) {
		if (!get()) {
			set(true);
			if (process != null) {
				process.set(true);
			}
			unlockBattle();
		}
		return this;
	}

	public BattleProcess lockProcessBegin(EventActionN e) {
		return lockProcessBegin(null, e);
	}

	public BattleProcess lockProcessBegin(BooleanValue process, EventActionN e) {
		if (!get()) {
			set(true);
			if (process != null) {
				process.set(false);
			}
			lockBattle();
			if (e != null) {
				HelperUtils.callEventAction(e, process);
			}
		}
		return this;
	}

	public BattleProcess unlockProcessEnd() {
		return unlockProcessEnd(null);
	}

	public BattleProcess unlockProcessEnd(BooleanValue process) {
		if (!get()) {
			set(false);
			if (process != null) {
				process.set(true);
			}
			unlockBattle();
		}
		return this;
	}

	public BattleProcess over() {
		kill();
		return this;
	}

	@Override
	public void close() {
		super.close();
		clean();
	}

}
