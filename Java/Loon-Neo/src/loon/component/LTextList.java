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
import loon.event.SysTouch;
import loon.font.FontSet;
import loon.font.IFont;
import loon.opengl.GLEx;

/**
 * 文字列表显示用UI,用以列表方式显示指定数据,LGame本身附带有默认UI,用户也可以自行注入图片进行替换.
 * 
 * Example1:
 * 
 * LTextList list = new LTextList(0,0,150,100); list.add("图灵测试");
 * list.add("人月神话"); list.add("费雪效应"); list.add("ABC"); list.add("EFG");
 */
public class LTextList extends LComponent implements FontSet<LTextList> {

	public final int LIST_SPACE_TOP = 5;

	public final int LIST_SPACE_LEFT = 5;

	public final int LIST_SPACE_BOTTOM = 5;

	private LTexture choiceTexture, scrollTexture, scrollFlagATexture, scrollFlagBTexture;

	private int max;
	private String[] name;
	private int[] number;
	private LColor[] color;

	private boolean[] lengthCheck;
	private int num;
	private LColor defaultStringColor = LColor.white.cpy();
	private LColor nextStringColor = this.defaultStringColor;

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
	 * @param max
	 *            允许插入的最大行数
	 * @param x
	 *            显示用坐标x
	 * @param y
	 *            显示用坐标y
	 * @param width
	 *            文本列表宽
	 * @param height
	 *            文本列表高
	 * @param scrollButtonWidth
	 *            滚轴按钮触发范围
	 * @param bg
	 *            背景图
	 * @param choice
	 *            选中单独栏用图
	 * @param scroll
	 *            滚轴用图
	 * @param scrollFlagA
	 *            滚轴上下标识用图(A)
	 * @param scrollFlagB
	 *            滚轴上下标识用图(B)
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
		freeRes().add(choiceTexture, scrollTexture, scrollFlagATexture, scrollFlagBTexture);
	}

	public void reset(int d_max) {
		this.max = (d_max + 1);
		this.name = new String[this.max];
		this.number = new int[this.max];
		this.color = new LColor[this.max];
		this.lengthCheck = new boolean[this.max];

		for (int i = 0; i < this.max; i++) {
			this.color[i] = this.defaultStringColor;
		}

		this.selectList = 0;
		this.num = 0;
		this.scrollList = 0;

		this.nextStringColor = this.defaultStringColor;
	}

	public void delete() {
		this.max = 0;
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
		this.name[position] = nameString;
		this.number[position] = numberInt;
		return this;
	}

	public LTextList changeColor(int numberInt, LColor colorValue) {
		this.color[numberInt] = colorValue;
		return this;
	}

	public LTextList setNextStringColor(LColor nextStringColor) {
		this.nextStringColor = nextStringColor;
		return this;
	}

	private void removeNames(int idx, int flag) {
		int size = flag - idx - 1;
		if (size > 0) {
			System.arraycopy(this.name, idx + 1, this.name, idx, size);
		}
		this.name[--flag] = null;
		if (size == 0) {
			name = new String[0];
		}
	}

	private void removeInteger(int idx, int flag) {
		int size = flag - idx - 1;
		if (size > 0) {
			System.arraycopy(this.number, idx + 1, this.number, idx, size);
		}
		this.number[--flag] = -1;
		if (size == 0) {
			number = new int[0];
		}
	}

	private void removeColor(int idx, int flag) {
		int size = flag - idx - 1;
		if (size > 0) {
			System.arraycopy(this.color, idx + 1, this.color, idx, size);
		}
		this.color[--flag] = null;
		if (size == 0) {
			color = new LColor[0];
		}
	}

	public void remove(String key) {
		int idx = 0;
		for (String s : name) {
			if (s != null && s.equalsIgnoreCase(key)) {
				remove(idx);
				break;
			}
			idx++;
		}
	}

	public void remove(int idx) {
		if (idx > -1 && idx < name.length) {
			this.removeNames(idx, this.num);
			this.removeInteger(idx, this.num);
			this.removeColor(idx, this.num);
			this.num -= 1;
		}
	}

	public void add(String nameString) {
		add(nameString, num);
	}

	public void add(String nameString, int numberInt) {
		this.name[this.num] = nameString;
		this.number[this.num] = numberInt;
		this.color[this.num] = this.nextStringColor;
		this.nextStringColor = this.defaultStringColor;
		this.num += 1;
	}

	public void setDefaultStringColor(LColor stringNewColor) {
		this.defaultStringColor = stringNewColor;
	}

	public void setDefaultStringColor(LColor newStringColor, LColor newChoiceStringColor,
			LColor newChoiceStringBoxColor) {
		this.defaultStringColor = newStringColor;
		this.choiceStringColor = newChoiceStringColor;
		this.choiceStringBoxColor = newChoiceStringBoxColor;
	}

	public void setListColor(LColor newColor) {
		this.listColor = newColor;
	}

	public void setUseHold(boolean bool) {
		this.useHold = bool;
	}

	public void setHold(int num) {
		this.hold = num;
	}

	public void setBoundsScrollButton(int width, int height) {
		this.scrollButtonWidth = width;
		this.scrollButtonHeight = height;
	}

	public int getSelectList() {
		return this.selectList;
	}

	public int getNumber(int num) {
		return num < max ? this.number[num] : this.number[max - 1];
	}

	public String getSelectName() {
		int idx = get();
		if (idx != -1) {
			return name[idx];
		}
		return LSystem.UNKOWN;
	}

	/**
	 * 获得选中数据所在列的数据标识
	 * 
	 * @return
	 */
	public int get() {
		if (this.selectList >= 0) {
			return this.number[this.selectList];
		}
		return -1;
	}

	public int getMax() {
		return this.max - 1;
	}

	public void setScrollList(int scroll) {
		this.scrollList = (scroll - this.drawNum);
		if (this.scrollList < 0) {
			this.scrollList = 0;
		}
	}

	private synchronized void drawString(GLEx g, String str, int x, int y) {
		if (_font != null) {
			_font.drawString(g, str, x, y);
		}
	}

	public synchronized void draw(GLEx g, int x, int y, float mouseX, float mouseY) {
		try {
			g.saveBrush();
			if (this.max > 0) {

				int fontSize = _font.getSize();

				// 如果没有设置背景，则绘制
				if (_background == null) {
					g.setTint(this.listColor);
					g.fillRect(x, y, getWidth(), getHeight());
					g.setTint(255, 255, 255);
					g.drawRect(x, y, getWidth(), getHeight());
				} else {
					g.draw(_background, x, y, getWidth(), getHeight(), _component_baseColor);
				}

				this.drawNum = (int) ((getHeight() - 10) / fontSize);
				this.loop = 0;
				this.selectList = -1;

				for (int i = this.scrollList; i < this.drawNum + this.scrollList; i++) {
					if (i >= this.num)
						break;
					this.drawX = (x + 5);
					this.drawY = (y + 5 + this.loop * fontSize);

					if (!this.scrollBarDrag) {
						if ((mouseY > this.drawY) && (mouseY <= this.drawY + fontSize) && (mouseX > this.drawX)
								&& (mouseX < this.drawX + getWidth())) {
							this.selectList = i;
						}

					}

					// 计算是否选中当前行
					if (!this.lengthCheck[i]) {
						this.lengthCheck[i] = true;
						if (this.name[i] != null) {
							while (_font.stringWidth(this.name[i]) > getWidth()) {
								this.name[i] = this.name[i].substring(0, this.name[i].length() - 1);
							}
						}
					}

					if ((this.selectList == i) || ((this.useHold) && (this.hold == i))) {
						if ((this.useHold) && (this.hold == i)) {
							g.setTint(255, 255, 0);
							g.fillRect(x + 1, this.drawY, getWidth() - 1, fontSize);
							g.setTint(LColor.black);
							drawString(g, this.name[i], this.drawX, this.drawY);
							this.hold = -1;
						}
						// 选中指定列时
						if (this.selectList == i) {
							if (choiceTexture == null) {
								g.setTint(this.choiceStringBoxColor);
								g.fillRect(x + 1, this.drawY, getWidth() - 2, fontSize + 2);
							} else {
								g.draw(this.choiceTexture, x + 2, this.drawY, getWidth() - 2, fontSize + 2,
										_component_baseColor);
							}
							g.setTint(this.choiceStringColor);
							drawString(g, this.name[i], this.drawX, this.drawY);
						}
					} else {
						g.setTint(this.color[i]);
						drawString(g, this.name[i], this.drawX, this.drawY);
					}

					this.loop += 1;
				}

				this.scrollBarX = (int) (x + getWidth() + 1);

				this.scrollBarHeight_max = (int) (getHeight() - this.scrollButtonHeight * 2);

				if ((this.drawNum < this.num) && (this.drawNum > 0)) {
					this.scrollBarHeight = (this.scrollBarHeight_max / this.num / this.drawNum);
					this.scrollBarHeight = (this.scrollBarHeight_max * this.drawNum / this.num);
					if (this.scrollBarHeight < 8)
						this.scrollBarHeight = 8;

					this.scrollBarY = (y + this.scrollButtonHeight + 1);
					this.scrollBarY += (this.scrollBarHeight_max - this.scrollBarHeight) * this.scrollList
							/ (this.num - this.drawNum);
				} else {
					this.scrollBarHeight = this.scrollBarHeight_max;
					this.scrollBarY = (y + this.scrollButtonHeight + 1);
				}

				if (this.scrollBarDrag) {
					if (mouseY < this.scrollBarY + this.scrollBarHeight / 3) {
						for (int i = 0; i < 5; i++) {
							if (this.scrollList <= 0)
								break;
							this.scrollList -= 1;
						}
					}

					if (mouseY > this.scrollBarY + this.scrollBarHeight * 2 / 3) {
						for (int i = 0; i < 5; i++) {
							if (this.scrollList >= this.num - this.drawNum)
								break;
							this.scrollList += 1;
						}
					}
				}

				if (SysTouch.isDrag()) {
					if ((mouseX > this.scrollBarX) && (mouseX <= this.scrollBarX + this.scrollButtonWidth)
							&& (mouseY > y + this.scrollButtonHeight)
							&& (mouseY < y + getHeight() - this.scrollButtonHeight)) {
						this.scrollBarDrag = true;
					}
				} else {
					this.scrollBarDrag = false;
				}

				this.scrollButtonX = (int) (x + getWidth());
				this.scrollButtonY = y;

				if (scrollFlagATexture == null) {
					if (this.scrollUpButtonON) {
						g.setTint(LColor.gray);
					} else {
						g.setTint(LColor.black);
					}
					g.fillRect(this.scrollButtonX + 1, this.scrollButtonY + 1, this.scrollButtonWidth,
							this.scrollButtonHeight);
					g.setTint(255, 255, 255);
					this.px[0] = (this.scrollButtonX + 1 + this.scrollButtonWidth / 6);
					this.px[1] = (this.scrollButtonX + 1 + this.scrollButtonWidth / 2);
					this.px[2] = (this.scrollButtonX + 1 + this.scrollButtonWidth * 5 / 6);
					this.py[0] = (this.scrollButtonY + 1 + this.scrollButtonHeight * 5 / 6);
					this.py[1] = (this.scrollButtonY + 1 + this.scrollButtonHeight / 6);
					this.py[2] = (this.scrollButtonY + 1 + this.scrollButtonHeight * 5 / 6);
					g.fillPolygon(this.px, this.py, 3);
				} else {
					g.draw(this.scrollFlagATexture, this.scrollButtonX + 1, this.scrollButtonY + 1,
							this.scrollButtonWidth - 1, this.scrollButtonHeight - 1, _component_baseColor);
				}

				this.scrollUpButtonON = false;
				if ((!this.scrollBarDrag) && isFocusable() && (mouseX > this.scrollButtonX)
						&& (mouseX <= this.scrollButtonX + this.scrollButtonWidth) && (mouseY > this.scrollButtonY)
						&& (mouseY < this.scrollButtonY + this.scrollButtonHeight)) {
					if (this.scrollList > 0) {
						this.scrollList -= 1;
					}
					this.scrollUpButtonON = true;
				}
				this.scrollButtonX = (int) (x + getWidth());
				this.scrollButtonY = (int) (y + getHeight() - this.scrollButtonHeight);
				this.scrollDownButtonON = false;

			}
			if (scrollTexture == null) {
				if (this.scrollBarDrag) {
					g.setTint(0, 255, 255);
				} else {
					g.setTint(255, 255, 255);
				}
				g.fillRect(this.scrollBarX, this.scrollBarY, this.scrollButtonWidth, this.scrollBarHeight);
			} else {
				g.draw(this.scrollTexture, this.scrollBarX, this.scrollBarY, this.scrollButtonWidth,
						this.scrollBarHeight, _component_baseColor);

			}
			if ((!this.scrollBarDrag) && isFocusable() && (mouseX > this.scrollButtonX)
					&& (mouseX <= this.scrollButtonX + this.scrollButtonWidth) && (mouseY > this.scrollButtonY)
					&& (mouseY < this.scrollButtonY + this.scrollButtonHeight)) {
				if (this.scrollList < this.num - this.drawNum) {
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
				g.fillRect(this.scrollButtonX + 1, this.scrollButtonY - 1, this.scrollButtonWidth,
						this.scrollButtonHeight);
				g.setTint(LColor.white);
				this.px[0] = (this.scrollButtonX + 1 + this.scrollButtonWidth / 6);
				this.px[1] = (this.scrollButtonX + 1 + this.scrollButtonWidth / 2);
				this.px[2] = (this.scrollButtonX + 1 + this.scrollButtonWidth * 5 / 6);
				this.py[0] = (this.scrollButtonY - 1 + this.scrollButtonHeight / 6);
				this.py[1] = (this.scrollButtonY - 1 + this.scrollButtonHeight * 5 / 6);
				this.py[2] = (this.scrollButtonY - 1 + this.scrollButtonHeight / 6);
				g.fillPolygon(this.px, this.py, 3);
			} else {
				g.draw(this.scrollFlagBTexture, this.scrollButtonX + 1, this.scrollButtonY + 1,
						this.scrollButtonWidth - 1, this.scrollButtonHeight - 1, _component_baseColor);
			}
		} finally {
			g.restoreBrush();
		}
	}

	public void setChoiceStringColor(LColor choiceStringColor) {
		this.choiceStringColor = choiceStringColor;
	}

	public void setChoiceStringBoxColor(LColor choiceStringBoxColor) {
		this.choiceStringBoxColor = choiceStringBoxColor;
	}

	@Override
	public LTextList setFontColor(LColor color) {
		this.defaultStringColor = color;
		return null;
	}

	@Override
	public LColor getFontColor() {
		return defaultStringColor.cpy();
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		synchronized (this) {
			if (getContainer() == null || !(getContainer() instanceof LScrollContainer)) {
				draw(g, x, y, SysTouch.getX(), SysTouch.getY());
			} else {
				draw(g, x, y, ((LScrollContainer) getContainer()).getScrollX() + SysTouch.getX(),
						((LScrollContainer) getContainer()).getScrollY() + SysTouch.getY());
			}
		}
	}

	@Override
	public String getUIName() {
		return "TextList";
	}

}
