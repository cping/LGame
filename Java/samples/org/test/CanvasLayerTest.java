package org.test;

import loon.LSystem;
import loon.LTransition;
import loon.Screen;
import loon.canvas.Canvas;
import loon.canvas.LColor;
import loon.event.GameTouch;
import loon.font.LFont;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.stage.CanvasPlayer;
import loon.utils.MathUtils;
import loon.utils.reply.Port;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public class CanvasLayerTest extends Screen {

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {

		// 设置默认字体大小为20号字
		LFont.setDefaultFont(LFont.getFont(20));
		// 构建一个CanvasLayer对象，并实时渲染Canvas对象(PS:实时修改渲染对象比较耗费渲染时间，不建议在游戏中出现多个
		// BTW:最主要是不同运行环境存在的差异较大，不同平台造成的帧率改变可能很多……)
		final CanvasPlayer canyer = new CanvasPlayer();
		// 添加CanvasPlayer到Screen
		add(canyer);

		// 为Screen添加一个实时监听，用于监控CanvasPlayer的变化
		add(new Port<LTimerContext>() {
			private int step = 20;
			private int direction = 1;
			private int color = LColor.red.getARGB();
			private Canvas lastCanvas;
			// 每1/30秒执行一次
			private LTimer timer = new LTimer(LSystem.SECOND / 30);

			public void onEmit(LTimerContext clock) {
				if (timer.action(clock)) {

					// 获得CanvasLayer中的Canvas
					lastCanvas = canyer.begin();
					lastCanvas.clear();
					lastCanvas.setStrokeWidth(2);
					lastCanvas.setStrokeColor(color);

					step += direction;
					if (step > 30) {
						direction = -1;
					}
					if (step < 10) {
						direction = 1;
					}

					// 渲染圆
					final float r = 100;
					for (int i = 0; i < step; i++) {
						float angle = 2 * MathUtils.PI * i / step;
						RectBox viewSize = getViewRect();
						float x = (r * MathUtils.cos(angle)) + viewSize.width()
								/ 2;
						float y = (r * MathUtils.sin(angle))
								+ viewSize.height() / 2;
						lastCanvas.strokeCircle(x, y, 100);
					}

					// 提交Canvas的改变到当前纹理
					canyer.end();
				}
			}
		});

		add(MultiScreenTest.getBackButton(this));
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
