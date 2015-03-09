package com.mygame;

import loon.core.LRelease;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;

public class Sprite implements LRelease{

	public final static int ALIGN_BOTTOM = 0x20;
	public final static int ALIGN_CENTERX = 2;
	public final static int ALIGN_CENTERY = 0x10;
	public final static int ALIGN_LEFT = 1;
	public final static int ALIGN_RIGHT = 4;
	public final static int ALIGN_TOP = 8;

	private int w;
	private int h;
	private int icols;
	private int irows;
	private Vector2f origin;
	private LTexture texture;

	public int xoff;
	public int yoff;

	private Vector2f position = new Vector2f();
	private Vector2f scale = new Vector2f();
	private RectBox rect = new RectBox();

	public Sprite(String aFile, int cols, int rows) {
		this(aFile, cols, rows, 0x12);
	}

	public Sprite(String aFile, int cols, int rows, int align) {
		this.texture = LTextures.loadTexture("assets/" + aFile + ".png");
		if (aFile.indexOf("coal") != -1) {
			cols = 12;
			rows = 2;
		} else if (cols == 19 && rows == 2) {
			cols = 8;
			rows = 5;
		} else if (cols == 19 && rows == 1) {
			cols = 8;
			rows = 3;
		}
		this.icols = cols;
		this.irows = rows;
		this.w = this.texture.getWidth() / cols;
		this.h = this.texture.getHeight() / rows;
		this.xoff = 0;
		this.yoff = 0;
		if ((align & 1) > 0) {
			this.xoff = 0;
		} else if ((align & 2) > 0) {
			this.xoff = -this.w / 2;
		} else if ((align & 4) > 0) {
			this.xoff = -this.w;
		}
		if ((align & 8) > 0) {
			this.yoff = 0;
		} else if ((align & 0x10) > 0) {
			this.yoff = -this.h / 2;
		} else if ((align & 0x20) > 0) {
			this.yoff = -this.h;
		}
		this.origin = new Vector2f(-this.xoff, -this.yoff);
	}

	public Sprite(String aFile, int cols, int rows, int align, boolean retain) {
		this(aFile, cols, rows, align);
	}

	public int getHeight() {
		return this.h;
	}

	public int getWidth() {
		return this.w;
	}

	public void Paint(Painter painter, float x, float y, int frame) {
		int sx = (frame % this.icols) * this.w;
		int sy = (frame / this.icols) * this.h;
		position.set(x, y);
		rect.setBounds(sx, sy, this.w, this.h);
		painter.draw(this.texture, position, this.origin, rect);
	}

	public void Paint(Painter painter, float x, float y, int frame, float angle) {
		int sx = (frame % this.icols) * this.w;
		int sy = (frame / this.icols) * this.h;
		position.set(x, y);
		scale.set(1f, 1f);
		rect.setBounds(sx, sy, this.w, this.h);
		painter.drawScaledRotated(this.texture, position, this.origin, scale,
				angle, rect);
	}

	public void PaintScaled(Painter painter, float x, float y, int frame,
			float scalex, float scaley) {
		int sx = (frame % this.icols) * this.w;
		int sy = (frame / this.icols) * this.h;
		position.set(x, y);
		scale.set(scalex, scaley);
		rect.setBounds(sx, sy, this.w, this.h);
		painter.drawScaled(this.texture, position, this.origin, scale, rect);
	}

	public void PaintScaled(Painter painter, float x, float y, int frame,
			float scalex, float scaley, boolean flipped) {
		int sx = (frame % this.icols) * this.w;
		int sy = (frame / this.icols) * this.h;
		position.set(x, y);
		scale.set(scalex, scaley);
		rect.setBounds(sx, sy, this.w, this.h);
		painter.drawScaled(this.texture, position, this.origin, scale, rect,
				flipped);
	}

	public void PaintScaledRotated(Painter painter, float x, float y,
			int frame, float scalex, float scaley, float angle) {
		int sx = (frame % this.icols) * this.w;
		int sy = (frame / this.icols) * this.h;
		position.set(x, y);
		scale.set(scalex, scaley);
		rect.setBounds(sx, sy, this.w, this.h);
		painter.drawScaledRotated(this.texture, position, this.origin, scale,
				angle, rect);
	}

	public void PaintScaledRotated(Painter painter, float x, float y,
			int frame, float scalex, float scaley, float angle, boolean flipped) {
		int sx = (frame % this.icols) * this.w;
		int sy = (frame / this.icols) * this.h;
		position.set(x, y);
		scale.set(scalex, scaley);
		rect.setBounds(sx, sy, this.w, this.h);
		painter.drawScaledRotated(this.texture, position, this.origin, scale,
				angle, rect, flipped);
	}

	public int getRows() {
		return irows;
	}

	@Override
	public void dispose() {
		texture.destroy();
	}

}
