/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.action.sprite;

import loon.LSystem;
import loon.canvas.LColor;
import loon.font.Font.Style;
import loon.font.IFont;
import loon.font.LFont;
import loon.font.Text;
import loon.font.TextOptions;
import loon.opengl.GLEx;

/**
 * 一个精灵用字符串显示用类
 * 
 */
public class SpriteLabel extends Entity {

	private final Text _text;

	public SpriteLabel(String label) {
		this(LSystem.getSystemGameFont(), label, 0, 0);
	}

	public SpriteLabel(String label, int x, int y) {
		this(LSystem.getSystemGameFont(), label, x, y);
	}

	public SpriteLabel(String label, String font, Style type, int size, int x, int y) {
		this(LFont.getFont(font, type, size), label, x, y);
	}

	public SpriteLabel(IFont font, String label, int x, int y) {
		this(font, TextOptions.LEFT(), label, x, y);
	}

	public SpriteLabel(IFont font, TextOptions opt, String label, int x, int y) {
		this._text = new Text(font, label, opt);
		this.setSize(_text.getWidth(), _text.getHeight());
		this.setRepaint(true);
		this.setColor(LColor.white);
		this.setLocation(x, y);
		this.setLabel(label);
	}

	public void setFont(String fontName, Style type, int size) {
		setFont(LFont.getFont(fontName, type, size));
	}

	public void setFont(IFont font) {
		this._text.setFont(font);
		this.setSize(_text.getWidth(), _text.getHeight());
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		_text.paintString(g, drawX(offsetX), drawY(offsetY), _baseColor);
	}

	public Text getOptions() {
		return this._text;
	}

	public CharSequence getLabel() {
		return _text.getText();
	}

	public SpriteLabel setLabel(int label) {
		return setLabel(String.valueOf(label));
	}

	public SpriteLabel setLabel(CharSequence label) {
		_text.setText(label);
		return this;
	}

	@Override
	public void close() {
		super.close();
		_text.close();
	}
}
