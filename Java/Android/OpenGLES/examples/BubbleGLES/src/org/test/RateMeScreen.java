package org.test;

import org.test.MenuEntry.SelectEvent;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.GameTime;

public class RateMeScreen extends MessageScreen {
	private MenuEntry neverAskAgain;
	private LTexture rateAddon;

	public RateMeScreen() {
		super(Strings.getRateMe(), false, true);
	}

	private void CancelRatingScreen() {

	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		batch.draw(this.rateAddon,
				new Vector2f(100f,
						250f - (800f * super.getTransitionPosition())),
				new RectBox(0, 0, 200, 200), LColor.white, 0f, new Vector2f(
						100f), (float) 1f, SpriteEffects.None);
	}

	@Override
	public void loadContent() {
		this.rateAddon = LTextures.loadTexture("assets/RateMe.png");
		super.loadContent();
		super.acceptMenuEntry.setBasePosition(new Vector2f(90f, 580f));
		super.backMenuEntry.setBasePosition(new Vector2f(195f, 580f));
		this.neverAskAgain = new MenuEntry(new RectBox(410, 0, 210, 0x69));
		this.neverAskAgain.setBasePosition(new Vector2f(360f, 580f));
		this.neverAskAgain.setEntryAnimation(MenuEntryEffects.GoToBottom
				| MenuEntryEffects.ComeFromBottom);

		this.neverAskAgain.Selected = new SelectEvent() {

			@Override
			public void invoke(MenuEntry entry) {
				NeverAskAgainMenuentrySelected();

			}
		};
		super.getMenuEntries().add(this.neverAskAgain);

		super.Accepted = new EventHandler() {

			@Override
			public void invoke(MessageScreen m) {
				OpenRatingScreen();
			}
		};

		super.Cancelled = new EventHandler() {

			@Override
			public void invoke(MessageScreen m) {
				CancelRatingScreen();
			}
		};
	}

	private void NeverAskAgainMenuentrySelected() {
		BubbleDataManager.askForRating = false;
		this.exitScreen();
	}

	private void OpenRatingScreen() {

	}
}