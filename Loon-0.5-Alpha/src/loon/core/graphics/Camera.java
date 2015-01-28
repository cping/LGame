package loon.core.graphics;

import loon.core.Ray;
import loon.core.geom.Frustum;
import loon.core.geom.Matrix4;
import loon.core.geom.Quaternion;
import loon.core.geom.Vector3f;
import loon.core.graphics.opengl.GLEx;

public abstract class Camera {

	public final Vector3f position = new Vector3f();

	public final Vector3f direction = new Vector3f(0, 0, -1);

	public final Vector3f up = new Vector3f(0, 1, 0);

	public final Matrix4 projection = new Matrix4();

	public final Matrix4 view = new Matrix4();

	public final Matrix4 combined = new Matrix4();

	public final Matrix4 invProjectionView = new Matrix4();

	public float near = 1;

	public float far = 100;

	public float viewportWidth = 0;

	public float viewportHeight = 0;

	public final Frustum frustum = new Frustum();

	private final Vector3f tmpVec = new Vector3f();
	private final Ray ray = new Ray(new Vector3f(), new Vector3f());

	public abstract void update();

	public abstract void update(boolean updateFrustum);

	public void lookAt(float x, float y, float z) {
		tmpVec.set(x, y, z).sub(position).nor();
		if (!tmpVec.isZero()) {
			float dot = tmpVec.dot(up);
			if (Math.abs(dot - 1) < 0.000000001f) {
				up.set(direction).scl(-1);
			} else if (Math.abs(dot + 1) < 0.000000001f) {
				up.set(direction);
			}
			direction.set(tmpVec);
			normalizeUp();
		}
	}

	public void lookAt(Vector3f target) {
		lookAt(target.x, target.y, target.z);
	}

	public void normalizeUp() {
		tmpVec.set(direction).crs(up).nor();
		up.set(tmpVec).crs(direction).nor();
	}

	public void rotate(float angle, float axisX, float axisY, float axisZ) {
		direction.rotate(angle, axisX, axisY, axisZ);
		up.rotate(angle, axisX, axisY, axisZ);
	}

	public void rotate(Vector3f axis, float angle) {
		direction.rotate(axis, angle);
		up.rotate(axis, angle);
	}

	public void rotate(final Matrix4 transform) {
		direction.rot(transform);
		up.rot(transform);
	}

	public void rotate(final Quaternion quat) {
		quat.transform(direction);
		quat.transform(up);
	}

	public void rotateAround(Vector3f point, Vector3f axis, float angle) {
		tmpVec.set(point);
		tmpVec.sub(position);
		translate(tmpVec);
		rotate(axis, angle);
		tmpVec.rotate(axis, angle);
		translate(-tmpVec.x, -tmpVec.y, -tmpVec.z);
	}

	public void transform(final Matrix4 transform) {
		position.mul(transform);
		rotate(transform);
	}

	public void translate(float x, float y, float z) {
		position.add(x, y, z);
	}

	public void translate(Vector3f vec) {
		position.add(vec);
	}

	public Vector3f unproject(Vector3f screenCoords, float viewportX,
			float viewportY, float viewportWidth, float viewportHeight) {
		float x = screenCoords.x, y = screenCoords.y;
		x = x - viewportX;
		y = GLEx.height() - y - 1;
		y = y - viewportY;
		screenCoords.x = (2 * x) / viewportWidth - 1;
		screenCoords.y = (2 * y) / viewportHeight - 1;
		screenCoords.z = 2 * screenCoords.z - 1;
		screenCoords.prj(invProjectionView);
		return screenCoords;
	}

	public Vector3f unproject(Vector3f screenCoords) {
		unproject(screenCoords, 0, 0, GLEx.width(), GLEx.height());
		return screenCoords;
	}

	public Vector3f project(Vector3f worldCoords) {
		project(worldCoords, 0, 0, GLEx.width(), GLEx.height());
		return worldCoords;
	}

	public Vector3f project(Vector3f worldCoords, float viewportX,
			float viewportY, float viewportWidth, float viewportHeight) {
		worldCoords.prj(combined);
		worldCoords.x = viewportWidth * (worldCoords.x + 1) / 2 + viewportX;
		worldCoords.y = viewportHeight * (worldCoords.y + 1) / 2 + viewportY;
		worldCoords.z = (worldCoords.z + 1) / 2;
		return worldCoords;
	}

	public Ray getPickRay(float screenX, float screenY, float viewportX,
			float viewportY, float viewportWidth, float viewportHeight) {
		unproject(ray.origin.set(screenX, screenY, 0), viewportX, viewportY,
				viewportWidth, viewportHeight);
		unproject(ray.direction.set(screenX, screenY, 1), viewportX, viewportY,
				viewportWidth, viewportHeight);
		ray.direction.sub(ray.origin).nor();
		return ray;
	}

	public Ray getPickRay(float screenX, float screenY) {
		return getPickRay(screenX, screenY, 0, 0, GLEx.width(), GLEx.height());
	}
}
