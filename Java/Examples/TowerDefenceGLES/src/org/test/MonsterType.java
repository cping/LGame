package org.test;

public enum MonsterType
{
	Peasant,
	Peon,
	Berserker,
	Chicken,
	Doctor,
	Chieftain;

	public int getValue()
	{
		return this.ordinal();
	}

	public static MonsterType forValue(int value)
	{
		return values()[value];
	}
}