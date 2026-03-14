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
import loon.utils.SortedList;

/**
 * 动画连续攻击动作管理器，抽象类，主要是配合AnimationManager生成魂类游戏连招用的
 */
public abstract class AnimationComboManager implements AnimationEventListener {

	private final AnimationManager animManager;
	private final int layerIndex;
	private final SortedList<String> comboQueue = new SortedList<String>();

	public AnimationComboManager(AnimationManager animManager, int layerIndex) {
		this.animManager = animManager;
		this.layerIndex = layerIndex;
	}

	public void addSkillToCombo(String skillParam) {
		comboQueue.add(skillParam);
	}

	public void startCombo() {
		if (!comboQueue.isEmpty()) {
			String firstSkill = comboQueue.poll();
			animManager.setParameter(firstSkill, true);
			animManager.evaluateStateMachine(layerIndex);
		}
	}

	public abstract void onTransition(ObjectState from, ObjectState to);

	public abstract void onStateExit(ObjectState state, Direction dir);

	public abstract void onStateEnter(ObjectState state, Direction dir);

	public abstract void onKeyFrame(ObjectState state, Direction dir, int frame, String eventType);

	@Override
	public void onAnimationComplete(ObjectState state, Direction dir) {
		if (!comboQueue.isEmpty()) {
			String nextSkill = comboQueue.poll();
			animManager.setParameter(nextSkill, true);
			animManager.evaluateStateMachine(layerIndex);
		} else {
			animManager.setParameter("default", true);
			animManager.evaluateStateMachine(layerIndex);
		}
	}
}
