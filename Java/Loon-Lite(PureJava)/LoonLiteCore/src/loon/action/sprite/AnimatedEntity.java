/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.action.sprite;

import loon.LTexture;
import loon.events.FrameListener;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.StringUtils;
import loon.utils.timer.Duration;

/**
 * Entity类的动画播放扩展类,相比单纯使用Sprite类,此类更容易进行具体帧的临时播放和设置参数变更,以及监听.
 */
public class AnimatedEntity extends Entity {

	/**
	 * 动画具体播放速度值及帧索引存储用类
	 *
	 */
	public static class PlayIndex {

		public final static PlayIndex at(int[] animates) {
			return at(220, animates);
		}

		public final static PlayIndex at(long frameTime, int[] animates) {
			if (animates == null) {
				return null;
			}
			int size = animates.length;
			long[] frames = new long[size];
			for (int i = 0; i < size; i++) {
				frames[i] = frameTime;
			}
			return create(frames, animates);
		}

		public final static PlayIndex at(long frameTime, int startIdx, int endIdx) {
			startIdx = MathUtils.abs(startIdx);
			endIdx = MathUtils.abs(endIdx);
			int size = endIdx - startIdx + 1;
			long[] frames = new long[size];
			for (int i = 0; i < size; i++) {
				frames[i] = frameTime;
			}
			return at(frames, startIdx, endIdx);
		}

		public final static PlayIndex at(long[] frames, int startIdx, int endIdx) {
			startIdx = MathUtils.abs(startIdx);
			endIdx = MathUtils.abs(endIdx);
			int size = endIdx - startIdx + 1;
			int[] animateIndexs = null;
			if (size <= 0) {
				animateIndexs = new int[2];
				animateIndexs[0] = startIdx;
				animateIndexs[1] = endIdx;
			} else {
				animateIndexs = new int[size];
				for (int i = 0; i < size; i++) {
					int idx = startIdx + i;
					animateIndexs[i] = idx;
				}
			}
			return create(frames, animateIndexs);
		}

		public final static PlayIndex at(long[] frames, int[] animates) {
			return create(frames, animates);
		}

		public final static PlayIndex create(long[] frames, int[] animates) {
			return new PlayIndex(frames, animates);
		}

		final long[] frames;

		final int[] animates;

		public PlayIndex(long[] frame, int[] ani) {
			this.frames = CollectionUtils.copyOf(frame);
			this.animates = CollectionUtils.copyOf(ani);
		}

		public long[] getFrames() {
			return CollectionUtils.copyOf(frames);
		}

		public int[] getAnimates() {
			return CollectionUtils.copyOf(animates);
		}

	}

	private final ObjectMap<String, PlayIndex> _playEvents;

	public static interface IAnimationListener {

		public void onAnimationStarted(final AnimatedEntity animatedSprite, final int initialLoopCount);

		public void onAnimationFrameChanged(final AnimatedEntity animatedSprite, final int oldFrameIndex,
				final int newFrameIndex);

		public void onAnimationLoopFinished(final AnimatedEntity animatedSprite, final int remainingLoopCount,
				final int initialLoopCount);

		public void onAnimationFinished(final AnimatedEntity animatedSprite);
	}

	private static final int FRAMEINDEX_INVALID = -1;

	private String _currentAnimateName;

	private boolean _animationRunning;
	private boolean _animationStartedFired;
	private boolean _animationDispose;

	private int _currentFrameIndex;
	private long _animationProgress;
	private int _remainingLoopCount;
	private Animation _animation;

	private final AnimationData _animationData = new AnimationData();
	private IAnimationListener _animationListener;

	public AnimatedEntity(final String[] paths, final int maxFrames, final float x, final float y, final float width,
			final float height) {
		this(Animation.getDefaultAnimation(paths, maxFrames, 0), x, y, width, height);
	}

	public AnimatedEntity(final LTexture[] texs, final int maxFrames, final float x, final float y, final float width,
			final float height) {
		this(Animation.getDefaultAnimation(texs, maxFrames, 0), x, y, width, height);
	}

	public AnimatedEntity(final String path, final int maxFrames, final int frameWidth, final int frameHeight,
			final float x, final float y, final float width, final float height) {
		this(Animation.getDefaultAnimation(path, maxFrames, frameWidth, frameHeight, 0), x, y, width, height);
	}

	public AnimatedEntity(final String path, final int frameWidth, final int frameHeight, final float x, final float y,
			final float width, final float height) {
		this(Animation.getDefaultAnimation(path, frameWidth, frameHeight, 10), x, y, width, height);
	}

	public AnimatedEntity(final Animation ani, final float x, final float y, final float width, final float height) {
		super(ani.getSpriteImage());
		this.setLocation(x, y);
		this.setSize(width, height);
		this._playEvents = new ObjectMap<String, AnimatedEntity.PlayIndex>(8);
		this._animation = ani;
		this._animationDispose = (this._animation != null);
	}

	@Override
	void onProcess(final long elapsedTime) {
		if (this._animationRunning) {

			final int loopCount = this._animationData.getLoopCount();
			final int[] frames = this._animationData.getFrames();
			final long animationDuration = this._animationData.getAnimationDuration();

			if (!this._animationStartedFired && (this._animationProgress == 0)) {
				this._animationStartedFired = true;
				if (frames == null) {
					this.setCurrentFrameIndex(this._animationData.getFirstFrameIndex());
				} else {
					this.setCurrentFrameIndex(frames[0]);
				}
				this._currentFrameIndex = 0;
				if (this._animationListener != null) {
					this._animationListener.onAnimationStarted(this, loopCount);
					this._animationListener.onAnimationFrameChanged(this, AnimatedEntity.FRAMEINDEX_INVALID, 0);
				}
			}
			this._animationProgress += Duration.ofS(elapsedTime);
			if (loopCount == AnimationData.LOOP_CONTINUOUS) {
				while (this._animationProgress > animationDuration) {
					this._animationProgress -= animationDuration;
					if (this._animationListener != null) {
						this._animationListener.onAnimationLoopFinished(this, this._remainingLoopCount, loopCount);
					}
				}
			} else {

				while (this._animationProgress > animationDuration) {
					this._animationProgress -= animationDuration;
					this._remainingLoopCount--;
					if (this._remainingLoopCount < 0) {
						break;
					} else if (this._animationListener != null) {
						this._animationListener.onAnimationLoopFinished(this, this._remainingLoopCount, loopCount);
					}
				}
			}

			if ((loopCount == AnimationData.LOOP_CONTINUOUS) || (this._remainingLoopCount >= 0)) {
				final int newFrameIndex = this._animationData.calculateCurrentFrameIndex(this._animationProgress);

				if (this._currentFrameIndex != newFrameIndex) {
					if (frames == null) {
						this.setCurrentFrameIndex(this._animationData.getFirstFrameIndex() + newFrameIndex);
					} else {
						this.setCurrentFrameIndex(frames[newFrameIndex]);
					}
					if (this._animationListener != null) {
						this._animationListener.onAnimationFrameChanged(this, this._currentFrameIndex, newFrameIndex);
					}
				}
				this._currentFrameIndex = newFrameIndex;
			} else {
				this._animationRunning = false;
				if (this._animationListener != null) {
					this._animationListener.onAnimationFinished(this);
				}
			}

			setTexture(_animation.getSpriteImage());

		}
	}

	public Animation getAnimation() {
		return this._animation;
	}

	public AnimatedEntity playAnimation() {
		this._animationRunning = true;
		return this;
	}

	public AnimatedEntity stopAnimation() {
		this._animationRunning = false;
		return this;
	}

	public AnimatedEntity stopAnimation(final int index) {
		this._animationRunning = false;
		this.setCurrentFrameIndex(index);
		return this;
	}

	public AnimatedEntity animate(final long frameDurationEach) {
		this.animate(frameDurationEach, null);
		return this;
	}

	public AnimatedEntity animate(final long frameDurationEach, final IAnimationListener animationListener) {
		this._animationData.set(frameDurationEach, this.getCount());
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity animate(final long frameDurationEach, final boolean loop) {
		this.animate(frameDurationEach, loop, null);
		return this;
	}

	public AnimatedEntity animate(final long frameDurationEach, final boolean loop,
			final IAnimationListener animationListener) {
		this._animationData.set(frameDurationEach, this.getCount(), loop);
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity animate(final long frameDurationEach, final int loopCount) {
		this.animate(frameDurationEach, loopCount, null);
		return this;
	}

	public AnimatedEntity animate(final long frameDurationEach, final int loopCount,
			final IAnimationListener animationListener) {
		this._animationData.set(frameDurationEach, this.getCount(), loopCount);
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity play(final String key) {
		return animate(key);
	}

	public AnimatedEntity play(final String key, final IAnimationListener animationListener) {
		return animate(key, animationListener);
	}

	public AnimatedEntity play(final String key, final boolean loop) {
		return animate(key, loop);
	}

	public AnimatedEntity play(final String key, final boolean loop, final IAnimationListener animationListener) {
		return animate(key, loop, animationListener);
	}

	public AnimatedEntity play(final String key, final int firstFrameIndex, final int lastFrameIndex,
			final boolean loop) {
		return animate(key, firstFrameIndex, lastFrameIndex, loop);
	}

	public AnimatedEntity play(final String key, final int firstFrameIndex, final int lastFrameIndex,
			final boolean loop, final IAnimationListener animationListener) {
		return animate(key, firstFrameIndex, lastFrameIndex, loop, animationListener);
	}

	public AnimatedEntity play(final String key, final int firstFrameIndex, final int lastFrameIndex,
			final int loopCount) {
		return animate(key, firstFrameIndex, lastFrameIndex, loopCount);
	}

	public AnimatedEntity play(final String key, final int firstFrameIndex, final int lastFrameIndex,
			final int loopCount, final IAnimationListener animationListener) {
		return animate(key, firstFrameIndex, lastFrameIndex, loopCount, animationListener);
	}

	public AnimatedEntity animate(final String key) {
		final PlayIndex play = _playEvents.get(key);
		if (play != null) {
			this._currentAnimateName = key;
			this.animate(play.frames, play.animates);
		}
		return this;
	}

	public AnimatedEntity animate(final String key, final IAnimationListener animationListener) {
		final PlayIndex play = _playEvents.get(key);
		if (play != null) {
			this._currentAnimateName = key;
			this.animate(play.frames, play.animates, animationListener);
		}
		return this;
	}

	public AnimatedEntity animate(final String key, final boolean loop) {
		final PlayIndex play = _playEvents.get(key);
		if (play != null) {
			this._currentAnimateName = key;
			this.animate(play.frames, play.animates, loop);
		}
		return this;
	}

	public AnimatedEntity animate(final String key, final boolean loop, final IAnimationListener animationListener) {
		final PlayIndex play = _playEvents.get(key);
		if (play != null) {
			this._currentAnimateName = key;
			this.animate(play.frames, play.animates, loop, animationListener);
		}
		return this;
	}

	public AnimatedEntity animate(final String key, final int firstFrameIndex, final int lastFrameIndex,
			final boolean loop) {
		final PlayIndex play = _playEvents.get(key);
		if (play != null) {
			this._currentAnimateName = key;
			this.animate(play.frames, firstFrameIndex, lastFrameIndex, loop);
		}
		return this;
	}

	public AnimatedEntity animate(final String key, final int firstFrameIndex, final int lastFrameIndex,
			final boolean loop, final IAnimationListener animationListener) {
		final PlayIndex play = _playEvents.get(key);
		if (play != null) {
			this._currentAnimateName = key;
			this.animate(play.frames, firstFrameIndex, lastFrameIndex, loop, animationListener);
		}
		return this;
	}

	public AnimatedEntity animate(final String key, final int firstFrameIndex, final int lastFrameIndex,
			final int loopCount) {
		final PlayIndex play = _playEvents.get(key);
		if (play != null) {
			this._currentAnimateName = key;
			this.animate(play.frames, firstFrameIndex, lastFrameIndex, loopCount);
		}
		return this;
	}

	public AnimatedEntity animate(final String key, final int firstFrameIndex, final int lastFrameIndex,
			final int loopCount, final IAnimationListener animationListener) {
		final PlayIndex play = _playEvents.get(key);
		if (play != null) {
			this._currentAnimateName = key;
			this.animate(play.frames, firstFrameIndex, lastFrameIndex, loopCount, animationListener);
		}
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations) {
		this.animate(frameDurations, (IAnimationListener) null);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final IAnimationListener animationListener) {
		this._animationData.set(frameDurations);
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final boolean loop) {
		this.animate(frameDurations, loop, null);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final boolean loop,
			final IAnimationListener animationListener) {
		this._animationData.set(frameDurations, loop);
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int loopCount) {
		this.animate(frameDurations, loopCount, null);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int loopCount,
			final IAnimationListener animationListener) {
		this._animationData.set(frameDurations, loopCount);
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int firstFrameIndex, final int lastFrameIndex,
			final boolean loop) {
		this.animate(frameDurations, firstFrameIndex, lastFrameIndex, loop, null);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int firstFrameIndex, final int lastFrameIndex,
			final boolean loop, final IAnimationListener animationListener) {
		this._animationData.set(frameDurations, firstFrameIndex, lastFrameIndex, loop);
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int firstFrameIndex, final int lastFrameIndex,
			final int loopCount) {
		this.animate(frameDurations, firstFrameIndex, lastFrameIndex, loopCount, null);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int firstFrameIndex, final int lastFrameIndex,
			final int loopCount, final IAnimationListener animationListener) {
		this._animationData.set(frameDurations, firstFrameIndex, lastFrameIndex, loopCount);
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int[] frames) {
		this.animate(frameDurations, frames, null);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int[] frames,
			final IAnimationListener animationListener) {
		this._animationData.set(frameDurations, frames);
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int[] frames, final boolean loop) {
		this.animate(frameDurations, frames, loop, null);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int[] frames, final boolean loop,
			final IAnimationListener animationListener) {
		this._animationData.set(frameDurations, frames, loop);
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int[] frames, final int loopCount) {
		this.animate(frameDurations, frames, loopCount, null);
		return this;
	}

	public AnimatedEntity animate(final long[] frameDurations, final int[] frames, final int loopCount,
			final IAnimationListener animationListener) {
		this._animationData.set(frameDurations, frames, loopCount);
		this.initAnimation(animationListener);
		return this;
	}

	public AnimatedEntity animate(final AnimationData animationData) {
		this.animate(animationData, null);
		return this;
	}

	public AnimatedEntity animate(final AnimationData animationData, final IAnimationListener animationListener) {
		this._animationData.set(animationData);
		this.initAnimation(animationListener);
		return this;
	}

	private void initAnimation(final IAnimationListener animationListener) {
		this._animationStartedFired = false;
		this._animationListener = animationListener;
		this._remainingLoopCount = this._animationData.getLoopCount();
		this._animationProgress = 0;
		this._animationRunning = true;
	}

	public boolean isAnimationRunning() {
		return this._animationRunning;
	}

	@Override
	public AnimatedEntity pause() {
		super.pause();
		this.stopAnimation();
		return this;
	}

	@Override
	public AnimatedEntity resume() {
		super.resume();
		this.playAnimation();
		return this;
	}

	public AnimatedEntity setDelay(long d) {
		_animation.setDelay(d);
		return this;
	}

	public AnimatedEntity setDelayS(float s) {
		_animation.setDelayS(s);
		return this;
	}

	public AnimatedEntity setCurrentFrameIndex(int idx) {
		_animation.setCurrentFrameIndex(idx);
		return this;
	}

	public long getDelay() {
		return _animation.getDelay();
	}

	public float getDelayS() {
		return _animation.getDelayS();
	}

	public int getCount() {
		return _animation.length;
	}

	public AnimatedEntity setMaxFrame(int max) {
		_animation.setMaxFrame(max);
		return this;
	}

	public int getMaxFrame() {
		return _animation.getMaxFrame();
	}

	public AnimatedEntity setPlayIndex(final String key, final PlayIndex play) {
		_playEvents.put(key, play);
		return this;
	}

	public AnimatedEntity setPlayIndex(final String key, int[] animates) {
		return setPlayIndex(key, PlayIndex.at(animates));
	}

	public AnimatedEntity setPlayIndex(final String key, long frameTime, int[] animates) {
		return setPlayIndex(key, PlayIndex.at(frameTime, animates));
	}

	public AnimatedEntity setPlayIndex(final String key, long frameTime, int startIdx, int endIdx) {
		return setPlayIndex(key, PlayIndex.at(frameTime, startIdx, endIdx));
	}

	public AnimatedEntity setPlayIndex(final String key, long[] frameTime, int startIdx, int endIdx) {
		return setPlayIndex(key, PlayIndex.at(frameTime, startIdx, endIdx));
	}

	public PlayIndex getPlayIndex(final String key) {
		return _playEvents.get(key);
	}

	public PlayIndex removePlayIndex(final String key) {
		return _playEvents.remove(key);
	}

	public boolean containsPlayIndex(final String key) {
		return _playEvents.containsKey(key);
	}

	public String getCurrentAnimateName() {
		return this._currentAnimateName;
	}

	public boolean isCurrentAnimateName(final String key) {
		if (StringUtils.isNullOrEmpty(key)) {
			return false;
		}
		return key.equals(this._currentAnimateName);
	}

	public AnimatedEntity clearPlayEvents() {
		_playEvents.clear();
		return this;
	}

	@Override
	public int size() {
		return _playEvents.size;
	}

	public boolean isAnimationDispose() {
		return _animationDispose;
	}

	public AnimatedEntity setAnimationDispose(boolean dispose) {
		this._animationDispose = dispose;
		return this;
	}

	public FrameListener getFrameListener() {
		return _animation.getFrameListener();
	}

	public AnimatedEntity setFrameListener(FrameListener listener) {
		this._animation.setFrameListener(listener);
		return this;
	}

	@Override
	public void close() {
		super.close();
		this.stopAnimation();
		this.clearPlayEvents();
		if (_animationDispose && _animation != null) {
			_animation.close();
		}
	}

}
