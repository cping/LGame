package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.geom.Vector2f;

public class TagGunner extends Button
{
	public TagGunner(LTexture buttonTexture, Vector2f position, float rotation, Help.ButtonID buttonID, int delayBeforeEffect)
	{
		super(buttonTexture, position, rotation, buttonID, delayBeforeEffect);
		super.description = "Free Gunner";
		super.subDescription = "Receive a free gunner \npermanently (1 allowed)";
		super.reqDescription = "Must be level 5 or up";
	}

	@Override
	public void ApplyEffect(Bunker player)
	{
		player.freeMercenary = new Weapon(ScreenLevelup.t2DSVT40, ScreenGameplay.rifleSound, new Vector2f(0f, 0f), "Rifleman", 8, 90, 5, 0x10, 0.03490658f, 180);
		player.isFreeMerAdded = false;
	}

	@Override
	public void CheckPrerequisite(Bunker player)
	{
		super.isPrerequisiteMet = true;
		for (Button button : player.skillsGained)
		{
			if (button.getButtonID() == Help.ButtonID.TagGunner)
			{
				super.isPrerequisiteMet = false;
				break;
			}
		}
		if (ScreenGameplay.level < 5)
		{
			super.isPrerequisiteMet = false;
		}
		for (int i = 0; i < player.skillsGained.size(); i++)
		{
			if (player.skillsGained.get(i).getButtonID() == Help.ButtonID.TagLearning)
			{
				return;
			}
			if (i == (player.skillsGained.size() - 1))
			{
				super.isPrerequisiteMet = false;
			}
		}
	}

	@Override
	public void Draw(SpriteBatch batch)
	{
		super.Draw(batch);
	}
}