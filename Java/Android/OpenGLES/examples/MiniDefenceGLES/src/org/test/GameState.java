package org.test;

public enum GameState
{
	inChooseLevel(4),
	inGame(1),
	inHelp(0x40),
	inLose(8),
	inMainMenu(0),
	inPause(2),
	inVictory(0x20),
	inWin(0x10);

	private int intValue;
	private static java.util.HashMap<Integer, GameState> mappings;
	private static java.util.HashMap<Integer, GameState> getMappings()
	{
		if (mappings == null)
		{
			synchronized (GameState.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, GameState>();
				}
			}
		}
		return mappings;
	}

	private GameState(int value)
	{
		intValue = value;
		GameState.getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static GameState forValue(int value)
	{
		return getMappings().get(value);
	}
}