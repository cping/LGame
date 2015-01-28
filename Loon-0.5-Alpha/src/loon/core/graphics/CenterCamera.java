package loon.core.graphics;

import loon.core.geom.Vector2f;
import loon.core.geom.Vector3f;
import loon.core.graphics.opengl.GLEx;
import loon.jni.NativeSupport;

public class CenterCamera extends Camera {

	public float zoom = 1;

	public CenterCamera() {
		this.near = 0;
	}

	public CenterCamera(float viewportWidth, float viewportHeight) {
		this.viewportWidth = viewportWidth;
		this.viewportHeight = viewportHeight;
		this.near = 0;
		update();
	}

	private final Vector3f tmp = new Vector3f();

	@Override
	public void update() {
		update(true);
	}

	@Override
	public void update(boolean updateFrustum) {
		projection.setToOrtho(zoom * -viewportWidth / 2, zoom
				* (viewportWidth / 2), zoom * -(viewportHeight / 2), zoom
				* viewportHeight / 2, near, far);
		view.setToLookAt(position, tmp.set(position).add(direction), up);
		combined.set(projection);
		NativeSupport.mul(combined.val, view.val);
		if (updateFrustum) {
			invProjectionView.set(combined);
			NativeSupport.inv(invProjectionView.val);
			frustum.update(invProjectionView);
		}
	}

	public void setToOrtho(boolean down) {
		setToOrtho(down, GLEx.width(), GLEx.height());
	}

	public void setToOrtho(boolean down, float viewportWidth,
			float viewportHeight) {
		if (down) {
			up.set(0, -1, 0);
			direction.set(0, 0, 1);
		} else {
			up.set(0, 1, 0);
			direction.set(0, 0, -1);
		}
		position.set(zoom * viewportWidth / 2.0f, zoom * viewportHeight / 2.0f,
				0);
		this.viewportWidth = viewportWidth;
		this.viewportHeight = viewportHeight;
		update();
	}

	public void rotate(float angle) {
		rotate(direction, angle);
	}

	public void translate(float x, float y) {
		translate(x, y, 0);
	}

	public void translate(Vector2f vec) {
		translate(vec.x, vec.y, 0);
	}
}
