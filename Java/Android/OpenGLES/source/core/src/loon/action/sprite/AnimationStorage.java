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

import java.util.ArrayList;

import loon.core.graphics.opengl.LTexture;
import loon.utils.CollectionUtils;


public class AnimationStorage extends Animation {

	private int animationIndexLocked = -1;

	private class AnimationStorageListener implements AnimationListener {

		private AnimationStorage store;

		public AnimationStorageListener(AnimationStorage s) {
			this.store = s;
		}

		@Override
		public void onComplete(Animation animation) {
			if (store.animationIndexLocked != -1) {
				store.currentFrameIndex = store.animationIndexLocked;
			} else {
				if (store.loopOverToRemove) {
					if (Listener != null) {
						Listener.onComplete(store);
					}
					store.playAnimations.remove(animation);
					store.size = store.playAnimations.size();
					store.loopPlay++;
				} else {
					if (currentFrameIndex < size - 1) {
						if (Listener != null) {
							Listener.onComplete(store);
						}
						store.currentFrameIndex++;
						store.loopPlay++;
					} else {
						if (loopOverToPlay) {
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

	private boolean loopOverToPlay;

	private boolean loopOverToRemove;

	private AnimationStorageListener asl;

	private ArrayList<Animation> playAnimations;

	public AnimationStorage(ArrayList<Animation> f) {
		this.asl = new AnimationStorageListener(this);
		if (f != null) {
			playAnimations = f;
		} else {
			playAnimations = new ArrayList<Animation>(
					CollectionUtils.INITIAL_CAPACITY);
		}
		for (Animation a : playAnimations) {
			if (a != null) {
				a.Listener = asl;
			}
		}
		this.size = playAnimations.size();
		this.loopOverToPlay = true;
		this.loopOverToRemove = false;
	}

	public AnimationStorage() {
		this(new ArrayList<Animation>(CollectionUtils.INITIAL_CAPACITY));
	}

	@Override
	public Object clone() {
		return new AnimationStorage(playAnimations);
	}

	public synchronized void addAnimation(Animation anm) {
		if (anm != null) {
			anm.Listener = asl;
			playAnimations.add(anm);
			isRunning = true;
			size++;
		}
	}

	@Override
	public synchronized void update(long timer) {
		if (loopCount != -1 && loopPlay > loopCount) {
			return;
		}
		if (isRunning) {
			if (currentFrameIndex > -1 && currentFrameIndex < size) {
				Animation animation = playAnimations.get(currentFrameIndex);
				if (animation != null) {
					if (animation.isRunning) {
						animation.update(timer);
					}
				}
			}
		}
	}

	public Animation getAnimation(int idx) {
		if (currentFrameIndex > -1 && currentFrameIndex < size) {
			return playAnimations.get(idx);
		} else {
			return null;
		}
	}

	public void playIndex(int idx) {
		if (currentFrameIndex > -1 && currentFrameIndex < size) {
			currentFrameIndex = idx;
			Animation animation = playAnimations.get(currentFrameIndex);
			if (animation != null) {
				animation.reset();
			}
		}
	}

	@Override
	public LTexture getSpriteImage() {
		if (currentFrameIndex > -1 && currentFrameIndex < size) {
			Animation animation = playAnimations.get(currentFrameIndex);
			return animation.getSpriteImage(animation.currentFrameIndex);
		} else {
			return null;
		}
	}

	@Override
	public LTexture getSpriteImage(int idx) {
		if (currentFrameIndex > -1 && currentFrameIndex < size) {
			Animation animation = playAnimations.get(currentFrameIndex);
			return animation.getSpriteImage(idx);
		} else {
			return null;
		}
	}

	public LTexture getSpriteImage(int animation, int idx) {
		if (currentFrameIndex > -1 && currentFrameIndex < size) {
			return playAnimations.get(animation).getSpriteImage(idx);
		} else {
			return null;
		}
	}

	public int getIndexLocked() {
		return animationIndexLocked;
	}

	public void indexLocked(int inx) {
		this.animationIndexLocked = inx;
		if (animationIndexLocked > -1 && animationIndexLocked < size) {
			this.currentFrameIndex = animationIndexLocked;
			Animation animation = playAnimations.get(currentFrameIndex);
			if (animation != null) {
				animation.reset();
			}
		}
	}

	@Override
	public void reset() {
		super.reset();
		loopOverToPlay = true;
		loopOverToRemove = false;
	}

	public boolean isLoopOverToRemove() {
		return loopOverToRemove;
	}

	public void loopOverToRemove(boolean l) {
		loopOverToRemove = l;
	}

	public boolean isLoopPlay() {
		return loopOverToPlay;
	}

	public void setLoopPlay(boolean l) {
		this.loopOverToPlay = l;
	}

}
