package org.test;

public enum TowerType
{
	Axe,
	Spear,
	AirDefence,
	Lur;

	public int getValue()
	{
		return this.ordinal();
	}

	public static TowerType forValue(int value)
	{
		return values()[value];
	}
}