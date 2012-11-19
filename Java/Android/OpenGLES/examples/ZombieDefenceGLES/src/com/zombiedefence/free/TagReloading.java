package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTexture;

public class TagReloading extends Button
{
	public TagReloading(LTexture buttonTexture, Vector2f position, float rotation, Help.ButtonID buttonID, int delayBeforeEffect)
	{
		super(buttonTexture, position, rotation, buttonID, delayBeforeEffect);
		super.description = "Faster Reloading";
		super.subDescription = " ";
	}

	@Override
	public void ApplyEffect(Bunker player)
	{
		player.reloadingTimeMultiplier *= 0.9f;
		player.weapon.currentReloadLength = (int)(player.reloadingTimeMultiplier * player.weapon.reloadLength);
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