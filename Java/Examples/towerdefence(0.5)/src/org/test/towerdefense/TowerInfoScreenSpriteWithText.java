package org.test.towerdefense;

import loon.action.sprite.SpriteBatch;
import loon.canvas.LColor;
import loon.font.LFont;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;
public class TowerInfoScreenSpriteWithText extends Sprite {

	private LFont font;

	public TowerInfoScreenSpriteWithText(MainGame game) {
		super(game, "assets/towers_2.png", 0, new Vector2f(0f, 0f));
		game.Components().add(this);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		int num = 0x1a;
		int num2 = 0x10;
		for (String str : LanguageResources.getTowerInfoPar1().split("[$]", -1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str, 99f, num,
					LColor.white);
			num += num2;
		}
		int num3 = 0x80;
		for (String str2 : LanguageResources.getTowerInfoPar2()
				.split("[$]", -1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str2, 99f, num3,
					LColor.white);
			num3 += num2;
		}
		int num4 = 0xe2;
		for (String str3 : LanguageResources.getTowerInfoPar3()
				.split("[$]", -1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str3, 99f, num4,
					LColor.white);
			num4 += num2;
		}
		int num5 = 0x146;
		for (String str4 : LanguageResources.getTowerInfoPar4()
				.split("[$]", -1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str4, 99f, num5,
					LColor.white);
			num5 += num2;
		}
		Utils.DrawStringAlignCenter(batch, this.font, LanguageResources
				.getBack().toUpperCase(), 169f, 435f, LColor.white);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.font = LFont.getFont(12);
	}
}