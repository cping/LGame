/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.geom;

import java.io.Serializable;
import java.nio.FloatBuffer;

import loon.LSystem;
import loon.Support;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;

public class Matrix4 implements Serializable, XY {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4331834668907675786L;

	public final static Matrix4 TMP() {
		return new Matrix4();
	}

	public final static Matrix4 ZERO() {
		return new Matrix4();
	}

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

	private Support support;

	private void init() {
		if (support == null) {
			support = LSystem.base().support();
		}
	}

	public Matrix4() {
		val[M00] = 1f;
		val[M11] = 1f;
		val[M22] = 1f;
		val[M33] = 1f;
		init();
	}

	public Matrix4(Matrix4 matrix) {
		init();
		this.set(matrix);
	}

	public Matrix4(float[] values) {
		init();
		this.set(values);
	}

	public Matrix4(Quaternion quaternion) {
		init();
		this.set(quaternion);
	}

	public Matrix4(Vector3f position, Quaternion rotation, Vector3f scale) {
		init();
		set(position, rotation, scale);
	}

	public Matrix4 set(Matrix4 matrix) {
		return this.set(matrix.val);
	}

	public Matrix4 set(float[] values) {
		System.arraycopy(values, 0, val, 0, val.length);
		return this;
	}

	public Matrix4 set(Quaternion quaternion) {
		return set(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
	}

	public Matrix4 set(float quaternionX, float quaternionY, float quaternionZ,
			float quaternionW) {
		return set(0f, 0f, 0f, quaternionX, quaternionY, quaternionZ,
				quaternionW);
	}

	public Matrix4 set(Vector3f position, Quaternion orientation) {
		return set(position.x, position.y, position.z, orientation.x,
				orientation.y, orientation.z, orientation.w);
	}

	public Matrix4 set(float translationX, float translationY,
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

	public Matrix4 set(Vector3f position, Quaternion orientation, Vector3f scale) {
		return set(position.x, position.y, position.z, orientation.x,
				orientation.y, orientation.z, orientation.w, scale.x, scale.y,
				scale.z);
	}

	public Matrix4 set(float translationX, float translationY,
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

	public Matrix4 set(Vector3f xAxis, Vector3f yAxis, Vector3f zAxis,
			Vector3f pos) {
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

	public Matrix4 set(int x, int y, float v) {
		val[y + x * 4] = v;
		return this;
	}

	public float get(int x, int y) {
		return val[y + x * 4];
	}

	public Matrix4 cpy() {
		return new Matrix4(this);
	}

	public Matrix4 trn(Vector3f vector) {
		val[M03] += vector.x;
		val[M13] += vector.y;
		val[M23] += vector.z;
		return this;
	}

	public Matrix4 trn(float x, float y, float z) {
		val[M03] += x;
		val[M13] += y;
		val[M23] += z;
		return this;
	}

	public float[] getValues() {
		return val;
	}

	public Matrix4 mul(Matrix4 matrix) {
		support.mul(val, matrix.val);
		return this;
	}

	public Matrix4 mul(Affine2f aff) {
		Matrix4 m = new Matrix4();
		m.set(aff);
		support.mul(val, m.val);
		return this;
	}

	public Matrix4 mulLeft(Matrix4 matrix) {
		tmpMat.set(matrix);
		support.mul(tmpMat.val, this.val);
		return set(tmpMat);
	}

	public Matrix4 tra() {
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

	public Matrix4 idt() {
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

	public Matrix4 izero() {
		val[M00] = 0;
		val[M01] = 0;
		val[M02] = 0;
		val[M03] = 0;
		val[M10] = 0;
		val[M11] = 0;
		val[M12] = 0;
		val[M13] = 0;
		val[M20] = 0;
		val[M21] = 0;
		val[M22] = 0;
		val[M23] = 0;
		val[M30] = 0;
		val[M31] = 0;
		val[M32] = 0;
		val[M33] = 0;
		return this;
	}

	public Matrix4 inv() {
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
		if (l_det == 0f) {
			throw new RuntimeException("non-invertible matrix");
		}
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

	public Matrix4 setToProjection(float near, float far, float fovy,
			float aspectRatio) {

		float l_fd = (1f / MathUtils.tan(fovy * MathUtils.DEG_TO_RAD) / 2f);
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

	public Matrix4 setToOrtho2D(float x, float y, float width, float height) {
		setToOrtho(x, x + width, y + height, y, 1f, -1f);
		return this;
	}

	public Matrix4 setToOrtho2D(float x, float y, float width, float height,
			float near, float far) {
		setToOrtho(x, x + width, y + height, y, near, far);
		return this;
	}

	public Matrix4 setToOrtho(float left, float right, float bottom, float top,
			float near, float far) {

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

	public Matrix4 setTranslation(Vector3f vector) {
		val[M03] = vector.x;
		val[M13] = vector.y;
		val[M23] = vector.z;
		return this;
	}

	public Matrix4 setTranslation(float x, float y, float z) {
		val[M03] = x;
		val[M13] = y;
		val[M23] = z;
		return this;
	}

	public Matrix4 setToTranslation(Vector3f vector) {
		idt();
		val[M03] = vector.x;
		val[M13] = vector.y;
		val[M23] = vector.z;
		return this;
	}

	public Matrix4 setToTranslation(float x, float y, float z) {
		idt();
		val[M03] = x;
		val[M13] = y;
		val[M23] = z;
		return this;
	}

	public Matrix4 setToTranslationAndScaling(Vector3f translation,
			Vector3f scaling) {
		idt();
		val[M03] = translation.x;
		val[M13] = translation.y;
		val[M23] = translation.z;
		val[M00] = scaling.x;
		val[M11] = scaling.y;
		val[M22] = scaling.z;
		return this;
	}

	public Matrix4 setToTranslationAndScaling(float translationX,
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

	public Matrix4 setToRotation(Vector3f axis, float degrees) {
		if (degrees == 0) {
			idt();
			return this;
		}
		return set(quat.set(axis, degrees));
	}

	public Matrix4 setToRotationRad(Vector3f axis, float radians) {
		if (radians == 0) {
			idt();
			return this;
		}
		return set(quat.setFromAxisRad(axis, radians));
	}

	public Matrix4 setToRotation(float axisX, float axisY, float axisZ,
			float degrees) {
		if (degrees == 0) {
			idt();
			return this;
		}
		return set(quat.setFromAxis(axisX, axisY, axisZ, degrees));
	}

	public Matrix4 setToRotationRad(float axisX, float axisY, float axisZ,
			float radians) {
		if (radians == 0) {
			idt();
			return this;
		}
		return set(quat.setFromAxisRad(axisX, axisY, axisZ, radians));
	}

	public Matrix4 setToRotation(final Vector3f v1, final Vector3f v2) {
		return set(quat.setFromCross(v1, v2));
	}

	public Matrix4 setToRotation(final float x1, final float y1,
			final float z1, final float x2, final float y2, final float z2) {
		return set(quat.setFromCross(x1, y1, z1, x2, y2, z2));
	}

	public Matrix4 setFromEulerAngles(float yaw, float pitch, float roll) {
		quat.setEulerAnglesSelf(yaw, pitch, roll);
		return set(quat);
	}

	public Matrix4 setToScaling(Vector3f vector) {
		idt();
		val[M00] = vector.x;
		val[M11] = vector.y;
		val[M22] = vector.z;
		return this;
	}

	public Matrix4 setToScaling(float x, float y, float z) {
		idt();
		val[M00] = x;
		val[M11] = y;
		val[M22] = z;
		return this;
	}

	static final Vector3f l_vez = new Vector3f();
	static final Vector3f l_vex = new Vector3f();
	static final Vector3f l_vey = new Vector3f();

	public Matrix4 setToLookAt(Vector3f direction, Vector3f up) {
		l_vez.set(direction).norSelf();
		l_vex.set(direction).norSelf();
		l_vex.crsSelf(up).norSelf();
		l_vey.set(l_vex).crsSelf(l_vez).norSelf();
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

	static final Vector3f tmpVec = new Vector3f();
	static final Matrix4 tmpMat = new Matrix4();

	public Matrix4 setToLookAt(Vector3f position, Vector3f target, Vector3f up) {
		tmpVec.set(target).subtractSelf(position);
		setToLookAt(tmpVec, up);
		this.mul(tmpMat.setToTranslation(-position.x, -position.y, -position.z));

		return this;
	}

	static final Vector3f right = new Vector3f();
	static final Vector3f tmpForward = new Vector3f();
	static final Vector3f tmpUp = new Vector3f();

	public Matrix4 setToWorld(Vector3f position, Vector3f forward, Vector3f up) {
		tmpForward.set(forward).norSelf();
		right.set(tmpForward).crsSelf(up).norSelf();
		tmpUp.set(right).crsSelf(tmpForward).norSelf();

		this.set(right, tmpUp, tmpForward.scaleSelf(-1), position);
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

	public Matrix4 lerp(Matrix4 matrix, float alpha) {
		for (int i = 0; i < 16; i++)
			this.val[i] = this.val[i] * (1 - alpha) + matrix.val[i] * alpha;
		return this;
	}

	public Matrix4 avg(Matrix4 other, float w) {

		getScale(tmpVec);
		other.getScale(tmpForward);

		getRotation(quat);
		other.getRotation(quat2);

		getTranslation(tmpUp);
		other.getTranslation(right);

		setToScaling(tmpVec.scaleSelf(w).addSelf(tmpForward.scaleSelf(1 - w)));

		rotate(quat.slerpSelf(quat2, 1 - w));

		setTranslation(tmpUp.scaleSelf(w).addSelf(right.scaleSelf(1 - w)));

		return this;
	}

	public Matrix4 avg(Matrix4[] t) {
		final float w = 1.0f / t.length;

		tmpVec.set(t[0].getScale(tmpUp).scaleSelf(w));

		quat.set(t[0].getRotation(quat2).expSelf(w));

		tmpForward.set(t[0].getTranslation(tmpUp).scaleSelf(w));

		for (int i = 1; i < t.length; i++) {

			tmpVec.addSelf(t[i].getScale(tmpUp).scaleSelf(w));

			quat.mulSelf(t[i].getRotation(quat2).expSelf(w));

			tmpForward.addSelf(t[i].getTranslation(tmpUp).scaleSelf(w));
		}
		quat.norSelf();

		setToScaling(tmpVec);
		rotate(quat);
		setTranslation(tmpForward);

		return this;
	}

	public Matrix4 avg(Matrix4[] t, float[] w) {

		tmpVec.set(t[0].getScale(tmpUp).scaleSelf(w[0]));

		quat.set(t[0].getRotation(quat2).expSelf(w[0]));

		tmpForward.set(t[0].getTranslation(tmpUp).scaleSelf(w[0]));

		for (int i = 1; i < t.length; i++) {

			tmpVec.addSelf(t[i].getScale(tmpUp).scaleSelf(w[i]));

			quat.mulSelf(t[i].getRotation(quat2).expSelf(w[i]));

			tmpForward.addSelf(t[i].getTranslation(tmpUp).scaleSelf(w[i]));
		}
		quat.norSelf();

		setToScaling(tmpVec);
		rotate(quat);
		setTranslation(tmpForward);

		return this;
	}

	public Matrix4 set(Matrix3 mat) {
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

	public Matrix4 setAsAffine(Matrix4 mat) {
		val[M00] = mat.val[M00];
		val[M10] = mat.val[M10];
		val[M01] = mat.val[M01];
		val[M11] = mat.val[M11];
		val[M03] = mat.val[M03];
		val[M13] = mat.val[M13];
		return this;
	}

	public Matrix4 scaleSelf(Vector3f scale) {
		val[M00] *= scale.x;
		val[M11] *= scale.y;
		val[M22] *= scale.z;
		return this;
	}

	public Matrix4 scaleSelf(float x, float y, float z) {
		val[M00] *= x;
		val[M11] *= y;
		val[M22] *= z;
		return this;
	}

	public Matrix4 scaleSelf(float scale) {
		val[M00] *= scale;
		val[M11] *= scale;
		val[M22] *= scale;
		return this;
	}

	public Vector3f getTranslation(Vector3f position) {
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
		return val[Matrix4.M00] * val[Matrix4.M00] + val[Matrix4.M01]
				* val[Matrix4.M01] + val[Matrix4.M02] * val[Matrix4.M02];
	}

	public float getScaleYSquared() {
		return val[Matrix4.M10] * val[Matrix4.M10] + val[Matrix4.M11]
				* val[Matrix4.M11] + val[Matrix4.M12] * val[Matrix4.M12];
	}

	public float getScaleZSquared() {
		return val[Matrix4.M20] * val[Matrix4.M20] + val[Matrix4.M21]
				* val[Matrix4.M21] + val[Matrix4.M22] * val[Matrix4.M22];
	}

	public float getScaleX() {
		return (MathUtils.isZero(val[Matrix4.M01]) && MathUtils
				.isZero(val[Matrix4.M02])) ? MathUtils.abs(val[Matrix4.M00])
				: MathUtils.sqrt(getScaleXSquared());
	}

	public float getScaleY() {
		return (MathUtils.isZero(val[Matrix4.M10]) && MathUtils
				.isZero(val[Matrix4.M12])) ? MathUtils.abs(val[Matrix4.M11])
				: MathUtils.sqrt(getScaleYSquared());
	}

	public float getScaleZ() {
		return (MathUtils.isZero(val[Matrix4.M20]) && MathUtils
				.isZero(val[Matrix4.M21])) ? MathUtils.abs(val[Matrix4.M22])
				: MathUtils.sqrt(getScaleZSquared());
	}

	public Vector3f getScale(Vector3f scale) {
		return scale.set(getScaleX(), getScaleY(), getScaleZ());
	}

	public Matrix4 toNormalMatrix() {
		val[M03] = 0;
		val[M13] = 0;
		val[M23] = 0;
		return inv().tra();
	}

	public Matrix4 translate(Vector3f translation) {
		return translate(translation.x, translation.y, translation.z);
	}

	public Matrix4 translate(float x, float y, float z) {
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

		support.mul(val, tmp);
		return this;
	}

	public Matrix4 rotate(Vector3f axis, float degrees) {
		if (degrees == 0)
			return this;
		quat.set(axis, degrees);
		return rotate(quat);
	}

	public Matrix4 rotateRad(Vector3f axis, float radians) {
		if (radians == 0)
			return this;
		quat.setFromAxisRad(axis, radians);
		return rotate(quat);
	}

	public Matrix4 rotate(float axisX, float axisY, float axisZ, float degrees) {
		if (degrees == 0)
			return this;
		quat.setFromAxis(axisX, axisY, axisZ, degrees);
		return rotate(quat);
	}

	public Matrix4 rotateRad(float axisX, float axisY, float axisZ,
			float radians) {
		if (radians == 0)
			return this;
		quat.setFromAxisRad(axisX, axisY, axisZ, radians);
		return rotate(quat);
	}

	public Matrix4 rotate(Quaternion rotation) {
		rotation.toMatrix(tmp);
		support.mul(val, tmp);
		return this;
	}

	public Matrix4 rotate(final Vector3f v1, final Vector3f v2) {
		return rotate(quat.setFromCross(v1, v2));
	}

	public Matrix4 scale(float scaleX, float scaleY, float scaleZ) {
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

		support.mul(val, tmp);
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

	public Matrix4 setAsAffine(Affine2f affine) {
		val[M00] = affine.m00;
		val[M10] = affine.m10;
		val[M01] = affine.m01;
		val[M11] = affine.m11;
		val[M03] = affine.tx;
		val[M13] = affine.ty;
		return this;
	}

	public Matrix4 set(Affine2f affine) {
		val[M00] = affine.m00;
		val[M10] = affine.m10;
		val[M20] = 0;
		val[M30] = 0;
		val[M01] = affine.m01;
		val[M11] = affine.m11;
		val[M21] = 0;
		val[M31] = 0;
		val[M02] = 0;
		val[M12] = 0;
		val[M22] = 1;
		val[M32] = 0;
		val[M03] = affine.tx;
		val[M13] = affine.ty;
		val[M23] = 0;
		val[M33] = 1;
		return this;
	}

	public Matrix4 newCombine(Affine2f affine) {

		float m00 = affine.m00;
		float m10 = affine.m10;
		float m20 = 0;
		float m30 = 0;
		float m01 = affine.m01;
		float m11 = affine.m11;
		float m21 = 0;
		float m31 = 0;
		float m02 = 0;
		float m12 = 0;
		float m22 = 1;
		float m32 = 0;
		float m03 = affine.tx;
		float m13 = affine.ty;
		float m23 = 0;
		float m33 = 1;

		float nm00 = val[M00] * m00 + val[M01] * m10 + val[M02] * m20
				+ val[M03] * m30;
		float nm01 = val[M00] * m01 + val[M01] * m11 + val[M02] * m21
				+ val[M03] * m31;
		float nm02 = val[M00] * m02 + val[M01] * m12 + val[M02] * m22
				+ val[M03] * m32;
		float nm03 = val[M00] * m03 + val[M01] * m13 + val[M02] * m23
				+ val[M03] * m33;
		float nm10 = val[M10] * m00 + val[M11] * m10 + val[M12] * m20
				+ val[M13] * m30;
		float nm11 = val[M10] * m01 + val[M11] * m11 + val[M12] * m21
				+ val[M13] * m31;
		float nm12 = val[M10] * m02 + val[M11] * m12 + val[M12] * m22
				+ val[M13] * m32;
		float nm13 = val[M10] * m03 + val[M11] * m13 + val[M12] * m23
				+ val[M13] * m33;
		float nm20 = val[M20] * m00 + val[M21] * m10 + val[M22] * m20
				+ val[M23] * m30;
		float nm21 = val[M20] * m01 + val[M21] * m11 + val[M22] * m21
				+ val[M23] * m31;
		float nm22 = val[M20] * m02 + val[M21] * m12 + val[M22] * m22
				+ val[M23] * m32;
		float nm23 = val[M20] * m03 + val[M21] * m13 + val[M22] * m23
				+ val[M23] * m33;
		float nm30 = val[M30] * m00 + val[M31] * m10 + val[M32] * m20
				+ val[M33] * m30;
		float nm31 = val[M30] * m01 + val[M31] * m11 + val[M32] * m21
				+ val[M33] * m31;
		float nm32 = val[M30] * m02 + val[M31] * m12 + val[M32] * m22
				+ val[M33] * m32;
		float nm33 = val[M30] * m03 + val[M31] * m13 + val[M32] * m23
				+ val[M33] * m33;

		Matrix4 m = new Matrix4();

		m.val[M00] = nm00;
		m.val[M10] = nm10;
		m.val[M20] = nm20;
		m.val[M30] = nm30;
		m.val[M01] = nm01;
		m.val[M11] = nm11;
		m.val[M21] = nm21;
		m.val[M31] = nm31;
		m.val[M02] = nm02;
		m.val[M12] = nm12;
		m.val[M22] = nm22;
		m.val[M32] = nm32;
		m.val[M03] = nm03;
		m.val[M13] = nm13;
		m.val[M23] = nm23;
		m.val[M33] = nm33;

		return m;
	}

	public Matrix4 thisCombine(Affine2f affine) {

		final float m00 = affine.m00;
		final float m10 = affine.m10;
		final float m20 = 0;
		final float m30 = 0;
		final float m01 = affine.m01;
		final float m11 = affine.m11;
		final float m21 = 0;
		final float m31 = 0;
		final float m02 = 0;
		final float m12 = 0;
		final float m22 = 1;
		final float m32 = 0;
		final float m03 = affine.tx;
		final float m13 = affine.ty;
		final float m23 = 0;
		final float m33 = 1;

		final float nm00 = val[M00] * m00 + val[M01] * m10 + val[M02] * m20
				+ val[M03] * m30;
		final float nm01 = val[M00] * m01 + val[M01] * m11 + val[M02] * m21
				+ val[M03] * m31;
		final float nm02 = val[M00] * m02 + val[M01] * m12 + val[M02] * m22
				+ val[M03] * m32;
		final float nm03 = val[M00] * m03 + val[M01] * m13 + val[M02] * m23
				+ val[M03] * m33;
		final float nm10 = val[M10] * m00 + val[M11] * m10 + val[M12] * m20
				+ val[M13] * m30;
		final float nm11 = val[M10] * m01 + val[M11] * m11 + val[M12] * m21
				+ val[M13] * m31;
		final float nm12 = val[M10] * m02 + val[M11] * m12 + val[M12] * m22
				+ val[M13] * m32;
		final float nm13 = val[M10] * m03 + val[M11] * m13 + val[M12] * m23
				+ val[M13] * m33;
		final float nm20 = val[M20] * m00 + val[M21] * m10 + val[M22] * m20
				+ val[M23] * m30;
		final float nm21 = val[M20] * m01 + val[M21] * m11 + val[M22] * m21
				+ val[M23] * m31;
		final float nm22 = val[M20] * m02 + val[M21] * m12 + val[M22] * m22
				+ val[M23] * m32;
		final float nm23 = val[M20] * m03 + val[M21] * m13 + val[M22] * m23
				+ val[M23] * m33;
		final float nm30 = val[M30] * m00 + val[M31] * m10 + val[M32] * m20
				+ val[M33] * m30;
		final float nm31 = val[M30] * m01 + val[M31] * m11 + val[M32] * m21
				+ val[M33] * m31;
		final float nm32 = val[M30] * m02 + val[M31] * m12 + val[M32] * m22
				+ val[M33] * m32;
		final float nm33 = val[M30] * m03 + val[M31] * m13 + val[M32] * m23
				+ val[M33] * m33;

		this.val[M00] = nm00;
		this.val[M10] = nm10;
		this.val[M20] = nm20;
		this.val[M30] = nm30;
		this.val[M01] = nm01;
		this.val[M11] = nm11;
		this.val[M21] = nm21;
		this.val[M31] = nm31;
		this.val[M02] = nm02;
		this.val[M12] = nm12;
		this.val[M22] = nm22;
		this.val[M32] = nm32;
		this.val[M03] = nm03;
		this.val[M13] = nm13;
		this.val[M23] = nm23;
		this.val[M33] = nm33;

		return this;
	}

	public static Matrix4 newCombine(Matrix4 m1, Matrix4 m2) {
		float m00 = m1.val[M00] * m2.val[M00] + m1.val[M01] * m2.val[M10]
				+ m1.val[M02] * m2.val[M20] + m1.val[M03] * m2.val[M30];
		float m01 = m1.val[M00] * m2.val[M01] + m1.val[M01] * m2.val[M11]
				+ m1.val[M02] * m2.val[M21] + m1.val[M03] * m2.val[M31];
		float m02 = m1.val[M00] * m2.val[M02] + m1.val[M01] * m2.val[M12]
				+ m1.val[M02] * m2.val[M22] + m1.val[M03] * m2.val[M32];
		float m03 = m1.val[M00] * m2.val[M03] + m1.val[M01] * m2.val[M13]
				+ m1.val[M02] * m2.val[M23] + m1.val[M03] * m2.val[M33];
		float m10 = m1.val[M10] * m2.val[M00] + m1.val[M11] * m2.val[M10]
				+ m1.val[M12] * m2.val[M20] + m1.val[M13] * m2.val[M30];
		float m11 = m1.val[M10] * m2.val[M01] + m1.val[M11] * m2.val[M11]
				+ m1.val[M12] * m2.val[M21] + m1.val[M13] * m2.val[M31];
		float m12 = m1.val[M10] * m2.val[M02] + m1.val[M11] * m2.val[M12]
				+ m1.val[M12] * m2.val[M22] + m1.val[M13] * m2.val[M32];
		float m13 = m1.val[M10] * m2.val[M03] + m1.val[M11] * m2.val[M13]
				+ m1.val[M12] * m2.val[M23] + m1.val[M13] * m2.val[M33];
		float m20 = m1.val[M20] * m2.val[M00] + m1.val[M21] * m2.val[M10]
				+ m1.val[M22] * m2.val[M20] + m1.val[M23] * m2.val[M30];
		float m21 = m1.val[M20] * m2.val[M01] + m1.val[M21] * m2.val[M11]
				+ m1.val[M22] * m2.val[M21] + m1.val[M23] * m2.val[M31];
		float m22 = m1.val[M20] * m2.val[M02] + m1.val[M21] * m2.val[M12]
				+ m1.val[M22] * m2.val[M22] + m1.val[M23] * m2.val[M32];
		float m23 = m1.val[M20] * m2.val[M03] + m1.val[M21] * m2.val[M13]
				+ m1.val[M22] * m2.val[M23] + m1.val[M23] * m2.val[M33];
		float m30 = m1.val[M30] * m2.val[M00] + m1.val[M31] * m2.val[M10]
				+ m1.val[M32] * m2.val[M20] + m1.val[M33] * m2.val[M30];
		float m31 = m1.val[M30] * m2.val[M01] + m1.val[M31] * m2.val[M11]
				+ m1.val[M32] * m2.val[M21] + m1.val[M33] * m2.val[M31];
		float m32 = m1.val[M30] * m2.val[M02] + m1.val[M31] * m2.val[M12]
				+ m1.val[M32] * m2.val[M22] + m1.val[M33] * m2.val[M32];
		float m33 = m1.val[M30] * m2.val[M03] + m1.val[M31] * m2.val[M13]
				+ m1.val[M32] * m2.val[M23] + m1.val[M33] * m2.val[M33];

		Matrix4 m = new Matrix4();

		m.val[M00] = m00;
		m.val[M10] = m10;
		m.val[M20] = m20;
		m.val[M30] = m30;
		m.val[M01] = m01;
		m.val[M11] = m11;
		m.val[M21] = m21;
		m.val[M31] = m31;
		m.val[M02] = m02;
		m.val[M12] = m12;
		m.val[M22] = m22;
		m.val[M32] = m32;
		m.val[M03] = m03;
		m.val[M13] = m13;
		m.val[M23] = m23;
		m.val[M33] = m33;

		return m;
	}

	public FloatBuffer getAsFloatBuffer() {
		return LSystem.base().support().newFloatBuffer(val, 0, val.length);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Matrix3) || o == null) {
			return false;
		}
		if (this == o) {
			return true;
		}
		Matrix3 comp = (Matrix3) o;
		for (int i = 0; i < 16; i++) {
			if (NumberUtils.compare(this.val[i], comp.val[i]) != 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public float getX() {
		return val[M13];
	}

	@Override
	public float getY() {
		return val[M03];
	}

	@Override
	public int hashCode() {
		int result = 17;
		for (int j = 0; j < 16; j++) {
			final long val = NumberUtils.floatToIntBits(this.val[j]);
			result += 31 * result + (int) (val ^ (val >>> 32));
		}
		return result;
	}
}
