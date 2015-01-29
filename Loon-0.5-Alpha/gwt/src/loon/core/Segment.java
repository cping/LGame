package loon.core;

import java.io.Serializable;

import loon.core.geom.Vector3f;


public class Segment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3682695256015093326L;

	public final Vector3f a = new Vector3f();

	public final Vector3f b = new Vector3f();

	public Segment(Vector3f a, Vector3f b) {
		this.a.set(a);
		this.b.set(b);
	}

	public Segment(float aX, float aY, float aZ, float bX, float bY, float bZ) {
		this.a.set(aX, aY, aZ);
		this.b.set(bX, bY, bZ);
	}

	public float len() {
		return a.dst(b);
	}

	public float len2() {
		return a.dst2(b);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || o.getClass() != this.getClass())
			return false;
		Segment s = (Segment) o;
		return this.a.equals(s.a) && this.b.equals(s.b);
	}

	@Override
	public int hashCode() {
		final int prime = 71;
		int result = 1;
		result = prime * result + this.a.hashCode();
		result = prime * result + this.b.hashCode();
		return result;
	}
}
