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

import loon.utils.MathUtils;
import loon.utils.NumberUtils;

public class Vector3f implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1114108169708351982L;
	public float x;

	public float y;

	public float z;

	public final static Vector3f X = new Vector3f(1, 0, 0);
	public final static Vector3f Y = new Vector3f(0, 1, 0);
	public final static Vector3f Z = new Vector3f(0, 0, 1);
	public final static Vector3f Zero = new Vector3f(0, 0, 0);

	private final static Matrix4 tmpMat = new Matrix4();

	public Vector3f() {
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

	public Vector3f add(final Vector3f vector) {
		return this.add(vector.x, vector.y, vector.z);
	}

	public Vector3f add(float x, float y, float z) {
		return this.set(this.x + x, this.y + y, this.z + z);
	}

	public Vector3f add(float values) {
		return this.set(this.x + values, this.y + values, this.z + values);
	}

	public Vector3f sub(final Vector3f a_vec) {
		return this.sub(a_vec.x, a_vec.y, a_vec.z);
	}

	public Vector3f sub(float x, float y, float z) {
		return this.set(this.x - x, this.y - y, this.z - z);
	}

	public Vector3f sub(float value) {
		return this.set(this.x - value, this.y - value, this.z - value);
	}

	public Vector3f scl(float scalar) {
		return this.set(this.x * scalar, this.y * scalar, this.z * scalar);
	}

	public Vector3f scl(final Vector3f other) {
		return this.set(x * other.x, y * other.y, z * other.z);
	}

	public Vector3f scl(float vx, float vy, float vz) {
		return this.set(this.x * vx, this.y * vy, this.z * vz);
	}

	public Vector3f mulAdd(Vector3f vec, float scalar) {
		this.x += vec.x * scalar;
		this.y += vec.y * scalar;
		this.z += vec.z * scalar;
		return this;
	}

	public Vector3f mulAdd(Vector3f vec, Vector3f mulVec) {
		this.x += vec.x * mulVec.x;
		this.y += vec.y * mulVec.y;
		this.z += vec.z * mulVec.z;
		return this;
	}

	public static float len(final float x, final float y, final float z) {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public float len() {
		return (float) Math.sqrt(x * x + y * y + z * z);
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

	public static float dst(final float x1, final float y1, final float z1,
			final float x2, final float y2, final float z2) {
		final float a = x2 - x1;
		final float b = y2 - y1;
		final float c = z2 - z1;
		return (float) Math.sqrt(a * a + b * b + c * c);
	}

	public float dst(final Vector3f vector) {
		final float a = vector.x - x;
		final float b = vector.y - y;
		final float c = vector.z - z;
		return (float) Math.sqrt(a * a + b * b + c * c);
	}

	public float dst(float x, float y, float z) {
		final float a = x - this.x;
		final float b = y - this.y;
		final float c = z - this.z;
		return (float) Math.sqrt(a * a + b * b + c * c);
	}

	public static float dst2(final float x1, final float y1, final float z1,
			final float x2, final float y2, final float z2) {
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

	public Vector3f nor() {
		final float len2 = this.len2();
		if (len2 == 0f || len2 == 1f) {
			return this;
		}
		return this.scl(1f / (float) Math.sqrt(len2));
	}

	public static float dot(float x1, float y1, float z1, float x2, float y2,
			float z2) {
		return x1 * x2 + y1 * y2 + z1 * z2;
	}

	public float dot(final Vector3f vector) {
		return x * vector.x + y * vector.y + z * vector.z;
	}

	public float dot(float x, float y, float z) {
		return this.x * x + this.y * y + this.z * z;
	}

	public Vector3f crs(final Vector3f vector) {
		return this.set(y * vector.z - z * vector.y, z * vector.x - x
				* vector.z, x * vector.y - y * vector.x);
	}

	public Vector3f crs(float x, float y, float z) {
		return this.set(this.y * z - this.z * y, this.z * x - this.x * z,
				this.x * y - this.y * x);
	}

	public Vector3f mul4x3(float[] matrix) {
		return set(x * matrix[0] + y * matrix[3] + z * matrix[6] + matrix[9], x
				* matrix[1] + y * matrix[4] + z * matrix[7] + matrix[10], x
				* matrix[2] + y * matrix[5] + z * matrix[8] + matrix[11]);
	}

	public Vector3f mul(final Matrix4 matrix) {
		final float l_mat[] = matrix.val;
		return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M01] + z
				* l_mat[Matrix4.M02] + l_mat[Matrix4.M03], x
				* l_mat[Matrix4.M10] + y * l_mat[Matrix4.M11] + z
				* l_mat[Matrix4.M12] + l_mat[Matrix4.M13], x
				* l_mat[Matrix4.M20] + y * l_mat[Matrix4.M21] + z
				* l_mat[Matrix4.M22] + l_mat[Matrix4.M23]);
	}

	public Vector3f traMul(final Matrix4 matrix) {
		final float l_mat[] = matrix.val;
		return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M10] + z
				* l_mat[Matrix4.M20] + l_mat[Matrix4.M30], x
				* l_mat[Matrix4.M01] + y * l_mat[Matrix4.M11] + z
				* l_mat[Matrix4.M21] + l_mat[Matrix4.M31], x
				* l_mat[Matrix4.M02] + y * l_mat[Matrix4.M12] + z
				* l_mat[Matrix4.M22] + l_mat[Matrix4.M32]);
	}

	public Vector3f mul(Matrix3 matrix) {
		final float l_mat[] = matrix.val;
		return set(x * l_mat[Matrix3.M00] + y * l_mat[Matrix3.M01] + z
				* l_mat[Matrix3.M02], x * l_mat[Matrix3.M10] + y
				* l_mat[Matrix3.M11] + z * l_mat[Matrix3.M12], x
				* l_mat[Matrix3.M20] + y * l_mat[Matrix3.M21] + z
				* l_mat[Matrix3.M22]);
	}

	public Vector3f traMul(Matrix3 matrix) {
		final float l_mat[] = matrix.val;
		return set(x * l_mat[Matrix3.M00] + y * l_mat[Matrix3.M10] + z
				* l_mat[Matrix3.M20], x * l_mat[Matrix3.M01] + y
				* l_mat[Matrix3.M11] + z * l_mat[Matrix3.M21], x
				* l_mat[Matrix3.M02] + y * l_mat[Matrix3.M12] + z
				* l_mat[Matrix3.M22]);
	}

	public Vector3f mul(final Quaternion quat) {
		return quat.transform(this);
	}

	public Vector3f prj(final Matrix4 matrix) {
		final float l_mat[] = matrix.val;
		final float l_w = 1f / (x * l_mat[Matrix4.M30] + y * l_mat[Matrix4.M31]
				+ z * l_mat[Matrix4.M32] + l_mat[Matrix4.M33]);
		return this.set((x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M01] + z
				* l_mat[Matrix4.M02] + l_mat[Matrix4.M03])
				* l_w, (x * l_mat[Matrix4.M10] + y * l_mat[Matrix4.M11] + z
				* l_mat[Matrix4.M12] + l_mat[Matrix4.M13])
				* l_w, (x * l_mat[Matrix4.M20] + y * l_mat[Matrix4.M21] + z
				* l_mat[Matrix4.M22] + l_mat[Matrix4.M23])
				* l_w);
	}

	public Vector3f rot(final Matrix4 matrix) {
		final float l_mat[] = matrix.val;
		return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M01] + z
				* l_mat[Matrix4.M02], x * l_mat[Matrix4.M10] + y
				* l_mat[Matrix4.M11] + z * l_mat[Matrix4.M12], x
				* l_mat[Matrix4.M20] + y * l_mat[Matrix4.M21] + z
				* l_mat[Matrix4.M22]);
	}

	public Vector3f unrotate(final Matrix4 matrix) {
		final float l_mat[] = matrix.val;
		return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M10] + z
				* l_mat[Matrix4.M20], x * l_mat[Matrix4.M01] + y
				* l_mat[Matrix4.M11] + z * l_mat[Matrix4.M21], x
				* l_mat[Matrix4.M02] + y * l_mat[Matrix4.M12] + z
				* l_mat[Matrix4.M22]);
	}

	public Vector3f untransform(final Matrix4 matrix) {
		final float l_mat[] = matrix.val;
		x -= l_mat[Matrix4.M03];
		y -= l_mat[Matrix4.M03];
		z -= l_mat[Matrix4.M03];
		return this.set(x * l_mat[Matrix4.M00] + y * l_mat[Matrix4.M10] + z
				* l_mat[Matrix4.M20], x * l_mat[Matrix4.M01] + y
				* l_mat[Matrix4.M11] + z * l_mat[Matrix4.M21], x
				* l_mat[Matrix4.M02] + y * l_mat[Matrix4.M12] + z
				* l_mat[Matrix4.M22]);
	}

	public Vector3f rotate(float degrees, float axisX, float axisY, float axisZ) {
		return this.mul(tmpMat.setToRotation(axisX, axisY, axisZ, degrees));
	}

	public Vector3f rotateRad(float radians, float axisX, float axisY,
			float axisZ) {
		return this.mul(tmpMat.setToRotationRad(axisX, axisY, axisZ, radians));
	}

	public Vector3f rotate(final Vector3f axis, float degrees) {
		tmpMat.setToRotation(axis, degrees);
		return this.mul(tmpMat);
	}

	public Vector3f rotateRad(final Vector3f axis, float radians) {
		tmpMat.setToRotationRad(axis, radians);
		return this.mul(tmpMat);
	}

	public boolean isUnit() {
		return isUnit(0.000000001f);
	}

	public boolean isUnit(final float margin) {
		return Math.abs(len2() - 1f) < margin;
	}

	public boolean isZero() {
		return x == 0 && y == 0 && z == 0;
	}

	public boolean isZero(final float margin) {
		return len2() < margin;
	}

	public boolean isOnLine(Vector3f other, float epsilon) {
		return len2(y * other.z - z * other.y, z * other.x - x * other.z, x
				* other.y - y * other.x) <= epsilon;
	}

	public boolean isOnLine(Vector3f other) {
		return len2(y * other.z - z * other.y, z * other.x - x * other.z, x
				* other.y - y * other.x) <= MathUtils.FLOAT_ROUNDING_ERROR;
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
		scl(1.0f - alpha);
		add(target.x * alpha, target.y * alpha, target.z * alpha);
		return this;
	}

	public Vector3f slerp(final Vector3f target, float alpha) {
		final float dot = dot(target);
		if (dot > 0.9995 || dot < -0.9995) {
			return lerp(target, alpha);
		}

		final float theta0 = (float) Math.acos(dot);
		final float theta = theta0 * alpha;

		final float st = (float) Math.sin(theta);
		final float tx = target.x - x * dot;
		final float ty = target.y - y * dot;
		final float tz = target.z - z * dot;
		final float l2 = tx * tx + ty * ty + tz * tz;
		final float dl = st
				* ((l2 < 0.0001f) ? 1f : 1f / (float) Math.sqrt(l2));

		return scl((float) Math.cos(theta)).add(tx * dl, ty * dl, tz * dl)
				.nor();
	}

	public String toString() {
		return "[" + x + ", " + y + ", " + z + "]";
	}

	public Vector3f limit(float limit) {
		return limit2(limit * limit);
	}

	public Vector3f limit2(float limit2) {
		float len2 = len2();
		if (len2 > limit2) {
			scl(limit2 / len2);
		}
		return this;
	}

	public Vector3f setLength(float len) {
		return setLength2(len * len);
	}

	public Vector3f setLength2(float len2) {
		float oldLen2 = len2();
		return (oldLen2 == 0 || oldLen2 == len2) ? this : scl((float) Math
				.sqrt(len2 / oldLen2));
	}

	public Vector3f clamp(float min, float max) {
		final float len2 = len2();
		if (len2 == 0f)
			return this;
		float max2 = max * max;
		if (len2 > max2)
			return scl((float) Math.sqrt(max2 / len2));
		float min2 = min * min;
		if (len2 < min2)
			return scl((float) Math.sqrt(min2 / len2));
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
		if (NumberUtils.floatToIntBits(x) != NumberUtils
				.floatToIntBits(other.x))
			return false;
		if (NumberUtils.floatToIntBits(y) != NumberUtils
				.floatToIntBits(other.y))
			return false;
		if (NumberUtils.floatToIntBits(z) != NumberUtils
				.floatToIntBits(other.z))
			return false;
		return true;
	}

	public boolean epsilonEquals(final Vector3f other, float epsilon) {
		if (other == null)
			return false;
		if (Math.abs(other.x - x) > epsilon)
			return false;
		if (Math.abs(other.y - y) > epsilon)
			return false;
		if (Math.abs(other.z - z) > epsilon)
			return false;
		return true;
	}

	public boolean epsilonEquals(float x, float y, float z, float epsilon) {
		if (Math.abs(x - this.x) > epsilon)
			return false;
		if (Math.abs(y - this.y) > epsilon)
			return false;
		if (Math.abs(z - this.z) > epsilon)
			return false;
		return true;
	}

	public Vector3f setZero() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		return this;
	}

}
