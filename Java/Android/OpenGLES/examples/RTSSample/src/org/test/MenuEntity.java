package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColorPool;
import loon.core.graphics.opengl.LTexture;
import loon.core.input.LInputFactory;
import loon.core.input.LInputFactory.Key;
import loon.core.input.LInputFactory.Touch;
import loon.core.timer.GameTime;

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

			//无图不显示
			
		} else {
			batch.draw(this.titleTexture, this.titlePosition, LColorPool.$().getColor(1f,
					1f, 1f, super.getTransitionAlpha()));
		}
		for (int i = 0; i < this.menuEntries.size(); i++) {
			MenuEntry entry = this.menuEntries.get(i);
			boolean isSelected = super.getIsActive()
					&& (i == this.selectedEntry);
			entry.Draw(isSelected, batch, gameTime);
		}

	}

	@Override
	public void HandleInput() {
		if (LInputFactory.getOnlyKey().isPressed()
				&& Key.isKeyPressed(Key.BACK)) {
			this.OnCancel();
		} else {
			if (Touch.isDown() || Touch.isDrag()) {
				for (int i = 0; i < this.menuEntries.size(); i++) {
					if (this.menuEntries.get(i).getGetMenuEntryHitBounds()
							.contains(Touch.x(), Touch.y())) {
						this.selectedEntry = i;
						if (Touch.isDown() || Touch.isDrag()) {
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
			boolean isSelected = super.getIsActive()
					&& (i == this.selectedEntry);
			this.menuEntries.get(i).Update(isSelected, gameTime);
		}
	}

	protected final java.util.List<MenuEntry> getMenuEntries() {
		return this.menuEntries;
	}
}