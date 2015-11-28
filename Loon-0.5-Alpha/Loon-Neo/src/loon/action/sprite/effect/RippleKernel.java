package loon.action.sprite.effect;

import loon.canvas.LColor;
import loon.opengl.GLEx;

public class RippleKernel {

	LColor color;
	int x, y;
	int existTime;

	public RippleKernel(int x, int y) {
		this.x = x;
		this.y = y;
		existTime = 0;

	}

	public void paint(final GLEx g) {
		int span = existTime * 2;
		g.drawOval(x - span, y - span, x + span, y + span);
		existTime++;
	}

	public boolean isExpired() {
		return existTime >= 25;
	}

}
