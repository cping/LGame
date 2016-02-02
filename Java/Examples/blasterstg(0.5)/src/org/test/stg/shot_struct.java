package org.test.stg;

public final class shot_struct
{
	public int active;
	public object_struct obj = new object_struct();
	public int by_player;
	public float damage;
	public int expend;
	public int target_npc;
	public int trail;
	public int bomb;
	public int bomb_ammo;
	public int last_npc;
	public int kills;

	public shot_struct cpy()
	{
		shot_struct varCopy = new shot_struct();

		varCopy.active = this.active;
		varCopy.obj = this.obj.cpy();
		varCopy.by_player = this.by_player;
		varCopy.damage = this.damage;
		varCopy.expend = this.expend;
		varCopy.target_npc = this.target_npc;
		varCopy.trail = this.trail;
		varCopy.bomb = this.bomb;
		varCopy.bomb_ammo = this.bomb_ammo;
		varCopy.last_npc = this.last_npc;
		varCopy.kills = this.kills;

		return varCopy;
	}
}