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

public class LNFadeIn extends LNAction {

	protected float _diff;

	protected float _orgOpacity;

	protected float _tarOpacity;
	
	LNFadeIn(){
		
	}

	public static LNFadeIn Action(float duration) {
		LNFadeIn ins = new LNFadeIn();
		ins._tarOpacity = 255f;
		ins._duration = duration;
		return ins;
	}

	@Override
	public void setTarget(LNNode node) {
		super._firstTick = true;
		super._isEnd = false;
		super._target = node;
		this._orgOpacity = node._alpha * 255f;
		this._diff = this._tarOpacity - this._orgOpacity;
	}

	@Override
	public void update(float t) {
		if (t == 1f) {
			super._isEnd = true;
			super._target.setAlpha(1f);
		} else {
			super._target
					.setAlpha(((t * this._diff) + this._orgOpacity) / 255f);
		}
	}

	@Override
	public LNAction copy() {
		return Action(_duration);
	}

	public LNFadeOut reverse() {
		return LNFadeOut.Action(_duration);
	}
}
