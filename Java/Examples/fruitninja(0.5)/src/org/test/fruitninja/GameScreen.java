package org.test.fruitninja;

import loon.Screen;
import loon.canvas.LColor;
import loon.component.LGesture;
import loon.event.GameTouch;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class GameScreen extends Screen {

	FruitGame fruitGame;

	public GameScreen() {
		fruitGame = new FruitGame();
	}

	private void drawFruit_s(Fruit fruit, GLEx g) {
		switch (fruit.type) {
		case 0:
			g.draw(Assets.apple_1, (int) fruit.x - 9, (int) fruit.y);
			g.draw(Assets.apple_2, (int) fruit.x + 8, (int) fruit.y);
			break;
		case 1:
			g.draw(Assets.banana_1, (int) fruit.x - 9, (int) fruit.y);
			g.draw(Assets.banana_2, (int) fruit.x - 20, (int) fruit.y);
			break;
		case 2:
			g.draw(Assets.basaha_1, (int) fruit.x - 9, (int) fruit.y);
			g.draw(Assets.basaha_2, (int) fruit.x + 8, (int) fruit.y);
			break;
		case 3:
			g.draw(Assets.sandia_1, (int) fruit.x - 9, (int) fruit.y);
			g.draw(Assets.sandia_2, (int) fruit.x + 8, (int) fruit.y);
			break;
		case 4:
			g.draw(Assets.peach_1, (int) fruit.x - 9, (int) fruit.y);
			g.draw(Assets.peach_2, (int) fruit.x + 8, (int) fruit.y);
			break;
		case 5:
			g.draw(Assets.boom, (int) fruit.x - 9, (int) fruit.y);
			g.draw(Assets.boom, (int) fruit.x + 8, (int) fruit.y);
			break;
		}

	}

	private void drawFruit(Fruit fruit, GLEx g) {
		switch (fruit.type) {
		case 0:
			g.draw(Assets.apple, (int) fruit.x, (int) fruit.y);
			break;
		case 1:
			g.draw(Assets.banana, (int) fruit.x, (int) fruit.y);
			break;
		case 2:
			g.draw(Assets.basaha, (int) fruit.x, (int) fruit.y);
			break;
		case 3:
			g.draw(Assets.sandia, (int) fruit.x, (int) fruit.y);
			break;
		case 4:
			g.draw(Assets.peach, (int) fruit.x, (int) fruit.y);
			break;
		case 5:
			g.draw(Assets.boom, (int) fruit.x, (int) fruit.y);
			break;
		}
		return;
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void draw(GLEx g) {
		g.draw(Assets.background, 0, 0);
		for (int i = 0; i < fruitGame.fruitList.size(); i++) {
			if (fruitGame.fruitList.get(i).flag)
				drawFruit(fruitGame.fruitList.get(i), g);
			else {
				drawFruit_s(fruitGame.fruitList.get(i), g);
			}
		}

	}

	@Override
	public void onLoad() {
		// 最先绘制用户界面
		fristUserDraw();
		// 添加手势绘制
		LGesture g = new LGesture();
		g.setColor(LColor.red);
		add(g);
	}

	@Override
	public void alter(LTimerContext timer) {
		fruitGame.update(timer.getMilliseconds());
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void touchDown(GameTouch e) {
		for (int j = 0; j < fruitGame.fruitList.size(); j++)
			if (fruitGame.fruitList.get(j).flag == true) {
				if (e.x() > fruitGame.fruitList.get(j).x
						&& e.x() < fruitGame.fruitList.get(j).x
								+ fruitGame.fruitList.get(j).width
						&& e.y() > fruitGame.fruitList.get(j).y
						&& e.y() < fruitGame.fruitList.get(j).y
								+ fruitGame.fruitList.get(j).height) {
					fruitGame.fruitList.get(j).flag = false;
				}
			}
	}

	@Override
	public void touchUp(GameTouch e) {

	}

	@Override
	public void touchMove(GameTouch e) {

	}

	@Override
	public void touchDrag(GameTouch e) {

		for (int j = 0; j < fruitGame.fruitList.size(); j++)
			if (fruitGame.fruitList.get(j).flag == true) {
				if (e.x() > fruitGame.fruitList.get(j).x
						&& e.x() < fruitGame.fruitList.get(j).x
								+ fruitGame.fruitList.get(j).width
						&& e.y() > fruitGame.fruitList.get(j).y
						&& e.y() < fruitGame.fruitList.get(j).y
								+ fruitGame.fruitList.get(j).height) {
					fruitGame.fruitList.get(j).flag = false;
				}
			}

	}

	@Override
	public void close() {

	}

}
