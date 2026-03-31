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
package loon.action.map.battle.script;

public class BattleScriptEvent {

	private final BattleScriptEventType scripttype;

	private final int priority;

	private final int triggerTurn;

	private final BattleScriptContext context;

	public BattleScriptEvent(BattleScriptEventType type, int priority, int triggerTurn, BattleScriptContext context) {
		this.scripttype = type;
		this.priority = priority;
		this.triggerTurn = triggerTurn;
		this.context = context;
	}

	public BattleScriptEventType getEventType() {
		return scripttype;
	}

	public int getPriority() {
		return priority;
	}

	public int getTriggerTurn() {
		return triggerTurn;
	}

	public BattleScriptContext getContext() {
		return context;
	}

	public boolean isTriggered() {
		if (context.turn < triggerTurn) {
			return false;
		}
		return scripttype.isTriggered(context);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BattleScriptEvent)) {
			return false;
		}
		BattleScriptEvent other = (BattleScriptEvent) obj;
		return this.scripttype.getName().equals(other.scripttype.getName()) && this.context.equals(other.context);
	}

	@Override
	public int hashCode() {
		return scripttype.getName().hashCode() ^ context.hashCode();
	}
}
