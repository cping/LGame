package org.loon.framework.javase.game.action;

import org.loon.framework.javase.game.core.graphics.component.ActorLayer;

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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
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

	public boolean isComplete() {
		return isComplete;
	}

	public void onLoad() {
		this.x = original.getX();
		this.y = original.getY();
		this.direction = Math.atan2(endY - y, endX - x);
		this.vx = (int) (Math.cos(direction) * this.speed);
		this.vy = (int) (Math.sin(direction) * this.speed);
	}

	public void update(long elapsedTime) {
		this.x += this.vx;
		this.y += this.vy;
		if (x == 0 && y == 0) {
			isComplete = true;
			return;
		}
		ActorLayer layer = original.getLLayer();
		if (layer.isBounded()) {
			if (layer.contains(x, y, original.getWidth(), original.getHeight())) {
				original.setLocation(x, y);
			} else {
				isComplete = true;
			}
		} else {
			if (x + original.getWidth() < 0) {
				isComplete = true;
			} else if (x > layer.getWidth() + original.getWidth()) {
				isComplete = true;
			}
			if (y + original.getHeight() < 0) {
				isComplete = true;
			} else if (y > layer.getHeight() + original.getHeight()) {
				isComplete = true;
			}

			original.setLocation(x, y);
		}
	}

	public double getDirection() {
		return direction;
	}

}
