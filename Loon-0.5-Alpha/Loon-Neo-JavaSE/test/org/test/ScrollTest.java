package org.test;

import loon.LSetting;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.canvas.LColor;
import loon.component.LPaper;
import loon.component.LScrollBar;
import loon.component.LScrollContainer;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class ScrollTest extends Screen {

	public LTransition onTransition() {
		return LTransition.newPixelWind(LColor.white);
	}

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {

		//设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));
		// 构建一个滚动容器（背景图片可以自行设置）
		LScrollContainer container = new LScrollContainer(50, 50, 240, 200);
		LPaper p = new LPaper("back1.png");
		p.setLocked(false);
		container.add(p);
		
		// 添加滚轴(图片可以自行设置,具体显示位置可以矫正LScrollBar)
		container.addScrollbar(new LScrollBar(LScrollBar.RIGHT));
		container.addScrollbar(new LScrollBar(LScrollBar.BOTTOM));
		// 也可以禁止显示滚轴
		// container.setShowScroll(true);
		add(container);

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
				return new ScrollTest();
			}
		});

	}
}
