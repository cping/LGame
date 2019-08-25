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

import loon.LRelease;
import loon.event.Updateable;

/**
 * 这是一个指定循环事务延迟触发用抽象类,本身并没有实现关键的loop方法,loop部分需要用户根据实际需求实现.<br>
 * 此类作用是提交一个指定间隔的循环到游戏进程中去,每隔指定时间执行一次loop中内容.
 * 
 * ps:事实上这只是一个LTimer的延迟调用部分功能的抽象封装,简化了LTimer的delay相关方法的调用.
 * 不使用此类,而直接使用LTimer也可以得到完全等价的效果.
 */
public abstract class Interval implements Updateable, LRelease {

	private final LTimer timer;

	public Interval(long delay) {
		this.timer = new LTimer(delay);
	}

	public Interval(Duration d) {
		this.timer = new LTimer(d);
	}

	public Interval(String name, long delay) {
		this.timer = new LTimer(name, delay);
	}

	public Interval(String name, Duration d) {
		this.timer = new LTimer(name, d);
	}

	public Interval start() {
		timer.start();
		timer.setUpdateable(this);
		timer.submit();
		return this;
	}

	public Interval stop() {
		timer.stop();
		timer.kill();
		return this;
	}

	public Interval pause() {
		timer.pause();
		return this;
	}

	public Interval unpause() {
		timer.unpause();
		return this;
	}

	public Interval setDelay(long d) {
		timer.setDelay(d);
		return this;
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public String getName() {
		return timer.getName();
	}

	public LTimer currentTimer() {
		return timer;
	}

	@Override
	public void action(Object o) {
		loop();
	}

	public abstract void loop();

	@Override
	public void close() {
		timer.close();
	}

}
