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
package org.test.actor;

import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.timer.GameTime;

import org.test.base.BaseSprite;
import org.test.item.SoundControl;

public class Shot extends BaseSprite {
	
	public final float actHeight = 36f;
	
	public final float actWidth = 60f;
	
	private float vx;
	
	private final float VX = 15f;

	public Shot() {
		super.Load("assets/shot", 6, 6, 0f, false);
		super.setAnimation(new int[] { 0, 1, 2, 3, 4, 5 });
		this.Origin.x = super.getWidth() / 2f;
		this.Origin.y = super.getHeight() / 2f;
		super.visible = false;
		this.bounds.x = (int) (-this.actWidth / 2f);
		this.bounds.y = (int) (-this.actHeight / 2f);
		this.bounds.width = (int) this.actWidth;
		this.bounds.height = (int) this.actHeight;
	}

	public void shoot(float x, float y, boolean toLeft) {
		if (SoundControl.on) {

		}
		this.Pos.x = x;
		this.Pos.y = y;
		super.visible = true;
		if (toLeft) {
			this.vx = -VX;
			this.Pos.x -= 20f;
			super.effects = SpriteEffects.None;
		} else {
			this.vx = VX;
			this.Pos.x += 20f;
			super.effects = SpriteEffects.FlipHorizontally;
		}
	}

	protected void specificUpdate(GameTime gameTime) {
		this.Pos.x += this.vx;
	}
}
