package org.test.act;

import loon.geom.Vector2f;


public final class me_struct
{
	public int x;
	public int y;
	public int w;
	public int h;
	public Vector2f position=new Vector2f();
	public int tile_x;
	public int tile_y;
	public int tile;
	public int ammo;
	public float angle;
	public int active;
	public float speed_x;
	public float speed_y;
	public float tspeed_x;
	public float tspeed_y;
	public float trot;
	public float rot;
	public int shot_wait;
	public int hp;
	public int score;
	public int shots;
	public int hits;
	public int kills;
	public int accuracy;
	public int streak;
	public int shaking;

	public me_struct cpy()
	{
		me_struct varCopy = new me_struct();

		varCopy.x = this.x;
		varCopy.y = this.y;
		varCopy.w = this.w;
		varCopy.h = this.h;
		varCopy.position = this.position;
		varCopy.tile_x = this.tile_x;
		varCopy.tile_y = this.tile_y;
		varCopy.tile = this.tile;
		varCopy.ammo = this.ammo;
		varCopy.angle = this.angle;
		varCopy.active = this.active;
		varCopy.speed_x = this.speed_x;
		varCopy.speed_y = this.speed_y;
		varCopy.tspeed_x = this.tspeed_x;
		varCopy.tspeed_y = this.tspeed_y;
		varCopy.trot = this.trot;
		varCopy.rot = this.rot;
		varCopy.shot_wait = this.shot_wait;
		varCopy.hp = this.hp;
		varCopy.score = this.score;
		varCopy.shots = this.shots;
		varCopy.hits = this.hits;
		varCopy.kills = this.kills;
		varCopy.accuracy = this.accuracy;
		varCopy.streak = this.streak;
		varCopy.shaking = this.shaking;

		return varCopy;
	}
}