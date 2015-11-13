package loon.component;

import java.util.HashMap;
import java.util.List;

import loon.LTexture;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.opengl.ShadowFont;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

public class LMessageBox {

	public static class DrawMessageBox extends AbstractBox {

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
		private final int DEFAULT_WIDTH;
		private final int DEFAULT_HEIGHT;
		private String flagType = "▼";

		protected DrawMessageBox(ShadowFont font, LTexture face, LTexture box,
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

		public void draw(GLEx g, String message, boolean isPage) {
			draw(g, this._boxX, this._boxY, message, isPage);
		}

		private void draw(GLEx g, float x, float y, String message,
				boolean isPage) {
			this._boxX = x;
			this._boxY = y;
			drawBorder(g, this._boxX, this._boxY);
			if (this.drawFace) {
				drawFace(g, this._boxX, this._boxY);
			}
			drawMessage(g, message, this._boxX + this.messageX, this._boxY
					+ this.messageY);
			if (isPage && flagType != null) {
				this.font.drawString(g, this._boxX + this.pageX, this._boxY
						+ this.pageY, flagType, this.fontColor);
			}
		}

		private void drawMessage(GLEx g, String message, float x, float y) {
			this.font.drawString(g, x, y, message, this.fontColor);
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

	private final static HashMap<String, LTexture> facepools = new HashMap<String, LTexture>();

	public static void addFaceImage(String name, LTexture tex) {
		facepools.put(name, tex);
	}

	public static LTexture getFaceImage(String name) {
		return facepools.get(name);
	}

	public class Message {
		private String message;
		private String comment;
		private String face;

		public Message() {
			this.message = "";
			this.comment = "";
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
	}

	protected int messageIndex;
	protected List<Message> messageList;
	protected List<String> lines;
	protected int typeDelayTime;
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

	public static int MESSAGE_TYPE_INTERVAL = 50;
	public static final int MESSAGE_DEFAULT_TYPE_INTERVAL = 50;
	public static int MESSAGE_PAGE_BLINK_TIME = 300;

	public final List<String> _list;

	private LFont _font;

	public LMessageBox(String text, LFont font, LTexture face, LTexture box,
			int w, int h) {
		this._list = Print.formatMessage(text, font, w);
		this._box = new DrawMessageBox(new ShadowFont(font, text, true), face,
				box, w, h);
		this._font = font;
	}

	protected void updateType() {
		if ((this.typeDelayTime <= 0) && (!this.finished)) {
			this.typeDelayTime = MESSAGE_TYPE_INTERVAL;
			if (this.renderCol > ((String) this.lines.get(this.renderRow))
					.length() - 1) {
				if (this.renderRow >= this.lines.size() - 1) {
					this.finished = true;
					this.pageBlinkTime = MESSAGE_PAGE_BLINK_TIME;
				} else {
					this.renderRow += 1;
					this.renderCol = 0;
				}
			} else
				this.renderCol += 1;
		}
	}

	public void nextIndex() {
		setIndex(++this.messageIndex);
	}

	public void setIndex(int index) {
		this.messageIndex = index;
	}

	protected final void postSetIndex() {
		if (this.messageList == null) {
			return;
		}
		String str = ((Message) this.messageList.get(this.messageIndex))
				.getFace();
		if (str == null || str.equals("null")) {
			setFaceImage(null);
		} else {
			String[] result = StringUtils.split(str, ',');
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
		restart();
		pauseMessage();
	}

	protected void drawMessage(GLEx g) {
		StringBuilder message = new StringBuilder();

		if (!this.lines.isEmpty()) {
			for (int i = 0; i < this.renderRow + 1; i++) {
				String line = (String) this.lines.get(i);

				int len = 0;

				if (i < this.renderRow)
					len = line.length();
				else {
					len = this.renderCol;
				}

				String t = line.substring(0, len);
				if (t.length() != 0) {
					if (len == line.length())
						message.append(t + "\n");
					else {
						message.append(t);
					}
				}
			}
		}

		if ((this.finished) && (!this.noPaged)) {
			if (this.pageBlinkTime > MESSAGE_PAGE_BLINK_TIME) {
				this.pageBlinkTime = 0;
				this.isPaged = (!this.isPaged);
			}
		} else {
			this.isPaged = false;
		}

		this._box.draw(g, message.toString(), this.isPaged);
	}

	protected void restart() {
		this.renderCol = 0;
		this.renderRow = 0;
		this.typeDelayTime = MESSAGE_TYPE_INTERVAL;
		this.pageBlinkTime = 0;
		this.finished = false;
		String message = null;
		if ((this.messageList != null) && (this.messageList.size() > 0)) {
			message = ((Message) this.messageList.get(this.messageIndex))
					.getMessage();
		}
		if (message != null) {
			this.lines = Print.formatMessage(message, this._font,
					this._box.getMessageWidth());
		}
	}

	protected void showAll() {
		if (this.lines.isEmpty()) {
			this.renderRow = (this.renderCol = 0);
		} else {
			this.renderRow = (this.lines.size() - 1);
			this.renderCol = ((String) this.lines.get(this.renderRow)).length();
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
}
