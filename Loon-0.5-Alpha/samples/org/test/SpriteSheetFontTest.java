package org.test;

import loon.LTransition;
import loon.Screen;
import loon.action.sprite.SpriteSheet;
import loon.action.sprite.SpriteSheetFont;
import loon.canvas.LColor;
import loon.event.GameTouch;
import loon.font.LFont;
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
			font.drawString(g, "FONT EXAMPLE \nTEST NEW LINES", 80, 25, LColor.red);
			font.drawString(g, "MORE COMPLETE LINE", 80, 100);
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
}
