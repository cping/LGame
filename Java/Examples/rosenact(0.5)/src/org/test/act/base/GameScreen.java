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
package org.test.act.base;

import org.test.act.item.LifePanel;
import org.test.act.item.SoundControl;
import org.test.act.stages.BossStage;
import org.test.act.stages.Control;
import org.test.act.stages.Gameover;
import org.test.act.stages.Stage;
import org.test.act.stages.Stage1;
import org.test.act.stages.Stage2;
import org.test.act.stages.Title;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableScreen;
import loon.events.GameKey;
import loon.events.GameTouch;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class GameScreen extends DrawableScreen {

	protected boolean bgmPlayed;
	private Control control;
	protected Gameover gameover;
	protected int gameoverCount;
	protected int gameoverMAXCOUNT = 400;
	private Stage nowStage;
	protected LifePanel playerLife;
	protected int stageNum;
	private Stage[] stages = new Stage[4];

	public void backToTitle() {
		this.nowStage.failed = true;
	}

	public boolean isInTitle() {
		return (this.stageNum == 0);
	}

	public void draw(SpriteBatch batch) {
		if (!isOnLoadComplete()) {
			return;
		}
		this.nowStage.DrawFrame(batch);
		for (BaseSprite sprite : this.playerLife.sps) {
			sprite.DrawFrame(batch);
		}
		this.gameover.DrawFrame(batch);
		this.control.DrawFrame(batch);

	}

	public void loadContent() {

		this.stages[0] = new Title();
		this.stages[1] = new Stage1();
		this.stages[2] = new Stage2();
		this.stages[3] = new BossStage();
		this.control = new Control();
		this.nowStage = this.stages[this.stageNum];
		this.gameover = new Gameover();
		this.playerLife = new LifePanel(10, "assets/lifePlayer", new Vector2f(
				10f, 10f));

		this.control.init();
		for (Stage stage : this.stages) {
			stage.init(this.control);
		}
		SoundControl.on = false;

	}

	public void unloadContent() {

	}

	public void pressed(GameTouch e) {
		for (BaseSprite sprite : this.playerLife.sps) {
			sprite.UpdateFrame(getGameTime());
		}
	}

	public void released(GameTouch e) {

	}

	public void move(GameTouch e) {

	}

	public void drag(GameTouch e) {

	}

	public void pressed(GameKey e) {

	}

	public void released(GameKey e) {

	}

	@Override
	public void update(GameTime gameTime) {
		if (!isOnLoadComplete()) {
			return;
		}
		this.control.UpdateFrame(gameTime);
		if (this.stageNum > 0) {
			this.playerLife.minusLife(this.nowStage.player.life);
		}
		this.nowStage.UpdateFrame(gameTime);
		if (this.nowStage.successful) {
			this.stageNum++;
			this.nowStage.successful = false;
			int n = 10;
			if (this.stageNum != 1) {
				n = this.nowStage.player.life;
			}
			if (this.stageNum == 4) {
				n = 10;
				this.stageNum = 1;
			}
			this.nowStage.destroy();
			this.nowStage.init(this.control);
			this.nowStage = this.stages[this.stageNum];
			this.nowStage.player.life = n;
			this.playerLife.reset(n);
			if (SoundControl.on) {
				switch (this.stageNum) {
				case 1:
				case 2:
					break;
				case 3:
					break;
				}
			}
		}
		this.gameover.UpdateFrame(gameTime);
		if (this.nowStage.failed) {
			if (!this.bgmPlayed) {
				this.gameover.visible = true;
				if (SoundControl.on) {

				}
				this.bgmPlayed = true;
			}
			this.gameoverCount++;
			if (this.gameoverCount > 400) {
				this.gameoverCount = 0;
				this.gameover.visible = false;
				if (SoundControl.on) {

				}
				this.bgmPlayed = false;
				this.nowStage.failed = false;
				this.nowStage.destroy();
				this.nowStage.init(this.control);
				this.nowStage = this.stages[0];
				this.stageNum = 0;
			}
		}

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

}