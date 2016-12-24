package org.test;

import loon.LSetting;
import loon.LSystem;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.action.sprite.Animation;
import loon.action.sprite.Sprite;
import loon.action.sprite.SpriteLabel;
import loon.canvas.LColor;
import loon.event.FrameLoopEvent;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.font.Font.Style;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.timer.LTimerContext;

public class AnimationTest extends Screen {

	int WIDTH = 110;
	int HEIGHT = 110;

	String[] character1 = { "ani/yd-6_01.png", "ani/yd-6_02.png",
			"ani/yd-6_03.png", "ani/yd-6_04.png",
			"ani/yd-6_05.png", "ani/yd-6_06.png",
			"ani/yd-6_07.png", "ani/yd-6_08.png", };
	String[] character2 = { "ani/yd-3_01.png", "ani/yd-3_02.png",
			"ani/yd-3_03.png", "ani/yd-3_04.png",
			"ani/yd-3_05.png", "ani/yd-3_06.png",
			"ani/yd-3_07.png", "ani/yd-3_08.png", };
	String[] character3 = { "ani/yd-2_01.png", "ani/yd-2_02.png",
			"ani/yd-2_03.png", "ani/yd-2_04.png",
			"ani/yd-2_05.png", "ani/yd-2_06.png",
			"ani/yd-2_07.png", "ani/yd-2_08.png", };
	String[] character4 = { "ani/wyd-1_01.png", "ani/wyd-1_02.png",
			"ani/wyd-1_03.png", "ani/wyd-1_04.png",
			"ani/wyd-1_05.png", "ani/wyd-1_06.png",
			"ani/wyd-1_07.png", "ani/wyd-1_08.png", };

	String[][] characterSkins = { character1, character2, character3,
			character4 };
	
	//Screen切换特效为像素画风
	public LTransition onTransition(){
		return LTransition.newPixelWind(LColor.white);
	}

	//单独构建一个角色类
	class Character extends Sprite {

		public int speed;

		public Character(int idx, Animation ani) {
			this.setAnimation(ani);
			this.createBloodBar();
			this.createNameLabel(idx);
		}

		@Override
		public void onUpdate(long e) {
			//以指定速度向右移动
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
			label.setOffset(10,-2);
			this.addChild(label);
		}

		public void createBloodBar() {
			Sprite bloodBar = new Sprite("ani/blood_1_r.png");
			bloodBar.setX(20);
			this.addChild(bloodBar);
		}
	}

	@Override
	public void onLoad() {


		//设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));
		
		add(MultiScreenTest.getBackButton(this,1));

		setBackground("ani/background.png");
		// 每隔两秒执行一次
		addFrameLoop(2, new FrameLoopEvent() {

			@Override
			public void invoke(long elapsedTime, Screen e) {

				int amount = 100;
				String[] charSkin;
				Character chr;
				//添加一组精灵
				for (int i = 0; i < amount; i++) {
					int idx = MathUtils.random(0, characterSkins.length - 1);
					charSkin = characterSkins[idx];
					//导入精灵图，速度70
					chr = new Character(i, Animation.getDefaultAnimation(
							charSkin, 70));

					chr.setLocation(MathUtils.random()
							* (getWidth() + WIDTH * 2), MathUtils.random()
							* (getHeight() - HEIGHT));
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

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void alter(LTimerContext timer) {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void touchDown(GameTouch e) {

	}

	@Override
	public void touchUp(GameTouch e) {

	}

	@Override
	public void touchMove(GameTouch e) {

	}

	@Override
	public void touchDrag(GameTouch e) {

	}

	@Override
	public void resume() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void close() {

	}
}
