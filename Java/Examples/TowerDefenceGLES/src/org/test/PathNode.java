package org.test;

public class PathNode {

	public int x() {
		return privateX;
	}

	public int y() {
		return privateY;
	}

	public PathNode(int x, int y, int cost) {
		this.setX(x);
		this.setY(y);
		this.setCost(cost);
	}

	private int privateCost;

	public final int getCost() {
		return privateCost;
	}

	public final void setCost(int value) {
		privateCost = value;
	}

	private int privateX;

	public final int getX() {
		return privateX;
	}

	public final void setX(int value) {
		privateX = value;
	}

	private int privateY;

	public final int getY() {
		return privateY;
	}

	public final void setY(int value) {
		privateY = value;
	}
}