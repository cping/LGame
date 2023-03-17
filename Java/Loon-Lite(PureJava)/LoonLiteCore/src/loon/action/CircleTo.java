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

public class CircleTo extends ActionEvent {

	private float x;

	private float y;

	private float startX = -1;

	private float startY = -1;

	private int radius;

	private int velocity;

	private float dt;

	private float speed;

	public CircleTo(int radius, int velocity) {
		this(-1, -1, radius, velocity, 0.1f);
	}

	public CircleTo(float centerX, float centerY, int radius, int velocity, float speed) {
		this.radius = radius;
		this.velocity = velocity;
		this.startX = centerX;
		this.startY = centerY;
		this.speed = speed;
	}

	public void setVelocity(int v) {
		this.velocity = v;
	}

	public int getVelocity() {
		return velocity;
	}

	public void setSpeed(float s) {
		this.speed = s;
	}

	public float getSpeed() {
		return this.speed;
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public void onLoad() {
		if (startX == -1) {
			this.startX = original.getX();
		}
		if (startY == -1) {
			this.startY = original.getY();
		}
		this.x = (startX + radius);
		this.y = startY;
	}

	@Override
	public void update(long elapsedTime) {
		dt += MathUtils.max(elapsedTime / 1000f, MathUtils.max(speed, LSystem.MIN_SECONE_SPEED_FIXED));
		this.x = (this.startX + this.radius * MathUtils.cos(MathUtils.toRadians(this.velocity * dt)));
		this.y = (this.startY + this.radius * MathUtils.sin(MathUtils.toRadians(this.velocity * dt)));
		synchronized (original) {
			movePos(x + offsetX, y + offsetY);
		}
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getStartX() {
		return startX;
	}

	public float getStartY() {
		return startY;
	}

	public int getRadius() {
		return radius;
	}

	@Override
	public ActionEvent cpy() {
		CircleTo circle = new CircleTo(startX, startY, radius, velocity, speed);
		circle.set(this);
		return circle;
	}

	@Override
	public ActionEvent reverse() {
		return cpy();
	}

	@Override
	public String getName() {
		return "circle";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("startX", startX).comma().kv("startY", startY).comma().kv("radius", radius).comma()
				.kv("speed", speed).comma().kv("velocity", velocity).comma().kv("delta", dt);
		return builder.toString();
	}

}
