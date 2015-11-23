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

public class FireTo extends ActionEvent {

	private float direction;

	private float x, y;

	private float vx, vy;

	private float endX, endY;

	private float speed;

	public FireTo(float endX, float endY, float speed) {
		this.endX = endX;
		this.endY = endY;
		this.speed = speed;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void onLoad() {
		this.x =  original.getX();
		this.y = original.getY();
		this.direction = MathUtils.atan2(endY - y, endX - x);
		this.vx =  (MathUtils.cos(direction) * this.speed);
		this.vy =  (MathUtils.sin(direction) * this.speed);
	}

	public void update(long elapsedTime) {
		this.x += this.vx;
		this.y += this.vy;
		if (x == 0 && y == 0) {
			isComplete = true;
			return;
		}
		if (original.isContainer() && original.isBounded()) {
			if (original.inContains(x, y, original.getWidth(),
					original.getHeight())) {
				synchronized (original) {
					original.setLocation(x + offsetX, y + offsetY);
				}
			} else {
				isComplete = true;
			}
		} else {
			if (x + original.getWidth() < 0) {
				isComplete = true;
			} else if (x > original.getContainerWidth() + original.getWidth()) {
				isComplete = true;
			}
			if (y + original.getHeight() < 0) {
				isComplete = true;
			} else if (y > original.getContainerHeight() + original.getHeight()) {
				isComplete = true;
			}
			synchronized (original) {
				original.setLocation(x + offsetX, y + offsetY);
			}
		}
	}

	public double getDirection() {
		return direction;
	}

	@Override
	public ActionEvent cpy() {
		return new FireTo(endX, endY, speed);
	}

}
