/**
 * Copyright 2008 - 2010
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
package loon.component;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.component.skin.SelectSkin;
import loon.component.skin.SkinManager;
import loon.events.ActionKey;
import loon.events.SysKey;
import loon.font.FontSet;
import loon.font.IFont;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

/**
 * 一个选项器UI,与LMenuSelect的最大区别在于这个的UI大小是固定的,而LMenuSelect会随着注入的内容不同而自行改变UI大小
 */
public class LSelect extends LContainer implements FontSet<LSelect> {

	private static String[] getListToStrings(TArray<String> list) {
		if (list == null || list.size == 0) {
			return null;
		}
		String[] result = new String[list.size];
		for (int i = 0; i < result.length; i++) {
			result[i] = list.get(i);
		}
		return result;
	}

	private IFont _messageFont;

	private LColor _fontColor = LColor.white;

	private LColor _cursorColor = LColor.white;

	private int _left, _top, _type, _nTop;

	private int sizeFont, doubleSizeFont, tmpOffset, messageLeft, nLeft, messageTop, selectSize, selectFlag;

	private int space;

	private float autoAlpha;

	private LTimer delay;

	private String[] selects;

	private String message, result;

	private LTexture cursor, buoyage;

	private boolean isAutoAlpha, isSelect;

	private boolean clicked;

	private ActionKey eventClick = new ActionKey();

	public LSelect(IFont font, int x, int y, int width, int height) {
		this(font, (LTexture) null, x, y, width, height);
	}

	public LSelect(IFont font, String fileName) {
		this(font, fileName, 0, 0);
	}

	public LSelect(IFont font, String fileName, int x, int y) {
		this(font, LSystem.loadTexture(fileName), x, y);
	}

	public LSelect(IFont font, LTexture formImage) {
		this(font, formImage, 0, 0);
	}

	public LSelect(IFont font, LTexture formImage, int x, int y) {
		this(font, formImage, x, y, formImage.getWidth(), formImage.getHeight());
	}

	public LSelect(LTexture formImage, int x, int y) {
		this(SkinManager.get().getMessageSkin().getFont(), formImage, x, y, formImage.getWidth(),
				formImage.getHeight());
	}

	public LSelect(IFont font, LTexture formImage, int x, int y, int width, int height) {
		this(font, formImage, x, y, width, height, SkinManager.get().getMessageSkin().getFontColor());
	}

	public LSelect(SelectSkin skin, int x, int y, int width, int height) {
		this(skin.getFont(), skin.getBackgroundTexture(), x, y, width, height, skin.getFontColor());
	}

	public LSelect(IFont font, LTexture formImage, int x, int y, int width, int height, LColor fontColor) {
		super(x, y, width, height);
		if (formImage == null) {
			this.setBackground(LSystem.createTexture(width, height, LTexture.Format.LINEAR));
			this.setAlpha(0.3F);
		} else {
			this.setBackground(formImage);
		}
		this._fontColor = fontColor;
		this._messageFont = (font == null ? LSystem.getSystemGameFont() : font);
		this.customRendering = true;
		this.selectFlag = -1;
		this.space = 30;
		this.tmpOffset = -(width / 10);
		this.delay = new LTimer(150);
		this.autoAlpha = 0.25F;
		this.isAutoAlpha = true;
		this.setCursor(LSystem.getSystemImagePath() + "creese.png");
		this.setElastic(true);
		this.setLocked(true);
	}

	public LSelect setMessage(String message, TArray<String> list) {
		return setMessage(message, getListToStrings(list));
	}

	public LSelect setMessage(String[] selects) {
		return setMessage(null, selects);
	}

	public LSelect setMessage(TArray<String> list) {
		return setMessage(null, list);
	}

	public LSelect setMessage(String message, String[] selects) {
		this.message = message;
		this.selects = selects;
		this.selectSize = selects.length;
		if (doubleSizeFont == 0) {
			doubleSizeFont = 20;
		}
		if (_messageFont instanceof LFont) {
			LSTRDictionary.get().bind((LFont) _messageFont, selects);
		}
		return this;
	}

	public LSelect setLeftOffset(int left) {
		this._left = left;
		return this;
	}

	public LSelect setTopOffset(int top) {
		this._top = top;
		return this;
	}

	public int getLeftOffset() {
		return _left;
	}

	public int getTopOffset() {
		return _top;
	}

	public int getResultIndex() {
		return selectFlag - 1;
	}

	public LSelect setDelay(long timer) {
		delay.setDelay(timer);
		return this;
	}

	public long getDelay() {
		return delay.getDelay();
	}

	public String getResult() {
		return result;
	}

	@Override
	public void update(long elapsedTime) {
		if (!isVisible()) {
			return;
		}
		super.update(elapsedTime);
		if (isAutoAlpha && buoyage != null) {
			if (delay.action(elapsedTime)) {
				if (autoAlpha < 0.95F) {
					autoAlpha += 0.05F;
				} else {
					autoAlpha = 0.25F;
				}
			}
		}
		if (!isClickUp()) {
			if (selects != null) {
				final int touchY = _input.getTouchIntY();
				selectFlag = selectSize - (((_nTop + space) - (touchY == 0 ? 1 : touchY)) / doubleSizeFont);
				if (selectFlag < 1) {
					selectFlag = 0;
				}
				if (selectFlag > selectSize) {
					selectFlag = selectSize;
				}
			}
		}
	}

	@Override
	protected void createCustomUI(GLEx g, int x, int y, int w, int h) {
		if (!isVisible()) {
			return;
		}
		final int oldColor = g.color();
		sizeFont = _messageFont.getSize();
		doubleSizeFont = sizeFont * 2;
		if (doubleSizeFont == 0) {
			doubleSizeFont = 20;
		}
		messageLeft = (x + doubleSizeFont + sizeFont / 2) + tmpOffset + _left + doubleSizeFont;
		if (message != null) {
			messageTop = y + doubleSizeFont + _top - 10;
			_messageFont.drawString(g, message, messageLeft, messageTop - _messageFont.getAscent(), _fontColor);
		} else {
			messageTop = y + _top;
		}
		_nTop = messageTop;
		if (selects != null) {
			nLeft = messageLeft - sizeFont / 4;
			for (int i = 0; i < selects.length; i++) {
				_nTop += space;
				_type = i + 1;
				isSelect = (_type == (selectFlag > 0 ? selectFlag : 1));
				if ((buoyage != null) && isSelect) {
					g.setAlpha(autoAlpha);
					g.draw(buoyage, nLeft, _nTop - MathUtils.iceil(buoyage.getHeight() / 1.5f), _component_baseColor);
					g.setAlpha(1F);
				}
				_messageFont.drawString(g, selects[i], messageLeft, _nTop - _messageFont.getAscent(), _fontColor);
				if ((cursor != null) && isSelect) {
					g.draw(cursor, nLeft, _nTop - cursor.getHeight() / 2, _cursorColor);
				}

			}
		}
		g.setColor(oldColor);
	}

	public LSelect setCursorColor(LColor c) {
		_cursorColor = c;
		return this;
	}

	public LColor getCursorColor() {
		return _cursorColor;
	}

	public boolean isClick() {
		return clicked;
	}

	@Override
	protected void processTouchPressed() {
		if (!eventClick.isPressed()) {
			this.clicked = false;
			super.processTouchPressed();
			eventClick.press();
		}
	}

	@Override
	protected void processTouchReleased() {
		if (eventClick.isPressed()) {
			this.clicked = true;
			if ((this.selects != null) && (this.selectFlag > 0)) {
				this.result = this.selects[selectFlag - 1];
			}
			super.processTouchReleased();
			eventClick.release();
		}
	}

	@Override
	protected void processKeyPressed() {
		super.processKeyPressed();
		if (this.isSelected() && this.isKeyDown(SysKey.ENTER)) {
			this.clicked = true;
		}
	}

	@Override
	public LColor getFontColor() {
		return _fontColor.cpy();
	}

	public LSelect setFontColor(LColor f) {
		this._fontColor = f;
		return this;
	}

	public IFont getMessageFont() {
		return _messageFont;
	}

	public LSelect setMessageFont(IFont m) {
		this._messageFont = m;
		return this;
	}

	@Override
	public LSelect setFont(IFont newFont) {
		return this.setMessageFont(newFont);
	}

	@Override
	public IFont getFont() {
		return getMessageFont();
	}

	public LTexture getCursor() {
		return cursor;
	}

	public LSelect setNotCursor() {
		this.cursor = null;
		return this;
	}

	public LSelect setCursor(LTexture cursor) {
		this.cursor = cursor;
		return this;
	}

	public LSelect setCursor(String fileName) {
		setCursor(LSystem.loadTexture(fileName));
		return this;
	}

	public LTexture getBuoyage() {
		return buoyage;
	}

	public LSelect setNotBuoyage() {
		this.cursor = null;
		return this;
	}

	public LSelect setBuoyage(LTexture buoyage) {
		this.buoyage = buoyage;
		return this;
	}

	public LSelect setBuoyage(String fileName) {
		setBuoyage(LSystem.loadTexture(fileName));
		return this;
	}

	public boolean isFlashBuoyage() {
		return isAutoAlpha;
	}

	public LSelect setFlashBuoyage(boolean flashBuoyage) {
		this.isAutoAlpha = flashBuoyage;
		return this;
	}

	public int getSpace() {
		return space;
	}

	public LSelect setSpace(int space) {
		this.space = space;
		return this;
	}

	@Override
	public void createUI(GLEx g, int x, int y) {

	}

	@Override
	public String getUIName() {
		return "Select";
	}

	@Override
	public void destory() {

	}

}
