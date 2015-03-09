package com.mygame;

public class MonsterPeasant extends Monster {
	public MonsterPeasant(MainGame game, Wave wave, float speed,
			int startHitPoints, int value) {
		super(game, wave, startHitPoints, speed, value, "assets/peasant.png",
				8, 8, 0x18, 0x18);
		super.setMonsterType(MonsterType.Peasant);
		super.setRadius(5f);
		super.setAnimationSpeedRatio(3);
	}
}