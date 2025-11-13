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
import loon.utils.MathUtils;
import loon.utils.StringUtils;

/**
 * 字符串显示用组件UI,支持多种文字显示特效,主要用来做信息推送显示之类游戏效果(比如rpg打怪时的文字描述什么的)
 */
public class LTextArea extends LComponent implements FontSet<LTextArea> {

	// 数据向下推入
	public static final int TYPE_DOWN = 0;

	// 数据向上推入
	public static final int TYPE_UP = 1;

	private int _leftOffset, _topOffset;

	private int _showType;
	private String[] _message;
	private int[] _bright;
	private int[] _brightType;

	private boolean[] _drawNew;

	private int[] _drawNewCr;
	private int[] _drawNewCg;
	private int[] _drawNewCb;
	private int[] _drawNewLV;

	private int _textMoveSpeed = 10;
	private int _brightMax = 100;
	private int _brightSpeed = 1;
	private int[] _crs;
	private int[] _cgs;
	private int[] _cbs;
	private int _default_cr;
	private int _default_cg;
	private int _default_cb;
	private String[] _getMessage;
	private int[] _getMessageLength;

	private int _messageWidthLimit = 200;
	private int _moveOffset = 20;
	private int _postLine = 0;
	private int _maxAmount;
	private int _amount;
	private int _drawY;

	private String _strTemp;
	private int _posx;
	private int _posy;
	private int _numBak;

	private boolean _centerText;
	private boolean _waitFlag;
	private boolean _flashFont;
	private boolean _over;
	private boolean _slideMessage;

	private boolean _dirty;

	private int[] _slideX;

	private IFont _displayFont;
	private int _countFrame;
	private LColor _triangleColor = LColor.orange;
	private LColor _tmpcolor = new LColor(LColor.white);
	private LColor _fontColor = new LColor(LColor.white);
	private String _lineFlag = LSystem.FLAG_TAG;

	private String _waitFlagString;

	private int _curretR;
	private int _curretG;
	private int _curretB;

	public LTextArea(int x, int y, int w, int h) {
		this(-1, x, y, w, h);
	}

	public LTextArea(int maxAmount, int x, int y, int w, int h) {
		this(LTextArea.TYPE_DOWN, maxAmount, SkinManager.get().getMessageSkin().getFont(), x, y, w, h);
	}

	public LTextArea(int maxAmount, IFont font, int x, int y, int w, int h) {
		this(LTextArea.TYPE_DOWN, maxAmount, font, x, y, w, h);
	}

	public LTextArea(int type, int maxAmount, IFont font, int x, int y, int w, int h) {
		this(type, maxAmount, font, x, y, w, h, SkinManager.get().getMessageSkin().getBackgroundTexture());
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

	public LTextArea(int type, int maxAmount, int x, int y, int w, int h, boolean flashFont) {
		this(type, maxAmount, SkinManager.get().getMessageSkin().getFont(), x, y, w, h, null, flashFont);
	}

	public LTextArea(MessageSkin skin, int type, int maxAmount, int x, int y, int w, int h) {
		this(type, maxAmount, skin.getFont(), x, y, w, h, skin.getBackgroundTexture());
	}

	public LTextArea(int type, int maxAmount, IFont font, int x, int y, int w, int h, boolean flashFont) {
		this(type, maxAmount, font, x, y, w, h, null, flashFont);
	}

	public LTextArea(int type, int maxAmount, IFont font, int x, int y, int w, int h, LTexture bg) {
		this(type, maxAmount, font, x, y, w, h, bg, true);
	}

	public LTextArea(int type, int maxAmount, IFont font, int x, int y, int w, int h, LTexture bg, boolean flashFont) {
		this(type, maxAmount, font, x, y, w, h, bg, flashFont, true, true);
	}

	public LTextArea(int type, int maxAmount, IFont textFont, int x, int y, int w, int h, LTexture bg,
			boolean flashFont, boolean waitFlag, boolean slide) {
		super(x, y, w, h);
		IFont tmp = textFont;
		if (tmp == null) {
			tmp = LSystem.getSystemGameFont();
		}
		this.setFont(tmp);
		this.setBrightSpeed(1);
		this._showType = type;
		this._waitFlagString = "new";
		this._textMoveSpeed = 10;
		if (maxAmount < 0) {
			int size = MathUtils.min(tmp.getHeight(), tmp.getSize());
			if ((size % 2) != 0) {
				size += 1;
			}
			if ((w % size) != 0) {
				setWidth(w + size / 4);
			}
			if ((h % size) != 0) {
				setHeight(h + size / 4);
			}
			this._postLine = MathUtils.ifloor((getHeight() - 4f) / size);
			this.set(_postLine, 255, 255, 255, flashFont);
		} else {
			this.set(maxAmount, 255, 255, 255, flashFont);
		}
		this.setWaitFlag(waitFlag);
		this.setSlideMessage(slide);
		this.setWidthLimit(w);
		if (bg != null) {
			this.onlyBackground(bg);
		}
	}

	public int getMaxLine() {
		return this._maxAmount;
	}

	public LTextArea setMaxLine(int max) {
		return set(max, this._curretR, this._curretG, this._curretB, this._flashFont);
	}

	public LTextArea set(int max, int r, int g, int b, boolean flash) {
		this._maxAmount = MathUtils.max(1, (max + 1));
		this._message = new String[this._maxAmount];
		this._crs = new int[this._maxAmount];
		this._cgs = new int[this._maxAmount];
		this._cbs = new int[this._maxAmount];
		this._bright = new int[this._maxAmount];
		this._brightType = new int[this._maxAmount];
		this._getMessage = new String[this._maxAmount];
		this._getMessageLength = new int[this._maxAmount];
		this._drawNew = new boolean[this._maxAmount];
		this._drawNewCr = new int[this._maxAmount];
		this._drawNewCg = new int[this._maxAmount];
		this._drawNewCb = new int[this._maxAmount];
		this._drawNewLV = new int[this._maxAmount];
		this._slideX = new int[this._maxAmount];
		this._amount = 0;
		for (int i = 0; i < this._maxAmount; i++) {
			this._message[i] = LSystem.EMPTY;
			this._getMessage[i] = LSystem.EMPTY;
			this._getMessageLength[i] = 0;
			this._bright[i] = (this._brightMax * i / this._maxAmount);
		}
		this.setDefaultColor(r, g, b, flash);
		return this;
	}

	public LTextArea setDefaultColor(int r, int g, int b) {
		return setDefaultColor(r, g, b, this._flashFont);
	}

	public LTextArea setDefaultColor(int r, int g, int b, boolean flash) {
		this._flashFont = flash;
		this._default_cr = r;
		this._default_cg = g;
		this._default_cb = b;
		if (this._flashFont) {
			if (this._default_cr > 255 - this._brightMax) {
				this._default_cr = (255 - this._brightMax);
			}
			if (this._default_cg > 255 - this._brightMax) {
				this._default_cg = (255 - this._brightMax);
			}
			if (this._default_cb > 255 - this._brightMax) {
				this._default_cb = (255 - this._brightMax);
			}
		}
		this._curretR = r;
		this._curretG = g;
		this._curretB = b;
		return this;
	}

	@Override
	public IFont getFont() {
		return _displayFont;
	}

	@Override
	public LTextArea setFont(IFont fn) {
		if (fn == null) {
			return this;
		}
		this._displayFont = fn;
		return this;
	}

	public LTextArea setWidthLimit(int widthLimit) {
		this._messageWidthLimit = widthLimit;
		return this;
	}

	public LTextArea setNewFlag() {
		this._drawNew[this._amount] = true;
		return this;
	}

	public LTextArea clear() {
		this._amount = 0;
		return this;
	}

	public int count() {
		return this._amount;
	}

	public LTextArea setSlideMessage(boolean b) {
		this._slideMessage = b;
		return this;
	}

	public LTextArea setCenter(boolean b) {
		this._centerText = b;
		return this;
	}

	public LTextArea setDefaultColor() {
		setColor(_default_cr, _default_cg, _default_cb);
		return this;
	}

	public LTextArea setColor(int d_cr, int d_cg, int d_cb) {
		this._crs[this._amount] = d_cr;
		this._cgs[this._amount] = d_cg;
		this._cbs[this._amount] = d_cb;
		if (_flashFont) {
			if (this._crs[this._amount] > 255 - this._brightMax) {
				this._crs[this._amount] = (255 - this._brightMax);
			}
			if (this._cgs[this._amount] > 255 - this._brightMax) {
				this._cgs[this._amount] = (255 - this._brightMax);
			}
			if (this._cbs[this._amount] > 255 - this._brightMax) {
				this._cbs[this._amount] = (255 - this._brightMax);
			}
		}
		_curretR = d_cr;
		_curretG = d_cg;
		_curretB = d_cb;
		return this;
	}

	public LTextArea put(String mes, LColor color) {
		if (StringUtils.isEmpty(mes)) {
			return this;
		}
		final String[] messages = StringUtils.split(mes, LSystem.LF);
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
		final String[] messages = StringUtils.split(mes, LSystem.LF);
		for (int i = messages.length - 1; i > -1; i--) {
			if (!_flashFont) {
				setDefaultColor();
			}
			putOne(messages[i]);
		}
		return this;
	}

	private void putOne(String mes) {
		this._over = false;
		this._numBak = this._amount;
		this._message[this._amount] = mes;
		if (this._flashFont) {
			if ((this._crs[this._amount] == 0) && (this._cgs[this._amount] == 0) && (this._cbs[this._amount] == 0)) {
				this._crs[this._amount] = this._default_cr;
				this._cgs[this._amount] = this._default_cg;
				this._cbs[this._amount] = this._default_cb;
			} else {
				if (this._crs[this._amount] + this._brightMax > 255) {
					this._crs[this._amount] = (255 - this._brightMax);
				} else if (this._crs[this._amount] < 0) {
					this._crs[this._amount] = 0;
				}
				if (this._cgs[this._amount] + this._brightMax > 255) {
					this._crs[this._amount] = (255 - this._brightMax);
				} else if (this._cgs[this._amount] < 0) {
					this._cgs[this._amount] = 0;
				}
				if (this._cbs[this._amount] + this._brightMax > 255) {
					this._crs[this._amount] = (255 - this._brightMax);
				} else if (this._cbs[this._amount] < 0) {
					this._cbs[this._amount] = 0;
				}
			}
		} else {
			this._crs[this._amount] = this._curretR;
			this._cgs[this._amount] = this._curretG;
			this._cbs[this._amount] = this._curretB;
		}

		if ((this._displayFont != null)
				&& (this._displayFont.stringWidth(this._message[this._amount]) > this._messageWidthLimit)) {
			this._posx = 1;
			for (;;) {
				if (this._displayFont.stringWidth(this._message[this._amount].substring(0,
						this._message[this._amount].length() - this._posx)) <= this._messageWidthLimit) {
					this._strTemp = this._message[this._amount].substring(
							this._message[this._amount].length() - this._posx, this._message[this._amount].length());
					this._message[this._amount] = this._message[this._amount].substring(0,
							this._message[this._amount].length() - this._posx);
					this._over = true;
					break;
				}
				this._posx += 1;
			}

		}

		this._amount += 1;
		if (this._amount >= this._maxAmount) {
			this._amount = 0;
		}

		this._crs[this._amount] = this._default_cr;
		this._cgs[this._amount] = this._default_cg;
		this._cbs[this._amount] = this._default_cb;

		this._getMessageLength[this._amount] = 0;
		this._getMessage[this._amount] = LSystem.EMPTY;

		this._drawNew[this._amount] = false;

		this._slideX[this._amount] = -200;

		if (this._over) {
			setColor(this._crs[this._numBak], this._cgs[this._numBak], this._cbs[this._numBak]);
			put(this._strTemp);
		}
		_dirty = true;
	}

	private void setGetMessageLength(int l) {
		this._getMessageLength[this._amount] = l;
	}

	@Override
	public LTextArea setFontColor(LColor color) {
		this._fontColor = color;
		return this;
	}

	@Override
	public LColor getFontColor() {
		return this._fontColor.cpy();
	}

	public LTextArea addString(String mes) {
		return addString(mes, _fontColor);
	}

	public LTextArea addString(String mes, LColor color) {
		if (color != null) {
			setColor(color.getRed(), color.getGreen(), color.getBlue());
		}
		this._amount -= 1;
		if (this._amount < 0) {
			this._amount = (this._maxAmount - 1);
		}
		setGetMessageLength(this._getMessageLength[this._amount]);
		put(this._message[this._amount] + mes);
		return this;
	}

	public LTextArea setBright(int maxAmount, int speed) {
		this._brightMax = maxAmount;
		this._brightSpeed = speed;
		return this;
	}

	public LTextArea setWaitTriangleColor(LColor color) {
		this._triangleColor = color;
		return this;
	}

	public LTextArea setWaitFlagString(String s) {
		this._waitFlagString = s;
		return this;
	}

	public String getWaitFlagString() {
		return this._waitFlagString;
	}

	public LTextArea setMoveOffset(int m) {
		_moveOffset = m;
		return this;
	}

	public int getMoveOffset() {
		return _moveOffset;
	}

	public void draw(GLEx g, int dx, int dy, int d_type, int lines) {
		if (_dirty) {
			if (_displayFont instanceof LFont) {
				String[] list = new String[this._amount];
				for (int i = 0; i < _amount && i < _message.length; i++) {
					list[i] = _message[i];
				}
				LSTRDictionary.get().bind((LFont) _displayFont, list);
			}
			_dirty = false;
		}
		if (_background != null) {
			g.draw(_background, dx, dy, getWidth(), getHeight(), _component_baseColor);
		}

		final int oldColor = g.color();
		this._countFrame += LSystem.toIScaleFPS(1);
		for (int i = 0; i < this._maxAmount - 1; i++) {
			this._amount -= 1;
			if (this._amount < 0) {
				this._amount = (this._maxAmount - 1);
			}
			if (i <= lines) {
				for (int j = 0; j < 2; j++) {
					if (this._getMessageLength[this._amount] < this._message[this._amount].length()) {
						final String[] temp = this._getMessage;
						temp[this._amount] = (temp[this._amount] + this._message[this._amount].substring(
								this._getMessageLength[this._amount], this._getMessageLength[this._amount] + 1));
						this._getMessageLength[this._amount] += 1;
					}
				}

				if (d_type == 0) {
					this._drawY = (dy + i * this._displayFont.getSize());
				} else {
					this._drawY = (dy - i * this._displayFont.getSize() - this._displayFont.getSize());
				}

				this._posx = dx;

				if (this._centerText) {
					this._posx -= this._displayFont.stringWidth(this._message[this._amount]) / 2;
				}

				if (this._slideMessage) {
					this._posx += this._slideX[this._amount];
					if (this._slideX[this._amount] < 0)
						this._slideX[this._amount] += LSystem.toIScaleFPS(_textMoveSpeed);
					else {
						this._slideX[this._amount] = 0;
					}
				}

				if (this._drawNew[this._amount]) {
					if (this._drawNewLV[this._amount] == 0) {
						this._drawNewCr[this._amount] += _moveOffset;
						if (this._drawNewCr[this._amount] >= 255) {
							this._drawNewCr[this._amount] = 255;
							this._drawNewLV[this._amount] = 1;
						}
					} else if (this._drawNewLV[this._amount] == 1) {
						this._drawNewCg[this._amount] += _moveOffset;
						if (this._drawNewCg[this._amount] >= 255) {
							this._drawNewCg[this._amount] = 255;
							this._drawNewLV[this._amount] = 2;
						}
					} else if (this._drawNewLV[this._amount] == 2) {
						this._drawNewCb[this._amount] += _moveOffset;
						if (this._drawNewCb[this._amount] >= 255) {
							this._drawNewCb[this._amount] = 255;
							this._drawNewLV[this._amount] = 3;
						}
					} else if (this._drawNewLV[this._amount] == 3) {
						this._drawNewCr[this._amount] -= _moveOffset;
						if (this._drawNewCr[this._amount] <= 0) {
							this._drawNewCr[this._amount] = 0;
							this._drawNewLV[this._amount] = 4;
						}
					} else if (this._drawNewLV[this._amount] == 4) {
						this._drawNewCg[this._amount] -= _moveOffset;
						if (this._drawNewCg[this._amount] <= 0) {
							this._drawNewCg[this._amount] = 0;
							this._drawNewLV[this._amount] = 5;
						}
					} else if (this._drawNewLV[this._amount] == 5) {
						this._drawNewCb[this._amount] -= _moveOffset;
						if (this._drawNewCb[this._amount] <= 0) {
							this._drawNewCb[this._amount] = 0;
							this._drawNewLV[this._amount] = 0;
						}
					}

					_tmpcolor.setColor(this._drawNewCr[this._amount], this._drawNewCg[this._amount],
							this._drawNewCb[this._amount]);

					this._strTemp = _waitFlagString;
					drawMessage(g, this._strTemp, this._posx, this._drawY, _tmpcolor);
					this._posx += this._displayFont.stringWidth(this._strTemp);
				}
				if (_flashFont) {
					_tmpcolor.setColor(50, 50, 50);
					drawMessage(g, this._getMessage[this._amount], this._posx + 1, this._drawY + 1, _tmpcolor);
					_tmpcolor.setColor(this._crs[this._amount] + this._bright[i],
							this._cgs[this._amount] + this._bright[i], this._cbs[this._amount] + this._bright[i]);
				} else {
					_tmpcolor.setColor(this._crs[this._amount], this._cgs[this._amount], this._cbs[this._amount]);
				}
				drawMessage(g, this._getMessage[this._amount], this._posx, this._drawY, _tmpcolor);
				final boolean showFlag = (this._waitFlag) && (i == 0);
				if (showFlag) {
					this._posy = (this._countFrame * 1 / 3 % this._displayFont.getSize() / 2 - 2);
					drawMessage(g, _lineFlag,
							this._posx + this._displayFont.stringWidth(this._getMessage[this._amount]),
							this._drawY - this._posy, this._triangleColor);
				}
				if (this._brightType[i] == TYPE_DOWN) {
					this._bright[i] += this._brightSpeed;
					if (this._bright[i] >= this._brightMax) {
						this._bright[i] = this._brightMax;
						this._brightType[i] = TYPE_UP;
					}
				} else {
					this._bright[i] -= this._brightSpeed;
					if (this._bright[i] < 0) {
						this._bright[i] = 0;
						this._brightType[i] = TYPE_DOWN;
					}
				}
			}
		}
		this._amount -= 1;
		if (this._amount < 0) {
			this._amount = (this._maxAmount - 1);
		}
		g.setColor(oldColor);
	}

	public int getTextMoveSpeed() {
		return this._textMoveSpeed;
	}

	public LTextArea setTextMoveSpeed(int s) {
		this._textMoveSpeed = s;
		return this;
	}

	public LTextArea setWaitFlag(boolean w) {
		this._waitFlag = w;
		return this;
	}

	public boolean isDirty() {
		return this._dirty;
	}

	public LTextArea setDirty(boolean d) {
		this._dirty = d;
		return this;
	}

	public int getMax() {
		return this._maxAmount - 1;
	}

	public int getShowType() {
		return _showType;
	}

	public LTextArea setShowType(int showType) {
		this._showType = showType;
		return this;
	}

	public int[] getBright() {
		return _bright;
	}

	public LTextArea setBright(int[] bright) {
		this._bright = bright;
		return this;
	}

	public int[] getBrightType() {
		return _brightType;
	}

	public LTextArea setBrightType(int[] brightType) {
		this._brightType = brightType;
		return this;
	}

	public int getBrightMax() {
		return _brightMax;
	}

	public LTextArea setBrightMax(int brightMax) {
		this._brightMax = brightMax;
		return this;
	}

	public int getBrightSpeed() {
		return _brightSpeed;
	}

	public LTextArea setBrightSpeed(int b) {
		this._brightSpeed = LSystem.toIScaleFPS(b);
		return this;
	}

	public int getPostLine() {
		return _postLine;
	}

	public LTextArea setPostLine(int postLine) {
		this._postLine = postLine;
		return this;
	}

	public int getCountFrame() {
		return _countFrame;
	}

	public LTextArea setCountFrame(int countFrame) {
		this._countFrame = countFrame;
		return this;
	}

	@Override
	public LComponent setBackground(LTexture texture) {
		this._drawBackground = false;
		this._background = texture;
		return this;
	}

	@Override
	public LComponent setBackground(String path) {
		return setBackground(LSystem.loadTexture(path));
	}

	private void drawMessage(GLEx g, String str, int x, int y, LColor color) {
		if (_showType == TYPE_DOWN) {
			_displayFont.drawString(g, str, x + _leftOffset + 5, (y - 5) + _topOffset + _displayFont.getAscent() / 2,
					color);
		} else {
			_displayFont.drawString(g, str, x + _leftOffset + 5,
					(y - 5) + _topOffset + _displayFont.getAscent() / 2 + getHeight() - _displayFont.getHeight(),
					color);
		}
	}

	public int getLeftOffset() {
		return _leftOffset;
	}

	public LTextArea setLeftOffset(int l) {
		this._leftOffset = l;
		return this;
	}

	public int getTopOffset() {
		return _topOffset;
	}

	public LTextArea setTopOffset(int t) {
		this._topOffset = t;
		return this;
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		draw(g, x, y, _showType, _postLine);
	}

	public String getLineFlag() {
		return _lineFlag;
	}

	public LTextArea setLineFlag(String flag) {
		this._lineFlag = flag;
		return this;
	}

	public boolean isFlashFont() {
		return _flashFont;
	}

	public LTextArea setFlashFont(boolean f) {
		this.setDefaultColor(_curretR, _curretG, _curretB, f);
		return this;
	}

	public LColor getCurrentColor() {
		return new LColor(_curretR, _curretG, _curretB);
	}

	@Override
	public String getUIName() {
		return "TextArea";
	}

	@Override
	public void destory() {
	}

}
