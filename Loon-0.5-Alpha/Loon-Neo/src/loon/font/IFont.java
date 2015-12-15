package loon.font;

import loon.canvas.LColor;
import loon.opengl.GLEx;

public interface IFont {

	void drawString(GLEx g, String string, float x, float y);

	void drawString(GLEx g, String string, float x, float y, LColor c);

	void drawString(GLEx g, String string, float x, float y, float rotation,
			LColor c);

	void drawString(GLEx g, String string, float x, float y, float sx,
			float sy, float ax, float ay, float rotation, LColor c);

	int stringWidth(String width);

	int stringHeight(String height);

	int getHeight();

	float getAscent();

	int getSize();

	String confineLength(String s, int width);
}
