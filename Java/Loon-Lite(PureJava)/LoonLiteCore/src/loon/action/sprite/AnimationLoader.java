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

import loon.Json;
import loon.Json.TypedArray;
import loon.LSystem;
import loon.LTexture;
import loon.action.map.Direction;
import loon.action.map.battle.BattleType.ObjectState;
import loon.opengl.TextureUtils;
import loon.utils.HelperUtils;
import loon.utils.IntArray;
import loon.utils.IntMap;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * 动画配置加载用类，基本上就是细分动画播放的Animation对象用，配置上比较复杂，虽然一劳永逸，但不用细分纹理动画的不用考虑用这个(魂类游戏，复杂动作，或者管理起来零碎图太多的，建议用这个，不然硬编码实际一写，你们会发现更麻烦)
 * 
 * sample: { "frameWidth": 64, // 每帧宽度，用于切割spritesheet "frameHeight": 64, //
 * 每帧高度 "basePath": "assets/anim/", // 所有图片的统一上级目录 "animations": { //
 * 待机动画：单帧图片，循环播放 "IDLE": { "filePattern": "idle_{0}.png", "speed": 1.0,
 * "looping": true, "isSheet": false },
 * 
 * // 火球术施法与释放 "CASTING_FIREBALL": { "filePattern":
 * "cast_fireball_{0}_sheet.png", "speed": 0.12, "looping": false, "isSheet":
 * true }, "FIREBALL": { "filePattern": "fireball_{0}_sheet.png", "speed": 0.15,
 * "looping": false, "isSheet": true },
 * 
 * // 冰冻术施法与释放 "CASTING_ICE": { "filePattern": "cast_ice_{0}_sheet.png",
 * "speed": 0.12, "looping": false, "isSheet": true }, "ICE": { "filePattern":
 * "ice_{0}_sheet.png", "speed": 0.15, "looping": false, "isSheet": true },
 * 
 * // 闪电术施法与释放 "CASTING_LIGHTNING": { "filePattern":
 * "cast_lightning_{0}_sheet.png", "speed": 0.12, "looping": false, "isSheet":
 * true }, "LIGHTNING": { "filePattern": "lightning_{0}_sheet.png", "speed":
 * 0.15, "looping": false, "isSheet": true },
 * 
 * // 大图拆分：如果我们设定一张大图包含多个动作时，分组时这样进行 "BIG_SHEET_ATTACK": { "filePattern":
 * "big_sheet.png", "speed": 0.12, "looping": false, "isSheet": true, "region":
 * { "x": 0, "y": 0, "rows": 1, "cols": 6 }, // 从大图的 (0,0) 开始，取 1 行 6 列
 * "frames": [0,1,2,3,4,5] // 指定播放的帧序列，可跳桢，可重复，只要索引存在[4,7,3,3]这样也可以 },
 * "BIG_SHEET_SKILL": { "filePattern": "big_sheet.png", "speed": 0.12,
 * "looping": false, "isSheet": true, "region": { "x": 0, "y": 64, "rows": 2,
 * "cols": 6 }, "frames": [0,1,2,3] } },
 * 
 * "transitions": { // 火球术过渡 "IDLE->CASTING_FIREBALL": { "blendTime": 0.2 },
 * "CASTING_FIREBALL->FIREBALL": { "blendTime": 0.1 }, "FIREBALL->IDLE": {
 * "blendTime": 0.3 },
 * 
 * // 冰冻术过渡 "IDLE->CASTING_ICE": { "blendTime": 0.2 }, "CASTING_ICE->ICE": {
 * "blendTime": 0.1 }, "ICE->IDLE": { "blendTime": 0.3 },
 * 
 * // 闪电术过渡 "IDLE->CASTING_LIGHTNING": { "blendTime": 0.2 },
 * "CASTING_LIGHTNING->LIGHTNING": { "blendTime": 0.1 }, "LIGHTNING->IDLE": {
 * "blendTime": 0.3 } },
 * 
 * "events": { // 火球术关键帧事件 "CASTING_FIREBALL": { "2": "chargeFireball" //
 * 触发事件，第2帧：开始蓄力 }, "FIREBALL": { "3": "spawnFireball", // 第3帧：生成火球 "6":
 * "explodeFireball" // 第6帧：火球爆炸 },
 * 
 * // 冰冻术关键帧事件 "CASTING_ICE": { "2": "chargeIce" // 第2帧：开始蓄力 }, "ICE": { "3":
 * "spawnIce", // 第3帧：生成冰锥 "6": "freezeArea" // 第6帧：范围冻结 },
 * 
 * // 闪电术关键帧事件 "CASTING_LIGHTNING": { "2": "chargeLightning" // 第2帧：开始蓄力 },
 * "LIGHTNING": { "3": "spawnLightning", // 第3帧：生成闪电 "5": "shockArea" //
 * 第5帧：范围电击 } },
 * 
 * "eventActions": { //自定义事件触发 // 战斗相关事件 "attackHit": { "type": "combat",
 * "method": "applyDamage" }, // 播放攻击音效 "attackSound": { "type": "sound",
 * "method": "playSound", "args": ["attack"] },
 * 
 * // 火球术事件映射 "chargeFireball": { "type": "skill", "method": "startCharge",
 * "args": ["火球术"] }, "spawnFireball": { "type": "skill", "method":
 * "spawnProjectile", "args": ["火球"] }, "explodeFireball": { "type": "skill",
 * "method": "explode", "args": ["火球爆炸"] },
 * 
 * // 冰冻术事件映射 "chargeIce": { "type": "skill", "method": "startCharge", "args":
 * ["冰冻术"] }, "spawnIce": { "type": "skill", "method": "spawnProjectile",
 * "args": ["冰锥"] }, "freezeArea": { "type": "skill", "method": "explode",
 * "args": ["冰冻范围"] },
 * 
 * // 闪电术事件映射 "chargeLightning": { "type": "skill", "method": "startCharge",
 * "args": ["闪电术"] }, "spawnLightning": { "type": "skill", "method":
 * "spawnProjectile", "args": ["闪电"] }, "shockArea": { "type": "skill",
 * "method": "explode", "args": ["雷击范围"] } },
 * 
 * "stateMachine": { //状态机设置，用以通过字符串条件选择方式，跳转动画事件 "layer1": [ { "param":
 * "isCastingFireball", "value": true, "target": "CASTING_FIREBALL" }, {
 * "param": "isCastingIce", "value": true, "target": "CASTING_ICE" }, { "param":
 * "default", "target": "IDLE" } ] } }
 * 
 */
public class AnimationLoader {

	public static String toString(ObjectState state) {
		switch (state) {
		case IDLE:
			return "IDLE";
		case MOVING:
			return "MOVING";
		case ATTACKING:
			return "ATTACKING";
		case CASTING:
			return "CASTING";
		case CASTING_SKILL:
			return "CASTING_SKILL";
		case DEAD:
			return "DEAD";
		case FLEEING:
			return "FLEEING";
		case PANICKED:
			return "PANICKED";
		case FAKE_RETREAT:
			return "FAKE_RETREAT";
		case CONFUSED:
			return "CONFUSED";
		case STUNNED:
			return "STUNNED";
		case FROZEN:
			return "FROZEN";
		case BURNING:
			return "BURNING";
		case POISONED:
			return "POISONED";
		case SILENCED:
			return "SILENCED";
		case FATIGUED:
			return "FATIGUED";
		case RESTING:
			return "RESTING";
		case HIDING:
			return "HIDING";
		case DEFENDING:
			return "DEFENDING";
		case WAITING:
			return "WAITING";
		case PATROLLING:
			return "PATROLLING";
		case AUTO_ATTACK:
			return "AUTO_ATTACK";
		case GARRISONED:
			return "GARRISONED";
		case BLOCKED:
			return "BLOCKED";
		case SKILL:
			return "SKILL";
		case PREPARE_ATTACK:
			return "PREPARE_ATTACK";
		case PREPARE_SKILL:
			return "PREPARE_SKILL";
		case LEFT:
			return "LEFT";
		case RIGHT:
			return "RIGHT";
		case UP:
			return "UP";
		case DOWN:
			return "DOWN";
		default:
			return "UNKNOWN";
		}
	}

	public static ObjectState parse(String input) {
		if (input == null || input.isEmpty()) {
			return ObjectState.IDLE;
		}
		String normalized = input.trim().toUpperCase();
		switch (normalized) {
		case "IDLE":
			return ObjectState.IDLE;
		case "MOVING":
			return ObjectState.MOVING;
		case "ATTACKING":
			return ObjectState.ATTACKING;
		case "CASTING":
			return ObjectState.CASTING;
		case "CASTING_SKILL":
			return ObjectState.CASTING_SKILL;
		case "DEAD":
			return ObjectState.DEAD;
		case "FLEEING":
			return ObjectState.FLEEING;
		case "PANICKED":
			return ObjectState.PANICKED;
		case "FAKE_RETREAT":
			return ObjectState.FAKE_RETREAT;
		case "CONFUSED":
			return ObjectState.CONFUSED;
		case "STUNNED":
			return ObjectState.STUNNED;
		case "FROZEN":
			return ObjectState.FROZEN;
		case "BURNING":
			return ObjectState.BURNING;
		case "POISONED":
			return ObjectState.POISONED;
		case "SILENCED":
			return ObjectState.SILENCED;
		case "FATIGUED":
			return ObjectState.FATIGUED;
		case "RESTING":
			return ObjectState.RESTING;
		case "HIDING":
			return ObjectState.HIDING;
		case "DEFENDING":
			return ObjectState.DEFENDING;
		case "WAITING":
			return ObjectState.WAITING;
		case "PATROLLING":
			return ObjectState.PATROLLING;
		case "AUTO_ATTACK":
			return ObjectState.AUTO_ATTACK;
		case "GARRISONED":
			return ObjectState.GARRISONED;
		case "BLOCKED":
			return ObjectState.BLOCKED;
		case "SKILL":
			return ObjectState.SKILL;
		case "PREPARE_ATTACK":
			return ObjectState.PREPARE_ATTACK;
		case "PREPARE_SKILL":
			return ObjectState.PREPARE_SKILL;
		case "CAST":
			return ObjectState.CASTING;
		case "SPELL":
			return ObjectState.CASTING_SKILL;
		case "LEFT":
			return ObjectState.LEFT;
		case "RIGHT":
			return ObjectState.RIGHT;
		case "UP":
			return ObjectState.UP;
		case "DOWN":
			return ObjectState.DOWN;
		default:
			return ObjectState.IDLE;
		}
	}

	public static class StateKeySet {

		public String key;

		public ObjectState state;

		public StateKeySet(String key) {
			this.key = key;
			this.state = parse(key);
		}

		@Override
		public String toString() {
			return key;
		}

	}

	public static class StateRule {

		public String param;

		public Object value;

		public String target;

	}

	public static class TransitionConfig {
		public float blendTime;
	}

	/**
	 * 区域配置类，定义从大图中裁剪的起始坐标和行列数。
	 */
	public static class RegionConfig {
		// 起始X坐标
		public int x;

		// 起始Y坐标
		public int y;

		// 行数
		public int rows;

		// 列数
		public int cols;
	}

	/**
	 * 动画配置类
	 */
	public class AnimationConfig {
		// 基础路径，例如 "assets/anim/"，所有filePattern都会拼接在这个路径下
		public String basePath = "";

		// 文件路径模式，例如"idle_{0}.png","big_sheet.png"/
		public String filePattern;

		// 播放速度
		public float speed;

		// 是否循环播放
		public boolean looping;

		// 是否为spritesheet
		public boolean isSheet;

		// 用于从大图中裁剪指定区域，若不需要剪切的单图或者一组动作一个图拆分，则不必设置
		public RegionConfig region;

		// 帧序列：指定播放哪些帧(仅在RegionConfig被设置后生效)
		public IntArray frames;

		/**
		 * 拼合并获取完整路径
		 * 
		 * @param directionSuffix
		 * @return
		 */
		public String getFullPath(String directionSuffix) {
			if (directionSuffix != null) {
				return basePath + StringUtils.format(filePattern, directionSuffix);
			}
			return basePath + filePattern;
		}
	}

	private final ObjectMap<String, TArray<StateRule>> stateMachineMap = new ObjectMap<String, TArray<StateRule>>();
	private final ObjectMap<String, ObjectMap<String, Object>> eventActionsMap = new ObjectMap<String, ObjectMap<String, Object>>();
	private final ObjectMap<String, TransitionConfig> transitionsMap = new ObjectMap<>();
	private final ObjectMap<String, AnimationConfig> animationMap = new ObjectMap<String, AnimationConfig>();
	private final ObjectMap<ObjectState, IntMap<String>> eventsMap = new ObjectMap<ObjectState, IntMap<String>>();
	private final int frameWidth;
	private final int frameHeight;

	public AnimationLoader(Object jsonObject) {
		Json.Object root = (Json.Object) jsonObject;
		String basePath = LSystem.EMPTY;
		if (root.containsKey("basePath")) {
			basePath = root.getString("basePath");
		}
		frameWidth = root.getInt("frameWidth", 64);
		frameHeight = root.getInt("frameHeight", 64);
		Json.Object anims = root.getObject("animations");
		if (anims != null) {
			TypedArray<String> keys = anims.keys();
			if (keys != null) {
				int keySize = keys.length();
				for (int i = 0; i < keySize; i++) {
					AnimationConfig aniCfg = new AnimationConfig();
					aniCfg.basePath = basePath;
					String key = keys.get(i);
					Json.Object entry = anims.getObject(key);
					if (entry != null) {
						StateKeySet state = new StateKeySet(key);
						aniCfg.filePattern = entry.getString("filePattern");
						aniCfg.speed = entry.getNumber("speed", 0.1f);
						aniCfg.looping = entry.getBoolean("looping", true);
						aniCfg.isSheet = entry.getBoolean("isSheet", false);
						if (entry.containsKey("region")) {
							RegionConfig regionConfig = new RegionConfig();
							Json.Object region = entry.getObject("region");
							if (region != null) {
								regionConfig.x = region.getInt("x", 0);
								regionConfig.y = region.getInt("y", 0);
								regionConfig.rows = region.getInt("rows", 1);
								regionConfig.cols = region.getInt("cols", 1);
								aniCfg.region = regionConfig;
							}
						}
						if (entry.containsKey("frames")) {
							IntArray frames = new IntArray();
							Json.Array arrays = entry.getArray("frames");
							if (arrays != null) {
								for (int j = 0; j < arrays.length(); j++) {
									frames.add(arrays.getInt(j));
								}
							}
							aniCfg.frames = frames;
						}
						animationMap.put(state.toString(), aniCfg);
					}
				}
			}
			Json.Object transitions = root.getObject("transitions");
			if (transitions != null) {
				TypedArray<String> transKeys = transitions.keys();
				if (transKeys != null) {
					for (int l = 0; l < transKeys.length(); l++) {
						String key = transKeys.get(l);
						TransitionConfig cfg = new TransitionConfig();
						Json.Object transitionObj = transitions.getObject(key);
						if (transitionObj != null) {
							cfg.blendTime = transitionObj.getNumber("blendTime", 0.1f);
						}
						transitionsMap.put(key, cfg);
					}
				}
			}
			// 动作事件具体桢
			Json.Object events = root.getObject("events");
			if (events != null) {
				TypedArray<String> eveKeys = events.keys();
				if (eveKeys != null) {
					for (int n = 0; n < eveKeys.length(); n++) {
						String key = eveKeys.get(n);
						Json.Object stateEvents = events.getObject(key);
						if (stateEvents != null) {
							ObjectState state = parse(eveKeys.get(n));
							IntMap<String> map = new IntMap<String>();
							TypedArray<String> stateKeys = stateEvents.keys();
							if (stateKeys != null) {
								for (int m = 0; m < stateKeys.length(); m++) {
									String statekey = stateKeys.get(m);
									map.put(HelperUtils.toInt(statekey), stateEvents.getString(statekey));
								}
							}
							eventsMap.put(state, map);
						}
					}
				}
			}
			// 具体动作调用的函数分配
			Json.Object eventActions = root.getObject("eventActions");
			if (eventActions != null) {
				TypedArray<String> actKeys = eventActions.keys();
				if (actKeys != null) {
					for (int k = 0; k < actKeys.length(); k++) {
						String key = actKeys.get(k);
						Json.Object actObject = eventActions.getObject(key);
						ObjectMap<String, Object> map = new ObjectMap<String, Object>();
						map.put("type", actObject.getString("type"));
						map.put("method", actObject.getString("method"));
						if (actObject.containsKey("args")) {
							TArray<String> args = new TArray<String>();
							Json.Array arrays = actObject.getArray("args");
							for (int i = 0; i < arrays.length(); i++) {
								args.add(arrays.getString(i));
							}
							map.put("args", args.toArray());
						}
						eventActionsMap.put(key, map);
					}
				}
			}

			// 解析状态机设置 ，用于通过条件方式，触发指定动画
			Json.Object stateMachine = root.getObject("stateMachine");
			if (stateMachine != null) {
				TypedArray<String> smKeys = stateMachine.keys();
				for (int p = 0; p < smKeys.length(); p++) {
					String layerName = smKeys.get(p);
					Json.Array layerRules = stateMachine.getArray(layerName);
					if (layerRules != null) {
						TArray<StateRule> rules = new TArray<StateRule>();
						for (int s = 0; s < layerRules.length(); s++) {
							Json.Object ruleObject = layerRules.getObject(s);
							if (ruleObject != null) {
								StateRule rule = new StateRule();
								rule.param = ruleObject.getString("param");
								rule.target = ruleObject.getString("target");
								if (ruleObject.containsKey("value")) {
									rule.value = HelperUtils.toStr(ruleObject.getObject("value"));
								}
								rules.add(rule);
							}
							stateMachineMap.put(layerName, rules);
						}
					}
				}
			}
		}
	}

	/**
	 * 加载某个状态和方向的动画
	 * 
	 * @param state
	 * @param dir
	 * @return
	 */
	public Animation loadAnimation(String key, Direction dir, String textureSuffix) {
		AnimationConfig cfg = animationMap.get(key);
		if (cfg == null) {
			return null;
		}
		final long timer = MathUtils.max(1, (long) (cfg.speed * LSystem.SECOND));
		String path = cfg.getFullPath(StringUtils.isEmpty(textureSuffix) ? LSystem.EMPTY : textureSuffix);
		Animation anim = null;
		if (cfg.region != null) {
			// 拆分图片
			LTexture[] textureList = TextureUtils.getSplitTextures(path, cfg.region.x, cfg.region.y, frameWidth,
					frameHeight);
			Animation animation = new Animation();
			for (int i = 0; i < textureList.length; i++) {
				if (cfg.frames == null || cfg.frames.contains(i)) {
					LTexture tex = textureList[i];
					if (tex != null) {
						animation.addFrame(tex, timer);
					}
				}
			}
			anim = animation;
		} else if (cfg.isSheet) {
			// 读取单独的序列spritesheet（一般都是用单一方向动作图，比如纯向左，右，上，下之类的任何一个相仿，总之必须单一体系的。简单说就是魂类游戏的单独大招序列图。
			// 需要跳帧的，什么样子都有用到的，参考示例用region设定）
			anim = Animation.getDefaultAnimation(path, frameWidth, frameHeight, timer);
		} else {
			// 单图模式
			anim = Animation.getDefaultAnimation(path);
		}
		// 循环次数
		anim.setLoopCount(cfg.looping ? -1 : 1);
		return anim;
	}

	public IntMap<String> getEvents(ObjectState state) {
		return eventsMap.get(state);
	}

	public TransitionConfig getTransition(String key) {
		return transitionsMap.get(key);
	}

	public ObjectMap<String, Object> getEventAction(String actionName) {
		return eventActionsMap.get(actionName);
	}

	/**
	 * 获取所有事件动作映射
	 */
	public ObjectMap<String, ObjectMap<String, Object>> getEventActions() {
		return eventActionsMap;
	}

	public TArray<StateRule> getStateMachineRules(String layerName) {
		return stateMachineMap.get(layerName);
	}
}
