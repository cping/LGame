package org.test;

import loon.LSetting;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.action.sprite.Sprite;
import loon.event.GameTouch;
import loon.event.LTouchArea;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class Test6 extends Screen {

	@Override
	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {

		// 构建一个球体的精灵
		final Sprite sprite = new Sprite("ball.png");
		add(sprite);

		// 注册一个有界限的触屏监听器
		registerTouchArea(new LTouchArea() {

			@Override
			public void onAreaTouched(Event e, float touchX, float touchY) {
				if (e == Event.DOWN) {
					// 设置一个指定精灵的动画事件
					set(sprite)
					.moveTo(touchX, touchY, false).// 地图方式，四方走法，移动到触屏位置
							fadeIn(60). // 动画淡入,速度60
							delay(2f). //延迟两秒
							fadeOut(60).// 动画淡出
							moveTo(20, 20, false). // 地图方式，四方走法，移动到20,20位置
							moveTo(220, 320, true) // 八方走法，移动到220,320位置
							.rotateTo(360) //旋转360度
							.scaleTo(2f,2f)
							.scaleTo(1f,1f)
							.start();
				}
			}

			// 不限制触屏位置
			@Override
			public boolean contains(float x, float y) {
				return true;
			}
		});

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

	public static void main(String[] args) {
		LSetting setting = new LSetting();
		setting.isFPS = true;
		setting.isLogo = false;
		setting.logoPath = "loon_logo.png";
		setting.fps = 60;
		setting.fontName = "黑体";
		setting.appName = "test";
		setting.emulateTouch = false;
		setting.width = 640;
		setting.height = 480;

		Loon.register(setting, new LazyLoading.Data() {

			@Override
			public Screen onScreen() {
				return new Test6();
			}
		});
	}

}
