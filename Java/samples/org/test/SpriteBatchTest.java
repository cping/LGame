package org.test;

import loon.LTexture;
import loon.LTransition;
import loon.Screen;
import loon.action.sprite.SpriteBatch;
import loon.event.GameTouch;
import loon.geom.Circle;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class SpriteBatchTest extends Screen {

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	private LTexture texture = LTexture.createTexture("ball.png");

	@Override
	public void draw(GLEx g) {
		SpriteBatch batch = new SpriteBatch();
		batch.begin();
		batch.fill(new Circle(155, 55, 100));
		batch.draw(texture, 127, 127);
		batch.end();
	}

	@Override
	public void onLoad() {

		add(MultiScreenTest.getBackButton(this,0));
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
