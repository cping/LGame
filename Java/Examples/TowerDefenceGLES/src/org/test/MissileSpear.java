package org.test;

public class MissileSpear extends Missile {

	public MissileSpear(MainGame game, Monster targetMonster, Tower tower) {
		super(game, MissileType.SPEAR, "assets/spear.png", tower.getPosition(),
				targetMonster, tower.getDamage(), 2, 2, 0x18, 0x18);
	}

	private int privateDamage;

	@Override
	public int getDamage() {
		return privateDamage;
	}

	@Override
	public void setDamage(int value) {
		privateDamage = value;
	}
}