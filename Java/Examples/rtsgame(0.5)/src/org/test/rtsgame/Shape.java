package org.test.rtsgame;
//角色类型（默认只提供两种，一种方形角色，一种圆形）
public enum Shape
{
	square,
	triangle;

	public int getValue()
	{
		return this.ordinal();
	}

	public static Shape forValue(int value)
	{
		return values()[value];
	}
}