package org.test.traintilesgles;

public enum ECaveTypes
{
	ECaveInRed,
	ECaveInYellow,
	ECaveInBlue,
	ECaveOutRed,
	ECaveOutYellow,
	ECaveOutBlue,
	ECaveTypeCount;

	public int getValue()
	{
		return this.ordinal();
	}

	public static ECaveTypes forValue(int value)
	{
		return values()[value];
	}
}