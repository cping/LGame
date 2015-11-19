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

import loon.BaseIO;
import loon.LSystem;
import loon.LTexture;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.utils.ObjectMap;

final public class AVGDialog {

	private static ObjectMap<String, LTexture> lazyImages;

	public final static LTexture getRMXPDialog(String fileName, int width,
			int height) {
		if (lazyImages == null) {
			lazyImages = new ObjectMap<String, LTexture>(10);
		}
		Image dialog = BaseIO.loadImage(fileName);
		int w = dialog.getWidth();
		int[] pixels = dialog.getPixels();
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
		return getRMXPloadBuoyage(BaseIO.loadImage(fileName), width, height);
	}

	public final static LTexture getRMXPloadBuoyage(Image rmxpImage, int width,
			int height) {
		if (lazyImages == null) {
			lazyImages = new ObjectMap<String, LTexture>(10);
		}
		String keyName = ("buoyage" + width + "|" + height).intern();
		LTexture lazy = lazyImages.get(keyName);
		if (lazy == null) {
			Image lazyImage;
			Image image, left, right, center, up, down = null;
			final int objWidth = 32;
			final int objHeight = 32;
			final int x1 = 128;
			final int x2 = 160;
			final int y1 = 64;
			final int y2 = 96;
			final int k = 1;

			try {
				image = Image.drawClipImage(rmxpImage, objWidth, objHeight, x1,
						y1, x2, y2);
				lazyImage = Image.createImage(width, height);
				Canvas g = lazyImage.getCanvas();
				left = Image
						.drawClipImage(image, k, height, 0, 0, k, objHeight);
				right = Image.drawClipImage(image, k, height, objWidth - k, 0,
						objWidth, objHeight);
				center = Image.drawClipImage(image, width, height, k, k,
						objWidth - k, objHeight - k);
				up = Image.drawClipImage(image, width, k, 0, 0, objWidth, k);
				down = Image.drawClipImage(image, width, k, 0, objHeight - k,
						objWidth, objHeight);
				g.draw(center, 0, 0);
				g.draw(left, 0, 0);
				g.draw(right, width - k, 0);
				g.draw(up, 0, 0);
				g.draw(down, 0, height - k);

				lazy = lazyImage.texture();

				if (lazyImage != null) {
					lazyImage.close();
					lazyImage = null;
				}

				lazyImages.put(keyName, lazy);
			} catch (Exception e) {
				return null;
			} finally {
				left = null;
				right = null;
				center = null;
				up = null;
				down = null;
				image = null;
				
				LSystem.base()
						.log()
						.debug("Converted image to RMXP dialog image:"
								+ rmxpImage);
			}
		}
		return lazy;

	}

	private final static LTexture getRMXPDialog(Image rmxpImage, int width,
			int height, int size, int offset) {
		if (lazyImages == null) {
			lazyImages = new ObjectMap<String, LTexture>(10);
		}
		String keyName = "dialog" + width + "|" + height;
		LTexture lazy = lazyImages.get(keyName);
		if (lazy == null) {
			try {
				final int objWidth = 64;
				final int objHeight = 64;
				final int x1 = 128;
				final int x2 = 192;
				final int y1 = 0;
				final int y2 = 64;

				int center_size = objHeight - size * 2;

				Image lazyImage = null;

				Image image = null;

				Image messageImage = null;

				image = Image.drawClipImage(rmxpImage, objWidth, objHeight, x1,
						y1, x2, y2);

				Image centerTop = Image.drawClipImage(image, center_size, size,
						size, 0);

				Image centerDown = Image.drawClipImage(image, center_size,
						size, size, objHeight - size);

				Image leftTop = Image.drawClipImage(image, size, size, 0, 0);

				Image leftCenter = Image.drawClipImage(image, size,
						center_size, 0, size);

				Image leftDown = Image.drawClipImage(image, size, size, 0,
						objHeight - size);

				Image rightTop = Image.drawClipImage(image, size, size,
						objWidth - size, 0);

				Image rightCenter = Image.drawClipImage(image, size,
						center_size, objWidth - size, size);

				Image rightDown = Image.drawClipImage(image, size, size,
						objWidth - size, objHeight - size);

				lazyImage = Image.createImage(width, height);

				messageImage = Image.drawClipImage(rmxpImage, 128, 128, 0, 0,
						128, 128);

				Canvas g = lazyImage.getCanvas();

				g.setAlpha(0.5f);

				messageImage = Image.getResize(messageImage,
						width - offset + 1, height - offset + 1);

				g.draw(messageImage,
						(lazyImage.getWidth() - messageImage.getWidth()) / 2,
						(lazyImage.getHeight() - messageImage.getHeight()) / 2);

				g.setAlpha(1.0f);

				Image tmp = Image
						.getResize(centerTop, width - (size * 2), size);

				g.draw(tmp, size, 0);
				tmp = null;
				tmp = Image.getResize(centerDown, width - (size * 2), size);

				g.draw(tmp, size, height - size);
				tmp = null;

				g.draw(leftTop, 0, 0);

				tmp = Image.getResize(leftCenter, leftCenter.getWidth(), width
						- (size * 2));

				g.draw(tmp, 0, size);
				tmp = null;
				g.draw(leftDown, 0, height - size);

				int right = width - size;

				g.draw(rightTop, right, 0);

				tmp = Image.getResize(rightCenter, leftCenter.getWidth(), width
						- (size * 2));

				g.draw(tmp, right, size);
				tmp = null;
				g.draw(rightDown, right, height - size);

				lazy = lazyImage.texture();

				if (lazyImage != null) {
					lazyImage.close();
					lazyImage = null;
				}

				lazyImages.put(keyName, lazy);

				image.close();
				messageImage.close();
				centerTop.close();
				centerDown.close();
				leftTop.close();
				leftCenter.close();
				leftDown.close();
				rightTop.close();
				rightCenter.close();
				rightDown.close();

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

				LSystem.base()
						.log()
						.debug("Converted image to RMXP dialog image:"
								+ rmxpImage);
			} catch (Exception e) {

			}
		}
		return lazy;
	}

	public static void clear() {
		for (LTexture tex2d : lazyImages.values()) {
			if (tex2d != null) {
				tex2d.close();
				tex2d = null;
			}
		}
		lazyImages.clear();
	}
}
