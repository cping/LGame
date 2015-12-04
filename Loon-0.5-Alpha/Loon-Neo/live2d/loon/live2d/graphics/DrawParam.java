package loon.live2d.graphics;

import loon.canvas.LColor;
import loon.utils.TArray;

public abstract class DrawParam {

	protected int size;
	protected float alpha;
	protected float red;
	protected float green;
	protected float blue;
	protected boolean _culling;
	protected boolean _updateAlpha;
	protected int o;
	protected float[] _matrix4x4;
	protected TArray<LColor> list;

	public DrawParam() {
		this.size = 32;
		this.alpha = 1.0f;
		this.red = 1.0f;
		this.green = 1.0f;
		this.blue = 1.0f;
		this._culling = false;
		this._updateAlpha = true;
		this.o = -1;
		this._matrix4x4 = new float[16];
		this.list = new TArray<LColor>(size);
	}

	public abstract void drawTexture(final int p0, final int p1,
			final short[] p2, final float[] p3, final float[] p4,
			final float p5, final int p6);

	public void releaseModelTextureNo(final int no) {

	}

	public void setBaseColor(float alpha, float red, float green, float blue) {
		if (alpha < 0.0f) {
			alpha = 0.0f;
		} else if (alpha > 1.0f) {
			alpha = 1.0f;
		}
		if (red < 0.0f) {
			red = 0.0f;
		} else if (red > 1.0f) {
			red = 1.0f;
		}
		if (green < 0.0f) {
			green = 0.0f;
		} else if (green > 1.0f) {
			green = 1.0f;
		}
		if (blue < 0.0f) {
			blue = 0.0f;
		} else if (blue > 1.0f) {
			blue = 1.0f;
		}
		this.alpha = alpha;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public void setCulling(final boolean culling) {
		this._culling = culling;
	}

	public void setMatrix(final float[] matrix4x4) {
		if (matrix4x4.length != 16) {
			return;
		}
		for (int i = 0; i < 16; ++i) {
			this._matrix4x4[i] = matrix4x4[i];
		}
	}

	public void setPremultipliedAlpha(final boolean b) {
		this._updateAlpha = b;
	}

	public boolean isPremultipliedAlpha() {
		return this._updateAlpha;
	}

	void a(final int o) {
		this.o = o;
	}

	int b() {
		return this.o;
	}

	public void setTextureColor(final int textureNo, final float r,
			final float g, final float b) {
		setTextureColor(textureNo, r, g, b, 1f);
	}

	public void setTextureColor(final int textureNo, final float r,
			final float g, final float b, final float a) {
		if (textureNo < this.list.size) {
			final LColor color = this.list.get(textureNo);
			color.r = r;
			color.g = g;
			color.b = b;
			color.a = a;
		}
	}

	protected void newColors(final int n) {
		for (; this.list.size < n + 1;) {
			this.list.add(new LColor());
		}
	}

}
