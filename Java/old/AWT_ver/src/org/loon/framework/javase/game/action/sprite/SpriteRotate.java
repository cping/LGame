package org.loon.framework.javase.game.action.sprite;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.loon.framework.javase.game.utils.GraphicsUtils;

/**
 * Copyright 2008 - 2010
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
 * @version 0.1.1
 */
public class SpriteRotate {

	private int oldAngle, newAngle;

	private BufferedImage bufferImage, showImage;

	public boolean updateImage;

	private int width, height;

	public SpriteRotate(SpriteImage img, int w, int h, float angle) {
		set(img.getImage(), w, h, angle);
	}

	public SpriteRotate(String fileName, float angle) {
		BufferedImage image = GraphicsUtils.loadBufferedImage(fileName);
		set(image, image.getWidth(null), image.getHeight(null), angle);
	}

	public SpriteRotate(BufferedImage image, int w, int h, float angle) {
		set(image, image.getWidth(), image.getHeight(), angle);
	}

	public void set(BufferedImage image, int w, int h, float angle) {
		if (angle > 360 || angle < -360) {
			angle = 0;
		}
		this.width = w;
		this.height = h;
		this.updateImage = true;
		this.bufferImage = image;
		this.showImage = null;
		this.newAngle = (int) angle;
	}

	public BufferedImage finalBitmap() {
		return bufferImage;
	}

	/**
	 * 由于Android版本下像素操作效率实在太低，为两版兼顾，JavaSE版本暂时设定为创建新图进行旋转。
	 * (PS:关键问题是，Android环境不支持直接修改显存中像素点，否则此部分将像素旋转适当角度的cos 与sin效率将在直接旋转图片之上。)
	 * 
	 * @return
	 */
	public BufferedImage getBitmap(final int type) {
		if (updateImage) {
			if (oldAngle != newAngle) {
				oldAngle = newAngle;
				BufferedImage tmp = showImage;
				set(bufferImage, bufferImage.getWidth(), bufferImage
						.getHeight(), newAngle);
				if (type == 0 || type == 3) {
					showImage = GraphicsUtils.rotateImageRect(bufferImage,
							newAngle);
				} else {
					showImage = GraphicsUtils
							.rotateImage(bufferImage, newAngle);
				}
				if (tmp != null) {
					tmp.flush();
					tmp = null;
				}
				return showImage;
			} else {
				if (showImage == null) {
					return bufferImage;
				} else {
					return showImage;
				}
			}
		}
		return bufferImage;
	}

	public void setAngle(float angle) {
		if (angle > 360) {
			angle = 360;
		} else if (angle < -360) {
			angle = -360;
		}
		this.newAngle = (int) angle;
	}

	public float getAngle() {
		return this.newAngle;
	}

	public int[] makeSpritePixels() {
		int angle = (int) newAngle;
		AffineTransform t = new AffineTransform();
		int width = bufferImage.getWidth();
		int height = bufferImage.getHeight();
		int dW = width;
		int dH = height;
		switch (angle) {
		case 90: {
			t.translate(height, 0);
			t.rotate(Math.PI / 2);
			dW = height;
			dH = width;
			break;
		}
		case 180: {
			t.translate(width, height);
			t.rotate(Math.PI);
			break;
		}
		case 270: {
			t.translate(0, width);
			t.rotate(Math.PI * 3 / 2);
			dW = height;
			dH = width;
			break;
		}
		case -360: {
			t.translate(width, 0);
			t.scale(-1, 1);
			break;
		}
		case -90: {
			t.translate(height, 0);
			t.rotate(Math.PI / 2);
			t.translate(width, 0);
			t.scale(-1, 1);
			dW = height;
			dH = width;
			break;
		}
		case -180: {
			t.translate(width, 0);
			t.scale(-1, 1);
			t.translate(width, height);
			t.rotate(Math.PI);
			break;
		}
		case -270: {
			t.rotate(Math.PI * 3 / 2);
			t.scale(-1, 1);
			dW = height;
			dH = width;
			break;
		}
		}
		BufferedImage img = new BufferedImage(dW, dH,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setTransform(t);
		g.drawImage(bufferImage, (dW - width) / 2, (dH - height) / 2, null);
		g.dispose();
		this.width = dW;
		this.height = dH;
		return GraphicsUtils.getPixels(img);
	}

	public int[] makePixels() {
		BufferedImage bitmap = getBitmap(0);
		this.width = bitmap.getWidth();
		this.height = bitmap.getHeight();
		return GraphicsUtils.getPixels(bitmap);
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

}
