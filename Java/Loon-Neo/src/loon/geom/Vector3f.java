/**
 * Copyright 2008 - 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a cpy of
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

import loon.utils.Array;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;

public class Vector3f implements Serializable, XYZ {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1114108169708351982L;

	public float x;

	public float y;

	public float z;

	private static final Array<Vector3f> _VEC3_CACHE = new Array<Vector3f>();

	public final static Vector3f TMP() {
		Vector3f temp = _VEC3_CACHE.pop();
		if (temp == null) {
			_VEC3_CACHE.add(temp = new Vector3f(0, 0, 0));
		}
		return temp;
	}

	public final static Vector3f ZERO() {
		return new Vector3f(0, 0, 0);
	}

	public final static Vector3f AXIS_X() {
		return new Vector3f(1, 0, 0);
	}

	public final static Vector3f AXIS_Y() {
		return new Vector3f(0, 1, 0);
	}

	public final static Vector3f AXIS_Z() {
		return new Vector3f(0, 0, 1);
	}

	private final static Matrix4 tmpMat = new Matrix4();

	private final static Vector3f tmpNormal1 = new Vector3f();

	private final static Vector3f tmpNormal2 = new Vector3f();

	public final static Vector3f at(float x, float y, float z) {
		return new Vector3f(x, y, z);
	}

	public Vector3f() {
		this(0, 0, 0);
	}

	public Vector3f(float x, float y, float z) {
		this.set(x, y, z);
	}

	public Vector3f(final Vector3f vector) {
		this.set(vector);
	}

	public Vector3f(final float[] values) {
		this.set(values[0], values[1], values[2]);
	}

	public Vector3f(final Vector2f vector, float z) {
		this.set(vector.x, vector.y, z);
	}

	public Vector3f(float v) {
		this(v, v, v);
	}

	public Vector3f(float x, Vector2f v) {
		this(x, v.getX(), v.getY());
	}

	public Vector3f set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public Vector3f set(final Vector3f vector) {
		return this.set(vector.x, vector.y, vector.z);
	}

	public Vector3f set(final float[] values) {
		return this.set(values[0], values[1], values[2]);
	}

	public Vector3f set(final Vector2f vector, float z) {
		return this.set(vector.x, vector.y, z);
	}

	public Vector3f cpy() {
		return new Vector3f(this);
	}

	public Vector3f addSelf(final Vector3f vector) {
		return this.addSelf(vector.x, vector.y, vector.z);
	}

	public Vector3f addSelf(float x, float y, float z) {
		return this.set(this.x + x, this.y + y, this.z + z);
	}

	public Vector3f addSelf(float values) {
		return this.set(this.x + values, this.y + values, this.z + values);
	}

	public Vector3f mulAddSelf(Vector3f vec, float scalar) {
		this.x += vec.x * scalar;
		this.y += vec.y * scalar;
		this.z += vec.z * scalar;
		return this;
	}

	public Vector3f mulAddSelf(Vector3f vec, Vector3f mulVec) {
		this.x += vec.x * mulVec.x;
		this.y += vec.y * mulVec.y;
		this.z += vec.z * mulVec.z;
		return this;
	}

	public static float len(final float x, final float y, final float z) {
		return MathUtils.sqrt(x * x + y * y + z * z);
	}

	public float len() {
		return MathUtils.sqrt(x * x + y * y + z * z);
	}

	public static float len2(final float x, final float y, final float z) {
		return x * x + y * y + z * z;
	}

	public float len2() {
		return x * x + y * y + z * z;
	}

	public boolean idt(final Vector3f vector) {
		return x == vector.x && y == vector.y && z == vector.z;
	}

	public static float dst(final float x1, final float y1, final float z1, final float x2, final float y2,
			final float z2) {
		final float a = x2 - x1;
		final float b = y2 - y1;
		final float c = z2 - z1;
		return MathUtils.sqrt(a * a + b * b + c * c);
	}

	public float dst(final Vector3f vector) {
		final float a = vector.x - x;
		final float b = vector.y - y;
		final float c = vector.z - z;
		return MathUtils.sqrt(a * a + b * b + c * c);
	}

	public float dst(float x, float y, float z) {
		final float a = x - this.x;
		final float b = y - this.y;
		final float c = z - this.z;
		return MathUtils.sqrt(a * a + b * b + c * c);
	}

	public static float dst2(final float x1, final float y1, final float z1, final float x2, final float y2,
			final float z2) {
		final float a = x2 - x1;
		final float b = y2 - y1;
		final float c = z2 - z1;
		return a * a + b * b + c * c;
	}

	public float dst2(Vector3f point) {
		final float a = point.x - x;
		final float b = point.y - y;
		final float c = point.z - z;
		return a * a + b * b + c * c;
	}

	public float dst2(float x, float y, float z) {
		final float a = x - this.x;
		final float b = y - this.y;
		final float c = z - this.z;
		return a * a + b * b + c * c;
	}

	public Vector3f norSelf() {
		final float len2 = this.len2();
		if (len2 == 0f || len2 == 1f) {
			return this;
		}
		return this.scaleSelf(1f / MathUtils.sqrt(len2));
	}

	public static float dot(float x1, float y1, float z1, float x2, float y2, float z2) {
		return x1 * x2 + y1 * y2 + z1 * z2;
	}

	public float dot(final Vector3f vector) {
		return x * vector.x + y * vector.y + z * vector.z;
	}

	public float dot(float x, float y, float z) {
		return this.x * x + this.y * y + this.z * z;
	}

	public final static float dot(Vector3f a, Vector3f b) {
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}

	public Vector3f crsSelf(final Vector3f vector) {
		return this.set(y * vector.z - z * vector.y, z * vector.x - x * vector.z, x * vector.y - y * vector.x);
	}

	public Vector3f crsSelf(float x, float y, float z) {
		return this.set(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
	}

	public Vector3f mul4x3(float[] matrix) {
		return set(x * matrix[0] + y * matrix[3] + z * matrix[6] + matrix[9],
				x * matrix[1] + y * matrix[4] + z * matrix[7] + matrix[10],
				x * matrix[2] + y * matrix[5] + z * matrix[8] + matrix[11]);
	}

	public Vector3f mulSelf(final Matrix4 matrix) {
		final float l_mat[] = matrix.val;
		return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M01] + z * l_mat[Matrix4.M02] + l_mat[Matrix4.M03],
				x * l_mat[Matrix4.M10] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M12] + l_mat[Matrix4.M13],
				x * l_mat[Matrix4.M20] + y * l_mat[Matrix4.M21] + z * l_mat[Matrix4.M22] + l_mat[Matrix4.M23]);
	}

	public Vector3f traMul(final Matrix4 matrix) {
		final float l_mat[] = matrix.val;
		return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M10] + z * l_mat[Matrix4.M20] + l_mat[Matrix4.M30],
				x * l_mat[Matrix4.M01] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M21] + l_mat[Matrix4.M31],
				x * l_mat[Matrix4.M02] + y * l_mat[Matrix4.M12] + z * l_mat[Matrix4.M22] + l_mat[Matrix4.M32]);
	}

	public Vector3f mulSelf(Matrix3 matrix) {
		final float l_mat[] = matrix.val;
		return set(x * l_mat[Matrix3.M00] + y * l_mat[Matrix3.M01] + z * l_mat[Matrix3.M02],
				x * l_mat[Matrix3.M10] + y * l_mat[Matrix3.M11] + z * l_mat[Matrix3.M12],
				x * l_mat[Matrix3.M20] + y * l_mat[Matrix3.M21] + z * l_mat[Matrix3.M22]);
	}

	public Vector3f traMul(Matrix3 matrix) {
		final float l_mat[] = matrix.val;
		return set(x * l_mat[Matrix3.M00] + y * l_mat[Matrix3.M10] + z * l_mat[Matrix3.M20],
				x * l_mat[Matrix3.M01] + y * l_mat[Matrix3.M11] + z * l_mat[Matrix3.M21],
				x * l_mat[Matrix3.M02] + y * l_mat[Matrix3.M12] + z * l_mat[Matrix3.M22]);
	}

	public Vector3f mulSelf(final Quaternion quat) {
		return quat.transformSelf(this);
	}

	public Vector3f prjSelf(final Matrix4 matrix) {
		final float l_mat[] = matrix.val;
		final float l_w = 1f
				/ (x * l_mat[Matrix4.M30] + y * l_mat[Matrix4.M31] + z * l_mat[Matrix4.M32] + l_mat[Matrix4.M33]);
		return this.set(
				(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M01] + z * l_mat[Matrix4.M02] + l_mat[Matrix4.M03]) * l_w,
				(x * l_mat[Matrix4.M10] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M12] + l_mat[Matrix4.M13]) * l_w,
				(x * l_mat[Matrix4.M20] + y * l_mat[Matrix4.M21] + z * l_mat[Matrix4.M22] + l_mat[Matrix4.M23]) * l_w);
	}

	public Vector3f rotSelf(final Matrix4 matrix) {
		final float l_mat[] = matrix.val;
		return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M01] + z * l_mat[Matrix4.M02],
				x * l_mat[Matrix4.M10] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M12],
				x * l_mat[Matrix4.M20] + y * l_mat[Matrix4.M21] + z * l_mat[Matrix4.M22]);
	}

	public Vector3f unrotateSelf(final Matrix4 matrix) {
		final float l_mat[] = matrix.val;
		return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M10] + z * l_mat[Matrix4.M20],
				x * l_mat[Matrix4.M01] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M21],
				x * l_mat[Matrix4.M02] + y * l_mat[Matrix4.M12] + z * l_mat[Matrix4.M22]);
	}

	public Vector3f untransformSelf(final Matrix4 matrix) {
		final float l_mat[] = matrix.val;
		x -= l_mat[Matrix4.M03];
		y -= l_mat[Matrix4.M03];
		z -= l_mat[Matrix4.M03];
		return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M10] + z * l_mat[Matrix4.M20],
				x * l_mat[Matrix4.M01] + y * l_mat[Matrix4.M11] + z * l_mat[Matrix4.M21],
				x * l_mat[Matrix4.M02] + y * l_mat[Matrix4.M12] + z * l_mat[Matrix4.M22]);
	}

	public Vector3f rotateSelf(float degrees, float axisX, float axisY, float axisZ) {
		return this.mulSelf(tmpMat.setToRotation(axisX, axisY, axisZ, degrees));
	}

	public Vector3f rotateRadSelf(float radians, float axisX, float axisY, float axisZ) {
		return this.mulSelf(tmpMat.setToRotationRad(axisX, axisY, axisZ, radians));
	}

	public Vector3f rotateSelf(final Vector3f axis, float degrees) {
		tmpMat.setToRotation(axis, degrees);
		return this.mulSelf(tmpMat);
	}

	public Vector3f rotateRadSelf(final Vector3f axis, float radians) {
		tmpMat.setToRotationRad(axis, radians);
		return this.mulSelf(tmpMat);
	}

	public boolean isUnit() {
		return isUnit(0.000000001f);
	}

	public boolean isUnit(final float margin) {
		return MathUtils.abs(len2() - 1f) < margin;
	}

	public boolean isZero() {
		return x == 0 && y == 0 && z == 0;
	}

	public boolean isZero(final float margin) {
		return len2() < margin;
	}

	public boolean isOnLine(Vector3f other, float epsilon) {
		return len2(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x) <= epsilon;
	}

	public boolean isOnLine(Vector3f other) {
		return len2(y * other.z - z * other.y, z * other.x - x * other.z,
				x * other.y - y * other.x) <= MathUtils.FLOAT_ROUNDING_ERROR;
	}

	public boolean isCollinear(Vector3f other, float epsilon) {
		return isOnLine(other, epsilon) && hasSameDirection(other);
	}

	public boolean isCollinear(Vector3f other) {
		return isOnLine(other) && hasSameDirection(other);
	}

	public boolean isCollinearOpposite(Vector3f other, float epsilon) {
		return isOnLine(other, epsilon) && hasOppositeDirection(other);
	}

	public boolean isCollinearOpposite(Vector3f other) {
		return isOnLine(other) && hasOppositeDirection(other);
	}

	public boolean isPerpendicular(Vector3f vector) {
		return MathUtils.isZero(dot(vector));
	}

	public boolean isPerpendicular(Vector3f vector, float epsilon) {
		return MathUtils.isZero(dot(vector), epsilon);
	}

	public boolean hasSameDirection(Vector3f vector) {
		return dot(vector) > 0;
	}

	public boolean hasOppositeDirection(Vector3f vector) {
		return dot(vector) < 0;
	}

	public Vector3f lerp(final Vector3f target, float alpha) {
		return cpy().lerpSelf(target, alpha);
	}

	public Vector3f slerp(final Vector3f target, float alpha) {
		return cpy().slerpSelf(target, alpha);
	}

	public Vector3f lerpSelf(final Vector3f target, float alpha) {
		scaleSelf(1.0f - alpha);
		addSelf(target.x * alpha, target.y * alpha, target.z * alpha);
		return this;
	}

	public Vector3f slerpSelf(final Vector3f target, float alpha) {
		final float dot = dot(target);
		if (dot > 0.9995 || dot < -0.9995) {
			return lerpSelf(target, alpha);
		}

		final float theta0 = MathUtils.acos(dot);
		final float theta = theta0 * alpha;

		final float st = MathUtils.sin(theta);
		final float tx = target.x - x * dot;
		final float ty = target.y - y * dot;
		final float tz = target.z - z * dot;
		final float l2 = tx * tx + ty * ty + tz * tz;
		final float dl = st * ((l2 < 0.0001f) ? 1f : 1f / MathUtils.sqrt(l2));

		return scaleSelf(MathUtils.cos(theta)).addSelf(tx * dl, ty * dl, tz * dl).norSelf();
	}

	public Vector3f limitSelf(float limit) {
		return limit2Self(limit * limit);
	}

	public Vector3f limit2Self(float limit2) {
		float len2 = len2();
		if (len2 > limit2) {
			scaleSelf(limit2 / len2);
		}
		return this;
	}

	public Vector3f setLengthSelf(float len) {
		return setLength2Self(len * len);
	}

	public Vector3f setLength2Self(float len2) {
		float oldLen2 = len2();
		return (oldLen2 == 0 || oldLen2 == len2) ? this : scaleSelf(MathUtils.sqrt(len2 / oldLen2));
	}

	public Vector3f clampSelf(float min, float max) {
		final float len2 = len2();
		if (len2 == 0f)
			return this;
		float max2 = max * max;
		if (len2 > max2)
			return scaleSelf(MathUtils.sqrt(max2 / len2));
		float min2 = min * min;
		if (len2 < min2)
			return scaleSelf(MathUtils.sqrt(min2 / len2));
		return this;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + NumberUtils.floatToIntBits(x);
		result = prime * result + NumberUtils.floatToIntBits(y);
		result = prime * result + NumberUtils.floatToIntBits(z);
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector3f other = (Vector3f) obj;
		if (NumberUtils.floatToIntBits(x) != NumberUtils.floatToIntBits(other.x))
			return false;
		if (NumberUtils.floatToIntBits(y) != NumberUtils.floatToIntBits(other.y))
			return false;
		if (NumberUtils.floatToIntBits(z) != NumberUtils.floatToIntBits(other.z))
			return false;
		return true;
	}

	public boolean epsilonEquals(final Vector3f other, float epsilon) {
		if (other == null)
			return false;
		if (MathUtils.abs(other.x - x) > epsilon)
			return false;
		if (MathUtils.abs(other.y - y) > epsilon)
			return false;
		if (MathUtils.abs(other.z - z) > epsilon)
			return false;
		return true;
	}

	public boolean epsilonEquals(float x, float y, float z, float epsilon) {
		if (MathUtils.abs(x - this.x) > epsilon)
			return false;
		if (MathUtils.abs(y - this.y) > epsilon)
			return false;
		if (MathUtils.abs(z - this.z) > epsilon)
			return false;
		return true;
	}

	public Vector3f zeroSelf() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		return this;
	}

	public Vector3f add(Vector3f v) {
		return add(v.x, v.y, v.z);
	}

	public Vector3f add(float x, float y, float z) {
		return cpy().addSelf(x, y, z);
	}

	public Vector3f add(Vector2f v, float z) {
		return add(v.x, v.y, z);
	}

	public Vector3f addSelf(Vector2f v, float z) {
		return addSelf(v.x, v.y, z);
	}

	public Vector3f add(float x, Vector2f v) {
		return add(x, v.x, v.y);
	}

	public Vector3f addSelf(float x, Vector2f v) {
		return addSelf(x, v.x, v.y);
	}

	public Vector3f subtractSelf(float x, float y, float z) {
		return addSelf(-x, -y, -z);
	}

	public Vector3f subtract(Vector3f v) {
		return add(-v.x, -v.y, -v.z);
	}

	public Vector3f subtractSelf(Vector3f v) {
		return addSelf(-v.x, -v.y, -v.z);
	}

	public Vector3f subtract(Vector2f v, float z) {
		return subtract(v.x, v.y, z);
	}

	public Vector3f subtract(float x, float y, float z) {
		return add(-x, -y, -z);
	}

	public Vector3f subtractSelf(Vector2f v, float z) {
		return addSelf(-v.x, -v.y, z);
	}

	public Vector3f subtract(float x, Vector2f v) {
		return subtract(x, v.x, v.y);
	}

	public Vector3f subtractSelf(float x, Vector2f v) {
		return addSelf(-x, -v.x, -v.y);
	}

	public Vector3f scale(float s) {
		return scale(s, s, s);
	}

	public Vector3f scale(float sx, float sy, float sz) {
		return cpy().scaleSelf(sx, sy, sz);
	}

	public Vector3f cross(Vector3f v) {
		return cross(v.x, v.y, v.z);
	}

	public Vector3f cross(float vx, float vy, float vz) {
		return cpy().crossSelf(vx, vy, vz);
	}

	public Vector3f crossSelf(float vx, float vy, float vz) {
		float x = this.x * vz - this.z * vy;
		float y = this.z * vx - this.x * vz;
		float z = this.x * vy - this.y * vx;

		return set(x, y, z);
	}

	public Vector3f crossSelf(Vector3f v) {
		return crossSelf(v.x, v.y, v.z);
	}

	public Vector3f normalize() {
		return cpy().normalizeSelf();
	}

	public Vector3f normalizeSelf() {
		float l = length();

		if (l == 0 || l == 1)
			return this;

		return set(x / l, y / l, z / l);
	}

	public float length() {
		return MathUtils.sqrt(lengthSquared());
	}

	public float lengthSquared() {
		return x * x + y * y + z * z;
	}

	public Vector3f negate() {
		return new Vector3f(-x, -y, -z);
	}

	public Vector3f negateSelf() {
		return set(-x, -y, -z);
	}

	public float distance(float x, float y, float z) {
		return MathUtils.sqrt(distanceSquared(x, y, z));
	}

	public float distanceSquared(float x, float y, float z) {
		final float x2 = (x - this.x) * (x - this.x);
		final float y2 = (y - this.y) * (y - this.y);
		final float z2 = (z - this.z) * (z - this.z);

		return x2 + y2 + z2;
	}

	public float distance(Vector3f v) {
		return MathUtils.sqrt(distanceSquared(v));
	}

	public float distanceSquared(Vector3f v) {
		return distanceSquared(v.x, v.y, v.z);
	}

	public float distance(Vector2f v) {
		return MathUtils.sqrt(distanceSquared(v));
	}

	public float distanceSquared(Vector2f v) {
		return distanceSquared(v.x, v.y, 0);
	}

	public Vector3f rotate(Vector3f axis, float angle) {
		return cpy().rotateSelf(axis, angle);
	}

	public Vector3f scaleSelf(float s) {
		return scaleSelf(s, s, s);
	}

	public Vector3f scaleSelf(float sx, float sy, float sz) {
		return set(x * sx, y * sy, z * sz);
	}

	public Vector3f multiply(Matrix3 m) {
		return cpy().multiplySelf(m);
	}

	public Vector3f multiplySelf(Matrix3 m) {
		float rx = x * m.get(0, 0) + y * m.get(0, 1) + z * m.get(0, 2);
		float ry = x * m.get(1, 0) + y * m.get(1, 1) + z * m.get(1, 2);
		float rz = x * m.get(2, 0) + y * m.get(2, 1) + z * m.get(2, 2);

		return set(rx, ry, rz);
	}

	public Vector3f multiply(Matrix4 m) {
		return cpy().multiplySelf(m);
	}

	public Vector3f multiplySelf(Matrix4 m) {
		float rx = x * m.get(0, 0) + y * m.get(1, 0) + z * m.get(2, 0) + 1 * m.get(3, 0);
		float ry = x * m.get(0, 1) + y * m.get(1, 1) + z * m.get(2, 1) + 1 * m.get(3, 1);
		float rz = x * m.get(0, 2) + y * m.get(1, 2) + z * m.get(2, 2) + 1 * m.get(3, 2);

		return set(rx, ry, rz);
	}

	public Vector3f set(float v) {
		return set(v, v, v);
	}

	public float getR() {
		return x;
	}

	public Vector3f setR(float r) {
		x = r;
		return this;
	}

	public float getG() {
		return y;
	}

	public Vector3f setG(float g) {
		y = g;
		return this;
	}

	public float getB() {
		return z;
	}

	public Vector3f setB(float b) {
		z = b;
		return this;
	}

	public Vector2f getXX() {
		return new Vector2f(x, x);
	}

	public Vector2f getXY() {
		return new Vector2f(x, y);
	}

	public Vector2f getXZ() {
		return new Vector2f(x, z);
	}

	public Vector2f getYX() {
		return new Vector2f(y, x);
	}

	public Vector2f getYY() {
		return new Vector2f(y, y);
	}

	public Vector2f getYZ() {
		return new Vector2f(y, z);
	}

	public Vector2f getZX() {
		return new Vector2f(z, x);
	}

	public Vector2f getZY() {
		return new Vector2f(z, y);
	}

	public Vector2f getZZ() {
		return new Vector2f(z, z);
	}

	@Override
	public float getX() {
		return this.x;
	}

	@Override
	public float getY() {
		return this.y;
	}

	@Override
	public float getZ() {
		return this.z;
	}

	public Vector3f nor() {
		float len = this.len();
		if (len == 0) {
			return this;
		} else {
			return this.div(len);
		}
	}

	public Vector3f div(float value) {
		float d = 1 / value;
		return this.set(this.x * d, this.y * d, this.z * d);
	}

	public Vector3f crs(Vector3f vector) {
		return this.set(y * vector.z - z * vector.y, z * vector.x - x * vector.z, x * vector.y - y * vector.x);
	}

	public Vector3f crs(float x, float y, float z) {
		return this.set(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
	}

	public Vector3f sub(float x, float y, float z) {
		return this.set(this.x - x, this.y - y, this.z - z);
	}

	public Vector3f sub(float value) {
		return this.set(this.x - value, this.y - value, this.z - value);
	}

	public Vector3f sub(Vector3f argVec) {
		return new Vector3f(x - argVec.x, y - argVec.y, z - argVec.z);
	}

	public final static Vector3f vectorSquareEquation(Vector3f a, Vector3f b, Vector3f c) {
		float baz = -1 * a.z / b.z;
		b.scaleSelf(baz).addSelf(a);
		float caz = -1 * a.z / c.z;
		c.scaleSelf(caz).addSelf(a);
		float cby = -1 * b.y / c.y;
		c.scaleSelf(cby).addSelf(b);
		float X = c.x;
		float Y = -1 * X * b.x / b.y;
		float Z = -1 * (X * a.x + Y * a.y) / a.z;
		return new Vector3f(X, Y, Z);
	}

	public final static Vector3f calcNormal(Vector3f zero, Vector3f one, Vector3f two) {
		tmpNormal1.set(one.x - zero.x, one.y - zero.y, one.z - zero.z);
		tmpNormal2.set(two.x - zero.x, two.y - zero.y, two.z - zero.z);
		Vector3f res = new Vector3f();
		return calcVectorNormal(tmpNormal1, tmpNormal2, res);
	}

	public final static Vector3f calcVectorNormal(Vector3f one, Vector3f two, Vector3f result) {
		result.x = two.y * one.z - two.z * one.y;
		result.y = two.z * one.x - two.x * one.z;
		result.z = two.x * one.y - two.y * one.x;
		return result.norSelf();
	}

	public Vector3f mul(float argScalar) {
		return new Vector3f(x * argScalar, y * argScalar, z * argScalar);
	}

	public Vector3f mul(Vector3f v) {
		return new Vector3f(x * v.x, y * v.y, z * v.z);
	}

	public static Vector3f set(Vector3f vectorA, Vector3f vectorB) {
		return set(vectorA, vectorB.x, vectorB.y, vectorB.z);
	}

	public static Vector3f set(Vector3f vectorA, float[] values) {
		return set(vectorA, values[0], values[1], values[2]);
	}

	public static Vector3f set(Vector3f vectorA, float x, float y, float z) {
		vectorA.x = x;
		vectorA.y = y;
		vectorA.z = z;
		return vectorA;
	}

	public static Vector3f cpy(Vector3f vectorA) {
		Vector3f newSVector = new Vector3f();

		newSVector.x = vectorA.x;
		newSVector.y = vectorA.y;
		newSVector.z = vectorA.z;

		return newSVector;
	}

	public static Vector3f add(Vector3f vectorA, Vector3f vectorB) {
		vectorA.x += vectorB.x;
		vectorA.y += vectorB.y;
		vectorA.z += vectorB.z;

		return vectorA;
	}

	public static Vector3f add(Vector3f vectorA, float x, float y, float z) {
		vectorA.x += x;
		vectorA.y += y;
		vectorA.z += z;

		return vectorA;
	}

	public static Vector3f add(Vector3f vectorA, float value) {
		vectorA.x += value;
		vectorA.y += value;
		vectorA.z += value;

		return vectorA;
	}

	public static Vector3f sub(Vector3f vectorA, Vector3f vectorB) {
		vectorA.x -= vectorB.x;
		vectorA.y -= vectorB.y;
		vectorA.z -= vectorB.z;

		return vectorA;
	}

	public static Vector3f sub(Vector3f vectorA, float x, float y, float z) {
		vectorA.x -= x;
		vectorA.y -= y;
		vectorA.z -= z;

		return vectorA;
	}

	public static Vector3f sub(Vector3f vectorA, float value) {
		vectorA.x -= value;
		vectorA.y -= value;
		vectorA.z -= value;

		return vectorA;
	}

	public static Vector3f mul(Vector3f vectorA, Vector3f vectorB) {
		vectorA.x = vectorB.x * vectorA.x;
		vectorA.y = vectorB.y * vectorA.y;
		vectorA.z = vectorB.z * vectorA.z;

		return vectorA;
	}

	public static Vector3f mul(Vector3f vectorA, float value) {
		vectorA.x = value * vectorA.x;
		vectorA.y = value * vectorA.y;
		vectorA.z = value * vectorA.z;

		return vectorA;
	}

	public static Vector3f div(Vector3f vectorA, float value) {
		float d = 1 / value;
		vectorA.x = d * vectorA.x;
		vectorA.y = d * vectorA.y;
		vectorA.z = d * vectorA.z;

		return vectorA;
	}

	public static float len(Vector3f vectorA) {
		return MathUtils.sqrt(vectorA.x * vectorA.x + vectorA.y * vectorA.y + vectorA.z * vectorA.z);
	}

	public static float len2(Vector3f vectorA) {
		return vectorA.x * vectorA.x + vectorA.y * vectorA.y + vectorA.z * vectorA.z;
	}

	public static boolean idt(Vector3f vectorA, Vector3f vectorB) {
		return vectorA.x == vectorB.x && vectorA.y == vectorB.y && vectorA.z == vectorB.z;
	}

	public static float dst(Vector3f vectorA, Vector3f vectorB) {
		float a = vectorB.x - vectorA.x;
		float b = vectorB.y - vectorA.y;
		float c = vectorB.z - vectorA.z;

		a *= a;
		b *= b;
		c *= c;

		return MathUtils.sqrt(a + b + c);
	}

	public static float dst(Vector3f vectorA, float x, float y, float z) {
		float a = x - vectorA.x;
		float b = y - vectorA.y;
		float c = z - vectorA.z;

		a *= a;
		b *= b;
		c *= c;

		return MathUtils.sqrt(a + b + c);
	}

	public static Vector3f crs(Vector3f vectorA, Vector3f vectorB) {
		vectorA.x = vectorA.y * vectorB.z - vectorA.z * vectorB.y;
		vectorA.y = vectorA.z * vectorB.x - vectorA.x * vectorB.z;
		vectorA.z = vectorA.x * vectorB.y - vectorA.y * vectorB.x;

		return vectorA;
	}

	public static Vector3f crs(Vector3f vectorA, float x, float y, float z) {
		vectorA.x = vectorA.y * z - vectorA.z * y;
		vectorA.y = vectorA.z * x - vectorA.x * z;
		vectorA.z = vectorA.x * y - vectorA.y * x;

		return vectorA;
	}

	public static boolean isZero(Vector3f vectorA) {
		return vectorA.x == 0 && vectorA.y == 0 && vectorA.z == 0;
	}

	public static Vector3f lerp(Vector3f vectorA, Vector3f target, float alpha) {
		Vector3f r = mul(vectorA, 1.0f - alpha);
		add(r, mul(cpy(vectorA), alpha));

		return r;
	}

	public static float dot(Vector3f vectorA, float x, float y, float z) {
		return vectorA.x * x + vectorA.y * y + vectorA.z * z;
	}

	public static float dst2(Vector3f vectorA, Vector3f vectorB) {
		float a = vectorB.x - vectorA.x;
		float b = vectorB.y - vectorA.y;
		float c = vectorB.z - vectorA.z;

		a *= a;
		b *= b;
		c *= c;

		return a + b + c;
	}

	public static float dst2(Vector3f vectorA, float x, float y, float z) {
		float a = x - vectorA.x;
		float b = y - vectorA.y;
		float c = z - vectorA.z;

		a *= a;
		b *= b;
		c *= c;

		return a + b + c;
	}

	public static Vector3f scale(Vector3f vectorA, float scalarX, float scalarY, float scalarZ) {
		vectorA.x *= scalarX;
		vectorA.y *= scalarY;
		vectorA.z *= scalarZ;
		return vectorA;
	}

	public static float angleBetween(Vector3f vectorA, Vector3f other) {
		float angle;

		float dot = dot(vectorA, other);

		float len1 = len(vectorA);
		float len2 = len(other);

		if (len1 == 0 && len2 == 0) {
			return 0;
		}

		angle = MathUtils.acos(dot / (len1 * len2));

		return angle;
	}

	public static float angleBetween(Vector3f vectorA, float x, float y, float z) {
		float angle;

		float dot = dot(vectorA, x, y, z);

		float len1 = len(vectorA);
		float len2 = MathUtils.sqrt(x * x + y * y + z * z);

		if (len1 == 0 || len2 == 0) {
			return 0;
		}

		angle = MathUtils.acos(dot / (len1 * len2));

		return angle;
	}

	public static float angleBetweenXY(Vector3f vectorA, float x, float y) {
		float angle;

		float dot = vectorA.x * x + vectorA.y * y;

		float len1 = MathUtils.sqrt(vectorA.x * vectorA.x + vectorA.y * vectorA.y);
		float len2 = MathUtils.sqrt(x * x + y * y);

		if (len1 == 0 || len2 == 0) {
			return 0;
		}

		angle = MathUtils.acos(dot / (len1 * len2));

		return angle;
	}

	public static float angleBetweenXZ(Vector3f vectorA, float x, float z) {
		float angle;

		float dot = vectorA.x * x + vectorA.z * z;

		float len1 = MathUtils.sqrt(vectorA.x * vectorA.x + vectorA.z * vectorA.z);
		float len2 = MathUtils.sqrt(x * x + z * z);

		if (len1 == 0 || len2 == 0) {
			return 0;
		}

		angle = MathUtils.acos(dot / (len1 * len2));

		return angle;
	}

	public static float angleBetweenYZ(Vector3f vectorA, float y, float z) {
		float angle;

		float dot = vectorA.y * y + vectorA.z * z;

		float len1 = MathUtils.sqrt(vectorA.y * vectorA.y + vectorA.z * vectorA.z);
		float len2 = MathUtils.sqrt(y * y + z * z);

		if (len1 == 0 || len2 == 0) {
			return 0;
		}

		angle = MathUtils.acos(dot / (len1 * len2));

		return angle;
	}

	public static float[] toArray(Vector3f vectorA) {
		return new float[] { vectorA.x, vectorA.y, vectorA.z };
	}

	public final static Vector3f cross(Vector3f a, Vector3f b) {
		return new Vector3f(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

}
