package org.test.towerdefense;

import loon.LTexture;
import loon.LTextures;
import loon.action.sprite.SpriteBatch;
import loon.canvas.LColor;
import loon.font.LFont;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;


public class SelectLevelScreen extends MenuScreen {
	private Difficulty difficulty;
	private LFont font;
	private MainGame game;
	private boolean level1Locked;
	private boolean level2Locked;
	private boolean level3Locked;
	private int remainingLivesRecordLevel1;
	private int remainingLivesRecordLevel2;
	private int remainingLivesRecordLevel3;
	private LTexture texture;
	private LTexture textureFlagGreen;
	private LTexture textureFlagRed;
	private LTexture textureHeart;

	public SelectLevelScreen(MainGame game, ScreenType prevScreen,
			Difficulty difficulty) {
		super("", game, prevScreen);
		this.level2Locked = true;
		this.level3Locked = true;
		this.remainingLivesRecordLevel1 = -1;
		this.remainingLivesRecordLevel2 = -1;
		this.remainingLivesRecordLevel3 = -1;
		this.game = game;
		super.setScreenType(ScreenType.SelectLevelScreen);
		this.difficulty = difficulty;
		Vector2f vector = new Vector2f(110f, 41f);
		MenuEntry item = new MenuEntry("");
		item.setuseButtonBackground(false);
		item.setPosition(new Vector2f(86f, 210f));
		item.setnoButtonBackgroundSize(vector);
		MenuEntry entry2 = new MenuEntry("");
		entry2.setuseButtonBackground(false);
		entry2.setPosition(new Vector2f(86f, 270f));
		entry2.setnoButtonBackgroundSize(vector);
		MenuEntry entry3 = new MenuEntry("");
		entry3.setuseButtonBackground(false);
		entry3.setPosition(new Vector2f(86f, 330f));
		entry3.setnoButtonBackgroundSize(vector);
		MenuEntry entry4 = new MenuEntry("");
		entry4.setuseButtonBackground(false);
		entry4.setPosition(new Vector2f(86f, 390f));
		entry4.setnoButtonBackgroundSize(vector);
		super.getMenuEntries().add(item);
		super.getMenuEntries().add(entry2);
		super.getMenuEntries().add(entry3);
		super.getMenuEntries().add(entry4);
		for (CompletedLevel level : game.getCompletedLevels()) {
			if (level.getDifficulty() == difficulty.getValue()) {
				if (level.getLevel() == 1) {
					this.remainingLivesRecordLevel1 = level.getRemainingLives();
				} else {
					if (level.getLevel() == 2) {
						this.remainingLivesRecordLevel2 = level
								.getRemainingLives();
						continue;
					}
					if (level.getLevel() == 3) {
						this.remainingLivesRecordLevel3 = level
								.getRemainingLives();
					}
				}
			}
		}

		item.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartLevel1MenuEntrySelected();

			}
		};

		entry2.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartLevel2MenuEntrySelected();

			}
		};

		entry3.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartLevel3MenuEntrySelected();

			}
		};

		entry4.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartMainMenuEntrySelected();

			}
		};
	}

	Vector2f result = new Vector2f(160f, 219f);

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.texture, 0f, 0f, LColor.white);

		result.set(160f, 219f);
		Utils.DrawLevelText(batch, this.font,
				LanguageResources.getLevel1Title(), this.level1Locked, result);

		result.set(160f, 279f);
		Utils.DrawLevelText(batch, this.font,
				LanguageResources.getLevel2Title(), this.level2Locked, result);

		result.set(160f, 339f);
		Utils.DrawLevelText(batch, this.font,
				LanguageResources.getLevel3Title(), this.level3Locked, result);

		result.set(160f, 402f);
		Utils.DrawStringAlignCenter(batch, this.font, LanguageResources
				.getMainMenu().toUpperCase(), result, LColor.white);

		this.DrawBestRemainingLives(batch, this.remainingLivesRecordLevel1,
				244f, 235f, this.font);
		this.DrawBestRemainingLives(batch, this.remainingLivesRecordLevel2,
				244f, 295f, this.font);
		this.DrawBestRemainingLives(batch, this.remainingLivesRecordLevel3,
				244f, 355f, this.font);
		super.draw(batch, gameTime);
	}

	private void DrawBestRemainingLives(SpriteBatch batch,
			int remainingLivesRecord, float x, float y, LFont font) {
		if (remainingLivesRecord >= 0) {
			batch.draw(this.textureFlagGreen, x, y, LColor.white);
			Utils.DrawStringAlignRight(batch, font, (new Integer(
					remainingLivesRecord)).toString() + "/" + 20, new Vector2f(
					83f, y), LColor.white);
			batch.draw(this.textureHeart, 84f, y + 3f, LColor.white);
		} else {
			batch.draw(this.textureFlagRed, x, y, LColor.white);
		}
	}

	@Override
	public void LoadContent() {
		this.texture = LTextures
				.loadTexture("assets/backgrounds/select_level_menu.png");
		this.textureHeart = LTextures.loadTexture("assets/icon_heart.png");
		this.textureFlagGreen = LTextures
				.loadTexture("assets/icon_flag_green.png");
		this.textureFlagRed = LTextures.loadTexture("assets/icon_flag_red.png");
		this.font = LFont.getFont(12);
	}

	private void StartGame(int level) {
		super.getScreenManager().ExitAllScreens();
		super.getScreenManager().AddScreen(
				new GameplayScreen(this.game, this.difficulty, level));
	}

	private void StartLevel1MenuEntrySelected() {
		this.StartGame(1);
	}

	private void StartLevel2MenuEntrySelected() {

		this.StartGame(2);

	}

	private void StartLevel3MenuEntrySelected() {

		this.StartGame(3);

	}

	private void StartMainMenuEntrySelected() {
		super.getScreenManager().ExitAllScreens();
		super.getScreenManager().AddScreen(
				new MainMenuScreen(this.game, ScreenType.SelectLevelScreen));
	}

	@Override
	public void Update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		super.Update(gameTime, otherScreenHasFocus, coveredByOtherScreen);

		this.level2Locked = false;
		this.level3Locked = false;

	}
}