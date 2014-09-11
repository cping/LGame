package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTexture;

public class TagOverRepair extends Button {
	public TagOverRepair(LTexture buttonTexture, Vector2f position,
			float rotation, Help.ButtonID buttonID, int delayBeforeEffect) {
		super(buttonTexture, position, rotation, buttonID, delayBeforeEffect);
		super.description = "Over Repair";
		super.subDescription = "Can repair beyond full \nstrength by 25%";
	}

	@Override
	public void ApplyEffect(Bunker player) {
		Help.barrierHMax += 25f;
	}

	@Override
	public void CheckPrerequisite(Bunker player) {
		super.isPrerequisiteMet = true;
	}

	@Override
	public void Draw(SpriteBatch batch) {
		super.Draw(batch);
	}
}