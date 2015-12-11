package org.test;

import loon.LSetting;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.component.LClickButton;
import loon.component.LComponent;
import loon.component.LToast;
import loon.component.LToast.Style;
import loon.component.layout.HorizontalLayout;
import loon.event.ClickListener;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class ToastTest extends Screen {

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
			if (comp instanceof LClickButton) {
				LClickButton click = (LClickButton) comp;
				String text = click.getText();
				if ("test1".equals(text)) {
					add(LToast.makeText(text, Style.ERROR));
				} else if ("test2".equals(text)) {
					add(LToast.makeText(text, Style.SUCCESS));
				} else if ("test3".equals(text)) {
					add(LToast.makeText(text, Style.NORMAL));
				}
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

		// 设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));
		MyClickListener clickListener = new MyClickListener();

		// 产生四个按钮(按钮大小和位置会根据布局改变，所以此处无需设置按钮大小)
		final LClickButton test1 = LClickButton.make("test1");
		final LClickButton test2 = LClickButton.make("test2");
		final LClickButton test3 = LClickButton.make("test3");

		// 添加按钮
		add(test1, test2, test3);

		// 设定留空大小，分别为屏幕上方35%，左15%，下35%，右15%
		getRootConstraints().setPadding("35%", "15%", "35%", "15%");

		// 布局器为水平方式
		HorizontalLayout layout = new HorizontalLayout();

		// 如果不需要自动改变对象大小，可以设置禁止改变布局大小(不过，那样就请自行设定组件大小比率)
		// layout.setChangeSize(false);
		// 执行布局
		layoutElements(layout, test1, test2, test3);

		test1.SetClick(clickListener);
		test2.SetClick(clickListener);
		test3.SetClick(clickListener);

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
				return new ToastTest();
			}
		});
	}

}
