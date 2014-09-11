package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.DrawableScreen;
import loon.action.sprite.painting.DrawableState;
import loon.core.LSystem;
import loon.core.graphics.LColor;
import loon.core.timer.GameTime;

public class EntityManager extends DrawableGameComponent {

	private GameContent gameContent;

	private boolean isInitialized;
	private java.util.ArrayList<GameEntity> screens;
	private java.util.ArrayList<GameEntity> screensToUpdate;

	private boolean traceEnabled;

	public EntityManager(DrawableScreen game) {
		super(game);
		this.screens = new java.util.ArrayList<GameEntity>();
		this.screensToUpdate = new java.util.ArrayList<GameEntity>();
	}

	public final void AddScreen(GameEntity screen) {

		screen.setScreenManager(this);
		screen.setIsExiting(false);
		if (this.isInitialized) {
			screen.LoadContent();
		}
		this.screens.add(screen);

	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		for (GameEntity screen : this.screens) {
			if (screen.getScreenState() != DrawableState.Hidden) {
				screen.Draw(batch, gameTime);
			}
		}
	}

	public final void FadeBackBufferToBlack(SpriteBatch batch, float alpha) {
		batch.draw(this.getGameContent().blank, LSystem.screenRect, new LColor(
		1f, 1f, 1f, alpha));
	}

	public final GameEntity[] GetScreens() {
		return this.screens.toArray(new GameEntity[0]);
	}

	@Override
	public void initialize() {
		super.initialize();
		this.isInitialized = true;
	}

	@Override
	public void loadContent() {
		for (GameEntity screen : this.screens) {
			screen.LoadContent();
		}
	}

	public final void LoadGameContent() {
		if (this.gameContent == null) {
			this.gameContent = new GameContent(this);
		}
	}

	public final void RemoveScreen(GameEntity screen) {
		if (this.isInitialized) {
			screen.UnloadContent();
		}
		this.screens.remove(screen);
		this.screensToUpdate.remove(screen);
	}

	private void TraceScreens() {
		java.util.ArrayList<String> list = new java.util.ArrayList<String>();
		for (GameEntity screen : this.screens) {
			list.add(screen.getClass().getName());
		}
	}

	@Override
	public void unloadContent() {
		for (GameEntity screen : this.screens) {
			screen.UnloadContent();
		}
		if (this.gameContent != null) {
			this.gameContent.UnloadContent();
		}
	}

	@Override
	public void update(GameTime gameTime) {

		this.screensToUpdate.clear();
		for (GameEntity screen : this.screens) {
			this.screensToUpdate.add(screen);
		}
	
		boolean coveredByOtherScreen = false;
		while (this.screensToUpdate.size() > 0) {
			GameEntity screen2 = this.screensToUpdate.get(this.screensToUpdate
					.size() - 1);
			this.screensToUpdate.remove(this.screensToUpdate.size() - 1);
			screen2.Update(gameTime, coveredByOtherScreen);
			if ((screen2.getScreenState() == DrawableState.TransitionOn)
					|| (screen2.getScreenState() == DrawableState.Active)) {

				screen2.HandleInput();

				if (!screen2.getIsPopup()) {
					coveredByOtherScreen = true;
				}
			}
		}
		if (this.traceEnabled) {
			this.TraceScreens();
		}
	}

	public final GameContent getGameContent() {
		return this.gameContent;
	}

	public final boolean getTraceEnabled() {
		return this.traceEnabled;
	}

	public final void setTraceEnabled(boolean value) {
		this.traceEnabled = value;
	}
}