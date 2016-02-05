package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.event.LTouchCollection;
import loon.event.LTouchLocation;
import loon.event.LTouchLocationState;
import loon.event.SysInputFactory;
import loon.event.SysKey;
import loon.font.LFont;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class Screen {
	protected LTexture adBannerTexture;
	public static LFont ariel14;
	public static LFont ariel18;
	public static LFont ariel60;
	protected LTexture bgTexture;
	protected Button buttonClicked;

	protected java.util.ArrayList<Button> buttonList;

	public static LFont gothic18;
	public static LFont gothic24;
	public static LFont gothic60;

	public int iScreen;
	protected boolean isPaused;
	public static boolean isTouchInputValid;
	protected boolean isTranAnimFinished;
	protected float maskAlpha;
	protected LTexture maskTexture;
	public java.util.ArrayList<Vector2f> mousePositionList;
	public static LFont myFont;
	protected ScreenPause screenPause;
	public static LFont segoe16;
	public static LFont segoe24;

	public Screen() {
		SysInputFactory.startTouchCollection();
		this.isTranAnimFinished = false;
		this.isPaused = false;
		isTouchInputValid = false;
		this.iScreen = 0;
	}

	public void Draw(SpriteBatch batch) {
		if (this.isPaused && (this.screenPause != null)) {
			this.screenPause.Draw(batch);
		} else {
			batch.draw(this.bgTexture, 0f, 0f);
			for (Button button : this.buttonList) {
				button.Draw(batch);
			}
		}
	}

	public void LoadContent() {
		this.maskTexture = Global.Load("Mask");
		myFont = LFont.getFont(20);
		ariel18 = LFont.getFont(22);
		ariel14 = LFont.getFont(14);
		ariel60 = LFont.getFont(60);
		segoe16 = LFont.getFont(16);
		segoe24 = LFont.getFont(24);
		gothic18 = LFont.getFont(22);
		gothic24 = LFont.getFont(24);
		gothic60 = LFont.getFont(60);
		this.buttonList = new java.util.ArrayList<Button>();
		this.mousePositionList = new java.util.ArrayList<Vector2f>();
	}

	public void Update(GameTime gameTime) {
		this.iScreen++;
		if (this.isPaused && (this.screenPause != null)) {
			this.screenPause.Update(gameTime);
			this.screenPause.setIsStillPaused(this.screenPause
					.getIsStillPaused());
			if (!this.screenPause.getIsStillPaused()) {
				this.isPaused = false;
			}
		} else {
			LTouchCollection state = SysInputFactory.getTouchState();
			if (this.buttonClicked == null) {
				if (state.size() == 0) {
					isTouchInputValid = false;
				} else {
					for (LTouchLocation location : state) {
						if ((location.getState() == LTouchLocationState.Pressed)
								|| (location.getState() == LTouchLocationState.Dragged)) {
							this.mousePositionList
									.add(new Vector2f(location.getPosition().x,
											location.getPosition().y));
							for (Button button : this.buttonList) {
								for (Vector2f vector : this.mousePositionList) {
									if (button.IsClicked(vector)) {
										this.buttonClicked = button;
									}
								}
							}
							isTouchInputValid = true;
						}
					}
				}
				if (this.maskAlpha > 0f) {
					this.maskAlpha -= 0.1f;
				} else if (this.maskAlpha < 0f) {
					this.maskAlpha = 0f;
				}
			}
			if (this.buttonClicked != null) {
				this.buttonClicked.TransAnimation();
				if (this.buttonClicked.isTakingEffect) {
					this.isTranAnimFinished = true;
				} else {
					this.maskAlpha = (1f - this.buttonClicked.delayBeforeEffect) * 1.5f;
					if (this.maskAlpha > 1f) {
						this.maskAlpha = 1f;
					}
				}
				if (!this.buttonClicked.isInTransition) {
					this.buttonClicked = null;
					this.isTranAnimFinished = false;
				}
			}
			if (SysKey.isKeyPressed(SysKey.BACK)) {
				if (this.screenPause != null) {
					if (!this.isPaused) {
						this.isPaused = true;
						this.screenPause.setIsStillPaused(true);
					} else {
						Help.currentGameState = this.screenPause.screenWithinType;
						this.screenPause.setIsStillPaused(false);
						this.isPaused = false;
					}
				} else if (Help.currentGameState != Help.GameScreen.MainMenu) {
					Help.currentGameState = Help.GameScreen.MainMenu;
				}
			}
		}
	}
}