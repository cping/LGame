package com.mygame;

public class Entity
{
	public int subtype;
	public int type;
	public int x;
	public int y;

	public EEntityClass getEntityType()
	{
		return EEntityClass.EEntityDoodad;
	}
}