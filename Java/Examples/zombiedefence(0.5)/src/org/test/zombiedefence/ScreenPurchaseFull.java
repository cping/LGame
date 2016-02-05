package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.canvas.LColor;
import loon.event.SysKey;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class ScreenPurchaseFull extends Screen {
	private float alpha;
	private float alphaInc;
	private LTexture t2DButtonBack;
	private LTexture t2DButtonDemo;
	private LTexture t2DButtonPurchase;
	private LTexture t2DIcon;
	private String txtInfo1;
	private String txtInfo2;

	public ScreenPurchaseFull() {
		this.txtInfo1 = "Commander is not available in free version, \nplease purchase the full game (Ads free)";
		this.txtInfo2 = "Check out the awesome demo to see commander \nin action :)";
	}

	@Override
	public void Draw(SpriteBatch batch) {
		super.Draw(batch);
		if (!super.isPaused) {
			batch.drawString(Screen.gothic60, "购买全部游戏", 20f, 20f, LColor.white,
					0f, 0f, 0f, 0.7f);
			batch.drawString(Screen.gothic24, this.txtInfo1, 200f, 120f,
					LColor.white, 0f, 0f, 0f, 0.9f);
			batch.drawString(Screen.gothic24, this.txtInfo2, 200f, 200f,
					LColor.white, 0f, 0f, 0f, 0.7f);
			batch.draw(ScreenSkill.t2DTagArtillery, 200f, 280f, LColor.white);
			batch.draw(ScreenSkill.t2DTagBoost, 320f, 280f, LColor.white);
			batch.draw(ScreenDay.t2DWhiteSquare, 20f, 100f, null, LColor.white,
					0f, 0f, 0f, 70f, 0.2f, SpriteEffects.None);
			batch.draw(this.t2DIcon, 20f, 120f, null, LColor.white, 0f, 0f, 0f,
					0.8f, 0.8f, SpriteEffects.None);
			batch.draw(
					this.t2DIcon,
					new Vector2f(20f, 120f + (this.t2DIcon.getHeight() * 0.8f)),
					null, new LColor(1f, 1f, 1f, 0.2f), 0f,
					new Vector2f(0f, 0f), new Vector2f(0.8f, 0.8f),
					SpriteEffects.FlipVertically);
		}
	}

	@Override
	public void LoadContent() {
		super.LoadContent();
		super.bgTexture = Global.Load("ScratchBG");
		this.t2DButtonBack = Global.Load("ButtonCancel");
		this.t2DButtonPurchase = Global.Load("ButtonConfirm");
		this.t2DButtonDemo = Global.Load("ButtonWatchDemo");
		this.t2DIcon = Global.Load("IconPro_200");
		super.buttonList.add(new Button(this.t2DButtonBack, new Vector2f(70f,
				450f), 0f, Help.ButtonID.Cancel, 30));
		super.buttonList.add(new Button(this.t2DButtonPurchase, new Vector2f(
				720f, 450f), 0f, Help.ButtonID.Proceed, 15));
		super.buttonList.add(new Button(this.t2DButtonDemo, new Vector2f(400f,
				450f), 0f, Help.ButtonID.Info, 15));
	}

	@Override
	public void Update(GameTime gameTime) {
		super.Update(gameTime);
		this.alpha += this.alphaInc;
		if (this.alpha >= 1f) {
			this.alphaInc = -0.05f;
		} else if (this.alpha <= 0f) {
			this.alphaInc = 0.05f;
		}
		if (super.isTranAnimFinished) {
			if (super.buttonClicked != null) {
				switch (super.buttonClicked.getButtonID()) {
				case Proceed: {

					super.buttonClicked = null;
					break;
				}
				case Info:
					Help.currentGameState = Help.GameScreen.Gameplay;
					ScreenGameplay.isDemoMode = true;
					ScreenGameplay.zombieBirthRate = 0.04233333f;
					super.buttonClicked = null;
					break;

				case Cancel:
					Help.currentGameState = Help.GameScreen.MainMenu;
					super.buttonClicked = null;
					break;
				default:
					break;
				}
			}
			super.buttonClicked = null;
			super.isTranAnimFinished = false;
		}
		if (super.mousePositionList.size() > 0) {
			super.mousePositionList.clear();
		}
		if (SysKey.isKeyPressed(SysKey.BACK)) {
			Help.currentGameState = Help.GameScreen.MainMenu;
		}
	}
}