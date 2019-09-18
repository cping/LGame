/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, version 2.0 (the "License"); you may not
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

import loon.geom.Matrix4;
import loon.geom.Quaternion;
import loon.geom.Vector2f;
import loon.geom.Vector3f;
import loon.utils.Easing;
import loon.utils.MathUtils;
import loon.utils.TArray;

public class BoneAnimation {

	protected String version;

	protected int frameRate;

	protected boolean loop;
	protected int loopFrame;
	protected float loopTime;

	protected TArray<BoneEntry> textures;
	protected TArray<BoneKeyFrame> keyframes;

	public void recalculateFrameTimes() {
		float fr = 1.0f / frameRate;
		loopTime = loopFrame * frameRate;
		loop = loopFrame != -1;
		for (BoneKeyFrame kf : keyframes) {
			kf.frameTime = fr * kf.frameNumber;
		}
	}

	public void addTexture(BoneEntry ent) {
		textures.add(ent);
	}

	public void deleteBone(String name, BoneKeyFrame frame) {
		for (int i = 0; i < frame.bones.size(); ++i) {
			Bone b = frame.bones.get(i);
			if (b != null && b.name.equals(name)) {
				frame.bones.remove(b);
				break;
			}
		}
	}

	public void deleteBone(String name) {
		for (BoneKeyFrame fr : keyframes) {
			for (int i = 0; i < fr.bones.size(); ++i) {
				Bone b = fr.bones.get(i);
				if (b != null && b.name.equals(name)) {
					fr.bones.remove(b);
					break;
				}
			}
		}
	}

	public void addBone(String name, int tex, Bone parent, BoneKeyFrame frame) {
		Bone b = new Bone();
		b.parentIndex = parent.selfIndex;
		b.position = parent.position.cpy();
		b.textureIndex = tex;
		b.selfIndex = frame.bones.size();
		b.name = name;
		b.rotation = 0;
		b.scale = new Vector2f(1f, 1f);
		b.hidden = false;
		frame.bones.add(b);
		frame.sortBones();
	}

	public void getBoneTransformations(TArray<BoneTransformation> transforms, TArray<BoneTransitionState> transitions,
			int keyframeIndex, float time) {
		BoneKeyFrame currentKeyframe = keyframes.get(keyframeIndex);
		BoneKeyFrame nextKeyframe;
		float t;
		if (keyframeIndex == keyframes.size() - 1) {
			nextKeyframe = keyframes.get(0);
			if (loop) {
				t = time / (loopTime - currentKeyframe.frameTime);
			} else {
				t = 0;
			}
		} else {
			nextKeyframe = keyframes.get(keyframeIndex + 1);
			t = time / (nextKeyframe.frameTime - currentKeyframe.frameTime);
		}

		for (int boneIndex = 0; boneIndex < keyframes.get(0).updateOrderBones.size(); boneIndex++) {
			Vector2f position = currentKeyframe.updateOrderBones.get(boneIndex).position.cpy()
					.lerp(nextKeyframe.updateOrderBones.get(boneIndex).position, t);
			Vector2f scale = currentKeyframe.updateOrderBones.get(boneIndex).scale.cpy()
					.lerp(nextKeyframe.updateOrderBones.get(boneIndex).scale, t);
			float rotation = Easing.linear(t, 1f, currentKeyframe.updateOrderBones.get(boneIndex).rotation,
					nextKeyframe.updateOrderBones.get(boneIndex).rotation);

			transitions.get(boneIndex).position = position;
			transitions.get(boneIndex).rotation = rotation;

			Matrix4 identity = new Matrix4();
			identity.idt();
			Matrix4 parentTransform = currentKeyframe.updateOrderBones.get(boneIndex).parentIndex == -1 ? null
					: transforms.get(currentKeyframe.updateOrderBones.get(boneIndex).parentIndex).transform;

			int drawIndex = currentKeyframe.updateOrderBones.get(boneIndex).selfIndex;

			Matrix4 scl = new Matrix4();
			scl.idt();
			scl.setToScaling(scale.y, scale.y, 1);
			Matrix4 trans = new Matrix4();
			trans.idt();
			trans.setToTranslation(position.x, position.y, 0);
			Matrix4 rot = new Matrix4();
			rot.idt();
			rot.setToRotation(Vector3f.AXIS_Z(), MathUtils.RAD_TO_DEG * rotation);

			if (parentTransform != null) {
				Matrix4 mat = transforms.get(drawIndex).transform;
				if (mat == null)
					mat = new Matrix4();
				mat.set(trans);
				mat.rotate(Vector3f.AXIS_Z(), MathUtils.RAD_TO_DEG * rotation);
				mat.mul(parentTransform);
				transforms.get(drawIndex).transform = mat;
			} else {
				Matrix4 mat = transforms.get(drawIndex).transform;
				if (mat == null)
					mat = new Matrix4();
				mat.set(trans);
				mat.rotate(Vector3f.AXIS_Z(), MathUtils.RAD_TO_DEG * rotation);
				transforms.get(drawIndex).transform = mat;
			}

			Vector3f position3 = new Vector3f(), scale3 = new Vector3f();
			Vector3f direction = Vector3f.AXIS_X().cpy();
			Quaternion rotationQ = new Quaternion();

			transforms.get(drawIndex).transform.getTranslation(position3);
			transforms.get(drawIndex).transform.getRotation(rotationQ);
			transforms.get(drawIndex).transform.getScale(scale3);

			direction = rotationQ.transformSelf(direction);

			transforms.get(drawIndex).position = new Vector2f(position3.x, position3.y);
			transforms.get(drawIndex).scale = new Vector2f(scale3.x, scale3.y);
			transforms.get(drawIndex).rotation = MathUtils.RAD_TO_DEG * MathUtils.atan2(direction.y, direction.x);
		}
	}

	public static void getBoneTransformationsTransition(TArray<BoneTransformation> transforms,
			TArray<BoneTransitionState> transitionState, BoneAnimation currentAnimation, BoneAnimation stopAnimation,
			float transitionPosition) {
		for (int boneIndex = 0; boneIndex < currentAnimation.keyframes.get(0).updateOrderBones.size(); boneIndex++) {
			Bone currentBone = currentAnimation.keyframes.get(0).updateOrderBones.get(boneIndex);
			Bone transitionBone = null;

			for (Bone b : stopAnimation.keyframes.get(0).updateOrderBones) {
				if (currentBone.name == b.name) {
					transitionBone = b;
					break;
				}
			}

			if (transitionBone == null)
				continue;

			Vector2f position = transitionState.get(boneIndex).position.cpy().lerp(transitionBone.position,
					transitionPosition);
			Vector2f scale = new Vector2f(1, 1);
			float rotation = Easing.linear(transitionPosition, 1f, transitionState.get(boneIndex).rotation,
					transitionBone.rotation);

			Matrix4 ident = new Matrix4();
			ident.idt();
			Matrix4 parentTransform = currentBone.parentIndex == -1 ? ident
					: transforms.get(currentBone.parentIndex).transform;

			int drawIndex = currentBone.selfIndex;

			Matrix4 scl = new Matrix4();
			Matrix4 trn = new Matrix4();
			Matrix4 rot = new Matrix4();
			scl.setToScaling(scale.x, scale.y, 1);
			rot.setToRotation(Vector3f.AXIS_Z(), MathUtils.RAD_TO_DEG * rotation);
			trn.setToTranslation(position.x, position.y, 0);

			transforms.get(drawIndex).transform = scl.mul(trn).mul(rot).mul(parentTransform);

			Vector3f position3 = new Vector3f(), scale3 = new Vector3f();
			Vector3f direction = Vector3f.AXIS_X().cpy();
			Quaternion rotationQ = new Quaternion();

			transforms.get(drawIndex).transform.getScale(scale3);
			transforms.get(drawIndex).transform.getTranslation(position3);
			transforms.get(drawIndex).transform.getRotation(rotationQ);

			direction = rotationQ.transformSelf(direction);

			transforms.get(drawIndex).position = new Vector2f(position3.x, position3.y);
			transforms.get(drawIndex).rotation = MathUtils.atan2(direction.y, direction.x);
			transforms.get(drawIndex).scale = new Vector2f(scale3.x, scale3.y);
		}
	}

	public static void updateBoneTransitions(TArray<BoneTransitionState> transitionState,
			BoneAnimation currentAnimation, BoneAnimation stopAnimation, float transitionPosition) {
		for (int boneIndex = 0; boneIndex < currentAnimation.keyframes.get(0).updateOrderBones.size(); boneIndex++) {
			Bone currentBone = currentAnimation.keyframes.get(0).updateOrderBones.get(boneIndex);
			Bone transitionBone = null;

			for (Bone b : stopAnimation.keyframes.get(0).updateOrderBones) {
				if (currentBone.name == b.name) {
					transitionBone = b;
					break;
				}
			}

			if (transitionBone == null) {
				continue;
			}

			transitionState.get(boneIndex).position = transitionState.get(boneIndex).position.cpy()
					.lerp(transitionBone.position, transitionPosition);
			transitionState.get(boneIndex).rotation = Easing.linear(transitionPosition, 1f,
					transitionState.get(boneIndex).rotation, transitionBone.rotation);
		}
	}
}
