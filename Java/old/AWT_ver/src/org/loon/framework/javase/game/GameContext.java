package org.loon.framework.javase.game;

import org.loon.framework.javase.game.core.timer.SystemTimer;

/**
 * Copyright 2008 - 2009
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public final class GameContext {

	public static int nextContextID = 0;

	private ThreadGroup threadGroup;

	private final GameView view;

	private SystemTimer timer;

	public GameContext(GameView view, SystemTimer timer) {
		this.view = view;
		this.timer = timer;
	}

	public ThreadGroup getThreadGroup() {
		if (threadGroup == null || threadGroup.isDestroyed()) {
			threadGroup = new ThreadGroup("LGame-View" + nextContextID);
			nextContextID++;
		}
		return threadGroup;
	}

	public Thread createThread(Runnable runnable) {
		while (true) {
			ThreadGroup currentGroup = getThreadGroup();
			synchronized (currentGroup) {
				if (getThreadGroup() != currentGroup) {
					continue;
				}
				Thread thread = new Thread(currentGroup, runnable, "LGame-View"
						+ nextContextID);
				return thread;
			}
		}
	}

	public void setAnimationThread(Thread thread) {
		if (thread != null) {
			if (threadGroup == null
					|| !threadGroup.parentOf(thread.getThreadGroup())) {
				threadGroup = thread.getThreadGroup();
			}
		}
	}

	public SystemTimer getTimer() {
		return timer;
	}

	public GameView getView() {
		return view;
	}

}
