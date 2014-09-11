package org.test.traintilesgles;

public enum EValues
{
	EValueSelectedLevel,
	EValueSelectedMainLevel,
	EValueSelectedEditorIndex,
	EValueDoLevelSelectAnimation,
	EValueDoGameEndAnimation,
	EValueTrialClickedFrom,
	EValueCount;

	public int getValue()
	{
		return this.ordinal();
	}

	public static EValues forValue(int value)
	{
		return values()[value];
	}
}