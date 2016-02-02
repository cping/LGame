package org.test.towerdefense;

public class TowerLur extends Tower {
	public TowerLur(MainGame game) {
		super(game, TowerType.Lur, Capability.Bash,
				"assets/towers/normal_tower.png");
		super.setTowerLevels(new TowerLevel[] {
				new TowerLevel(15, 45f, 15, 3f, 0.8f, 0f),
				new TowerLevel(15, 50f, 30, 3f, 0.8f, 10f),
				new TowerLevel(0x19, 55f, 50, 3f, 0.8f, 15f),
				new TowerLevel(40, 60f, 0x4b, 3f, 0.8f, 20f),
				new TowerLevel(100, 60f, 150, 3f, 0.8f, 25f) });
		super.SetValuesFromTowerLevel(0);
		super.SetInitialValue();
	}
}