package org.test;

import loon.LSetting;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.component.LMenu;
import loon.component.LMenu.MenuItem;
import loon.component.LMenu.MenuItemClick;
import loon.component.LToast;
import loon.component.LToast.Style;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class MenuTest extends Screen {

	@Override
	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {

		//设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));
		LFont font = LFont.getFont(18);
		LMenu panel = new LMenu(LMenu.MOVE_LEFT, "我是菜单", 120, 50);
		panel.setCellWidth(64);
		MenuItem item = panel.add("保存记录", "ball.png", new MenuItemClick() {

			@Override
			public void onClick(MenuItem item) {
				add(LToast.makeText("保存完毕", Style.SUCCESS));
			}
		});
		item.setFont(font).offsetX = 3;
		panel.add("读取记录").setFont(font).offsetX = 2;
		panel.add("离开").setFont(font).offsetX = 2;
		add(panel);

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
				return new MenuTest();
			}
		});
	}
}
