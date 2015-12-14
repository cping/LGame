package loon.component;

import loon.LTexture;
import loon.font.IFont;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.opengl.ShadowFont;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * 此组件功能近似LMessage，并且允许连续播放文字序列 ，设置角色头像和显示位置，差异在于，此组件不支持彩色文字设置，也就是只允许'\n'符号生效
 * 而在效率上无文字缓存，所以总体帧率耗费比LMessage更大，适合动态频率高的场合使用，但是此组件多个同时存在会影响帧率
 **/
/*
 * 以下为简单用例:
 * 
 * LTexture texture = DefUI.getGameWinFrame(200, 200); LMessageBox box = new
 * LMessageBox(new String[] { "人间谁能看尽山色，千里孤行终归寂寞。翻天覆地炙手可热，百年之后有谁记得。",
 * "明月西斜遗珠何落，金乌归海乾坤并合。世事如棋造化难说，能解其中非你非我。" }, texture, 66, 66, 180, 180);
 * box.getMessageBox().setOffset(10, 10); add(box); box.SetClick(new
 * ClickListener() {
 * 
 * @Override public void UpClick(LComponent comp, float x, float y) {
 * 
 * }
 * 
 * @Override public void DragClick(LComponent comp, float x, float y) {
 * 
 * }
 * 
 * @Override public void DownClick(LComponent comp, float x, float y) {
 * LMessageBox box = (LMessageBox) comp; box.next(); }
 * 
 * @Override public void DoClick(LComponent comp) {
 * 
 * } });
 * 
 * } });
 */
public class LMessageBox extends LComponent {

	public final static String defalut_flagType = "▼";

	public static class DrawMessageBox extends AbstractBox {

		private final int DEFAULT_WIDTH;
		private final int DEFAULT_HEIGHT;

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

		private String flagType = defalut_flagType;

		protected DrawMessageBox(IFont font, LTexture face, LTexture box,
				int w, int h) {
			super(font);
			super.init(w, h);

			this.DEFAULT_WIDTH = w;
			this.DEFAULT_HEIGHT = h;

			this.imgFace = face;
			this.drawFace = false;
			this._radius = 10;
			this._textureBox = box;
		}

		public void setFlagType(String f) {
			this.flagType = f;
		}

		public void reinit() {
			this._boxWidth = DEFAULT_WIDTH;
			this._boxHeight = DEFAULT_HEIGHT;
			this._borderW = 3f;
			this.messageHeight = (this._boxHeight * 0.8f);
			this.messageY = (this._boxHeight * 0.08f);
			setFaceDrawMode();
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
				this.messageWidth = (this._boxWidth - (this.faceX * 2f
						+ this.faceWidth + this._boxWidth * 0.025f));
			} else {
				this.faceX = 0;
				this.faceY = 0;
				this.faceWidth = 0;
				this.faceHeight = 0;
				this.messageX = (this._boxWidth * 0.023f);
				this.messageWidth = (this._boxWidth - this.messageX * 2f);
			}
		}

		public void draw(GLEx g, String message, int row, boolean isPage) {
			draw(g, this._boxX, this._boxY, message, row, isPage);
		}

		private void draw(GLEx g, float x, float y, String message, int row,
				boolean isPage) {
			this._boxX = x;
			this._boxY = y;
			drawBorder(g, this._boxX, this._boxY);
			if (this.drawFace) {
				drawFace(g, this._boxX + offsetX, this._boxY + offsetY);
			}
			drawMessage(g, message, this._boxX + this.messageX + offsetX,
					this._boxY + this.messageY + offsetY);
			if (isPage && flagType != null) {
				this.font.drawString(g, flagType, this._boxX + this.pageX
						+ this.offsetX, this._boxY + this.pageY
						+ this.font.stringHeight(message)
						+ this.offsetY, this.fontColor);
			}
		}

		private void drawMessage(GLEx g, String message, float x, float y) {
			this.font.drawString(g, message, x, y, this.fontColor);
		}

		private void drawFace(GLEx g, float x, float y) {
			g.draw(this.imgFace, x + this.faceCenterX, y + this.faceCenterY);
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

		public void setOffset(float x, float y) {
			this.offsetX = x;
			this.offsetY = y;
		}

		public void setOffsetX(float x) {
			this.offsetX = x;
		}

		public void setOffsetY(float y) {
			this.offsetY = y;
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
			String path = "";
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

	private final static ObjectMap<String, LTexture> facepools = new ObjectMap<String, LTexture>();

	public static void addFaceImage(String name, LTexture tex) {
		facepools.put(name, tex);
	}

	public static LTexture getFaceImage(String name) {
		return facepools.get(name);
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

		public String toString() {
			return this.message;
		}
	}

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
	protected DrawMessageBox _box;

	protected int delay = 50;

	protected int pageTime = 300;

	private IFont _font;

	public LMessageBox(TArray<Message> messages, int x, int y, int width,
			int height) {
		this(messages, null, LFont.getDefaultFont(), null, x, y, width, height);
	}

	public LMessageBox(TArray<Message> messages, LTexture texture, int x,
			int y, int width, int height) {
		this(messages, null, LFont.getDefaultFont(), texture, x, y, width,
				height);
	}

	public LMessageBox(TArray<Message> messages, String typeFlag, LFont font,
			LTexture box, int x, int y, int width, int height) {
		super(x, y, width, height);
		this._messageList = messages;
		StringBuilder sbr = new StringBuilder();
		if (messages != null) {
			for (Message text : messages) {
				sbr.append(text.message);
			}
		}
		this._box = new DrawMessageBox(new ShadowFont(font, sbr.toString(),
				typeFlag == null ? defalut_flagType : typeFlag, true), null,
				box, width, height);
		this._font = font;
	}

	public LMessageBox(String[] messages, int x, int y, int width, int height) {
		this(messages, null, LFont.getDefaultFont(), null, null, x, y, width,
				height);
	}

	public LMessageBox(String[] messages, LTexture texture, int x, int y,
			int width, int height) {
		this(messages, null, LFont.getDefaultFont(), null, texture, x, y,
				width, height);
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
	public LMessageBox(String[] messages, String typeFlag, LFont font,
			String face, LTexture box, int x, int y, int width, int height) {
		super(x, y, width, height);
		if (messages != null) {
			_messageList = new TArray<LMessageBox.Message>();
			for (String text : messages) {
				_messageList.add(new Message(text, null, face, Print
						.formatMessage(text, font, width)));
			}
		}
		this._box = new DrawMessageBox(new ShadowFont(font, messages,
				typeFlag == null ? defalut_flagType : typeFlag, true), null,
				box, width, height);
		if (!StringUtils.isEmpty(face)) {
			toFaceImage(face);
		}
		this._font = font;
	}

	public IFont getFont() {
		return this._font;
	}

	public void setFont(IFont font) {
		this._font = font;
	}

	public DrawMessageBox getMessageBox() {
		return this._box;
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

	public void reset() {
		messageIndex = 0;
		stopMessage = false;
		restart();
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
		if ((str == null || str.equals("null"))) {
			setFaceImage(null);
		} else {
			toFaceImage(str);
		}
		restart();
		pauseMessage();
	}

	private final StringBuilder _message = new StringBuilder();

	public void drawMessage(GLEx g) {
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

		this._box.draw(g, _message.toString(), renderRow, this.isPaged);
	}

	public boolean isCompleted() {
		return this.finished;
	}

	public void restart() {
		this.renderCol = 0;
		this.renderRow = 0;
		this.typeDelayTime = delay;
		this.pageBlinkTime = 0;
		this.finished = false;
	}

	public void showAll() {
		Message message = _messageList.get(messageIndex);
		if (message.lines.isEmpty()) {
			this.renderRow = (this.renderCol = 0);
		} else {
			this.renderRow = (message.lines.size - 1);
			this.renderCol = message.lines.get(this.renderRow).length();
			this.finished = true;
		}
	}

	public void setFaceImage(LTexture texture) {
		this._box.setFaceImage(texture);
	}

	public void setFaceImage(LTexture texture, float x, float y) {
		this._box.setFaceImage(texture);
		this._box.setFaceCenterX(x);
		this._box.setFaceCenterY(y);
	}

	public LTexture getFaceImage() {
		return this._box.imgFace;
	}

	public void pauseMessage() {
		this.stopMessage = true;
	}

	public void resumeMessage() {
		this.stopMessage = false;
	}

	protected void toFaceImage(final String face) {
		if (face == null) {
			return;
		}
		String[] result = StringUtils.split(face, ',');
		int size = result.length;
		if (size > 0) {
			if (3 == size) {
				setFaceImage(facepools.get(result[0]),
						Float.valueOf(result[1]), Float.valueOf(result[2]));
			} else if (2 == size) {
				setFaceImage(facepools.get(result[0]),
						Float.valueOf(result[1]), Float.valueOf(result[1]));
			} else {
				setFaceImage(facepools.get(result[0]));
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

	public void setPageTime(int pageTime) {
		this.pageTime = pageTime;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int time) {
		this.delay = time;
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		drawMessage(g);
	}

	@Override
	public String getUIName() {
		return "MessageBox";
	}

}
