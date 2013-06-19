package loon.core.graphics.component;

import java.util.ArrayList;

import loon.core.graphics.LColor;
import loon.core.graphics.LComponent;
import loon.core.graphics.LContainer;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LSTRFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.graphics.opengl.LTexture.Format;
import loon.core.input.LInputFactory.Key;
import loon.core.timer.LTimer;


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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
public class LInfo extends LContainer {

	private boolean isDirty;

	private LSTRFont mesFont;

	private float offsetX = 10, offsetY = 10;

	private static LTexture page; // 分页标记用图

	private static LTexture line; // 行末标记用图

	class FlagImage {

		private boolean visible = true; // 是否显示标记

		private boolean flag;

		private LTimer timer = new LTimer(10);

		private LTexture texture;// 背景图片

		private float startTime = 0; // 开始时间

		private float alpha; // 透明度

		private int x = 0; // 显示的x坐标

		private int y = 0; // 显示的y坐标

		public int width = 24; // 宽

		public int height = 24; // 高

		public FlagImage() {
			// 读取基础标记
			if (page == null || (page != null && page.isRecycled())) {
				if (page != null) {
					page.destroy();
					page = null;
				}
				page = LTextures.loadTexture("assets/loon_page.png");
			}
			if (line == null || (line != null && line.isRecycled())) {
				if (line != null) {
					line.destroy();
					line = null;
				}
				line = LTextures.loadTexture("assets/loon_line.png");
			}
			texture = page;
		}

		// 绘制
		public void draw(GLEx g) {
			if (!visible) {
				return;
			}
			alpha = startTime / 255f;
			g.setAlpha(alpha);
			g.drawTexture(texture, offsetX + x, offsetY + y, width, height);
			g.setAlpha(1f);
		}

		public void update(long elapsedTime) {
			if (timer.action(elapsedTime)) {
				if (flag) {
					startTime -= 2;
					if (startTime <= 50) {
						flag = false;
					}
				} else {
					startTime += 2;
					if (startTime >= 250) {
						flag = true;
					}
				}
			}
		}

		public void setType(int type) {
			if (type == 0) {
				texture = line;
			} else {
				texture = page;
			}
		}

		public void setAlpha(int a) {
			this.alpha = a;
		}

		public void setPos(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public void setX(int x) {
			setPos(x, this.y);
		}

		public void setY(int y) {
			setPos(this.x, y);
		}

		public void setSize(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public void setVisible(boolean visible) {
			this.visible = visible;
		}

	}

	public LInfo(int width, int height) {
		this(0, 0, width, height);
	}

	public static int messageCountMax = 1000; // 允许显示的最大文字数量

	private LFont deffont = LFont.getDefaultFont();

	private LColor fontColor = LColor.white;

	private boolean isHch = false;

	private int indentPoint = 0; // 此变量用以记录换行点位置

	private float fontSize = deffont.getSize(); // 默认的字体大小

	private int linespacing = 15; // 默认的行间距

	private int linesize = 15; // 默认的列间距

	private int pitch = 0; // 默认的文字间距离

	private boolean autoreturn = true; // 是否开启自动换行

	private int margin_left = 10; // 文字显示时距离左侧位置修正 10

	private int margin_right = 10; // 文字显示时距离右侧位置修正 10

	private int margin_top = 10; // 文字显示时距离上方位置修正 10

	private int margin_bottom = 10; // 文字显示时距离下方位置修正 10

	private String message = ""; // 完整的文字信息

	private String tmpMessage = null;

	private int message_char_count; // 转化为字符串的信息数量

	private char align = 'l'; // 文字对齐方式（l为左对齐）

	private int[] message_x = null; // 文字显示位置的x坐标集合

	private int[] message_y = null; // 文字显示位置的y坐标集合

	// 已经出现过的字符坐标集合(每打印过一个字符，就在这里多保存一个)
	private ArrayList<LocatePoint> locatePoint = new ArrayList<LocatePoint>();

	private FlagImage flag = null; // 文字显示进度标记

	class LocatePoint {
		int point;

		Integer x = 0;

		Integer y = 0;
	}

	public LInfo(int x, int y, int width, int height) {
		this((LTexture) null, x, y, width, height);
	}

	public LInfo(String fileName, int x, int y) {
		this(LTextures.loadTexture(fileName), x, y);
	}

	public LInfo(LTexture formImage, int x, int y) {
		this(formImage, x, y, formImage.getWidth(), formImage.getHeight());
	}

	public LInfo(LTexture formImage, int x, int y, int width, int height) {
		super(x, y, width, height);
		if (formImage == null) {
			this.setBackground(new LTexture(width, height, true, Format.SPEED));
			this.setAlpha(0.3F);
		} else {
			this.setBackground(formImage);
			if (width == -1) {
				width = formImage.getWidth();
			}
			if (height == -1) {
				height = formImage.getHeight();
			}
		}
		this.message_char_count = 0;
		this.message_x = new int[messageCountMax];
		this.message_y = new int[messageCountMax];
		this.locatePoint = new ArrayList<LocatePoint>();
		this.flag = new FlagImage();
		this.customRendering = true;
		this.setElastic(true);
		this.setLayer(100);
	}

	public void locate(Integer x, Integer y) {
		LocatePoint l = new LocatePoint();
		l.point = message_char_count;
		l.x = x;
		l.y = y;
		locatePoint.add(l);
	}

	public void flagOn(int type) {
		flag.setVisible(true);
		flag.setType(type);
	}

	public void flagOff() {
		flag.setVisible(false);
	}

	public void setIndent() {
		indentPoint = message_char_count;
	}

	public void endIndent() {
		indentPoint = -1;
	}

	public void putMessage(String text) {
		if (text == null) {
			return;
		}
		this.tmpMessage = message;
		this.message += text;
		this.message_char_count += text.replaceAll("\n", "").length();
		this.resetMessagePos();
	}

	public void resetMessagePos() {
		int x = 0, y = 0;
		int len = message.length();
		float mesWidth;
		int rightLimit = (int) (this.x() + getWidth() - margin_right - fontSize);
		String[] line;
		char[] ch = new char[1];
		int count = 0;
		int baseX = 0, baseY = 0;
		LocatePoint lp;
		int locateCount = locatePoint.size();
		int widthBuff;
		baseY = (int) (this.y() + margin_top + fontSize);
		baseX = (this.x() + margin_left);
		switch (align) {
		case 'c': // 居中
			line = message.split("\n");
			y = baseY;
			for (int j = 0; j < line.length; j++) {
				len = line[j].length();
				mesWidth = (fontSize + pitch) * (len);
				widthBuff = (int) ((getWidth() - margin_left - margin_right) / 2 - mesWidth / 2);
				x = baseX + widthBuff;
				for (int i = 0; i < len; i++) {
					ch[0] = line[j].charAt(i);
					for (int k = 0; k < locateCount; k++) {
						lp = locatePoint.get(k);
						if (count == lp.point) {
							if (lp.x != null)
								x = baseX + lp.x + widthBuff;
							if (lp.y != null)
								y = baseY + lp.y;
						}
					}
					message_x[count] = x;
					message_y[count] = y;
					count++;
					x += fontSize + pitch;
				}
				if (isHch) {
					flag.setPos((y - flag.height), x);
				} else {
					flag.setPos(x, (y - flag.height));
				}
				y += linesize + linespacing;
			}
			break;

		case 'r': // 右对齐
			line = message.split("\n");
			y = baseY;
			for (int j = 0; j < line.length; j++) {
				len = line[j].length();
				mesWidth = (fontSize + pitch) * len;
				widthBuff = (int) ((getWidth() - margin_left - margin_right) - mesWidth);
				x = baseX + widthBuff;
				for (int i = 0; i < len; i++) {
					ch[0] = line[j].charAt(i);
					for (int k = 0; k < locateCount; k++) {
						lp = locatePoint.get(k);
						if (count == lp.point) {
							if (lp.x != null)
								x = baseX + lp.x + widthBuff;
							if (lp.y != null)
								y = baseY + lp.y;
						}
					}
					message_x[count] = x;
					message_y[count] = y;
					count++;
					x += fontSize + pitch;
				}
				if (isHch) {
					flag.setPos((y - flag.height), x);
				} else {
					flag.setPos(x, (y - flag.height));
				}
				y += linesize + linespacing;
			}
			break;
		default: // 左对齐
			line = message.split("\n");
			y = baseY;
			for (int j = 0; j < line.length; j++) {
				len = line[j].length();
				x = baseX;
				for (int i = 0; i < len; i++) {
					ch[0] = line[j].charAt(i);
					if (autoreturn && x >= rightLimit) {
						y += linesize + linespacing;
						x = baseX;
					}
					for (int k = 0; k < locateCount; k++) {
						lp = locatePoint.get(k);
						if (count == lp.point) {
							if (lp.x != null)
								x = baseX + lp.x;
							if (lp.y != null)
								y = baseY + lp.y;
						}
					}
					message_x[count] = x;
					message_y[count] = y;
					if (count == indentPoint) {
						baseX = x;
					}
					count++;
					x += fontSize + pitch;
				}
				y += linesize + linespacing;
			}
			if (autoreturn && x >= rightLimit) {
				y += linesize + linespacing;
				x = baseX;
			}
			y -= linesize + linespacing;
			if (isHch) {
				flag.setPos((y - flag.height), x);
			} else {
				flag.setPos(x, (y - flag.height));
			}
			break;
		}
		if (tmpMessage != null && !message.equals(tmpMessage)) {
			if (mesFont != null) {
				synchronized (mesFont) {
					mesFont.dispose();
					mesFont = new LSTRFont(deffont, message);
				}
			} else {
				mesFont = new LSTRFont(deffont, message);
			}
		}
		this.isDirty = true;
	}

	public String getMessage() {
		return message;
	}

	public void setFlagType(int type) {
		flag.setType(type);
	}

	public void setAlign(char align) {
		this.align = align;
	}

	public void setMargin(int margin) {
		setMargin(margin, margin, margin, margin);
	}

	public void setMargin(int top, int right, int bottom, int left) {
		margin_top = top;
		margin_right = right;
		margin_bottom = bottom;
		margin_left = left;
	}

	public void setMarginTop(int top) {
		margin_top = top;
	}

	public void setMarginRight(int right) {
		margin_right = right;
	}

	public void setMarginBottom(int bottom) {
		margin_bottom = bottom;
	}

	public void setMarginLeft(int left) {
		margin_left = left;
	}

	public int getMarginBottom() {
		return margin_bottom;
	}

	/**
	 * 处理点击事件（请重载实现）
	 * 
	 */
	public void doClick() {
		if (Click != null) {
			Click.DoClick(this);
		}
	}

	@Override
	protected void processTouchClicked() {
		this.doClick();
	}

	@Override
	protected void processKeyPressed() {
		if (this.isSelected() && this.input.getKeyPressed() == Key.ENTER) {
			this.doClick();
		}
	}

	@Override
	public void update(long elapsedTime) {
		if (!visible) {
			return;
		}
		super.update(elapsedTime);
		flag.update(elapsedTime);
	}

	private void drawMessage(GLEx g) {
		if (mesFont == null) {
			return;
		}
		synchronized (mesFont) {
			if (isDirty) {
				final char[] chars = message.toCharArray();
				final int len = message.length();
				int i, j = 0;
				char ch;
				mesFont.startChar();
				if (isHch) {
					for (i = 0; i < len; i++) {
						ch = chars[i];
						if (ch != '\n') {
							mesFont.addChar(ch, offsetY + message_y[j], offsetX
									+ message_x[j], fontColor);
							j++;
						}
					}
				} else {
					for (i = 0; i < len; i++) {
						ch = chars[i];
						if (ch != '\n') {
							mesFont.addChar(ch, offsetX + message_x[j], offsetY
									+ message_y[j], fontColor);
							j++;
						}
					}
				}
				mesFont.stopChar();
				mesFont.saveCharCache();
				this.isDirty = false;
			} else {
				mesFont.postCharCache();
			}
		}
	}

	@Override
	protected synchronized void createCustomUI(GLEx g, int x, int y, int w,
			int h) {
		if (!visible) {
			return;
		}
		if (message.length() != 0) {
			drawMessage(g);
		}
		flag.draw(g);
	}

	@Override
	protected void processTouchDragged() {
		if (!locked) {
			if (getContainer() != null) {
				getContainer().sendToFront(this);
			}
			this.move(this.input.getTouchDX(), this.input.getTouchDY());
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {

	}

	public LFont getFont() {
		return deffont;
	}

	public void setFont(LFont deffont) {
		if (deffont == null) {
			return;
		}
		this.deffont = deffont;
		this.fontSize = deffont.getSize();
	}

	public LColor getFontColor() {
		return fontColor;
	}

	public void setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
	}

	public void setPitch(int pitch) {
		this.pitch = pitch;
	}

	public void setLineSpacing(int lineSpacing) {
		this.linespacing = lineSpacing;
	}

	public void setLineSize(int linesize) {
		this.linesize = linesize;
	}

	public void setAutoReturn(boolean autoreturn) {
		this.autoreturn = autoreturn;
	}

	public float getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;
	}

	public boolean isHch() {
		return isHch;
	}

	public void setHch(boolean isHch) {
		this.isHch = isHch;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (page != null) {
			page.destroy();
			page = null;
		}
		if (line != null) {
			line.destroy();
			line = null;
		}
		if (mesFont != null) {
			mesFont.dispose();
			mesFont = null;
		}
	}

	@Override
	public String getUIName() {
		return "Info";
	}

}
