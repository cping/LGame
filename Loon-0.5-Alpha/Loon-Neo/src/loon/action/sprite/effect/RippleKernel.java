package loon.action.sprite.effect;

import loon.action.sprite.effect.RippleEffect.Model;
import loon.canvas.LColor;
import loon.opengl.GLEx;

public class RippleKernel {

	LColor color;
	float x, y;
	int existTime;

	public RippleKernel(float x, float y) {
		this.x = x;
		this.y = y;
		this.existTime = 0;
	}

	public void draw(final GLEx g, Model model, float mx, float my) {
		int span = existTime * 2;
		switch (model) {
		case OVAL:
			g.drawOval(mx + x - span / 2, my + y - span / 2, span, span);
			break;
		case RECT:
			g.drawRect(mx + x - span / 2, my + y - span / 2, span, span);
			break;
		}
		existTime++;
	}

	public boolean isExpired() {
		return existTime >= 25;
	}

}
