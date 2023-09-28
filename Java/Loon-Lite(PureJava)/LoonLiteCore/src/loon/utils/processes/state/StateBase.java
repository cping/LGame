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
package loon.utils.processes.state;

import loon.LRelease;
import loon.LSysException;
import loon.events.EventAction;
import loon.geom.BooleanValue;
import loon.utils.HelperUtils;
import loon.utils.ObjectMap;
import loon.utils.SortedList;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

public class StateBase implements IState, LRelease {

	private StateType _stateType;

	private IState _parent;

	private String _stateName;

	private final TArray<Condition> _conditions;

	private final ObjectMap<String, EventAction> _events;

	private final ObjectMap<String, IState> _stateChildren;

	private final SortedList<IState> _activeChildren;

	private final LTimer _delayTimer;

	private EventAction _currentUpdate;

	private EventAction _currentEnter;

	private EventAction _currentExit;

	private boolean _closed;

	public StateBase() {
		this("State");
	}

	public StateBase(String stateName) {
		this._stateType = StateType.None;
		this._stateName = stateName;
		if (StringUtils.isEmpty(stateName)) {
			this._stateName = "State";
		} else {
			this._stateName = stateName;
		}
		this._events = new ObjectMap<String, EventAction>();
		this._conditions = new TArray<Condition>();
		this._stateChildren = new ObjectMap<String, IState>();
		this._activeChildren = new SortedList<IState>();
		this._delayTimer = new LTimer(0);
	}

	@Override
	public IState pushState(String stateName) {
		IState newState = _stateChildren.get(stateName);
		if (newState == null) {
			throw new LSysException("The State change to \"" + stateName + "\", But it not in the list .");
		}
		_activeChildren.push(newState);
		newState.onEnter();
		return this;
	}

	@Override
	public IState changeState(String stateName) {
		IState newState = _stateChildren.get(stateName);
		if (newState == null && _parent != null) {
			return _parent.changeState(stateName);
		}
		if (newState == null) {
			throw new LSysException("The State change to \"" + stateName + "\", But it not in the list .");
		}
		if (_activeChildren.size > 0) {
			IState state = _activeChildren.pop();
			if (state != null) {
				state.onExit();
			}
		}
		_activeChildren.push(newState);
		newState.onEnter();
		return this;
	}

	@Override
	public IState popState() {
		if (_activeChildren.size > 0) {
			IState state = _activeChildren.pop();
			if (state != null) {
				state.onExit();
			}
		} else {
			throw new LSysException("The PopState called no active children to pop.");
		}
		return this;
	}

	@Override
	public void update(long elapsedTime) {
		if (_closed) {
			return;
		}
		if (_delayTimer.action(elapsedTime)) {
			if (_activeChildren.size > 0) {
				IState state = _activeChildren.peek();
				if (state != null) {
					state.update(elapsedTime);
				}
				return;
			}
			if (_currentUpdate != null) {
				this._stateType = StateType.Processing;
				HelperUtils.callEventAction(_currentUpdate, this, elapsedTime);
			}
			for (int i = 0; i < _conditions.size; i++) {
				Condition cond = _conditions.get(i);
				if (cond != null && cond._predicate.get()) {
					HelperUtils.callEventAction(cond._action, this, cond._predicate);
				}
			}
		}
	}

	@Override
	public IState setDelay(float s) {
		_delayTimer.setDelayS(s);
		return this;
	}

	@Override
	public float getDelay() {
		return _delayTimer.getDelayS();
	}

	public StateBase addChild(IState newState, String stateName) {
		try {
			_stateChildren.put(stateName, newState);
			newState.setParent(this);
		} catch (Exception ex) {
			throw new LSysException("The State with name \"" + stateName + "\" already exists in list .");
		}
		return this;
	}

	public StateBase addChild(IState newState) {
		if (newState == null) {
			return this;
		}
		String name = newState.getName();
		return addChild(newState, name);
	}

	@Override
	public void setParent(IState s) {
		this._parent = s;
	}

	@Override
	public IState getParent() {
		return _parent;
	}

	@Override
	public StateType getStateType() {
		return _stateType;
	}

	public StateBase setCondition(BooleanValue predicate, EventAction action) {
		_conditions.add(new Condition(predicate, action));
		return this;
	}

	@Override
	public void onEnter() {
		if (_currentEnter != null) {
			_stateType = StateType.Enter;
			HelperUtils.callEventAction(_currentEnter, this);
		}
	}

	@Override
	public void onExit() {
		if (_currentExit != null) {
			_stateType = StateType.Exit;
			HelperUtils.callEventAction(_currentExit, this);
		}
		for (; _activeChildren.size > 0;) {
			IState state = _activeChildren.pop();
			if (state != null) {
				state.onExit();
			}
		}
		_activeChildren.clear();
	}

	public StateBase addEvent(final String identifier, final EventAction eventAction) {
		_events.put(identifier, eventAction);
		return this;
	}

	@Override
	public void callEvent(String name) {
		callEvent(name, null);
	}

	@Override
	public void callEvent(String name, EventAction eventAction) {
		if (_activeChildren.size > 0) {
			IState state = _activeChildren.peek();
			if (state != null) {
				state.callEvent(name, eventAction);
			}
			return;
		}
		EventAction event = _events.get(name);
		if (event != null) {
			this._stateType = StateType.Event;
			HelperUtils.callEventAction(event, this);
		}
	}

	public StateBase setUpdate(EventAction update) {
		this._currentUpdate = update;
		return this;
	}

	public StateBase setEnter(EventAction enter) {
		this._currentEnter = enter;
		return this;
	}

	public StateBase setExit(EventAction exit) {
		this._currentExit = exit;
		return this;
	}

	@Override
	public String getName() {
		return _stateName;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		if (_closed) {
			return;
		}
		this._stateType = StateType.Closed;
		this._events.clear();
		this._conditions.clear();
		this._stateChildren.clear();
		this._activeChildren.clear();
		_closed = true;
	}
}
