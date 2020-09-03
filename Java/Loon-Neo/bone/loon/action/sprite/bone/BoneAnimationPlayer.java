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
package loon.action.sprite.bone;

import loon.utils.ObjectMap;
import loon.utils.TArray;

public class BoneAnimationPlayer {

	protected String currentAnimation;
	protected float currentAnimationTime;
	protected int currentKeyframeIndex;

	protected boolean transitioning = false;
	protected String transitionAnimation;
	protected float transitionTime;
	protected float transitionTotalTime;

	protected TArray<BoneTransitionState> transitionStates;

	protected ObjectMap<String, BoneAnimation> animations = new ObjectMap<String, BoneAnimation>();

	protected TArray<BoneTransformation> BoneTransformations;
	
	public String currentAnimation() {
		return currentAnimation;
	}

	public int getCurrentKeyframe() {
		return animations.get(currentAnimation).keyframes.get(currentKeyframeIndex).frameNumber;
	}

	public boolean getTransitioning() {
		return transitioning;
	}

	public void setCurrentTime(float time) {
		currentAnimationTime = time;
	}

	public void add(String name, BoneAnimation animation) {
		animations.put(name, animation);
		if (BoneTransformations == null || animation.keyframes.get(0).bones.size() > BoneTransformations.size()) {
			BoneTransformations = new TArray<BoneTransformation>(animation.keyframes.get(0).bones.size());
			for (int i = 0; i < animation.keyframes.get(0).bones.size(); ++i) {
				BoneTransformations.add(new BoneTransformation());
			}
			transitionStates = new TArray<BoneTransitionState>(animation.keyframes.get(0).bones.size());
			for (int i = 0; i < animation.keyframes.get(0).bones.size(); ++i) {
				transitionStates.add(new BoneTransitionState());
			}
		}
	}

	public void startAnimation(String animation) {
		startAnimation(animation, false);
	}

	public void startAnimation(String animation, boolean allowRestart) {
		transitioning = false;

		if (currentAnimation != animation || allowRestart) {
			currentAnimation = animation;
			currentAnimationTime = 0;
			currentKeyframeIndex = 0;

			for (Bone b : animations.get(currentAnimation).keyframes.get(0).bones) {
				transitionStates.get(b.updateIndex).position = b.position;
				transitionStates.get(b.updateIndex).rotation = b.rotation;
			}
		}

		update(0);
	}

	public void forceAnimationSwitch(String animation) {
		currentAnimation = animation;
	}

	public void transitionToAnimation(String animation, float time) {
		if (transitioning) {
			BoneAnimation.updateBoneTransitions(transitionStates, animations.get(currentAnimation),
					animations.get(transitionAnimation), transitionTime / transitionTotalTime);
		}

		transitioning = true;
		transitionTime = 0;
		transitionTotalTime = time;
		transitionAnimation = animation;
	}

	public int getBoneTransformIndex(String boneName) {
		BoneAnimation animation = animations.get(currentAnimation);

		for (int boneIndex = 0; boneIndex < animation.keyframes.get(0).bones.size(); boneIndex++) {
			Bone bone = animation.keyframes.get(currentKeyframeIndex).bones.get(boneIndex);
			if (bone.name == boneName)
				return boneIndex;
		}

		return -1;
	}

	public boolean update(float deltaSeconds) {
		if (currentAnimation == null || currentAnimation.isEmpty())
			return false;

		boolean returnValue = false;
	
		if (transitioning) {
			transitionTime += deltaSeconds;

			if (transitionTime > transitionTotalTime) {
				transitioning = false;

				currentAnimation = transitionAnimation;
				currentAnimationTime = transitionTime - transitionTotalTime;
				currentKeyframeIndex = 0;

				BoneAnimation animation = animations.get(currentAnimation);
				animation.getBoneTransformations(BoneTransformations, transitionStates, currentKeyframeIndex,
						currentAnimationTime - animation.keyframes.get(currentKeyframeIndex).frameTime);
			} else {
				BoneAnimation.getBoneTransformationsTransition(BoneTransformations, transitionStates,
						animations.get(currentAnimation), animations.get(transitionAnimation),
						transitionTime / transitionTotalTime);
			}
		} else {
			boolean reachedEnd = false;

			currentAnimationTime += deltaSeconds;

			BoneAnimation animation = animations.get(currentAnimation);

			if (currentKeyframeIndex == animation.keyframes.size() - 1) {
				if (animation.loop) {
					if (currentAnimationTime > animation.loopTime) {
						currentAnimationTime -= animation.loopTime;
						currentKeyframeIndex = 0;
					}
				} else {
					currentAnimationTime = animation.keyframes.get(currentKeyframeIndex).frameTime;
					reachedEnd = true;
				}
			} else {
				if (currentAnimationTime > animation.keyframes.get(currentKeyframeIndex + 1).frameTime){
					currentKeyframeIndex++;
				}
			}

			animation.getBoneTransformations(BoneTransformations, transitionStates, currentKeyframeIndex,
					currentAnimationTime - animation.keyframes.get(currentKeyframeIndex).frameTime);

			returnValue = reachedEnd;
		}

		return returnValue;
	}

}
