package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.geom.Vector2f;

public class TagFieldRepair extends Button
{
	public TagFieldRepair(LTexture buttonTexture, Vector2f position, float rotation, Help.ButtonID buttonID, int delayBeforeEffect)
	{
		super(buttonTexture, position, rotation, buttonID, delayBeforeEffect);
		super.description = "Field Repair";
		super.subDescription = "Carry out automatic repair \nwork during fight";
		super.reqDescription = "Must be level 5 or up";
	}

	@Override
	public void ApplyEffect(Bunker player)
	{
		player.fieldRepair += 0.03f;
	}

	@Override
	public void CheckPrerequisite(Bunker player)
	{
		super.isPrerequisiteMet = false;
		for (Button button : player.skillsGained)
		{
			if (button.getButtonID() == Help.ButtonID.TagOverRepair)
			{
				super.isPrerequisiteMet = true;
				break;
			}
		}
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