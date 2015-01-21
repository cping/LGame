package loon.core;

import java.io.Serializable;

import loon.core.graphics.opengl.math.Location3;

public class Segment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3682695256015093326L;

	public final Location3 a = new Location3();

	public final Location3 b = new Location3();

	public Segment(Location3 a, Location3 b) {
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
