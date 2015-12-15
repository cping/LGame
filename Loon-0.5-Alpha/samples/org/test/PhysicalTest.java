package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatchObject;
import loon.action.sprite.SpriteBatchScreen;
import loon.action.sprite.TextureObject;
import loon.event.GameKey;
import loon.event.GameTouch;
import loon.font.LFont;

public class PhysicalTest extends SpriteBatchScreen {

	@Override
	public void onResume() {

	}

	@Override
	public void onPause() {

	}

	@Override
	public void create() {

		// 设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));
		setPhysics(true);
		addPhysics(false, new TextureObject(166, 66, "a4.png"));
		// 获得物理世界
		// getPhysicsManager().getWorld();
		// 刷新速度30fps
		setTimeStep(1f / 30f);
		add(MultiScreenTest.getBackButton(this));
	}

	@Override
	public void after(SpriteBatch batch) {

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
		if (isPhysics()) {
			SpriteBatchObject o = findObject(e.getX(), e.getY());
			if (o == null) {
				addCirclePhysics(false, new TextureObject(e.x(), e.y(),
						"ball.png"));
			} else {
				remove(o);
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

	}
	/*
	 * public static void main(String[] args) { LSetting setting = new
	 * LSetting(); setting.isFPS = true; setting.isLogo = false;
	 * setting.logoPath = "loon_logo.png"; // 原始大小 setting.width = 480;
	 * setting.height = 320; // 缩放为 setting.width_zoom = 640;
	 * setting.height_zoom = 480; setting.fps = 60; setting.fontName = "黑体";
	 * setting.appName = "test"; setting.emulateTouch = false;
	 * Loon.register(setting, new LazyLoading.Data() {
	 * 
	 * @Override public Screen onScreen() { return new PhysicalTest(); } }); }
	 */
}
