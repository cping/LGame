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

import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.FloatArray;
import loon.utils.MathUtils;
import loon.utils.TArray;

public class BoneAnimation implements Comparable<BoneAnimation> {

	private TArray<FloatArray> _animationValuesY;
	private FloatArray _animationValuesX;
	private FloatArray _currentAnimationValues;
	private TArray<BoneSheet> _boneSheet;

	private int _nextAnimationIndex;
	private int _currentFrames;
	private int _targetFrames;

	private float _lastRotation;
	private float _deltaScaleValue;

	private boolean _animationComplete;
	private boolean _looped;

	private Bone _bone;

	private String _name;

	private RectBox _boneRect;

	public BoneAnimation(String name, TArray<Bone> bones) {
		this._name = name;
		this._deltaScaleValue = 0.2f;
		this._targetFrames = 5;
		this._boneRect = new RectBox();
		this._boneSheet = bones.get(0).getSheetList();
		TArray<FloatArray> animationValues = new TArray<FloatArray>();
		for (int i = 0; i < bones.size; i++) {
			Bone bone = bones.get(i);
			if (bone != null) {
				_boneRect.union(bone.getRectBox());
				animationValues.add(bone.getBoneValues());
			}
		}
		_animationValuesY = new TArray<FloatArray>();
		for (int y = 0; y < animationValues.size(); y++) {
			_animationValuesX = new FloatArray();
			for (int x = 0; x < animationValues.get(0).size(); x++) {
				_animationValuesX.add(animationValues.get(y).get(x));
			}
			_animationValuesY.add(_animationValuesX);
		}
		_currentAnimationValues = new FloatArray();
		for (int x = 0; x < _animationValuesY.get(0).size(); x++) {
			_currentAnimationValues.add(_animationValuesY.get(0).get(x));
		}
		_nextAnimationIndex++;
	}

	public void update(long elapsedTime) {
		if (_currentFrames == 0 && _nextAnimationIndex < _animationValuesY.size()) {
			_lastRotation = _animationValuesY.get(_nextAnimationIndex - 1).get(BoneFlags.ANGLE);
			float targetRotation = _animationValuesY.get(_nextAnimationIndex).get(BoneFlags.ANGLE);
			float addFull = _lastRotation + MathUtils.DEG_FULL;
			float subFull = _lastRotation - MathUtils.DEG_FULL;
			if (MathUtils.abs(targetRotation - addFull) < MathUtils.abs(targetRotation - subFull)) {
				if (MathUtils.abs(targetRotation - addFull) < MathUtils.abs(targetRotation - _lastRotation)) {
					_lastRotation = addFull;
				}
			} else if (MathUtils.abs(targetRotation - subFull) < MathUtils.abs(targetRotation - _lastRotation)) {
				_lastRotation = subFull;
			}
		}
		if (_nextAnimationIndex < _animationValuesY.size()) {
			for (int x = 0; x < _animationValuesX.size(); x++) {
				if (x == BoneFlags.ANGLE) {
					_currentAnimationValues.set(x,
							_currentAnimationValues.get(x) + calculateDeltaNextValue(_lastRotation,
									_animationValuesY.get(_nextAnimationIndex).get(x)));
				} else if (x >= BoneFlags.FLIP && x <= BoneFlags.CLIP_HEIGHT || x == BoneFlags.ORIGIN_X
						|| x == BoneFlags.ORIGIN_Y) {
					_currentAnimationValues.set(x, _animationValuesY.get(_nextAnimationIndex).get(x));
				} else
					_currentAnimationValues.set(x,
							_currentAnimationValues.get(x)
									+ calculateDeltaNextValue(_animationValuesY.get(_nextAnimationIndex - 1).get(x),
											_animationValuesY.get(_nextAnimationIndex).get(x)));
			}
			_currentFrames++;
			if (_currentFrames == _targetFrames) {
				for (int i = 0; i < _currentAnimationValues.size(); i++) {
					_currentAnimationValues.set(i, _animationValuesY.get(_nextAnimationIndex).get(i));
				}
				_nextAnimationIndex++;
				_currentFrames = 0;
			}
		} else {
			_animationComplete = true;
		}
	}

	public BoneAnimation addFrame(FloatArray f) {
		_animationValuesY.add(f);
		return this;
	}

	public float calculateDeltaNextValue(float last, float target) {
		return ((target - last) * _deltaScaleValue);
	}

	public FloatArray getCurrentAnimationValues() {
		return _currentAnimationValues;
	}

	private void updateAnimationValues() {
		if (_bone == null) {
			_bone = new Bone(_name, _currentAnimationValues, _boneSheet);
		} else {
			_bone.load(_name, _currentAnimationValues, _boneSheet);
		}
	}

	public void draw(GLEx g) {
		_currentAnimationValues.set(BoneFlags.ANGLE, 0f);
		updateAnimationValues();
		_bone.draw(g, 0, 0);
	}

	public void draw(GLEx g, int x, int y) {
		updateAnimationValues();
		_bone.draw(g, x, y);
	}

	public RectBox getRectBox() {
		return this._boneRect;
	}

	public BoneAnimation setTargetFrames(int t) {
		this._targetFrames = t;
		return this;
	}

	public int getCurrentFrames() {
		return _currentFrames;
	}

	public int getTargetFrames() {
		return _targetFrames;
	}

	public float getDeltaScale() {
		return _deltaScaleValue;
	}

	public BoneAnimation setDeltaScale(float v) {
		this._deltaScaleValue = v;
		return this;
	}

	public BoneAnimation setLoop(boolean b) {
		this._looped = b;
		return this;
	}

	public BoneAnimation reset() {
		_nextAnimationIndex = 1;
		_currentFrames = 0;
		_animationComplete = false;
		for (int i = 0; i < _animationValuesY.get(0).size(); i++) {
			_currentAnimationValues.set(i, _animationValuesY.get(0).get(i));
		}
		return this;
	}

	public boolean checkAnimationComplete() {
		return _animationComplete;
	}

	@Override
	public int compareTo(BoneAnimation b) {
		float src = _currentAnimationValues.get(BoneFlags.LAYER);
		float dst = ((BoneAnimation) b).getCurrentAnimationValues().get(BoneFlags.LAYER);
		float v = (int) src - dst;
		if (v > 0) {
			return -1;
		} else if (v == 0) {
			return 0;
		} else {
			return 1;
		}
	}

	public TArray<FloatArray> getAnimationValuesY() {
		return _animationValuesY;
	}

	public FloatArray getAnimationValuesX() {
		return _animationValuesX;
	}

	public int getNextAnimationIndex() {
		return _nextAnimationIndex;
	}

	public float getLastRotation() {
		return _lastRotation;
	}

	public boolean isAnimationComplete() {
		return _animationComplete;
	}

	public boolean isLooping() {
		return _looped;
	}
}
