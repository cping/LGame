package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class ScreenFail extends Screen {
	private java.util.ArrayList<BloodSplatter> bloodSplatterList;
	private boolean isTitleReady;
	private Random rand;
	private LTexture t2DBloodSplatter;
	private LTexture t2DButtonBack;
	private LTexture t2DGameOverTitle;

	public ScreenFail() {
		this.rand = new Random();
		this.isTitleReady = false;
	}

	@Override
	public void Draw(SpriteBatch batch) {
		super.Draw(batch);
		for (BloodSplatter splatter : this.bloodSplatterList) {
			splatter.Draw(batch);
		}
		if (this.isTitleReady) {
			batch.draw(super.maskTexture, 0f, 0f,
					Global.Pool.getColor(1f, 1f, 1f, 0.7f));
			batch.draw(this.t2DGameOverTitle, 200f, 180f);
			for (Button button : super.buttonList) {
				button.Draw(batch);
			}
		}
	}

	@Override
	public void LoadContent() {
		super.LoadContent();
		super.bgTexture = Global.Load("GameOver");
		this.t2DButtonBack = Global.Load("ButtonBack");
		this.t2DBloodSplatter = Global.Load("BloodSplatter");
		this.t2DGameOverTitle = Global.Load("GameOverTitle");
		super.buttonList.add(new Button(this.t2DButtonBack, new Vector2f(700f,
				420f), 0f, Help.ButtonID.Back, 15));
		this.bloodSplatterList = new java.util.ArrayList<BloodSplatter>();
	}

	@Override
	public void Update(GameTime gameTime) {
		super.Update(gameTime);
		if ((super.iScreen > 20) && (this.bloodSplatterList.size() < 1)) {
			this.bloodSplatterList.add(new BloodSplatter(this.t2DBloodSplatter,
					new Vector2f(405f, 320f)));
			this.bloodSplatterList.get(0).angle = (((float) this.rand
					.NextDouble()) * 3.141593f) * 2f;
			this.bloodSplatterList.get(0).scale = new Vector2f(2f, 2f);
			ScreenGameplay.soundZombie1.Play();
		}
		if ((super.iScreen > 0x19) && (this.bloodSplatterList.size() < 2)) {
			this.bloodSplatterList.add(new BloodSplatter(this.t2DBloodSplatter,
					new Vector2f(440f, 280f)));
			this.bloodSplatterList.get(1).angle = (((float) this.rand
					.NextDouble()) * 3.141593f) * 2f;
			this.bloodSplatterList.get(1).scale = new Vector2f(1.8f, 1.8f);
		}
		if ((super.iScreen > 30) && (this.bloodSplatterList.size() < 3)) {
			this.bloodSplatterList.add(new BloodSplatter(this.t2DBloodSplatter,
					new Vector2f(490f, 250f)));
			this.bloodSplatterList.get(2).angle = (((float) this.rand
					.NextDouble()) * 3.141593f) * 2f;
			this.bloodSplatterList.get(2).scale = new Vector2f(1.6f, 1.6f);
		}
		if ((super.iScreen > 0x23) && (this.bloodSplatterList.size() < 4)) {
			this.bloodSplatterList.add(new BloodSplatter(this.t2DBloodSplatter,
					new Vector2f(550f, 230f)));
			this.bloodSplatterList.get(3).angle = (((float) this.rand
					.NextDouble()) * 3.141593f) * 2f;
			this.bloodSplatterList.get(3).scale = new Vector2f(1.4f, 1.4f);
		}
		if ((super.iScreen > 40) && (this.bloodSplatterList.size() < 5)) {
			this.bloodSplatterList.add(new BloodSplatter(this.t2DBloodSplatter,
					new Vector2f(620f, 215f)));
			this.bloodSplatterList.get(4).angle = (((float) this.rand
					.NextDouble()) * 3.141593f) * 2f;
			this.bloodSplatterList.get(4).scale = new Vector2f(1.2f, 1.2f);
		}
		if ((super.iScreen > 0x2d) && (this.bloodSplatterList.size() < 6)) {
			this.bloodSplatterList.add(new BloodSplatter(this.t2DBloodSplatter,
					new Vector2f(690f, 208f)));
			this.bloodSplatterList.get(5).angle = (((float) this.rand
					.NextDouble()) * 3.141593f) * 2f;
		}
		if (super.iScreen > 60) {
			this.isTitleReady = true;
		}
		if (super.isTranAnimFinished) {
			if (super.buttonClicked != null) {
				switch (super.buttonClicked.getButtonID()) {
				case Proceed:
					Help.currentGameState = Help.GameScreen.LevelUp;
					super.buttonClicked = null;
					break;

				case Back:
					Help.currentGameState = Help.GameScreen.MainMenu;
					ScreenGameplay.isToBeDeleted = true;
					ScreenSkill.isToBeDeleted = true;
					Help.money = 0;
					Help.zombieHealthMax = 20;
					ScreenGameplay.isToBeDeleted = true;
					Help.numGrenade = 5;
					Help.barrierHealth = Help.barrierHMax;
					Help.AvailSkillPoint = 0;
					Help.numSkill1 = 0;
					Help.numSkill2 = 0;
					Help.numSkill3 = 0;
					Help.numSkill4 = 0;
					Help.numSkill5 = 0;
					Help.numSkill6 = 0;
					Help.numSkill7 = 0;
					Help.numSkill8 = 0;
					super.buttonClicked = null;
					break;
				default:
					break;
				}
			}
			super.buttonClicked = null;
			super.isTranAnimFinished = false;
			for (Button button : super.buttonList) {
				button.ButtonInitialize();
			}
		}
		if (super.mousePositionList.size() > 0) {
			super.mousePositionList.clear();
		}
	}
}