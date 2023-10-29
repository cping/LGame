package org.test;

import loon.Stage;
import loon.component.LMenu;
import loon.component.LMenu.MenuItem;
import loon.component.LMenu.MenuItemClick;
import loon.component.LToast;
import loon.component.LToast.Style;
import loon.font.LFont;

public class MenuTest extends Stage {

	@Override
	public void create() {
		LFont font = LFont.getFont(18);
		// 左侧菜单,名称,宽120,高50
		LMenu panel = new LMenu(LMenu.MOVE_LEFT, "我是菜单", 120, 50);
		// 每个菜单子单元大小64
		panel.setCellWidth(64);
		// 所有菜单集体向左6个像素
		panel.setLeftOffsetMoveMenu(-6);
		// 字体位置偏移3个像素
		panel.add("保存记录", "ball.png", new MenuItemClick() {

			@Override
			public void onClick(MenuItem item) {
				add(LToast.makeText("保存完毕", Style.SUCCESS));
			}
		}).setFont(font).setOffsetX(3);

		panel.add("读取记录").setFont(font).offsetX = 2;
		panel.add("测试1").setFont(font).offsetX = 2;
		panel.add("测试2").setFont(font).offsetX = 2;
		panel.add("测试3").setFont(font).offsetX = 2;
		panel.add("测试4").setFont(font).offsetX = 2;
		panel.add("测试5").setFont(font).offsetX = 2;
		panel.add("测试6").setFont(font).offsetX = 2;
		panel.add("离开", "ball.png", new MenuItemClick() {

			@Override
			public void onClick(MenuItem item) {
				add(LToast.makeText("离开游戏", Style.SUCCESS));
			}
		}).setFont(font).setOffsetX(3);

		// 支持滚动菜单内容
		panel.setSupportScroll(true);
		add(panel);

		add(MultiScreenTest.getBackButton(this, 0));
	}

}
