package org.test;

import loon.core.store.Session;

public class ScoreData {

	public int Level;
	public int Score;

	private Session session;

	public ScoreData() {
		session = new Session("score");
	}

	public void Load() {
		Level = session.getInt("level");
		Score = session.getInt("score");
	}

	public void Save() {
		session.set("level", Level);
		session.set("score", Score);
		session.save();
	}

}
