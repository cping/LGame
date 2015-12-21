package loon.action.camera;

import loon.geom.Transforms;
import loon.geom.Vector2f;
import loon.geom.Vector3f;

public class OrthographicCamera extends EmptyCamera {

	private float width;
	private float height;

	public OrthographicCamera() {
		super();
	}

	public OrthographicCamera(float width, float height) {
		this(0, width, height, 0);
	}

	public OrthographicCamera(float left, float right, float bottom, float top) {
		super();
		width = right - left;
		height = bottom - top;
		_viewMatrix4 = Transforms.createOrtho2d(left, right, bottom, top, 0,
				100);
	}

	public OrthographicCamera translate(Vector2f v) {
		_viewMatrix4.mul(Transforms.createTranslation(new Vector3f(v, 0)));
		return this;
	}

	public OrthographicCamera translateTo(float x, float y) {
		_viewMatrix4.idt().mul(
				Transforms.createTranslation(new Vector3f(x, y, 0)));
		return this;
	}

	public OrthographicCamera translateTo(Vector2f v) {
		_viewMatrix4.idt()
				.mul(Transforms.createTranslation(new Vector3f(v, 0)));
		return this;
	}

	public OrthographicCamera center(float x, float y) {
		return center(new Vector2f(x, y));
	}

	public OrthographicCamera center(Vector2f v) {
		_viewMatrix4.idt();
		float x = (width / 2) - v.getX();
		float y = (height / 2) - v.getY();

		return translate(x, y);
	}

	public OrthographicCamera translate(float x, float y) {
		_viewMatrix4.mul(Transforms.createTranslation(new Vector3f(x, y, 0)));
		return this;
	}

	public OrthographicCamera rotate(Vector3f axis, float angle) {
		_viewMatrix4.mul(Transforms.createRotation(axis, angle));
		return this;
	}

	public OrthographicCamera initProjection(float width, float height) {
		return initProjection(0, width, height, 0);
	}

	public OrthographicCamera initProjection(float left, float right,
			float bottom, float top) {
		width = right - left;
		height = bottom - top;
		Transforms
				.createOrtho2d(left, right, bottom, top, 0, 100, _projMatrix4);
		return this;
	}

}
