package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;

public class CMenuScreen implements CScreen {
	private LTexture backGroundTexture;
	private LTexture buttonMarket;
	private RectBox buttonMarketRect;
	private MainGame mainGame;
	private CMenu menu;

	public CMenuScreen(MainGame game) {
		this.mainGame = game;
		this.buttonMarketRect = new RectBox();
	}

	public final void draw(SpriteBatch batch, LColor defaultSceneColor) {
		batch.draw(this.backGroundTexture, this.mainGame.fullScreenRect,
				defaultSceneColor);
		if (this.mainGame.isOSUI) {
			batch.draw(this.buttonMarket, this.buttonMarketRect,
					defaultSceneColor);
		}
		this.menu.draw(batch, defaultSceneColor);
	}

	public final void LoadContent() {
		this.backGroundTexture = LTextures
				.loadTexture("assets/menu/menuBack.png");
		this.buttonMarket = LTextures
				.loadTexture("assets/menu/buttons/buttonMarket.png");
		this.buttonMarketRect.x = -15;
		this.buttonMarketRect.y = 0x198;
		this.buttonMarketRect.height = this.buttonMarket.getHeight();
		this.buttonMarketRect.width = this.buttonMarket.getWidth();
		this.menu = new CMenu(this.mainGame, 4);
		this.menu.setMenuItem(0, new Vector2f(-6f, 157f),
				LTextures.loadTexture("assets/menu/buttons/buttonStart.png"),
				1.7f);
		this.menu.setMenuItem(1, new Vector2f(-12f, 309f), LTextures
				.loadTexture("assets/menu/buttons/buttonHowToPlay.png"), 1.7f);
		this.menu.setMenuItem(2, new Vector2f(-23f, 228f),
				this.mainGame.buttonStatistics, 1.7f);
		this.menu
				.setMenuItem(
						3,
						new Vector2f(715f, 10f),
						2,
						new LTexture[] {
								LTextures
										.loadTexture("assets/menu/buttons/buttonSound.png"),
								LTextures
										.loadTexture("assets\\menu\\buttons\\buttonSoundOff.png") },
						1.7f);
		this.menu.menuItem[3].value = this.mainGame.noSound ? 1 : 0;
	}

	public final void reset() {
		if (this.menu != null) {
			this.menu.reset();
		}
	}

	public final void update(float time) {
		if (!this.mainGame.currentToucheState.AnyTouch()
				&& this.mainGame.previouseToucheState.AnyTouch()) {
			Vector2f vector = this.mainGame.getCurrentTouchPos();
			if (vector.x > 625f) {
				if (vector.y > 400f) {
					this.mainGame
							.switchGameMode(MainGame.EGMODE.GMODE_COMERCIAL);
					return;
				}
			} else if (((this.mainGame.isOSUI && (vector.x > this.buttonMarketRect.x)) && ((vector.x < (this.buttonMarketRect.x + this.buttonMarketRect
					.getWidth())) && (vector.y > this.buttonMarketRect.y)))
					&& (vector.y < (this.buttonMarketRect.y + this.buttonMarketRect
							.getHeight()))) {
				return;
			}
		}
		this.menu.update(time);
		if ((this.menu.selectedItem != -1) && this.menu.ready) {
			switch (this.menu.selectedItem) {
			case 0:
				this.mainGame
						.switchGameMode(MainGame.EGMODE.GMODE_LEVELCHOOSER);
				return;

			case 1:
				this.mainGame.switchGameMode(MainGame.EGMODE.GMODE_HOWTOPLAY);
				return;

			case 2:
				this.mainGame.switchGameMode(MainGame.EGMODE.GMODE_STATISTICS);
				return;

			case 3:
				this.mainGame.noSound = !this.mainGame.noSound;
				this.mainGame.saveConfig();
				this.menu.selectedItem = -1;
				if (!this.mainGame.noSound) {
					this.mainGame.playTitleSong();
					return;
				}
				this.mainGame.stopTitleSong();
				return;

			default:
				return;
			}
		}
		if (this.mainGame.isPressedBack()) {
			LSystem.exit();
		}
	}

	public enum EMenuItems {
		EMENUITEM_STARTGAME, EMENUITEM_HOWTOPLAY, EMENUITEM_STATISTICS, EMENUITEM_NOSOUND, EMENUITEM_END;

		public int getValue() {
			return this.ordinal();
		}

		public static EMenuItems forValue(int value) {
			return values()[value];
		}
	}
}