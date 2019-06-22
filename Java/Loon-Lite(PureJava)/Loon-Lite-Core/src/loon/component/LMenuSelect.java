/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.component;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.event.ActionKey;
import loon.event.CallFunction;
import loon.event.SysKey;
import loon.event.SysTouch;
import loon.font.FontSet;
import loon.font.FontUtils;
import loon.font.IFont;
import loon.font.LFont;
import loon.geom.PointF;
import loon.geom.RectF;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

/**
 * 游戏中常见的分行选择型菜单栏,注入几行文字(字符串数组),就会自行产生几行可选菜单UI
 * 
 * <pre>
 * LMenuSelect ms = new LMenuSelect("第一选项,第二个,第三个,第四个,我是第五个", 66, 66); 
 * // 选中行的选择外框渲染颜色,不设置不显示 
 * // ms.setSelectRectColor(LColor.red); 
 * // 选中行所用的图像标记(箭头图之类),不设置使用默认样式 
 * // ms.setImageFlag(LSystem.FRAMEWORK_IMG_NAME+"creese.png"); 
 * // 选择框菜单所用的背景图,不设置使用默认样式,也可以noneBackground不显示
 * ms.setBackground(DefUI.getGameWinFrame(ms.width(),
 * ms.height(),LColor.black,LColor.blue, false)); 
 * // 设置监听 ms.setMenuListener(new LMenuSelect.ClickEvent() {
 * 
 * // 监听当前点击的索引与内容
 * 
 * public void onSelected(int index, String context) { 
 *           // 添加气泡提示
 *           add(LToast.makeText(context, Style.SUCCESS));
 * 
 *           }}); 
 *           // 添加到screen 
 *           add(ms);
 * </pre>
 */
public class LMenuSelect extends LComponent implements FontSet<LMenuSelect> {

	public static interface ClickEvent {

		public void onSelected(int index, String context);

	}

	private ClickEvent _menuSelectedEvent;

	private ActionKey _touchEvent = new ActionKey(ActionKey.NORMAL);

	private ActionKey _keyEvent = new ActionKey(ActionKey.NORMAL);

	private CallFunction _function;

	private boolean _over, _pressed, _focused;

	private int _pressedTime = 0;

	private int _selected = 0;

	private IFont _font;

	private String[] _labels;

	private RectF[] _selectRects;

	private int _selectCountMax = -1;

	private String _flag = LSystem.FLAG_SELECT_TAG;

	private LTexture _flag_image = null;

	private PointF _offsetFont = new PointF();

	private float _flag_text_space;

	private boolean _showRect;

	private boolean _showBackground;

	private float _flagWidth = 0;

	private float _flagHeight = 0;

	private String _result = null;

	private LColor selectRectColor;

	private LColor fontColor;

	private LColor selectedFillColor;

	private LColor selectBackgroundColor;

	private LColor selectFlagColor;

	private LTimer colorUpdate;

	public static LMenuSelect make(String labels) {
		return new LMenuSelect(labels, 0, 0);
	}

	public static LMenuSelect make(String labels, int x, int y) {
		return new LMenuSelect(labels, x, y);
	}

	public static LMenuSelect make(String[] labels, int x, int y) {
		return new LMenuSelect(labels, x, y);
	}

	public static LMenuSelect make(IFont font, String[] labels, int x, int y) {
		return new LMenuSelect(font, labels, x, y);
	}

	public static LMenuSelect make(IFont font, String[] labels, String path, int x, int y) {
		return new LMenuSelect(font, labels, path, x, y);
	}

	public static LMenuSelect make(IFont font, String[] labels, LTexture bg, int x, int y) {
		return new LMenuSelect(font, labels, bg, x, y);
	}

	public LMenuSelect(String labels, int x, int y) {
		this(StringUtils.split(labels, ','), x, y);
	}

	public LMenuSelect(String[] labels, int x, int y) {
		this(LSystem.getSystemGameFont(), labels, x, y);
	}

	public LMenuSelect(IFont font, String[] labels, int x, int y) {
		this(font, labels, (LTexture) null, x, y);
	}

	public LMenuSelect(IFont font, String[] labels, String path, int x, int y) {
		this(font, labels, LSystem.loadTexture(path), x, y);
	}

	public LMenuSelect(IFont font, String[] labels, LTexture bg, int x, int y) {
		this(x, y, 1, 1);
		this.selectRectColor = LColor.white;
		this.selectedFillColor = LColor.blue;
		this.selectBackgroundColor = LColor.blue.darker();
		this.selectFlagColor = LColor.orange;
		this.fontColor = LColor.white;
		this.colorUpdate = new LTimer(LSystem.SECOND * 2);
		this._flag_text_space = 10;
		this._showRect = false;
		this._showBackground = true;
		this.onlyBackground(bg);
		this.setFont(font);
		this.setLabels(labels);
	}

	public LMenuSelect(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	public LMenuSelect setTextFlag(String flag) {
		if (!_flag.equals(flag)) {
			this._flag = flag;
			this.setLabels(_labels);
		}
		return this;
	}

	public String getTextFlag() {
		return this._flag;
	}

	public LTexture getImageFlag() {
		return this._flag_image;
	}

	public LMenuSelect setImageFlag(LTexture tex) {
		this._flag_image = tex;
		if (_flag_image != null) {
			freeRes().add(_flag_image);
		}
		return this;
	}

	public LMenuSelect setImageFlag(String path) {
		return setImageFlag(LSystem.loadTexture(path));
	}

	public LMenuSelect setLabels(String labels) {
		return setLabels(StringUtils.split(labels, ','));
	}

	public LMenuSelect setLabels(String[] labels) {
		_labels = labels;
		if (_labels != null) {
			_selectCountMax = labels.length;
			_selectRects = new RectF[_selectCountMax];
			TArray<CharSequence> chars = new TArray<CharSequence>(_selectCountMax);
			float maxWidth = 0;
			float maxHeight = 0;
			if (_flag_image == null) {
				_flagWidth = _font.stringWidth(_flag);
				_flagHeight = _font.stringHeight(_flag);
			} else {
				_flagWidth = _flag_image.getWidth();
				_flagHeight = _flag_image.getHeight();
			}
			float lastWidth = 0;
			float lastHeight = 0;
			for (int i = 0; i < _labels.length; i++) {
				chars.clear();
				chars = FontUtils.splitLines(_labels[i], chars);
				float height = 0;
				lastWidth = maxWidth;
				lastHeight = maxHeight;
				for (CharSequence ch : chars) {
					maxWidth = MathUtils.max(maxWidth,
							FontUtils.measureText(_font, ch) + _font.getHeight() + _flagWidth + _flag_text_space);
					height += MathUtils.max(_font.stringHeight(new StringBuilder(ch).toString()), _flagHeight);
				}
				if (maxWidth > lastWidth) {
					for (int j = 0; j < _selectRects.length; j++) {
						if (_selectRects[j] != null) {
							_selectRects[j].width = maxWidth;
						}
					}
				}
				if (maxHeight > lastHeight) {
					for (int j = 0; j < _selectRects.length; j++) {
						if (_selectRects[j] != null) {
							_selectRects[j].height = maxHeight;
						}
					}
				}
				height = height + _font.getHeight() / 2;
				_selectRects[i] = new RectF(0, maxHeight, maxWidth, height);
				maxHeight += height;
			}
			setSize(maxWidth + _flag_text_space * 2, maxHeight + _flag_text_space * 2);
			if (_font instanceof LFont) {
				LSTRDictionary.get().bind((LFont) _font, _labels);
			}

		}
		return this;
	}

	public LMenuSelect setMenuListener(ClickEvent event) {
		_menuSelectedEvent = event;
		return this;
	}

	public ClickEvent getMenuListener() {
		return this._menuSelectedEvent;
	}

	@Override
	public LMenuSelect setFont(IFont font) {
		_font = font;
		return this;
	}

	@Override
	public IFont getFont() {
		return _font;
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		if (!isVisible()) {
			return;
		}
		IFont tmp = g.getFont();
		g.setFont(_font);
		if (_showBackground && _background != null) {
			if (_selectRects != null && _selectRects.length > 0) {
				RectF rect = _selectRects[0];
				g.draw(_background, x + rect.x - _flag_text_space, y + rect.y - _flag_text_space, getWidth(),
						getHeight());
			}
		} else if (_showBackground) {
			if (_selectRects != null && _selectRects.length > 0) {
				RectF rect = _selectRects[0];
				g.fillRect(x + rect.x - _flag_text_space, y + rect.y - _flag_text_space, getWidth(), getHeight(),
						selectBackgroundColor.setAlpha(0.5f));
			}
		}
		if (_labels != null) {
			for (int i = 0; i < _labels.length; i++) {
				if (_selectRects != null && i < _selectRects.length) {
					RectF rect = _selectRects[i];
					if (_selected == i) {
						drawSelectedFill(g, _offsetFont.x + x + rect.x, _offsetFont.y + y + rect.y, rect.width,
								rect.height);
					}
					if (_flagWidth == 0 && _flagHeight == 0) {
						g.drawString(_labels[i],
								_offsetFont.x + x + rect.x + (rect.width - _font.stringWidth(_labels[i])) / 2,
								_offsetFont.y + y + rect.y + (rect.height - _font.stringHeight(_labels[i])) / 4,
								fontColor);
					} else {
						g.drawString(_labels[i],
								_offsetFont.x + x + rect.x + (rect.width - _font.stringWidth(_labels[i])) / 2
										+ _flagWidth,
								_offsetFont.y + y + rect.y + (rect.height - _font.stringHeight(_labels[i])) / 4,
								fontColor);
						if (_selected == i) {
							if (_flag_image == null) {
								g.drawString(_flag, _offsetFont.x + x + rect.x + _flagWidth / 2,
										_offsetFont.y + y + rect.y + _flagHeight / 8, selectFlagColor);
							} else {
								g.draw(_flag_image, _offsetFont.x + x + rect.x + _flagWidth / 2,
										_offsetFont.y + y + rect.y + _flagHeight / 8);
							}
						}
					}
					if (_showRect) {
						g.drawRect(x + rect.x, y + rect.y, rect.width, rect.height, selectRectColor);
					}
				}
			}
		}
		g.setFont(tmp);
	}

	protected void drawSelectedFill(GLEx g, float x, float y, float width, float height) {
		int color = g.color();
		g.setColor(selectedFillColor.getRed(), selectedFillColor.getGreen(), selectedFillColor.getBlue(),
				(int) (155 * MathUtils.max(0.5f, colorUpdate.getPercentage())));
		g.fillRect(x, y, width, height);
		g.drawRect(x, y, width - 2, height - 2);
		g.setColor(color);

	}

	@Override
	public void update(long elapsedTime) {
		if (!isVisible()) {
			return;
		}
		super.update(elapsedTime);
		if (SysTouch.isDown() || SysTouch.isDrag() || SysTouch.isMove()) {
			if (_selectRects != null) {
				for (int i = 0; i < _selectRects.length; i++) {
					RectF touched = _selectRects[i];
					if (touched != null && touched.inside(getUITouchX(), getUITouchY())) {
						_selected = i;
					}
				}
			}
		}

		if (colorUpdate.action(elapsedTime)) {
			colorUpdate.refresh();
		}
		if (_focused) {
			_pressed = true;
			return;
		}
		if (this._pressedTime > 0 && --this._pressedTime <= 0) {
			this._pressed = false;
		}
	}

	public String getResult() {
		if (_labels != null && _selected > -1 && _selected < _labels.length) {
			_result = _labels[_selected];
		}
		return _result;
	}

	public boolean isTouchOver() {
		return this._over;
	}

	public boolean isTouchPressed() {
		return this._pressed;
	}

	@Override
	protected void processTouchDragged() {
		this._over = this._pressed = this.intersects(getUITouchX(), getUITouchY());
		super.processTouchDragged();
	}

	@Override
	protected void processTouchPressed() {
		if (!isVisible()) {
			return;
		}
		if (!_touchEvent.isPressed()) {
			this._pressed = true;
			super.processTouchPressed();
			this._touchEvent.press();
		}
	}

	@Override
	protected void processTouchReleased() {
		if (!isVisible()) {
			return;
		}
		if (_touchEvent.isPressed()) {
			this._pressed = false;
			if (_menuSelectedEvent != null && _labels != null && _labels.length > 0) {
				try {
					_menuSelectedEvent.onSelected(_selected, _labels[_selected]);
				} catch (Throwable thr) {
					LSystem.error("LMenuSelect onSelected() exception", thr);
				}
			}
			super.processTouchReleased();
			if (_function != null) {
				_function.call(this);
			}
			_touchEvent.release();
		}
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
		if (!isVisible()) {
			return;
		}
		if (this.isSelected()) {
			if (!_keyEvent.isPressed()) {
				this._pressedTime = 5;
				this._pressed = true;
				if (input != null) {
					int code = input.getKeyPressed();
					switch (code) {
					case SysKey.UP:
						_selected--;
						_selected = (_selected > 0 ? _selected : 0);
						break;
					case SysKey.DOWN:
						_selected++;
						_selected = (_selected < _labels.length ? _selected : _labels.length - 1);
						break;
					}
				}
				if (_menuSelectedEvent != null && _labels != null && _labels.length > 0) {
					try {
						_menuSelectedEvent.onSelected(_selected, _labels[_selected]);
					} catch (Throwable t) {
						LSystem.error("LMenuSelect onSelected() exception", t);
					}
				}
				this.doClick();
				_keyEvent.press();
			}

		}
	}

	@Override
	protected void processKeyReleased() {
		if (!isVisible()) {
			return;
		}
		if (this.isSelected()) {
			if (_keyEvent.isPressed()) {
				this._pressed = false;
				_keyEvent.release();
			}
		}
	}

	public PointF getOffsetFont() {
		return _offsetFont;
	}

	public LMenuSelect setOffsetFont(PointF offset) {
		this._offsetFont = offset;
		return this;
	}

	public boolean isShowRect() {
		return _showRect;
	}

	public LMenuSelect setShowRect(boolean s) {
		this._showRect = s;
		return this;
	}

	public LColor getSelectRectColor() {
		return selectRectColor.cpy();
	}

	public LMenuSelect setSelectRectColor(LColor c) {
		this.setShowRect(true);
		this.selectRectColor = c;
		return this;
	}

	public int getSelected() {
		return _selected;
	}

	public LMenuSelect setSelected(int s) {
		this._selected = s;
		return this;
	}

	public LColor getSelectedFillColor() {
		return selectedFillColor.cpy();
	}

	public LMenuSelect setSelectedFillColor(LColor s) {
		this.selectedFillColor = s;
		return this;
	}

	public LTimer getColorUpdateTimer() {
		return colorUpdate;
	}

	public CallFunction getFunction() {
		return _function;
	}

	public LMenuSelect setFunction(CallFunction f) {
		this._function = f;
		return this;
	}

	public String[] getLabels() {
		return _labels;
	}

	@Override
	public LColor getFontColor() {
		return fontColor.cpy();
	}

	@Override
	public LMenuSelect setFontColor(LColor fc) {
		this.fontColor = fc;
		return this;
	}

	public LMenuSelect noneBackground() {
		this._drawBackground = false;
		this._showBackground = false;
		if (_background != null) {
			this._background.close();
			this._background = null;
		}
		return this;
	}

	@Override
	public LComponent clearBackground() {
		this.noneBackground();
		return this;
	}

	public float getFlagTextSpace() {
		return _flag_text_space;
	}

	public LMenuSelect setFlagTextSpace(float f) {
		if (_flag_text_space == f) {
			return this;
		}
		this._flag_text_space = f;
		this.setLabels(_labels);
		return this;
	}

	public LColor getSelectBackgroundColor() {
		return selectBackgroundColor.cpy();
	}

	public void setSelectBackgroundColor(LColor selectBackgroundColor) {
		this.selectBackgroundColor = selectBackgroundColor;
	}

	public LColor getSelectFlagColor() {
		return selectFlagColor.cpy();
	}

	public void setSelectFlagColor(LColor selectFlagColor) {
		this.selectFlagColor = selectFlagColor;
	}

	@Override
	public String getUIName() {
		return "MenuSelect";
	}

}
