package org.test;

import loon.LSetting;
import loon.LazyLoading;
import loon.Screen;
import loon.component.LClickButton;
import loon.component.layout.HorizontalLayout;
import loon.event.GameTouch;
import loon.geom.SizeValue;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class LayoutTest extends Screen {

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {

		// 产生四个按钮(按钮大小和位置会根据布局改变，所以此处无需设置按钮大小)
		LClickButton test1 = LClickButton.make("test1");
		LClickButton test2 = LClickButton.make("test2");
		LClickButton test3 = LClickButton.make("test3");
		LClickButton test4 = LClickButton.make("test4");
		// 添加按钮
		add(test1, test2, test3, test4);

		// 设定留空大小
		getRootConstraints().setPadding(new SizeValue(50));

		// 单独设置下方间隔
		getRootConstraints().setPaddingBottom(new SizeValue(100));

		// 布局器为水平方式
		HorizontalLayout centerLayout = new HorizontalLayout();

		// 执行布局
		layoutElements(centerLayout, test1, test2, test3, test4);
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
				return new LayoutTest();
			}
		});
	}

}
