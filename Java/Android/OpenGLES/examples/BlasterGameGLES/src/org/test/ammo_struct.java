package org.test;

public final class ammo_struct
{
	public String name;
	public int shots;
	public shot_struct[] shot;
	public float shots_counted;
	public int tile;
	public int reload;
	public int sound;

	public ammo_struct clone()
	{
		ammo_struct varCopy = new ammo_struct();

		varCopy.name = this.name;
		varCopy.shots = this.shots;
		varCopy.shot = this.shot.clone();
		varCopy.shots_counted = this.shots_counted;
		varCopy.tile = this.tile;
		varCopy.reload = this.reload;
		varCopy.sound = this.sound;

		return varCopy;
	}
}