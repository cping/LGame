package loon.live2d.framework;

public class L2DViewMatrix extends L2DMatrix44 {
	private float max;
	private float min;

	private float screenLeft;
	private float screenRight;
	private float screenTop;
	private float screenBottom;
	private float maxLeft;
	private float maxRight;
	private float maxTop;
	private float maxBottom;

	public L2DViewMatrix() {
		max = 1f;
		min = 1f;
	}

	public float getMaxScale() {
		return max;
	}

	public float getMinScale() {
		return min;
	}

	public void setMaxScale(float v) {
		max = v;
	}

	public void setMinScale(float v) {
		min = v;
	}

	public boolean isMaxScale() {
		return getScaleX() == max;
	}

	public boolean isMinScale() {
		return getScaleX() == min;
	}

	public void adjustTranslate(float shiftX, float shiftY) {
		if (_val[0] * maxLeft + (_val[12] + shiftX) > screenLeft)
			shiftX = screenLeft - _val[0] * maxLeft - _val[12];
		if (_val[0] * maxRight + (_val[12] + shiftX) < screenRight)
			shiftX = screenRight - _val[0] * maxRight - _val[12];

		if (_val[5] * maxTop + (_val[13] + shiftY) < screenTop)
			shiftY = screenTop - _val[5] * maxTop - _val[13];
		if (_val[5] * maxBottom + (_val[13] + shiftY) > screenBottom)
			shiftY = screenBottom - _val[5] * maxBottom - _val[13];

		float tr1[] = { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, shiftX, shiftY, 0,
				1 };
		mul(tr1, _val, _val);
	}

	public void adjustScale(float cx, float cy, float scale) {
		float targetScale = scale * _val[0];
		if (targetScale < min) {
			if (_val[0] > 0)
				scale = min / _val[0];
		} else if (targetScale > max) {
			if (_val[0] > 0)
				scale = max / _val[0];
		}

		float tr1[] = { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, cx, cy, 0, 1 };
		float tr2[] = { scale, 0, 0, 0, 0, scale, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };
		float tr3[] = { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, -cx, -cy, 0, 1 };

		mul(tr3, _val, _val);
		mul(tr2, _val, _val);
		mul(tr1, _val, _val);
	}

	public void setScreenRect(float left, float right, float bottom, float top) {
		screenLeft = left;
		screenRight = right;
		screenTop = top;
		screenBottom = bottom;
	}

	public void setMaxScreenRect(float left, float right, float bottom,
			float top) {
		maxLeft = left;
		maxRight = right;
		maxTop = top;
		maxBottom = bottom;
	}

	public float getScreenLeft() {
		return screenLeft;
	}

	public float getScreenRight() {
		return screenRight;
	}

	public float getScreenBottom() {
		return screenBottom;
	}

	public float getScreenTop() {
		return screenTop;
	}

	public float getMaxLeft() {
		return maxLeft;
	}

	public float getMaxRight() {
		return maxRight;
	}

	public float getMaxBottom() {
		return maxBottom;
	}

	public float getMaxTop() {
		return maxTop;
	}
}
