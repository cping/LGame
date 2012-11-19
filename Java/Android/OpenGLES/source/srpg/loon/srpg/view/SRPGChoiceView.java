package loon.srpg.view;

import java.util.HashMap;

import loon.core.LSystem;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.LGradation;
import loon.core.graphics.LImage;
import loon.core.graphics.Screen;
import loon.core.graphics.device.LGraphics;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LSTRDictionary;
import loon.core.graphics.opengl.LTexture;


/**
 * Copyright 2008 - 2011
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class SRPGChoiceView extends SRPGView {

	private LTexture dialogImage;

	private String[][] choice;

	private int posX, posY, width, height;

	private int space, select;

	private int[] mesList, joint;

	private LColor[] colors;

	private int tab, maxSize, view;

	private LFont defFont;

	private boolean visible;

	public SRPGChoiceView() {
		super.exist = false;
		super.cache = false;
	}

	public SRPGChoiceView(final String[] messages, LFont font, int x, int y) {
		set(choiceFormat(messages), jointFormat(messages.length), font, x, y);
	}

	public SRPGChoiceView(String[] messages, int[] joint, LFont font, int x,
			int y) {
		set(choiceFormat(messages), joint, font, x, y);
	}

	public SRPGChoiceView(String[][] messages, LFont font, int x, int y) {
		set(messages, jointFormat(messages.length), font, x, y);
	}

	public SRPGChoiceView(String[][] messages, int[] joint, LFont font, int x,
			int y) {
		set(messages, joint, font, x, y);
	}

	public void set(String[] messages, int[] joint, LFont font, int x, int y) {
		set(choiceFormat(messages), joint, font, x, y);
	}

	public void set(String[][] messages, LFont font, int x, int y) {
		set(messages, jointFormat(messages.length), font, x, y);
	}

	public void set(final String[] messages, LFont font, int x, int y) {
		set(choiceFormat(messages), jointFormat(messages.length), font, x, y);
	}

	/**
	 * 设定选择器的基本构成(背景图，文字信息，信息连接顺序，字体，坐标)
	 * 
	 * @param messages
	 * @param joint
	 * @param font
	 * @param x
	 * @param y
	 */
	public void set(String[][] messages, int[] joint, LFont font, int x, int y) {
		this.set(null, messages, joint, font, x, y);
	}

	/**
	 * 设定选择器的基本构成(背景图，文字信息，信息连接顺序，字体，坐标)
	 * 
	 * @param image
	 * @param messages
	 * @param joint
	 * @param font
	 * @param x
	 * @param y
	 */
	public void set(LTexture image, String[][] messages, int[] joint,
			LFont font, int x, int y) {
		super.exist = true;
		super.cancelflag = false;
		StringBuffer sbr = new StringBuffer(100);
		for (int j = 0; j < messages.length; j++) {
			for (int i = 0; i < messages[j].length; i++) {
				sbr.append(messages[j][i]);
			}
		}
		LSTRDictionary.bind(font, sbr.toString());
		this.dialogImage = image;
		this.defFont = font;
		this.choice = messages;
		this.joint = joint;
		this.posX = x;
		this.posY = y;
		this.tab = 10;
		this.mesList = new int[messages[0].length];
		for (int i = 0; i < mesList.length; i++) {
			mesList[i] = 0;
		}
		for (int i = 0; i < messages.length; i++) {
			for (int j = 0; j < messages[i].length; j++) {
				int width = defFont.stringWidth(messages[i][j]);
				if (mesList[j] < width) {
					mesList[j] = width;
				}
			}
		}
		// 单独一行高度
		this.height = defFont.getHeight();
		// 获得默认的上下文间隔大小
		this.space = height + defFont.getSize() / 2;
		this.width = getWidthTotal(mesList, tab);
		// 颜色集合（用以改变指定行选项颜色）
		this.colors = new LColor[messages.length];
		for (int i = 0; i < colors.length; i++) {
			colors[i] = null;
		}
		this.view = 0;
		this.maxSize = choice.length;
		this.select = 0;
		this.visible = true;
	}

	/**
	 * 格式化选择器连接顺序
	 * 
	 * @param size
	 * @return
	 */
	private int[] jointFormat(int size) {
		int[] joint = new int[size];
		for (int i = 0; i < size; i++) {
			joint[i] = i;
		}
		return joint;
	}

	/**
	 * 格式化选择器文字内容
	 * 
	 * @param messages
	 * @return
	 */
	private String[][] choiceFormat(String[] messages) {
		String[][] select = new String[messages.length][1];
		for (int i = 0; i < select.length; i++) {
			select[i][0] = messages[i];
		}
		return select;
	}

	/**
	 * 返回选择其中文字信息
	 * 
	 * @return
	 */
	public String[][] getChoice() {
		return choice;
	}

	private static HashMap<String, LTexture> lazyDialog;

	/**
	 * 创建默认的选择器背景图片
	 * 
	 * @param w
	 * @param h
	 * @return
	 */
	private synchronized LTexture createDefaultDialog(int w, int h) {
		if (lazyDialog == null) {
			lazyDialog = new HashMap<String, LTexture>();
		}
		int hash = 1;
		hash = LSystem.unite(hash, w);
		hash = LSystem.unite(hash, h);
		String key = String.valueOf(hash);
		LTexture o = (LTexture) lazyDialog.get(key);
		if (o == null) {
			LImage tmp = LImage.createImage(w, h, true);
			LGraphics g = tmp.getLGraphics();
			LGradation.getInstance(LColor.white, LColor.black, w, h)
					.drawHeight(g, 0, 0);
			g.setColor(LColor.black);
			g.drawRect(0, 0, w - 1, h - 1);
			g.dispose();
			o = tmp.getTexture();
			if (tmp != null) {
				tmp.dispose();
				tmp = null;
			}
			lazyDialog.put(key, o);
		}
		return o;
	}

	/**
	 * 绘制选单
	 * 
	 * @param g
	 * @param color1
	 * @param color2
	 */
	public synchronized void drawChoice(GLEx g) {
		if (!visible) {
			return;
		}
		int x1 = this.getX() - 5;
		int y1 = this.getY();
		int x2 = this.getWidth() + 20;
		int y2 = this.getAllHeight() + 10;
		int w = x2 + 15;
		int h = y2 + 10;
		if (dialogImage == null) {
			dialogImage = createDefaultDialog(w, h);
		}
		g.drawTexture(dialogImage, x1, y1 - 5);
		LFont old = g.getFont();
		try {
			g.setFont(defFont);
			// 选中项
			int i = getDrawContent();
			if (i >= 0 && i < maxSize) {
				LGradation.getInstance(LColor.white, LColor.black,
						getWidth() + 6, getSpace()).drawHeight(g, x1 + 14,
						y1 + getSpace() * getDrawContent() + 2);
				g.setColor(LColor.darkGray);
				g.drawRect(x1 + 14, y1 + getSpace() * getDrawContent() + 2,
						getWidth() + 6, getSpace());
				g.resetColor();
			}
			int index = 0;
			// 遍历文字与颜色信息
			for (;;) {
				if (index >= maxSize) {
					break;
				}
				int viewIndex = index + view;
				if (viewIndex >= choice.length) {
					break;
				}
				LColor nColor;
				if (colors[viewIndex] != null) {
					nColor = colors[viewIndex];
				} else {
					nColor = LColor.white;
				}
				int i1 = 0;
				for (int j1 = 0; j1 < choice[viewIndex].length; j1++) {
					g.drawEastString(choice[viewIndex][j1], posX + i1 + 14,
							posY + index * getSpace() + getHeight(), 0, nColor);
					i1 += mesList[j1] + tab;
				}
				index++;
			}
		} finally {
			g.setFont(old);
			g.resetColor();
		}
	}

	/**
	 * 滚轴坐标移动
	 * 
	 * @param x
	 * @param y
	 */
	public void scrollMouse(int x, int y) {
		x -= posX;
		y -= posY;
		if (x > -1 && x <= width + 1) {
			if (y > -1 * getSpace() && y <= 0 && getView() > 0) {
				setView(getView() - 1);
			}
			if (y > getAllHeight() && y <= getAllHeight() + getSpace()) {
				setView(getView() + 1);
			}
		}
	}

	/**
	 * 选中项坐标移动
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int choiceMouse(int x, int y) {
		x -= posX;
		y -= posY;
		if (x >= -1 && x <= width + 1) {
			int index = 0;
			do {
				if (index > maxSize) {
					break;
				}
				int size = index + view;
				if (size >= choice.length) {
					break;
				}
				if (y >= index * getSpace() && y <= (index + 1) * getSpace()) {
					return size;
				}
				index++;
			} while (true);
		}
		return -1;
	}

	public int moveMouse(int x, int y) {
		this.scrollMouse(x, y);
		return choiceMouse(x, y);
	}

	public int choiceMouseExecute(int x, int y) {
		int index = choiceMouse(x, y);
		if (index != -1) {
			super.cache = true;
			this.select = index;
		}
		return getJointContent(index);
	}

	public int choiceExecute() {
		if (select != -1) {
			super.cache = true;
		}
		return select;
	}

	public int getCacheContent() {
		return getCacheContent(false);
	}

	public int getCacheContent(boolean flag) {
		super.exist = flag;
		super.cache = false;
		return getJointContent(select);
	}

	public int getContent() {
		return select;
	}

	public int getDrawContent() {
		return select - view;
	}

	public int getJointContent() {
		return getJointContent(select);
	}

	public int getJointContent(int i) {
		if (i == -1) {
			return -1;
		} else {
			return joint[i];
		}
	}

	public void setContent(int i) {
		if (i > -1 && i < size()) {
			select = i;
		}
	}

	public void setSelect(int x, int y) {
		setContent(choiceMouse(x, y));
	}

	public void setContentMove(int i) {
		setContent(i);
		if (i > -1 && i < size()) {
			if (view > i) {
				setView(optimizeView(i));
			} else if (view + maxSize <= i) {
				setView(optimizeView(i - (maxSize - 1)));
			}
		}
	}

	public void setContentAuto(int i) {
		setContent(i);
		if ((view > i || view + maxSize < i) && i > -1 && i < size()) {
			setView(optimizeView(i));
		}
	}

	public void setContentScroll(int i) {
		setContent(i);
		if (i > -1 && i < size()) {
			setView(optimizeView(i));
		}
	}

	public int[] getWidthList() {
		return mesList;
	}

	public void setWidthList(int[] res) {
		this.mesList = res;
		this.width = getWidthTotal(res, tab);
	}

	public int getTab() {
		return tab;
	}

	public void setTab(int i) {
		this.tab = i;
		this.width = getWidthTotal(mesList, i);
	}

	public int getWidthTotal(int[] res, int index) {
		int j = 0;
		for (int i = 0; i < res.length; i++) {
			j += res[i];
		}
		j += (choice[0].length - 1) * index;
		return j;
	}

	public int size() {
		return choice.length;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getSpace() {
		return space;
	}

	public int getAllHeight() {
		return maxSize * space;
	}

	public String[] getChoiceIndex(int i) {
		return choice[i];
	}

	public void setColor(LColor acolor[]) {
		colors = acolor;
	}

	public void setColor(int i, LColor color1) {
		colors[i] = color1;
	}

	public LColor getColor(int i) {
		return colors[i];
	}

	public LColor[] getColor() {
		return colors;
	}

	public int getLower() {
		int i = size() - maxSize;
		if (i < 0) {
			i = 0;
		}
		return i;
	}

	public int optimizeView(int i) {
		if (size() - i < maxSize) {
			i = getLower();
		}
		return i;
	}

	public void setMaxOnly(int i) {
		maxSize = i;
	}

	public void setMax(int i) {
		setMaxOnly(i);
		setViewOnly(optimizeView(getView()));
	}

	public void setMaxAuto(int size) {
		if (size < size()) {
			setMax(size);
		}
	}

	public int getMax() {
		return maxSize;
	}

	public void setViewOnly(int i) {
		view = i;
	}

	public void setView(int i) {
		setViewOnly(optimizeView(i));
	}

	public int getView() {
		return view;
	}

	public int choiceWait() {
		return choiceWait(false);
	}

	public int choiceWait(boolean flag) {
		if (!viewWait(flag)) {
			getCacheContent();
			return -1;
		} else {
			return getCacheContent();
		}
	}

	public int choiceWait(Screen screen) {
		return choiceWait(screen, false);
	}

	public int choiceWait(Screen screen, boolean flag) {
		return choiceWait(screen, flag, false);
	}

	public int choiceWait(Screen screen, boolean flag, boolean close) {
		if (!viewWait(screen, flag)) {
			getCacheContent(close);
			return -1;
		} else {
			return getCacheContent(close);
		}
	}

	public int getX() {
		return posX;
	}

	public int getY() {
		return posY;
	}

	public void setX(int x) {
		this.posX = x;
	}

	public void setY(int y) {
		this.posY = y;
	}

}
