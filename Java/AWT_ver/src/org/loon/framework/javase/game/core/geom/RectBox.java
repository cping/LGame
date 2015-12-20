package org.loon.framework.javase.game.core.geom;

import java.awt.Point;
import java.awt.geom.Rectangle2D;

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
 * @version 0.1.2
 */
public class RectBox {

	public int x;

	public int y;

	public int width;

	public int height;

	public RectBox() {
		setBounds(0, 0, 0, 0);
	}

	public RectBox(double x, double y, double width, double height) {
		setBounds(x, y, width, height);
	}

	public RectBox(int x, int y, int width, int height) {
		setBounds(x, y, width, height);
	}

	public RectBox(RectBox rect) {
		setBounds(rect.x, rect.y, rect.width, rect.height);
	}

	public void setBounds(RectBox rect) {
		setBounds(rect.x, rect.y, rect.width, rect.height);
	}

	public void setBounds(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void setBounds(double x, double y, double width, double height) {
		this.x = (int) x;
		this.y = (int) y;
		this.width = (int) width;
		this.height = (int) height;
	}

	public void setLocation(RectBox r) {
		this.x = r.x;
		this.y = r.y;
	}

	public void setLocation(Point r) {
		this.x = r.x;
		this.y = r.y;
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
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

	public void copy(RectBox other) {
		this.x = other.x;
		this.y = other.y;
		this.width = other.width;
		this.height = other.height;
	}

	public int getMinX() {
		return getX();
	}

	public int getMinY() {
		return getY();
	}

	public int getMaxX() {
		return this.x + this.width;
	}

	public int getMaxY() {
		return this.y + this.height;
	}

	public int getRight() {
		return getMaxX();
	}

	public int getTop() {
		return getMaxY();
	}

	public int getMiddleX() {
		return this.x + this.width / 2;
	}

	public int getMiddleY() {
		return this.y + this.height / 2;
	}

	public double getCenterX() {
		return getX() + getWidth() / 2.0;
	}

	public double getCenterY() {
		return getY() + getHeight() / 2.0;
	}

	public static RectBox getIntersection(RectBox a, RectBox b) {
		int a_x = a.getX();
		int a_r = a.getRight();
		int a_y = a.getY();
		int a_t = a.getTop();
		int b_x = b.getX();
		int b_r = b.getRight();
		int b_y = b.getY();
		int b_t = b.getTop();
		int i_x = Math.max(a_x, b_x);
		int i_r = Math.min(a_r, b_r);
		int i_y = Math.max(a_y, b_y);
		int i_t = Math.min(a_t, b_t);
		return i_x < i_r && i_y < i_t ? new RectBox(i_x, i_y, i_r - i_x, i_t
				- i_y) : null;
	}

	public Rectangle2D getRectangle2D() {
		return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
	}

	public RectBox getRect() {
		return this;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public boolean equals(Object obj) {
		if (obj instanceof RectBox) {
			RectBox rect = (RectBox) obj;
			return equals(rect.x, rect.y, rect.width, rect.height);
		} else {
			return false;
		}
	}

	public boolean equals(int x, int y, int width, int height) {
		return (this.x == x && this.y == y && this.width == width && this.height == height);
	}

	public int getArea() {
		return width * height;
	}

	/**
	 * 检查是否包含指定坐标
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean contains(int x, int y) {
		return contains(x, y, 0, 0);
	}

	/**
	 * 检查是否包含指定坐标
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public boolean contains(int x, int y, int width, int height) {
		return (x >= this.x && y >= this.y
				&& ((x + width) <= (this.x + this.width)) && ((y + height) <= (this.y + this.height)));
	}

	/**
	 * 检查是否包含指定坐标
	 * 
	 * @param rect
	 * @return
	 */

	public boolean contains(RectBox rect) {
		return contains(rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * 设定矩形选框交集
	 * 
	 * @param rect
	 * @return
	 */

	public boolean intersects(RectBox rect) {
		return intersects(rect.x, rect.y, rect.width, rect.height);
	}

	public boolean intersects(int x, int y) {
		return intersects(0, 0, width, height);
	}

	/**
	 * 设定矩形选框交集
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public boolean intersects(int x, int y, int width, int height) {
		return x + width > this.x && x < this.x + this.width
				&& y + height > this.y && y < this.y + this.height;
	}

	/**
	 * 设定矩形选框交集
	 * 
	 * @param rect
	 */
	public void intersection(RectBox rect) {
		intersection(rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * 设定矩形选框交集
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void intersection(int x, int y, int width, int height) {
		int x1 = Math.max(this.x, x);
		int y1 = Math.max(this.y, y);
		int x2 = Math.min(this.x + this.width - 1, x + width - 1);
		int y2 = Math.min(this.y + this.height - 1, y + height - 1);
		setBounds(x1, y1, Math.max(0, x2 - x1 + 1), Math.max(0, y2 - y1 + 1));
	}

	/**
	 * 判断指定坐标是否在一条直线上
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public final boolean intersectsLine(final int x1, final int y1,
			final int x2, final int y2) {
		return contains(x1, y1) || contains(x2, y2);
	}

	/**
	 * 判定指定坐标是否位于当前RectBox内部
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean inside(int x, int y) {
		return (x >= this.x) && ((x - this.x) < this.width) && (y >= this.y)
				&& ((y - this.y) < this.height);
	}

	/**
	 * 返回当前的矩形选框交集
	 * 
	 * @param rect
	 * @return
	 */
	public RectBox getIntersection(RectBox rect) {
		int x1 = Math.max(x, rect.x);
		int x2 = Math.min(x + width, rect.x + rect.width);
		int y1 = Math.max(y, rect.y);
		int y2 = Math.min(y + height, rect.y + rect.height);
		return new RectBox(x1, y1, x2 - x1, y2 - y1);
	}

	/**
	 * 合并矩形选框
	 * 
	 * @param rect
	 */
	public void union(RectBox rect) {
		union(rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * 合并矩形选框
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void union(int x, int y, int width, int height) {
		int x1 = Math.min(this.x, x);
		int y1 = Math.min(this.y, y);
		int x2 = Math.max(this.x + this.width - 1, x + width - 1);
		int y2 = Math.max(this.y + this.height - 1, y + height - 1);
		setBounds(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
	}

}
