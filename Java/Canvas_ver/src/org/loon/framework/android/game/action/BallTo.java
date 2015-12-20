package org.loon.framework.android.game.action;

import org.loon.framework.android.game.core.graphics.component.ActorLayer;

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
// 投球事件
public class BallTo extends ActionEvent {

	private int radius;

	private int x, y;

	private double vx, vy;

	private double gravity;

	private boolean isComplete;

	public BallTo(int vx, int vy) {
		this(0, vx, vy, 0.3);
	}

	public BallTo(int vx, int vy, double g) {
		this(0, vx, vy, g);
	}

	public BallTo(int r, int vx, int vy, double g) {
		this.radius = r;
		this.setVelocity(vx, vy);
		this.gravity = g;
	}

	/**
	 * 判定球体是否处于指定半径之内
	 * 
	 * @param bx
	 * @param by
	 * @return
	 */
	public boolean inside(int bx, int by) {
		return ((x - bx) * (x - bx) + (y - by) * (y - by)) - radius * radius <= 0;
	}

	/**
	 * 判定球体是否与指定坐标相撞
	 * 
	 * @param bx
	 * @param by
	 * @return
	 */
	public boolean isCollide(int bx, int by) {
		int nx = bx;
		int ny = by;
		if (inside(nx, ny)) {
			double d = atan2(x - nx, y - ny);
			x = nx + (int) Math.round((double) radius * Math.sin(d));
			y = ny + (int) Math.round((double) radius * Math.cos(d));
			double d1 = Math.sqrt(vx * vx + vy * vy) * 0.90000000000000002D;
			double d2 = atan2(vx, -vy);
			d2 = 2D * d - d2;
			vx = d1 * Math.sin(d2);
			vy = d1 * Math.cos(d2);
			return true;
		}
		nx = bx + 60;
		if (inside(nx, ny)) {
			double d = Math.atan2(x - nx, y - ny);
			x = nx + (int) Math.round((double) radius * Math.sin(d));
			y = ny + (int) Math.round((double) radius * Math.cos(d));
			double d1 = Math.sqrt(vx * vx + vy * vy) * 0.90000000000000002D;
			double d2 = atan2(vx, -vy);
			d2 = 2D * d - d2;
			vx = d1 * Math.sin(d2);
			vy = d1 * Math.cos(d2);
			return true;
		} else {
			return false;
		}
	}

	public void gravity(double d) {
		double d1 = Math.sqrt(vx * vx + vy * vy) * (1.0D - d);
		double d2 = atan2(vx, vy);
		this.vx = d1 * Math.sin(d2);
		this.vy = d1 * Math.cos(d2);
	}

	public void setVelocity(double d, double d1) {
		this.vx = d;
		this.vy = d1;
	}

	public boolean move(int i, int j, int w, int h) {
		this.x = i;
		this.y = j;
		return !checkWall(w, h);
	}

	/**
	 * 进行球体运动检测
	 * 
	 * @param w
	 * @param h
	 */
	public void check(int w, int h) {
		x += vx;
		y += vy;
		move(x, y, w, h);
		if (Math.abs(vx) < 1.0D && Math.abs(vy) < 2D && y == h - radius) {
			isComplete = true;
			vx = 0.0D;
			vy = 0.0D;
		}
		vy += 0.80000000000000004D;
	}

	/**
	 * 判断是否碰触到墙壁边缘
	 * 
	 * @param w
	 * @param h
	 * @return
	 */
	public boolean checkWall(int w, int h) {
		if (x <= radius) {
			x = 2 * radius - x;
			vx = -vx;
			gravity(gravity);
			return true;
		}
		if (x >= w - radius) {
			x = 2 * (w - radius) - x;
			vx = -vx;
			gravity(gravity);
			return true;
		}
		if (y >= h - radius) {
			y = 2 * (h - radius) - y;
			vy = -vy;
			gravity(gravity);
			return true;
		} else {
			return false;
		}
	}

	private double atan2(double d, double d1) {
		if (d == 0.0D && d1 == 0.0D) {
			return Math.atan2(0.0D, 1.0D);
		} else {
			return Math.atan2(d, d1);
		}
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void onLoad() {
		x = original.getX();
		y = original.getY();
		isComplete = false;
	}

	public double getGravity() {
		return gravity;
	}

	public void setGravity(double gravity) {
		this.gravity = gravity;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public double getVx() {
		return vx;
	}

	public void setVx(double vx) {
		this.vx = vx;
	}

	public double getVy() {
		return vy;
	}

	public void setVy(double vy) {
		this.vy = vy;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void update(long elapsedTime) {
		ActorLayer layer = original.getLLayer();
		check(layer.getWidth(), layer.getHeight());
		original.setLocation(x, y);
	}

}
