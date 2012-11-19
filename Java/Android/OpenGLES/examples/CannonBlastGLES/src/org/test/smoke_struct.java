package org.test;

public final class smoke_struct
{
	public int x;
	public int y;
	public int tile;
	public float alpha;
	public int fading;
	public int active;
	public float fx;
	public float fy;
	public float speed_x;
	public float speed_y;
	public float rot;
	public int dur;

	public smoke_struct clone()
	{
		smoke_struct varCopy = new smoke_struct();

		varCopy.x = this.x;
		varCopy.y = this.y;
		varCopy.tile = this.tile;
		varCopy.alpha = this.alpha;
		varCopy.fading = this.fading;
		varCopy.active = this.active;
		varCopy.fx = this.fx;
		varCopy.fy = this.fy;
		varCopy.speed_x = this.speed_x;
		varCopy.speed_y = this.speed_y;
		varCopy.rot = this.rot;
		varCopy.dur = this.dur;

		return varCopy;
	}
}