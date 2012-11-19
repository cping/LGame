package org.test;

public enum GameEndedState
{
	NotSet,
	Win,
	Lose;

	public int getValue()
	{
		return this.ordinal();
	}

	public static GameEndedState forValue(int value)
	{
		return values()[value];
	}
}