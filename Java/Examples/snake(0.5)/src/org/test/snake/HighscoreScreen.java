package org.test.snake;

import loon.Screen;
import loon.event.GameTouch;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class HighscoreScreen extends Screen {

	String lines[] = new String[5];

	public HighscoreScreen() {
		for (int i = 0; i < 5; i++) {
			lines[i] = "" + (i + 1) + ". " + Settings.highscores[i];
		}
	}

	@Override
	public void draw(GLEx g) {

		g.draw(Assets.background, 0, 0);
		g.draw(Assets.mainMenu, 64, 20, 0, 42, 196, 42);

		int y = 100;
		for (int i = 0; i < 5; i++) {
			drawText(g, lines[i], 20, y);
			y += 50;
		}

		g.draw(Assets.buttons, 0, 416, 64, 64, 64, 64);
	}

	public void drawText(GLEx g, String line, int x, int y) {
		int len = line.length();
		for (int i = 0; i < len; i++) {
			char character = line.charAt(i);

			if (character == ' ') {
				x += 20;
				continue;
			}

			int srcX = 0;
			int srcWidth = 0;
			if (character == '.') {
				srcX = 200;
				srcWidth = 10;
			} else {
				srcX = (character - '0') * 20;
				srcWidth = 20;
			}

			g.draw(Assets.numbers, x, y, srcX, 0, srcWidth, 32);
			x += srcWidth;
		}
	}

	@Override
	public void onLoad() {

	}

	@Override
	public void alter(LTimerContext timer) {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void touchDown(GameTouch e) {

	}

	@Override
	public void touchUp(GameTouch e) {
		if (e.x() < 64 && e.y() > 416) {
			if (Settings.soundEnabled)
				Assets.click.play();
			setScreen(new MainMenuScreen());
			return;
		}
	}

	@Override
	public void touchMove(GameTouch e) {

	}

	@Override
	public void touchDrag(GameTouch e) {

	}

	@Override
	public void resume() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void close() {

	}

}
