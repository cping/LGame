package org.test;

public final class button_struct
{
	public int x;
	public int y;
	public int w;
	public int h;
	public int tile;
	public int active;
	public int ammo;
	public int select;
	public String text;
	public int icon;

	public button_struct clone()
	{
		button_struct varCopy = new button_struct();

		varCopy.x = this.x;
		varCopy.y = this.y;
		varCopy.w = this.w;
		varCopy.h = this.h;
		varCopy.tile = this.tile;
		varCopy.active = this.active;
		varCopy.ammo = this.ammo;
		varCopy.select = this.select;
		varCopy.text = this.text;
		varCopy.icon = this.icon;

		return varCopy;
	}
}