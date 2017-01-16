package org.test;

import loon.Stage;
import loon.component.table.LTable;
import loon.component.table.ListItem;
import loon.utils.TArray;

public class TableTest extends Stage {

	@Override
	public void create() {
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
		LTable table = new LTable(60, 60, 300, 300);
		table.setData(list, 100);
		add(table);
		
		add(MultiScreenTest.getBackButton(this,0));

	}
	
}
