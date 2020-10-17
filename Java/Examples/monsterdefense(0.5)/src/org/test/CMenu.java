package org.test;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.canvas.LColor;
import loon.events.LTouchLocation;
import loon.geom.Vector2f;
import loon.utils.MathUtils;

public class CMenu {
	private boolean bdisappear;
	private int count;
	public int currentItem;
	public boolean fullOpaqueness;
	private MainGame mainGame;
	public CMenuItem[] menuItem;
	public boolean ready;
	public int selectedItem;
	private float timer;
	public boolean visible;

	public CMenu(MainGame game, int icount) {
		this.count = icount;
		this.mainGame = game;
		this.menuItem = new CMenuItem[this.count];
		this.currentItem = 0;
		for (int i = 0; i < this.count; i++) {
			this.menuItem[i] = new CMenuItem();
			this.menuItem[i].alpha = 0f;
		}
		this.visible = true;
		this.timer = 0f;
		this.fullOpaqueness = false;
		this.selectedItem = -1;
	}

	public final void clickButton(int index) {
		if ((!this.bdisappear && !this.menuItem[index].skipItem)
				&& (this.menuItem[index].currentSelScale >= this.menuItem[index].selScale)) {
			this.selectedItem = index;
			CMenuItem item1 = this.menuItem[index];
			item1.value++;
			this.menuItem[index].value = (this.menuItem[index].value > (this.menuItem[index].count - 1)) ? 0
					: this.menuItem[index].value;
			this.menuItem[index].currentSelScale = 1f;
		}
	}

	public final void disappear() {
		this.bdisappear = true;
	}

	public final void draw(SpriteBatch batch, LColor defaultSceneColor) {
		float num = 0f;
		float num2 = 0f;
		for (int i = 0; i < this.count; i++) {
			if (!this.menuItem[i].skipItem) {
				LColor color = new LColor(defaultSceneColor);
				if (this.menuItem[i].alpha > 0f) {
					int v = (int) (color.getAlpha() * this.menuItem[i].alpha);
					color.setColor(defaultSceneColor.getRed(),
							defaultSceneColor.getGreen(),
							defaultSceneColor.getBlue(), v);
					batch.draw(
							this.menuItem[i].texture[this.menuItem[i].value],
							this.menuItem[i].pos, color);
					if (this.menuItem[i].currentSelScale < this.menuItem[i].selScale) {
						v = (int) (255f - ((color.getAlpha() * (1f / (this.menuItem[i].selScale - 1f))) * (this.menuItem[i].currentSelScale - 1f)));
						color.setColor(defaultSceneColor.getRed(),
								defaultSceneColor.getGreen(),
								defaultSceneColor.getBlue(), v);
						int index = this.menuItem[i].value - 1;
						if (index < 0) {
							index = this.menuItem[i].count - 1;
						}
						num = ((this.menuItem[i].pos.getWidth() * ((this.menuItem[i].currentSelScale + this.menuItem[i].currentItemScale) - 1f)) - this.menuItem[i].pos
								.getWidth()) / 2f;
						num2 = ((this.menuItem[i].pos.getHeight() * ((this.menuItem[i].currentSelScale + this.menuItem[i].currentItemScale) - 1f)) - this.menuItem[i].pos
								.getHeight()) / 2f;
						batch.draw(
								this.menuItem[i].texture[index],
								this.menuItem[i].pos.x - num,
								this.menuItem[i].pos.y - num2,
								null,
								color,
								0f,
								Vector2f.STATIC_ZERO,
								((this.menuItem[i].currentSelScale + this.menuItem[i].currentItemScale) - 1f),
								SpriteEffects.None);
					}
				}
			}
		}
	}

	public final void nextItem() {
		this.currentItem++;
		if (this.currentItem < this.count) {
			while (this.menuItem[this.currentItem].skipItem) {
				this.currentItem++;
				if (this.currentItem >= this.count) {
					this.currentItem = 0;
				}
			}
		} else {
			this.currentItem = 0;
		}
	}

	public final void prevItem() {
		this.currentItem--;
		if (this.currentItem >= 0) {
			while (this.menuItem[this.currentItem].skipItem) {
				this.currentItem--;
				if (this.currentItem < 0) {
					this.currentItem = this.count - 1;
				}
			}
		} else {
			this.currentItem = this.count - 1;
		}
	}

	public final void removeEntry(int item) {
		this.menuItem[item].skipItem = true;
	}

	public final void reset() {
		this.bdisappear = false;
		this.visible = true;
		this.fullOpaqueness = false;
		this.timer = 0f;
		for (int i = 0; i < this.count; i++) {
			this.menuItem[i].alpha = 0f;
			this.menuItem[i].currentSelScale = this.menuItem[i].selScale;
		}
		this.selectedItem = -1;
	}

	public final void setMenuItem(int item, Vector2f vec, LTexture tex,
			float selScale) {
		this.setMenuItem(item, vec, 1, new LTexture[] { tex }, selScale, 1f);
	}

	public final void setMenuItem(int item, Vector2f vec, LTexture tex,
			float selScale, float scaleTime) {
		this.setMenuItem(item, vec, 1, new LTexture[] { tex }, selScale,
				scaleTime);
	}

	public final void setMenuItem(int item, Vector2f vec, int count,
			LTexture[] tex, float selScale) {
		this.setMenuItem(item, vec, count, tex, selScale, 1f);
	}

	public final void setMenuItem(int item, Vector2f vec, int count,
			LTexture[] tex, float selScale, float scaleTime) {
		this.menuItem[item].texture = tex;
		this.menuItem[item].count = count;
		this.menuItem[item].currentSelScale = this.menuItem[item].selScale = selScale;
		this.menuItem[item].skipItem = false;
		this.menuItem[item].pos.x = vec.x;
		this.menuItem[item].pos.y = vec.y;
		this.menuItem[item].pos.width = this.menuItem[item].texture[0]
				.getWidth();
		this.menuItem[item].pos.height = this.menuItem[item].texture[0]
				.getHeight();
		this.menuItem[item].scaleTime = selScale / scaleTime;
		this.menuItem[item].currentItemScale = 1f;
		this.menuItem[item].noButtonButton = true;
	}

	public final void setPointPos(int x, int y) {
		if (!this.bdisappear && (x != -1)) {
			for (int i = 0; i < this.count; i++) {
				if (((!this.menuItem[i].skipItem && (x >= this.menuItem[i].pos.x)) && ((y >= this.menuItem[i].pos.y) && (x <= (this.menuItem[i].pos.x + this.menuItem[i].pos
						.getWidth()))))
						&& ((y <= (this.menuItem[i].pos.y + this.menuItem[i].pos
								.getHeight())) && (this.menuItem[i].currentSelScale >= this.menuItem[i].selScale))) {
					this.selectedItem = i;
					CMenuItem item1 = this.menuItem[i];
					item1.value++;
					this.menuItem[i].value = (this.menuItem[i].value > (this.menuItem[i].count - 1)) ? 0
							: this.menuItem[i].value;
					this.menuItem[i].currentSelScale = 1f;
				}
			}
		}
	}

	public final void update(float time) {
		this.visible = false;
		this.fullOpaqueness = true;
		this.ready = true;
		if (this.mainGame.currentToucheState.AnyTouch()
				&& !this.mainGame.previouseToucheState.AnyTouch()) {
			LTouchLocation location = this.mainGame.currentToucheState.get(0);
			LTouchLocation location2 = this.mainGame.currentToucheState.get(0);
			this.setPointPos(location.getPosition().x(), location2
					.getPosition().y());
		}
		for (int i = 0; i < this.count; i++) {
			if (!this.menuItem[i].skipItem) {
				if (this.bdisappear) {
					if (this.menuItem[i].alpha > 0f) {
						this.menuItem[i].alpha = this.timer - (i * 0.1f);
						this.visible = true;
					}
					this.fullOpaqueness = false;
				} else {
					if (this.menuItem[i].alpha < 1f) {
						this.menuItem[i].alpha = this.timer - (i * 0.1f);
						this.fullOpaqueness = false;
					}
					this.visible = true;
				}
				if (this.bdisappear) {
					this.timer -= time;
				} else {
					this.timer += time;
				}
				this.timer = MathUtils.clamp(this.timer, 0f, this.count - 1f);
				this.menuItem[i].alpha = MathUtils.clamp(
						this.menuItem[i].alpha, 0f, 1f);
				if (this.menuItem[i].currentSelScale < this.menuItem[i].selScale) {
					this.ready = false;
					CMenuItem item1 = this.menuItem[i];
					item1.currentSelScale += time * this.menuItem[i].scaleTime;
				}
			}
		}
	}
}