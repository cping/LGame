package org.test.crazyjumpergles.common;

import loon.geom.Vector3f;


public class Line3 {
	public Vector3f b;
	public Vector3f m;

	public Line3() {
		this.b = new Vector3f();
		this.m = new Vector3f();
	}

	public Line3(Line3 l) {
		this.b = new Vector3f();
		this.m = new Vector3f();
		this.b.x = l.b.x;
		this.b.y = l.b.y;
		this.b.z = l.b.z;
		this.m.x = l.m.x;
		this.m.y = l.m.y;
		this.m.z = l.m.z;
	}

	public Line3(Vector3f v0, Vector3f v1) {
		this.b = new Vector3f();
		this.m = new Vector3f();
		this.b.x = v0.x;
		this.b.y = v0.y;
		this.b.z = v0.z;
		this.m = v1.sub(v0);
	}

	public final float distance(Vector3f p) {
		Vector3f vector = p.sub(this.b);
		float num = Vector3f.dot(this.m, this.m);
		if (num > 0f) {
			float num2 = Vector3f.dot(this.m, vector) / num;
			Vector3f.sub(vector, this.m.mul(num2));
			return vector.len2();
		}
		Vector3f vector2 = p.sub(this.b);
		return vector2.len2();
	}

	public final float distance_sqr(Vector3f p) {
		Vector3f vector = p.sub(this.b);
		float num = Vector3f.dot(this.m, this.m);
		if (num > 0f) {
			float num2 = Vector3f.dot(this.m, vector) / num;
			Vector3f.sub(vector, this.m.mul(num2));
			return vector.len();
		}
		Vector3f vector2 = p.sub(this.b);
		return vector2.len();
	}

	public final Vector3f end() {
		return (this.b.add(this.m));
	}

	public final Vector3f ipol(float t) {
		return this.b.add(this.m.mul(t));
	}

	public final float len() {
		return this.m.len();
	}

	public final void set(Vector3f v0, Vector3f v1) {
		this.b.set(v0);
		this.m.set(v1.x - v0.x, v1.y - v0.y, v1.z - v0.z);
	}

	public final Vector3f start() {
		return this.b;
	}
}