package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.RectBox;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;

public class WinMenu {
	
	public LTexture background;
	
	private LColor colorC;
	
	private LColor colorP;
	
	public LFont font;
	
	public RectBox MenuEntry_NEXT = new RectBox();
	
	public RectBox MenuEntry_QUIT = new RectBox();
	
	public RectBox MenuEntry_UPDATECASTLE = new RectBox();
	
	public RectBox MenuEntry_UPDATEPLAYER = new RectBox();
	
	public LTexture winTitle;

	public final void cannotUpdate(int cho) {
		if (cho == 0) {
			this.colorP = new LColor(80, 80, 80, 150);
		}
		if (cho == 1) {
			this.colorC = new LColor(80, 80, 80, 150);
		}
	}

	public final void canUpdate(int cho) {
		if (cho == 0) {
			this.colorP = LColor.white;
		}
		if (cho == 1) {
			this.colorC = LColor.white;
		}
	}

	public final void Draw(SpriteBatch batch) {
		batch.draw(this.background, 20f, 20f, LColor.white);
		batch.draw(this.winTitle, 120f, 100f);
		batch.drawString(this.font, "Update Weapen",
				this.MenuEntry_UPDATEPLAYER.x, this.MenuEntry_UPDATEPLAYER.y,
				this.colorP);
		batch.drawString(this.font, "Update Castle",
				this.MenuEntry_UPDATECASTLE.x, this.MenuEntry_UPDATECASTLE.y,
				this.colorC);
		batch.drawString(this.font, "NEXT LEVEL", this.MenuEntry_NEXT.x,
				this.MenuEntry_NEXT.y, LColor.white);
		batch.drawString(this.font, "QUIT GAME", this.MenuEntry_QUIT.x,
				this.MenuEntry_QUIT.y, LColor.white);
	}

	public final void Initialize() {
		this.background = LTextures.loadTexture("assets/blur.png");
		this.winTitle = LTextures.loadTexture("assets/stageclear.png");
		this.MenuEntry_UPDATEPLAYER.setBounds(140, 240, 210, 30);
		this.MenuEntry_UPDATECASTLE.setBounds(450, 240, 190, 30);
		this.MenuEntry_NEXT.setBounds(330, 320, 140, 30);
		this.MenuEntry_QUIT.setBounds(330, 400, 140, 30);
		this.font = LFont.getFont("黑体", 1,28);
		this.colorP = LColor.white;
		this.colorC = LColor.white;
	}

	public final boolean Tap_MenuEntry_NEXT(float posX, float posY) {
		boolean flag;
		if ((((posX >= this.MenuEntry_NEXT.Left()) && (posX <= this.MenuEntry_NEXT
				.Right())) && (posY >= this.MenuEntry_NEXT.Top()))
				&& (posY <= this.MenuEntry_NEXT.Bottom())) {
			flag = true;
		} else {
			return false;
		}
		return flag;
	}

	public final boolean Tap_MenuEntry_QUIT(float posX, float posY) {
		boolean flag;
		if ((((posX >= this.MenuEntry_QUIT.Left()) && (posX <= this.MenuEntry_QUIT
				.Right())) && (posY >= this.MenuEntry_QUIT.Top()))
				&& (posY <= this.MenuEntry_QUIT.Bottom())) {
			flag = true;
		} else {
			return false;
		}
		return flag;
	}

	public final boolean Tap_MenuEntry_UPDATECASTLE(float posX, float posY) {
		boolean flag;
		if ((((posX >= this.MenuEntry_UPDATECASTLE.Left()) && (posX <= this.MenuEntry_UPDATECASTLE
				.Right())) && (posY >= this.MenuEntry_UPDATECASTLE.Top()))
				&& (posY <= this.MenuEntry_UPDATECASTLE.Bottom())) {
			flag = true;
		} else {
			return false;
		}
		return flag;
	}

	public final boolean Tap_MenuEntry_UPDATEPLAYER(float posX, float posY) {
		boolean flag;
		if ((((posX >= this.MenuEntry_UPDATEPLAYER.Left()) && (posX <= this.MenuEntry_UPDATEPLAYER
				.Right())) && (posY >= this.MenuEntry_UPDATEPLAYER.Top()))
				&& (posY <= this.MenuEntry_UPDATEPLAYER.Bottom())) {
			flag = true;
		} else {
			return false;
		}
		return flag;
	}
}