package org.test;

import loon.LSetting;
import loon.LTexture;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.canvas.LColor;
import loon.component.DefUI;
import loon.component.LTextArea;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class TextAreaTest extends Screen {

	@Override
	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {

		// 设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));
		// 构建一个300x100的游戏窗体背景图,颜色黑蓝相间,横向渐变
		LTexture texture = DefUI.getGameWinFrame(300, 100, LColor.black,
				LColor.blue, false);
		LTextArea area = new LTextArea(66, 66, 300, 100);
		area.setBackground(texture);
		area.put("你惊扰了【撒旦】的安眠", LColor.red);
		area.put("2333333333333", LColor.yellow);
		area.put("6666666666");
		// 偏移文字显示位置
		area.setLeftOffset(5);
		area.setTopOffset(5);
		// addString为在前一行追加数据
		// area.addString("1",LColor.red);
		add(area);
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
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "test";
		setting.emulateTouch = false;
		setting.width = 480;
		setting.height = 320;

		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new TextAreaTest();
			}
		});
	}
}
