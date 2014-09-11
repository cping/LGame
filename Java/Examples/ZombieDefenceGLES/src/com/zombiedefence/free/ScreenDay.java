package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;

public class ScreenDay extends Screen {
	private java.util.ArrayList<FlashingSquare> flashingSquareList;
	private int iNext;
	private boolean isChangingTip;
	private int iSelect;
	private int iSquare;
	private int iSquareIndex;
	private int length;
	public static LTexture t2DMask;
	public static LTexture t2DWhiteSquare;
	private Tips[] tips;
	private float tipsAlpha;
	private int tipsYOffset;

	public ScreenDay() {
		super();
		super.screenPause = new ScreenPause(this, Help.GameScreen.Day);
		this.flashingSquareList = new java.util.ArrayList<FlashingSquare>();
	}

	@Override
	public void Draw(SpriteBatch batch) {
		super.Draw(batch);
		if (!super.isPaused) {
			float num;
			if (super.iScreen > (this.length - 30)) {
				num = ((float) (this.length - super.iScreen)) / 30f;
			} else {
				num = 1f;
			}
			batch.draw(t2DMask, 0f, 0f, null,
					Global.Pool.getColor(1f, 1f, 1f, 1f * num), 0f, 0f, 0f, 1f,
					SpriteEffects.None);
			batch.drawString(Screen.ariel60, "Day " + ScreenGameplay.day, 50f,
					100f, Global.Pool.getColor(1f, 1f, 1f, 1f * num));
			batch.drawString(Screen.gothic24, "Starting in", 370f, 150f,
					Global.Pool.getColor(1f, 1f, 1f, 1f * num));
			float c = 1f * num * 0.8f;
			LColor color = Global.Pool.getColor(1f, 1f, 1f, c);
			batch.draw(t2DWhiteSquare, 540f, 170f, null, color, 0f, 0f, 0f,
					1.4f, SpriteEffects.None);
			batch.draw(t2DWhiteSquare, 580f, 170f, null, color, 0f, 0f, 0f,
					1.4f, SpriteEffects.None);
			batch.draw(t2DWhiteSquare, 620f, 170f, null, color, 0f, 0f, 0f,
					1.4f, SpriteEffects.None);
			batch.drawString(Screen.gothic24, "Tips", 100f, 280f, LColor.wheat);
			batch.draw(t2DWhiteSquare, 100f, 330f, null, color, 0f, 0f, 0f,
					70f, 0.2f, SpriteEffects.None);
			for (FlashingSquare square : this.flashingSquareList) {
				RectBox sourceRectangle = null;
				batch.draw(
						square.texture,
						square.position,
						sourceRectangle,
						Global.Pool.getColor(1f, 1f, 1f, 1f * num
								* square.alpha), 0f, 0f, 0f, 2.5f,
						SpriteEffects.None);
			}
			batch.drawString(Screen.ariel60, ""
					+ (((this.length - 30) - super.iScreen) / 30), 680f, 100f,
					LColor.white);
			this.tips[this.iSelect].Draw(batch, this.tipsAlpha * num,
					this.tipsYOffset);
			this.tips[this.iNext].Draw(batch, (1f - this.tipsAlpha) * num, -50
					+ this.tipsYOffset);
			for (Button button : super.buttonList) {
				button.Draw(batch);
			}
		}
	}

	@Override
	public void LoadContent() {
		super.LoadContent();
		super.bgTexture = Global.Load("PlayGround");
		t2DMask = Global.Load("ScratchBG");
		t2DWhiteSquare = Global.Load("WhiteSquare");
		this.length = 270;
		this.tips = new Tips[] {
				new Tips(ScreenSkill.t2DTagArtillery,
						"我们可以呼叫炮火进行支援"),
				new Tips(ScreenSkill.t2DTagAim,
						"提升技能获得更高的爆头率"),
				new Tips(ScreenSkill.t2DTagAAGun,
						"在学习必要的技能后，可以购买 'AA Gun(高射炮)'"),
				new Tips(
						"手雷很好用，但每天最多只有五个") };
		this.iSelect = (int) ((MathUtils.random() * this.tips.length) * 0.99f);
		this.iNext = (int) ((MathUtils.random() * this.tips.length) * 0.99);
		this.tipsAlpha = 1f;
		this.tipsYOffset = 0;
		this.iSquare = 0;
		this.iSquareIndex = 0;
		this.isChangingTip = false;
		super.screenPause.LoadContent();
	}

	@Override
	public void Update(GameTime gameTime) {
		super.Update(gameTime);
		this.iSquare++;
		if (super.iScreen > this.length) {
			Help.currentGameState = Help.GameScreen.Gameplay;
			super.iScreen = 0;
			this.iSelect = (int) ((MathUtils.random() * this.tips.length) * 0.99);
			this.iNext = (int) ((MathUtils.random() * this.tips.length) * 0.99);
			this.tipsAlpha = 1f;
			this.tipsYOffset = 0;
			this.isChangingTip = false;
		}
		if ((super.iScreen > (this.length / 3)) && (this.tipsYOffset <= 50)) {
			this.isChangingTip = true;
		}
		if (this.isChangingTip) {
			this.tipsYOffset += 3;
			this.tipsAlpha -= 0.1f;
			if (this.tipsAlpha < 0f) {
				this.tipsAlpha = 0f;
			}
		}
		if (this.tipsYOffset > 50) {
			this.isChangingTip = false;
		}
		if (this.iSquare >= 20) {
			this.iSquare = 0;
			this.flashingSquareList.add(new FlashingSquare(t2DWhiteSquare,
					new Vector2f((float) (0x217 + (40 * this.iSquareIndex)),
							165f)));
			this.iSquareIndex++;
			if (this.iSquareIndex > 2) {
				this.iSquareIndex = 0;
			}
		}
		for (int i = 0; i < this.flashingSquareList.size(); i++) {
			this.flashingSquareList.get(i).Update();
			if (this.flashingSquareList.get(i).isDead) {
				this.flashingSquareList.remove(i);
			}
		}
		if (super.isTranAnimFinished) {
			if (super.buttonClicked.getButtonID() == Help.ButtonID.Back) {
				Help.currentGameState = Help.GameScreen.MainMenu;
				super.buttonClicked = null;
			}
			super.buttonClicked = null;
			super.isTranAnimFinished = false;
		}
		if (super.mousePositionList.size() > 0) {
			super.mousePositionList.clear();
		}
	}
}