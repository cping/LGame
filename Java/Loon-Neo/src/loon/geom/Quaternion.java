/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.geom;

import java.io.Serializable;

import loon.utils.Array;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;

public class Quaternion implements XY, Serializable {

	public static Quaternion createFromAxisAngle(Vector3f axis, float angle) {
		float half = angle * 0.5f;
		float sin = MathUtils.sin(half);
		float cos = MathUtils.cos(half);
		return new Quaternion(axis.x * sin, axis.y * sin, axis.z * sin, cos);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Quaternion tmp1 = new Quaternion(0, 0, 0, 0);
	private static Quaternion tmp2 = new Quaternion(0, 0, 0, 0);

	private static final Array<Quaternion> _quan_cache = new Array<Quaternion>();

	public final static Quaternion TMP() {
		Quaternion temp = _quan_cache.pop();
		if (temp == null) {
			_quan_cache.add(temp = new Quaternion(0, 0, 0, 0));
		}
		return temp;
	}

	public final static Quaternion ZERO() {
		return new Quaternion(0, 0, 0, 0);
	}

	public float x;
	public float y;
	public float z;
	public float w;

	public Quaternion(float x, float y, float z, float w) {
		this.set(x, y, z, w);
	}

	public Quaternion() {
		idt();
	}

	public Quaternion(Quaternion quaternion) {
		this.set(quaternion);
	}

	public Quaternion(Vector3f axis, float angle) {
		this.set(axis, angle);
	}

	public Quaternion(float pitch, float yaw, float roll) {
		set(pitch, yaw, roll);
	}

	public Quaternion set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}

	public Quaternion set(Quaternion quaternion) {
		return this.set(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
	}

	public Quaternion set(Vector3f axis, float angle) {
		return setFromAxis(axis.x, axis.y, axis.z, angle);
	}

	public Quaternion cpy() {
		return new Quaternion(this);
	}

	public final static float len(final float x, final float y, final float z, final float w) {
		return MathUtils.sqrt(x * x + y * y + z * z + w * w);
	}

	public float len() {
		return MathUtils.sqrt(x * x + y * y + z * z + w * w);
	}

	public Quaternion setEulerAnglesSelf(float yaw, float pitch, float roll) {
		return setEulerAnglesRadSelf(yaw * MathUtils.DEG_TO_RAD, pitch * MathUtils.DEG_TO_RAD,
				roll * MathUtils.DEG_TO_RAD);
	}

	public Quaternion setEulerAnglesRadSelf(float yaw, float pitch, float roll) {
		final float hr = roll * 0.5f;
		final float shr = MathUtils.sin(hr);
		final float chr = MathUtils.cos(hr);
		final float hp = pitch * 0.5f;
		final float shp = MathUtils.sin(hp);
		final float chp = MathUtils.cos(hp);
		final float hy = yaw * 0.5f;
		final float shy = MathUtils.sin(hy);
		final float chy = MathUtils.cos(hy);
		final float chy_shp = chy * shp;
		final float shy_chp = shy * chp;
		final float chy_chp = chy * chp;
		final float shy_shp = shy * shp;

		x = (chy_shp * chr) + (shy_chp * shr);
		y = (shy_chp * chr) - (chy_shp * shr);
		z = (chy_chp * shr) - (shy_shp * chr);
		w = (chy_chp * chr) + (shy_shp * shr);
		return this;
	}

	public int getGimbalPole() {
		final float t = y * x + z * w;
		return t > 0.499f ? 1 : (t < -0.499f ? -1 : 0);
	}

	public float getRollRad() {
		final int pole = getGimbalPole();
		return pole == 0 ? MathUtils.atan2(2f * (w * z + y * x), 1f - 2f * (x * x + z * z))
				: (float) pole * 2f * MathUtils.atan2(y, w);
	}

	public float getRoll() {
		return getRollRad() * MathUtils.RAD_TO_DEG;
	}

	public float getPitchRad() {
		final int pole = getGimbalPole();
		return pole == 0 ? MathUtils.asin(MathUtils.clamp(2f * (w * x - z * y), -1f, 1f))
				: (float) pole * MathUtils.PI * 0.5f;
	}

	public float getPitch() {
		return getPitchRad() * MathUtils.RAD_TO_DEG;
	}

	public float getYawRad() {
		return getGimbalPole() == 0 ? MathUtils.atan2(2f * (y * w + x * z), 1f - 2f * (y * y + x * x)) : 0f;
	}

	public float getYaw() {
		return getYawRad() * MathUtils.RAD_TO_DEG;
	}

	public final static float len2(final float x, final float y, final float z, final float w) {
		return x * x + y * y + z * z + w * w;
	}

	public float len2() {
		return x * x + y * y + z * z + w * w;
	}

	public Quaternion norSelf() {
		float len = len2();
		if (len != 0.f && !MathUtils.isEqual(len, 1f)) {
			len = MathUtils.sqrt(len);
			w /= len;
			x /= len;
			y /= len;
			z /= len;
		}
		return this;
	}

	public Quaternion conjugateSelf() {
		x = -x;
		y = -y;
		z = -z;
		return this;
	}

	public Vector3f transformSelf(Vector3f v) {
		tmp2.set(this);
		tmp2.conjugateSelf();
		tmp2.mulLeftSelf(tmp1.set(v.x, v.y, v.z, 0)).mulLeftSelf(this);

		v.x = tmp2.x;
		v.y = tmp2.y;
		v.z = tmp2.z;
		return v;
	}

	public Quaternion mulSelf(final Quaternion other) {
		final float newX = this.w * other.x + this.x * other.w + this.y * other.z - this.z * other.y;
		final float newY = this.w * other.y + this.y * other.w + this.z * other.x - this.x * other.z;
		final float newZ = this.w * other.z + this.z * other.w + this.x * other.y - this.y * other.x;
		final float newW = this.w * other.w - this.x * other.x - this.y * other.y - this.z * other.z;
		this.x = newX;
		this.y = newY;
		this.z = newZ;
		this.w = newW;
		return this;
	}

	public Quaternion mulSelf(final float x, final float y, final float z, final float w) {
		final float newX = this.w * x + this.x * w + this.y * z - this.z * y;
		final float newY = this.w * y + this.y * w + this.z * x - this.x * z;
		final float newZ = this.w * z + this.z * w + this.x * y - this.y * x;
		final float newW = this.w * w - this.x * x - this.y * y - this.z * z;
		this.x = newX;
		this.y = newY;
		this.z = newZ;
		this.w = newW;
		return this;
	}

	public Quaternion mulLeftSelf(Quaternion other) {
		final float newX = other.w * this.x + other.x * this.w + other.y * this.z - other.z * y;
		final float newY = other.w * this.y + other.y * this.w + other.z * this.x - other.x * z;
		final float newZ = other.w * this.z + other.z * this.w + other.x * this.y - other.y * x;
		final float newW = other.w * this.w - other.x * this.x - other.y * this.y - other.z * z;
		this.x = newX;
		this.y = newY;
		this.z = newZ;
		this.w = newW;
		return this;
	}

	public Quaternion mulLeftSelf(final float x, final float y, final float z, final float w) {
		final float newX = w * this.x + x * this.w + y * this.z - z * y;
		final float newY = w * this.y + y * this.w + z * this.x - x * z;
		final float newZ = w * this.z + z * this.w + x * this.y - y * x;
		final float newW = w * this.w - x * this.x - y * this.y - z * z;
		this.x = newX;
		this.y = newY;
		this.z = newZ;
		this.w = newW;
		return this;
	}

	public Quaternion addSelf(Quaternion quaternion) {
		this.x += quaternion.x;
		this.y += quaternion.y;
		this.z += quaternion.z;
		this.w += quaternion.w;
		return this;
	}

	public Quaternion addSelf(float qx, float qy, float qz, float qw) {
		this.x += qx;
		this.y += qy;
		this.z += qz;
		this.w += qw;
		return this;
	}

	public void toMatrix(final float[] matrix) {
		final float xx = x * x;
		final float xy = x * y;
		final float xz = x * z;
		final float xw = x * w;
		final float yy = y * y;
		final float yz = y * z;
		final float yw = y * w;
		final float zz = z * z;
		final float zw = z * w;

		matrix[Matrix4.M00] = 1 - 2 * (yy + zz);
		matrix[Matrix4.M01] = 2 * (xy - zw);
		matrix[Matrix4.M02] = 2 * (xz + yw);
		matrix[Matrix4.M03] = 0;
		matrix[Matrix4.M10] = 2 * (xy + zw);
		matrix[Matrix4.M11] = 1 - 2 * (xx + zz);
		matrix[Matrix4.M12] = 2 * (yz - xw);
		matrix[Matrix4.M13] = 0;
		matrix[Matrix4.M20] = 2 * (xz - yw);
		matrix[Matrix4.M21] = 2 * (yz + xw);
		matrix[Matrix4.M22] = 1 - 2 * (xx + yy);
		matrix[Matrix4.M23] = 0;
		matrix[Matrix4.M30] = 0;
		matrix[Matrix4.M31] = 0;
		matrix[Matrix4.M32] = 0;
		matrix[Matrix4.M33] = 1;
	}

	public Quaternion idt() {
		return this.set(0, 0, 0, 1);
	}

	public boolean isIdentity() {
		return MathUtils.isZero(x) && MathUtils.isZero(y) && MathUtils.isZero(z) && MathUtils.isEqual(w, 1f);
	}

	public boolean isIdentity(final float tolerance) {
		return MathUtils.isZero(x, tolerance) && MathUtils.isZero(y, tolerance) && MathUtils.isZero(z, tolerance)
				&& MathUtils.isEqual(w, 1f, tolerance);
	}

	public Quaternion setFromAxis(final Vector3f axis, final float degrees) {
		return setFromAxis(axis.x, axis.y, axis.z, degrees);
	}

	public Quaternion setFromAxisRad(final Vector3f axis, final float radians) {
		return setFromAxisRad(axis.x, axis.y, axis.z, radians);
	}

	public Quaternion setFromAxis(final float x, final float y, final float z, final float degrees) {
		return setFromAxisRad(x, y, z, degrees * MathUtils.DEG_TO_RAD);
	}

	public Quaternion setFromAxisRad(final float x, final float y, final float z, final float radians) {
		float d = Vector3f.len(x, y, z);
		if (d == 0f)
			return idt();
		d = 1f / d;
		float l_ang = radians < 0 ? MathUtils.TWO_PI - (-radians % MathUtils.TWO_PI) : radians % MathUtils.TWO_PI;
		float l_sin = MathUtils.sin(l_ang / 2);
		float l_cos = MathUtils.cos(l_ang / 2);
		return this.set(d * x * l_sin, d * y * l_sin, d * z * l_sin, l_cos).norSelf();
	}

	public Quaternion setFromMatrix(boolean normalizeAxes, Matrix4 matrix) {
		return setFromAxes(normalizeAxes, matrix.val[Matrix4.M00], matrix.val[Matrix4.M01], matrix.val[Matrix4.M02],
				matrix.val[Matrix4.M10], matrix.val[Matrix4.M11], matrix.val[Matrix4.M12], matrix.val[Matrix4.M20],
				matrix.val[Matrix4.M21], matrix.val[Matrix4.M22]);
	}

	public Quaternion setFromMatrix(Matrix4 matrix) {
		return setFromMatrix(false, matrix);
	}

	public Quaternion setFromMatrix(boolean normalizeAxes, Matrix3 matrix) {
		return setFromAxes(normalizeAxes, matrix.val[Matrix3.M00], matrix.val[Matrix3.M01], matrix.val[Matrix3.M02],
				matrix.val[Matrix3.M10], matrix.val[Matrix3.M11], matrix.val[Matrix3.M12], matrix.val[Matrix3.M20],
				matrix.val[Matrix3.M21], matrix.val[Matrix3.M22]);
	}

	public Quaternion setFromMatrix(Matrix3 matrix) {
		return setFromMatrix(false, matrix);
	}

	public Quaternion setFromAxes(float xx, float xy, float xz, float yx, float yy, float yz, float zx, float zy,
			float zz) {
		return setFromAxes(false, xx, xy, xz, yx, yy, yz, zx, zy, zz);
	}

	public Quaternion setFromAxes(boolean normalizeAxes, float xx, float xy, float xz, float yx, float yy, float yz,
			float zx, float zy, float zz) {
		if (normalizeAxes) {
			final float lx = 1f / Vector3f.len(xx, xy, xz);
			final float ly = 1f / Vector3f.len(yx, yy, yz);
			final float lz = 1f / Vector3f.len(zx, zy, zz);
			xx *= lx;
			xy *= lx;
			xz *= lx;
			yz *= ly;
			yy *= ly;
			yz *= ly;
			zx *= lz;
			zy *= lz;
			zz *= lz;
		}

		final float t = xx + yy + zz;

		if (t >= 0) {
			float s = MathUtils.sqrt(t + 1);
			w = 0.5f * s;
			s = 0.5f / s;
			x = (zy - yz) * s;
			y = (xz - zx) * s;
			z = (yx - xy) * s;
		} else if ((xx > yy) && (xx > zz)) {
			float s = MathUtils.sqrt(1f + xx - yy - zz);
			x = s * 0.5f;
			s = 0.5f / s;
			y = (yx + xy) * s;
			z = (xz + zx) * s;
			w = (zy - yz) * s;
		} else if (yy > zz) {
			float s = MathUtils.sqrt(1f + yy - xx - zz);
			y = s * 0.5f;
			s = 0.5f / s;
			x = (yx + xy) * s;
			z = (zy + yz) * s;
			w = (xz - zx) * s;
		} else {
			float s = MathUtils.sqrt(1f + zz - xx - yy);
			z = s * 0.5f;
			s = 0.5f / s;
			x = (xz + zx) * s;
			y = (zy + yz) * s;
			w = (yx - xy) * s;
		}

		return this;
	}

	public Quaternion setFromCross(final Vector3f v1, final Vector3f v2) {
		final float dot = MathUtils.clamp(v1.dot(v2), -1f, 1f);
		final float angle = MathUtils.acos(dot);
		return setFromAxisRad(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x, angle);
	}

	public Quaternion setFromCross(final float x1, final float y1, final float z1, final float x2, final float y2,
			final float z2) {
		final float dot = MathUtils.clamp(Vector3f.dot(x1, y1, z1, x2, y2, z2), -1f, 1f);
		final float angle = MathUtils.acos(dot);
		return setFromAxisRad(y1 * z2 - z1 * y2, z1 * x2 - x1 * z2, x1 * y2 - y1 * x2, angle);
	}

	public Quaternion slerpSelf(Quaternion end, float alpha) {
		final float dot = dot(end);
		float absDot = dot < 0.f ? -dot : dot;
		float scale0 = 1 - alpha;
		float scale1 = alpha;
		if ((1 - absDot) > 0.1) {
			final float angle = MathUtils.acos(absDot);
			final float invSinTheta = 1f / MathUtils.sin(angle);

			scale0 = (MathUtils.sin((1 - alpha) * angle) * invSinTheta);
			scale1 = (MathUtils.sin((alpha * angle)) * invSinTheta);
		}

		if (dot < 0.f) {
			scale1 = -scale1;
		}

		x = (scale0 * x) + (scale1 * end.x);
		y = (scale0 * y) + (scale1 * end.y);
		z = (scale0 * z) + (scale1 * end.z);
		w = (scale0 * w) + (scale1 * end.w);

		return this;
	}

	public Quaternion slerpSelf(Quaternion[] q) {

		final float w = 1.0f / q.length;
		set(q[0]).expSelf(w);
		for (int i = 1; i < q.length; i++) {
			mulSelf(tmp1.set(q[i]).expSelf(w));
		}
		norSelf();
		return this;
	}

	public Quaternion slerpSelf(Quaternion[] q, float[] w) {

		set(q[0]).expSelf(w[0]);
		for (int i = 1; i < q.length; i++) {
			mulSelf(tmp1.set(q[i]).expSelf(w[i]));
		}
		norSelf();
		return this;
	}

	public Quaternion expSelf(float alpha) {

		float norm = len();
		float normExp = MathUtils.pow(norm, alpha);

		float theta = MathUtils.acos(w / norm);

		float coeff = 0;
		if (MathUtils.abs(theta) < 0.001) {
			coeff = normExp * alpha / norm;
		} else {
			coeff = (float) (normExp * MathUtils.sin(alpha * theta) / (norm * MathUtils.sin(theta)));
		}

		w = (float) (normExp * MathUtils.cos(alpha * theta));
		x *= coeff;
		y *= coeff;
		z *= coeff;

		norSelf();

		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + NumberUtils.floatToRawIntBits(w);
		result = prime * result + NumberUtils.floatToRawIntBits(x);
		result = prime * result + NumberUtils.floatToRawIntBits(y);
		result = prime * result + NumberUtils.floatToRawIntBits(z);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Quaternion)) {
			return false;
		}
		Quaternion other = (Quaternion) obj;
		return (NumberUtils.floatToRawIntBits(w) == NumberUtils.floatToRawIntBits(other.w))
				&& (NumberUtils.floatToRawIntBits(x) == NumberUtils.floatToRawIntBits(other.x))
				&& (NumberUtils.floatToRawIntBits(y) == NumberUtils.floatToRawIntBits(other.y))
				&& (NumberUtils.floatToRawIntBits(z) == NumberUtils.floatToRawIntBits(other.z));
	}

	public final static float dot(final float x1, final float y1, final float z1, final float w1, final float x2,
			final float y2, final float z2, final float w2) {
		return x1 * x2 + y1 * y2 + z1 * z2 + w1 * w2;
	}

	public float dot(final Quaternion other) {
		return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
	}

	public float dot(final float x, final float y, final float z, final float w) {
		return this.x * x + this.y * y + this.z * z + this.w * w;
	}

	public Quaternion mulSelf(float scalar) {
		this.x *= scalar;
		this.y *= scalar;
		this.z *= scalar;
		this.w *= scalar;
		return this;
	}

	public float getAxisAngle(Vector3f axis) {
		return getAxisAngleRad(axis) * MathUtils.RAD_TO_DEG;
	}

	public float getAxisAngleRad(Vector3f axis) {
		if (this.w > 1) {
			this.norSelf();
		}
		float angle = (float) (2.0 * MathUtils.acos(this.w));
		double s = MathUtils.sqrt(1 - this.w * this.w);
		if (s < MathUtils.FLOAT_ROUNDING_ERROR) {
			axis.x = this.x;
			axis.y = this.y;
			axis.z = this.z;
		} else {
			axis.x = (float) (this.x / s);
			axis.y = (float) (this.y / s);
			axis.z = (float) (this.z / s);
		}

		return angle;
	}

	public float getAngleRad() {
		return (float) (2.0 * MathUtils.acos((this.w > 1) ? (this.w / len()) : this.w));
	}

	public float getAngle() {
		return getAngleRad() * MathUtils.RAD_TO_DEG;
	}

	public void getSwingTwistSelf(final float axisX, final float axisY, final float axisZ, final Quaternion swing,
			final Quaternion twist) {
		final float d = Vector3f.dot(this.x, this.y, this.z, axisX, axisY, axisZ);
		twist.set(axisX * d, axisY * d, axisZ * d, this.w).norSelf();
		swing.set(twist).conjugateSelf().mulLeftSelf(this);
	}

	public void getSwingTwistSelf(final Vector3f axis, final Quaternion swing, final Quaternion twist) {
		getSwingTwistSelf(axis.x, axis.y, axis.z, swing, twist);
	}

	public float getAngleAroundRad(final float axisX, final float axisY, final float axisZ) {
		final float d = Vector3f.dot(this.x, this.y, this.z, axisX, axisY, axisZ);
		final float l2 = Quaternion.len2(axisX * d, axisY * d, axisZ * d, this.w);
		return MathUtils.isZero(l2) ? 0f
				: (float) (2.0 * MathUtils.acos(MathUtils.clamp((float) (this.w / MathUtils.sqrt(l2)), -1f, 1f)));
	}

	public float getAngleAroundRad(final Vector3f axis) {
		return getAngleAroundRad(axis.x, axis.y, axis.z);
	}

	public float getAngleAround(final float axisX, final float axisY, final float axisZ) {
		return getAngleAroundRad(axisX, axisY, axisZ) * MathUtils.RAD_TO_DEG;
	}

	public float getAngleAround(final Vector3f axis) {
		return getAngleAround(axis.x, axis.y, axis.z);
	}

	public Quaternion set(float pitch, float yaw, float roll) {
		pitch = MathUtils.toRadians(pitch) * 0.5f;
		yaw = MathUtils.toRadians(yaw) * 0.5f;
		roll = MathUtils.toRadians(roll) * 0.5f;

		float sinP = MathUtils.sin(pitch);
		float sinY = MathUtils.sin(yaw);
		float sinR = MathUtils.sin(roll);
		float cosP = MathUtils.cos(pitch);
		float cosY = MathUtils.cos(yaw);
		float cosR = MathUtils.cos(roll);

		x = sinP * cosY * cosR - cosP * sinY * sinR;
		y = cosP * sinY * cosR + sinP * cosY * sinR;
		z = cosP * cosY * sinR - sinP * sinY * cosR;
		w = cosP * cosY * cosR + sinP * sinY * sinR;

		return this;
	}

	public Quaternion add(Quaternion q) {
		return add(q.x, q.y, q.z, q.w);
	}

	public Quaternion add(float x, float y, float z, float w) {
		return cpy().addSelf(x, y, z, w);
	}

	public Quaternion subtract(Quaternion q) {
		return subtract(q.x, q.y, q.z, q.w);
	}

	public Quaternion subtract(float x, float y, float z, float w) {
		return add(-x, -y, -z, -w);
	}

	public Quaternion subtractSelf(Quaternion q) {
		return subtractSelf(q.x, q.y, q.z, q.w);
	}

	public Quaternion subtractSelf(float x, float y, float z, float w) {
		return addSelf(-x, -y, -z, -w);
	}

	public Quaternion normalize() {
		return cpy().normalizeSelf();
	}

	public float length() {
		return MathUtils.sqrt(lengthSquared());
	}

	public float lengthSquared() {
		return x * x + y * y + z * z + w * w;
	}

	public Quaternion multiply(Quaternion q) {
		return cpy().multiplySelf(q);
	}

	public Quaternion normalizeSelf() {
		float length = length();

		if (length == 0 || length == 1) {
			return this;
		}

		return set(x / length, y / length, z / length, w / length);
	}

	public Quaternion multiplySelf(Quaternion q) {
		float nx = w * q.x + x * q.w + y * q.z - z * q.y;
		float ny = w * q.y + y * q.w + z * q.x - x * q.z;
		float nz = w * q.z + z * q.w + x * q.y - y * q.x;
		float nw = w * q.w - x * q.x - y * q.y - z * q.z;

		return set(nx, ny, nz, nw).normalizeSelf();
	}

	public Vector3f multiplyInverse(Vector3f v) {
		return multiplyInverse(v, new Vector3f());
	}

	public Vector3f multiplyInverse(Vector3f v, Vector3f dest) {
		invertSelf().multiply(v, dest);
		invertSelf();

		return dest;
	}

	public Vector3f multiply(Vector3f v) {
		return multiply(v, new Vector3f());
	}

	public Vector3f multiply(Vector3f v, Vector3f dest) {
		Vector3f temp = Vector3f.TMP();

		Quaternion temp1 = Quaternion.TMP();
		Quaternion temp2 = Quaternion.TMP();
		Quaternion temp3 = Quaternion.TMP();

		float length = v.length();
		v = temp.set(v).normalizeSelf();

		Quaternion q1 = temp1.set(this).conjugateSelf().normalizeSelf();
		Quaternion qv = temp2.set(v.x, v.y, v.z, 0);
		Quaternion q = this;

		Quaternion res = temp3.set(q).normalizeSelf().multiplySelf(qv.multiplySelf(q1).normalizeSelf());

		dest.x = res.x;
		dest.y = res.y;
		dest.z = res.z;

		return dest.normalizeSelf().scaleSelf(length);
	}

	public Quaternion invert() {
		return cpy().invertSelf();
	}

	public Quaternion invertSelf() {
		float norm = lengthSquared();

		if (norm == 0) {
			return conjugateSelf();
		}

		x = -x / norm;
		y = -y / norm;
		z = -z / norm;
		w = +w / norm;

		return this;
	}

	public Quaternion lerp(Quaternion target, float alpha) {
		return cpy().lerpSelf(target, alpha);
	}

	public Quaternion lerpSelf(Quaternion target, float alpha) {
		Vector4f temp1 = Vector4f.TMP();
		Vector4f temp2 = Vector4f.TMP();

		Vector4f start = temp1.set(x, y, z, w);
		Vector4f end = temp2.set(target.x, target.y, target.z, target.w);
		Vector4f lerp = start.lerpSelf(end, alpha).normalizeSelf();

		set(lerp.x, lerp.y, lerp.z, lerp.w);

		return this;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getW() {
		return w;
	}

	public void setW(float w) {
		this.w = w;
	}

	public Quaternion set() {
		return set(0, 0, 0, 1);
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + "," + z + "," + w + ")";
	}

}
