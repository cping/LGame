package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;

public class MenuEntry {

	private boolean alternativTexture = false;

	private LTexture alternativTexture2D;

	private Vector2f basePosition = new Vector2f();

	private static LTexture basicTexture;

	private LColor color = LColor.white;

	private Vector2f currentPosition = new Vector2f();

	private int entryAnimation = 0;

	private Vector2f movementDirection= new Vector2f(LSystem.screenRect.width,
			LSystem.screenRect.height);

	private float scale;

	public SelectEvent Selected;

	private RectBox source = new RectBox();

	public MenuEntry() {
	
	}

	public static interface SelectEvent {

		public void invoke(MenuEntry entry);

	}

	protected void OnSelectEntry() {
		if (this.Selected != null) {
			this.Selected.invoke(this);
		}
	}

	public MenuEntry(RectBox source) {
		this.setSource(source);
	}

	public final void ChangeTexture(LTexture texture) {
		this.alternativTexture = true;
		this.alternativTexture2D = texture;
	}

	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.alternativTexture ? this.alternativTexture2D
				: basicTexture, this.currentPosition, this.source, this.color,
				0f, this.getWidth() / 2, this.getHeight() / 2, this.scale,
				SpriteEffects.None);
	}

	public void update(GameTime gameTime, float transitionAlpha, boolean exiting) {
		this.currentPosition.set(this.basePosition);
		this.scale = 1f;
		this.color = LColor.white;

		if (transitionAlpha != 1f) {
			if ((this.entryAnimation & MenuEntryEffects.Shrink) == MenuEntryEffects.Shrink) {
				if (exiting) {
					this.scale = MathUtils.pow(transitionAlpha, 2.0f);
				} else {
					this.scale = 1.2f * (MathUtils
							.sin((transitionAlpha * 0.68658f) * 3.1415926535897931f));
				}
			}
			if ((this.entryAnimation & MenuEntryEffects.FadeInAndOut) == MenuEntryEffects.FadeInAndOut) {
				this.color = new LColor(1f * transitionAlpha,
						1f * transitionAlpha, 1f * transitionAlpha,
						1f * transitionAlpha);
			}
			if (exiting) {
				if ((this.entryAnimation & MenuEntryEffects.ComeFromBottom) == MenuEntryEffects.ComeFromBottom) {
					this.currentPosition.y += movementDirection.y
							* (1f - transitionAlpha);
				}
				if ((this.entryAnimation & MenuEntryEffects.ComeFromTop) == MenuEntryEffects.ComeFromTop) {
					this.currentPosition.y -= movementDirection.y
							* (1f - transitionAlpha);
				}
				if ((this.entryAnimation & MenuEntryEffects.ComeFromRight) == MenuEntryEffects.ComeFromRight) {
					this.currentPosition.x += movementDirection.x
							* (1f - transitionAlpha);
				}
				if ((this.entryAnimation & MenuEntryEffects.ComeFromLeft) == MenuEntryEffects.ComeFromLeft) {
					this.currentPosition.x -= movementDirection.x
							* (1f - transitionAlpha);
				}
			} else {
				if ((this.entryAnimation & MenuEntryEffects.GoToBottom) == MenuEntryEffects.GoToBottom) {
					this.currentPosition.y += movementDirection.y
							* (1f - transitionAlpha);
				}
				if ((this.entryAnimation & MenuEntryEffects.GoToTop) == MenuEntryEffects.GoToTop) {
					this.currentPosition.y -= movementDirection.y
							* (1f - transitionAlpha);
				}
				if ((this.entryAnimation & MenuEntryEffects.GoToRight) == MenuEntryEffects.GoToRight) {
					this.currentPosition.x += movementDirection.x
							* (1f - transitionAlpha);
				}
				if ((this.entryAnimation & MenuEntryEffects.GoToLeft) == MenuEntryEffects.GoToLeft) {
					this.currentPosition.x -= movementDirection.x
							* (1f - transitionAlpha);
				}
			}
		}
	}

	public final void setBasePosition(Vector2f value) {
		this.basePosition = value;
	}

	public final void setBasePosition(float x, float y) {
		this.basePosition.set(x, y);
	}

	public static void setBasicTexture(LTexture tex) {
		basicTexture = tex;
	}

	public static void setBasicTexture(String path) {
		basicTexture = LTextures.loadTexture(path);
	}

	public final void setEntryAnimation(int value) {
		this.entryAnimation = value;
	}

	public final int getHeight() {
		return this.source.height;
	}

	public final Vector2f getPosition() {
		return this.currentPosition;
	}

	public final void setSource(RectBox value) {
		this.source = value;
	}

	public final void setSource(int x, int y, int w, int h) {
		this.source.setBounds(x, y, w, h);
	}

	public final int getWidth() {
		return this.source.width;
	}
}