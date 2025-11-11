package org.test;

import loon.Stage;
import loon.component.LTextList;
import loon.component.LToast;
import loon.component.LToast.Style;
import loon.events.Touched;

public class ListTest extends Stage {

	@Override
	public void create() {

		final LTextList list = new LTextList(125, 125, 150, 100);
		//设定滚动按钮大小
		//list.setScrollButtonSize(50);
		//设定滚动按钮颜色
		//list.setArrowColor(LColor.red);
		//显示边框
		//list.setDrawListBorder(true);
		//边框颜色
		//list.setListBorderColor(LColor.red);
		list.add("图灵测试");
		list.add("人月神话");
		list.add("费雪效应");
		list.add("ABC");
		list.add("EFG");
		list.up(new Touched() {

			@Override
			public void on(float x, float y) {
				add(LToast.makeText(list.getSelectName(), Style.ERROR));
			}
		});

		add(list);

		add(MultiScreenTest.getBackButton(this, 0));
	}

}
