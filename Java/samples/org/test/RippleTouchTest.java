package org.test;

import loon.Stage;
import loon.action.sprite.effect.RippleEffect;

public class RippleTouchTest extends Stage {

	@Override
	public void create() {
		// 改变默认渲染顺序
		// 首先绘制桌面组件
		setSecondOrder(DRAW_DESKTOP_PAINT());
		// 其次绘制用户界面
		setFristOrder(DRAW_USER_PAINT());
		// 最后绘制精灵
		setLastOrder(DRAW_SPRITE_PAINT());
		// 构建Ripple特效并注入Screen
		RippleEffect ripple = new RippleEffect(RippleEffect.Model.RHOMBUS);
		add(ripple);
		add(MultiScreenTest.getBackButton(this,0));
	}

}
