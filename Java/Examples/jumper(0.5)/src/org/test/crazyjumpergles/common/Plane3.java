package org.test.crazyjumpergles.common;

import loon.geom.Vector3f;
import loon.utils.RefObject;

public class Plane3 {

	public float a;

	public float b;

	public float c;

	public float d;

	public Plane3() {
		this.a = 0f;
		this.b = 0f;
		this.c = 0f;
		this.d = 1f;
	}

	public Plane3(Plane3 p) {
		this.a = p.a;
		this.b = p.b;
		this.c = p.c;
		this.d = p.d;
	}

	public Plane3(Vector3f vNormal, Vector3f vPoint) {
		this.set(vNormal, vPoint);
	}

	public Plane3(Vector3f v0, Vector3f v1, Vector3f v2) {
		this.set(v0, v1, v2);
	}

	public Plane3(float _a, float _b, float _c, float _d) {
		this.a = _a;
		this.b = _b;
		this.c = _c;
		this.d = _d;
	}

	public final float distance(Vector3f v) {
		return ((((this.a * v.x) + (this.b * v.y)) + (this.c * v.z)) + this.d);
	}

	public final float distance2(Vector3f v) {
		return ((((this.a * v.x) + (this.b * v.y)) + (this.c * v.z)) - this.d);
	}

	private Vector3f vector1 = new Vector3f(this.a, this.b, this.c);

	public final boolean intersect(Line3 l, RefObject<Float> t) {
		vector1.set(a, b, c);
		float num = Vector3f.dot(vector1, l.m);
		if (num > 0f) {
			t.argvalue = (Vector3f.dot(vector1, l.b) + this.d) / num;
			return true;
		}
		return false;
	}

	public final boolean intersect(Plane3 p, Line3 l) {
		Vector3f vector = this.normal();
		Vector3f vector2 = p.normal();
		float num = Vector3f.dot(vector, vector);
		float num2 = Vector3f.dot(vector, vector2);
		float num3 = Vector3f.dot(vector2, vector2);
		float num4 = (num * num3) - (num2 * num2);
		if (Math.abs(num4) < 1E-06f) {
			return false;
		}
		float num5 = 1f / num4;
		float num6 = ((num3 * this.d) - (num2 * p.d)) * num5;
		float num7 = ((num * p.d) - (num2 * this.d)) * num5;
		l.m = Vector3f.cross(vector, vector2);
		l.b = Vector3f.add((vector.mul(num6)), (vector2.mul(num7)));
		return true;
	}

	private Vector3f vector2 = new Vector3f(this.a, this.b, this.c);

	public final boolean intersect_ray(Vector3f rorigin, Vector3f rdir,
			RefObject<Float> t) {
		vector2.set(this.a, this.b, this.c);
		float num = Vector3f.dot(vector2, rorigin) + this.d;
		float num2 = Vector3f.dot(vector2, rdir);
		if (num2 == 0f) {
			return false;
		}
		t.argvalue = -(num / num2);
		return true;
	}

	private Vector3f vector3 = new Vector3f(this.a, this.b, this.c);

	public final Vector3f mirror_point(Vector3f p) {
		vector3.set(this.a, this.b, this.c);
		Vector3f vector3 = this.project_point(p);
		float num = this.distance(p);
		return Vector3f.add(vector3, (vector3.mul(-num)));
	}

	private Vector3f nors = new Vector3f(this.a, this.b, this.c);

	public final Vector3f normal() {
		nors.set(this.a, this.b, this.c);
		return nors;
	}

	private Vector3f vector4 = new Vector3f(this.a, this.b, this.c);
	
	public final Vector3f project_point(Vector3f p) {
		float num2 = (((this.a * p.x) + (this.b * p.y)) + (this.c * p.z))
				+ this.d;
		float num3 = ((this.a * this.a) + (this.b * this.b))
				+ (this.c * this.c);
		float num = -(num2 / num3);
		vector4.x = p.x + (num * this.a);
		vector4.y = p.y + (num * this.b);
		vector4.z = p.z + (num * this.c);
		return vector4;
	}

	public final void set(Vector3f vNormal, Vector3f vPoint) {
		this.a = vNormal.x;
		this.b = vNormal.y;
		this.c = vNormal.z;
		this.d = -(((this.a * vPoint.x) + (this.b * vPoint.y)) + (this.c * vPoint.z));
	}

	public final void set(Vector3f v0, Vector3f v1, Vector3f v2) {
		Vector3f vector = Vector3f.cross(v2.sub(v0), v1.sub(v0));
		vector.nor();
		this.a = vector.x;
		this.b = vector.y;
		this.c = vector.z;
		this.d = -(((this.a * v0.x) + (this.b * v0.y)) + (this.c * v0.z));
	}

	public final void set(float _a, float _b, float _c, float _d) {
		this.a = _a;
		this.b = _b;
		this.c = _c;
		this.d = _d;
	}

	public final void set_normal(Vector3f n) {
		this.a = n.x;
		this.b = n.y;
		this.c = n.z;
	}

	public final Vector3f split(Vector3f a, Vector3f b) {
		Vector3f vector = this.normal();
		float num = Vector3f.dot(a, vector);
		float num2 = Vector3f.dot(b, vector);
		float num3 = (-this.d - num) / (num2 - num);
		return Vector3f.add(a, b.sub(a).mul(num3));
	}
}