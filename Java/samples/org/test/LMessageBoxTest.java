package org.test;

import loon.LTexture;
import loon.Stage;
import loon.component.LComponent;
import loon.component.LMessageBox;
import loon.event.ClickListener;

public class LMessageBoxTest extends Stage {

	@Override
	public void create() {
		LTexture texture = getGameWinFrame(200, 200);
		LMessageBox box = new LMessageBox(new String[] {
				"人间谁能看尽山色，千里孤行终归寂寞。翻天覆地炙手可热，百年之后有谁记得。",
				"明月西斜遗珠何落，金乌归海乾坤并合。世事如棋造化难说，能解其中非你非我。" }, texture, 66, 66, 180,
				180);
		//行间距3
		box.setLeading(3);
		//偏移10,10
		box.setOffset(10, 10);
		add(box);
		centerOn(box);
		box.S(new ClickListener() {

			@Override
			public void UpClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DragClick(LComponent comp, float x, float y) {

			}

			@Override
			public void DownClick(LComponent comp, float x, float y) {
				LMessageBox box = (LMessageBox) comp;
				box.loop();
			}

			@Override
			public void DoClick(LComponent comp) {

			}
		});
		add(MultiScreenTest.getBackButton(this,0));
	}
}
