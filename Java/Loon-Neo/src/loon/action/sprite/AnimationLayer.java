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

import loon.LTexture;
import loon.action.map.Direction;
import loon.action.map.battle.BattleMapObject;
import loon.action.map.battle.BattleType.ObjectState;
import loon.utils.IntMap;
import loon.utils.ObjectMap;

/**
 * 动画层，负责播放动画、处理过渡、触发关键帧事件。
 */
public class AnimationLayer {

	final ObjectMap<ObjectState, ObjectMap<Direction, Animation>> animations = new ObjectMap<ObjectState, ObjectMap<Direction, Animation>>();

	// 当前状态和方向
	ObjectState currentState = ObjectState.IDLE;
	Direction currentDirection = Direction.DOWN;

	// 层权重，用于混合选择
	private float weight = 1.0f;

	// 过渡动画
	private Animation blendingFrom;
	private Animation blendingTo;
	private float blendTimer = 0f;

	// 动画加载器，用于获取事件配置
	private final AnimationLoader loader;

	public AnimationLayer(ObjectMap<ObjectState, ObjectMap<Direction, Animation>> animations, AnimationLoader loader) {
		this.animations.putAll(animations);
		this.loader = loader;
	}

	/**
	 * 设置状态并处理过渡
	 * 
	 * @param newState
	 * @param newDir
	 * @param trans
	 */
	public void setState(ObjectState newState, Direction newDir, AnimationLoader.TransitionConfig trans) {
		if (newState == currentState && newDir == currentDirection) {
			return;
		}
		if (trans != null) {
			blendingFrom = getAnimation(currentState, currentDirection);
			blendingTo = getAnimation(newState, newDir);
			blendTimer = trans.blendTime;
		}
		currentState = newState != null ? newState : ObjectState.IDLE;
		currentDirection = newDir != null ? newDir : Direction.DOWN;
	}

	/**
	 * 更新动画播放，触发关键帧事件
	 * 
	 * @param deltaTime
	 * @param speedFactor
	 * @param listener
	 * @param executor
	 * @param character
	 */
	public void update(float deltaTime, AnimationEventListener listener, AnimationEventExecutor executor,
			BattleMapObject character) {
		Animation anim = getAnimation(currentState, currentDirection);
		if (anim != null) {
			int prevFrame = anim.getCurrentFrameIndex();
			anim.update(deltaTime);
			int newFrame = anim.getCurrentFrameIndex();

			// 检查关键帧事件
			IntMap<String> events = loader.getEvents(currentState);
			if (events != null) {
				for (int frame = prevFrame + 1; frame <= newFrame; frame++) {
					if (events.containsKey(frame)) {
						String eventType = events.get(frame);
						// 回调监听器
						if (listener != null) {
							listener.onKeyFrame(currentState, currentDirection, frame, eventType);
						}
						// 配置驱动执行器
						if (executor != null) {
							executor.execute(eventType, character, currentDirection);
						}
					}
				}
			}

			// 动画事件完毕
			if (anim.isFinished()) {
				if (listener != null) {
					listener.onAnimationComplete(currentState, currentDirection);
				}
			}
		}

		// 处理过渡混合
		if (blendTimer > 0 && blendingFrom != null && blendingTo != null) {
			blendTimer -= deltaTime;
			if (blendTimer <= 0) {
				blendingFrom = null;
				blendingTo = null;
			}
		}
	}

	/**
	 * 获取当前帧
	 */
	public LTexture getCurrentFrame() {
		if (blendTimer > 0 && blendingFrom != null && blendingTo != null) {
			return blendingTo.getSpriteImage();
		}
		Animation anim = getAnimation(currentState, currentDirection);
		return anim != null ? anim.getSpriteImage() : getFallbackFrame();
	}

	/**
	 * 获取指定状态和方向的动画
	 * 
	 * @param state
	 * @param direction
	 * @return
	 */
	private Animation getAnimation(ObjectState state, Direction direction) {
		ObjectMap<Direction, Animation> stateAnims = animations.get(state);
		if (stateAnims != null) {
			Animation anim = stateAnims.get(direction);
			if (anim != null)
				return anim;
		}
		// 回退到默认待机动画
		ObjectMap<Direction, Animation> idleAnims = animations.get(ObjectState.IDLE);
		return idleAnims != null ? idleAnims.get(Direction.DOWN) : null;
	}

	/**
	 * 获取回退帧
	 * 
	 * @return
	 */
	private LTexture getFallbackFrame() {
		ObjectMap<Direction, Animation> idleAnims = animations.get(ObjectState.IDLE);
		if (idleAnims != null) {
			Animation idleAnim = idleAnims.get(Direction.DOWN);
			if (idleAnim != null) {
				return idleAnim.getSpriteImage();
			}
		}
		return null;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}
}
