package loon.action.sprite;

import loon.core.LObject;
import loon.core.geom.RectBox;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;


/**
 * Copyright 2008 - 2009
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
 * @version 0.1
 */
public class Label extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private LFont font;

	private boolean visible;

	private int width, height;

	private LColor color;

	private String label;

	public Label(String label, int x, int y) {
		this(LFont.getDefaultFont(), label, x, y);
	}

	public Label(String label, String font, int type, int size, int x, int y) {
		this(LFont.getFont(font, type, size), label, x, y);
	}

	public Label(LFont font, String label, int x, int y) {
		this.font = font;
		this.label = label;
		this.color = LColor.black;
		this.visible = true;
		this.setLocation(x, y);
	}

	public void setFont(String fontName, int type, int size) {
		setFont(LFont.getFont(fontName, type, size));
	}

	public void setFont(LFont font) {
		this.font = font;
	}

	@Override
	public void createUI(GLEx g) {
		if (visible) {
			LFont oldFont = g.getFont();
			int oldColor = g.getColorRGB();
			g.setFont(font);
			g.setColor(color);
			this.width = font.stringWidth(label);
			this.height = font.getSize();
			if (alpha > 0 && alpha < 1) {
				g.setAlpha(alpha);
				g.drawString(label, x(), y());
				g.setAlpha(1.0F);
			} else {
				g.drawString(label, x(), y());
			}
			g.setFont(oldFont);
			g.setColor(oldColor);
		}
	}


	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void update(long timer) {

	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(int label) {
		setLabel(String.valueOf(label));
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public LColor getColor() {
		return color;
	}

	public void setColor(LColor color) {
		this.color = color;
	}

	@Override
	public LTexture getBitmap() {
		return null;
	}

	@Override
	public void dispose() {

	}

}
