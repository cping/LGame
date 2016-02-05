package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.geom.Vector2f;

public class TagAim extends Button
{
	public TagAim(LTexture buttonTexture, Vector2f position, float rotation, Help.ButtonID buttonID, int delayBeforeEffect)
	{
		super(buttonTexture, position, rotation, buttonID, delayBeforeEffect);
		super.description = "Improve Accuracy";
		super.subDescription = "Easier to get head shot";
	}

	@Override
	public void ApplyEffect(Bunker player)
	{
		player.AccMultiplier *= 0.7f;
		player.weapon.currentAccuracy = player.weapon.accuracy * player.AccMultiplier;
	}

	@Override
	public void CheckPrerequisite(Bunker player)
	{
		super.isPrerequisiteMet = true;
	}

	@Override
	public void Draw(SpriteBatch batch)
	{
		super.Draw(batch);
	}
}