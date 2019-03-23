package org.test;

import loon.LTransition;
import loon.Stage;
import loon.canvas.LColor;
import loon.component.LLabel;
import loon.component.LMenuSelect;
import loon.component.LPaper;
import loon.component.LScrollContainer;

public class ScrollTest extends Stage {

	public LTransition onTransition() {
		return LTransition.newPixelWind(LColor.white);
	}

	@Override
	public void create() {
		// 构建一个滚动容器（背景图片可以自行设置）
		LScrollContainer container = new LScrollContainer(50, 50, 240, 200);
		LPaper p = new LPaper("assets/back1.png");
		p.setLocked(false);
		container.add(p);
		container.add(LLabel.make("我是一个毫无存在感的文字"),55,55);
		container.add(LMenuSelect.make("菜单一,菜单二,特工86"),100,80);
		// 添加滚轴(图片可以自行设置,具体显示位置可以矫正LScrollBar),如果不填,容器内部组件大于容器时也会自行添加滚轴
		//container.addScrollbar(new LScrollBar(LScrollBar.RIGHT));
		//container.addScrollbar(new LScrollBar(LScrollBar.BOTTOM));
		// 也可以禁止显示滚轴
		// container.setShowScroll(true);
		add(container);

		add(MultiScreenTest.getBackButton(this,0));
	}

}
