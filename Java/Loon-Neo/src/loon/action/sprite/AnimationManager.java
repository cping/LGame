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

import loon.BaseIO;
import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.action.map.Direction;
import loon.action.map.battle.BattleMapObject;
import loon.action.map.battle.BattleType.ObjectState;
import loon.action.sprite.AnimationEventExecutor.ICombatService;
import loon.action.sprite.AnimationEventExecutor.IOtherService;
import loon.action.sprite.AnimationEventExecutor.ISkillService;
import loon.action.sprite.AnimationEventExecutor.ISoundService;
import loon.action.sprite.AnimationLoader.StateRule;
import loon.utils.HelperUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;

/**
 * 动画管理器，负责管理多个动画层，处理状态机、过渡、事件触发和最终帧选择(非复杂纹理动画播放，没有使用必要，这个是管理配置复杂动画的json用的，更准确地说是：就是魂类游戏专属)
 */
public class AnimationManager implements LRelease {

	private final ObjectMap<String, Object> parameters = new ObjectMap<String, Object>();

	private final TArray<AnimationLayer> layers = new TArray<AnimationLayer>();

	private final AnimationLoader loader;

	private AnimationEventListener eventListener;

	private final AnimationEventExecutor eventExecutor;

	private final BattleMapObject owner;

	public AnimationManager(String configPath, BattleMapObject owner, ISoundService soundService,
			ICombatService combatService, ISkillService skillService, IOtherService otherService) {
		this(configPath, LSystem.EMPTY, owner, soundService, combatService, skillService, otherService);
	}

	public AnimationManager(String configPath, String textureSuffix, BattleMapObject owner, ISoundService soundService,
			ICombatService combatService, ISkillService skillService, IOtherService otherService) {
		this.loader = new AnimationLoader(BaseIO.loadJsonObject(configPath));
		this.owner = owner;
		this.eventExecutor = new AnimationEventExecutor(soundService, combatService, skillService, otherService,
				loader.getEventActions());
		initDefaultLayers(textureSuffix);
	}

	/**
	 * 默认参数分组设置，可自行添加修改
	 * 
	 * @param textureSuffix
	 */
	private void initDefaultLayers(String textureSuffix) {
		layers.add(createLayer(textureSuffix, ObjectState.IDLE, ObjectState.MOVING, ObjectState.RESTING,
				ObjectState.WAITING));
		layers.add(createLayer(textureSuffix, ObjectState.ATTACKING, ObjectState.SKILL, ObjectState.CASTING,
				ObjectState.PREPARE_ATTACK, ObjectState.PREPARE_SKILL));
		layers.add(createLayer(textureSuffix, ObjectState.BURNING, ObjectState.POISONED, ObjectState.FROZEN,
				ObjectState.FATIGUED));
		layers.add(createLayer(textureSuffix, ObjectState.STUNNED, ObjectState.PANICKED, ObjectState.DEAD,
				ObjectState.CONFUSED));
	}

	private AnimationLayer createLayer(String textureSuffix, ObjectState... states) {
		ObjectMap<ObjectState, ObjectMap<Direction, Animation>> anims = new ObjectMap<ObjectState, ObjectMap<Direction, Animation>>();
		for (ObjectState state : states) {
			ObjectMap<Direction, Animation> stateAnims = new ObjectMap<>();
			TArray<Direction> dirs = Direction.values();
			for (Direction dir : dirs) {
				if (dir != Direction.NONE) {
					Animation anim = loader.loadAnimation(state, dir, textureSuffix);
					if (anim != null) {
						stateAnims.put(dir, anim);
					}
				}
			}
			if (!stateAnims.isEmpty()) {
				anims.put(state, stateAnims);
			}
		}
		return new AnimationLayer(anims, loader);
	}

	public void setParameter(String key, Object value) {
		parameters.put(key, value);
	}

	public Object getParameter(String key) {
		return parameters.get(key);
	}

	public void evaluateStateMachine(int layerIndex) {
		if (layerIndex < 0 || layerIndex >= layers.size) {
			return;
		}
		AnimationLayer layer = layers.get(layerIndex);
		TArray<StateRule> rules = loader.getStateMachineRules("layer" + layerIndex);
		if (rules == null) {
			return;
		}
		for (StateRule rule : rules) {
			Object paramValue = parameters.get(rule.param);
			if (rule.param.equals("default") || HelperUtils.areEqual(paramValue, rule.value)) {
				ObjectState targetState = AnimationLoader.parse(rule.target);
				if (layer.currentState != targetState) {
					setState(layerIndex, targetState, owner.getDirection());
				}
				break;
			}
		}
	}

	public void setEventListener(AnimationEventListener listener) {
		this.eventListener = listener;
	}

	/**
	 * 设置某层的状态
	 * 
	 * @param layerIndex
	 * @param newState
	 * @param newDir
	 */
	public void setState(int layerIndex, ObjectState newState, Direction newDir) {
		if (layerIndex < 0 || layerIndex >= layers.size) {
			return;
		}
		AnimationLayer layer = layers.get(layerIndex);
		// 特殊定义，用字符A->B这样自动跳动画
		String key = layer.currentState.name() + "->" + newState.name();
		AnimationLoader.TransitionConfig trans = loader.getTransition(key);
		if (eventListener != null) {
			eventListener.onTransition(layer.currentState, newState);
			eventListener.onStateExit(layer.currentState, layer.currentDirection);
		}
		layer.setState(newState, newDir, trans);
		if (eventListener != null) {
			eventListener.onStateEnter(newState, newDir);
		}
	}

	/**
	 * 更新所有层动画
	 * 
	 * @param deltaTime
	 */
	public void update(float deltaTime) {
		for (AnimationLayer layer : layers) {
			layer.update(deltaTime, eventListener, eventExecutor, owner);
		}
	}

	/**
	 * 获取最终帧 (按权重选择最高层)
	 * 
	 * @return
	 */
	public LTexture getCurrentFrame() {
		AnimationLayer topLayer = null;
		float maxWeight = -1f;
		for (AnimationLayer layer : layers) {
			if (layer.getWeight() > maxWeight) {
				maxWeight = layer.getWeight();
				topLayer = layer;
			}
		}
		return topLayer != null ? topLayer.getCurrentFrame() : null;
	}

	/**
	 * 添加动态层管理
	 * 
	 * @param layer
	 */
	public void addLayer(AnimationLayer layer) {
		layers.add(layer);
	}

	/**
	 * 删除动态管理层
	 * 
	 * @param layer
	 */
	public void removeLayer(AnimationLayer layer) {
		layers.removeValue(layer, true);
	}

	/**
	 * 清空动画层
	 * 
	 * @return
	 */
	public AnimationManager clearLayers() {
		layers.clear();
		return this;
	}

	public AnimationLayer getLayer(int index) {
		if (index < 0 || index >= layers.size) {
			return null;
		}
		return layers.get(index);
	}

	public AnimationLoader getLoader() {
		return loader;
	}

	public void debugPrintStates() {
		for (int i = 0; i < layers.size; i++) {
			AnimationLayer layer = layers.get(i);
			LSystem.debug("Layer " + i + ": " + layer.currentState + " (" + layer.currentDirection + ")");
		}
	}

	@Override
	public void close() {
		for (AnimationLayer layer : layers) {
			for (ObjectMap<Direction, Animation> stateAnims : layer.animations.values()) {
				for (Animation anim : stateAnims.values()) {
					anim.close();
				}
			}
		}
		layers.clear();
		parameters.clear();
	}

}
