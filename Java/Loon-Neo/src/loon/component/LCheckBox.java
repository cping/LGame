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
package loon.component;

import loon.LTexture;
import loon.LTextures;
import loon.canvas.LColor;
import loon.component.skin.CheckBoxSkin;
import loon.component.skin.SkinManager;
import loon.event.CallFunction;
import loon.font.FontSet;
import loon.font.IFont;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * CheckBox,单纯的选项打勾用UI
 */
public class LCheckBox extends LComponent implements FontSet<LCheckBox> {

	public final static LCheckBox at(String txt, int x, int y) {
		return new LCheckBox(txt, x, y);
	}

	public final static LCheckBox at(String txt, int x, int y, LColor c) {
		return new LCheckBox(txt, x, y, c);
	}

	public final static LCheckBox at(IFont font, String txt, int x, int y, LColor c) {
		return new LCheckBox(txt, x, y, c, font);
	}

	private LTexture unchecked, checked;

	private float boxsize;

	private boolean boxtoleftoftext = false, showtext = true;

	private int fontSpace = 0;

	private LColor fontColor;

	private IFont font;

	private boolean pressed = false, over = false, ticked = false;

	private long pressedTime = 0;

	private String text;

	private CallFunction _function;

	public LCheckBox(String txt, int x, int y) {
		this(txt, x, y, SkinManager.get().getCheckBoxSkin().getFontColor());
	}

	public LCheckBox(String txt, int x, int y, LColor textcolor) {
		this(txt, x, y, textcolor, SkinManager.get().getCheckBoxSkin().getFont());
	}

	public LCheckBox(String txt, int x, int y, LColor textcolor, IFont font) {
		this(txt, x, y, SkinManager.get().getCheckBoxSkin().getUncheckedTexture(),
				SkinManager.get().getCheckBoxSkin().getCheckedTexture(),
				SkinManager.get().getCheckBoxSkin().getUncheckedTexture().getWidth(), true, textcolor, font);
	}

	public LCheckBox(String txt, int x, int y, int boxsize) {
		this(txt, x, y, SkinManager.get().getCheckBoxSkin().getUncheckedTexture(),
				SkinManager.get().getCheckBoxSkin().getCheckedTexture(), boxsize, true,
				SkinManager.get().getCheckBoxSkin().getFontColor(), SkinManager.get().getCheckBoxSkin().getFont());
	}

	public LCheckBox(String txt, int x, int y, int boxsize, LColor textcolor) {
		this(txt, x, y, SkinManager.get().getCheckBoxSkin().getUncheckedTexture(),
				SkinManager.get().getCheckBoxSkin().getCheckedTexture(), boxsize, true, textcolor,
				SkinManager.get().getCheckBoxSkin().getFont());
	}

	public LCheckBox(String txt, int x, int y, int boxsize, boolean boxtoleftoftext) {
		this(txt, x, y, boxsize, boxtoleftoftext, SkinManager.get().getCheckBoxSkin().getFont());
	}

	public LCheckBox(String txt, int x, int y, int boxsize, boolean boxtoleftoftext, IFont font) {
		this(txt, x, y, SkinManager.get().getCheckBoxSkin().getUncheckedTexture(),
				SkinManager.get().getCheckBoxSkin().getCheckedTexture(), boxsize, boxtoleftoftext,
				SkinManager.get().getCheckBoxSkin().getFontColor(), font);
	}

	public LCheckBox(String txt, int x, int y, int boxsize, boolean boxtoleftoftext, LColor textcolor, IFont font) {
		this(txt, x, y, SkinManager.get().getCheckBoxSkin().getUncheckedTexture(),
				SkinManager.get().getCheckBoxSkin().getCheckedTexture(), boxsize, boxtoleftoftext, textcolor, font);
	}

	public LCheckBox(String txt, int x, int y, String uncheckedFile, String checkedFile, int boxsize,
			boolean boxtoleftoftext, LColor textcolor, IFont font) {
		this(txt, x, y, LTextures.loadTexture(uncheckedFile), LTextures.loadTexture(checkedFile), boxsize,
				boxtoleftoftext, textcolor, font);
	}

	public LCheckBox(CheckBoxSkin skin, String txt, int x, int y, int boxsize, boolean boxtoleftoftext) {
		this(txt, x, y, skin.getUncheckedTexture(), skin.getCheckedTexture(), boxsize, boxtoleftoftext,
				skin.getFontColor(), skin.getFont());
	}

	public LCheckBox(String txt, int x, int y, LTexture unchecked, LTexture checked, int boxsize,
			boolean boxtoleftoftext, LColor textcolor, IFont font) {
		super(x, y, font.stringWidth(txt) + boxsize, (int) MathUtils.max(font.getHeight(), boxsize));
		this.text = txt;
		this.unchecked = unchecked;
		this.checked = checked;
		this.boxsize = boxsize;
		this.boxtoleftoftext = boxtoleftoftext;
		this.fontColor = textcolor;
		this.font = font;
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		IFont tmp = g.getFont();
		g.setFont(font);
		if (boxtoleftoftext) {
			if (showtext && text != null) {
				g.drawString(text, x + boxsize, y + (font.getHeight() - boxsize) / 2 + fontSpace, fontColor);
			}
			if (!ticked) {
				g.draw(unchecked, x, y, boxsize, boxsize, baseColor);
			} else {
				g.draw(checked, x, y, boxsize, boxsize, baseColor);
			}
		} else {
			if (showtext && text != null) {
				g.drawString(text, x + boxsize + fontSpace, y + (font.getHeight() - boxsize) / 2 + fontSpace,
						fontColor);
			}
			if (!ticked) {
				g.draw(unchecked, x + font.stringWidth(text) + boxsize + fontSpace,
						y + (font.getHeight() / 2 - boxsize / 2) + fontSpace, boxsize, boxsize, baseColor);
			} else {
				g.draw(checked, x + font.stringWidth(text) + boxsize + fontSpace,
						y + (font.getHeight() / 2 - boxsize / 2) + fontSpace, boxsize, boxsize, baseColor);
			}
		}
		g.setFont(tmp);
	}

	@Override
	public boolean isSelected() {
		return super.isSelected() || isTicked();
	}

	@Override
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

	@Override
	protected void processTouchDragged() {
		if (input != null) {
			this.over = this.pressed = this.intersects(this.input.getTouchX(), this.input.getTouchY());
		}
		super.processTouchDragged();
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

	@Override
	protected void processTouchPressed() {
		super.processTouchPressed();
		this.pressed = true;
	}

	@Override
	protected void processTouchReleased() {
		super.processTouchReleased();
		if (_function != null) {
			_function.call(this);
		}
		this.pressed = false;
		this.ticked = !ticked;
	}

	public CallFunction getFunction() {
		return _function;
	}

	public void setFunction(CallFunction function) {
		this._function = function;
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

	public LTexture getChecked() {
		return checked;
	}

	public int getFontSpace() {
		return fontSpace;
	}

	public void setFontSpace(int fontSpace) {
		this.fontSpace = fontSpace;
	}

	public boolean isBoxtoleftofText() {
		return boxtoleftoftext;
	}

	public void setBoxtoleftofText(boolean b) {
		this.boxtoleftoftext = b;
	}

	@Override
	public LCheckBox setFont(IFont font) {
		this.font = font;
		this.setSize((int) (this.font.stringWidth(text) + boxsize), (int) MathUtils.max(font.getHeight(), boxsize));
		return this;
	}

	@Override
	public IFont getFont() {
		return font;
	}

	@Override
	public String getUIName() {
		return "CheckBox";
	}

}
