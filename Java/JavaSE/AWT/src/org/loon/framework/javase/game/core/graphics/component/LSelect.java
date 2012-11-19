package org.loon.framework.javase.game.core.graphics.component;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;

import org.loon.framework.javase.game.core.LSystem;
import org.loon.framework.javase.game.core.graphics.LComponent;
import org.loon.framework.javase.game.core.graphics.LContainer;
import org.loon.framework.javase.game.core.graphics.LFont;
import org.loon.framework.javase.game.core.graphics.LImage;
import org.loon.framework.javase.game.core.graphics.device.LGraphics;
import org.loon.framework.javase.game.core.timer.LTimer;

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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class LSelect extends LContainer {

	private LFont messageFont = LFont.getFont(LSystem.FONT, 0, 20);

	private Color fontColor = Color.white;

	private int left, top, type, nTop;

	private int sizeFont, doubleSizeFont, tmpOffset, messageLeft, nLeft,
			messageTop, selectSize, selectFlag;

	private float autoAlpha;

	private LTimer delay;

	private String[] selects;

	private String message, result;

	private LImage cursor, buoyage;

	private boolean isAutoAlpha, isSelect;

	public LSelect(int x, int y, int width, int height) {
		this((LImage) null, x, y, width, height);
	}

	public LSelect(String fileName) {
		this(fileName, 0, 0);
	}

	public LSelect(String fileName, int x, int y) {
		this(new LImage(fileName), x, y);
	}

	public LSelect(Image img) {
		this(img, 0, 0);
	}

	public LSelect(Image img, int x, int y) {
		this(new LImage(img), x, y);
	}

	public LSelect(LImage formImage) {
		this(formImage, 0, 0);
	}

	public LSelect(LImage formImage, int x, int y) {
		this(formImage, x, y, formImage.getWidth(), formImage.getHeight());
	}

	public LSelect(LImage formImage, int x, int y, int width, int height) {
		super(x, y, width, height);
		if (formImage == null) {
			this.setBackground(new LImage(width, height, true));
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
			result[i] = (String) list.get(i);
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

	protected void createCustomUI(LGraphics g, int x, int y, int w, int h) {
		if (!visible) {
			return;
		}
		Color oldColor = g.getColor();
		LFont oldFont = g.getLFont();
		g.setColor(fontColor);
		g.setFont(messageFont);
		sizeFont = messageFont.getSize();
		doubleSizeFont = sizeFont * 2;
		if (doubleSizeFont == 0) {
			doubleSizeFont = 20;
		}
		messageLeft = (x + doubleSizeFont + sizeFont / 2) + tmpOffset + left
				+ doubleSizeFont;
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
					g.drawImage(buoyage, nLeft, nTop
							- (int) (buoyage.getHeight() / 1.5));
					g.setAlpha(1.0F);
				}
				g.drawString(selects[i], messageLeft, nTop);
				if ((cursor != null) && isSelect) {
					g.drawImage(cursor, nLeft, nTop - cursor.getHeight() / 2);
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

	}

	public boolean isClick() {
		return onClick;
	}

	protected void processTouchClicked() {
		if (input.getTouchReleased() == MouseEvent.BUTTON1) {
			if ((selects != null) && (selectFlag > 0)) {
				this.result = selects[selectFlag - 1];
			}
			this.doClick();
			this.onClick = true;
		} else {
			this.onClick = false;
		}
	}

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

	protected void processKeyPressed() {
		if (this.isSelected()
				&& this.input.getKeyPressed() == KeyEvent.VK_ENTER) {
			this.doClick();
		}
	}

	protected void processTouchDragged() {
		processTouchMoved();
		if (!locked) {
			if (getContainer() != null) {
				getContainer().sendToFront(this);
			}
			this.move(this.input.getTouchDX(), this.input.getTouchDY());
		}
	}

	public Color getFontColor() {
		return fontColor;
	}

	public void setFontColor(Color fontColor) {
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

	protected void validateSize() {
		super.validateSize();
	}

	public LImage getCursor() {
		return cursor;
	}

	public void setNotCursor() {
		this.cursor = null;
	}

	public void setCursor(LImage cursor) {
		this.cursor = cursor;
	}

	public void setCursor(String fileName) {
		setCursor(new LImage(fileName));
	}

	public LImage getBuoyage() {
		return buoyage;
	}

	public void setNotBuoyage() {
		this.cursor = null;
	}

	public void setBuoyage(LImage buoyage) {
		this.buoyage = buoyage;
	}

	public void setBuoyage(String fileName) {
		setBuoyage(new LImage(fileName));
	}

	public boolean isFlashBuoyage() {
		return isAutoAlpha;
	}

	public void setFlashBuoyage(boolean flashBuoyage) {
		this.isAutoAlpha = flashBuoyage;
	}

	public void setVisible(boolean v) {
		super.setVisible(v);
	}

	public void createUI(LGraphics g, int x, int y, LComponent component,
			LImage[] buttonImage) {

	}

	public String getUIName() {
		return "Select";
	}

}
