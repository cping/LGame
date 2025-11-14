/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.component;

import java.util.Iterator;

import loon.LRelease;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.canvas.LColor;
import loon.component.skin.MessageSkin;
import loon.component.skin.SkinManager;
import loon.font.FontSet;
import loon.font.FontUtils;
import loon.font.IFont;
import loon.opengl.GLEx;
import loon.utils.ConfigReader;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.ObjectSet;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.timer.Duration;

/**
 * 此组件功能近似LMessage，并且允许连续播放文字序列
 * ，设置角色头像和显示位置，差异在于，此组件不支持彩色文字设置，也就是只允许LSystem.LF符号生效
 * 而在效率上无文字缓存，所以总体帧率耗费比LMessage更大，适合动态频率高的场合使用，但是此组件多个同时存在会影响帧率
 * 
 * 以下为简单用例:
 * 
 * <pre>
 * LTexture texture = DefUI.getGameWinFrame(200, 200); 
 * LMessageBox box = new LMessageBox(new String[] { "人间谁能看尽山色，千里孤行终归寂寞。翻天覆地炙手可热，百年之后有谁记得。",
 * "明月西斜遗珠何落，金乌归海乾坤并合。世事如棋造化难说，能解其中非你非我。" }, texture, 66, 66, 180, 180);
 * box.getMessageBox().setOffset(10, 10); add(box); box.SetClick(new ClickListener() {
 * 
 * &#64;Override public void UpClick(LComponent comp, float x, float y) {
 * 
 * }
 * 
 * &#64;Override public void DragClick(LComponent comp, float x, float y) {
 * 
 * }
 * 
 * &#64;Override public void DownClick(LComponent comp, float x, float y) {
 * LMessageBox box = (LMessageBox) comp; box.next(); 
 * }
 * 
 * &#64;Override public void DoClick(LComponent comp) {
 * 
 * } });
 * 
 * } });
 * </pre>
 */
public class LMessageBox extends LComponent implements FontSet<LMessageBox> {

	public static class DrawMessageBox extends AbstractBox implements LRelease {

		private final int drawWidth;
		private final int drawHeight;

		LTexture imgFace;

		private boolean _drawFace;
		private boolean _showFlag;
		private boolean _gradientFontColor;

		private float _faceX;
		private float _faceY;
		private float _faceCenterX;
		private float _faceCenterY;
		private float _faceWidth;
		private float _faceHeight;
		private float _messageX;
		private float _messageY;
		private float _messageWidth;
		private float _messageHeight;
		private float _pageX;
		private float _pageY;
		private float _offsetX;
		private float _offsetY;
		private float _drawScale;

		private float _leading = 0;
		private String _flagType = LSystem.FLAG_TAG;
		private LColor _flagColor;
		private LColor _fontGradientTempColor = new LColor();

		protected DrawMessageBox(IFont font, LTexture face, LTexture box, String flag, int w, int h) {
			super(font);
			super.init(w, h);

			this.drawWidth = w;
			this.drawHeight = h;

			this._flagColor = LColor.orange;

			this.imgFace = face;
			this._drawFace = false;
			this._showFlag = true;
			this._drawScale = 0.023f;
			this._radius = 10;
			this._textureBox = box;
			this._flagType = StringUtils.isEmpty(flag) ? LSystem.FLAG_TAG : flag;
		}

		public boolean isGradientFontColor() {
			return _gradientFontColor;
		}

		public DrawMessageBox setGradientFontColor(boolean g) {
			this._gradientFontColor = g;
			return this;
		}

		public void setFlagShow(boolean f) {
			this._showFlag = f;
		}

		public void setFlagType(String f) {
			this._flagType = f;
		}

		public void reinit() {
			this._boxWidth = drawWidth;
			this._boxHeight = drawHeight;
			this._borderW = 3f;
			this._messageHeight = (this._boxHeight * 0.8f);
			this._messageY = (this._boxHeight * 0.08f);
			this._pageX = (this._boxWidth * 0.95f);
			this._pageY = (this._boxHeight * 0.75f);
			this.setFaceDrawMode();
		}

		private void setFaceDrawMode() {
			if (this._drawFace) {
				final float faceScaleWidth = this._boxWidth * _drawScale;
				this._faceWidth = this._faceHeight = (this._boxWidth * 0.18f);
				this._faceX = faceScaleWidth - 5f;
				this._faceY = (this._boxHeight - this._faceHeight) / 2f;
				this._messageX = (this._faceX + this._faceWidth + faceScaleWidth);
				this._messageWidth = (this._boxWidth - _messageX * 1.3f);
			} else {
				this._faceX = 0;
				this._faceY = 0;
				this._faceWidth = 0;
				this._faceHeight = 0;
				this._messageX = (this._boxWidth * _drawScale);
				this._messageWidth = (this._boxWidth - this._messageX * 2f);
			}
			this._faceCenterX = (this._faceX + this._faceWidth / 2f);
			this._faceCenterY = (this._faceY + this._faceHeight / 2f);
		}

		public void draw(GLEx g, String message, int row, boolean isPage, boolean finished, LColor c) {
			draw(g, this._boxX, this._boxY, message, row, isPage, finished, c);
		}

		private void draw(GLEx g, float x, float y, String message, int row, boolean isPage, boolean finished,
				LColor c) {
			this._boxX = x;
			this._boxY = y;
			drawBorder(g, this._boxX, this._boxY, c);
			if (this._drawFace) {
				_drawFace(g, this._boxX + _offsetX, this._boxY + _offsetY);
			}
			final float newX = this._boxX + this._messageX + _offsetX;
			final float newY = this._boxY + this._messageY + _offsetY;

			drawMessage(g, message, newX, newY, finished);
			if (_showFlag && isPage && _flagType != null) {
				int size = StringUtils.charCount(message, LSystem.LF);
				if (_leading > 0) {
					if (_drawFace) {
						this.font.drawString(g, _flagType, newX + this.font.stringWidth(message) - this.font.getSize(),
								newY + this.font.stringHeight(message) + (size * _leading), this._flagColor);
					} else {
						this.font.drawString(g, _flagType, this._boxX + this._pageX + this._offsetX,
								this._boxY + this._pageY + this.font.stringHeight(message) + this._offsetY
										+ (this.font.getSize() * 0.10f) + (size * _leading),
								this._flagColor);
					}
				} else {
					if (_drawFace) {
						this.font.drawString(g, _flagType, newX + this.font.stringWidth(message) - this.font.getSize(),
								newY + this.font.stringHeight(message), this._flagColor);
					} else {
						this.font.drawString(g, _flagType, this._boxX + this._pageX + this._offsetX,
								this._boxY + this._pageY + this.font.stringHeight(message) + this._offsetY
										+ (this.font.getSize() * 0.10f),
								this._flagColor);
					}
				}
			}

		}

		private LColor getGradientFontColor(float curIndex, float maxTextCount, boolean finished, LColor color) {
			if (!finished && _gradientFontColor) {
				float alpha = MathUtils.clamp((1f - curIndex / maxTextCount) + 0.05f, 0, 1f);
				_fontGradientTempColor.setColor(color, alpha);
				return _fontGradientTempColor;
			} else {
				return color;
			}
		}

		private void drawMessage(GLEx g, String message, float x, float y, boolean finished) {
			final IFont displayFont = (this.font == null) ? g.getFont() : this.font;
			if (_leading > 0) {
				final String[] texts = StringUtils.split(message, LSystem.LF);
				final float height = displayFont.getHeight();
				for (int i = 0, size = texts.length; i < size; i++) {
					displayFont.drawString(g, texts[i], x, y + (i * (height + _leading)),
							getGradientFontColor(i, size, finished, fontColor));
				}
			} else {
				displayFont.drawString(g, message, x, y, this.fontColor);
			}
		}

		private void _drawFace(GLEx g, float x, float y) {
			g.draw(this.imgFace, x + this._faceX, y + this._faceY, this._faceWidth, this._faceHeight);
		}

		public float getLeading() {
			return this._leading;
		}

		public DrawMessageBox setLeading(final float leading) {
			this._leading = leading;
			return this;
		}

		public LColor getFlagColor() {
			return _flagColor.cpy();
		}

		public DrawMessageBox setFlagColor(LColor c) {
			this._flagColor = new LColor(c);
			return this;
		}

		public int getMessageWidth() {
			return MathUtils.round(this._messageWidth);
		}

		public int getMessageHeight() {
			return MathUtils.round(this._messageHeight);
		}

		public DrawMessageBox setOffset(float x, float y) {
			this._offsetX = x;
			this._offsetY = y;
			return this;
		}

		public DrawMessageBox setOffsetX(float x) {
			this._offsetX = x;
			return this;
		}

		public DrawMessageBox setOffsetY(float y) {
			this._offsetY = y;
			return this;
		}

		public float getOffsetX() {
			return this._offsetX;
		}

		public float getOffetY() {
			return this._offsetY;
		}

		public boolean isFaceMode() {
			return this._drawFace;
		}

		public void setAutoFaceImage() {
			if (font != null) {
				float sizeOffset = font.getSize() / 2f;
				setOffsetX(sizeOffset);
			}
			this._drawFace = true;
			setFaceDrawMode();
			reinit();
		}

		public void setAutoFaceImage(String path) {
			setAutoFaceImage(LTextures.loadTexture(path));
		}

		public void setAutoFaceImage(LTexture face) {
			if (face == this.imgFace) {
				return;
			}
			if (font != null) {
				float sizeOffset = font.getSize() / 2f;
				setOffsetX(sizeOffset);
			}
			setFaceImage(face);
			setFaceDrawMode();
			reinit();
		}

		public void setFaceImage(String path) {
			setFaceImage(LTextures.loadTexture(path));
		}

		public void setFaceImage(LTexture face) {
			if (face == this.imgFace) {
				return;
			}
			this.imgFace = face;
			this._drawFace = (this.imgFace != null);
		}

		public void setFaceWidth(float w) {
			this._faceWidth = w;
		}

		public void setFaceHeight(float h) {
			this._faceHeight = h;
		}

		public void setFaceSize(float w, float h) {
			setFaceWidth(w);
			setFaceHeight(h);
		}

		public void setFaceX(float x) {
			this._faceX = x;
		}

		public void setFaceY(float y) {
			this._faceY = y;
		}

		public void setFacePos(float x, float y) {
			this.setFaceX(x);
			this.setFaceY(y);
		}

		public float getFaceX() {
			return this._faceX;
		}

		public float getFaceY() {
			return this._faceY;
		}

		public void setFaceCenterX(float x) {
			this._faceCenterX = x;
		}

		public void setFaceCenterY(float y) {
			this._faceCenterY = y;
		}

		public void setFaceCenterPos(float x, float y) {
			setFaceCenterX(x);
			setFaceCenterY(y);
		}

		public float getFaceCenterX() {
			return this._faceCenterX;
		}

		public float getFaceCenterY() {
			return this._faceCenterY;
		}

		@Override
		public void dirty() {

		}

		@Override
		public void close() {
			if (imgFace != null) {
				imgFace.close();
				imgFace = null;
			}

		}

	}

	public static class Message {

		private String _message;
		private String _comment;
		private String _face;
		TArray<String> _lines;

		public Message(String text, String comm, String face, TArray<String> ls) {
			this._message = text;
			this._comment = comm;
			this._face = face;
			this._lines = ls;
		}

		public String getMessage() {
			return this._message;
		}

		public String getComment() {
			return this._comment;
		}

		public void setMessage(String message) {
			this._message = message;
		}

		public void setComment(String comment) {
			this._comment = comment;
		}

		public String getFace() {
			return this._face;
		}

		public void setFace(String face) {
			this._face = face;
		}

		@Override
		public String toString() {
			return this._message;
		}
	}

	private final ObjectMap<String, LTexture> _faceCache = new ObjectMap<String, LTexture>();

	private final StrBuilder _message = new StrBuilder();

	protected int _messageIndex = 0;
	protected TArray<Message> _messageList;

	protected long _typeDelayTime;
	protected int _renderRow;
	protected int _renderCol;
	protected boolean _finished;
	protected boolean _noMessage;
	protected boolean _currentNoMessage;
	protected boolean _stopMessage;
	protected boolean _noPaged;
	protected boolean _isPaged;

	protected long _pageBlinkTime;

	protected long _delay = 30;

	protected long _pageTime = 300;

	protected DrawMessageBox _box;

	protected String _messageComma;

	private IFont _font;

	private String _messageString;

	public LMessageBox(int x, int y, int width, int height) {
		this((TArray<Message>) null, x, y, width, height);
	}

	public LMessageBox(LTexture texture, int x, int y, int width, int height) {
		this(null, null, SkinManager.get().getMessageSkin().getFont(), null, texture, x, y, width, height);
	}

	public LMessageBox(TArray<Message> messages, int x, int y, int width, int height) {
		this(messages, null, SkinManager.get().getMessageSkin().getFont(), null, x, y, width, height);
	}

	public LMessageBox(TArray<Message> messages, LTexture texture, int x, int y, int width, int height) {
		this(messages, null, SkinManager.get().getMessageSkin().getFont(), texture, x, y, width, height);
	}

	public LMessageBox(String[] messages, int x, int y, int width, int height) {
		this(messages, null, SkinManager.get().getMessageSkin().getFont(), null, null, x, y, width, height);
	}

	public LMessageBox(String[] messages, IFont font, int x, int y, int width, int height) {
		this(messages, null, font, null, null, x, y, width, height);
	}

	public LMessageBox(String[] messages, LTexture texture, IFont font, int x, int y, int width, int height) {
		this(messages, null, font, null, texture, x, y, width, height);
	}

	public LMessageBox(String[] messages, LTexture texture, int x, int y, int width, int height) {
		this(messages, null, SkinManager.get().getMessageSkin().getFont(), null, texture, x, y, width, height);
	}

	public LMessageBox(TArray<Message> messages, String typeFlag, IFont font, LTexture box, int x, int y) {
		this(messages, typeFlag, font, box, x, y, 0, 0);
	}

	public LMessageBox(TArray<Message> messages, String typeFlag, IFont font, LTexture box, int x, int y, int width,
			int height) {
		this(messages, typeFlag, font, box, x, y, width, height, SkinManager.get().getMessageSkin().getFontColor());
	}

	public LMessageBox(MessageSkin skin, TArray<Message> messages, String typeFlag, int x, int y, int width,
			int height) {
		this(messages, typeFlag, skin.getFont(), skin.getBackgroundTexture(), x, y, width, height, skin.getFontColor());
	}

	public LMessageBox(String[] messages, String typeFlag, IFont font, LTexture box, int x, int y) {
		this(messages, typeFlag, font, null, box, x, y, 0, 0);
	}

	public LMessageBox(String[] messages, IFont font, LTexture box, int x, int y) {
		this(messages, null, font, null, box, x, y, 0, 0);
	}

	public LMessageBox(String[] messages, LTexture box, int x, int y) {
		this(messages, null, SkinManager.get().getMessageSkin().getFont(), null, box, x, y, 0, 0);
	}

	public LMessageBox(TArray<Message> messages, String typeFlag, IFont font, LTexture box, int x, int y, int width,
			int height, LColor color) {
		super(x, y, width, height);
		initMessages(messages, typeFlag, font, box, x, y, width, height, color);
	}

	public LMessageBox(String[] messages, String typeFlag, IFont font, String face, LTexture box, int x, int y,
			int width, int height) {
		this(messages, typeFlag, font, face, box, x, y, width, height,
				SkinManager.get().getMessageSkin().getFontColor());
	}

	/**
	 * 若传递字符串数组，则只能构建统一头像位置的对话框
	 * 
	 * @param messages
	 * @param typeFlag
	 * @param font
	 * @param face
	 * @param box
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public LMessageBox(String[] messages, String typeFlag, IFont font, String face, LTexture box, int x, int y,
			int width, int height, LColor color) {
		super(x, y, width, height);
		initStrings(messages, typeFlag, font, face, box, x, y, width, height, color);
	}

	private void initStrings(String[] messages, String typeFlag, IFont font, String face, LTexture box, int x, int y,
			int width, int height, LColor color) {
		final TArray<LMessageBox.Message> tempMessages = new TArray<LMessageBox.Message>();
		if (messages != null) {
			for (String text : messages) {
				tempMessages.add(new Message(text, null, face, FontUtils.splitLines(text, font, width())));
			}
		}
		initMessages(tempMessages, typeFlag, font, box, x, y, width, height, color);
	}

	private void initMessages(TArray<Message> messages, String typeFlag, IFont font, LTexture box, int x, int y,
			int width, int height, LColor color) {
		this._component_baseColor = color;
		if (box != null && width == 0 && height == 0) {
			this.setSize(box.getWidth(), box.getHeight());
		}
		this.setMessages(messages);
		if (this._box == null) {
			this._box = new DrawMessageBox(this._font = font, null, box, typeFlag, width(), height());
		} else {
			this._box.font = font;
			this._box._textureBox = box;
			this._box._flagType = typeFlag;
			this._box._boxWidth = width;
			this._box._boxHeight = height;
		}
		this._box.setLocation(x, y);
		this._delay = 30;
		this._pageTime = 300;
		freeRes().add(box);
	}

	public boolean isGradientFontColor() {
		return _box.isGradientFontColor();
	}

	public LMessageBox setGradientFontColor(boolean g) {
		this._box.setGradientFontColor(g);
		return this;
	}

	public LMessageBox setMessages(TArray<Message> messages) {
		if (messages == null) {
			messages = new TArray<LMessageBox.Message>();
		}
		this._messageList = messages;
		final StrBuilder sbr = new StrBuilder();
		if (_messageList != null) {
			for (int i = 0; i < _messageList.size; i++) {
				Message mes = _messageList.get(i);
				if (mes != null) {
					sbr.append(mes._message);
				}
			}
		}
		if (sbr.size() > 0) {
			this._messageString = sbr.toString();
		}
		return this;
	}

	public LMessageBox addMessage(Message mes) {
		if (mes == null) {
			return this;
		}
		if (this._messageList == null) {
			this._messageList = new TArray<LMessageBox.Message>();
		}
		this._messageList.add(mes);
		this.setMessages(_messageList);
		return this;
	}

	public LMessageBox addMessage(String face, String message) {
		if (face == null || message == null) {
			return this;
		}
		addMessage(new Message(message, null, face, null));
		updateMessageLocation();
		return this;
	}

	public LMessageBox clearMessage() {
		_messageIndex = 0;
		if (_messageList != null) {
			_messageList.clear();
		}
		return this;
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		if (_box != null) {
			this._box.setLocation(x, y);
		}
	}

	public float getLeading() {
		return this._box.getLeading();
	}

	public LMessageBox setLeading(final float leading) {
		this._box.setLeading(leading);
		return this;
	}

	public String getString() {
		return this._messageString;
	}

	@Override
	public IFont getFont() {
		return this._font;
	}

	@Override
	public LMessageBox setFont(IFont font) {
		this._font = font;
		if (_box != null) {
			_box.setFont(font);
		}
		return this;
	}

	@Override
	public LColor getFontColor() {
		if (_box != null) {
			return _box.getFontColor();
		}
		return LColor.white.cpy();
	}

	@Override
	public LMessageBox setFontColor(LColor color) {
		if (_box != null) {
			_box.setFontColor(color);
		}
		return this;
	}

	public LMessageBox setBoxAlpha(float alpha) {
		if (_box != null) {
			_box.setBoxAlpha(alpha);
		}
		return this;
	}

	public DrawMessageBox getMessageBox() {
		return this._box;
	}

	public LMessageBox setBoxOffset(float x, float y) {
		this._box.setOffset(x, y);
		return this;
	}

	public LMessageBox setBoxOffsetX(float x) {
		this._box.setOffsetX(x);
		return this;
	}

	public LMessageBox setBoxOffsetY(float y) {
		this._box.setOffsetY(y);
		return this;
	}

	protected void updateType() {
		Message message = _messageList.get(_messageIndex);
		if ((this._typeDelayTime <= 0) && (!this._finished)) {
			this._typeDelayTime = _delay;
			if (this._renderCol > message._lines.get(this._renderRow).length() - 1) {
				if (this._renderRow >= message._lines.size - 1) {
					this._finished = true;
					this._pageBlinkTime = _pageTime;
				} else {
					this._renderRow += 1;
					this._renderCol = 0;
				}
			} else
				this._renderCol += 1;
		}

	}

	@Override
	public LMessageBox reset() {
		super.reset();
		_messageIndex = 0;
		_stopMessage = false;
		return restart();
	}

	public LMessageBox loop() {
		if (_finished) {
			if (hasNext()) {
				setIndex(++this._messageIndex);
				restart();
			} else {
				reset();
			}
		}
		return this;
	}

	public LMessageBox next() {
		if (_finished) {
			if (hasNext()) {
				setIndex(++this._messageIndex);
				restart();
			}
		}
		return this;
	}

	public boolean hasNext() {
		return this._messageIndex + 1 < this._messageList.size;
	}

	public LMessageBox setIndex(int index) {
		if (index > -1 && index < this._messageList.size) {
			this._messageIndex = index;
			restart();
		}
		return this;
	}

	public LMessageBox setMessageIndex(int index) {
		return setIndex(index);
	}

	public Message getMessage(int index) {
		if (this._messageList == null) {
			return null;
		}
		return this._messageList.get(index);
	}

	public LMessageBox setMessageFace(int index, String face) {
		if (this._messageList == null) {
			return this;
		}
		if (index > this._messageList.size - 1) {
			return null;
		}
		this._messageList.get(index)._face = face;
		return this;
	}

	public final void postSetIndex() {
		if (this._messageList == null || this._messageList.size == 0) {
			return;
		}
		Message message = this.getMessage(_messageIndex);
		String str = message._face;
		if ((StringUtils.isNullOrEmpty(str) || LSystem.NULL.equals(str))) {
			setFaceImage((LTexture) null);
		} else {
			toFaceImage(str, _messageComma);
		}
		restart();
		pauseMessage();
	}

	public void drawMessage(GLEx g) {
		if (_messageList == null || _messageList.size == 0) {
			return;
		}
		Message message = _messageList.get(_messageIndex);
		_message.setLength(0);

		if (!message._lines.isEmpty()) {

			final int sizeRow = this._renderRow;
			final int sizeCol = this._renderCol;

			for (int i = 0; i < sizeRow + 1; i++) {

				int linelen = message._lines.size;

				if (i >= linelen) {
					continue;
				}

				final String line = message._lines.get(i);
				int len = 0;
				if (i < sizeRow) {
					len = line.length();
				} else {
					len = sizeCol;
				}
				final String mes = line.substring(0, len);
				if (mes.length() != 0) {
					if (len == line.length())
						_message.append(mes).append(LSystem.LF);
					else {
						_message.append(mes);
					}
				}
			}
		}

		if ((this._finished) && (!this._noPaged)) {
			if (this._pageBlinkTime > _pageTime) {
				this._pageBlinkTime = 0;
				this._isPaged = (!this._isPaged);
			}
		} else {
			this._isPaged = false;
		}

		this._box.draw(g, _message.toString(), _renderRow, this._isPaged, this._finished, this._component_baseColor);
	}

	public boolean isCompleted() {
		return this._finished;
	}

	public LMessageBox restart() {
		this._renderCol = 0;
		this._renderRow = 0;
		this._typeDelayTime = _delay;
		this._pageBlinkTime = 0;
		this._finished = false;
		return this;
	}

	public LMessageBox showAll() {
		final Message message = _messageList.get(_messageIndex);
		if (message._lines.isEmpty()) {
			this._renderRow = (this._renderCol = 0);
		} else {
			this._renderRow = (message._lines.size - 1);
			this._renderCol = message._lines.get(this._renderRow).length();
			this._finished = true;
		}
		return this;
	}

	public LMessageBox setFaceImage(String path) {
		this._box.setFaceImage(path);
		return this;
	}

	public LMessageBox setFaceImage(LTexture texture) {
		this._box.setFaceImage(texture);
		return this;
	}

	public LMessageBox setFaceImage(String path, float x, float y) {
		return setFaceImage(LTextures.loadTexture(path), x, y);
	}

	public LMessageBox setFaceImage(LTexture texture, float x, float y) {
		this._box.setFaceImage(texture);
		this._box.setFacePos(x, y);
		return this;
	}

	public LMessageBox setAutoFaceImage(LTexture texture) {
		this._box.setAutoFaceImage(texture);
		return this;
	}

	public LMessageBox setAutoFaceImage(String path) {
		this._box.setAutoFaceImage(path);
		this.updateMessageLocation();
		return this;
	}

	public LMessageBox setAutoFaceImage() {
		this._box.setAutoFaceImage();
		this.updateMessageLocation();
		return this;
	}

	public LMessageBox updateMessageLocation() {
		if (this._messageList == null) {
			return this;
		}
		final TArray<Message> mess = _messageList;
		final int size = mess.size;
		if (size > 0) {
			for (int i = size - 1; i > -1; i--) {
				final Message mes = mess.get(i);
				if (mes != null && mes._message != null) {
					mes._lines = FontUtils.splitLines(mes._message, this._font, this._box.getMessageWidth());
				}
			}
		}
		return this;
	}

	public LTexture getFaceImage() {
		return this._box.imgFace;
	}

	public LMessageBox pauseMessage() {
		this._stopMessage = true;
		return this;
	}

	public LMessageBox resumeMessage() {
		this._stopMessage = false;
		return this;
	}

	protected void toFaceImage(final String face, final String comma) {
		if (face == null || _faceCache.size == 0) {
			return;
		}
		String[] result = null;
		if (StringUtils.isNotEmpty(comma)) {
			result = StringUtils.split(face, comma);
		} else {
			result = StringUtils.split(face, LSystem.COMMA);
		}
		final int size = result.length;
		if (size > 0) {
			if (3 == size) {
				setFaceImage(_faceCache.get(result[0]), Float.valueOf(result[1]), Float.valueOf(result[2]));
			} else if (2 == size) {
				setFaceImage(_faceCache.get(result[0]), Float.valueOf(result[1]), Float.valueOf(result[1]));
			} else {
				setFaceImage(_faceCache.get(result[0]));
			}
		}
	}

	public LMessageBox load(String path, String keyName) {
		return load(path, keyName, true);
	}

	public LMessageBox load(String path, String keyName, boolean saveName) {
		return load(path, keyName, null, saveName);
	}

	public LMessageBox load(String path, String keyName, String childName) {
		return load(path, keyName, childName, true);
	}

	public LMessageBox load(String path, String keyName, String childName, boolean displayName) {
		if (keyName == null) {
			return this;
		}
		setAutoFaceImage();
		clearMessage();
		final ConfigReader config = ConfigReader.shared(path);
		final ObjectSet<String> items = new ObjectSet<String>();
		String[] lists = null;
		if (!StringUtils.isNullOrEmpty(childName)) {
			lists = config.getNewlineList(keyName + LSystem.DOT + childName);
		} else {
			lists = config.getNewlineList(keyName);
		}
		for (int i = 0; i < lists.length; i++) {
			String[] message = StringUtils.split(lists[i], LSystem.COLON);
			if (message.length == 0) {
				message = StringUtils.split(lists[i], LSystem.EQUAL);
			}
			if (message.length > 1) {
				if (displayName) {
					addMessage(message[0], message[0] + LSystem.COLON + message[1]);
				} else {
					addMessage(message[0], message[1]);
				}
				items.add(message[0]);
			}
		}
		if (items.size() > 0) {
			for (Iterator<String> it = items.iterator(); it.hasNext();) {
				String result = it.next();
				if (!StringUtils.isNullOrEmpty(result)) {
					String key = result + LSystem.DOT + "face";
					bindFaceImage(result, config.get(key));
				}
			}
		}
		restart();
		return this;
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		drawMessage(g);
	}

	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		this._box.setLocation(this._objectLocation);
		if (!this._noMessage && _messageList.size > 0) {
			Message message = _messageList.get(_messageIndex);
			if (!StringUtils.isEmpty(message._face)) {
				toFaceImage(message._face, _messageComma);
			}
			if ((!this._stopMessage) && (!message._lines.isEmpty())) {
				this._typeDelayTime -= elapsedTime;
				updateType();
			}
			if (this._finished) {
				this._pageBlinkTime += elapsedTime;
			}
		}
	}

	public long getPageTime() {
		return _pageTime;
	}

	public LMessageBox setPageTimeS(float sec) {
		return setPageTime(Duration.ofS(sec));
	}

	public LMessageBox setPageTime(long pageTime) {
		this._pageTime = pageTime;
		return this;
	}

	public long getDelay() {
		return _delay;
	}

	public LMessageBox setDelayS(float sec) {
		return setDelay(Duration.ofS(sec));
	}

	public LMessageBox setDelay(long time) {
		this._delay = time;
		return this;
	}

	public LMessageBox setMessageDelayS(float sec) {
		return setDelayS(sec);
	}

	public LMessageBox setMessageDelay(long time) {
		return setDelay(time);
	}

	public LColor getFlagColor() {
		return _box.getFlagColor();
	}

	public LMessageBox setFlagColor(LColor c) {
		this._box.setFlagColor(c);
		return this;
	}

	public LMessageBox bindFaceImage(String name, LTexture tex) {
		_faceCache.put(name, tex);
		return this;
	}

	public LMessageBox bindFaceImage(String name, String path) {
		_faceCache.put(name, LTextures.loadTexture(path));
		return this;
	}

	public LTexture getFaceImage(String name) {
		return _faceCache.get(name);
	}

	public String getMessageComma() {
		return _messageComma;
	}

	public LMessageBox setMessageComma(String c) {
		this._messageComma = c;
		return this;
	}

	public int getMessageIndex() {
		return this._messageIndex;
	}

	public LMessageBox setBoxFlag(String type) {
		this._box.setFlagType(type);
		return this;
	}

	public LMessageBox flagShow() {
		this._box.setFlagShow(true);
		return this;
	}

	public LMessageBox flagHide() {
		this._box.setFlagShow(false);
		return this;
	}

	@Override
	public String getUIName() {
		return "MessageBox";
	}

	@Override
	public void destory() {
		if (_box != null) {
			_box.close();
		}
		for (Iterator<LTexture> it = _faceCache.values(); it.hasNext();) {
			LTexture tex = it.next();
			if (tex != null) {
				tex.close();
			}
		}
		_faceCache.clear();
		clearMessage();
		_stopMessage = true;
	}

}
