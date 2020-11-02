package org.loon.framework.javase.game.core.graphics.component;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyEvent;

import org.loon.framework.javase.game.action.sprite.Animation;
import org.loon.framework.javase.game.core.LSystem;
import org.loon.framework.javase.game.core.graphics.LComponent;
import org.loon.framework.javase.game.core.graphics.LContainer;
import org.loon.framework.javase.game.core.graphics.LFont;
import org.loon.framework.javase.game.core.graphics.LImage;
import org.loon.framework.javase.game.core.graphics.device.LGraphics;

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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class LMessage extends LContainer {

	private Animation animation;

	private LFont messageFont = LFont.getFont("黑体", 0, 20);

	private Color fontColor = Color.white;

	private long printTime, totalDuration;

	private int dx, dy, dw, dh;

	private Print print;

	public LMessage(int width, int height) {
		this(0, 0, width, height);
	}

	public LMessage(int x, int y, int width, int height) {
		this((LImage) null, x, y, width, height);
	}

	public LMessage(String fileName, int x, int y) {
		this(new LImage(fileName), x, y);
	}

	public LMessage(Image img, int x, int y) {
		this(new LImage(img), x, y);
	}

	public LMessage(LImage formImage, int x, int y) {
		this(formImage, x, y, formImage.getWidth(), formImage.getHeight());
	}

	public LMessage(LImage formImage, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.animation = new Animation();
		if (formImage == null) {
			this.setBackground(new LImage(width, height, true));
			this.setAlpha(0.3F);
		} else {
			this.setBackground(formImage);
			if (width == -1) {
				width = formImage.getWidth();
			}
			if (height == -1) {
				height = formImage.getHeight();
			}
		}
		this.print = new Print(getLocation(), messageFont, width, height);
		this.setTipIcon(LSystem.FRAMEWORK_IMG_NAME + "creese.png");
		this.totalDuration = 80;
		this.customRendering = true;
		this.setWait(false);
		this.setElastic(true);
		this.setLocked(true);
		this.setLayer(100);
	}

	public void setWait(boolean flag) {
		print.setWait(flag);
	}

	public boolean isWait() {
		return print.isWait();
	}

	public void complete() {
		print.complete();
	}

	public void setLeftOffset(int left) {
		print.setLeftOffset(left);
	}

	public void setTopOffset(int top) {
		print.setTopOffset(top);
	}

	public int getLeftOffset() {
		return print.getLeftOffset();
	}

	public int getTopOffset() {
		return print.getTopOffset();
	}

	public int getMessageLength() {
		return print.getMessageLength();
	}

	public void setMessageLength(int messageLength) {
		print.setMessageLength(messageLength);
	}

	public void setTipIcon(String fileName) {
		print.setCreeseIcon(new LImage(fileName));
	}

	public void setNotTipIcon() {
		print.setCreeseIcon((LImage) null);
	}

	public void setEnglish(boolean e) {
		print.setEnglish(true);
	}

	public boolean isEnglish() {
		return print.isEnglish();
	}

	public void setDelay(long delay) {
		this.totalDuration = (delay < 1 ? 1 : delay);
	}

	public long getDelay() {
		return totalDuration;
	}

	public boolean isComplete() {
		return print.isComplete();
	}

	public void setPauseIconAnimationLocation(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}

	public void setMessage(String context, boolean isComplete) {
		print.setMessage(context, messageFont, isComplete);
	}

	public void setMessage(String context) {
		print.setMessage(context, messageFont);
	}

	public String getMessage() {
		return print.getMessage();
	}

	/**
	 * 处理点击事件（请重载实现）
	 * 
	 */
	public void doClick() {

	}

	protected void processTouchClicked() {
		this.doClick();
	}

	protected void processKeyPressed() {
		if (this.isSelected()
				&& this.input.getKeyPressed() == KeyEvent.VK_ENTER) {
			this.doClick();
		}
	}

	public void update(long elapsedTime) {
		if (!visible) {
			return;
		}
		super.update(elapsedTime);
		if (print.isComplete()) {
			animation.update(elapsedTime);
		}
		printTime += elapsedTime;
		if (printTime >= totalDuration) {
			printTime = printTime % totalDuration;
			print.next();
		}
	}

	protected synchronized void createCustomUI(LGraphics g, int x, int y,
			int w, int h) {
		if (!visible) {
			return;
		}
		LFont oldFont = g.getLFont();
		g.setColor(fontColor);
		g.setFont(messageFont);
		print.draw(g, fontColor);
		g.setFont(oldFont);
		g.resetColor();
		if (print.isComplete() && animation != null) {
			if (animation.getSpriteImage() != null) {
				g.setAlpha(1.0F);
				updateIcon();
				g.drawImage(animation.getSpriteImage().getLImage(), dx, dy);
			}
		}
	}

	protected void processTouchDragged() {
		if (!locked) {
			if (getContainer() != null) {
				getContainer().sendToFront(this);
			}
			this.move(this.input.getTouchDX(), this.input.getTouchDY());
			this.updateIcon();
		}
	}

	public void setPauseIconAnimation(Animation animation) {
		this.animation = animation;
		if (animation != null) {
			LImage image = animation.getSpriteImage(0).getLImage();
			if (image != null) {
				this.dw = image.getWidth();
				this.dh = image.getHeight();
				this.updateIcon();
			}
		}
	}

	private void updateIcon() {
		this.setPauseIconAnimationLocation(getScreenX() + getWidth() - dw / 2
				- 20, getScreenY() + getHeight() - dh - 10);
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

	public void createUI(LGraphics g, int x, int y, LComponent component,
			LImage[] buttonImage) {

	}

	public void setVisible(boolean v) {
		super.setVisible(v);
	}

	public String getUIName() {
		return "Message";
	}

	public void dispose() {
		super.dispose();
		print.dispose();
	}
}
