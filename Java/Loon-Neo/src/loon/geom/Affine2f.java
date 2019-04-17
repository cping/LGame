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

import loon.LSystem;
import loon.LTrans;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.StringKeyValue;

/**
 * 以对象存储，而非数组的方式实现一个3x2(标准矩阵应为3x3)的2D仿射矩阵类，
 * 也就是保留了线的“直线性”和“平行性”，但缺少了长宽高的3D矩阵延展能力。 所以，此类仅适合2D应用中使用.
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
public class Affine2f implements LTrans, XY {

	public final static Affine2f multiply(Affine2f a, Affine2f b, Affine2f into) {
		return multiply(a.m00, a.m01, a.m10, a.m11, a.tx, a.ty, b.m00, b.m01, b.m10, b.m11, b.tx, b.ty, into);
	}

	public final static Affine2f multiply(Affine2f a, float m00, float m01, float m10, float m11, float tx, float ty,
			Affine2f into) {
		return multiply(a.m00, a.m01, a.m10, a.m11, a.tx, a.ty, m00, m01, m10, m11, tx, ty, into);
	}

	public final static Affine2f multiply(float m00, float m01, float m10, float m11, float tx, float ty, Affine2f b,
			Affine2f into) {
		return multiply(m00, m01, m10, m11, tx, ty, b.m00, b.m01, b.m10, b.m11, b.tx, b.ty, into);
	}

	public final static Affine2f multiply(float am00, float am01, float am10, float am11, float atx, float aty,
			float bm00, float bm01, float bm10, float bm11, float btx, float bty, Affine2f into) {
		into.setTransform(am00 * bm00 + am10 * bm01, am01 * bm00 + am11 * bm01, am00 * bm10 + am10 * bm11,
				am01 * bm10 + am11 * bm11, am00 * btx + am10 * bty + atx, am01 * btx + am11 * bty + aty);
		return into;
	}

	private static Matrix4 projectionMatrix = null;

	protected Affine2f(Affine2f other) {
		this(other.scaleX(), other.scaleY(), other.rotation(), other.tx(), other.ty());
	}

	public static Affine2f transform(Affine2f tx, float x, float y, int transform) {
		switch (transform) {
		case TRANS_ROT90: {
			tx.translate(x, y);
			tx.rotate(ANGLE_90);
			tx.translate(-x, -y);
			break;
		}
		case TRANS_ROT180: {
			tx.translate(x, y);
			tx.rotate(MathUtils.PI);
			tx.translate(-x, -y);
			break;
		}
		case TRANS_ROT270: {
			tx.translate(x, y);
			tx.rotate(ANGLE_270);
			tx.translate(-x, -y);
			break;
		}
		case TRANS_MIRROR: {
			tx.translate(x, y);
			tx.scale(-1, 1);
			tx.translate(-x, -y);
			break;
		}
		case TRANS_MIRROR_ROT90: {
			tx.translate(x, y);
			tx.rotate(ANGLE_90);
			tx.translate(-x, -y);
			tx.scale(-1, 1);
			break;
		}
		case TRANS_MIRROR_ROT180: {
			tx.translate(x, y);
			tx.scale(-1, 1);
			tx.translate(-x, -y);
			tx.translate(x, y);
			tx.rotate(MathUtils.PI);
			tx.translate(-x, -y);
			break;
		}
		case TRANS_MIRROR_ROT270: {
			tx.translate(x, y);
			tx.rotate(ANGLE_270);
			tx.translate(-x, -y);
			tx.scale(-1, 1);
			break;
		}
		}
		return tx;
	}

	public static Affine2f transform(Affine2f tx, float x, float y, int transform, float width, float height) {
		switch (transform) {
		case TRANS_ROT90: {
			float w = x + width / 2;
			float h = y + height / 2;
			tx.translate(w, h);
			tx.rotate(ANGLE_90);
			tx.translate(-w, -h);
			break;
		}
		case TRANS_ROT180: {
			float w = x + width / 2;
			float h = y + height / 2;
			tx.translate(w, h);
			tx.rotate(MathUtils.PI);
			tx.translate(-w, -h);
			break;
		}
		case TRANS_ROT270: {
			float w = x + width / 2;
			float h = y + height / 2;
			tx.translate(w, h);
			tx.rotate(ANGLE_270);
			tx.translate(-w, -h);
			break;
		}
		case TRANS_MIRROR: {
			float w = x + width / 2;
			float h = y + height / 2;
			tx.translate(w, h);
			tx.scale(-1, 1);
			tx.translate(-w, -h);
			break;
		}
		case TRANS_MIRROR_ROT90: {
			float w = x + width / 2;
			float h = y + height / 2;
			tx.translate(w, h);
			tx.rotate(ANGLE_90);
			tx.translate(-w, -h);
			tx.scale(-1, 1);
			break;
		}
		case TRANS_MIRROR_ROT180: {
			float w = x + width / 2;
			float h = y + height / 2;
			tx.translate(w, h);
			tx.scale(-1, 1);
			tx.translate(-w, -h);
			w = x + width / 2;
			h = y + height / 2;
			tx.translate(w, h);
			tx.rotate(MathUtils.PI);
			tx.translate(-w, -h);
			break;
		}
		case TRANS_MIRROR_ROT270: {
			float w = x + width / 2;
			float h = y + height / 2;
			tx.translate(w, h);
			tx.rotate(ANGLE_270);
			tx.translate(-w, -h);
			tx.scale(-1, 1);
			break;
		}
		}
		return tx;
	}

	public static Affine2f transformRegion(Affine2f tx, float x, float y, int transform, float width, float height) {
		switch (transform) {
		case TRANS_ROT90: {
			float w = height;
			float h = y;
			tx.translate(w, h);
			tx.rotate(ANGLE_90);
			tx.translate(-w, -h);
			break;
		}
		case TRANS_ROT180: {
			float w = x + width;
			float h = y + height;
			tx.translate(w, h);
			tx.rotate(MathUtils.PI);
			tx.translate(-w, -h);
			break;
		}
		case TRANS_ROT270: {
			float w = x;
			float h = y + width;
			tx.translate(w, h);
			tx.rotate(ANGLE_270);
			tx.translate(-w, -h);
			break;
		}
		case TRANS_MIRROR: {
			float w = x + width;
			float h = y;
			tx.translate(w, h);
			tx.scale(-1, 1);
			tx.translate(-w, -h);
			break;
		}
		case TRANS_MIRROR_ROT90: {
			float w = x + height;
			float h = y;
			tx.translate(w, h);
			tx.rotate(ANGLE_90);
			tx.translate(-w, -h);
			tx.scale(-1, 1);
			break;
		}
		case TRANS_MIRROR_ROT180: {
			float w = x + width;
			float h = y;
			tx.translate(w, h);
			tx.scale(-1, 1);
			tx.translate(-w, -h);
			w = x + width;
			h = y + height;
			tx.translate(w, h);
			tx.rotate(MathUtils.PI);
			tx.translate(-w, -h);
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
		float m10 = m[Matrix4.M00] * this.tx + m[Matrix4.M01] * this.ty + m[Matrix4.M02];
		float m11 = m[Matrix4.M10] * this.tx + m[Matrix4.M11] * this.ty + m[Matrix4.M12];

		m[Matrix4.M00] = m00;
		m[Matrix4.M01] = m01;
		m[Matrix4.M03] = tx;
		m[Matrix4.M10] = m10;
		m[Matrix4.M11] = m11;
		m[Matrix4.M13] = ty;

		return this;

	}

	public Affine2f combined4x4(float[] vals) {
		float[] m = vals;

		float m00 = m[Matrix4.M00] * this.m00 + m[Matrix4.M01] * this.m10;
		float m01 = m[Matrix4.M00] * this.m11 + m[Matrix4.M11] * this.m10;
		float tx = m[Matrix4.M00] * this.m01 + m[Matrix4.M01] * this.m11;
		float m10 = m[Matrix4.M00] * this.tx + m[Matrix4.M01] * this.ty + m[Matrix4.M02];
		float m11 = m[Matrix4.M10] * this.tx + m[Matrix4.M11] * this.ty + m[Matrix4.M12];

		m[Matrix4.M00] = m00;
		m[Matrix4.M01] = m01;
		m[Matrix4.M03] = tx;
		m[Matrix4.M10] = m10;
		m[Matrix4.M11] = m11;
		m[Matrix4.M13] = ty;

		return this;

	}

	public static final int GENERALITY = 4;
	/* x scale */
	public float m00 = 1.0f;
	/* y skew */
	public float m01 = 0.0f;
	/* x skew */
	public float m10 = 0.0f;
	/* y scale */
	public float m11 = 1.0f;
	/* x translation */
	public float tx = 0.0f;
	/* y translation */
	public float ty = 0.0f;

	public Affine2f() {
		this(1, 0, 0, 1, 0, 0);
	}

	public Affine2f idt() {
		this.m00 = 1;
		this.m01 = 0;
		this.tx = 0;
		this.m10 = 0;
		this.m11 = 1;
		this.ty = 0;
		return this;
	}

	public final void reset() {
		this.idt();
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
			if (a2f.m00 == m00 && a2f.m01 == m01 && a2f.tx == tx && a2f.ty == ty && a2f.m10 == m10 && a2f.m11 == m11) {
				return true;
			}
		}
		return false;
	}

	public boolean equals(Affine2f a2f) {
		if (a2f == null) {
			return false;
		}
		return a2f.m00 == m00 && a2f.m01 == m01 && a2f.tx == tx && a2f.ty == ty && a2f.m10 == m10 && a2f.m11 == m11;
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

	public Affine2f setValue3x3(float[] vals) {
		m00 = vals[Matrix3.M00];
		m01 = vals[Matrix3.M01];
		tx = vals[Matrix3.M02];
		m10 = vals[Matrix3.M10];
		m11 = vals[Matrix3.M11];
		ty = vals[Matrix3.M12];
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

	public Affine2f setValue4x4(float[] vals) {
		m00 = vals[Matrix4.M00];
		m01 = vals[Matrix4.M01];
		tx = vals[Matrix4.M03];
		m10 = vals[Matrix4.M10];
		m11 = vals[Matrix4.M11];
		ty = vals[Matrix4.M13];
		return this;
	}

	public final void setThis(final Affine2f aff) {
		this.m00 = aff.m00;
		this.m11 = aff.m11;
		this.m01 = aff.m01;
		this.m10 = aff.m10;
		this.tx = aff.tx;
		this.ty = aff.ty;
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

	public Affine2f(float m00, float m01, float m10, float m11, float tx, float ty) {
		this.m00 = m00;
		this.m01 = m01;
		this.m10 = m10;
		this.m11 = m11;
		this.tx = tx;
		this.ty = ty;
	}

	public Affine2f set(Affine2f other) {
		return setTransform(other.m00, other.m01, other.m10, other.m11, other.tx, other.ty);
	}

	public float uniformScale() {
		float cp = m00 * m11 - m01 * m10;
		return (cp < 0f) ? -MathUtils.sqrt(-cp) : MathUtils.sqrt(cp);
	}

	public float scaleX() {
		return MathUtils.sqrt(m00 * m00 + m01 * m01);
	}

	public float scaleY() {
		return MathUtils.sqrt(m10 * m10 + m11 * m11);
	}

	public float rotation() {
		float n00 = m00, n10 = m10;
		float n01 = m01, n11 = m11;
		for (int ii = 0; ii < 10; ii++) {
			float o00 = n00, o10 = n10;
			float o01 = n01, o11 = n11;
			float det = o00 * o11 - o10 * o01;
			if (MathUtils.abs(det) == 0f) {
				throw LSystem.runThrow("Affine2f exception " + this.toString());
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

	public float getAngle() {
		return MathUtils.toRadians(rotation());
	}

	public float tx() {
		return this.tx;
	}

	public float ty() {
		return this.ty;
	}

	public void get(float[] matrix) {
		matrix[0] = m00;
		matrix[1] = m01;
		matrix[2] = m10;
		matrix[3] = m11;
		matrix[4] = tx;
		matrix[5] = ty;
	}

	public Affine2f setUniformScale(float scale) {
		return (Affine2f) setScale(scale, scale);
	}

	public Affine2f setScaleX(float scaleX) {
		// 计算新的X轴缩放
		float mult = scaleX / scaleX();
		m00 *= mult;
		m01 *= mult;
		return this;
	}

	public Affine2f setScaleY(float scaleY) {
		// 计算新的Y轴缩放
		float mult = scaleY / scaleY();
		m10 *= mult;
		m11 *= mult;
		return this;
	}

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

	public Affine2f rotate(float angle) {
		float sina = MathUtils.sin(angle), cosa = MathUtils.cos(angle);
		return multiply(this, cosa, sina, -sina, cosa, 0, 0, this);
	}

	public Affine2f rotate(float angle, float x, float y) {
		float sina = MathUtils.sin(angle), cosa = MathUtils.cos(angle);
		return multiply(this, cosa, sina, -sina, cosa, x, y, this);
	}

	public final Affine2f preRotate(final float angle) {
		final float angleRad = MathUtils.DEG_TO_RAD * angle;
		final float sin = MathUtils.sin(angleRad);
		final float cos = MathUtils.cos(angleRad);
		final float m00 = this.m00;
		final float m01 = this.m01;
		final float m10 = this.m10;
		final float m11 = this.m11;
		this.m00 = cos * m00 + sin * m10;
		this.m01 = cos * m01 + sin * m11;
		this.m10 = cos * m10 - sin * m00;
		this.m11 = cos * m11 - sin * m01;
		return this;
	}

	public final Affine2f postRotate(final float angle) {
		final float angleRad = MathUtils.DEG_TO_RAD * angle;

		final float sin = MathUtils.sin(angleRad);
		final float cos = MathUtils.cos(angleRad);

		final float m00 = this.m00;
		final float m01 = this.m01;
		final float m10 = this.m10;
		final float m11 = this.m11;
		final float tx = this.tx;
		final float ty = this.ty;

		this.m00 = m00 * cos - m01 * sin;
		this.m01 = m00 * sin + m01 * cos;
		this.m10 = m10 * cos - m11 * sin;
		this.m11 = m10 * sin + m11 * cos;
		this.tx = tx * cos - ty * sin;
		this.ty = tx * sin + ty * cos;
		return this;
	}

	public final Affine2f setToRotate(final float angle) {
		final float angleRad = MathUtils.DEG_TO_RAD * angle;

		final float sin = MathUtils.sin(angleRad);
		final float cos = MathUtils.cos(angleRad);

		this.m00 = cos;
		this.m01 = sin;
		this.m10 = -sin;
		this.m11 = cos;
		this.tx = 0.0f;
		this.ty = 0.0f;

		return this;
	}

	public Affine2f setTranslation(float tx, float ty) {
		this.tx = tx;
		this.ty = ty;
		return this;
	}

	public Affine2f setTx(float tx) {
		this.tx = tx;
		return this;
	}

	public Affine2f setTy(float ty) {
		this.ty = ty;
		return this;
	}

	public Affine2f setTo(float m00, float m01, float m10, float m11, float tx, float ty) {
		return setTransform(m00, m01, m10, m11, tx, ty);
	}

	public Affine2f setTransform(float m00, float m01, float m10, float m11, float tx, float ty) {
		this.m00 = m00;
		this.m01 = m01;
		this.m10 = m10;
		this.m11 = m11;
		this.tx = tx;
		this.ty = ty;
		return this;
	}

	public Affine2f uniformScale(float scale) {
		return scale(scale, scale);
	}

	public Affine2f scale(float scaleX, float scaleY) {
		m00 *= scaleX;
		m01 *= scaleX;
		m10 *= scaleY;
		m11 *= scaleY;
		return this;
	}

	public final Affine2f preScale(final float sx, final float sy) {
		return scale(sx, sy);
	}

	public final Affine2f postScale(final float sx, final float sy) {
		this.m00 = this.m00 * sx;
		this.m01 = this.m01 * sy;
		this.m10 = this.m10 * sx;
		this.m11 = this.m11 * sy;
		this.tx = this.tx * sx;
		this.ty = this.ty * sy;
		return this;
	}

	public final Affine2f setToScale(final float sx, final float sy) {
		this.m00 = sx;
		this.m01 = 0.0f;
		this.m10 = 0.0f;
		this.m11 = sy;
		this.tx = 0.0f;
		this.ty = 0.0f;
		return this;
	}

	public Affine2f scaleX(float scaleX) {
		return multiply(this, scaleX, 0, 0, 1, 0, 0, this);
	}

	public Affine2f scaleY(float scaleY) {
		return multiply(this, 1, 0, 0, scaleY, 0, 0, this);
	}

	public Affine2f translate(float tx, float ty) {
		this.tx += m00 * tx + m10 * ty;
		this.ty += m11 * ty + m01 * tx;
		return this;
	}

	public final Affine2f preTranslate(final float tx, final float ty) {
		return translate(tx, ty);
	}

	public final Affine2f postTranslate(final float tx, final float ty) {
		this.tx += tx;
		this.ty += ty;
		return this;
	}

	public final Affine2f setToTranslate(final float tx, final float ty) {
		this.m00 = 1.0f;
		this.m01 = 0.0f;
		this.m10 = 0.0f;
		this.m11 = 1.0f;
		this.tx = tx;
		this.ty = ty;
		return this;
	}

	public Affine2f translateX(float tx) {
		return multiply(this, 1, 0, 0, 1, tx, 0, this);
	}

	public Affine2f translateY(float ty) {
		return multiply(this, 1, 0, 0, 1, 0, ty, this);
	}

	public Affine2f shear(float sx, float sy) {
		return multiply(this, 1, sy, sx, 1, 0, 0, this);
	}

	public Affine2f shearX(float sx) {
		return multiply(this, 1, 0, sx, 1, 0, 0, this);
	}

	public Affine2f shearY(float sy) {
		return multiply(this, 1, sy, 0, 1, 0, 0, this);
	}

	public final Affine2f preShear(final float sx, final float sy) {
		final float tanX = MathUtils.tan(-MathUtils.DEG_TO_RAD * sx);
		final float tanY = MathUtils.tan(-MathUtils.DEG_TO_RAD * sy);

		final float m00 = this.m00;
		final float m01 = this.m01;
		final float m10 = this.m10;
		final float m11 = this.m11;
		final float tx = this.tx;
		final float ty = this.ty;

		this.m00 = m00 + tanY * m10;
		this.m01 = m01 + tanY * m11;
		this.m10 = tanX * m00 + m10;
		this.m11 = tanX * m01 + m11;
		this.tx = tx;
		this.ty = ty;
		return this;
	}

	public final void postShear(final float sx, final float sy) {
		final float tanX = MathUtils.tan(-MathUtils.DEG_TO_RAD * sx);
		final float tanY = MathUtils.tan(-MathUtils.DEG_TO_RAD * sy);

		final float m00 = this.m00;
		final float m01 = this.m01;
		final float m10 = this.m10;
		final float m11 = this.m11;
		final float tx = this.tx;
		final float ty = this.ty;

		this.m00 = m00 + m01 * tanX;
		this.m01 = m00 * tanY + m01;
		this.m10 = m10 + m11 * tanX;
		this.m11 = m10 * tanY + m11;
		this.tx = tx + ty * tanX;
		this.ty = tx * tanY + ty;
	}

	public final Affine2f setToShear(final float sx, final float sy) {
		this.m00 = 1.0f;
		this.m01 = MathUtils.tan(-MathUtils.DEG_TO_RAD * sy);
		this.m10 = MathUtils.tan(-MathUtils.DEG_TO_RAD * sx);
		this.m11 = 1.0f;
		this.tx = 0.0f;
		this.ty = 0.0f;

		return this;
	}

	public Affine2f invert() {
		// 计算行列式，并临时存储数值
		float det = m00 * m11 - m10 * m01;
		if (MathUtils.abs(det) == 0f) {
			// 行列式为零时，矩阵将不可逆，无法还原所以报错
			throw LSystem.runThrow(this.toString());
		}
		float rdet = 1f / det;
		return new Affine2f(+m11 * rdet, -m10 * rdet, -m01 * rdet, +m00 * rdet, (m10 * ty - m11 * tx) * rdet,
				(m01 * tx - m00 * ty) * rdet);
	}

	public Affine2f concatenate(Affine2f other) {
		if (generality() < other.generality()) {
			return other.preConcatenate(this);
		}
		if (other instanceof Affine2f) {
			return multiply(this, (Affine2f) other, new Affine2f());
		} else {
			Affine2f oaff = new Affine2f(other);
			return multiply(this, oaff, oaff);
		}
	}

	public Affine2f preConcatenate(Affine2f other) {
		if (generality() < other.generality()) {
			return other.concatenate(this);
		}
		if (other instanceof Affine2f) {
			return multiply((Affine2f) other, this, new Affine2f());
		} else {
			Affine2f oaff = new Affine2f(other);
			return multiply(oaff, this, oaff);
		}
	}

	public final Affine2f postConcatenate(final Affine2f t) {
		return postConcatenate(t.m00, t.m01, t.m10, t.m11, t.tx, t.ty);
	}

	public Affine2f postConcatenate(final float ma, final float mb, final float mc, final float md, final float mx,
			final float my) {
		final float m00 = this.m00;
		final float m01 = this.m01;
		final float m10 = this.m10;
		final float m11 = this.m11;
		final float tx = this.tx;
		final float ty = this.ty;

		this.m00 = m00 * ma + m01 * mc;
		this.m01 = m00 * mb + m01 * md;
		this.m10 = m10 * ma + m11 * mc;
		this.m11 = m10 * mb + m11 * md;
		this.tx = tx * ma + ty * mc + mx;
		this.ty = tx * mb + ty * md + my;
		return this;
	}

	public Affine2f lerp(Affine2f other, float t) {
		if (generality() < other.generality()) {
			return other.lerp(this, -t);
		}

		Affine2f ot = (other instanceof Affine2f) ? (Affine2f) other : new Affine2f(other);
		return new Affine2f(m00 + t * (ot.m00 - m00), m01 + t * (ot.m01 - m01), m10 + t * (ot.m10 - m10),
				m11 + t * (ot.m11 - m11), tx + t * (ot.tx - tx), ty + t * (ot.ty - ty));
	}

	public void transform(Vector2f[] src, int srcOff, Vector2f[] dst, int dstOff, int count) {
		for (int ii = 0; ii < count; ii++) {
			transform(src[srcOff++], dst[dstOff++]);
		}
	}

	public void transform(float[] src, int srcOff, float[] dst, int dstOff, int count) {
		for (int ii = 0; ii < count; ii++) {
			float x = src[srcOff++], y = src[srcOff++];
			dst[dstOff++] = m00 * x + m10 * y + tx;
			dst[dstOff++] = m01 * x + m11 * y + ty;
		}
	}

	public void transform(final float[] vertices) {
		int count = vertices.length >> 1;
		int i = 0;
		int j = 0;
		while (--count >= 0) {
			final float x = vertices[i++];
			final float y = vertices[i++];
			vertices[j++] = x * this.m00 + y * this.m10 + this.tx;
			vertices[j++] = x * this.m01 + y * this.m11 + this.ty;
		}
	}

	public PointI transformPoint(int pointX, int pointY, PointI resultPoint) {
		int x = (int) (this.m00 * pointX + this.m01 * pointY + this.tx);
		int y = (int) (this.m10 * pointX + this.m11 * pointY + this.ty);
		if (resultPoint != null) {
			resultPoint.set(x, y);
			return resultPoint;
		}
		return new PointI(x, y);
	}

	public PointF transformPoint(float pointX, float pointY, PointF resultPoint) {
		float x = this.m00 * pointX + this.m01 * pointY + this.tx;
		float y = this.m10 * pointX + this.m11 * pointY + this.ty;
		if (resultPoint != null) {
			resultPoint.set(x, y);
			return resultPoint;
		}
		return new PointF(x, y);
	}

	public Vector2f transformPoint(float pointX, float pointY, Vector2f resultPoint) {
		float x = this.m00 * pointX + this.m01 * pointY + this.tx;
		float y = this.m10 * pointX + this.m11 * pointY + this.ty;
		if (resultPoint != null) {
			resultPoint.set(x, y);
			return resultPoint;
		}
		return new Vector2f(x, y);
	}

	public Vector2f transformPoint(Vector2f v, Vector2f into) {
		float x = v.x(), y = v.y();
		return into.set(m00 * x + m10 * y + tx, m01 * x + m11 * y + ty);
	}

	public Vector2f transform(Vector2f v, Vector2f into) {
		float x = v.x(), y = v.y();
		return into.set(m00 * x + m10 * y, m01 * x + m11 * y);
	}

	public Vector2f inverseTransform(Vector2f v, Vector2f into) {
		float x = v.x(), y = v.y();
		float det = m00 * m11 - m01 * m10;
		if (MathUtils.abs(det) == 0f) {
			// 行列式为零时，矩阵将不可逆，无法还原所以报错
			throw LSystem.runThrow("Affine2f exception " + this.toString());
		}
		float rdet = 1 / det;
		return into.set((x * m11 - y * m10) * rdet, (y * m00 - x * m01) * rdet);
	}

	public Matrix4 toViewMatrix4() {
		Dimension dim = LSystem.viewSize;
		if (projectionMatrix == null) {
			projectionMatrix = new Matrix4();
		}
		projectionMatrix.setToOrtho2D(0, 0, dim.width * LSystem.getScaleWidth(), dim.height * LSystem.getScaleHeight());
		projectionMatrix.thisCombine(this);
		return projectionMatrix;
	}

	public Affine2f cpy() {
		return new Affine2f(m00, m01, m10, m11, tx, ty);
	}

	public int generality() {
		return GENERALITY;
	}

	public Object tag;

	public Vector2f scale() {
		return new Vector2f(scaleX(), scaleY());
	}

	public Vector2f translation() {
		return new Vector2f(tx(), ty());
	}

	public Affine2f setScale(float scaleX, float scaleY) {
		setScaleX(scaleX);
		setScaleY(scaleY);
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 17;
		result = prime * result + NumberUtils.floatToIntBits(m00);
		result = prime * result + NumberUtils.floatToIntBits(m11);
		result = prime * result + NumberUtils.floatToIntBits(m01);
		result = prime * result + NumberUtils.floatToIntBits(m10);
		result = prime * result + NumberUtils.floatToIntBits(tx);
		result = prime * result + NumberUtils.floatToIntBits(ty);
		return result;
	}

	@Override
	public float getX() {
		return tx();
	}

	@Override
	public float getY() {
		return ty();
	}

	/** 显示结果上补足了不存在的长高宽坐标，充当完整3x3矩阵…… **/
	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("Affine");
		builder.newLine().pushBracket().addValue(MathUtils.toString((m00))).comma().addValue(MathUtils.toString(m10))
				.comma().addValue(MathUtils.toString(tx)).popBracket().newLine().pushBracket()
				.addValue(MathUtils.toString(m01)).comma().addValue(MathUtils.toString(m11)).comma()
				.addValue(MathUtils.toString(ty)).popBracket().newLine().addValue("[0.0,0.0,1.0]").newLine();
		return builder.toString();
	}

}
