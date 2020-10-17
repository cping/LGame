package org.test;

import loon.Stage;
import loon.action.sprite.NumberSprite;
import loon.canvas.LColor;
import loon.component.LClickButton;
import loon.events.Touched;
import loon.utils.timer.CountdownTimer;

public class CountdownTest extends Stage {

	@Override
	public void create() {

		add(MultiScreenTest.getBackButton(this, 1));
		//设置默认倒计时器,倒数30秒
		final CountdownTimer timer = new CountdownTimer(30);
		// 不显示毫秒,只显示秒
		// final CountdownTimer timer = new CountdownTimer(30,false);
		// 以CountdownTimer设置NumberSprite内容,显示色彩白色,构成数字的每块小格像素大小5（渲染为3x6的像素块）
		NumberSprite sprite = new NumberSprite(timer, LColor.white, 5);
		centerOn(sprite);
		add(sprite);
		add(new LClickButton("30 Play", 50, 50, 100, 50).up(new Touched() {

			@Override
			public void on(float x, float y) {
				timer.play(30);
			}
		}));
		add(new LClickButton("60 Play", 50, 180, 100, 50).up(new Touched() {

			@Override
			public void on(float x, float y) {
				timer.play(60);
			}
		}));
	}

}
