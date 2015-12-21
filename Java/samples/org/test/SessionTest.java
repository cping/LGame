package org.test;

import loon.Screen;
import loon.Session;
import loon.component.LLabel;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class SessionTest extends Screen {

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {

		// 设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));
		
		Session session = Session.load("session_test");
		int count = session.getInt("count", 0);
		LLabel lab = LLabel.make("你是第" + count + "次访问此Screen", 66, 66);
		add(lab);
		session.set("count", count += 1);
		session.save();

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
