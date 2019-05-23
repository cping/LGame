package org.test;

import loon.Stage;
import loon.action.sprite.AnimatedEntity;
import loon.action.sprite.AnimatedEntity.PlayIndex;
import loon.action.sprite.MoveControl;
import loon.component.LControl;
import loon.component.LControl.DigitalListener;

public class ControlTest extends Stage {

	@Override
	public void create() {
		// 构建精灵以70x124的大小拆分图片，放置在坐标位置300x60,显示大小宽70,高124
		final AnimatedEntity hero = new AnimatedEntity("assets/rpg/sword.png", 70, 124, 300, 60, 70, 124);
		// 播放动画,速度每帧220
		final long[] frames = { 220, 220, 220, 220 };
		// 绑定字符串和帧索引关系,左右下上以及斜角(等距视角)上下左右共8方向的帧播放顺序(也可以理解为具体播放的帧)
		// PlayIndex的作用是序列化帧,注入每帧播放时间以及播放帧的顺序,比如4,7就是播放索引号4,5,6,7这4帧
		hero.setPlayIndex("tleft", PlayIndex.at(frames, 4, 7));
		hero.setPlayIndex("tright", PlayIndex.at(frames, 8, 11));
		hero.setPlayIndex("tdown", PlayIndex.at(frames, 0, 3));
		hero.setPlayIndex("tup", PlayIndex.at(frames, 12, 15));
		hero.setPlayIndex("left", PlayIndex.at(frames, 24, 27));
		hero.setPlayIndex("right", PlayIndex.at(frames, 20, 23));
		hero.setPlayIndex("down", PlayIndex.at(frames, 16, 19));
		hero.setPlayIndex("up", PlayIndex.at(frames, 28, 31));
		// 播放绑定到down的动画帧
		hero.animate("tdown");

		// 注入精灵到Screen
		add(hero);

		// 构架移动控制器,注入控制的角色
		final MoveControl moveControl = new MoveControl(hero);
		// 启动控制器
		moveControl.start();
		// 注销窗体时关闭移动控制器
		putRelease(moveControl);

		final LControl c = new LControl(66, 66);

		c.setControl(new DigitalListener() {

			@Override
			public void up45() {
				// 如果上一个方向不是斜角up,则指定指定帧播放(避免动画反复初始化)
				if (!c.isLastUp()) {
					hero.animate("up");
				}
				moveControl.upIso();
			}

			@Override
			public void up() {
				if (!c.isLastTUp()) {
					hero.animate("tup");
				}
				moveControl.tup();
			}

			@Override
			public void right45() {
				if (!c.isLastRight()) {
					hero.animate("right");
				}
				moveControl.rightIso();
			}

			@Override
			public void right() {
				if (!c.isLastTRight()) {
					hero.animate("tright");
				}
				moveControl.tright();
			}

			@Override
			public void left45() {
				if (!c.isLastLeft()) {
					hero.animate("left");
				}
				moveControl.leftIso();
			}

			@Override
			public void left() {
				if (!c.isLastTLeft()) {
					hero.animate("tleft");
				}
				moveControl.tleft();
			}

			@Override
			public void down45() {
				if (!c.isLastDown()) {
					hero.animate("down");
				}
				moveControl.downIso();
			}

			@Override
			public void down() {
				if (!c.isLastTDown()) {
					hero.animate("tdown");
				}
				moveControl.tdown();
			}
		});
		add(c);

		add(MultiScreenTest.getBackButton(this, 0));
	}

}
