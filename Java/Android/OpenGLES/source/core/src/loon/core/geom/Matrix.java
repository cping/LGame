package loon.core.geom;

import java.util.Collection;

import loon.utils.MathUtils;



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
public final class Matrix {

	float[] matrixs;

	public Matrix() {
		this.idt();
	}

	public Matrix(Matrix m) {
		matrixs = new float[9];
		System.arraycopy(m.matrixs, 0, matrixs, 0, 9);
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

	@Override
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

	@Override
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

	@Override
	public Matrix clone() {
		return new Matrix(this.matrixs);
	}

	public float[] getValues() {
		return matrixs;
	}
}
