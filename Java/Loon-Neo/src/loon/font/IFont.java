package loon.font;

import loon.LRelease;
import loon.canvas.LColor;
import loon.geom.PointI;
import loon.opengl.GLEx;

/**
 * loon的统一字体实现接口，可以通过注入不同的IFont，改变全局或局部字体
 */
public interface IFont extends LRelease {

	void drawString(GLEx g, String string, float x, float y);

	void drawString(GLEx g, String string, float x, float y, LColor c);

	void drawString(GLEx g, String string, float x, float y, float rotation,
			LColor c);

	void drawString(GLEx g, String string, float x, float y, float sx,
			float sy, float ax, float ay, float rotation, LColor c);

	int charWidth(char c);

	int stringWidth(String width);

	int stringHeight(String height);

	int getHeight();

	void setAssent(float assent);

	float getAscent();

	void setSize(int size);

	int getSize();

	PointI getOffset();

	void setOffset(PointI val);

	void setOffsetX(int x);

	void setOffsetY(int y);

	String confineLength(String s, int width);
}
