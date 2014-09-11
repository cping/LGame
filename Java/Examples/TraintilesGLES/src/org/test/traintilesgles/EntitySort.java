package org.test.traintilesgles;

public class EntitySort implements java.util.Comparator<Entity>
{
	public final int compare(Entity s1, Entity s2)
	{
		if ((s1.y < s2.y) || ((s1.y == s2.y) && (s1.x < s2.x)))
		{
			return -1;
		}
		return 1;
	}
}