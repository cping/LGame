package org.test;

public final class BubbleDataManager {
	public static boolean askForRating = true;
	public static int askRatingAfterMinute = 8;
	private static int freeLevels = 3;
	public static int games = 0;
	public static int levels = 50;
	private static int[] levelScore;
	private static boolean[] levelWon;

	public static java.util.ArrayList<String> rankingName = new java.util.ArrayList<String>();
	public static java.util.ArrayList<Integer> rankingScore = new java.util.ArrayList<Integer>();
	public static int showRateAfterGame = 8;
	public static boolean soundEnabled = true;
	public static boolean vibrateEnabled = true;

	public static void Initialize() {
		levelWon = new boolean[levels];
		levelScore = new int[levels];
	}

	public static int LevelScore(int levelIndex) {
		return levelScore[levelIndex];
	}

	public static boolean LevelUnocked(int levelIndex) {
		return (levelIndex < freeLevels);
	}

	public static void UploadScore(int level, boolean won, int score) {

	}

	public static void WonGame(int levelIndex, int score) {
		if (!levelWon[levelIndex]) {
			freeLevels++;
			levelWon[levelIndex] = true;
		}
		if (levelScore[levelIndex] < score) {
			levelScore[levelIndex] = score;
		}
	}
}