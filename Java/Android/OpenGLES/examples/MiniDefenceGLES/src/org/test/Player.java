package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.timer.GameTime;

public class Player
{
	public boolean Active;
	public int Health;
	public int Kills;
	public Animation PlayerAnimation;
	public Vector2f Position=new Vector2f();
	public int Score;

	public final void Draw(SpriteBatch spriteBatch)
	{
		this.PlayerAnimation.Draw(spriteBatch);
	}

	public final void Initialize(Animation animation, Vector2f position)
	{
		this.PlayerAnimation = animation;
		this.Position.set(position);
		this.Active = true;
		this.Health = 0;
		this.Kills = 0;
		this.Score = 0;
	}

	public final void Update(GameTime gameTime)
	{
		this.PlayerAnimation.Position.set(this.Position);
		this.PlayerAnimation.Update(gameTime);
	}

	public final int getHeight()
	{
		return this.PlayerAnimation.FrameHeight;
	}

	public final int getWidth()
	{
		return this.PlayerAnimation.FrameWidth;
	}
}