package loon.core.graphics.opengl.math;

import java.io.Serializable;

public class Plane implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5726090493187213261L;

	public enum PlaneSide {
		OnPlane, Back, Front
	}

	public final Location3 normal = new Location3();
	public float d = 0;

	public Plane(Location3 normal, float d) {
		this.normal.set(normal).nor();
		this.d = d;
	}

	public Plane(Location3 normal, Location3 point) {
		this.normal.set(normal).nor();
		this.d = -this.normal.dot(point);
	}

	public Plane(Location3 point1, Location3 point2, Location3 point3) {
		set(point1, point2, point3);
	}

	public void set(Location3 point1, Location3 point2, Location3 point3) {
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

	public float distance(Location3 point) {
		return normal.dot(point) + d;
	}

	public PlaneSide testPoint(Location3 point) {
		float dist = normal.dot(point) + d;
		if (dist == 0){
			return PlaneSide.OnPlane;
		}
		else if (dist < 0){
			return PlaneSide.Back;
		}
		else{
			return PlaneSide.Front;
		}
	}

	public PlaneSide testPoint(float x, float y, float z) {
		float dist = normal.dot(x, y, z) + d;
		if (dist == 0){
			return PlaneSide.OnPlane;
		}
		else if (dist < 0){
			return PlaneSide.Back;
		}
		else{
			return PlaneSide.Front;
		}
	}

	public boolean isFrontFacing(Location3 direction) {
		float dot = normal.dot(direction);
		return dot <= 0;
	}

	public Location3 getNormal() {
		return normal;
	}

	public float getD() {
		return d;
	}

	public void set(Location3 point, Location3 normal) {
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
