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

import loon.LSystem;

public class BattleState {

	// 这个参数事实上是BattleProcess中事件运行的优先级,系统默认的调高一点,并且间隔大,方便插入自定义参数
	// (当然也可以完全自定义,不使用系统Event而直接自己继承BattleEvent或者BattleTurnEvent构建)
	public final static int TurnBegin = 100;

	public final static int TurnPlayer = 80;

	public final static int TurnEnemy = 60;

	public final static int TurnNpc = 40;

	public final static int TurnOther = 20;

	public final static int TurnEnd = 10;

	public final static int TurnDone = 0;

	public final static BattleState TurnBeginState = new BattleState(TurnBegin, "TurnBegin");

	public final static BattleState TurnPlayerState = new BattleState(TurnPlayer, "TurnPlayer");

	public final static BattleState TurnEnemyState = new BattleState(TurnEnemy, "TurnEnemy");

	public final static BattleState TurnNpcState = new BattleState(TurnNpc, "TurnNpc");

	public final static BattleState TurnOtherState = new BattleState(TurnOther, "TurnOther");

	public final static BattleState TurnEndState = new BattleState(TurnEnd, "TurnEnd");

	public final static BattleState TurnDoneState = new BattleState(TurnDone, "TurnDone");

	private final int _priority;

	private final String _name;

	public BattleState(int priority, String name) {
		this._priority = priority;
		this._name = name;
	}

	public int getPriority() {
		return this._priority;
	}

	public String getName() {
		return this._name;
	}

	@Override
	public int hashCode() {
		int result = 38;
		result = LSystem.unite(result, this._priority);
		result = LSystem.unite(result, this._name);
		result = LSystem.unite(result, super.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof BattleState)) {
			return false;
		}
		BattleState state = (BattleState) o;
		if ((this._name == state._name || this._name.equals(state._name)) && this._priority == state._priority) {
			return true;
		}
		if (state._name != null && state._name.equalsIgnoreCase(_name) && this._priority == state._priority) {
			return true;
		}
		if (this._name != null && this._name.equalsIgnoreCase(state._name) && this._priority == state._priority) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "name = " + this._name + " , priority = " + this._priority;
	}
}
