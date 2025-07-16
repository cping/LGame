package org.test;

import loon.Stage;
import loon.component.LLabel;
import loon.utils.timer.SimulationTimer;
import loon.utils.timer.SimulationTimer.MonthType;

public class SimulationTimerTest extends Stage {

	@Override
	public void create() {
		// 构建一个标签组件
		final LLabel label = node("l", "testing");
		// 位于屏幕中心
		centerOn(label);
		// 设定一个虚拟时间,为1889年6月3日0点
		SimulationTimer time = new SimulationTimer(1889, MonthType.June, 3, 0);
		// 设定游戏每帧等于虚拟时间720分钟
		time.setMinuteSpeed(720);
		// 循环监听虚拟时间变化
		time.setEventAction((t) -> {
			// 显示虚拟时间
			label.setText(t.toData());
			// 再次居中显示
			centerOn(label);
		});
		// 添加虚拟时间到游戏进程
		addProcess(time);
		// 关闭Screen时关闭此进程
		putRelease(time);
	}

}
