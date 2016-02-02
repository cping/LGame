package org.test.towerdefense;

import loon.geom.Vector2f;

public class MonsterPeon extends Monster {
	public MonsterPeon(MainGame game, Wave wave, float speed,
			int startHitPoints, int value) {
		super(game, wave, startHitPoints, speed, value, "assets/peon.png", 8,
				8, 0x18, 0x18);
		this.Init();
	}

	public MonsterPeon(MainGame game, Wave wave, float speed,
			int startHitPoints, int value, Vector2f gridPosition) {
		super(game, wave, startHitPoints, speed, value, "assets/peon.png", 8,
				8, 0x18, 0x18, gridPosition);
		this.Init();
	}

	private void Init() {
		super.setMonsterType(MonsterType.Peon);
		super.setRadius(5f);
		super.setAnimationSpeedRatio(3);
	}
}