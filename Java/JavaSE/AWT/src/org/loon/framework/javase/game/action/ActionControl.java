package org.loon.framework.javase.game.action;

import org.loon.framework.javase.game.core.LSystem;
import org.loon.framework.javase.game.core.graphics.component.Actor;
import org.loon.framework.javase.game.core.timer.SystemTimer;

/**
 * Copyright 2008 - 2011
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
public class ActionControl {

	private Actions actions;

	private static ActionControl instanceAction;

	private ActionThread actionThread;

	private int fps = 30;

	private boolean pause;

	public static ActionControl getInstance() {
		synchronized (ActionControl.class) {
			if (instanceAction == null) {
				instanceAction = new ActionControl();
			}
			return instanceAction;
		}
	}

	private ActionControl() {
		actions = new Actions();
	}

	public void addAction(ActionEvent action, Actor obj, boolean paused) {
		synchronized (actions) {
			actions.addAction(action, obj, paused);
			makeActionThread();
		}
	}

	public void addAction(ActionEvent action, Actor obj) {
		addAction(action, obj, false);
	}

	public void removeAllActions(Actor actObject) {
		synchronized (actions) {
			actions.removeAllActions(actObject);
		}
	}

	public int getCount() {
		synchronized (actions) {
			return actions.getCount();
		}
	}

	public void removeAction(Object tag, Actor actObject) {
		synchronized (actions) {
			actions.removeAction(tag, actObject);
		}
	}

	public void removeAction(ActionEvent action) {
		synchronized (actions) {
			actions.removeAction(action);
		}
	}

	public ActionEvent getAction(Object tag, Actor actObject) {
		synchronized (actions) {
			return actions.getAction(tag, actObject);
		}
	}

	public void clear() {
		actions.clear();
	}

	public void stopAll() {
		setFPS(1);
		clear();
		stop();
	}

	public void stop(Actor actObject) {
		synchronized (actions) {
			actions.stop(actObject);
		}
	}

	public void start(Actor actObject) {
		synchronized (actions) {
			actions.start(actObject);
		}
	}

	public void paused(boolean pause, Actor actObject) {
		synchronized (actions) {
			actions.paused(pause, actObject);
		}
	}

	public void setFPS(int fps) {
		this.fps = fps;
	}

	public int getFPS() {
		return fps;
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public void stop() {
		if (actionThread != null) {
			actionThread.end();
		}
	}

	private void makeActionThread() {
		synchronized (actions) {
			if (actions.getCount() > 0) {
				if (actionThread == null) {
					actionThread = new ActionThread();
					actionThread.start();
				}
			}
		}
	}

	class ActionThread extends Thread {

		private boolean isRunning;

		public ActionThread() {
			isRunning = true;
		}

		public void end() {
			isRunning = false;
		}

		public void run() {
			long elapsedTime, goalTimeMicros, elapsedTimeMicros;
			SystemTimer timer = LSystem.getSystemTimer();
			Thread currentThread = Thread.currentThread();
			long currTimeMicros = 0, lastTimeMicros = timer.getTimeMicros(), remainderMicros = 0;
			do {
				if (LSystem.isPaused) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {
					}
					lastTimeMicros = timer.getTimeMicros();
					elapsedTime = 0;
					continue;
				}
				if (actions.getCount() == 0 || pause) {
					try {
						Thread.sleep(300);
					} catch (InterruptedException ex) {
					}
					continue;
				} else {
					goalTimeMicros = lastTimeMicros + 1000000L / fps;
					currTimeMicros = timer.sleepTimeMicros(goalTimeMicros);
					elapsedTimeMicros = currTimeMicros - lastTimeMicros
							+ remainderMicros;
					elapsedTime = Math.max(0, (int) (elapsedTimeMicros / 1000));
					remainderMicros = elapsedTimeMicros - elapsedTime * 1000;
					lastTimeMicros = currTimeMicros;
					synchronized (actions) {
						actions.update(elapsedTime);
					}
				}
			} while (isRunning && actionThread == currentThread);
			try {
				actionThread.interrupt();
			} catch (Exception e) {
			} finally {
				actionThread = null;
				isRunning = true;
			}
		}

	}

}
