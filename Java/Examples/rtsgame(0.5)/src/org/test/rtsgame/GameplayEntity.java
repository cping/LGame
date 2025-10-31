package org.test.rtsgame;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableEvent;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.timer.GameTime;

public class GameplayEntity extends GameEntity {

	private GameContent gameContent;
	private RoleMoveControl level;
	public static int levelIndex;
	private MessageBoxEntity m;
	private int prevScore;
	private float tempScore;

	public GameplayEntity() {
		super.setTransitionOnTime(1.5f);
		super.setTransitionOffTime(0.2f);
	}

	@Override
	public void Draw(SpriteBatch batch, GameTime gameTime) {
		this.level.Draw(batch, gameTime);
		this.DrawScore(batch, gameTime);
		if (super.getTransitionPosition() > 0f) {
			super.getScreenManager().FadeBackBufferToBlack(batch, 1f - super.getTransitionAlpha());
		}
	}

	private void DrawScore(SpriteBatch spriteBatch, GameTime gameTime) {
		float num = gameTime.getElapsedGameTime() * 50f;
		if (this.tempScore < this.getScore()) {
			this.tempScore = MathUtils.min(this.getScore(), this.tempScore + num);
		} else if (this.tempScore > this.getScore()) {
			this.tempScore = MathUtils.max(this.getScore(), this.tempScore - num);
		}
		spriteBatch.drawString(this.gameContent.gameFont,
				StringUtils.concat(new Object[] { "level: ", levelIndex, " / ", 15 }), new Vector2f(192f, 0f),
				LColor.white, 0f, Vector2f.STATIC_ZERO, (float) 0.75f);
	}

	@Override
	public void HandleInput() {

		this.level.HandleInput();

	}

	@Override
	public void LoadContent() {
		this.gameContent = super.getScreenManager().getGameContent();
		levelIndex = MainGame.ScoreData.Level;
		this.tempScore = this.prevScore = MainGame.ScoreData.Score;
		this.LoadNextLevel();
	}

	private void LoadNextLevel() {
		if (this.level != null) {
			this.prevScore += this.level.getScore();
			this.tempScore = this.prevScore;
			this.level.close();
		}
		this.level = new RoleMoveControl(super.getScreenManager(), levelIndex);
		levelIndex++;
		this.m = null;
	}

	private void MessageBoxAccepted() {
		if (this.level.getIsLevelUp()) {
			if (levelIndex == 15) {
				LoadingEntity.Load(super.getScreenManager(), false,
						new GameEntity[] { new BackgroundEntity(), new MainMenuEntity() });
			} else {
				this.LoadNextLevel();
				super.getScreenManager().AddScreen(new PauseMenuEntity());
			}
		} else if (this.level.getReloadLevel()) {
			this.ReloadCurrentLevel();
		}
	}

	private void ReloadCurrentLevel() {
		levelIndex--;
		this.LoadNextLevel();
	}

	@Override
	public void Update(GameTime gameTime, boolean coveredByOtherScreen) {
		super.Update(gameTime, coveredByOtherScreen);
		if (this.m == null) {
			if (this.level.getIsLevelUp()) {
				this.m = new MessageBoxEntity(this.gameContent.levelUp, null);
				MainGame.ScoreData.Level = levelIndex;
				if (levelIndex == 15) {
					this.m = new MessageBoxEntity(this.gameContent.gameOver, null);
					MainGame.ScoreData.Level = 0;
				}

				this.m.Accepted = new DrawableEvent() {

					@Override
					public void invoke() {
						MessageBoxAccepted();

					}
				};
				super.getScreenManager().AddScreen(this.m);
			} else if (this.level.getReloadLevel()) {
				this.m = new MessageBoxEntity(this.gameContent.retry, null);
				this.m.Accepted = new DrawableEvent() {

					@Override
					public void invoke() {
						MessageBoxAccepted();
					}
				};
				super.getScreenManager().AddScreen(this.m);
			}
		}
		if (super.getIsActive()) {
			this.level.Update(gameTime);
		}
	}

	public final int getScore() {
		return (this.prevScore + this.level.getScore());
	}
}