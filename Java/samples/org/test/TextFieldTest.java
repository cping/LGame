package org.test;

import loon.Stage;
import loon.component.LTextField;

public class TextFieldTest extends Stage {

	@Override
	public void create() {
		LTextField f = new LTextField("", 66, 66);
		//没有背景框
		//f.setHideBackground(true);
		//最多输入触及到150个像素的宽度后换行
		f.setMaxWidth(150);
		//最多允许输入32个字符
		f.setLimit(32);
		add(f);
		add(MultiScreenTest.getBackButton(this, 1));

	}

}
