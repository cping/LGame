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

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.component.skin.MessageSkin;
import loon.component.skin.SkinManager;
import loon.font.FontSet;
import loon.font.IFont;
import loon.font.LFont;
import loon.font.ShadowFont;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.StrBuilder;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * 此组件功能近似LMessage，并且允许连续播放文字序列 ，设置角色头像和显示位置，差异在于，此组件不支持彩色文字设置，也就是只允许'\n'符号生效
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

	public static class DrawMessageBox extends AbstractBox {

		private final int drawWidth;
		private final int drawHeight;

		LTexture imgFace;

		private boolean drawFace;
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
			this._radius = 10;
			this._textureBox = box;
			this._flagType = StringUtils.isEmpty(flag) ? LSystem.FLAG_TAG : flag;
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
			this.setFaceDrawMode();
			this.pageX = (this._boxWidth * 0.95f);
			this.pageY = (this._boxHeight * 0.75f);
		}

		private void setFaceDrawMode() {
			if (this.drawFace) {
				this.faceX = (this._boxWidth * 0.023f);
				this.faceY = 5f;
				this.faceWidth = (this._boxWidth * 0.19f);
				this.faceHeight = this.faceWidth;
				this.faceCenterX = (this.faceX + this.faceWidth / 2f);
				this.faceCenterY = (this.faceY + this.faceHeight / 2f);
				this.messageX = (this.faceX + this.faceWidth + this._boxWidth * 0.023f);
				this.messageWidth = (this._boxWidth - (this.faceX * 2f + this.faceWidth + this._boxWidth * 0.025f));
			} else {
				this.faceX = 0;
				this.faceY = 0;
				this.faceWidth = 0;
				this.faceHeight = 0;
				this.messageX = (this._boxWidth * 0.023f);
				this.messageWidth = (this._boxWidth - this.messageX * 2f);
			}
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

			boolean supportPack = false;

			if (useLFont) {
				LFont newFont = (LFont) font;
				supportPack = newFont.isSupportCacheFontPack();
				newFont.setSupportCacheFontPack(false);
			}

			drawMessage(g, message, this._boxX + this.messageX + offsetX, this._boxY + this.messageY + offsetY);
			if (isPage && _flagType != null) {
				int size = StringUtils.charCount(message, '\n');
				if (_leading > 0) {
					this.font.drawString(g, _flagType, this._boxX + this.pageX + this.offsetX,
							this._boxY + this.pageY + this.font.stringHeight(message) + this.offsetY
									+ (this.font.getSize() * 0.10f) + (size * _leading),
							this.flagColor);
				} else {
					this.font.drawString(g, _flagType,
							this._boxX + this.pageX + this.offsetX, this._boxY + this.pageY
									+ this.font.stringHeight(message) + this.offsetY + (this.font.getSize() * 0.10f),
							this.flagColor);
				}
			}

			if (useLFont && supportPack) {
				LFont newFont = (LFont) font;
				newFont.setSupportCacheFontPack(supportPack);
			}
		}

		private void drawMessage(GLEx g, String message, float x, float y) {
			if (_leading > 0) {
				String[] texts = StringUtils.split(message, '\n');
				for (int i = 0, size = texts.length; i < size; i++) {
					this.font.drawString(g, texts[i], x, y + (i * (font.getHeight() + _leading)), this.fontColor);
				}
			} else {
				this.font.drawString(g, message, x, y, this.fontColor);
			}
		}

		private void drawFace(GLEx g, float x, float y) {
			g.draw(this.imgFace, x + this.faceCenterX, y + this.faceCenterY);
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

		@Override
		public void setBoxAlpha(float alpha) {
			super.setBoxAlpha(alpha);
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

		public void setFaceImage(LTexture face) {
			String path = LSystem.EMPTY;
			if (this.imgFace != null) {
				path = this.imgFace.getSource();
			}
			if (face != null) {
				this.drawFace = true;
				if (!path.equals(face.getSource())) {
					this.imgFace = face;
				}
			} else {
				this.drawFace = false;
			}
			setFaceDrawMode();
		}

		public void setFaceCenterX(float x) {
			this.faceCenterX = x;
		}

		public void setFaceCenterY(float y) {
			this.faceCenterX = faceCenterY;
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

	}

	private final ObjectMap<String, LTexture> faceCache = new ObjectMap<String, LTexture>();

	public void addFaceImage(String name, LTexture tex) {
		faceCache.put(name, tex);
	}

	public LTexture getFaceImage(String name) {
		return faceCache.get(name);
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

	protected int delay = 50;

	protected int pageTime = 300;

	protected final DrawMessageBox _box;

	private IFont _font;

	private String _tmpString;

	private boolean _showShadow = false;

	public LMessageBox(TArray<Message> messages, int x, int y, int width, int height) {
		this(messages, null, SkinManager.get().getMessageSkin().getFont(), null, x, y, width, height);
	}

	public LMessageBox(TArray<Message> messages, LTexture texture, int x, int y, int width, int height) {
		this(messages, null, SkinManager.get().getMessageSkin().getFont(), texture, x, y, width, height);
	}

	public LMessageBox(String[] messages, int x, int y, int width, int height) {
		this(messages, null, SkinManager.get().getMessageSkin().getFont(), null, null, x, y, width, height);
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

	public LMessageBox(TArray<Message> messages, String typeFlag, IFont font, LTexture box, int x, int y, int width,
			int height, LColor color) {
		this(messages, typeFlag, font, box, x, y, width, height, color, false);
	}

	public LMessageBox(TArray<Message> messages, String typeFlag, IFont font, LTexture box, int x, int y, int width,
			int height, LColor color, boolean shadow) {
		super(x, y, width, height);
		this._component_baseColor = color;
		this._showShadow = shadow;
		if (box != null && width == 0 && height == 0) {
			this.setSize(box.getWidth(), box.getHeight());
		}
		this._messageList = messages;
		StrBuilder sbr = new StrBuilder();
		if (messages != null) {
			for (Message text : messages) {
				sbr.append(text.message);
			}
		}
		this._tmpString = sbr.toString();
		if (font instanceof LFont) {
			this._box = new DrawMessageBox(new ShadowFont((LFont) font, _tmpString,
					typeFlag == null ? LSystem.FLAG_TAG : typeFlag, _showShadow), null, box, typeFlag, width(),
					height());
		} else {
			this._box = new DrawMessageBox(font, null, box, typeFlag, width(), height());
		}
		this._box.setLocation(x, y);
		this._font = font;
		freeRes().add(box);
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
			int width, int height) {
		super(x, y, width, height);

		if (box != null && width <= 1 && height <= 1) {
			this.setSize(box.getWidth(), box.getHeight());
		}

		if (messages != null) {
			_messageList = new TArray<LMessageBox.Message>();
			for (String text : messages) {

				_messageList.add(new Message(text, null, face, Print.formatMessage(text, font, width())));

				_tmpString += text;
			}
		}
		if (font instanceof LFont) {
			this._box = new DrawMessageBox(
					new ShadowFont((LFont) font, messages, typeFlag == null ? LSystem.FLAG_TAG : typeFlag, _showShadow),
					null, box, typeFlag, width(), height());
		} else {
			this._box = new DrawMessageBox(font, null, box, typeFlag, width(), height());
		}
		this._box.setLocation(getX(), getY());
		if (!StringUtils.isEmpty(face)) {
			toFaceImage(face);
		}
		this._font = font;
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

	public final void postSetIndex() {
		if (this._messageList == null) {
			return;
		}
		String str = this._messageList.get(this.messageIndex).getFace();
		if ((str == null || "null".equals(str))) {
			setFaceImage(null);
		} else {
			toFaceImage(str);
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
		Message message = _messageList.get(messageIndex);
		_message.delete(0, _message.length());

		if (!message.lines.isEmpty()) {
			for (int i = 0; i < this.renderRow + 1; i++) {
				String line = message.lines.get(i);

				int len = 0;

				if (i < this.renderRow)
					len = line.length();
				else {
					len = this.renderCol;
				}

				String t = line.substring(0, len);
				if (t.length() != 0) {
					if (len == line.length())
						_message.append(t + "\n");
					else {
						_message.append(t);
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
		Message message = _messageList.get(messageIndex);
		if (message.lines.isEmpty()) {
			this.renderRow = (this.renderCol = 0);
		} else {
			this.renderRow = (message.lines.size - 1);
			this.renderCol = message.lines.get(this.renderRow).length();
			this.finished = true;
		}
		return this;
	}

	public LMessageBox setFaceImage(LTexture texture) {
		this._box.setFaceImage(texture);
		return this;
	}

	public LMessageBox setFaceImage(LTexture texture, float x, float y) {
		this._box.setFaceImage(texture);
		this._box.setFaceCenterX(x);
		this._box.setFaceCenterY(y);
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

	protected void toFaceImage(final String face) {
		if (face == null) {
			return;
		}
		String[] result = StringUtils.split(face, ',');
		int size = result.length;
		if (size > 0) {
			if (3 == size) {
				setFaceImage(faceCache.get(result[0]), Float.valueOf(result[1]), Float.valueOf(result[2]));
			} else if (2 == size) {
				setFaceImage(faceCache.get(result[0]), Float.valueOf(result[1]), Float.valueOf(result[1]));
			} else {
				setFaceImage(faceCache.get(result[0]));
			}
		}
	}

	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		this._box.setLocation(this._location);
		if (!this.noMessage) {
			Message message = _messageList.get(messageIndex);
			if (!StringUtils.isEmpty(message.face)) {
				toFaceImage(message.face);
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

	public void setFlagColor(LColor c) {
		this._box.setFlagColor(c);
	}

	public int getDelay() {
		return delay;
	}

	public LMessageBox setDelay(int time) {
		this.delay = time;
		return this;
	}

	public boolean isShowShadowFont() {
		return _showShadow;
	}

	public void setShowShadowFont(boolean s) {
		this._showShadow = s;
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		drawMessage(g);
	}

	@Override
	public String getUIName() {
		return "MessageBox";
	}

}
