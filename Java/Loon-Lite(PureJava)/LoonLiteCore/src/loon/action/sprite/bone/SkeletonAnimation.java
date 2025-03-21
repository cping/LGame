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

import loon.LSystem;
import loon.geom.Affine2f;
import loon.opengl.GLEx;
import loon.utils.TArray;
import loon.utils.timer.LTimer;

public class SkeletonAnimation {

	private LTimer _timer = new LTimer(0);

	private Affine2f _mirror = new Affine2f();

	private TArray<BoneAnimation> _boneAnimations;

	private TArray<Skeleton> _skeletons;

	private String _name;

	public SkeletonAnimation(String name, TArray<Skeleton> s) {
		this._name = name;
		this._boneAnimations = new TArray<BoneAnimation>();
		this._skeletons = s;
		this._mirror.scale(-1, 1);
		this.createBoneAnimations();
	}

	public SkeletonAnimation createSubAnimationX(int x) {
		return createSubAnimation(LSystem.UNKNOWN, x, _skeletons.size - 1);
	}

	public SkeletonAnimation createSubAnimationY(int y) {
		return createSubAnimation(LSystem.UNKNOWN, 0, y);
	}

	public SkeletonAnimation createSubAnimation(int x, int y) {
		return createSubAnimation(LSystem.UNKNOWN, x, y);
	}

	public SkeletonAnimation createSubAnimation(String name, int x, int y) {
		final TArray<Skeleton> res = new TArray<Skeleton>();
		for (int i = x; i < y; i++) {
			res.add(_skeletons.get(i));
		}
		return new SkeletonAnimation(name, res);
	}

	public void createBoneAnimations() {
		for (int x = 0; x < _skeletons.get(0).getBones().size(); x++) {
			TArray<Bone> boneList = new TArray<Bone>();
			for (int y = 0; y < _skeletons.size(); y++) {
				Skeleton skeleton = _skeletons.get(y);
				if (skeleton.getBones().size > x) {
					boneList.add(skeleton.getBone(x));
				}
			}
			_boneAnimations.add(new BoneAnimation(_name, boneList));
		}
	}

	public LTimer getTimer() {
		return _timer;
	}

	public TArray<Skeleton> getSkeletons() {
		return _skeletons;
	}

	public void addFrame(Skeleton s) {
		for (int x = 0; x < s.getBones().size(); x++) {
			_boneAnimations.get(x).addFrame(s.getBone(x).getBoneValues());
		}
	}

	public void update(long elapsedTime) {
		if (_timer.action(elapsedTime)) {
			_boneAnimations.sort(null);
			for (int i = 0; i < _boneAnimations.size; i++) {
				BoneAnimation bone = _boneAnimations.get(i);
				if (bone.getCurrentFrames() != bone.getTargetFrames()) {
					bone.update(elapsedTime);
				}
				if (bone.checkAnimationComplete() && bone.isLooping()) {
					bone.reset();
				}
			}
		}
	}

	public void setLoop(boolean b) {
		for (int i = 0; i < _boneAnimations.size; i++) {
			BoneAnimation bone = _boneAnimations.get(i);
			bone.setLoop(b);
		}
	}

	public void play() {
		for (int i = 0; i < _boneAnimations.size; i++) {
			BoneAnimation bone = _boneAnimations.get(i);
			bone.reset();
		}
	}

	public void draw(GLEx g) {
		for (int i = 0; i < _boneAnimations.size; i++) {
			BoneAnimation bone = _boneAnimations.get(i);
			bone.draw(g);
		}
	}

	public void draw(GLEx g, int x, int y) {
		for (int i = 0; i < _boneAnimations.size; i++) {
			BoneAnimation bone = _boneAnimations.get(i);
			bone.draw(g, x, y);
		}
	}

	public void draw(GLEx g, int x, int y, int direction) {
		if (direction != 0) {
			g.saveTx();
			if (direction == 1) {
				_mirror.reset();
				_mirror.scale(-1, 1);
				g.mulAffine(_mirror);
				for (int i = 0; i < _boneAnimations.size; i++) {
					BoneAnimation bone = _boneAnimations.get(i);
					bone.draw(g, -x, y);
				}
			} else if (direction == 2) {
				_mirror.reset();
				_mirror.scale(-1, -1);
				g.mulAffine(_mirror);
				for (int i = 0; i < _boneAnimations.size; i++) {
					BoneAnimation bone = _boneAnimations.get(i);
					bone.draw(g, -x, -y);
				}
			}
			g.restoreTx();
		} else {
			for (int i = 0; i < _boneAnimations.size; i++) {
				BoneAnimation bone = _boneAnimations.get(i);
				bone.draw(g, x, y);
			}
		}
	}
}
