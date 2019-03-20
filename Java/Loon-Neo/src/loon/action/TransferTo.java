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
package loon.action;

import loon.utils.Easing.EasingMode;
import loon.utils.timer.EaseTimer;

public class TransferTo extends ActionEvent {

	private float startPos = -1;
	private float endPos = -1;
	private float travelDistance;
	private float currentPosition;

	private boolean controllingX;
	private boolean controllingY;

	private EaseTimer easeTimer;

	public TransferTo(float startPos, float endPos, float duration,
			EasingMode mode, boolean controlX, boolean controlY) {
		this(startPos, endPos, duration, 1f / 60f, mode, controlX, controlY);
	}

	public TransferTo(float startPos, float endPos, float duration,
			EasingMode mode) {
		this(startPos, endPos, duration, 1f / 60f, mode, true, false);
	}

	public TransferTo(float startPos, float endPos, float duration,
			float delay, EasingMode mode, boolean controlX, boolean controlY) {
		this.easeTimer = new EaseTimer(duration, delay, mode);
		this.startPos = startPos;
		this.endPos = endPos;
		this.travelDistance = endPos - startPos;
		this.currentPosition = startPos;
		this.controllingX = controlX;
		this.controllingY = controlY;
	}

	public void setControl(boolean controlX, boolean controlY) {
		this.controllingX = controlX;
		this.controllingY = controlY;
	}

	public void reset() {
		currentPosition = startPos;
	}

	public float getStartPos() {
		return startPos;
	}

	public void setStartPos(float startPos) {
		this.startPos = startPos;
	}

	public float getEndPos() {
		return endPos;
	}

	public void setEndPos(float endPos) {
		this.endPos = endPos;
	}

	public boolean isControllingX() {
		return controllingX;
	}

	public void setControllingX(boolean controllingX) {
		this.controllingX = controllingX;
	}

	public boolean setControlX(boolean control) {
		return controllingX = control;
	}

	public boolean setControlY(boolean control) {
		return controllingY = control;
	}

	public boolean isControllingY() {
		return controllingY;
	}

	public void setControllingY(boolean controllingY) {
		this.controllingY = controllingY;
	}

	public float getDistance() {
		return travelDistance;
	}

	@Override
	public void update(long elapsedTime) {
		easeTimer.update(elapsedTime);
		if (easeTimer.isCompleted()) {
			this._isCompleted = true;
			return;
		}
		currentPosition = easeTimer.getProgress() * travelDistance + startPos;
		if (original != null) {
			synchronized (original) {
				if (this.controllingX) {
					this.original.setX(getCurrentPos() + offsetX);
				}
				if (this.controllingY) {
					this.original.setY(getCurrentPos() + offsetY);
				}
			}
		}

	}

	public float getCurrentPos() {
		return currentPosition;
	}

	@Override
	public void onLoad() {
		if (original != null) {
			if (startPos == -1) {
				startPos = original.getX();
			}
			if (endPos == -1) {
				endPos = original.getY();
			}
		}
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public ActionEvent cpy() {
		TransferTo t = new TransferTo(this.startPos, this.endPos,
				easeTimer.getDuration(), easeTimer.getDelay(),
				easeTimer.getEasingMode(), this.controllingX, this.controllingY);
		t.set(this);
		return t;
	}

	@Override
	public ActionEvent reverse() {
		TransferTo t = new TransferTo(this.endPos, this.startPos,
				easeTimer.getDuration(), easeTimer.getDelay(),
				easeTimer.getEasingMode(), this.controllingX, this.controllingY);
		t.set(this);
		return t;
	}

	@Override
	public String getName() {
		return "transfer";
	}

}
