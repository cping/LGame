
package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.timer.GameTime;

public class WinScreenSpriteWithText extends Sprite {

	private LFont font;
	private LFont fontHeader;

	public WinScreenSpriteWithText(MainGame game) {
		super(game, "assets/win.png", 0, new Vector2f(0f, 0f));
		game.Components().add(this);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		Utils.DrawStringAlignCenter(batch, this.fontHeader,
				LanguageResources.getWinHeader(), 164f, 51f, LColor.white);
		int num = 0x53;
		for (String str : LanguageResources.getWinPar1().split("[$]")) {
			Utils.DrawStringAlignLeft(batch, this.font, str, new Vector2f(34f,
					(float) num), LColor.white);
			num += 20;
		}
		String text = LanguageResources.getMenu().toUpperCase();
		Utils.DrawStringAlignCenter(batch, this.font, text, 160f, 400f,
				LColor.white);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.font = LFont.getFont(16);
		this.fontHeader = LFont.getFont(26);
	}
}