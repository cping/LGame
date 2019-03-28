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
package loon.component;

import loon.HorizontalAlign;
import loon.LTexture;
import loon.canvas.LColor;
import loon.component.skin.MessageSkin;
import loon.component.skin.SkinManager;
import loon.font.Font.Style;
import loon.font.FontSet;
import loon.font.IFont;
import loon.font.LFont;
import loon.font.Text;
import loon.font.TextOptions;
import loon.opengl.GLEx;

/**
 * 该类用以创建单独的标签组件(LLables为成批渲染文字，此类为单独渲染，效率上较慢)
 * PS:具体位置可用setOffsetLeft和setOffsetTop进一步微调
 * 
 * Example1:
 * 
 * <pre>
 * LLabel label = LLabel.make(HorizontalAlign.LEFT, "ABC", 0, 0, 200, 100, LColor.red);
 * </pre>
 * 
 * Example2:
 * 
 * <pre>
 * LLabel label = LLabel.make("ABC", 99, 99, LColor.red);
 * </pre>
 */
public class LLabel extends LComponent implements FontSet<LLabel>{

	public static LLabel make(HorizontalAlign alignment, String mes, int x, int y, int size, LTexture tex, LColor c) {
		IFont font = LFont.getFont(size);
		return new LLabel(alignment, font, c, tex, mes, x, y, 0, 0);
	}

	public static LLabel make(HorizontalAlign alignment, String mes, int x, int y, int width, int height,
			String fontname, int size) {
		return new LLabel(alignment, LFont.getFont(fontname, size), SkinManager.get().getMessageSkin().getFontColor(),
				null, mes, x, y, width, height);
	}

	public static LLabel make(HorizontalAlign alignment, String mes, int x, int y, int width, int height,
			String fontname, int size, Style style) {
		return new LLabel(alignment, LFont.getFont(fontname, style, size),
				SkinManager.get().getMessageSkin().getFontColor(), null, mes, x, y, width, height);
	}

	public static LLabel make(String mes, String fontname, int size, Style style) {
		return new LLabel(HorizontalAlign.LEFT, LFont.getFont(fontname, style, size),
				SkinManager.get().getMessageSkin().getFontColor(), null, mes, 0, 0, 0, 0);
	}

	public static LLabel make(String mes, String fontname, int size) {
		return new LLabel(HorizontalAlign.LEFT, LFont.getFont(fontname, size),
				SkinManager.get().getMessageSkin().getFontColor(), null, mes, 0, 0, 0, 0);
	}

	public static LLabel make(HorizontalAlign alignment, String mes, int x, int y, IFont font) {
		return new LLabel(alignment, font, SkinManager.get().getMessageSkin().getFontColor(), mes, x, y);
	}

	public static LLabel make(HorizontalAlign alignment, String mes, int x, int y, LColor color) {
		return new LLabel(alignment, SkinManager.get().getMessageSkin().getFont(), color, null, mes, x, y, 0, 0);
	}

	public static LLabel make(HorizontalAlign alignment, LTexture tex, String mes, int x, int y, LColor color) {
		return new LLabel(alignment, SkinManager.get().getMessageSkin().getFont(), color, tex, mes, x, y, 0, 0);
	}

	public static LLabel make(HorizontalAlign alignment, LTexture tex, String mes, int x, int y, int width, int height,
			LColor color) {
		return new LLabel(alignment, SkinManager.get().getMessageSkin().getFont(), color, tex, mes, x, y, width,
				height);
	}

	public static LLabel make(HorizontalAlign alignment, String mes, int x, int y, int width, int height,
			LColor color) {
		return new LLabel(alignment, SkinManager.get().getMessageSkin().getFont(), color, null, mes, x, y, width,
				height);
	}

	public static LLabel make(HorizontalAlign alignment, int size, String mes, int x, int y, LColor color) {
		return new LLabel(alignment, LFont.getFont(size), color, null, mes, x, y, 0, 0);
	}

	public static LLabel make(int size, String mes, int x, int y, LColor color) {
		return new LLabel(HorizontalAlign.LEFT, LFont.getFont(size), color, null, mes, x, y, 0, 0);
	}

	public static LLabel make(int size, String mes, int x, int y) {
		return new LLabel(HorizontalAlign.LEFT, LFont.getFont(size), SkinManager.get().getMessageSkin().getFontColor(),
				null, mes, x, y, 0, 0);
	}

	public static LLabel make(String mes, int x, int y, LColor color) {
		return new LLabel(HorizontalAlign.LEFT, SkinManager.get().getMessageSkin().getFont(), color, null, mes, x, y, 0,
				0);
	}

	public static LLabel make(HorizontalAlign alignment, String mes, IFont font, int x, int y, LColor color) {
		return new LLabel(alignment, font, color, null, mes, x, y, 0, 0);
	}

	public static LLabel make(String mes, IFont font, int x, int y, LColor color) {
		return new LLabel(HorizontalAlign.LEFT, font, color, null, mes, x, y, 0, 0);
	}

	public static LLabel make(String mes) {
		return make(mes, 0, 0);
	}

	public static LLabel make(String mes, int x, int y) {
		return make(mes, x, y, SkinManager.get().getMessageSkin().getFontColor());
	}

	public LLabel(HorizontalAlign alignment, int size, LColor c, String mes, int x, int y) {
		this(alignment, LFont.getFont(size), c, mes, x, y);
	}

	public LLabel(HorizontalAlign alignment, IFont font, LColor c, String mes, int x, int y) {
		this(alignment, font, c, null, mes, x, y, font.stringWidth(mes), font.getHeight());
	}

	private final Text _text;

	private float _offsetX, _offsetY;
	
	private LColor _fontColor;

	public LLabel(HorizontalAlign alignment, IFont font, LColor c, LTexture bg, String mes, int x, int y, int width,
			int height) {
		this(alignment, new TextOptions(), font, c, bg, mes, x, y, width, height);
	}

	public LLabel(MessageSkin skin, HorizontalAlign alignment, TextOptions opt, String mes, int x, int y, int width,
			int height) {
		this(alignment, skin.getFont(), skin.getFontColor(), skin.getBackgroundTexture(), mes, x, y, width, height);
	}

	public LLabel(HorizontalAlign alignment, TextOptions opt, IFont font, LColor c, LTexture bg, String mes, int x,
			int y, int width, int height) {
		super(x, y, width, height);
		this._fontColor = c;
		this._text = new Text(font, mes, opt);
		this.setBackground(bg);
		opt.setHorizontalAlign(alignment);
		if (bg == null) {
			setWidth(_text.getWidth());
			setHeight(_text.getHeight());
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		draw(g, x, y);
	}

	public void draw(GLEx g, int x, int y) {
		_text.paintString(g, x + _offsetX, y + _offsetY, _fontColor);
	}

	public IFont getFont() {
		return _text.getFont();
	}

	public HorizontalAlign getLabelAlignment() {
		return _text.getHorizontalAlign();
	}

	public CharSequence getText() {
		return _text.getText();
	}

	public LLabel setText(CharSequence ch) {
		_text.setText(ch);
		return this;
	}

	public float getOffsetLeft() {
		return _offsetX;
	}

	public LLabel setOffsetLeft(float offsetLeft) {
		this._offsetX = offsetLeft;
		return this;
	}

	public float getOffsetTop() {
		return _offsetY;
	}

	public LLabel setOffsetTop(float offsetTop) {
		this._offsetY = offsetTop;
		return this;
	}

	public float getSpace() {
		return _text.getSpace();
	}

	public LLabel setSpace(float space) {
		this._text.setSpace(space);
		return this;
	}

	public Text getOptions() {
		return this._text;
	}

	@Override
	public LLabel setFontColor(LColor color) {
		_fontColor = color;
		return this;
	}

	@Override
	public LColor getFontColor() {
		return _fontColor.cpy();
	}
	
	@Override
	public String getUIName() {
		return "Label";
	}

	@Override
	public void close() {
		super.close();
		_text.close();
	}

	@Override
	public LLabel setFont(IFont font) {
		this._text.setFont(font);
		return this;
	}


}
