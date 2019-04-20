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
 *          该类效果与LInfo和LMessage近似，但与强调专用场合的前两类不同，此类的应用场合更广阔，默认效果使用上也较前两类自动化。
 *          API也更简便，并且，此并不强迫背景图的使用，缺省状态系统也提供了默认背景。
 * 
 *          Examples1:
 * 
 *          LTextArea area = new LTextArea(66, 66, 300, 100);
 *          area.put("GGGGGGGGGG",LColor.red); area.put("GGGGGGGGGG");
 *          //addString为在前一行追加数据 area.addString("1",LColor.red);
 */
package loon.component;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.component.skin.MessageSkin;
import loon.component.skin.SkinManager;
import loon.font.FontSet;
import loon.font.IFont;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.utils.StringUtils;

/**
 * 字符串显示用组件UI,支持多种文字显示特效,主要用来做信息推送显示之类游戏效果(比如rpg打怪时的文字描述什么的)
 */
public class LTextArea extends LComponent implements FontSet<LTextArea> {

	// 数据向下推入
	public static final int TYPE_DOWN = 0;

	// 数据向上推入
	public static final int TYPE_UP = 1;

	private int leftOffset, topOffset;

	private int showType;
	private String[] message;
	private int[] bright;
	private int[] brightType;
	private boolean[] drawNew;
	private int[] drawNewCr;
	private int[] drawNewCg;
	private int[] drawNewCb;
	private int[] drawNewLV;

	private int brightMax = 100;
	private int brightSpeed = 1;
	private int[] cr;
	private int[] cg;
	private int[] cb;
	private int default_cr = 255 - this.brightMax;
	private int default_cg = 255 - this.brightMax;
	private int default_cb = 255 - this.brightMax;
	private String[] getMessage;
	private int[] getMessageLength;

	private int messageWidthLimit = 200;
	private int postLine = 0;
	private int max;
	private int num;
	private int drawY;
	private boolean waitFlag;
	private String str;
	private int posx;
	private int posy;
	private int numBak;
	private boolean over;
	private boolean slideMessage;

	private int[] slideX;
	private boolean center;
	private IFont font;
	private int countFrame;
	private LColor triangleColor = LColor.orange;
	private LColor tmpcolor = new LColor(LColor.white);
	private LColor fontColor = new LColor(LColor.white);
	private String lineFlag = LSystem.FLAG_TAG;

	public LTextArea(int x, int y, int w, int h) {
		this(-1, x, y, w, h);
	}

	public LTextArea(int max, int x, int y, int w, int h) {
		this(LTextArea.TYPE_DOWN, max, SkinManager.get().getMessageSkin().getFont(), x, y, w, h);
	}

	public LTextArea(int max, IFont font, int x, int y, int w, int h) {
		this(LTextArea.TYPE_DOWN, max, font, x, y, w, h);
	}

	public LTextArea(int type, int max, IFont font, int x, int y, int w, int h) {
		this(type, max, font, x, y, w, h, SkinManager.get().getMessageSkin().getBackgroundTexture());
	}

	public LTextArea(int x, int y, int w, int h, String bgFile) {
		this(LTextArea.TYPE_DOWN, w, SkinManager.get().getMessageSkin().getFont(), x, y, w, h,
				LSystem.loadTexture(bgFile));
	}

	public LTextArea(int x, int y, int w, int h, LTexture bg) {
		this(LTextArea.TYPE_DOWN, w, SkinManager.get().getMessageSkin().getFont(), x, y, w, h, bg);
	}

	public LTextArea(MessageSkin skin, int type, int max, int x, int y, int w, int h) {
		this(type, max, skin.getFont(), x, y, w, h, skin.getBackgroundTexture());
	}

	public LTextArea(int type, int max, IFont font, int x, int y, int w, int h, LTexture bg) {
		super(x, y, w, h);
		this.showType = type;
		this.font = font;
		this.postLine = (h / font.getHeight());
		if (max < 0) {
			this.set(LSystem.isDesktop() ? postLine - 1 : postLine + 1);
		} else {
			this.set(max);
		}
		this.setWidthLimit(w);
		this.setWaitFlag(true);
		this.setSlideMessage(true);
		this.onlyBackground(bg);
	}

	public void set(int mMax) {
		this.max = (mMax + 1);
		this.message = new String[this.max];
		this.cr = new int[this.max];
		this.cg = new int[this.max];
		this.cb = new int[this.max];
		this.bright = new int[this.max];
		this.brightType = new int[this.max];
		this.getMessage = new String[this.max];
		this.getMessageLength = new int[this.max];
		this.drawNew = new boolean[this.max];
		this.drawNewCr = new int[this.max];
		this.drawNewCg = new int[this.max];
		this.drawNewCb = new int[this.max];
		this.drawNewLV = new int[this.max];
		this.slideX = new int[this.max];
		this.num = 0;
		for (int i = 0; i < this.max; i++) {
			this.message[i] = "";
			this.getMessage[i] = "";
			this.getMessageLength[i] = 0;
			this.bright[i] = (this.brightMax * i / this.max);
		}
	}

	public void setDefaultColor(int d_cr, int d_cg, int d_cb) {
		this.default_cr = d_cr;
		this.default_cg = d_cg;
		this.default_cb = d_cb;

		if (this.default_cr > 255 - this.brightMax) {
			this.default_cr = (255 - this.brightMax);
		}
		if (this.default_cg > 255 - this.brightMax) {
			this.default_cg = (255 - this.brightMax);
		}
		if (this.default_cb > 255 - this.brightMax) {
			this.default_cb = (255 - this.brightMax);
		}
	}

	@Override
	public IFont getFont() {
		return font;
	}

	@Override
	public LTextArea setFont(IFont changeFont) {
		this.font = changeFont;
		return this;
	}

	public void setWidthLimit(int widthLimit) {
		this.messageWidthLimit = widthLimit;
	}

	public void setNewFlag() {
		this.drawNew[this.num] = true;
	}

	public void clear() {
		this.num = 0;
	}

	public int count() {
		return this.num;
	}

	public void setSlideMessage(boolean bool) {
		this.slideMessage = bool;
	}

	public void setCenter(boolean bool) {
		this.center = bool;
	}

	public void setColor(int d_cr, int d_cg, int d_cb) {
		this.cr[this.num] = d_cr;
		this.cg[this.num] = d_cg;
		this.cb[this.num] = d_cb;
		if (this.cr[this.num] > 255 - this.brightMax) {
			this.cr[this.num] = (255 - this.brightMax);
		}
		if (this.cg[this.num] > 255 - this.brightMax) {
			this.cg[this.num] = (255 - this.brightMax);
		}
		if (this.cb[this.num] > 255 - this.brightMax) {
			this.cb[this.num] = (255 - this.brightMax);
		}
	}

	public void put(String mes, LColor color) {
		if (StringUtils.isEmpty(mes)) {
			return;
		}
		String[] messages = StringUtils.split(mes, '\n');
		for (int i = messages.length - 1; i > -1; i--) {
			setColor(color.getRed(), color.getGreen(), color.getBlue());
			putOne(messages[i]);
		}
	}

	public void put(String mes) {
		if (StringUtils.isEmpty(mes)) {
			return;
		}
		String[] messages = StringUtils.split(mes, '\n');
		for (int i = messages.length - 1; i > -1; i--) {
			putOne(messages[i]);
		}
	}

	private void putOne(String mes) {
		this.over = false;
		this.numBak = this.num;

		if (font != null && font instanceof LFont) {
			LSTRDictionary.get().bind((LFont) font, mes);
		}

		this.message[this.num] = mes;
		if ((this.cr[this.num] == 0) && (this.cg[this.num] == 0) && (this.cb[this.num] == 0)) {
			this.cr[this.num] = this.default_cr;
			this.cg[this.num] = this.default_cg;
			this.cb[this.num] = this.default_cb;
		} else {
			if (this.cr[this.num] + this.brightMax > 255) {
				this.cr[this.num] = (255 - this.brightMax);
			} else if (this.cr[this.num] < 0) {
				this.cr[this.num] = 0;
			}
			if (this.cg[this.num] + this.brightMax > 255) {
				this.cr[this.num] = (255 - this.brightMax);
			} else if (this.cg[this.num] < 0) {
				this.cg[this.num] = 0;
			}
			if (this.cb[this.num] + this.brightMax > 255) {
				this.cr[this.num] = (255 - this.brightMax);
			} else if (this.cb[this.num] < 0) {
				this.cb[this.num] = 0;
			}
		}

		if ((this.font != null) && (this.font.stringWidth(this.message[this.num]) > this.messageWidthLimit)) {
			this.posx = 1;
			for (;;) {
				if (this.font.stringWidth(this.message[this.num].substring(0,
						this.message[this.num].length() - this.posx)) <= this.messageWidthLimit) {
					this.str = this.message[this.num].substring(this.message[this.num].length() - this.posx,
							this.message[this.num].length());
					this.message[this.num] = this.message[this.num].substring(0,
							this.message[this.num].length() - this.posx);
					this.over = true;
					break;
				}
				this.posx += 1;
			}

		}

		this.num += 1;
		if (this.num >= this.max) {
			this.num = 0;
		}

		this.cr[this.num] = this.default_cr;
		this.cg[this.num] = this.default_cg;
		this.cb[this.num] = this.default_cb;

		this.getMessageLength[this.num] = 0;
		this.getMessage[this.num] = "";

		this.drawNew[this.num] = false;

		this.slideX[this.num] = -200;

		if (this.over) {
			setColor(this.cr[this.numBak], this.cg[this.numBak], this.cb[this.numBak]);
			put(this.str);
		}
	}

	private void setGetMessageLength(int d_length) {
		this.getMessageLength[this.num] = d_length;
	}

	@Override
	public LTextArea setFontColor(LColor color) {
		this.fontColor = color;
		return this;
	}

	@Override
	public LColor getFontColor() {
		return this.fontColor.cpy();
	}

	public LTextArea addString(String mes) {
		return addString(mes, fontColor);
	}

	public LTextArea addString(String mes, LColor color) {
		if (color != null) {
			setColor(color.getRed(), color.getGreen(), color.getBlue());
		}
		this.num -= 1;
		if (this.num < 0) {
			this.num = (this.max - 1);
		}
		setGetMessageLength(this.getMessageLength[this.num]);
		put(this.message[this.num] + mes);
		return this;
	}

	public LTextArea setBright(int max, int speed) {
		this.brightMax = max;
		this.brightSpeed = speed;
		return this;
	}

	public LTextArea setWaitTriangleColor(LColor color) {
		this.triangleColor = color;
		return this;
	}

	public LTextArea draw(GLEx g, int dx, int dy, int d_type, int lines) {
		if (_background != null) {
			g.draw(_background, dx, dy, getWidth(), getHeight(), _component_baseColor);
		}

		boolean useLFont = (font instanceof LFont);
		boolean supportPack = false;

		if (useLFont) {
			LFont newFont = (LFont) font;
			supportPack = newFont.isSupportCacheFontPack();
			newFont.setSupportCacheFontPack(false);
		}

		int oldColor = g.color();
		this.countFrame += 1;
		int index = num;
		for (int i = 0; i < this.max - 1; i++) {
			this.num -= 1;
			if (this.num < 0) {
				this.num = (this.max - 1);
			}
			if (i <= lines) {
				for (int i2 = 0; i2 < 2; i2++) {
					if (this.getMessageLength[this.num] < this.message[this.num].length()) {
						String[] temp = this.getMessage;
						temp[this.num] = (temp[this.num] + this.message[this.num]
								.substring(this.getMessageLength[this.num], this.getMessageLength[this.num] + 1));
						this.getMessageLength[this.num] += 1;
					}
				}

				if (d_type == 0) {
					this.drawY = (dy + i * this.font.getSize());
				} else {
					this.drawY = (dy - i * this.font.getSize() - this.font.getSize());
				}

				this.posx = dx;

				if (this.center) {
					this.posx -= this.font.stringWidth(this.message[this.num]) / 2;
				}

				if (this.slideMessage) {
					this.posx += this.slideX[this.num];
					if (this.slideX[this.num] < 0)
						this.slideX[this.num] += 10;
					else {
						this.slideX[this.num] = 0;
					}
				}

				if (this.drawNew[this.num]) {
					if (this.drawNewLV[this.num] == 0) {
						this.drawNewCr[this.num] += 20;
						if (this.drawNewCr[this.num] >= 255) {
							this.drawNewCr[this.num] = 255;
							this.drawNewLV[this.num] = 1;
						}
					} else if (this.drawNewLV[this.num] == 1) {
						this.drawNewCg[this.num] += 20;
						if (this.drawNewCg[this.num] >= 255) {
							this.drawNewCg[this.num] = 255;
							this.drawNewLV[this.num] = 2;
						}
					} else if (this.drawNewLV[this.num] == 2) {
						this.drawNewCb[this.num] += 20;
						if (this.drawNewCb[this.num] >= 255) {
							this.drawNewCb[this.num] = 255;
							this.drawNewLV[this.num] = 3;
						}
					} else if (this.drawNewLV[this.num] == 3) {
						this.drawNewCr[this.num] -= 20;
						if (this.drawNewCr[this.num] <= 0) {
							this.drawNewCr[this.num] = 0;
							this.drawNewLV[this.num] = 4;
						}
					} else if (this.drawNewLV[this.num] == 4) {
						this.drawNewCg[this.num] -= 20;
						if (this.drawNewCg[this.num] <= 0) {
							this.drawNewCg[this.num] = 0;
							this.drawNewLV[this.num] = 5;
						}
					} else if (this.drawNewLV[this.num] == 5) {
						this.drawNewCb[this.num] -= 20;
						if (this.drawNewCb[this.num] <= 0) {
							this.drawNewCb[this.num] = 0;
							this.drawNewLV[this.num] = 0;
						}
					}

					tmpcolor.setColor(this.drawNewCr[this.num], this.drawNewCg[this.num], this.drawNewCb[this.num]);

					this.str = "new";
					drawString(g, this.str, this.posx, this.drawY, tmpcolor);
					this.posx += this.font.stringWidth(this.str);
				}
				tmpcolor.setColor(50, 50, 50);
				drawString(g, this.getMessage[this.num], this.posx + 1, this.drawY + 1, tmpcolor);
				tmpcolor.setColor(this.cr[this.num] + this.bright[i], this.cg[this.num] + this.bright[i],
						this.cb[this.num] + this.bright[i]);
				drawString(g, this.getMessage[this.num], this.posx, this.drawY, tmpcolor);
				if ((this.waitFlag) && (i == 0) && index > 0) {
					this.posy = (this.countFrame * 1 / 3 % this.font.getSize() / 2 - 2);
					drawString(g, lineFlag, this.posx + this.font.stringWidth(this.getMessage[this.num]),
							this.drawY - this.posy, this.triangleColor);
				}
				if (this.brightType[i] == TYPE_DOWN) {
					this.bright[i] += this.brightSpeed;
					if (this.bright[i] >= this.brightMax) {
						this.bright[i] = this.brightMax;
						this.brightType[i] = TYPE_UP;
					}
				} else {
					this.bright[i] -= this.brightSpeed;
					if (this.bright[i] < 0) {
						this.bright[i] = 0;
						this.brightType[i] = TYPE_DOWN;
					}
				}
			}
		}
		this.num -= 1;
		if (this.num < 0) {
			this.num = (this.max - 1);
		}
		g.setColor(oldColor);

		if (useLFont && supportPack) {
			LFont newFont = (LFont) font;
			newFont.setSupportCacheFontPack(supportPack);
		}

		return this;
	}

	public LTextArea setWaitFlag(boolean w) {
		this.waitFlag = w;
		return this;
	}

	public int getMax() {
		return this.max - 1;
	}

	public int getShowType() {
		return showType;
	}

	public LTextArea setShowType(int showType) {
		this.showType = showType;
		return this;
	}

	public int[] getBright() {
		return bright;
	}

	public LTextArea setBright(int[] bright) {
		this.bright = bright;
		return this;
	}

	public int[] getBrightType() {
		return brightType;
	}

	public LTextArea setBrightType(int[] brightType) {
		this.brightType = brightType;
		return this;
	}

	public int getBrightMax() {
		return brightMax;
	}

	public LTextArea setBrightMax(int brightMax) {
		this.brightMax = brightMax;
		return this;
	}

	public int getBrightSpeed() {
		return brightSpeed;
	}

	public LTextArea setBrightSpeed(int brightSpeed) {
		this.brightSpeed = brightSpeed;
		return this;
	}

	public int getPostLine() {
		return postLine;
	}

	public LTextArea setPostLine(int postLine) {
		this.postLine = postLine;
		return this;
	}

	public int getCountFrame() {
		return countFrame;
	}

	public LTextArea setCountFrame(int countFrame) {
		this.countFrame = countFrame;
		return this;
	}

	@Override
	public LComponent setBackground(LTexture texture) {
		this._background = texture;
		return this;
	}

	@Override
	public LComponent setBackground(String path) {
		return setBackground(LSystem.loadTexture(path));
	}

	private void drawString(GLEx g, String str, int x, int y, LColor color) {
		if (showType == TYPE_DOWN) {
			font.drawString(g, str, x + leftOffset + 5, (y - 5) + topOffset + font.getAscent() / 2, color);
		} else {
			font.drawString(g, str, x + leftOffset + 5,
					(y - 5) + topOffset + font.getAscent() / 2 + getHeight() - font.getHeight(), color);
		}
	}

	public int getLeftOffset() {
		return leftOffset;
	}

	public LTextArea setLeftOffset(int leftOffset) {
		this.leftOffset = leftOffset;
		return this;
	}

	public int getTopOffset() {
		return topOffset;
	}

	public LTextArea setTopOffset(int topOffset) {
		this.topOffset = topOffset;
		return this;
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		draw(g, x, y, showType, postLine);
	}

	public String getLineFlag() {
		return lineFlag;
	}

	public LTextArea setLineFlag(String flag) {
		this.lineFlag = flag;
		return this;
	}

	@Override
	public String getUIName() {
		return "TextArea";
	}

}
