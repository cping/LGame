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
import loon.action.collision.CollisionHelper;
import loon.action.map.Field2D;
import loon.utils.Array;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.reply.TChange;

public class Vector2f implements Serializable, SetXY, XY {

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

	public final static Vector2f at(String v) {
		if (StringUtils.isEmpty(v)) {
			return new Vector2f();
		}
		String[] result = StringUtils.split(v, LSystem.COMMA);
		int len = result.length;
		if (len > 1) {
			try {
				float x = Float.parseFloat(result[0].trim());
				float y = Float.parseFloat(result[1].trim());
				return new Vector2f(x, y);
			} catch (Exception ex) {
			}
		}
		return new Vector2f();
	}

	public final static Vector2f[] of(int n) {
		return of(n, 0f);
	}

	public final static Vector2f[] of(int n, float v) {
		Vector2f[] result = new Vector2f[n];
		for (int i = 0; i < n; i++) {
			result[i] = new Vector2f(v);
		}
		return result;
	}

	public final static boolean isNan(XY v) {
		return MathUtils.isNan(v.getX()) || MathUtils.isNan(v.getY());
	}

	public final static boolean greaterThan(XY a, XY b) {
		float a1 = MathUtils.mag(a.getX(), a.getY());
		float b1 = MathUtils.mag(b.getX(), b.getY());
		return a1 > b1;
	}

	public final static boolean lessThan(XY a, XY b) {
		float a1 = MathUtils.mag(a.getX(), a.getY());
		float b1 = MathUtils.mag(b.getX(), b.getY());
		return a1 < b1;
	}

	public final static boolean equal(XY a, XY b) {
		float a1 = MathUtils.mag(a.getX(), a.getY());
		float b1 = MathUtils.mag(b.getX(), b.getY());
		return a1 == b1;
	}

	public final static boolean greaterThanOrEqual(XY a, XY b) {
		return greaterThan(a, b) || equal(a, b);
	}

	public final static boolean lessThanOrEqual(XY a, XY b) {
		return lessThan(a, b) || equal(a, b);
	}

	public final static Vector2f ZERO() {
		return new Vector2f(0);
	}

	public final static Vector2f HALF() {
		return new Vector2f(0.5f, 0.5f);
	}

	public final static Vector2f ONE() {
		return new Vector2f(1f);
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

	public final static Vector2f bezierLerp(float alpha, Vector2f... points) {
		if (points.length < 2) {
			return points[0];
		}
		final Vector2f[] next = new Vector2f[points.length - 1];
		for (int i = 0; i < next.length; i++) {
			next[i] = lerp(points[i], points[i + 1], alpha);
		}
		return bezierLerp(alpha, next);
	}

	public final static Vector2f[] getBezierPoints(int count, Vector2f... points) {
		final Vector2f[] p1 = new Vector2f[count];
		p1[0] = new Vector2f(points[0]);
		for (int i = 1; i < count - 1; i++) {
			p1[i] = bezierLerp((float) i / count, points);
		}
		p1[count - 1] = new Vector2f(points[points.length - 1]);
		return p1;
	}

	public final static Vector2f reflect(Vector2f ri, Vector2f normal) {
		final Vector2f normalized = normal.nor();
		final float product = dot(ri, normalized);
		final Vector2f n = mul(normalized, product);
		return sub(ri, n.mulSelf(2));
	}

	public final static Vector2f intersect(Vector2f start, Vector2f dir, Vector2f p0, Vector2f p1) {
		Vector2f p = start, r = dir, q = p0, s = p1.sub(p0);
		final float cross = cross(r, s);
		if (cross == 0) {
			return null;
		}
		final Vector2f qmp = q.sub(p);
		final float u = cross(qmp, r) / cross;
		if (u < 0 || u > 1) {
			return null;
		}
		final float t = cross(qmp, s) / cross;
		if (t < 0) {
			return null;
		}
		return s.mul(u).addSelf(q);
	}

	public final static Vector2f bounce(Vector2f ri, Vector2f normal, float restitution, float friction) {
		friction = 1f - friction;
		final Vector2f normalized = normal.nor();
		final Vector2f axis = normalized.right();
		final float rest = dot(ri, normalized);
		final float fric = dot(ri, axis);
		final Vector2f result = Vector2f.ZERO();
		result.subtractSelf(normalized.x * rest * restitution, normalized.y * rest * restitution);
		result.addSelf(axis.x * fric * friction, axis.y * fric * friction);
		return result;
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

	public final static Vector2f all(float v) {
		return new Vector2f(v, v);
	}

	public final static Vector2f at(float x, float y) {
		return new Vector2f(x, y);
	}

	public final static Vector2f at(XY xy) {
		return new Vector2f(xy.getX(), xy.getY());
	}

	public final static Vector2f rotationRight(XY v) {
		return new Vector2f(v.getY(), -v.getX());
	}

	public final static Vector2f rotationLeft(XY v) {
		return new Vector2f(-v.getY(), v.getX());
	}

	public static Vector2f rotate90CCW(XY v) {
		return rotationLeft(v);
	}

	public static Vector2f rotate90CW(XY v) {
		return rotationRight(v);
	}

	public final static Vector2f fromAngle(float angle) {
		return new Vector2f(MathUtils.cos(angle), MathUtils.sin(angle));
	}

	public final static Vector2f fromDegreesAngle(float degAngle) {
		return fromAngle(MathUtils.toRadians(degAngle));
	}

	public final static Vector2f cpy(XY pos) {
		return at(pos.getX(), pos.getY());
	}

	public final static Vector2f nor(float x, float y) {
		Vector2f v = new Vector2f(x, y);
		float len = len(v);
		if (len != 0) {
			v.x /= len;
			v.y /= len;
		}
		return v;
	}

	public final static Vector2f nor(float x, float y, Vector2f o) {
		Vector2f v = new Vector2f(x, y);
		float len = len(v);
		if (len != 0) {
			o.x /= len;
			o.y /= len;
		}
		return o;
	}

	public final static Vector2f nor(XY pos) {
		return nor(pos.getX(), pos.getY(), pos == null ? new Vector2f() : new Vector2f(pos.getX(), pos.getY()));
	}

	public final static float len(XY pos) {
		return len(pos.getX(), pos.getY());
	}

	public final static float len2(XY pos) {
		return len2(pos.getX(), pos.getY());
	}

	public final static float len(float x, float y) {
		return MathUtils.sqrt(x * x + y * y);
	}

	public final static float len2(float x, float y) {
		return x * x + y * y;
	}

	public final static Vector2f set(Vector2f o, Vector2f d) {
		if (d == null) {
			return o;
		}
		return set(o, d.getX(), d.getY());
	}

	public final static Vector2f set(Vector2f o, float x, float y) {
		if (o == null) {
			return o;
		}
		o.x = x;
		o.y = y;
		return o;
	}

	public final static Vector2f add(Vector2f pos, Vector2f dst) {
		return add(pos, dst, new Vector2f());
	}

	public final static Vector2f add(Vector2f pos, Vector2f dst, Vector2f out) {
		out.x = pos.x + dst.x;
		out.y = pos.y + dst.y;
		return out;
	}

	public final static Vector2f add(Vector2f pos, float x, float y) {
		return add(pos, x, y, new Vector2f());
	}

	public final static Vector2f add(Vector2f pos, float x, float y, Vector2f out) {
		out.x = pos.x + x;
		out.y = pos.y + y;
		return out;
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
		return abs(a, new Vector2f());
	}

	public final static Vector2f abs(Vector2f a, Vector2f out) {
		if (a == null) {
			return new Vector2f();
		}
		out.x = MathUtils.abs(a.x);
		out.y = MathUtils.abs(a.y);
		return out;
	}

	public final static float dot(Vector2f a, Vector2f b) {
		return a.x * b.x + a.y * b.y;
	}

	public final static float cross(Vector2f a, Vector2f b) {
		return a.x * b.y - a.y * b.x;
	}

	public final static Vector2f cross(Vector2f a, float s) {
		return cross(a, s, new Vector2f());
	}

	public final static Vector2f cross(Vector2f a, float s, Vector2f out) {
		float tempy = -s * a.x;
		out.x = s * a.y;
		out.y = tempy;
		return out;
	}

	public final static Vector2f cross(float s, Vector2f a) {
		return cross(s, a, new Vector2f());
	}

	public final static Vector2f cross(float s, Vector2f a, Vector2f out) {
		if (a == null) {
			return new Vector2f();
		}
		float tempY = s * a.x;
		out.x = -s * a.y;
		out.y = tempY;
		return out;
	}

	public final static Vector2f negate(Vector2f a) {
		return negate(a, new Vector2f());
	}

	public final static Vector2f negate(Vector2f a, Vector2f out) {
		if (a == null) {
			return new Vector2f();
		}
		out.x = -a.x;
		out.y = -a.y;
		return out;
	}

	public final static Vector2f min(Vector2f a, Vector2f b) {
		return min(a, b, new Vector2f());
	}

	public final static Vector2f max(Vector2f a, Vector2f b) {
		return max(a, b, new Vector2f());
	}

	public final static Vector2f min(Vector2f a, Vector2f b, Vector2f out) {
		out.x = a.x < b.x ? a.x : b.x;
		out.y = a.y < b.y ? a.y : b.y;
		return out;
	}

	public final static Vector2f max(Vector2f a, Vector2f b, Vector2f out) {
		out.x = a.x > b.x ? a.x : b.x;
		out.y = a.y > b.y ? a.y : b.y;
		return out;
	}

	public final static Vector2f mul(Vector2f a, Vector2f b, Vector2f out) {
		out.x = a.x * b.x;
		out.y = a.y * b.y;
		return out;
	}

	public final static Vector2f mul(Vector2f a, Vector2f b) {
		return mul(a, b, new Vector2f());
	}

	public final static Vector2f mul(Vector2f pos, float scalar, Vector2f out) {
		out.x = pos.x * scalar;
		out.y = pos.y * scalar;
		return out;
	}

	public final static Vector2f mul(Vector2f pos, float scalar) {
		return mul(pos, scalar, new Vector2f());
	}

	public final static Vector2f div(Vector2f a, Vector2f b, Vector2f out) {
		out.x = a.x / b.x;
		out.y = a.y / b.y;
		return out;
	}

	public final static Vector2f div(Vector2f a, Vector2f b) {
		return mul(a, b, new Vector2f());
	}

	public final static Vector2f div(Vector2f pos, float scalar, Vector2f out) {
		out.x = pos.x / scalar;
		out.y = pos.y / scalar;
		return out;
	}

	public final static Vector2f div(Vector2f pos, float scalar) {
		return mul(pos, scalar, new Vector2f());
	}

	public final static Vector2f direction(Vector2f v1, Vector2f v2) {
		Vector2f vector = v2.sub(v1);
		vector.normalizeSelf();
		return vector;
	}

	public final static float dst(Vector2f pos, Vector2f dst) {
		final float x_d = dst.x - pos.x;
		final float y_d = dst.y - pos.y;
		return MathUtils.sqrt(x_d * x_d + y_d * y_d);
	}

	public final static float dst(Vector2f pos, float x, float y) {
		final float x_d = x - pos.x;
		final float y_d = y - pos.y;
		return MathUtils.sqrt(x_d * x_d + y_d * y_d);
	}

	public final static float dst2(Vector2f pos, Vector2f dst) {
		final float x_d = dst.x - pos.x;
		final float y_d = dst.y - pos.y;
		return x_d * x_d + y_d * y_d;
	}

	public final static Vector2f sub(Vector2f pos, float x, float y, Vector2f out) {
		out.x = pos.x - x;
		out.y = pos.y - y;
		return out;
	}

	public final static Vector2f sub(Vector2f pos, float x, float y) {
		return sub(pos, x, y, new Vector2f());
	}

	public final static Vector2f sub(Vector2f a, Vector2f b, Vector2f out) {
		out.x = a.x - b.x;
		out.y = a.y - b.y;
		return out;
	}

	public final static Vector2f sub(Vector2f a, Vector2f b) {
		return sub(a, b, new Vector2f());
	}

	public final static float crs(Vector2f pos, Vector2f dst) {
		return pos.x * dst.y - pos.y * dst.x;
	}

	public final static float crs(Vector2f pos, float x, float y) {
		return pos.x * y - pos.y * x;
	}

	public final static Vector2f rotate(Vector2f pos, Vector2f origin, float angle) {
		return rotate(pos, origin, angle, true);
	}

	public final static Vector2f rotate(Vector2f pos, Vector2f origin, float angle, boolean clockWise) {

		float rad = MathUtils.toRadians(angle);
		float angleRadians = clockWise ? rad : rad * -1;
		Vector2f newVector = new Vector2f();

		pos.x += -origin.x;
		pos.y += -origin.y;
		newVector.x = (pos.x * MathUtils.cos(angleRadians) - (pos.y * MathUtils.sin(angleRadians)));
		newVector.y = (MathUtils.sin(angleRadians * pos.x) + (MathUtils.cos(angleRadians * pos.y)));

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
		if (v != null)
			set(v);
	}

	public Vector2f(float value) {
		this(value, value);
	}

	public Vector2f(float[] coords) {
		if (coords != null)
			set(coords[0], coords[1]);
	}

	public Vector2f cpy() {
		return new Vector2f(this);
	}

	public Vector2f limit(float max) {
		if (lengthSquared() > max * max) {
			return nor().mulSelf(max);
		}
		return this;
	}

	public final float length() {
		return MathUtils.sqrt(x * x + y * y);
	}

	public Vector2f length(float length) {
		return norZero().mulSelf(length);
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

	public Vector2f norLeft() {
		if (x != 0f || y != 0f) {
			return nor();
		} else {
			return AXIS_Y();
		}
	}

	public Vector2f norRight() {
		if (x != 0f || y != 0f) {
			return nor();
		} else {
			return AXIS_X();
		}
	}

	public Vector2f norZero() {
		if (x != 0f || y != 0f) {
			return nor();
		} else {
			return ZERO();
		}
	}

	public final Vector2f mul(float s) {
		return new Vector2f(this.x * s, this.y * s);
	}

	public final Vector2f mul(float sx, float sy) {
		return new Vector2f(this.x * sx, this.y * sy);
	}

	public final Vector2f mulSelf(float scale) {
		return mulSelf(scale, scale);
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

	public float mag(XY pos) {
		if (pos == null) {
			return 0f;
		}
		return MathUtils.sqrt(magSquared(pos));
	}

	public float magSquared(XY pos) {
		if (pos == null) {
			return 0f;
		}
		return (pos.getX() * pos.getX()) + (pos.getY() * pos.getY());
	}

	public float max() {
		return MathUtils.max(this.x, this.y);
	}

	public float min() {
		return MathUtils.min(this.x, this.y);
	}

	public Vector2f max(Vector2f v) {
		if (v == null) {
			return cpy();
		}
		return max(this, v);
	}

	public Vector2f min(Vector2f v) {
		if (v == null) {
			return cpy();
		}
		return min(this, v);
	}

	public Vector2f maxSelf(Vector2f v) {
		if (v == null) {
			return this;
		}
		return max(this, v, this);
	}

	public Vector2f minSelf(Vector2f v) {
		if (v == null) {
			return this;
		}
		return min(this, v, this);
	}

	public float maxX(XY v) {
		if (v == null) {
			return this.x;
		}
		return MathUtils.max(this.x, v.getX());
	}

	public float minX(XY v) {
		if (v == null) {
			return this.x;
		}
		return MathUtils.min(this.x, v.getX());
	}

	public float maxY(XY v) {
		if (v == null) {
			return this.y;
		}
		return MathUtils.max(this.y, v.getY());
	}

	public float minY(XY v) {
		if (v == null) {
			return this.y;
		}
		return MathUtils.min(this.y, v.getY());
	}

	public Vector2f setMin(Vector2f t) {
		if (t == null) {
			return this;
		}
		return setMin(t.x, t.y);
	}

	public Vector2f setMax(Vector2f t) {
		if (t == null) {
			return this;
		}
		return setMax(t.x, t.y);
	}

	public Vector2f setMin(float tx, float ty) {
		this.x = (tx < this.x) ? tx : this.x;
		this.y = (ty < this.y) ? ty : this.y;
		return this;
	}

	public Vector2f setMax(float tx, float ty) {
		this.x = (tx > this.x) ? tx : this.x;
		this.y = (ty > this.y) ? ty : this.y;
		return this;
	}

	public Vector2f sign() {
		return cpy().signSelf();
	}

	public Vector2f signSelf() {
		return set(MathUtils.sign(x), MathUtils.sign(y));
	}

	public Vector2f slerp(Vector2f v, float weight) {
		float startLengthSquared = lengthSquared();
		float endLengthSquared = v.lengthSquared();
		if (startLengthSquared == 0f || endLengthSquared == 0f) {
			return lerp(v, weight);
		}
		float startLength = MathUtils.sqrt(startLengthSquared);
		float resultLength = MathUtils.lerp(startLength, MathUtils.sqrt(endLengthSquared), weight);
		float angle = angleRad(v);
		return rotateDegrees((angle * weight) * (resultLength / startLength));
	}

	public Vector2f slerpSelf(Vector2f v, float weight) {
		return set(slerp(v, weight));
	}

	public Vector2f slide(Vector2f normal) {
		return sub(normal.mul(dot(normal)));
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

	public Vector2f div(float sx, float sy) {
		return new Vector2f(this.x / sx, this.y / sy);
	}

	public Vector2f divSelf(float scale) {
		return mulSelf(scale, scale);
	}

	public Vector2f divSelf(float sx, float sy) {
		this.x /= sx;
		this.y /= sy;
		return this;
	}

	public Vector2f divSelf(Affine2f mat) {
		float nx = this.x / mat.m00 + this.y / mat.m01 + mat.tx;
		float ny = this.x / mat.m10 + this.y / mat.m11 + mat.ty;
		this.x = nx;
		this.y = ny;
		return this;
	}

	public Vector2f divSelf(Matrix3 mat) {
		float nx = this.x / mat.val[0] + this.y / mat.val[3] + mat.val[6];
		float ny = this.x / mat.val[1] + this.y / mat.val[4] + mat.val[7];
		this.x = nx;
		this.y = ny;
		return this;
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

	public Vector2f floor() {
		return new Vector2f(MathUtils.floor(this.x), MathUtils.floor(this.y));
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

	public Vector2f clamp(Vector2f min, Vector2f max) {
		if (this.x < min.x) {
			this.x = min.x;
		}
		if (this.x > max.x) {
			this.x = max.x;
		}
		if (this.y < min.y) {
			this.y = min.y;
		}
		if (this.y > max.y) {
			this.y = max.y;
		}
		return this;
	}

	public float cross(float x, float y) {
		return (this.x * y) - (this.y * x);
	}

	public float cross(final Vector2f v) {
		return this.cross(v.x, v.y);
	}

	public float crs(Vector2f v) {
		return this.cross(v);
	}

	public float crs(float x, float y) {
		return this.cross(x, y);
	}

	public float component(Vector2f src, Vector2f direction) {
		float alpha = MathUtils.atan2(direction.y, direction.x);
		float theta = MathUtils.atan2(this.y, this.x);
		float mag = length();
		float a = mag * MathUtils.cos(theta - alpha);
		return a;
	}

	public Vector2f componentVector(Vector2f direction) {
		Vector2f src = direction.nor();
		return scale(component(src, direction));
	}

	public float getAngle() {
		return angleTo(this);
	}

	public boolean isArrayCheck(float px, float py) {
		return this.x >= 0 && this.y >= 0 && this.x < px && this.y < py;
	}

	public boolean isArrayCheck(XY arraySize) {
		if (arraySize == null) {
			return false;
		}
		return isArrayCheck(arraySize.getX(), arraySize.getY());
	}

	public float area() {
		return MathUtils.abs(this.x * this.y);
	}

	public float atan2() {
		return MathUtils.atan2(y, x);
	}

	public float angle() {
		return getAngle();
	}

	public float angleBetween(Vector2f v, boolean radians) {
		if (null == v) {
			return 0f;
		}
		final float magSqMult = this.lengthSquared() * v.lengthSquared();
		if (magSqMult == 0) {
			return 0f;
		}
		final float angle = MathUtils.atan2(this.crs(v), this.dot(v));
		return radians ? MathUtils.toRadians(angle) : MathUtils.toDegrees(angle);
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

	public Vector2f angle(float angle) {
		return fromAngle(angle).mulSelf(length());
	}

	public Vector2f angleDegrees(float degAngle) {
		return fromDegreesAngle(degAngle).mulSelf(length());
	}

	public Vector2f round() {
		return cpy().roundSelf();
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

	public Vector2f rotateDegrees(float angle) {
		return cpy().rotateDegrees(angle);
	}

	public Vector2f roundSelf() {
		return set(MathUtils.round(this.x), MathUtils.round(this.y));
	}

	public Vector2f rotateDegreesSelf(float angle) {
		float cos = MathUtils.cos(angle);
		float sin = MathUtils.sin(angle);
		float newX = this.x * cos - this.y * sin;
		float newY = this.x * sin + this.y * cos;
		this.x = newX;
		this.y = newY;
		return this;
	}

	public Vector2f rotateSelf(float cx, float cy, float angle) {

		float rad = MathUtils.toRadians(angle);
		float cos = MathUtils.cos(rad);
		float sin = MathUtils.sin(rad);

		float nx = cx + (this.x - cx) * cos - (this.y - cy) * sin;
		float ny = cy + (this.x - cx) * sin + (this.y - cy) * cos;

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

	public Vector2f rotateRadians(float radians) {
		if (x == 0f && y == 0f) {
			return cpy();
		} else {
			float angle = getAngle();
			float newAngle = angle + radians;
			return fromAngle(newAngle).mul(length());
		}
	}

	public Vector2f move_45D_up() {
		return move_45D_up(1f);
	}

	public Vector2f move_45D_up(float multiples) {
		return move_multiples(Field2D.UP, multiples);
	}

	public Vector2f move_45D_left() {
		return move_45D_left(1f);
	}

	public Vector2f move_45D_left(float multiples) {
		return move_multiples(Field2D.LEFT, multiples);
	}

	public Vector2f move_45D_right() {
		return move_45D_right(1f);
	}

	public Vector2f move_45D_right(float multiples) {
		return move_multiples(Field2D.RIGHT, multiples);
	}

	public Vector2f move_45D_down() {
		return move_45D_down(1f);
	}

	public Vector2f move_45D_down(float multiples) {
		return move_multiples(Field2D.DOWN, multiples);
	}

	public Vector2f move_up() {
		return move_up(1f);
	}

	public Vector2f move_up(float multiples) {
		return move_multiples(Field2D.TUP, multiples);
	}

	public Vector2f move_left() {
		return move_left(1f);
	}

	public Vector2f move_left(float multiples) {
		return move_multiples(Field2D.TLEFT, multiples);
	}

	public Vector2f move_right() {
		return move_right(1f);
	}

	public Vector2f move_right(float multiples) {
		return move_multiples(Field2D.TRIGHT, multiples);
	}

	public Vector2f move_down() {
		return move_down(1f);
	}

	public Vector2f move_down(float multiples) {
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

	public Vector3f getXYZ(Vector2f p2, Vector2f p3) {
		return getXYZ(this, p2, p3);
	}

	public Vector3f getXYZ(Vector2f p1, Vector2f p2, Vector2f p3) {
		Vector3f v3 = new Vector3f();
		float m1Up = (p2.y - p3.y) * p1.x - (p2.x - p3.x) * p1.y + p2.x * p3.y - p3.x * p2.y;
		float m1Down = (p2.x - p3.x) * (p1.x - p2.x) * (p1.x - p3.x);
		v3.x = -m1Up / m1Down;
		float m2Up = (p2.y - p3.y) * p1.x * p1.x + p2.x * p2.x * p3.y - p3.x * p3.x * p2.y
				- (p2.x * p2.x - p3.x * p3.x) * p1.y;
		float m2Down = (p2.x - p3.x) * (p1.x - p2.x) * (p1.x - p3.x);
		v3.y = m2Up / m2Down;
		float m3Up = (p2.x * p3.y) * p1.x * p1.x - (p2.x * p2.x * p3.y - p3.x * p3.x * p2.y) * p1.x
				+ (p2.x * p2.x * p3.x - p2.x * p3.x * p3.x) * p1.y;
		float m3Down = (p2.x - p3.x) * (p1.x - p2.x) * (p1.x - p3.x);
		v3.z = m3Up / m3Down;
		return v3;
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

	public Vector2f moveX(float x) {
		return move(x, this.y);
	}

	public Vector2f moveY(float y) {
		return move(this.x, y);
	}

	public Vector2f move(float dx, float dy) {
		this.x += dx;
		this.y += dy;
		return this;
	}

	public Vector2f move(XY pos) {
		if (pos == null) {
			return this;
		}
		return move(pos.getX(), pos.getY());
	}

	public Vector2f move_multiples(int direction, float multiples) {
		if (multiples <= 0) {
			multiples = 1f;
		}
		Vector2f v = Field2D.getDirection(direction);
		return move(v.x() * multiples, v.y() * multiples);
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

	public Vector2f getOrthogonal(boolean polarity) {
		return polarity ? new Vector2f(-y, x) : new Vector2f(y, -x);
	}

	public Vector2f getOrthonormal(boolean polarity, boolean allowZero) {
		float len = length();
		if (len == 0) {
			return polarity ? new Vector2f(0f, (!allowZero) ? 0 : 1) : new Vector2f(0f, -((!allowZero) ? 0 : 1));
		}
		return polarity ? new Vector2f(-y / len, x / len) : new Vector2f(y / len, -x / len);
	}

	public Vector2f setLocation(float x, float y) {
		return set(x, y);
	}

	@Override
	public void setX(float x) {
		this.x = x;
	}

	@Override
	public void setY(float y) {
		this.y = y;
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

	public Vector2f reset() {
		return set(0f, 0f);
	}

	public Vector2f reverse() {
		x = -x;
		y = -y;
		return this;
	}

	public Vector2f mul(Vector2f pos) {
		if (pos == null) {
			return this;
		}
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
		if (v == null) {
			return this;
		}
		this.x = v.getX();
		this.y = v.getY();
		return this;
	}

	public final Vector2f set(Vector2f v) {
		if (v == null) {
			return this;
		}
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

	public final Vector2f setEmpty() {
		return set(0f);
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
		return cpy().lerpSelf(target, alpha);
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
		int hashCode = 1;
		hashCode = prime * LSystem.unite(hashCode, x);
		hashCode = prime * LSystem.unite(hashCode, y);
		return hashCode;
	}

	public boolean equals(float x, float y) {
		if (NumberUtils.floatToIntBits(x) != NumberUtils.floatToIntBits(this.x)) {
			return false;
		}
		if (NumberUtils.floatToIntBits(y) != NumberUtils.floatToIntBits(this.y)) {
			return false;
		}
		return true;
	}

	public boolean equals(XY pos) {
		if (pos == null) {
			return false;
		}
		return equals(pos.getX(), pos.getY());
	}

	public boolean equals(Vector2f v) {
		if (v == null) {
			return false;
		}
		if (this == v) {
			return true;
		}
		return equals(v.getX(), v.getY());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof Vector2f) {
			return equals((Vector2f) obj);
		}
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

	public boolean greaterThan(Vector2f o) {
		return greaterThan(this, o);
	}

	public boolean lessThan(Vector2f o) {
		return lessThan(this, o);
	}

	public boolean greaterThanOrEqual(Vector2f o) {
		return greaterThanOrEqual(this, o);
	}

	public boolean lessThanOrEqual(Vector2f o) {
		return lessThanOrEqual(this, o);
	}

	public Vector2f unit() {
		final float len = this.length();
		return len == 0 ? new Vector2f(0) : this.scale(1f / len);
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

	public boolean isOutOfBounds(Vector2f max) {
		return isOutOfBounds(max, null);
	}

	public boolean isOutOfBounds(Vector2f max, Vector2f min) {
		Vector2f minCoords = (min == null ? new Vector2f(0f, 0f) : min);
		if (this.x >= max.x || y >= max.y) {
			return true;
		}
		if (x < minCoords.x || y < minCoords.y) {
			return true;
		}
		return false;
	}

	public boolean hasSameDirection(Vector2f vector) {
		return dot(vector) > 0;
	}

	public boolean hasOppositeDirection(Vector2f vector) {
		return dot(vector) < 0;
	}

	public boolean hasDifferentValues(Vector2f vector) {
		return this.x != vector.x || this.y != vector.y;
	}

	public static String pointToString(float x, float y) {
		return MathUtils.toString(x) + "," + MathUtils.toString(y);
	}

	public final Vector2f approach(Vector2f target, float alpha) {
		return cpy().approachSelf(target, alpha);
	}

	public final Vector2f approachSelf(Vector2f target, float alpha) {
		float dx = x - target.x, dy = y - target.y;
		float alpha2 = alpha * alpha;
		float len2 = len2(dx, dy);
		if (len2 > alpha2) {
			float scl = MathUtils.sqrt(alpha2 / len2);
			dx *= scl;
			dy *= scl;
			return subtractSelf(dx, dy);
		} else {
			return set(target);
		}
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
		return (this.y * zx) - (this.x * zy);
	}

	public float zcross(Vector2f v) {
		return this.zcross(v.x, v.y);
	}

	public float zcrs(float zx, float zy) {
		return this.zcross(zx, zy);
	}

	public float zcrs(Vector2f v) {
		return this.zcross(v);
	}

	public float dir() {
		return atan2();
	}

	public float dot(float x, float y) {
		return this.x * x + this.y * y;
	}

	public float dist(Vector2f v) {
		return distance(v);
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

	public float lenManhattan() {
		return MathUtils.abs(this.x) + MathUtils.abs(this.y);
	}

	public boolean inCircle(XYZ cir) {
		return CollisionHelper.checkPointvsCircle(this.x, this.y, cir);
	}

	public boolean inCircle(Circle c) {
		return CollisionHelper.checkPointvsCircle(this.x, this.y, c.getRealX(), c.getRealY(), c.getDiameter());
	}

	public boolean inCircle(float cx, float cy, float d) {
		return CollisionHelper.checkPointvsCircle(this.x, this.y, cx, cy, d);
	}

	public boolean inEllipse(float ex, float ey, float ew, float eh) {
		return CollisionHelper.checkPointvsEllipse(this.x, this.y, ex, ey, ew, eh);
	}

	public boolean inEllipse(Ellipse e) {
		if (e == null) {
			return false;
		}
		return CollisionHelper.checkPointvsEllipse(this.x, this.y, e.getRealX(), e.getRealY(), e.getDiameter1(),
				e.getDiameter2());
	}

	public boolean inEllipse(XYZW rect) {
		if (rect == null) {
			return false;
		}
		return CollisionHelper.checkPointvsEllipse(this.x, this.y, rect.getX(), rect.getY(), rect.getZ(), rect.getW());
	}

	public boolean inArc(float ax, float ay, float arcRadius, float arcHeading, float arcAngle) {
		return CollisionHelper.checkPointvsArc(this.x, this.y, ax, ay, arcRadius, arcHeading, arcAngle);
	}

	public boolean inRect(XYZW rect) {
		if (rect == null) {
			return false;
		}
		return CollisionHelper.checkPointvsAABB(this.x, this.y, rect);
	}

	public boolean inRect(RectBox rect) {
		if (rect == null) {
			return false;
		}
		return CollisionHelper.checkPointvsAABB(this.x, this.y, rect.getX(), rect.getY(), rect.getWidth(),
				rect.getHeight());
	}

	public boolean inRect(float rx, float ry, float rw, float rh) {
		return CollisionHelper.checkPointvsAABB(this.x, this.y, rx, ry, rw, rh);
	}

	public boolean inLine(XYZW line) {
		if (line == null) {
			return false;
		}
		return CollisionHelper.checkPointvsLine(this.x, this.y, line.getX(), line.getY(), line.getZ(), line.getW());
	}

	public boolean inLine(Line line) {
		if (line == null) {
			return false;
		}
		return CollisionHelper.checkPointvsLine(this.x, this.y, line.getX1(), line.getY1(), line.getX2(), line.getY2());
	}

	public boolean inLine(Line line, float size) {
		if (line == null) {
			return false;
		}
		return CollisionHelper.checkPointvsLine(this.x, this.y, line.getX1(), line.getY1(), line.getX2(), line.getY2(),
				size);
	}

	public boolean inLine(float x1, float y1, float x2, float y2, float size) {
		return CollisionHelper.checkPointvsLine(this.x, this.y, x1, y1, x2, y2, size);
	}

	public boolean inTriangle(Triangle2f t) {
		if (t == null) {
			return false;
		}
		return CollisionHelper.checkPointvsTriangle(this.x, this.y, t.getX1(), t.getY1(), t.getX2(), t.getY2(),
				t.getX3(), t.getY3());
	}

	public boolean inTriangle(float x1, float y1, float x2, float y2, float x3, float y3) {
		return CollisionHelper.checkPointvsTriangle(this.x, this.y, x1, y1, x2, y2, x3, y3);
	}

	public boolean inPolygon(Polygon poly) {
		if (poly == null) {
			return false;
		}
		return CollisionHelper.checkPointvsPolygon(this.x, this.y, poly.getVertices());
	}

	public <T extends XY> boolean inPolygon(TArray<T> poly) {
		if (poly == null) {
			return false;
		}
		return CollisionHelper.checkPointvsPolygon(this.x, this.y, poly);
	}

	public boolean collided(Shape shape) {
		if (shape instanceof Polygon) {
			return inPolygon((Polygon) shape);
		} else if (shape instanceof Line) {
			return inLine((Line) shape);
		} else if (shape instanceof RectBox) {
			return inRect((RectBox) shape);
		} else if (shape instanceof Triangle2f) {
			return inTriangle((Triangle2f) shape);
		} else if (shape instanceof Circle) {
			return inCircle((Circle) shape);
		} else if (shape instanceof Ellipse) {
			return inEllipse((Ellipse) shape);
		}
		return CollisionHelper.checkPointvsPolygon(this.x, this.y, shape.getPoints(), 1f);
	}

	public float[] toFloat() {
		return new float[] { x, y };
	}

	public int[] toInt() {
		return new int[] { x(), y() };
	}

	public Vector3f toVector3() {
		return new Vector3f(x, y, 0f);
	}

	public String toCSS() {
		return this.x + "px " + this.y + "px";
	}

	public ObservableXY<Vector2f> observable(TChange<Vector2f> v) {
		return ObservableXY.at(v, this, this);
	}

	@Override
	public final String toString() {
		return "(" + x + "," + y + ")";
	}

}
