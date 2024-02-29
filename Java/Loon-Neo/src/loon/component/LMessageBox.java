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
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;
import loon.utils.TArray;

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

		private boolean drawFace;
		private boolean _showFlag;
		private float faceX;
		private float faceY;
		private float faceCenterX;
		private float faceCenterY;
		private float faceWidth;
		private float faceHeight;
		private float messageX;
		private float messageY;
		private float messageWidth;
		private float messageHeight;
		private float pageX;
		private float pageY;
		private float offsetX;
		private float offsetY;
		private float _drawScale;

		private float _leading = 0;
		private String _flagType = LSystem.FLAG_TAG;
		private LColor flagColor;

		protected DrawMessageBox(IFont font, LTexture face, LTexture box, String flag, int w, int h) {
			super(font);
			super.init(w, h);

			this.drawWidth = w;
			this.drawHeight = h;

			this.flagColor = LColor.orange;

			this.imgFace = face;
			this.drawFace = false;
			this._showFlag = true;
			this._drawScale = 0.023f;
			this._radius = 10;
			this._textureBox = box;
			this._flagType = StringUtils.isEmpty(flag) ? LSystem.FLAG_TAG : flag;
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
			this.messageHeight = (this._boxHeight * 0.8f);
			this.messageY = (this._boxHeight * 0.08f);
			this.pageX = (this._boxWidth * 0.95f);
			this.pageY = (this._boxHeight * 0.75f);
			this.setFaceDrawMode();
		}

		private void setFaceDrawMode() {
			if (this.drawFace) {
				final float faceScaleWidth = this._boxWidth * _drawScale;
				this.faceWidth = this.faceHeight = (this._boxWidth * 0.18f);
				this.faceX = faceScaleWidth - 5f;
				this.faceY = (this._boxHeight - this.faceHeight) / 2f;
				this.messageX = (this.faceX + this.faceWidth + faceScaleWidth);
				this.messageWidth = (this._boxWidth - messageX * 1.3f);
			} else {
				this.faceX = 0;
				this.faceY = 0;
				this.faceWidth = 0;
				this.faceHeight = 0;
				this.messageX = (this._boxWidth * _drawScale);
				this.messageWidth = (this._boxWidth - this.messageX * 2f);
			}
			this.faceCenterX = (this.faceX + this.faceWidth / 2f);
			this.faceCenterY = (this.faceY + this.faceHeight / 2f);
		}

		public void draw(GLEx g, String message, int row, boolean isPage, LColor c) {
			draw(g, this._boxX, this._boxY, message, row, isPage, c);
		}

		private void draw(GLEx g, float x, float y, String message, int row, boolean isPage, LColor c) {
			this._boxX = x;
			this._boxY = y;
			drawBorder(g, this._boxX, this._boxY, c);
			if (this.drawFace) {
				drawFace(g, this._boxX + offsetX, this._boxY + offsetY);
			}
			final float newX = this._boxX + this.messageX + offsetX;
			final float newY = this._boxY + this.messageY + offsetY;

			drawMessage(g, message, newX, newY);
			if (_showFlag && isPage && _flagType != null) {
				int size = StringUtils.charCount(message, LSystem.LF);
				if (_leading > 0) {
					if (drawFace) {
						this.font.drawString(g, _flagType, newX + this.font.stringWidth(message) - this.font.getSize(),
								newY + this.font.stringHeight(message) + (size * _leading), this.flagColor);
					} else {
						this.font.drawString(g, _flagType, this._boxX + this.pageX + this.offsetX,
								this._boxY + this.pageY + this.font.stringHeight(message) + this.offsetY
										+ (this.font.getSize() * 0.10f) + (size * _leading),
								this.flagColor);
					}
				} else {
					if (drawFace) {
						this.font.drawString(g, _flagType, newX + this.font.stringWidth(message) - this.font.getSize(),
								newY + this.font.stringHeight(message), this.flagColor);
					} else {
						this.font.drawString(g, _flagType, this._boxX + this.pageX + this.offsetX,
								this._boxY + this.pageY + this.font.stringHeight(message) + this.offsetY
										+ (this.font.getSize() * 0.10f),
								this.flagColor);
					}
				}
			}

		}

		private void drawMessage(GLEx g, String message, float x, float y) {
			final IFont displayFont = (this.font == null) ? g.getFont() : this.font;
			if (_leading > 0) {
				final String[] texts = StringUtils.split(message, LSystem.LF);
				final float height = displayFont.getHeight();
				for (int i = 0, size = texts.length; i < size; i++) {
					displayFont.drawString(g, texts[i], x, y + (i * (height + _leading)), this.fontColor);
				}
			} else {
				displayFont.drawString(g, message, x, y, this.fontColor);
			}
		}

		private void drawFace(GLEx g, float x, float y) {
			g.draw(this.imgFace, x + this.faceX, y + this.faceY, this.faceWidth, this.faceHeight);
		}

		public float getLeading() {
			return this._leading;
		}

		public DrawMessageBox setLeading(final float leading) {
			this._leading = leading;
			return this;
		}

		public LColor getFlagColor() {
			return flagColor.cpy();
		}

		public DrawMessageBox setFlagColor(LColor c) {
			this.flagColor = new LColor(c);
			return this;
		}

		public int getMessageWidth() {
			return MathUtils.round(this.messageWidth);
		}

		public int getMessageHeight() {
			return MathUtils.round(this.messageHeight);
		}

		public DrawMessageBox setOffset(float x, float y) {
			this.offsetX = x;
			this.offsetY = y;
			return this;
		}

		public DrawMessageBox setOffsetX(float x) {
			this.offsetX = x;
			return this;
		}

		public DrawMessageBox setOffsetY(float y) {
			this.offsetY = y;
			return this;
		}

		public float getOffsetX() {
			return this.offsetX;
		}

		public float getOffetY() {
			return this.offsetY;
		}

		public boolean isFaceMode() {
			return this.drawFace;
		}

		public void setAutoFaceImage() {
			if (font != null) {
				float sizeOffset = font.getSize() / 2f;
				setOffsetX(sizeOffset);
			}
			this.drawFace = true;
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
			this.drawFace = (this.imgFace != null);
		}

		public void setFaceWidth(float w) {
			this.faceWidth = w;
		}

		public void setFaceHeight(float h) {
			this.faceHeight = h;
		}

		public void setFaceSize(float w, float h) {
			setFaceWidth(w);
			setFaceHeight(h);
		}

		public void setFaceX(float x) {
			this.faceX = x;
		}

		public void setFaceY(float y) {
			this.faceY = y;
		}

		public void setFacePos(float x, float y) {
			this.setFaceX(x);
			this.setFaceY(y);
		}

		public float getFaceX() {
			return this.faceX;
		}

		public float getFaceY() {
			return this.faceY;
		}

		public void setFaceCenterX(float x) {
			this.faceCenterX = x;
		}

		public void setFaceCenterY(float y) {
			this.faceCenterY = y;
		}

		public void setFaceCenterPos(float x, float y) {
			setFaceCenterX(x);
			setFaceCenterY(y);
		}

		public float getFaceCenterX() {
			return this.faceCenterX;
		}

		public float getFaceCenterY() {
			return this.faceCenterY;
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

		private String message;
		private String comment;
		private String face;
		TArray<String> lines;

		public Message(String text, String comm, String face, TArray<String> ls) {
			this.message = text;
			this.comment = comm;
			this.face = face;
			this.lines = ls;
		}

		public String getMessage() {
			return this.message;
		}

		public String getComment() {
			return this.comment;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public String getFace() {
			return this.face;
		}

		public void setFace(String face) {
			this.face = face;
		}

		@Override
		public String toString() {
			return this.message;
		}
	}

	private final ObjectMap<String, LTexture> _faceCache = new ObjectMap<String, LTexture>();

	private final StrBuilder _message = new StrBuilder();

	private boolean _initNativeDraw = false;

	protected int messageIndex = 0;
	protected TArray<Message> _messageList;

	protected long typeDelayTime;
	protected int renderRow;
	protected int renderCol;
	protected boolean finished;
	protected boolean noMessage;
	protected boolean currentNoMessage;
	protected boolean stopMessage;
	protected boolean noPaged;
	protected boolean isPaged;

	protected int pageBlinkTime;

	protected int delay = 30;

	protected int pageTime = 300;

	protected DrawMessageBox _box;

	protected String _messageComma;

	private IFont _font;

	private String _tmpString;

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
		freeRes().add(box);
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
					sbr.append(mes.message);
				}
			}
		}
		if (sbr.size() > 0) {
			this._tmpString = sbr.toString();
			this._initNativeDraw = false;
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
		return this._tmpString;
	}

	@Override
	public IFont getFont() {
		return this._font;
	}

	@Override
	public LMessageBox setFont(IFont font) {
		this._font = font;
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
		Message message = _messageList.get(messageIndex);
		if ((this.typeDelayTime <= 0) && (!this.finished)) {
			this.typeDelayTime = delay;
			if (this.renderCol > message.lines.get(this.renderRow).length() - 1) {
				if (this.renderRow >= message.lines.size - 1) {
					this.finished = true;
					this.pageBlinkTime = pageTime;
				} else {
					this.renderRow += 1;
					this.renderCol = 0;
				}
			} else
				this.renderCol += 1;
		}

	}

	public LMessageBox reset() {
		messageIndex = 0;
		stopMessage = false;
		return restart();
	}

	public LMessageBox loop() {
		if (finished) {
			int size = this.messageIndex + 1;
			if (size < this._messageList.size) {
				setIndex(++this.messageIndex);
				restart();
			} else {
				reset();
			}
		}
		return this;
	}

	public LMessageBox next() {
		if (finished) {
			int size = this.messageIndex + 1;
			if (size < this._messageList.size) {
				setIndex(++this.messageIndex);
				restart();
			}
		}
		return this;
	}

	public LMessageBox setIndex(int index) {
		int size = this.messageIndex + 1;
		if (size > 0 && size < this._messageList.size) {
			this.messageIndex = index;
			restart();
		}
		return this;
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
		this._messageList.get(index).face = face;
		return this;
	}

	public final void postSetIndex() {
		if (this._messageList == null || this._messageList.size == 0) {
			return;
		}
		Message message = this.getMessage(messageIndex);
		String str = message.face;
		if ((StringUtils.isNullOrEmpty(str) || LSystem.NULL.equals(str))) {
			setFaceImage((LTexture) null);
		} else {
			toFaceImage(str, _messageComma);
		}
		restart();
		pauseMessage();
	}

	public void drawMessage(GLEx g) {
		if (!_initNativeDraw) {
			if (_font instanceof LFont) {
				LSTRDictionary.get().bind((LFont) _font, _tmpString);
			}
			_initNativeDraw = true;
		}
		if (_messageList == null || _messageList.size == 0) {
			return;
		}
		Message message = _messageList.get(messageIndex);
		_message.setLength(0);

		if (!message.lines.isEmpty()) {

			final int sizeRow = this.renderRow;
			final int sizeCol = this.renderCol;

			for (int i = 0; i < sizeRow + 1; i++) {

				final String line = message.lines.get(i);
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

		if ((this.finished) && (!this.noPaged)) {
			if (this.pageBlinkTime > pageTime) {
				this.pageBlinkTime = 0;
				this.isPaged = (!this.isPaged);
			}
		} else {
			this.isPaged = false;
		}

		this._box.draw(g, _message.toString(), renderRow, this.isPaged, this._component_baseColor);
	}

	public boolean isCompleted() {
		return this.finished;
	}

	public LMessageBox restart() {
		this.renderCol = 0;
		this.renderRow = 0;
		this.typeDelayTime = delay;
		this.pageBlinkTime = 0;
		this.finished = false;
		return this;
	}

	public LMessageBox showAll() {
		final Message message = _messageList.get(messageIndex);
		if (message.lines.isEmpty()) {
			this.renderRow = (this.renderCol = 0);
		} else {
			this.renderRow = (message.lines.size - 1);
			this.renderCol = message.lines.get(this.renderRow).length();
			this.finished = true;
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
				if (mes != null && mes.message != null) {
					mes.lines = FontUtils.splitLines(mes.message, this._font, this._box.getMessageWidth());
				}
			}
		}
		return this;
	}

	public LTexture getFaceImage() {
		return this._box.imgFace;
	}

	public LMessageBox pauseMessage() {
		this.stopMessage = true;
		return this;
	}

	public LMessageBox resumeMessage() {
		this.stopMessage = false;
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
		int size = result.length;
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

	@Override
	public void createUI(GLEx g, int x, int y) {
		drawMessage(g);
	}

	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		this._box.setLocation(this._objectLocation);
		if (!this.noMessage && _messageList.size > 0) {
			Message message = _messageList.get(messageIndex);
			if (!StringUtils.isEmpty(message.face)) {
				toFaceImage(message.face, _messageComma);
			}
			if ((!this.stopMessage) && (!message.lines.isEmpty())) {
				this.typeDelayTime -= elapsedTime;
				updateType();
			}
			if (this.finished) {
				this.pageBlinkTime += elapsedTime;
			}
		}
	}

	public int getPageTime() {
		return pageTime;
	}

	public LMessageBox setPageTime(int pageTime) {
		this.pageTime = pageTime;
		return this;
	}

	public LColor getFlagColor() {
		return _box.getFlagColor();
	}

	public LMessageBox setFlagColor(LColor c) {
		this._box.setFlagColor(c);
		return this;
	}

	public int getDelay() {
		return delay;
	}

	public LMessageBox setDelay(int time) {
		this.delay = time;
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
	}

}
