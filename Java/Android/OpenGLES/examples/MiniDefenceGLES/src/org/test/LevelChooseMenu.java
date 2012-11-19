package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;

public class LevelChooseMenu {
	public LTexture background;
	private LColor color2;
	private LColor color3;
	private LColor color4;
	private LColor color5;
	public LFont font;
	public RectBox Level1;
	public RectBox Level2;
	public RectBox Level3;
	public RectBox Level4;
	public RectBox Level5;
	public RectBox MenuEntry_BACK;

	public final void checkSaveLevel(int levelSave) {
		switch (levelSave) {
		case 0:
			this.color2 = new LColor(80, 80, 80, 150);
			this.color3 = new LColor(80, 80, 80, 150);
			this.color4 = new LColor(80, 80, 80, 150);
			this.color5 = new LColor(80, 80, 80, 150);
			break;

		case 1:
			this.color2 = LColor.white;
			this.color3 = new LColor(80, 80, 80, 150);
			this.color4 = new LColor(80, 80, 80, 150);
			this.color5 = new LColor(80, 80, 80, 150);
			break;

		case 2:
			this.color2 = LColor.white;
			this.color3 = LColor.white;
			this.color4 = new LColor(80, 80, 80, 150);
			this.color5 = new LColor(80, 80, 80, 150);
			break;

		case 3:
			this.color2 = LColor.white;
			this.color3 = LColor.white;
			this.color4 = LColor.white;
			this.color5 = new LColor(80, 80, 80, 150);
			break;

		case 4:
			this.color2 = LColor.white;
			this.color3 = LColor.white;
			this.color4 = LColor.white;
			this.color5 = LColor.white;
			break;
		}
	}

	public final void draw(SpriteBatch batch) {
		batch.draw(this.background, Vector2f.Zero, LColor.white);
		batch.drawString(this.font, "LV1", new Vector2f((float) this.Level1.x,
				(float) this.Level1.y), LColor.white);
		batch.drawString(this.font, "LV2", new Vector2f((float) this.Level2.x,
				(float) this.Level2.y), this.color2);
		batch.drawString(this.font, "LV3", new Vector2f((float) this.Level3.x,
				(float) this.Level3.y), this.color3);
		batch.drawString(this.font, "LV4", new Vector2f((float) this.Level4.x,
				(float) this.Level4.y), this.color4);
		batch.drawString(this.font, "LV5", new Vector2f((float) this.Level5.x,
				(float) this.Level5.y), this.color5);
		batch.drawString(this.font, "BACK", new Vector2f(
				(float) this.MenuEntry_BACK.x, (float) this.MenuEntry_BACK.y),
				LColor.white);
	}

	public final void Initialize() {
		this.background = LTextures.loadTexture("assets/chooselevelmenu.png");
		this.Level1 = new RectBox(0x179, 90, 0x2d, 30);
		this.Level2 = new RectBox(0x179, 150, 0x2d, 30);
		this.Level3 = new RectBox(0x179, 210, 0x2d, 30);
		this.Level4 = new RectBox(0x179, 270, 0x2d, 30);
		this.Level5 = new RectBox(0x179, 330, 0x2d, 30);
		this.MenuEntry_BACK = new RectBox(370, 400, 60, 30);
		this.font = LFont.getFont(20);
		this.color2 = LColor.white;
		this.color3 = LColor.white;
		this.color4 = LColor.white;
		this.color5 = LColor.white;
	}

	public final boolean Tap_Level1(float posX, float posY) {
		boolean flag;
		if ((((posX >= this.Level1.Left()) && (posX <= this.Level1.Right())) && (posY >= this.Level1
				.Top())) && (posY <= this.Level1.Bottom())) {
			flag = true;
		} else {
			return false;
		}
		return flag;
	}

	public final boolean Tap_Level2(float posX, float posY) {
		boolean flag;
		if ((((posX >= this.Level2.Left()) && (posX <= this.Level2.Right())) && ((posY >= this.Level2
				.Top()) && (posY <= this.Level2.Bottom())))
				&& (this.color2.equals(LColor.white))) {
			flag = true;
		} else {
			return false;
		}
		return flag;
	}

	public final boolean Tap_Level3(float posX, float posY) {
		boolean flag;
		if ((((posX >= this.Level3.Left()) && (posX <= this.Level3.Right())) && ((posY >= this.Level3
				.Top()) && (posY <= this.Level3.Bottom())))
				&& (this.color3.equals(LColor.white))) {
			flag = true;
		} else {
			return false;
		}
		return flag;
	}

	public final boolean Tap_Level4(float posX, float posY) {
		boolean flag;
		if ((((posX >= this.Level4.Left()) && (posX <= this.Level4.Right())) && ((posY >= this.Level4
				.Top()) && (posY <= this.Level4.Bottom())))
				&& (this.color4.equals(LColor.white))) {
			flag = true;
		} else {
			return false;
		}
		return flag;
	}

	public final boolean Tap_Level5(float posX, float posY) {
		boolean flag;
		if ((((posX >= this.Level5.Left()) && (posX <= this.Level5.Right())) && ((posY >= this.Level5
				.Top()) && (posY <= this.Level5.Bottom())))
				&& (this.color5.equals(LColor.white))) {
			flag = true;
		} else {
			return false;
		}
		return flag;
	}

	public final boolean Tap_MenuEntry_BACK(float posX, float posY) {
		boolean flag;
		if ((((posX >= this.MenuEntry_BACK.Left()) && (posX <= this.MenuEntry_BACK
				.Right())) && (posY >= this.MenuEntry_BACK.Top()))
				&& (posY <= this.MenuEntry_BACK.Bottom())) {
			flag = true;
		} else {
			return false;
		}
		return flag;
	}
}