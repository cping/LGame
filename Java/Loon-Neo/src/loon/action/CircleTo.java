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

import loon.utils.MathUtils;

public class CircleTo extends ActionEvent {

	private float x;

	private float y;

	private float cx;

	private float cy;

	private int radius;

	private int velocity;

	private float dt;

	private float speed;

	public CircleTo(int radius, int velocity) {
		this(radius, velocity, 0.1f);
	}

	public CircleTo(int radius, int velocity, float speed) {
		this.radius = radius;
		this.velocity = velocity;
		this.speed = speed;
	}

	public void setSpeed(float s) {
		this.speed = s;
	}

	public float getSpeed() {
		return this.speed;
	}

	public boolean isComplete() {
		return _isCompleted;
	}
	
	@Override
	public void onLoad() {
		this.cx = original.getX();
		this.cy = original.getY();
		this.x = (cx + radius);
		this.y = cy;
	}

	@Override
	public void update(long elapsedTime) {
		dt += MathUtils.max((elapsedTime / 1000f), speed);
		this.x = (this.cx + this.radius
				* MathUtils.cos(MathUtils.toRadians(this.velocity * dt)));
		this.y = (this.cy + this.radius
				* MathUtils.sin(MathUtils.toRadians(this.velocity * dt)));
		synchronized (original) {
			original.setLocation(x + offsetX, y + offsetY);
		}
	}

	@Override
	public ActionEvent cpy() {
		CircleTo circle = new CircleTo(radius, velocity, speed);
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

}
