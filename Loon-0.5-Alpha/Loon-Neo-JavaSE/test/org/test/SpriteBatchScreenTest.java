package org.test;

import loon.LSetting;
import loon.LTexture;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatchScreen;
import loon.event.GameKey;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.javase.Loon;

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

		// 设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));
		add(MultiScreenTest.getBackButton(this));
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

	public static void main(String[] args) {
		LSetting setting = new LSetting();
		setting.isFPS = true;
		setting.isLogo = false;
		setting.logoPath = "loon_logo.png";
		setting.fps = 60;
		setting.width_zoom = 640;
		setting.height_zoom = 480;
		setting.fontName = "黑体";
		setting.appName = "test";
		setting.emulateTouch = false;
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new SpriteBatchScreenTest();
			}
		});
	}
}
