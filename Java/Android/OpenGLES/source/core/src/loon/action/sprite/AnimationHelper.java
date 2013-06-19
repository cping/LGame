package loon.action.sprite;

import java.util.HashMap;

import loon.core.graphics.LColor;
import loon.core.graphics.LImage;
import loon.core.graphics.device.LGraphics;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.TextureUtils;
import loon.utils.CollectionUtils;


/**
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
public class AnimationHelper {

	private final static HashMap<String, AnimationHelper> animations = new HashMap<String, AnimationHelper>();

	// 角色向下
	public LTexture[] downImages;

	// 角色向上
	public LTexture[] upImages;

	// 角色向左
	public LTexture[] leftImages;

	// 角色向右
	public LTexture[] rightImages;

	/**
	 * 以RMVX的角色格式创建对象(总图大小96x128，每格大小32x32)
	 * 
	 * @param fileName
	 * @return
	 */
	public static AnimationHelper makeRMVXObject(String fileName) {
		return makeObject(fileName, 4, 3, 32, 32);
	}

	/**
	 * 以RMXP的角色格式创建对象(总图大小128x192，每格大小32x48)
	 * 
	 * @param fileName
	 * @return
	 */
	public static AnimationHelper makeRMXPObject(String fileName) {
		return makeObject(fileName, 4, 4, 32, 48);
	}

	/**
	 * 以E社的角色格式创建对象(总图大小200x200，每格大小40x50)
	 * 
	 * @param fileName
	 * @return
	 */
	public static AnimationHelper makeEObject(String fileName) {
		return makeObject(fileName, 40, 50, LColor.green);
	}

	/**
	 * 以RMVX的角色格式创建分解头象
	 * 
	 * @param fileName
	 * @return
	 */
	public static LTexture[] makeFace(String fileName) {
		return TextureUtils.getSplitTextures(fileName, 96, 96);
	}

	/**
	 * 绘制一个RMVX样式的游标
	 * 
	 * @return
	 */
	public static LTexture makeCursor(int w, int h) {
		LImage cursor = LImage.createImage(w, h, true);
		LGraphics g = cursor.getLGraphics();
		g.setColor(0, 0, 0, 255);
		g.fillRect(0, 0, w, h);
		g.setColor(255, 255, 255, 255);
		g.fillRect(1, 1, w - 2, h - 2);
		g.setColor(0, 0, 0, 255);
		g.fillRect(4, 4, w - 8, h - 8);
		g.setColor(0, 0, 0, 255);
		g.fillRect(w / 4, 0, w / 2, h);
		g.setColor(0, 0, 0, 255);
		g.fillRect(0, h / 4, w, h / 2);
		g.dispose();
		g = null;
		int[] basePixels = cursor.getPixels();
		int length = basePixels.length;
		int c = LColor.black.getRGB();
		for (int i = 0; i < length; i++) {
			if (basePixels[i] == c) {
				basePixels[i] = 0xffffff;
			}
		}
		cursor.setPixels(basePixels, w, h);
		LTexture texture = cursor.getTexture();
		if (cursor != null) {
			cursor.dispose();
			cursor = null;
		}
		return texture;
	}

	public static AnimationHelper makeObject(String fileName, int row, int col,
			int tileWidth, int tileHeight) {
		String key = fileName.trim().toLowerCase();
		AnimationHelper animation =  animations.get(key);
		if (animation == null) {
			LTexture[][] images = TextureUtils.getSplit2Textures(fileName,
					tileWidth, tileHeight);
			LTexture[][] result = new LTexture[row][col];
			for (int y = 0; y < col; y++) {
				for (int x = 0; x < row; x++) {
					result[x][y] = images[y][x];
				}
			}
			images = null;
			animations.put(key, animation = makeObject(result[0], result[1],
					result[2], result[3]));
		}
		return animation;
	}

	public static AnimationHelper makeObject(String fileName, int tileWidth,
			int tileHeight, LColor col) {
		String key = fileName.trim().toLowerCase();
		AnimationHelper animation =  animations.get(key);
		if (animation == null) {
			LTexture texture = TextureUtils.filterColor(fileName, col);

			int wlength = texture.getWidth() / tileWidth;
			int hlength = texture.getHeight() / tileHeight;

			LTexture[][] images = TextureUtils.getSplit2Textures(texture,
					tileWidth, tileHeight);

			LTexture[][] result = new LTexture[hlength][wlength];
			for (int y = 0; y < wlength; y++) {
				for (int x = 0; x < hlength; x++) {
					result[x][y] = images[y][x];
				}
			}

			images = null;

			animations.put(key, animation = makeObject(result[0], result[1],
					result[3], result[2]));
		}
		return animation;

	}

	public final static AnimationHelper makeObject(LTexture[] down,
			LTexture[] left, LTexture[] right, LTexture[] up) {
		AnimationHelper animation = new AnimationHelper();
		animation.downImages = down;
		animation.leftImages = left;
		animation.rightImages = right;
		animation.upImages = up;
		return animation;
	}

	public final static void dispose(LTexture[] images) {
		if (images == null) {
			return;
		}
		for (int i = 0; i < images.length; i++) {
			images[i].dispose();
			images[i] = null;
		}
	}

	AnimationHelper() {

	}

	public AnimationHelper(AnimationHelper animation) {
		leftImages = (LTexture[]) CollectionUtils.copyOf(animation.leftImages);
		downImages = (LTexture[]) CollectionUtils.copyOf(animation.downImages);
		upImages = (LTexture[]) CollectionUtils.copyOf(animation.upImages);
		rightImages = (LTexture[]) CollectionUtils
				.copyOf(animation.rightImages);
	}

	public void dispose() {
		dispose(downImages);
		dispose(upImages);
		dispose(leftImages);
		dispose(rightImages);
		animations.remove(this);
	}
}
