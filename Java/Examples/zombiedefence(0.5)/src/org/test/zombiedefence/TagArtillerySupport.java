package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.geom.Vector2f;

public class TagArtillerySupport extends Button {
	public TagArtillerySupport(LTexture buttonTexture, Vector2f position,
			float rotation, Help.ButtonID buttonID, int delayBeforeEffect) {
		super(buttonTexture, position, rotation, buttonID, delayBeforeEffect);
		super.description = "Artillery Support";
		super.subDescription = "Call for artillery \nsupport (5 hits) each \nlevelup +3 hits";
		super.reqDescription = "";
	}

	@Override
	public void ApplyEffect(Bunker player) {
		if (!player.isArtilleryEnabled) {
			player.isArtilleryEnabled = true;
		} else {
			player.numArtilleryHit += 3;
		}
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