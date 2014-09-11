package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;

public class LoadBoard extends DrawableObject {
	private GameSave data;
	private LFont myFont;

	public LoadBoard(LTexture t2DScoreBoard, Vector2f position, LFont myFont,
			GameSave data) {
		super(t2DScoreBoard, position);
		this.myFont = myFont;
		this.data = data;
	}

	@Override
	public void Draw(SpriteBatch batch) {
		super.Draw(batch);
		batch.drawString(this.myFont, (new Integer(this.data.day)).toString(),
				305f, 163f, LColor.wheat, 0f, this.myFont
						.stringWidth((new Integer(this.data.day)).toString()),
				0f, 1f);

		batch.drawString(this.myFont,
				(new Integer(this.data.level)).toString(), 305f, 215f,
				LColor.wheat, 0f, this.myFont.stringWidth((new Integer(
						this.data.level)).toString()), 0f, 1f);
		batch.drawString(this.myFont,
				(new Integer(this.data.money)).toString(), 305f, 267f,
				LColor.wheat, 0f, this.myFont.stringWidth((new Integer(
						this.data.money)).toString()), 0f, 1f);
		batch.drawString(this.myFont, this.data.weaponName, 570f, 280f,
				LColor.wheat, 0f,
				this.myFont.stringWidth(this.data.weaponName), 0f, 1f);
		for (DrawableObject weapon : ScreenLevelup.scrollablePane.itemList) {
			if (weapon instanceof Weapon) {
				Weapon o = (Weapon) weapon;
				if (o.name.equals(this.data.weaponName)) {
					batch.draw(o.texture, 485f, 200f, null, LColor.white, 0f,
							o.origin, 0.5f, SpriteEffects.None);
				}
			}
		}
		batch.drawString(Screen.ariel18,
				(new Integer(this.data.month)).toString() + "/"
						+ (new Integer(this.data.dayOfMonth)).toString() + "/"
						+ (new Integer(this.data.year)).toString(), 330f, 317f,
				LColor.wheat, 0f, 0f, 0f, 1f);
		batch.drawString(Screen.ariel18,
				(new Integer(this.data.hour)).toString() + ":"
						+ (new Integer(this.data.minute)).toString(), 505f,
				317f, LColor.wheat, 0f, 0f, 0f, 1f);
	}

	@Override
	public void Update() {
		super.Update();
	}
}