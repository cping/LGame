package loon.action;

import loon.utils.MathUtils;

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
public class FireTo extends ActionEvent {

	private double direction;

	private int x, y;

	private int vx, vy;

	private int endX, endY;

	private double speed;

	public FireTo(int endX, int endY, double speed) {
		this.endX = endX;
		this.endY = endY;
		this.speed = speed;
	}

	@Override
	public boolean isComplete() {
		return isComplete;
	}

	@Override
	public void onLoad() {
		this.x = (int) original.getX();
		this.y = (int) original.getY();
		this.direction = MathUtils.atan2(endY - y, endX - x);
		this.vx = (int) (MathUtils.cos(direction) * this.speed);
		this.vy = (int) (MathUtils.sin(direction) * this.speed);
	}

	@Override
	public void update(long elapsedTime) {
		this.x += this.vx;
		this.y += this.vy;
		if (x == 0 && y == 0) {
			isComplete = true;
			return;
		}
		if (original.isContainer()&&original.isBounded()) {
			if (original.inContains(x, y, original.getWidth(), original.getHeight())) {
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

}
