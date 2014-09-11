package org.test;

import loon.core.graphics.LColor;
import loon.utils.CollectionUtils;

public final class button_struct
{
	public int active;
	public int type;
	public int val;
	public loc_struct loc = new loc_struct();
	public loc_struct dest = new loc_struct();
	public int clicked;
	public int tiles;
	public int[] tile;
	public LColor[] tile_color;
	public String text_tile;
	public String text_label;
	public int locked;

	public button_struct clone()
	{
		button_struct varCopy = new button_struct();

		varCopy.active = this.active;
		varCopy.type = this.type;
		varCopy.val = this.val;
		varCopy.loc = this.loc.clone();
		varCopy.dest = this.dest.clone();
		varCopy.clicked = this.clicked;
		varCopy.tiles = this.tiles;
		varCopy.tile = CollectionUtils.copyOf(this.tile);
		varCopy.tile_color =this.tile_color;
		varCopy.text_tile = this.text_tile;
		varCopy.text_label = this.text_label;
		varCopy.locked = this.locked;

		return varCopy;
	}
}