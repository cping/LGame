package loon.action.sprite;

import loon.Graphics;
import loon.LSystem;
import loon.LTexture;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.geom.Dimension;

public class CanvasPlayer extends Entity {

	private final Graphics gfx;
	private Canvas canvas;

	public CanvasPlayer() {
		this(LSystem.base().graphics(), LSystem.viewSize);
	}

	public CanvasPlayer(Graphics gfx, Dimension size) {
		this(gfx, size.width(), size.height());
	}

	public CanvasPlayer(float width, float height) {
		this(LSystem.base().graphics(), width, height);
	}

	public CanvasPlayer(Graphics gfx, float width, float height) {
		this.gfx = gfx;
		this.setRepaint(true);
		resize(width, height);
	}

	public CanvasPlayer(Graphics gfx, Canvas canvas) {
		this.gfx = gfx;
		this.canvas = canvas;
		setTexture(canvas.image.createTexture(LTexture.Format.DEFAULT));
	}

	public void resize(float width, float height) {
		if (canvas != null) {
			canvas.close();
		}
		canvas = gfx.createCanvas(width, height);
		setSize(width, height);
	}

	@Override
	public void setTexture(LTexture tex) {
		setSize(tex.width(), tex.height());
		if (this.getBitmap() != tex) {
			if (this.getBitmap() != null) {
				this.getBitmap().texture().release();
			}
			super.setTexture(tex);
			if (tex != null) {
				tex.texture().reference();
			}
		}
	}

	public Canvas begin() {
		return canvas;
	}

	public void end() {
		LTexture tex = super.getBitmap();
		Image image = canvas.image;
		if (tex != null && tex.pixelWidth() == image.pixelWidth() && tex.pixelHeight() == image.pixelHeight()) {
			tex.update(image, false);
		} else {
			setTexture(canvas.image.texture());
		}
	}

	@Override
	public void close() {
		super.close();
		if (canvas != null) {
			canvas.close();
			if (canvas.image != null) {
				canvas.image.close();
			}
			canvas = null;
		}
	}
}
