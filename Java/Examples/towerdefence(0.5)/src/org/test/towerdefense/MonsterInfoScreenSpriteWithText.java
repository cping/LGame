package org.test.towerdefense;

import loon.action.sprite.SpriteBatch;
import loon.canvas.LColor;
import loon.font.LFont;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class MonsterInfoScreenSpriteWithText extends Sprite {

	private LFont font;

	public MonsterInfoScreenSpriteWithText(MainGame game) {
		super(game, "assets/screen_monsters.png", 0, new Vector2f(0f, 0f));
		game.Components().add(this);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		int num = 0x18;
		for (String str : LanguageResources.getMonsterInfoPar1().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str, 100f, num,
					LColor.white);
			num += 20;
		}
		int num2 = 0x60;
		for (String str2 : LanguageResources.getMonsterInfoPar2().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str2, 4f, num2,
					LColor.white);
			num2 += 20;
		}
		int num3 = 0x9a;
		for (String str3 : LanguageResources.getMonsterInfoPar3().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str3, 100f, num3,
					LColor.white);
			num3 += 20;
		}
		int num4 = 0xe0;
		for (String str4 : LanguageResources.getMonsterInfoPar4().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str4, 4f, num4,
					LColor.white);
			num4 += 20;
		}
		int num5 = 290;
		for (String str5 : LanguageResources.getMonsterInfoPar5().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str5, 100f, num5,
					LColor.white);
			num5 += 20;
		}
		int num6 = 0x162;
		for (String str6 : LanguageResources.getMonsterInfoPar6().split("[$]",
				-1)) {
			Utils.DrawStringAlignLeft(batch, this.font, str6, 4f, num6,
					LColor.white);
			num6 += 20;
		}
		Utils.DrawStringAlignCenter(batch, this.font, LanguageResources
				.getBack().toUpperCase(), 169f, 435f, LColor.white);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.font = LFont.getFont(16);
	}
}