package org.test;

import loon.action.sprite.ActionObject;
import loon.action.sprite.TextureObject;
import loon.events.GameKey;
import loon.events.GameTouch;
import loon.opengl.GLEx;
import loon.physics.PyhsicsScreen;

public class PhysicalTest extends PyhsicsScreen {

	@Override
	public void onResume() {

	}

	@Override
	public void onPause() {

	}

	@Override
	public void create() {
		setPhysics(true);
		addPhysics(false, new TextureObject(166, 66, "a4.png"));
		// 获得物理世界
		// getPhysicsManager().getWorld();
		// 刷新速度30fps
		setTimeStep(1f / 30f);
		add(MultiScreenTest.getBackButton(this, 0));
	}

	@Override
	public void after(GLEx batch) {

	}

	@Override
	public void before(GLEx batch) {

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
		if (isPhysics()) {
			ActionObject o = findObject(e.getX(), e.getY());
			if (o == null) {
				addCirclePhysics(false, new TextureObject(e.x(), e.y(), "ball.png"));
			} else {
				remove(o);
			}
		}
	}

	@Override
	public void touchMove(GameTouch e) {

	}

	@Override
	public void touchDrag(GameTouch e) {

	}

}
