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

	private LTexture choiceTexture, scrollTexture, scrollFlagATexture, scrollFlagBTexture;

	private int _max;
	private String[] _names;
	private int[] _numbers;
	private LColor[] _colors;

	private boolean[] _lengthChecks;
	private int curIndex;
	private LColor defaultStringColor = LColor.white.cpy();
	private LColor nextStringColor = this.defaultStringColor;

	private LColor arrowColor = LColor.white.cpy();

	private LColor choiceStringColor = LColor.black.cpy();
	private LColor choiceStringBoxColor = LColor.cyan.cpy();

	private IFont _font;
	private int selectList;

	public static final int defaultWidth = 150;
	public static final int defaultHeight = 300;

	private LColor listColor = LColor.black;
	private int drawNum;
	private int loop;
	private int drawX;
	private int drawY;
	private int scrollList;

	private int scrollBarX;
	private int scrollBarY;
	private int scrollBarHeight;

	private int scrollBarHeight_max;
	private boolean scrollBarDrag;

	private int scrollButtonWidth = 15;
	private int scrollButtonHeight = 15;
	private int scrollButtonX;
	private int scrollButtonY;
	private boolean scrollUpButtonON;
	private boolean scrollDownButtonON;
	private float[] px = new float[3];
	private float[] py = new float[3];
	private boolean useHold;
	private int hold;
	private int _maxX = 0;
	private int _maxY = 0;
	private float _sizeFillOffset = 0f;

	public LTextList(int x, int y) {
		this(128, x, y, defaultWidth, defaultHeight, 30);
	}

	public LTextList(int x, int y, int w, int h) {
		this(128, x, y, w, h, 30);
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
		this.choiceTexture = choice;
		this.scrollTexture = scroll;
		this.scrollFlagATexture = scrollFlagA;
		this.scrollFlagBTexture = scrollFlagB;
		this.onlyBackground(bg);
		this.setFocusable(true);
		freeRes().add(choiceTexture, scrollTexture, scrollFlagATexture, scrollFlagBTexture);
	}

	public LTextList reset(int max) {
		this._max = (max + 1);
		this._names = new String[this._max];
		this._numbers = new int[this._max];
		this._colors = new LColor[this._max];
		this._lengthChecks = new boolean[this._max];

		for (int i = 0; i < this._max; i++) {
			this._colors[i] = this.defaultStringColor;
		}

		this.selectList = 0;
		this.curIndex = 0;
		this.scrollList = 0;

		this.nextStringColor = this.defaultStringColor;
		return this;
	}

	public LTextList clear() {
		return delete();
	}

	public LTextList delete() {
		this.curIndex = 0;
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
		this.nextStringColor = nextStringColor;
		return this;
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
			this.removeNames(idx, this.curIndex);
			this.removeInteger(idx, this.curIndex);
			this.removeColor(idx, this.curIndex);
			this.curIndex -= 1;
		}
		return this;
	}

	public LTextList add(String nameString) {
		add(nameString, curIndex);
		return this;
	}

	public LTextList add(String nameString, int numberInt) {
		this._names[this.curIndex] = nameString;
		this._numbers[this.curIndex] = numberInt;
		this._colors[this.curIndex] = this.nextStringColor;
		this.nextStringColor = this.defaultStringColor;
		this.curIndex += 1;
		return this;
	}

	public LTextList setDefaultStringColor(LColor stringNewColor) {
		this.defaultStringColor = stringNewColor;
		return this;
	}

	public LTextList setDefaultStringColor(LColor newStringColor, LColor newChoiceStringColor,
			LColor newChoiceStringBoxColor) {
		this.defaultStringColor = newStringColor;
		this.choiceStringColor = newChoiceStringColor;
		this.choiceStringBoxColor = newChoiceStringBoxColor;
		return this;
	}

	public LTextList setListColor(LColor newColor) {
		this.listColor = newColor;
		return this;
	}

	public LTextList setUseHold(boolean bool) {
		this.useHold = bool;
		return this;
	}

	public LTextList setHold(int curIndex) {
		this.hold = curIndex;
		return this;
	}

	public LTextList setBoundsScrollButton(int width, int height) {
		this.scrollButtonWidth = width;
		this.scrollButtonHeight = height;
		return this;
	}

	public int getSelectList() {
		return this.selectList;
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
		if (this.selectList >= 0) {
			return this._numbers[this.selectList];
		}
		return -1;
	}

	public int getMax() {
		return this._max - 1;
	}

	public void setScrollList(int scroll) {
		this.scrollList = (scroll - this.drawNum);
		if (this.scrollList < 0) {
			this.scrollList = 0;
		}
	}

	private void drawString(GLEx g, String str, int x, int y) {
		if (_font != null) {
			_font.drawString(g, str, x, y);
		}
	}

	public void draw(GLEx g, int x, int y, float mouseX, float mouseY) {
		try {
			g.saveBrush();
			if (this._max > 0) {
				final float backgroundWidth = getWidth() + _sizeFillOffset;
				final float backgroundHeight = getHeight() + _sizeFillOffset;
				final int fontSize = _font.getSize();
				// 如果没有设置背景，则绘制
				if (_background == null) {
					g.setTint(this.listColor);
					g.fillRect(x, y, backgroundWidth, backgroundHeight);
					g.setTint(255, 255, 255);
					g.drawRect(x, y, backgroundWidth, backgroundHeight);
				} else {
					g.draw(_background, x, y, backgroundWidth, backgroundHeight, _component_baseColor);
				}
				this.drawNum = MathUtils.ifloor((backgroundHeight - 10) / fontSize);
				this.loop = 0;
				this.selectList = -1;
				for (int i = this.scrollList; i < this.drawNum + this.scrollList; i++) {
					if (i >= this.curIndex) {
						break;
					}
					this.drawX = (x + 5);
					this.drawY = (y + 5 + this.loop * fontSize);
					if (!this.scrollBarDrag) {
						if ((mouseY > this.drawY + _maxY) && (mouseY <= this.drawY + _maxY + fontSize)
								&& (mouseX > this.drawX + _maxX) && (mouseX < this.drawX + _maxX + backgroundWidth)) {
							this.selectList = i;
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

					if ((this.selectList == i) || ((this.useHold) && (this.hold == i))) {
						if ((this.useHold) && (this.hold == i)) {
							g.setTint(255, 255, 0);
							g.fillRect(x + _sizeFillOffset, this.drawY, backgroundWidth - _sizeFillOffset, fontSize);
							g.setTint(LColor.black);
							drawString(g, this._names[i], this.drawX, this.drawY);
							this.hold = -1;
						}
						// 选中指定列时
						if (this.selectList == i) {
							if (choiceTexture == null) {
								g.setTint(this.choiceStringBoxColor);
								g.fillRect(x + _sizeFillOffset, this.drawY, backgroundWidth - _sizeFillOffset,
										fontSize + _sizeFillOffset);
							} else {
								g.draw(this.choiceTexture, x + _sizeFillOffset, this.drawY,
										backgroundWidth - _sizeFillOffset, fontSize + _sizeFillOffset,
										_component_baseColor);
							}
							g.setTint(this.choiceStringColor);
							drawString(g, this._names[i], this.drawX, this.drawY);
						}
					} else {
						g.setTint(this._colors[i]);
						drawString(g, this._names[i], this.drawX, this.drawY);
					}

					this.loop += 1;
				}

				this.scrollBarX = (int) (x + backgroundWidth + _sizeFillOffset);

				this.scrollBarHeight_max = (int) (getHeight() - this.scrollButtonHeight * 2f);

				if ((this.drawNum < this.curIndex) && (this.drawNum > 0)) {
					this.scrollBarHeight = (this.scrollBarHeight_max / this.curIndex / this.drawNum);
					this.scrollBarHeight = (this.scrollBarHeight_max * this.drawNum / this.curIndex);
					if (this.scrollBarHeight < 8)
						this.scrollBarHeight = 8;

					this.scrollBarY = (int) (y + this.scrollButtonHeight + _sizeFillOffset);
					this.scrollBarY += (this.scrollBarHeight_max - this.scrollBarHeight) * this.scrollList
							/ (this.curIndex - this.drawNum);
				} else {
					this.scrollBarHeight = this.scrollBarHeight_max;
					this.scrollBarY = (int) (y + this.scrollButtonHeight + _sizeFillOffset);
				}

				if (this.scrollBarDrag) {
					if (mouseY < _maxY + this.scrollBarY + this.scrollBarHeight / 3) {
						for (int i = 0; i < 5; i++) {
							if (this.scrollList <= 0)
								break;
							this.scrollList -= 1;
						}
					}

					if (mouseY > _maxY + this.scrollBarY + this.scrollBarHeight * 2 / 3) {
						for (int i = 0; i < 5; i++) {
							if (this.scrollList >= this.curIndex - this.drawNum)
								break;
							this.scrollList += 1;
						}
					}
				}

				if (SysTouch.isDrag()) {
					if ((mouseX > _maxX + this.scrollBarX)
							&& (mouseX <= _maxX + this.scrollBarX + this.scrollButtonWidth)
							&& (mouseY > _maxY + y + this.scrollButtonHeight)
							&& (mouseY < _maxY + y + getHeight() - this.scrollButtonHeight)) {
						this.scrollBarDrag = true;
					}
				} else {
					this.scrollBarDrag = false;
				}

				this.scrollButtonX = (int) (x + backgroundWidth);
				this.scrollButtonY = y;

				if (scrollFlagATexture == null) {
					if (this.scrollUpButtonON) {
						g.setTint(LColor.gray);
					} else {
						g.setTint(LColor.black);
					}
					g.fillRect(this.scrollButtonX + _sizeFillOffset, this.scrollButtonY, this.scrollButtonWidth,
							this.scrollButtonHeight);
					g.setTint(arrowColor);
					this.px[0] = (this.scrollButtonX + _sizeFillOffset + this.scrollButtonWidth / 6);
					this.px[1] = (this.scrollButtonX + _sizeFillOffset + this.scrollButtonWidth / 2);
					this.px[2] = (this.scrollButtonX + _sizeFillOffset + this.scrollButtonWidth * 5 / 6);
					this.py[0] = (this.scrollButtonY + this.scrollButtonHeight * 5 / 6);
					this.py[1] = (this.scrollButtonY + this.scrollButtonHeight / 6);
					this.py[2] = (this.scrollButtonY + this.scrollButtonHeight * 5 / 6);
					g.fillPolygon(this.px, this.py, 3);
				} else {
					g.draw(this.scrollFlagATexture, this.scrollButtonX + _sizeFillOffset,
							this.scrollButtonY + _sizeFillOffset, this.scrollButtonWidth - _sizeFillOffset,
							this.scrollButtonHeight - _sizeFillOffset, _component_baseColor);
				}

				this.scrollUpButtonON = false;
				if ((!this.scrollBarDrag) && true && (mouseX > this.scrollButtonX + _maxX)
						&& (mouseX <= this.scrollButtonX + _maxX + this.scrollButtonWidth)
						&& (mouseY > _maxY + this.scrollButtonY)
						&& (mouseY < _maxY + this.scrollButtonY + this.scrollButtonHeight)) {
					if (this.scrollList > 0) {
						this.scrollList -= 1;
					}
					this.scrollUpButtonON = true;
				}
				this.scrollButtonX = (int) (x + backgroundWidth);
				this.scrollButtonY = (int) (y + getHeight() - this.scrollButtonHeight);
				this.scrollDownButtonON = false;

			}
			if (scrollTexture == null) {
				if (this.scrollBarDrag) {
					g.setTint(0, 255, 255);
				} else {
					g.setTint(255, 255, 255);
				}
				g.fillRect(this.scrollBarX, this.scrollBarY - _sizeFillOffset, this.scrollButtonWidth,
						this.scrollBarHeight + _sizeFillOffset);
			} else {
				g.draw(this.scrollTexture, this.scrollBarX, this.scrollBarY - _sizeFillOffset, this.scrollButtonWidth,
						this.scrollBarHeight + _sizeFillOffset, _component_baseColor);

			}
			if ((!this.scrollBarDrag) && true && (mouseX > _maxX + this.scrollButtonX)
					&& (mouseX <= _maxX + this.scrollButtonX + this.scrollButtonWidth)
					&& (mouseY > _maxY + this.scrollButtonY)
					&& (mouseY < _maxY + this.scrollButtonY + this.scrollButtonHeight)) {
				if (this.scrollList < this.curIndex - this.drawNum) {
					this.scrollList += 1;
				}
				this.scrollDownButtonON = true;
			}
			if (scrollFlagBTexture == null) {
				if (this.scrollDownButtonON) {
					g.setTint(LColor.gray);
				} else {
					g.setTint(LColor.black);
				}
				g.fillRect(this.scrollButtonX + _sizeFillOffset, this.scrollButtonY + _sizeFillOffset,
						this.scrollButtonWidth, this.scrollButtonHeight);
				g.setTint(arrowColor);
				this.px[0] = (this.scrollButtonX + _sizeFillOffset + this.scrollButtonWidth / 6);
				this.px[1] = (this.scrollButtonX + _sizeFillOffset + this.scrollButtonWidth / 2);
				this.px[2] = (this.scrollButtonX + _sizeFillOffset + this.scrollButtonWidth * 5 / 6);
				this.py[0] = (this.scrollButtonY + _sizeFillOffset + this.scrollButtonHeight / 6);
				this.py[1] = (this.scrollButtonY + _sizeFillOffset + this.scrollButtonHeight * 5 / 6);
				this.py[2] = (this.scrollButtonY + _sizeFillOffset + this.scrollButtonHeight / 6);
				g.fillPolygon(this.px, this.py, 3);
			} else {
				g.draw(this.scrollFlagBTexture, this.scrollButtonX + _sizeFillOffset,
						this.scrollButtonY + _sizeFillOffset, this.scrollButtonWidth, this.scrollButtonHeight,
						_component_baseColor);
			}
		} finally {
			g.restoreBrush();
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
		return arrowColor;
	}

	public LTextList setArrowColor(LColor c) {
		this.arrowColor = new LColor(c);
		return this;
	}

	public LTextList setChoiceStringColor(LColor c) {
		this.choiceStringColor = new LColor(c);
		return this;
	}

	public LTextList setChoiceStringBoxColor(LColor c) {
		this.choiceStringBoxColor = new LColor(c);
		return this;
	}

	@Override
	public LTextList setFontColor(LColor c) {
		this.defaultStringColor = new LColor(c);
		return null;
	}

	@Override
	public LColor getFontColor() {
		return defaultStringColor.cpy();
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
