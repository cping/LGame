/**
 * 
 * Copyright 2014
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
 * @email：javachenpeng@yahoo.com
 * @version 0.4.1
 */
package loon.core.graphics.component;

import loon.LSystem;
import loon.core.graphics.device.LColor;
import loon.core.graphics.device.LGradation;
import loon.core.graphics.device.LGraphics;
import loon.core.graphics.device.LImage;
import loon.core.graphics.opengl.LSubTexture;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.utils.MathUtils;
import loon.utils.collection.Array;
import loon.utils.collection.ArrayMap;

public class DefUI {

	public static String win_frame_UI = LSystem.FRAMEWORK_IMG_NAME + "wbar.png";

	/**
	 * 返回一组随机纹理当做背景图
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public static LTexture getGameRandomBackground(int width, int height) {
		return getGameRandomBackground(45, width, height);
	}

	/**
	 * 返回一组指定色彩元素的随机纹理当做背景图
	 * 
	 * @param color
	 * @param width
	 * @param height
	 * @return
	 */
	public static LTexture getGameRandomBackground(int color, int width,
			int height) {
		LImage temp = new LImage(width, height, false);
		LGraphics g = temp.getLGraphics();
		g.setColor(color, color, color);
		g.fillRect(0, 0, width, height);
		for (int i = 0; i < 20000; i++) {
			int size = 50;
			int rand = (int) (MathUtils.random() * 2);
			int randX = (int) (MathUtils.random() * (width + size)) - size;
			int randY = (int) (MathUtils.random() * (height + size)) - size;
			int maxDelta = size;
			float delta = 0;
			for (int j = 0; j < maxDelta; j++) {
				if (j < maxDelta / 2) {
					delta += 0.35;
				} else {
					delta -= 0.35;
				}
				g.setColor(color + (int) (delta), color + (int) (delta), color
						+ (int) (delta));
				if (rand == 0) {
					g.fillRect(randX + j, randY, 1, 1);
				} else {
					g.fillRect(randX, randY + j, 1, 1);
				}
			}
		}
		g.dispose();
		temp.setAutoDispose(true);
		LTexture background = temp.getTexture();
		if (temp != null) {
			temp.dispose();
			temp = null;
		}
		return background;
	}

	/**
	 * 生成指定大小，指定列数的表格图
	 * 
	 * @param width
	 * @param height
	 * @param size
	 * @return
	 */
	public static LTexture getGameWinTable(int width, int height, int size) {
		return getGameWinTable(width, height, size, LColor.blue, LColor.black,
				true);
	}

	/**
	 * 生成指定大小，指定列数的表格图
	 * 
	 * @param width
	 * @param height
	 * @param size
	 * @param start
	 * @param end
	 * @param drawHeigth
	 * @return
	 */
	public static LTexture getGameWinTable(int width, int height, int size,
			LColor start, LColor end, boolean drawHeigth) {
		DefUI tool = new DefUI();
		LImage image = new LImage(width, height);
		LGradation gradation = LGradation.getInstance(start, end, width,
				height, 125);
		LGraphics g = image.getLGraphics();
		if (drawHeigth) {
			gradation.drawHeight(g, 0, 0);
		} else {
			gradation.drawWidth(g, 0, 0);
		}
		tool.drawTable(g, 0, 0, width, height, size);
		g.dispose();
		LTexture texture = image.getTexture();
		if (image != null) {
			image.dispose();
		}
		return texture;
	}

	/**
	 * 返回指定大小的窗口背景
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public static LTexture getGameWinFrame(int width, int height) {
		return getGameWinFrame(width, height, LColor.blue, LColor.black, true);
	}

	/**
	 * 返回指定大小的窗口背景
	 * 
	 * @param width
	 * @param height
	 * @param start
	 * @param end
	 * @param drawHeigth
	 * @return
	 */
	public static LTexture getGameWinFrame(int width, int height, LColor start,
			LColor end, boolean drawHeigth) {
		DefUI tool = new DefUI();
		LImage image = new LImage(width, height);
		LGraphics g = image.getLGraphics();
		LGradation gradation = LGradation.getInstance(start, end, width,
				height, 125);
		if (drawHeigth) {
			gradation.drawHeight(g, 0, 0);
		} else {
			gradation.drawWidth(g, 0, 0);
		}
		tool.drawFrame(g, 0, 0, width, height);
		g.dispose();
		LTexture texture = image.getTexture();
		if (image != null) {
			image.dispose();
		}
		return texture;
	}

	public void drawTable(LGraphics g, int x, int y, int width, int height,
			int size, boolean border) {
		boolean[] flags = new boolean[size];
		for (int i = 0; i < flags.length; i++) {
			flags[i] = true;
		}
		drawChoices(g, x, y, width, height, size, flags, LColor.white);
		drawFrame(g, x, y, width, height);
		if (border) {
			drawBorder(g, x, y, width, height, size);
		}
	}

	public void drawTable(LGraphics g, int x, int y, int width, int height,
			int size) {
		drawTable(g, x, y, width, height, size, true);
	}

	public void drawFrame(LGraphics g, int x, int y, int width, int height) {
		LImage[] corners = new LImage[4];
		for (int i = 0; i < corners.length; i++) {
			corners[i] = DefUI.getDefaultWindow("window" + (i + 4));
		}
		int CornerSize = corners[0].getWidth();
		for (int a = 0; a < 4; a++) {
			LImage img = null;
			int length = 0;
			int size = 0;
			int StartX = 0;
			int StartY = 0;
			switch (a) {
			case 0:
				length = width;
				img = DefUI.getDefaultWindow("window0");
				size = img.getWidth();
				break;
			case 1:
				length = height;
				img = DefUI.getDefaultWindow("window1");
				size = img.getHeight();
				break;
			case 2:
				length = width;
				img = DefUI.getDefaultWindow("window2");
				size = img.getWidth();
				StartY = height - img.getHeight();
				break;
			case 3:
				length = height;
				img = DefUI.getDefaultWindow("window3");
				size = img.getHeight();
				StartX = width - img.getWidth();
			}

			int finish = length - CornerSize;
			for (int i = CornerSize; i <= finish; i += size) {
				if (a % 2 == 0)
					g.drawImage(img, x + i + StartX, y + StartY);
				else {
					g.drawImage(img, x + StartX, y + i + StartY);
				}
			}
		}
		g.drawImage(corners[0], x, y);
		g.drawImage(corners[1], x, y + height - CornerSize);
		g.drawImage(corners[2], x + width - CornerSize, y + height - CornerSize);
		g.drawImage(corners[3], x + width - CornerSize, y);
	}

	private void drawBorder(LGraphics g, int x, int y, int width, int height,
			int nums) {
		LImage img = DefUI.getDefaultWindow("window0");
		int size = img.getHeight();
		int length = img.getWidth();
		int bun = (int) MathUtils.round(1.0D * (height - size) / nums);
		int offset = 0;

		for (int i = 1; i < nums; i++) {
			for (int j = 0; j <= width - size - length / 2; j += length) {
				offset = x + j;
				if (offset > x - 4) {
					g.drawImage(img, offset + 4, y + bun * i);
				}
			}
		}
	}

	public void drawHorizonLine(LGraphics g, int x, int y, int width) {
		LImage img = DefUI.getDefaultWindow("window0");
		int length = img.getWidth();
		for (int j = 0; j <= width; j += length)
			g.drawImage(img, x + j, y);
	}

	private void drawChoices(LGraphics g, int x, int y, int width, int height,
			int size, boolean[] oks, LColor col) {
		LColor[] colors = new LColor[size];
		for (int i = 0; i < colors.length; i++) {
			colors[i] = col;
		}
		drawChoices(g, x, y, width, height, size, oks, colors);
	}

	private void drawChoices(LGraphics g, int x, int y, int width, int height,
			int messize, boolean[] oks, LColor[] colors) {
		LImage img = DefUI.getDefaultWindow("window0");
		int size = img.getHeight();
		int bun = (int) MathUtils.round(1.0D * (height - size) / messize);
		for (int i = 0; i < messize; i++) {
			g.setColor(colors[i]);
			if (!oks[i]) {
				setTransmission(g, x, y + bun * i, width, bun, LColor.black,
						0.7F);
			}
		}
	}

	private void setTransmission(LGraphics g, int x, int y, int w, int h,
			LColor col, float t) {
		g.setAlpha(t);
		g.setColor(col);
		g.fillRect(x, y, w, h);
		g.setAlpha(1f);
	}

	private static Array<LTexture> defaultTextures;

	private static ArrayMap defaultWindowHash;

	public static LImage[] getWindow(String fileName, int frameSize,
			int cornerSize, int wholeSize, int borderLength) {
		LImage[] texs = new LImage[8];
		LImage tmp = LImage.createImage(fileName);
		int[] pixels = tmp.getPixels();
		int color = LColor.white.getARGB();
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] == color) {
				pixels[i] = 0;
			}
		}
		LImage imgs = new LImage(tmp.getWidth(), tmp.getHeight(), true);
		imgs.setPixels(pixels, tmp.getWidth(), tmp.getHeight());
		if (tmp != null) {
			tmp.dispose();
			tmp = null;
		}
		texs[0] = imgs.getSubImage(wholeSize / 2 - borderLength / 2, 0,
				borderLength, frameSize);
		texs[1] = imgs.getSubImage(0, wholeSize / 2 - borderLength / 2,
				frameSize, borderLength);
		texs[2] = imgs.getSubImage(wholeSize / 2 - borderLength / 2, wholeSize
				- frameSize, borderLength, frameSize);
		texs[3] = imgs.getSubImage(wholeSize - frameSize, wholeSize / 2
				- borderLength / 2, frameSize, borderLength);
		texs[4] = imgs.getSubImage(0, 0, cornerSize, cornerSize);
		texs[5] = imgs.getSubImage(0, wholeSize - cornerSize, cornerSize,
				cornerSize);
		texs[6] = imgs.getSubImage(wholeSize - cornerSize, wholeSize
				- cornerSize, cornerSize, cornerSize);
		texs[7] = imgs.getSubImage(wholeSize - cornerSize, 0, cornerSize,
				cornerSize);
		if (imgs != null) {
			imgs.dispose();
			imgs = null;
		}
		return texs;
	}

	public static synchronized void reset() {
		defaultWindowHash = null;
		defaultTextures = null;
	}

	public static synchronized LImage getDefaultWindow(String name) {
		if (defaultWindowHash == null) {
			LImage[] texs = getWindow(win_frame_UI, 6, 14, 64, 8);
			defaultWindowHash = new ArrayMap(texs.length);
			for (int i = 0; i < texs.length; i++) {
				defaultWindowHash.put("window" + i, texs[i]);
			}
		}
		return (LImage) defaultWindowHash.get(name);
	}

	public static synchronized LTexture getDefaultTextures(int index) {
		if (defaultTextures == null) {
			defaultTextures = new Array<LTexture>();
			LTexture spritesheet = LTextures
					.loadTexture(LSystem.FRAMEWORK_IMG_NAME + "ui.png");
			LSubTexture windowbar = new LSubTexture(spritesheet, 0, 0, 512, 32);
			LSubTexture panelbody = new LSubTexture(spritesheet, 1, 41 - 8, 17,
					57 - 8);
			LSubTexture panelborder = new LSubTexture(spritesheet, 0, 41 - 8,
					1, 512 - 8);
			LSubTexture buttonleft = new LSubTexture(spritesheet, 17, 41 - 8,
					33, 72 - 8);
			LSubTexture buttonbody = new LSubTexture(spritesheet, 34, 41 - 8,
					48, 72 - 8);
			LSubTexture checkboxunchecked = new LSubTexture(spritesheet, 49,
					41 - 8, 72, 63 - 8);
			LSubTexture checkboxchecked = new LSubTexture(spritesheet, 73,
					41 - 8, 96, 63 - 8);
			LSubTexture imagebuttonidle = new LSubTexture(spritesheet, 145,
					41 - 8, 176, 72 - 8);
			LSubTexture imagebuttonhover = new LSubTexture(spritesheet, 177,
					41 - 8, 208, 72 - 8);
			LSubTexture imagebuttonactive = new LSubTexture(spritesheet, 209,
					41 - 8, 240, 72 - 8);
			LSubTexture textfieldleft = new LSubTexture(spritesheet, 218,
					40 - 8, 233, 72 - 8);
			LSubTexture textfieldbody = new LSubTexture(spritesheet, 234,
					40 - 8, 250, 72 - 8);
			defaultTextures.add(windowbar.get());
			defaultTextures.add(panelbody.get());
			defaultTextures.add(panelborder.get());
			defaultTextures.add(buttonleft.get());
			defaultTextures.add(buttonbody.get());
			defaultTextures.add(checkboxunchecked.get());
			defaultTextures.add(checkboxchecked.get());
			defaultTextures.add(imagebuttonidle.get());
			defaultTextures.add(imagebuttonhover.get());
			defaultTextures.add(imagebuttonactive.get());
			defaultTextures.add(textfieldleft.get());
			defaultTextures.add(textfieldbody.get());
		}
		return defaultTextures.get(index);
	}
}
