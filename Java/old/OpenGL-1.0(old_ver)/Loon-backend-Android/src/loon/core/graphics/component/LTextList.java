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
 *          新增类，用以列表方式显示指定数据，本身有默认UI，用户也可以自行注入图片进行替换.
 * 
 *          Example1:
 * 
 *          LTextList list = new LTextList(0,0,150,100); list.add("图灵测试");
 *          list.add("人月神话"); list.add("费雪效应"); list.add("ABC");
 *          list.add("EFG");
 * 
 */
package loon.core.graphics.component;

import loon.Touch;
import loon.core.graphics.LComponent;
import loon.core.graphics.LScrollContainer;
import loon.core.graphics.device.LColor;
import loon.core.graphics.device.LFont;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;

public class LTextList extends LComponent {

	public final int LIST_SPACE_TOP = 5;

	public final int LIST_SPACE_LEFT = 5;

	public final int LIST_SPACE_BOTTOM = 5;

	private LTexture bgTexture, choiceTexture, scrollTexture,
			scrollFlagATexture, scrollFlagBTexture;

	private int max;
	private String[] name;
	private int[] number;
	private LColor[] color;

	private boolean[] lengthCheck;
	private int num;
	private LColor defaultStringColor = LColor.white;
	private LColor nextStringColor = this.defaultStringColor;

	private LColor choiceStringColor = LColor.black;
	private LColor choiceStringBoxColor = LColor.cyan;

	private LFont font = LFont.getDefaultFont();
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

	public LTextList(int max, int x, int y, int width, int height,
			int scrollButtonWidth) {
		this(max, x, y, width, height, scrollButtonWidth, DefUI
				.getDefaultTextures(2), DefUI.getDefaultTextures(11), DefUI
				.getDefaultTextures(3), null, null);
	}

	/**
	 * 
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
	public LTextList(int max, int x, int y, int width, int height,
			int scrollButtonWidth, LTexture bg, LTexture choice,
			LTexture scroll, LTexture scrollFlagA, LTexture scrollFlagB) {
		super(x, y, (width - scrollButtonWidth), height);
		this.reset(max);
		this.bgTexture = bg;
		this.choiceTexture = choice;
		this.scrollTexture = scroll;
		this.scrollFlagATexture = scrollFlagA;
		this.scrollFlagBTexture = scrollFlagB;
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

	public void setFont(LFont newFont) {
		this.font = newFont;
	}

	public void changeName(int position, String nameString, int numberInt) {
		this.name[position] = nameString;
		this.number[position] = numberInt;
	}

	public void changeColor(int numberInt, LColor colorValue) {
		this.color[numberInt] = colorValue;
	}

	public void setNextStringColor(LColor nextStringColor) {
		this.nextStringColor = nextStringColor;
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

	public void setDefaultStringColor(LColor newStringColor,
			LColor newChoiceStringColor, LColor newChoiceStringBoxColor) {
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

	private void drawString(GLEx g, String str, int x, int y) {
		g.drawString(str, x, y + font.getHeight() - 5);
	}

	public void draw(GLEx g, int x, int y, float mouseX, float mouseY) {
		if (this.max > 0) {

			LFont oldFont = g.getFont();
			int oldColor = g.getColorARGB();

			g.setFont(this.font);
			int fontSize = font.getSize();

			// 如果没有设置背景，则绘制
			if (bgTexture == null) {
				g.setColor(this.listColor);
				g.fillRect(x, y, getWidth(), getHeight());
				g.setColor(255, 255, 255);
				g.drawRect(x, y, getWidth(), getHeight());
			} else {
				g.drawTexture(bgTexture, x, y, getWidth(), getHeight());
			}

			this.drawNum = ((getHeight() - 10) / fontSize);
			this.loop = 0;
			this.selectList = -1;

			for (int i = this.scrollList; i < this.drawNum + this.scrollList; i++) {
				if (i >= this.num)
					break;
				this.drawX = (x + 5);
				this.drawY = (y + 5 + this.loop * fontSize);

				if (!this.scrollBarDrag) {
					if ((mouseY > this.drawY)
							&& (mouseY <= this.drawY + fontSize)
							&& (mouseX > this.drawX)
							&& (mouseX < this.drawX + getWidth())) {
						this.selectList = i;
					}

				}

				// 计算是否选中当前行
				if (!this.lengthCheck[i]) {
					this.lengthCheck[i] = true;
					if (this.name[i] != null) {
						while (font.stringWidth(this.name[i]) > getWidth()) {
							this.name[i] = this.name[i].substring(0,
									this.name[i].length() - 1);
						}
					}
				}

				if ((this.selectList == i)
						|| ((this.useHold) && (this.hold == i))) {
					if ((this.useHold) && (this.hold == i)) {
						g.setColor(255, 255, 0);
						g.fillRect(x + 1, this.drawY, getWidth() - 1, fontSize);
						g.setColor(LColor.black);
						drawString(g, this.name[i], this.drawX, this.drawY);
						this.hold = -1;
					}
					// 选中指定列时
					if (this.selectList == i) {
						if (choiceTexture == null) {
							g.setColor(this.choiceStringBoxColor);
							g.fillRect(x + 1, this.drawY, getWidth() - 2,
									fontSize + 2);
						} else {
							g.drawTexture(this.choiceTexture, x + 2,
									this.drawY, getWidth() - 2, fontSize + 2);
						}
						g.setColor(this.choiceStringColor);
						drawString(g, this.name[i], this.drawX, this.drawY);
					}
				} else {
					g.setColor(this.color[i]);
					drawString(g, this.name[i], this.drawX, this.drawY);
				}

				this.loop += 1;
			}

			this.scrollBarX = (x + getWidth() + 1);

			this.scrollBarHeight_max = (getHeight() - this.scrollButtonHeight * 2);

			if ((this.drawNum < this.num) && (this.drawNum > 0)) {
				this.scrollBarHeight = (this.scrollBarHeight_max / this.num / this.drawNum);
				this.scrollBarHeight = (this.scrollBarHeight_max * this.drawNum / this.num);
				if (this.scrollBarHeight < 8)
					this.scrollBarHeight = 8;

				this.scrollBarY = (y + this.scrollButtonHeight + 1);
				this.scrollBarY += (this.scrollBarHeight_max - this.scrollBarHeight)
						* this.scrollList / (this.num - this.drawNum);
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

			if (Touch.isDrag()) {
				if ((mouseX > this.scrollBarX)
						&& (mouseX <= this.scrollBarX + this.scrollButtonWidth)
						&& (mouseY > y + this.scrollButtonHeight)
						&& (mouseY < y + getHeight() - this.scrollButtonHeight)) {
					this.scrollBarDrag = true;
				}
			} else {
				this.scrollBarDrag = false;
			}

			if (scrollTexture == null) {
				if (this.scrollBarDrag) {
					g.setColor(0, 255, 255);
				} else {
					g.setColor(255, 255, 255);
				}
				g.fillRect(this.scrollBarX, this.scrollBarY,
						this.scrollButtonWidth, this.scrollBarHeight);
			} else {
				g.drawTexture(this.scrollTexture, this.scrollBarX,
						this.scrollBarY, this.scrollButtonWidth,
						this.scrollBarHeight);

			}

			this.scrollButtonX = (x + getWidth());
			this.scrollButtonY = y;

			if (scrollFlagATexture == null) {
				if (this.scrollUpButtonON) {
					g.setColor(LColor.gray);
				} else {
					g.setColor(LColor.black);
				}
				g.fillRect(this.scrollButtonX + 1, this.scrollButtonY + 1,
						this.scrollButtonWidth, this.scrollButtonHeight);
				g.setColor(255, 255, 255);
				this.px[0] = (this.scrollButtonX + 1 + this.scrollButtonWidth / 6);
				this.px[1] = (this.scrollButtonX + 1 + this.scrollButtonWidth / 2);
				this.px[2] = (this.scrollButtonX + 1 + this.scrollButtonWidth * 5 / 6);
				this.py[0] = (this.scrollButtonY + 1 + this.scrollButtonHeight * 5 / 6);
				this.py[1] = (this.scrollButtonY + 1 + this.scrollButtonHeight / 6);
				this.py[2] = (this.scrollButtonY + 1 + this.scrollButtonHeight * 5 / 6);
				g.fillPolygon(this.px, this.py, 3);
			} else {
				g.drawTexture(this.scrollFlagATexture, this.scrollButtonX + 1,
						this.scrollButtonY + 1, this.scrollButtonWidth - 1,
						this.scrollButtonHeight - 1);
			}

			this.scrollUpButtonON = false;
			if ((!this.scrollBarDrag) && isFocusable()
					&& (mouseX > this.scrollButtonX)
					&& (mouseX <= this.scrollButtonX + this.scrollButtonWidth)
					&& (mouseY > this.scrollButtonY)
					&& (mouseY < this.scrollButtonY + this.scrollButtonHeight)) {
				if (this.scrollList > 0) {
					this.scrollList -= 1;
				}
				this.scrollUpButtonON = true;
			}
			this.scrollButtonX = (x + getWidth());
			this.scrollButtonY = (y + getHeight() - this.scrollButtonHeight);
			this.scrollDownButtonON = false;
			if ((!this.scrollBarDrag) && isFocusable()
					&& (mouseX > this.scrollButtonX)
					&& (mouseX <= this.scrollButtonX + this.scrollButtonWidth)
					&& (mouseY > this.scrollButtonY)
					&& (mouseY < this.scrollButtonY + this.scrollButtonHeight)) {
				if (this.scrollList < this.num - this.drawNum) {
					this.scrollList += 1;
				}
				this.scrollDownButtonON = true;
			}
			if (scrollFlagBTexture == null) {
				if (this.scrollDownButtonON) {
					g.setColor(LColor.gray);
				} else {
					g.setColor(LColor.black);
				}
				g.fillRect(this.scrollButtonX + 1, this.scrollButtonY - 1,
						this.scrollButtonWidth, this.scrollButtonHeight);
				g.setColor(LColor.white);
				this.px[0] = (this.scrollButtonX + 1 + this.scrollButtonWidth / 6);
				this.px[1] = (this.scrollButtonX + 1 + this.scrollButtonWidth / 2);
				this.px[2] = (this.scrollButtonX + 1 + this.scrollButtonWidth * 5 / 6);
				this.py[0] = (this.scrollButtonY - 1 + this.scrollButtonHeight / 6);
				this.py[1] = (this.scrollButtonY - 1 + this.scrollButtonHeight * 5 / 6);
				this.py[2] = (this.scrollButtonY - 1 + this.scrollButtonHeight / 6);
				g.fillPolygon(this.px, this.py, 3);
			} else {
				g.drawTexture(this.scrollFlagBTexture, this.scrollButtonX + 1,
						this.scrollButtonY + 1, this.scrollButtonWidth - 1,
						this.scrollButtonHeight - 1);
			}
			g.setFont(oldFont);
			g.setColor(oldColor);
		}
	}

	public void setChoiceStringColor(LColor choiceStringColor) {
		this.choiceStringColor = choiceStringColor;
	}

	public void setChoiceStringBoxColor(LColor choiceStringBoxColor) {
		this.choiceStringBoxColor = choiceStringBoxColor;
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		if (getContainer() == null
				|| !(getContainer() instanceof LScrollContainer)) {
			draw(g, x, y, Touch.getX(), Touch.getY());
		} else {
			draw(g,
					x,
					y,
					((LScrollContainer) getContainer()).getScrollX()
							+ Touch.getX(),
					((LScrollContainer) getContainer()).getScrollY()
							+ Touch.getY());
		}
	}

	@Override
	public String getUIName() {
		return "TextList";
	}

}
