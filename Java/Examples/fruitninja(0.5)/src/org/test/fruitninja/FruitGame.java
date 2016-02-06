package org.test.fruitninja;

import java.util.ArrayList;

import loon.utils.MathUtils;

public class FruitGame {

	private static final float TICK_INITIAL = 0.06f;

	public boolean gameover = false;
	private float tickTime = 0.0f;
	public ArrayList<Fruit> fruitList = new ArrayList<Fruit>();

	public FruitGame() {
		throwFruit();
	}

	public void throwFruit() {
		int num = MathUtils.nextInt(3) + 1;
		for (int i = 1; i <= num; i++) {
			fruitList.add(new Fruit(100 + MathUtils.nextInt(440), 480,
					MathUtils.nextInt(5)));
		}
	}

	public void update(float deltaTime) {
		if (gameover)
			return;
		tickTime += deltaTime;
		while (tickTime > TICK_INITIAL) {
			tickTime -= TICK_INITIAL;
			for (int i = 0; i < fruitList.size(); i++) {
				if (fruitList.get(i).Changer() == false)
					fruitList.remove(i);
			}
			if (fruitList.size() == 0)
			{
				fruitList.clear();
				throwFruit();
			}
		}
	}
}
