package application;

import loon.Stage;
import loon.canvas.LColor;
import loon.component.LClickButton;
import loon.component.table.LTable;
import loon.component.table.TableView;

public class TableTest extends Stage {

	@Override
	public void create() {
		// 设置table数据,初始页号0(页号由索引0开始),一页显示3行数据
		TableView view = new TableView(0, 3);
		// 设定表头名称
		view.columnNames("表格1", "表格2", "表格3", "表格4");
		// 设定对应列名和数据
		// view.columns("表格1", "ffffff", "gggggggg", "hhhhhhhhh");
		// view.columns("表格2", "zzzzzz", "kkkkkkkk", "xxxxxxxxx");
		// view.columns("表格3", "zzzzzz", "kkkkkkkk", "aaaaaaa");
		// view.columns("表格4", "zzzzzz", "kkkkkkkk", "bbbbbbbb");
		// view.columns("表格5", "434345", "43442111", "cdgggggg");
		// 增加一行
		view.addRows("123", "456", "999", "1010dsd");
		view.addRows("12433", "22456", "fdfdg", "fdfdgg");
		view.addRows("dsdsd", "我是table", "gdgdg", "gfgfgf");
		view.addRows("543", "fdfdsf", "gdgdg", "gfgfgf");
		view.addRows("678", "fdfdf", "gdgdg", "gfgfgf");
		view.addRows("976", "fdfdf", "gdgdg", "gfgfgf");
		// 翻页到下一页
		// view.next();
		// 修改索引行0,列2的数据
		// view.setValue(0, 2, "66666");
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

		//上一页
		LClickButton back = LClickButton.make("back", 40, getHeight() - 70, 90, 20);
		back.up((float x,float y)->view.back());
		add(back);

		// 下一页
		LClickButton next = LClickButton.make("next", 145, getHeight() - 70, 90, 20);
		next.up((float x,float y)->view.next());
		add(next);

	}

}
