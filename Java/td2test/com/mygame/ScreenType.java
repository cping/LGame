package com.mygame;

public enum ScreenType {
	ConfirmScreen, GamePausedScreen, GameplayScreen, InstructionsScreen, LoadingScreen, LoseScreen, MainMenuScreen, MonsterInfoScreen, SelectLevelScreen, TowerInfoScreen, WinScreen, BuyToGetFeaturesScreen;

	public int getValue() {
		return this.ordinal();
	}

	public static ScreenType forValue(int value) {
		return values()[value];
	}
}