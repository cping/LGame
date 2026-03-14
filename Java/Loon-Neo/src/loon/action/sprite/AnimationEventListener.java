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
import loon.action.map.battle.BattleType.ObjectState;

/**
 * 动画事件监听接口，用于接收动画播放过程中的各种事件回调(并非所有游戏类型都必须开动画按帧监听，基本上上这个就证明动画效果很复杂，为魂类游戏准备的)
 */
public interface AnimationEventListener {

	void onStateEnter(ObjectState state, Direction dir);

	void onStateExit(ObjectState state, Direction dir);

	void onAnimationComplete(ObjectState state, Direction dir);

	void onInterrupted(ObjectState from, ObjectState to);

	void onTransition(ObjectState from, ObjectState to);

	void onKeyFrame(ObjectState state, Direction dir, int frameIndex, String eventType);

}