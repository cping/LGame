package loon.font;

import loon.canvas.LColor;
import loon.opengl.GLEx;

public interface IFont {

	void drawString(GLEx g, String string, float x, float y);

	void drawString(GLEx g, String string, float x, float y, LColor c);

	int stringWidth(String width);

	int stringHeight(String height);
	
	int getHeight();
	
	float getAscent();

	int getSize();
}
