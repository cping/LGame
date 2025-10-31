package org.test.rtsgame;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.canvas.LColorPool;
import loon.events.SysKey;
import loon.events.SysTouch;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

//菜单显示用类
public abstract class MenuEntity extends GameEntity {

	private java.util.ArrayList<MenuEntry> menuEntries = new java.util.ArrayList<MenuEntry>();
	private int selectedEntry;
	protected Vector2f titlePosition = new Vector2f();
	protected float titleSize = 50f;
	protected String titleString;
	protected LTexture titleTexture;

	public MenuEntity() {
		super.setTransitionOnTime(0.5f);
		super.setTransitionOffTime(0.5f);
	}

	@Override
	public void Draw(SpriteBatch batch, GameTime gameTime) {
		if (this.titleTexture == null) {

			// 无图不显示

		} else {
			batch.draw(this.titleTexture, this.titlePosition,
					LColorPool.get().getColor(1f, 1f, 1f, super.getTransitionAlpha()));
		}
		for (int i = 0; i < this.menuEntries.size(); i++) {
			MenuEntry entry = this.menuEntries.get(i);
			boolean isSelected = super.getIsActive() && (i == this.selectedEntry);
			entry.Draw(isSelected, batch, gameTime);
		}

	}

	@Override
	public void HandleInput() {
		if (SysTouch.getOnlyKey().isPressed() && SysKey.isKeyPressed(SysKey.BACK)) {
			this.OnCancel();
		} else {
			if (SysTouch.isDown() || SysTouch.isDrag()) {
				for (int i = 0; i < this.menuEntries.size(); i++) {
					if (this.menuEntries.get(i).getGetMenuEntryHitBounds().contains(SysTouch.x(), SysTouch.y())) {
						this.selectedEntry = i;
						if (SysTouch.isDown() || SysTouch.isDrag()) {
							this.OnSelectEntry();
							break;
						}
					}
				}
			}
		}
	}

	protected void OnCancel() {
		super.ExitScreen();
	}

	protected void OnSelectEntry() {
		this.menuEntries.get(this.selectedEntry).OnSelectEntry();
	}

	@Override
	public void Update(GameTime gameTime, boolean coveredByOtherScreen) {
		super.Update(gameTime, coveredByOtherScreen);
		for (int i = 0; i < this.menuEntries.size(); i++) {
			boolean isSelected = super.getIsActive() && (i == this.selectedEntry);
			this.menuEntries.get(i).Update(isSelected, gameTime);
		}
	}

	protected final java.util.List<MenuEntry> getMenuEntries() {
		return this.menuEntries;
	}
}