package loon.action.sprite;

import loon.LSystem;
import loon.canvas.LColor;
import loon.opengl.GLEx;

public class ColorBackground extends ImageBackground {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private LColor color = new LColor(LColor.black);

	public ColorBackground(LColor c, float x, float y, float w, float h) {
		super(LSystem.base().graphics().finalColorTex(), x, y, w, h);
		color.setColor(c);
	}

	@Override
	public void createUI(GLEx g) {
		if (!_visible) {
			return;
		}
		if (_alpha <= 0.01) {
			return;
		}
		g.saveBrush();
		g.setAlpha(_alpha);
		g.setColor(color);
		if (_texture != null) {
			g.draw(_texture, _location.x, _location.y, _screen.width,
					_screen.height, _rotation);

		}
		g.restoreBrush();
	}

	public LColor getColor() {
		return color;
	}

	public void setColor(LColor color) {
		this.color = color;
	}

	public void setColor(float r, float g, float b, float a) {
		this.color.setColor(r, g, b, a);
	}
}
