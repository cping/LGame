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

	private EaseTimer easeTimer;

	public ArrowTo(float tx, float ty) {
		this(tx, ty, 1f, 200f);
	}

	public ArrowTo(float tx, float ty, float g) {
		this(tx, ty, 1f, g);
	}

	public ArrowTo(float tx, float ty, EasingMode mode) {
		this(-1f, -1f, tx, ty, 1f, 200f, 1f, 1f / 60f, mode);
	}

	public ArrowTo(float tx, float ty, float speed, float g) {
		this(-1f, -1f, tx, ty, speed, g);
	}

	public ArrowTo(float st, float sy, float tx, float ty, float speed, float g) {
		this(st, sy, tx, ty, speed, g, 1f, 1f / 60f, EasingMode.Linear);
	}

	public ArrowTo(float tx, float ty, float speed, float g, EasingMode mode) {
		this(-1f, -1f, tx, ty, speed, g, 1f, 1f / 60f, mode);
	}

	public ArrowTo(float st, float sy, float tx, float ty, float speed,
			float g, float duration, float delay, EasingMode mode) {
		this.startX = st;
		this.startY = sy;
		this.endX = tx;
		this.endY = ty;
		this.speed = speed;
		this.gravity = g;
		this.currentX = startX;
		this.currentY = startY;
		this.easeTimer = new EaseTimer(duration, delay, mode);
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

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
		this.vy = 1 / speed * (dy - 1.0f / 2.0f * gravity * speed * speed);
		this.dir = Field2D.getDirection(MathUtils.atan2(endX - startX, endY
				- startY));
		this.currentX = startX;
		this.currentY = startY;
	}

	public void update(long elapsedTime) {
		easeTimer.update(elapsedTime);
		if (easeTimer.isCompleted()) {
			_isCompleted = true;
			return;
		}
		vy += gravity * easeTimer.getProgress();
		currentX += vx * easeTimer.getProgress();
		currentY += vy * easeTimer.getProgress();
		if (original.isContainer() && original.isBounded()) {
			if (currentX < -original.getWidth()
					|| startY < -original.getHeight()
					|| currentX > original.getContainerWidth()
					|| currentY > original.getContainerHeight()) {
				this._isCompleted = true;
			}
		} else if (currentX < -original.getWidth() * 2
				|| currentY < -original.getHeight() * 2
				|| currentX > LSystem.viewSize.width + original.getWidth() * 2
				|| currentY > LSystem.viewSize.height + original.getHeight()
						* 2) {
			this._isCompleted = true;
		} 
		if (this._isCompleted) {
			return;
		}
		synchronized (original) {
			float slope = vy / vx;
			float theta = MathUtils.atan(slope);
			original.setRotation(theta * MathUtils.RAD_TO_DEG);
			original.setLocation(currentX + offsetX, currentY + offsetY);
		}
	}

	public int getDirection() {
		return dir;
	}

	@Override
	public ActionEvent cpy() {
		ArrowTo arrow = new ArrowTo(startX, startY, endX, endY, speed, gravity,
				easeTimer.getDuration(), easeTimer.getDelay(),
				easeTimer.getEasingMode());
		arrow.set(this);
		return arrow;
	}

	@Override
	public ActionEvent reverse() {
		ArrowTo arrow = new ArrowTo(endX, endY, startX, startY, speed, gravity,
				easeTimer.getDuration(), easeTimer.getDelay(),
				easeTimer.getEasingMode());
		arrow.set(this);
		return arrow;
	}

	@Override
	public String getName() {
		return "arrow";
	}

}
