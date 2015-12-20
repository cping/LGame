package org.loon.framework.javase.game.action.avg;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.util.HashMap;

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
 * @email ceponline@yahoo.com.cn
 * @version 0.1.1
 */
final public class AVGDialog {

	private static HashMap<String, Image> lazyImages;

	final static private int objWidth = 64;

	final static private int objHeight = 64;

	final static private int x1 = 128;

	final static private int x2 = 192;

	final static private int y1 = 0;

	final static private int y2 = 64;

	public final static Image getRMXPDialog(String fileName, int width,
			int height) {
		if (lazyImages == null) {
			lazyImages = new HashMap<String, Image>(10);
		}
		Image dialog = GraphicsUtils.loadImage(fileName);
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

	public final static Image getRMXPloadBuoyage(String fileName, int width,
			int height) {
		return getRMXPloadBuoyage(GraphicsUtils.loadImage(fileName), width,
				height);
	}

	public final static Image getRMXPloadBuoyage(Image rmxpImage, int width,
			int height) {
		if (lazyImages == null) {
			lazyImages = new HashMap<String, Image>(10);
		}
		String keyName = ("buoyage" + width + "|" + height).intern();
		Image lazyImage = (Image) lazyImages.get(keyName);
		if (lazyImage == null) {
			Image image, left, right, center, up, down = null;
			int objWidth = 32;
			int objHeight = 32;
			int x1 = 128;
			int x2 = 160;
			int y1 = 64;
			int y2 = 96;
			int k = 1;
			try {
				image = GraphicsUtils.drawClipImage(rmxpImage, objWidth,
						objHeight, x1, y1, x2, y2);
				lazyImage = GraphicsUtils.createImage(width, height, false);
				Graphics g = lazyImage.getGraphics();
				left = GraphicsUtils.drawClipImage(image, k, height, 0, 0, k,
						objHeight);
				right = GraphicsUtils.drawClipImage(image, k, height, objWidth
						- k, 0, objWidth, objHeight);
				center = GraphicsUtils.drawClipImage(image, width, height, k,
						k, objWidth - k, objHeight - k);
				up = GraphicsUtils.drawClipImage(image, width, k, 0, 0,
						objWidth, k);
				down = GraphicsUtils.drawClipImage(image, width, k, 0,
						objHeight - k, objWidth, objHeight);
				g.drawImage(center, 0, 0, null);
				g.drawImage(left, 0, 0, null);
				g.drawImage(right, width - k, 0, null);
				g.drawImage(up, 0, 0, null);
				g.drawImage(down, 0, height - k, null);
				g.dispose();
				lazyImages.put(keyName, lazyImage);
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
		return lazyImage;

	}

	private final static Image getRMXPDialog(Image rmxpImage, int width,
			int height, int size, int offset) {
		if (lazyImages == null) {
			lazyImages = new HashMap<String, Image>(10);
		}
		String keyName = "dialog" + width + "|" + height;
		Image lazyImage = (Image) lazyImages.get(keyName);
		if (lazyImage == null) {

			int center_size = objHeight - size * 2;

			Image image = null;

			Image messageImage = null;

			image = GraphicsUtils.drawClipImage(rmxpImage, objWidth, objHeight,
					x1, y1, x2, y2);

			Image centerTop = GraphicsUtils.drawClipImage(image, center_size,
					size, size, 0);

			Image centerDown = GraphicsUtils.drawClipImage(image, center_size,
					size, size, objHeight - size);

			Image leftTop = GraphicsUtils
					.drawClipImage(image, size, size, 0, 0);

			Image leftCenter = GraphicsUtils.drawClipImage(image, size,
					center_size, 0, size);

			Image leftDown = GraphicsUtils.drawClipImage(image, size, size, 0,
					objHeight - size);

			Image rightTop = GraphicsUtils.drawClipImage(image, size, size,
					objWidth - size, 0);

			Image rightCenter = GraphicsUtils.drawClipImage(image, size,
					center_size, objWidth - size, size);

			Image rightDown = GraphicsUtils.drawClipImage(image, size, size,
					objWidth - size, objHeight - size);

			lazyImage = GraphicsUtils.createImage(width, height, true);

			messageImage = GraphicsUtils.drawClipImage(rmxpImage, 128, 128, 0,
					0, 128, 128);

			Graphics g = lazyImage.getGraphics();

			GraphicsUtils.setAlpha(g, 0.5D);

			messageImage = GraphicsUtils.getResize(messageImage,
					width - offset, height - offset);

			g
					.drawImage(messageImage,
							(lazyImage.getWidth(null) - messageImage
									.getWidth(null)) / 2, (lazyImage
									.getHeight(null) - messageImage
									.getHeight(null)) / 2, null);

			GraphicsUtils.setAlpha(g, 1.0D);

			Image tmp = GraphicsUtils.getResize(centerTop, width - (size * 2),
					size);

			g.drawImage(tmp, size, 0, null);
			tmp = null;
			tmp = GraphicsUtils.getResize(centerDown, width - (size * 2), size);

			g.drawImage(tmp, size, height - size, null);
			tmp = null;

			g.drawImage(leftTop, 0, 0, null);

			tmp = GraphicsUtils.getResize(leftCenter,
					leftCenter.getWidth(null), width - (size * 2));

			g.drawImage(tmp, 0, size, null);
			tmp = null;
			g.drawImage(leftDown, 0, height - size, null);

			int right = width - size;

			g.drawImage(rightTop, right, 0, null);

			tmp = GraphicsUtils.getResize(rightCenter, leftCenter
					.getWidth(null), width - (size * 2));

			g.drawImage(tmp, right, size, null);
			tmp = null;
			g.drawImage(rightDown, right, height - size, null);

			g.dispose();

			lazyImages.put(keyName, lazyImage);

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
		return lazyImage;
	}

	public static void clear() {
		for (Image img : lazyImages.values()) {
			img.flush();
			img = null;
		}
		lazyImages.clear();
	}
}
