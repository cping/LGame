package com.mygame;

public enum ETileTypes
{
	ETileEmpty,
	ETileLeftRight,
	ETileTopDown,
	ETileTopRight,
	ETileRightDown,
	ETileDownLeft,
	ETileLeftTop,
	ETileCross,
	ETileLake,
	ETileCustom1,
	ETileCustom2,
	ETileCustom3,
	ETileCustom4,
	ETileCustom5,
	ETileCustom6,
	ETileCustom7,
	ETileCustom8,
	ETileCustom9,
	ETileCustom10,
	ETileCustom11,
	ETileCustom12,
	ETileCustom13,
	ETileBridgeHorizontal,
	ETileBridgeVertical,
	ECaveBottom,
	ECaveLeft,
	ECaveRight,
	ECaveTop,
	ETileTypeCount;

	public int getValue()
	{
		return this.ordinal();
	}

	public static ETileTypes forValue(int value)
	{
		return values()[value];
	}
}