package loon.core.graphics.opengl.math;

import java.io.Serializable;

import loon.jni.NativeSupport;
import loon.utils.MathUtils;

public class Transform4 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4331834668907675786L;
	
	public static final int M00 = 0;

	public static final int M01 = 4;

	public static final int M02 = 8;

	public static final int M03 = 12;

	public static final int M10 = 1;

	public static final int M11 = 5;

	public static final int M12 = 9;

	public static final int M13 = 13;

	public static final int M20 = 2;

	public static final int M21 = 6;

	public static final int M22 = 10;

	public static final int M23 = 14;

	public static final int M30 = 3;

	public static final int M31 = 7;

	public static final int M32 = 11;

	public static final int M33 = 15;


	public static final float tmp[] = new float[16]; 
	public final float val[] = new float[16];

	public Transform4() {
		val[M00] = 1f;
		val[M11] = 1f;
		val[M22] = 1f;
		val[M33] = 1f;
	}

	public Transform4(Transform4 matrix) {
		this.set(matrix);
	}

	public Transform4(float[] values) {
		this.set(values);
	}

	public Transform4(Quaternion quaternion) {
		this.set(quaternion);
	}

	public Transform4(Location3 position, Quaternion rotation, Location3 scale) {
		set(position, rotation, scale);
	}

	public Transform4 set(Transform4 matrix) {
		return this.set(matrix.val);
	}

	public Transform4 set(float[] values) {
		System.arraycopy(values, 0, val, 0, val.length);
		return this;
	}

	public Transform4 set(Quaternion quaternion) {
		return set(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
	}

	public Transform4 set(float quaternionX, float quaternionY, float quaternionZ,
			float quaternionW) {
		return set(0f, 0f, 0f, quaternionX, quaternionY, quaternionZ,
				quaternionW);
	}

	public Transform4 set(Location3 position, Quaternion orientation) {
		return set(position.x, position.y, position.z, orientation.x,
				orientation.y, orientation.z, orientation.w);
	}

	public Transform4 set(float translationX, float translationY,
			float translationZ, float quaternionX, float quaternionY,
			float quaternionZ, float quaternionW) {
		final float xs = quaternionX * 2f, ys = quaternionY * 2f, zs = quaternionZ * 2f;
		final float wx = quaternionW * xs, wy = quaternionW * ys, wz = quaternionW
				* zs;
		final float xx = quaternionX * xs, xy = quaternionX * ys, xz = quaternionX
				* zs;
		final float yy = quaternionY * ys, yz = quaternionY * zs, zz = quaternionZ
				* zs;

		val[M00] = (1.0f - (yy + zz));
		val[M01] = (xy - wz);
		val[M02] = (xz + wy);
		val[M03] = translationX;

		val[M10] = (xy + wz);
		val[M11] = (1.0f - (xx + zz));
		val[M12] = (yz - wx);
		val[M13] = translationY;

		val[M20] = (xz - wy);
		val[M21] = (yz + wx);
		val[M22] = (1.0f - (xx + yy));
		val[M23] = translationZ;

		val[M30] = 0.f;
		val[M31] = 0.f;
		val[M32] = 0.f;
		val[M33] = 1.0f;
		return this;
	}

	public Transform4 set(Location3 position, Quaternion orientation, Location3 scale) {
		return set(position.x, position.y, position.z, orientation.x,
				orientation.y, orientation.z, orientation.w, scale.x, scale.y,
				scale.z);
	}

	public Transform4 set(float translationX, float translationY,
			float translationZ, float quaternionX, float quaternionY,
			float quaternionZ, float quaternionW, float scaleX, float scaleY,
			float scaleZ) {
		final float xs = quaternionX * 2f, ys = quaternionY * 2f, zs = quaternionZ * 2f;
		final float wx = quaternionW * xs, wy = quaternionW * ys, wz = quaternionW
				* zs;
		final float xx = quaternionX * xs, xy = quaternionX * ys, xz = quaternionX
				* zs;
		final float yy = quaternionY * ys, yz = quaternionY * zs, zz = quaternionZ
				* zs;

		val[M00] = scaleX * (1.0f - (yy + zz));
		val[M01] = scaleY * (xy - wz);
		val[M02] = scaleZ * (xz + wy);
		val[M03] = translationX;

		val[M10] = scaleX * (xy + wz);
		val[M11] = scaleY * (1.0f - (xx + zz));
		val[M12] = scaleZ * (yz - wx);
		val[M13] = translationY;

		val[M20] = scaleX * (xz - wy);
		val[M21] = scaleY * (yz + wx);
		val[M22] = scaleZ * (1.0f - (xx + yy));
		val[M23] = translationZ;

		val[M30] = 0.f;
		val[M31] = 0.f;
		val[M32] = 0.f;
		val[M33] = 1.0f;
		return this;
	}

	public Transform4 set(Location3 xAxis, Location3 yAxis, Location3 zAxis, Location3 pos) {
		val[M00] = xAxis.x;
		val[M01] = xAxis.y;
		val[M02] = xAxis.z;
		val[M10] = yAxis.x;
		val[M11] = yAxis.y;
		val[M12] = yAxis.z;
		val[M20] = zAxis.x;
		val[M21] = zAxis.y;
		val[M22] = zAxis.z;
		val[M03] = pos.x;
		val[M13] = pos.y;
		val[M23] = pos.z;
		val[M30] = 0;
		val[M31] = 0;
		val[M32] = 0;
		val[M33] = 1;
		return this;
	}

	public Transform4 cpy() {
		return new Transform4(this);
	}

	public Transform4 trn(Location3 vector) {
		val[M03] += vector.x;
		val[M13] += vector.y;
		val[M23] += vector.z;
		return this;
	}

	public Transform4 trn(float x, float y, float z) {
		val[M03] += x;
		val[M13] += y;
		val[M23] += z;
		return this;
	}

	public float[] getValues() {
		return val;
	}

	public Transform4 mul(Transform4 matrix) {
		NativeSupport.mul(val, matrix.val);
		return this;
	}

	public Transform4 mulLeft(Transform4 matrix) {
		tmpMat.set(matrix);
		NativeSupport.mul(tmpMat.val, this.val);
		return set(tmpMat);
	}

	public Transform4 tra() {
		tmp[M00] = val[M00];
		tmp[M01] = val[M10];
		tmp[M02] = val[M20];
		tmp[M03] = val[M30];
		tmp[M10] = val[M01];
		tmp[M11] = val[M11];
		tmp[M12] = val[M21];
		tmp[M13] = val[M31];
		tmp[M20] = val[M02];
		tmp[M21] = val[M12];
		tmp[M22] = val[M22];
		tmp[M23] = val[M32];
		tmp[M30] = val[M03];
		tmp[M31] = val[M13];
		tmp[M32] = val[M23];
		tmp[M33] = val[M33];
		return set(tmp);
	}

	public Transform4 idt() {
		val[M00] = 1;
		val[M01] = 0;
		val[M02] = 0;
		val[M03] = 0;
		val[M10] = 0;
		val[M11] = 1;
		val[M12] = 0;
		val[M13] = 0;
		val[M20] = 0;
		val[M21] = 0;
		val[M22] = 1;
		val[M23] = 0;
		val[M30] = 0;
		val[M31] = 0;
		val[M32] = 0;
		val[M33] = 1;
		return this;
	}

	public Transform4 inv() {
		float l_det = val[M30] * val[M21] * val[M12] * val[M03] - val[M20]
				* val[M31] * val[M12] * val[M03] - val[M30] * val[M11]
				* val[M22] * val[M03] + val[M10] * val[M31] * val[M22]
				* val[M03] + val[M20] * val[M11] * val[M32] * val[M03]
				- val[M10] * val[M21] * val[M32] * val[M03] - val[M30]
				* val[M21] * val[M02] * val[M13] + val[M20] * val[M31]
				* val[M02] * val[M13] + val[M30] * val[M01] * val[M22]
				* val[M13] - val[M00] * val[M31] * val[M22] * val[M13]
				- val[M20] * val[M01] * val[M32] * val[M13] + val[M00]
				* val[M21] * val[M32] * val[M13] + val[M30] * val[M11]
				* val[M02] * val[M23] - val[M10] * val[M31] * val[M02]
				* val[M23] - val[M30] * val[M01] * val[M12] * val[M23]
				+ val[M00] * val[M31] * val[M12] * val[M23] + val[M10]
				* val[M01] * val[M32] * val[M23] - val[M00] * val[M11]
				* val[M32] * val[M23] - val[M20] * val[M11] * val[M02]
				* val[M33] + val[M10] * val[M21] * val[M02] * val[M33]
				+ val[M20] * val[M01] * val[M12] * val[M33] - val[M00]
				* val[M21] * val[M12] * val[M33] - val[M10] * val[M01]
				* val[M22] * val[M33] + val[M00] * val[M11] * val[M22]
				* val[M33];
		if (l_det == 0f)
			throw new RuntimeException("non-invertible matrix");
		float inv_det = 1.0f / l_det;
		tmp[M00] = val[M12] * val[M23] * val[M31] - val[M13] * val[M22]
				* val[M31] + val[M13] * val[M21] * val[M32] - val[M11]
				* val[M23] * val[M32] - val[M12] * val[M21] * val[M33]
				+ val[M11] * val[M22] * val[M33];
		tmp[M01] = val[M03] * val[M22] * val[M31] - val[M02] * val[M23]
				* val[M31] - val[M03] * val[M21] * val[M32] + val[M01]
				* val[M23] * val[M32] + val[M02] * val[M21] * val[M33]
				- val[M01] * val[M22] * val[M33];
		tmp[M02] = val[M02] * val[M13] * val[M31] - val[M03] * val[M12]
				* val[M31] + val[M03] * val[M11] * val[M32] - val[M01]
				* val[M13] * val[M32] - val[M02] * val[M11] * val[M33]
				+ val[M01] * val[M12] * val[M33];
		tmp[M03] = val[M03] * val[M12] * val[M21] - val[M02] * val[M13]
				* val[M21] - val[M03] * val[M11] * val[M22] + val[M01]
				* val[M13] * val[M22] + val[M02] * val[M11] * val[M23]
				- val[M01] * val[M12] * val[M23];
		tmp[M10] = val[M13] * val[M22] * val[M30] - val[M12] * val[M23]
				* val[M30] - val[M13] * val[M20] * val[M32] + val[M10]
				* val[M23] * val[M32] + val[M12] * val[M20] * val[M33]
				- val[M10] * val[M22] * val[M33];
		tmp[M11] = val[M02] * val[M23] * val[M30] - val[M03] * val[M22]
				* val[M30] + val[M03] * val[M20] * val[M32] - val[M00]
				* val[M23] * val[M32] - val[M02] * val[M20] * val[M33]
				+ val[M00] * val[M22] * val[M33];
		tmp[M12] = val[M03] * val[M12] * val[M30] - val[M02] * val[M13]
				* val[M30] - val[M03] * val[M10] * val[M32] + val[M00]
				* val[M13] * val[M32] + val[M02] * val[M10] * val[M33]
				- val[M00] * val[M12] * val[M33];
		tmp[M13] = val[M02] * val[M13] * val[M20] - val[M03] * val[M12]
				* val[M20] + val[M03] * val[M10] * val[M22] - val[M00]
				* val[M13] * val[M22] - val[M02] * val[M10] * val[M23]
				+ val[M00] * val[M12] * val[M23];
		tmp[M20] = val[M11] * val[M23] * val[M30] - val[M13] * val[M21]
				* val[M30] + val[M13] * val[M20] * val[M31] - val[M10]
				* val[M23] * val[M31] - val[M11] * val[M20] * val[M33]
				+ val[M10] * val[M21] * val[M33];
		tmp[M21] = val[M03] * val[M21] * val[M30] - val[M01] * val[M23]
				* val[M30] - val[M03] * val[M20] * val[M31] + val[M00]
				* val[M23] * val[M31] + val[M01] * val[M20] * val[M33]
				- val[M00] * val[M21] * val[M33];
		tmp[M22] = val[M01] * val[M13] * val[M30] - val[M03] * val[M11]
				* val[M30] + val[M03] * val[M10] * val[M31] - val[M00]
				* val[M13] * val[M31] - val[M01] * val[M10] * val[M33]
				+ val[M00] * val[M11] * val[M33];
		tmp[M23] = val[M03] * val[M11] * val[M20] - val[M01] * val[M13]
				* val[M20] - val[M03] * val[M10] * val[M21] + val[M00]
				* val[M13] * val[M21] + val[M01] * val[M10] * val[M23]
				- val[M00] * val[M11] * val[M23];
		tmp[M30] = val[M12] * val[M21] * val[M30] - val[M11] * val[M22]
				* val[M30] - val[M12] * val[M20] * val[M31] + val[M10]
				* val[M22] * val[M31] + val[M11] * val[M20] * val[M32]
				- val[M10] * val[M21] * val[M32];
		tmp[M31] = val[M01] * val[M22] * val[M30] - val[M02] * val[M21]
				* val[M30] + val[M02] * val[M20] * val[M31] - val[M00]
				* val[M22] * val[M31] - val[M01] * val[M20] * val[M32]
				+ val[M00] * val[M21] * val[M32];
		tmp[M32] = val[M02] * val[M11] * val[M30] - val[M01] * val[M12]
				* val[M30] - val[M02] * val[M10] * val[M31] + val[M00]
				* val[M12] * val[M31] + val[M01] * val[M10] * val[M32]
				- val[M00] * val[M11] * val[M32];
		tmp[M33] = val[M01] * val[M12] * val[M20] - val[M02] * val[M11]
				* val[M20] + val[M02] * val[M10] * val[M21] - val[M00]
				* val[M12] * val[M21] - val[M01] * val[M10] * val[M22]
				+ val[M00] * val[M11] * val[M22];
		val[M00] = tmp[M00] * inv_det;
		val[M01] = tmp[M01] * inv_det;
		val[M02] = tmp[M02] * inv_det;
		val[M03] = tmp[M03] * inv_det;
		val[M10] = tmp[M10] * inv_det;
		val[M11] = tmp[M11] * inv_det;
		val[M12] = tmp[M12] * inv_det;
		val[M13] = tmp[M13] * inv_det;
		val[M20] = tmp[M20] * inv_det;
		val[M21] = tmp[M21] * inv_det;
		val[M22] = tmp[M22] * inv_det;
		val[M23] = tmp[M23] * inv_det;
		val[M30] = tmp[M30] * inv_det;
		val[M31] = tmp[M31] * inv_det;
		val[M32] = tmp[M32] * inv_det;
		val[M33] = tmp[M33] * inv_det;
		return this;
	}

	public float det() {
		return val[M30] * val[M21] * val[M12] * val[M03] - val[M20] * val[M31]
				* val[M12] * val[M03] - val[M30] * val[M11] * val[M22]
				* val[M03] + val[M10] * val[M31] * val[M22] * val[M03]
				+ val[M20] * val[M11] * val[M32] * val[M03] - val[M10]
				* val[M21] * val[M32] * val[M03] - val[M30] * val[M21]
				* val[M02] * val[M13] + val[M20] * val[M31] * val[M02]
				* val[M13] + val[M30] * val[M01] * val[M22] * val[M13]
				- val[M00] * val[M31] * val[M22] * val[M13] - val[M20]
				* val[M01] * val[M32] * val[M13] + val[M00] * val[M21]
				* val[M32] * val[M13] + val[M30] * val[M11] * val[M02]
				* val[M23] - val[M10] * val[M31] * val[M02] * val[M23]
				- val[M30] * val[M01] * val[M12] * val[M23] + val[M00]
				* val[M31] * val[M12] * val[M23] + val[M10] * val[M01]
				* val[M32] * val[M23] - val[M00] * val[M11] * val[M32]
				* val[M23] - val[M20] * val[M11] * val[M02] * val[M33]
				+ val[M10] * val[M21] * val[M02] * val[M33] + val[M20]
				* val[M01] * val[M12] * val[M33] - val[M00] * val[M21]
				* val[M12] * val[M33] - val[M10] * val[M01] * val[M22]
				* val[M33] + val[M00] * val[M11] * val[M22] * val[M33];
	}

	public float det3x3() {
		return val[M00] * val[M11] * val[M22] + val[M01] * val[M12] * val[M20]
				+ val[M02] * val[M10] * val[M21] - val[M00] * val[M12]
				* val[M21] - val[M01] * val[M10] * val[M22] - val[M02]
				* val[M11] * val[M20];
	}

	public Transform4 setToProjection(float near, float far, float fovy,
			float aspectRatio) {
		idt();
		float l_fd = (float) (1.0 / Math.tan((fovy * (Math.PI / 180)) / 2.0));
		float l_a1 = (far + near) / (near - far);
		float l_a2 = (2 * far * near) / (near - far);
		val[M00] = l_fd / aspectRatio;
		val[M10] = 0;
		val[M20] = 0;
		val[M30] = 0;
		val[M01] = 0;
		val[M11] = l_fd;
		val[M21] = 0;
		val[M31] = 0;
		val[M02] = 0;
		val[M12] = 0;
		val[M22] = l_a1;
		val[M32] = -1;
		val[M03] = 0;
		val[M13] = 0;
		val[M23] = l_a2;
		val[M33] = 0;

		return this;
	}

	public Transform4 setToOrtho2D(float x, float y, float width, float height) {
		setToOrtho(x, x + width, y + height, y, 1f, -1f);
		return this;
	}

	public Transform4 setToOrtho2D(float x, float y, float width, float height,
			float near, float far) {
		setToOrtho(x, x + width, y + height, y, near, far);
		return this;
	}

	public Transform4 setToOrtho(float left, float right, float bottom, float top,
			float near, float far) {

		this.idt();
		float x_orth = 2 / (right - left);
		float y_orth = 2 / (top - bottom);
		float z_orth = -2 / (far - near);

		float tx = -(right + left) / (right - left);
		float ty = -(top + bottom) / (top - bottom);
		float tz = -(far + near) / (far - near);

		val[M00] = x_orth;
		val[M10] = 0;
		val[M20] = 0;
		val[M30] = 0;
		val[M01] = 0;
		val[M11] = y_orth;
		val[M21] = 0;
		val[M31] = 0;
		val[M02] = 0;
		val[M12] = 0;
		val[M22] = z_orth;
		val[M32] = 0;
		val[M03] = tx;
		val[M13] = ty;
		val[M23] = tz;
		val[M33] = 1;

		return this;
	}

	public Transform4 setTranslation(Location3 vector) {
		val[M03] = vector.x;
		val[M13] = vector.y;
		val[M23] = vector.z;
		return this;
	}

	public Transform4 setTranslation(float x, float y, float z) {
		val[M03] = x;
		val[M13] = y;
		val[M23] = z;
		return this;
	}

	public Transform4 setToTranslation(Location3 vector) {
		idt();
		val[M03] = vector.x;
		val[M13] = vector.y;
		val[M23] = vector.z;
		return this;
	}

	public Transform4 setToTranslation(float x, float y, float z) {
		idt();
		val[M03] = x;
		val[M13] = y;
		val[M23] = z;
		return this;
	}

	public Transform4 setToTranslationAndScaling(Location3 translation,
			Location3 scaling) {
		idt();
		val[M03] = translation.x;
		val[M13] = translation.y;
		val[M23] = translation.z;
		val[M00] = scaling.x;
		val[M11] = scaling.y;
		val[M22] = scaling.z;
		return this;
	}

	public Transform4 setToTranslationAndScaling(float translationX,
			float translationY, float translationZ, float scalingX,
			float scalingY, float scalingZ) {
		idt();
		val[M03] = translationX;
		val[M13] = translationY;
		val[M23] = translationZ;
		val[M00] = scalingX;
		val[M11] = scalingY;
		val[M22] = scalingZ;
		return this;
	}

	static Quaternion quat = new Quaternion();
	static Quaternion quat2 = new Quaternion();

	public Transform4 setToRotation(Location3 axis, float degrees) {
		if (degrees == 0) {
			idt();
			return this;
		}
		return set(quat.set(axis, degrees));
	}

	public Transform4 setToRotationRad(Location3 axis, float radians) {
		if (radians == 0) {
			idt();
			return this;
		}
		return set(quat.setFromAxisRad(axis, radians));
	}

	public Transform4 setToRotation(float axisX, float axisY, float axisZ,
			float degrees) {
		if (degrees == 0) {
			idt();
			return this;
		}
		return set(quat.setFromAxis(axisX, axisY, axisZ, degrees));
	}

	public Transform4 setToRotationRad(float axisX, float axisY, float axisZ,
			float radians) {
		if (radians == 0) {
			idt();
			return this;
		}
		return set(quat.setFromAxisRad(axisX, axisY, axisZ, radians));
	}

	public Transform4 setToRotation(final Location3 v1, final Location3 v2) {
		return set(quat.setFromCross(v1, v2));
	}

	public Transform4 setToRotation(final float x1, final float y1,
			final float z1, final float x2, final float y2, final float z2) {
		return set(quat.setFromCross(x1, y1, z1, x2, y2, z2));
	}

	public Transform4 setFromEulerAngles(float yaw, float pitch, float roll) {
		quat.setEulerAngles(yaw, pitch, roll);
		return set(quat);
	}

	public Transform4 setToScaling(Location3 vector) {
		idt();
		val[M00] = vector.x;
		val[M11] = vector.y;
		val[M22] = vector.z;
		return this;
	}

	public Transform4 setToScaling(float x, float y, float z) {
		idt();
		val[M00] = x;
		val[M11] = y;
		val[M22] = z;
		return this;
	}

	static final Location3 l_vez = new Location3();
	static final Location3 l_vex = new Location3();
	static final Location3 l_vey = new Location3();

	public Transform4 setToLookAt(Location3 direction, Location3 up) {
		l_vez.set(direction).nor();
		l_vex.set(direction).nor();
		l_vex.crs(up).nor();
		l_vey.set(l_vex).crs(l_vez).nor();
		idt();
		val[M00] = l_vex.x;
		val[M01] = l_vex.y;
		val[M02] = l_vex.z;
		val[M10] = l_vey.x;
		val[M11] = l_vey.y;
		val[M12] = l_vey.z;
		val[M20] = -l_vez.x;
		val[M21] = -l_vez.y;
		val[M22] = -l_vez.z;

		return this;
	}

	static final Location3 tmpVec = new Location3();
	static final Transform4 tmpMat = new Transform4();

	public Transform4 setToLookAt(Location3 position, Location3 target, Location3 up) {
		tmpVec.set(target).sub(position);
		setToLookAt(tmpVec, up);
		this.mul(tmpMat.setToTranslation(-position.x, -position.y, -position.z));

		return this;
	}

	static final Location3 right = new Location3();
	static final Location3 tmpForward = new Location3();
	static final Location3 tmpUp = new Location3();

	public Transform4 setToWorld(Location3 position, Location3 forward, Location3 up) {
		tmpForward.set(forward).nor();
		right.set(tmpForward).crs(up).nor();
		tmpUp.set(right).crs(tmpForward).nor();

		this.set(right, tmpUp, tmpForward.scl(-1), position);
		return this;
	}

	public String toString() {
		return "[" + val[M00] + "|" + val[M01] + "|" + val[M02] + "|"
				+ val[M03] + "]\n" + "[" + val[M10] + "|" + val[M11] + "|"
				+ val[M12] + "|" + val[M13] + "]\n" + "[" + val[M20] + "|"
				+ val[M21] + "|" + val[M22] + "|" + val[M23] + "]\n" + "["
				+ val[M30] + "|" + val[M31] + "|" + val[M32] + "|" + val[M33]
				+ "]\n";
	}

	public Transform4 lerp(Transform4 matrix, float alpha) {
		for (int i = 0; i < 16; i++)
			this.val[i] = this.val[i] * (1 - alpha) + matrix.val[i] * alpha;
		return this;
	}

	public Transform4 avg(Transform4 other, float w) {

		getScale(tmpVec);
		other.getScale(tmpForward);

		getRotation(quat);
		other.getRotation(quat2);

		getTranslation(tmpUp);
		other.getTranslation(right);

		setToScaling(tmpVec.scl(w).add(tmpForward.scl(1 - w)));

		rotate(quat.slerp(quat2, 1 - w));

		setTranslation(tmpUp.scl(w).add(right.scl(1 - w)));

		return this;
	}

	public Transform4 avg(Transform4[] t) {
		final float w = 1.0f / t.length;

		tmpVec.set(t[0].getScale(tmpUp).scl(w));

		quat.set(t[0].getRotation(quat2).exp(w));

		tmpForward.set(t[0].getTranslation(tmpUp).scl(w));

		for (int i = 1; i < t.length; i++) {

			tmpVec.add(t[i].getScale(tmpUp).scl(w));

			quat.mul(t[i].getRotation(quat2).exp(w));

			tmpForward.add(t[i].getTranslation(tmpUp).scl(w));
		}
		quat.nor();

		setToScaling(tmpVec);
		rotate(quat);
		setTranslation(tmpForward);

		return this;
	}

	public Transform4 avg(Transform4[] t, float[] w) {

		tmpVec.set(t[0].getScale(tmpUp).scl(w[0]));

		quat.set(t[0].getRotation(quat2).exp(w[0]));

		tmpForward.set(t[0].getTranslation(tmpUp).scl(w[0]));

		for (int i = 1; i < t.length; i++) {

			tmpVec.add(t[i].getScale(tmpUp).scl(w[i]));

			quat.mul(t[i].getRotation(quat2).exp(w[i]));

			tmpForward.add(t[i].getTranslation(tmpUp).scl(w[i]));
		}
		quat.nor();

		setToScaling(tmpVec);
		rotate(quat);
		setTranslation(tmpForward);

		return this;
	}

	public Transform4 set(Transform3 mat) {
		val[0] = mat.val[0];
		val[1] = mat.val[1];
		val[2] = mat.val[2];
		val[3] = 0;
		val[4] = mat.val[3];
		val[5] = mat.val[4];
		val[6] = mat.val[5];
		val[7] = 0;
		val[8] = 0;
		val[9] = 0;
		val[10] = 1;
		val[11] = 0;
		val[12] = mat.val[6];
		val[13] = mat.val[7];
		val[14] = 0;
		val[15] = mat.val[8];
		return this;
	}

	public Transform4 setAsAffine(Transform4 mat) {
		val[M00] = mat.val[M00];
		val[M10] = mat.val[M10];
		val[M01] = mat.val[M01];
		val[M11] = mat.val[M11];
		val[M03] = mat.val[M03];
		val[M13] = mat.val[M13];
		return this;
	}

	public Transform4 scl(Location3 scale) {
		val[M00] *= scale.x;
		val[M11] *= scale.y;
		val[M22] *= scale.z;
		return this;
	}

	public Transform4 scl(float x, float y, float z) {
		val[M00] *= x;
		val[M11] *= y;
		val[M22] *= z;
		return this;
	}

	public Transform4 scl(float scale) {
		val[M00] *= scale;
		val[M11] *= scale;
		val[M22] *= scale;
		return this;
	}

	public Location3 getTranslation(Location3 position) {
		position.x = val[M03];
		position.y = val[M13];
		position.z = val[M23];
		return position;
	}

	public Quaternion getRotation(Quaternion rotation, boolean normalizeAxes) {
		return rotation.setFromMatrix(normalizeAxes, this);
	}

	public Quaternion getRotation(Quaternion rotation) {
		return rotation.setFromMatrix(this);
	}

	public float getScaleXSquared() {
		return val[Transform4.M00] * val[Transform4.M00] + val[Transform4.M01]
				* val[Transform4.M01] + val[Transform4.M02] * val[Transform4.M02];
	}

	public float getScaleYSquared() {
		return val[Transform4.M10] * val[Transform4.M10] + val[Transform4.M11]
				* val[Transform4.M11] + val[Transform4.M12] * val[Transform4.M12];
	}

	public float getScaleZSquared() {
		return val[Transform4.M20] * val[Transform4.M20] + val[Transform4.M21]
				* val[Transform4.M21] + val[Transform4.M22] * val[Transform4.M22];
	}

	public float getScaleX() {
		return (MathUtils.isZero(val[Transform4.M01]) && MathUtils
				.isZero(val[Transform4.M02])) ? Math.abs(val[Transform4.M00])
				: (float) Math.sqrt(getScaleXSquared());
	}

	public float getScaleY() {
		return (MathUtils.isZero(val[Transform4.M10]) && MathUtils
				.isZero(val[Transform4.M12])) ? Math.abs(val[Transform4.M11])
				: (float) Math.sqrt(getScaleYSquared());
	}

	public float getScaleZ() {
		return (MathUtils.isZero(val[Transform4.M20]) && MathUtils
				.isZero(val[Transform4.M21])) ? Math.abs(val[Transform4.M22])
				: (float) Math.sqrt(getScaleZSquared());
	}

	public Location3 getScale(Location3 scale) {
		return scale.set(getScaleX(), getScaleY(), getScaleZ());
	}

	public Transform4 toNormalMatrix() {
		val[M03] = 0;
		val[M13] = 0;
		val[M23] = 0;
		return inv().tra();
	}

	public Transform4 translate(Location3 translation) {
		return translate(translation.x, translation.y, translation.z);
	}
	
	public Transform4 translate(float x, float y, float z) {
		tmp[M00] = 1;
		tmp[M01] = 0;
		tmp[M02] = 0;
		tmp[M03] = x;
		tmp[M10] = 0;
		tmp[M11] = 1;
		tmp[M12] = 0;
		tmp[M13] = y;
		tmp[M20] = 0;
		tmp[M21] = 0;
		tmp[M22] = 1;
		tmp[M23] = z;
		tmp[M30] = 0;
		tmp[M31] = 0;
		tmp[M32] = 0;
		tmp[M33] = 1;

		NativeSupport.mul(val, tmp);
		return this;
	}

	public Transform4 rotate(Location3 axis, float degrees) {
		if (degrees == 0)
			return this;
		quat.set(axis, degrees);
		return rotate(quat);
	}

	public Transform4 rotateRad(Location3 axis, float radians) {
		if (radians == 0)
			return this;
		quat.setFromAxisRad(axis, radians);
		return rotate(quat);
	}

	public Transform4 rotate(float axisX, float axisY, float axisZ, float degrees) {
		if (degrees == 0)
			return this;
		quat.setFromAxis(axisX, axisY, axisZ, degrees);
		return rotate(quat);
	}

	public Transform4 rotateRad(float axisX, float axisY, float axisZ,
			float radians) {
		if (radians == 0)
			return this;
		quat.setFromAxisRad(axisX, axisY, axisZ, radians);
		return rotate(quat);
	}
	public Transform4 rotate(Quaternion rotation) {
		rotation.toMatrix(tmp);
		NativeSupport.mul(val, tmp);
		return this;
	}

	public Transform4 rotate(final Location3 v1, final Location3 v2) {
		return rotate(quat.setFromCross(v1, v2));
	}

	public Transform4 scale(float scaleX, float scaleY, float scaleZ) {
		tmp[M00] = scaleX;
		tmp[M01] = 0;
		tmp[M02] = 0;
		tmp[M03] = 0;
		tmp[M10] = 0;
		tmp[M11] = scaleY;
		tmp[M12] = 0;
		tmp[M13] = 0;
		tmp[M20] = 0;
		tmp[M21] = 0;
		tmp[M22] = scaleZ;
		tmp[M23] = 0;
		tmp[M30] = 0;
		tmp[M31] = 0;
		tmp[M32] = 0;
		tmp[M33] = 1;

		NativeSupport.mul(val, tmp);
		return this;
	}

	public void extract4x3Matrix(float[] dst) {
		dst[0] = val[M00];
		dst[1] = val[M10];
		dst[2] = val[M20];
		dst[3] = val[M01];
		dst[4] = val[M11];
		dst[5] = val[M21];
		dst[6] = val[M02];
		dst[7] = val[M12];
		dst[8] = val[M22];
		dst[9] = val[M03];
		dst[10] = val[M13];
		dst[11] = val[M23];
	}
}
