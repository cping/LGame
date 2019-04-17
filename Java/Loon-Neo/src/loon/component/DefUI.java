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
package loon.component;

import loon.BaseIO;
import loon.LSystem;
import loon.LTexture;
import loon.canvas.Canvas;
import loon.canvas.Canvas.Composite;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.canvas.LGradation;
import loon.canvas.Pixmap;
import loon.opengl.LSubTexture;
import loon.utils.TArray;
import loon.utils.ArrayMap;
import loon.utils.MathUtils;

public class DefUI {

	private static DefUI instance;

	public final static DefUI make() {
		return new DefUI();
	}

	public final static DefUI self() {
		if (instance == null) {
			synchronized (DefUI.class) {
				if (instance == null) {
					instance = make();
				}
			}
		}
		return instance;
	}

	public final static String win_frame_UI = LSystem.getSystemImagePath() + "wbar.png";

	private TArray<LTexture> defaultTextures;

	private ArrayMap defaultWindowHash;

	/**
	 * 剪切指定图片边缘为指定半径(主要就是把正方形的头像图剪成半圆或椭圆的，或者切出椭圆按钮之类)
	 * 
	 * @param path
	 * @return
	 */
	public final static LTexture getRoundTexture(String path) {
		return getRoundImage(path).texture();
	}

	/**
	 * 剪切指定图片边缘为指定半径(主要就是把正方形的头像图剪成半圆或椭圆的，或者切出椭圆按钮之类)
	 * 
	 * @param path
	 * @return
	 */
	public final static Image getRoundImage(String path) {
		return getRoundImage(path, 128, 3);
	}

	/**
	 * 剪切指定图片边缘为指定半径(主要就是把正方形的头像图剪成半圆或椭圆的，或者切出椭圆按钮之类)
	 * 
	 * @param path
	 * @param strokeWidth
	 * @return
	 */
	public final static Image getRoundImage(String path, int strokeWidth) {
		return getRoundImage(path, 128, strokeWidth);
	}

	/**
	 * 剪切指定图片边缘为指定半径(主要就是把正方形的头像图剪成半圆或椭圆的，或者切出椭圆按钮之类)
	 * 
	 * @param path
	 * @param radius
	 * @param strokeWidth
	 * @return
	 */
	public final static Image getRoundImage(String path, int radius, int strokeWidth) {
		Image img = BaseIO.loadImage(path);
		Image tmp = getRoundImage(img, radius, strokeWidth);
		if (img != null) {
			img.close();
			img = null;
		}
		return tmp;
	}

	/**
	 * 剪切指定图片边缘为指定半径(主要就是把正方形的头像图剪成半圆或椭圆的，或者切出椭圆按钮之类)
	 * 
	 * @param bitmap
	 * @return
	 */
	public final static Image getRoundImage(Image bitmap) {
		return getRoundImage(bitmap, 128, 3);
	}

	/**
	 * 剪切指定图片边缘为指定半径(主要就是把正方形的头像图剪成半圆或椭圆的，或者切出椭圆按钮之类)
	 * 
	 * @param bitmap
	 * @param radius
	 * @return
	 */
	public final static Image getRoundImage(Image bitmap, int radius) {
		return getRoundImage(bitmap, radius, 3);
	}

	/**
	 * 剪切指定图片边缘为指定半径(主要就是把正方形的头像图剪成半圆或椭圆的，或者切出椭圆按钮之类)
	 * 
	 * @param bitmap
	 * @param radius
	 * @param strokeWidth
	 * @return
	 */
	public final static Image getRoundImage(Image bitmap, int radius, int strokeWidth) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float left, top, right, bottom;
		float dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Image output = Image.createImage(width, height);
		Canvas canvas = output.getCanvas();
		canvas.setColor(LColor.white);
		canvas.fillRoundRect(dst_left, dst_top, dst_right, dst_bottom, radius);
		canvas.setCompositeOperation(Composite.SRC_IN);
		canvas.draw(bitmap, 0, 0, right, bottom, left, top, right, bottom);
		if (strokeWidth > 0) {
			canvas.setColor(LColor.black);
			canvas.setStrokeWidth(strokeWidth);
			canvas.strokeRoundRect(dst_left, dst_top, dst_right, dst_bottom, radius);
		}
		return output;
	}

	/**
	 * 绘制指定大小的圆形游戏窗口图片
	 * 
	 * @param width
	 * @param height
	 * @param radius
	 * @param color
	 * @return
	 */
	public final static LTexture getGameWinRound(int width, int height, int radius, LColor color) {
		Canvas g = LSystem.base().graphics().createCanvas(width, height);
		g.setColor(color);
		g.fillRect(0, radius, width, height - 2 * radius);
		g.fillRect(radius, 0, width - 2 * radius, height);
		g.fillCircle(radius, radius, radius);
		g.fillCircle(radius, height - radius, radius);
		g.fillCircle(width - radius, radius, radius);
		g.fillCircle(width - radius, height - radius, radius);
		return g.toTexture();
	}

	/**
	 * 绘制指定大小的中空窗体图片
	 * 
	 * @param width
	 * @param height
	 * @param lineWidth
	 * @param color
	 * @return
	 */
	public final static LTexture getGameWinHollow(int width, int height, int lineWidth, LColor color) {
		Canvas g = LSystem.base().graphics().createCanvas(width, height);
		g.setColor(color);
		g.fillRect(0, 0, lineWidth, height);
		g.fillRect(width - lineWidth, 0, lineWidth, height);
		g.fillRect(lineWidth, height - lineWidth, width - lineWidth * 2, lineWidth);
		g.fillRect(lineWidth, 0, width - lineWidth * 2, lineWidth);
		return g.toTexture();
	}

	/**
	 * 绘制一个菱形的窗体图片
	 * 
	 * @param width
	 * @param height
	 * @param color
	 * @return
	 */
	public final static LTexture getGameWinDiamond(int width, int height, LColor color) {
		Pixmap pixmap = new Pixmap(width, height, true);
		pixmap.setColor(color);
		pixmap.fillTriangle(0, height / 2, width / 2, height, width, height / 2);
		pixmap.fillTriangle(0, height / 2, width / 2, 0, width, height / 2);
		return pixmap.toTexture();
	}

	/**
	 * 返回一组随机纹理当做背景图
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public final static LTexture getGameRandomBackground(int width, int height) {
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
	public final static LTexture getGameRandomBackground(int color, int width, int height) {
		Canvas g = LSystem.base().graphics().createCanvas(width, height);
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
				g.setColor(color + (int) (delta), color + (int) (delta), color + (int) (delta));
				if (rand == 0) {
					g.fillRect(randX + j, randY, 1, 1);
				} else {
					g.fillRect(randX, randY + j, 1, 1);
				}
			}
		}
		LTexture background = g.toTexture();
		if (g.image != null) {
			g.image.close();
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
	public final static LTexture getGameWinTable(int width, int height, int size) {
		return getGameWinTable(width, height, size, LColor.blue, LColor.black, true);
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
	public final static LTexture getGameWinTable(int width, int height, int size, LColor start, LColor end,
			boolean drawHeigth) {
		DefUI tool = new DefUI();
		Canvas g = LSystem.base().graphics().createCanvas(width, height);
		LGradation gradation = LGradation.getInstance(start, end, width, height, 125);
		if (drawHeigth) {
			gradation.drawHeight(g, 0, 0);
		} else {
			gradation.drawWidth(g, 0, 0);
		}
		tool.drawTable(g, 0, 0, width, height, size);
		LTexture texture = g.toTexture();
		if (g.image != null) {
			g.image.close();
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
	public final static LTexture getGameWinFrame(int width, int height) {
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
	public final static LTexture getGameWinFrame(int width, int height, LColor start, LColor end, boolean drawHeigth) {
		DefUI tool = new DefUI();
		Canvas g = LSystem.base().graphics().createCanvas(width, height);
		LGradation gradation = LGradation.getInstance(start, end, width, height, 125);
		if (drawHeigth) {
			gradation.drawHeight(g, 0, 0);
		} else {
			gradation.drawWidth(g, 0, 0);
		}
		tool.drawFrame(g, 0, 0, width, height);
		LTexture texture = g.toTexture();
		if (g.image != null) {
			g.image.close();
		}
		return texture;
	}

	public final static LTexture getGameWinPixelFrame(int width, int height) {
		return getGameWinFrame(width, height, LColor.blue, LColor.black, true);
	}

	public final static LTexture getGameWinPixelFrame(int width, int height, LColor start, LColor end,
			boolean drawHeigth) {
		Canvas g = LSystem.base().graphics().createCanvas(width, height);
		DefUI tool = new DefUI();
		Pixmap pix = Pixmap.createImage(width, height);
		LGradation gradation = LGradation.getInstance(start, end, width, height, 125);
		if (drawHeigth) {
			gradation.drawHeight(pix, 0, 0);
		} else {
			gradation.drawWidth(pix, 0, 0);
		}
		g.draw(pix.getImage(), 0, 0, width, height);
		tool.drawFrame(g, 0, 0, width, height);
		LTexture texture = g.toTexture();
		if (g.image != null) {
			g.image.close();
		}
		return texture;
	}

	public final void drawTable(Canvas g, int x, int y, int width, int height, int size, boolean border) {
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

	public final void drawTable(Canvas g, int x, int y, int width, int height, int size) {
		drawTable(g, x, y, width, height, size, true);
	}

	public final void drawFrame(Canvas g, int x, int y, int width, int height) {
		Image[] corners = new Image[4];
		for (int i = 0; i < corners.length; i++) {
			corners[i] = getDefaultWindow("window" + (i + 4));
		}
		int CornerSize = corners[0].getWidth();
		for (int a = 0; a < 4; a++) {
			Image img = null;
			int length = 0;
			int size = 0;
			int StartX = 0;
			int StartY = 0;
			switch (a) {
			case 0:
				length = width;
				img = getDefaultWindow("window0");
				size = img.getWidth();
				break;
			case 1:
				length = height;
				img = getDefaultWindow("window1");
				size = img.getHeight();
				break;
			case 2:
				length = width;
				img = getDefaultWindow("window2");
				size = img.getWidth();
				StartY = height - img.getHeight();
				break;
			case 3:
				length = height;
				img = getDefaultWindow("window3");
				size = img.getHeight();
				StartX = width - img.getWidth();
			}

			int finish = length - CornerSize;
			for (int i = CornerSize; i <= finish; i += size) {
				if (a % 2 == 0)
					g.draw(img, x + i + StartX, y + StartY);
				else {
					g.draw(img, x + StartX, y + i + StartY);
				}
			}
		}
		g.draw(corners[0], x, y);
		g.draw(corners[1], x, y + height - CornerSize);
		g.draw(corners[2], x + width - CornerSize, y + height - CornerSize);
		g.draw(corners[3], x + width - CornerSize, y);
	}

	private final void drawBorder(Canvas g, int x, int y, int width, int height, int nums) {
		Image img = getDefaultWindow("window0");
		int size = img.getHeight();
		int length = img.getWidth();
		int bun = MathUtils.round(1f * (height - size) / nums);
		int offset = 0;

		for (int i = 1; i < nums; i++) {
			for (int j = 0; j <= width - size - length / 2; j += length) {
				offset = x + j;
				if (offset > x - 4) {
					g.draw(img, offset + 4, y + bun * i);
				}
			}
		}
	}

	public final void drawHorizonLine(Canvas g, int x, int y, int width) {
		Image img = getDefaultWindow("window0");
		int length = (int) img.width();
		for (int j = 0; j <= width; j += length)
			g.draw(img, x + j, y);
	}

	private final void drawChoices(Canvas g, int x, int y, int width, int height, int size, boolean[] oks, LColor col) {
		LColor[] colors = new LColor[size];
		for (int i = 0; i < colors.length; i++) {
			colors[i] = col;
		}
		drawChoices(g, x, y, width, height, size, oks, colors);
	}

	private final void drawChoices(Canvas g, int x, int y, int width, int height, int messize, boolean[] oks,
			LColor[] colors) {
		Image img = getDefaultWindow("window0");
		int size = (int) img.height();
		int bun = MathUtils.round(1f * (height - size) / messize);
		for (int i = 0; i < messize; i++) {
			g.setColor(colors[i]);
			if (!oks[i]) {
				setTransmission(g, x, y + bun * i, width, bun, LColor.black, 0.7F);
			}
		}
	}

	private final void setTransmission(Canvas g, int x, int y, int w, int h, LColor col, float t) {
		g.setAlpha(t);
		g.setColor(col);
		g.fillRect(x, y, w, h);
		g.setAlpha(1f);
	}

	public final static Image[] getWindow(String fileName, int frameSize, int cornerSize, int wholeSize,
			int borderLength) {
		Image[] texs = new Image[8];
		Image tmp = BaseIO.loadImage(fileName);
		int[] pixels = tmp.getPixels();
		int color = LColor.white.getARGB();
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] == color) {
				pixels[i] = 0;
			}
		}
		Canvas canvas = LSystem.base().graphics().createCanvas(tmp.width(), tmp.height());
		Image imgs = canvas.image;
		imgs.setPixels(pixels, (int) tmp.width(), (int) tmp.height());
		if (tmp != null) {
			tmp.close();
			tmp = null;
		}

		texs[0] = imgs.getSubImage(wholeSize / 2 - borderLength / 2, 0, borderLength, frameSize);
		texs[1] = imgs.getSubImage(0, wholeSize / 2 - borderLength / 2, frameSize, borderLength);
		texs[2] = imgs.getSubImage(wholeSize / 2 - borderLength / 2, wholeSize - frameSize, borderLength, frameSize);
		texs[3] = imgs.getSubImage(wholeSize - frameSize, wholeSize / 2 - borderLength / 2, frameSize, borderLength);
		texs[4] = imgs.getSubImage(0, 0, cornerSize, cornerSize);
		texs[5] = imgs.getSubImage(0, wholeSize - cornerSize, cornerSize, cornerSize);
		texs[6] = imgs.getSubImage(wholeSize - cornerSize, wholeSize - cornerSize, cornerSize, cornerSize);
		texs[7] = imgs.getSubImage(wholeSize - cornerSize, 0, cornerSize, cornerSize);
		if (imgs != null) {
			imgs.close();
			imgs = null;
		}
		return texs;
	}

	public final void resetDefaultUI() {
		defaultWindowHash = null;
		defaultTextures = null;
	}

	public final Image getDefaultWindow(String name) {
		if (defaultWindowHash == null || defaultWindowHash.size() == 0) {
			Image[] texs = getWindow(win_frame_UI, 6, 14, 64, 8);
			defaultWindowHash = new ArrayMap(texs.length);
			for (int i = 0; i < texs.length; i++) {
				defaultWindowHash.put("window" + i, texs[i]);
			}
		}
		return (Image) defaultWindowHash.get(name);
	}

	private LTexture lastTexture;

	public final LTexture getDefaultTextures(int index) {
		if (LSystem.base() == null) {
			return null;
		}
		if (defaultTextures == null || defaultTextures.size == 0) {
			if (defaultTextures == null) {
				defaultTextures = new TArray<LTexture>();
			} else {
				defaultTextures.clear();
			}
			if (lastTexture != null) {
				lastTexture.setDisabledTexture(false);
				lastTexture.close(true);
				lastTexture = null;
			}
			lastTexture = LSystem.newTexture(LSystem.getSystemImagePath() + "ui.png");
			lastTexture.setDisabledTexture(true);
			LSubTexture windowbar = new LSubTexture(lastTexture, 0, 0, 512, 32);
			LSubTexture panelbody = new LSubTexture(lastTexture, 1, 41 - 8, 17, 57 - 8);
			LSubTexture panelborder = new LSubTexture(lastTexture, 0, 41 - 8, 1, 512 - 8);
			LSubTexture buttonleft = new LSubTexture(lastTexture, 17, 41 - 8, 33, 72 - 8);
			LSubTexture buttonbody = new LSubTexture(lastTexture, 34, 41 - 8, 48, 72 - 8);
			LSubTexture checkboxunchecked = new LSubTexture(lastTexture, 49, 41 - 8, 72, 63 - 8);
			LSubTexture checkboxchecked = new LSubTexture(lastTexture, 73, 41 - 8, 96, 63 - 8);
			LSubTexture imagebuttonidle = new LSubTexture(lastTexture, 145, 41 - 8, 176, 72 - 8);
			LSubTexture imagebuttonhover = new LSubTexture(lastTexture, 177, 41 - 8, 208, 72 - 8);
			LSubTexture imagebuttonactive = new LSubTexture(lastTexture, 209, 41 - 8, 240, 72 - 8);
			LSubTexture textfieldleft = new LSubTexture(lastTexture, 218, 40 - 8, 233, 72 - 8);
			LSubTexture textfieldbody = new LSubTexture(lastTexture, 234, 40 - 8, 250, 72 - 8);
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

	public final void clearDefaultUI() {
		if (defaultTextures != null) {
			for (LTexture tex : defaultTextures) {
				if (tex != null) {
					tex.setDisabledTexture(false);
					tex.close();
				}
			}
			defaultTextures.clear();
		}
		LGradation.dispose();
	}
}
