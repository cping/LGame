package loon.action.sprite.effect;

import loon.canvas.LColor;
import loon.opengl.GLEx;

public class PixelChopEffect extends PixelBaseEffect {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private float t_x, t_y;

	private float width = 3;

	public PixelChopEffect(LColor color, float x, float y) {
		this(color, x, y, 3);
	}

	public PixelChopEffect(LColor color, float x, float y, float width) {
		super(color, x, y, 0, 0);
		this.width = width;
		this.t_x = x;
		this.t_y = y;
		this.limit = 25;
		setDelay(0);
		setEffectDelay(0);
	}

	@Override
	public void draw(GLEx g, float tx, float ty) {
		if (super.completed) {
			return;
		}
		int tmp = g.color();
		g.setColor(_baseColor);
		float x = t_x - tx;
		float y = t_y - ty;
		int f = super.frame;
		if (f > 25) {
			f = 25 - f;
		}
		float x1 = x - f;
		float y1 = y - f;
		float x2 = x + f;
		float y2 = y + f;
		g.drawLine(x1, y1, x2, y2, width);
		g.setColor(tmp);
		if (super.frame >= limit) {
			super.completed = true;
		}
	}

}
