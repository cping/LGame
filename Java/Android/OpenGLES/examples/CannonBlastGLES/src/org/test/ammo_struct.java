package org.test;

public final class ammo_struct
{
	public int tile;
	public float drop;
	public float drag;
	public float velocity;
	public int active;
	public int bounces;
	public int size;
	public int smoke;
	public int burst;
	public int wait;
	public int radius;
	public int power;
	public float accel;

	public ammo_struct clone()
	{
		ammo_struct varCopy = new ammo_struct();

		varCopy.tile = this.tile;
		varCopy.drop = this.drop;
		varCopy.drag = this.drag;
		varCopy.velocity = this.velocity;
		varCopy.active = this.active;
		varCopy.bounces = this.bounces;
		varCopy.size = this.size;
		varCopy.smoke = this.smoke;
		varCopy.burst = this.burst;
		varCopy.wait = this.wait;
		varCopy.radius = this.radius;
		varCopy.power = this.power;
		varCopy.accel = this.accel;

		return varCopy;
	}
}