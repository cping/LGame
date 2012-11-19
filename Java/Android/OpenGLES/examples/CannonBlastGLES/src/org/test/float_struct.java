package org.test;

import loon.core.graphics.LColor;

public final class float_struct
{
	public int active;
	public float fx;
	public float fy;
	public String text;
	public int dur;
	public int alpha;
	public LColor color;

	public float_struct clone()
	{
		float_struct varCopy = new float_struct();

		varCopy.active = this.active;
		varCopy.fx = this.fx;
		varCopy.fy = this.fy;
		varCopy.text = this.text;
		varCopy.dur = this.dur;
		varCopy.alpha = this.alpha;
		varCopy.color = this.color;

		return varCopy;
	}
}