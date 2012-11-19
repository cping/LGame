package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.GameTime;

public class MainMenuScreen extends MenuScreen {
	private boolean easyLocked;

	private LFont fontStd;
	private MainGame game;
	private boolean hardLocked;
	private boolean mediumLocked;
	private MenuEntry startHardGameMenuEntry;
	private MenuEntry startMediumGameMenuEntry;
	private LTexture texture;
	private LTexture textureSound;
	private LTexture textureSoundOff;
	private LTexture textureSoundOn;

	public MainMenuScreen(MainGame game, ScreenType prevScreen) {
		super("", game, prevScreen);
		this.game = game;
		super.setScreenType(ScreenType.MainMenuScreen);
		Vector2f vector = new Vector2f(110f, 41f);
		MenuEntry item = new MenuEntry("");
		item.setuseButtonBackground(false);
		item.setPosition(new Vector2f(86f, 390f));
		item.setnoButtonBackgroundSize(vector);
		MenuEntry entry2 = new MenuEntry("");
		entry2.setuseButtonBackground(false);
		entry2.setPosition(new Vector2f(86f, 210f));
		entry2.setnoButtonBackgroundSize(vector);
		this.startMediumGameMenuEntry = new MenuEntry("");
		this.startMediumGameMenuEntry.setuseButtonBackground(false);
		this.startMediumGameMenuEntry.setPosition(new Vector2f(86f, 270f));
		this.startMediumGameMenuEntry.setnoButtonBackgroundSize(vector);
		this.startHardGameMenuEntry = new MenuEntry("");
		this.startHardGameMenuEntry.setuseButtonBackground(false);
		this.startHardGameMenuEntry.setPosition(new Vector2f(86f, 330f));
		this.startHardGameMenuEntry.setnoButtonBackgroundSize(vector);
		MenuEntry entry3 = new MenuEntry("");
		entry3.setuseButtonBackground(false);
		entry3.setPosition(new Vector2f(5f, 410f));
		entry3.setnoButtonBackgroundSize(new Vector2f(60f, 60f));
		MenuEntry entry4 = new MenuEntry("");
		entry4.setuseButtonBackground(false);
		entry4.setPosition(new Vector2f(230f, 20f));
		entry4.setnoButtonBackgroundSize(new Vector2f(60f, 60f));
		this.UpdateLockedDifficulties();

		item.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartInstructionsMenuEntrySelected();
			}
		};

		entry3.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				ToggleSoundEnabledSelected();

			}
		};

		entry2.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartEasyGameMenuEntrySelected();

			}
		};

		this.startMediumGameMenuEntry.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartMediumGameMenuEntrySelected();

			}
		};

		this.startHardGameMenuEntry.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartHardGameMenuEntrySelected();
			}
		};

		super.getMenuEntries().add(item);
		super.getMenuEntries().add(entry2);
		super.getMenuEntries().add(this.startMediumGameMenuEntry);
		super.getMenuEntries().add(this.startHardGameMenuEntry);
		super.getMenuEntries().add(entry3);
		super.getMenuEntries().add(entry4);
	}

	Vector2f result = new Vector2f(160f, 219f);
	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.texture, 0f, 0f, LColor.white);
		batch.draw(this.textureSound, 20f, 444f, LColor.white);

		result.set(160f, 219f);
		Utils.DrawLevelText(batch, this.fontStd, LanguageResources.getEasy()
				.toUpperCase(), this.easyLocked, result);

		result.set(160f, 279f);
		Utils.DrawLevelText(batch, this.fontStd, LanguageResources.getMedium()
				.toUpperCase(), this.mediumLocked, result);

		result.set(160f, 339f);
		Utils.DrawLevelText(batch, this.fontStd, LanguageResources.getHard()
				.toUpperCase(), this.hardLocked, result);

		Utils.DrawStringAlignCenter(batch, this.fontStd, LanguageResources
				.getInstructions().toUpperCase(), 160f, 405f, LColor.white);

		super.draw(batch, gameTime);
	}

	@Override
	public void LoadContent() {
		this.texture = LTextures
				.loadTexture("assets/backgrounds/main_menu.png");
		this.textureSoundOn = LTextures
				.loadTexture("assets/speaker_icon_on.png");
		this.textureSoundOff = LTextures
				.loadTexture("assets/speaker_icon_off.png");
		this.fontStd = LFont.getFont(12);
		this.SetSoundTexture();
	}

	public final void PreloadAssets() {
		this.PreloadTextures();
		this.PreloadSound();
	}

	private void PreloadSound() {

	}

	private void PreloadTextures() {
	}

	private void SelectLevel(Difficulty difficulty) {
		super.getScreenManager().ExitAllScreens();
		if (((difficulty == Difficulty.Medium) || (difficulty == Difficulty.Hard))) {
			super.getScreenManager().AddScreen(
					new BuyToGetFeaturesScreen(this.game,
							ScreenType.MainMenuScreen, null));
		} else {
			super.getScreenManager().AddScreen(
					new SelectLevelScreen(this.game, ScreenType.MainMenuScreen,
							difficulty));
		}
	}

	private void SetSoundTexture() {
		if (this.game.getSoundEnabled()) {
			this.textureSound = this.textureSoundOn;
		} else {
			this.textureSound = this.textureSoundOff;
		}
	}

	private void StartEasyGameMenuEntrySelected() {
		this.SelectLevel(Difficulty.Easy);
	}

	private void StartHardGameMenuEntrySelected() {
		this.SelectLevel(Difficulty.Hard);
	}

	private void StartInstructionsMenuEntrySelected() {
		super.getScreenManager().ExitAllScreens();
		super.getScreenManager().AddScreen(
				new InstructionScreen(this.game, ScreenType.MainMenuScreen));
	}

	private void StartMediumGameMenuEntrySelected() {
		this.SelectLevel(Difficulty.Medium);
	}

	private void ToggleSoundEnabledSelected() {
		this.SetSoundTexture();
	}

	@Override
	public void Update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		super.Update(gameTime, otherScreenHasFocus, coveredByOtherScreen);
		this.UpdateLockedDifficulties();
	}

	private void UpdateLockedDifficulties() {
		this.easyLocked = false;
		this.mediumLocked = false;
		this.hardLocked = false;
	}
}