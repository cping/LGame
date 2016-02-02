package org.test.towerdefense;

public class TowerAxe extends Tower {
	public TowerAxe(MainGame game) {
		super(game, TowerType.Axe, Capability.AirGround,
				"assets/towers/bash_tower.png");
		super.setTowerLevels(new TowerLevel[] {
				new TowerLevel(5, 60f, 10, 1.5f, 0.6f, 0f),
				new TowerLevel(5, 60f, 20, 1.5f, 0.6f, 5f),
				new TowerLevel(10, 70f, 0x23, 1.5f, 0.6f, 8f),
				new TowerLevel(30, 70f, 60, 1.5f, 0.6f, 12f),
				new TowerLevel(70, 90f, 80, 1.5f, 0.6f, 15f) });
		super.SetValuesFromTowerLevel(0);
		super.SetInitialValue();
	}
}