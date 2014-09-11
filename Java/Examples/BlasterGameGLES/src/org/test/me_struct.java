package org.test;

public final class me_struct
{
	public object_struct obj = new object_struct();
	public int shield;
	public float shield_rot;
	public int score;
	public int score_display;
	public int level;
	public int shake;
	public int ammo;
	public int shots;
	public int bomb;
	public float life;
	public float life_max;
	public float accuracy;
	public float shot_count;
	public float hits;
	public int streak;
	public int combo_group;
	public int combo;
	public int last_shot;
	public int power_ball;
	public int immune;
	public int dead;
	public int win;
	public float difficulty;
	public reward_struct rewards = new reward_struct();

	public me_struct clone()
	{
		me_struct varCopy = new me_struct();

		varCopy.obj = this.obj.clone();
		varCopy.shield = this.shield;
		varCopy.shield_rot = this.shield_rot;
		varCopy.score = this.score;
		varCopy.score_display = this.score_display;
		varCopy.level = this.level;
		varCopy.shake = this.shake;
		varCopy.ammo = this.ammo;
		varCopy.shots = this.shots;
		varCopy.bomb = this.bomb;
		varCopy.life = this.life;
		varCopy.life_max = this.life_max;
		varCopy.accuracy = this.accuracy;
		varCopy.shot_count = this.shot_count;
		varCopy.hits = this.hits;
		varCopy.streak = this.streak;
		varCopy.combo_group = this.combo_group;
		varCopy.combo = this.combo;
		varCopy.last_shot = this.last_shot;
		varCopy.power_ball = this.power_ball;
		varCopy.immune = this.immune;
		varCopy.dead = this.dead;
		varCopy.win = this.win;
		varCopy.difficulty = this.difficulty;
		varCopy.rewards = this.rewards.clone();

		return varCopy;
	}
}