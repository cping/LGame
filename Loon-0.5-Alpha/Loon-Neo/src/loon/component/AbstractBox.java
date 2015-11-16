package loon.component;

import loon.LTexture;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.GLEx;
import loon.opengl.ShadowFont;

public abstract class AbstractBox implements XY {

	protected int _width;
	protected int _height;
	protected int _boxWidth;
	protected int _boxHeight;
	protected float _boxX;
	protected float _boxY;
	protected float _borderW;
	protected LColor borderColor = new LColor(LColor.white);
	protected float _alpha = 1f;
	protected ShadowFont font;
	protected LColor fontColor = new LColor(LColor.white);
	protected LTexture _textureBox;
	protected int _radius;

	protected AbstractBox(ShadowFont font) {
		this.font = font;
	}

	protected void init(int w, int h) {
		this._width = w;
		this._height = h;
		this._alpha = 0.65f;
		this._borderW = 3f;
		this._radius = 0;
		this.dirty();
	}

	public abstract void dirty();

	public void setFont(ShadowFont font) {
		this.font = font;
		dirty();
	}

	public void setBorderWidth(float b) {
		this._borderW = b;
		dirty();
	}

	public void setBoxAlpha(float alpha) {
		this._alpha = alpha;
		dirty();
	}

	public void setCornerRadius(int r) {
		this._radius = r;
	}

	@Override
	public float getX() {
		return this._boxX;
	}

	@Override
	public float getY() {
		return this._boxY;
	}

	public int getWidth() {
		return this._boxWidth;
	}

	public int getHeight() {
		return this._boxHeight;
	}

	public void setLocation(float x, float y) {
		this._boxX = x;
		this._boxY = y;
	}

	public void setLocation(Vector2f pos) {
		this._boxX = pos.x;
		this._boxY = pos.y;
	}

	protected void drawBorder(GLEx g, float x, float y) {
		if (this._textureBox != null) {
			g.draw(_textureBox, x, y);
		}
	}

	protected void setFontColor(LColor color) {
		this.fontColor = color;
	}

	protected void setBorderColor(LColor color) {
		this.borderColor = color;
	}
}
