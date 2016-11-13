package loon.action.sprite.effect;

import loon.LSystem;
import loon.canvas.LColor;
import loon.opengl.GLEx;

public class PixelThunderEffect extends PixelBaseEffect {

	private float t_x, t_y;

	private float width = 3;

	public PixelThunderEffect(LColor color) {
		this(color, LSystem.viewSize.width/2,LSystem.viewSize.height-100);
	}
	
	public PixelThunderEffect(LColor color, float x, float y) {
		this(color, x, y,LSystem.viewSize.width,LSystem.viewSize.height, 3);
	}

	public PixelThunderEffect(LColor color, float x, float y,float w,float h, float width) {
		super(color, x, y, w, h);
		this.width = width;
		this.t_x = x;
		this.t_y = y;
		this.limit = 50;
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
		g.setColor(_baseColor);
		if (f <= 20) {
			float size = y - (getWidth() * (20 - super.frame)) / 20;
			g.setAlpha(0.5f);
			g.drawLine(x, size - 100, x, size, width);
			g.drawLine(x + 1, (size - 100) + 1, x + 1, size - 1, width);
			g.drawLine(x - 1, (size - 100) + 1, x - 1, size - 1, width);
			g.setAlpha(1f);
		} else {
			f -= 20;
			for (int j = 0; j < 6; j++) {
				g.drawOval(x - f * 6, y - f - j, f * 12, f * 2 + j * 2);
			}
		}
		g.setColor(tmp);
		if (super.frame >= limit) {
			super.completed = true;
		}
	}

}
