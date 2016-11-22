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
package loon.action;

import loon.utils.ArrayMap;
import loon.utils.CollectionUtils;
import loon.utils.TArray;

public class Actions {

	final private ArrayMap actions;

	Actions() {
		this.actions = new ArrayMap(CollectionUtils.INITIAL_CAPACITY);
	}

	public void clear() {
		actions.clear();
	}

	public void addAction(ActionEvent action, ActionBind actObject,
			boolean paused) {
		ActionElement element = (ActionElement) actions.get(actObject);
		if (element == null) {
			element = new ActionElement(actObject, paused);
			actions.put(actObject, element);
		}
		element.actions.add(action);
		action.start(actObject);
	}

	private void deleteElement(ActionElement element) {
		element.actions.clear();
		actions.remove(element.key);
	}

	public void removeAllActions(ActionBind actObject) {
		if (actObject == null) {
			return;
		}
		Object o = actions.get(actObject);
		if (o != null) {
			ActionElement element = (ActionElement) o;
			if (element != null) {
				element.actions.clear();
				deleteElement(element);
			}
		}
	}

	private void removeAction(int index, ActionElement element) {
		element.actions.removeIndex(index);
		if (element.actionIndex >= index) {
			element.actionIndex--;
		}
		if (element.actions.isEmpty()) {
			deleteElement(element);
		}
	}

	public int getCount() {
		return actions.size();
	}

	public void removeAction(Object tag, ActionBind actObject) {
		ActionElement element = (ActionElement) actions.get(actObject);
		if (element != null) {
			if (element.actions != null) {
				int limit = element.actions.size;
				for (int i = 0; i < limit; i++) {
					ActionEvent a = element.actions.get(i);
					if (a.getTag() == tag && a.getOriginal() == actObject) {
						removeAction(i, element);
					}
				}
			}
		}
	}

	public void removeAction(ActionEvent action) {
		if (action == null) {
			return;
		}
		ActionElement element = (ActionElement) actions.get(action
				.getOriginal());
		if (element != null) {
			int i = element.actions.indexOf(action);
			if (i != -1) {
				removeAction(i, element);
			}
		}
	}

	public ActionEvent getAction(Object tag, ActionBind actObject) {
		ActionElement element = (ActionElement) actions.get(actObject);
		if (element != null) {
			if (element.actions != null) {
				int limit = element.actions.size;
				for (int i = 0; i < limit; i++) {
					ActionEvent a = element.actions.get(i);
					if (a.getTag() == tag)
						return a;
				}
			}
		}
		return null;
	}

	public void update(long elapsedTime) {
		int size = actions.size();
		for (int i = size - 1; i > -1; --i) {
			ActionElement currentTarget = (ActionElement) actions.get(i);
			if (currentTarget == null) {
				continue;
			}
			synchronized (currentTarget) {
				if (!currentTarget.paused) {
					for (currentTarget.actionIndex = 0; currentTarget.actionIndex < currentTarget.actions.size; currentTarget.actionIndex++) {
						currentTarget.currentAction = currentTarget.actions
								.get(currentTarget.actionIndex);
						if (currentTarget.currentAction == null) {
							continue;
						}
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

	public void paused(boolean pause, ActionBind actObject) {
		ActionElement element = (ActionElement) actions.get(actObject);
		if (element != null) {
			element.paused = pause;
		}
	}

	public void stop(ActionBind actObject) {
		ActionElement element = (ActionElement) actions.get(actObject);
		if (element != null) {
			element.paused = true;
		}
	}

	public void start(ActionBind actObject) {
		ActionElement element = (ActionElement) actions.get(actObject);
		if (element != null) {
			element.paused = false;
		}
	}

	public void start() {
		for (int i = 0; i < actions.size(); i++) {
			((ActionElement) actions.get(i)).paused = false;
		}
	}

	public void stop() {
		for (int i = 0; i < actions.size(); i++) {
			((ActionElement) actions.get(i)).paused = true;
		}
	}

	final static class ActionElement {

		private ActionBind key;

		private int actionIndex;

		private boolean paused;

		private TArray<ActionEvent> actions;

		private ActionEvent currentAction;

		public ActionElement(ActionBind k, boolean v) {
			this.actions = new TArray<ActionEvent>(
					CollectionUtils.INITIAL_CAPACITY);
			this.key = k;
			this.paused = v;
		}
	}
}
