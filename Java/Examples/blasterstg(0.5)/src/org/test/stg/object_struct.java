package org.test.stg;

import loon.canvas.LColor;

public final class object_struct
{
	public int active;
	public loc_struct loc = new loc_struct();
	public loc_struct dest = new loc_struct();
	public int tile;
	public int tile_top;
	public LColor top_color;
	public int cx;
	public int cy;
	public String text;

	public object_struct cpy()
	{
		object_struct varCopy = new object_struct();

		varCopy.active = this.active;
		varCopy.loc = this.loc.cpy();
		varCopy.dest = this.dest.cpy();
		varCopy.tile = this.tile;
		varCopy.tile_top = this.tile_top;
		varCopy.top_color = this.top_color;
		varCopy.cx = this.cx;
		varCopy.cy = this.cy;
		varCopy.text = this.text;

		return varCopy;
	}
}