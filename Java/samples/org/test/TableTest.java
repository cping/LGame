package org.test;

import loon.Stage;
import loon.canvas.LColor;
import loon.component.table.LTable;
import loon.component.table.LTable.TableView;

public class TableTest extends Stage {

	@Override
	public void create() {
		TableView view = new TableView();
		view.columns("表格1", "ffffff", "gggggggg", "hhhhhhhhh");
		view.columns("表格2", "zzzzzz", "kkkkkkkk", "xxxxxxxxx");
		view.columns("表格3", "zzzzzz", "kkkkkkkk", "xxxxxxxxx");
		view.columns("表格4", "zzzzzz", "kkkkkkkk", "xxxxxxxxx");
		LTable table = new LTable(60, 60, 300, 300);
		table.setHeadTextColor(LColor.orange);
		table.setTextColor(LColor.white);
		//table.setReadOnly(true);
		table.setData(view, 100);
		add(table);
		centerOn(table);

		add(MultiScreenTest.getBackButton(this, 0));

	}

}
