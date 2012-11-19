package org.test;

import loon.core.graphics.LColor;

public final class npc_struct
{
	public int active;
	public object_struct obj = new object_struct();
	public int ai;
	public int ai_way;
	public int ai_dir;
	public int ai_npc;
	public int ai_circle_entered;
	public float ai_speed;
	public float ai_circle_angle;
	public float ai_circle_distance;
	public int shoot_min;
	public int shoot_max;
	public int next_shot;
	public int shake;
	public float life;
	public float life_max;
	public float shield;
	public int shield_count;
	public int shielding;
	public int ammo;
	public int boss;
	public int solid;
	public int boost_ammo;
	public int boost_life;
	public int boost_tile;
	public int boost_power_ball;
	public int boost_label;
	public LColor boost_tile_color;
	public LColor boost_color;
	public int points;
	public float open_min;
	public float open_max;
	public int trail;
	public float mod;
	public int type;
	public int pause;
	public int end;
	public float accuracy;
	public int kill_time;

	public npc_struct clone()
	{
		npc_struct varCopy = new npc_struct();

		varCopy.active = this.active;
		varCopy.obj = this.obj.clone();
		varCopy.ai = this.ai;
		varCopy.ai_way = this.ai_way;
		varCopy.ai_dir = this.ai_dir;
		varCopy.ai_npc = this.ai_npc;
		varCopy.ai_circle_entered = this.ai_circle_entered;
		varCopy.ai_speed = this.ai_speed;
		varCopy.ai_circle_angle = this.ai_circle_angle;
		varCopy.ai_circle_distance = this.ai_circle_distance;
		varCopy.shoot_min = this.shoot_min;
		varCopy.shoot_max = this.shoot_max;
		varCopy.next_shot = this.next_shot;
		varCopy.shake = this.shake;
		varCopy.life = this.life;
		varCopy.life_max = this.life_max;
		varCopy.shield = this.shield;
		varCopy.shield_count = this.shield_count;
		varCopy.shielding = this.shielding;
		varCopy.ammo = this.ammo;
		varCopy.boss = this.boss;
		varCopy.solid = this.solid;
		varCopy.boost_ammo = this.boost_ammo;
		varCopy.boost_life = this.boost_life;
		varCopy.boost_tile = this.boost_tile;
		varCopy.boost_power_ball = this.boost_power_ball;
		varCopy.boost_label = this.boost_label;
		varCopy.boost_tile_color = this.boost_tile_color;
		varCopy.boost_color = this.boost_color;
		varCopy.points = this.points;
		varCopy.open_min = this.open_min;
		varCopy.open_max = this.open_max;
		varCopy.trail = this.trail;
		varCopy.mod = this.mod;
		varCopy.type = this.type;
		varCopy.pause = this.pause;
		varCopy.end = this.end;
		varCopy.accuracy = this.accuracy;
		varCopy.kill_time = this.kill_time;

		return varCopy;
	}
}