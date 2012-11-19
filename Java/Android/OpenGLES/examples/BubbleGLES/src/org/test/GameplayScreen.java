package org.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.test.MenuEntry.SelectEvent;

import loon.action.sprite.SpriteBatch;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.input.LInputFactory.Touch;
import loon.core.input.LKey;
import loon.core.input.LTouch;
import loon.core.resource.Resources;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;

public class GameplayScreen extends MenuScreen {
	private LTexture background;
	private java.util.ArrayList<Ball> ballList;

	private Canone canone;
	private GameGrit gameGrit;
	private boolean gameOver = false;

	private int LevelIndex = 0;

	public static BubbleParticleEngine particleEngine;
	private MenuEntry pauseMenuEntry;

	private Vector2f PinchEffectCenter = new Vector2f(240f, 800f);

	private float playedTime = 0f;
	private MenuEntry restartMenuEntry;
	private float restartTimer = 1f;

	private int shotballs = 0;

	public GameplayScreen(int levelIndex) {
		super.transitionOnTime = 1.5f;
		super.transitionOffTime = 1.5f;
		this.pauseMenuEntry = new MenuEntry(new RectBox(180, 0, 80, 80));
		this.pauseMenuEntry.setBasePosition(new Vector2f(45f, 750f));
		this.pauseMenuEntry.setEntryAnimation(MenuEntryEffects.GoToBottom
				| MenuEntryEffects.ComeFromBottom);
		this.pauseMenuEntry.Selected = new SelectEvent() {

			@Override
			public void invoke(MenuEntry entry) {
				PauseMenuEntrySelected();

			}
		};
		super.getMenuEntries().add(this.pauseMenuEntry);
		this.restartMenuEntry = new MenuEntry(new RectBox(180, 80, 80, 80));
		this.restartMenuEntry.setBasePosition(new Vector2f(130f, 750f));
		this.restartMenuEntry.setEntryAnimation(MenuEntryEffects.GoToBottom
				| MenuEntryEffects.ComeFromBottom);

		this.restartMenuEntry.Selected = new SelectEvent() {

			@Override
			public void invoke(MenuEntry entry) {
				RestartMenuEntrySelected();

			}
		};
		super.getMenuEntries().add(this.restartMenuEntry);
		particleEngine = new BubbleParticleEngine();
		this.LevelIndex = levelIndex;
		Ball.game = this;
	}

	public final void AddBombEffect(Vector2f position) {
		this.PinchEffectCenter.set(position);
	}

	private void CheckGameOver(float PassedTime) {
	}

	private void DelayInput() {
		this.restartTimer = 1f;
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.background, 0, 0, LColor.white);
		this.gameGrit.Draw(gameTime, batch);
		for (int i = 0; i < this.ballList.size(); i++) {
			this.ballList.get(i).Draw(gameTime, batch);
		}
		this.canone.Draw(gameTime, batch);
		particleEngine.Draw(batch);
		super.draw(batch, gameTime);
	}

	public final int getLevelIndex() {
		return this.LevelIndex;
	}

	@Override
	public void loadContent() {
		this.canone = new Canone();
		this.ballList = new java.util.ArrayList<Ball>();
		this.gameGrit = new GameGrit();
		this.background = LTextures.loadTexture("assets/Background.png");
		this.Restart();
		this.SetupLevel();

		Ball.LoadSounds();
	}

	public final void NextLevel() {
		this.LevelIndex++;
		if (this.LevelIndex >= BubbleDataManager.levels) {
			this.LevelIndex = 0;
		}
		this.Restart();
	}

	@Override
	public void onCancel() {
		MessageScreen screen = new MessageScreen(Strings.getReturnToMain(),
				false, true);
	
		screen.Accepted = new MessageScreen.EventHandler() {
			public void invoke(MessageScreen screen) {
				QuitLevelSelected();
			}
		};

		screen.Cancelled = new MessageScreen.EventHandler() {
			public void invoke(MessageScreen screen) {
				DelayInput();
			}
		};

		super.drawableScreen.addDrawable(screen);
	}

	private void PauseMenuEntrySelected() {
		this.restartTimer = 1f;
		this.onCancel();
	}

	private void QuitLevelSelected() {
		this.exitScreen();
	}

	public void Restart() {
		this.restartTimer = 1f;
		this.gameOver = false;
		this.playedTime = 0f;
		this.shotballs = 0;
		this.canone.Reset();
		particleEngine.Reset();
		this.ballList.clear();
		this.gameGrit.Reset(this.restartTimer);
		this.SetupLevel();
	}

	private void RestartMenuEntrySelected() {
		this.Restart();
	}

	protected final void SetupLevel() {
		BufferedReader reader = null;
		try {
			int num = 0;
			reader = new BufferedReader(new InputStreamReader(
					Resources.openResource("assets/levels.txt"),
					LSystem.encoding));

			String str;
			while ((str = reader.readLine()) != null) {
				if (num != this.LevelIndex) {
					num++;
				} else {
					for (int i = 0; i < str.length(); i++) {
						Ball ball = null;
						switch (str.charAt(i)) {
						case '*':
							ball = Ball.CreateBall(Vector2f.Zero,
									Ball.JokerColor);
							break;

						case '0':
							ball = Ball.CreateBall(Vector2f.Zero,
									Ball.YellowColor);
							break;

						case '1':
							ball = Ball
									.CreateBall(Vector2f.Zero, Ball.RedColor);
							break;

						case '2':
							ball = Ball.CreateBall(Vector2f.Zero,
									Ball.BlueColor);
							break;

						case '3':
							ball = Ball.CreateBall(Vector2f.Zero,
									Ball.GreenColor);
							break;

						case '4':
							ball = Ball.CreateBall(Vector2f.Zero,
									Ball.WhiteColor);
							break;

						case '5':
							ball = Ball.CreateBall(Vector2f.Zero,
									Ball.PinkColor);
							break;

						case '6':
							ball = Ball.CreateBall(Vector2f.Zero,
									Ball.TealColor);
							break;

						case '7':
							ball = Ball.CreateBall(Vector2f.Zero,
									Ball.BrownColor);
							break;

						case '#':
							ball = Ball.CreateBall(Vector2f.Zero,
									Ball.BombColor);
							break;
						}
						if (ball != null) {
							this.gameGrit.AddBall(i % 9, i / 9, ball);
						}
					}
					this.canone.SetSpawnableColors(this.gameGrit
							.getColorsInGrit());
					return;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		super.update(gameTime, otherScreenHasFocus, coveredByOtherScreen);
		if (!this.gameOver) {
			this.playedTime += (float) gameTime.getElapsedGameTime();
		}
		this.restartTimer -= (float) gameTime.getElapsedGameTime();
		this.UpdateGameObjects(gameTime);
		particleEngine.Update(gameTime);
		this.CheckGameOver((float) gameTime.getElapsedGameTime());
	}

	private void UpdateGameObjects(GameTime gameTime) {
		this.canone.setPosition(240f, 750f + (200f * super
				.getTransitionPosition()));
		if (super.isExiting()) {
			this.gameGrit.SetPosition(this.gameGrit.GetPosition().sub(0f,
					800f * super.getTransitionPosition()));
			if (super.getTransitionAlpha() <= 0f) {

			}
		}
		this.gameGrit.update(gameTime);
		this.canone.update(gameTime);
		for (int i = 0; i < this.ballList.size(); i++) {
			this.ballList.get(i).update(gameTime);
			if (this.ballList.get(i).Removable()
					|| this.gameGrit.CheckBallCollision(this.ballList.get(i))) {

				this.canone.SetSpawnableColors(this.gameGrit.getColorsInGrit());
				this.ballList.remove(i);
				i--;
				if (this.gameGrit.IsGameOver()) {

					super.drawableScreen.addDrawable(new GameOverScreen(this,
							false, 0));
					this.gameOver = true;
					BubbleDataManager.games++;
					BubbleDataManager.UploadScore(this.LevelIndex, false, 0);
				}
				if (this.gameGrit.IsWon()) {
					BubbleDataManager.games++;
					this.gameOver = true;
					BubbleDataManager.WonGame(
							this.LevelIndex,
							MathUtils.max(100, (0x2710 - (100 * this.shotballs))
									- (((int) this.playedTime) * 2)));

					super.drawableScreen.addDrawable(new GameOverScreen(this,
							true, MathUtils.max(100,
									(0x2710 - (100 * this.shotballs))
											- (((int) this.playedTime) * 2))));
					if (((gameTime.getTotalGameTime() > BubbleDataManager.askRatingAfterMinute) && (BubbleDataManager.games > BubbleDataManager.showRateAfterGame))
							&& BubbleDataManager.askForRating) {
						super.drawableScreen.addDrawable(new RateMeScreen());
						BubbleDataManager.askRatingAfterMinute *= 3;
						BubbleDataManager.showRateAfterGame *= 3;
					}
					BubbleDataManager.UploadScore(
							this.LevelIndex,
							true,
							Math.max(100, (0x2710 - (100 * this.shotballs))
									- (((int) this.playedTime) * 2)));
				}
			}
		}
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
		if ((super.getTransitionAlpha() > 0.9) && (this.restartTimer <= 0f)) {

			this.canone.AimToPosition(Touch.getLocation());

		}

	}

	@Override
	public void released(LTouch e) {
		if ((super.getTransitionAlpha() > 0.9) && (this.restartTimer <= 0f)) {

			if (this.ballList.isEmpty()) {
				this.canone.Fire(Touch.getLocation().cpy(), this.ballList);
				this.shotballs++;
			}

		}
	}

	@Override
	public void move(LTouch e) {
	/*	if ((super.getTransitionAlpha() > 0.9) && (this.restartTimer <= 0f)) {
			this.canone.AimToPosition(Touch.getLocation());
		}*/
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