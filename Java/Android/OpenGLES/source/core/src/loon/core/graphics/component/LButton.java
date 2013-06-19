package loon.core.graphics.component;

import loon.core.graphics.LColor;
import loon.core.graphics.LComponent;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.graphics.opengl.TextureUtils;
import loon.core.input.LInputFactory.Key;
import loon.core.input.LInputFactory.Touch;


/**
 * 
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

public class LButton extends LComponent {

	private String text = null;

	private boolean over, pressed, exception;

	private int pressedTime, offsetLeft, offsetTop, type;

	private LFont font = LFont.getDefaultFont();

	private LColor fontColor = LColor.white;

	public LButton(String fileName) {
		this(fileName, null, 0, 0);
	}

	public LButton(String fileName, String text, int x, int y) {
		this(new LTexture(fileName), text, x, y);
	}

	public LButton(LTexture img, String text, int x, int y) {
		this(img, text, img.getWidth(), img.getHeight(), x, y);
	}

	public LButton(String fileName, int row, int col) {
		this(fileName, null, row, col, 0, 0);
	}

	public LButton(String fileName, String text, int row, int col, int x, int y) {
		this(LTextures.loadTexture(fileName), text, row, col, x, y);
	}

	public LButton(LTexture img, String text, int row, int col, int x, int y) {
		this(TextureUtils.getSplitTextures(img, row, col), text, row, col, x, y);
	}

	public LButton(LTexture[] img, String text, int row, int col, int x, int y) {
		super(x, y, row, col);
		this.setImages(img);
		this.text = text;
	}

	public LButton(String text, int x, int y, int w, int h) {
		super(x, y, w, h);
		this.text = text;
	}

	public void setImages(LTexture... images) {
		LTexture[] buttons = new LTexture[4];
		if (images != null) {
			int size = images.length;
			this.type = size;
			switch (size) {
			case 1:
				buttons[0] = images[0];
				buttons[1] = images[0];
				buttons[2] = images[0];
				buttons[3] = images[0];
				break;
			case 2:
				buttons[0] = images[0];
				buttons[1] = images[1];
				buttons[2] = images[0];
				buttons[3] = images[0];
				break;
			case 3:
				buttons[0] = images[0];
				buttons[1] = images[1];
				buttons[2] = images[2];
				buttons[3] = images[0];
				break;
			case 4:
				buttons = images;
				break;
			default:
				exception = true;
				break;
			}
		}
		if (!exception) {
			this.setImageUI(buttons, true);
		}

	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		LButton button = (LButton) component;
		if (buttonImage != null) {
			if (!button.isEnabled()) {
				g.drawTexture(buttonImage[3], x, y);
			} else if (button.isTouchPressed()) {
				g.drawTexture(buttonImage[2], x, y);
			} else if (button.isTouchOver()) {
				g.drawTexture(buttonImage[1], x, y);
			} else {
				if (type == 1) {
					g.drawTexture(buttonImage[0], x, y, LColor.gray);
				} else {
					g.drawTexture(buttonImage[0], x, y);
				}
			}
		}
		if (text != null) {
			LFont old = g.getFont();
			g.setFont(font);
			g.setColor(fontColor);
			g.drawString(
					text,
					x + button.getOffsetLeft()
							+ (button.getWidth() - font.stringWidth(text)) / 2,
					y + button.getOffsetTop()
							+ (button.getHeight() - font.getLineHeight()) / 2
							+ font.getLineHeight());
			g.setFont(old);
			g.resetColor();
		}
	}

	@Override
	public void update(long timer) {
		if (this.pressedTime > 0 && --this.pressedTime <= 0) {
			this.pressed = false;
		}
	}

	public boolean isTouchOver() {
		return this.over;
	}

	public boolean isTouchPressed() {
		return this.pressed;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String st) {
		this.text = st;
	}

	@Override
	protected void processTouchDragged() {
		if (this.input.getTouchPressed() == Touch.TOUCH_MOVE) {
			this.over = this.pressed = this.intersects(this.input.getTouchX(),
					this.input.getTouchY());
		}
	}

	/**
	 * 处理点击事件（请重载实现）
	 * 
	 */
	public void doClick() {
		if (Click != null) {
			Click.DoClick(this);
		}
	}

	public void downClick() {
		if (Click != null) {
			Click.DownClick(this,input.getTouchX(), input.getTouchY());
		}
	}

	public void upClick() {
		if (Click != null) {
			Click.UpClick(this,input.getTouchX(), input.getTouchY());
		}
	}

	@Override
	protected void processTouchClicked() {
		if (this.input.getTouchReleased() == Touch.TOUCH_UP) {
			this.doClick();
		}
	}

	@Override
	protected void processTouchPressed() {
		if (this.input.getTouchPressed() == Touch.TOUCH_DOWN) {
			this.downClick();
			this.pressed = true;
		}
	}

	@Override
	protected void processTouchReleased() {
		if (this.input.getTouchReleased() == Touch.TOUCH_UP) {
			this.upClick();
			this.pressed = false;
		}
	}

	@Override
	protected void processTouchEntered() {
		this.over = true;
	}

	@Override
	protected void processTouchExited() {
		this.over = this.pressed = false;
	}

	@Override
	protected void processKeyPressed() {
		if (this.isSelected() && this.input.getKeyPressed() == Key.ENTER) {
			this.pressedTime = 5;
			this.pressed = true;
			this.doClick();
		}
	}

	@Override
	protected void processKeyReleased() {
		if (this.isSelected() && this.input.getKeyReleased() == Key.ENTER) {
			this.pressed = false;
		}
	}

	public boolean isException() {
		return exception;
	}

	@Override
	public String getUIName() {
		return "Button";
	}

	public LFont getFont() {
		return font;
	}

	public void setFont(LFont font) {
		this.font = font;
	}

	public LColor getFontColor() {
		return fontColor;
	}

	public void setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
	}

	public int getOffsetLeft() {
		return offsetLeft;
	}

	public void setOffsetLeft(int offsetLeft) {
		this.offsetLeft = offsetLeft;
	}

	public int getOffsetTop() {
		return offsetTop;
	}

	public void setOffsetTop(int offsetTop) {
		this.offsetTop = offsetTop;
	}

}
