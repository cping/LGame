package loon.stg.effect;

import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.stg.STGPlane;

public class PictureRect implements Picture {

	int w;

	int h;

	LColor color;

	PictureRect(int w, int h, LColor c) {
		this.w = w;
		this.h = h;
		this.color = c;
	}

	@Override
	public boolean paint(GLEx g, STGPlane p) {
		g.setColor(this.color);
		g.fillRect(p.posX, p.posY, this.w, this.h);
		g.resetColor();
		return true;
	}

	public void darker() {
		this.color = this.color.darker();
	}

	@Override
	public void close() {

	}

}
