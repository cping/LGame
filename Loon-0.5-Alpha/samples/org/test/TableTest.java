package org.test;

import loon.LTransition;
import loon.Screen;
import loon.component.table.LTable;
import loon.component.table.ListItem;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.opengl.GLEx;
import loon.utils.TArray;
import loon.utils.timer.LTimerContext;

public class TableTest extends Screen {

	@Override
	public LTransition onTransition() {
		return LTransition.newEmpty();
	}
	
	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {

		//设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));
		TArray<ListItem> list = new TArray<ListItem>();

		ListItem item = new ListItem();
		item.name = "表格1";
		item.list.add("ffffff");
		item.list.add("gggggggg");
		item.list.add("hhhhhhhhh");
		list.add(item);

		ListItem item2 = new ListItem();
		item2.name = "表格2";
		item2.list.add("zzzzzz");
		item2.list.add("kkkkkkkk");
		item2.list.add("xxxxxxxxx");
		list.add(item2);
		LTable table = new LTable(LFont.getDefaultFont(), 60, 60, 300, 300);
		table.setData(list, 100);
		add(table);
		
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
