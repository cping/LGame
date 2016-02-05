package org.test;

import loon.LTexture;
import loon.LTextures;
import loon.action.sprite.SpriteBatch;
import loon.canvas.LColor;

public class CHelpScreen implements CScreen {
	private int currentHowToPlayScreenIndex;
	private float howToPlayAlpha;
	private int howToPlayScreens;
	private LTexture[] howToPlayTextures;
	private MainGame mainGame;

	public CHelpScreen(MainGame mGame) {
		this.mainGame = mGame;
		this.howToPlayScreens = 2;
		this.howToPlayAlpha = 0f;
		this.currentHowToPlayScreenIndex = 0;
	}

	public final void draw(SpriteBatch batch, LColor defaultSceneColor) {
		LColor color = new LColor(defaultSceneColor);
		batch.draw(this.howToPlayTextures[this.currentHowToPlayScreenIndex],
				this.mainGame.fullScreenRect, defaultSceneColor);
		int v = (int) (this.howToPlayAlpha * 255f);
		if (v > defaultSceneColor.getAlpha()) {
			v = defaultSceneColor.getAlpha();
		}
		color.setColor(defaultSceneColor.getRed(),
				defaultSceneColor.getGreen(), defaultSceneColor.getBlue(), v);
		if (this.currentHowToPlayScreenIndex == 1) {
			batch.draw(this.howToPlayTextures[0], this.mainGame.fullScreenRect,
					color);
		}
	}

	public final void LoadContent() {
		this.howToPlayTextures = new LTexture[this.howToPlayScreens];
		this.howToPlayTextures[0] = LTextures
				.loadTexture("BackgroundFamily.png");
		this.howToPlayTextures[1] = LTextures
				.loadTexture("HowToPlay.png");
	}

	public final void reset() {
		this.howToPlayAlpha = 1f;
		this.currentHowToPlayScreenIndex = 0;
	}

	public final void update(float time) {
		if (this.howToPlayAlpha > 0f) {
			this.howToPlayAlpha -= time * 2f;
		}
		if (this.howToPlayAlpha < 0f) {
			this.howToPlayAlpha = 0f;
		}
		if (this.mainGame.currentToucheState.AnyTouch()
				&& !this.mainGame.previouseToucheState.AnyTouch()) {
			this.currentHowToPlayScreenIndex++;
			this.howToPlayAlpha = 1f;
			if (this.currentHowToPlayScreenIndex > (this.howToPlayScreens - 1)) {
				this.howToPlayAlpha = 0f;
				this.currentHowToPlayScreenIndex = 1;
				this.mainGame.switchGameMode(MainGame.EGMODE.GMODE_MENU);
			}
		}
		if (this.mainGame.isPressedBack()) {
			this.howToPlayAlpha = 0f;
			this.mainGame.switchGameMode(MainGame.EGMODE.GMODE_MENU);
		}
	}
}