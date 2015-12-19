package com.mygame;

import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.canvas.LColor;
import loon.font.LFont;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.timer.GameTime;

public class ScreenManager extends DrawableGameComponent {

	private LTexture buttonBackground;
	private LFont font;
	private MainGame game;

	private boolean isInitialized;
	private java.util.ArrayList<GameScreen> screens;
	private java.util.ArrayList<GameScreen> screensToUpdate;

	private boolean traceEnabled;

	public ScreenManager(MainGame game) {
		super(game);
		this.screens = new java.util.ArrayList<GameScreen>();
		this.screensToUpdate = new java.util.ArrayList<GameScreen>();
		this.game = game;
	}

	public final void AddScreen(GameScreen screen) {
		screen.setScreenManager(this);
		screen.setIsExiting(false);
		if (this.isInitialized) {
			screen.LoadContent();
		}
		this.screens.add(screen);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		for (GameScreen screen : this.screens) {
			if (screen.getScreenState() != ScreenState.Hidden) {
				screen.draw(batch, gameTime);
			}
		}
	}

	public final void ExitAllScreens() {
		for (GameScreen screen : this.GetScreens()) {
			screen.ExitScreen();
		}
	}

	public void FadeBackBufferToBlack(float a) {
		drawRectangle(LSystem.viewSize.getRect(), 0f, 0f, 0f, a);
	}

	public void drawRectangle(RectBox rect, LColor c) {
		drawRectangle(rect, c.r, c.g, c.b, c.a);
	}

	public void drawRectangle(RectBox rect, float r, float g, float b, float a) {
		GLEx gl = LSystem.base().display().GL();
		if (gl != null) {
			int color = gl.color();
			gl.setColor(r, g, b, a);
			gl.fillRect(rect.x, rect.y, rect.width, rect.height);
			gl.setColor(color);
		}
	}

	public final GameScreen[] GetScreens() {
		return this.screens.toArray(new GameScreen[0]);
	}

	@Override
	public void initialize() {
		super.initialize();
		this.isInitialized = true;
	}

	@Override
	protected void loadContent() {
		this.font = LFont.getFont(12);
		this.buttonBackground = LTextures
				.loadTexture("assets/backgrounds/buttonBackground.png");
		for (GameScreen screen : this.screens) {
			screen.LoadContent();
		}
	}

	public final void RemoveScreen(GameScreen screen) {
		if (this.isInitialized) {
			screen.UnloadContent();
		}
		this.screens.remove(screen);
		this.screensToUpdate.remove(screen);
		if (this.screens.size() > 0) {

		}
	}

	public final void SerializeState() {
	}

	private void TraceScreens() {
		java.util.ArrayList<String> list = new java.util.ArrayList<String>();
		for (GameScreen screen : this.screens) {
			list.add(screen.getClass().getName());
		}
	}

	@Override
	protected void unloadContent() {
		for (GameScreen screen : this.screens) {
			screen.UnloadContent();
		}
	}

	@Override
	public void update(GameTime gameTime) {
		this.screensToUpdate.clear();
		for (GameScreen screen : this.screens) {
			this.screensToUpdate.add(screen);
		}
		boolean otherScreenHasFocus = false;
		boolean coveredByOtherScreen = false;
		while (this.screensToUpdate.size() > 0) {
			GameScreen screen2 = this.screensToUpdate.get(this.screensToUpdate
					.size() - 1);
			this.screensToUpdate.remove(this.screensToUpdate.size() - 1);
			if (screen2.getScreenState() != ScreenState.Hidden) {
				screen2.Update(gameTime, otherScreenHasFocus,
						coveredByOtherScreen);
			}
			if ((screen2.getScreenState() == ScreenState.TransitionOn)
					|| (screen2.getScreenState() == ScreenState.Active)) {
				if (!otherScreenHasFocus) {
					screen2.HandleInput(gameTime, game);
					otherScreenHasFocus = true;
				}
				if (!screen2.getIsPopup()) {
					coveredByOtherScreen = true;
				}
			}
		}
		if (this.traceEnabled) {
			this.TraceScreens();
		}
	}

	public final LTexture getButtonBackground() {
		return this.buttonBackground;
	}

	public final LFont getFont() {
		return this.font;
	}

	public final boolean getTraceEnabled() {
		return this.traceEnabled;
	}

	public final void setTraceEnabled(boolean value) {
		this.traceEnabled = value;
	}
}