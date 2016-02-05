package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.geom.Vector2f;

public class BloodStain extends DrawableObject
{
	public BloodStain(LTexture t2DBloodStain, Vector2f position, float angle)
	{
		super(t2DBloodStain, position);
		super.angle = angle;
		super.life = 0x2d;
		super.scale = new Vector2f(0.8f, 0.8f);
		super.origin = new Vector2f((float)(t2DBloodStain.getWidth() / 2), (float)(t2DBloodStain.getHeight() / 2));
	}

	@Override
	public void Draw(SpriteBatch batch)
	{
		super.Draw(batch);
	}

	@Override
	public void Update()
	{
		super.scale.addSelf(1.7f / ((float) super.life), 1.7f / ((float) super.life));
		super.alpha -= 1f / ((float) super.life);
		super.Update();
	}
}