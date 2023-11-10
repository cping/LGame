/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.action.sprite;

import loon.LTexture;
import loon.utils.CollectionUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class AnimationStorage extends Animation {

	private int _animationIndexLocked = -1;

	private static class AnimationStorageListener implements AnimationListener {

		private AnimationStorage store;

		public AnimationStorageListener(AnimationStorage s) {
			this.store = s;
		}

		@Override
		public void onComplete(Animation animation) {
			if (store._animationIndexLocked != -1) {
				store.currentFrameIndex = store._animationIndexLocked;
			} else {
				if (store._loopOverToRemove) {
					if (store.listener != null) {
						store.listener.onComplete(store);
					}
					store._playAnimations.remove(animation);
					store.length = store._playAnimations.size;
					store.loopPlay++;
				} else {
					if (store.currentFrameIndex < store.length - 1) {
						if (store.listener != null) {
							store.listener.onComplete(store);
						}
						store.currentFrameIndex++;
						store.loopPlay++;
					} else {
						if (store._loopOverToPlay) {
							store.currentFrameIndex = 0;
						} else {
							store.currentFrameIndex = 0;
							store.isRunning = false;
						}
					}
				}
			}
		}

	}

	private boolean _loopOverToPlay;

	private boolean _loopOverToRemove;

	private AnimationStorageListener _asl;

	private TArray<Animation> _playAnimations;

	public AnimationStorage(TArray<Animation> f) {
		this._asl = new AnimationStorageListener(this);
		if (f != null) {
			_playAnimations = f;
		} else {
			_playAnimations = new TArray<Animation>(CollectionUtils.INITIAL_CAPACITY);
		}
		for (Animation a : _playAnimations) {
			if (a != null) {
				a.listener = _asl;
			}
		}
		this.length = _playAnimations.size;
		this._loopOverToPlay = true;
		this._loopOverToRemove = false;
	}

	public AnimationStorage() {
		this(new TArray<Animation>(CollectionUtils.INITIAL_CAPACITY));
	}

	@Override
	public AnimationStorage cpy() {
		return new AnimationStorage(_playAnimations);
	}

	public Animation findAnimation(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		for (Animation ani : _playAnimations) {
			if (ani != null && name.equals(ani.animationName)) {
				return ani;
			}
		}
		return null;
	}

	public Animation removeAnimation(Animation anm) {
		if (anm != null) {
			anm.listener = null;
			if (_playAnimations.remove(anm)) {
				length--;
			}
			isRunning = !_playAnimations.isEmpty();
		}
		return this;
	}

	public Animation addAnimation(Animation anm) {
		if (anm != null) {
			anm.listener = _asl;
			_playAnimations.add(anm);
			isRunning = true;
			length++;
		}
		return this;
	}

	@Override
	public void update(long timer) {
		if (loopCount != -1 && loopPlay > loopCount) {
			return;
		}
		if (isRunning) {
			if (currentFrameIndex > -1 && currentFrameIndex < length) {
				Animation animation = _playAnimations.get(currentFrameIndex);
				if (animation != null) {
					if (animation.isRunning) {
						animation.update(timer);
					}
				}
			}
		}
	}

	public Animation getAnimation(int idx) {
		if (currentFrameIndex > -1 && currentFrameIndex < length) {
			return _playAnimations.get(idx);
		} else {
			return null;
		}
	}

	public AnimationStorage playIndex(int idx) {
		if (currentFrameIndex > -1 && currentFrameIndex < length) {
			currentFrameIndex = idx;
			Animation animation = _playAnimations.get(currentFrameIndex);
			if (animation != null) {
				animation.reset();
			}
		}
		return this;
	}

	@Override
	public LTexture getSpriteImage() {
		if (currentFrameIndex > -1 && currentFrameIndex < length) {
			Animation animation = _playAnimations.get(currentFrameIndex);
			return animation.getSpriteImage(animation.currentFrameIndex);
		} else {
			return null;
		}
	}

	@Override
	public LTexture getSpriteImage(int idx) {
		if (currentFrameIndex > -1 && currentFrameIndex < length) {
			Animation animation = _playAnimations.get(currentFrameIndex);
			return animation.getSpriteImage(idx);
		} else {
			return null;
		}
	}

	public LTexture getSpriteImage(int animation, int idx) {
		if (currentFrameIndex > -1 && currentFrameIndex < length) {
			return _playAnimations.get(animation).getSpriteImage(idx);
		} else {
			return null;
		}
	}

	public int getIndexLocked() {
		return _animationIndexLocked;
	}

	public AnimationStorage indexLocked(int idx) {
		this._animationIndexLocked = idx;
		if (_animationIndexLocked > -1 && _animationIndexLocked < length) {
			this.currentFrameIndex = _animationIndexLocked;
			Animation animation = _playAnimations.get(currentFrameIndex);
			if (animation != null) {
				animation.reset();
			}
		}
		return this;
	}

	public boolean isLoopOverToRemove() {
		return _loopOverToRemove;
	}

	public AnimationStorage setLoopOverToRemove(boolean l) {
		_loopOverToRemove = l;
		return this;
	}

	public boolean isLoopPlay() {
		return _loopOverToPlay;
	}

	public AnimationStorage setLoopPlay(boolean l) {
		this._loopOverToPlay = l;
		return this;
	}

	@Override
	public Animation reset() {
		super.reset();
		_loopOverToPlay = true;
		_loopOverToRemove = false;
		return this;
	}

}
