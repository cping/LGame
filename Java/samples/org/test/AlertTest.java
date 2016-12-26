package org.test;

import loon.Stage;
import loon.canvas.LColor;
import loon.component.LComponent;
import loon.component.LToast;
import loon.component.LWindow;
import loon.component.LToast.Style;
import loon.event.CallFunction;
import loon.font.LFont;

public class AlertTest extends Stage {

	@Override
	public void create() {

		setBackground(LColor.red);

		// 弹出alert,标题为测试,按钮选择1,2,3三个,坐标66,66,大小330x200,注册ABC的按钮事件
		LWindow.alert("测试中", "选择1", "选择2", "选择3", 66, 66, 330, 200,
				new CallFunction() {

					@Override
					public void call(LComponent c) {
						add(LToast.makeText("1", Style.SUCCESS));

					}
				}, new CallFunction() {

					@Override
					public void call(LComponent c) {
						add(LToast.makeText("2", Style.SUCCESS));

					}
				}, new CallFunction() {

					@Override
					public void call(LComponent c) {
						add(LToast.makeText("3", Style.SUCCESS));
					}
					// 按钮横排,禁止拖拽
				}, false).setLocked(true);
		
		// 弹出alert,标题,按钮选择AB,坐标266,166,大小150x150,注册A的按钮事件
		LWindow.alert("测试2","选择A","选择B", 266, 166, 150, 150, new CallFunction() {

			@Override
			public void call(LComponent c) {
				add(LToast.makeText("A", Style.SUCCESS));

			}
			// 按钮竖排,不禁止拖拽
		}, new CallFunction() {
			
			@Override
			public void call(LComponent comp) {
				
			}
		}, true).setLocked(false);
		
		// 弹出alert,无标题,按钮选择A,坐标66,66,大小150x150,注册A的按钮事件
		LWindow.alert("选择A", 166, 66, 150, 150, new CallFunction() {

			@Override
			public void call(LComponent c) {
				add(LToast.makeText("A", Style.SUCCESS));

			}
			// 按钮竖排,不禁止拖拽
		}, true).setLocked(false);
		LFont.setDefaultFont(LFont.getFont(20));
		add(MultiScreenTest.getBackButton(this, 1));
		
	}

}
