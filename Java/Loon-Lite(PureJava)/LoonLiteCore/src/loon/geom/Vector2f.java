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
package loon.geom;

import java.io.Serializable;

import loon.LSystem;
import loon.action.map.Field2D;
import loon.utils.Array;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;

public class Vector2f implements Serializable, XY {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1844534518528011982L;

	private static final Array<Vector2f> _VEC2_CACHE = new Array<Vector2f>();

	public final static Vector2f STATIC_ZERO = new Vector2f();

	public final static Vector2f TMP() {
		Vector2f temp = _VEC2_CACHE.pop();
		if (temp == null) {
			_VEC2_CACHE.add(temp = new Vector2f(0, 0));
		}
		return temp;
	}

	public final static boolean isNan(Vector2f v) {
		return MathUtils.isNan(v.x) || MathUtils.isNan(v.y);
	}

	public final static Vector2f ZERO() {
		return new Vector2f(0);
	}

	public final static Vector2f HALF() {
		return new Vector2f(0.5f, 0.5f);
	}

	public final static Vector2f ONE() {
		return new Vector2f(1);
	}

	public final static Vector2f AXIS_X() {
		return new Vector2f(1f, 0f);
	}

	public final static Vector2f AXIS_Y() {
		return new Vector2f(0f, 1f);
	}

	public final static Vector2f RIGHT() {
		return AXIS_X();
	}

	public final static Vector2f LEFT() {
		return new Vector2f(-1f, 0f);
	}

	public final static Vector2f UP() {
		return new Vector2f(0f, -1f);
	}

	public final static Vector2f DOWN() {
		return AXIS_Y();
	}

	public final static float angleTo(Vector2f pos) {
		float theta = MathUtils.toDegrees(MathUtils.atan2(pos.y, pos.x));
		if ((theta < -360) || (theta > 360)) {
			theta = theta % 360;
		}
		if (theta < 0) {
			theta = 360 + theta;
		}
		return theta;
	}

	public final static Vector2f at(float x, float y) {
		return new Vector2f(x, y);
	}

	public final static Vector2f at(XY xy) {
		return new Vector2f(xy.getX(), xy.getY());
	}

	public final static Vector2f rotationRight(Vector2f v) {
		return new Vector2f(v.y, -v.x);
	}

	public final static Vector2f rotationLeft(Vector2f v) {
		return new Vector2f(-v.y, v.x);
	}

	public static Vector2f rotate90CCW(Vector2f v) {
		return rotationLeft(v);
	}

	public static Vector2f rotate90CW(Vector2f v) {
		return rotationRight(v);
	}

	public final static Vector2f fromAngle(float angle) {
		return new Vector2f(MathUtils.cos(angle), MathUtils.sin(angle));
	}

	public final static Vector2f sum(Vector2f a, Vector2f b) {
		Vector2f answer = new Vector2f(a);
		return answer.add(b);
	}

	public final static Vector2f mult(Vector2f vector, float scalar) {
		Vector2f answer = new Vector2f(vector);
		return answer.mul(scalar);
	}

	public final static Vector2f cpy(Vector2f pos) {
		Vector2f newSVector2 = new Vector2f();

		newSVector2.x = pos.x;
		newSVector2.y = pos.y;

		return newSVector2;
	}

	public final static float len(Vector2f pos) {
		return MathUtils.sqrt(pos.x * pos.x + pos.y * pos.y);
	}

	public final static float len2(Vector2f pos) {
		return pos.x * pos.x + pos.y * pos.y;
	}

	public final static Vector2f set(Vector2f pos, Vector2f vectorB) {
		pos.x = vectorB.x;
		pos.y = vectorB.y;
		return pos;
	}

	public final static Vector2f set(Vector2f pos, float x, float y) {
		pos.x = x;
		pos.y = y;
		return pos;
	}

	public final Vector2f subNew(Vector2f vectorB) {
		return subNew(this, vectorB);
	}

	public final static Vector2f subNew(Vector2f pos, Vector2f vectorB) {
		return at(pos.x - vectorB.x, pos.y - vectorB.y);
	}

	public final static Vector2f sub(Vector2f pos, Vector2f vectorB) {
		pos.x -= vectorB.x;
		pos.y -= vectorB.y;
		return pos;
	}

	public final static Vector2f nor(Vector2f pos) {
		float len = len(pos);
		if (len != 0) {
			pos.x /= len;
			pos.y /= len;
		}
		return pos;
	}

	public final static Vector2f addNew(Vector2f pos, Vector2f vectorB) {
		return at(pos.x + vectorB.x, pos.y + vectorB.y);
	}

	public final static Vector2f add(Vector2f pos, Vector2f vectorB) {
		pos.x += vectorB.x;
		pos.y += vectorB.y;
		return pos;
	}

	public final static Vector2f add(Vector2f pos, float x, float y) {
		pos.x += x;
		pos.y += y;
		return pos;
	}

	public final static Vector2f smoothStep(Vector2f a, Vector2f b, float amount) {
		return new Vector2f(MathUtils.smoothStep(a.x, b.x, amount), MathUtils.smoothStep(a.y, b.y, amount));
	}

	public final static Vector2f transform(Vector2f value, Quaternion rotation) {
		return transform(value, rotation, null);
	}

	public final static Vector2f transform(Vector2f value, Quaternion rotation, Vector2f result) {
		if (result == null) {
			result = new Vector2f();
		}
		Vector3f rot1 = new Vector3f(rotation.x + rotation.x, rotation.y + rotation.y, rotation.z + rotation.z);
		Vector3f rot2 = new Vector3f(rotation.x, rotation.x, rotation.w);
		Vector3f rot3 = new Vector3f(1, rotation.y, rotation.z);
		Vector3f rot4 = rot1.mul(rot2);
		Vector3f rot5 = rot1.mul(rot3);
		Vector2f v = new Vector2f();
		v.x = (value.x * (1f - rot5.y - rot5.z) + value.y * (rot4.y - rot4.z));
		v.y = (value.x * (rot4.y + rot4.z) + value.y * (1f - rot4.x - rot5.z));
		result.x = v.x;
		result.y = v.y;
		return result;
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

	public final static Vector2f mul(Vector2f pos, float scalar) {
		pos.x *= scalar;
		pos.y *= scalar;
		return pos;
	}

	public final static Vector2f direction(Vector2f v1, Vector2f v2) {
		Vector2f vector = v2.sub(v1);
		vector.normalizeSelf();
		return vector;
	}

	public final static float dst(Vector2f pos, Vector2f vectorB) {
		final float x_d = vectorB.x - pos.x;
		final float y_d = vectorB.y - pos.y;
		return MathUtils.sqrt(x_d * x_d + y_d * y_d);
	}

	public final static float dst(Vector2f pos, float x, float y) {
		final float x_d = x - pos.x;
		final float y_d = y - pos.y;
		return MathUtils.sqrt(x_d * x_d + y_d * y_d);
	}

	public final static float dst2(Vector2f pos, Vector2f vectorB) {
		final float x_d = vectorB.x - pos.x;
		final float y_d = vectorB.y - pos.y;
		return x_d * x_d + y_d * y_d;
	}

	public final static Vector2f sub(Vector2f pos, float x, float y) {
		pos.x -= x;
		pos.y -= y;
		return pos;
	}

	public final static float crs(Vector2f pos, Vector2f vectorB) {
		return pos.x * vectorB.y - pos.y * vectorB.x;
	}

	public final static float crs(Vector2f pos, float x, float y) {
		return pos.x * y - pos.y * x;
	}

	public final static Vector2f rotate(Vector2f pos, Vector2f origin, float angle) {
		float rad = MathUtils.toRadians(angle);

		Vector2f newVector = new Vector2f();

		pos.x += -origin.x;
		pos.y += -origin.y;
		newVector.x = (pos.x * MathUtils.cos(rad) - (pos.y * MathUtils.sin(rad)));
		newVector.y = (MathUtils.sin(rad * pos.x) + (MathUtils.cos(rad * pos.y)));

		newVector.x += origin.x;
		newVector.y += origin.y;

		return newVector;
	}

	public final static Vector2f rotate(Vector2f pos, float angle) {
		float rad = MathUtils.toRadians(angle);
		float cos = MathUtils.cos(rad);
		float sin = MathUtils.sin(rad);

		float newX = pos.x * cos - pos.y * sin;
		float newY = pos.x * sin + pos.y * cos;

		pos.x = newX;
		pos.y = newY;

		return pos;
	}

	public final static Vector2f lerp(Vector2f pos, Vector2f target, float alpha) {
		Vector2f r = mul(pos, 1.0f - alpha);
		add(r, mul(cpy(target), alpha));
		return r;
	}

	public final static float dst2(float x1, float y1, float x2, float y2) {
		final float x_d = x2 - x1;
		final float y_d = y2 - y1;
		return x_d * x_d + y_d * y_d;
	}

	public final static Vector2f polar(float len, float angle) {
		return new Vector2f(len * MathUtils.cos(angle / MathUtils.DEG_TO_RAD),
				len * MathUtils.sin(angle / MathUtils.DEG_TO_RAD));
	}

	public float x;

	public float y;

	public Vector2f() {
		this(0, 0);
	}

	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector2f(XY v) {
		set(v);
	}

	public Vector2f cpy() {
		return new Vector2f(this);
	}

	public final float length() {
		return MathUtils.sqrt(x * x + y * y);
	}

	public final float lengthSquared() {
		return (x * x + y * y);
	}

	public float len() {
		return length();
	}

	public float len2() {
		return lengthSquared();
	}

	public Vector2f normalizeSelf() {
		float l = length();
		if (l == 0 || l == 1) {
			return this;
		}
		return set(x / l, y / l);
	}

	public Vector2f normalizeNew() {
		return nor(len());
	}

	public Vector2f norSelf() {
		return normalizeSelf();
	}

	public Vector2f nor() {
		return normalizeNew();
	}

	public Vector2f nor(float n) {
		return new Vector2f(this.x == 0 ? 0 : this.x / n, this.y == 0 ? 0 : this.y / n);
	}

	public final Vector2f mul(float s) {
		return new Vector2f(this.x * s, this.y * s);
	}

	public final Vector2f mul(float sx, float sy) {
		return new Vector2f(this.x * sx, this.y * sy);
	}

	public final Vector2f mulSelf(float scale) {
		return mulSelf(scale);
	}

	public final Vector2f mulSelf(float sx, float sy) {
		this.x *= sx;
		this.y *= sy;
		return this;
	}

	public Vector2f mulSelf(Affine2f mat) {
		float nx = this.x * mat.m00 + this.y * mat.m01 + mat.tx;
		float ny = this.x * mat.m10 + this.y * mat.m11 + mat.ty;
		this.x = nx;
		this.y = ny;
		return this;
	}

	public Vector2f mulSelf(Matrix3 mat) {
		float nx = this.x * mat.val[0] + this.y * mat.val[3] + mat.val[6];
		float ny = this.x * mat.val[1] + this.y * mat.val[4] + mat.val[7];
		this.x = nx;
		this.y = ny;
		return this;
	}

	public float max() {
		return MathUtils.max(this.x, this.y);
	}

	public float min() {
		return MathUtils.min(this.x, this.y);
	}

	public Vector2f smoothStep(Vector2f v, float amount) {
		return smoothStep(this, v, amount);
	}

	public Vector2f sub(float x, float y) {
		return new Vector2f(this.x - x, this.y - y);
	}

	public Vector2f sub(float v) {
		return new Vector2f(this.x - v, this.y - v);
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

	public Vector2f addSelfX(float x) {
		this.x = this.x + x;
		return this;
	}

	public Vector2f addSelfY(float y) {
		this.y = this.y + y;
		return this;
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

	public float getAngle() {
		return angleTo(this);
	}

	public float angle() {
		return getAngle();
	}

	public float angleRad(Vector2f v) {
		if (null == v) {
			return 0f;
		}
		return MathUtils.atan2(v.crs(this), v.dot(this));
	}

	public float angleDeg(Vector2f v) {
		if (null == v) {
			return getAngle();
		}
		float theta = MathUtils.toDegrees(MathUtils.atan2(v.crs(this), v.dot(this)));
		if ((theta < -360) || (theta > 360)) {
			theta = theta % 360;
		}
		if (theta < 0) {
			theta = 360 + theta;
		}
		return theta;
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

	public Vector2f rotate(float angle) {
		return cpy().rotateSelf(angle);
	}

	public Vector2f rotateX(float angle) {
		return cpy().rotateSelfX(angle);
	}

	public Vector2f rotateY(float angle) {
		return cpy().rotateSelfY(angle);
	}

	public Vector2f rotate(float cx, float cy, float angle) {
		return cpy().rotateSelf(cx, cy, angle);
	}

	public Vector2f rotateSelf(float cx, float cy, float angle) {

		float rad = MathUtils.toRadians(angle);
		float cos = MathUtils.cos(rad);
		float sin = MathUtils.sin(rad);

		float nx = cx + (this.x - cx) * MathUtils.cos(rad) - (this.y - cy) * sin;
		float ny = cy + (this.x - cx) * MathUtils.sin(rad) + (this.y - cy) * cos;

		return set(nx, ny);
	}

	public Vector2f rotateSelf(float angle) {

		float rad = MathUtils.toRadians(angle);
		float cos = MathUtils.cos(rad);
		float sin = MathUtils.sin(rad);

		float newX = this.x * cos - this.y * sin;
		float newY = this.x * sin + this.y * cos;

		this.x = newX;
		this.y = newY;

		return this;
	}

	public Vector2f rotateSelfX(float angle) {

		float rad = MathUtils.toRadians(angle);
		float cos = MathUtils.cos(rad);
		float sin = MathUtils.sin(rad);
		this.x = this.x * cos - this.y * sin;

		return this;
	}

	public Vector2f rotateSelfY(float angle) {

		float rad = MathUtils.toRadians(angle);
		float cos = MathUtils.cos(rad);
		float sin = MathUtils.sin(rad);
		this.y = this.x * sin + this.y * cos;

		return this;
	}

	public Vector2f(float value) {
		this(value, value);
	}

	public Vector2f(float[] coords) {
		x = coords[0];
		y = coords[1];
	}

	public Vector2f move_45D_up() {
		return move_45D_up(1);
	}

	public Vector2f move_45D_up(int multiples) {
		return move_multiples(Field2D.UP, multiples);
	}

	public Vector2f move_45D_left() {
		return move_45D_left(1);
	}

	public Vector2f move_45D_left(int multiples) {
		return move_multiples(Field2D.LEFT, multiples);
	}

	public Vector2f move_45D_right() {
		return move_45D_right(1);
	}

	public Vector2f move_45D_right(int multiples) {
		return move_multiples(Field2D.RIGHT, multiples);
	}

	public Vector2f move_45D_down() {
		return move_45D_down(1);
	}

	public Vector2f move_45D_down(int multiples) {
		return move_multiples(Field2D.DOWN, multiples);
	}

	public Vector2f move_up() {
		return move_up(1);
	}

	public Vector2f move_up(int multiples) {
		return move_multiples(Field2D.TUP, multiples);
	}

	public Vector2f move_left() {
		return move_left(1);
	}

	public Vector2f move_left(int multiples) {
		return move_multiples(Field2D.TLEFT, multiples);
	}

	public Vector2f move_right() {
		return move_right(1);
	}

	public Vector2f move_right(int multiples) {
		return move_multiples(Field2D.TRIGHT, multiples);
	}

	public Vector2f move_down() {
		return move_down(1);
	}

	public Vector2f move_down(int multiples) {
		return move_multiples(Field2D.TDOWN, multiples);
	}

	public Vector2f up(float v) {
		return cpy().set(this.x, this.y - v);
	}

	public Vector2f down(float v) {
		return cpy().set(this.x, this.y + v);
	}

	public Vector2f left(float v) {
		return cpy().set(this.x - v, this.y);
	}

	public Vector2f right(float v) {
		return cpy().set(this.x + v, this.y);
	}

	public Vector2f translate(float dx, float dy) {
		return cpy().set(this.x + dx, this.y + dy);
	}

	public Vector2f up() {
		return up(1f);
	}

	public Vector2f down() {
		return down(1f);
	}

	public Vector2f left() {
		return left(1f);
	}

	public Vector2f right() {
		return right(1f);
	}

	public Vector2f translateSelf(float dx, float dy) {
		return move(dx, dy);
	}

	public Vector2f translateSelfAngle(float angle, float amount) {
		return set(amount, 0).rotateSelf(angle);
	}

	public Vector2f translateSelfAngle(float angle, float x, float y) {
		return set(x, y).rotateSelf(angle);
	}

	public Vector2f move(float dx, float dy) {
		this.x += dx;
		this.y += dy;
		return this;
	}

	public Vector2f move(Vector2f pos) {
		this.x += pos.x;
		this.y += pos.y;
		return this;
	}

	public Vector2f move_multiples(int direction, int multiples) {
		if (multiples <= 0) {
			multiples = 1;
		}
		Vector2f v = Field2D.getDirection(direction);
		return move(v.x() * multiples, v.y() * multiples);
	}

	public Vector2f moveX(float x) {
		this.x += x;
		return this;
	}

	public Vector2f moveY(float y) {
		this.y += y;
		return this;
	}

	public Vector2f moveByAngle(int degAngle, float distance) {
		if (distance == 0) {
			return this;
		}
		float Angle = MathUtils.toRadians(degAngle);
		float dX = (MathUtils.cos(Angle) * distance);
		float dY = (-MathUtils.sin(Angle) * distance);
		int idX = MathUtils.round(dX);
		int idY = MathUtils.round(dY);
		return move(idX, idY);
	}

	public Vector2f move(float distance) {
		float angle = MathUtils.toRadians(getAngle());
		int x = MathUtils.round(getX() + MathUtils.cos(angle) * distance);
		int y = MathUtils.round(getY() + MathUtils.sin(angle) * distance);
		return setLocation(x, y);
	}

	public boolean nearlyCompare(Vector2f v, int range) {
		int dX = MathUtils.abs(x() - v.x());
		int dY = MathUtils.abs(y() - v.y());
		return (dX <= range) && (dY <= range);
	}

	public float[] getCoords() {
		return (new float[] { x, y });
	}

	public Vector2f setLocation(float x, float y) {
		return set(x, y);
	}

	public Vector2f setX(float x) {
		this.x = x;
		return this;
	}

	public Vector2f setY(float y) {
		this.y = y;
		return this;
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
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

	public Vector2f mul(Vector2f pos) {
		return new Vector2f(x * pos.x, y * pos.y);
	}

	public final void setZero() {
		x = 0f;
		y = 0f;
	}

	public final Vector2f set(float v) {
		return set(v, v);
	}

	public final Vector2f set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public final Vector2f set(XY v) {
		this.x = v.getX();
		this.y = v.getY();
		return this;
	}

	public final Vector2f set(Vector2f v) {
		this.x = v.x;
		this.y = v.y;
		return this;
	}

	public final Vector2f add(float v) {
		return new Vector2f(x + v, y + v);
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

	public final Vector2f setLength(float len) {
		len = len * len;
		float oldLength = lengthSquared();
		return (oldLength == 0 || oldLength == len) ? this : scaleSelf(MathUtils.sqrt(len / oldLength));
	}

	public final Vector2f setAngle(float radians) {
		this.set(length(), 0f);
		this.rotateSelf(radians);
		return this;
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

	public Vector2f lerp(Vector2f target, float alpha) {
		Vector2f r = this.mul(1f - alpha);
		r.add(target.tmp().mul(alpha));
		return r;
	}

	public Vector2f lerpSelf(Vector2f target, float alpha) {
		final float oneMinusAlpha = 1f - alpha;
		float x = (this.x * oneMinusAlpha) + (target.x * alpha);
		float y = (this.y * oneMinusAlpha) + (target.y * alpha);
		return set(x, y);
	}

	public Vector2f lerpSelf(float x, float y, float alpha) {
		this.x += alpha * (x - this.x);
		this.y += alpha * (y - this.y);
		return this;
	}

	public Vector2f lerp(float x, float y, float alpha) {
		return cpy().lerpSelf(x, y, alpha);
	}

	public final Vector2f abs() {
		return new Vector2f(MathUtils.abs(x), MathUtils.abs(y));
	}

	public final void absLocal() {
		x = MathUtils.abs(x);
		y = MathUtils.abs(y);
	}

	public Vector2f random() {
		this.x = MathUtils.random(0f, LSystem.viewSize.getWidth());
		this.y = MathUtils.random(0f, LSystem.viewSize.getHeight());
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + NumberUtils.floatToIntBits(x);
		result = prime * result + NumberUtils.floatToIntBits(y);
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
		if (NumberUtils.floatToIntBits(x) != NumberUtils.floatToIntBits(other.x))
			return false;
		if (NumberUtils.floatToIntBits(y) != NumberUtils.floatToIntBits(other.y))
			return false;
		return true;
	}

	public boolean epsilonEquals(Vector2f other, float epsilon) {
		if (other == null)
			return false;
		if (MathUtils.abs(other.x - x) > epsilon)
			return false;
		if (MathUtils.abs(other.y - y) > epsilon)
			return false;
		return true;
	}

	public boolean epsilonEquals(float x, float y, float epsilon) {
		if (MathUtils.abs(x - this.x) > epsilon)
			return false;
		if (MathUtils.abs(y - this.y) > epsilon)
			return false;
		return true;
	}

	public boolean isUnit() {
		return isUnit(0.000000001f);
	}

	public boolean isUnit(final float margin) {
		return MathUtils.abs(len2() - 1f) < margin;
	}

	public boolean isZero() {
		return x == 0 && y == 0;
	}

	public boolean isZero(final float margin) {
		return len2() < margin;
	}

	public boolean isOnLine(Vector2f other) {
		return MathUtils.isZero(x * other.y - y * other.x);
	}

	public boolean isOnLine(Vector2f other, float epsilon) {
		return MathUtils.isZero(x * other.y - y * other.x, epsilon);
	}

	public boolean isCollinear(Vector2f other, float epsilon) {
		return isOnLine(other, epsilon) && dot(other) > 0f;
	}

	public boolean isCollinear(Vector2f other) {
		return isOnLine(other) && dot(other) > 0f;
	}

	public boolean isCollinearOpposite(Vector2f other, float epsilon) {
		return isOnLine(other, epsilon) && dot(other) < 0f;
	}

	public boolean isCollinearOpposite(Vector2f other) {
		return isOnLine(other) && dot(other) < 0f;
	}

	public boolean isPerpendicular(Vector2f vector) {
		return MathUtils.isZero(dot(vector));
	}

	public boolean isPerpendicular(Vector2f vector, float epsilon) {
		return MathUtils.isZero(dot(vector), epsilon);
	}

	public boolean hasSameDirection(Vector2f vector) {
		return dot(vector) > 0;
	}

	public boolean hasOppositeDirection(Vector2f vector) {
		return dot(vector) < 0;
	}

	public static String pointToString(float x, float y) {
		return MathUtils.toString(x) + "," + MathUtils.toString(y);
	}

	public final Vector2f inverse() {
		return new Vector2f(-this.x, -this.y);
	}

	public final Vector2f inverseSelf() {
		this.x = -this.x;
		this.y = -this.y;
		return this;
	}

	public final Vector2f addSelf(Vector2f v) {
		this.x += v.x;
		this.y += v.y;
		return this;
	}

	public final Vector2f addSelf(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public Vector2f subtract(float x, float y) {
		return add(-x, -y);
	}

	public Vector2f negateSelf() {
		return set(-x, -y);
	}

	public float zcross(float zx, float zy) {
		return (this.x * zy) - (this.y * zx);
	}

	public float zcross(Vector2f v) {
		return (this.x * v.y) - (this.y * v.x);
	}

	public float dot(float x, float y) {
		return this.x * x + this.y * y;
	}

	public float distance(Vector2f v) {
		return MathUtils.sqrt(distanceSquared(v));
	}

	public float distanceSquared(Vector2f v) {
		return (v.x - x) * (v.x - x) + (v.y - y) * (v.y - y);
	}

	public Vector2f perpendicular() {
		return new Vector2f(y, -x);
	}

	public Vector2f perpendicularSelf() {
		return set(y, x);
	}

	public Vector2f projectSelf(Vector2f v) {
		return scaleSelf(dot(v) / v.lengthSquared());
	}

	public boolean parallel(Vector2f a, Vector2f b) {
		Vector2f rotated = rotationLeft(a);
		return rotated.dot(b) == 0f;
	}

	public Vector2f scaleSelf(float s) {
		return scaleSelf(s, s);
	}

	public Vector2f scaleSelf(float sx, float sy) {
		return set(x * sx, y * sy);
	}

	public Vector2f reflect(Vector2f axis) {
		return project(axis).scale(2).subtract(this);
	}

	public Vector2f subtract(Vector2f v) {
		return add(-v.x, -v.y);
	}

	public Vector2f scale(float s) {
		return mul(s);
	}

	public Vector2f scale(float sx, float sy) {
		return mul(x * sx, y * sy);
	}

	public Vector2f project(Vector2f v) {
		return mul(dot(v) / v.lengthSquared());
	}

	public Vector2f reflectSelf(Vector2f axis) {
		return set(project(axis).scaleSelf(2).subtractSelf(this));
	}

	public Vector2f subtractSelf(Vector2f v) {
		return subtractSelf(v.x, v.y);
	}

	public Vector2f subtractSelf(float x, float y) {
		return addSelf(-x, -y);
	}

	public float cross(final Vector2f v) {
		return this.x * v.y - v.x * this.y;
	}

	public float lenManhattan() {
		return MathUtils.abs(this.x) + MathUtils.abs(this.y);
	}

	public float[] toFloat() {
		return new float[] { x, y };
	}

	public int[] toInt() {
		return new int[] { x(), y() };
	}

	public String toCSS() {
		return this.x + "px " + this.y + "px";
	}

	@Override
	public final String toString() {
		return "(" + x + "," + y + ")";
	}

}
