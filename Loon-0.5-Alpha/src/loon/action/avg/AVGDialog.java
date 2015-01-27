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
 * @project loon
 * @author cping
 * @email javachenpeng@yahoo.com
 * @version 0.1.1
 */
package loon.action.avg;

import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.util.HashMap;

import loon.JavaSEGraphicsUtils;
import loon.core.graphics.device.LGraphics;
import loon.core.graphics.device.LImage;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTexture.Format;

final public class AVGDialog {

	private static HashMap<String, LTexture> lazyImages;

	final static private int objWidth = 64;

	final static private int objHeight = 64;

	final static private int x1 = 128;

	final static private int x2 = 192;

	final static private int y1 = 0;

	final static private int y2 = 64;

	public final static LTexture getRMXPDialog(String fileName, int width,
			int height) {
		if (lazyImages == null) {
			lazyImages = new HashMap<String, LTexture>(10);
		}
		Image dialog = JavaSEGraphicsUtils.loadImage(fileName);
		int w = dialog.getWidth(null);
		int h = dialog.getHeight(null);
		PixelGrabber pg = new PixelGrabber(dialog, 0, 0, w, h, true);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
		}
		int[] pixels = (int[]) pg.getPixels();
		int index = -1;
		int count = 0;
		int pixel;
		for (int i = 0; i < 5; i++) {
			pixel = pixels[(141 + i) + w * 12];
			if (index == -1) {
				index = pixel;
			}
			if (index == pixel) {
				count++;
			}
		}

		if (count == 5) {
			return getRMXPDialog(dialog, width, height, 16, 5);
		} else if (count == 1) {
			return getRMXPDialog(dialog, width, height, 27, 5);
		} else if (count == 2) {
			return getRMXPDialog(dialog, width, height, 20, 5);
		} else {
			return getRMXPDialog(dialog, width, height, 27, 5);
		}
	}

	public final static LTexture getRMXPloadBuoyage(String fileName, int width,
			int height) {
		return getRMXPloadBuoyage(JavaSEGraphicsUtils.loadImage(fileName), width,
				height);
	}

	public final static LTexture getRMXPloadBuoyage(Image rmxpImage, int width,
			int height) {
		if (lazyImages == null) {
			lazyImages = new HashMap<String, LTexture>(10);
		}
		String keyName = ("buoyage" + width + "|" + height).intern();
		LTexture lazyTexture = lazyImages.get(keyName);
		if (lazyTexture == null) {
			Image image, left, right, center, up, down = null;
			int objWidth = 32;
			int objHeight = 32;
			int x1 = 128;
			int x2 = 160;
			int y1 = 64;
			int y2 = 96;
			int k = 1;
			try {
				image = JavaSEGraphicsUtils.drawClipImage(rmxpImage, objWidth,
						objHeight, x1, y1, x2, y2);
				LImage lazyImage = LImage.createImage(width, height, false);
				LGraphics g = lazyImage.getLGraphics();
				left = JavaSEGraphicsUtils.drawClipImage(image, k, height, 0, 0, k,
						objHeight);
				right = JavaSEGraphicsUtils.drawClipImage(image, k, height, objWidth
						- k, 0, objWidth, objHeight);
				center = JavaSEGraphicsUtils.drawClipImage(image, width, height, k,
						k, objWidth - k, objHeight - k);
				up = JavaSEGraphicsUtils.drawClipImage(image, width, k, 0, 0,
						objWidth, k);
				down = JavaSEGraphicsUtils.drawClipImage(image, width, k, 0,
						objHeight - k, objWidth, objHeight);
				g.drawImage(center, 0, 0);
				g.drawImage(left, 0, 0);
				g.drawImage(right, width - k, 0);
				g.drawImage(up, 0, 0);
				g.drawImage(down, 0, height - k);
				g.dispose();

				lazyImage.setFormat(Format.STATIC);
				lazyTexture = lazyImage.getTexture();

				if (lazyImage != null) {
					lazyImage.dispose();
					lazyImage = null;
				}

				lazyImages.put(keyName, lazyTexture);
			} catch (Exception e) {
				return null;
			} finally {
				left = null;
				right = null;
				center = null;
				up = null;
				down = null;
				image = null;
			}
		}
		return lazyTexture;

	}

	private final static LTexture getRMXPDialog(Image rmxpImage, int width,
			int height, int size, int offset) {
		if (lazyImages == null) {
			lazyImages = new HashMap<String, LTexture>(10);
		}
		String keyName = "dialog" + width + "|" + height;
		LTexture lazyTexture = lazyImages.get(keyName);
		if (lazyTexture == null) {

			int center_size = objHeight - size * 2;

			Image image = null;

			Image messageImage = null;

			image = JavaSEGraphicsUtils.drawClipImage(rmxpImage, objWidth, objHeight,
					x1, y1, x2, y2);

			Image centerTop = JavaSEGraphicsUtils.drawClipImage(image, center_size,
					size, size, 0);

			Image centerDown = JavaSEGraphicsUtils.drawClipImage(image, center_size,
					size, size, objHeight - size);

			Image leftTop = JavaSEGraphicsUtils
					.drawClipImage(image, size, size, 0, 0);

			Image leftCenter = JavaSEGraphicsUtils.drawClipImage(image, size,
					center_size, 0, size);

			Image leftDown = JavaSEGraphicsUtils.drawClipImage(image, size, size, 0,
					objHeight - size);

			Image rightTop = JavaSEGraphicsUtils.drawClipImage(image, size, size,
					objWidth - size, 0);

			Image rightCenter = JavaSEGraphicsUtils.drawClipImage(image, size,
					center_size, objWidth - size, size);

			Image rightDown = JavaSEGraphicsUtils.drawClipImage(image, size, size,
					objWidth - size, objHeight - size);

			LImage lazyImage = LImage.createImage(width, height, true);

			messageImage = JavaSEGraphicsUtils.drawClipImage(rmxpImage, 128, 128, 0,
					0, 128, 128);

			LGraphics g = lazyImage.getLGraphics();

			g.setAlpha(0.5f);

			messageImage = JavaSEGraphicsUtils.getResize(messageImage, width - offset
					+ 1, height - offset + 1);

			g.drawImage(messageImage,
					(lazyImage.getWidth() - messageImage.getWidth(null)) / 2,
					(lazyImage.getHeight() - messageImage.getHeight(null)) / 2);

			g.setAlpha(1.0f);

			Image tmp = JavaSEGraphicsUtils.getResize(centerTop, width - (size * 2),
					size);

			g.drawImage(tmp, size, 0);
			tmp = null;
			tmp = JavaSEGraphicsUtils.getResize(centerDown, width - (size * 2), size);

			g.drawImage(tmp, size, height - size);
			tmp = null;

			g.drawImage(leftTop, 0, 0);

			tmp = JavaSEGraphicsUtils.getResize(leftCenter,
					leftCenter.getWidth(null), width - (size * 2));

			g.drawImage(tmp, 0, size);
			tmp = null;
			g.drawImage(leftDown, 0, height - size);

			int right = width - size;

			g.drawImage(rightTop, right, 0);

			tmp = JavaSEGraphicsUtils.getResize(rightCenter,
					leftCenter.getWidth(null), width - (size * 2));

			g.drawImage(tmp, right, size);
			tmp = null;
			g.drawImage(rightDown, right, height - size);

			g.dispose();

			lazyImage.setFormat(Format.STATIC);
			lazyTexture = lazyImage.getTexture();

			if (lazyImage != null) {
				lazyImage.dispose();
				lazyImage = null;
			}

			lazyImages.put(keyName, lazyTexture);

			image = null;
			messageImage = null;
			centerTop = null;
			centerDown = null;
			leftTop = null;
			leftCenter = null;
			leftDown = null;
			rightTop = null;
			rightCenter = null;
			rightDown = null;

		}
		return lazyTexture;
	}

	public static void clear() {
		for (LTexture texture : lazyImages.values()) {
			texture.destroy();
			texture = null;
		}
		lazyImages.clear();
	}
}
