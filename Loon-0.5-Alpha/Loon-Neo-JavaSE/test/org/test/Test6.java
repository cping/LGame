package org.test;

import loon.LSetting;
import loon.LTransition;
import loon.LazyLoading;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.ActionListener;
import loon.action.ActionTween;
import loon.action.ActionType;
import loon.action.sprite.Sprite;
import loon.action.sprite.StatusBar;
import loon.event.GameTouch;
import loon.event.LTouchArea;
import loon.javase.Loon;
import loon.opengl.GLEx;
import loon.utils.Easing;
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
		sprite.setLocation(66, 66);
		// Entity sprite=Entity.make("ball.png", 66, 66);
		add(sprite);

		// 注册一个有界限的触屏监听器
		registerTouchArea(new LTouchArea() {

			@Override
			public void onAreaTouched(Event e, float touchX, float touchY) {
				if (e == Event.DOWN) {
					// 设置一个指定精灵的动画事件
					set(sprite).moveTo(touchX, touchY, false).// 地图方式，四方走法，移动到触屏位置
							fadeIn(60) // 动画淡入,速度60
							.delay(1f) // 延迟1秒
							.fadeOut(60)// 动画淡出
							.repeat(0.5f) // 回放上述动作,延迟0.5秒
							.moveTo(120, 160, false) // 地图方式，四方走法，移动到120,160位置
							.moveTo(260, 320, true) // 八方走法，移动到260,320位置
							.repeat(0.5f) // 回放上述动作,延迟0.5秒
							.rotateTo(360) // 旋转360度
							// 放大两倍，缩小还原，开始执行
							.scaleTo(2f, 2f).scaleTo(1f, 1f).start()
							// 监听事件进度 ( PS:如果没有新的动作执行或操作，不必后续监听)
							.setActionListener(new ActionListener() {

								@Override
								public void stop(ActionBind o) {

									// 动作停止后，继续执行to（位置前进）事件
									ActionTween tween = to(o,
											ActionType.POSITION, 400f, false). // 改变精灵坐标，延迟400,不删除先前设置的事件（如果删了，此监听也会删除）
											target(touchX, touchY). // 以触屏点为基础
											ease(Easing.QUAD_INOUT); // 伸缩方式
																		// QUAD_INOUT
									tween.delay(0.5f); // 延迟0.5f
									tween.repeat(2, 0.5f); // 往返两次，延迟0.5f
									tween.repeatBackward(2, 0.5f);
									tween.showTo(false); // 隐藏当前动作对象
									tween.delay(1f);// 延迟1秒
									tween.showTo(true);// 重新显示
									tween.start(); // 开始

								}

								@Override
								public void start(ActionBind o) {

								}

								@Override
								public void process(ActionBind o) {

								}
							});

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
