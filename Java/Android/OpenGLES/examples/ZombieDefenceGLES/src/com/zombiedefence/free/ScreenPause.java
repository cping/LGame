package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTexture;
import loon.core.input.LInputFactory;
import loon.core.input.LInputFactory.Key;
import loon.core.input.LTouchCollection;
import loon.core.input.LTouchLocation;
import loon.core.input.LTouchLocationState;
import loon.core.timer.GameTime;

public class ScreenPause extends Screen {
	public boolean isStillPaused;
	public Screen screenWithin;
	public Help.GameScreen screenWithinType;
	private LTexture t2DButtonNo;
	private LTexture t2DButtonYes;
	private LTexture t2DTitle;

	public ScreenPause(Screen screenWithin, Help.GameScreen screenWithinType) {
		super();
		this.screenWithin = screenWithin;
		this.screenWithinType = screenWithinType;
		this.isStillPaused = true;
	}

	@Override
	public void Draw(SpriteBatch batch) {
		super.Draw(batch);
		batch.draw(this.t2DTitle, 150f, 150f);
	}

	@Override
	public void LoadContent() {
		super.LoadContent();
		super.bgTexture = Global.Load("ScratchBG");
		this.t2DButtonYes = Global.Load("ButtonYes");
		this.t2DButtonNo = Global.Load("ButtonNo");
		this.t2DTitle = Global.Load("ExitTitle");
		super.buttonList.add(new Button(this.t2DButtonYes, new Vector2f(220f,
				320f), 0f, Help.ButtonID.Proceed, 20));
		super.buttonList.add(new Button(this.t2DButtonNo, new Vector2f(520f,
				320f), 0f, Help.ButtonID.Back, 20));
	}

	@Override
	public void Update(GameTime gameTime) {
		LTouchCollection state = LInputFactory.getTouchState();
		if (super.buttonClicked == null) {
			for (LTouchLocation location : state) {
				if (location.getState() == LTouchLocationState.Released) {
					Vector2f mousePosition = new Vector2f(
							location.getPosition().x, location.getPosition().y);
					for (Button button : super.buttonList) {
						if (button.IsClicked(mousePosition)) {
							super.buttonClicked = button;
						}
					}
					continue;
				}
			}
			if (super.maskAlpha > 0f) {
				super.maskAlpha -= 0.1f;
			} else if (super.maskAlpha < 0f) {
				super.maskAlpha = 0f;
			}
		}
		if (super.buttonClicked != null) {
			super.buttonClicked.TransAnimation();
			if (super.buttonClicked.isTakingEffect) {
				super.isTranAnimFinished = true;
			} else {
				super.maskAlpha = (1f - super.buttonClicked.getTransAlpha()) * 1.5f;
				if (super.maskAlpha > 1f) {
					super.maskAlpha = 1f;
				}
			}
		}
		if (!super.isTranAnimFinished) {
			if (Key.isKeyPressed(Key.BACK)
					&& LInputFactory.getOnlyKey().isPressed()) {
				Help.currentGameState = this.screenWithinType;
				this.isStillPaused = false;
			}
		}
		if (super.buttonClicked != null) {
			switch (super.buttonClicked.getButtonID()) {
			case Proceed:
				Help.currentGameState = Help.GameScreen.MainMenu;
				this.screenWithin.iScreen = 0;
				if (((this.screenWithinType != Help.GameScreen.Gameplay) && (this.screenWithinType != Help.GameScreen.LevelUp))
						&& ((this.screenWithinType != Help.GameScreen.LevelUp2) && (this.screenWithinType != Help.GameScreen.Day))) {
					if (this.screenWithinType == Help.GameScreen.Skill) {
						ScreenSkill.isToBeDeleted = true;
					}
					break;
				}
				ScreenGameplay.isToBeDeleted = true;
				break;

			case Back:
				Help.currentGameState = this.screenWithinType;
				super.buttonClicked = null;

				this.isStillPaused = false;
				super.isTranAnimFinished = false;
				for (Button button2 : super.buttonList) {
					button2.ButtonInitialize();
				}

				if (Key.isKeyPressed(Key.BACK)&&LInputFactory.getOnlyKey().isPressed()) {
					Help.currentGameState = this.screenWithinType;
					this.isStillPaused = false;
				}
				break;

			default:
				this.isStillPaused = false;
				super.isTranAnimFinished = false;
				for (Button button2 : super.buttonList) {
					button2.ButtonInitialize();
				}

				if (Key.isKeyPressed(Key.BACK)&&LInputFactory.getOnlyKey().isPressed()) {
					Help.currentGameState = this.screenWithinType;
					this.isStillPaused = false;
				}
				break;
			}
		}
		super.buttonClicked = null;
	}

	public final boolean getIsStillPaused() {
		return this.isStillPaused;
	}

	public final void setIsStillPaused(boolean value) {
		this.isStillPaused = value;
	}
}