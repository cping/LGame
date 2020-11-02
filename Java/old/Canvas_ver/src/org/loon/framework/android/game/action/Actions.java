package org.loon.framework.android.game.action;

import java.util.ArrayList;

import org.loon.framework.android.game.core.graphics.component.Actor;
import org.loon.framework.android.game.utils.collection.ArrayMap;

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
public class Actions {

	final private ArrayMap actions;

	Actions() {
		this.actions = new ArrayMap(10);
	}
	
	public void clear(){
		actions.clear();
	}

	public void addAction(ActionEvent action, Actor actObject, boolean paused) {
		synchronized (actions) {
			ActionElement element = (ActionElement) actions.get(actObject);
			if (element == null) {
				element = new ActionElement(actObject, paused);
				actions.put(actObject, element);
			}
			element.actions.add(action);
			action.start(actObject);
		}
	}

	private void deleteElement(ActionElement element) {
		synchronized (actions) {
			element.actions.clear();
			actions.remove(element.key);
		}
	}

	public void removeAllActions(Actor actObject) {
		if (actObject == null) {
			return;
		}
		synchronized (actions) {
			ActionElement element = (ActionElement) actions.get(actObject);
			if (element != null) {
				element.actions.clear();
				deleteElement(element);
			}
		}
	}

	private void removeAction(int index, ActionElement element) {
		synchronized (actions) {
			element.actions.remove(index);
			if (element.actionIndex >= index) {
				element.actionIndex--;
			}
			if (element.actions.isEmpty()) {
				deleteElement(element);
			}
		}
	}

	public int getCount() {
		return actions.size();
	}

	public void removeAction(Object tag, Actor actObject) {
		synchronized (actions) {
			ActionElement element = (ActionElement) actions.get(actObject);
			if (element != null) {
				if (element.actions != null) {
					int limit = element.actions.size();
					for (int i = 0; i < limit; i++) {
						ActionEvent a = (ActionEvent) element.actions.get(i);
						if (a.getTag() == tag && a.getOriginal() == actObject) {
							removeAction(i, element);
						}
					}
				}
			}
		}
	}

	public void removeAction(ActionEvent action) {
		if (action == null) {
			return;
		}
		synchronized (actions) {
			ActionElement element = (ActionElement) actions.get(action
					.getOriginal());
			if (element != null) {
				int i = element.actions.indexOf(action);
				if (i != -1) {
					removeAction(i, element);
				}
			}
		}
	}

	public ActionEvent getAction(Object tag, Actor actObject) {
		synchronized (actions) {
			ActionElement element = (ActionElement) actions.get(actObject);
			if (element != null) {
				if (element.actions != null) {
					int limit = element.actions.size();
					for (int i = 0; i < limit; i++) {
						ActionEvent a = (ActionEvent) element.actions.get(i);
						if (a.getTag() == tag)
							return a;
					}
				}
			}
			return null;
		}
	}

	public void update(long elapsedTime) {
		synchronized (actions) {
			int size = actions.size();
			for (int i = size - 1; i > -1; --i) {
				ActionElement currentTarget = (ActionElement) actions.get(i);
				synchronized (currentTarget) {
					if (!currentTarget.paused) {
						for (currentTarget.actionIndex = 0; currentTarget.actionIndex < currentTarget.actions
								.size(); currentTarget.actionIndex++) {
							currentTarget.currentAction = (ActionEvent) currentTarget.actions
									.get(currentTarget.actionIndex);
							if (!currentTarget.currentAction.isInit) {
								currentTarget.currentAction.isInit = true;
								currentTarget.currentAction.onLoad();
							}
							currentTarget.currentAction.step(elapsedTime);
							if (currentTarget.currentAction.isComplete()) {
								currentTarget.currentAction.stop();
								removeAction(currentTarget.currentAction);
							}
							currentTarget.currentAction = null;
						}
					}
					if (currentTarget.actions.isEmpty()) {
						deleteElement(currentTarget);
					}
				}
			}
		}
	}

	public void paused(boolean pause, Actor actObject) {
		synchronized (actions) {
			ActionElement element = (ActionElement) actions.get(actObject);
			if (element != null) {
				element.paused = pause;
			}
		}
	}

	public void stop(Actor actObject) {
		synchronized (actions) {
			ActionElement element = (ActionElement) actions.get(actObject);
			if (element != null) {
				element.paused = true;
			}
		}
	}

	public void start(Actor actObject) {
		synchronized (actions) {
			ActionElement element = (ActionElement) actions.get(actObject);
			if (element != null) {
				element.paused = false;
			}
		}
	}

	public void start() {
		synchronized (actions) {
			for (int i = 0; i < actions.size(); i++) {
				((ActionElement) actions.get(i)).paused = false;
			}
		}
	}

	public void stop() {
		synchronized (actions) {
			for (int i = 0; i < actions.size(); i++) {
				((ActionElement) actions.get(i)).paused = true;
			}
		}
	}

	final static class ActionElement {

		private Actor key;

		private int actionIndex;

		private boolean paused;

		private ArrayList<ActionEvent> actions;

		private ActionEvent currentAction;

		public ActionElement(Actor k, boolean v) {
			this.actions = new ArrayList<ActionEvent>(10);
			this.key = k;
			this.paused = v;
		}
	}
}
