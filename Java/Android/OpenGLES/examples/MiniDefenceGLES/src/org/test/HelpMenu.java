package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;

public class HelpMenu
{
	public LTexture background;
	public LFont font;
	public RectBox MenuEntry_BACK;
	public LFont smallfont;

	public final void Draw(SpriteBatch batch)
	{
		batch.draw(this.background, Vector2f.Zero, LColor.white);
		batch.drawString(this.font, "BACK", this.MenuEntry_BACK.x, (float) this.MenuEntry_BACK.y, LColor.white);
	}

	public final void Initialize()
	{
		this.background = LTextures.loadTexture("assets/helpmenu.png");
		this.MenuEntry_BACK = new RectBox(390, 400, 60, 30);
		this.font = LFont.getFont(20);
		this.smallfont = LFont.getFont(15);
	}

	public final boolean Tap_MenuEntry_BACK(float posX, float posY)
	{
		boolean flag;
		if ((((posX >= this.MenuEntry_BACK.Left()) && (posX <= this.MenuEntry_BACK.Right())) && (posY >= this.MenuEntry_BACK.Top())) && (posY <= this.MenuEntry_BACK.Bottom()))
		{
			flag = true;
		}
		else
		{
			return false;
		}
		return flag;
	}
}