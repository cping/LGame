package org.test.snake;

import loon.LTexture;
import loon.Screen;
import loon.canvas.LColor;
import loon.event.GameTouch;
import loon.event.LTouchCollection;
import loon.event.LTouchLocation;
import loon.event.SysInputFactory;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class GameScreen extends Screen {

	enum GameState {
		Ready, Running, Paused, GameOver
	}

	GameState state = GameState.Ready;
	World world;
	int oldScore = 0;
	String score = "0";

	public GameScreen() {
		world = new World();
	}

	private void updateReady(LTouchCollection touchEvents) {
		if (touchEvents.size() > 0) {
			state = GameState.Running;
		}
	}

	private void updateRunning(LTouchCollection touchEvents, float deltaTime) {
		for (LTouchLocation event : touchEvents) {
			if (event.isUp()) {
				if (event.x() < 64 && event.y() < 64) {
					if (Settings.soundEnabled) {
						Assets.click.play();
					}
					state = GameState.Paused;
					return;
				}
			}
			if (event.isDown()) {
				if (event.x() < 64 && event.y() > 416) {
					world.snake.turnLeft();
				}
				if (event.x() > 256 && event.y() > 416) {
					world.snake.turnRight();
				}
			}
		}

		world.update(deltaTime);
		if (world.gameOver) {
			if (Settings.soundEnabled)
				Assets.bitten.play();
			state = GameState.GameOver;
		}
		if (oldScore != world.score) {
			oldScore = world.score;
			score = "" + oldScore;
			if (Settings.soundEnabled)
				Assets.eat.play();
		}
	}

	private void updatePaused(LTouchCollection touchEvents) {
		for (LTouchLocation event : touchEvents) {
			if (event.isUp()) {
				if (event.x() > 80 && event.x() <= 240) {
					if (event.y() > 100 && event.y() <= 148) {
						if (Settings.soundEnabled)
							Assets.click.play();
						state = GameState.Running;
						return;
					}
					if (event.y() > 148 && event.y() < 196) {
						if (Settings.soundEnabled)
							Assets.click.play();
						setScreen(new MainMenuScreen());
						return;
					}
				}
			}
		}
	}

	private void updateGameOver(LTouchCollection touchEvents) {
		for (LTouchLocation event : touchEvents) {
			if (event.isUp()) {
				if (event.x() >= 128 && event.x() <= 192 && event.y() >= 200
						&& event.y() <= 264) {
					if (Settings.soundEnabled)
						Assets.click.play();
					setScreen(new MainMenuScreen());
					return;
				}
			}
		}
	}

	private void drawWorld(GLEx g, World world) {

		Snake snake = world.snake;
		SnakePart head = snake.parts.get(0);
		Stain stain = world.stain;

		LTexture stainPixmap = null;
		if (stain.type == Stain.TYPE_1)
			stainPixmap = Assets.stain1;
		if (stain.type == Stain.TYPE_2)
			stainPixmap = Assets.stain2;
		if (stain.type == Stain.TYPE_3)
			stainPixmap = Assets.stain3;
		int x = stain.x * 32;
		int y = stain.y * 32;
		g.draw(stainPixmap, x, y);

		int len = snake.parts.size();
		for (int i = 1; i < len; i++) {
			SnakePart part = snake.parts.get(i);
			x = part.x * 32;
			y = part.y * 32;
			g.draw(Assets.tail, x, y);
		}

		LTexture headPixmap = null;
		if (snake.direction == Snake.UP)
			headPixmap = Assets.headUp;
		if (snake.direction == Snake.LEFT)
			headPixmap = Assets.headLeft;
		if (snake.direction == Snake.DOWN)
			headPixmap = Assets.headDown;
		if (snake.direction == Snake.RIGHT)
			headPixmap = Assets.headRight;
		x = head.x * 32 + 16;
		y = head.y * 32 + 16;
		g.draw(headPixmap, x - headPixmap.getWidth() / 2,
				y - headPixmap.getHeight() / 2);
	}

	private void drawReadyUI(GLEx g) {
		g.draw(Assets.ready, 47, 100);
		g.drawLine(0, 416, 480, 416, LColor.black);
	}

	private void drawRunningUI(GLEx g) {
		g.draw(Assets.buttons, 0, 0, 64, 128, 64, 64);
		g.drawLine(0, 416, 480, 416, LColor.black);
		g.draw(Assets.buttons, 0, 416, 64, 64, 64, 64);
		g.draw(Assets.buttons, 256, 416, 0, 64, 64, 64);
	}

	private void drawPausedUI(GLEx g) {
		g.draw(Assets.pause, 80, 100);
		g.drawLine(0, 416, 480, 416, LColor.black);
	}

	private void drawGameOverUI(GLEx g) {
		g.draw(Assets.gameOver, 62, 100);
		g.draw(Assets.buttons, 128, 200, 0, 128, 64, 64);
		g.drawLine(0, 416, 480, 416, LColor.black);
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
	public void pause() {
		if (state == GameState.Running)
			state = GameState.Paused;

		if (world.gameOver) {
			Settings.addScore(world.score);
			Settings.save();
		}
	}

	@Override
	public void resume() {

	}

	@Override
	public void draw(GLEx g) {
		g.draw(Assets.background, 0, 0);
		drawWorld(g, world);
		if (state == GameState.Ready) {
			drawReadyUI(g);
		}
		if (state == GameState.Running) {
			drawRunningUI(g);
		}
		if (state == GameState.Paused) {
			drawPausedUI(g);
		}
		if (state == GameState.GameOver) {
			drawGameOverUI(g);
		}
		drawText(g, score, g.getWidth() / 2 - score.length() * 20 / 2,
				g.getHeight() - 42);
	}

	@Override
	public void onLoad() {
		// 允许Loon记录触屏事件到统一的集合中(这种模式和在屏幕上直接写入touch事件中并不互相影响，只是方便用户遍历具体都触发了那些触屏操作
		// 因为比较耗费资源，默认不开启)
		SysInputFactory.startTouchCollection();
		// 最后显示用户绘制
		lastUserDraw();
	}

	@Override
	public void alter(LTimerContext timer) {
		LTouchCollection touchEvents = SysInputFactory.getTouchState();
		if (state == GameState.Ready) {
			updateReady(touchEvents);
		}
		if (state == GameState.Running) {
			updateRunning(touchEvents, timer.getMilliseconds());
		}
		if (state == GameState.Paused) {
			updatePaused(touchEvents);
		}
		if (state == GameState.GameOver) {
			updateGameOver(touchEvents);
		}

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void touchDown(GameTouch e) {

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
		SysInputFactory.stopTouchCollection();
	}
}