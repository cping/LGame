package org.test.stg;

import loon.utils.CollectionUtils;

public final class ai_struct
{
	public int ways;
	public int way;
	public int[] x_way;
	public int[] y_way;
	public float[] speed;
	public int[] hold;
	public float[] turn_speed;
	public int circle_npc;
	public float circle_x;
	public float circle_y;
	public float circle_angle;
	public float circle_distance;
	public float circle_speed;
	public float circle_entered;

	public ai_struct cpy()
	{
		ai_struct varCopy = new ai_struct();

		varCopy.ways = this.ways;
		varCopy.way = this.way;
		varCopy.x_way = CollectionUtils.copyOf(this.x_way);
		varCopy.y_way = CollectionUtils.copyOf(this.y_way);
		varCopy.speed = CollectionUtils.copyOf(this.speed);
		varCopy.hold = this.hold;
		varCopy.turn_speed = CollectionUtils.copyOf(this.turn_speed);
		varCopy.circle_npc = this.circle_npc;
		varCopy.circle_x = this.circle_x;
		varCopy.circle_y = this.circle_y;
		varCopy.circle_angle = this.circle_angle;
		varCopy.circle_distance = this.circle_distance;
		varCopy.circle_speed = this.circle_speed;
		varCopy.circle_entered = this.circle_entered;

		return varCopy;
	}
}