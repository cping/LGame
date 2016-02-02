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

import java.util.ArrayList;

import loon.utils.timer.GameTime;

import org.test.act.actor.Enemy;
import org.test.act.actor.Enemy1;
import org.test.act.actor.Enemy2;
import org.test.act.actor.Player;
import org.test.act.actor.Shot;
import org.test.act.base.BaseContainer;
import org.test.act.base.BaseSprite;
import org.test.act.item.Block;
import org.test.act.item.MovableBG;
import org.test.sdata.MyStageData;
import org.test.sdata.StageData;

public abstract class Stage extends BaseContainer {

	protected BaseSprite backGround;
	protected ArrayList<Block> blocks;
	protected ArrayList<Enemy1> enemy1s;
	protected ArrayList<Enemy2> enemy2s;
	public boolean failed;
	protected MovableBG[] mbgs;
	protected MyStageData mySData;
	public Player player;
	public boolean successful;

	protected Stage() {
	}

	public void destroy() {
		super.removeChild(this.backGround);
		for (int i = 0; i < 2; i++) {
			if (this.mbgs[i] != null) {
				super.removeChild(this.mbgs[i].sps[0]);
				super.removeChild(this.mbgs[i].sps[1]);
			}
		}
		for (Block block : this.blocks) {
			super.removeChild(block);
		}
		for (Enemy1 enemy : this.enemy1s) {
			for (BaseSprite sprite : enemy.de.sps) {
				super.removeChild(sprite);
			}
			super.removeChild(enemy);
		}
		for (Enemy2 enemy2 : this.enemy2s) {
			for (BaseSprite sprite2 : enemy2.de.sps) {
				super.removeChild(sprite2);
			}
			super.removeChild(enemy2);
		}
		for (BaseSprite sprite3 : this.player.de.sps) {
			super.removeChild(sprite3);
		}
		for (Shot shot : this.player.shots) {
			super.removeChild(shot);
		}
		super.removeChild(this.player);
	}

	protected void Enemy2HitBlock() {
		for (Enemy2 enemy : this.enemy2s) {
			for (Block block : this.blocks) {
				if ((enemy.Pos.x > (block.Pos.x - (enemy.actWidth / 2f)))
						&& (enemy.Pos.x < ((block.Pos.x + block.getWidth()) + (enemy.actWidth / 2f)))) {
					if ((enemy.Pos.y >= block.Pos.y)
							&& (enemy.Pos.y <= (block.Pos.y + enemy.VY))) {
						enemy.hitBlockTop(block);
					} else if (((enemy.Pos.y - enemy.actHeight) <= (block.Pos.y + block
							.getHeight()))
							&& ((enemy.Pos.y - enemy.actHeight) >= ((block.Pos.y + block
									.getHeight()) - enemy.VY))) {
						enemy.hitBlockBottom(block);
					}
				}
				if ((enemy.Pos.y > block.Pos.y)
						&& (enemy.Pos.y < ((block.Pos.y + block.getHeight()) + enemy.actHeight))) {
					if (((enemy.Pos.x + (enemy.actWidth / 2f)) >= block.Pos.x)
							&& ((enemy.Pos.x + (enemy.actWidth / 2f)) <= ((block.Pos.x + 4f) + (enemy.actWidth / 2f)))) {
						enemy.hitBlockLeft(block);
						continue;
					}
					if (((enemy.Pos.x - (enemy.actWidth / 2f)) <= (block.Pos.x + block
							.getWidth()))
							&& ((enemy.Pos.x - (enemy.actWidth / 2f)) >= (((block.Pos.x + block
									.getWidth()) - 4f) - (enemy.actWidth / 2f)))) {
						enemy.hitBlockRight(block);
					}
				}
			}
		}
	}

	public abstract void init(Control control);

	protected abstract void isPlayerSuccessful();

	protected void oneEnemyHit(Enemy e, boolean able) {
		if (able && e.visible) {
			for (Shot shot : this.player.shots) {
				if (shot.visible && e.isHittedBy(shot)) {
					shot.visible = false;
					e.hitted();
				}
			}
			if ((this.player.visible && !this.player.god)
					&& this.player.isHittedBy(e)) {
				this.player.hitted();
			}
		}
	}

	protected void PlayerHitBlock() {
		for (Block block : this.blocks) {
			if ((this.player.Pos.x > (block.Pos.x - (this.player.actWidth / 2f)))
					&& (this.player.Pos.x < ((block.Pos.x + block.getWidth()) + (this.player.actWidth / 2f)))) {
				if ((this.player.Pos.y >= block.Pos.y)
						&& (this.player.Pos.y <= (block.Pos.y + this.player.VY))) {
					this.player.hitBlockTop(block);
				} else if (((this.player.Pos.y - this.player.actHeight) <= (block.Pos.y + block
						.getHeight()))
						&& ((this.player.Pos.y - this.player.actHeight) >= ((block.Pos.y + block
								.getHeight()) - this.player.VY))) {
					this.player.hitBlockBottom(block);
				}
			}
			if ((this.player.Pos.y > block.Pos.y)
					&& (this.player.Pos.y < ((block.Pos.y + block.getHeight()) + this.player.actHeight))) {
				if (((this.player.Pos.x + (this.player.actWidth / 2f)) >= block.Pos.x)
						&& ((this.player.Pos.x + (this.player.actWidth / 2f)) <= ((block.Pos.x + 4f) + (this.player.actWidth / 2f)))) {
					this.player.hitBlockLeft(block);
					continue;
				}
				if (((this.player.Pos.x - (this.player.actWidth / 2f)) <= (block.Pos.x + block
						.getWidth()))
						&& ((this.player.Pos.x - (this.player.actWidth / 2f)) >= (((block.Pos.x + block
								.getWidth()) - 4f) - (this.player.actWidth / 2f)))) {
					this.player.hitBlockRight(block);
				}
			}
		}
	}

	protected void setup() {
		this.mySData = new MyStageData();
		this.blocks = new ArrayList<Block>();
		this.enemy1s = new ArrayList<Enemy1>();
		this.enemy2s = new ArrayList<Enemy2>();
		this.mbgs = new MovableBG[2];
		this.successful = false;
		this.failed = false;
	}

	public void setupStage(int i) {
		StageData data = this.mySData.sData[i];
		super.addChild(this.backGround);
		int index = 0;
		for (StageData.mbg mbg : data.mbgs) {
			for (int j = 0; j < 2; j++) {
				this.mbgs[index].sps[j].Pos.y = mbg._y;
				this.mbgs[index].sps[j].moveRate = mbg._rate;
				super.addChild(this.mbgs[index].sps[j]);
			}
			index++;
		}
		for (StageData.bb bb : data.bbs) {
			Block sp = new Block();
			sp.Pos = bb.Pos;
			sp.resize(bb._w, bb._h);
			super.addChild(sp);
			this.blocks.add(sp);
		}
		for (StageData.enemy enemy : data.e1s) {
			Enemy1 enemy2 = new Enemy1();
			enemy2.Pos.x = enemy._x + (enemy2.Origin.x - 0f);
			enemy2.Pos.y = enemy._y + (enemy2.Origin.y - enemy2.actHeight);
			for (BaseSprite sprite : enemy2.de.sps) {
				super.addChild(sprite);
			}
			super.addChild(enemy2);
			this.enemy1s.add(enemy2);
		}
		for (StageData.enemy enemy3 : data.e2s) {
			Enemy2 enemy4 = new Enemy2(this.player);
			enemy4.Pos.x = enemy3._x;
			enemy4.Pos.y = enemy3._y;
			for (BaseSprite sprite2 : enemy4.de.sps) {
				super.addChild(sprite2);
			}
			super.addChild(enemy4);
			this.enemy2s.add(enemy4);
		}
		for (BaseSprite sprite3 : this.player.de.sps) {
			super.addChild(sprite3);
		}
		for (Shot shot : this.player.shots) {
			super.addChild(shot);
		}
		this.player.Pos.x = 300f;
		this.player.Pos.y = data.player_y;
		this.player.vy = data.player_vy;
		super.addChild(this.player);
	}

	protected void specificUpdate(GameTime gameTime) {
		for (int i = 0; i < 2; i++) {
			if (this.mbgs[i] != null) {
				this.mbgs[i].update();
			}
		}
		this.PlayerHitBlock();
		this.Enemy2HitBlock();
		this.player.updateAnimation();
		for (Enemy2 enemy : this.enemy2s) {
			enemy.updateAnimation();
		}
		for (Enemy1 enemy2 : this.enemy1s) {
			this.oneEnemyHit(enemy2, true);
		}
		for (Enemy2 enemy3 : this.enemy2s) {
			this.oneEnemyHit(enemy3, enemy3.moveable);
		}
		super.addPosX(375f - this.player.Pos.x);
		if (((this.player.Pos.y - this.player.actHeight) > 580f)
				&& this.player.visible) {
			this.player.die();
		}
		this.isPlayerSuccessful();
	}
}