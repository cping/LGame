package loon.component;

import loon.LTexture;
import loon.opengl.GLEx;



public class LPanel extends LContainer {

	public LPanel(int x, int y, int w, int h) {
		super(x, y, w, h);
		this.customRendering = true;
	}

	public String getUIName() {
		return "Panel";
	}

	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {

	}
}
