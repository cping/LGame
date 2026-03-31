/**
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
package loon.action;

import loon.LSystem;
import loon.utils.MathUtils;
import loon.utils.StringKeyValue;

public class FireTo extends ActionEvent {

	private float direction;

	private float startX, startY;

	private float vx, vy;

	private float endX, endY;

	private float speed;

	public FireTo(ActionBind act, float speed) {
		this(act == null ? 0f : act.getX() + act.getWidth() / 2f, act == null ? 0f : act.getY() + act.getHeight() / 2f,
				speed);
	}

	public FireTo(float endX, float endY, float speed) {
		this.endX = endX;
		this.endY = endY;
		this.speed = speed;
	}

	@Override
	public void onLoad() {
		this.startX = original.getX();
		this.startY = original.getY();
		this.direction = MathUtils.atan2(endY - startY, endX - startX);
		this.vx = (MathUtils.cos(direction) * this.speed);
		this.vy = (MathUtils.sin(direction) * this.speed);
	}

	public float getX() {
		return startX;
	}

	public float getY() {
		return startY;
	}

	public float getVelocityX() {
		return vx;
	}

	public float getVelocityY() {
		return vy;
	}

	public float getEndX() {
		return endX;
	}

	public float getEndY() {
		return endY;
	}

	public float getSpeed() {
		return speed;
	}

	@Override
	public void update(long elapsedTime) {
		final float v = LSystem.getScaleFPS();
		this.startX += this.vx * v;
		this.startY += this.vy * v;
		if (startX == 0 && startY == 0) {
			_isCompleted = true;
			return;
		}
		if (original.isContainer() && original.isBounded()) {
			if (original.inContains(startX, startY, original.getWidth(), original.getHeight())) {
				movePos(startX + offsetX, startY + offsetY);
			} else {
				_isCompleted = true;
			}
		} else {
			if (startX + original.getWidth() < 0) {
				_isCompleted = true;
			} else if (startX > original.getContainerWidth() + original.getWidth()) {
				_isCompleted = true;
			}
			if (startY + original.getHeight() < 0) {
				_isCompleted = true;
			} else if (startY > original.getContainerHeight() + original.getHeight()) {
				_isCompleted = true;
			}
			movePos(startX + offsetX, startY + offsetY);
		}
	}

	public double getDirection() {
		return direction;
	}

	@Override
	public ActionEvent cpy() {
		FireTo fire = new FireTo(endX, endY, speed);
		fire.set(this);
		return fire;
	}

	@Override
	public ActionEvent reverse() {
		return new FireTo(oldX, oldY, speed);
	}

	@Override
	public String getName() {
		return "fire";
	}

	@Override
	public String toString() {
		final StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("startX", startX).comma().kv("startY", startY).comma().kv("endX", endX).comma().kv("endY", endY)
				.comma().kv("speed", speed);
		return builder.toString();
	}

}
