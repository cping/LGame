package org.test.towerdefense;

public class CompletedLevel {
	public static void PersistLevelCompleted(MainGame game,
			Difficulty difficulty, int level) {
		boolean flag = false;
		int numRemainingLives = game.getGameplayScreen().getRemainingLives()
				.getNumRemainingLives();
		for (CompletedLevel level2 : game.getCompletedLevels()) {
			if ((level2.getDifficulty() == difficulty.getValue())
					&& (level2.getLevel() == level)) {
				flag = true;
				if (numRemainingLives > level2.getRemainingLives()) {
					level2.setRemainingLives(numRemainingLives);
				}
			}
		}
		if (!flag) {
			CompletedLevel level4 = new CompletedLevel();
			level4.setDifficulty(difficulty.getValue());
			level4.setLevel(level);
			level4.setRemainingLives(numRemainingLives);
			CompletedLevel item = level4;
			game.getCompletedLevels().add(item);
		}
	}

	private int privateDifficulty;

	public final int getDifficulty() {
		return privateDifficulty;
	}

	public final void setDifficulty(int value) {
		privateDifficulty = value;
	}

	private int privateLevel;

	public final int getLevel() {
		return privateLevel;
	}

	public final void setLevel(int value) {
		privateLevel = value;
	}

	private int privateRemainingLives;

	public final int getRemainingLives() {
		return privateRemainingLives;
	}

	public final void setRemainingLives(int value) {
		privateRemainingLives = value;
	}
}