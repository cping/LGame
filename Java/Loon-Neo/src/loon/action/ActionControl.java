/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.action;

import loon.utils.timer.LTimer;

/** 全局生效的动作控制类（在Loon中任何场景都适用），所有实现了ActionBind的类都可以被此类控制 **/
public class ActionControl {
	
	public static final ActionLinear LINEAR = new ActionLinear();
	public static final ActionSmooth SMOOTH = new ActionSmooth();

	private static ActionControl instanceAction;

	private final Actions actions;

	private final LTimer delayTimer;

	private boolean pause;

	public static ActionControl get() {
		if (instanceAction != null) {
			return instanceAction;
		}
		synchronized (ActionControl.class) {
			if (instanceAction == null) {
				instanceAction = new ActionControl();
			}
			return instanceAction;
		}
	}

	public final void call(long elapsedTime) {
		if (pause || actions.getCount() == 0) {
			return;
		}
		if (delayTimer.action(elapsedTime)) {
			actions.update(elapsedTime);
		}
	}

	public final void delay(long d) {
		delayTimer.setDelay(d);
	}

	public final LTimer getTimer() {
		return delayTimer;
	}

	public static final void setDelay(long delay) {
		if (instanceAction != null) {
			instanceAction.delay(delay);
		}
	}

	public static final void update(long elapsedTime) {
		if (instanceAction != null) {
			instanceAction.call(elapsedTime);
		}
	}

	private ActionControl() {
		actions = new Actions();
		delayTimer = new LTimer(0);
	}

	public void addAction(ActionEvent action, ActionBind obj, boolean paused) {
		actions.addAction(action, obj, paused);
	}

	public void addAction(ActionEvent action, ActionBind obj) {
		addAction(action, obj, false);
	}

	public void removeAllActions(ActionBind actObject) {
		actions.removeAllActions(actObject);
	}

	public boolean containsKey(ActionBind actObject) {
		return actions.containsKey(actObject);
	}

	public boolean isCompleted(ActionBind actObject) {
		return actions.isCompleted(actObject);
	}

	public int getCount() {
		return actions.getCount();
	}

	public boolean stopNames(ActionBind k, String name) {
		return actions.stopNames(k, name);
	}

	public boolean stopTags(ActionBind k, Object tag) {
		return actions.stopTags(k, tag);
	}

	public void removeAction(Object tag, ActionBind actObject) {
		actions.removeAction(tag, actObject);
	}

	public void removeAction(ActionEvent action) {
		actions.removeAction(action);
	}

	public ActionEvent getAction(Object tag, ActionBind actObject) {
		return actions.getAction(tag, actObject);
	}

	public void stop(ActionBind actObject) {
		actions.stop(actObject);
	}

	public void start(ActionBind actObject) {
		actions.start(actObject);
	}

	public void paused(boolean pause, ActionBind actObject) {
		actions.paused(pause, actObject);
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public void clear() {
		actions.clear();
	}

	public void stop() {
		clear();
		pause();
	}

	public void pause() {
		pause = true;
	}

}
