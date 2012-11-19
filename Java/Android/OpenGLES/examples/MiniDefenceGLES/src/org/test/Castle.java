package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.timer.GameTime;

public class Castle
{
	public boolean Active;
	public Animation CastleAnimation;
	public int Health;
	public int level;
	public Vector2f Position=new Vector2f();

	public final void Draw(SpriteBatch batch)
	{
		this.CastleAnimation.Draw(batch);
	}

	public final void Initialize(Animation animation, Vector2f position, int castleLevel)
	{
		this.CastleAnimation = animation;
		this.Position.set(position);
		this.Active = true;
		this.level = castleLevel;
		switch (this.level)
		{
			case 1:
				this.Health = 0x3e8;
				break;

			case 2:
				this.Health = 0x7d0;
				break;

			case 3:
				this.Health = 0xfa0;
				break;

			case 4:
				this.Health = 0x1b58;
				break;

			case 5:
				this.Health = 0x2710;
				break;
		}
	}

	public final void Update(GameTime gameTime)
	{
		this.CastleAnimation.Position.set(this.Position);
		this.CastleAnimation.Update(gameTime);
	}

	public final int getHeight()
	{
		return this.CastleAnimation.FrameHeight;
	}

	public final int getWidth()
	{
		return this.CastleAnimation.FrameWidth;
	}
}