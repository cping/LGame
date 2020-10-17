package org.test;

import loon.Stage;
import loon.canvas.LColor;
import loon.component.LGesture;
import loon.component.LLabel;
import loon.events.Touched;

public class GestureTest extends Stage {

	@Override
	public void create() {

		final LGesture g = new LGesture();
		g.setColor(LColor.red);
		add(g);

		final LLabel label = addLabel("简单手势识别");
		// 居中控件
		centerOn(label);

		g.up(new Touched() {

			@Override
			public void on(float x, float y) {
				// 分析手势(手势识别需要采样,默认只能识别非常简单的几种,有需要可以自行导入采样数据)
				label.setText(g.getRecognizer().getName());
				// 自行导入
				//label.setText(g.getRecognizer("assets/rftemplates.txt",true).getName());
			}
		});

		add(MultiScreenTest.getBackButton(this, 0));
	}


}
