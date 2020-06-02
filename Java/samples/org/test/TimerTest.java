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

import loon.LSystem;
import loon.Stage;
import loon.component.LLabel;
import loon.event.Updateable;
import loon.utils.timer.LTimer;

public class TimerTest extends Stage {

	@Override
	public void create() {

		final LTimer timer1 = new LTimer(LSystem.SECOND * 15);

		final LLabel label = addLabel("test");

		centerOn(label);

		add(label);

		final LTimer timer2 = new LTimer(LSystem.SECOND);
		// 执行10次
		timer2.setRepeats(10);
		timer2.setUpdateable(new Updateable() {

			@Override
			public void action(Object a) {
				label.setText("Timer2 running count:" + timer2.getTimesRepeated());
				centerOn(label);
			}
		});
		// 提交计时器到Loon循环中
		timer2.submit();

		// 关闭Screen时注销计时器
		putRelease(timer2);

		setTimeout(new Updateable() {

			@Override
			public void action(Object a) {
				// 满足Timer1延迟条件时
				if (timer1.action(getElapsedTime())) {
					// 打印info
					label.setText("Timer1 Running");
					centerOn(label);
				}
			}
		}, 500);

		add(MultiScreenTest.getBackButton(this, 2));
	}

}
