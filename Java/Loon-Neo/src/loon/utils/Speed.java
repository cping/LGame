package loon.utils;

import loon.geom.Vector2f;
import loon.utils.MathUtils;

public class Speed {

	private static Vector2f gravity = new Vector2f(0.0f, 0.6f);

	private float dx = 0.0f;

	private float dy = 0.0f;

	private float direction = 0;

	private float length;

	public Speed() {
	}

	public Speed(float direction, float length) {
		this.set(direction, length);
	}

	public static Vector2f getVelocity(Vector2f velocity, TArray<Vector2f> forces) {
		for (Vector2f v : forces) {
			velocity.add(v);
		}
		return velocity;
	}

	public static Vector2f elasticForce(Vector2f displacement,
			float forceConstant) {
		float forceX = -forceConstant * displacement.getX();
		float forceY = -forceConstant * displacement.getY();
		Vector2f theForce = new Vector2f(forceX, forceY);
		return theForce;
	}

	public static Vector2f getVelocity(Vector2f velocity, Vector2f force) {
		velocity.add(force);
		return velocity;
	}

	public static Vector2f getVelocity(Vector2f velocity, Vector2f force,
			float mass) {
		Vector2f acceleration = new Vector2f(force.getX() / mass, force.getY()
				/ mass);
		velocity.add(acceleration);
		return velocity;
	}

	public static void setGravity(int g) {
		gravity.setY(g);
	}

	public static Vector2f Gravity() {
		return gravity;
	}

	public void set(float direction, float length) {
		this.length = length;
		this.direction = direction;
		this.dx = (length * MathUtils.cos(MathUtils
				.toRadians(direction)));
		this.dy = (length * MathUtils.sin(MathUtils
				.toRadians(direction)));
	}

	public void setDirection(float direction) {
		this.direction = direction;
		this.dx = (this.length * MathUtils.cos(MathUtils
				.toRadians(direction)));
		this.dy = (this.length * MathUtils.sin(MathUtils
				.toRadians(direction)));
	}

	public void add(Speed other) {
		this.dx += other.dx;
		this.dy += other.dy;
		this.direction = (int) MathUtils.toDegrees(MathUtils.atan2(this.dy,
				this.dx));
		this.length = MathUtils.sqrt(this.dx * this.dx + this.dy
				* this.dy);
	}

	public float getX() {
		return this.dx;
	}

	public float getY() {
		return this.dy;
	}

	public float getDirection() {
		return this.direction;
	}

	public float getLength() {
		return this.length;
	}

	public Speed copy() {
		Speed copy = new Speed();
		copy.dx = this.dx;
		copy.dy = this.dy;
		copy.direction = this.direction;
		copy.length = this.length;
		return copy;
	}
}
