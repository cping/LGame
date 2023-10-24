/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package org.test;

import loon.Counter;
import loon.LSystem;
import loon.Stage;
import loon.component.LLabel;
import loon.utils.timer.Interval;
import loon.utils.timer.Scheduler;
import loon.utils.timer.Task;

public class SchedulerTest extends Stage {

	@Override
	public void create() {

		// 添加label组件,位于150,150位置
		final LLabel label = addLabel("empty", 150, 150);

		// 构建一个延迟事务管理器(remove项为true时,删除已经完成的延迟事务),顺序执行(上一个任务不完成不继续)
		final Scheduler s = new Scheduler(true, true);
		// 添加事务1,间隔0,执行3次
		s.add(new Interval(0, 3) {

			// 构建计数器
			Counter c = newCounter();

			@Override
			public void loop() {
				label.setText("a" + c.increment(1));
			}
		});
		// 添加事务2
		s.add(new Interval() {

			@Override
			public void loop() {
				label.setText("b");
				// 停止此事务(强制完成)
				stop();

			}
		});
		// 添加事务3
		s.add(new Interval() {

			@Override
			public void loop() {
				label.setText("c");
				// 停止此事务(强制完成)
				stop();
			}
		});
		// 延迟1秒
		s.setDelay(LSystem.SECOND);
		// 开始执行
		s.start();

		// 关闭Stage时关闭Scheduler
		putRelease(s);

		add(MultiScreenTest.getBackButton(this, 2));
	}

}
