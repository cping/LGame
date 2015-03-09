package com.mygame;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.device.LColor;
import loon.core.graphics.device.LFont;
import loon.core.timer.GameTime;

public class ConfirmScreenSpriteWithText extends Sprite {

	private LFont font;

	private LFont fontStdHuge;

	public ConfirmScreenSpriteWithText(MainGame game) {
		super(game, "assets/shield.png", 0, new Vector2f(0f, 0f));
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		batch.drawString(this.fontStdHuge, LanguageResources.getAreYouSure(),
				80f, 160f, LColor.white);
		Utils.DrawStringAlignCenter(batch, this.font, LanguageResources
				.getYes().toUpperCase(), 80f, 240f, LColor.white);
		Utils.DrawStringAlignCenter(batch, this.font, LanguageResources.getNo()
				.toUpperCase(), 240f, 240f, LColor.white);
	}

	@Override
	protected void loadContent() {
		this.fontStdHuge = LFont.getFont(26);
		this.font = LFont.getFont(12);
		super.loadContent();
	}
}