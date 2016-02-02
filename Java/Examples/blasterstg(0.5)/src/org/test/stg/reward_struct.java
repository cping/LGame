package org.test.stg;

public final class reward_struct
{
	public String text;
	public float weapon_power;
	public float weapon_speed;
	public float life;
	public float mitigate;
	public float drop_rate;
	public float score_increase;

	public reward_struct cpy()
	{
		reward_struct varCopy = new reward_struct();

		varCopy.text = this.text;
		varCopy.weapon_power = this.weapon_power;
		varCopy.weapon_speed = this.weapon_speed;
		varCopy.life = this.life;
		varCopy.mitigate = this.mitigate;
		varCopy.drop_rate = this.drop_rate;
		varCopy.score_increase = this.score_increase;

		return varCopy;
	}
}