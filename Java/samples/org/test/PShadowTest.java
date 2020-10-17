package org.test;

import loon.Screen;
import loon.Stage;
import loon.action.sprite.effect.PShadowEffect;
import loon.component.LClickButton;
import loon.events.FrameLoopEvent;

public class PShadowTest extends Stage {

	private int pshadowIndex = 0;

	@Override
	public void create() {
		pshadowIndex = 0;
		//背景
		setBackground("assets/back1.png");
		//特效图
		final String[] list = { "battle0.png", "battle1.png", "battle2.png", "battle3.png", "battle4.png", "battle5.png" };
		final LClickButton click = MultiScreenTest.getBackButton(this, 1);
		// 图片型渐变特效
		final PShadowEffect p = new PShadowEffect(list[pshadowIndex++]);
		add(p);
		// 循环监听
		addFrameLoop(new FrameLoopEvent() {

			@Override
			public void invoke(long elapsedTime, Screen e) {
                //特效播放完毕
				if (p.isComplete()) {
					if (pshadowIndex >= list.length) {
						// 当渐变完毕，执行退出按钮
						click.getClick().DoClick(click);
						// 删除监听
						kill();
					} else {
						//替换特效图片
						p.setEffect(list[pshadowIndex++]);
					}
				}
			}

			@Override
			public void completed() {

			}
		});
		add(click);
	}

}
