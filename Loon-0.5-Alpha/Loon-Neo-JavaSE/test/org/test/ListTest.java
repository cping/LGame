package org.test;

import loon.LSetting;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.component.LComponent;
import loon.component.LTextList;
import loon.component.LToast;
import loon.component.LToast.Style;
import loon.event.ClickListener;
import loon.event.GameTouch;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class ListTest extends Screen {

	@Override
	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {

	}

	// 制作一个按钮监听器
	private class MyClickListener implements ClickListener {

		@Override
		public void DoClick(LComponent comp) {

		}

		@Override
		public void DownClick(LComponent comp, float x, float y) {
			if (comp instanceof LTextList) {
				LTextList list = (LTextList) comp;
				add(LToast.makeText(list.getSelectName(), Style.ERROR));
			}

		}

		@Override
		public void UpClick(LComponent comp, float x, float y) {

		}

		@Override
		public void DragClick(LComponent comp, float x, float y) {

		}

	}

	@Override
	public void onLoad() {

		MyClickListener clickListener = new MyClickListener();

		LTextList list = new LTextList(125, 125, 150, 100);
		list.add("图灵测试");
		list.add("人月神话");
		list.add("费雪效应");
		list.add("ABC");
		list.add("EFG");
		list.SetClick(clickListener);
		add(list);
		
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
				return new ListTest();
			}
		});
	}

}
