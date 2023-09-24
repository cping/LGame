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

import loon.Stage;
import loon.utils.processes.TimeLineEvent;
import loon.utils.processes.TimeLineProcess;

public class TimeLineTest extends Stage {

	@Override
	public void create() {

		// 循环三次(不填无限循环)
		TimeLineProcess p = createTimeLineProcess(3);

		// 构建时间线事件Test,间隔1秒,优先级1(设定上高者执行在前，低者执行在后),控制台输出"3"+经过时间
		TimeLineEvent e1 = new TimeLineEvent("Test", 1, 1, e -> {
			System.out.println("3 " + e.getElapsed());
		});
		// 构建时间线事件Test,间隔6秒,优先级2,控制台输出"2"+经过时间
		TimeLineEvent e2 = new TimeLineEvent("Test", 1, 2, e -> {
			System.out.println("2 " + e.getElapsed());
		});
		// 构建时间线事件Test,间隔1秒,优先级3,控制台输出"1"+经过时间
		TimeLineEvent e3 = new TimeLineEvent("Test", 1, 3, e -> {
			System.out.println("1 " + e.getElapsed());
		});

		// 禁止所有Test事件参与循环
		// p.disableLoopEvents("Test");
		// 注入监听事件
		p.addEventListeners(e1, e2, e3);
		// 自动翻转时间线(所有事件都完成后才会执行)
		// p.setAutoReverse(true);
		// 等待上一个事件执行完毕再进入下一个
		// p.setWaitLastEvent(true);

		down((float x, float y) -> {
			// p.enableLoopEvents("Test");
			// e3.complete();
		});

	}

}
