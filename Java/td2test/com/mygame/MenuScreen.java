package com.mygame;

import java.util.Iterator;

import loon.LSystem;
import loon.action.sprite.SpriteBatch;
import loon.canvas.LColor;
import loon.event.LTouchCollection;
import loon.event.LTouchLocation;
import loon.event.LTouchLocationState;
import loon.event.SysInput;
import loon.event.SysKey;
import loon.event.SysTouch;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.LIterator;
import loon.utils.timer.GameTime;

public abstract class MenuScreen extends GameScreen {

	private MainGame game;

	private java.util.ArrayList<MenuEntry> menuEntries = new java.util.ArrayList<MenuEntry>();

	private String menuTitle;

	public ScreenType prevScreen;

	private int selectedEntry;

	public MenuScreen(String menuTitle, MainGame game, ScreenType prevScreen) {
		this.prevScreen = prevScreen;
		this.game = game;
		this.menuTitle = menuTitle;
		super.setTransitionOnTime(0.5f);
		super.setTransitionOffTime(0.5f);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		for (int i = 0; i < this.menuEntries.size(); i++) {
			MenuEntry entry = this.menuEntries.get(i);
			boolean isSelected = super.getIsActive()
					&& (i == this.selectedEntry);
			entry.Draw(batch, this, isSelected, gameTime);
		}
		float num2 = (float) Math.pow((double) super.getTransitionPosition(),
				2.0);
		Vector2f position = new Vector2f();
		Vector2f origin = new Vector2f(batch.getFont().stringWidth(
				this.menuTitle) / 2f);
		LColor color = PoolColor.getColor(0xc0 * super.getTransitionAlpha(),
				0xc0 * super.getTransitionAlpha(),
				0xc0 * super.getTransitionAlpha(),
				0xc0 * super.getTransitionAlpha());
		float scale = 1.25f;
		position.y -= num2 * 100f;
		batch.drawString(batch.getFont(), this.menuTitle, position, color, 0f,
				origin, scale);
	}

	protected RectBox GetMenuEntryHitBounds(MenuEntry entry) {
		return entry.getBounds();
	}

	@Override
	public void HandleInput(GameTime gameTime, SysInput input) {
		if (SysKey.isKeyPressed(SysKey.BACK)) {
			this.OnCancel();
		}
		LTouchCollection collection = SysTouch.getTouchState();
		if (collection.size() > 0) {
			for (LIterator<LTouchLocation> it = collection.listIterator(); it
					.hasNext();) {
				LTouchLocation touch = it.next();
				if (touch.getPrevState() == LTouchLocationState.Pressed) {
					for (int i = 0; i < this.menuEntries.size(); i++) {
						MenuEntry entry = this.menuEntries.get(i);
						if (this.GetMenuEntryHitBounds(entry).contains(
								SysTouch.x(), SysTouch.y())) {
							this.OnSelectEntry(i);
						}
					}
				}
			}
		}
	}

	protected void OnCancel() {
		super.ExitScreen();
		if (this.getScreenType() == ScreenType.MainMenuScreen) {
			LSystem.exit();
		} else if ((this.getScreenType() == ScreenType.MonsterInfoScreen)
				|| (this.getScreenType() == ScreenType.TowerInfoScreen)) {
			super.getScreenManager().AddScreen(
					new InstructionScreen(this.game, this.getScreenType()));
		} else if ((this.getScreenType() == ScreenType.InstructionsScreen)
				|| (this.getScreenType() == ScreenType.SelectLevelScreen)) {
			super.getScreenManager().AddScreen(
					new MainMenuScreen(this.game, this.getScreenType()));
		} else if (this.prevScreen == ScreenType.GamePausedScreen) {
			super.getScreenManager().AddScreen(
					new GamePausedScreen(this.game, this.getScreenType()));
		} else {
			super.getScreenManager().AddScreen(
					new MainMenuScreen(this.game, this.getScreenType()));
		}
	}

	protected void OnSelectEntry(int entryIndex) {
		this.menuEntries.get(entryIndex).OnSelectEntry();
	}

	@Override
	public void Update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		super.Update(gameTime, otherScreenHasFocus, coveredByOtherScreen);
		for (int i = 0; i < this.menuEntries.size(); i++) {
			boolean isSelected = super.getIsActive()
					&& (i == this.selectedEntry);
			this.menuEntries.get(i).Update(this, isSelected, gameTime);
		}
	}

	protected void UpdateMenuEntryLocations() {
		float num = (float) Math.pow((double) super.getTransitionPosition(),
				2.0);
		Vector2f vector = new Vector2f(0f, (float) ((super.getScreenManager()
				.getGame().getHeight() / 2) - (this.menuEntries.get(0)
				.GetHeight(this) + (70 * this.menuEntries.size()))));
		for (int i = 0; i < this.menuEntries.size(); i++) {
			MenuEntry entry = this.menuEntries.get(i);
			vector.x = (LSystem.viewSize.width / 2)
					- (entry.GetWidth(this) / 2);
			if (super.getScreenState() == ScreenState.TransitionOn) {
				vector.x -= num * 256f;
			} else {
				vector.x += num * 512f;
			}
			entry.setPosition(vector);
			vector.y += entry.GetHeight(this) + 70;
		}
	}

	protected final java.util.List<MenuEntry> getMenuEntries() {
		return this.menuEntries;
	}

	private ScreenType privateScreenType;

	public final ScreenType getScreenType() {
		return privateScreenType;
	}

	public final void setScreenType(ScreenType value) {
		privateScreenType = value;
	}
}