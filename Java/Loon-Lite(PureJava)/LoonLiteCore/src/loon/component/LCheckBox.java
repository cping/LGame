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
import loon.LSystem;
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

	private LTexture _unchecked, _checked;

	private float _boxsize;

	private boolean _boxtoleftoftext = false, _showtext = true;

	private int _fontSpace = 0;

	private LColor _fontColor;

	private IFont _font;

	private boolean _pressed = false, _over = false, _ticked = false;

	private long _pressedTime = 0;

	private String _text;

	private Vector2f _offset = new Vector2f();

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
		super(x, y, font.stringWidth(txt) + boxsize, (int) MathUtils.max(font.getHeight(), boxsize));
		this._text = txt;
		this._unchecked = unchecked;
		this._checked = checked;
		this._boxsize = boxsize;
		this._boxtoleftoftext = boxtoleftoftext;
		this._fontColor = textcolor;
		this._font = font;
		freeRes().add(unchecked, checked);
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		IFont tmp = g.getFont();
		g.setFont(_font);
		if (_boxtoleftoftext) {
			if (_showtext && _text != null) {
				g.drawString(_text, 2 + _offset.x + x + _boxsize,
						_offset.y + y + (_font.getHeight() - _boxsize) / 2 + _fontSpace, _fontColor);
			}
			if (!_ticked) {
				g.draw(_unchecked, x, y, _boxsize, _boxsize, _component_baseColor);
			} else {
				g.draw(_checked, x, y, _boxsize, _boxsize, _component_baseColor);
			}
		} else {
			if (_showtext && _text != null) {
				g.drawString(_text, 2 + _offset.x + x + _boxsize + _fontSpace,
						_offset.y + y + (_font.getHeight() - _boxsize) / 2 + _fontSpace, _fontColor);
			}
			if (!_ticked) {
				g.draw(_unchecked, x + _font.stringWidth(_text) + _boxsize + _fontSpace,
						y + (_font.getHeight() / 2 - _boxsize / 2) + _fontSpace, _boxsize, _boxsize,
						_component_baseColor);
			} else {
				g.draw(_checked, x + _font.stringWidth(_text) + _boxsize + _fontSpace,
						y + (_font.getHeight() / 2 - _boxsize / 2) + _fontSpace, _boxsize, _boxsize,
						_component_baseColor);
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
		if (this._pressedTime > 0 && --this._pressedTime <= 0) {
			this._pressed = false;
		}
	}

	public boolean isTouchOver() {
		return this._over;
	}

	public boolean isTouchPressed() {
		return this._pressed;
	}

	@Override
	protected void processTouchDragged() {
		if (_input != null) {
			this._over = this._pressed = this.intersects(getUITouchX(), getUITouchY());
		}
		super.processTouchDragged();
	}

	@Override
	protected void processTouchEntered() {
		this._over = true;
	}

	@Override
	protected void processTouchExited() {
		this._over = this._pressed = false;
	}

	@Override
	protected void processKeyPressed() {
		if (this.isSelected() && isKeyDown(SysKey.ENTER)) {
			this._pressedTime = 5;
			this._pressed = true;
			this.doClick();
		}
	}

	@Override
	protected void processKeyReleased() {
		if (this.isSelected() && isKeyUp(SysKey.ENTER)) {
			this._pressed = false;
		}
	}

	@Override
	protected void processTouchPressed() {
		super.processTouchPressed();
		this._pressed = true;
	}

	@Override
	protected void processTouchReleased() {
		super.processTouchReleased();
		if (_function != null) {
			_function.call(this);
		}
		this._pressed = false;
		this._ticked = !_ticked;
	}

	public CallFunction getFunction() {
		return _function;
	}

	public LCheckBox setFunction(CallFunction f) {
		this._function = f;
		return this;
	}

	public boolean isTicked() {
		return _ticked;
	}

	public LCheckBox setTicked(boolean ticked) {
		this._ticked = ticked;
		return this;
	}

	public boolean isShowText() {
		return _showtext;
	}

	public LCheckBox setShowText(boolean show) {
		this._showtext = show;
		return this;
	}

	@Override
	public LColor getFontColor() {
		return _fontColor.cpy();
	}

	@Override
	public LCheckBox setFontColor(LColor fontColor) {
		this._fontColor = fontColor;
		return this;
	}

	public LTexture getChecked() {
		return _checked;
	}

	public int getFontSpace() {
		return _fontSpace;
	}

	public LCheckBox setFontSpace(int fontSpace) {
		this._fontSpace = fontSpace;
		return this;
	}

	public boolean isBoxtoleftofText() {
		return _boxtoleftoftext;
	}

	public LCheckBox setBoxtoleftofText(boolean b) {
		this._boxtoleftoftext = b;
		return this;
	}

	public Vector2f getBoxOffset() {
		return _offset;
	}

	public LCheckBox setBoxOffset(Vector2f offset) {
		this._offset = offset;
		return this;
	}

	@Override
	public LCheckBox setFont(IFont font) {
		this._font = font;
		this.setSize((int) (this._font.stringWidth(_text) + _boxsize), MathUtils.max(font.getHeight(), _boxsize));
		return this;
	}

	@Override
	public IFont getFont() {
		return _font;
	}

	@Override
	public String getUIName() {
		return "CheckBox";
	}

	@Override
	public void destory() {

	}

}
