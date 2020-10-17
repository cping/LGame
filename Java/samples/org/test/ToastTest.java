package org.test;

import loon.Stage;
import loon.component.LClickButton;
import loon.component.LComponent;
import loon.component.LToast;
import loon.component.LToast.Style;
import loon.component.layout.HorizontalLayout;
import loon.events.ClickListener;


public class ToastTest extends Stage {

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
	public void create() {
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

		test1.S(clickListener);
		test2.S(clickListener);
		test3.S(clickListener);

		add(MultiScreenTest.getBackButton(this,0));
	}


}
