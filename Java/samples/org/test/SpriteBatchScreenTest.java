package org.test;

import loon.LTexture;
import loon.LTransition;
import loon.action.sprite.SpriteBatch;
import loon.events.GameKey;
import loon.events.GameTouch;
import loon.physics.SpriteBatchScreen;

public class SpriteBatchScreenTest extends SpriteBatchScreen {

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void onResume() {

	}

	@Override
	public void onPause() {

	}

	@Override
	public void create() {

		add(MultiScreenTest.getBackButton(this,0));
	}

	LTexture tex = LTexture.createTexture("back1.png");

	@Override
	public void after(SpriteBatch batch) {
		batch.draw(tex, 66, 66);
		batch.drawRect(55, 55, 100, 100);
		batch.drawRect(255, 155, 300, 300);

	}

	@Override
	public void before(SpriteBatch batch) {

	}

	@Override
	public void press(GameKey e) {

	}

	@Override
	public void release(GameKey e) {

	}

	@Override
	public void update(long elapsedTime) {

	}

	@Override
	public void dispose() {

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

}
