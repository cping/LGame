package loon.action.sprite;

import java.util.ArrayList;

import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.event.Updateable;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
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
public class Animation implements LRelease {

	public interface AnimationListener {
		public void onComplete(Animation animation);
	}

	public AnimationListener Listener;

	public void setAnimationListener(AnimationListener l) {
		this.Listener = l;
	}

	public AnimationListener getAnimationListener() {
		return this.Listener;
	}

	boolean isRunning;

	private ArrayList<AnimationFrame> frames;

	int loopCount, loopPlay;

	int currentFrameIndex;

	long animTime, totalDuration;

	int size;

	public Animation() {
		this(new ArrayList<AnimationFrame>(CollectionUtils.INITIAL_CAPACITY), 0);
	}

	public Animation(Animation a) {
		this.isRunning = a.isRunning;
		this.frames = new ArrayList<Animation.AnimationFrame>(a.frames);
		this.loopCount = a.loopCount;
		this.loopPlay = a.loopPlay;
		this.currentFrameIndex = a.currentFrameIndex;
		this.animTime = a.animTime;
		this.totalDuration = a.totalDuration;
		this.size = frames.size();
	}

	private Animation(ArrayList<AnimationFrame> frames, long totalDuration) {
		this.loopCount = -1;
		this.frames = frames;
		this.size = frames.size();
		this.totalDuration = totalDuration;
		this.isRunning = true;
		start();
	}

	public static Animation getDefaultAnimation(String fileName) {
		return Animation.getDefaultAnimation(LTextures.loadTexture(fileName));
	}

	public static Animation getDefaultAnimation(final LTexture texture) {
		return Animation.getDefaultAnimation(new LTexture[] { texture }, 1,
				65535);
	}

	/**
	 * 转化指定文件为动画图像
	 * 
	 * @param fileName
	 * @param width
	 * @param height
	 * @param timer
	 * @return
	 */
	public static Animation getDefaultAnimation(String fileName, int width,
			int height, int timer) {
		return Animation.getDefaultAnimation(
				TextureUtils.getSplitTextures(fileName, width, height), -1,
				timer);
	}

	/**
	 * 转化指定文件为动画图像
	 * 
	 * @param fileName
	 * @param width
	 * @param height
	 * @param timer
	 * @param filterColor
	 * @return
	 */
	public static Animation getDefaultAnimation(String fileName, int width,
			int height, int timer, LColor filterColor) {
		return Animation.getDefaultAnimation(
				TextureUtils.getSplitTextures(
						TextureUtils.filterColor(fileName, filterColor), width,
						height), -1, timer);
	}

	/**
	 * 转化指定文件为动画图像
	 * 
	 * @param fileName
	 * @param maxFrame
	 * @param width
	 * @param height
	 * @param timer
	 * @return
	 */
	public static Animation getDefaultAnimation(String fileName, int maxFrame,
			int width, int height, int timer) {
		return Animation.getDefaultAnimation(
				TextureUtils.getSplitTextures(fileName, width, height),
				maxFrame, timer);
	}

	/**
	 * 转化一组Image为动画图像
	 * 
	 * @param images
	 * @param maxFrame
	 * @param width
	 * @param height
	 * @param timer
	 * @return
	 */
	public static Animation getDefaultAnimation(LTexture[] images,
			int maxFrame, int timer) {
		if (images == null) {
			return new Animation();
		}
		Animation animation = new Animation();
		if (maxFrame != -1) {
			for (int i = 0; i < maxFrame; i++) {
				animation.addFrame(images[i], timer);
			}
		} else {
			int size = images.length;
			for (int i = 0; i < size; i++) {
				animation.addFrame(images[i], timer);
			}
		}
		return animation;
	}

	/**
	 * 克隆一个独立动画
	 */
	@Override
	public Object clone() {
		return new Animation(frames, totalDuration);
	}

	/**
	 * 添加一个动画图像
	 * 
	 * @param image
	 * @param timer
	 */
	public synchronized void addFrame(LTexture image, long timer) {
		totalDuration += timer;
		frames.add(new AnimationFrame(image, totalDuration));
		size++;
	}

	/**
	 * 添加一个动画图像
	 * 
	 * @param fileName
	 * @param timer
	 */
	public synchronized void addFrame(String fileName, long timer) {
		addFrame(LTextures.loadTexture(fileName), timer);
	}

	/**
	 * 开始执行动画
	 * 
	 */
	public synchronized void start() {
		animTime = 0;
		if (size > 0) {
			currentFrameIndex = 0;
		}
	}

	/**
	 * 刷新动画为初始状态
	 */
	public void reset() {
		animTime = 0;
		currentFrameIndex = 0;
		loopPlay = 0;
		loopCount = -1;
		isRunning = true;
	}

	/**
	 * 更新当前动画
	 * 
	 * @param timer
	 */
	public synchronized void update(long timer) {
		if (loopCount != -1 && loopPlay > loopCount) {
			return;
		}
		if (isRunning) {
			if (size > 0) {
				animTime += timer;
				if (animTime > totalDuration) {
					if (Listener != null) {
						Listener.onComplete(this);
					}
					animTime = animTime % totalDuration;
					currentFrameIndex = 0;
					loopPlay++;
				}
				for (; animTime > getFrame(currentFrameIndex).endTimer;) {
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
	public LTexture getSpriteImage() {
		if (size == 0) {
			return null;
		} else {
			final LTexture texture = getFrame(currentFrameIndex).image;
			if (!texture.isLoaded()) {
				Updateable update = new Updateable() {
					@Override
					public void action() {
						texture.loadTexture();
					}
				};
				LSystem.load(update);
			}
			return texture;
		}
	}

	/**
	 * 返回当前动画图象
	 * 
	 * @param index
	 * @return
	 */
	public LTexture getSpriteImage(int index) {
		if (index < 0 || index >= size) {
			return null;
		} else {
			LTexture texture = getFrame(index).image;
			if (!texture.isLoaded()) {
				texture.loadTexture();
			}
			return texture;
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
			return frames.get(0);
		} else if (index >= size) {
			return frames.get(size - 1);
		}
		return frames.get(index);
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

	public void setCurrentFrameIndex(int index) {
		this.currentFrameIndex = index;
	}

	public int getTotalFrames() {
		return size;
	}

	public int getLoopCount() {
		return loopCount;
	}

	public void setLoopCount(int loopCount) {
		this.loopCount = loopCount;
	}

	private class AnimationFrame implements LRelease {

		LTexture image;

		long endTimer;

		public AnimationFrame(LTexture image, long endTimer) {
			this.image = image;
			this.endTimer = endTimer;
		}

		@Override
		public void dispose() {
			if (image != null) {
				LTexture father = image.getParent();
				if (father != null && !father.isClose()) {
					father.destroy();
				} else if (image != null && !image.isClose()) {
					image.destroy();
				}
			}
		}
	}

	@Override
	public void dispose() {
		if (frames != null) {
			for (AnimationFrame frame : frames) {
				if (frame != null) {
					frame.dispose();
					frame = null;
				}
			}
			frames.clear();
		}
		this.size = 0;
	}

}
