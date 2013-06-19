package loon.core.graphics.component;

import java.util.List;

import loon.core.LSystem;
import loon.core.graphics.LColor;
import loon.core.graphics.LComponent;
import loon.core.graphics.LContainer;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.graphics.opengl.LTexture.Format;
import loon.core.input.LInputFactory.Key;
import loon.core.timer.LTimer;

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
public class LSelect extends LContainer {

	private LFont messageFont = LFont.getDefaultFont();

	private LColor fontColor = LColor.white;

	private int left, top, type, nTop;

	private int sizeFont, doubleSizeFont, tmpOffset, messageLeft, nLeft,
			messageTop, selectSize, selectFlag;

	private float autoAlpha;

	private LTimer delay;

	private String[] selects;

	private String message, result;

	private LTexture cursor, buoyage;

	private boolean isAutoAlpha, isSelect;

	public LSelect(int x, int y, int width, int height) {
		this((LTexture) null, x, y, width, height);
	}

	public LSelect(String fileName) {
		this(fileName, 0, 0);
	}

	public LSelect(String fileName, int x, int y) {
		this(LTextures.loadTexture(fileName), x, y);
	}

	public LSelect(LTexture formImage) {
		this(formImage, 0, 0);
	}

	public LSelect(LTexture formImage, int x, int y) {
		this(formImage, x, y, formImage.getWidth(), formImage.getHeight());
	}

	public LSelect(LTexture formImage, int x, int y, int width, int height) {
		super(x, y, width, height);
		if (formImage == null) {
			this.setBackground(new LTexture(width, height, true, Format.SPEED));
			this.setAlpha(0.3F);
		} else {
			this.setBackground(formImage);
		}
		this.customRendering = true;
		this.selectFlag = 1;
		this.tmpOffset = -(width / 10);
		this.delay = new LTimer(150);
		this.autoAlpha = 0.25F;
		this.isAutoAlpha = true;
		this.setCursor(LSystem.FRAMEWORK_IMG_NAME + "creese.png");
		this.setElastic(true);
		this.setLocked(true);
		this.setLayer(100);
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

	private static String[] getListToStrings(List<String> list) {
		if (list == null || list.size() == 0)
			return null;
		String[] result = new String[list.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = list.get(i);
		}
		return result;
	}

	public void setMessage(String message, List<String> list) {
		setMessage(message, getListToStrings(list));
	}

	public void setMessage(String[] selects) {
		setMessage(null, selects);
	}

	public void setMessage(String message, String[] selects) {
		this.message = message;
		this.selects = selects;
		this.selectSize = selects.length;
		if (doubleSizeFont == 0) {
			doubleSizeFont = 20;
		}
	}

	@Override
	public void update(long elapsedTime) {
		if (!visible) {
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
		if (!visible) {
			return;
		}
		LColor oldColor = g.getColor();
		LFont oldFont = g.getFont();
		g.setColor(fontColor);
		g.setFont(messageFont);
		sizeFont = messageFont.getSize();
		doubleSizeFont = sizeFont * 2;
		if (doubleSizeFont == 0) {
			doubleSizeFont = 20;
		}
		messageLeft = (x + doubleSizeFont + sizeFont / 2) + tmpOffset + left
				+ doubleSizeFont;
		// g.setAntiAlias(true);
		if (message != null) {
			messageTop = y + doubleSizeFont + top - 10;
			g.drawString(message, messageLeft, messageTop);
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
					g.drawTexture(buoyage, nLeft,
							nTop - (int) (buoyage.getHeight() / 1.5));
					g.setAlpha(1.0F);
				}
				g.drawString(selects[i], messageLeft, nTop);
				if ((cursor != null) && isSelect) {
					g.drawTexture(cursor, nLeft, nTop - cursor.getHeight() / 2,
							LColor.white);
				}

			}
		}
		// g.setAntiAlias(false);
		g.setColor(oldColor);
		g.setFont(oldFont);

	}

	private boolean onClick;

	/**
	 * 处理点击事件（请重载实现）
	 * 
	 */
	public void doClick() {
		if (Click != null) {
			Click.DoClick(this);
		}
	}

	public boolean isClick() {
		return onClick;
	}

	@Override
	protected void processTouchClicked() {
		if (!input.isMoving()) {
			if ((selects != null) && (selectFlag > 0)) {
				this.result = selects[selectFlag - 1];
			}
			this.doClick();
			this.onClick = true;
		} else {
			this.onClick = false;
		}
	}

	@Override
	protected synchronized void processTouchMoved() {
		if (selects != null) {
			int touchY = input.getTouchY();
			selectFlag = selectSize
					- (((nTop + 30) - (touchY == 0 ? 1 : touchY)) / doubleSizeFont);
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
		if (this.isSelected() && this.input.getKeyPressed() == Key.ENTER) {
			this.doClick();
		}
	}

	@Override
	protected void processTouchDragged() {
		processTouchMoved();
		if (!locked) {
			if (getContainer() != null) {
				getContainer().sendToFront(this);
			}
			this.move(this.input.getTouchDX(), this.input.getTouchDY());
		}
	}

	public LColor getFontColor() {
		return fontColor;
	}

	public void setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
	}

	public LFont getMessageFont() {
		return messageFont;
	}

	public void setMessageFont(LFont messageFont) {
		this.messageFont = messageFont;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
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
		setCursor(new LTexture(fileName));
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
		setBuoyage(new LTexture(fileName));
	}

	public boolean isFlashBuoyage() {
		return isAutoAlpha;
	}

	public void setFlashBuoyage(boolean flashBuoyage) {
		this.isAutoAlpha = flashBuoyage;
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {

	}

	@Override
	public String getUIName() {
		return "Select";
	}

}
