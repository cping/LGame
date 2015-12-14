package org.test;

import loon.LSetting;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.action.sprite.SpriteSheet;
import loon.action.sprite.SpriteSheetFont;
import loon.canvas.LColor;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class SpriteSheetFontTest extends Screen {

	SpriteSheetFont font;

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {
		if (font != null) {
			font.drawString(g, 80, 25, "FONT EXAMPLE", LColor.red);
			font.drawString(g, 80, 50, "MORE COMPLETE LINE");
		}
	}

	@Override
	public void onLoad() {
		// 设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));
		SpriteSheet sheet = new SpriteSheet("spriteSheetFont.png", 32, 32);
		font = new SpriteSheetFont(sheet, ' ');
		font.setFontScale(0.5f);
		add(MultiScreenTest.getBackButton(this));
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

	public static void main(String[] args) {

		LSetting setting = new LSetting();
		setting.isFPS = true;
		setting.isLogo = false;
		setting.logoPath = "loon_logo.png";
		setting.width = 480;
		setting.height = 320;
		setting.width_zoom = 640;
		setting.height_zoom = 480;
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "Live2dTest";
		setting.emulateTouch = false;
		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new SpriteSheetFontTest();
			}
		});

	}
}
