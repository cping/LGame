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

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.component.skin.CheckBoxSkin;
import loon.component.skin.SkinManager;
import loon.events.CallFunction;
import loon.events.SysKey;
import loon.font.FontSet;
import loon.font.IFont;
import loon.geom.Vector2f;
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

	public final static LCheckBox at(IFont font, String txt, int x, int y) {
		return new LCheckBox(txt, x, y, LColor.white, font);
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

	private Vector2f offset = new Vector2f();

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
		this(txt, x, y, LSystem.loadTexture(uncheckedFile), LSystem.loadTexture(checkedFile), boxsize, boxtoleftoftext,
				textcolor, font);
	}

	public LCheckBox(CheckBoxSkin skin, String txt, int x, int y, int boxsize, boolean boxtoleftoftext) {
		this(txt, x, y, skin.getUncheckedTexture(), skin.getCheckedTexture(), boxsize, boxtoleftoftext,
				skin.getFontColor(), skin.getFont());
	}

	public LCheckBox(String txt, int x, int y, LTexture unchecked, LTexture checked, int boxsize,
			boolean boxtoleftoftext, LColor textcolor, IFont font) {
		super(x, y, font.stringWidth(txt) + boxsize, MathUtils.max(font.getHeight(), boxsize));
		this.text = txt;
		this.unchecked = unchecked;
		this.checked = checked;
		this.boxsize = boxsize;
		this.boxtoleftoftext = boxtoleftoftext;
		this.fontColor = textcolor;
		this.font = font;
		freeRes().add(unchecked, checked);
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		IFont tmp = g.getFont();
		g.setFont(font);
		if (boxtoleftoftext) {
			if (showtext && text != null) {
				g.drawString(text, 2 + offset.x + x + boxsize,
						offset.y + y + (font.getHeight() - boxsize) / 2 + fontSpace, fontColor);
			}
			if (!ticked) {
				g.draw(unchecked, x, y, boxsize, boxsize, _component_baseColor);
			} else {
				g.draw(checked, x, y, boxsize, boxsize, _component_baseColor);
			}
		} else {
			if (showtext && text != null) {
				g.drawString(text, 2 + offset.x + x + boxsize + fontSpace,
						offset.y + y + (font.getHeight() - boxsize) / 2 + fontSpace, fontColor);
			}
			if (!ticked) {
				g.draw(unchecked, x + font.stringWidth(text) + boxsize + fontSpace,
						y + (font.getHeight() / 2 - boxsize / 2) + fontSpace, boxsize, boxsize, _component_baseColor);
			} else {
				g.draw(checked, x + font.stringWidth(text) + boxsize + fontSpace,
						y + (font.getHeight() / 2 - boxsize / 2) + fontSpace, boxsize, boxsize, _component_baseColor);
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
		if (!isVisible()) {
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
			this.over = this.pressed = this.intersects(getUITouchX(), getUITouchY());
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
		if (this.isSelected() && isKeyDown(SysKey.ENTER)) {
			this.pressedTime = 5;
			this.pressed = true;
			this.doClick();
		}
	}

	@Override
	protected void processKeyReleased() {
		if (this.isSelected() && isKeyUp(SysKey.ENTER)) {
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

	public LCheckBox setFunction(CallFunction f) {
		this._function = f;
		return this;
	}

	public boolean isTicked() {
		return ticked;
	}

	public LCheckBox setTicked(boolean ticked) {
		this.ticked = ticked;
		return this;
	}

	public boolean isShowText() {
		return showtext;
	}

	public LCheckBox setShowText(boolean show) {
		this.showtext = show;
		return this;
	}

	@Override
	public LColor getFontColor() {
		return fontColor.cpy();
	}

	@Override
	public LCheckBox setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
		return this;
	}

	public LTexture getChecked() {
		return checked;
	}

	public int getFontSpace() {
		return fontSpace;
	}

	public LCheckBox setFontSpace(int fontSpace) {
		this.fontSpace = fontSpace;
		return this;
	}

	public boolean isBoxtoleftofText() {
		return boxtoleftoftext;
	}

	public LCheckBox setBoxtoleftofText(boolean b) {
		this.boxtoleftoftext = b;
		return this;
	}

	public Vector2f getBoxOffset() {
		return offset;
	}

	public LCheckBox setBoxOffset(Vector2f offset) {
		this.offset = offset;
		return this;
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

	@Override
	public void destory() {

	}

}
