package loon.geom;

import java.io.Serializable;

import loon.utils.MathUtils;
import loon.utils.NumberUtils;


public class Quaternion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Quaternion tmp1 = new Quaternion(0, 0, 0, 0);
	private static Quaternion tmp2 = new Quaternion(0, 0, 0, 0);

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

	public final static float len(final float x, final float y, final float z,
			final float w) {
		return (float) Math.sqrt(x * x + y * y + z * z + w * w);
	}

	public float len() {
		return (float) Math.sqrt(x * x + y * y + z * z + w * w);
	}

	@Override
	public String toString() {
		return "[" + x + "|" + y + "|" + z + "|" + w + "]";
	}

	public Quaternion setEulerAngles(float yaw, float pitch, float roll) {
		return setEulerAnglesRad(yaw * MathUtils.DEG_TO_RAD, pitch
				* MathUtils.DEG_TO_RAD, roll * MathUtils.DEG_TO_RAD);
	}

	public Quaternion setEulerAnglesRad(float yaw, float pitch, float roll) {
		final float hr = roll * 0.5f;
		final float shr = (float) Math.sin(hr);
		final float chr = (float) Math.cos(hr);
		final float hp = pitch * 0.5f;
		final float shp = (float) Math.sin(hp);
		final float chp = (float) Math.cos(hp);
		final float hy = yaw * 0.5f;
		final float shy = (float) Math.sin(hy);
		final float chy = (float) Math.cos(hy);
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
		return pole == 0 ? MathUtils.atan2(2f * (w * z + y * x), 1f - 2f * (x
				* x + z * z)) : (float) pole * 2f * MathUtils.atan2(y, w);
	}

	public float getRoll() {
		return getRollRad() * MathUtils.RAD_TO_DEG;
	}

	public float getPitchRad() {
		final int pole = getGimbalPole();
		return pole == 0 ? (float) Math.asin(MathUtils.clamp(2f * (w * x - z
				* y), -1f, 1f)) : (float) pole * MathUtils.PI * 0.5f;
	}

	public float getPitch() {
		return getPitchRad() * MathUtils.RAD_TO_DEG;
	}

	public float getYawRad() {
		return getGimbalPole() == 0 ? MathUtils.atan2(2f * (y * w + x * z),
				1f - 2f * (y * y + x * x)) : 0f;
	}

	public float getYaw() {
		return getYawRad() * MathUtils.RAD_TO_DEG;
	}

	public final static float len2(final float x, final float y, final float z,
			final float w) {
		return x * x + y * y + z * z + w * w;
	}

	public float len2() {
		return x * x + y * y + z * z + w * w;
	}

	public Quaternion nor() {
		float len = len2();
		if (len != 0.f && !MathUtils.isEqual(len, 1f)) {
			len = (float) Math.sqrt(len);
			w /= len;
			x /= len;
			y /= len;
			z /= len;
		}
		return this;
	}

	public Quaternion conjugate() {
		x = -x;
		y = -y;
		z = -z;
		return this;
	}

	public Vector3f transform(Vector3f v) {
		tmp2.set(this);
		tmp2.conjugate();
		tmp2.mulLeft(tmp1.set(v.x, v.y, v.z, 0)).mulLeft(this);

		v.x = tmp2.x;
		v.y = tmp2.y;
		v.z = tmp2.z;
		return v;
	}

	public Quaternion mul(final Quaternion other) {
		final float newX = this.w * other.x + this.x * other.w + this.y
				* other.z - this.z * other.y;
		final float newY = this.w * other.y + this.y * other.w + this.z
				* other.x - this.x * other.z;
		final float newZ = this.w * other.z + this.z * other.w + this.x
				* other.y - this.y * other.x;
		final float newW = this.w * other.w - this.x * other.x - this.y
				* other.y - this.z * other.z;
		this.x = newX;
		this.y = newY;
		this.z = newZ;
		this.w = newW;
		return this;
	}

	public Quaternion mul(final float x, final float y, final float z,
			final float w) {
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

	public Quaternion mulLeft(Quaternion other) {
		final float newX = other.w * this.x + other.x * this.w + other.y
				* this.z - other.z * y;
		final float newY = other.w * this.y + other.y * this.w + other.z
				* this.x - other.x * z;
		final float newZ = other.w * this.z + other.z * this.w + other.x
				* this.y - other.y * x;
		final float newW = other.w * this.w - other.x * this.x - other.y
				* this.y - other.z * z;
		this.x = newX;
		this.y = newY;
		this.z = newZ;
		this.w = newW;
		return this;
	}

	public Quaternion mulLeft(final float x, final float y, final float z,
			final float w) {
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

	public Quaternion add(Quaternion quaternion) {
		this.x += quaternion.x;
		this.y += quaternion.y;
		this.z += quaternion.z;
		this.w += quaternion.w;
		return this;
	}

	public Quaternion add(float qx, float qy, float qz, float qw) {
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
		return MathUtils.isZero(x) && MathUtils.isZero(y)
				&& MathUtils.isZero(z) && MathUtils.isEqual(w, 1f);
	}

	public boolean isIdentity(final float tolerance) {
		return MathUtils.isZero(x, tolerance) && MathUtils.isZero(y, tolerance)
				&& MathUtils.isZero(z, tolerance)
				&& MathUtils.isEqual(w, 1f, tolerance);
	}

	public Quaternion setFromAxis(final Vector3f axis, final float degrees) {
		return setFromAxis(axis.x, axis.y, axis.z, degrees);
	}

	public Quaternion setFromAxisRad(final Vector3f axis, final float radians) {
		return setFromAxisRad(axis.x, axis.y, axis.z, radians);
	}

	public Quaternion setFromAxis(final float x, final float y, final float z,
			final float degrees) {
		return setFromAxisRad(x, y, z, degrees * MathUtils.DEG_TO_RAD);
	}

	public Quaternion setFromAxisRad(final float x, final float y,
			final float z, final float radians) {
		float d = Vector3f.len(x, y, z);
		if (d == 0f)
			return idt();
		d = 1f / d;
		float l_ang = radians < 0 ? MathUtils.TWO_PI
				- (-radians % MathUtils.TWO_PI) : radians % MathUtils.TWO_PI;
		float l_sin = (float) Math.sin(l_ang / 2);
		float l_cos = (float) Math.cos(l_ang / 2);
		return this.set(d * x * l_sin, d * y * l_sin, d * z * l_sin, l_cos)
				.nor();
	}

	public Quaternion setFromMatrix(boolean normalizeAxes, Matrix4 matrix) {
		return setFromAxes(normalizeAxes, matrix.val[Matrix4.M00],
				matrix.val[Matrix4.M01], matrix.val[Matrix4.M02],
				matrix.val[Matrix4.M10], matrix.val[Matrix4.M11],
				matrix.val[Matrix4.M12], matrix.val[Matrix4.M20],
				matrix.val[Matrix4.M21], matrix.val[Matrix4.M22]);
	}

	public Quaternion setFromMatrix(Matrix4 matrix) {
		return setFromMatrix(false, matrix);
	}

	public Quaternion setFromMatrix(boolean normalizeAxes, Matrix3 matrix) {
		return setFromAxes(normalizeAxes, matrix.val[Matrix3.M00],
				matrix.val[Matrix3.M01], matrix.val[Matrix3.M02],
				matrix.val[Matrix3.M10], matrix.val[Matrix3.M11],
				matrix.val[Matrix3.M12], matrix.val[Matrix3.M20],
				matrix.val[Matrix3.M21], matrix.val[Matrix3.M22]);
	}

	public Quaternion setFromMatrix(Matrix3 matrix) {
		return setFromMatrix(false, matrix);
	}

	public Quaternion setFromAxes(float xx, float xy, float xz, float yx,
			float yy, float yz, float zx, float zy, float zz) {
		return setFromAxes(false, xx, xy, xz, yx, yy, yz, zx, zy, zz);
	}

	public Quaternion setFromAxes(boolean normalizeAxes, float xx, float xy,
			float xz, float yx, float yy, float yz, float zx, float zy, float zz) {
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
			float s = (float) Math.sqrt(t + 1);
			w = 0.5f * s;
			s = 0.5f / s;
			x = (zy - yz) * s;
			y = (xz - zx) * s;
			z = (yx - xy) * s;
		} else if ((xx > yy) && (xx > zz)) {
			float s = (float) Math.sqrt(1.0 + xx - yy - zz);
			x = s * 0.5f;
			s = 0.5f / s;
			y = (yx + xy) * s;
			z = (xz + zx) * s;
			w = (zy - yz) * s;
		} else if (yy > zz) {
			float s = (float) Math.sqrt(1.0 + yy - xx - zz);
			y = s * 0.5f;
			s = 0.5f / s;
			x = (yx + xy) * s;
			z = (zy + yz) * s;
			w = (xz - zx) * s;
		} else {
			float s = (float) Math.sqrt(1.0 + zz - xx - yy);
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
		final float angle = (float) Math.acos(dot);
		return setFromAxisRad(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x
				* v2.z, v1.x * v2.y - v1.y * v2.x, angle);
	}

	public Quaternion setFromCross(final float x1, final float y1,
			final float z1, final float x2, final float y2, final float z2) {
		final float dot = MathUtils.clamp(Vector3f.dot(x1, y1, z1, x2, y2, z2),
				-1f, 1f);
		final float angle = (float) Math.acos(dot);
		return setFromAxisRad(y1 * z2 - z1 * y2, z1 * x2 - x1 * z2, x1 * y2
				- y1 * x2, angle);
	}

	public Quaternion slerp(Quaternion end, float alpha) {
		final float dot = dot(end);
		float absDot = dot < 0.f ? -dot : dot;
		float scale0 = 1 - alpha;
		float scale1 = alpha;
		if ((1 - absDot) > 0.1) {
			final double angle = Math.acos(absDot);
			final double invSinTheta = 1f / Math.sin(angle);

			scale0 = (float) (Math.sin((1 - alpha) * angle) * invSinTheta);
			scale1 = (float) (Math.sin((alpha * angle)) * invSinTheta);
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

	public Quaternion slerp(Quaternion[] q) {

		final float w = 1.0f / q.length;
		set(q[0]).exp(w);
		for (int i = 1; i < q.length; i++) {
			mul(tmp1.set(q[i]).exp(w));
		}
		nor();
		return this;
	}

	public Quaternion slerp(Quaternion[] q, float[] w) {

		set(q[0]).exp(w[0]);
		for (int i = 1; i < q.length; i++) {
			mul(tmp1.set(q[i]).exp(w[i]));
		}
		nor();
		return this;
	}

	public Quaternion exp(float alpha) {

		float norm = len();
		float normExp = (float) Math.pow(norm, alpha);

		float theta = (float) Math.acos(w / norm);

		float coeff = 0;
		if (Math.abs(theta) < 0.001) {
			coeff = normExp * alpha / norm;
		} else {
			coeff = (float) (normExp * Math.sin(alpha * theta) / (norm * Math
					.sin(theta)));
		}

		w = (float) (normExp * Math.cos(alpha * theta));
		x *= coeff;
		y *= coeff;
		z *= coeff;

		nor();

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
		return (NumberUtils.floatToRawIntBits(w) == NumberUtils
				.floatToRawIntBits(other.w))
				&& (NumberUtils.floatToRawIntBits(x) == NumberUtils
						.floatToRawIntBits(other.x))
				&& (NumberUtils.floatToRawIntBits(y) == NumberUtils
						.floatToRawIntBits(other.y))
				&& (NumberUtils.floatToRawIntBits(z) == NumberUtils
						.floatToRawIntBits(other.z));
	}

	public final static float dot(final float x1, final float y1,
			final float z1, final float w1, final float x2, final float y2,
			final float z2, final float w2) {
		return x1 * x2 + y1 * y2 + z1 * z2 + w1 * w2;
	}

	public float dot(final Quaternion other) {
		return this.x * other.x + this.y * other.y + this.z * other.z + this.w
				* other.w;
	}

	public float dot(final float x, final float y, final float z, final float w) {
		return this.x * x + this.y * y + this.z * z + this.w * w;
	}

	public Quaternion mul(float scalar) {
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
			this.nor();
		}
		float angle = (float) (2.0 * Math.acos(this.w));
		double s = Math.sqrt(1 - this.w * this.w);
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
		return (float) (2.0 * Math.acos((this.w > 1) ? (this.w / len())
				: this.w));
	}

	public float getAngle() {
		return getAngleRad() * MathUtils.RAD_TO_DEG;
	}

	public void getSwingTwist(final float axisX, final float axisY,
			final float axisZ, final Quaternion swing, final Quaternion twist) {
		final float d = Vector3f.dot(this.x, this.y, this.z, axisX, axisY,
				axisZ);
		twist.set(axisX * d, axisY * d, axisZ * d, this.w).nor();
		swing.set(twist).conjugate().mulLeft(this);
	}

	public void getSwingTwist(final Vector3f axis, final Quaternion swing,
			final Quaternion twist) {
		getSwingTwist(axis.x, axis.y, axis.z, swing, twist);
	}

	public float getAngleAroundRad(final float axisX, final float axisY,
			final float axisZ) {
		final float d = Vector3f.dot(this.x, this.y, this.z, axisX, axisY,
				axisZ);
		final float l2 = Quaternion.len2(axisX * d, axisY * d, axisZ * d,
				this.w);
		return MathUtils.isZero(l2) ? 0f : (float) (2.0 * Math.acos(MathUtils
				.clamp((float) (this.w / Math.sqrt(l2)), -1f, 1f)));
	}

	public float getAngleAroundRad(final Vector3f axis) {
		return getAngleAroundRad(axis.x, axis.y, axis.z);
	}

	public float getAngleAround(final float axisX, final float axisY,
			final float axisZ) {
		return getAngleAroundRad(axisX, axisY, axisZ) * MathUtils.RAD_TO_DEG;
	}

	public float getAngleAround(final Vector3f axis) {
		return getAngleAround(axis.x, axis.y, axis.z);
	}
}
