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

import loon.LSysException;
import loon.LSystem;
import loon.LTrans;
import loon.action.ActionBind;
import loon.utils.MathUtils;
import loon.utils.StringKeyValue;

/**
 * 2D矩阵存储用类.
 * 
 * 以对象存储，而非数组的方式实现一个3x2(标准矩阵应为3x3)的2D仿射矩阵类，
 * 也就是保留了线的“直线性”和“平行性”，但缺少了长宽高的3D矩阵延展能力。 所以，此类仅适合2D应用中使用(当然,也可以转化为3D应用,只是没有Z值).
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

	public final static Affine2f ofPos(float tx, float ty) {
		Affine2f xf = new Affine2f();
		xf.setToTranslate(tx, ty);
		return xf;
	}

	public final static Affine2f ofRotate(float angle) {
		Affine2f xf = new Affine2f();
		xf.setToRotate(angle);
		return xf;
	}

	public final static Affine2f ofRotate(float angle, float anchorx, float anchory) {
		Affine2f xf = new Affine2f();
		xf.setToRotate(angle, anchorx, anchory);
		return xf;
	}

	public final static Affine2f ofRotate(float vecx, float vecy) {
		Affine2f xf = new Affine2f();
		xf.setToRotate(vecx, vecy);
		return xf;
	}

	public final static Affine2f ofRotate(float vecx, float vecy, float anchorx, float anchory) {
		Affine2f xf = new Affine2f();
		xf.setToRotate(vecx, vecy, anchorx, anchory);
		return xf;
	}

	public final static Affine2f ofScale(float sx, float sy) {
		Affine2f xf = new Affine2f();
		xf.setToScale(sx, sy);
		return xf;
	}

	public final static Affine2f ofShear(float shx, float shy) {
		Affine2f xf = new Affine2f();
		xf.setToShear(shx, shy);
		return xf;
	}

	public final static Affine2f ofRect(BoxSize r) {
		final float cx = r.getCenterX();
		final float cy = r.getCenterY();
		final Affine2f xform = Affine2f.ofPos(cx, cy);
		xform.scale(r.getWidth() / 2, r.getHeight() / 2);
		xform.translate(-cx, -cy);
		return xform;
	}

	public final static Affine2f transform(Affine2f tx, float x, float y, int transform) {
		switch (transform) {
		case TRANS_ROT90: {
			tx.translate(x, y);
			tx.rotateDegrees(ANGLE_90);
			tx.translate(-x, -y);
			break;
		}
		case TRANS_ROT180: {
			tx.translate(x, y);
			tx.rotateDegrees(MathUtils.PI);
			tx.translate(-x, -y);
			break;
		}
		case TRANS_ROT270: {
			tx.translate(x, y);
			tx.rotateDegrees(ANGLE_270);
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
			tx.rotateDegrees(ANGLE_90);
			tx.translate(-x, -y);
			tx.scale(-1, 1);
			break;
		}
		case TRANS_MIRROR_ROT180: {
			tx.translate(x, y);
			tx.scale(-1, 1);
			tx.translate(-x, -y);
			tx.translate(x, y);
			tx.rotateDegrees(MathUtils.PI);
			tx.translate(-x, -y);
			break;
		}
		case TRANS_MIRROR_ROT270: {
			tx.translate(x, y);
			tx.rotateDegrees(ANGLE_270);
			tx.translate(-x, -y);
			tx.scale(-1, 1);
			break;
		}
		}
		return tx;
	}

	public final static Affine2f transformOrigin(Affine2f tx, float x, float y, int transform, float originX,
			float originY) {
		final float w = x + originX;
		final float h = y + originY;
		switch (transform) {
		case TRANS_ROT90: {
			tx.translate(w, h);
			tx.rotateDegrees(ANGLE_90);
			tx.translate(-w, -h);
			break;
		}
		case TRANS_ROT180: {
			tx.translate(w, h);
			tx.rotateDegrees(MathUtils.PI);
			tx.translate(-w, -h);
			break;
		}
		case TRANS_ROT270: {
			tx.translate(w, h);
			tx.rotateDegrees(ANGLE_270);
			tx.translate(-w, -h);
			break;
		}
		case TRANS_MIRROR: {
			tx.translate(w, h);
			tx.scale(-1, 1);
			tx.translate(-w, -h);
			break;
		}
		case TRANS_MIRROR_ROT90: {
			tx.translate(w, h);
			tx.rotateDegrees(ANGLE_90);
			tx.translate(-w, -h);
			tx.scale(-1, 1);
			break;
		}
		case TRANS_MIRROR_ROT180: {
			tx.translate(w, h);
			tx.scale(-1, 1);
			tx.translate(-w, -h);
			tx.translate(w, h);
			tx.rotateDegrees(MathUtils.PI);
			tx.translate(-w, -h);
			break;
		}
		case TRANS_MIRROR_ROT270: {
			tx.translate(w, h);
			tx.rotateDegrees(ANGLE_270);
			tx.translate(-w, -h);
			tx.scale(-1, 1);
			break;
		}
		}
		return tx;

	}

	public final static Affine2f transform(Affine2f tx, float x, float y, int transform, float width, float height) {
		return transformOrigin(tx, x, y, transform, width / 2, height / 2);
	}

	public final static Affine2f transformRegion(Affine2f tx, float x, float y, int transform, float width,
			float height) {
		switch (transform) {
		case TRANS_ROT90: {
			float w = height;
			float h = y;
			tx.translate(w, h);
			tx.rotateDegrees(ANGLE_90);
			tx.translate(-w, -h);
			break;
		}
		case TRANS_ROT180: {
			float w = x + width;
			float h = y + height;
			tx.translate(w, h);
			tx.rotateDegrees(MathUtils.PI);
			tx.translate(-w, -h);
			break;
		}
		case TRANS_ROT270: {
			float w = x;
			float h = y + width;
			tx.translate(w, h);
			tx.rotateDegrees(ANGLE_270);
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
			tx.rotateDegrees(ANGLE_90);
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
			tx.rotateDegrees(MathUtils.PI);
			tx.translate(-w, -h);
			break;
		}
		case TRANS_MIRROR_ROT270: {
			tx.rotateDegrees(ANGLE_270);
			tx.scale(-1, 1);
			break;
		}
		}
		return tx;
	}

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

	/* default generality */
	protected int GENERALITY = 4;
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
	/* convert Affine to Matrix3 */
	private float[] matrix3f = new float[9];
	/* one 4x4 matrix temp object */
	private Matrix4 projectionMatrix = null;

	protected Affine2f(Affine2f other) {
		this(other.scaleX(), other.scaleY(), other.rotation(), other.tx(), other.ty());
	}

	public Affine2f() {
		this(1, 0, 0, 1, 0, 0);
	}

	public Affine2f(float scale, float angle, float tx, float ty) {
		this(scale, scale, angle, tx, ty);
	}

	public Affine2f(float scaleX, float scaleY, float angle, float tx, float ty, float width, float height,
			float originX, float originY) {
		this.setTo(scaleX, scaleY, angle, tx, ty, width, height, originX, originY);
	}

	public Affine2f(float scaleX, float scaleY, float angle, float tx, float ty) {
		this.setTo(scaleX, scaleY, angle, tx, ty);
	}

	public Affine2f(float m00, float m01, float m10, float m11, float tx, float ty) {
		this.setTransform(m00, m01, m10, m11, tx, ty);
	}

	public Affine2f(ActionBind bind, float originX, float originY) {
		this.setTo(bind, originX, originY);
	}

	public Affine2f(ActionBind bind) {
		this.setTo(bind);
	}

	public Affine2f combined(Affine2f aff) {

		float a = aff.m00 * this.m00 + aff.m01 * this.m10;
		float b = aff.m00 * this.m11 + aff.m11 * this.m10;
		float tx = aff.m00 * this.m01 + aff.m01 * this.m11;
		float c = aff.m00 * this.tx + aff.m01 * this.ty + aff.tx;
		float d = aff.m10 * this.tx + aff.m11 * this.ty + aff.ty;

		this.m00 = a;
		this.m01 = b;
		this.tx = tx;
		this.m10 = c;
		this.m11 = d;
		return this;
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

	/**
	 * 还原矩阵基本数值
	 * 
	 * @return
	 */
	public Affine2f idt() {
		this.m00 = 1;
		this.m01 = 0;
		this.tx = 0;
		this.m10 = 0;
		this.m11 = 1;
		this.ty = 0;
		return this;
	}

	public float det() {
		return m00 * m11 - m01 * m10;
	}

	public boolean isIdt() {
		return (m00 == 1 && m01 == 0 && tx == 0 && m10 == 0 && m11 == 1 && ty == 0);
	}

	public boolean isTranslation() {
		return (m00 == 1 && m11 == 1 && m01 == 0 && m10 == 0);
	}

	/**
	 * 还原矩阵为默认基本数值
	 * 
	 * @return
	 */
	public final Affine2f reset() {
		return this.idt();
	}

	/**
	 * 检查当前矩阵是否为默认基本数值
	 * 
	 * @return
	 */
	public boolean checkBaseTransform() {
		return (m00 != 1 || m01 != 0 || m10 != 0 || m11 != 1 || tx != 0 || ty != 0);
	}

	@Override
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

	/**
	 * 判断指定矩阵是否与当前矩阵等值
	 * 
	 * @param a2f
	 * @return
	 */
	public boolean equals(Affine2f a2f) {
		if (a2f == null) {
			return false;
		}
		if (a2f == this) {
			return true;
		}
		return a2f.m00 == m00 && a2f.m01 == m01 && a2f.tx == tx && a2f.ty == ty && a2f.m10 == m10 && a2f.m11 == m11;
	}

	/**
	 * 设定当前矩阵参数为3x3(9元素)矩阵数值
	 * 
	 * @param matrix
	 * @return
	 */
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

	/**
	 * 设定当前矩阵参数为3x3(9元素)矩阵数值
	 * 
	 * @param vals
	 * @return
	 */
	public Affine2f setValue3x3(float[] vals) {
		m00 = vals[Matrix3.M00];
		m01 = vals[Matrix3.M01];
		tx = vals[Matrix3.M02];
		m10 = vals[Matrix3.M10];
		m11 = vals[Matrix3.M11];
		ty = vals[Matrix3.M12];
		return this;
	}

	/**
	 * 设定当前矩阵参数为4x4(16元素)矩阵数值
	 * 
	 * @param matrix
	 * @return
	 */
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

	/**
	 * 设定当前矩阵参数为4x4(16元素)矩阵数值
	 * 
	 * @param vals
	 * @return
	 */
	public Affine2f setValue4x4(float[] vals) {
		m00 = vals[Matrix4.M00];
		m01 = vals[Matrix4.M01];
		tx = vals[Matrix4.M03];
		m10 = vals[Matrix4.M10];
		m11 = vals[Matrix4.M11];
		ty = vals[Matrix4.M13];
		return this;
	}

	public final Affine2f setThis(final Affine2f aff) {
		this.m00 = aff.m00;
		this.m11 = aff.m11;
		this.m01 = aff.m01;
		this.m10 = aff.m10;
		this.tx = aff.tx;
		this.ty = aff.ty;
		return this;
	}

	public Affine2f set(Affine2f other) {
		return setTransform(other.m00, other.m01, other.m10, other.m11, other.tx, other.ty);
	}

	public float uniformScale() {
		float cp = m00 * m11 - m01 * m10;
		return (cp < 0f) ? -MathUtils.sqrt(-cp) : MathUtils.sqrt(cp);
	}

	/**
	 * 返回当前矩阵缩放的X值
	 * 
	 * @return
	 */
	public float scaleX() {
		return m01 == 0 ? m00 : MathUtils.sqrt(m00 * m00 + m01 * m01);
	}

	/**
	 * 返回当前矩阵缩放的Y值
	 * 
	 * @return
	 */
	public float scaleY() {
		return m10 == 0 ? m11 : MathUtils.sqrt(m10 * m10 + m11 * m11);
	}

	/**
	 * 返回当前矩阵倾斜的X值
	 * 
	 * @return
	 */
	public float skewX() {
		if (this.m11 < 0) {
			return MathUtils.atan2(this.m11, this.m01) + (MathUtils.PI / 2);
		} else {
			return MathUtils.atan2(this.m11, this.m01) - (MathUtils.PI / 2);
		}
	}

	/**
	 * 返回当前矩阵倾斜的Y值
	 * 
	 * @return
	 */
	public float skewY() {
		if (this.m00 < 0) {
			return MathUtils.atan2(this.m10, this.m00) - MathUtils.PI;
		} else {
			return MathUtils.atan2(this.m10, this.m00);
		}
	}

	public float rotation() {
		float n00 = m00, n10 = m10;
		float n01 = m01, n11 = m11;
		for (int ii = 0; ii < 10; ii++) {
			float o00 = n00, o10 = n10;
			float o01 = n01, o11 = n11;
			float det = o00 * o11 - o10 * o01;
			if (MathUtils.abs(det) == 0f) {
				throw new LSysException("Affine2f exception " + this.toString());
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

	public float[] getMartix3f() {
		matrix3f[0] = m00;
		matrix3f[1] = m10;
		matrix3f[2] = 0;
		matrix3f[3] = m01;
		matrix3f[4] = m11;
		matrix3f[5] = 0;
		matrix3f[6] = tx;
		matrix3f[7] = ty;
		matrix3f[8] = 1;
		return matrix3f;
	}

	public float[] get(float[] matrix) {
		if (matrix == null) {
			return getMartix3f();
		}
		final int len = matrix.length;
		if (len == 6) {
			matrix[0] = m00;
			matrix[1] = m01;
			matrix[2] = m10;
			matrix[3] = m11;
			matrix[4] = tx;
			matrix[5] = ty;
		} else if (len == 9) {
			matrix[0] = m00;
			matrix[1] = m10;
			matrix[3] = m01;
			matrix[4] = m11;
			matrix[6] = tx;
			matrix[7] = ty;
		} else if (len == 16) {
			matrix[0] = m00;
			matrix[1] = m10;
			matrix[4] = m01;
			matrix[5] = m11;
			matrix[12] = tx;
			matrix[13] = ty;
		}
		return matrix;
	}

	public Affine2f setUniformScale(float scale) {
		return setScale(scale, scale);
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

	public float rotationX() {
		return MathUtils.atan2(m01, m00);
	}

	public float rotationY() {
		return MathUtils.atan2(-m11, m10);
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

	public Affine2f skew(float x, float y) {
		float tanX = MathUtils.tan(x);
		float tanY = MathUtils.tan(y);
		float a1 = m00;
		float b1 = m01;
		m00 += tanY * m10;
		m01 += tanY * m11;
		m10 += tanX * a1;
		m11 += tanX * b1;
		return this;
	}

	public Vector2f map(Vector2f dst) {
		if (dst == null) {
			dst = new Vector2f();
		}
		dst.setX(this.m00 * dst.getX() + this.m10 * dst.getY() + this.tx);
		dst.setY(this.m01 * dst.getY() + this.m11 * dst.getY() + this.ty);
		return dst;
	}

	public float mapX(float tx, float ty) {
		return this.m00 * tx + this.m10 * ty + this.tx;
	}

	public float mapY(float tx, float ty) {
		return this.m01 * tx + this.m11 * ty + this.ty;
	}

	/**
	 * 获得矩阵转换后的X坐标
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public float getX(float x, float y) {
		return x * this.m00 + y * this.m01 + this.tx;
	}

	/**
	 * 获得矩阵转换后的Y坐标
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public float getY(float x, float y) {
		return x * this.m10 + y * this.m11 + this.ty;
	}

	public RectBox getRect(float w, float h) {
		float a = m00 * w, b = m01 * w;
		float c = m10 * h, d = m11 * h;
		float dx = tx, dy = ty;
		Number[] xw = loon.utils.HelperUtils.sortIncrement(dx, dx + a, dx + c, dx + a + c);
		Number[] yh = loon.utils.HelperUtils.sortIncrement(dy, dy + b, dy + d, dy + b + d);
		float x = xw[0].floatValue();
		float y = yh[0].floatValue();
		float width = xw[3].floatValue();
		float height = yh[3].floatValue();
		return new RectBox(x, y, width - x, height - y);
	}

	public Bound getOrientedRect(float w, float h) {
		float a = m00 * w, b = m01 * w;
		float c = m10 * h, d = m11 * h;
		float dx = tx, dy = ty;
		return new Bound(dx, dy, dx + a, dy + b, dx + c, dy + d, dx + a + c, dy + b + d);
	}

	public Affine2f rotateDegrees(float degrees) {
		float sina = MathUtils.sin(degrees), cosa = MathUtils.cos(degrees);
		return multiply(this, cosa, sina, -sina, cosa, 0, 0, this);
	}

	public Affine2f rotateDegrees(float degrees, float x, float y) {
		float sina = MathUtils.sin(degrees), cosa = MathUtils.cos(degrees);
		return multiply(this, cosa, sina, -sina, cosa, x, y, this);
	}

	public Affine2f toRotate(float angle, float x, float y) {
		return rotateDegrees(MathUtils.toRadians(angle), x, y);
	}

	public final Affine2f preRotate(final float angle) {

		final float rad = MathUtils.toRadians(angle);
		final float cos = MathUtils.cos(rad);
		final float sin = MathUtils.sin(rad);

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

		final float rad = MathUtils.toRadians(angle);
		final float cos = MathUtils.cos(rad);
		final float sin = MathUtils.sin(rad);

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

		final float rad = MathUtils.toRadians(angle);
		final float cos = MathUtils.cos(rad);
		final float sin = MathUtils.sin(rad);

		this.m00 = cos;
		this.m01 = sin;
		this.m10 = -sin;
		this.m11 = cos;
		this.tx = 0.0f;
		this.ty = 0.0f;

		return this;
	}

	public final Affine2f setToRotate(final float vecx, final float vecy) {
		float sin, cos;
		if (vecy == 0f) {
			sin = 0f;
			if (vecx < 0f) {
				cos = -1f;
			} else {
				cos = 1f;
			}
		} else if (vecx == 0f) {
			cos = 0f;
			sin = (vecy > 0f) ? 1f : -1f;
		} else {
			float len = MathUtils.sqrt(vecx * vecx + vecy * vecy);
			cos = vecx / len;
			sin = vecy / len;
		}
		this.m00 = cos;
		this.m10 = sin;
		this.m01 = -sin;
		this.m11 = cos;
		this.tx = 0.0f;
		this.ty = 0.0f;

		return this;
	}

	public Affine2f setToRotate(float vecx, float vecy, float anchorx, float anchory) {
		setToRotate(vecx, vecy);
		float sin = m10;
		float oneMinusCos = 1f - m00;
		this.tx = anchorx * oneMinusCos + anchory * sin;
		this.ty = anchory * oneMinusCos - anchorx * sin;
		return this;
	}

	public Affine2f setToRotate(float angle, float anchorx, float anchory) {
		setToRotate(angle);
		float sin = m10;
		float oneMinusCos = 1f - m00;
		this.tx = anchorx * oneMinusCos + anchory * sin;
		this.ty = anchory * oneMinusCos - anchorx * sin;
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

	public Affine2f setTo(float scaleX, float scaleY, float angle, float tx, float ty) {
		float sina = MathUtils.sin(angle), cosa = MathUtils.cos(angle);
		this.m00 = cosa * scaleX;
		this.m01 = sina * scaleY;
		this.m10 = -sina * scaleX;
		this.m11 = cosa * scaleY;
		this.tx = tx;
		this.ty = ty;
		return this;
	}

	public Affine2f setTo(ActionBind bind, float originX, float originY) {
		if (bind == null) {
			return this;
		}
		return setTo(bind.getScaleX(), bind.getScaleY(), MathUtils.toRadians(bind.getRotation()), bind.getX(),
				bind.getY(), bind.getWidth(), bind.getHeight(), originX, originY);
	}

	public Affine2f setTo(ActionBind bind) {
		if (bind == null) {
			return this;
		}
		return setTo(bind, bind.getWidth() / 2f, bind.getHeight() / 2f);
	}

	public Affine2f setTo(float scaleX, float scaleY, float angle, float tx, float ty, float width, float height,
			float originX, float originY) {
		float sina = MathUtils.sin(angle), cosa = MathUtils.cos(angle);
		float a = scaleX * cosa;
		float b = scaleX * sina;
		float c = scaleY * sina;
		float d = scaleY * cosa;
		this.m00 = a;
		this.m01 = b;
		this.m10 = -c;
		this.m11 = d;
		this.tx = -a * originX + c * originY + tx + originX;
		this.ty = -b * originX - d * originY + ty + originY;
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

	/**
	 * 对Affine中所有数值应用缩放转换(会改变tx,ty坐标)
	 * 
	 * @param sx
	 * @param sy
	 * @return
	 */
	public Affine2f scaleAll(final float scaleX, final float scaleY) {
		this.m00 *= scaleX;
		this.m01 *= scaleY;
		this.m10 *= scaleX;
		this.m11 *= scaleY;
		this.tx *= scaleX;
		this.ty *= scaleY;
		return this;
	}

	/**
	 * 对Affine中数值进行缩放转换(不改变tx,ty坐标)
	 * 
	 * @param scaleX
	 * @param scaleY
	 * @return
	 */
	public Affine2f scale(float scaleX, float scaleY) {
		this.m00 *= scaleX;
		this.m01 *= scaleX;
		this.m10 *= scaleY;
		this.m11 *= scaleY;
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

	/**
	 * 沿x和y轴平移矩阵，平移的变化量由上一个x和 y参数决定.
	 * 
	 * @param tx
	 * @param ty
	 * @return
	 */
	public final Affine2f postTranslate(final float tx, final float ty) {
		this.tx += tx;
		this.ty += ty;
		return this;
	}

	/**
	 * 设置矩阵x与y轴的平移距离,还原其它参数为默认值
	 * 
	 * @param tx
	 * @param ty
	 * @return
	 */
	public final Affine2f setToTranslate(final float tx, final float ty) {
		this.m00 = 1.0f;
		this.m01 = 0.0f;
		this.m10 = 0.0f;
		this.m11 = 1.0f;
		this.tx = tx;
		this.ty = ty;
		return this;
	}

	/**
	 * 单纯设置矩阵x与y轴的平移距离,不改变其它参数.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Affine2f setTranslate(final float x, final float y) {
		this.tx = x;
		this.ty = y;
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
			throw new LSysException(this.toString());
		}
		float rdet = 1f / det;
		return new Affine2f(+m11 * rdet, -m10 * rdet, -m01 * rdet, +m00 * rdet, (m10 * ty - m11 * tx) * rdet,
				(m01 * tx - m00 * ty) * rdet);
	}

	public Affine2f invertSelf() {
		float a1 = this.m00;
		float b1 = this.m01;
		float c1 = this.m10;
		float d1 = this.m11;
		float tx1 = this.tx;
		float n = a1 * d1 - b1 * c1;
		this.m00 = d1 / n;
		this.m01 = -b1 / n;
		this.m10 = -c1 / n;
		this.m11 = a1 / n;
		this.tx = (c1 * this.ty - d1 * tx1) / n;
		this.ty = -(a1 * this.ty - b1 * tx1) / n;
		return this;
	}

	public Vector2f apply(XY pos, Vector2f newPos) {
		if (newPos == null) {
			newPos = new Vector2f();
		}
		final float x = pos.getX();
		final float y = pos.getY();
		newPos.x = (this.m00 * x) + (this.m10 * y) + this.tx;
		newPos.y = (this.m01 * x) + (this.m11 * y) + this.ty;
		return newPos;
	}

	/**
	 * 直接设定参数给Affine2f
	 * 
	 * @param x
	 * @param y
	 * @param rotation
	 * @param scaleX
	 * @param scaleY
	 * @return
	 */
	public Affine2f applyITRS(float x, float y, float rotation, float scaleX, float scaleY) {
		float radianSin = MathUtils.sin(rotation);
		float radianCos = MathUtils.cos(rotation);
		this.m00 = radianCos * scaleX;
		this.m01 = radianSin * scaleX;
		this.m10 = -radianSin * scaleY;
		this.m11 = radianCos * scaleY;
		this.tx = x;
		this.ty = y;
		return this;
	}

	/**
	 * 反转x和y坐标
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector2f applyInverse(float x, float y) {
		Vector2f output = new Vector2f();

		float a = this.m00;
		float b = this.m01;
		float c = this.m10;
		float d = this.m11;

		float id = 1f / ((a * d) + (c * -b));

		output.x = (d * id * x) + (-c * id * y) + (((ty * c) - (tx * d)) * id);
		output.y = (a * id * y) + (-b * id * x) + (((-ty * a) + (tx * b)) * id);

		return output;
	}

	/**
	 * 将指定矩阵与当前矩阵连接,从而将这两个矩阵中设定的几何效果结合在一起显示.
	 * 
	 * @param other
	 * @return
	 */
	public Affine2f concat(Affine2f other) {
		float a = this.m00 * other.m00;
		float b = 0f;
		float c = 0f;
		float d = this.m11 * other.m11;
		float tx = this.tx * other.m00 + other.tx;
		float ty = this.ty * other.m11 + other.ty;

		if (this.m10 != 0f || this.m01 != 0f || other.m10 != 0f || other.m01 != 0f) {
			a += this.m10 * other.m01;
			d += this.m01 * other.m10;
			b += this.m00 * other.m10 + this.m10 * other.m11;
			c += this.m01 * other.m00 + this.m11 * other.m01;
			tx += this.ty * other.m01;
			ty += this.tx * other.m10;
		}

		this.m00 = a;
		this.m01 = b;
		this.m10 = c;
		this.m11 = d;
		this.tx = tx;
		this.ty = ty;
		return this;
	}

	/**
	 * 将指定矩阵与当前矩阵连接,从而将这两个矩阵中设定的几何效果结合在一起显示.
	 * 
	 * @param other
	 * @return
	 */
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

	/**
	 * 将指定矩阵与当前矩阵连接,从而将这两个矩阵中设定的几何效果结合在一起显示.
	 * 
	 * @param other
	 * @return
	 */
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

	/**
	 * 将指定矩阵与当前矩阵连接,从而将这两个矩阵中设定的几何效果结合在一起显示.
	 * 
	 * @param t
	 * @return
	 */
	public final Affine2f postConcatenate(final Affine2f t) {
		return postConcatenate(t.m00, t.m01, t.m10, t.m11, t.tx, t.ty);
	}

	/**
	 * 将指定矩阵与当前矩阵连接,从而将这两个矩阵中设定的几何效果结合在一起显示.
	 * 
	 * @param ma
	 * @param mb
	 * @param mc
	 * @param md
	 * @param mx
	 * @param my
	 * @return
	 */
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

	/**
	 * 让矩阵前置一组新矩阵数据
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @param tx
	 * @param ty
	 * @return
	 */
	public Affine2f prepend(float a, float b, float c, float d, float tx, float ty) {
		float tx1 = this.tx;
		if (this.m00 != 1 || b != 0 || c != 0 || d != 1) {
			float a1 = this.m00;
			float c1 = this.m01;
			this.m00 = a1 * a + this.m10 * c;
			this.m10 = a1 * b + this.m10 * d;
			this.m01 = c1 * a + this.m11 * c;
			this.m11 = c1 * b + this.m11 * d;
		}
		this.tx = tx1 * a + this.ty * c + tx;
		this.ty = tx1 * b + this.ty * d + ty;
		return this;
	}

	/**
	 * 让矩阵后置一组新矩阵数据
	 * 
	 * @param other
	 * @return
	 */
	public Affine2f prepend(Affine2f other) {
		return prepend(other.m00, other.m10, other.m01, other.m11, other.tx, other.ty);
	}

	/**
	 * 让矩阵后置一组新矩阵数据
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @param tx
	 * @param ty
	 * @return
	 */
	public Affine2f append(float a, float b, float c, float d, float tx, float ty) {
		float a1 = this.m00;
		float b1 = this.m10;
		float c1 = this.m01;
		float d1 = this.m11;
		if (a != 1 || b != 0 || c != 0 || d != 1) {
			this.m00 = a * a1 + b * c1;
			this.m10 = a * b1 + b * d1;
			this.m01 = c * a1 + d * c1;
			this.m11 = c * b1 + d * d1;
		}
		this.tx = tx * a1 + ty * c1 + this.tx;
		this.ty = tx * b1 + ty * d1 + this.ty;
		return this;
	}

	/**
	 * 让矩阵后置一组新矩阵数据
	 * 
	 * @param other
	 * @return
	 */
	public Affine2f append(Affine2f other) {
		return append(other.m00, other.m10, other.m01, other.m11, other.tx, other.ty);
	}

	/**
	 * 以线性插值方式构建一个新的矩阵
	 * 
	 * @param other
	 * @param t
	 * @return
	 */
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

	public RectBox transformRect(RectBox rect) {

		float top = rect.getMinX();
		float left = rect.getMinX();
		float right = rect.getMaxX();
		float bottom = rect.getMaxY();

		PointF topLeft = new PointF(left, top);
		PointF topRight = new PointF(right, top);
		PointF bottomLeft = new PointF(left, bottom);
		PointF bottomRight = new PointF(right, bottom);

		transformPoint(topLeft);
		transformPoint(topRight);
		transformPoint(bottomLeft);
		transformPoint(bottomRight);

		float minX = MathUtils.min(MathUtils.min(topLeft.x, topRight.x), MathUtils.min(bottomLeft.x, bottomRight.x));
		float maxX = MathUtils.max(MathUtils.max(topLeft.x, topRight.x), MathUtils.max(bottomLeft.x, bottomRight.x));
		float minY = MathUtils.min(MathUtils.min(topLeft.y, topRight.y), MathUtils.min(bottomLeft.y, bottomRight.y));
		float maxY = MathUtils.max(MathUtils.max(topLeft.y, topRight.y), MathUtils.max(bottomLeft.y, bottomRight.y));

		return rect.setBounds(minX, minY, (maxX - minX), (maxY - minY));
	}

	public PointI transformPoint(PointI resultPoint) {
		return transformPoint(resultPoint.x, resultPoint.y, resultPoint);
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

	public PointF transformPoint(PointF resultPoint) {
		return transformPoint(resultPoint.x, resultPoint.y, resultPoint);
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

	public Vector2f transformPoint(Vector2f resultPoint) {
		return transformPoint(resultPoint.x, resultPoint.y, resultPoint);
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
			throw new LSysException("Affine2f exception " + this.toString());
		}
		float rdet = 1 / det;
		return into.set((x * m11 - y * m10) * rdet, (y * m00 - x * m01) * rdet);
	}

	public Transform decompose(Transform transform) {
		final float a = this.m00;
		final float b = this.m01;
		final float c = this.m10;
		final float d = this.m11;
		final ObservableXY<Vector2f> pivot = transform.pivot;

		final float skewX = -MathUtils.atan2(-c, d);
		final float skewY = MathUtils.atan2(b, a);

		final float delta = MathUtils.abs(skewX + skewY);

		if (delta < MathUtils.EPSILON || MathUtils.abs(MathUtils.TWO_PI - delta) < MathUtils.EPSILON) {
			transform.setRotation(skewY);
			transform.skew.setX(0f);
			transform.skew.setY(0f);
		} else {
			transform.setRotation(0f);
			transform.skew.setX(skewX);
			transform.skew.setY(skewY);
		}

		transform.scale.setX(MathUtils.sqrt((a * a) + (b * b)));
		transform.scale.setY(MathUtils.sqrt((c * c) + (d * d)));

		transform.position.setX(this.tx + ((pivot.getX() * a) + (pivot.getY() * c)));
		transform.position.setY(this.ty + ((pivot.getX() * b) + (pivot.getY() * d)));

		return transform;
	}

	public Affine2f setToOrtho2D(float x, float y, float width, float height) {
		setToOrtho(x, x + width, y + height, y, 1f, -1f);
		return this;
	}

	public Affine2f setToOrtho2D(float x, float y, float width, float height, float near, float far) {
		setToOrtho(x, x + width, y + height, y, near, far);
		return this;
	}

	public Affine2f setToOrtho(float left, float right, float bottom, float top, float near, float far) {
		float x_orth = 2 / (right - left);
		float y_orth = 2 / (top - bottom);

		float m03 = -(right + left) / (right - left);
		float m13 = -(top + bottom) / (top - bottom);

		this.m00 = x_orth;
		this.m11 = y_orth;
		this.tx = m03;
		this.ty = m13;

		return this;
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

	public Affine2f cpy(Affine2f other) {
		this.m00 = other.m00;
		this.m01 = other.m01;
		this.m10 = other.m10;
		this.m11 = other.m11;
		this.tx = other.tx;
		this.ty = other.ty;
		return this;
	}

	public Affine2f cpyTo(Affine2f other) {
		other.m00 = this.m00;
		other.m01 = this.m01;
		other.m10 = this.m10;
		other.m11 = this.m11;
		other.tx = this.tx;
		other.ty = this.ty;
		return this;
	}

	public Affine2f cpy() {
		return new Affine2f(m00, m01, m10, m11, tx, ty);
	}

	/**
	 * 如果Affine2f中此函数返回值不为默认值,则所有会从另一个Affine2f对象产生Affine2f实体的方法都不会产生新的Affine2f,而是改变自身参数
	 * 
	 * @return
	 */
	public int generality() {
		return GENERALITY;
	}

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
		final int prime = 86;
		int hashCode = 17;
		hashCode = prime * LSystem.unite(hashCode, m00);
		hashCode = prime * LSystem.unite(hashCode, m01);
		hashCode = prime * LSystem.unite(hashCode, m10);
		hashCode = prime * LSystem.unite(hashCode, m11);
		hashCode = prime * LSystem.unite(hashCode, tx);
		hashCode = prime * LSystem.unite(hashCode, ty);
		return hashCode;
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
