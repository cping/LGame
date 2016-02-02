package org.test.towerdefense;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;
public class MenuEntry {

	private LTexture buttonTexture;
	private Vector2f position;

	private float selectionFade;
	private String text;

	public MenuEntry(String text) {
		this.text = text;
		this.setScale(1f);
		this.setRotation(0f);
	}

	public void Draw(SpriteBatch batch, MenuScreen screen, boolean isSelected,
			GameTime gameTime) {
		LColor black = isSelected ? LColor.white : LColor.black;
		LColor white = isSelected ? LColor.white : LColor.gray;
		isSelected = false;
		white = LColor.white;
		black = LColor.black;
		ScreenManager screenManager = screen.getScreenManager();
		this.buttonTexture = screenManager.getButtonBackground();
		if (this.getuseButtonBackground()) {
			batch.draw(this.buttonTexture, position, white);
		}
		batch.drawString(screenManager.getFont(), this.text,
				this.getTextPosition(screen), black, this.getRotation(),
				Vector2f.STATIC_ZERO, this.getScale());
	}

	public int GetHeight(MenuScreen screen) {
		return (int) screen.getScreenManager().getFont().getHeight();
	}

	private Vector2f pos = new Vector2f();

	private Vector2f getTextPosition(MenuScreen screen) {
		if (this.getScale() == 1f) {
			pos.set(((((int) this.position.x) + (this.buttonTexture.getWidth() / 2)) - (this
					.GetWidth(screen) / 2)), ((int) this.position.y));
			return pos;
		}
		pos.set((this.position.x)
				+ ((this.buttonTexture.getWidth() / 2) - ((this
						.GetWidth(screen) / 2) * this.getScale())),
				(this.position.y)
						+ ((this.GetHeight(screen) - (this.GetHeight(screen) * this
								.getScale())) / 2f));
		return pos;
	}

	public int GetWidth(MenuScreen screen) {
		return (int) screen.getScreenManager().getFont()
				.stringWidth(this.getText());
	}

	public GameEvent Selected;

	protected void OnSelectEntry() {
		if (this.Selected != null) {
			Selected.invoke(this);
		}
	}

	public void Update(MenuScreen screen, boolean isSelected, GameTime gameTime) {
		isSelected = false;
		float num = ((float) gameTime.getElapsedGameTime()) * 4f;
		if (isSelected) {
			this.selectionFade = Math.min((float) (this.selectionFade + num),
					(float) 1f);
		} else {
			this.selectionFade = Math.max((float) (this.selectionFade - num),
					(float) 0f);
		}
	}

	public final RectBox getBounds() {
		if (this.getuseButtonBackground()) {
			return new RectBox((int) this.position.x, (int) this.position.y,
					this.buttonTexture.getWidth(),
					this.buttonTexture.getHeight());
		}
		return new RectBox((int) this.position.x, (int) this.position.y,
				(int) this.getnoButtonBackgroundSize().x,
				(int) this.getnoButtonBackgroundSize().y);
	}

	private Vector2f privatenoButtonBackgroundSize;

	public final Vector2f getnoButtonBackgroundSize() {
		return privatenoButtonBackgroundSize;
	}

	public final void setnoButtonBackgroundSize(Vector2f value) {
		privatenoButtonBackgroundSize = value;
	}

	public final Vector2f getPosition() {
		return this.position;
	}

	public final void setPosition(Vector2f value) {
		this.position = value;
	}

	private float privateRotation;

	public final float getRotation() {
		return privateRotation;
	}

	public final void setRotation(float value) {
		privateRotation = value;
	}

	private float privateScale;

	public final float getScale() {
		return privateScale;
	}

	public final void setScale(float value) {
		privateScale = value;
	}

	public final String getText() {
		return this.text;
	}

	public final void setText(String value) {
		this.text = value;
	}

	private boolean privateuseButtonBackground;

	public final boolean getuseButtonBackground() {
		return privateuseButtonBackground;
	}

	public final void setuseButtonBackground(boolean value) {
		privateuseButtonBackground = value;
	}
}