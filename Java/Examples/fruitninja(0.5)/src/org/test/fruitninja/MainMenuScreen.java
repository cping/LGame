package org.test.fruitninja;

import loon.LTextures;
import loon.Screen;
import loon.event.GameTouch;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class MainMenuScreen extends Screen {

	public MainMenuScreen() {

	}

	@Override
	public void pause() {
		Settings.save();
	}

	@Override
	public void resume() {

	}

	private float rotation = 0;

	@Override
	public void draw(GLEx g) {
		rotation += (super.elapsedTime / 10000f);
		if (rotation > 1f) {
			rotation = 0;
		}
		float angle = 360 * rotation;
		g.draw(Assets.background, 0, 0);
		g.draw(Assets.home_make, 0, 0);
		g.draw(Assets.logo, 5, 5);
		g.draw(Assets.quit, 480, 320, angle);
		g.draw(Assets.boom, 517, 360);
		g.draw(Assets.new_game, 250, 250, angle);
		g.draw(Assets.sandia, 300, 300);
		g.draw(Assets.dojo, 30, 280, angle);
		g.draw(Assets.peach, 85, 330);
	}

	@Override
	public void onLoad() {

		Assets.background = LTextures.loadTexture("background.jpg");
		Assets.logo = LTextures.loadTexture("logo.png");
		Assets.home_make = LTextures.loadTexture("home-mask.png");
		Assets.$new = LTextures.loadTexture("new.png");
		Assets.developing = LTextures.loadTexture("developing.png");
		Assets.dojo = LTextures.loadTexture("dojo.png");
		Assets.gameover = LTextures.loadTexture("game-over.png");
		Assets.quit = LTextures.loadTexture("quit.png");
		Assets.ninja = LTextures.loadTexture("ninja.png");
		Assets.lose = LTextures.loadTexture("lose.png");
		Assets.new_game = LTextures.loadTexture("new-game.png");
		Assets.shadow = LTextures.loadTexture("shadow.png");

		Assets.x = LTextures.loadTexture("x.png");
		Assets.xx = LTextures.loadTexture("xx.png");
		Assets.xxx = LTextures.loadTexture("xxx.png");
		Assets.xf = LTextures.loadTexture("xf.png");
		Assets.xxf = LTextures.loadTexture("xxf.png");
		Assets.xxxf = LTextures.loadTexture("xxxf.png");

		Assets.number_0 = LTextures.loadTexture("number_0.png");
		Assets.number_1 = LTextures.loadTexture("number_1.png");
		Assets.number_2 = LTextures.loadTexture("number_2.png");
		Assets.number_3 = LTextures.loadTexture("number_3.png");
		Assets.number_4 = LTextures.loadTexture("number_4.png");
		Assets.number_5 = LTextures.loadTexture("number_5.png");
		Assets.number_6 = LTextures.loadTexture("number_6.png");
		Assets.number_7 = LTextures.loadTexture("number_7.png");
		Assets.number_8 = LTextures.loadTexture("number_8.png");
		Assets.number_9 = LTextures.loadTexture("number_9.png");

		Assets.boom = LTextures.loadTexture("boom.png");
		Assets.apple = LTextures.loadTexture("apple.png");
		Assets.apple_1 = LTextures.loadTexture("apple-1.png");
		Assets.apple_2 = LTextures.loadTexture("apple-2.png");
		Assets.banana = LTextures.loadTexture("banana.png");
		Assets.banana_1 = LTextures.loadTexture("banana-1.png");
		Assets.banana_2 = LTextures.loadTexture("banana-2.png");
		Assets.basaha = LTextures.loadTexture("basaha.png");
		Assets.basaha_1 = LTextures.loadTexture("basaha-1.png");
		Assets.basaha_2 = LTextures.loadTexture("basaha-2.png");
		Assets.peach = LTextures.loadTexture("peach.png");
		Assets.peach_1 = LTextures.loadTexture("peach-1.png");
		Assets.peach_2 = LTextures.loadTexture("peach-2.png");
		Assets.sandia = LTextures.loadTexture("sandia.png");
		Assets.sandia_1 = LTextures.loadTexture("sandia-1.png");
		Assets.sandia_2 = LTextures.loadTexture("sandia-2.png");

		Settings.load();

	}

	@Override
	public void alter(LTimerContext timer) {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void touchDown(GameTouch e) {
		if (inBounds(e, 85, 330, 147, 389)) {
			// empty
			return;
		}
		if (inBounds(e, 300, 300, 398, 385)) {
			setScreen(new GameScreen());
			return;
		}
		if (inBounds(e, 517, 360, 583, 428)) {
			// empty
			return;
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

	}

	@Override
	public void close() {

	}
}
