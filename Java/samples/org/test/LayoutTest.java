package org.test;

import loon.Stage;
import loon.component.LClickButton;
import loon.component.layout.HorizontalLayout;
import loon.component.layout.LayoutManager;
import loon.component.layout.VerticalLayout;
import loon.event.LTouchArea;

public class LayoutTest extends Stage {

	@Override
	public void create() {

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

		// 如果不需要自动改变对象大小，可以设置禁止改变布局大小(不过，那样就请自行设定组件大小比率)
		// layout.setChangeSize(false);
		// 执行布局,布局器为水平方式
		packLayout(HorizontalLayout.at());

		// 构建一个触屏监听
		registerTouchArea(new LTouchArea() {

			LayoutManager layout;

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
					// layout.setChangeSize(false);
					// 执行布局(若直接packLayout,则会执行全部组件,那么back按钮也会被自动布局)
					layoutElements(layout, test1, test2, test3, test4);
				}
			}

			// 让所有区域点击都有效
			@Override
			public boolean contains(float x, float y) {
				return true;
			}
		});

		add(MultiScreenTest.getBackButton(this, 0));
	}

}
