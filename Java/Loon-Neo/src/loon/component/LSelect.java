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
import loon.LTextures;
import loon.canvas.LColor;
import loon.component.skin.MessageSkin;
import loon.component.skin.SkinManager;
import loon.event.SysKey;
import loon.font.FontSet;
import loon.font.IFont;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

/**
 * 一个选项器UI,与LMenuSelect的最大区别在于这个的UI大小是固定的,而LMenuSelect会随着注入的内容不同而自行改变UI大小
 */
public class LSelect extends LContainer implements FontSet<LSelect> {

	private IFont messageFont;

	private LColor fontColor = LColor.white;

	private int left, top, type, nTop;

	private int sizeFont, doubleSizeFont, tmpOffset, messageLeft, nLeft, messageTop, selectSize, selectFlag;

	private float autoAlpha;

	private LTimer delay;

	private String[] selects;

	private String message, result;

	private LTexture cursor, buoyage;

	private boolean isAutoAlpha, isSelect;

	public LSelect(IFont font, int x, int y, int width, int height) {
		this(font, (LTexture) null, x, y, width, height);
	}

	public LSelect(IFont font, String fileName) {
		this(font, fileName, 0, 0);
	}

	public LSelect(IFont font, String fileName, int x, int y) {
		this(font, LTextures.loadTexture(fileName), x, y);
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

	public LSelect(MessageSkin skin, int x, int y, int width, int height) {
		this(skin.getFont(), skin.getBackgroundTexture(), x, y, width, height, skin.getFontColor());
	}

	public LSelect(IFont font, LTexture formImage, int x, int y, int width, int height, LColor fontColor) {
		super(x, y, width, height);
		if (formImage == null) {
			this.setBackground(LTextures.createTexture(width, height, LTexture.Format.LINEAR));
			this.setAlpha(0.3F);
		} else {
			this.setBackground(formImage);
		}
		this.fontColor = fontColor;
		this.messageFont = (font == null ? LSystem.getSystemGameFont() : font);
		this.customRendering = true;
		this.selectFlag = 1;
		this.tmpOffset = -(width / 10);
		this.delay = new LTimer(150);
		this.autoAlpha = 0.25F;
		this.isAutoAlpha = true;
		this.setCursor(LSystem.FRAMEWORK_IMG_NAME + "creese.png");
		this.setElastic(true);
		this.setLocked(true);
	}

	public void setLeftOffset(int left) {
		this.left = left;
	}

	public void setTopOffset(int top) {
		this.top = top;
	}

	public int getLeftOffset() {
		return left;
	}

	public int getTopOffset() {
		return top;
	}

	public int getResultIndex() {
		return selectFlag - 1;
	}

	public void setDelay(long timer) {
		delay.setDelay(timer);
	}

	public long getDelay() {
		return delay.getDelay();
	}

	public String getResult() {
		return result;
	}

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

	public void setMessage(String message, TArray<String> list) {
		setMessage(message, getListToStrings(list));
	}

	public void setMessage(String[] selects) {
		setMessage(null, selects);
	}

	public void setMessage(TArray<String> list) {
		setMessage(null, list);
	}

	public void setMessage(String message, String[] selects) {
		this.message = message;
		this.selects = selects;
		this.selectSize = selects.length;
		if (doubleSizeFont == 0) {
			doubleSizeFont = 20;
		}
		if (messageFont instanceof LFont) {
			LSTRDictionary.get().bind((LFont) messageFont, selects);
		}
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
	}

	@Override
	protected void createCustomUI(GLEx g, int x, int y, int w, int h) {
		if (!isVisible()) {
			return;
		}
		LColor oldColor = g.getColor();
		sizeFont = messageFont.getSize();
		doubleSizeFont = sizeFont * 2;
		if (doubleSizeFont == 0) {
			doubleSizeFont = 20;
		}
		messageLeft = (x + doubleSizeFont + sizeFont / 2) + tmpOffset + left + doubleSizeFont;
		if (message != null) {
			messageTop = y + doubleSizeFont + top - 10;
			messageFont.drawString(g, message, messageLeft, messageTop - messageFont.getAscent(), fontColor);
		} else {
			messageTop = y + top;
		}
		nTop = messageTop;
		if (selects != null) {
			nLeft = messageLeft - sizeFont / 4;
			for (int i = 0; i < selects.length; i++) {
				nTop += 30;
				type = i + 1;
				isSelect = (type == (selectFlag > 0 ? selectFlag : 1));
				if ((buoyage != null) && isSelect) {
					g.setAlpha(autoAlpha);
					g.draw(buoyage, nLeft, nTop - (int) (buoyage.getHeight() / 1.5), baseColor);
					g.setAlpha(1F);
				}
				messageFont.drawString(g, selects[i], messageLeft, nTop - messageFont.getAscent(), fontColor);
				if ((cursor != null) && isSelect) {
					g.draw(cursor, nLeft, nTop - cursor.getHeight() / 2, LColor.white);
				}

			}
		}
		g.setColor(oldColor);
	}

	private boolean onClick;

	public boolean isClick() {
		return onClick;
	}

	@Override
	protected void processTouchClicked() {
		if (!input.isMoving()) {
			if ((selects != null) && (selectFlag > 0)) {
				this.result = selects[selectFlag - 1];
			}
			super.processTouchClicked();
			this.onClick = true;
		} else {
			this.onClick = false;
		}
	}

	@Override
	protected void processTouchReleased() {
		super.processTouchReleased();
		if (LSystem.base() != null && (LSystem.base().isMobile() || LSystem.base().setting.emulateTouch)) {
			this.processTouchMoved();
		}
	}

	@Override
	protected synchronized void processTouchMoved() {
		if (selects != null) {
			int touchY = input.getTouchY();
			selectFlag = selectSize - (((nTop + 30) - (touchY == 0 ? 1 : touchY)) / doubleSizeFont);
			if (selectFlag < 1) {
				selectFlag = 0;
			}
			if (selectFlag > selectSize) {
				selectFlag = selectSize;
			}
		}
	}

	@Override
	protected void processKeyPressed() {
		if (this.isSelected() && this.input.getKeyPressed() == SysKey.ENTER) {
			this.doClick();
		}
	}

	public LColor getFontColor() {
		return fontColor;
	}

	public void setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
	}

	public IFont getMessageFont() {
		return messageFont;
	}

	public LSelect setMessageFont(IFont messageFont) {
		this.messageFont = messageFont;
		return this;
	}

	public LSelect setFont(IFont newFont) {
		return this.setMessageFont(newFont);
	}

	public IFont getFont() {
		return getMessageFont();
	}

	public LTexture getCursor() {
		return cursor;
	}

	public void setNotCursor() {
		this.cursor = null;
	}

	public void setCursor(LTexture cursor) {
		this.cursor = cursor;
	}

	public void setCursor(String fileName) {
		setCursor(LTextures.loadTexture(fileName));
	}

	public LTexture getBuoyage() {
		return buoyage;
	}

	public void setNotBuoyage() {
		this.cursor = null;
	}

	public void setBuoyage(LTexture buoyage) {
		this.buoyage = buoyage;
	}

	public void setBuoyage(String fileName) {
		setBuoyage(LTextures.loadTexture(fileName));
	}

	public boolean isFlashBuoyage() {
		return isAutoAlpha;
	}

	public void setFlashBuoyage(boolean flashBuoyage) {
		this.isAutoAlpha = flashBuoyage;
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {

	}

	@Override
	public String getUIName() {
		return "Select";
	}

}
