package org.test;

import loon.Screen;
import loon.component.LClickButton;
import loon.component.layout.HorizontalLayout;
import loon.component.layout.LayoutManager;
import loon.component.layout.VerticalLayout;
import loon.event.GameTouch;
import loon.event.LTouchArea;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class LayoutTest extends Screen {

	private LayoutManager layout;

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {

		//设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));
		// 产生四个按钮(按钮大小和位置会根据布局改变，所以此处无需设置按钮大小)
		final LClickButton test1 = LClickButton.make("test1");
		final LClickButton test2 = LClickButton.make("test2");
		final LClickButton test3 = LClickButton.make("test3");
		final LClickButton test4 = LClickButton.make("test4");
		// 添加按钮
		add(test1, test2, test3, test4);

		// 设定留空大小
		getRootConstraints().setPadding(50);

		// 单独设置下方间隔
		getRootConstraints().setPaddingBottom(100);

		// 布局器为水平方式
		layout = new HorizontalLayout();
		//如果不需要自动改变对象大小，可以设置禁止改变布局大小(不过，那样就请自行设定组件大小比率)
		//layout.setChangeSize(false);
		// 执行布局
		layoutElements(layout, test1, test2, test3, test4);

		// 构建一个触屏监听
		registerTouchArea(new LTouchArea() {

			@Override
			public void onAreaTouched(Event e, float touchX, float touchY) {

				// 当触发屏幕点击时
				if (e == Event.DOWN) {
					// 每次点击转换一次布局方式
					if (layout instanceof HorizontalLayout) {
						layout = new VerticalLayout();
					} else {
						layout = new HorizontalLayout();
					}
					//layout.setChangeSize(false);
					// 执行布局
					layoutElements(layout, test1, test2, test3, test4);
				}
			}

			// 让所有区域点击都有效
			@Override
			public boolean contains(float x, float y) {
				return true;
			}
		});
		
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
