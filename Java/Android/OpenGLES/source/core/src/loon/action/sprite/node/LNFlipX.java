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

public class LNFlipX extends LNAction {

	protected boolean _flipX;
	
	LNFlipX(){
		
	}

	public static LNFlipX Action(boolean fx) {
		LNFlipX flipx = new LNFlipX();
		flipx._flipX = fx;
		return flipx;
	}

	@Override
	public void setTarget(LNNode node) {
		super._firstTick = true;
		super._isEnd = false;
		super._target = node;
		if (super._target instanceof LNSprite) {
			((LNSprite) super._target).setFlipX(_flipX);
		}
	}

	@Override
	public void update(float t) {
		super._isEnd = true;
		if (super._target instanceof LNSprite) {
			((LNSprite) super._target).setFlipX(_flipX);
		}
	}

	@Override
	public LNAction copy() {
		return Action(_flipX);
	}

	public LNFlipX reverse() {
		return LNFlipX.Action(!_flipX);
	}
}
