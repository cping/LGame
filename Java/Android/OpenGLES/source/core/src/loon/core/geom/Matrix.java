
/**
 * 
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
package loon.core.geom;

import java.util.Collection;
import loon.utils.MathUtils;

public final class Matrix {

	float[] matrixs;

	public Matrix() {
		this.idt();
	}

	public Matrix(Matrix m) {
		matrixs = new float[9];
		System.arraycopy(m.matrixs, 0, matrixs, 0, 9);
	}

	public Matrix(float mat[], int offset) {
		matrixs = new float[9];
		for (int i = 0; i < 9; i++) {
			matrixs[i] = mat[i + offset];
		}
	}

	public Matrix(Matrix t1, Matrix t2) {
		this(t1);
		concatenate(t2);
	}

	public Matrix(float[] matrixs) {
		if (matrixs.length != 9) {
			throw new RuntimeException("matrixs.length != 9");
		}
		this.matrixs = new float[] { matrixs[0], matrixs[1], matrixs[2],
				matrixs[3], matrixs[4], matrixs[5], matrixs[6], matrixs[7],
				matrixs[8] };
	}

	public void set(float x1, float y1, float x2, float y2) {
		set(x1, y1, 1, x2, y2, 1);
	}

	public Matrix(float a1, float a2, float a3, float b1, float b2, float b3) {
		set(a1, a2, a3, b1, b2, b3);
	}

	public void set(float a1, float a2, float a3, float b1, float b2, float b3) {
		set(a1, a2, a3, b1, b2, b3, 0, 0, 1);
	}

	public void set(float a1, float a2, float a3, float b1, float b2, float b3,
			float c1, float c2, float c3) {
		matrixs = new float[] { a1, a2, a3, b1, b2, b3, c1, c2, c3 };
	}

	public void transform(float[] source, int sourceOffset,
			float[] destination, int destOffset, int numberOfPoints) {

		float result[] = source == destination ? new float[numberOfPoints * 2]
				: destination;

		for (int i = 0; i < numberOfPoints * 2; i += 2) {
			for (int j = 0; j < 6; j += 3) {
				result[i + (j / 3)] = source[i + sourceOffset] * matrixs[j]
						+ source[i + sourceOffset + 1] * matrixs[j + 1] + 1
						* matrixs[j + 2];
			}
		}

		if (source == destination) {
			for (int i = 0; i < numberOfPoints * 2; i += 2) {
				destination[i + destOffset] = result[i];
				destination[i + destOffset + 1] = result[i + 1];
			}
		}
	}

	public Matrix concatenate(Matrix m) {
		float[] mp = new float[9];
		float n00 = matrixs[0] * m.matrixs[0] + matrixs[1] * m.matrixs[3];
		float n01 = matrixs[0] * m.matrixs[1] + matrixs[1] * m.matrixs[4];
		float n02 = matrixs[0] * m.matrixs[2] + matrixs[1] * m.matrixs[5]
				+ matrixs[2];
		float n10 = matrixs[3] * m.matrixs[0] + matrixs[4] * m.matrixs[3];
		float n11 = matrixs[3] * m.matrixs[1] + matrixs[4] * m.matrixs[4];
		float n12 = matrixs[3] * m.matrixs[2] + matrixs[4] * m.matrixs[5]
				+ matrixs[5];
		mp[0] = n00;
		mp[1] = n01;
		mp[2] = n02;
		mp[3] = n10;
		mp[4] = n11;
		mp[5] = n12;

		matrixs = mp;
		return this;
	}

	public static Matrix createRotateTransform(float angle) {
		return new Matrix(MathUtils.cos(angle), -MathUtils.sin(angle), 0,
				MathUtils.sin(angle), MathUtils.cos(angle), 0);
	}

	public static Matrix createRotateTransform(float angle, float x, float y) {
		Matrix temp = Matrix.createRotateTransform(angle);
		float sinAngle = temp.matrixs[3];
		float oneMinusCosAngle = 1.0f - temp.matrixs[4];
		temp.matrixs[2] = x * oneMinusCosAngle + y * sinAngle;
		temp.matrixs[5] = y * oneMinusCosAngle - x * sinAngle;
		return temp;
	}

	public static Matrix createTranslateTransform(float xOffset, float yOffset) {
		return new Matrix(1, 0, xOffset, 0, 1, yOffset);
	}

	public static Matrix createScaleTransform(float scalex, float scaley) {
		return new Matrix(scalex, 0, 0, 0, scaley, 0);
	}

	public float get(int i) {
		return matrixs[i];
	}

	public float get(int x, int y) {
		try {
			return matrixs[x * 3 + y];
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid indices into matrix !");
		}
	}

	public void set(int x, int y, float v) {
		try {
			this.matrixs[x * 3 + y] = v;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid indices into matrix !");
		}
	}

	public Matrix set(Matrix m) {
		matrixs[0] = m.matrixs[0];
		matrixs[1] = m.matrixs[1];
		matrixs[2] = m.matrixs[2];
		matrixs[3] = m.matrixs[3];
		matrixs[4] = m.matrixs[4];
		matrixs[5] = m.matrixs[5];
		matrixs[6] = m.matrixs[6];
		matrixs[7] = m.matrixs[7];
		matrixs[8] = m.matrixs[8];
		return this;
	}

	public Matrix from(float[] source, boolean rowMajor) {
		Matrix m = new Matrix();
		if (rowMajor) {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					m.set(i, j, source[i * 3 + j]);
				}
			}
		} else {
			for (int j = 0; j < 3; j++) {
				for (int i = 0; i < 3; i++) {
					m.set(i, j, source[j * 3 + i]);
				}
			}
		}
		return this;
	}

	public float getTranslationX() {
		return this.matrixs[6];
	}

	public float getTranslationY() {
		return this.matrixs[7];
	}

	public void translation(float x, float y) {
		this.matrixs[0] = 1;
		this.matrixs[1] = 0;
		this.matrixs[2] = 0;
		this.matrixs[3] = 0;
		this.matrixs[4] = 1;
		this.matrixs[5] = 0;
		this.matrixs[6] = x;
		this.matrixs[7] = y;
		this.matrixs[8] = 1;
	}

	public void rotation(float angle) {
		angle = MathUtils.DEG_TO_RAD * angle;
		float cos = MathUtils.cos(angle);
		float sin = MathUtils.sin(angle);
		this.matrixs[0] = cos;
		this.matrixs[1] = sin;
		this.matrixs[2] = 0;
		this.matrixs[3] = -sin;
		this.matrixs[4] = cos;
		this.matrixs[5] = 0;
		this.matrixs[6] = 0;
		this.matrixs[7] = 0;
		this.matrixs[8] = 1;
	}

	private float[] result = new float[16];

	public float[] get() {
		result[0] = matrixs[0];
		result[1] = matrixs[1];
		result[2] = matrixs[2];
		result[3] = 0;
		result[4] = matrixs[3];
		result[5] = matrixs[4];
		result[6] = matrixs[5];
		result[7] = 0;
		result[8] = 0;
		result[9] = 0;
		result[10] = 1;
		result[11] = 0;
		result[12] = matrixs[6];
		result[13] = matrixs[7];
		result[14] = 0;
		result[15] = matrixs[8];
		return result;
	}

	public void rotationX(float angleX) {
		angleX = MathUtils.PI / 180 * angleX;
		set(1f, 0f, 0f, 0f, MathUtils.cos(angleX), -MathUtils.sin(angleX), 0f,
				MathUtils.sin(angleX), MathUtils.cos(angleX));
	}

	public void rotationY(float angleY) {
		angleY = MathUtils.PI / 180 * angleY;
		set(MathUtils.cos(angleY), 0f, MathUtils.sin(angleY), 0f, 1f, 0f,
				-MathUtils.sin(angleY), 0f, MathUtils.cos(angleY));
	}

	public void rotationZ(float angleZ) {
		angleZ = MathUtils.PI / 180 * angleZ;
		set(MathUtils.cos(angleZ), -MathUtils.sin(angleZ), 0f,
				MathUtils.sin(angleZ), MathUtils.cos(angleZ), 0f, 0f, 0f, 1f);
	}

	public void scale(float sx, float sy) {
		this.matrixs[0] = sx;
		this.matrixs[1] = 0;
		this.matrixs[2] = 0;
		this.matrixs[3] = 0;
		this.matrixs[4] = sy;
		this.matrixs[5] = 0;
		this.matrixs[6] = 0;
		this.matrixs[7] = 0;
		this.matrixs[8] = 1;
	}

	public void idt() {
		if (matrixs == null) {
			matrixs = new float[] { 1, 0, 0, 0, 1, 0, 0, 0, 1 };
		} else {
			this.matrixs[0] = 1;
			this.matrixs[1] = 0;
			this.matrixs[2] = 0;
			this.matrixs[3] = 0;
			this.matrixs[4] = 1;
			this.matrixs[5] = 0;
			this.matrixs[6] = 0;
			this.matrixs[7] = 0;
			this.matrixs[8] = 1;
		}
	}

	public boolean isIdt() {
		return (matrixs[0] == 1 && matrixs[1] == 0 && matrixs[2] == 0)
				&& (matrixs[3] == 0 && matrixs[4] == 1 && matrixs[5] == 0)
				&& (matrixs[6] == 0 && matrixs[7] == 0 && matrixs[8] == 1);
	}

	public float det() {
		return matrixs[0] * matrixs[4] * matrixs[8] + matrixs[3] * matrixs[7]
				* matrixs[2] + matrixs[6] * matrixs[1] * matrixs[5]
				- matrixs[0] * matrixs[7] * matrixs[5] - matrixs[3]
				* matrixs[1] * matrixs[8] - matrixs[6] * matrixs[4]
				* matrixs[2];
	}

	private final static float detd(float a, float b, float c, float d) {
		return (a * d) - (b * c);
	}

	public void adj() {

		float a11 = this.matrixs[0];
		float a12 = this.matrixs[1];
		float a13 = this.matrixs[2];

		float a21 = this.matrixs[3];
		float a22 = this.matrixs[4];
		float a23 = this.matrixs[5];

		float a31 = this.matrixs[6];
		float a32 = this.matrixs[7];
		float a33 = this.matrixs[8];

		this.matrixs[0] = detd(a22, a23, a32, a33);
		this.matrixs[1] = detd(a13, a12, a33, a32);
		this.matrixs[2] = detd(a12, a13, a22, a23);

		this.matrixs[3] = detd(a23, a21, a33, a31);
		this.matrixs[4] = detd(a11, a13, a31, a33);
		this.matrixs[5] = detd(a13, a11, a23, a21);

		this.matrixs[6] = detd(a21, a22, a31, a32);
		this.matrixs[7] = detd(a12, a11, a32, a31);
		this.matrixs[8] = detd(a11, a12, a21, a22);
	}

	public void add(Matrix m) {
		float a1 = this.matrixs[0];
		float a2 = this.matrixs[1];
		float a3 = this.matrixs[2];

		float b1 = this.matrixs[3];
		float b2 = this.matrixs[4];
		float b3 = this.matrixs[5];

		float c1 = this.matrixs[6];
		float c2 = this.matrixs[7];
		float c3 = this.matrixs[8];

		a1 += m.matrixs[0];
		a2 += m.matrixs[1];
		a3 += m.matrixs[2];

		b1 += m.matrixs[3];
		b2 += m.matrixs[4];
		b3 += m.matrixs[5];

		c1 += m.matrixs[6];
		c2 += m.matrixs[7];
		c3 += m.matrixs[8];

		this.matrixs[0] = a1;
		this.matrixs[1] = a2;
		this.matrixs[2] = a3;
		this.matrixs[3] = b1;
		this.matrixs[4] = b2;
		this.matrixs[5] = b3;
		this.matrixs[6] = c1;
		this.matrixs[7] = c2;
		this.matrixs[8] = c3;
	}

	public Matrix addEqual(Matrix m) {
		Matrix newMatrix = new Matrix(this.matrixs);
		newMatrix.add(m);
		return newMatrix;
	}

	public void mul(float c) {
		float a1 = this.matrixs[0];
		float a2 = this.matrixs[1];
		float a3 = this.matrixs[2];

		float b1 = this.matrixs[3];
		float b2 = this.matrixs[4];
		float b3 = this.matrixs[5];

		float c1 = this.matrixs[6];
		float c2 = this.matrixs[7];
		float c3 = this.matrixs[8];

		this.matrixs[0] = a1 * c;
		this.matrixs[1] = a2 * c;
		this.matrixs[2] = a3 * c;
		this.matrixs[3] = b1 * c;
		this.matrixs[4] = b2 * c;
		this.matrixs[5] = b3 * c;
		this.matrixs[6] = c1 * c;
		this.matrixs[7] = c2 * c;
		this.matrixs[8] = c3 * c;
	}

	public void mul(Matrix m) {
		float a1 = matrixs[0] * m.matrixs[0] + matrixs[3] * m.matrixs[1]
				+ matrixs[6] * m.matrixs[2];
		float a2 = matrixs[0] * m.matrixs[3] + matrixs[3] * m.matrixs[4]
				+ matrixs[6] * m.matrixs[5];
		float a3 = matrixs[0] * m.matrixs[6] + matrixs[3] * m.matrixs[7]
				+ matrixs[6] * m.matrixs[8];

		float b1 = matrixs[1] * m.matrixs[0] + matrixs[4] * m.matrixs[1]
				+ matrixs[7] * m.matrixs[2];
		float b2 = matrixs[1] * m.matrixs[3] + matrixs[4] * m.matrixs[4]
				+ matrixs[7] * m.matrixs[5];
		float b3 = matrixs[1] * m.matrixs[6] + matrixs[4] * m.matrixs[7]
				+ matrixs[7] * m.matrixs[8];

		float c1 = matrixs[2] * m.matrixs[0] + matrixs[5] * m.matrixs[1]
				+ matrixs[8] * m.matrixs[2];
		float c2 = matrixs[2] * m.matrixs[3] + matrixs[5] * m.matrixs[4]
				+ matrixs[8] * m.matrixs[5];
		float c3 = matrixs[2] * m.matrixs[6] + matrixs[5] * m.matrixs[7]
				+ matrixs[8] * m.matrixs[8];

		this.matrixs[0] = a1;
		this.matrixs[1] = a2;
		this.matrixs[2] = a3;

		this.matrixs[3] = b1;
		this.matrixs[4] = b2;
		this.matrixs[5] = b3;

		this.matrixs[6] = c1;
		this.matrixs[7] = c2;
		this.matrixs[8] = c3;

	}

	public Matrix mulEqual(Matrix m) {
		if (m == null) {
			m = new Matrix();
		}
		Matrix result = new Matrix(this.matrixs);
		result.mul(m);
		return result;
	}

	public Matrix invert(Matrix m) {
		Matrix result = m;
		if (result == null) {
			result = new Matrix();
		}

		final float det = det();
		if (Math.abs(det) <= MathUtils.EPSILON) {
			throw new ArithmeticException("This matrix cannot be inverted !");
		}

		final float temp00 = matrixs[4] * matrixs[8] - matrixs[5] * matrixs[7];
		final float temp01 = matrixs[2] * matrixs[7] - matrixs[1] * matrixs[8];
		final float temp02 = matrixs[1] * matrixs[5] - matrixs[2] * matrixs[4];
		final float temp10 = matrixs[5] * matrixs[6] - matrixs[3] * matrixs[8];
		final float temp11 = matrixs[0] * matrixs[8] - matrixs[2] * matrixs[6];
		final float temp12 = matrixs[2] * matrixs[3] - matrixs[0] * matrixs[5];
		final float temp20 = matrixs[3] * matrixs[7] - matrixs[4] * matrixs[6];
		final float temp21 = matrixs[1] * matrixs[6] - matrixs[0] * matrixs[7];
		final float temp22 = matrixs[0] * matrixs[4] - matrixs[1] * matrixs[3];
		result.set(temp00, temp01, temp02, temp10, temp11, temp12, temp20,
				temp21, temp22);
		result.mul(1.0f / det);
		return result;
	}

	public boolean isFloatValid() {

		boolean valid = true;

		valid &= !Float.isNaN(matrixs[0]);
		valid &= !Float.isNaN(matrixs[1]);
		valid &= !Float.isNaN(matrixs[2]);

		valid &= !Float.isNaN(matrixs[3]);
		valid &= !Float.isNaN(matrixs[4]);
		valid &= !Float.isNaN(matrixs[5]);

		valid &= !Float.isNaN(matrixs[6]);
		valid &= !Float.isNaN(matrixs[7]);
		valid &= !Float.isNaN(matrixs[8]);

		return valid;
	}

	public final static Matrix avg(Collection<Matrix> set) {
		Matrix average = new Matrix();
		average.set(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f);
		float hist = 0;
		for (Matrix matrix3d : set) {
			if (matrix3d.isFloatValid()) {
				average.add(matrix3d);
				hist++;
			}
		}
		average.mul(1f / hist);
		return average;
	}

	public void copy(Matrix m) {
		if (m == null) {
			idt();
		} else {
			set(m);
		}
	}

	public boolean equals(Object o) {
		if (!(o instanceof Matrix) || o == null) {
			return false;
		}

		if (this == o) {
			return true;
		}

		Matrix comp = (Matrix) o;

		if (Float.compare(matrixs[0], comp.matrixs[0]) != 0) {
			return false;
		}
		if (Float.compare(matrixs[1], comp.matrixs[1]) != 0) {
			return false;
		}
		if (Float.compare(matrixs[2], comp.matrixs[2]) != 0) {
			return false;
		}

		if (Float.compare(matrixs[3], comp.matrixs[3]) != 0) {
			return false;
		}
		if (Float.compare(matrixs[4], comp.matrixs[4]) != 0) {
			return false;
		}
		if (Float.compare(matrixs[5], comp.matrixs[5]) != 0) {
			return false;
		}

		if (Float.compare(matrixs[6], comp.matrixs[6]) != 0) {
			return false;
		}
		if (Float.compare(matrixs[7], comp.matrixs[7]) != 0) {
			return false;
		}
		if (Float.compare(matrixs[8], comp.matrixs[8]) != 0) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		int result = 17;
		for (int j = 0; j < 9; j++) {
			final long val = Float.floatToIntBits(matrixs[j]);
			result += 31 * result + (int) (val ^ (val >>> 32));
		}
		return result;
	}

	public Vector2f transform(Vector2f pt) {
		float[] in = new float[] { pt.x, pt.y };
		float[] out = new float[2];

		transform(in, 0, out, 0, 1);

		return new Vector2f(out[0], out[1]);
	}

	public Matrix clone() {
		return new Matrix(this.matrixs);
	}

	public float[] getValues() {
		return matrixs;
	}

	public void set(int i, float value) {
		matrixs[i] = value;
	}

	public static void add(Matrix result, Matrix m1, Matrix m2) {
		for (int i = 0; i < 9; i++) {
			result.set(i, m1.get(i) + m2.get(i));
		}
	}

	public static void sub(Matrix result, Matrix m1, Matrix m2) {
		for (int i = 0; i < 9; i++) {
			result.set(i, m1.get(i) - m2.get(i));
		}
	}

	public static void mul(Matrix result, Matrix m1, Matrix m2) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				result.set(i, j, m1.get(i, 0) * m2.get(0, j) + m1.get(i, 1)
						* m2.get(1, j) + m1.get(i, 2) * m2.get(2, j));
			}
		}
	}

	public static void mul(float result[], Matrix m, float v[]) {
		float a, b, c;
		a = m.get(0, 0) * v[0] + m.get(1, 0) * v[1] + m.get(2, 0) * v[2];
		b = m.get(0, 1) * v[0] + m.get(1, 1) * v[1] + m.get(2, 1) * v[2];
		c = m.get(0, 2) * v[0] + m.get(1, 2) * v[1] + m.get(2, 2) * v[2];
		result[0] = a;
		result[1] = b;
		result[2] = c;
	}

	public static Matrix getRotationMatrixExact(float ax, float ay, float az) {
		float cosax = MathUtils.cos(MathUtils.toRadians(ax));
		float sinax = MathUtils.sin(MathUtils.toRadians(ax));
		float cosay = MathUtils.cos(MathUtils.toRadians(ay));
		float sinay = MathUtils.sin(MathUtils.toRadians(ay));
		float cosaz = MathUtils.cos(MathUtils.toRadians(az));
		float sinaz = MathUtils.sin(MathUtils.toRadians(az));
		float tx[] = { 1, 0, 0, 0, cosax, -sinax, 0, sinax, cosax };
		float ty[] = { cosay, 0, sinay, 0, 1.f, 0.f, -sinay, 0, cosay };
		float tz[] = { cosaz, -sinaz, 0, sinaz, cosaz, 0, 0, 0, 1 };
		Matrix Rx = new Matrix(tx);
		Matrix Ry = new Matrix(ty);
		Matrix Rz = new Matrix(tz);
		Matrix result = new Matrix();
		Matrix tmpresult = new Matrix();
		Matrix.mul(tmpresult, Rx, Ry);
		Matrix.mul(result, tmpresult, Rz);
		return result;
	}

	public static float distance2d(float x1, float y1, float x2, float y2) {
		return distance(x1, y1, 0.f, x2, y2, 0.f);
	}

	public static float distance(float x1, float y1, float z1, float x2,
			float y2, float z2) {
		return MathUtils.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1)
				* (y2 - y1) + (z2 - z1) * (z2 - z1));
	}

	public static boolean isOnTriange(float x1, float y1, float x2, float y2,
			float x3, float y3, float x, float y) {
		float a;
		float b;
		boolean s;
		boolean s2;

		if (x2 - x1 != 0.f) {
			a = (y2 - y1) / (x2 - x1);
			b = y1 - a * x1;

			if (a * x3 + b > y3) {
				s = true;
			} else{
				s = false;
			}
			if (a * x + b > y) {
				s2 = true;
			} else{
				s2 = false;
			}

			if ((s != s2) && (a * x + b != y)) {
				return false;
			}

		} else {
			if (x1 > x3) {
				s = true;
			} else {
				s = false;
			}
			if (x1 > x) {
				s2 = true;
			} else {
				s2 = false;
			}
			if ((s != s2) && (x1 != x)) {
				return false;
			}
		}

		if (x3 - x2 != 0.f) {
			a = (y3 - y2) / (x3 - x2);
			b = y2 - a * x2;

			if (a * x1 + b > y1) {
				s = true;
			} else {
				s = false;
			}
			if (a * x + b > y) {
				s2 = true;
			} else {
				s2 = false;
			}
			if ((s != s2) && (a * x + b != y)) {
				return false;
			}

		} else {
			if (x2 > x1) {
				s = true;
			} else {
				s = false;
			}
			if (x2 > x) {
				s2 = true;
			} else {
				s2 = false;
			}
			if ((s != s2) && (x1 != x)) {
				return false;
			}
		}

		if (x1 - x3 != 0.f) {
			a = (y1 - y3) / (x1 - x3);
			b = y3 - a * x3;

			if (a * x2 + b > y2) {
				s = true;
			} else {
				s = false;
			}

			if (a * x + b > y) {
				s2 = true;
			} else {
				s2 = false;
			}

			if ((s != s2) && (a * x + b != y)) {
				return false;
			}

		} else {
			if (x1 > x2) {
				s = true;
			} else {
				s = false;
			}

			if (x1 > x) {
				s2 = true;
			} else {
				s2 = false;
			}

			if ((s != s2) && (x1 != x)) {
				return false;
			}
		}
		return true;

	}

	public static float[] convert33to44(float m33[], int offset) {
		float m44[] = new float[16];

		m44[0] = m33[0 + offset];
		m44[1] = m33[1 + offset];
		m44[2] = m33[2 + offset];

		m44[4] = m33[3 + offset];
		m44[5] = m33[4 + offset];
		m44[6] = m33[5 + offset];

		m44[8] = m33[6 + offset];
		m44[9] = m33[7 + offset];
		m44[10] = m33[8 + offset];

		m44[15] = 1.0f;
		return m44;
	}

	public static void setRotateEulerM(float[] rm, int rmOffset, float x,
			float y, float z) {
		x = x * 0.01745329f;
		y = y * 0.01745329f;
		z = z * 0.01745329f;
		float sx = MathUtils.sin(x);
		float sy = MathUtils.sin(y);
		float sz = MathUtils.sin(z);
		float cx = MathUtils.cos(x);
		float cy = MathUtils.cos(y);
		float cz = MathUtils.cos(z);
		float cxsy = cx * sy;
		float sxsy = sx * sy;

		rm[rmOffset + 0] = cy * cz;
		rm[rmOffset + 1] = -cy * sz;
		rm[rmOffset + 2] = sy;
		rm[rmOffset + 3] = 0.0f;

		rm[rmOffset + 4] = sxsy * cz + cx * sz;
		rm[rmOffset + 5] = -sxsy * sz + cx * cz;
		rm[rmOffset + 6] = -sx * cy;
		rm[rmOffset + 7] = 0.0f;

		rm[rmOffset + 8] = -cxsy * cz + sx * sz;
		rm[rmOffset + 9] = cxsy * sz + sx * cz;
		rm[rmOffset + 10] = cx * cy;
		rm[rmOffset + 11] = 0.0f;

		rm[rmOffset + 12] = 0.0f;
		rm[rmOffset + 13] = 0.0f;
		rm[rmOffset + 14] = 0.0f;
		rm[rmOffset + 15] = 1.0f;
	}
}
