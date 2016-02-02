package org.test.stg;

import loon.utils.CollectionUtils;

public final class tile_struct
{
	public int texture;
	public int x;
	public int y;
	public int cx;
	public int cy;
	public int w;
	public int h;
	public int frames;
	public int[] frame;
	public int clicked;

	public tile_struct cpy()
	{
		tile_struct varCopy = new tile_struct();

		varCopy.texture = this.texture;
		varCopy.x = this.x;
		varCopy.y = this.y;
		varCopy.cx = this.cx;
		varCopy.cy = this.cy;
		varCopy.w = this.w;
		varCopy.h = this.h;
		varCopy.frames = this.frames;
		varCopy.frame = CollectionUtils.copyOf(this.frame);
		varCopy.clicked = this.clicked;

		return varCopy;
	}
}