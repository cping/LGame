package org.test.act;

public final class tile_struct
{
	public int x;
	public int y;
	public int w;
	public int h;

	public tile_struct cpy()
	{
		tile_struct varCopy = new tile_struct();

		varCopy.x = this.x;
		varCopy.y = this.y;
		varCopy.w = this.w;
		varCopy.h = this.h;

		return varCopy;
	}
}