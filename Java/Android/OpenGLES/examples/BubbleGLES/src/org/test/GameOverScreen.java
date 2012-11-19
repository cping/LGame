package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.input.LKey;
import loon.core.input.LTouch;
import loon.core.timer.GameTime;

public class GameOverScreen extends MenuScreen {
	
	private int currentScore;
	private GameplayScreen gameScreen;
	private LTexture highscoreTexture;
	private MenuEntry pauseMenuEntry;
	private MenuEntry restartMenuEntry;
	private boolean won;

	public GameOverScreen(GameplayScreen gameScreen, boolean won,
			int currentScore) {
		super.IsPopup = true;
		super.transitionOnTime = 1.5f;
		super.transitionOffTime = 1.5f;
		this.currentScore = currentScore;
		this.gameScreen = gameScreen;
		this.won = won;
		this.pauseMenuEntry = new MenuEntry(new RectBox(180, 0, 80, 80));
		this.pauseMenuEntry.setBasePosition(new Vector2f(45f, 750f));
		this.pauseMenuEntry.Selected = new MenuEntry.SelectEvent() {

			@Override
			public void invoke(MenuEntry entry) {
				ReturnToMainMenuEntrySelected();
			}
		};
		this.pauseMenuEntry.setEntryAnimation(MenuEntryEffects.GoToBottom|MenuEntryEffects.ComeFromBottom);
		super.getMenuEntries().add(this.pauseMenuEntry);
		this.restartMenuEntry = new MenuEntry(new RectBox(180, 80, 80, 80));
		this.restartMenuEntry.setBasePosition(new Vector2f(130f, 750f));

		this.restartMenuEntry.Selected = new MenuEntry.SelectEvent() {

			@Override
			public void invoke(MenuEntry entry) {
				RestartMenuEntrySelected();
			}
		};
		this.restartMenuEntry.setEntryAnimation(MenuEntryEffects.GoToBottom|MenuEntryEffects.ComeFromBottom);
		super.getMenuEntries().add(this.restartMenuEntry);
		MenuEntry item = new MenuEntry(new RectBox(0x395, 0x1f1, 480, 700));
		item.setBasePosition(new Vector2f(240f, 350f));
		if (won) {

			item.Selected = new MenuEntry.SelectEvent() {

				@Override
				public void invoke(MenuEntry entry) {
					NextLevelMenuEntrySelected();
				}
			};
		} else {

			item.Selected = new MenuEntry.SelectEvent() {

				@Override
				public void invoke(MenuEntry entry) {
					RestartMenuEntrySelected();
				}
			};

		}
		super.getMenuEntries().add(item);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.drawableScreen
				.fadeBackBufferToBlack((super.getTransitionAlpha() * 2f) / 3f);
		super.draw(batch, gameTime);
		if (this.won) {
			if (BubbleDataManager.LevelScore(this.gameScreen.getLevelIndex()) <= this.currentScore) {
				batch.draw(
						this.highscoreTexture,
						new Vector2f(240f, 505f),
						new RectBox(620, 0x1c1, 260, 0x2b),
						LColor.white,
						0f,
						new Vector2f(120f, 21f),
						(float) (0.7f + (((float) Math.abs(Math
								.cos((gameTime.getTotalGameTime() * 3.1415926535897931) * 0.5))) * 0.1f)),
						SpriteEffects.None);
			}
		}
	}

	@Override
	public void loadContent() {
		this.highscoreTexture = LTextures.loadTexture("assets/MenuEntries.png");
	}

	private void NextLevelMenuEntrySelected() {
		this.gameScreen.NextLevel();
		this.exitScreen();
	}

	@Override
	public void onCancel() {
		super.onCancel();
		this.gameScreen.Restart();
	}

	private void RestartMenuEntrySelected() {
		this.gameScreen.Restart();
		this.exitScreen();
	}

	private void ReturnToMainMenuEntrySelected() {
		this.gameScreen.exitScreen();
		this.exitScreen();
	}

	@Override
	public void unloadContent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(GameTime elapsedTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pressed(LTouch e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void released(LTouch e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void move(LTouch e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pressed(LKey e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void released(LKey e) {
		// TODO Auto-generated method stub
		
	}
}