package org.test;

public enum WaveState
{
	Started,
	NotStarted,
	Finished;

	public int getValue()
	{
		return this.ordinal();
	}

	public static WaveState forValue(int value)
	{
		return values()[value];
	}
}