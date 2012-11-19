package org.test.traintilesgles;

public enum ELavaBorders
{
	ELavaBorderDown(4),
	ELavaBorderLeft(8),
	ELavaBorderRight(2),
	ELavaBorderTop(1);

	private int intValue;
	private static java.util.HashMap<Integer, ELavaBorders> mappings;
	private static java.util.HashMap<Integer, ELavaBorders> getMappings()
	{
		if (mappings == null)
		{
			synchronized (ELavaBorders.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, ELavaBorders>();
				}
			}
		}
		return mappings;
	}

	private ELavaBorders(int value)
	{
		intValue = value;
		ELavaBorders.getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static ELavaBorders forValue(int value)
	{
		return getMappings().get(value);
	}
}