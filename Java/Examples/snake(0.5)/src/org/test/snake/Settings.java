package org.test.snake;

import loon.Session;
import loon.utils.StringUtils;

public class Settings {
	public static boolean soundEnabled = true;
	public static int[] highscores = new int[] { 100, 80, 50, 30, 10 };

	public static Session session;

	private static void loadSession() {
		if (session == null) {
			session = new Session("snake");
		}
	}

	public static void load() {
		loadSession();
		String result = session.get("score");
		if (result != null) {
			String[] list = StringUtils.split(result, ',');
			for (int i = 0; i < list.length && i < highscores.length; i++) {
				highscores[i] = Integer.parseInt(list[i]);
			}
		}
	}

	public static void save() {
		loadSession();
		session.add("score", StringUtils.join(',', highscores));
	}

	public static void addScore(int score) {
		for (int i = 0; i < 5; i++) {
			if (highscores[i] < score) {
				for (int j = 4; j > i; j--) {
					highscores[j] = highscores[j - 1];
				}
				highscores[i] = score;
				break;
			}
		}
	}
}
