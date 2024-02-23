/**
 * Copyright 2008 - 2023 The Loon Game Engine Authors
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
import loon.component.LClickButton;
import loon.component.LLabel;

public class YieldTest extends Stage {

	@Override
	public void create() {
		// 构建一个Label控件
		final LLabel label = LLabel.make("Loon Yield Test");
		// 居中
		centerOn(label);
		add(label);
		// 构建计数器
		final Counter c = new Counter();
		// 构建test1(putcall命令除非后面调用否则不会执行,只会缓存)
		putCall("test1", yield -> {

			label.setText("hello world");
			return yield.breakSelf();
		});

		// 创建并调用loon内置的虚拟协程yield函数test2(真实线程移植html环境有问题,所以不用,自制丐版协程......)
		call("test2", yield -> {

			// 如果计数器小于100循环
			if (yield.loop(c.increment() < 100, () -> {
				label.setText(c.getValue());
				// 如果循环成立
			}).isLoop()) {
				// 间隔0.1秒循环本函数1次
				return yield.returnings(0.1f);
			} else {
				// 如果计数等于0
				if (c.getValue() == 0) {
					label.setText("yield 1");
					// 累加计数器
					c.increment();
					// 重复这个yield函数,间隔两秒
					return yield.returnings(2f);
				}
				// 显示
				label.setText("return " + yield.getReturn());
				if (c.getValue() < 200) {
					// 累加计数并返回结果
					return yield.returning(c.increment());
				} else {
					label.setText("next");
					// 结束此次操作协程循环
					// return yield.breakSelf();
					// 结束此yield循环
					return yield.returning(false);
				}
			}
		}, yield -> {
			label.setText("yield 2 wait 10s");
			// 不循环,10秒后进入下一个yield
			return yield.seconds(10f);
		}, yield -> {
			label.setText("10s yield 3");
			return yield.seconds(1f);
		});
		LClickButton click = node("click", "Reset", 160, 35);

		centerBottomOn(click, 0, -10);
		add(click.up((x, y) -> {
			c.clear();
			// 再次调用已有的loon协程函数
			call("test2");
			// 调用test1
			// call("test1");
		}));
	}

}
