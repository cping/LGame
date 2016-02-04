package org.test.rtsgame;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableEvent;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class PauseMenuEntity extends MenuEntity {

	public PauseMenuEntity() {
		super.setIsPopup(true);
	}

	@Override
	public void Draw(SpriteBatch batch, GameTime gameTime) {
		super.getScreenManager().FadeBackBufferToBlack(batch,
				(super.getTransitionAlpha() * 2f) / 3f);
		super.Draw(batch, gameTime);
	}

	@Override
	public void LoadContent() {
		int num;
		if (GameplayEntity.levelIndex <= 2) {
			num = 0;
		} else if (GameplayEntity.levelIndex <= 4) {
			num = 1;
		} else {
			num = 2;
		}
		super.titleTexture = super.getScreenManager().getGameContent().tutorial[num];
		MenuEntry item = new MenuEntry(this, "Resume Game", new Vector2f(252f,
				240f));
		MenuEntry entry2 = new MenuEntry(this, "Main Menu", new Vector2f(276f,
				282f));

		item.Selected = new DrawableEvent() {

			@Override
			public void invoke() {
				OnCancel();
			}
		};

		entry2.Selected = new DrawableEvent() {

			@Override
			public void invoke() {
				QuitGameMenuEntrySelected();

			}
		};
		super.getMenuEntries().add(item);
		super.getMenuEntries().add(entry2);
	}

	private void QuitGameMenuEntrySelected() {
		LoadingEntity.Load(super.getScreenManager(), false, new GameEntity[] {
				new BackgroundEntity(), new MainMenuEntity() });
	}
}