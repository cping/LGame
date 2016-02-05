package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.geom.Vector2f;

public class TagLevelUp extends DrawableObject
{
	public TagLevelUp(LTexture texture, Vector2f position)
	{
		super(texture, position);
		super.life = 90;
	}

	@Override
	public void Draw(SpriteBatch batch)
	{
		super.Draw(batch);
	}

	@Override
	public void Update()
	{
		super.Update();
		this.position.y--;
		super.alpha -= 0.02f;
		if (this.position.y <= 100f)
		{
			super.isDead = true;
		}
	}
}