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

import loon.LSystem;
import loon.utils.Array;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.reply.TChange;

public class Vector3f implements Serializable, XYZ, SetXYZ {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1114108169708351982L;

	private static final Array<Vector3f> _VEC3_CACHE = new Array<Vector3f>();

	private final static Vector3f tmpNormal1 = new Vector3f();

	private final static Vector3f tmpNormal2 = new Vector3f();

	public final static Vector3f TMP() {
		Vector3f temp = _VEC3_CACHE.pop();
		if (temp == null) {
			_VEC3_CACHE.add(temp = new Vector3f(0, 0, 0));
		}
		return temp;
	}

	public final static Vector3f ZERO() {
		return new Vector3f(0);
	}

	public final static Vector3f HALF() {
		return new Vector3f(0.5f, 0.5f, 0.5f);
	}

	public final static Vector3f RIGHT() {
		return AXIS_X();
	}

	public final static Vector3f LEFT() {
		return new Vector3f(-1f, 0f, 0f);
	}

	public final static Vector3f UP() {
		return new Vector3f(0f, -1f, 0f);
	}

	public final static Vector3f DOWN() {
		return AXIS_Y();
	}

	public final static Vector3f ONE() {
		return new Vector3f(1);
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

	public final static Vector3f all(float v) {
		return new Vector3f(v, v, v);
	}

	public final static Vector3f at(float x, float y, float z) {
		return new Vector3f(x, y, z);
	}

	public final static Vector3f smoothStep(Vector3f a, Vector3f b, float amount) {
		return new Vector3f(MathUtils.smoothStep(a.x, b.x, amount), MathUtils.smoothStep(a.y, b.y, amount),
				MathUtils.smoothStep(a.z, b.z, amount));
	}

	public static Vector3f set(Vector3f v1, Vector3f v2) {
		return set(v1, v2.x, v2.y, v2.z);
	}

	public static Vector3f set(Vector3f v1, float[] values) {
		return set(v1, values[0], values[1], values[2]);
	}

	public static Vector3f set(Vector3f v1, float x, float y, float z) {
		v1.x = x;
		v1.y = y;
		v1.z = z;
		return v1;
	}

	public static Vector3f cpy(Vector3f v1) {
		Vector3f newSVector = new Vector3f();

		newSVector.x = v1.x;
		newSVector.y = v1.y;
		newSVector.z = v1.z;

		return newSVector;
	}

	public static Vector3f add(Vector3f v1, Vector3f v2) {
		v1.x += v2.x;
		v1.y += v2.y;
		v1.z += v2.z;

		return v1;
	}

	public static Vector3f add(Vector3f v1, float x, float y, float z) {
		v1.x += x;
		v1.y += y;
		v1.z += z;

		return v1;
	}

	public static Vector3f add(Vector3f v1, float v) {
		v1.x += v;
		v1.y += v;
		v1.z += v;

		return v1;
	}

	public static Vector3f sub(Vector3f v1, Vector3f v2) {
		v1.x -= v2.x;
		v1.y -= v2.y;
		v1.z -= v2.z;

		return v1;
	}

	public static Vector3f sub(Vector3f v1, float x, float y, float z) {
		v1.x -= x;
		v1.y -= y;
		v1.z -= z;

		return v1;
	}

	public static Vector3f sub(Vector3f v1, float v) {
		v1.x -= v;
		v1.y -= v;
		v1.z -= v;

		return v1;
	}

	public static Vector3f mul(Vector3f v1, Vector3f v2) {
		v1.x = v2.x * v1.x;
		v1.y = v2.y * v1.y;
		v1.z = v2.z * v1.z;

		return v1;
	}

	public static Vector3f mul(Vector3f v1, float v) {
		v1.x = v * v1.x;
		v1.y = v * v1.y;
		v1.z = v * v1.z;

		return v1;
	}

	public static Vector3f div(Vector3f v1, float v) {
		float d = 1 / v;
		v1.x = d * v1.x;
		v1.y = d * v1.y;
		v1.z = d * v1.z;

		return v1;
	}

	public static float len(Vector3f v1) {
		return MathUtils.sqrt(v1.x * v1.x + v1.y * v1.y + v1.z * v1.z);
	}

	public static float len2(Vector3f v1) {
		return v1.x * v1.x + v1.y * v1.y + v1.z * v1.z;
	}

	public static boolean idt(Vector3f v1, Vector3f v2) {
		return v1.x == v2.x && v1.y == v2.y && v1.z == v2.z;
	}

	public static float dst(Vector3f v1, Vector3f v2) {
		float a = v2.x - v1.x;
		float b = v2.y - v1.y;
		float c = v2.z - v1.z;

		a *= a;
		b *= b;
		c *= c;

		return MathUtils.sqrt(a + b + c);
	}

	public static float dst(Vector3f v1, float x, float y, float z) {
		float a = x - v1.x;
		float b = y - v1.y;
		float c = z - v1.z;

		a *= a;
		b *= b;
		c *= c;

		return MathUtils.sqrt(a + b + c);
	}

	public static Vector3f crs(Vector3f v1, Vector3f v2) {
		v1.x = v1.y * v2.z - v1.z * v2.y;
		v1.y = v1.z * v2.x - v1.x * v2.z;
		v1.z = v1.x * v2.y - v1.y * v2.x;

		return v1;
	}

	public static Vector3f crs(Vector3f v1, float x, float y, float z) {
		v1.x = v1.y * z - v1.z * y;
		v1.y = v1.z * x - v1.x * z;
		v1.z = v1.x * y - v1.y * x;

		return v1;
	}

	public static boolean isZero(Vector3f v1) {
		return v1.x == 0 && v1.y == 0 && v1.z == 0;
	}

	public static Vector3f lerp(Vector3f v1, Vector3f target, float alpha) {
		Vector3f r = mul(v1, 1.0f - alpha);
		add(r, mul(cpy(v1), alpha));
		return r;
	}

	public static float dot(Vector3f v1, float x, float y, float z) {
		return v1.x * x + v1.y * y + v1.z * z;
	}

	public static float dst2(Vector3f v1, Vector3f v2) {
		float a = v2.x - v1.x;
		float b = v2.y - v1.y;
		float c = v2.z - v1.z;

		a *= a;
		b *= b;
		c *= c;

		return a + b + c;
	}

	public static float dst2(Vector3f v1, float x, float y, float z) {
		float a = x - v1.x;
		float b = y - v1.y;
		float c = z - v1.z;

		a *= a;
		b *= b;
		c *= c;

		return a + b + c;
	}

	public static Vector3f scaleTo(Vector3f v1, float scalarX, float scalarY, float scalarZ) {
		v1.x *= scalarX;
		v1.y *= scalarY;
		v1.z *= scalarZ;
		return v1;
	}

	public static float angleBetween(Vector3f v1, Vector3f other) {
		float angle;

		float dot = dot(v1, other);

		float len1 = len(v1);
		float len2 = len(other);

		if (len1 == 0 && len2 == 0) {
			return 0;
		}

		angle = MathUtils.acos(dot / (len1 * len2));

		return angle;
	}

	public static float angleBetween(Vector3f v1, float x, float y, float z) {
		float angle;

		float dot = dot(v1, x, y, z);

		float len1 = len(v1);
		float len2 = MathUtils.sqrt(x * x + y * y + z * z);

		if (len1 == 0 || len2 == 0) {
			return 0;
		}

		angle = MathUtils.acos(dot / (len1 * len2));

		return angle;
	}

	public static float angleBetweenXY(Vector3f v1, float x, float y) {
		float angle;

		float dot = v1.x * x + v1.y * y;

		float len1 = MathUtils.sqrt(v1.x * v1.x + v1.y * v1.y);
		float len2 = MathUtils.sqrt(x * x + y * y);

		if (len1 == 0 || len2 == 0) {
			return 0;
		}

		angle = MathUtils.acos(dot / (len1 * len2));

		return angle;
	}

	public static float angleBetweenXZ(Vector3f v1, float x, float z) {
		float angle;

		float dot = v1.x * x + v1.z * z;

		float len1 = MathUtils.sqrt(v1.x * v1.x + v1.z * v1.z);
		float len2 = MathUtils.sqrt(x * x + z * z);

		if (len1 == 0 || len2 == 0) {
			return 0;
		}

		angle = MathUtils.acos(dot / (len1 * len2));

		return angle;
	}

	public static float angleBetweenYZ(Vector3f v1, float y, float z) {
		float angle;

		float dot = v1.y * y + v1.z * z;

		float len1 = MathUtils.sqrt(v1.y * v1.y + v1.z * v1.z);
		float len2 = MathUtils.sqrt(y * y + z * z);

		if (len1 == 0 || len2 == 0) {
			return 0;
		}

		angle = MathUtils.acos(dot / (len1 * len2));

		return angle;
	}

	public static float[] toArray(Vector3f v1) {
		return new float[] { v1.x, v1.y, v1.z };
	}

	public final static Vector3f cross(Vector3f a, Vector3f b) {
		return new Vector3f(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
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

	public float x;

	public float y;

	public float z;

	public Vector3f() {
		this(0, 0, 0);
	}

	public Vector3f(float x, float y, float z) {
		this.set(x, y, z);
	}

	public Vector3f(final XYZ v) {
		this.set(v);
	}

	public Vector3f(final Vector3f v) {
		this.set(v);
	}

	public Vector3f(final float[] values) {
		this.set(values[0], values[1], values[2]);
	}

	public Vector3f(final Vector2f v, float z) {
		this.set(v.x, v.y, z);
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

	public Vector3f set(final XYZ v) {
		return this.set(v.getX(), v.getY(), v.getZ());
	}

	public Vector3f set(final Vector3f v) {
		return this.set(v.x, v.y, v.z);
	}

	public Vector3f set(final float[] values) {
		return this.set(values[0], values[1], values[2]);
	}

	public Vector3f set(final Vector2f v, float z) {
		return this.set(v.x, v.y, z);
	}

	public Vector3f cpy() {
		return new Vector3f(this);
	}

	public Vector3f addSelf(final Vector3f v) {
		return this.addSelf(v.x, v.y, v.z);
	}

	public Vector3f addSelf(float x, float y, float z) {
		return this.set(this.x + x, this.y + y, this.z + z);
	}

	public Vector3f addSelf(float v) {
		return this.set(this.x + v, this.y + v, this.z + v);
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

	public boolean idt(final Vector3f v) {
		return x == v.x && y == v.y && z == v.z;
	}

	public static float dst(final float x1, final float y1, final float z1, final float x2, final float y2,
			final float z2) {
		final float a = x2 - x1;
		final float b = y2 - y1;
		final float c = z2 - z1;
		return MathUtils.sqrt(a * a + b * b + c * c);
	}

	public float dst(final Vector3f v) {
		final float a = v.x - x;
		final float b = v.y - y;
		final float c = v.z - z;
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

	public float dot(final Vector3f v) {
		return x * v.x + y * v.y + z * v.z;
	}

	public float dot(float x, float y, float z) {
		return this.x * x + this.y * y + this.z * z;
	}

	public final static float dot(Vector3f a, Vector3f b) {
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}

	public Vector3f crsSelf(final Vector3f v) {
		return this.set(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
	}

	public Vector3f crsSelf(float x, float y, float z) {
		return this.set(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
	}

	public Vector3f mul4x3(float[] matrix) {
		return set(x * matrix[0] + y * matrix[3] + z * matrix[6] + matrix[9],
				x * matrix[1] + y * matrix[4] + z * matrix[7] + matrix[10],
				x * matrix[2] + y * matrix[5] + z * matrix[8] + matrix[11]);
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

	public Vector3f round() {
		return new Vector3f(MathUtils.round(x), MathUtils.round(y), MathUtils.round(z));
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

	public boolean isPerpendicular(Vector3f v) {
		return MathUtils.isZero(dot(v));
	}

	public boolean isPerpendicular(Vector3f v, float epsilon) {
		return MathUtils.isZero(dot(v), epsilon);
	}

	public boolean hasSameDirection(Vector3f v) {
		return dot(v) > 0;
	}

	public boolean hasOppositeDirection(Vector3f v) {
		return dot(v) < 0;
	}

	public boolean hasDifferentValues(Vector3f vector) {
		return this.x != vector.x || this.y != vector.y || this.z != vector.z;
	}

	public Vector3f lerpSelf(float x, float y, float z, float alpha) {
		this.x += alpha * (x - this.x);
		this.y += alpha * (y - this.y);
		this.z += alpha * (z - this.z);
		return this;
	}

	public Vector3f lerp(float x, float y, float z, float alpha) {
		return cpy().lerpSelf(x, y, z, alpha);
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

	public Vector3f sign() {
		return set(MathUtils.sign(x), MathUtils.sign(y), MathUtils.sign(z));
	}

	public Vector3f signSelf() {
		return new Vector3f(MathUtils.sign(x), MathUtils.sign(y), MathUtils.sign(z));
	}

	public Vector3f slide(Vector3f normal) {
		return this.sub(normal.mul(dot(normal)));
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

	public final Vector3f setEmpty() {
		return set(0f);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int hashCode = 1;
		hashCode = prime * LSystem.unite(hashCode, x);
		hashCode = prime * LSystem.unite(hashCode, y);
		hashCode = prime * LSystem.unite(hashCode, z);
		return hashCode;
	}

	@Override
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

	public Vector3f scale(Vector3f origin, float dx, float dy, float dz) {
		return cpy().set((this.x - origin.x) * dx + origin.x, (this.y - origin.y) * dy + origin.y,
				(this.z - origin.z) * dz + origin.z);
	}

	public Vector3f scale(Vector3f origin, float dx) {
		return scale(origin, dx, dx, dx);
	}

	public Vector3f scale(Vector3f origin, float dx, float dy) {
		return scale(origin, dx, dy, 1f);
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

	public Vector3f rotateX(Vector3f origin, float angle) {
		float pY = this.y - origin.y;
		float pZ = this.z - origin.z;
		float cos = MathUtils.cos(angle);
		float sin = MathUtils.sin(angle);
		float z = pZ * cos - pY * sin;
		float y = pZ * sin + pY * cos;
		pZ = z;
		pY = y;
		return cpy().set(this.x, pY + origin.y, pZ + origin.z);
	}

	public Vector3f rotateY(Vector3f origin, float angle) {
		float pX = this.x - origin.x;
		float pZ = this.z - origin.z;
		float cos = MathUtils.cos(angle);
		float sin = MathUtils.sin(angle);
		float x = pX * cos - pZ * sin;
		float z = pX * sin + pZ * cos;
		pX = x;
		pZ = z;
		return cpy().set(pX + origin.x, this.y, pZ + origin.z);
	}

	public Vector3f rotateZ(Point origin, float angle) {
		float pX = this.x - origin.x;
		float pY = this.y - origin.y;
		float cos = MathUtils.cos(angle);
		float sin = MathUtils.sin(angle);
		float x = pX * cos - pY * sin;
		float y = pX * sin + pY * cos;
		pX = x;
		pY = y;
		return cpy().set(pX + origin.x, pY + origin.y, this.z);
	}

	public Vector3f scaleSelf(float s) {
		return scaleSelf(s, s, s);
	}

	public Vector3f scaleSelf(float sx, float sy, float sz) {
		return set(x * sx, y * sy, z * sz);
	}

	public Vector3f translate(float dx, float dy, float dz) {
		return cpy().translateSelf(dx, dy, dz);
	}

	public Vector3f translateSelf(float dx, float dy, float dz) {
		return set(this.x + dx, this.y + dy, this.z + dz);
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

	public Vector3f moveToward(Vector3f v, float delta) {
		Vector3f vd = v.sub(v);
		float len = vd.length();
		if (len <= delta || len < MathUtils.EPSILON) {
			return v;
		}
		return v.add(vd.div(len).mul(delta));
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

	@Override
	public void setX(float x) {
		this.x = x;

	}

	@Override
	public void setY(float y) {
		this.y = y;
	}

	@Override
	public void setZ(float z) {
		this.z = z;
	}

	public Vector3f nor() {
		float len = this.len();
		if (len == 0) {
			return this;
		} else {
			return this.div(len);
		}
	}

	public Vector3f div(float v) {
		float d = 1 / v;
		return this.set(this.x * d, this.y * d, this.z * d);
	}

	public Vector3f crs(Vector3f v) {
		return this.set(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
	}

	public Vector3f crs(float x, float y, float z) {
		return this.set(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
	}

	public Vector3f sub(float x, float y, float z) {
		return this.set(this.x - x, this.y - y, this.z - z);
	}

	public Vector3f sub(float v) {
		return this.set(this.x - v, this.y - v, this.z - v);
	}

	public Vector3f sub(Vector3f argVec) {
		return new Vector3f(x - argVec.x, y - argVec.y, z - argVec.z);
	}

	public Vector3f smoothStep(Vector3f v, float amount) {
		return smoothStep(this, v, amount);
	}

	public Vector3f mul(float argScalar) {
		return new Vector3f(x * argScalar, y * argScalar, z * argScalar);
	}

	public Vector3f mul(Vector3f v) {
		return new Vector3f(x * v.x, y * v.y, z * v.z);
	}

	public Vector3f random() {
		this.x = MathUtils.random(0f, LSystem.viewSize.getWidth());
		this.y = MathUtils.random(0f, LSystem.viewSize.getHeight());
		this.z = MathUtils.random();
		return this;
	}

	public ObservableXYZ<Vector3f> observable(TChange<Vector3f> v) {
		return ObservableXYZ.at(v, this, this);
	}

	public Vector2f toVector2() {
		return new Vector2f(x, y);
	}

	public Vector4f toVector4() {
		return new Vector4f(x, y, z, 0f);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

}
