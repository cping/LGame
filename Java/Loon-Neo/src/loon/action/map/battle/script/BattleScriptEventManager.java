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

import java.util.Comparator;

import loon.utils.MathUtils;
import loon.utils.TArray;

public class BattleScriptEventManager {

	private final static Comparator<BattleScriptEvent> sortScriptEvent = new Comparator<BattleScriptEvent>() {

		@Override
		public int compare(BattleScriptEvent o1, BattleScriptEvent o2) {
			return MathUtils.compare(o1.getPriority(), o2.getPriority());
		}
	};
	
	private static volatile BattleScriptEventManager instance;

	public static BattleScriptEventManager getInstance() {
		if (instance == null) {
			synchronized (BattleScriptEventManager.class) {
				if (instance == null) {
					instance = new BattleScriptEventManager();
				}
			}
		}
		return instance;
	}

	private final TArray<BattleScriptEventListener> listeners;
	private final TArray<BattleScriptEvent> eventQueue;
	private final TArray<BattleScriptEvent> eventLog;

	private BattleScriptEventManager() {
		listeners = new TArray<BattleScriptEventListener>();
		eventQueue = new TArray<BattleScriptEvent>();
		eventLog = new TArray<BattleScriptEvent>();
	}

	public void addListener(BattleScriptEventListener listener) {
		listeners.add(listener);
	}

	public void removeListener(BattleScriptEventListener listener) {
		listeners.remove(listener);
	}

	public void dispatchEvent(BattleScriptEvent event) {
		if (!event.isTriggered()) {
			return;
		}
		for (BattleScriptEventListener listener : listeners) {
			listener.onEvent(event);
		}
		eventLog.add(event);
	}

	public void queueEvent(BattleScriptEvent event) {
		if (!eventQueue.contains(event)) {
			eventQueue.add(event);
		}
	}

	public void processEvents(int currentTurn) {
		eventQueue.sort(sortScriptEvent);
		TArray<BattleScriptEvent> remaining = new TArray<BattleScriptEvent>();
		for (BattleScriptEvent event : eventQueue) {
			if (event.getTriggerTurn() > currentTurn) {
				remaining.add(event);
				continue;
			}
			if (event.isTriggered()) {
				dispatchEvent(event);
			}
		}
		eventQueue.clear();
		eventQueue.addAll(remaining);
	}

	public void processEvents() {
		eventQueue.sort(sortScriptEvent);
		for (BattleScriptEvent event : eventQueue) {
			dispatchEvent(event);
		}
		eventQueue.clear();
	}

	public TArray<BattleScriptEvent> getEventQueue() {
		return new TArray<BattleScriptEvent>(eventQueue);
	}

	public TArray<BattleScriptEvent> getEventLog() {
		return new TArray<BattleScriptEvent>(eventLog);
	}

	public void clear() {
		eventQueue.clear();
		eventLog.clear();
	}
}
