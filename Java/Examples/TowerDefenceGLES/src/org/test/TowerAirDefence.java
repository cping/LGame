package org.test;

public class TowerAirDefence extends Tower
{
	public TowerAirDefence(MainGame game)
	{
		super(game, TowerType.AirDefence, Capability.Air, "assets/towers/air_tower.png");
		super.setTowerLevels(new TowerLevel[] {new TowerLevel(10, 70f, 10, 0.5f, 0.2f, 0f), new TowerLevel(10, 80f, 0x12, 0.5f, 0.2f, 5f), new TowerLevel(15, 90f, 0x19, 0.5f, 0.2f, 5f), new TowerLevel(20, 100f, 0x23, 0.5f, 0.2f, 5f), new TowerLevel(0x19, 100f, 50, 0.5f, 0.2f, 10f)});
		super.SetValuesFromTowerLevel(0);
		super.SetInitialValue();
	}
}