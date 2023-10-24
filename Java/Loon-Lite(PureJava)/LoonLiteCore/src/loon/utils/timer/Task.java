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
package loon.utils.timer;

import loon.events.TaskRunnable;

/**
 * 本质上就是Interval类的别名实现,变更时间设定方式为浮点秒,方便用惯Task写法的……
 */
public class Task extends Interval implements TaskRunnable {

	public Task() {
		super();
	}

	public Task(float seconds) {
		super(Duration.ofS(seconds));
	}

	public Task(Duration d) {
		super(d);
	}

	public Task(String name, float seconds) {
		super(name, Duration.ofS(seconds));
	}

	public Task(float seconds, int loopCount) {
		super(Duration.ofS(seconds), loopCount);
	}

	public Task(String name, float seconds, int loopCount) {
		super(name, Duration.ofS(seconds), loopCount);
	}

	public Task(Runnable e) {
		super(0, e);
	}

	public Task(float seconds, Runnable e) {
		super(Duration.ofS(seconds), e);
	}

	public Task(float seconds, int loopCount, Runnable e) {
		super(Duration.ofS(seconds), loopCount, e);
	}

	public Task(String name, float seconds, int loopCount, Runnable e) {
		super(name, Duration.ofS(seconds), loopCount, e);
	}

	public Task(String name, Duration d) {
		super(name, d);
	}

	public Task(String name, Duration d, Runnable e) {
		super(name, d, e);
	}

	@Override
	public void loop() {
		run();
	}

	@Override
	public void run() {

	}

}
