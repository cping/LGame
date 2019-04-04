package org.test;

import loon.Counter;
import loon.LSystem;
import loon.Stage;
import loon.action.sprite.WaitSprite;
import loon.utils.processes.RealtimeProcess;
import loon.utils.timer.LTimerContext;

public class CycleTest extends Stage {


	@Override
	public void create() {

		// 添加等待特效1
		add(new WaitSprite(0));

		// 构建一个独立的游戏进程(内部不是线程，只是loon提供的独立循环结构)
		RealtimeProcess process = new RealtimeProcess() {

			private Counter counter = newCounter();

			@Override
			public void run(LTimerContext time) {
				getSprites().removeAll();
				// 添加一个循环的特效为指定id
				add(new WaitSprite(counter.getValue()));
				counter.increment();
				// 累计到9重新计算
				if (counter.getValue() > 9) {
					counter.clear();
				}
				// 如果Screen关闭，杀掉当前游戏进程
				if (isClosed()) {
					kill();
				}
			}
		};
		// 延迟2秒执行一次特效切换
		process.setDelay(LSystem.SECOND * 2);
		
		//添加这个进程
		addProcess(process);
		
		//窗体注销时关闭process
		putRelease(process);
	
		add(MultiScreenTest.getBackButton(this, 0));
	}

}
