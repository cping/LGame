package org.test;

import java.util.ArrayList;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.Drawable;
import loon.action.sprite.painting.DrawableState;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.input.LInput;
import loon.core.input.LTouch;
import loon.core.input.LInputFactory.Key;
import loon.core.input.LInputFactory.Touch;
import loon.core.timer.GameTime;

public abstract class MenuScreen extends Drawable {

	private ArrayList<MenuEntry> menuEntries = new ArrayList<MenuEntry>();

	public MenuScreen() {
		super.transitionOnTime = 0.5f;
		super.transitionOffTime = 0.5f;
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		for (int i = 0; i < this.menuEntries.size(); i++) {
			this.menuEntries.get(i).draw(batch, gameTime);
		}
	}

	protected RectBox getMenuEntryHitBounds(MenuEntry entry) {
		return new RectBox(((int) entry.getPosition().x)
				- (entry.getWidth() / 2), ((int) entry.getPosition().y)
				- (entry.getHeight() / 2), entry.getWidth(), entry.getHeight());
	}

	protected final void setDefaultEntryPositions(
			MenuEntry... entriesToPosition) {
		Vector2f vector = new Vector2f(ScreenData.screenCenter.x,
				(float) (ScreenData.screenHeight / 4));
		float num = ScreenData.screenHeight / 20;
		for (int i = 0; i < entriesToPosition.length; i++) {
			entriesToPosition[i].setBasePosition(vector);
			vector.y += entriesToPosition[i].getHeight() + num;
		}
	}

	public void update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		super.update(gameTime, otherScreenHasFocus, coveredByOtherScreen);
		for (int i = 0; i < this.getMenuEntries().size(); i++) {
			this.getMenuEntries()
					.get(i)
					.update(gameTime,
							super.getTransitionAlpha(),
							super.getDrawableState() == DrawableState.TransitionOff);
		}
	}

	protected final ArrayList<MenuEntry> getMenuEntries() {
		return this.menuEntries;
	}

	public void onCancel() {

	}

	public void onSelectEntry(int index) {
		this.menuEntries.get(index).OnSelectEntry();
	}

	private boolean click;

	public void handleInput(LInput input) {
		if (super.getTransitionAlpha() > 0.9) {
			if (Key.isKeyPressed(Key.BACK)) {
				this.onCancel();
			}
			if (!click && Touch.isDown()) {
				for (int j = 0; j < this.menuEntries.size(); j++) {
					if (this.getMenuEntryHitBounds(this.menuEntries.get(j))
							.contains(Touch.x(), Touch.y())) {
						this.onSelectEntry(j);
					}
				}
				click = true;
			} else if (Touch.isUp()) {
				click = false;
			}
		}
	}

	public void released(LTouch e) {
		click = false;
	}

}