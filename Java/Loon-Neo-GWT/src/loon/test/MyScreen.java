package loon.test;

import loon.Screen;
import loon.canvas.LColor;
import loon.events.GameTouch;
import loon.opengl.GLEx;
import loon.opengl.GLRenderer;
import loon.opengl.GLRenderer.GLType;
import loon.utils.timer.LTimerContext;

public class MyScreen extends Screen {

	GLRenderer renderer;

	@Override
	public void draw(GLEx g) {
		if (renderer != null) {
			renderer.begin(g.tx(), GLType.Filled);
			renderer.setColor(LColor.red);
			renderer.rect(0, 0, 480, 320);
			renderer.end();
		}
	}

	@Override
	public void onLoad() {
		renderer = new GLRenderer();
	}

	@Override
	public void alter(LTimerContext timer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchDown(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchUp(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchMove(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void touchDrag(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
