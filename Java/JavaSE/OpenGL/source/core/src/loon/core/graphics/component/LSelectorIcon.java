package loon.core.graphics.component;

import loon.action.sprite.SpriteBatch;
import loon.core.graphics.LColor;
import loon.core.graphics.device.LGraphics;
import loon.core.graphics.opengl.GLEx;

public class LSelectorIcon {
	private int x;
	private int y;
	private int mainX;
	private int mainY;
	private int pSize;
	private int minX;
	private int minY;
	private int maxX;
	private int maxY;

	private int colorIndex;
	private double alpha;
	private double alphaRate;
	private boolean increaseAlpha;

	public LSelectorIcon(int mainX, int mainY, int cols, int rows,int size) {
		this.mainX = mainX;
		this.mainY = mainY;
		this.minX = 0;
		this.minY = 0;
		this.maxX = cols;
		this.maxY = rows;
		this.pSize = size;
		this.alphaRate = 10f;
	}

	public void draw(LGraphics g) {
		LColor color = g.getGLColor();
		g.setColor(255, 255, 255);
		g.drawRect(mainX + (x * pSize) - 1, mainY + (y * pSize) - 1, pSize,
				pSize);
		g.drawRect(mainX + (x * pSize), mainY + (y * pSize), pSize - 2,
				pSize - 2);
		g.setColor(70, 0, 0, (int) alpha);
		g.drawRect(mainX + (x * pSize) - 1, mainY + (y * pSize) - 1, pSize,
				pSize);
		g.drawRect(mainX + (x * pSize), mainY + (y * pSize), pSize - 2,
				pSize - 2);
		g.setColor(color);
	}

	public void draw(GLEx g) {

		int color = g.getColorARGB();
		g.setColor(255, 255, 255,124);
		g.fillRect(mainX + (x * pSize) - 1, mainY + (y * pSize) - 1, pSize,
				pSize);
		g.drawRect(mainX + (x * pSize), mainY + (y * pSize), pSize - 2,
				pSize - 2);
		g.setColor(70, 0, 0, (int) alpha);
		g.drawRect(mainX + (x * pSize) - 1, mainY + (y * pSize) - 1, pSize,
				pSize);
		g.drawRect(mainX + (x * pSize), mainY + (y * pSize), pSize - 2,
				pSize - 2);
		g.setColor(color);
	}

	public void draw(SpriteBatch batch) {
		float color = batch.getFloatColor();
		batch.setColor(255, 255, 255);
		batch.drawRect(mainX + (x * pSize) - 1, mainY + (y * pSize) - 1, pSize,
				pSize);
		batch.drawRect(mainX + (x * pSize), mainY + (y * pSize), pSize - 2,
				pSize - 2);
		batch.setColor(70, 0, 0, (int) alpha);
		batch.drawRect(mainX + (x * pSize) - 1, mainY + (y * pSize) - 1, pSize,
				pSize);
		batch.drawRect(mainX + (x * pSize), mainY + (y * pSize), pSize - 2,
				pSize - 2);
		batch.setColor(color);
	}

	public void update() {
		if (increaseAlpha) {
			if (alpha + alphaRate < 255.0)
				alpha += alphaRate;
			else {
				alpha = 255;
				increaseAlpha = false;
			}
		} else {
			if (alpha - alphaRate > 70)
				alpha -= alphaRate;
			else {
				alpha = 70;
				increaseAlpha = true;
			}
		}
	}

	public void move(int direction) {
		switch (direction) {
		case (0):
			if (y > minY)
				y--;
			else {
				if (x > minX) {
					y = maxY - 1;
					x--;
				} else {
					x = maxX - 1;
					y = maxY - 1;
				}
			}
			break;
		case (1):
			if (x < maxX - 1)
				x++;
			else {
				if (y < maxY - 1) {
					y++;
					x = minX;
				} else {
					y = minY;
					x = minX;
				}
			}
			break;
		case (2):
			if (y < maxY - 1)
				y++;
			else {
				if (x < maxX - 1) {
					y = minY;
					x++;
				} else {
					x = minX;
					y = minY;
				}
			}
			break;
		case (3):
			if (x > minX)
				x--;
			else {
				if (y > minY) {
					y--;
					x = maxX - 1;
				} else {
					y = maxY - 1;
					x = maxX - 1;
				}
			}
			break;
		default:
			move(0);
			break;
		}
		updateIndex();
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
		updateIndex();
	}

	public void setWidth(int width) {
		this.pSize = width;
	}

	public void setHeight(int height) {
		this.pSize = height;
	}

	public void setColorIndex(int i) {
		colorIndex = i;
	}

	public void setMinMax(int minX, int minY, int maxX, int maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		if (x < minX){
			x = minX;
		}
		if (x > maxX - 1){
			x = maxX - 1;
		}
		if (y < minY){
			y = minY;
		}
		if (y > maxY - 1){
			y = maxY - 1;
		}
		updateIndex();
	}

	public void updateIndex() {

	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return pSize;
	}

	public int getHeight() {
		return pSize;
	}

	public int getColorIndex() {
		return colorIndex;
	}

	public double getAlpha() {
		return alpha;
	}
}
