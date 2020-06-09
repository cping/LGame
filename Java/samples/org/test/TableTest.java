package org.test;

import loon.Stage;
import loon.canvas.LColor;
import loon.component.table.LTable;
import loon.component.table.LTable.TableView;

public class TableTest extends Stage {

	@Override
	public void create() {
		TableView view = new TableView();
		// 设定表头名称
		// view.columnNames("1","2","3","4");
		// 设定对应列名和数据
		view.columns("表格1", "ffffff", "gggggggg", "hhhhhhhhh");
		view.columns("表格2", "zzzzzz", "kkkkkkkk", "xxxxxxxxx");
		view.columns("表格3", "zzzzzz", "kkkkkkkk", "aaaaaaa");
		view.columns("表格4", "zzzzzz", "kkkkkkkk", "bbbbbbbb");
	
		// 增加一行
		view.addRows("123", "456", "999", "1010");
		// 修改索引行0,列2的数据
		view.setValue(0, 2, "66666");
		// 删除第二行
		// view.removeLine(2);
		LTable table = new LTable(60, 60, 300, 300);
		table.setHeadTextColor(LColor.orange);
		table.setTextColor(LColor.white);
		// table.setReadOnly(true);
		// 设定表格数据
		table.setData(view, 100);
		add(table);
		centerOn(table);

		add(MultiScreenTest.getBackButton(this, 0));

	}

}
