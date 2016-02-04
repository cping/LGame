package org.test.act;

public final class target_struct
{
	public int x;
	public int y;
	public int w;
	public int h;
	public int speed_x;
	public int speed_y;
	public int tile_x;
	public int tile_y;
	public float fx;
	public float fy;
	public int tile;
	public int hp;
	public int active;
	public float lift;
	public float rot;
	public int air;
	public int explodes;

	public target_struct cpy()
	{
		target_struct varCopy = new target_struct();

		varCopy.x = this.x;
		varCopy.y = this.y;
		varCopy.w = this.w;
		varCopy.h = this.h;
		varCopy.speed_x = this.speed_x;
		varCopy.speed_y = this.speed_y;
		varCopy.tile_x = this.tile_x;
		varCopy.tile_y = this.tile_y;
		varCopy.fx = this.fx;
		varCopy.fy = this.fy;
		varCopy.tile = this.tile;
		varCopy.hp = this.hp;
		varCopy.active = this.active;
		varCopy.lift = this.lift;
		varCopy.rot = this.rot;
		varCopy.air = this.air;
		varCopy.explodes = this.explodes;

		return varCopy;
	}
}