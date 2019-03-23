package org.test;

import loon.Stage;
import loon.component.LComponent;
import loon.component.LTextList;
import loon.component.LToast;
import loon.component.LToast.Style;
import loon.event.ClickListener;


public class ListTest extends Stage {

	// 制作一个按钮监听器
	private class MyClickListener implements ClickListener {

		@Override
		public void DoClick(LComponent comp) {

		}

		@Override
		public void DownClick(LComponent comp, float x, float y) {
			if (comp instanceof LTextList) {
				LTextList list = (LTextList) comp;
				add(LToast.makeText(list.getSelectName(), Style.ERROR));
			}

		}

		@Override
		public void UpClick(LComponent comp, float x, float y) {

		}

		@Override
		public void DragClick(LComponent comp, float x, float y) {

		}

	}

	@Override
	public void create() {
		
		MyClickListener clickListener = new MyClickListener();

		LTextList list = new LTextList(125, 125, 150, 100);
		list.add("图灵测试");
		list.add("人月神话");
		list.add("费雪效应");
		list.add("ABC");
		list.add("EFG");
		list.SetClick(clickListener);
		add(list);
		
		add(MultiScreenTest.getBackButton(this,0));
	}

}
