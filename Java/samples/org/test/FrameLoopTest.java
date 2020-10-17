package org.test;

import loon.Screen;
import loon.Stage;
import loon.action.sprite.Sprite;
import loon.events.FrameLoopEvent;
import loon.utils.MathUtils;

public class FrameLoopTest extends Stage {

	@Override
	public void create() {
		
		add(MultiScreenTest.getBackButton(this,1));

		float layoutRadius = 70;
		float radianUnit = MathUtils.PI / 2;

		final Sprite role = new Sprite();
		add(role);

		// 添加4张人头图片
		for (int i = 0; i < 4; i++) {
			Sprite s = new Sprite("assets/ccc.png");

			// 以圆周排列人头
			s.setLocation(MathUtils.cos(radianUnit * i) * layoutRadius,
					MathUtils.sin(radianUnit * i) * layoutRadius);

			role.addChild(s);
		}

		role.setPivot(55, 72);
		role.setLocation(175, 75);

		// 添加一个事件到循环中
		addFrameLoop(new FrameLoopEvent() {

			// 递增旋转角度
			@Override
			public void invoke(long elapsedTime, Screen e) {
				role.setRotation(role.getRotation() + 2);
			}

			@Override
			public void completed() {

			}
		});
	}

}
