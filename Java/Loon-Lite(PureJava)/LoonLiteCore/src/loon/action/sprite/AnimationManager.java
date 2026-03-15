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
import loon.utils.SortedList;
import loon.utils.TArray;

/**
 * 动画管理器，负责管理多个动画层，处理状态机、过渡、事件触发和最终帧选择(非复杂纹理动画播放，没有使用必要，这个是管理配置复杂动画的json用的，更准确地说是：就是魂类游戏专属)
 */
public class AnimationManager implements LRelease {

	private final static String MOVE_FLAG = "->";

	private static class QueuedAnimation {

		int layerIndex;
		ObjectState state;
		Direction dir;
		AnimationLoader.TransitionConfig transition;

		QueuedAnimation(int layerIndex, ObjectState state, Direction dir) {
			this(layerIndex, state, dir, null);
		}

		QueuedAnimation(int layerIndex, ObjectState state, Direction dir, AnimationLoader.TransitionConfig transition) {
			this.layerIndex = layerIndex;
			this.state = state;
			this.dir = dir;
			this.transition = transition;
		}

		public void execute(AnimationManager manager) {
			if (layerIndex < 0 || layerIndex >= manager.layers.size) {
				return;
			}
			AnimationLayer layer = manager.layers.get(layerIndex);
			if (manager.eventListener != null) {
				manager.eventListener.onTransition(layer.currentState, state);
				manager.eventListener.onStateExit(layer.currentState, layer.currentDirection);
			}
			layer.setState(state, dir, transition);
			if (manager.eventListener != null) {
				manager.eventListener.onStateEnter(state, dir);
			}
		}
	}

	private static class CrossFadeState {
		int layerIndex;
		ObjectState sourceState;
		ObjectState targetState;
		Direction dir;
		float duration;
		float elapsed;
		float weight;

		CrossFadeState(int layerIndex, ObjectState sourceState, ObjectState targetState, Direction dir,
				float duration) {
			this.layerIndex = layerIndex;
			this.sourceState = sourceState;
			this.targetState = targetState;
			this.dir = dir;
			this.duration = duration;
			this.elapsed = 0f;
			this.weight = 0f;
		}
	}

	private final ObjectMap<String, Object> parameters = new ObjectMap<String, Object>();

	private final TArray<AnimationLayer> layers = new TArray<AnimationLayer>();

	// 播放队列
	private final SortedList<QueuedAnimation> playQueue = new SortedList<QueuedAnimation>();

	private final AnimationLoader loader;

	private AnimationEventListener eventListener;

	private final AnimationEventExecutor eventExecutor;

	private final BattleMapObject owner;

	private CrossFadeState crossFadeState;

	private int currentLayerIndex = 0;

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
	}

	public AnimationManager addLayer(Direction dir, String key) {
		return addLayer(LSystem.EMPTY, dir, key);
	}

	public AnimationManager addLayer(String textureSuffix, Direction dir, String key) {
		AnimationLayer layer = createLayer(textureSuffix, dir, key);
		layers.add(layer);
		return this;
	}

	public AnimationManager addLayer(Direction dir, ObjectState state) {
		return addLayer(LSystem.EMPTY, dir, state);
	}

	public AnimationManager addLayer(String textureSuffix, Direction dir, ObjectState state) {
		AnimationLayer layer = createLayer(textureSuffix, dir, state);
		layers.add(layer);
		return this;
	}

	public int findLayerIndex(String key) {
		int idx = 0;
		for (AnimationLayer layer : layers) {
			if (layer != null && key.equalsIgnoreCase(layer.currentStateKey)) {
				return idx;
			}
			idx++;
		}
		return -1;
	}

	public int findLayerIndex(ObjectState state) {
		int idx = 0;
		for (AnimationLayer layer : layers) {
			if (layer != null && state.equals(layer.currentState)) {
				return idx;
			}
			idx++;
		}
		return -1;
	}

	/**
	 * 每个AnimationLayer其实可以设置8个不同方向的动画，创建为省事默认值只能填一个，多了自己构建AnimationLayer再addLayer……
	 * 
	 * @param textureSuffix
	 * @param dir
	 * @param key
	 * @return
	 */
	public AnimationLayer createLayer(String textureSuffix, Direction dir, String key) {
		ObjectMap<String, ObjectMap<Direction, Animation>> anims = new ObjectMap<String, ObjectMap<Direction, Animation>>();
		ObjectMap<Direction, Animation> stateAnims = new ObjectMap<Direction, Animation>();
		Animation anim = loader.loadAnimation(key, dir, textureSuffix);
		if (anim != null) {
			stateAnims.put(dir, anim);
		}
		if (!stateAnims.isEmpty()) {
			anims.put(key, stateAnims);
		}
		return new AnimationLayer(anims, loader, key);
	}

	public AnimationLayer createLayer(String textureSuffix, Direction dir, ObjectState state) {
		ObjectMap<String, ObjectMap<Direction, Animation>> anims = new ObjectMap<String, ObjectMap<Direction, Animation>>();
		String stateKey = AnimationLoader.toString(state);
		ObjectMap<Direction, Animation> stateAnims = new ObjectMap<Direction, Animation>();
		Animation anim = loader.loadAnimation(stateKey, dir, textureSuffix);
		if (anim != null) {
			stateAnims.put(dir, anim);
		}
		if (!stateAnims.isEmpty()) {
			anims.put(stateKey, stateAnims);
		}
		return new AnimationLayer(anims, loader, state);
	}

	public AnimationManager play(int layerIndex, String state, Direction dir) {
		if (layerIndex < 0 || layerIndex >= layers.size) {
			return this;
		}
		AnimationLayer layer = layers.get(layerIndex);
		AnimationLoader.TransitionConfig trans = loader.getTransition(layer.currentStateKey + MOVE_FLAG + state);
		if (eventListener != null) {
			eventListener.onTransition(layer.currentState, AnimationLoader.parse(state));
			eventListener.onStateExit(layer.currentState, layer.currentDirection);
		}
		layer.setState(state, dir, trans);
		if (eventListener != null) {
			eventListener.onStateEnter(AnimationLoader.parse(state), dir);
		}
		return this;
	}

	public AnimationManager play(int layerIndex, ObjectState state, Direction dir) {
		if (layerIndex < 0 || layerIndex >= layers.size) {
			return this;
		}
		AnimationLayer layer = layers.get(layerIndex);
		AnimationLoader.TransitionConfig trans = loader.getTransition(
				AnimationLoader.toString(layer.currentState) + MOVE_FLAG + AnimationLoader.toString(state));
		if (eventListener != null) {
			eventListener.onTransition(layer.currentState, state);
			eventListener.onStateExit(layer.currentState, layer.currentDirection);
		}
		layer.setState(state, dir, trans);
		if (eventListener != null) {
			eventListener.onStateEnter(state, dir);
		}
		return this;
	}

	public void playQueue(int layerIndex, ObjectState transitionState, ObjectState targetState, Direction dir) {
		play(layerIndex, transitionState, dir);
		playQueue.add(new QueuedAnimation(layerIndex, targetState, dir));
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
		String key = AnimationLoader.toString(layer.currentState) + MOVE_FLAG + AnimationLoader.toString(newState);
		AnimationLoader.TransitionConfig trans = loader.getTransition(key);
		if (eventListener != null) {
			eventListener.onTransition(layer.currentState, newState);
			eventListener.onStateExit(layer.currentState, layer.currentDirection);
		}
		layer.setState(newState, newDir, trans);
		if (eventListener != null) {
			eventListener.onStateEnter(newState, newDir);
		}
		currentLayerIndex = layerIndex;
	}

	public void setState(int layerIndex, String newState, Direction newDir) {
		if (layerIndex < 0 || layerIndex >= layers.size) {
			return;
		}
		AnimationLayer layer = layers.get(layerIndex);
		// 特殊定义，用字符A->B这样自动跳动画
		String key = layer.currentStateKey + MOVE_FLAG + newState;
		AnimationLoader.TransitionConfig trans = loader.getTransition(key);
		if (eventListener != null) {
			eventListener.onTransition(layer.currentState, AnimationLoader.parse(newState));
			eventListener.onStateExit(layer.currentState, layer.currentDirection);
		}
		layer.setState(newState, newDir, trans);
		if (eventListener != null) {
			eventListener.onStateEnter(AnimationLoader.parse(newState), newDir);
		}
		currentLayerIndex = layerIndex;
	}

	public void setState(String newState, Direction newDir) {
		setState(findLayerIndex(newState), newState, newDir);
	}

	public void setState(ObjectState newState, Direction newDir) {
		setState(findLayerIndex(newState), newState, newDir);
	}

	public void crossFade(int layerIndex, ObjectState targetState, Direction dir, float duration) {
		if (layerIndex < 0 || layerIndex >= layers.size)
			return;
		AnimationLayer layer = layers.get(layerIndex);
		crossFadeState = new CrossFadeState(layerIndex, layer.currentState, targetState, dir, duration);
		if (eventListener != null) {
			eventListener.onTransition(layer.currentState, targetState);
			eventListener.onStateExit(layer.currentState, layer.currentDirection);
		}
		currentLayerIndex = layerIndex;
	}

	public LTexture getCurrentFrame(int idx, String textureSuffix) {
		if (crossFadeState != null) {
			AnimationLayer layer = layers.get(crossFadeState.layerIndex);
			Animation sourceAnim = loader.loadAnimation(AnimationLoader.toString(crossFadeState.sourceState),
					layer.currentDirection, textureSuffix);
			Animation targetAnim = loader.loadAnimation(AnimationLoader.toString(crossFadeState.targetState),
					crossFadeState.dir, textureSuffix);
			LTexture sourceFrame = sourceAnim.getSpriteImage(idx);
			LTexture targetFrame = targetAnim.getSpriteImage(idx);
			float weight = crossFadeState.weight;
			return weight < 0.5f ? sourceFrame : targetFrame;
		}
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
	 * 更新所有层动画
	 * 
	 * @param deltaTime
	 */
	public void update(float deltaTime) {
		for (AnimationLayer layer : layers) {
			layer.update(deltaTime, eventListener, eventExecutor, owner);
			if (layer.isFinished() && !playQueue.isEmpty()) {
				QueuedAnimation next = playQueue.poll();
				next.execute(this);
			}
		}
		if (crossFadeState != null) {
			crossFadeState.elapsed += deltaTime;
			float t = crossFadeState.elapsed / crossFadeState.duration;
			if (t >= 1f) {
				setState(crossFadeState.layerIndex, crossFadeState.targetState, crossFadeState.dir);
				if (eventListener != null) {
					eventListener.onStateEnter(crossFadeState.targetState, crossFadeState.dir);
				}
				crossFadeState = null;
			} else {
				crossFadeState.weight = t;
			}
		}
	}

	public void start() {
		for (AnimationLayer layer : layers) {
			layer.start();
		}
	}

	public void stop() {
		for (AnimationLayer layer : layers) {
			layer.stop();
		}
	}

	public void resume() {
		for (AnimationLayer layer : layers) {
			layer.resume();
		}
	}

	public void reset() {
		for (AnimationLayer layer : layers) {
			layer.reset();
		}
	}

	public void pause() {
		for (AnimationLayer layer : layers) {
			layer.pause();
		}
	}

	/**
	 * 获取最终帧 (若不设置，按权重选择最高层)
	 * 
	 * @return
	 */
	public LTexture getTopLayerFrame() {
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

	public LTexture getCurrentFrame() {
		AnimationLayer layer = layers.get(currentLayerIndex);
		return layer != null ? layer.getCurrentFrame() : getTopLayerFrame();
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
			for (ObjectMap<Direction, Animation> stateAnims : layer.animationObjects.values()) {
				for (Animation anim : stateAnims.values()) {
					anim.close();
				}
			}
		}
		layers.clear();
		parameters.clear();
	}

}
