package org.test.traintilesgles;

public enum EFocusEvents
{
	FOCUSGAINED(2),
	FOCUSLOST(1);

	private int intValue;
	private static java.util.HashMap<Integer, EFocusEvents> mappings;
	private static java.util.HashMap<Integer, EFocusEvents> getMappings()
	{
		if (mappings == null)
		{
			synchronized (EFocusEvents.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, EFocusEvents>();
				}
			}
		}
		return mappings;
	}

	private EFocusEvents(int value)
	{
		intValue = value;
		EFocusEvents.getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static EFocusEvents forValue(int value)
	{
		return getMappings().get(value);
	}
}