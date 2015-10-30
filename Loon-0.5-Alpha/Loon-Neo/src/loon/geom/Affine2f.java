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

import loon.LTrans;
import loon.utils.MathUtils;

/**
 * 以对象存储，而非数组的方式实现一个3x2(标准矩阵应为3x3)的2D仿射矩阵类，
 * 也就是保留了线的“直线性”和“平行性”，但缺少了长宽高的3D矩阵延展能力。
 * 所以，此类仅适合2D应用中使用.
 * 
 * 对应的3x3矩阵关系如下所示:
 * 
 * <pre>
 * {@code
 * [ m00, m10, tx ]
 * [ m01, m11, ty ]
 * [   0,   0,  1 ]
 * }
 * </pre>
 */
public class Affine2f extends AbstractTransform implements LTrans {

	protected Affine2f(Transform other) {
		this(other.scaleX(), other.scaleY(), other.rotation(), other.tx(),
				other.ty());
	}
	
	public static Affine2f transform(Affine2f tx, int transform, float width,
			float height) {
		switch (transform) {
		case TRANS_ROT90: {
			tx.translate(height, 0);
			tx.rotate(ANGLE_90);
			break;
		}
		case TRANS_ROT180: {
			tx.translate(width, height);
			tx.rotate(MathUtils.PI);
			break;
		}
		case TRANS_ROT270: {
			tx.translate(0, width);
			tx.rotate(ANGLE_270);
			break;
		}
		case TRANS_MIRROR: {
			tx.translate(width, 0);
			tx.scale(-1, 1);
			break;
		}
		case TRANS_MIRROR_ROT90: {
			tx.translate(height, 0);
			tx.rotate(ANGLE_90);
			tx.translate(width, 0);
			tx.scale(-1, 1);
			break;
		}
		case TRANS_MIRROR_ROT180: {
			tx.translate(width, 0);
			tx.scale(-1, 1);
			tx.translate(width, height);
			tx.rotate(MathUtils.PI);
			break;
		}
		case TRANS_MIRROR_ROT270: {
			tx.rotate(ANGLE_270);
			tx.scale(-1, 1);
			break;
		}
		}
		return tx;
	}

	public Affine2f combined(Matrix4 mat) {
		float[] m = mat.val;

		float m00 = m[Matrix4.M00] * this.m00 + m[Matrix4.M01] * this.m10;
		float m01 = m[Matrix4.M00] * this.m11 + m[Matrix4.M11] * this.m10;
		float tx = m[Matrix4.M00] * this.m01 + m[Matrix4.M01] * this.m11;
		float m10 = m[Matrix4.M00] * this.tx + m[Matrix4.M01] * this.ty
				+ m[Matrix4.M02];
		float m11 = m[Matrix4.M10] * this.tx + m[Matrix4.M11] * this.ty
				+ m[Matrix4.M12];

		m[Matrix4.M00] = m00;
		m[Matrix4.M01] = m01;
		m[Matrix4.M03] = tx;
		m[Matrix4.M10] = m10;
		m[Matrix4.M11] = m11;
		m[Matrix4.M13] = ty;

		return this;

	}

	public static final int GENERALITY = 4;

	public float m00, m01, m10, m11;

	public float tx, ty;

	public Affine2f() {
		this(1, 0, 0, 1, 0, 0);
	}

	public Affine2f idt() {
		m00 = 1;
		m01 = 0;
		tx = 0;
		m10 = 0;
		m11 = 1;
		ty = 0;
		return this;
	}

	public boolean equals(Object o) {
		if (null == o) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (o instanceof Affine2f) {
			Affine2f a2f = (Affine2f) o;
			if (a2f.m00 == m00 && a2f.m01 == m01 && a2f.tx == tx
					&& a2f.ty == ty && a2f.m10 == m10 && a2f.m11 == m11) {
				return true;
			}
		}
		return false;
	}

	public Affine2f set(Matrix3 matrix) {
		float[] other = matrix.val;
		m00 = other[Matrix3.M00];
		m01 = other[Matrix3.M01];
		tx = other[Matrix3.M02];
		m10 = other[Matrix3.M10];
		m11 = other[Matrix3.M11];
		ty = other[Matrix3.M12];
		return this;
	}

	public Affine2f set(Matrix4 matrix) {
		float[] other = matrix.val;
		m00 = other[Matrix4.M00];
		m01 = other[Matrix4.M01];
		tx = other[Matrix4.M03];
		m10 = other[Matrix4.M10];
		m11 = other[Matrix4.M11];
		ty = other[Matrix4.M13];
		return this;
	}

	public Affine2f(float scale, float angle, float tx, float ty) {
		this(scale, scale, angle, tx, ty);
	}

	public Affine2f(float scaleX, float scaleY, float angle, float tx, float ty) {
		float sina = MathUtils.sin(angle), cosa = MathUtils.cos(angle);
		this.m00 = cosa * scaleX;
		this.m01 = sina * scaleY;
		this.m10 = -sina * scaleX;
		this.m11 = cosa * scaleY;
		this.tx = tx;
		this.ty = ty;
	}

	public Affine2f(float m00, float m01, float m10, float m11, float tx,
			float ty) {
		this.m00 = m00;
		this.m01 = m01;
		this.m10 = m10;
		this.m11 = m11;
		this.tx = tx;
		this.ty = ty;
	}

	public Affine2f set(Affine2f other) {
		return setTransform(other.m00, other.m01, other.m10, other.m11,
				other.tx, other.ty);
	}

	@Override
	public float uniformScale() {
		float cp = m00 * m11 - m01 * m10;
		return (cp < 0f) ? -MathUtils.sqrt(-cp) : MathUtils.sqrt(cp);
	}

	@Override
	public float scaleX() {
		return MathUtils.sqrt(m00 * m00 + m01 * m01);
	}

	@Override
	public float scaleY() {
		return MathUtils.sqrt(m10 * m10 + m11 * m11);
	}

	@Override
	public float rotation() {
		float n00 = m00, n10 = m10;
		float n01 = m01, n11 = m11;
		for (int ii = 0; ii < 10; ii++) {
			float o00 = n00, o10 = n10;
			float o01 = n01, o11 = n11;
			float det = o00 * o11 - o10 * o01;
			if (Math.abs(det) == 0f) {
				throw new RuntimeException(this.toString());
			}
			float hrdet = 0.5f / det;
			n00 = +o11 * hrdet + o00 * 0.5f;
			n10 = -o01 * hrdet + o10 * 0.5f;

			n01 = -o10 * hrdet + o01 * 0.5f;
			n11 = +o00 * hrdet + o11 * 0.5f;

			float d00 = n00 - o00, d10 = n10 - o10;
			float d01 = n01 - o01, d11 = n11 - o11;
			if (d00 * d00 + d10 * d10 + d01 * d01 + d11 * d11 < MathUtils.EPSILON) {
				break;
			}
		}
		return MathUtils.atan2(n01, n00);
	}

	@Override
	public float tx() {
		return this.tx;
	}

	@Override
	public float ty() {
		return this.ty;
	}

	@Override
	public void get(float[] matrix) {
		matrix[0] = m00;
		matrix[1] = m01;
		matrix[2] = m10;
		matrix[3] = m11;
		matrix[4] = tx;
		matrix[5] = ty;
	}

	@Override
	public Affine2f setUniformScale(float scale) {
		return (Affine2f) setScale(scale, scale);
	}

	@Override
	public Affine2f setScaleX(float scaleX) {
		// 计算新的X轴缩放
		float mult = scaleX / scaleX();
		m00 *= mult;
		m01 *= mult;
		return this;
	}

	@Override
	public Affine2f setScaleY(float scaleY) {
		// 计算新的Y轴缩放
		float mult = scaleY / scaleY();
		m10 *= mult;
		m11 *= mult;
		return this;
	}

	@Override
	public Affine2f setRotation(float angle) {
		// 提取比例，然后重新应用旋转和缩放在一起
		float sx = scaleX(), sy = scaleY();
		float sina = MathUtils.sin(angle), cosa = MathUtils.cos(angle);
		m00 = cosa * sx;
		m01 = sina * sx;
		m10 = -sina * sy;
		m11 = cosa * sy;
		return this;
	}

	@Override
	public Affine2f setTranslation(float tx, float ty) {
		this.tx = tx;
		this.ty = ty;
		return this;
	}

	@Override
	public Affine2f setTx(float tx) {
		this.tx = tx;
		return this;
	}

	@Override
	public Affine2f setTy(float ty) {
		this.ty = ty;
		return this;
	}

	@Override
	public Affine2f setTransform(float m00, float m01, float m10, float m11,
			float tx, float ty) {
		this.m00 = m00;
		this.m01 = m01;
		this.m10 = m10;
		this.m11 = m11;
		this.tx = tx;
		this.ty = ty;
		return this;
	}

	@Override
	public Affine2f uniformScale(float scale) {
		return scale(scale, scale);
	}

	@Override
	public Affine2f scale(float scaleX, float scaleY) {
		m00 *= scaleX;
		m01 *= scaleX;
		m10 *= scaleY;
		m11 *= scaleY;
		return this;
	}

	@Override
	public Affine2f scaleX(float scaleX) {
		return Transforms.multiply(this, scaleX, 0, 0, 1, 0, 0, this);
	}

	@Override
	public Affine2f scaleY(float scaleY) {
		return Transforms.multiply(this, 1, 0, 0, scaleY, 0, 0, this);
	}

	@Override
	public Affine2f rotate(float angle) {
		float sina = MathUtils.sin(angle), cosa = MathUtils.cos(angle);
		return Transforms.multiply(this, cosa, sina, -sina, cosa, 0, 0, this);
	}

	@Override
	public Affine2f translate(float tx, float ty) {
		this.tx += m00 * tx + m10 * ty;
		this.ty += m11 * ty + m01 * tx;
		return this;
	}

	@Override
	public Affine2f translateX(float tx) {
		return Transforms.multiply(this, 1, 0, 0, 1, tx, 0, this);
	}

	@Override
	public Affine2f translateY(float ty) {
		return Transforms.multiply(this, 1, 0, 0, 1, 0, ty, this);
	}

	@Override
	public Affine2f shear(float sx, float sy) {
		return Transforms.multiply(this, 1, sy, sx, 1, 0, 0, this);
	}

	@Override
	public Affine2f shearX(float sx) {
		return Transforms.multiply(this, 1, 0, sx, 1, 0, 0, this);
	}

	@Override
	public Affine2f shearY(float sy) {
		return Transforms.multiply(this, 1, sy, 0, 1, 0, 0, this);
	}

	@Override
	public Affine2f invert() {
		// 计算行列式，并临时存储数值
		float det = m00 * m11 - m10 * m01;
		if (Math.abs(det) == 0f) {
			// 行列式为零时，矩阵将不可逆，无法还原所以报错
			throw new RuntimeException(this.toString());
		}
		float rdet = 1f / det;
		return new Affine2f(+m11 * rdet, -m10 * rdet, -m01 * rdet, +m00 * rdet,
				(m10 * ty - m11 * tx) * rdet, (m01 * tx - m00 * ty) * rdet);
	}

	@Override
	public Transform concatenate(Transform other) {
		if (generality() < other.generality()) {
			return other.preConcatenate(this);
		}
		if (other instanceof Affine2f) {
			return Transforms.multiply(this, (Affine2f) other, new Affine2f());
		} else {
			Affine2f oaff = new Affine2f(other);
			return Transforms.multiply(this, oaff, oaff);
		}
	}

	@Override
	public Transform preConcatenate(Transform other) {
		if (generality() < other.generality()) {
			return other.concatenate(this);
		}
		if (other instanceof Affine2f) {
			return Transforms.multiply((Affine2f) other, this, new Affine2f());
		} else {
			Affine2f oaff = new Affine2f(other);
			return Transforms.multiply(oaff, this, oaff);
		}
	}

	@Override
	public Transform lerp(Transform other, float t) {
		if (generality() < other.generality()) {
			return other.lerp(this, -t);
		}

		Affine2f ot = (other instanceof Affine2f) ? (Affine2f) other
				: new Affine2f(other);
		return new Affine2f(m00 + t * (ot.m00 - m00), m01 + t * (ot.m01 - m01),
				m10 + t * (ot.m10 - m10), m11 + t * (ot.m11 - m11), tx + t
						* (ot.tx - tx), ty + t * (ot.ty - ty));
	}

	@Override
	public void transform(Vector2f[] src, int srcOff, Vector2f[] dst,
			int dstOff, int count) {
		for (int ii = 0; ii < count; ii++) {
			transform(src[srcOff++], dst[dstOff++]);
		}
	}

	@Override
	public void transform(float[] src, int srcOff, float[] dst, int dstOff,
			int count) {
		for (int ii = 0; ii < count; ii++) {
			float x = src[srcOff++], y = src[srcOff++];
			dst[dstOff++] = m00 * x + m10 * y + tx;
			dst[dstOff++] = m01 * x + m11 * y + ty;
		}
	}

	@Override
	public Vector2f transformPoint(Vector2f v, Vector2f into) {
		float x = v.x(), y = v.y();
		return into.set(m00 * x + m10 * y + tx, m01 * x + m11 * y + ty);
	}

	@Override
	public Vector2f transform(Vector2f v, Vector2f into) {
		float x = v.x(), y = v.y();
		return into.set(m00 * x + m10 * y, m01 * x + m11 * y);
	}

	@Override
	public Vector2f inverseTransform(Vector2f v, Vector2f into) {
		float x = v.x(), y = v.y();
		float det = m00 * m11 - m01 * m10;
		if (Math.abs(det) == 0f) {
			// 行列式为零时，矩阵将不可逆，无法还原所以报错
			throw new RuntimeException(this.toString());
		}
		float rdet = 1 / det;
		return into.set((x * m11 - y * m10) * rdet, (y * m00 - x * m01) * rdet);
	}

	@Override
	public Affine2f cpy() {
		return new Affine2f(m00, m01, m10, m11, tx, ty);
	}

	@Override
	public int generality() {
		return GENERALITY;
	}

	@Override
	public String toString() {
		return "affine [" + MathUtils.toString(m00) + " "
				+ MathUtils.toString(m01) + " " + MathUtils.toString(m10) + " "
				+ MathUtils.toString(m11) + " " + translation() + "]";
	}

}
