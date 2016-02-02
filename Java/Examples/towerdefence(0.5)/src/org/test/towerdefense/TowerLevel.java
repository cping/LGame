package org.test.towerdefense;

public class TowerLevel {

	public TowerLevel(int cost, float range, int damage, float reloadTime,
			float releaseTime, float upgradeTime) {
		this.setCost(cost);
		this.setRange(range);
		this.setDamage(damage);
		this.setReloadTime(reloadTime);
		this.setReleaseTime(releaseTime);
		this.setUpgradeTime(upgradeTime);
	}

	private int privateCost;

	public final int getCost() {
		return privateCost;
	}

	public final void setCost(int value) {
		privateCost = value;
	}

	private int privateDamage;

	public final int getDamage() {
		return privateDamage;
	}

	public final void setDamage(int value) {
		privateDamage = value;
	}

	private float privateRange;

	public final float getRange() {
		return privateRange;
	}

	public final void setRange(float value) {
		privateRange = value;
	}

	private float privateReleaseTime;

	public final float getReleaseTime() {
		return privateReleaseTime;
	}

	public final void setReleaseTime(float value) {
		privateReleaseTime = value;
	}

	private float privateReloadTime;

	public final float getReloadTime() {
		return privateReloadTime;
	}

	public final void setReloadTime(float value) {
		privateReloadTime = value;
	}

	private float privateUpgradeTime;

	public final float getUpgradeTime() {
		return privateUpgradeTime;
	}

	public final void setUpgradeTime(float value) {
		privateUpgradeTime = value;
	}
}