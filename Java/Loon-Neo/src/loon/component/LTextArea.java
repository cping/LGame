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
	private boolean useLFont;

	private int[] drawNewCr;
	private int[] drawNewCg;
	private int[] drawNewCb;
	private int[] drawNewLV;

	private int brightMax = 100;
	private int brightSpeed = 1;
	private int[] crs;
	private int[] cgs;
	private int[] cbs;
	private int default_cr;
	private int default_cg;
	private int default_cb;
	private String[] getMessage;
	private int[] getMessageLength;

	private int messageWidthLimit = 200;
	private int postLine = 0;
	private int max;
	private int num;
	private int drawY;

	private String strTemp;
	private int posx;
	private int posy;
	private int numBak;

	private boolean centerText;
	private boolean waitFlag;
	private boolean flashFont;
	private boolean over;
	private boolean slideMessage;

	private int[] slideX;

	private IFont displayFont;
	private int countFrame;
	private LColor triangleColor = LColor.orange;
	private LColor tmpcolor = new LColor(LColor.white);
	private LColor fontColor = new LColor(LColor.white);
	private String lineFlag = LSystem.FLAG_TAG;

	private String waitFlagString;

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
		this(LTextArea.TYPE_DOWN, -1, SkinManager.get().getMessageSkin().getFont(), x, y, w, h,
				LSystem.loadTexture(bgFile));
	}

	public LTextArea(int x, int y, int w, int h, LTexture bg) {
		this(LTextArea.TYPE_DOWN, -1, SkinManager.get().getMessageSkin().getFont(), x, y, w, h, bg);
	}

	public LTextArea(int x, int y, int w, int h, boolean flashFont) {
		this(LTextArea.TYPE_DOWN, -1, x, y, w, h, flashFont);
	}

	public LTextArea(int type, int max, int x, int y, int w, int h, boolean flashFont) {
		this(type, max, SkinManager.get().getMessageSkin().getFont(), x, y, w, h, null, flashFont);
	}

	public LTextArea(MessageSkin skin, int type, int max, int x, int y, int w, int h) {
		this(type, max, skin.getFont(), x, y, w, h, skin.getBackgroundTexture());
	}

	public LTextArea(int type, int max, IFont font, int x, int y, int w, int h, boolean flashFont) {
		this(type, max, font, x, y, w, h, null, flashFont);
	}

	public LTextArea(int type, int max, IFont font, int x, int y, int w, int h, LTexture bg) {
		this(type, max, font, x, y, w, h, bg, true);
	}

	public LTextArea(int type, int max, IFont font, int x, int y, int w, int h, LTexture bg, boolean flashFont) {
		this(type, max, font, x, y, w, h, bg, flashFont, true, true);
	}

	public LTextArea(int type, int max, IFont textFont, int x, int y, int w, int h, LTexture bg, boolean flashFont,
			boolean waitFlag, boolean slide) {
		super(x, y, w, h);
		IFont tmp = textFont;
		if (tmp == null) {
			tmp = LSystem.getSystemGameFont();
		}
		this.setFont(tmp);
		this.showType = type;
		this.postLine = (h / tmp.getHeight());
		this.waitFlagString = "new";
		if (max < 0) {
			this.set(LSystem.isDesktop() ? postLine - 1 : postLine + 1);
		} else {
			this.set(max);
		}
		this.setDefaultColor(255, 255, 255, flashFont);
		this.setWaitFlag(waitFlag);
		this.setSlideMessage(slide);
		this.setWidthLimit(w);
		if (bg != null) {
			this.onlyBackground(bg);
		}
	}

	public LTextArea set(int mMax) {
		this.max = (mMax + 1);
		this.message = new String[this.max];
		this.crs = new int[this.max];
		this.cgs = new int[this.max];
		this.cbs = new int[this.max];
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
			this.message[i] = LSystem.EMPTY;
			this.getMessage[i] = LSystem.EMPTY;
			this.getMessageLength[i] = 0;
			this.bright[i] = (this.brightMax * i / this.max);
		}
		return this;
	}

	public LTextArea setDefaultColor(int r, int g, int b) {
		return setDefaultColor(r, g, b, this.flashFont);
	}

	public LTextArea setDefaultColor(int r, int g, int b, boolean flash) {
		this.setFlashFont(flash);
		this.default_cr = r;
		this.default_cg = g;
		this.default_cb = b;
		if (this.flashFont) {
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
		return this;
	}

	@Override
	public IFont getFont() {
		return displayFont;
	}

	@Override
	public LTextArea setFont(IFont fn) {
		if (fn == null) {
			return this;
		}
		this.displayFont = fn;
		this.useLFont = (this.displayFont instanceof LFont);
		return this;
	}

	public LTextArea setWidthLimit(int widthLimit) {
		this.messageWidthLimit = widthLimit;
		return this;
	}

	public LTextArea setNewFlag() {
		this.drawNew[this.num] = true;
		return this;
	}

	public LTextArea clear() {
		this.num = 0;
		return this;
	}

	public int count() {
		return this.num;
	}

	public LTextArea setSlideMessage(boolean b) {
		this.slideMessage = b;
		return this;
	}

	public LTextArea setCenter(boolean b) {
		this.centerText = b;
		return this;
	}

	public LTextArea setColor(int d_cr, int d_cg, int d_cb) {
		this.crs[this.num] = d_cr;
		this.cgs[this.num] = d_cg;
		this.cbs[this.num] = d_cb;
		if (flashFont) {
			if (this.crs[this.num] > 255 - this.brightMax) {
				this.crs[this.num] = (255 - this.brightMax);
			}
			if (this.cgs[this.num] > 255 - this.brightMax) {
				this.cgs[this.num] = (255 - this.brightMax);
			}
			if (this.cbs[this.num] > 255 - this.brightMax) {
				this.cbs[this.num] = (255 - this.brightMax);
			}
		}
		return this;
	}

	public LTextArea put(String mes, LColor color) {
		if (StringUtils.isEmpty(mes)) {
			return this;
		}
		String[] messages = StringUtils.split(mes, '\n');
		for (int i = messages.length - 1; i > -1; i--) {
			setColor(color.getRed(), color.getGreen(), color.getBlue());
			putOne(messages[i]);
		}
		return this;
	}

	public LTextArea put(String mes) {
		if (StringUtils.isEmpty(mes)) {
			return this;
		}
		String[] messages = StringUtils.split(mes, '\n');
		for (int i = messages.length - 1; i > -1; i--) {
			putOne(messages[i]);
		}
		return this;
	}

	private void putOne(String mes) {
		this.over = false;
		this.numBak = this.num;

		if (displayFont != null && useLFont) {
			LSTRDictionary.get().bind((LFont) displayFont, mes);
		}

		this.message[this.num] = mes;

		if (this.flashFont) {
			if ((this.crs[this.num] == 0) && (this.cgs[this.num] == 0) && (this.cbs[this.num] == 0)) {
				this.crs[this.num] = this.default_cr;
				this.cgs[this.num] = this.default_cg;
				this.cbs[this.num] = this.default_cb;
			} else {
				if (this.crs[this.num] + this.brightMax > 255) {
					this.crs[this.num] = (255 - this.brightMax);
				} else if (this.crs[this.num] < 0) {
					this.crs[this.num] = 0;
				}
				if (this.cgs[this.num] + this.brightMax > 255) {
					this.crs[this.num] = (255 - this.brightMax);
				} else if (this.cgs[this.num] < 0) {
					this.cgs[this.num] = 0;
				}
				if (this.cbs[this.num] + this.brightMax > 255) {
					this.crs[this.num] = (255 - this.brightMax);
				} else if (this.cbs[this.num] < 0) {
					this.cbs[this.num] = 0;
				}
			}
		}

		if ((this.displayFont != null)
				&& (this.displayFont.stringWidth(this.message[this.num]) > this.messageWidthLimit)) {
			this.posx = 1;
			for (;;) {
				if (this.displayFont.stringWidth(this.message[this.num].substring(0,
						this.message[this.num].length() - this.posx)) <= this.messageWidthLimit) {
					this.strTemp = this.message[this.num].substring(this.message[this.num].length() - this.posx,
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

		this.crs[this.num] = this.default_cr;
		this.cgs[this.num] = this.default_cg;
		this.cbs[this.num] = this.default_cb;

		this.getMessageLength[this.num] = 0;
		this.getMessage[this.num] = LSystem.EMPTY;

		this.drawNew[this.num] = false;

		this.slideX[this.num] = -200;

		if (this.over) {
			setColor(this.crs[this.numBak], this.cgs[this.numBak], this.cbs[this.numBak]);
			put(this.strTemp);
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

	public LTextArea setWaitFlagString(String s) {
		this.waitFlagString = s;
		return this;
	}

	public String getWaitFlagString() {
		return this.waitFlagString;
	}

	public LTextArea draw(GLEx g, int dx, int dy, int d_type, int lines) {
		if (_background != null) {
			g.draw(_background, dx, dy, getWidth(), getHeight(), _component_baseColor);
		}

		boolean supportPack = false;

		if (useLFont) {
			LFont newFont = (LFont) displayFont;
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
					this.drawY = (dy + i * this.displayFont.getSize());
				} else {
					this.drawY = (dy - i * this.displayFont.getSize() - this.displayFont.getSize());
				}

				this.posx = dx;

				if (this.centerText) {
					this.posx -= this.displayFont.stringWidth(this.message[this.num]) / 2;
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

					this.strTemp = waitFlagString;
					drawMessage(g, this.strTemp, this.posx, this.drawY, tmpcolor);
					this.posx += this.displayFont.stringWidth(this.strTemp);
				}
				if (flashFont) {
					tmpcolor.setColor(50, 50, 50);
					drawMessage(g, this.getMessage[this.num], this.posx + 1, this.drawY + 1, tmpcolor);
					tmpcolor.setColor(this.crs[this.num] + this.bright[i], this.cgs[this.num] + this.bright[i],
							this.cbs[this.num] + this.bright[i]);
				} else {
					tmpcolor.setColor(this.crs[this.num], this.cgs[this.num], this.cbs[this.num], 255);
				}
				drawMessage(g, this.getMessage[this.num], this.posx, this.drawY, tmpcolor);
				boolean showFlag = (this.waitFlag) && (i == 0) && index > 0;
				if (showFlag) {
					this.posy = (this.countFrame * 1 / 3 % this.displayFont.getSize() / 2 - 2);
					drawMessage(g, lineFlag, this.posx + this.displayFont.stringWidth(this.getMessage[this.num]),
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
			LFont newFont = (LFont) displayFont;
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

	private void drawMessage(GLEx g, String str, int x, int y, LColor color) {
		if (showType == TYPE_DOWN) {
			displayFont.drawString(g, str, x + leftOffset + 5, (y - 5) + topOffset + displayFont.getAscent() / 2,
					color);
		} else {
			displayFont.drawString(g, str, x + leftOffset + 5,
					(y - 5) + topOffset + displayFont.getAscent() / 2 + getHeight() - displayFont.getHeight(), color);
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

	public boolean isFlashFont() {
		return flashFont;
	}

	public LTextArea setFlashFont(boolean f) {
		this.flashFont = f;
		return this;
	}

	@Override
	public String getUIName() {
		return "TextArea";
	}

}
