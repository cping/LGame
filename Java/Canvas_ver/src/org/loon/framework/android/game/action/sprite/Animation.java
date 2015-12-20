package org.loon.framework.android.game.action.sprite;

import java.util.ArrayList;
import java.util.List;

import org.loon.framework.android.game.core.LRelease;
import org.loon.framework.android.game.core.graphics.LImage;
import org.loon.framework.android.game.utils.GraphicsUtils;

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
public class Animation implements LRelease {

	private float alpha;

	private boolean isRunning;

	private List<AnimationFrame> frames;

	private int currentFrameIndex;

	private long animTime, totalDuration;

	private SpriteImage sprImage = null;

	public Animation() {
		this(new ArrayList<AnimationFrame>(10), 0);
	}

	private Animation(List<AnimationFrame> frames, long totalDuration) {
		this.frames = frames;
		this.totalDuration = totalDuration;
		this.isRunning = true;
		start();
	}

	/**
	 * 转化指定文件为动画图像
	 * 
	 * @param fileName
	 * @param row
	 * @param col
	 * @param timer
	 * @return
	 */
	public static Animation getDefaultAnimation(String fileName, int row,
			int col, int timer) {
		return Animation.getDefaultAnimation(GraphicsUtils.getSplitImages(
				fileName, row, col, false), -1, timer);
	}

	/**
	 * 转化指定文件为动画图像
	 * 
	 * @param fileName
	 * @param maxFrame
	 * @param row
	 * @param col
	 * @param timer
	 * @return
	 */
	public static Animation getDefaultAnimation(String fileName, int maxFrame,
			int row, int col, int timer) {
		return Animation.getDefaultAnimation(GraphicsUtils.getSplitImages(
				fileName, row, col, false), maxFrame, timer);
	}

	/**
	 * 转化一组Image为动画图像
	 * 
	 * @param images
	 * @param maxFrame
	 * @param row
	 * @param col
	 * @param timer
	 * @return
	 */
	public static Animation getDefaultAnimation(LImage[] images, int maxFrame,
			int timer) {
		Animation animation = new Animation();
		if (maxFrame != -1) {
			for (int i = 0; i < maxFrame; i++) {
				animation.addFrame(new SpriteImage(images[i].getBitmap()),
						timer);
			}
		} else {
			for (int i = 0; i < images.length; i++) {
				animation.addFrame(new SpriteImage(images[i].getBitmap()),
						timer);
			}
		}
		return animation;
	}

	/**
	 * 克隆一个独立动画
	 */
	public Object clone() {
		return new Animation(frames, totalDuration);
	}

	/**
	 * 添加一个动画图像
	 * 
	 * @param image
	 * @param timer
	 */
	public synchronized void addFrame(SpriteImage image, long timer) {
		totalDuration += timer;
		frames.add(new AnimationFrame(image, totalDuration));
	}

	/**
	 * 添加一个动画图像
	 * 
	 * @param image
	 * @param timer
	 */
	public synchronized void addFrame(LImage image, long timer) {
		addFrame(new SpriteImage(image), timer);
	}

	/**
	 * 添加一个动画图像
	 * 
	 * @param fileName
	 * @param timer
	 */
	public synchronized void addFrame(String fileName, long timer) {
		addFrame(new SpriteImage(fileName), timer);
	}

	/**
	 * 开始执行动画
	 * 
	 */
	public synchronized void start() {
		animTime = 0;
		if (frames.size() > 0) {
			currentFrameIndex = 0;
		}
	}

	/**
	 * 更新当前动画
	 * 
	 * @param timer
	 */
	public synchronized void update(long timer) {
		if (isRunning) {
			if (frames.size() > 0) {
				animTime += timer;
				if (animTime > totalDuration) {
					animTime = animTime % totalDuration;
					currentFrameIndex = 0;
				}
				while (animTime > getFrame(currentFrameIndex).endTimer) {
					currentFrameIndex++;
				}
			}
		}
	}

	/**
	 * 返回当前动画图象
	 * 
	 * @return
	 */
	public SpriteImage getSpriteImage() {
		if (frames.size() == 0) {
			return sprImage;
		} else {
			if (alpha > 0) {
				sprImage = getFrame(currentFrameIndex).image;
			} else {
				sprImage = getFrame(currentFrameIndex).image;
			}
			return sprImage;
		}
	}

	/**
	 * 返回当前动画图象
	 * 
	 * @param index
	 * @return
	 */
	public SpriteImage getSpriteImage(int index) {
		if (index < 0 || index >= frames.size()) {
			return sprImage;
		} else {
			if (alpha > 0) {
				sprImage = getFrame(index).image;
			} else {
				sprImage = getFrame(index).image;
			}
			return sprImage;
		}
	}

	/**
	 * 返回当前动画面板
	 * 
	 * @param i
	 * @return
	 */
	private AnimationFrame getFrame(int index) {
		if (index < 0) {
			return (AnimationFrame) frames.get(0);
		} else if (index >= frames.size()) {
			return (AnimationFrame) frames.get(frames.size() - 1);
		}
		return (AnimationFrame) frames.get(index);
	}

	/**
	 * 设定停止状态
	 * 
	 * @param isStop
	 */
	public void setRunning(boolean runing) {
		this.isRunning = runing;
	}

	/**
	 * 返回动画状态
	 * 
	 * @param isStop
	 */
	public boolean isRunning() {
		return this.isRunning;
	}

	/**
	 * 返回当前动画索引
	 * 
	 * @return
	 */
	public int getCurrentFrameIndex() {
		return this.currentFrameIndex;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public void setCurrentFrameIndex(int index) {
		this.currentFrameIndex = index;
	}

	public int getTotalFrames() {
		return frames.size();
	}

	private class AnimationFrame {

		SpriteImage image;

		long endTimer;

		public AnimationFrame(SpriteImage image, long endTimer) {
			this.image = image;
			this.endTimer = endTimer;
		}
	}

	public void dispose() {
		if (frames != null) {
			frames.clear();
			frames = null;
		}
		if (sprImage != null) {
			sprImage.dispose();
			sprImage = null;
		}
	}

}
