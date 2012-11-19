package org.loon.framework.android.game.core.geom;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.loon.framework.android.game.action.map.Field2D;


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
public class Vector2D implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1844534518528011982L;

	public double x, y;

	public Vector2D(double value) {
		this(value, value);
	}

	public Vector2D() {
		this(0, 0);
	}

	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector2D(Vector2D vector2D) {
		this.x = vector2D.x;
		this.y = vector2D.y;
	}

	public void move(Vector2D vector2D) {
		this.x += vector2D.x;
		this.y += vector2D.y;
	}

	public void move_multiples(int direction, int multiples) {
		if (multiples <= 0) {
			multiples = 1;
		}
		Vector2D v = Field2D.getDirection(direction);
		move(v.x() * multiples, v.y() * multiples);
	}

	public void moveX(int x) {
		this.x += x;
	}

	public void moveY(int y) {
		this.y += y;
	}

	public void moveByAngle(int degAngle, double distance) {
		if (distance == 0) {
			return;
		}
		double Angle = Math.toRadians(degAngle);
		double dX = Math.cos(Angle) * distance;
		double dY = -Math.sin(Angle) * distance;
		int idX = (int) Math.round(dX);
		int idY = (int) Math.round(dY);
		move(idX, idY);
	}

	public void move(double x, double y) {
		this.x += x;
		this.y += y;
	}

	public boolean nearlyCompare(Vector2D v, int range) {
		int dX = Math.abs(x() - v.x());
		int dY = Math.abs(y() - v.y());
		return (dX <= range) && (dY <= range);
	}

	public int angle(Vector2D v) {
		int dx = v.x() - x();
		int dy = v.y() - y();
		int adx = Math.abs(dx);
		int ady = Math.abs(dy);
		if ((dy == 0) && (dx == 0)) {
			return 0;
		}
		if ((dy == 0) && (dx > 0)) {
			return 0;
		}
		if ((dy == 0) && (dx < 0)) {
			return 180;
		}
		if ((dy > 0) && (dx == 0)) {
			return 90;
		}
		if ((dy < 0) && (dx == 0)) {
			return 270;
		}
		double rwinkel = Math.atan(ady / adx);
		double dwinkel = 0.0D;
		if ((dx > 0) && (dy > 0)) {
			dwinkel = Math.toDegrees(rwinkel);
		} else if ((dx < 0) && (dy > 0)) {
			dwinkel = 180.0D - Math.toDegrees(rwinkel);
		} else if ((dx > 0) && (dy < 0)) {
			dwinkel = 360.0D - Math.toDegrees(rwinkel);
		} else if ((dx < 0) && (dy < 0)) {
			dwinkel = 180.0D + Math.toDegrees(rwinkel);
		}
		int iwinkel = (int) dwinkel;
		if (iwinkel == 360) {
			iwinkel = 0;
		}
		return iwinkel;
	}

	public double[] getCoords() {
		return (new double[] { x, y });
	}

	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public boolean equals(Object o) {
		if (o instanceof Vector2D) {
			Vector2D p = (Vector2D) o;
			return p.x == x && p.y == y;
		}
		return false;
	}

	public int hashCode() {
		return (int) (x + y);
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public int x() {
		return (int) x;
	}

	public int y() {
		return (int) y;
	}

	public Object clone() {
		return new Vector2D(x, y);
	}

	public void set(Vector2D other) {
		set(other.getX(), other.getY());
	}

	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector2D reverse() {
		x = -x;
		y = -y;
		return this;
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public double lengthSquared() {
		return (x * x) + (y * y);
	}

	public Vector2D add(Vector2D other) {
		double x = this.x + other.x;
		double y = this.y + other.y;
		return new Vector2D(x, y);
	}

	public Vector2D addThis(Vector2D other) {
		this.x += other.x;
		this.y += other.y;
		return this;
	}

	public static Vector2D sum(List<?> summands) {
		Vector2D result = new Vector2D(0, 0);
		for (Iterator<?> it = summands.iterator(); it.hasNext();) {
			Vector2D v = (Vector2D) it.next();
			result.addThis(v);
		}
		return result;
	}

	public static Vector2D sum(Vector2D a, Vector2D b) {
		Vector2D answer = new Vector2D(a);
		return answer.addThis(b);
	}

	public static Vector2D mean(List<?> points) {
		int n = points.size();
		if (n == 0) {
			return new Vector2D(0, 0);
		}
		return Vector2D.sum(points).scale(1.0 / n);

	}

	public Vector2D sub(Vector2D v) {
		x -= v.getX();
		y -= v.getY();
		return this;
	}

	public Vector2D subtract(Vector2D other) {
		double x = this.x - other.x;
		double y = this.y - other.y;
		return new Vector2D(x, y);
	}

	public double dot(Vector2D vec) {
		return (x * vec.x) + (y * vec.y);
	}

	public static double cross(Vector2D a, Vector2D b) {
		return a.cross(b);
	}

	public double cross(Vector2D vec) {
		return x * vec.y - y * vec.x;
	}

	public Vector2D multiply(double value) {
		return new Vector2D(value * x, value * y);
	}

	public double dotProduct(Vector2D other) {
		return other.x * x + other.y * y;
	}

	public Vector2D scale(double a) {
		x *= a;
		y *= a;
		return this;
	}

	public Vector2D normalize() {
		double magnitude = Math.sqrt(dotProduct(this));
		return new Vector2D(x / magnitude, y / magnitude);
	}

	public double level() {
		return Math.sqrt(dotProduct(this));
	}

	public double distanceSquared(Vector2D other) {
		double dx = other.getX() - getX();
		double dy = other.getY() - getY();

		return (dx * dx) + (dy * dy);
	}

	public double distance(Vector2D other) {
		return Math.sqrt(distanceSquared(other));
	}

	public Vector2D modulate(Vector2D other) {
		double x = this.x * other.x;
		double y = this.y * other.y;
		return new Vector2D(x, y);
	}

	public boolean equalsDelta(Vector2D other, double delta) {
		return (other.getX() - delta < x && other.getX() + delta > x
				&& other.getY() - delta < y && other.getY() + delta > y);
	}

	public static Vector2D difference(Vector2D first, Vector2D second) {
		Vector2D answer = new Vector2D(first);
		return answer.sub(second);
	}

	public void rotate90() {
		setLocation(y, -x);
	}

	public static Vector2D rotate90(Vector2D vec) {
		return new Vector2D(-vec.y, vec.x);
	}

	public static Vector2D rotate90R(Vector2D vec) {
		return new Vector2D(vec.y, -vec.x);
	}

	public static double dot(Vector2D a, Vector2D b) {
		return a.dot(b);
	}

	public static double crossZ(Vector2D a, Vector2D b) {
		return a.x * b.y - a.y * b.x;
	}

	public static Vector2D mult(Vector2D vector, double scalar) {
		Vector2D answer = new Vector2D(vector);
		return answer.scale(scalar);
	}

	public String toString() {
		return (new StringBuffer("[Vector2D x:")).append(x).append(" y:")
				.append(y).append("]").toString();
	}
}
