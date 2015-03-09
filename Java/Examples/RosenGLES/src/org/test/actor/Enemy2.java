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

import org.test.item.Block;

import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.graphics.device.LColor;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;

public class Enemy2 extends Enemy {
	private Player _player;
	public boolean actable;
	public float actDis = 300f;
	private boolean i_am_hardly_walking_to_left;
	private boolean i_am_hardly_walking_to_right;
	private boolean isfacingToLeft;
	public boolean moveable;
	public final float VX = 3f;
	public float vy;
	public final float VY = 15f;

	public Enemy2(Player player) {
		super.actWidth = 42f;
		super.actHeight = 58f;
		this._player = player;
		super.life = 3;
		super.loadSe("assets/enemyDE", 0.3f);
		super.Load("assets/e2", 0x12, 9, 2f, true);
		super.setAnimation(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 });
		super._Paused = true;
		this.Origin.x = super.getWidth() / 2f;
		this.Origin.y = super.getHeight();
		this.bounds.x = (int) (-super.actWidth / 2f);
		this.bounds.y = -((int) super.actHeight);
		this.bounds.width = (int) super.actWidth;
		this.bounds.height = (int) super.actHeight;
	}

	private void applyGravity() {
		this.Pos.y += this.vy;
		this.vy += 0.8f;
		if (this.vy > this.VY) {
			this.vy = this.VY;
		}
	}

	public void hitBlockBottom(Block block) {
		if (this.vy < 0f) {
			this.vy = -this.vy;
		}
	}

	public void hitBlockLeft(Block block) {
		if (this.i_am_hardly_walking_to_right) {
			this.Pos.x = block.Pos.x - (super.actWidth / 2f);
		}
	}

	public void hitBlockRight(Block block) {
		if (this.i_am_hardly_walking_to_left) {
			this.Pos.x = (block.Pos.x + block.getWidth())
					+ (super.actWidth / 2f);
		}
	}

	public void hitBlockTop(Block block) {
		this.vy = 0f;
		this.Pos.y = block.Pos.y;
	}

	protected void specificUpdate(GameTime gameTime) {
		this.applyGravity();
		super.de.update(gameTime);
		if (!this.actable
				&& (MathUtils.abs((float) (this.Pos.x - this._player.Pos.x)) <= this.actDis)) {
			this.actable = true;
		}
		if (this.actable) {
			super._Paused = false;
			if (super._Frame == 8) {
				super._TimePerFrame = 0.06666667f;
				super.setAnimation(new int[] { 9, 10, 11, 10 });
				this.moveable = true;
			}
		}
		if (this.moveable) {
			if ((this.Pos.x - this._player.Pos.x) >= 0f) {
				this.i_am_hardly_walking_to_left = true;
				this.i_am_hardly_walking_to_right = false;
				this.isfacingToLeft = true;
				this.Pos.x -= this.VX;
			} else {
				this.i_am_hardly_walking_to_left = false;
				this.i_am_hardly_walking_to_right = true;
				this.isfacingToLeft = false;
				this.Pos.x += this.VX;
			}
		}
		super.color = LColor.white;
	}

	public void updateAnimation() {
		if (this.isfacingToLeft) {
			super.effects = SpriteEffects.None;
		} else {
			super.effects = SpriteEffects.FlipHorizontally;
		}
	}
}
