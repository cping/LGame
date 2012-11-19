package org.test.traintilesgles;

public enum EEntityClass
{
	EEntityTrain,
	EEntityDoodad;

	public int getValue()
	{
		return this.ordinal();
	}

	public static EEntityClass forValue(int value)
	{
		return values()[value];
	}
}