package loon.stage;

import loon.Graphics;
import loon.LSystem;
import loon.LTexture;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.geom.Dimension;

public class CanvasPlayer extends ImagePlayer {

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
		resize(width, height);
	}

	public CanvasPlayer(Graphics gfx, Canvas canvas) {
		this.gfx = gfx;
		this.canvas = canvas;
		super.setPainter(canvas.image.createTexture(LTexture.Format.DEFAULT));
	}

	public void resize(float width, float height) {
		if (canvas != null) {
			canvas.close();
		}
		canvas = gfx.createCanvas(width, height);
	}

	public Canvas begin() {
		return canvas;
	}

	public void end() {
		LTexture tex = (LTexture) super.getPainter();
		Image image = canvas.image;
		if (tex != null && tex.pixelWidth() == image.pixelWidth()
				&& tex.pixelHeight() == image.pixelHeight()) {
			tex.update(image);
		} else {
			super.setPainter(canvas.image.texture());
		}
	}

	@Override
	public float getWidth() {
		return (forceWidth < 0) ? canvas.width : forceWidth;
	}

	@Override
	public float getHeight() {
		return (forceHeight < 0) ? canvas.height : forceHeight;
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
