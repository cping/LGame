package com.mygame;

public class ScheduleItem
{
	public int caveid;
	public int ticks;

	public ScheduleItem()
	{
	}

	public ScheduleItem(int aCaveId, int aTicks)
	{
		this.caveid = aCaveId;
		this.ticks = aTicks;
	}
}