package org.test;

import loon.core.geom.Vector2f;

public class MonsterChicken extends Monster {
	private MainGame game;

	public MonsterChicken(MainGame game, Wave wave, float speed,
			int startHitPoints, int value) {
		super(game, wave, startHitPoints, speed, value, "assets/chicken.png",
				8, 8, 0x20, 0x20);
		this.game = game;
		super.setMonsterType(MonsterType.Chicken);
		super.setRadius(6f);
		super.setDrawOrder(30);
		super.getHealthBar().setDrawOrder(30);
	}

	private Vector2f result = new Vector2f();

	@Override
	public Vector2f GetNextGridPoint(Vector2f gridPosition) {
		if (this.game.getGameplayScreen().getLevel() >= 3) {
			if (gridPosition.x < 11) {
				super.setRotation(Utils.GetAngle(new Vector2f(1f, -1f)));
				result.set(gridPosition.x + 1, gridPosition.y - 1);
				return result;
			}
			super.setRotation(0f);
		}
		result.set(gridPosition.x + 1, gridPosition.y);
		return result;
	}

	@Override
	public int GetVerticalTextureOffset() {
		return 0;
	}
}