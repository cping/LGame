package org.test.act;

public final class block_struct
{
	public int[] tile;
	public int active;
	public int hits;

	public block_struct cpy()
	{
		block_struct varCopy = new block_struct();

		varCopy.tile = this.tile;
		varCopy.active = this.active;
		varCopy.hits = this.hits;

		return varCopy;
	}
}