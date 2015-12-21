package loon.stg.effect;

import loon.LRelease;
import loon.LTexture;
import loon.LTextures;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 简单的特效生成器，STG模块专用
 */
public class EffectOne implements LRelease {

	public final static int TYPE_NONE = 0;

	public final static int TYPE_BREAK = 1;

	public final static int TYPE_FIRE = 2;

	public final static int TYPE_BALL = 3;

	int number;

	int[] arrayX, arrayY, arrayR;

	int[] mageList;

	LTexture[] images;

	protected int imageLength;

	protected int time;

	protected int drawType;

	protected int drawX;

	protected int drawY;

	protected int drawW;

	protected int drawH;

	protected int iBx;

	protected int iBy;

	protected static double[] sinX;

	protected static double[] cosX;

	public EffectOne(String fileName) {
		this(LTextures.loadTexture(fileName));
	}

	public EffectOne(final LTexture texture) {
		this(new LTexture[] { texture });
	}

	public EffectOne(LTexture[] image) {
		this(image, 10);
	}

	public EffectOne(LTexture[] image, int num) {
		if (sinX == null || cosX == null) {
			sinX = new double[20];
			cosX = new double[20];
			for (int j = 0; j < 20; j++) {
				sinX[j] = MathUtils.sin((MathUtils.PI * j) / 10f);
				cosX[j] = MathUtils.cos((MathUtils.PI * j) / 10f);
			}
		}
		this.time = 0;
		this.drawType = 0;
		this.number = num;
		this.arrayX = new int[number];
		this.arrayY = new int[number];
		this.arrayR = new int[number];
		this.mageList = new int[number];
		this.images = image;
		this.imageLength = images.length;
		if (imageLength < number) {
			int itime = number / imageLength;
			for (int j = 0; j < imageLength; j++) {
				for (int l = 0; l < itime; l++) {
					mageList[j * itime + l] = j;
				}
			}
			for (int j = imageLength * itime; j < number; j++) {
				mageList[j] = imageLength;
			}
		} else {
			for (int j = 0; j < number; j++) {
				mageList[j] = j;
			}
		}
		this.iBx = images[0].getWidth() / 2;
		this.iBy = images[0].getHeight() / 2;
	}

	public void load() {
		for (int j = 0; j < number; j++) {
			arrayX[j] = 0;
			arrayY[j] = -20;
			arrayR[j] = 11 + j * number;
		}
	}

	public void setType(int type, int x, int y, int w, int h) {
		this.drawType = type;
		this.setDrawLocation(x, y, w, h);
		for (int j = 0; j < number; j++) {
			arrayX[j] = drawX;
			arrayY[j] = drawY;
		}
		this.time = 0;
	}

	public void setDrawLocation(int x, int y, int w, int h) {
		this.drawX = x - w / 2;
		this.drawY = y - h / 2;
		this.drawW = w;
		this.drawH = h;
	}

	public void draw(GLEx g) {
		time++;
		switch (drawType) {
		case TYPE_NONE:
			break;
		case TYPE_BREAK:
			drawBreak(g);
			break;
		case TYPE_FIRE:
			drawFire(g);
			break;
		case TYPE_BALL:
			drawBall(g);
			break;
		default:
			renderExpand(g, drawType);
			break;
		}
	}

	protected void renderExpand(GLEx g, int i) {
	}

	protected void drawBreak(GLEx g) {
		int activeNum = randLocation();
		arrayX[activeNum] = (int) (drawX
				+ MathUtils.abs(MathUtils.random() % drawW));
		arrayY[activeNum] = (int) (drawY
				+ MathUtils.abs(MathUtils.random() % drawH));
		for (int j = 0; j < activeNum; j++) {
			int state = (j + number) - activeNum;
			if (mageList[state] != imageLength) {
				g.draw(images[mageList[state]], arrayX[j] - iBx,
						arrayY[j] - iBy);
			}
		}
		for (int j = activeNum; j < number; j++) {
			int state = j - activeNum;
			if (mageList[state] != imageLength) {
				g.draw(images[mageList[state]], arrayX[j] - iBx,
						arrayY[j] - iBy);
			}
		}

	}

	protected void drawBall(GLEx g) {
		int activeNum = randLocation();
		arrayR[activeNum] = 20;
		for (int j = 0; j < number; j++) {
			arrayR[j] += 8;
			g.setColor(255 - j * 10, 100 + j * 5, 0);
			for (int l = 0; l < 20; l++) {
				g.drawOval(getX(j, l) - arrayR[j] / 2, getY(j, l) - arrayR[j]
						/ 2, arrayR[j], arrayR[j]);
			}
		}
		g.resetColor();
	}

	protected void drawFire(GLEx g) {
		int activeNum = randLocation();
		arrayR[activeNum] = 20;
		for (int j = 0; j < number; j++) {
			arrayR[j] += 8;
			g.setColor(255 - j * 10, 100 + j * 5, 0);
			for (int l = 0; l < 20; l++) {
				g.drawLine(getX0(j, l), getY0(j, l), getX(j, l), getY(j, l));
			}
		}
		g.resetColor();
	}

	protected int randLocation() {
		int activeNum = getActiveNum();
		arrayX[activeNum] = (int) (drawX
				+ MathUtils.abs(MathUtils.random() % drawW));
		arrayY[activeNum] = (int) (drawY
				+ MathUtils.abs(MathUtils.random() % drawH));
		return activeNum;
	}

	protected int getActiveNum() {
		return number - time % number - 1;
	}

	protected int getX0(int index, int direct) {
		return arrayX[index]
				+ ((int) (arrayR[index] * cosX[direct]) >> 2);
	}

	protected int getY0(int index, int direct) {
		return arrayY[index]
				+ ((int) (arrayR[index] * sinX[direct]) >> 2);
	}

	protected int getX(int index, int direct) {
		return arrayX[index] + (int) (arrayR[index] * cosX[direct]);
	}

	protected int getY(int index, int direct) {
		return arrayY[index] + (int) (arrayR[index] * sinX[direct]);
	}

	public int getType() {
		return drawType;
	}

	public void setNullType() {
		drawType = 0;
	}

	@Override
	public void close() {
		if (images != null) {
			for (int i = 0; i < images.length; i++) {
				if (images[i] != null) {
					images[i].close();
					images[i] = null;
				}
			}
		}
	}

}
