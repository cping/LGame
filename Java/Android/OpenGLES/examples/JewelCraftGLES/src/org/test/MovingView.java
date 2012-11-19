package org.test;

import loon.core.geom.Vector2f;

public class MovingView
{
	public boolean isMoving;
	public Vector2f position;
	public Vector2f position2;
	public java.util.ArrayList<ImageView> subviews;
	public Vector2f velocity;

	public MovingView(Vector2f position)
	{
		this.position = position.cpy();
		this.subviews = new java.util.ArrayList<ImageView>();
	}

	public final void transition(float dt)
	{
		if (this.isMoving)
		{
			this.velocity = new Vector2f(this.velocity.x, this.velocity.y);
			this.position = new Vector2f(this.position.x + this.velocity.x, this.position.y + this.velocity.y);
			if (((this.velocity.x * (this.position2.x - this.position.x)) < 0f) || ((this.velocity.y * (this.position2.y - this.position.y)) < 0f))
			{
				this.position = this.position2;
				this.position2 = Vector2f.Zero;
				this.isMoving = false;
			}
		}
	}
}