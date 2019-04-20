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
package loon.component;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.event.CallFunction;
import loon.font.FontSet;
import loon.font.IFont;
import loon.opengl.GLEx;
import loon.opengl.TextureUtils;

/**
 * 按钮用UI,它和LClickButton的主要差异在于，这个按钮必须依赖纹理生成,也就是有图才能用(而且最好注入4张图,对应四种状态)
 */
public class LButton extends LComponent implements FontSet<LButton> {

	private String text = null;

	private boolean over, pressed, exception, selected;

	private int pressedTime, offsetLeft, offsetTop, type;

	private IFont font;

	private LColor fontColor = LColor.white;

	private CallFunction _function;

	public LButton(String fileName) {
		this(fileName, null, 0, 0);
	}

	public LButton(String fileName, String text, int row, int col) {
		this(LSystem.loadTexture(fileName), text, row, col, 0, 0);
	}

	public LButton(String fileName, int row, int col) {
		this(fileName, null, row, col, 0, 0);
	}

	public LButton(String fileName, String text, int row, int col, int x, int y) {
		this(LSystem.loadTexture(fileName), text, row, col, x, y);
	}

	public LButton(LTexture img, String text, int row, int col, int x, int y) {
		this(TextureUtils.getSplitTextures(img, row, col), text, row, col, x, y);
	}

	public LButton(LTexture[] img, String text, int row, int col, int x, int y) {
		this(LSystem.getSystemGameFont(), img, text, row, col, x, y);
	}

	public LButton(IFont font, LTexture[] img, String text, int row, int col, int x, int y) {
		super(x, y, row, col);
		this.font = font;
		this.text = text;
		if (img != null) {
			this.setImages(img);
		}
		freeRes().add(img);
	}

	public LButton(String text, int x, int y, int w, int h) {
		this(LSystem.getSystemGameFont(), text, x, y, w, h);
	}

	public LButton(IFont font, String text, int x, int y, int w, int h) {
		super(x, y, w, h);
		this.font = font;
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
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		LButton button = (LButton) component;
		if (buttonImage != null) {
			if (!button.isEnabled()) {
				g.draw(buttonImage[3], x, y, _component_baseColor);
			} else if (button.isTouchPressed()) {
				g.draw(buttonImage[2], x, y, _component_baseColor);
			} else if (button.isTouchOver()) {
				g.draw(buttonImage[1], x, y, _component_baseColor);
			} else {
				if (type == 1) {
					g.draw(buttonImage[0], x, y, _component_baseColor == null ? LColor.gray : _component_baseColor.mul(LColor.gray));
				} else {
					g.draw(buttonImage[0], x, y, _component_baseColor);
				}
			}
		}
		if (text != null) {
			int tmp = g.color();
			g.setColor(fontColor);
			font.drawString(g, text, x + button.getOffsetLeft() + (button.getWidth() - font.stringWidth(text)) / 2,
					y + button.getOffsetTop() + (button.getHeight() - font.getHeight() - font.getAscent()) / 2);
			g.setColor(tmp);
		}
	}

	@Override
	public void update(long elapsedTime) {
		if (!isVisible()) {
			return;
		}
		super.update(elapsedTime);
		if (selected) {
			this.pressed = true;
			return;
		}
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

	public void checked() {
		this.pressed = true;
		this.selected = true;
	}

	public void unchecked() {
		this.pressed = false;
		this.selected = false;
	}

	@Override
	protected void processTouchDragged() {
		this.over = this.pressed = this.intersects(getUITouchX(), getUITouchY());
		super.processTouchDragged();
	}

	@Override
	protected void processTouchPressed() {
		super.processTouchPressed();
		this.pressed = true;
	}

	@Override
	protected void processTouchReleased() {
		super.processTouchReleased();
		if (_function != null) {
			try {
				_function.call(this);
			} catch (Throwable t) {
				LSystem.error("LButton call() exception", t);
			}
		}
		this.pressed = false;
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
		if (this.isSelected()) {
			this.pressedTime = 5;
			this.pressed = true;
			this.doClick();
		}
	}

	@Override
	protected void processKeyReleased() {
		if (this.isSelected()) {
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

	@Override
	public IFont getFont() {
		return font;
	}

	@Override
	public LButton setFont(IFont font) {
		this.font = font;
		return this;
	}

	public CallFunction getFunction() {
		return _function;
	}

	public void setFunction(CallFunction function) {
		this._function = function;
	}

	@Override
	public LColor getFontColor() {
		return fontColor.cpy();
	}

	@Override
	public LButton setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
		return this;
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
