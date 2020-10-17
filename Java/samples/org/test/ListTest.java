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
