package org.test;

import loon.LSystem;
import loon.LTransition;
import loon.Screen;
import loon.action.ActionBind;
import loon.action.ActionListener;
import loon.action.ActionTween;
import loon.action.ActionType;
import loon.action.ColorTo;
import loon.action.RotateTo;
import loon.action.ScaleTo;
import loon.action.sprite.Sprite;
import loon.action.sprite.SpriteLabel;
import loon.canvas.LColor;
import loon.event.FrameLoopEvent;
import loon.event.GameTouch;
import loon.event.LTouchArea;
import loon.event.Updateable;
import loon.geom.Bezier;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.Easing;
import loon.utils.Easing.EasingMode;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public class ActionEventTest extends Screen {

	@Override
	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {
	}

	@Override
	public void onLoad() {

		final SpriteLabel label = new SpriteLabel("Plase Touch The Screen", 120, 20);
		add(label);

		// 构建一个球体的精灵
		final Sprite sprite = new Sprite("ball.png");
		sprite.setLocation(66, 66);
		// Entity sprite=Entity.make("ball.png", 66, 66);
		add(sprite);
		
		// 注册一个有界限的触屏监听器
		registerTouchArea(new LTouchArea() {

			@Override
			public void onAreaTouched(final Event e, final float touchX,
					final float touchY) {
				if (e == Event.DOWN) {
					// 设置一个指定精灵的动画事件(set或on函数设置指定对象的连续缓动动画，off或removeAllActions函数关闭所有缓动动画)
					on(sprite).bezierBy(1f, new Bezier(113, 122, 24, 14, 60, 62)).
					circleTo(50,50,100,90).//环绕50,50位置，半径100,每次移动90个像素，做不间断的旋转
					listenNames("circle", new ActionListener() { //监听已经注入的，所有名称为circle的对象
						
						private LTimer timer = new LTimer(1500);
						
						@Override
						public void stop(ActionBind o) {
							
							
						}
						
						@Override
						public void start(ActionBind o) {
							
							
						}
						
						@Override
						public void process(ActionBind o) {
							if(timer.action(elapsedTime)){
								//停止所有circleTo事件
								stopActionNames(o, "circle");
							}
							
						}
					}).
					arrowTo(199f,66f,100f). //向指定位置做弓弩发射移动（也就是做抛物线,由于加入了g值，所以此位置非停止位置而是经过位置）,重力100
					moveTo(330, 66).moveTo(66,66).loop(1). //移动到330x66位置后再移动到66,66，循环上述动画1次
					moveOvalTo(360f, 100, 20, new Vector2f(130,60), 1f, EasingMode.Linear).//椭圆形移动,初始x为0,旋转360,椭圆宽100,高20,以130,60为中心,移动1秒
					moveRoundTo(360, 90, new Vector2f(130,60),3f, EasingMode.Linear) //做环绕移动，旋转360度,半径90个像素，以130,130为中心点,移动3秒,缓动方式Linear
					.loopLast(2)//重复2次上述最后一个动画事件
					.parallelTo(new ScaleTo(2f),new RotateTo(360),new ColorTo(LColor.blue)) //同时执行缩放和旋转以及变色
					.scaleTo(1f)//缩放回原始大小
					.colorTo(LColor.white) //变回原始颜色
					.flashTo(2f)//精灵闪烁2秒
					.updateTo(new Updateable() { //执行一次Updateable(updateTo命令只会执行一次)
						
						@Override
						public void action(Object a) {

							//改变Label内容
							label.setLabel("Call Updateable");
							centerOn(label);
						
							
						}
					}).
					shakeTo(2f, 2f).//振动一次，振动范围值x2,y2
					eventTo(new FrameLoopEvent() { //执行一次循环事件(FrameLoopEvent不执行kill事件不会自行停止)
						
						private LTimer timer = new LTimer(LSystem.SECOND);
						
						@Override
						public void invoke(long elapsedTime, Screen e) {
						
							//改变Label内容
							label.setLabel("Call Event");
							centerOn(label);
							//间隔一秒后删除此循环事件
							if(timer.action(elapsedTime)){
								kill();
							}
						}
						
						@Override
						public void completed() {
							label.setLabel("Plase Touch The Screen");
							label.setLocation(120, 20);
							
						}
					}).
					moveTo(66,66)
					.transferTo(-1, 200, EasingMode.InBack,true,false) //让对象移动200个像素,渐进方式InBack,仅限于x轴允许改变位置(-1时不改变原有对象x或y坐标初始位置)
					.colorTo(LColor.red) //渐变到红色
					.shakeTo(2f, 3f). //让精灵产生振动，x轴最大移动2，y轴最大移动3
					        moveTo(touchX, touchY, false).// 地图方式，四方走法(true为8方向)，移动到触屏位置.
					        //若需限制地图行走区域,请注入Field2D对象(一个简单的，2维数组方式的地形存储器),矫正显示位置请注入offsetX和offsetY参数
							fadeOut(60) // 动画淡出,速度60
							.delay(1f) // 延迟1秒
							.fadeIn(60)// 动画淡入
							.colorTo(LColor.green) // 渐变到绿色
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

		add(MultiScreenTest.getBackButton(this, 0));

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
