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
 * 
 */
package loon.component;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.component.skin.SkinManager;
import loon.component.skin.TextListSkin;
import loon.events.SysTouch;
import loon.font.FontSet;
import loon.font.IFont;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 文字列表显示用UI,用以列表方式显示指定数据,LGame本身附带有默认UI,用户也可以自行注入图片进行替换.
 * 
 * Example1:
 * 
 * LTextList list = new LTextList(0,0,150,100); list.add("图灵测试");
 * list.add("人月神话"); list.add("费雪效应"); list.add("ABC"); list.add("EFG");
 */
public class LTextList extends LComponent implements FontSet<LTextList> {

	private LTexture _choiceTexture, _scrollTexture, _scrollFlagATexture, _scrollFlagBTexture;

	private int _max;
	private String[] _names;
	private int[] _numbers;
	private LColor[] _colors;

	private boolean[] _lengthChecks;
	private int _curIndex;

	private LColor _defaultStringColor = LColor.white;
	private LColor _nextStringColor = _defaultStringColor;
	private LColor _arrowColor = LColor.white;
	private LColor _choiceStringColor = _defaultStringColor;
	private LColor _choiceStringBoxColor = LColor.gray;
	private LColor _scrollBarSelectColor = _choiceStringBoxColor;
	private LColor _scrollBarButtonColor = LColor.black;
	private LColor _listColor = _scrollBarButtonColor;
	private LColor _listBorderColor = LColor.white;
	private LColor _listHoldColor = LColor.yellow;

	private IFont _font;
	private int _selectList;

	private boolean _drawListBorder;

	private int _drawNum;
	private int _loop;
	private int _drawX;
	private int _drawY;
	private int _scrollList;

	private int _scrollBarX;
	private int _scrollBarY;
	private int _scrollBarHeight;

	private int _scrollBarHeight_max;
	private boolean scrollBarDrag;

	private int _scrollButtonWidth = 15;
	private int _scrollButtonHeight = 15;
	private int _scrollButtonX;
	private int _scrollButtonY;
	private boolean _scrollUpButtonON;
	private boolean _scrollDownButtonON;
	private float[] _px = new float[3];
	private float[] _py = new float[3];
	private boolean _useHold;
	private int _hold;
	private int _maxX = 0;
	private int _maxY = 0;
	private float _sizeFillOffset = 0f;

	public LTextList(int x, int y) {
		this(128, x, y, 150, 300, 30);
	}

	public LTextList(int x, int y, int w, int h) {
		this(128, x, y, w, h, 30);
	}

	public LTextList(int x, int y, int w, int h, int scrollButtonWidth) {
		this(128, x, y, w, h, scrollButtonWidth);
	}

	public LTextList(int max, int x, int y, int width, int height, int scrollButtonWidth) {
		this(SkinManager.get().getTextListSkin().getFont(), max, x, y, width, height, scrollButtonWidth,
				SkinManager.get().getTextListSkin().getBackgoundTexture(),
				SkinManager.get().getTextListSkin().getChoiceTexture(),
				SkinManager.get().getTextListSkin().getScrollTexture(),
				SkinManager.get().getTextListSkin().getScrollFlagATexture(),
				SkinManager.get().getTextListSkin().getScrollFlagBTexture());
	}

	public LTextList(int max, int x, int y, int width, int height, int scrollButtonWidth, LTexture bg, LTexture choice,
			LTexture scroll, LTexture scrollFlagA, LTexture scrollFlagB) {
		this(SkinManager.get().getTextListSkin().getFont(), max, x, y, width, height, scrollButtonWidth, bg, choice,
				scroll, scrollFlagA, scrollFlagB);
	}

	public LTextList(TextListSkin skin, int max, int x, int y, int width, int height, int scrollButtonWidth) {
		this(skin.getFont(), max, x, y, width, height, scrollButtonWidth, skin.getBackgoundTexture(),
				skin.getChoiceTexture(), skin.getScrollTexture(), skin.getScrollFlagATexture(),
				skin.getScrollFlagBTexture());
	}

	/**
	 * @param font
	 * 
	 * @param max               允许插入的最大行数
	 * @param x                 显示用坐标x
	 * @param y                 显示用坐标y
	 * @param width             文本列表宽
	 * @param height            文本列表高
	 * @param scrollButtonWidth 滚轴按钮触发范围
	 * @param bg                背景图
	 * @param choice            选中单独栏用图
	 * @param scroll            滚轴用图
	 * @param scrollFlagA       滚轴上下标识用图(A)
	 * @param scrollFlagB       滚轴上下标识用图(B)
	 */
	public LTextList(IFont font, int max, int x, int y, int width, int height, int scrollButtonWidth, LTexture bg,
			LTexture choice, LTexture scroll, LTexture scrollFlagA, LTexture scrollFlagB) {
		super(x, y, (width - scrollButtonWidth), height);
		this.reset(max);
		this._font = font;
		this._choiceTexture = choice;
		this._scrollTexture = scroll;
		this._scrollFlagATexture = scrollFlagA;
		this._scrollFlagBTexture = scrollFlagB;
		this.onlyBackground(bg);
		this.setFocusable(true);
		freeRes().add(_choiceTexture, _scrollTexture, _scrollFlagATexture, _scrollFlagBTexture);
	}

	public LTextList reset(int max) {
		this._max = (max + 1);
		this._names = new String[this._max];
		this._numbers = new int[this._max];
		this._colors = new LColor[this._max];
		this._lengthChecks = new boolean[this._max];

		for (int i = 0; i < this._max; i++) {
			this._colors[i] = this._defaultStringColor;
		}

		this._selectList = 0;
		this._curIndex = 0;
		this._scrollList = 0;

		this._nextStringColor = this._defaultStringColor;
		return this;
	}

	public LTextList clear() {
		return delete();
	}

	public LTextList delete() {
		this._curIndex = 0;
		return this;
	}

	@Override
	public LTextList setFont(IFont newFont) {
		this._font = newFont;
		return this;
	}

	@Override
	public IFont getFont() {
		return _font;
	}

	public LTextList changeName(int position, String nameString, int numberInt) {
		this._names[position] = nameString;
		this._numbers[position] = numberInt;
		return this;
	}

	public LTextList changeColor(int numberInt, LColor colorValue) {
		this._colors[numberInt] = colorValue;
		return this;
	}

	public LTextList setNextStringColor(LColor nextStringColor) {
		this._nextStringColor = nextStringColor;
		return this;
	}

	public LTextList setScrollButtonWidth(int w) {
		_scrollButtonWidth = w;
		return this;
	}

	public int getScrollButtonWidth() {
		return _scrollButtonWidth;
	}

	public LTextList setScrollButtonHeight(int h) {
		_scrollButtonHeight = h;
		return this;
	}

	public int getScrollButtonHeight() {
		return _scrollButtonHeight;
	}

	/**
	 * 设定滚动按钮大小
	 * 
	 * @param w
	 * @param h
	 * @return
	 */
	public LTextList setScrollButtonSize(int w, int h) {
		setScrollButtonWidth(w);
		setScrollButtonHeight(h);
		return this;
	}

	/**
	 * 设定滚动按钮大小
	 * 
	 * @param s
	 * @return
	 */
	public LTextList setScrollButtonSize(int s) {
		return setScrollButtonSize(s, s);
	}

	private void removeNames(int idx, int flag) {
		int size = flag - idx - 1;
		if (size > 0) {
			System.arraycopy(this._names, idx + 1, this._names, idx, size);
		}
		this._names[--flag] = null;
		if (size == 0) {
			_names = new String[0];
		}
	}

	private void removeInteger(int idx, int flag) {
		int size = flag - idx - 1;
		if (size > 0) {
			System.arraycopy(this._numbers, idx + 1, this._numbers, idx, size);
		}
		this._numbers[--flag] = -1;
		if (size == 0) {
			_numbers = new int[0];
		}
	}

	private void removeColor(int idx, int flag) {
		int size = flag - idx - 1;
		if (size > 0) {
			System.arraycopy(this._colors, idx + 1, this._colors, idx, size);
		}
		this._colors[--flag] = null;
		if (size == 0) {
			_colors = new LColor[0];
		}
	}

	public LTextList remove(String key) {
		int idx = 0;
		for (String s : _names) {
			if (s != null && s.equalsIgnoreCase(key)) {
				remove(idx);
				break;
			}
			idx++;
		}
		return this;
	}

	public LTextList remove(int idx) {
		if (idx > -1 && idx < _names.length) {
			this.removeNames(idx, this._curIndex);
			this.removeInteger(idx, this._curIndex);
			this.removeColor(idx, this._curIndex);
			this._curIndex -= 1;
		}
		return this;
	}

	public LTextList add(String nameString) {
		add(nameString, _curIndex);
		return this;
	}

	public LTextList add(String nameString, int numberInt) {
		this._names[this._curIndex] = nameString;
		this._numbers[this._curIndex] = numberInt;
		this._colors[this._curIndex] = this._nextStringColor;
		this._nextStringColor = this._defaultStringColor;
		this._curIndex += 1;
		return this;
	}

	public LTextList setDefaultStringColor(LColor stringNewColor) {
		this._defaultStringColor = stringNewColor;
		return this;
	}

	public LTextList setDefaultStringColor(LColor newStringColor, LColor newChoiceStringColor,
			LColor newChoiceStringBoxColor) {
		this._defaultStringColor = newStringColor;
		this._choiceStringColor = newChoiceStringColor;
		this._choiceStringBoxColor = newChoiceStringBoxColor;
		return this;
	}

	public LTextList setListColor(LColor c) {
		_listColor = c;
		return this;
	}

	public LColor getListColor() {
		return _listColor;
	}

	public LTextList setListBorderColor(LColor c) {
		_listBorderColor = c;
		return this;
	}

	public LColor getListBorderColor() {
		return _listBorderColor;
	}

	public LTextList setScrollBarButtonColor(LColor c) {
		_scrollBarButtonColor = c;
		return this;
	}

	public LColor getScrollBarButtonColor() {
		return _scrollBarButtonColor;
	}

	public LColor getScrollBarSelectColor() {
		return _scrollBarSelectColor;
	}

	public LTextList setScrollBarSelectColor(LColor s) {
		_scrollBarSelectColor = s;
		return this;
	}

	public LTextList setDrawListBorder(boolean d) {
		_drawListBorder = d;
		return this;
	}

	public boolean getDrawListBorder() {
		return _drawListBorder;
	}

	public LTextList setUseHold(boolean bool) {
		this._useHold = bool;
		return this;
	}

	public LTextList setHold(int curIndex) {
		this._hold = curIndex;
		return this;
	}

	public LTextList setBoundsScrollButton(int width, int height) {
		this._scrollButtonWidth = width;
		this._scrollButtonHeight = height;
		return this;
	}

	public int getSelectList() {
		return this._selectList;
	}

	public int getNumber(int curIndex) {
		return curIndex < _max ? this._numbers[curIndex] : this._numbers[_max - 1];
	}

	public String getSelectName() {
		int idx = getIndex();
		if (idx != -1) {
			return _names[idx];
		}
		return LSystem.UNKNOWN;
	}

	/**
	 * 获得选中数据所在列的数据标识
	 * 
	 * @return
	 */
	public int getIndex() {
		if (this._selectList >= 0) {
			return this._numbers[this._selectList];
		}
		return -1;
	}

	public int getMax() {
		return this._max - 1;
	}

	public void setScrollList(int scroll) {
		this._scrollList = (scroll - this._drawNum);
		if (this._scrollList < 0) {
			this._scrollList = 0;
		}
	}

	private void drawText(GLEx g, String str, int x, int y, LColor c) {
		if (_font != null) {
			if (c == null) {
				_font.drawString(g, str, x, y);
			} else {
				_font.drawString(g, str, x, y, c);
			}
		}
	}

	public void draw(GLEx g, int x, int y, float mouseX, float mouseY) {
		final int oldColor = g.color();
		try {
			if (this._max > 0) {
				final float backgroundWidth = getWidth() + _sizeFillOffset;
				final float backgroundHeight = getHeight() + _sizeFillOffset;
				final int fontSize = _font.getSize();
				// 如果没有设置背景，则绘制
				if (_background == null) {
					g.fillRect(x, y, backgroundWidth, backgroundHeight, _listColor);
				} else {
					g.draw(_background, x, y, backgroundWidth, backgroundHeight, _component_baseColor);
				}
				this._drawNum = MathUtils.floor((backgroundHeight - 10) / fontSize);
				this._loop = 0;
				this._selectList = -1;
				for (int i = this._scrollList; i < this._drawNum + this._scrollList; i++) {
					if (i >= this._curIndex) {
						break;
					}
					this._drawX = (x + 5);
					this._drawY = (y + 5 + this._loop * fontSize);
					if (!this.scrollBarDrag) {
						if ((mouseY > this._drawY + _maxY) && (mouseY <= this._drawY + _maxY + fontSize)
								&& (mouseX > this._drawX + _maxX) && (mouseX < this._drawX + _maxX + backgroundWidth)) {
							this._selectList = i;
						}
					}

					// 计算是否选中当前行
					if (!this._lengthChecks[i]) {
						this._lengthChecks[i] = true;
						if (this._names[i] != null) {
							while (_font.stringWidth(this._names[i]) > backgroundWidth) {
								this._names[i] = this._names[i].substring(0, this._names[i].length() - 1);
							}
						}
					}

					if ((this._selectList == i) || ((this._useHold) && (this._hold == i))) {
						if ((this._useHold) && (this._hold == i)) {
							g.fillRect(x + _sizeFillOffset, this._drawY, backgroundWidth - _sizeFillOffset, fontSize,
									_listHoldColor);
							drawText(g, this._names[i], this._drawX, this._drawY, this._choiceStringColor);
							this._hold = -1;
						}
						// 选中指定列时
						if (this._selectList == i) {
							if (_choiceTexture == null) {
								g.fillRect(x + _sizeFillOffset, this._drawY, backgroundWidth - _sizeFillOffset,
										fontSize + _sizeFillOffset, this._choiceStringBoxColor);
							} else {
								g.draw(this._choiceTexture, x + _sizeFillOffset, this._drawY,
										backgroundWidth - _sizeFillOffset, fontSize + _sizeFillOffset,
										_component_baseColor);
							}
							drawText(g, this._names[i], this._drawX, this._drawY, this._choiceStringColor);
						}
					} else {
						drawText(g, this._names[i], this._drawX, this._drawY, this._colors[i]);
					}
					this._loop += 1;
				}

				this._scrollBarX = MathUtils.floor(x + backgroundWidth + _sizeFillOffset);

				this._scrollBarHeight_max = MathUtils.floor(getHeight() - this._scrollButtonHeight * 2f);

				if ((this._drawNum < this._curIndex) && (this._drawNum > 0)) {
					this._scrollBarHeight = (this._scrollBarHeight_max / this._curIndex / this._drawNum);
					this._scrollBarHeight = (this._scrollBarHeight_max * this._drawNum / this._curIndex);
					if (this._scrollBarHeight < 8)
						this._scrollBarHeight = 8;

					this._scrollBarY = (int) (y + this._scrollButtonHeight + _sizeFillOffset);
					this._scrollBarY += (this._scrollBarHeight_max - this._scrollBarHeight) * this._scrollList
							/ (this._curIndex - this._drawNum);
				} else {
					this._scrollBarHeight = this._scrollBarHeight_max;
					this._scrollBarY = (int) (y + this._scrollButtonHeight + _sizeFillOffset);
				}

				if (this.scrollBarDrag) {
					if (mouseY < _maxY + this._scrollBarY + this._scrollBarHeight / 3) {
						for (int i = 0; i < 5; i++) {
							if (this._scrollList <= 0)
								break;
							this._scrollList -= 1;
						}
					}

					if (mouseY > _maxY + this._scrollBarY + this._scrollBarHeight * 2 / 3) {
						for (int i = 0; i < 5; i++) {
							if (this._scrollList >= this._curIndex - this._drawNum)
								break;
							this._scrollList += 1;
						}
					}
				}

				if (SysTouch.isDrag() && _input.isMoving()) {
					if ((mouseX > _maxX + this._scrollBarX)
							&& (mouseX <= _maxX + this._scrollBarX + this._scrollButtonWidth)
							&& (mouseY > _maxY + y + this._scrollButtonHeight)
							&& (mouseY < _maxY + y + getHeight() - this._scrollButtonHeight)) {
						this.scrollBarDrag = true;
					}
				} else {
					this.scrollBarDrag = false;
				}
				this._scrollButtonX = MathUtils.ceil(x + backgroundWidth);
				this._scrollButtonY = y;

				LColor newColor = null;
				if (_component_baseColor == null) {
					newColor = (this.scrollBarDrag ? _scrollBarSelectColor : _listColor);
				} else {
					newColor = (this.scrollBarDrag ? _component_baseColor.mul(_scrollBarSelectColor)
							: _component_baseColor.mul(_listColor));
				}

				if (_scrollTexture == null) {
					g.fillRect(this._scrollBarX, this._scrollBarY - _sizeFillOffset, this._scrollButtonWidth,
							this._scrollBarHeight + _sizeFillOffset, newColor);
				} else {
					g.draw(this._scrollTexture, this._scrollBarX, this._scrollBarY - _sizeFillOffset,
							this._scrollButtonWidth, this._scrollBarHeight + _sizeFillOffset, newColor);
				}

				if (_scrollFlagATexture == null) {
					g.fillRect(this._scrollButtonX + _sizeFillOffset, this._scrollButtonY, this._scrollButtonWidth,
							this._scrollButtonHeight, (this._scrollUpButtonON ? _scrollBarSelectColor : _listColor));
					g.setTint(_arrowColor);
					this._px[0] = (this._scrollButtonX + _sizeFillOffset + this._scrollButtonWidth / 6);
					this._px[1] = (this._scrollButtonX + _sizeFillOffset + this._scrollButtonWidth / 2);
					this._px[2] = (this._scrollButtonX + _sizeFillOffset + this._scrollButtonWidth * 5 / 6);
					this._py[0] = (this._scrollButtonY + this._scrollButtonHeight * 5 / 6);
					this._py[1] = (this._scrollButtonY + this._scrollButtonHeight / 6);
					this._py[2] = (this._scrollButtonY + this._scrollButtonHeight * 5 / 6);
					g.fillPolygon(this._px, this._py, 3);
				} else {
					g.draw(this._scrollFlagATexture, this._scrollButtonX + _sizeFillOffset,
							this._scrollButtonY + _sizeFillOffset, this._scrollButtonWidth - _sizeFillOffset,
							this._scrollButtonHeight - _sizeFillOffset, _component_baseColor);
				}

				this._scrollUpButtonON = false;
				if ((!this.scrollBarDrag) && true && (mouseX > this._scrollButtonX + _maxX)
						&& (mouseX <= this._scrollButtonX + _maxX + this._scrollButtonWidth)
						&& (mouseY > _maxY + this._scrollButtonY)
						&& (mouseY < _maxY + this._scrollButtonY + this._scrollButtonHeight)) {
					if (this._scrollList > 0) {
						this._scrollList -= 1;
					}
					this._scrollUpButtonON = true;
				}
				this._scrollButtonX = MathUtils.floor(x + backgroundWidth);
				this._scrollButtonY = MathUtils.floor(y + getHeight() - this._scrollButtonHeight);
				this._scrollDownButtonON = false;

				if ((!this.scrollBarDrag) && (mouseX > _maxX + this._scrollButtonX)
						&& (mouseX <= _maxX + this._scrollButtonX + this._scrollButtonWidth)
						&& (mouseY > _maxY + this._scrollButtonY)
						&& (mouseY < _maxY + this._scrollButtonY + this._scrollButtonHeight)) {
					if (this._scrollList < this._curIndex - this._drawNum) {
						this._scrollList += 1;
					}
					this._scrollDownButtonON = true;
				}
				if (_scrollFlagBTexture == null) {
					g.fillRect(this._scrollButtonX + _sizeFillOffset, this._scrollButtonY + _sizeFillOffset,
							this._scrollButtonWidth, this._scrollButtonHeight,
							(this._scrollDownButtonON ? _scrollBarSelectColor : _listColor));
					g.setTint(_arrowColor);
					this._px[0] = (this._scrollButtonX + _sizeFillOffset + this._scrollButtonWidth / 6);
					this._px[1] = (this._scrollButtonX + _sizeFillOffset + this._scrollButtonWidth / 2);
					this._px[2] = (this._scrollButtonX + _sizeFillOffset + this._scrollButtonWidth * 5 / 6);
					this._py[0] = (this._scrollButtonY + _sizeFillOffset + this._scrollButtonHeight / 6);
					this._py[1] = (this._scrollButtonY + _sizeFillOffset + this._scrollButtonHeight * 5 / 6);
					this._py[2] = (this._scrollButtonY + _sizeFillOffset + this._scrollButtonHeight / 6);
					g.fillPolygon(this._px, this._py, 3);
				} else {
					g.draw(this._scrollFlagBTexture, this._scrollButtonX + _sizeFillOffset,
							this._scrollButtonY + _sizeFillOffset, this._scrollButtonWidth, this._scrollButtonHeight,
							_component_baseColor);
				}

				if (_drawListBorder) {
					g.drawRect(x, y, backgroundWidth, backgroundHeight, _listBorderColor);
				}
			}

		} finally {
			g.setColor(oldColor);
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		float touchX = getTouchX();
		float touchY = getTouchY();
		Desktop desk = getDesktop();
		if (desk != null) {
			final Vector2f pos = desk.getUITouch(touchX, touchY, false);
			touchX = pos.getX();
			touchY = pos.getY();
		}
		if (getContainer() == null || !(getContainer() instanceof LScrollContainer)) {
			draw(g, x, y, touchX, touchY);
		} else {
			draw(g, x, y, ((LScrollContainer) getContainer()).getBoxScrollX() + touchX,
					((LScrollContainer) getContainer()).getBoxScrollY() + touchY);
		}
	}

	public LColor getArrowColor() {
		return _arrowColor;
	}

	public LTextList setArrowColor(LColor c) {
		this._arrowColor = new LColor(c);
		return this;
	}

	public LTextList setChoiceStringColor(LColor c) {
		this._choiceStringColor = new LColor(c);
		return this;
	}

	public LTextList setChoiceStringBoxColor(LColor c) {
		this._choiceStringBoxColor = new LColor(c);
		return this;
	}

	@Override
	public LTextList setFontColor(LColor c) {
		this._defaultStringColor = new LColor(c);
		return null;
	}

	@Override
	public LColor getFontColor() {
		return _defaultStringColor.cpy();
	}

	public float getSizeFillOffset() {
		return _sizeFillOffset;
	}

	public LTextList setSizeFillOffset(float s) {
		this._sizeFillOffset = s;
		return this;
	}

	@Override
	public String getUIName() {
		return "TextList";
	}

	@Override
	public void destory() {

	}

}
