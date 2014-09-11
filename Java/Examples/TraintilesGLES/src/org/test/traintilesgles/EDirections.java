package org.test.traintilesgles;

public enum EDirections
{
	EDirDown(2),
	EDirLeft(3),
	EDirRight(1),
	EDirTop(0),
	TRAIN_CRASH(-1),
	TRAIN_CRASH_SOFT(-2);

	private int intValue;
	private static java.util.HashMap<Integer, EDirections> mappings;
	private static java.util.HashMap<Integer, EDirections> getMappings()
	{
		if (mappings == null)
		{
			synchronized (EDirections.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, EDirections>();
				}
			}
		}
		return mappings;
	}

	private EDirections(int value)
	{
		intValue = value;
		EDirections.getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static EDirections forValue(int value)
	{
		return getMappings().get(value);
	}
}