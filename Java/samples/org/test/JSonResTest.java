package org.test;

import loon.Stage;
import loon.component.LClickButton;
import loon.component.LToast;
import loon.component.layout.VerticalLayout;
import loon.utils.res.ResourceLocal;

public class JSonResTest extends Stage {

	@Override
	public void create() {
		// 加载json配置的资源文件（loon中默认识别的，是一种flash游戏开发中常见的json资源格式，很多flash小游戏都采取这种格式，方便移植，然后你懂的……）
		ResourceLocal local = RES("resource");
		LClickButton quitgame = LClickButton.make(local.getTexture("quitgame").img());
		quitgame.setGrayButton(true);

		LClickButton tryagain = LClickButton.make(local.getTexture("tryagain").img());
		tryagain.setGrayButton(true);

		LClickButton gushimoshi = LClickButton.make(local.getTexture("gushimoshi").img());
		gushimoshi.setGrayButton(true);

		// 点击后缩放按钮1.2倍迅速还原
		quitgame.up((x, y) -> {
			quitgame.selfAction().flashScaleTo(1.2f).start().dispose(() -> {
				add(LToast.makeText("选择了选项1"));
			});
		});
		tryagain.up((x, y) -> {
			tryagain.selfAction().flashScaleTo(1.2f).start().dispose(() -> {
				add(LToast.makeText("选择了选项2"));
			});
		});
		gushimoshi.up((x, y) -> {
			gushimoshi.selfAction().flashScaleTo(1.2f).start().dispose(() -> {
				add(LToast.makeText("选择了选项3"));
			});
		});
		add(quitgame, tryagain, gushimoshi);

		// 设定留空大小，分别为屏幕上方25%，右15%，下15%，左32%
		getRootConstraints().setPadding("15%", "25%", "15%", "32%");

		// 布局器为竖立方式
		VerticalLayout layout = new VerticalLayout();
		// 不需要自动改变对象大小
		layout.setChangeSize(false);
		// 执行布局
		layoutElements(layout, quitgame, tryagain, gushimoshi);

		add(MultiScreenTest.getBackButton(this, 0));
	}

}
