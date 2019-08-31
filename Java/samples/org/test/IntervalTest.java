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

public class IntervalTest extends Stage {

	@Override
	public void create() {

		//添加label组件,位于150,150位置
		final LLabel label = addLabel("empty",150,150);
		
		// 构建一个间隔执行的事务对象，间隔1秒,执行10次loop(Interval类不填次数默认无限循环,直到调用stop或者pause)
		Interval i = new Interval(LSystem.SECOND, 10) {
			
			// 构建计数器
			Counter counter = new Counter();
			
			@Override
			public void loop() {
				label.setText("test loop " + counter.increment(1));
			}
			
		};
		i.start();
		
		//关闭Stage时关闭Interval
		putRelease(i);
		

		add(MultiScreenTest.getBackButton(this, 2));
	}

}
