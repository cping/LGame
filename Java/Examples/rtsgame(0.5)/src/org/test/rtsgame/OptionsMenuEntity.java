package org.test.rtsgame;

import loon.action.sprite.painting.DrawableEvent;
import loon.geom.Vector2f;

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
		MainGame.Settings.MusicEnabled = !MainGame.Settings.MusicEnabled;
		if (MainGame.Settings.MusicEnabled) {
			super.getScreenManager().getGameContent().PlayMusic();
		} else {

		}
		this.SetMenuEntryText();
	}

	private void SetMenuEntryText() {
		this.soundMenuEntry.Text = "Sound: " + (MainGame.Settings.SoundEnabled ? "on" : "off");
		this.musicMenuEntry.Text = "Music: " + (MainGame.Settings.MusicEnabled ? "on" : "off");
	}

	private void SoundEntrySelected() {
		MainGame.Settings.SoundEnabled = !MainGame.Settings.SoundEnabled;
		if (MainGame.Settings.SoundEnabled) {
			Sound.MasterVolume = 1f;
		} else {
			Sound.MasterVolume = 0f;
		}
		this.SetMenuEntryText();
	}
}