package org.test;
//敌人类型（部分未实装）
public enum RoleRank
{
	archer,
	ninja,
	bazooka,
	tribal,
	bomber,
	sword;

	public int getValue()
	{
		return this.ordinal();
	}

	public static RoleRank forValue(int value)
	{
		return values()[value];
	}
}