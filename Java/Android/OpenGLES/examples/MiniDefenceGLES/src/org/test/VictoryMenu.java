package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;

public class VictoryMenu {
	
	public LTexture background;
	
	public LFont font;
	
	public RectBox MenuEntry_BACK = new RectBox();

	public final void Draw(SpriteBatch spriteBatch) {
		spriteBatch.draw(this.background, Vector2f.Zero, LColor.white);
		spriteBatch.drawString(this.font, "You have finished all the levels ",
				200f, 310f, LColor.white);
		spriteBatch.drawString(this.font,
				"Thank you for playing my first game",165f, 350f,
				LColor.white);
		spriteBatch.drawString(this.font, "BACK", 
				this.MenuEntry_BACK.x,  this.MenuEntry_BACK.y,
				LColor.white);
	}

	public final void Initialize( ) {
		this.background = LTextures.loadTexture("assets/mainmenu.png");
		this.MenuEntry_BACK.setBounds(370, 430, 60, 30);
		this.font = LFont.getFont("黑体", 1,28);
	}

	public final boolean Tap_MenuEntry_BACK(float posX, float posY) {
		boolean flag;
		if ((((posX >= this.MenuEntry_BACK.Left()) && (posX <= this.MenuEntry_BACK.Right())) && (posY >= this.MenuEntry_BACK.Top()))
				&& (posY <= this.MenuEntry_BACK.Bottom())) {
			flag = true;
		} else {
			return false;
		}
		return flag;
	}
}