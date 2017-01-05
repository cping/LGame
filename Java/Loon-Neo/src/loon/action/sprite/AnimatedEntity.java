package loon.action.sprite;

import loon.LTexture;

/**
 * Entity类的动画播放扩展类,相比单纯使用Sprite类,此类更容易进行具体帧的临时播放和设置参数变更,以及监听.
 */
public class AnimatedEntity extends Entity {

	public static interface IAnimationListener {

		public void onAnimationStarted(final AnimatedEntity animatedSprite, final int initialLoopCount);

		public void onAnimationFrameChanged(final AnimatedEntity animatedSprite, final int oldFrameIndex,
				final int newFrameIndex);

		public void onAnimationLoopFinished(final AnimatedEntity animatedSprite, final int remainingLoopCount,
				final int initialLoopCount);

		public void onAnimationFinished(final AnimatedEntity animatedSprite);
	}

	private static final int FRAMEINDEX_INVALID = -1;

	private boolean _animationRunning;
	private boolean _animationStartedFired;

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
		this._animation = ani;
	}

	public boolean isAnimationRunning() {
		return this._animationRunning;
	}

	public void setCurrentFrameIndex(int idx) {
		_animation.setCurrentFrameIndex(idx);
	}

	public int getCount() {
		return _animation.size;
	}

	@Override
	public void onUpdate(final long elapsedTime) {
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
			this._animationProgress += (elapsedTime * 1000);
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

	public void stopAnimation() {
		this._animationRunning = false;
	}

	public void stopAnimation(final int index) {
		this._animationRunning = false;
		this.setCurrentFrameIndex(index);
	}

	public void animate(final long frameDurationEach) {
		this.animate(frameDurationEach, null);
	}

	public void animate(final long frameDurationEach, final IAnimationListener animationListener) {
		this._animationData.set(frameDurationEach, this.getCount());
		this.initAnimation(animationListener);
	}

	public void animate(final long frameDurationEach, final boolean loop) {
		this.animate(frameDurationEach, loop, null);
	}

	public void animate(final long frameDurationEach, final boolean loop, final IAnimationListener animationListener) {
		this._animationData.set(frameDurationEach, this.getCount(), loop);
		this.initAnimation(animationListener);
	}

	public void animate(final long frameDurationEach, final int loopCount) {
		this.animate(frameDurationEach, loopCount, null);
	}

	public void animate(final long frameDurationEach, final int loopCount, final IAnimationListener animationListener) {
		this._animationData.set(frameDurationEach, this.getCount(), loopCount);
		this.initAnimation(animationListener);
	}

	public void animate(final long[] frameDurations) {
		this.animate(frameDurations, (IAnimationListener) null);
	}

	public void animate(final long[] frameDurations, final IAnimationListener animationListener) {
		this._animationData.set(frameDurations);
		this.initAnimation(animationListener);
	}

	public void animate(final long[] frameDurations, final boolean loop) {
		this.animate(frameDurations, loop, null);
	}

	public void animate(final long[] frameDurations, final boolean loop, final IAnimationListener animationListener) {
		this._animationData.set(frameDurations, loop);
		this.initAnimation(animationListener);
	}

	public void animate(final long[] frameDurations, final int loopCount) {
		this.animate(frameDurations, loopCount, null);
	}

	public void animate(final long[] frameDurations, final int loopCount, final IAnimationListener animationListener) {
		this._animationData.set(frameDurations, loopCount);
		this.initAnimation(animationListener);
	}

	public void animate(final long[] frameDurations, final int firstTileIndex, final int lastTileIndex,
			final boolean loop) {
		this.animate(frameDurations, firstTileIndex, lastTileIndex, loop, null);
	}

	public void animate(final long[] frameDurations, final int firstTileIndex, final int lastTileIndex,
			final boolean loop, final IAnimationListener animationListener) {
		this._animationData.set(frameDurations, firstTileIndex, lastTileIndex, loop);
		this.initAnimation(animationListener);
	}

	public void animate(final long[] frameDurations, final int firstTileIndex, final int lastTileIndex,
			final int loopCount) {
		this.animate(frameDurations, firstTileIndex, lastTileIndex, loopCount, null);
	}

	public void animate(final long[] frameDurations, final int firstTileIndex, final int lastTileIndex,
			final int loopCount, final IAnimationListener animationListener) {
		this._animationData.set(frameDurations, firstTileIndex, lastTileIndex, loopCount);
		this.initAnimation(animationListener);
	}

	public void animate(final long[] frameDurations, final int[] frames) {
		this.animate(frameDurations, frames, null);
	}

	public void animate(final long[] frameDurations, final int[] frames, final IAnimationListener animationListener) {
		this._animationData.set(frameDurations, frames);

		this.initAnimation(animationListener);
	}

	public void animate(final long[] frameDurations, final int[] frames, final boolean loop) {
		this.animate(frameDurations, frames, loop, null);
	}

	public void animate(final long[] frameDurations, final int[] frames, final boolean loop,
			final IAnimationListener animationListener) {
		this._animationData.set(frameDurations, frames, loop);
		this.initAnimation(animationListener);
	}

	public void animate(final long[] frameDurations, final int[] frames, final int loopCount) {
		this.animate(frameDurations, frames, loopCount, null);
	}

	public void animate(final long[] frameDurations, final int[] frames, final int loopCount,
			final IAnimationListener animationListener) {
		this._animationData.set(frameDurations, frames, loopCount);
		this.initAnimation(animationListener);
	}

	public void animate(final AnimationData animationData) {
		this.animate(animationData, null);
	}

	public void animate(final AnimationData animationData, final IAnimationListener animationListener) {
		this._animationData.set(animationData);
		this.initAnimation(animationListener);
	}

	private void initAnimation(final IAnimationListener animationListener) {
		this._animationStartedFired = false;
		this._animationListener = animationListener;
		this._remainingLoopCount = this._animationData.getLoopCount();
		this._animationProgress = 0;
		this._animationRunning = true;
	}

}
