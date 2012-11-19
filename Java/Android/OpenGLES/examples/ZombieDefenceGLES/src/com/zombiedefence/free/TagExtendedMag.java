package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTexture;

public class TagExtendedMag extends Button
{
	public TagExtendedMag(LTexture buttonTexture, Vector2f position, float rotation, Help.ButtonID buttonID, int delayBeforeEffect)
	{
		super(buttonTexture, position, rotation, buttonID, delayBeforeEffect);
		super.description = "Extended Magazine";
		super.subDescription = "Allows 30% more bullets \nto be loaded";
		super.reqDescription = "Must be level 5 or up";
	}

	@Override
	public void ApplyEffect(Bunker player)
	{
		player.magSizeMultiplier += 0.3f;
		player.weapon.currentMagSize = (int)(player.weapon.magSize * player.magSizeMultiplier);
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