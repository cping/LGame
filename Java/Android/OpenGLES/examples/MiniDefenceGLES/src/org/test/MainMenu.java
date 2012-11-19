package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;

public class MainMenu
{
	public LTexture background;
	public LFont font;
	public LTexture help;
	public LTexture ismute;
	public RectBox MenuEntry_CHOOSELEVEL;
	public RectBox MenuEntry_EXIT;
	public RectBox MenuEntry_HELP;
	public RectBox MenuEntry_MUTE;
	public RectBox MenuEntry_PLAY;
	public LTexture notmute;
	public LTexture sound;

	public final void Draw(SpriteBatch batch)
	{
		batch.draw(this.background, Vector2f.Zero, LColor.white);
		batch.drawString(this.font, "NEW GAME",  this.MenuEntry_PLAY.x, this.MenuEntry_PLAY.y, LColor.white);
		batch.drawString(this.font, "CHOOSE LEVEL",  this.MenuEntry_CHOOSELEVEL.x,  this.MenuEntry_CHOOSELEVEL.y, LColor.white);
		batch.drawString(this.font, "EXIT",  this.MenuEntry_EXIT.x, this.MenuEntry_EXIT.y, LColor.white);
		batch.draw(this.sound,  this.MenuEntry_MUTE.x, (float) this.MenuEntry_MUTE.y, LColor.white);
		batch.draw(this.help, this.MenuEntry_HELP.x, (float) this.MenuEntry_HELP.y, LColor.white);
	}

	public final void Initialize()
	{
		this.background = LTextures.loadTexture("assets/mainmenu.png");
		this.ismute = LTextures.loadTexture("assets/ismute.png");
		this.notmute = LTextures.loadTexture("assets/notmute.png");
		this.help = LTextures.loadTexture("assets/help.png");
		this.sound = this.notmute;
		this.MenuEntry_PLAY = new RectBox(0x14f, 310, 0x87, 30);
		this.MenuEntry_CHOOSELEVEL = new RectBox(0x139, 370, 180, 30);
		this.MenuEntry_EXIT = new RectBox(0x177, 430, 60, 30);
		this.MenuEntry_MUTE = new RectBox(700, 400, 50, 50);
		this.MenuEntry_HELP = new RectBox(630, 400, 50, 50);
		this.font = LFont.getFont("黑体", 1,28);
	}

	public final void Ste_MenuEntry_EXIT(boolean isMute)
	{
		if (isMute)
		{
			this.sound = this.ismute;
		}
		else
		{
			this.sound = this.notmute;
		}
	}

	public final boolean Tap_MenuEntry_CHOOSELEVEL(float posX, float posY)
	{
		boolean flag;
		if ((((posX >= this.MenuEntry_CHOOSELEVEL.Left()) && (posX <= this.MenuEntry_CHOOSELEVEL.Right())) && (posY >= this.MenuEntry_CHOOSELEVEL.Top())) && (posY <= this.MenuEntry_CHOOSELEVEL.Bottom()))
		{
			flag = true;
		}
		else
		{
			return false;
		}
		return flag;
	}

	public final boolean Tap_MenuEntry_EXIT(float posX, float posY)
	{
		boolean flag;
		if ((((posX >= this.MenuEntry_EXIT.Left()) && (posX <= this.MenuEntry_EXIT.Right())) && (posY >= this.MenuEntry_EXIT.Top())) && (posY <= this.MenuEntry_EXIT.Bottom()))
		{
			flag = true;
		}
		else
		{
			return false;
		}
		return flag;
	}

	public final boolean Tap_MenuEntry_HELP(float posX, float posY)
	{
		boolean flag;
		if ((((posX >= this.MenuEntry_HELP.Left()) && (posX <= this.MenuEntry_HELP.Right())) && (posY >= this.MenuEntry_HELP.Top())) && (posY <= this.MenuEntry_HELP.Bottom()))
		{
			flag = true;
		}
		else
		{
			return false;
		}
		return flag;
	}

	public final boolean Tap_MenuEntry_MUTE(float posX, float posY)
	{
		boolean flag;
		if ((((posX >= this.MenuEntry_MUTE.Left()) && (posX <= this.MenuEntry_MUTE.Right())) && (posY >= this.MenuEntry_MUTE.Top())) && (posY <= this.MenuEntry_MUTE.Bottom()))
		{
			flag = true;
		}
		else
		{
			return false;
		}
		return flag;
	}

	public final boolean Tap_MenuEntry_PLAY(float posX, float posY)
	{
		boolean flag;
		if ((((posX >= this.MenuEntry_PLAY.Left()) && (posX <= this.MenuEntry_PLAY.Right())) && (posY >= this.MenuEntry_PLAY.Top())) && (posY <= this.MenuEntry_PLAY.Bottom()))
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