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

import loon.BaseIO;
import loon.Json;
import loon.LSystem;
import loon.action.map.battle.script.BattleScriptEventType.EventCondition;
import loon.utils.ObjectMap;
import loon.utils.TArray;

/**
 * 战斗事件触发器总控，以回合为基本单位，按设定触发不同战斗事件(即时战略本质也一样，敌我双方同时行动，过一定时间后台自动挑一个回合就是即时战略触发器了……)
 * 
 * BattleScriptContext context = new BattleScriptContext(0, hero, map, "Sunny", allUnits, commander);

   BattleScriptScheduler scheduler = new BattleScriptScheduler();
   scheduler.loadFromJson("game_events.json", context);

   // 示例，战斗回合推进(battle包中有很多设置回合增加的地方，随便放在哪里都行，或者自己写一个也行，battlemap中也会内置，外部调用一下即可)
   for (int turn = 1; turn <= 12; turn++) {
    context.setTurn(turn);
    scheduler.update(turn);
   }

 * json配置示例(纯编码也行):
 *{
  "events": [
    {
      "type": "REINFORCEMENT_ARRIVED",
      "priority": 1,
      "triggerTurn": 2,
      "actions": [
        { "action": "Dialogue", "text": "什么，敌军居然在北方集结了！" },
        { "action": "Reinforcement", "unit": "敌军弓箭手", "x": 3, "y": 8 }
      ]
    },
    {
      "type": "REINFORCEMENT_ARRIVED",
      "priority": 1,
      "triggerTurn": 5,
      "actions": [
        { "action": "Dialogue", "text": "敌军骑兵突袭南方防线！" },
        { "action": "Reinforcement", "unit": "敌军骑兵", "x": 12, "y": 2 }
      ]
    },
    {
      "type": "TIME_LIMIT_REACHED",
      "priority": 1,
      "triggerTurn": 12,
      "actions": [
        { "action": "Dialogue", "text": "时间已到，未能完成任务……战斗失败！" },
        { "action": "Defeat" }
      ]
    },
    {
      "type": "OBJECTIVE_COMPLETED",
      "priority": 1,
      "triggerTurn": 0,
      "actions": [
        { "action": "Dialogue", "text": "目标一完成：成功击败敌军首领！" }
      ]
    },
    {
      "type": "OBJECTIVE_COMPLETED",
      "priority": 1,
      "triggerTurn": 0,
      "actions": [
        { "action": "Dialogue", "text": "目标二完成：成功守住我方防线！" }
      ]
    },
    {
      "type": "SPECIAL_TRIGGER",
      "priority": 1,
      "triggerTurn": 10,
      "actions": [
        { "action": "Dialogue", "text": "隐藏剧情触发：敌军援军大部队到达！" },
        { "action": "Reinforcement", "unit": "敌军大部队", "x": 7, "y": 7 }
      ]
    },
    {
      "type": "BATTLE_VICTORY",
      "priority": 1,
      "triggerTurn": 0,
      "actions": [
        { "action": "Dialogue", "text": "两个目标均已完成！第一章胜利！" },
        { "action": "Victory" }
      ]
    },
    {
      "type": "COMMANDER_DEFEATED",
      "priority": 1,
      "triggerTurn": 0,
      "actions": [
        { "action": "Dialogue", "text": "我方指挥官阵亡，战斗失败……" },
        { "action": "Defeat" }
      ]
    }
  ]
}
 */
public class BattleScriptScheduler {

	public static interface ActionFactory {
		BattleScriptAction create(JsonAction ja);
	}

	public static interface BattleScriptAction {
		void execute(BattleScriptEvent event);
	}

	public static class CompositeAction implements BattleScriptAction {

		private final TArray<BattleScriptAction> actions = new TArray<BattleScriptAction>();

		public CompositeAction addAction(BattleScriptAction action) {
			actions.add(action);
			return this;
		}

		@Override
		public void execute(BattleScriptEvent event) {
			for (BattleScriptAction action : actions) {
				action.execute(event);
			}
		}
	}

	// JSON 映射类
	public static class JsonConfig {
		TArray<JsonEvent> events;
	}

	public static class JsonEvent {
		String type;
		int priority;
		int triggerTurn;
		TArray<JsonAction> actions;
	}

	public static class JsonAction {
		String action;
		String text;
		String unit;
		int x;
		int y;
		String weather;
	}

	public static class ScheduledEvent {

		private BattleScriptEvent event;
		private final BattleScriptAction action;
		private final boolean repeating;
		private final int interval;

		public ScheduledEvent(BattleScriptEvent event, BattleScriptAction action, boolean repeating, int interval) {
			this.event = event;
			this.action = action;
			this.repeating = repeating;
			this.interval = interval;
		}

		public BattleScriptEvent getEvent() {
			return event;
		}

		public void setEvent(BattleScriptEvent event) {
			this.event = event;
		}

		public BattleScriptAction getAction() {
			return action;
		}

		public boolean isRepeating() {
			return repeating;
		}

		public int getInterval() {
			return interval;
		}
	}

	public static class ScriptedEventListener implements BattleScriptEventListener {

		private final BattleScriptAction action;

		public ScriptedEventListener(BattleScriptAction action) {
			this.action = action;
		}

		@Override
		public void onEvent(BattleScriptEvent event) {
			action.execute(event);
		}
	}

	private final ObjectMap<String, ActionFactory> registry = new ObjectMap<String, ActionFactory>();

	private final TArray<ScheduledEvent> scheduledEvents;

	private final BattleScriptEventManager eventManager;

	public BattleScriptScheduler() {
		this(BattleScriptEventManager.getInstance());
	}

	public BattleScriptScheduler(BattleScriptEventManager e) {
		eventManager = e;
		scheduledEvents = new TArray<ScheduledEvent>();
	}

	/**
	 * 具体名称对应的action事件触发在此注册
	 * 
	 * @param name
	 * @param factory
	 */
	public void register(String name, ActionFactory factory) {
		registry.put(name, factory);
	}

	public BattleScriptAction create(JsonAction ja) {
		ActionFactory factory = registry.get(ja.action);
		if (factory != null) {
			return factory.create(ja);
		}
		return null;
	}

	public void loadFromJson(String filePath, BattleScriptContext context) {
		Json.Object json = (Json.Object) BaseIO.loadJsonObject(filePath);
		Json.Array events = json.getArray("events");
		if (events != null) {
			for (int i = 0; i < events.length(); i++) {
				Json.Object eventNode = events.getObject(i);
				String typeName = eventNode.getString("type");
				int priority = eventNode.getInt("priority");
				int triggerTurn = eventNode.getInt("triggerTurn");
				BattleScriptEventType type = getEventTypeByName(typeName);
				BattleScriptEvent event = new BattleScriptEvent(type, priority, triggerTurn, context);
				CompositeAction composite = new CompositeAction();
				Json.Array actionNodes = eventNode.getArray("actions");
				if (actionNodes != null) {
					for (int j = 0; j < actionNodes.length(); j++) {
						Json.Object actionNode = actionNodes.getObject(j);
						JsonAction ja = new JsonAction();
						ja.action = actionNode.getString("action", LSystem.UNKNOWN);
						ja.text = actionNode.getString("text", LSystem.EMPTY);
						ja.unit = actionNode.getString("unit", LSystem.UNKNOWN);
						ja.x = actionNode.getInt("x", 0);
						ja.y = actionNode.getInt("y", 0);
						ja.weather = actionNode.getString("weather", LSystem.UNKNOWN);
						BattleScriptAction action = create(ja);
						if (action != null) {
							composite.addAction(action);
						}
					}
				}
				scheduledEvents.add(new ScheduledEvent(event, composite, false, 0));
			}
		}
	}

	private BattleScriptEventType getEventTypeByName(String name) {
		if ("REINFORCEMENT_ARRIVED".equals(name)) {
			return BattleScriptEventType.REINFORCEMENT_ARRIVED;
		}
		if ("COMMANDER_DEFEATED".equals(name)) {
			return BattleScriptEventType.COMMANDER_DEFEATED;
		}
		if ("OBJECTIVE_COMPLETED".equals(name)) {
			return BattleScriptEventType.OBJECTIVE_COMPLETED;
		}
		if ("UNIT_DEATH".equals(name)) {
			return BattleScriptEventType.UNIT_DEATH;
		}
		if ("UNIT_LOW_HEALTH".equals(name)) {
			return BattleScriptEventType.UNIT_LOW_HEALTH;
		}
		if ("WEATHER_CHANGED".equals(name)) {
			return BattleScriptEventType.WEATHER_CHANGED;
		}
		if ("BATTLE_VICTORY".equals(name)) {
			return BattleScriptEventType.BATTLE_VICTORY;
		}
		if ("BATTLE_DEFEAT".equals(name)) {
			return BattleScriptEventType.BATTLE_DEFEAT;
		}
		// 自定义事件在这
		return new BattleScriptEventType(name, "Custom Event");
	}

	public BattleScriptScheduler addEvent(BattleScriptEventType type, int priority, int triggerTurn,
			BattleScriptContext context, BattleScriptAction action) {
		BattleScriptEvent event = new BattleScriptEvent(type, priority, triggerTurn, context);
		eventManager.addListener(new ScriptedEventListener(action));
		scheduledEvents.add(new ScheduledEvent(event, action, false, 0));
		return this;
	}

	public BattleScriptScheduler addEvents(TArray<BattleScriptEvent> events, BattleScriptAction action) {
		for (BattleScriptEvent e : events) {
			eventManager.addListener(new ScriptedEventListener(action));
			scheduledEvents.add(new ScheduledEvent(e, action, false, 0));
		}
		return this;
	}

	public BattleScriptScheduler addEventWithCondition(BattleScriptEventType type, int priority,
			BattleScriptContext context, EventCondition condition, BattleScriptAction action) {
		type.addCondition(condition);
		BattleScriptEvent event = new BattleScriptEvent(type, priority, 0, context);
		scheduledEvents.add(new ScheduledEvent(event, action, false, 0));
		return this;
	}

	public BattleScriptScheduler addRepeatingEvent(BattleScriptEventType type, int priority, int startTurn,
			int interval, BattleScriptContext context, BattleScriptAction action) {
		BattleScriptEvent event = new BattleScriptEvent(type, priority, startTurn, context);
		scheduledEvents.add(new ScheduledEvent(event, action, true, interval));
		return this;
	}

	public void updateTurn(int currentTurn) {
		TArray<ScheduledEvent> triggered = new TArray<ScheduledEvent>();
		for (ScheduledEvent se : scheduledEvents) {
			BattleScriptEvent event = se.getEvent();
			boolean shouldTrigger = false;
			// 回合触发
			if (event.getTriggerTurn() > 0 && event.getTriggerTurn() == currentTurn) {
				shouldTrigger = true;
			}
			// 条件触发
			if (event.getEventType().isTriggered(event.getContext())) {
				shouldTrigger = true;
			}
			if (shouldTrigger) {
				eventManager.addListener(new ScriptedEventListener(se.getAction()));
				eventManager.queueEvent(event);
				// 循环事件，更新下次触发回合
				if (se.isRepeating()) {
					int nextTurn = currentTurn + se.getInterval();
					BattleScriptEvent nextEvent = new BattleScriptEvent(event.getEventType(), event.getPriority(),
							nextTurn, event.getContext());
					se.setEvent(nextEvent);
				} else {
					triggered.add(se);
				}
			}
		}
		scheduledEvents.removeAll(triggered);
		eventManager.processEvents(currentTurn);
	}

	public BattleScriptScheduler addEventAtTurn(BattleScriptEventType type, int priority, int triggerTurn,
			BattleScriptContext context, BattleScriptAction action) {
		BattleScriptEvent event = new BattleScriptEvent(type, priority, triggerTurn, context);
		scheduledEvents.add(new ScheduledEvent(event, action, false, 0));
		return this;
	}

	public TArray<ScheduledEvent> getScheduledEvents() {
		return new TArray<ScheduledEvent>(scheduledEvents);
	}
}
