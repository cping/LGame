package org.test.towerdefense;

public enum MissileType {
	AXE, SPEAR;

	public int getValue() {
		return this.ordinal();
	}

	public static MissileType forValue(int value) {
		return values()[value];
	}
}