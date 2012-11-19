package org.test;

import org.test.MenuEntry.SelectEvent;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.input.LInput;
import loon.core.input.LInputFactory.Key;
import loon.core.input.LKey;
import loon.core.input.LTouch;
import loon.core.timer.GameTime;

public class MessageScreen extends MenuScreen {
	public static interface EventHandler {
		public void invoke(MessageScreen m);
	}

	public EventHandler Accepted;
	protected MenuEntry acceptMenuEntry;
	private boolean backEqualsYes = false;
	protected MenuEntry backMenuEntry;
	private LTexture bgTexture;
	public EventHandler Cancelled;
	private String message;
	private boolean onlyOk;
	private LFont textFont;
	protected float textheight = 0f;
	private boolean trim;

	public MessageScreen(String message, boolean onlyOk, boolean trim) {
		super.IsPopup = true;
		super.transitionOnTime = 0.5f;
		super.transitionOffTime = 0.5f;
		this.message = message;
		this.trim = trim;
		this.onlyOk = onlyOk;
	}

	private void AcceptMenuEntrySelected() {
		if (this.Accepted != null) {
			this.Accepted.invoke(this);
		}
		this.exitScreen();
	}

	private void BackMenuEntrySelected() {
		if (this.Cancelled != null) {
			this.Cancelled.invoke(this);
		}
		this.exitScreen();
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		drawableScreen
				.fadeBackBufferToBlack((super.getTransitionAlpha() * 2f) / 3f);
		batch.draw(this.bgTexture, 240f,
				400f - (800f * super.getTransitionPosition()), 0, 0, 460, 400,
				LColor.white, 0f, 230f, 200f, 1f, 1f, SpriteEffects.None);
		super.draw(batch, gameTime);
	}

	@Override
	public void handleInput(LInput input) {
		if (Key.isKeyPressed(Key.BACK)) {
			if (this.backEqualsYes) {
				if (this.Accepted != null) {
					this.Accepted.invoke(this);
				}
			} else if (this.Cancelled != null) {
				this.Cancelled.invoke(this);
			}
			this.exitScreen();
		} else {
			if (input.getTouchPressed() != LInput.NO_BUTTON) {
				super.handleInput(input);
			}
		}
	}

	@Override
	public void loadContent() {
		this.textFont = LFont.getFont(15);
		this.bgTexture = LTextures.loadTexture("assets/MessageScreen.png");
		if (this.trim) {
			this.message = trimMessage(this.textFont, this.message, 270f);
		}
		Vector2f vector = new Vector2f(this.textFont.stringWidth(this.message));
		this.textheight = vector.y + 40f;
		this.backMenuEntry = new MenuEntry(new RectBox(260, 0xe1, 0x69, 0x69));
		this.acceptMenuEntry = new MenuEntry(new RectBox(0x16d, 0xe1, 0x69,
				0x69));
		this.acceptMenuEntry.setBasePosition(new Vector2f(140f, 580f));
		this.backMenuEntry.setBasePosition(new Vector2f(340f, 580f));
		this.acceptMenuEntry.setEntryAnimation(MenuEntryEffects.GoToBottom
				| MenuEntryEffects.ComeFromBottom);
		this.backMenuEntry.setEntryAnimation(MenuEntryEffects.GoToBottom
				| MenuEntryEffects.ComeFromBottom);

		this.acceptMenuEntry.Selected = new SelectEvent() {

			@Override
			public void invoke(MenuEntry entry) {
				AcceptMenuEntrySelected();
			}
		};

		this.backMenuEntry.Selected = new SelectEvent() {

			@Override
			public void invoke(MenuEntry entry) {
				BackMenuEntrySelected();
			}
		};
		if (!this.onlyOk) {
			super.getMenuEntries().add(this.acceptMenuEntry);
		}
		super.getMenuEntries().add(this.backMenuEntry);
	}

	public static String trimMessage(LFont font, String message, float maxwidth) {
		float x = 0f;
		String[] strArray = message.split("[ ]", -1);
		String str = "";
		for (int i = 0; i < strArray.length; i++) {
			x += font.stringWidth(strArray[i] + " ");
			if (x > maxwidth) {
				x = font.stringWidth(strArray[i]);
				str = str + "\n";
			}
			str = str + strArray[i] + " ";
		}
		return str;
	}

	public final void setBackEqualsYes(boolean value) {
		this.backEqualsYes = value;
	}

	@Override
	public void unloadContent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(GameTime elapsedTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pressed(LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void released(LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void move(LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pressed(LKey e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void released(LKey e) {
		// TODO Auto-generated method stub

	}
}