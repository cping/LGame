package loon.opengl.light;

import loon.canvas.LColor;

public abstract class BaseLight {
	
	public final LColor color = new LColor(0f,0f,0f,1f);

	public LColor getColor() {
		return color;
	}
	
}