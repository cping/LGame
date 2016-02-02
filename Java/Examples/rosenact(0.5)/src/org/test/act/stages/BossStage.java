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
package org.test.act.stages;

import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

import org.test.act.actor.Boss;
import org.test.act.actor.BossShot1;
import org.test.act.actor.BossShot2;
import org.test.act.actor.Player;
import org.test.act.actor.Shot;
import org.test.act.base.BaseSprite;
import org.test.act.item.Block;
import org.test.act.item.LifePanel;
import org.test.act.item.SoundControl;

public class BossStage extends Stage {

	public Boss boss;
	private boolean bossDoorShouldClose;
	public LifePanel bossLife;
	private int count;
	private boolean songPlayed;

	protected void BossHit() {
		if (this.boss.visible) {
			for (Shot shot : super.player.shots) {
				if (shot.visible && this.boss.isHittedBy(shot)) {
					shot.visible = false;
					this.boss.hitted();
				}
			}
			if ((super.player.visible && !super.player.god)
					&& super.player.isHittedBy(this.boss)) {
				super.player.hitted();
			}
			float num = 25f;
			for (BossShot2 shot2 : this.boss.shot2s) {
				float num2 = super.player.Pos.x - shot2.Pos.x;
				float num3 = (super.player.Pos.y - num) - shot2.Pos.y;
				if ((((num2 * num2) + (num3 * num3)) < 17500f)
						&& !super.player.god) {
					super.player.hitted();
				}
			}
			for (BossShot1 shot3 : this.boss.shot1s) {
				float num4 = super.player.Pos.x - shot3.Pos.x;
				float num5 = (super.player.Pos.y - num) - shot3.Pos.y;
				if ((((num4 * num4) + (num5 * num5)) < (4096f + (num * num)))
						&& !super.player.god) {
					super.player.hitted();
				}
			}
		}
	}

	protected void BossHitBlock() {
		for (Block block : super.blocks) {
			if (((this.boss.Pos.x > (block.Pos.x - (this.boss.actWidth / 2f))) && (this.boss.Pos.x < ((block.Pos.x + block
					.getWidth()) + (this.boss.actWidth / 2f))))
					&& ((this.boss.Pos.y >= block.Pos.y) && (this.boss.Pos.y <= (block.Pos.y + this.boss.VY)))) {
				this.boss.hitBlockTop(block);
			}
		}
	}

	public void destroy() {
		this.count = 0;
		this.songPlayed = false;
		super.destroy();
		for (BaseSprite sprite : this.bossLife.sps) {
			super.removeChild(sprite);
		}
		for (BossShot1 shot : this.boss.shot1s) {
			super.removeChild(shot);
		}
		for (BossShot2 shot2 : this.boss.shot2s) {
			super.removeChild(shot2);
		}
		for (BaseSprite sprite2 : this.boss.de.sps) {
			super.removeChild(sprite2);
		}
		super.removeChild(this.boss);
	}

	public void init(Control control) {
		super.setup();
		this.songPlayed = false;
		super.backGround = new BaseSprite();
		super.backGround.Load("assets/bg1", 1, 1f, true);
		super.backGround.moveRate = 0f;
		super.player = new Player(control);
		this.bossLife = new LifePanel(20, "assets/lifeBoss", new Vector2f(774f,
				10f));
		this.bossLife.reset(20);
		this.boss = new Boss();
		this.boss.Pos.x = 670f - this.boss.getWidth();
		this.boss.Pos.y = 30f + this.boss.getHeight();
		super.setupStage(2);
		for (BossShot1 shot : this.boss.shot1s) {
			super.addChild(shot);
		}
		for (BossShot2 shot2 : this.boss.shot2s) {
			super.addChild(shot2);
		}
		for (BaseSprite sprite : this.boss.de.sps) {
			super.addChild(sprite);
		}
		super.addChild(this.boss);
		super.addPosX(80f);
		for (BaseSprite sprite2 : this.bossLife.sps) {
			super.addChild(sprite2);
		}
	}

	protected void isPlayerSuccessful() {
		if (this.count > 550) {
			this.count = 550;
			super.successful = true;
		}
		if (super.player.life <= 0) {
			super.failed = true;
		}
	}

	protected void specificUpdate(GameTime gameTime) {
		if (!this.songPlayed) {
			if (this.boss.life <= 0) {
				this.songPlayed = true;
				if (SoundControl.on) {

				}
			}
		} else {
			this.count++;
		}
		this.bossLife.minusLife(this.boss.life);
		super.PlayerHitBlock();
		this.BossHitBlock();
		this.BossHit();
		super.player.updateAnimation();
		if (super.player.Pos.y < 610f) {
			this.bossDoorShouldClose = true;
		}
		if (this.bossDoorShouldClose
				&& ((super.blocks.get(3).Pos.x + super.blocks.get(3).getWidth()) < super.blocks
						.get(4).Pos.x)) {
			super.blocks.get(3).Scale.x += 4f / ((float) super.blocks.get(3)._width);
			super.blocks.get(4).Scale.x += 4f / ((float) super.blocks.get(4)._width);
			super.blocks.get(4).Pos.x -= 4f;
		}
		this.isPlayerSuccessful();
	}
}