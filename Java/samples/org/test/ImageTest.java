package org.test;

import loon.Screen;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.component.DefUI;
import loon.event.GameTouch;
import loon.font.BMFont;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class ImageTest extends Screen {

	private Image rimage;

	private Image timage;

	private Image trimage;

	@Override
	public void draw(GLEx g) {
		if (rimage != null) {
			g.draw(rimage.texture(), 50, 36);
		}
		if (timage != null) {
			g.draw(timage.texture(), 150, 36, LColor.red);
		}
		if (trimage != null) {
			g.draw(trimage.texture(), 250, 36, LColor.gray);
		}
		// 螺旋画圈
		for (int i = 0; i < 60; i++) {
			g.setColor(LColor.red);
			g.drawOval((i * 5) + 25, (i * 3) + 25, 50, 50);
		}
		// 使用默认的bmfont
		g.setFont(BMFont.getDefaultFont());
		g.drawString(message, 0, 0, LColor.yellow);
		// 还原默认配置
		g.resetFont();
	}

	@Override
	public void onLoad() {
		// 生成椭圆头像
		rimage = DefUI.getRoundImage("tu.jpg");
		timage = rimage;
		trimage = timage;

		add(MultiScreenTest.getBackButton(this, 1));
	}

	@Override
	public void alter(LTimerContext timer) {

	}

	@Override
	public void resize(int width, int height) {

	}

	private String message;

	@Override
	public void touchDown(GameTouch e) {
		message = "Touch Screen \n" + e;
	}

	@Override
	public void touchUp(GameTouch e) {
		message = "Touch Screen \n" + e;
	}

	@Override
	public void touchMove(GameTouch e) {
		message = "Touch Screen \n" + e;
	}

	@Override
	public void touchDrag(GameTouch e) {
		message = "Touch Screen \n" + e;
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
