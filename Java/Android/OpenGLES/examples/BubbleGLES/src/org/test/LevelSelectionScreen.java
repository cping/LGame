package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.input.LInput;
import loon.core.input.LInputFactory.Touch;
import loon.core.input.LKey;
import loon.core.input.LTouch;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;

public class LevelSelectionScreen extends MenuScreen {
	private boolean enableAutoScroll = true;
	private java.util.ArrayList<Vector2f> entryPosition;
	private RectBox featuredAppLink;
	private int lastPage = 1;
	protected int levelPages;
	private LTexture pageNumberTexture;
	private float pageNumberY = 680f;
	private int scrollGoal = 1;
	protected float scrollProcess = 1f;

	public LevelSelectionScreen(int levels,
			java.util.ArrayList<Vector2f> entryPosition) {
		this.levelPages = levels / entryPosition.size();
		super.transitionOffTime = 0.5f;
		super.transitionOnTime = 0.5f;
		this.entryPosition = entryPosition;
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		int pageIndex = (int) Math.round((double) this.scrollProcess);
		float num2 = Math.abs((float) (this.scrollProcess - pageIndex));
		this.DrawPage(batch, gameTime, this.scrollProcess - pageIndex,
				pageIndex - 1);
		if (this.scrollProcess > pageIndex) {
			this.DrawPage(batch, gameTime,
					(this.scrollProcess - pageIndex) - 1f, pageIndex);
		}
		if (this.scrollProcess < pageIndex) {
			this.DrawPage(batch, gameTime,
					(this.scrollProcess - pageIndex) + 1f, pageIndex - 2);
		}
		batch.draw(this.pageNumberTexture, new Vector2f(240f, this.pageNumberY
				+ (800f * (1f - super.getTransitionAlpha()))), new RectBox(0,
				0x12, 14 * (this.levelPages + 2), 10), new LColor(0.35f, 0.35f,
				0.35f, 0.35f), 0f, new Vector2f(
				(float) (7 * (this.levelPages + 1)), 10f), new Vector2f(1f),
				SpriteEffects.None);
		batch.draw(this.pageNumberTexture, new Vector2f(
				247f - (14f * (((this.levelPages + 1) * 0.5f) - pageIndex)),
				this.pageNumberY + (800f * (1f - super.getTransitionAlpha()))),
				new RectBox(14 + ((pageIndex - 1) * 14), 0, 14, 0x1c),
				LColor.white, 0f, new Vector2f(7f, 26.5f), new Vector2f(
						1.5f - (0.5f * num2)), SpriteEffects.None);

	}

	public void DrawFirstPage(GameTime gameTime, Vector2f pageCenter,
			SpriteBatch spriteBatch) {
	}

	public void DrawLastPage(GameTime gameTime, Vector2f pageCenter,
			SpriteBatch spriteBatch) {
	}

	public void DrawLevelEntry(GameTime gameTime, int level,
			Vector2f entryCenter, SpriteBatch spriteBatch) {
	}

	private void DrawPage(SpriteBatch batch, GameTime gameTime,
			float scrollOffset, int pageIndex) {
		if (pageIndex == -1) {
			this.DrawFirstPage(gameTime,
					new Vector2f(240f - (480f * scrollOffset),
							380f + (800f * (1f - super.getTransitionAlpha()))),
					batch);
		} else if (pageIndex == this.levelPages) {
			this.DrawLastPage(gameTime,
					new Vector2f(240f - (480f * scrollOffset),
							380f + (800f * (1f - super.getTransitionAlpha()))),
					batch);
		} else {
			for (int i = 0; i < this.entryPosition.size(); i++) {
				this.DrawLevelEntry(
						gameTime,
						i + (pageIndex * this.entryPosition.size()),
						this.entryPosition.get(i).add(-480f * scrollOffset,
								800f * (1f - super.getTransitionAlpha())),
						batch);
			}
		}
	}

	@Override
	public void loadContent() {
		this.pageNumberTexture = LTextures
				.loadTexture("assets/PageNumbers.png");
	}

	public final void SetPagepositionY(float y) {
		this.pageNumberY = y;
	}

	private void ShowFeaturedApp() {

	}

	public void StartLevel(int level) {
	}

	@Override
	public void update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		super.update(gameTime, otherScreenHasFocus, coveredByOtherScreen);
		if (this.enableAutoScroll) {
			if ((this.scrollGoal == this.lastPage)
					&& (this.scrollProcess != this.lastPage)) {
				if (this.scrollProcess > this.lastPage) {
					this.scrollProcess -= ((float) gameTime
							.getElapsedGameTime()) * 4f;
					if (this.scrollProcess < this.lastPage) {
						this.scrollProcess = this.lastPage;
					}
				} else {
					this.scrollProcess += ((float) gameTime
							.getElapsedGameTime()) * 4f;
					if (this.scrollProcess > this.lastPage) {
						this.scrollProcess = this.lastPage;
					}
				}
			}
			if ((this.scrollGoal != this.lastPage)
					&& (this.scrollProcess != this.scrollGoal)) {
				if (this.scrollProcess > this.scrollGoal) {
					this.scrollProcess -= ((float) gameTime
							.getElapsedGameTime()) * 2f;
					if (this.scrollProcess < this.scrollGoal) {
						this.scrollProcess = this.scrollGoal;
						this.lastPage = this.scrollGoal;
					}
				} else {
					this.scrollProcess += ((float) gameTime
							.getElapsedGameTime()) * 2f;
					if (this.scrollProcess > this.scrollGoal) {
						this.scrollProcess = this.scrollGoal;
						this.lastPage = this.scrollGoal;
					}
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


	}
	
	public void handleInput(LInput input){
		super.handleInput(input);

		if ((this.scrollProcess <= this.levelPages)
				&& (this.scrollProcess >= 1f)) {

			for (int i = 0; i < this.entryPosition.size(); i++) {
				if ((((Touch.x() > (this.entryPosition.get(i).x() - 50f)) && (Touch.x() < (this.entryPosition
						.get(i).x() + 50f))) && (Touch.y() > (this.entryPosition
						.get(i).y() - 50f)))
						&& (Touch.y() < (this.entryPosition.get(i).y + 50f))) {
					if (this.scrollProcess == ((int) MathUtils
							.round(this.scrollProcess))) {
						this.StartLevel(i
								+ ((((int) MathUtils.round(this.scrollProcess)) - 1) * this.entryPosition
										.size()));
					}
					break;
				}
			}
		} else if (this.scrollProcess > this.levelPages) {

			if ((((Touch.x() > this.featuredAppLink.x) && (Touch.x() < (this.featuredAppLink.x + this.featuredAppLink
					.getWidth()))) && (Touch.y() > this.featuredAppLink.y()))
					&& (Touch.y() < (this.featuredAppLink.y() + this.featuredAppLink
							.getHeight()))) {
				this.ShowFeaturedApp();

			}
		} else if (this.scrollProcess < 1f) {

			if ((((Touch.x() > this.featuredAppLink.x) && (Touch.x() < (this.featuredAppLink.x + this.featuredAppLink
					.getWidth()))) && (Touch.y() > this.featuredAppLink.y()))
					&& (Touch.y() < (this.featuredAppLink.y() + this.featuredAppLink
							.getHeight()))) {
				this.ShowFeaturedApp();

			}
		}
	}

	@Override
	public void released(LTouch e) {

	}

	@Override
	public void move(LTouch e) {

	}

	@Override
	public void pressed(LKey e) {

	}

	@Override
	public void released(LKey e) {

	}
}