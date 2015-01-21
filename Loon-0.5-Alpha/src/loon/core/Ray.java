package loon.core;

import java.io.Serializable;

import loon.core.graphics.opengl.math.Transform4;
import loon.core.graphics.opengl.math.Location3;


public class Ray implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4546096379943625054L;
	public final Location3 origin = new Location3();
	public final Location3 direction = new Location3();

	public Ray(Location3 origin, Location3 direction) {
		this.origin.set(origin);
		this.direction.set(direction).nor();
	}

	public Ray cpy() {
		return new Ray(this.origin, this.direction);
	}

	public Location3 getEndPoint(float distance) {
		return getEndPoint(new Location3(), distance);
	}

	public Location3 getEndPoint(final Location3 out, final float distance) {
		return out.set(direction).scl(distance).add(origin);
	}

	static Location3 tmp = new Location3();

	public Ray mul(Transform4 matrix) {
		tmp.set(origin).add(direction);
		tmp.mul(matrix);
		origin.mul(matrix);
		direction.set(tmp.sub(origin));
		return this;
	}

	public String toString() {
		return "ray [" + origin + ":" + direction + "]";
	}

	public Ray set(Location3 origin, Location3 direction) {
		this.origin.set(origin);
		this.direction.set(direction);
		return this;
	}

	public Ray set(float x, float y, float z, float dx, float dy, float dz) {
		this.origin.set(x, y, z);
		this.direction.set(dx, dy, dz);
		return this;
	}

	public Ray set(Ray ray) {
		this.origin.set(ray.origin);
		this.direction.set(ray.direction);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || o.getClass() != this.getClass())
			return false;
		Ray r = (Ray) o;
		return this.direction.equals(r.direction)
				&& this.origin.equals(r.origin);
	}

	@Override
	public int hashCode() {
		final int prime = 73;
		int result = 1;
		result = prime * result + this.direction.hashCode();
		result = prime * result + this.origin.hashCode();
		return result;
	}
}
