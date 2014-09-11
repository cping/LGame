package org.test;

import loon.action.sprite.painting.DrawableEvent;
import loon.core.geom.Vector2f;

public class OptionsMenuEntity extends MenuEntity {
	private MenuEntry musicMenuEntry;
	private MenuEntry soundMenuEntry;

	@Override
	public void LoadContent() {
		super.titleTexture = super.getScreenManager().getGameContent().options;
		this.soundMenuEntry = new MenuEntry(this, "", new Vector2f(270f, 192f));
		this.musicMenuEntry = new MenuEntry(this, "", new Vector2f(270f, 234f));
		this.SetMenuEntryText();
		MenuEntry item = new MenuEntry(this, "Back", new Vector2f(288f, 300f));

		this.soundMenuEntry.Selected = new DrawableEvent() {

			@Override
			public void invoke() {
				SoundEntrySelected();

			}
		};

		this.musicMenuEntry.Selected = new DrawableEvent() {

			@Override
			public void invoke() {
				MusicMenuEntrySelected();

			}
		};

		item.Selected = new DrawableEvent() {

			@Override
			public void invoke() {
				OnCancel();

			}
		};
		super.getMenuEntries().add(this.soundMenuEntry);
		super.getMenuEntries().add(this.musicMenuEntry);
		super.getMenuEntries().add(item);
	}

	private void MusicMenuEntrySelected() {
		GameMain.Settings.MusicEnabled = !GameMain.Settings.MusicEnabled;
		if (GameMain.Settings.MusicEnabled) {
			super.getScreenManager().getGameContent().PlayMusic();
		} else {

		}
		this.SetMenuEntryText();
	}

	private void SetMenuEntryText() {
		this.soundMenuEntry.Text = "Sound: "
				+ (GameMain.Settings.SoundEnabled ? "on" : "off");
		this.musicMenuEntry.Text = "Music: "
				+ (GameMain.Settings.MusicEnabled ? "on" : "off");
	}

	private void SoundEntrySelected() {
		GameMain.Settings.SoundEnabled = !GameMain.Settings.SoundEnabled;
		if (GameMain.Settings.SoundEnabled) {
			Sound.MasterVolume = 1f;
		} else {
			Sound.MasterVolume = 0f;
		}
		this.SetMenuEntryText();
	}
}