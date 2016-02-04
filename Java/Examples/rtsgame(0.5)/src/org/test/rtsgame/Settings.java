package org.test.rtsgame;

import loon.Session;

public class Settings {

	public boolean MusicEnabled = true;
	public boolean SoundEnabled = true;

	private Session session;

	public Settings() {
		session = new Session("setting");
	}

	public void Load() {
		MusicEnabled = session.getBoolean("music");
		SoundEnabled = session.getBoolean("sound");
	}

	public void Save() {
		session.set("music", MusicEnabled);
		session.set("sound", SoundEnabled);
		session.save();
	}

}
