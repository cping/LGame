package loon.core.geom;

import java.io.Serializable;

public class Plane implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5726090493187213261L;

	public enum PlaneSide {
		OnPlane, Back, Front
	}

	public final Vector3f normal = new Vector3f();
	public float d = 0;

	public Plane(Vector3f normal, float d) {
		this.normal.set(normal).nor();
		this.d = d;
	}

	public Plane(Vector3f normal, Vector3f point) {
		this.normal.set(normal).nor();
		this.d = -this.normal.dot(point);
	}

	public Plane(Vector3f point1, Vector3f point2, Vector3f point3) {
		set(point1, point2, point3);
	}

	public void set(Vector3f point1, Vector3f point2, Vector3f point3) {
		normal.set(point1)
				.sub(point2)
				.crs(point2.x - point3.x, point2.y - point3.y,
						point2.z - point3.z).nor();
		d = -point1.dot(normal);
	}

	public void set(float nx, float ny, float nz, float d) {
		normal.set(nx, ny, nz);
		this.d = d;
	}

	public float distance(Vector3f point) {
		return normal.dot(point) + d;
	}

	public PlaneSide testPoint(Vector3f point) {
		float dist = normal.dot(point) + d;
		if (dist == 0) {
			return PlaneSide.OnPlane;
		} else if (dist < 0) {
			return PlaneSide.Back;
		} else {
			return PlaneSide.Front;
		}
	}

	public PlaneSide testPoint(float x, float y, float z) {
		float dist = normal.dot(x, y, z) + d;
		if (dist == 0) {
			return PlaneSide.OnPlane;
		} else if (dist < 0) {
			return PlaneSide.Back;
		} else {
			return PlaneSide.Front;
		}
	}

	public boolean isFrontFacing(Vector3f direction) {
		float dot = normal.dot(direction);
		return dot <= 0;
	}

	public Vector3f getNormal() {
		return normal;
	}

	public float getD() {
		return d;
	}

	public void set(Vector3f point, Vector3f normal) {
		this.normal.set(normal);
		d = -point.dot(normal);
	}

	public void set(float pointX, float pointY, float pointZ, float norX,
			float norY, float norZ) {
		this.normal.set(norX, norY, norZ);
		d = -(pointX * norX + pointY * norY + pointZ * norZ);
	}

	public void set(Plane plane) {
		this.normal.set(plane.normal);
		this.d = plane.d;
	}

	public String toString() {
		return normal.toString() + ", " + d;
	}
}
