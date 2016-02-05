package org.test.snake;

import loon.BaseIO;
import loon.LTextures;
import loon.Screen;
import loon.event.GameTouch;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class MainMenuScreen extends Screen {

	@Override
	public void pause() {
		Settings.save();
	}

	@Override
	public void resume() {

	}

	@Override
	public void draw(GLEx g) {
		g.draw(Assets.background, 0, 0);
		g.draw(Assets.logo, 60, 20);
		g.draw(Assets.mainMenu, 64, 220);
		if (Settings.soundEnabled) {
			g.draw(Assets.buttons, 0, 416, 0, 0, 64, 64);
		} else {
			g.draw(Assets.buttons, 0, 416, 64, 0, 64, 64);
		}
	}

	@Override
	public void onLoad() {
		Assets.background = LTextures.loadTexture("background.png");
		Assets.logo = LTextures.loadTexture("snaketitle.png");
		Assets.mainMenu = LTextures.loadTexture("mainmenu.png");
		Assets.buttons = LTextures.loadTexture("buttons.png");
		Assets.help1 = LTextures.loadTexture("help1.png");
		Assets.help2 = LTextures.loadTexture("help2.png");
		Assets.help3 = LTextures.loadTexture("help3.png");
		Assets.numbers = LTextures.loadTexture("numbers.png");
		Assets.ready = LTextures.loadTexture("ready.png");
		Assets.pause = LTextures.loadTexture("pausemenu.png");
		Assets.gameOver = LTextures.loadTexture("gameover.png");
		Assets.headUp = LTextures.loadTexture("headup.png");
		Assets.headLeft = LTextures.loadTexture("headleft.png");
		Assets.headDown = LTextures.loadTexture("headdown.png");
		Assets.headRight = LTextures.loadTexture("headright.png");
		Assets.tail = LTextures.loadTexture("tail.png");
		Assets.stain1 = LTextures.loadTexture("stain1.png");
		Assets.stain2 = LTextures.loadTexture("stain2.png");
		Assets.stain3 = LTextures.loadTexture("stain3.png");
		Assets.click = BaseIO.loadSound("click.ogg");
		Assets.eat = BaseIO.loadSound("eat.ogg");
		Assets.bitten = BaseIO.loadSound("bitten.ogg");
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

	}

	@Override
	public void touchUp(GameTouch e) {
		// 判定是否触屏位置，与按钮位置一致，是则触发事件
		if (inBounds(e, 0, getHeight() - 64, 64, 64)) {
			Settings.soundEnabled = !Settings.soundEnabled;
			if (Settings.soundEnabled)
				Assets.click.play();
		}
		if (inBounds(e, 64, 220, 192, 42)) {
			setScreen(new GameScreen());
			if (Settings.soundEnabled)
				Assets.click.play();
			return;
		}
		if (inBounds(e, 64, 220 + 42, 192, 42)) {
			setScreen(new HighscoreScreen());
			if (Settings.soundEnabled)
				Assets.click.play();
			return;
		}
		if (inBounds(e, 64, 220 + 84, 192, 42)) {
			setScreen(new HelpScreen());
			if (Settings.soundEnabled)
				Assets.click.play();
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
	public void close() {

	}
}