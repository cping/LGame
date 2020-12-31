package org.test;

import loon.LTransition;
import loon.Screen;
import loon.events.GameTouch;
import loon.font.BMFont;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class BMFontTest extends Screen {

	BMFont font;

	@Override
	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {
		if (font != null) {
			font.drawString(g, "ABCF GHAX", 60, 66 - font.getAscent());
			font.drawString(g, "ZXXC\n01234", 60, 166);
			font.drawString("Cache ZXXC\n01234", 160, 200);
		}
		g.drawString("Cache ABCFGHAX", 160, 66 - g.getFont().getAscent());
	}

	@Override
	public void onLoad() {
		try {
			font = new BMFont("info.fnt", "info.png");
			// 放大2倍
			// font.setFontScale(2f);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
