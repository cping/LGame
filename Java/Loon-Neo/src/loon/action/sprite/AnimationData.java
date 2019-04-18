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

import java.util.Arrays;

import loon.LSysException;
import loon.LSystem;
import loon.utils.MathUtils;

/**
 * 一个Animation播放序列的存储器，需要与Animation或Sprite合用才能生效
 */
public class AnimationData {

	public static final int LOOP_CONTINUOUS = -1;
	private int _frameCount;
	private int[] _frames;
	private long[] _frameDurations;
	private int _firstFrameIndex;

	private int _loopCount;
	private long[] _frameEndsInNanoseconds;

	private long _animationDuration;

	public AnimationData() {
		this(new long[] { 0, 0 });
	}

	public AnimationData(final long frameDurationEach, final int frameCount) {
		this.set(frameDurationEach, frameCount);
	}

	public AnimationData(final long frameDurationEach, final int frameCount,
			final boolean loop) {
		this.set(frameDurationEach, frameCount, loop);
	}

	public AnimationData(final long frameDurationEach, final int frameCount,
			final int loopCount) {
		this.set(frameDurationEach, frameCount, loopCount);
	}

	public AnimationData(final long[] frameDurations) {
		this.set(frameDurations);
	}

	public AnimationData(final long[] frameDurations, final boolean loop) {
		this.set(frameDurations, loop);
	}

	public AnimationData(final long[] frameDurations, final int loopCount) {
		this.set(frameDurations, loopCount);
	}

	public AnimationData(final long[] frameDurations,
			final int firstFrameIndex, final int lastFrameIndex,
			final boolean loop) {
		this.set(frameDurations, firstFrameIndex, lastFrameIndex, loop);
	}

	public AnimationData(final long[] frameDurations, final int[] frames,
			final int loopCount) {
		this.set(frameDurations, frames, loopCount);
	}

	public AnimationData(final long[] frameDurations,
			final int firstFrameIndex, final int lastFrameIndex,
			final int loopCount) {
		this.set(frameDurations, firstFrameIndex, lastFrameIndex, loopCount);
	}

	public AnimationData(final AnimationData animationData) {
		this.set(animationData);
	}

	public int[] getFrames() {
		return this._frames;
	}

	public long[] getFrameDurations() {
		return this._frameDurations;
	}

	public int getLoopCount() {
		return this._loopCount;
	}

	public int getFrameCount() {
		return this._frameCount;
	}

	public int getFirstFrameIndex() {
		return this._firstFrameIndex;
	}

	public long getAnimationDuration() {
		return this._animationDuration;
	}

	public int calculateCurrentFrameIndex(final long animationProgress) {
		final long[] frameEnds = this._frameEndsInNanoseconds;
		final int frameCount = this._frameCount;
		for (int i = 0; i < frameCount; i++) {
			if (frameEnds[i] > animationProgress) {
				return i;
			}
		}
		return frameCount - 1;
	}

	public void set(final long frameDurationEach, final int frameCount) {
		this.set(frameDurationEach, frameCount, true);
	}

	public void set(final long frameDurationEach, final int frameCount,
			final boolean loop) {
		this.set(frameDurationEach, frameCount,
				(loop) ? AnimationData.LOOP_CONTINUOUS : 0);
	}

	public void set(final long frameDurationEach, final int frameCount,
			final int loopCount) {
		this.set(
				AnimationData.fillFrameDurations(frameDurationEach, frameCount),
				loopCount);
	}

	public void set(final long[] frameDurations) {
		this.set(frameDurations, true);
	}

	public void set(final long[] frameDurations, final boolean loop) {
		this.set(frameDurations, (loop) ? AnimationData.LOOP_CONTINUOUS : 0);
	}

	public void set(final long[] frameDurations, final int loopCount) {
		this.set(frameDurations, 0, frameDurations.length - 1, loopCount);
	}

	public void set(final long[] frameDurations, final int firstFrameIndex,
			final int lastFrameIndex) {
		this.set(frameDurations, firstFrameIndex, lastFrameIndex, true);
	}

	public void set(final long[] frameDurations, final int firstFrameIndex,
			final int lastFrameIndex, final boolean loop) {
		this.set(frameDurations, firstFrameIndex, lastFrameIndex,
				(loop) ? AnimationData.LOOP_CONTINUOUS : 0);
	}

	public void set(final long[] frameDurations, final int firstFrameIndex,
			final int lastFrameIndex, final int loopCount) {
		this.set(frameDurations, (lastFrameIndex - firstFrameIndex) + 1, null,
				firstFrameIndex, loopCount);

		if ((firstFrameIndex + 1) > lastFrameIndex) {
			throw new LSysException(
					"An animation needs at least two tiles to animate between.");
		}
	}

	public void set(final long[] frameDurations, final int[] frames) {
		this.set(frameDurations, frames, true);
	}

	public void set(final long[] frameDurations, final int[] frames,
			final boolean loop) {
		this.set(frameDurations, frames, (loop) ? AnimationData.LOOP_CONTINUOUS
				: 0);
	}

	public void set(final long[] frameDurations, final int[] frames,
			final int loopCount) {
		this.set(frameDurations, frames.length, frames, 0, loopCount);
	}

	public void set(final AnimationData animationData) {
		this.set(animationData.getFrameDurations(),
				animationData.getFrameCount(), animationData.getFrames(),
				animationData.getFirstFrameIndex(),
				animationData.getLoopCount());
	}

	private void set(final long[] frameDurations, final int frameCount,
			final int[] frames, final int firstFrameIndex, final int loopCount) {
		if (frameDurations.length != frameCount) {
			throw new LSysException(
					"frameDurations does not equal frameCount!");
		}

		this._frameDurations = frameDurations;
		this._frameCount = frameCount;
		this._frames = frames;
		this._firstFrameIndex = firstFrameIndex;
		this._loopCount = loopCount;

		if ((this._frameEndsInNanoseconds == null)
				|| (this._frameCount > this._frameEndsInNanoseconds.length)) {
			this._frameEndsInNanoseconds = new long[this._frameCount];
		}
		final long[] frameEndsInNanoseconds = this._frameEndsInNanoseconds;
		MathUtils.arraySumInto(this._frameDurations, frameEndsInNanoseconds,
				LSystem.SECOND);

		final long lastFrameEnd = frameEndsInNanoseconds[this._frameCount - 1];
		this._animationDuration = lastFrameEnd;
	}

	private static long[] fillFrameDurations(final long frameDurationEach,
			final int frameCount) {
		final long[] frameDurations = new long[frameCount];
		Arrays.fill(frameDurations, frameDurationEach);
		return frameDurations;
	}

	public AnimationData cpy() {
		return new AnimationData(this);
	}

}
