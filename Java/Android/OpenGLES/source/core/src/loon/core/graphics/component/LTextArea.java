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
package loon.core.graphics.component;

import loon.core.graphics.LComponent;
import loon.core.graphics.device.LColor;
import loon.core.graphics.device.LFont;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;

public class LTextArea extends LComponent {

	public static final int TYPE_DOWN = 0;

	public static final int TYPE_UP = 1;

	private LTexture bgLTexture;
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
	private LFont font = LFont.getDefaultFont();
	private int countFrame;
	private LColor triangleColor = LColor.orange;
	private LColor tmpcolor = new LColor(LColor.white);
	
	public LTextArea(int x, int y, int w, int h) {
		this(w, x, y, w, h);
	}

	public LTextArea(int max, int x, int y, int w, int h) {
		this(LTextArea.TYPE_DOWN, max, LFont.getDefaultFont(), x, y, w, h);
	}

	public LTextArea(int max, LFont font, int x, int y, int w, int h) {
		this(LTextArea.TYPE_DOWN, max, font, x, y, w, h);
	}

	public LTextArea(int type, int max, LFont font, int x, int y, int w, int h) {
		this(type, max, font, x, y, w, h, DefUI.getDefaultTextures(3));
	}

	public LTextArea(int x, int y, int w, int h, String bgFile) {
		this(LTextArea.TYPE_DOWN, w, LFont.getDefaultFont(), x, y, w, h,
				LTextures.loadTexture(bgFile));
	}

	public LTextArea(int x, int y, int w, int h, LTexture bg) {
		this(LTextArea.TYPE_DOWN, w, LFont.getDefaultFont(), x, y, w, h, bg);
	}

	public LTextArea(int type, int max, LFont font, int x, int y, int w, int h,
			LTexture bg) {
		super(x, y, w, h);
		this.font = font;
		this.postLine = (h / font.getHeight());
		this.set(max);
		this.setWidthLimit(w);
		this.setWaitFlag(true);
		this.setSlideMessage(true);
		this.bgLTexture = bg;
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

	public void setFont(LFont changeFont) {
		this.font = changeFont;
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
		setColor(color.getRed(), color.getGreen(), color.getBlue());
		put(mes);
	}

	public void put(String mes) {
		this.over = false;
		this.numBak = this.num;

		this.message[this.num] = mes;
		if ((this.cr[this.num] == 0) && (this.cg[this.num] == 0)
				&& (this.cb[this.num] == 0)) {
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

		if ((this.font != null)
				&& (this.font.stringWidth(this.message[this.num]) > this.messageWidthLimit)) {
			this.posx = 1;
			for (;;) {
				if (this.font.stringWidth(this.message[this.num].substring(0,
						this.message[this.num].length() - this.posx)) <= this.messageWidthLimit) {
					this.str = this.message[this.num].substring(
							this.message[this.num].length() - this.posx,
							this.message[this.num].length());
					this.message[this.num] = this.message[this.num].substring(
							0, this.message[this.num].length() - this.posx);
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
			setColor(this.cr[this.numBak], this.cg[this.numBak],
					this.cb[this.numBak]);
			put(this.str);
		}
	}

	private void setGetMessageLength(int d_length) {
		this.getMessageLength[this.num] = d_length;
	}

	public void addString(String mes, LColor color) {
		setColor(color.getRed(), color.getGreen(), color.getBlue());
		addString(mes);
	}

	public void addString(String mes) {
		this.num -= 1;
		if (this.num < 0) {
			this.num = (this.max - 1);
		}
		setGetMessageLength(this.getMessageLength[this.num]);
		put(this.message[this.num] + mes);
	}

	public void setBright(int max, int speed) {
		this.brightMax = max;
		this.brightSpeed = speed;
	}

	public void setWaitTriangleColor(LColor color) {
		this.triangleColor = color;
	}

	public void draw(GLEx g, int dx, int dy, int d_type, int lines) {
		if (bgLTexture != null) {
			g.drawTexture(bgLTexture, dx, dy, getWidth(), getHeight());
		}
		LFont oldFont = g.getFont();
		int oldColor = g.getColorARGB();
		g.setFont(this.font);
		this.countFrame += 1;
		int index = num;
		for (int i = 0; i < this.max - 1; i++) {
			this.num -= 1;
			if (this.num < 0) {
				this.num = (this.max - 1);
			}
			if (i <= lines) {
				for (int i2 = 0; i2 < 2; i2++) {
					if (this.getMessageLength[this.num] < this.message[this.num]
							.length()) {
						String[] temp = this.getMessage;
						temp[this.num] = (temp[this.num] + this.message[this.num]
								.substring(this.getMessageLength[this.num],
										this.getMessageLength[this.num] + 1));
						this.getMessageLength[this.num] += 1;
					}
				}

				if (d_type == 0) {
					this.drawY = (dy + i * this.font.getSize());
				} else {
					this.drawY = (dy - i * this.font.getSize() - this.font
							.getSize());
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

					tmpcolor.setColor(this.drawNewCr[this.num],
							this.drawNewCg[this.num], this.drawNewCb[this.num]);

					this.str = "new";
					drawString(g, this.str, this.posx, this.drawY,tmpcolor);
					this.posx += this.font.stringWidth(this.str);
				}
				tmpcolor.setColor(50, 50, 50);
				drawString(g, this.getMessage[this.num], this.posx + 1,
						this.drawY + 1,tmpcolor);
				tmpcolor.setColor(this.cr[this.num] + this.bright[i],
						this.cg[this.num] + this.bright[i], this.cb[this.num]
								+ this.bright[i]);
				drawString(g, this.getMessage[this.num], this.posx, this.drawY,tmpcolor);
				if ((this.waitFlag) && (i == 0) && index > 0) {
					this.posy = (this.countFrame * 1 / 3 % this.font.getSize()
							/ 2 - 2);
					drawString(
							g,
							"▼",
							this.posx
									+ this.font
											.stringWidth(this.getMessage[this.num]),
							this.drawY - this.posy,this.triangleColor);
				}
				if (this.brightType[i] == 0) {
					this.bright[i] += this.brightSpeed;
					if (this.bright[i] >= this.brightMax) {
						this.bright[i] = this.brightMax;
						this.brightType[i] = 1;
					}
				} else {
					this.bright[i] -= this.brightSpeed;
					if (this.bright[i] < 0) {
						this.bright[i] = 0;
						this.brightType[i] = 0;
					}
				}
			}
		}
		this.num -= 1;
		if (this.num < 0) {
			this.num = (this.max - 1);
		}
		g.setColor(oldColor);
		g.setFont(oldFont);
	}

	public void setWaitFlag(boolean w) {
		this.waitFlag = w;
	}

	public int getMax() {
		return this.max - 1;
	}

	public int getShowType() {
		return showType;
	}

	public void setShowType(int showType) {
		this.showType = showType;
	}

	public int[] getBright() {
		return bright;
	}

	public void setBright(int[] bright) {
		this.bright = bright;
	}

	public int[] getBrightType() {
		return brightType;
	}

	public void setBrightType(int[] brightType) {
		this.brightType = brightType;
	}

	public int getBrightMax() {
		return brightMax;
	}

	public void setBrightMax(int brightMax) {
		this.brightMax = brightMax;
	}

	public int getBrightSpeed() {
		return brightSpeed;
	}

	public void setBrightSpeed(int brightSpeed) {
		this.brightSpeed = brightSpeed;
	}

	public int getPostLine() {
		return postLine;
	}

	public void setPostLine(int postLine) {
		this.postLine = postLine;
	}

	public int getCountFrame() {
		return countFrame;
	}

	public void setCountFrame(int countFrame) {
		this.countFrame = countFrame;
	}

	private void drawString(GLEx g, String str, int x, int y, LColor color) {
		g.drawString(str, x + leftOffset, (y + font.getHeight() - 5)
				+ topOffset, color);
	}

	public int getLeftOffset() {
		return leftOffset;
	}

	public void setLeftOffset(int leftOffset) {
		this.leftOffset = leftOffset;
	}

	public int getTopOffset() {
		return topOffset;
	}

	public void setTopOffset(int topOffset) {
		this.topOffset = topOffset;
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		draw(g, x, y, showType, postLine);
	}

	@Override
	public String getUIName() {
		return "TextArea";
	}

}
