package loon.action.sprite;

import loon.LSystem;
import loon.canvas.LColor;

public class ColorBackground extends ImageBackground {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ColorBackground(LColor c, float x, float y, float w, float h) {
		super(LSystem.base().graphics().finalColorTex(), x, y, w, h);
		this.setColor(c);
	}

}
