package loon.stg.effect;

import loon.LTexture;
import loon.opengl.GLEx;

public class EffectTwo extends EffectOne {

	private String message;

	private int mesWidth;

	private int mesHeight;

	private LTexture[] shoutImg;

	public static final int TYPE_STRING = 3;

	public static final int TYPE_IMGS = 4;

	public EffectTwo(String fileName) {
		this(LTexture.createTexture(fileName));
	}

	public EffectTwo(final LTexture texture) {
		this(new LTexture[] { texture });
	}

	public EffectTwo(LTexture[] image) {
		this(image, 20);
	}

	public EffectTwo(LTexture[] image, int num) {
		super(image, num);
		this.message = "";
	}

	public void setString(String str, int width, int height) {
		this.message = str;
		this.mesWidth = width;
		this.mesHeight = height;
	}

	public void setShoutImg(LTexture[] img) {
		this.shoutImg = img;
	}

	@Override
	protected void renderExpand(GLEx g, int type) {
		if (type == TYPE_STRING) {
			drawString(g);
		} else if (type == TYPE_IMGS && shoutImg != null) {
			drawImage(g);
		}
	}

	private void drawString(GLEx g) {
		int activeNum = getActiveNum();
		arrayR[activeNum] = 20;
		g.setColor(224, 255, 255);
		for (int j = 0; j < number; j++) {
			arrayR[j] += 8;
			g.drawString(message, getShoutX(j) - mesWidth, getShoutY(j) - mesHeight);
		}
		g.resetColor();
	}

	protected int getShoutX(int direct) {
		return drawX + (int) (arrayR[direct] * cosX[direct]);
	}

	protected int getShoutY(int direct) {
		return drawY + (int) (arrayR[direct] * sinX[direct]);
	}

	private void drawImage(GLEx g) {
		int length = shoutImg.length;
		if (length == 0) {
			return;
		}
		int activeNum = getActiveNum();
		arrayR[activeNum] = 20;
		for (int j = 0; j < number; j++) {
			arrayR[j] += 8;
			for (int i = 0; i < 20; i++) {
				g.draw(shoutImg[arrayR[j] / 8 & length], getX(j, i), getY(j, i));
			}
		}
	}
}
