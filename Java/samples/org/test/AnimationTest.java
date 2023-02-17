package org.test;

import loon.LTransition;
import loon.Screen;
import loon.Stage;
import loon.action.sprite.Animation;
import loon.action.sprite.Sprite;
import loon.action.sprite.SpriteLabel;
import loon.canvas.LColor;
import loon.events.FrameLoopEvent;
import loon.font.Font.Style;
import loon.utils.MathUtils;

public class AnimationTest extends Stage {

	int WIDTH = 110;
	int HEIGHT = 110;

	String[] character1 = { "ani/yd-6_01.png", "ani/yd-6_02.png", "ani/yd-6_03.png", "ani/yd-6_04.png",
			"ani/yd-6_05.png", "ani/yd-6_06.png", "ani/yd-6_07.png", "ani/yd-6_08.png", };
	String[] character2 = { "ani/yd-3_01.png", "ani/yd-3_02.png", "ani/yd-3_03.png", "ani/yd-3_04.png",
			"ani/yd-3_05.png", "ani/yd-3_06.png", "ani/yd-3_07.png", "ani/yd-3_08.png", };
	String[] character3 = { "ani/yd-2_01.png", "ani/yd-2_02.png", "ani/yd-2_03.png", "ani/yd-2_04.png",
			"ani/yd-2_05.png", "ani/yd-2_06.png", "ani/yd-2_07.png", "ani/yd-2_08.png", };
	String[] character4 = { "ani/wyd-1_01.png", "ani/wyd-1_02.png", "ani/wyd-1_03.png", "ani/wyd-1_04.png",
			"ani/wyd-1_05.png", "ani/wyd-1_06.png", "ani/wyd-1_07.png", "ani/wyd-1_08.png", };

	String[][] characterSkins = { character1, character2, character3, character4 };

	// Screen切换特效为像素画风
	public LTransition onTransition() {
		return LTransition.newPixelWind(LColor.white);
	}

	// 单独构建一个角色类
	class Character extends Sprite {

		public int speed;

		public Character(int idx, Animation ani) {
			this.setAnimation(ani);
			this.createBloodBar();
			this.createNameLabel(idx);
		}

		@Override
		public void onUpdate(long e) {
			// 以指定速度向右移动
			move_right(this.speed);
			if (this.getX() >= getScreenWidth() + WIDTH) {
				this.setX(-WIDTH);
			}
		}

		public void createNameLabel(int idx) {
			SpriteLabel label = new SpriteLabel(String.valueOf(idx), 0, 0);
			label.setFont("Dialog", Style.PLAIN, 10);
			centerOn(label);
			label.setY(0);
			label.setOffsetX(4);
			this.addChild(label);
		}

		public void createBloodBar() {
			Sprite bloodBar = new Sprite("ani/blood_1_r.png");
			bloodBar.setX(20);
			this.addChild(bloodBar);
		}
	}

	@Override
	public void create() {

		add(MultiScreenTest.getBackButton(this, 1));

		setBackground("ani/background.png");
		// 每隔两秒执行一次
		addFrameLoop(2, new FrameLoopEvent() {

			@Override
			public void invoke(long elapsedTime, Screen e) {

				int amount = 100;
				String[] charSkin;
				Character chr;
				// 添加一组精灵
				for (int i = 0; i < amount; i++) {
					int idx = MathUtils.random(0, characterSkins.length - 1);
					charSkin = characterSkins[idx];
					// 导入精灵图，速度70
					chr = new Character(i, Animation.getDefaultAnimation(charSkin, 70));
					chr.setTag("" + i);
					chr.setLocation(MathUtils.random() * (getWidth() + WIDTH * 2),
							MathUtils.random() * (getHeight() - HEIGHT));
					chr.setZOrder(chr.y());
					chr.speed = MathUtils.round(MathUtils.random() * 2 + 3);

					add(chr);
				}
				// 执行以后kill掉当前loop事件
				kill();

			}

			@Override
			public void completed() {

			}
		});

	}

}
