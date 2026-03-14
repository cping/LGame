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
package loon.action.sprite;

import loon.action.map.Direction;
import loon.action.map.battle.BattleMapObject;
import loon.utils.ObjectMap;

/**
 * 通用事件执行器，根据配置文件中的eventActions调用接口方法
 */
public class AnimationEventExecutor {

	public static interface IOtherService {
		void callOther(String name);
	}

	public static interface ISoundService {
		void playSound(String soundId);
	}

	public static interface ICombatService {
		void applyDamage(BattleMapObject character, Direction dir);
	}

	public static interface ISkillService {
		void startCharge(BattleMapObject caster, String skillName);

		void spawnProjectile(BattleMapObject caster, Direction dir, String projectileName);

		void explode(BattleMapObject caster, Direction dir, String effectName);
	}

	public static interface IEventExecutor {
		void execute(String eventType, BattleMapObject character, Direction dir);
	}

	private final ISoundService soundService;
	private final ICombatService combatService;
	private final ISkillService skillService;
	private final IOtherService otherService;
	private final ObjectMap<String, ObjectMap<String, Object>> eventActions;

	public AnimationEventExecutor(ISoundService soundService, ICombatService combatService, ISkillService skillService,
			IOtherService otherService, ObjectMap<String, ObjectMap<String, Object>> eventActions) {
		this.soundService = soundService;
		this.combatService = combatService;
		this.skillService = skillService;
		this.otherService = otherService;
		this.eventActions = eventActions;
	}

	public void execute(String eventType, BattleMapObject character, Direction dir) {
		ObjectMap<String, Object> actionCfg = eventActions.get(eventType);
		if (actionCfg == null) {
			return;
		}
		String type = (String) actionCfg.get("type");
		String method = (String) actionCfg.get("method");
		Object[] args = (Object[]) actionCfg.get("args");
		switch (type) {
		case "sound":
			if (soundService != null && "playSound".equals(method) && args != null && args.length > 0) {
				soundService.playSound((String) args[0]);
			}
			break;
		case "combat":
			if (combatService != null && "applyDamage".equals(method)) {
				combatService.applyDamage(character, dir);
			}
			break;
		case "skill":
			if (skillService != null) {
				if ("startCharge".equals(method) && args != null && args.length > 0) {
					skillService.startCharge(character, (String) args[0]);
				} else if ("spawnProjectile".equals(method) && args != null && args.length > 0) {
					skillService.spawnProjectile(character, dir, (String) args[0]);
				} else if ("explode".equals(method) && args != null && args.length > 0) {
					skillService.explode(character, dir, (String) args[0]);
				}
			}
			break;
		default:
			if (otherService != null) {
				otherService.callOther((String) args[0]);
			}
			break;
		}
	}
}
