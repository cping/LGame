package org.loon.framework.javase.game.core.graphics.component;

import org.loon.framework.javase.game.core.graphics.component.Actor;

/**
 * Copyright 2008 - 2010
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

public abstract class ActorSpeed extends Actor {

	private Speed speed = new Speed();

	protected double x;

	protected double y;

	public ActorSpeed() {
	}

	public ActorSpeed(Speed speed) {
		this.speed = speed;
	}

	public void move() {
		this.x += this.speed.getX();
		this.y += this.speed.getY();
		if (this.x >= getLLayer().getWidth()) {
			this.x = 0.0D;
		}
		if (this.x < 0.0D) {
			this.x = (getLLayer().getWidth() - 1);
		}
		if (this.y >= getLLayer().getHeight()) {
			this.y = 0.0D;
		}
		if (this.y < 0.0D) {
			this.y = (getLLayer().getHeight() - 1);
		}
		setLocation(this.x, this.y);
	}

	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
		super.setLocation((int) x, (int) y);
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
		super.setLocation(x, y);
	}

	public void increaseSpeed(Speed s) {
		this.speed.add(s);
	}

	public Speed getSpeed() {
		return this.speed;
	}
}
