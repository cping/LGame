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
import loon.Stage;

public class TaskTest extends Stage {

	@Override
	public void create() {
		final Counter conter = newCounter();
		//提交一个计时任务,1秒执行一次,循环7次结束
		postTask(() -> {
			println(conter.getValue());
			conter.increment();
			//如果执行时间大于6s显示超时
		}, 1f, 7).onTimeOutS(() -> {
			println("> 6s");
			//执行完毕调用此函数
		}, 6f).onComplete(() -> {
			println("over");
		});

	}

}
