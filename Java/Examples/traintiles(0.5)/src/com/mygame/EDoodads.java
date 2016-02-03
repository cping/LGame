package com.mygame;

public enum EDoodads
{
	EDoodadTree1,
	EDoodadTree2,
	EDoodadTree3,
	EDoodadBush1,
	EDoodadBush2,
	EDoodadBush3,
	EDoodadAnimal1,
	EDoodadAnimal2,
	EDoodadAnimal3,
	EDoodadCount;

	public int getValue()
	{
		return this.ordinal();
	}

	public static EDoodads forValue(int value)
	{
		return values()[value];
	}
}