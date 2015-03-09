/**
 * Copyright 2008 - 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except : compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to : writing, software
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

import java.util.Random;

import loon.LSystem;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.Vector2f;
import loon.core.graphics.device.LColor;
import loon.core.timer.GameTime;

import org.test.base.BaseSprite;
import org.test.item.Block;
import org.test.item.DeadEffect;
import org.test.item.SoundControl;

public class Boss extends BaseSprite {
	public float actHeight = 58f;
	public float actWidth = 42f;

	private int countStayInAir;
	public DeadEffect de;

	public boolean haventPlayedDead = true;

	private boolean i_am_hardly_walking_to_left;
	private boolean i_am_hardly_walking_to_right;
	private boolean isJumpingUp;
	public boolean isLanded;
	public boolean KA;
	public boolean KD;
	public boolean KI;
	public boolean KJ;
	public boolean KK;
	public int life = 20;
	public int MAX_ATTACK = 10;
	private int MAX_STAY_IN_AIR = 100;
	public float MAX_VY = 21f;
	private final int[] NORMAL_LOOP = new int[] { 6, 7, 8 };
	private Random random = new Random();

	public BossShot1[] shot1s = new BossShot1[3];
	public BossShot2[] shot2s = new BossShot2[2];
	private boolean skill0ing;
	private boolean stayInAir;
	private boolean toLeft = true;
	public final float VX = 7f;
	public float vy;
	public final float VY = 21f;

	public Boss() {
		super.Load("assets/boss", 0x12, 3, 3f, false);
		super.setAnimation(this.NORMAL_LOOP);
		this.Origin.x = super.getWidth() / 2f;
		this.Origin.y = super.getHeight();
		for (int i = 0; i < this.shot1s.length; i++) {
			this.shot1s[i] = new BossShot1();
		}
		this.shot2s[0] = new BossShot2(true);
		this.shot2s[1] = new BossShot2(false);
		super.effects = SpriteEffects.None;
		this.bounds.x = (int) (-this.actWidth / 2f);
		this.bounds.y = -((int) this.actHeight);
		this.bounds.width = (int) this.actWidth;
		this.bounds.height = (int) this.actHeight;

		this.de = new DeadEffect();
		this.de.init("assets/bossDE", 0f);
	}

	private void applyGravity() {
		this.Pos.y += this.vy;
		this.vy += 0.6f;
		if (this.vy > this.VY) {
			this.vy = this.VY;
		}
	}

	private void attack() {
		for (BossShot1 shot : this.shot1s) {
			if (!shot.visible) {
				if (SoundControl.on) {

				}
				shot.Pos.x = this.Pos.x;
				shot.Pos.y = this.Pos.y - 25f;
				shot.setV(new Vector2f(320f - this.Pos.x, 430f - this.Pos.y),
						8f);
				shot.visible = true;
				return;
			}
		}
	}

	private void bossLogic() {
		if (this.KJ) {
			this.attack();
		}
		if (this.isLanded) {
			this.clearThinkActionMove();
			this.clearThinkActionAttack();
			this.thinkActionJump();
			float num = (float) this.random.nextFloat();
			if (num < 0.65) {
				this.skill0();
				this.skill0ing = true;
			} else {
				this.skill1();
				this.skill0ing = false;
			}
		}
		if (this.skill0ing) {
			this.clearThinkActionAttack();
			float num2 = (float) this.random.nextFloat();
			if ((this.Pos.y < 430f) && (num2 < 0.05)) {
				this.thinkActionAttack();
			}
		} else {
			if (Math.abs(this.vy) < 0.6f) {
				this.stayInAir = true;
				this.thinkActionAttack2();
				this.shot2s[0].beginShoot(super.effects == SpriteEffects.None);
				this.shot2s[1].beginShoot(super.effects == SpriteEffects.None);
			}
			if (this.stayInAir) {
				this.countStayInAir++;
				if (this.countStayInAir > this.MAX_STAY_IN_AIR) {
					this.countStayInAir = 0;
					this.stayInAir = false;
					this.clearThinkActionAttack();
				}
			}
		}
	}

	private void cleanOutShot1s() {
		for (BossShot1 shot : this.shot1s) {
			if (shot.visible
					&& (((((shot.Pos.x - shot.Origin.x) + super.getWidth()) < 0f) || ((shot.Pos.x - shot.Origin.x) > LSystem.screenRect.width)) || (((shot.Pos.y - shot.Origin.y) > LSystem.screenRect.height) || (((shot.Pos.y - shot.Origin.y) + super
							.getHeight()) < 0f)))) {
				shot.visible = false;
			}
		}
	}

	private void clearThinkActionAttack() {
		if ((super._Frame >= 9) && (super._Frame <= 11)) {
			super._Frame -= 3;
		}
		this.KJ = false;
		this.KI = false;
	}

	private void clearThinkActionMove() {
		this.KA = false;
		this.KD = false;
		this.KK = false;
	}

	public void die() {
		if (this.haventPlayedDead) {
			if (SoundControl.on) {

			}
			this.haventPlayedDead = false;
		}
		this.life = 0;
		for (BaseSprite sprite : this.de.sps) {
			sprite.Pos = (super.Pos.sub(super.Origin)).add(this.actWidth / 2f,
					this.actHeight / 2f);
		}
		this.de.shoot();
		super.visible = false;
		this.clearThinkActionAttack();
		this.clearThinkActionMove();
	}

	public void hitBlockBottom(Block block) {
		if (this.vy < 0f) {
			this.vy = -this.vy;
		}
	}

	public void hitBlockLeft(Block block) {
		if (this.i_am_hardly_walking_to_right) {
			this.Pos.x = block.Pos.x - (this.actWidth / 2f);
		}
	}

	public void hitBlockRight(Block block) {
		if (this.i_am_hardly_walking_to_left) {
			this.Pos.x = (block.Pos.x + block.getWidth())
					+ (this.actWidth / 2f);
		}
	}

	public void hitBlockTop(Block block) {
		if (!this.isJumpingUp) {
			this.vy = 0f;
			this.isLanded = true;
			this.Pos.y = block.Pos.y;
		}
	}

	public void hitted() {
		super.color = LColor.red;
		this.life--;
		if ((this.life <= 0) && super.visible) {
			this.die();
		} else if (SoundControl.on) {

		}
	}

	private void jump() {
		if (!this.isJumpingUp) {
			this.vy = -this.VY;
			this.isJumpingUp = true;
			this.isLanded = false;
		}
	}

	private void skill0() {
		if (this.toLeft) {
			this.thinkActionLeft();
			this.toLeft = false;
		} else {
			this.thinkActionRight();
			this.toLeft = true;
		}
	}

	private void skill1() {
		if (this.Pos.x > 320f) {
			super.effects = SpriteEffects.None;
		} else {
			super.effects = SpriteEffects.FlipHorizontally;
		}
	}

	protected void specificUpdate(GameTime gameTime) {
		this.de.update(gameTime);
		super.color = LColor.white;
		if (super.visible) {
			this.applyGravity();
			this.cleanOutShot1s();
			if (super.visible) {
				if (this.KA && !this.KD) {
					this.Pos.x -= this.VX;
				} else if (this.KD && !this.KA) {
					this.Pos.x += this.VX;
				}
			}
			if (this.vy > this.MAX_VY) {
				this.vy = this.MAX_VY;
			}
			if (this.vy > 0f) {
				this.isJumpingUp = false;
			}
			this.bossLogic();
		}
	}

	private void thinkActionAttack() {
		if ((super._Frame >= 6) && (super._Frame <= 8)) {
			super._Frame += 3;
		}
		this.KJ = true;
	}

	private void thinkActionAttack2() {
		if ((super._Frame >= 6) && (super._Frame <= 8)) {
			super._Frame += 3;
		}
		this.KI = true;
	}

	private void thinkActionJump() {
		this.KK = true;
		this.jump();
	}

	private void thinkActionLeft() {
		this.KA = true;
		super.effects = SpriteEffects.None;
	}

	private void thinkActionRight() {
		this.KD = true;
		super.effects = SpriteEffects.FlipHorizontally;
	}
}
