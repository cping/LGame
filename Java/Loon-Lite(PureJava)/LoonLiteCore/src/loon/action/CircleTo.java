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
import loon.utils.timer.Duration;

public class CircleTo extends ActionEvent {

	private float currentX;

	private float currentY;

	private float startX = -1;

	private float startY = -1;

	private int radius;

	private int velocity;

	private float delta;

	private float speed;

	public CircleTo(int radius, int velocity) {
		this(-1, -1, radius, velocity, LSystem.MIN_SECONE_SPEED_FIXED);
	}

	public CircleTo(float centerX, float centerY, int radius, int velocity, float speed) {
		this.radius = radius;
		this.velocity = velocity;
		this.startX = centerX;
		this.startY = centerY;
		this.speed = speed;
	}

	public CircleTo setVelocity(int v) {
		this.velocity = v;
		return this;
	}

	public int getVelocity() {
		return velocity;
	}

	public CircleTo setSpeed(float s) {
		this.speed = s;
		return this;
	}

	public float getSpeed() {
		return this.speed;
	}

	@Override
	public void onLoad() {
		if (startX == -1) {
			this.startX = original.getX();
		}
		if (startY == -1) {
			this.startY = original.getY();
		}
		this.currentX = (startX + radius);
		this.currentY = startY;
	}

	@Override
	public void update(long elapsedTime) {
		delta += MathUtils.max(Duration.toS(elapsedTime), speed);
		final float angle = MathUtils.toRadians(this.velocity * delta);
		this.currentX = (this.startX + this.radius * MathUtils.cos(angle));
		this.currentY = (this.startY + this.radius * MathUtils.sin(angle));
		movePos(currentX + offsetX, currentY + offsetY);
	}

	@Override
	public CircleTo reset() {
		super.reset();
		delta = 0f;
		return this;
	}

	public float getX() {
		return currentX;
	}

	public float getY() {
		return currentY;
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
		final StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("startX", startX).comma().kv("startY", startY).comma().kv("radius", radius).comma()
				.kv("speed", speed).comma().kv("velocity", velocity).comma().kv("delta", delta);
		return builder.toString();
	}

}
