package loon.action.map.view;

import loon.LTexture;
import loon.canvas.LColor;
import loon.opengl.GLEx;

public class FilledView extends GameView {

	private LTexture texture;
	private LColor colorToFill;

	public FilledView(int x, int y, int w, int h) {
		this(null, x, y, w, h, LColor.white);
	}

	public FilledView(int x, int y, int w, int h, LColor color) {
		this(null, x, y, w, h, color);
	}

	public FilledView(LTexture texture, int x, int y, int w, int h, LColor color) {
		super(x, y, w, h);
		colorToFill = color;
	}

	@Override
	public void draw(GLEx g) {
		if (texture == null) {
			g.fillRect(_x, _y, _width, _height, colorToFill);
		} else {
			g.draw(texture, _x, _y, _width, _height, colorToFill);
		}
	}

}
