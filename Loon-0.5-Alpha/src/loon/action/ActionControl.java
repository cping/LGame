package loon.action;

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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
public class ActionControl {

	private static ActionControl instanceAction;

	private Actions actions;

	private boolean pause;

	public static ActionControl getInstance() {
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

	private final void call(long elapsedTime) {
		if (pause || actions.getCount() == 0) {
			return;
		}
		actions.update(elapsedTime);
	}

	public static final void update(long elapsedTime) {
		if (instanceAction != null) {
			instanceAction.call(elapsedTime);
		}
	}

	private ActionControl() {
		actions = new Actions();
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

	public int getCount() {
		return actions.getCount();
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

	public void stopAll() {
		clear();
		stop();
	}

	public void stop() {
		pause = true;
	}

}
