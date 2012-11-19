package org.test;

public class TowerButtonAxe extends TowerButton
{
	public TowerButtonAxe(MainGame game)
	{
		super(game, TowerType.Axe);
		super.setTowerPrice(5);
	}
}