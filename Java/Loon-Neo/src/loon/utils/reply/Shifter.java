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
package loon.utils.reply;

import loon.utils.MathUtils;

public class Shifter {

	private float _shiftLevel;

	private float _shiftTarget;

	private float _speed;

	private boolean _swing;

	private float _low = 0f;

	private float _high = 1f;

	public Shifter() {
		this(0.6f);
	}

	public Shifter(float speed) {
		this._speed = speed;
	}

	public int getMoverShift(int maxDistance) {
		return MathUtils.iceil((getShiftLevel() - getLow()) / (getHigh() - getLow()) * maxDistance);
	}

	public int getInvertedMoverShift(int maxDistance) {
		return maxDistance - MathUtils.iceil((getShiftLevel() - getLow()) / (getHigh() - getLow()) * maxDistance);
	}

	public int getMoverShift(float maxDistance) {
		return MathUtils.iceil((getShiftLevel() - getLow()) / (getHigh() - getLow()) * maxDistance);
	}

	public int getInvertedMoverShift(float maxDistance) {
		return MathUtils.iceil(maxDistance - (getShiftLevel() - getLow()) / (getHigh() - getLow()) * maxDistance);
	}

	public void setSwing(boolean swing) {
		this._swing = swing;
	}

	public boolean getSwing() {
		return this._swing;
	}

	public float getShiftLevel() {
		return this._shiftLevel;
	}

	public boolean isShiftLevelEqualsLow() {
		return (this._shiftLevel == getLow());
	}

	public boolean isShiftLevelEqualsHigh() {
		return (this._shiftLevel == getHigh());
	}

	public boolean isShiftTargetEqualsLow() {
		return (this._shiftTarget == getLow());
	}

	public boolean getShiftTargetEqualsHigh() {
		return (this._shiftTarget == getHigh());
	}

	public void setShiftLevelHigh() {
		setShiftLevel(getHigh());
	}

	public void setShiftLevelLow() {
		setShiftLevel(getLow());
	}

	public void setShiftLevel(float shiftLevel) {
		this._shiftLevel = shiftLevel;
	}

	public void setShiftTargetHigh() {
		setShiftTarget(getHigh());
	}

	public void setShiftTargetLow() {
		setShiftTarget(getLow());
	}

	public float getShiftTarget() {
		return this._shiftTarget;
	}

	public void setShiftTarget(float shiftTarget) {
		this._shiftTarget = shiftTarget;
	}

	public void shift(boolean solid) {
		setShiftTarget(solid ? getHigh() : getLow());
	}

	public void set(float shift, boolean targetAlso) {
		setShiftLevel(shift);
		if (this._swing || !targetAlso) {
			return;
		}
		setShiftTarget(shift);
	}

	public void setSpeed(float speed) {
		this._speed = speed;
	}

	public float getSpeed() {
		return this._speed;
	}

	public void setLow(float low) {
		this._low = low;
		if (this._shiftLevel < low) {
			setShiftLevel(getLow());
		}
		if (this._shiftTarget < low) {
			setShiftTarget(getLow());
		}
	}

	public float getLow() {
		return this._low;
	}

	public void setHigh(float high) {
		this._high = high;
		if (this._shiftLevel > high) {
			setShiftLevel(getHigh());
		}
		if (this._shiftTarget > high) {
			setShiftTarget(getHigh());
		}
	}

	public boolean getShifting() {
		return !(this._shiftLevel == this._shiftTarget && !this._swing);
	}

	public float getHigh() {
		return this._high;
	}

	public float getUpdatedLevel(float shiftLevel, float delta) {
		float shiftTarget = this._shiftTarget;
		if (shiftLevel == shiftTarget) {
			if (!this._swing) {
				return shiftLevel;
			}
			if (shiftTarget == getLow()) {
				shiftTarget = getHigh();
			} else if (shiftTarget == getHigh()) {
				shiftTarget = getLow();
			}
		}
		int direction = (shiftTarget < shiftLevel) ? -1 : 1;
		float newShiftLevel = shiftLevel + direction * delta * this._speed / 100f;
		if (newShiftLevel < getLow()) {
			newShiftLevel = getLow();
		}
		if (newShiftLevel > getHigh()) {
			newShiftLevel = getHigh();
		}
		if (newShiftLevel < shiftTarget && shiftLevel > shiftTarget) {
			newShiftLevel = shiftTarget;
		}
		if (newShiftLevel > shiftTarget && shiftLevel < shiftTarget) {
			newShiftLevel = shiftTarget;
		}
		return newShiftLevel;
	}

	public boolean update(float delta) {
		if (delta == 0f || !getShifting()) {
			return false;
		}
		if (this._swing && this._shiftLevel == this._shiftTarget) {
			if (this._shiftTarget == getLow()) {
				setShiftTarget(getHigh());
			} else if (this._shiftTarget == getHigh()) {
				setShiftTarget(getLow());
			}
		}
		int direction = (this._shiftTarget < this._shiftLevel) ? -1 : 1;
		float shiftLevel = this._shiftLevel + direction * delta * this._speed / 100f;
		if (shiftLevel < getLow()) {
			shiftLevel = getLow();
		}
		if (shiftLevel > getHigh()) {
			shiftLevel = getHigh();
		}
		if (shiftLevel < this._shiftTarget && this._shiftLevel > this._shiftTarget) {
			shiftLevel = this._shiftTarget;
		}
		if (shiftLevel > this._shiftTarget && this._shiftLevel < this._shiftTarget) {
			shiftLevel = this._shiftTarget;
		}
		setShiftLevel(shiftLevel);
		return true;
	}
}