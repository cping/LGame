package org.loon.framework.android.game.action.sprite;

import org.loon.framework.android.game.utils.GraphicsUtils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

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
 * @version 0.1
 */
public class SpriteRotate {
	
	private int oldAngle, newAngle;

	private Bitmap bufferImage, showImage;

	public boolean updateImage;

	private int width, height;

	public SpriteRotate(SpriteImage img, int w, int h, float angle) {
		set(img.getBitmap(), w, h, angle);
	}

	public SpriteRotate(String fileName, float angle) {
		Bitmap image = GraphicsUtils.loadImage(fileName, true).getBitmap();
		set(image, image.getWidth(), image.getHeight(), angle);
	}

	public SpriteRotate(Bitmap image, int w, int h, float angle) {
		set(image, image.getWidth(), image.getHeight(), angle);
	}

	public void set(Bitmap image, int w, int h, float angle) {
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

	/**
	 * 由于Android版本下像素操作效率实在太低，无奈下修改原本的像素操作方式，改为旋转图像为指定角度。
	 * 
	 * (PS:关键问题是，Android环境不支持直接修改显存中像素点，否则此部分将像素旋转适当角度的cos 
	 * 与 sin效率将在直接旋转图片之上。)
	 * 
	 * @return
	 */
	public Bitmap getBitmap(final int type) {
		if (updateImage) {
			if (oldAngle != newAngle) {
				oldAngle = newAngle;
				if (bufferImage != null) {
					synchronized (bufferImage) {
						Bitmap tmp = showImage;
						set(bufferImage, bufferImage.getWidth(), bufferImage
								.getHeight(), newAngle);
						if (type == 0) {
							showImage = GraphicsUtils.rotate(bufferImage,
									newAngle);
						} else {
							showImage = GraphicsUtils.rotate(bufferImage,
									newAngle);
						}
						if (tmp != null) {
							tmp.recycle();
							tmp = null;
						}
						return showImage;
					}
				}
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

	public Bitmap finalBitmap() {
		return bufferImage;
	}

	public float getAngle() {
		return this.newAngle;
	}
	
	public void setAngle(float angle) {
		if (angle > 360) {
			angle = 360;
		} else if (angle < -360) {
			angle = -360;
		}
		this.newAngle = (int) angle;
	}

	public int[] makeSpritePixels() {
		
		int angle = newAngle;
		int width = bufferImage.getWidth();
		int height = bufferImage.getHeight();
		
		int dW = width;
		int dH = height;

		Matrix matrix = new Matrix();

		Bitmap newBitmap = null;

		switch (angle) {
		case 90:
			matrix.preRotate(90);
			newBitmap = Bitmap.createBitmap(bufferImage, 0, 0, width, height,
					matrix, false);
			dW = height;
			dH = width;
			break;
		case 180:
			matrix.preRotate(180);
			newBitmap = Bitmap.createBitmap(bufferImage, 0, 0, width, height,
					matrix, false);
			break;
		case 270:
			matrix.preRotate(270);
			newBitmap = Bitmap.createBitmap(bufferImage, 0, 0, width, height,
					matrix, false);
			dW = height;
			dH = width;
			break;
		case -360: {
			matrix.preScale(-1, 1);
			newBitmap = Bitmap.createBitmap(bufferImage, 0, 0, width, height,
					matrix, false);
			break;
		}
		case -90: {
			matrix.preScale(-1, 1);
			matrix.preRotate(-90);
			newBitmap = Bitmap.createBitmap(bufferImage, 0, 0, width, height,
					matrix, false);
			dW = height;
			dH = width;
			break;
		}
		case -180: {
			matrix.preScale(-1, 1);
			matrix.preRotate(-180);
			newBitmap = Bitmap.createBitmap(bufferImage, 0, 0, width, height,
					matrix, false);
			break;
		}
		case -270: {
			matrix.preScale(-1, 1);
			matrix.preRotate(-270);
			newBitmap = Bitmap.createBitmap(bufferImage, 0, 0, width, height,
					matrix, false);
			dW = height;
			dH = width;
			break;
		}
		}
		try {
			if (newBitmap != null) {
				this.width = dW;
				this.height = dH;
				int[] pixels = GraphicsUtils.getPixels(newBitmap);
				return pixels;
			}
		} catch (Exception e) {

		} finally {
			if (newBitmap != null) {
				newBitmap.recycle();
				newBitmap = null;
			}
		}
		return null;

	}

	public int[] makePixels() {
		Bitmap bitmap = getBitmap(0);
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
