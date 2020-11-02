package org.loon.framework.javase.game.action.sprite.j2me;

import org.loon.framework.javase.game.core.graphics.LImage;
import org.loon.framework.javase.game.core.graphics.device.LGraphics;
import org.loon.framework.javase.game.core.graphics.device.LTrans;
/**
 * Copyright 2008 - 2009
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class Sprite extends Layer implements LTrans {

	private int frame;

	private int[] sequence;

	private int refX;

	private int refY;

	private int cols;

	private int rows;

	private int transform;

	private LImage img;

	private int collX;

	private int collY;

	private int collWidth;

	private int collHeight;

	private int[] rgbData;

	private int[] rgbDataAux;

	public Sprite(String fileName) {
		this(LImage.createImage(fileName));
	}

	public Sprite(LImage img) {
		this(img, img.getWidth(), img.getHeight());
	}

	public Sprite(LImage img, int frameWidth, int frameHeight) {

		super(0, 0, frameWidth, frameHeight, true);

		if (img.getWidth() % frameWidth != 0
				|| img.getHeight() % frameHeight != 0) {
			throw new IllegalArgumentException();
		}
		this.img = img;
		cols = img.getWidth() / frameWidth;
		rows = img.getHeight() / frameHeight;
		collX = collY = 0;
		collWidth = frameWidth;
		collHeight = frameHeight;
	}

	public Sprite(Sprite otherSprite) {
		super(otherSprite.getX(), otherSprite.getY(), otherSprite.getWidth(),
				otherSprite.getHeight(), otherSprite.isVisible());
		this.frame = otherSprite.frame;
		this.sequence = otherSprite.sequence;
		this.refX = otherSprite.refX;
		this.refY = otherSprite.refY;
		this.cols = otherSprite.cols;
		this.rows = otherSprite.rows;
		this.transform = otherSprite.transform;
		this.img = otherSprite.img;
		this.collX = otherSprite.collX;
		this.collY = otherSprite.collY;
		this.collWidth = otherSprite.collWidth;
		this.collHeight = otherSprite.collHeight;
	}

	public final boolean collidesWith(LImage image, int iX, int iY,
			boolean pixelLevel) {
		if (image == null) {
			throw new IllegalArgumentException();
		}
		if (!this.isVisible())
			return false;

		if (pixelLevel)
			return collidesWithPixelLevel(image, iX, iY);
		else
			return collidesWith(image, iX, iY);
	}

	public final boolean collidesWith(TiledLayer layer, boolean pixelLevel) {

		if (layer == null) {
			throw new NullPointerException();
		}

		if (!this.isVisible())
			return false;

		if (!layer.isVisible() || !this.isVisible())
			return false;

		if (pixelLevel)
			return collidesWithPixelLevel(layer, 0, 0);
		else
			return collidesWith(layer, 0, 0);
	}

	public final boolean collidesWith(Sprite otherSprite, boolean pixelLevel) {

		if (otherSprite == null) {
			throw new NullPointerException();
		}

		if (!otherSprite.isVisible() || !this.isVisible())
			return false;

		if (pixelLevel)
			return collidesWithPixelLevel(otherSprite, 0, 0);
		else
			return collidesWith(otherSprite, 0, 0);
	}

	public void defineReferencePixel(int x, int y) {
		refX = x;
		refY = y;
	}

	public int getRefPixelX() {
		return getX() + refX;
	}

	public int getRefPixelY() {
		return getY() + refY;
	}

	public void setRefPixelPosition(int x, int y) {
		int curRefX, curRefY;
		int width = getWidth();
		int height = getHeight();

		switch (transform) {
		case TRANS_NONE:
			curRefX = refX;
			curRefY = refY;
			break;
		case TRANS_MIRROR_ROT180:
			curRefX = width - refX;
			curRefY = height - refY;
			break;
		case TRANS_MIRROR:
			curRefX = width - refX;
			curRefY = refY;
			break;
		case TRANS_ROT180:
			curRefX = refX;
			curRefY = height - refY;
			break;
		case TRANS_MIRROR_ROT270:
			curRefX = height - refY;
			curRefY = refX;
			break;
		case TRANS_ROT90:
			curRefX = height - refY;
			curRefY = width - refX;
			break;
		case TRANS_ROT270:
			curRefX = refY;
			curRefY = refX;
			break;
		case TRANS_MIRROR_ROT90:
			curRefX = refY;
			curRefY = width - refX;
			break;
		default:
			return;
		}

		setPosition(x - curRefX, y - curRefY);
	}

	public void defineCollisionRectangle(int x, int y, int width, int height) {
		if (width < 0 || height < 0)
			throw new IllegalArgumentException();
		collX = x;
		collY = y;
		collWidth = width;
		collHeight = height;
	}

	public void setFrameSequence(int[] sequence) {
		if (sequence == null) {
			this.sequence = null;
			return;
		}

		int max = (rows * cols) - 1;

		int l = sequence.length;

		if (l == 0)
			throw new IllegalArgumentException();

		for (int i = 0; i < l; i++) {
			int value = sequence[i];
			if (value > max || value < 0)
				throw new ArrayIndexOutOfBoundsException();
		}

		this.sequence = sequence;

		this.frame = 0;
	}

	public final int getFrame() {
		return frame;
	}

	public int getFrameSequenceLength() {
		return (sequence == null) ? rows * cols : sequence.length;
	}

	public void setFrame(int frame) {
		int l = (sequence == null) ? rows * cols : sequence.length;
		if (frame < 0 || frame >= l) {
			throw new IndexOutOfBoundsException();
		}
		this.frame = frame;
	}

	public void nextFrame() {
		if (frame == ((sequence == null) ? rows * cols : sequence.length) - 1)
			frame = 0;
		else
			frame++;
	}

	public void prevFrame() {
		if (frame == 0)
			frame = ((sequence == null) ? rows * cols : sequence.length) - 1;
		else
			frame--;
	}

	public void setImage(LImage img, int frameWidth, int frameHeight) {
		synchronized (this) {
			int oldW = getWidth();
			int oldH = getHeight();
			int newW = img.getWidth();
			int newH = img.getHeight();

			setSize(frameWidth, frameHeight);

			if (img.getWidth() % frameWidth != 0
					|| img.getHeight() % frameHeight != 0)
				throw new IllegalArgumentException();
			this.img = img;

			int oldFrames = cols * rows;
			cols = img.getWidth() / frameWidth;
			rows = img.getHeight() / frameHeight;

			if (rows * cols < oldFrames) {

				sequence = null;
				frame = 0;
			}

			if (frameWidth != getWidth() || frameHeight != getHeight()) {

				defineCollisionRectangle(0, 0, frameWidth, frameHeight);
				rgbData = rgbDataAux = null;

				if (transform != TRANS_NONE) {
					int dx, dy;
					switch (transform) {
					case TRANS_MIRROR_ROT180:
						dx = newW - oldW;
						dy = newH - oldH;
						break;
					case TRANS_MIRROR:
						dx = newW - oldW;
						dy = 0;
						break;
					case TRANS_ROT180:
						dx = 0;
						dy = newH - oldH;
						break;
					case TRANS_MIRROR_ROT270:
						dx = newH - oldH;
						dy = 0;
						break;
					case TRANS_ROT90:
						dx = newH - oldH;
						dy = newW - oldW;
						break;
					case TRANS_ROT270:
						dx = 0;
						dy = 0;
						break;
					case TRANS_MIRROR_ROT90:
						dx = 0;
						dy = newW - oldW;
						break;
					default:
						return;
					}

					move(dx, dy);
				}
			}
		}
	}

	public final void paint(LGraphics g) {
		if (!isVisible()) {
			return;
		}
		int f = (sequence == null) ? frame : sequence[frame];
		int w = getWidth();
		int h = getHeight();
		int fx = w * (f % cols);
		int fy = h * (f / cols);

		g.drawRegion(img, fx, fy, w, h, transform, getX(), getY(),
				LGraphics.TOP | LGraphics.LEFT);
	}

	public int getRawFrameCount() {
		return cols * rows;
	}

	public void setTransform(int transform) {
		if (this.transform == transform)
			return;

		int width = getWidth();
		int height = getHeight();
		int currentTransform = this.transform;

		int newRefX, newRefY;

		switch (transform) {
		case TRANS_NONE:
			newRefX = refX;
			newRefY = refY;
			break;
		case TRANS_MIRROR_ROT180:
			newRefX = width - refX;
			newRefY = height - refY;
			break;
		case TRANS_MIRROR:
			newRefX = width - refX;
			newRefY = refY;
			break;
		case TRANS_ROT180:
			newRefX = refX;
			newRefY = height - refY;
			break;
		case TRANS_MIRROR_ROT270:
			newRefX = height - refY;
			newRefY = refX;
			break;
		case TRANS_ROT90:
			newRefX = height - refY;
			newRefY = width - refX;
			break;
		case TRANS_ROT270:
			newRefX = refY;
			newRefY = refX;
			break;
		case TRANS_MIRROR_ROT90:
			newRefX = refY;
			newRefY = width - refX;
			break;
		default:
			throw new IllegalArgumentException();
		}

		int curRefX, curRefY;

		switch (currentTransform) {
		case TRANS_NONE:
			curRefX = refX;
			curRefY = refY;
			break;
		case TRANS_MIRROR_ROT180:
			curRefX = width - refX;
			curRefY = height - refY;
			break;
		case TRANS_MIRROR:
			curRefX = width - refX;
			curRefY = refY;
			break;
		case TRANS_ROT180:
			curRefX = refX;
			curRefY = height - refY;
			break;
		case TRANS_MIRROR_ROT270:
			curRefX = height - refY;
			curRefY = refX;
			break;
		case TRANS_ROT90:
			curRefX = height - refY;
			curRefY = width - refX;
			break;
		case TRANS_ROT270:
			curRefX = refY;
			curRefY = refX;
			break;
		case TRANS_MIRROR_ROT90:
			curRefX = refY;
			curRefY = width - refX;
			break;
		default:
			return;
		}

		move(curRefX - newRefX, curRefY - newRefY);
		this.transform = transform;
	}

	private synchronized boolean collidesWith(Object o, int oX, int oY) {

		int tX = 0, tY = 0, tW = 0, tH = 0;
		int oW = 0, oH = 0;

		Sprite t = this;
		boolean another = true;

		while (another) {
			int sX, sY, sW, sH;

			int cX = t.collX;
			int cY = t.collY;
			int cW = t.collWidth;
			int cH = t.collHeight;

			if (cW == 0 || cH == 0) {
				return false;
			}

			switch (t.transform) {
			case TRANS_NONE:
				sX = t.getX() + cX;
				sY = t.getY() + cY;
				sW = cW;
				sH = cH;
				break;
			case TRANS_MIRROR_ROT180:
				sX = t.getX() + cX;
				sY = t.getY() + (t.getHeight() - cY - 1) - cH;
				sW = cW;
				sH = cH;
				break;
			case TRANS_MIRROR:
				sX = t.getX() + (t.getWidth() - cX - 1) - cW;
				sY = t.getY() + cY;
				sW = cW;
				sH = cH;
				break;
			case TRANS_ROT180:
				sX = t.getX() + (t.getWidth() - cX - 1) - cW;
				sY = t.getY() + (t.getHeight() - cY - 1) - cH;
				sW = cW;
				sH = cH;
				break;
			case TRANS_MIRROR_ROT270:
				sX = t.getX() + cY;
				sY = t.getY() + cX;
				sW = cH;
				sH = cW;
				break;
			case TRANS_ROT90:
				sX = t.getX() + (t.getHeight() - cY - 1) - cH;
				sY = t.getY() + cX;
				sW = cH;
				sH = cW;
				break;
			case TRANS_MIRROR_ROT90:
				sX = t.getX() + (t.getHeight() - cY - 1) - cH;
				sY = t.getY() + (t.getWidth() - cX - 1) - cW;
				sW = cH;
				sH = cW;
				break;
			case TRANS_ROT270:
				sX = t.getX() + cY;
				sY = t.getY() + (t.getWidth() - cX - 1) - cW;
				sW = cH;
				sH = cW;
				break;
			default:
				return false;
			}

			if (o != t) {
				tX = sX;
				tY = sY;
				tW = sW;
				tH = sH;
				if (o instanceof Sprite) {

					t = (Sprite) o;
				} else if (o instanceof TiledLayer) {
					another = false;
					TiledLayer layer = (TiledLayer) o;
					oX = layer.getX();
					oY = layer.getY();
					oW = layer.getWidth();
					oH = layer.getHeight();
				} else {
					another = false;
					LImage img = (LImage) o;
					oW = img.getWidth();
					oH = img.getHeight();
				}
			} else {
				another = false;

				oX = sX;
				oY = sY;
				oW = sW;
				oH = sH;
			}
		}

		if (tX > oX && tX >= oX + oW)
			return false;
		else if (tX < oX && tX + tW <= oX)
			return false;
		else if (tY > oY && tY >= oY + oH)
			return false;
		else if (tY < oY && tY + tH <= oY)
			return false;

		if (o instanceof TiledLayer) {

			TiledLayer layer = (TiledLayer) o;

			int rX, rY, rW, rH;

			if (oX > tX) {
				rX = oX;
				rW = ((oX + oW < tX + tW) ? oX + oW : tX + tW) - rX;
			} else {
				rX = tX;
				rW = ((tX + tW < oX + oW) ? tX + tW : oX + oW) - rX;
			}
			if (oY > tY) {
				rY = oY;
				rH = ((oY + oH < tY + tH) ? oY + oH : tY + tH) - rY;
			} else {
				rY = tY;
				rH = ((tY + tH < oY + oH) ? tY + tH : oY + oH) - rY;
			}

			int lW = layer.getCellWidth();
			int lH = layer.getCellHeight();

			int minC = (rX - oX) / lW;
			int minR = (rY - oY) / lH;
			int maxC = (rX - oX + rW - 1) / lW;
			int maxR = (rY - oY + rH - 1) / lH;

			for (int row = minR; row <= maxR; row++) {
				for (int col = minC; col <= maxC; col++) {
					int cell = layer.getCell(col, row);

					if (cell < 0)
						cell = layer.getAnimatedTile(cell);

					if (cell != 0)
						return true;
				}
			}

			return false;
		} else {

			return true;
		}
	}

	private synchronized boolean collidesWithPixelLevel(Object o, int oX, int oY) {

		boolean another = true;
		Sprite t = this;

		int tX = 0, tY = 0, tW = 0, tH = 0;
		int oW = 0, oH = 0;

		while (another) {

			int cX, cY, cW, cH;

			int sX, sY, sW, sH;

			if (t.collX >= t.getWidth() || t.collX + t.collWidth <= 0
					|| t.collY >= t.getHeight() || t.collY + t.collHeight <= 0)

				return false;

			cX = (t.collX >= 0) ? t.collX : 0;
			cY = (t.collY >= 0) ? t.collY : 0;
			cW = (t.collX + t.collWidth < t.getWidth()) ? t.collX + t.collWidth
					- cX : t.getWidth() - cX;
			cH = (t.collY + t.collHeight < t.getHeight()) ? t.collY
					+ t.collHeight - cY : t.getHeight() - cY;

			switch (t.transform) {
			case TRANS_NONE:
				sX = t.getX() + cX;
				sY = t.getY() + cY;
				sW = cW;
				sH = cH;
				break;
			case TRANS_MIRROR_ROT180:
				sX = t.getX() + cX;
				sY = t.getY() + (t.getHeight() - cY - 1) - cH;
				sW = cW;
				sH = cH;
				break;
			case TRANS_MIRROR:
				sX = t.getX() + (t.getWidth() - cX - 1) - cW;
				sY = t.getY() + cY;
				sW = cW;
				sH = cH;
				break;
			case TRANS_ROT180:
				sX = t.getX() + (t.getWidth() - cX - 1) - cW;
				sY = t.getY() + (t.getHeight() - cY - 1) - cH;
				sW = cW;
				sH = cH;
				break;
			case TRANS_MIRROR_ROT270:
				sX = t.getX() + cY;
				sY = t.getY() + cX;
				sW = cH;
				sH = cW;
				break;
			case TRANS_ROT90:
				sX = t.getX() + (t.getHeight() - cY) - cH;
				sY = t.getY() + cX;
				sW = cH;
				sH = cW;
				break;
			case TRANS_MIRROR_ROT90:
				sX = t.getX() + (t.getHeight() - cY) - cH;
				sY = t.getY() + (t.getWidth() - cX) - cW;
				sW = cH;
				sH = cW;
				break;
			case TRANS_ROT270:
				sX = t.getX() + cY;
				sY = t.getY() + (t.getWidth() - cX) - cW;
				sW = cH;
				sH = cW;
				break;
			default:
				return false;
			}

			if (o != t) {
				tX = sX;
				tY = sY;
				tW = sW;
				tH = sH;
				if (o instanceof Sprite) {

					t = (Sprite) o;
				} else if (o instanceof TiledLayer) {
					another = false;
					TiledLayer layer = (TiledLayer) o;
					oX = layer.getX();
					oY = layer.getY();
					oW = layer.getWidth();
					oH = layer.getHeight();
				} else {
					another = false;
					LImage img = (LImage) o;
					oW = img.getWidth();
					oH = img.getHeight();
				}
			} else {
				another = false;

				oX = sX;
				oY = sY;
				oW = sW;
				oH = sH;
			}
		}

		if (tX > oX && tX >= oX + oW)
			return false;
		else if (tX < oX && tX + tW <= oX)
			return false;
		else if (tY > oY && tY >= oY + oH)
			return false;
		else if (tY < oY && tY + tH <= oY)
			return false;

		int rX, rY, rW, rH;

		if (oX > tX) {
			rX = oX;
			rW = ((oX + oW < tX + tW) ? oX + oW : tX + tW) - rX;
		} else {
			rX = tX;
			rW = ((tX + tW < oX + oW) ? tX + tW : oX + oW) - rX;
		}
		if (oY > tY) {
			rY = oY;
			rH = ((oY + oH < tY + tH) ? oY + oH : tY + tH) - rY;
		} else {
			rY = tY;
			rH = ((tY + tH < oY + oH) ? tY + tH : oY + oH) - rY;
		}

		int tColIncr = 0, tRowIncr = 0, tOffset = 0;
		int oColIncr = 0, oRowIncr = 0, oOffset = 0;

		int f = (sequence == null) ? frame : sequence[frame];

		int fW = getWidth();
		int fH = getHeight();
		int fX = fW * (f % rows);
		int fY = fH * (f / rows);

		if (rgbData == null) {
			rgbData = new int[fW * fH];
			rgbDataAux = new int[fW * fH];
		}

		t = this;
		another = true;
		int[] tRgbData = this.rgbData;

		while (another) {
			int sOffset;
			int sColIncr;
			int sRowIncr;

			switch (t.transform) {
			case TRANS_NONE:
				t.img.getRGB(tRgbData, 0, rW, fX + rX - t.getX(), fY + rY
						- t.getY(), rW, rH);
				sOffset = 0;
				sColIncr = 1;
				sRowIncr = 0;
				break;
			case TRANS_ROT180:
				t.img.getRGB(tRgbData, 0, rW, fX + fW - (rX - t.getX()) - rW
						- 1, fY + fH - (rY - t.getY()) - rH - 1, rW, rH);
				sOffset = (rH * rW) - 1;
				sColIncr = -1;
				sRowIncr = 0;
				break;
			case TRANS_MIRROR:
				t.img.getRGB(tRgbData, 0, rW, fX + fW - (rX - t.getX()) - rW
						- 1, fY + rY - t.getY(), rW, rH);
				sOffset = rW - 1;
				sColIncr = -1;
				sRowIncr = rW << 1;
				break;
			case TRANS_MIRROR_ROT180:
				t.img.getRGB(tRgbData, 0, rW, fX + rX - t.getX(), fY + fH
						- (rY - t.getY()) - rH - 1, rW, rH);
				sOffset = (rH - 1) * rW;
				sColIncr = 1;
				sRowIncr = -(rW << 1);
				break;
			case TRANS_ROT90:
				t.img.getRGB(tRgbData, 0, rH, fX + rY - t.getY(), fY + fH
						- (rX - t.getX()) - rW, rH, rW);
				sOffset = (rW - 1) * rH;
				sColIncr = -rH;
				sRowIncr = (rH * rW) + 1;
				break;
			case TRANS_MIRROR_ROT90:
				t.img.getRGB(tRgbData, 0, rH, fX + fW - (rY - t.getY()) - rH,
						fY + fH - (rX - t.getX()) - rW, rH, rW);
				sOffset = (rH * rW) - 1;
				sColIncr = -rH;
				sRowIncr = (rH * rW) - 1;
				break;
			case TRANS_MIRROR_ROT270:
				t.img.getRGB(tRgbData, 0, rH, fX + rY - t.getY(), fY + rX
						- t.getX(), rH, rW);
				sOffset = 0;
				sColIncr = rH;
				sRowIncr = -(rH * rW) + 1;
				break;
			case TRANS_ROT270:
				t.img.getRGB(tRgbData, 0, rH, fX + fW - (rY - t.getY()) - rH,
						fY + rX - t.getX(), rH, rW);
				sOffset = rH - 1;
				sColIncr = rH;
				sRowIncr = -(rH * rW) - 1;
				break;
			default:
				return false;
			}

			if (o != t) {
				tOffset = sOffset;
				tRowIncr = sRowIncr;
				tColIncr = sColIncr;

				if (o instanceof Sprite) {

					t = (Sprite) o;
					tRgbData = this.rgbDataAux;

					f = (t.sequence == null) ? t.frame : t.sequence[t.frame];

					fW = t.getWidth();
					fH = t.getHeight();
					fX = fW * (f % t.rows);
					fY = fH * (f / t.rows);
				} else if (o instanceof TiledLayer) {
					another = false;
					TiledLayer layer = (TiledLayer) o;
					LImage img = layer.img;

					oOffset = 0;
					oColIncr = 1;
					oRowIncr = 0;

					int lW = layer.getCellWidth();
					int lH = layer.getCellHeight();

					int minC = (rX - oX) / lW;
					int minR = (rY - oY) / lH;
					int maxC = (rX - oX + rW - 1) / lW;
					int maxR = (rY - oY + rH - 1) / lH;

					for (int row = minR; row <= maxR; row++) {
						for (int col = minC; col <= maxC; col++) {
							int cell = layer.getCell(col, row);

							if (cell < 0)
								cell = layer.getAnimatedTile(cell);

							int minX = (col == minC) ? (rX - oX) % lW : 0;
							int minY = (row == minR) ? (rY - oY) % lH : 0;
							int maxX = (col == maxC) ? (rX + rW - oX - 1) % lW
									: lW - 1;
							int maxY = (row == maxR) ? (rY + rH - oY - 1) % lH
									: lH - 1;

							int c = (row - minR) * lH * rW + (col - minC) * lW
									- ((col == minC) ? 0 : (rX - oX) % lW)
									- ((row == minR) ? 0 : (rY - oY) % lH) * rW;

							if (cell == 0) {

								for (int y = minY; y <= maxY; y++, c += rW
										- (maxX - minX + 1)) {
									for (int x = minX; x <= maxX; x++, c++) {
										rgbDataAux[c] = 0;
									}
								}
							} else {

								cell--;

								int imgCols = img.getWidth()
										/ layer.getCellWidth();
								int xSrc = lW * (cell % imgCols);
								int ySrc = (cell / imgCols) * lH;
								img.getRGB(rgbDataAux, c, rW, xSrc + minX, ySrc
										+ minY, maxX - minX + 1, maxY - minY
										+ 1);

							}
						}
					}
				} else {
					another = false;
					LImage img = (LImage) o;
					img.getRGB(rgbDataAux, 0, rW, rX - oX, rY - oY, rW, rH);
					oOffset = 0;
					oColIncr = 1;
					oRowIncr = 0;
				}
			} else {
				another = false;
				oOffset = sOffset;
				oRowIncr = sRowIncr;
				oColIncr = sColIncr;
			}
		}

		for (int row = 0; row < rH; row++, tOffset += tRowIncr, oOffset += oRowIncr) {
			for (int col = 0; col < rW; col++, tOffset += tColIncr, oOffset += oColIncr) {
				int rgb = rgbData[tOffset];
				int rgbA = rgbDataAux[oOffset];
				if (((rgb & rgbA) >> 24) == -1)
					return true;
			}
		}
		return false;
	}

}
