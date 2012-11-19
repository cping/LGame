package org.test;

public final class shot_struct
{
	public int x;
	public int y;
	public int tile_x;
	public int tile_y;
	public int ammo;
	public int active;
	public float fx;
	public float fy;
	public float start_angle;
	public float angle;
	public float rot;
	public int bounces;
	public float speed;
	public float lift;
	public float drag;
	public int mx;
	public int my;
	public int wait;
	public int start_x;
	public int start_y;
	public int frames;
	public int source;
	public float accel;

	public shot_struct clone()
	{
		shot_struct varCopy = new shot_struct();

		varCopy.x = this.x;
		varCopy.y = this.y;
		varCopy.tile_x = this.tile_x;
		varCopy.tile_y = this.tile_y;
		varCopy.ammo = this.ammo;
		varCopy.active = this.active;
		varCopy.fx = this.fx;
		varCopy.fy = this.fy;
		varCopy.start_angle = this.start_angle;
		varCopy.angle = this.angle;
		varCopy.rot = this.rot;
		varCopy.bounces = this.bounces;
		varCopy.speed = this.speed;
		varCopy.lift = this.lift;
		varCopy.drag = this.drag;
		varCopy.mx = this.mx;
		varCopy.my = this.my;
		varCopy.wait = this.wait;
		varCopy.start_x = this.start_x;
		varCopy.start_y = this.start_y;
		varCopy.frames = this.frames;
		varCopy.source = this.source;
		varCopy.accel = this.accel;

		return varCopy;
	}
}