package org.test;

import org.test.MenuEntry.SelectEvent;
import org.test.MessageScreen.EventHandler;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.LSystem;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.input.LKey;
import loon.core.input.LTouch;
import loon.core.timer.GameTime;

public class MainMenuScreen extends MenuScreen {
	private MenuEntry BestListMenuEntry;
	private MenuEntry FacebookMenuEntry;
	private MenuEntry InfoMenuEntry;
	private LTexture mainbuttons;
	private boolean openSettingMenu = false;
	private MenuEntry OptionMenuEntry;
	private MenuEntry SelectLevelMenuEntry;
	private float settingMenuProcess = 0f;
	private MenuEntry SoundMenuEntry;
	private LTexture submenu;
	private MenuEntry VibrateMenuEntry;

	public MainMenuScreen() {
		super.transitionOnTime = 4f;
		super.transitionOffTime = 4f;
	}

	private void BestlistMenuentrySelected() {
		super.drawableScreen.addDrawable(new RankingScreen());
		this.openSettingMenu = false;

	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		if (this.settingMenuProcess > 0f) {
			batch.draw(this.submenu, -335f * (1f - this.settingMenuProcess),
					550f, 0, 360, 0x14f, 90, LColor.white, 0f, 0f, 45f, 1f, 1f,
					SpriteEffects.FlipHorizontally);
		}

	}

	private void InstructionMenuentrySelected() {
		super.drawableScreen.addDrawable(new MessageScreen(
				Strings.getCredits(), true, false));
		this.openSettingMenu = false;
	}

	@Override
	public void loadContent() {
		this.submenu = LTextures.loadTexture("assets/MenuEntries.png");
		this.mainbuttons = LTextures.loadTexture("assets/MainButtons.png");
		this.SelectLevelMenuEntry = new MenuEntry(new RectBox(0, 0, 0x150, 140));
		this.SelectLevelMenuEntry.ChangeTexture(this.mainbuttons);
		this.BestListMenuEntry = new MenuEntry(new RectBox(0, 140, 0x150, 140));
		this.BestListMenuEntry.ChangeTexture(this.mainbuttons);
		this.OptionMenuEntry = new MenuEntry(new RectBox(180, 160, 80, 80));
		this.InfoMenuEntry = new MenuEntry(new RectBox(260, 150, 0x4b, 0x4b));
		this.SoundMenuEntry = new MenuEntry(new RectBox(260, 0, 0x4b, 0x4b));
		this.VibrateMenuEntry = new MenuEntry(
				new RectBox(260, 0x4b, 0x4b, 0x4b));
		this.FacebookMenuEntry = new MenuEntry(new RectBox(0x322, 0x166, 0x5b,
				0x5b));
		this.SelectLevelMenuEntry.Selected = new SelectEvent() {

			@Override
			public void invoke(MenuEntry entry) {
				SelectLevelMenuentrySelected();
			}
		};

		this.BestListMenuEntry.Selected = new SelectEvent() {

			@Override
			public void invoke(MenuEntry entry) {
				BestlistMenuentrySelected();

			}
		};

		this.OptionMenuEntry.Selected = new SelectEvent() {

			@Override
			public void invoke(MenuEntry entry) {
				OptionMenuentrySelected();

			}
		};

		this.InfoMenuEntry.Selected = new SelectEvent() {

			@Override
			public void invoke(MenuEntry entry) {
				InstructionMenuentrySelected();

			}
		};

		this.SoundMenuEntry.Selected = new SelectEvent() {

			@Override
			public void invoke(MenuEntry entry) {
				SoundMenuentrySelected();

			}
		};

		this.VibrateMenuEntry.Selected = new SelectEvent() {

			@Override
			public void invoke(MenuEntry entry) {
				VibrateMenuentrySelected();
			}
		};

		this.FacebookMenuEntry.Selected = new SelectEvent() {

			@Override
			public void invoke(MenuEntry entry) {

			}
		};
		this.SelectLevelMenuEntry.setBasePosition(new Vector2f(240f, 240f));
		this.BestListMenuEntry.setBasePosition(new Vector2f(240f, 400f));
		this.OptionMenuEntry.setBasePosition(new Vector2f(45f, 670f));
		this.InfoMenuEntry.setBasePosition(new Vector2f(-200f, 550f));
		this.SoundMenuEntry.setBasePosition(new Vector2f(580f, 170f));
		this.VibrateMenuEntry.setBasePosition(new Vector2f(580f, 255f));
		this.FacebookMenuEntry.setBasePosition(new Vector2f(435f, 670f));
		this.SelectLevelMenuEntry.setEntryAnimation(MenuEntryEffects.GoToLeft
				| MenuEntryEffects.ComeFromLeft);
		this.BestListMenuEntry.setEntryAnimation(MenuEntryEffects.GoToRight
				| MenuEntryEffects.ComeFromRight);
		this.OptionMenuEntry.setEntryAnimation(MenuEntryEffects.GoToBottom
				| MenuEntryEffects.ComeFromBottom);
		this.FacebookMenuEntry.setEntryAnimation(MenuEntryEffects.GoToBottom
				| MenuEntryEffects.ComeFromBottom);
		super.getMenuEntries().add(this.SelectLevelMenuEntry);
		super.getMenuEntries().add(this.BestListMenuEntry);
		super.getMenuEntries().add(this.OptionMenuEntry);
		super.getMenuEntries().add(this.InfoMenuEntry);
		super.getMenuEntries().add(this.SoundMenuEntry);
		super.getMenuEntries().add(this.VibrateMenuEntry);
		super.getMenuEntries().add(this.FacebookMenuEntry);

	}

	@Override
	public void onCancel() {
		MessageScreen screen = new MessageScreen(Strings.getQuitGame(), false,
				true);
		screen.Accepted = new EventHandler() {

			@Override
			public void invoke(MessageScreen m) {
				QuitGameSelected();
			}
		};
		super.drawableScreen.addDrawable(screen);
	}

	private void OptionMenuentrySelected() {
		this.openSettingMenu = !this.openSettingMenu;
	}

	private void QuitGameSelected() {
		LSystem.exit();
	}

	private void SelectLevelMenuentrySelected() {
		super.drawableScreen.addDrawable(new BubbleLevelSelectionScreen());
		this.openSettingMenu = false;
	}

	private void SoundMenuentrySelected() {
		BubbleDataManager.soundEnabled = !BubbleDataManager.soundEnabled;
		if (BubbleDataManager.soundEnabled) {
			this.SoundMenuEntry.setSource(new RectBox(260, 0, 0x4b, 0x4b));
		} else {
			this.SoundMenuEntry.setSource(new RectBox(0x14f, 0, 0x4b, 0x4b));
		}
	}

	@Override
	public void update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		super.update(gameTime, otherScreenHasFocus, coveredByOtherScreen);

		this.InfoMenuEntry.setBasePosition(new Vector2f(
				55f - (335f * (1f - this.settingMenuProcess)), 550f));
		this.SoundMenuEntry.setBasePosition(new Vector2f(
				150f - (335f * (1f - this.settingMenuProcess)), 550f));
		this.VibrateMenuEntry.setBasePosition(new Vector2f(
				235f - (335f * (1f - this.settingMenuProcess)), 550f));
		if (this.openSettingMenu && (this.settingMenuProcess < 1f)) {
			this.settingMenuProcess += ((float) gameTime.getElapsedGameTime()) * 3f;
			if (this.settingMenuProcess > 1f) {
				this.settingMenuProcess = 1f;
			}
		}
		if (!this.openSettingMenu && (this.settingMenuProcess > 0f)) {
			this.settingMenuProcess -= ((float) gameTime.getElapsedGameTime()) * 3f;
			if (this.settingMenuProcess < 0f) {
				this.settingMenuProcess = 0f;
			}
		}
	}

	private void VibrateMenuentrySelected() {
		BubbleDataManager.vibrateEnabled = !BubbleDataManager.vibrateEnabled;
		if (BubbleDataManager.vibrateEnabled) {
			this.VibrateMenuEntry.setSource(new RectBox(260, 0x4b, 0x4b, 0x4b));
		} else {
			this.VibrateMenuEntry
					.setSource(new RectBox(0x14f, 0x4b, 0x4b, 0x4b));
		}
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