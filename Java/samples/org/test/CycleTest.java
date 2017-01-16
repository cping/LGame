package org.test;

import loon.LSystem;
import loon.LTransition;
import loon.Screen;
import loon.action.sprite.WaitSprite;
import loon.event.GameTouch;
import loon.opengl.GLEx;
import loon.utils.processes.RealtimeProcess;
import loon.utils.timer.LTimerContext;

public class CycleTest extends Screen {

	RealtimeProcess process;

	@Override
	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {

		// 添加等待特效1
		add(new WaitSprite(0));

		// 构建一个独立的游戏进程(内部不是线程，只是loon提供的独立循环结构)
		process = new RealtimeProcess() {

			private int count = 0;

			@Override
			public void run(LTimerContext time) {
				getSprites().removeAll();
				// 添加一个循环等待的特效
				add(new WaitSprite(count++));
				// 总共只有11个特效……
				if (count > 11) {
					count = 0;
				}
				// 如果Screen关闭，杀掉当前游戏进程
				if (isClose()) {
					kill();
				}
			}
		};
		// 延迟5秒执行一次特效切换
		process.setDelay(LSystem.SECOND * 5);
		// 注入进程
		addProcess(process);

		add(MultiScreenTest.getBackButton(this,0));
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
		removeProcess(process);
	}

}
