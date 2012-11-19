package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTexture;

public class FlashingSquare extends DrawableObject
{
	public FlashingSquare(LTexture texture, Vector2f position)
	{
		super(texture, position);
		super.alpha = 1f;
	}

	@Override
	public void Draw(SpriteBatch batch)
	{
	}

	@Override
	public void Update()
	{
		super.alpha -= 0.03f;
		if (super.alpha <= 0f)
		{
			super.isDead = true;
		}
		super.Update();
	}
}