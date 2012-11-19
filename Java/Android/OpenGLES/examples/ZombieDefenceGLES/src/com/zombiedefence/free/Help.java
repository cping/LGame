package com.zombiedefence.free;

public abstract class Help
{
	public static Weapon AIWeapon;
	public static int AvailSkillPoint = 0;
	public static float barrierHMax = 100f;
	public static float barrierHealth = barrierHMax;
	public static Bunker currentBunker;
	public static GameScreen currentGameState = GameScreen.values()[0];
	public static Weapon currentWeapon;
	public static float expMultiplyFactor = 1.3f;
	public static int initialNumSound = 5;
	public static int initialPromoteThreshold = 0x23;
	public static boolean isFromLoadedGame = false;
	public static int maxLevel = 9;
	public static int maxNumGrenade = 5;
	public static java.util.ArrayList<Weapon> mercenaryList;
	public static int money = 0;
	public static int numGrenade = 5;
	public static int numSkill1 = 0;
	public static int numSkill10 = 0;
	public static int numSkill2 = 0;
	public static int numSkill3 = 0;
	public static int numSkill4 = 0;
	public static int numSkill5 = 0;
	public static int numSkill6 = 0;
	public static int numSkill7 = 0;
	public static int numSkill8 = 0;
	public static int numSkill9 = 0;
	public static GameScreen previousGameState = GameScreen.values()[0];
	public static Profession profession = Profession.None;
	public static int scorePerHeadShot = 3;
	public static int scorePerKill = 1;
	public static float zombieBirthRateScaler = 1.065f;
	public static int zombieHealthMax = 20;
	public static float zombieHPScaler = 1.1f;

	protected Help()
	{
	}

	public enum ButtonID
	{
		Proceed,
		Back,
		Settings,
		Load,
		About,
		Info,
		Difficulty,
		Email,
		Series,
		Rate,
		Share,
		Resume,
		NoTransition,
		Buy,
		Buy2,
		Skill,
		Yes,
		No,
		Confirm,
		Twitter,
		Cancel,
		Bombard,
		Volume,
		Option1,
		Option2,
		Option3,
		TagReloading,
		TagAAGun,
		TagExtendedMag,
		TagAim,
		TagOverRepair,
		TagFieldRepair,
		TagLearning,
		TagGunner,
		TagArtillery,
		TagBoost;

		public int getValue()
		{
			return this.ordinal();
		}

		public static ButtonID forValue(int value)
		{
			return values()[value];
		}
	}

	public enum GameScreen
	{
		Gameplay,
		MainMenu,
		LevelUp,
		LevelUp2,
		GameOver,
		Options,
		About,
		Info,
		Instruction,
		Result,
		Prepare,
		Series,
		Skill,
		Profession,
		Day,
		PurchaseFull;

		public int getValue()
		{
			return this.ordinal();
		}

		public static GameScreen forValue(int value)
		{
			return values()[value];
		}
	}

	public enum Profession
	{
		Rifleman,
		BattleEngineer,
		Commander,
		None;

		public int getValue()
		{
			return this.ordinal();
		}

		public static Profession forValue(int value)
		{
			return values()[value];
		}
	}
}