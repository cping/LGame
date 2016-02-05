package org.test;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.Vector2f;

public class CTowerIcons {
	private int count;
	private RectBox[] iconPos;
	private MainGame mainGame;
	private Vector2f origin;
	public Vector2f pos;
	private float scaleSize;
	private LTexture square;
	private String[] text;
	private Vector2f[] textPos;
	public LTexture[] textures;
	public boolean visible;

	public CTowerIcons(MainGame game) {
		this.mainGame = game;
		this.origin = new Vector2f();
	}

	public CTowerIcons(MainGame game, LTexture square) {
		this.mainGame = game;
		this.square = square;
		this.origin = new Vector2f();
	}

	public final int checkClick(Vector2f pos) {
		for (int i = 0; i < this.count; i++) {
			if (this.iconPos[i].intersects(
					(int) (pos.x + this.origin.x),
					(int) (pos.y + this.origin.y), 1, 1)) {
				return i;
			}
		}
		return -1;
	}

	public final int checkClick(int posx, int posy) {
		for (int i = 0; i < this.count; i++) {
			if (this.iconPos[i]
					.intersects(posx + ( this.origin.x), posy
							+ ( this.origin.y), 1, 1)) {
				return i;
			}
		}
		return -1;
	}

	public final void draw(SpriteBatch batch, LColor defaultSceneColor) {
		if (this.visible) {
			for (int i = 0; i < this.count; i++) {
				if (this.square != null) {
					batch.draw(this.square, this.pos, defaultSceneColor);
				}
				batch.draw(this.textures[i], this.iconPos[i], null,
						defaultSceneColor, 0f, this.origin, SpriteEffects.None);
				if (this.text[i] != null) {
					batch.drawString(this.mainGame.iconFont, this.text[i],
							this.textPos[i], defaultSceneColor, 0f,
							Vector2f.STATIC_ZERO, this.scaleSize);
				}
			}
		}
	}

	public final void hideIcons() {
		this.visible = false;
	}

	public final boolean isText(int index) {
		if (this.text[index] == null) {
			return false;
		}
		return true;
	}

	public final void setText(int index, String str) {
		this.text[index] = str;
		if (str != null) {
			if (str.length() > 3) {
				if (this.mainGame.scalePos.y > 1.5) {
					this.textPos[index].x = (this.iconPos[index].x + (6f * this.mainGame.scalePos.y))
							- this.origin.x;
				} else if (this.mainGame.scalePos.y > 1.0) {
					this.textPos[index].x = (this.iconPos[index].x + (26f * this.mainGame.scalePos.y))
							- this.origin.x;
				} else {
					this.textPos[index].x = (this.iconPos[index].x + (6f * this.mainGame.scalePos.y))
							- this.origin.x;
				}
			} else if (str.length() > 2) {
				this.textPos[index].x = (this.iconPos[index].x + 12f)
						- this.origin.x;
			} else if (this.mainGame.scalePos.y > 1.5) {
				this.textPos[index].x = (this.iconPos[index].x + (18f * this.mainGame.scalePos.y))
						- this.origin.x;
			} else if (this.mainGame.scalePos.y > 1.0) {
				this.textPos[index].x = (this.iconPos[index].x + (33f * this.mainGame.scalePos.y))
						- this.origin.x;
			} else {
				this.textPos[index].x = (this.iconPos[index].x + (18f * this.mainGame.scalePos.y))
						- this.origin.x;
			}
			this.textPos[index].y = (this.iconPos[index].y + 32f)
					- this.origin.y;
		}
	}

	public final void showIcons(LTexture[] textures, int count, int tilex,
			int tiley) {
		this.visible = true;
		this.textures = textures;
		this.count = count;
		this.text = new String[count];
		this.textPos = new Vector2f[count];
		for (int i = 0; i < count; i++) {
			this.textPos[i] = new Vector2f();
		}
		int num2 = 0;
		int num3 = 0;
		this.pos = new Vector2f((float) tilex, (float) tiley);
		this.scaleSize = 0.1f;
		this.iconPos = new RectBox[count];
		for (int i = 0; i < iconPos.length; i++) {
			if (iconPos[i] == null) {
				iconPos[i] = new RectBox();
			}
		}
		tilex -= (int) this.mainGame.TILESIZE.x;
		switch (count) {
		case 1:
			num2 = tilex - ((int) this.mainGame.TILESIZE.x);
			num3 = tiley;
			break;

		case 2:
			num2 = tilex - ((int) this.mainGame.TILESIZE.x);
			num3 = tiley;
			break;

		default:
			num2 = tilex - ((int) this.mainGame.TILESIZE.x);
			num3 = tiley - ((int) this.mainGame.TILESIZE.y);
			break;
		}
		num2 += 5;
		int num4 = num2;
		int num5 = num3;
		for (int j = 0; j < count; j++) {
			this.text[j] = null;
			this.iconPos[j].x = num4 += (int) this.mainGame.TILESIZE.x;
			if ((num4 == (tilex + 5)) && (num5 == tiley)) {
				num4 += (int) this.mainGame.TILESIZE.x;
			}
			this.iconPos[j].y = num5;
			if (num4 >= (tilex + (((int) this.mainGame.TILESIZE.x) * 2))) {
				num4 = num2;
				num5 += (int) this.mainGame.TILESIZE.y;
			}
			this.iconPos[j].height = (int) (textures[j].getHeight() * this.scaleSize);
			this.iconPos[j].width = (int) (textures[j].getWidth() * this.scaleSize);
			this.iconPos[j].x += (int) (this.mainGame.TILESIZE.x / 2f);
			this.iconPos[j].y += (int) (this.mainGame.TILESIZE.y / 2f);
		}
		this.origin.x = textures[0].getWidth() / 2;
		this.origin.y = textures[0].getHeight() / 2;
	}

	public final void update(float time) {
		if (this.visible && (this.scaleSize < 1f)) {
			this.scaleSize += time * 10f;
			if (this.scaleSize > 1f) {
				this.scaleSize = 1f;
				for (int i = 0; i < this.count; i++) {
					this.iconPos[i].height = this.textures[i].getHeight();
					this.iconPos[i].width = this.textures[i].getWidth();
				}
			} else {
				for (int j = 0; j < this.count; j++) {
					this.iconPos[j].height = (int) (this.textures[j]
							.getHeight() * this.scaleSize);
					this.iconPos[j].width = (int) (this.textures[j].getWidth() * this.scaleSize);
				}
			}
		}
	}
}