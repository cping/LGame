package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.timer.GameTime;

public class LoseScreenSpriteWithText extends Sprite {
	private LFont font;
	private LFont fontHeader;

	public LoseScreenSpriteWithText(MainGame game) {
		super(game, "assets/lose.png", 0, new Vector2f(0f, 0f));
		game.Components().add(this);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		Utils.DrawStringAlignCenter(batch, this.fontHeader, LanguageResources
				.getLoseHeader().toUpperCase(), 164f, 51f,
				LColor.white);
		int num = 0x23;
		int num2 = 0x65;
		for (String str : LanguageResources.getLosePar1().split("[$]", -1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str, 
					 num, num2, LColor.white);
			num2 += 20;
		}
		String text = LanguageResources.getMenu().toUpperCase();
		Utils.DrawStringAlignCenter(batch, this.font, text,160f,
				400f, LColor.white);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.font = LFont.getFont(16);
		this.fontHeader = LFont.getFont(38);
	}
}