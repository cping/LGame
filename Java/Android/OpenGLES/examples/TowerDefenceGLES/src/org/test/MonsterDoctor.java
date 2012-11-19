package org.test;

public class MonsterDoctor extends Monster
{
	public MonsterDoctor(MainGame game, Wave wave, float speed, int startHitPoints, int value)
	{
		super(game, wave, startHitPoints, speed, value, "assets/doctor.png", 8, 8, 40, 40);
		super.setMonsterType(MonsterType.Doctor);
		super.setRadius(8f);
		super.setAnimationSpeedRatio( 3);
	}
}