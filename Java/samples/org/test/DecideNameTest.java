package org.test;

import loon.Stage;
import loon.component.LDecideName;
import loon.utils.TArray;

public class DecideNameTest extends Stage {

	@Override
	public void create() {

		TArray<String> list = new TArray<String>();
		list.add("赵钱孙李周吴郑王");
		list.add("冯陈褚卫蒋沈韩杨");
		list.add("朱秦尤许何吕施张");
		list.add("孔曹严华金魏陶姜");
		list.add("龙虎狮豹鹰鹏麒麟");
		list.add("<>");
		LDecideName decideName = new LDecideName(list, 0, 0);
		decideName.setLabelOffsetY(10);
		decideName.setLabelName("角色名:");
		decideName.setLeftOffset(20);
		decideName.setTopOffset(50);
		centerOn(decideName);
		add(decideName);

		add(MultiScreenTest.getBackButton(this,0));
	}

}
