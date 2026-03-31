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
package loon.action;

import loon.LSystem;
import loon.action.map.Field2D;
import loon.utils.MathUtils;
import loon.utils.StringKeyValue;
import loon.utils.Easing.EasingMode;
import loon.utils.timer.EaseTimer;

//0.3.3新增动作，让指定对象做弓箭射出状（抛物线）
public class ArrowTo extends ActionEvent {

	private float gravity;

	private float startX = -1f;
	private float startY = -1f;

	private float endX;
	private float endY;

	private float currentX = 0;
	private float currentY = 0;

	private float vx;
	private float vy;

	private float speed;

	private int dir;

	public ArrowTo(float tx, float ty) {
		this(tx, ty, 1f, 200f);
	}

	public ArrowTo(float tx, float ty, float g) {
		this(tx, ty, 1f, g);
	}

	public ArrowTo(float tx, float ty, EasingMode mode) {
		this(-1f, -1f, tx, ty, 1f, 200f, 1f, LSystem.DEFAULT_EASE_DELAY, mode);
	}

	public ArrowTo(float tx, float ty, float speed, float g) {
		this(-1f, -1f, tx, ty, speed, g);
	}

	public ArrowTo(float st, float sy, float tx, float ty, float speed, float g) {
		this(st, sy, tx, ty, speed, g, 1f, LSystem.DEFAULT_EASE_DELAY, EasingMode.Linear);
	}

	public ArrowTo(float tx, float ty, float speed, float g, EasingMode mode) {
		this(-1f, -1f, tx, ty, speed, g, 1f, LSystem.DEFAULT_EASE_DELAY, mode);
	}

	public ArrowTo(float st, float sy, float tx, float ty, float speed, float g, float duration, float delay,
			EasingMode mode) {
		this.startX = st;
		this.startY = sy;
		this.endX = tx;
		this.endY = ty;
		this.speed = speed;
		this.gravity = g;
		this.currentX = startX;
		this.currentY = startY;
		this._easeTimer = new EaseTimer(duration, delay, mode);
	}

	@Override
	public void onLoad() {
		if (this.startX == -1) {
			this.startX = original.getX();
		}
		if (this.startY == -1) {
			this.startY = original.getY();
		}
		float dx = endX - startX;
		float dy = endY - startY;
		this.vx = dx / speed;
		this.vy = 1f / speed * (dy - 1.0f / 2.0f * gravity * speed * speed);
		this.dir = Field2D.getDirection(MathUtils.atan2(endX - startX, endY - startY));
		this.currentX = startX;
		this.currentY = startY;
	}

	@Override
	public void update(long elapsedTime) {
		_easeTimer.update(elapsedTime);
		if (_easeTimer.isCompleted()) {
			_isCompleted = true;
			return;
		}
		vy += gravity * _easeTimer.getProgress();
		currentX += vx * _easeTimer.getProgress();
		currentY += vy * _easeTimer.getProgress();
		if (original.isContainer() && original.isBounded()) {
			if (currentX < -original.getWidth() || startY < -original.getHeight()
					|| currentX > original.getContainerWidth() || currentY > original.getContainerHeight()) {
				this._isCompleted = true;
			}
		} else if (currentX < -original.getWidth() * 2 || currentY < -original.getHeight() * 2
				|| currentX > LSystem.viewSize.width + original.getWidth() * 2
				|| currentY > LSystem.viewSize.height + original.getHeight() * 2) {
			this._isCompleted = true;
		}
		if (this._isCompleted) {
			return;
		}
		float slope = vy / vx;
		float theta = MathUtils.atan(slope);
		original.setRotation(MathUtils.toDegrees(theta));
		movePos(currentX + offsetX, currentY + offsetY);
	}

	public int getDirection() {
		return dir;
	}

	public float getGravity() {
		return gravity;
	}

	public float getStartX() {
		return startX;
	}

	public float getStartY() {
		return startY;
	}

	public float getEndX() {
		return endX;
	}

	public float getEndY() {
		return endY;
	}

	public float getCurrentX() {
		return currentX;
	}

	public float getCurrentY() {
		return currentY;
	}

	public float getVelocityX() {
		return vx;
	}

	public float getVelocityY() {
		return vy;
	}

	public float getSpeed() {
		return speed;
	}

	@Override
	public ActionEvent cpy() {
		ArrowTo arrow = new ArrowTo(startX, startY, endX, endY, speed, gravity, _easeTimer.getDuration(),
				_easeTimer.getDelay(), _easeTimer.getEasingMode());
		arrow.set(this);
		return arrow;
	}

	@Override
	public ActionEvent reverse() {
		ArrowTo arrow = new ArrowTo(endX, endY, startX, startY, speed, gravity, _easeTimer.getDuration(),
				_easeTimer.getDelay(), _easeTimer.getEasingMode());
		arrow.set(this);
		return arrow;
	}

	@Override
	public String getName() {
		return "arrow";
	}

	@Override
	public String toString() {
		final StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("gravity", gravity).comma().kv("startX", startX).comma().kv("startY", startY).comma()
				.kv("currentX", currentX).comma().kv("currentY", currentY).comma().kv("direction", dir).comma()
				.kv("EaseTimer", _easeTimer);
		return builder.toString();
	}

}
