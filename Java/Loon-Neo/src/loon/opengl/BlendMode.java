package loon.opengl;

import loon.LSystem;

public class BlendMode {

	protected int _blend = LSystem.MODE_NORMAL;

	public void blendNormal() {
		_blend = LSystem.MODE_NORMAL;
	}

	public void blendSpeed() {
		_blend = LSystem.MODE_SPEED;
	}

	public void blendAdd() {
		_blend = LSystem.MODE_ALPHA_ADD;
	}

	public void blendMultiply() {
		_blend = LSystem.MODE_MULTIPLY;
	}

	public void blendMask() {
		_blend = LSystem.MODE_MASK;
	}

	public int getBlend() {
		return _blend;
	}

	public void setBlend(int b) {
		this._blend = b;
	}
}
