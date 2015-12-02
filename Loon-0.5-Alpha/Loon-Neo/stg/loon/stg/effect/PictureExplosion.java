package loon.stg.effect;

import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.stg.STGPlane;

public class PictureExplosion implements Picture {

	int count = 0;

	int x;

	int y;

	PictureExplosion() {
		this.x = this.y = 5;
	}

	@Override
	public boolean paint(GLEx g, STGPlane p) {
		switch (this.count) {
		case 4:
			this.x = this.y = 27;
			break;
		case 8:
			this.x = 5;
			this.y = 27;
			break;
		case 12:
			this.x = 27;
			this.y = 5;
			break;
		case 16:
			this.x = this.y = 16;
		}
		g.setColor(LColor.yellow);
		g.fillOval(p.posX + this.x - this.count % 4 * 4, p.posY + this.y
				- this.count % 4 * 4, this.count % 4 * 8, this.count % 4 * 8);
		++this.count;
		return true;
	}

	@Override
	public void close() {
	
	}

}
