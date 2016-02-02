package org.test.stg;

import loon.utils.CollectionUtils;

public final class float_number_struct
{
	public int active;
	public loc_struct loc = new loc_struct();
	public loc_struct dest = new loc_struct();
	public int center;
	public int number;
	public int count;
	public int[] num_tile;
	public float sw;
	public float sh;

	public float_number_struct cpy()
	{
		float_number_struct varCopy = new float_number_struct();

		varCopy.active = this.active;
		varCopy.loc = this.loc.cpy();
		varCopy.dest = this.dest.cpy();
		varCopy.center = this.center;
		varCopy.number = this.number;
		varCopy.count = this.count;
		varCopy.num_tile = CollectionUtils.copyOf(this.num_tile);
		varCopy.sw = this.sw;
		varCopy.sh = this.sh;

		return varCopy;
	}
}