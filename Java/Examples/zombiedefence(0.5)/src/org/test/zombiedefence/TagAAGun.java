package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.geom.Vector2f;

public class TagAAGun extends Button
{
	public TagAAGun(LTexture buttonTexture, Vector2f position, float rotation, Help.ButtonID buttonID, int delayBeforeEffect)
	{
		super(buttonTexture, position, rotation, buttonID, delayBeforeEffect);
		super.description = "AA Gun";
		super.subDescription = "Allows to buy and operate \na powerful anti-air gun \n(1 allowed)";
		super.reqDescription = "Must've learned 'Learner'";
	}

	@Override
	public void ApplyEffect(Bunker player)
	{
		if (!player.isAAGunUsable)
		{
			player.isAAGunUsable = true;
		}
	}

	@Override
	public void CheckPrerequisite(Bunker player)
	{
		super.isPrerequisiteMet = false;
		for (Button button : player.skillsGained)
		{
			if (button.getButtonID() == Help.ButtonID.TagLearning)
			{
				super.isPrerequisiteMet = true;
				break;
			}
		}
		for (Button button2 : player.skillsGained)
		{
			if (button2.getButtonID() == Help.ButtonID.TagAAGun)
			{
				super.isPrerequisiteMet = false;
				break;
			}
		}
	}

	@Override
	public void Draw(SpriteBatch batch)
	{
		super.Draw(batch);
	}
}