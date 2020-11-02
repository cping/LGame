package org.loon.framework.javase.game.core.graphics.component;

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
public class Speed {

	private double dx = 0.0D;

	private double dy = 0.0D;

	private int direction = 0;

	private double length;

	public Speed() {
	}

	public Speed(int direction, double length) {
		this.set(direction, length);
	}

	public void set(int direction, double length) {
		this.length = length;
		this.direction = direction;
		this.dx = (length * Math.cos(Math.toRadians(direction)));
		this.dy = (length * Math.sin(Math.toRadians(direction)));
	}

	public void setDirection(int direction) {
		this.direction = direction;
		this.dx = (this.length * Math.cos(Math.toRadians(direction)));
		this.dy = (this.length * Math.sin(Math.toRadians(direction)));
	}

	public void add(Speed other) {
		this.dx += other.dx;
		this.dy += other.dy;
		this.direction = (int) Math.toDegrees(Math.atan2(this.dy, this.dx));
		this.length = Math.sqrt(this.dx * this.dx + this.dy * this.dy);
	}

	public double getX() {
		return this.dx;
	}

	public double getY() {
		return this.dy;
	}

	public int getDirection() {
		return this.direction;
	}

	public double getLength() {
		return this.length;
	}

	public Speed copy() {
		Speed copy = new Speed();
		copy.dx = this.dx;
		copy.dy = this.dy;
		copy.direction = this.direction;
		copy.length = this.length;
		return copy;
	}
}
