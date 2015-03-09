package com.mygame;

public enum EStates
{
	EGameStateSplash,
	EGameStateMainMenu,
	EGameStateGame,
	EGameStateLevelFailed,
	EGameStateLevelSuccess,
	EGameStateLoadGame,
	EGameStateLevelSelect,
	EGameStateMainLevelSelect,
	EGameStateGameEnd,
	EGameStateTrial,
	EGameStateCount;

	public int getValue()
	{
		return this.ordinal();
	}

	public static EStates forValue(int value)
	{
		return values()[value];
	}
}