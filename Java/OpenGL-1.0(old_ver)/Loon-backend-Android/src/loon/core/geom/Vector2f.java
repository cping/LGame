/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.core.geom;

import java.io.Serializable;

import loon.action.map.Field2D;
import loon.utils.MathUtils;

public class Vector2f implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1844534518528011982L;

	public final static Vector2f TMP() {
		return new Vector2f();
	}

	public final static Vector2f X() {
		return new Vector2f(1, 0);
	}

	public final static Vector2f Y() {
		return new Vector2f(0, 1);
	}

	public final static Vector2f ZERO() {
		return new Vector2f(0, 0);
	}

	public final static Vector2f Zero = new Vector2f();

	public float x;

	public float y;

	public Vector2f() {

	}

	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector2f(Vector2f v) {
		set(v);
	}

	public Vector2f cpy() {
		return new Vector2f(this);
	}

	public float len() {
		return MathUtils.sqrt(x * x + y * y);
	}

	public float len2() {
		return x * x + y * y;
	}

	public Vector2f nor() {
		float len = len();
		if (len != 0) {
			x /= len;
			y /= len;
		}
		return this;
	}

	public final Vector2f mul(float a) {
		return new Vector2f(x * a, y * a);
	}

	public Vector2f sub(float x, float y) {
		return new Vector2f(this.x - x, this.y - y);
	}

	public Vector2f div() {
		return new Vector2f(x / 2, y / 2);
	}

	public Vector2f div(float v) {
		return new Vector2f(x / v, y / v);
	}

	public Vector2f div(Vector2f v) {
		return new Vector2f(x / v.x, y / v.y);
	}
	
	public Vector2f add(float x, float y) {
		return new Vector2f(this.x + x, this.y + y);
	}

	public float dot(Vector2f v) {
		return x * v.x + y * v.y;
	}

	public float dst(Vector2f v) {
		final float x_d = v.x - x;
		final float y_d = v.y - y;
		return MathUtils.sqrt(x_d * x_d + y_d * y_d);
	}

	public float dst(float x, float y) {
		final float x_d = x - this.x;
		final float y_d = y - this.y;
		return MathUtils.sqrt(x_d * x_d + y_d * y_d);
	}

	public float dst2(Vector2f v) {
		final float x_d = v.x - x;
		final float y_d = v.y - y;
		return x_d * x_d + y_d * y_d;
	}

	public float dst2(float x, float y) {
		final float x_d = x - this.x;
		final float y_d = y - this.y;
		return x_d * x_d + y_d * y_d;
	}

	public Vector2f tmp() {
		return TMP().set(this);
	}

	public float crs(Vector2f v) {
		return this.x * v.y - this.y * v.x;
	}

	public float crs(float x, float y) {
		return this.x * y - this.y * x;
	}

	public float angle() {
		float angle = MathUtils.atan2(y, x) * MathUtils.RAD_TO_DEG;
		if (angle < 0) {
			angle += 360;
		}
		return angle;
	}

	public Vector2f rotate(float angle) {
		float rad = angle * MathUtils.DEG_TO_RAD;
		float cos = MathUtils.cos(rad);
		float sin = MathUtils.sin(rad);

		float newX = this.x * cos - this.y * sin;
		float newY = this.x * sin + this.y * cos;

		this.x = newX;
		this.y = newY;

		return this;
	}

	public Vector2f lerp(Vector2f target, float alpha) {
		Vector2f r = this.mul(1.0f - alpha);
		r.add(target.tmp().mul(alpha));
		return r;
	}

	public Vector2f(float value) {
		this(value, value);
	}

	public Vector2f(float[] coords) {
		x = coords[0];
		y = coords[1];
	}

	public void move(Vector2f vector2D) {
		this.x += vector2D.x;
		this.y += vector2D.y;
	}

	public void move_multiples(int direction, int multiples) {
		if (multiples <= 0) {
			multiples = 1;
		}
		Vector2f v = Field2D.getDirection(direction);
		move(v.x() * multiples, v.y() * multiples);
	}

	public void moveX(int x) {
		this.x += x;
	}

	public void moveY(int y) {
		this.y += y;
	}

	public void moveByAngle(int degAngle, float distance) {
		if (distance == 0) {
			return;
		}
		float Angle = MathUtils.toRadians(degAngle);
		float dX = (MathUtils.cos(Angle) * distance);
		float dY = (-MathUtils.sin(Angle) * distance);
		int idX = MathUtils.round(dX);
		int idY = MathUtils.round(dY);
		move(idX, idY);
	}

	public void move(float x, float y) {
		this.x += x;
		this.y += y;
	}

	public void move(float distance) {
		float angle = MathUtils.toRadians(getAngle());
		int x = MathUtils.round(getX() + MathUtils.cos(angle) * distance);
		int y = MathUtils.round(getY() + MathUtils.sin(angle) * distance);
		setLocation(x, y);
	}

	public boolean nearlyCompare(Vector2f v, int range) {
		int dX = MathUtils.abs(x() - v.x());
		int dY = MathUtils.abs(y() - v.y());
		return (dX <= range) && (dY <= range);
	}

	public int angle(Vector2f v) {
		int dx = v.x() - x();
		int dy = v.y() - y();
		int adx = MathUtils.abs(dx);
		int ady = MathUtils.abs(dy);
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
		float rwinkel = MathUtils.atan(ady / adx);
		float dwinkel = 0.0f;
		if ((dx > 0) && (dy > 0)) {
			dwinkel = MathUtils.toDegrees(rwinkel);
		} else if ((dx < 0) && (dy > 0)) {
			dwinkel = (180.0f - MathUtils.toDegrees(rwinkel));
		} else if ((dx > 0) && (dy < 0)) {
			dwinkel = (360.0f - MathUtils.toDegrees(rwinkel));
		} else if ((dx < 0) && (dy < 0)) {
			dwinkel = (180.0f + MathUtils.toDegrees(rwinkel));
		}
		int iwinkel = (int) dwinkel;
		if (iwinkel == 360) {
			iwinkel = 0;
		}
		return iwinkel;
	}

	public float getAngle() {
		float theta = MathUtils.toDegrees(MathUtils.atan2(y, x));
		if ((theta < -360) || (theta > 360)) {
			theta = theta % 360;
		}
		if (theta < 0) {
			theta = 360 + theta;
		}
		return theta;
	}

	public float[] getCoords() {
		return (new float[] { x, y });
	}

	public void setLocation(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public int x() {
		return (int) x;
	}

	public int y() {
		return (int) y;
	}

	public Vector2f reverse() {
		x = -x;
		y = -y;
		return this;
	}

	public static Vector2f sum(Vector2f a, Vector2f b) {
		Vector2f answer = new Vector2f(a);
		return answer.add(b);
	}

	public static Vector2f mult(Vector2f vector, float scalar) {
		Vector2f answer = new Vector2f(vector);
		return answer.mul(scalar);
	}

	public float cross(final Vector2f v) {
		return this.x * v.y - v.x * this.y;
	}

	public float lenManhattan() {
		return Math.abs(this.x) + Math.abs(this.y);
	}

	public static Vector2f cpy(Vector2f vectorA) {
		Vector2f newSVector2 = new Vector2f();

		newSVector2.x = vectorA.x;
		newSVector2.y = vectorA.y;

		return newSVector2;
	}

	public static float len(Vector2f vectorA) {
		return MathUtils.sqrt(vectorA.x * vectorA.x + vectorA.y * vectorA.y);
	}

	public static float len2(Vector2f vectorA) {
		return vectorA.x * vectorA.x + vectorA.y * vectorA.y;
	}

	public static Vector2f set(Vector2f vectorA, Vector2f vectorB) {
		vectorA.x = vectorB.x;
		vectorA.y = vectorB.y;
		return vectorA;
	}

	public static Vector2f set(Vector2f vectorA, float x, float y) {
		vectorA.x = x;
		vectorA.y = y;
		return vectorA;
	}

	public static Vector2f sub(Vector2f vectorA, Vector2f vectorB) {
		vectorA.x -= vectorB.x;
		vectorA.y -= vectorB.y;
		return vectorA;
	}

	public Vector2f nor(float n) {
		return new Vector2f(x == 0 ? 0 : x / n, y == 0 ? 0 : y / n);
	}
	
	public static Vector2f nor(Vector2f vectorA) {
		float len = len(vectorA);
		if (len != 0) {
			vectorA.x /= len;
			vectorA.y /= len;
		}
		return vectorA;
	}

	public static Vector2f add(Vector2f vectorA, Vector2f vectorB) {
		vectorA.x += vectorB.x;
		vectorA.y += vectorB.y;
		return vectorA;
	}

	public static Vector2f add(Vector2f vectorA, float x, float y) {
		vectorA.x += x;
		vectorA.y += y;
		return vectorA;
	}

	public Vector2f mul(Vector2f vectorA) {
		return new Vector2f(x * vectorA.x, y * vectorA.y);
	}

	public static Vector2f mul(Vector2f vectorA, float scalar) {
		vectorA.x *= scalar;
		vectorA.y *= scalar;
		return vectorA;
	}

	public static float dst(Vector2f vectorA, Vector2f vectorB) {
		final float x_d = vectorB.x - vectorA.x;
		final float y_d = vectorB.y - vectorA.y;
		return MathUtils.sqrt(x_d * x_d + y_d * y_d);
	}

	public static float dst(Vector2f vectorA, float x, float y) {
		final float x_d = x - vectorA.x;
		final float y_d = y - vectorA.y;
		return MathUtils.sqrt(x_d * x_d + y_d * y_d);
	}

	public static float dst2(Vector2f vectorA, Vector2f vectorB) {
		final float x_d = vectorB.x - vectorA.x;
		final float y_d = vectorB.y - vectorA.y;
		return x_d * x_d + y_d * y_d;
	}

	public static Vector2f sub(Vector2f vectorA, float x, float y) {
		vectorA.x -= x;
		vectorA.y -= y;
		return vectorA;
	}

	public static float crs(Vector2f vectorA, Vector2f vectorB) {
		return vectorA.x * vectorB.y - vectorA.y * vectorB.x;
	}

	public static float crs(Vector2f vectorA, float x, float y) {
		return vectorA.x * y - vectorA.y * x;
	}

	public static float angleTo(Vector2f vectorA) {
		float angle = MathUtils.atan2(vectorA.y, vectorA.x)
				* MathUtils.RAD_TO_DEG;
		if (angle < 0) {
			angle += 360;
		}
		return angle;
	}

	public static Vector2f rotate(Vector2f vectorA, float angle) {
		float rad = angle * MathUtils.DEG_TO_RAD;
		float cos = MathUtils.cos(rad);
		float sin = MathUtils.sin(rad);

		float newX = vectorA.x * cos - vectorA.y * sin;
		float newY = vectorA.x * sin + vectorA.y * cos;

		vectorA.x = newX;
		vectorA.y = newY;

		return vectorA;
	}

	public static Vector2f lerp(Vector2f vectorA, Vector2f target, float alpha) {
		Vector2f r = mul(vectorA, 1.0f - alpha);
		add(r, mul(cpy(target), alpha));
		return r;
	}

	public static float dst2(float x1, float y1, float x2, float y2) {
		final float x_d = x2 - x1;
		final float y_d = y2 - y1;
		return x_d * x_d + y_d * y_d;
	}

	public final void setZero() {
		x = 0.0f;
		y = 0.0f;
	}

	public final Vector2f set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public final Vector2f set(Vector2f v) {
		this.x = v.x;
		this.y = v.y;
		return this;
	}

	public final Vector2f add(Vector2f v) {
		return new Vector2f(x + v.x, y + v.y);
	}

	public final Vector2f sub(Vector2f v) {
		return new Vector2f(x - v.x, y - v.y);
	}

	public final Vector2f negate() {
		return new Vector2f(-x, -y);
	}

	public final Vector2f negateLocal() {
		x = -x;
		y = -y;
		return this;
	}

	public final Vector2f addLocal(Vector2f v) {
		x += v.x;
		y += v.y;
		return this;
	}

	public final Vector2f addLocal(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public final Vector2f subLocal(Vector2f v) {
		x -= v.x;
		y -= v.y;
		return this;
	}

	public final Vector2f mulLocal(float a) {
		x *= a;
		y *= a;
		return this;
	}

	public final float length() {
		return MathUtils.sqrt(x * x + y * y);
	}

	public final float lengthSquared() {
		return (x * x + y * y);
	}

	public final float normalize() {
		float length = length();
		if (length < MathUtils.EPSILON) {
			return 0f;
		}

		float invLength = 1.0f / length;
		x *= invLength;
		y *= invLength;
		return length;
	}

	public final boolean isValid() {
		return x != Float.NaN && x != Float.NEGATIVE_INFINITY
				&& x != Float.POSITIVE_INFINITY && y != Float.NaN
				&& y != Float.NEGATIVE_INFINITY && y != Float.POSITIVE_INFINITY;
	}

	public final Vector2f abs() {
		return new Vector2f(MathUtils.abs(x), MathUtils.abs(y));
	}

	public final void absLocal() {
		x = MathUtils.abs(x);
		y = MathUtils.abs(y);
	}

	@Override
	public final Vector2f clone() {
		return new Vector2f(x, y);
	}

	@Override
	public final String toString() {
		return "(" + x + "," + y + ")";
	}

	public final static Vector2f abs(Vector2f a) {
		return new Vector2f(MathUtils.abs(a.x), MathUtils.abs(a.y));
	}

	public final static void absToOut(Vector2f a, Vector2f out) {
		out.x = MathUtils.abs(a.x);
		out.y = MathUtils.abs(a.y);
	}

	public final static float dot(Vector2f a, Vector2f b) {
		return a.x * b.x + a.y * b.y;
	}

	public final static float cross(Vector2f a, Vector2f b) {
		return a.x * b.y - a.y * b.x;
	}

	public final static Vector2f cross(Vector2f a, float s) {
		return new Vector2f(s * a.y, -s * a.x);
	}

	public final static void crossToOut(Vector2f a, float s, Vector2f out) {
		float tempy = -s * a.x;
		out.x = s * a.y;
		out.y = tempy;
	}

	public final static Vector2f cross(float s, Vector2f a) {
		return new Vector2f(-s * a.y, s * a.x);
	}

	public final static void crossToOut(float s, Vector2f a, Vector2f out) {
		float tempY = s * a.x;
		out.x = -s * a.y;
		out.y = tempY;
	}

	public final static void negateToOut(Vector2f a, Vector2f out) {
		out.x = -a.x;
		out.y = -a.y;
	}

	public final static Vector2f min(Vector2f a, Vector2f b) {
		return new Vector2f(a.x < b.x ? a.x : b.x, a.y < b.y ? a.y : b.y);
	}

	public final static Vector2f max(Vector2f a, Vector2f b) {
		return new Vector2f(a.x > b.x ? a.x : b.x, a.y > b.y ? a.y : b.y);
	}

	public final static void minToOut(Vector2f a, Vector2f b, Vector2f out) {
		out.x = a.x < b.x ? a.x : b.x;
		out.y = a.y < b.y ? a.y : b.y;
	}

	public final static void maxToOut(Vector2f a, Vector2f b, Vector2f out) {
		out.x = a.x > b.x ? a.x : b.x;
		out.y = a.y > b.y ? a.y : b.y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}

	public boolean equals(float x, float y) {
		return this.x == x && this.y == y;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector2f other = (Vector2f) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}
}
