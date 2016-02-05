package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.geom.Vector2f;
import loon.utils.MathUtils;

public class ArtilleryShell extends DrawableObject
{
	private int hitY;
	public boolean isExploding;
	private int speedY;

	public ArtilleryShell(LTexture t2DShell, Vector2f position)
	{
		super(t2DShell, position);
		this.speedY = 30;
		this.hitY = ((int)(MathUtils.random() * 250f)) + 200;
		this.isExploding = false;
	}

	@Override
	public void Draw(SpriteBatch batch)
	{
		super.Draw(batch);
	}

	@Override
	public void Update()
	{
		this.position.y += this.speedY;
		if (this.position.y >= this.hitY)
		{
			this.isExploding = true;
		}
		super.Update();
	}
}