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

public class LNAnimate extends LNAction {

	public LNAnimation _ans;

	public String _animName;

	public boolean _restoreOriginalFrame;
	
	LNAnimate(){
		
	}

	public static LNAnimate Action(LNAnimation anim) {
		LNAnimate animate = new LNAnimate();
		animate._ans = anim;
		animate._duration = anim.getDuration();
		animate._animName = anim.getName();
		animate._restoreOriginalFrame = true;
		return animate;
	}

	public static LNAnimate Action(LNAnimation anim,
			boolean restoreOriginalFrame) {
		LNAnimate animate = new LNAnimate();
		animate._ans = anim;
		animate._duration = anim.getDuration();
		animate._animName = anim.getName();
		animate._restoreOriginalFrame = restoreOriginalFrame;
		return animate;
	}

	@Override
	public void setTarget(LNNode node) {
		super._firstTick = true;
		super._isEnd = false;
		super._target = node;
		if (super._target instanceof LNSprite) {
			((LNSprite) super._target).setAnimation(_ans);
		}
	}

	@Override
	public void update(float t) {
		if (super._target instanceof LNSprite) {
			if (t == 1f) {
				super._isEnd = true;
				if (this._restoreOriginalFrame) {
					((LNSprite) super._target).setFrame(0);
				}
			} else {
				((LNSprite) super._target).setFrameTime(t);
			}
		}
	}

	@Override
	public LNAction copy() {
		return Action(_ans, _restoreOriginalFrame);
	}
}
