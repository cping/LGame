package loon.component;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.font.IFont;
import loon.font.LFont;
import loon.opengl.GLEx;

public class LRoleTextField extends LRole {

	private final int TIME_LINE = 400;
	private String curString;
	private int position;
	private int time;
	private boolean bLineOn;
	private int maxLength;
	private IFont font;

	public LRoleTextField(float x, float y, float width, float height) {
		super(null, x, y, width, height);
	}

	@Override
	public void init() {
		super.init();
		this.curString = "";
		this.position = this.curString.length();
		this.bLineOn = true;
		this.time = 0;
		if (this.maxLength <= 0) {
			this.maxLength = 20;
		}
		if (this.font == null) {
			this.font = LFont.getFont(LSystem.FONT_NAME, 15);
		}
	}

	public IFont getFont() {
		return this.font;
	}

	public void setFont(IFont font) {
		this.font = font;
	}

	public int getMaxLength() {
		return this.maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public String getCurString() {
		return this.curString;
	}
	
	public void setCurString(String curString) {
		this.curString = curString;
	}

	public String getText(){
		return this.curString;
	}

	public void setText(String text) {
		this.setCurString(text);
	}
	
	public int getPosition() {
		return this.position;
	}

	public void setPosition(int position) {
		this.position = position;
		if (this.position < 0)
			this.position = 0;
		else if (this.position > this.curString.length()) {
			this.position = this.curString.length();
		}
		showLine();
	}

	private void deleteTextBackspace() {
		if ((this.curString.length() > 0) && (this.position != 0)) {
			this.curString = (this.curString.substring(0, this.position - 1) + this.curString
					.substring(this.position, this.curString.length()));
			setPosition(getPosition() - 1);
		}
		showLine();
	}

	private void deleteTextDelete() {
		if (this.curString.length() != this.position) {
			this.curString = (this.curString.substring(0, getPosition()) + this.curString
					.substring(getPosition() + 1, this.curString.length()));
		}
		showLine();
	}

	public void addCharacter(int button, char character) {
		if (isBSelect())
			if (button == 8) {
				deleteTextBackspace();
			} else if (button == 127) {
				deleteTextDelete();
			} else if (button == 37) {
				setPosition(getPosition() - 1);
			} else if (button == 39) {
				setPosition(getPosition() + 1);
			} else if ((this.curString.length() <= 20) && (character >= '!')
					&& (character <= '~')) {
				this.curString = (this.curString.substring(0, this.position)
						+ character + this.curString.substring(this.position,
						this.curString.length()));
				this.position += 1;
				showLine();
			}
	}

	private void showLine() {
		if (isBSelect()) {
			this.bLineOn = true;
			this.time = 0;
		}
	}

	@Override
	public void update(long elapsedTime) {
		think(elapsedTime);
	}

	public void think(long delta) {
		this.time += delta;
		if (this.time > TIME_LINE) {
			this.time = 0;
			this.bLineOn = (!this.bLineOn);
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		render(g, x, y);
	}

	@Override
	public void render(GLEx g, int changeX, int changeY) {
		g.setColor(LColor.white);
		g.fillRect((getX() + changeX), (getY() + changeY), getWidth(),
				getHeight());
		if (!isBSelect()) {
			g.setColor(LColor.black);
		} else {
			g.setColor(LColor.red);
		}
		IFont tmp = g.getFont();
		g.drawRect((getX() + changeX), (getY() + changeY), getWidth(),
				getHeight());
		int color = g.color();
		g.setColor(LColor.black);
		if (this.curString != null) {
			g.setFont(this.font);
			g.drawText(this.curString, (getX() + 5.0F + changeX), (getY()
					+ getHeight() / 2.0F + 5F + changeY));
			if ((isBSelect()) && (this.bLineOn)) {
				int w = font.stringWidth(this.curString.substring(0,
						this.position));
				g.drawLine((getX() + 5.0F + w + changeX),
						(getY() + 5.0F + changeY),
						(getX() + 5.0F + w + changeX), (getY() + getHeight()
								/ 2.0F + 10.0F + changeY));
			}
		}
		g.setColor(color);
		g.setFont(tmp);
	}

	@Override
	public String getUIName() {
		return "RoleTextField";
	}
}
