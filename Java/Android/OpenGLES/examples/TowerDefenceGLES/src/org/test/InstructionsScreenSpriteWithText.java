package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.timer.GameTime;

public class InstructionsScreenSpriteWithText extends Sprite {
	private LFont font;
	private LFont fontBig;

	public InstructionsScreenSpriteWithText(MainGame game) {
		super(game, "assets/screen_introduction.png", 0, new Vector2f(0f, 0f));
		game.Components().add(this);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);

		int num = 6;
		int num2 = 6;
		Utils.DrawStringAlignLeft(batch, this.fontBig,
				LanguageResources.getInstructionsHeader1(), num, num2,
				LColor.white);
		num2 += 30;
		for (String str : LanguageResources.getInstructionsPar1().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str, num, num2,
					LColor.white);
			num2 += 0x11;
		}
		num2 += 6;
		Utils.DrawStringAlignLeft(batch, this.fontBig,
				LanguageResources.getInstructionsHeader2(), num, num2,
				LColor.white);
		num2 += 30;
		for (String str2 : LanguageResources.getInstructionsPar2().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str2, num, num2,
					LColor.white);
			num2 += 0x11;
		}
		num2 += 6;
		Utils.DrawStringAlignLeft(batch, this.fontBig,
				LanguageResources.getInstructionsHeader3(), num, num2,
				LColor.white);
		num2 += 30;
		for (String str3 : LanguageResources.getInstructionsPar3().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str3, num, num2,
					LColor.white);
			num2 += 0x11;
		}
		Utils.DrawStringAlignCenter(batch, this.font, LanguageResources
				.getTowers().toUpperCase(), 62f, 439f, LColor.white);
		Utils.DrawStringAlignCenter(batch, this.font, LanguageResources
				.getEnemies().toUpperCase(), 179f, 439f, LColor.white);
		Utils.DrawStringAlignCenter(batch, this.font, LanguageResources
				.getMenu().toUpperCase(), 278f, 439f, LColor.white);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.font = LFont.getFont(16);
		this.fontBig = LFont.getFont(20);
	}
}