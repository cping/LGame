package org.test.fruitninja;

import loon.LSystem;
import loon.utils.MathUtils;

public class Fruit {
	public static final int TYPE_1 = 0;// apple
	public static final int TYPE_2 = 1;// banana
	public static final int TYPE_3 = 2;// basaha
	public static final int TYPE_4 = 3;// peach
	public static final int TYPE_5 = 4;// sandia
	public static final int TYPE_6 = 5;// boom

	public float x, y;
	public int width;
	public int height;
	public int type;
	public float xSpeed, ySpeed;
	public boolean flag = true;

	public Fruit(int x, int y, int type) {
		this.x = x;
		this.y = y;
		this.type = type;
		xSpeed = MathUtils.nextInt(40) - 20;
		ySpeed = -30 - MathUtils.nextInt(8);
		switch (type) {
		case TYPE_1:
			width = 66;
			height = 66;
			break;
		case TYPE_2:
			width = 126;
			height = 50;
			break;
		case TYPE_3:
			width = 68;
			height = 72;
			break;
		case TYPE_4:
			width = 62;
			height = 59;
			break;
		case TYPE_5:
			width = 98;
			height = 85;
			break;
		case TYPE_6:
			width = 66;
			height = 68;
			break;
		}
	}

	public boolean Changer() {
		x += xSpeed;
		y += ySpeed;
		ySpeed += 1.66;
		if (x > LSystem.viewSize.getWidth() || x < -100
				|| y > LSystem.viewSize.getHeight() || y < 0) {
			return false;
		}
		return true;
	}
}
