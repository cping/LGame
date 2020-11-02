
/**
 * 
 * Copyright 2014
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
 * @version 0.4.1
 * 
 * Example1:
 * 
 *          new LTextField("", 0, 0,35);
 *          
 *  Example3:
 *           
 *          LTextField field = new LTextField("", 0,0,35);
 *  		field.setFontColor(LColor.white);
 *  		field.setHideBackground(true);
 * 
 *  文本输入类，可以用setHideBackground函数隐藏背景，从而把其放置到理想的输入背景中
 * 
 */
package loon.core.graphics.component;

import loon.AndroidInputFactory;
import loon.Key;
import loon.core.graphics.LComponent;
import loon.core.graphics.device.LColor;
import loon.core.graphics.device.LFont;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;

public class LTextField extends LTextBar {

	private OnscreenKeyboard keyboard = new DefaultOnscreenKeyboard();

	static public interface OnscreenKeyboard {
		public void show(boolean visible);
	}

	static public class DefaultOnscreenKeyboard implements OnscreenKeyboard {
		@Override
		public void show(boolean visible) {
			AndroidInputFactory.setOnscreenKeyboardVisible(visible);
		}
	}

	protected void processTouchReleased() {
		super.processKeyReleased();
		keyboard.show(true);
	}

	public String getUIName() {
		return "TextField";
	}

	public OnscreenKeyboard getOnscreenKeyboard() {
		return keyboard;
	}

	public void setOnscreenKeyboard(OnscreenKeyboard keyboard) {
		this.keyboard = keyboard;
	}

	public static final int INPUT_STRING = 0, INPUT_SIGNED_INTEGER_NUM = 1,
			INPUT_UNSIGNED_INTEGER_NUM = 2,
			INPUT_INTEGER = INPUT_SIGNED_INTEGER_NUM,
			INPUT_SIGNED_FLOATING_POINT_NUM = 3,
			INPUT_UNSIGNED_FLOATING_POINT_NUM = 4,
			INPUT_FLOATING_POINT_NUM = INPUT_SIGNED_INTEGER_NUM;

	private String cursor = "_";
	protected int INPUT_TYPE = INPUT_STRING;
	protected int startidx, limit;

	public LTextField(String txt, LTexture left, LTexture right, LTexture body,
			int x, int y, LColor textcolor, LFont textfont, int type, int limit) {
		super(txt, left, right, body, x, y, textcolor, textfont);
		this.INPUT_TYPE = type;
		this.startidx = txt.length();
		this.limit = limit + startidx;
	}

	public LTextField(String txt, int x, int y, LColor textcolor,
			int INPUT_TYPE, int limit) {
		this(txt, DefUI.getDefaultTextures(10), DefUI.getDefaultTextures(10),
				DefUI.getDefaultTextures(11), x, y, textcolor, LFont
						.getDefaultFont(), INPUT_TYPE, limit);
	}

	public LTextField(String txt, LTexture left, LTexture right, LTexture body,
			int x, int y, LColor textcolor, int INPUT_TYPE, int limit) {
		this(txt, left, right, body, x, y, textcolor, LFont.getDefaultFont(),
				INPUT_TYPE, limit);
	}

	public LTextField(String txt, LTexture left, LTexture right, LTexture body,
			int x, int y, int INPUT_TYPE, int limit) {
		this(txt, left, right, body, x, y, LColor.black, INPUT_TYPE, limit);
	}

	public LTextField(String txt, int x, int y, int INPUT_TYPE, int limit) {
		this(txt, x, y, LColor.black, INPUT_TYPE, limit);
	}

	public LTextField(String txt, int x, int y, int limit) {
		this(txt, x, y, LColor.black, INPUT_STRING, limit);
	}

	public LTextField(String txt, int x, int y) {
		this(txt, x, y, LColor.black, INPUT_STRING, 35);
	}

	public int getInputType() {
		return INPUT_TYPE;
	}

	public LTextField setInputType(int type) {
		INPUT_TYPE = type;

		return this;
	}

	public String getInput() {
		String result = text.substring(startidx);
		if ((result.endsWith("-") || result.length() == 0)
				&& INPUT_TYPE != INPUT_STRING) {
			return "0";
		}
		return result;
	}

	public boolean wasEntered() {
		return this.input.getKeyPressed() == Key.ENTER || !this.isFocusable();
	}

	public void update(long delta) {
		super.update(delta);
		if (Key.isUp() && AndroidInputFactory.getOnlyKey().isPressed()) {
			if (!isFocusable()) {
				return;
			}
			char nextchar = Key.getKeyChar();
			if (nextchar == 0) {
				return;
			}
			boolean isatstart = text.length() == startidx;
			if (nextchar == '\b' && text.length() != 0 && !isatstart) {
				text = text.substring(0, text.length() - 1);
				return;
			}
			if (text.length() == limit) {
				return;
			}

			boolean valid = true;
			if (INPUT_TYPE != INPUT_STRING) {
				switch (INPUT_TYPE) {
				case INPUT_UNSIGNED_INTEGER_NUM:
					valid = Character.isDigit(nextchar);
					break;
				case INPUT_SIGNED_INTEGER_NUM:
					valid = Character.isDigit(nextchar) || nextchar == '-'
							&& isatstart;
					break;
				case INPUT_UNSIGNED_FLOATING_POINT_NUM:
					valid = Character.isDigit(nextchar) || nextchar == '.';
					break;
				case INPUT_SIGNED_FLOATING_POINT_NUM:
					valid = Character.isDigit(nextchar) || nextchar == '.'
							|| nextchar == '-' && isatstart;
					break;
				}
			}
			if (valid) {
				text += nextchar;
			}
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		addCursor();
		super.createUI(g, x, y, component, buttonImage);
		removeCursor();
	}

	public void setCursor(String cursor) {
		this.cursor = cursor;
	}

	protected void addCursor() {
		if (!isFocusable()) {
			return;
		}
		text += cursor;
	}

	protected void removeCursor() {
		if (!isFocusable()) {
			return;
		}
		text = text.substring(0,
				Math.max(startidx, text.length() - cursor.length()));
	}

}
