package org.test;

import loon.LGame;
import loon.LKey;
import loon.LSetting;
import loon.LTouch;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatchObject;
import loon.action.sprite.SpriteBatchScreen;
import loon.action.sprite.TextureObject;

public class PPTest extends SpriteBatchScreen {

	public static void main(String[] args) {
		LSetting setting = new LSetting();
		setting.showFPS = true;
		setting.showLogo = true;
		LGame.register(setting, PPTest.class);
	}

	@Override
	public void create() {
		setPhysics(true);
		addPhysics(false, new TextureObject(166, 66, "assets/a4.png"));
		//获得物理世界
		//getPhysicsManager().getWorld();
        setTimeStep(1F/30F);
	}

	@Override
	public void after(SpriteBatch batch) {

	}

	@Override
	public void before(SpriteBatch batch) {

	}

	@Override
	public void press(LKey e) {

	}

	@Override
	public void release(LKey e) {

	}

	@Override
	public void update(long elapsedTime) {

	}

	@Override
	public void close() {

	}

	@Override
	public void touchDown(LTouch e) {
		if (isPhysics()) {
			SpriteBatchObject o = findObject(e.getX(), e.getY());
			if (o == null) {
				addCirclePhysics(false, new TextureObject(e.x(), e.y(),
						"assets/ball.png"));
			} else {
				remove(o);
			}
		}
	}

	@Override
	public void touchUp(LTouch e) {

	}

	@Override
	public void touchMove(LTouch e) {

	}

	@Override
	public void touchDrag(LTouch e) {

	}

}
