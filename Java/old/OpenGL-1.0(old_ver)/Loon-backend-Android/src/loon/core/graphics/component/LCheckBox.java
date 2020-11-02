
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
 */
package loon.core.graphics.component;

import loon.Key;
import loon.Touch;
import loon.core.graphics.LComponent;
import loon.core.graphics.device.LColor;
import loon.core.graphics.device.LFont;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.utils.MathUtils;

public class LCheckBox extends LComponent {

	private LTexture unchecked, checked;

	private float boxsize;

	private boolean boxtoleftoftext = false, showtext = true;

	private LColor fontColor;

	private LFont font;

	private boolean pressed = false, over = false, ticked = false;

	private long pressedTime = 0;

	private String text;

	public LCheckBox(String txt, int x, int y) {
		this(txt, x, y, LColor.white);
	}

	public LCheckBox(String txt, int x, int y, LColor textcolor) {
		this(txt, x, y, textcolor, LFont.getDefaultFont());
	}

	public LCheckBox(String txt, int x, int y, LColor textcolor, LFont font) {
		this(txt, x, y, DefUI.getDefaultTextures(5), DefUI
				.getDefaultTextures(6), DefUI.getDefaultTextures(5).getWidth(),
				true, textcolor, font);
	}

	public LCheckBox(String txt, int x, int y, int boxsize) {
		this(txt, x, y, DefUI.getDefaultTextures(5), DefUI
				.getDefaultTextures(6), boxsize, true, LColor.white, LFont
				.getDefaultFont());
	}

	public LCheckBox(String txt, int x, int y, int boxsize, LColor textcolor) {
		this(txt, x, y, DefUI.getDefaultTextures(5), DefUI
				.getDefaultTextures(6), boxsize, true, textcolor, LFont
				.getDefaultFont());
	}

	public LCheckBox(String txt, int x, int y, int boxsize,
			boolean boxtoleftoftext) {
		this(txt, x, y, boxsize, boxtoleftoftext, LFont.getDefaultFont());
	}

	public LCheckBox(String txt, int x, int y, int boxsize,
			boolean boxtoleftoftext, LFont font) {
		this(txt, x, y, DefUI.getDefaultTextures(5), DefUI
				.getDefaultTextures(6), boxsize, boxtoleftoftext, LColor.white,
				font);
	}

	public LCheckBox(String txt, int x, int y, int boxsize,
			boolean boxtoleftoftext, LColor textcolor, LFont font) {
		this(txt, x, y, DefUI.getDefaultTextures(5), DefUI
				.getDefaultTextures(6), boxsize, boxtoleftoftext, textcolor,
				font);
	}

	public LCheckBox(String txt, int x, int y, String uncheckedFile,
			String checkedFile, int boxsize, boolean boxtoleftoftext,
			LColor textcolor, LFont font) {
		this(txt, x, y, LTextures.loadTexture(uncheckedFile), LTextures
				.loadTexture(checkedFile), boxsize, boxtoleftoftext, textcolor,
				font);
	}

	public LCheckBox(String txt, int x, int y, LTexture unchecked,
			LTexture checked, int boxsize, boolean boxtoleftoftext,
			LColor textcolor, LFont font) {
		super(x, y, font.stringWidth(txt) + boxsize, MathUtils.max(
				font.getHeight(), boxsize));
		this.text = txt;
		this.unchecked = unchecked;
		this.checked = checked;
		this.boxsize = boxsize;
		this.boxtoleftoftext = boxtoleftoftext;
		this.fontColor = textcolor;
		this.font = font;
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		if (boxtoleftoftext) {
			if (showtext && text != null) {
				LFont old = g.getFont();
				g.setFont(font);
				g.drawString(text, x + boxsize,
						(y + font.getHeight() - boxsize / 2) + 5, fontColor);
				g.setFont(old);
			}
			if (!ticked) {
				g.drawTexture(unchecked, x, y, boxsize, boxsize);
			} else {
				g.drawTexture(checked, x, y, boxsize, boxsize);
			}
		} else {
			if (showtext && text != null) {
				LFont old = g.getFont();
				g.setFont(font);
				g.drawString(text, x + boxsize + 5, y + font.getHeight() + 15,
						fontColor);
				g.setFont(old);
			}
			if (!ticked) {
				g.drawTexture(unchecked, y + font.stringWidth(text),
						y + font.getHeight() / 2 - boxsize / 2, boxsize,
						boxsize);
			} else {
				g.drawTexture(checked, y + font.stringWidth(text),
						y + font.getHeight() / 2 - boxsize / 2, boxsize,
						boxsize);
			}
		}
	}

	public void update(long elapsedTime) {
		if (!visible) {
			return;
		}
		super.update(elapsedTime);
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

	protected void processTouchDragged() {
		if (this.input.getTouchPressed() == Touch.TOUCH_MOVE) {
			this.over = this.pressed = this.intersects(this.input.getTouchX(),
					this.input.getTouchY());
		}
	}

	protected void processTouchEntered() {
		this.over = true;
	}

	protected void processTouchExited() {
		this.over = this.pressed = false;
	}

	protected void processKeyPressed() {
		if (this.isSelected() && this.input.getKeyPressed() == Key.ENTER) {
			this.pressedTime = 5;
			this.pressed = true;
			this.doClick();
		}
	}

	protected void processKeyReleased() {
		if (this.isSelected() && this.input.getKeyReleased() == Key.ENTER) {
			this.pressed = false;
		}
	}

	public void doClick() {
		if (Click != null) {
			Click.DoClick(this);
		}
	}

	public void downClick() {
		if (Click != null) {
			Click.DownClick(this, input.getTouchX(), input.getTouchY());
		}
	}

	public void upClick() {
		if (Click != null) {
			Click.UpClick(this, input.getTouchX(), input.getTouchY());
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
			this.ticked = !ticked;
		}
	}

	public boolean isTicked() {
		return ticked;
	}

	public void setTicked(boolean ticked) {
		this.ticked = ticked;
	}

	public boolean isShowText() {
		return showtext;
	}

	public void setShowText(boolean show) {
		this.showtext = show;
	}

	public LColor getFontColor() {
		return fontColor;
	}

	public void setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
	}

	@Override
	public String getUIName() {
		return "CheckBox";
	}


}
