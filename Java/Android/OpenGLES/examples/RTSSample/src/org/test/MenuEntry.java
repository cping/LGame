package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.action.sprite.painting.DrawableEvent;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LColorPool;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;

//单独的菜单元素
public class MenuEntry {
	
	private LFont font;
	public float fontSize;
	private Vector2f position;
	private GameEntity screen;

	public DrawableEvent Selected;
	private float selectionFade;
	public String Text;
	public float TextSize;
	private LTexture texture;

	public MenuEntry(MenuEntity screen, LTexture texture, Vector2f position) {
		this(screen, "", position);
		this.texture = texture;
	}

	public MenuEntry(MenuEntity screen, String text, Vector2f position) {
		this.TextSize = 60f;
		this.screen = screen;
		this.Text = text;
		this.position = position;
		this.font = screen.getScreenManager().getGameContent().gameFont;
		this.fontSize = screen.getScreenManager().getGameContent().gameFontSize;
	}

	public void Draw(boolean isSelected, SpriteBatch batch, GameTime gameTime) {
		isSelected = false;
		LColor color = LColorPool.$().getColor(1f, 1f, 1f,
				this.screen.getTransitionAlpha());
		if (this.texture == null) {
			batch.drawString(this.font, this.Text, this.position, color, 0f,
					Vector2f.Zero, (this.TextSize / this.fontSize));
		} else {
			batch.draw(this.texture, this.position, null, color, 0f,
					Vector2f.Zero, 1f, SpriteEffects.None);
		}
	}

	public int GetHeight() {
		return this.font.getLineHeight();
	}

	protected void OnSelectEntry() {
		if (this.Selected != null) {
			Selected.invoke();
		}
	}

	public void Update(boolean isSelected, GameTime gameTime) {
		isSelected = false;
		float num = (gameTime.getElapsedGameTime()) * 4f;
		if (isSelected) {
			this.selectionFade = MathUtils.min((this.selectionFade + num), 1f);
		} else {
			this.selectionFade = MathUtils.max((this.selectionFade - num), 0f);
		}
	}

	private RectBox rect = new RectBox();

	public final RectBox getGetMenuEntryHitBounds() {
		if (this.texture == null) {
			Vector2f vector = new Vector2f(
					(this.font.stringWidth(this.Text) * this.TextSize)
							/ this.fontSize, (font.getSize() * this.TextSize)
							/ this.fontSize);
			rect.setBounds(this.position.x, this.position.y, vector.x, vector.y);
			return rect;
		}
		rect.setBounds(this.position.x,this.position.y,
				this.texture.getWidth(), this.texture.getHeight());
		return rect;
	}
}