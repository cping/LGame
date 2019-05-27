/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
 * @version 0.5
 */
package org.test;

import loon.LTexture;
import loon.Screen;
import loon.canvas.LColor;
import loon.event.GameTouch;
import loon.opengl.GLEx;
import loon.utils.IntArray;
import loon.utils.timer.LTimerContext;

public class JigsawTest extends Screen {

	private boolean completed = false;

	private LTexture texture;

	private LTexture[] textures;

	private IntArray initBlocks;

	private IntArray randBlocks;

	private int cols;

	private int rows;

	private int tileWidth;

	private int tileHeight;

	private int selectIndex = -1;

	@Override
	public void draw(GLEx g) {
		if (isOnLoadComplete()) {
			// 拼图完成
			if (completed) {
				g.draw(texture, 0, 0);
			} else {
				int orderCount = 0;
				int texId = -1;
				for (int i = 0; i < rows; i++) {
					for (int j = 0; j < cols; j++) {
						int idx = getIndex(i, j);
						int oldTexId = texId;
						texId = randBlocks.get(idx);
						LTexture texture = textures[texId];
						// 如果旧纹理索引小于当前纹理索引
						if (oldTexId < texId) {
							// 已排序的图片数量+1
							orderCount++;
						}
						int x = i * tileWidth;
						int y = j * tileHeight;
						g.draw(texture, x, y, tileWidth, tileHeight);
						// 选中的瓦片
						if (selectIndex == idx) {
							g.setColor(LColor.green.setAlpha(0.5f));
							g.fillRect(i * tileWidth, j * tileHeight, tileWidth - 1, tileHeight - 1);
						} else {
							g.setColor(LColor.orange);
							g.drawRect(i * tileWidth, j * tileHeight, tileWidth - 1, tileHeight - 1);
						}
						g.resetColor();
					}
				}
				// 如果所有图片序列化
				if (orderCount == randBlocks.length) {
					completed = true;
				}
			}
		}
	}

	@Override
	public void onLoad() {
		// 最后绘制桌面组件
		lastDesktopDraw();
		// 瓦片大小
		tileWidth = 120;
		tileHeight = 80;
		// 以此万片全屏输出拼图需要的行列数
		rows = getWidth() / tileWidth;
		cols = getHeight() / tileHeight;
		// 瓦片总数
		int count = rows * cols;
		// 缩放纹理为屏幕大小，并且注销时无视子纹理使用情况,强制删除纹理
		texture = loadTexture("backimage.jpg").scale(getWidth(), getHeight()).setForcedDelete(true);

		textures = new LTexture[count];
		initBlocks = new IntArray(count);
		// 注入拼图索引到initBlocks
		int idx = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				textures[idx] = texture.copy(i * tileWidth, j * tileHeight, tileWidth, tileHeight);
				initBlocks.add(idx);
				idx++;
			}
		}
		// 获得拼图的随机索引
		randBlocks = initBlocks.randomIntArray();
		// 注销Screen时注销纹理
		putRelease(texture);

		add(MultiScreenTest.getBackButton(this, 1));
	}

	@Override
	public void alter(LTimerContext timer) {
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void touchDown(GameTouch e) {
	}

	private int getIndex(int x, int y) {
		return y * rows + x;
	}

	@Override
	public void touchUp(GameTouch e) {
		if (completed) {
			return;
		}
		int old = selectIndex;
		int x = e.getTileX(tileWidth);
		int y = e.getTileY(tileHeight);
		// 获得当前点击区域索引
		selectIndex = getIndex(x, y);
		// 如果点击了已选区域
		if (old == selectIndex) {
			selectIndex = -1;
		} else if (selectIndex != -1 && old != -1) { // 点击了新的区域
			// 获得临近区域索引
			int left = getIndex(x - 1, y);
			int right = getIndex(x + 1, y);
			int up = getIndex(x, y - 1);
			int down = getIndex(x, y + 1);
			// 如果新选中项上下左右中任意一格索引与旧选中索引等值
			if (old == left || old == right || old == up || old == down) {
				// 交换图片
				randBlocks.swap(old, selectIndex);
			}
		}
	}

	@Override
	public void touchMove(GameTouch e) {

	}

	@Override
	public void touchDrag(GameTouch e) {

	}

	@Override
	public void resume() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void close() {
	}

}
