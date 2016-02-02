package org.test.stg;

import loon.canvas.LColor;

public final class loc_struct
{
	public int dest_reached;
	public float fx;
	public float fy;
	public float fw;
	public float fh;
	public int x;
	public int y;
	public int w;
	public int h;
	public float sw;
	public float sh;
	public int alpha;
	public float scale;
	public float dur;
	public int hold;
	public LColor color;
	public float rot;
	public float speed;
	public float speed_up;
	public float speed_min;
	public float slow_dist;
	public float turn_speed;
	public int show_rot;
	public float spin;
	public float spin_speed;
	public int lock_rot;
	public int alpha_hold;
	public int scale_hold;

	public loc_struct cpy()
	{
		loc_struct varCopy = new loc_struct();

		varCopy.dest_reached = this.dest_reached;
		varCopy.fx = this.fx;
		varCopy.fy = this.fy;
		varCopy.fw = this.fw;
		varCopy.fh = this.fh;
		varCopy.x = this.x;
		varCopy.y = this.y;
		varCopy.w = this.w;
		varCopy.h = this.h;
		varCopy.sw = this.sw;
		varCopy.sh = this.sh;
		varCopy.alpha = this.alpha;
		varCopy.scale = this.scale;
		varCopy.dur = this.dur;
		varCopy.hold = this.hold;
		varCopy.color = this.color;
		varCopy.rot = this.rot;
		varCopy.speed = this.speed;
		varCopy.speed_up = this.speed_up;
		varCopy.speed_min = this.speed_min;
		varCopy.slow_dist = this.slow_dist;
		varCopy.turn_speed = this.turn_speed;
		varCopy.show_rot = this.show_rot;
		varCopy.spin = this.spin;
		varCopy.spin_speed = this.spin_speed;
		varCopy.lock_rot = this.lock_rot;
		varCopy.alpha_hold = this.alpha_hold;
		varCopy.scale_hold = this.scale_hold;

		return varCopy;
	}
}