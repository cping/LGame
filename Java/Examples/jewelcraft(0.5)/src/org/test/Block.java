package org.test;

import loon.LTexture;
import loon.geom.Vector2f;

public class Block
{
	public int alpha;
	public java.util.ArrayList<LTexture> backgroundAnimationImages;
	public int backgroundAnimationIndex;
	public float breakDelay;
	public int color;
	public float delay;
	public LTexture image;
	public boolean isMoving;
	public int item;
	public int newTop;
	public Vector2f position;
	public Vector2f position2;
	public int state;
	public Vector2f velocity;

	public Block(Vector2f position)
	{
		this.position = position;
		this.alpha = 1;
	}

	public final void setBreakState()
	{
		if ((this.color != -1) && (this.color < 100))
		{
			this.state = 3;
			this.delay = 0.3f;
			if (this.item == 0)
			{
				this.alpha = 0;
			}
		}
	}

	public final void transition(float dt)
	{
		if (this.isMoving)
		{
			if (this.state == 40)
			{
				this.velocity = new Vector2f(Math.min((float) 300f, (float)(this.velocity.x * 1.4f)), Math.min((float) 300f, (float)(this.velocity.y * 1.4f)));
				this.position = new Vector2f(this.position.x + (this.velocity.x * 0.1f), this.position.y + (this.velocity.y * 0.1f));
			}
			else
			{
				this.velocity = new Vector2f(this.velocity.x, this.velocity.y);
				this.position = new Vector2f(this.position.x + this.velocity.x, this.position.y + this.velocity.y);
			}
			if (((this.velocity.x * (this.position2.x - this.position.x)) < 0f) || ((this.velocity.y * (this.position2.y - this.position.y)) < 0f))
			{
				this.position = this.position2;
				this.position2 = Vector2f.ZERO();
				this.isMoving = false;
			}
		}
	}
}