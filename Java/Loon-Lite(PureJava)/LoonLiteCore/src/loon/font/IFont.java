/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.font;

import loon.LRelease;
import loon.canvas.LColor;
import loon.geom.PointI;
import loon.opengl.GLEx;

/**
 * loon的统一字体实现接口，可以通过注入不同的IFont，改变全局或局部字体
 */
public interface IFont extends LRelease {

	/**
	 * 返回当前翻译器
	 *
	 * @return
	 */
	public ITranslator getTranslator();

	/**
	 * 翻译器注入
	 *
	 * @param translator
	 * @return
	 */
	public IFont setTranslator(ITranslator translator);

	void drawString(GLEx g, String string, float x, float y);

	void drawString(GLEx g, String string, float x, float y, LColor c);

	void drawString(GLEx g, String string, float x, float y, float rotation, LColor c);

	void drawString(GLEx g, String string, float x, float y, float sx, float sy, float ax, float ay, float rotation,
			LColor c);

	int charWidth(char c);

	int stringWidth(String width);

	int stringHeight(String height);

	int getHeight();

	void setAssent(float assent);

	String getFontName();

	float getAscent();

	void setSize(int size);

	int getSize();

	PointI getOffset();

	void setOffset(PointI val);

	void setOffsetX(int x);

	void setOffsetY(int y);

	String confineLength(String s, int width);
}
