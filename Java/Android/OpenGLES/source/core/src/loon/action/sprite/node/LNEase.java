/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.action.sprite.node;

public class LNEase extends LNAction {

	protected LNAction _action;

	LNEase() {

	}

	/**
	 * 由于LNEasing类已经实现了具体的Ease算法，因此EaseAction并没有存在多个的必要， 替换不同LNEasing即可实现不同效果。
	 * 
	 * @param act
	 * @param e
	 * @return
	 */
	public static LNEase Action(Easing e, LNAction act) {
		LNEase action = new LNEase();
		action._duration = act._duration;
		action._action = act;
		act._easing = e;
		return action;
	}

	@Override
	public void setTarget(LNNode node) {
		super.setTarget(node);
		if (_action != null) {
			_action.setTarget(node);
		}
	}

	@Override
	public void step(float dt) {
		if (_action != null) {
			_action.step(dt);
			_isEnd = _action.isEnd();
		}
	}

	@Override
	public LNAction copy() {
		return Action(_easing, _action);
	}

}
