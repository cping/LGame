package loon.live2d.framework;

import loon.geom.Matrix4;

public class L2DMatrix44 {

	protected float _val[] = new float[16];

	public L2DMatrix44() {
		identity();
	}

	public void identity() {
		for (int i = 0; i < 16; i++)
			_val[i] = ((i % 5) == 0) ? 1 : 0;
	}

	public float[] getArray() {
		return _val;
	}

	public float[] getCopyMatrix() {
		return _val.clone();
	}

	public void setMatrix(float _val[]) {
		if (_val == null || this._val.length != _val.length)
			return;
		for (int i = 0; i < 16; i++)
			this._val[i] = _val[i];
	}

	public float getScaleX() {
		return _val[0];
	}

	public float getScaleY() {
		return _val[5];
	}

	public float transformX(float src) {
		return _val[0] * src + _val[12];
	}

	public float transformY(float src) {
		return _val[5] * src + _val[13];
	}

	public float invertTransformX(float src) {
		return (src - _val[12]) / _val[0];
	}

	public float invertTransformY(float src) {
		return (src - _val[13]) / _val[5];
	}

	protected static void mul(float[] a, float[] b, float[] dst) {
		float c[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int n = 4;
		int i, j, k;

		for (i = 0; i < n; i++) {
			for (j = 0; j < n; j++) {
				for (k = 0; k < n; k++) {
					c[i + j * 4] += a[i + k * 4] * b[k + j * 4];
				}
			}
		}

		for (i = 0; i < 16; i++) {
			dst[i] = c[i];
		}
	}

	public void multTranslate(float shiftX, float shiftY) {
		float tr1[] = { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, shiftX, shiftY, 0,
				1 };
		mul(tr1, _val, _val);
	}

	public void translate(float x, float y) {
		_val[12] = x;
		_val[13] = y;
	}

	public void translateX(float x) {
		_val[12] = x;
	}

	public void translateY(float y) {
		_val[13] = y;
	}

	public void multScale(float scaleX, float scaleY) {
		float tr1[] = { scaleX, 0, 0, 0, 0, scaleY, 0, 0, 0, 0, 1, 0, 0, 0, 0,
				1 };
		mul(tr1, _val, _val);
	}

	public void scale(float scaleX, float scaleY) {
		_val[0] = scaleX;
		_val[5] = scaleY;
	}

	public Matrix4 get() {
		return new Matrix4(_val);
	}
}
