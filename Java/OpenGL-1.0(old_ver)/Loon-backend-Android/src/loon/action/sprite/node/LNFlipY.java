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

public class LNFlipY extends LNAction {

	protected boolean _flipY;
	
	LNFlipY(){
		
	}

	public static LNFlipY Action(boolean fy) {
		LNFlipY flipy = new LNFlipY();
		flipy._flipY = fy;
		return flipy;
	}

	@Override
	public void setTarget(LNNode node) {
		super._firstTick = true;
		super._isEnd = false;
		super._target = node;
		if (super._target instanceof LNSprite) {
			((LNSprite) super._target).setFlipY(_flipY);
		}
	}

	@Override
	public void update(float t) {
		super._isEnd = true;
		if (super._target instanceof LNSprite) {
			((LNSprite) super._target).setFlipY(_flipY);
		}
	}

	@Override
	public LNAction copy() {
		return Action(_flipY);
	}
	
	public LNFlipY reverse() {
		return LNFlipY.Action(!_flipY);
	}
}
