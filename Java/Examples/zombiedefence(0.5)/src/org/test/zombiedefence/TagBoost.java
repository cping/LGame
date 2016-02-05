package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.geom.Vector2f;

public class TagBoost extends Button
{
	public TagBoost(LTexture buttonTexture, Vector2f position, float rotation, Help.ButtonID buttonID, int delayBeforeEffect)
	{
		super(buttonTexture, position, rotation, buttonID, delayBeforeEffect);
		super.description = "Boost";
		super.subDescription = "Faster cool-down between \nartillery fires x0.8";
		super.reqDescription = "";
	}

	@Override
	public void ApplyEffect(Bunker player)
	{
		player.artilleryCoolDown = (int)(player.artilleryCoolDown * 0.8);
	}

	@Override
	public void CheckPrerequisite(Bunker player)
	{
		super.isPrerequisiteMet = true;
		if (ScreenGameplay.level < 5)
		{
			super.isPrerequisiteMet = false;
		}
	}

	@Override
	public void Draw(SpriteBatch batch)
	{
		super.Draw(batch);
	}
}