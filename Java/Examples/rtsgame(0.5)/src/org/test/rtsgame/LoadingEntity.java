package org.test.rtsgame;

import loon.LSystem;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableState;
import loon.canvas.LColor;
import loon.font.FontUtils;
import loon.font.IFont;
import loon.font.Text;
import loon.geom.PointF;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

//初始加载用类
public class LoadingEntity extends GameEntity {

	private boolean loadingIsSlow;
	private boolean otherScreensAreGone;
	private GameEntity[] screensToLoad;

	private LoadingEntity(EntityManager screenManager, boolean loadingIsSlow, GameEntity[] screensToLoad) {
		this.loadingIsSlow = loadingIsSlow;
		this.screensToLoad = screensToLoad;

	}

	@Override
	public void Draw(SpriteBatch batch, GameTime gameTime) {
		if ((super.getScreenState() == DrawableState.Active) && (super.getScreenManager().GetScreens().length == 1)) {
			this.otherScreensAreGone = true;
		}
		if (this.loadingIsSlow) {
			PointF point = FontUtils.getTextWidthAndHeight(batch.getFont(), "LOADING...");
			batch.drawString("LOADING...", LSystem.viewSize.width / 2 - point.x / 2,
					LSystem.viewSize.height / 2 - point.y / 2, LColor.white);
		}
	}

	public static void Load(EntityManager screenManager, boolean loadingIsSlow, GameEntity... screensToLoad) {
		for (GameEntity screen : screenManager.GetScreens()) {
			screen.ExitScreen();
		}
		LoadingEntity screen2 = new LoadingEntity(screenManager, loadingIsSlow, screensToLoad);
		screenManager.AddScreen(screen2);
	}

	@Override
	public void LoadContent() {

	}

	@Override
	public void Update(GameTime gameTime, boolean coveredByOtherScreen) {
		super.Update(gameTime, coveredByOtherScreen);
		if (this.otherScreensAreGone) {
			super.getScreenManager().LoadGameContent();
			super.ExitScreen();
			if (super.getScreenManager().GetScreens().length == 0) {
				for (GameEntity screen : this.screensToLoad) {
					if (screen != null) {
						super.getScreenManager().AddScreen(screen);
					}
				}
			}
		}
	}
}