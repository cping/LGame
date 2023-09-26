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

	final static class ActionElement {

		private ActionBind key;

		private int actionIndex;

		private boolean paused;

		private TArray<ActionEvent> actions;

		private ActionEvent currentAction;

		public ActionElement(ActionBind k, boolean v) {
			this.actions = new TArray<ActionEvent>(CollectionUtils.INITIAL_CAPACITY);
			this.key = k;
			this.paused = v;
		}
	}

	private final ArrayMap actions;

	private boolean _started = false;

	private boolean _stoped = false;

	Actions() {
		this.actions = new ArrayMap(CollectionUtils.INITIAL_CAPACITY);
	}

	public Actions clear() {
		actions.clear();
		resetAction();
		return this;
	}

	public TArray<ActionBind> keys() {
		TArray<ActionBind> list = new TArray<ActionBind>(actions.size());
		for (int i = 0; i < actions.size(); i++) {
			Object key = actions.getKey(i);
			if (key != null && key instanceof ActionBind) {
				list.add((ActionBind) key);
			}
		}
		return list;
	}

	public boolean stopNames(ActionBind k, String name) {
		if (k == null) {
			return true;
		}
		ActionElement eles = (ActionElement) actions.getValue(k);
		if (eles == null || name == null) {
			return true;
		}
		TArray<ActionEvent> eves = eles.actions;
		if (eves == null || eves.size == 0) {
			return true;
		}
		int count = 0;
		for (int i = 0, size = eves.size; i < size; i++) {
			ActionEvent e = eves.get(i);
			if (e != null && name.trim().toLowerCase().equals(e.getName())) {
				e.kill();
				count++;
			}
		}
		return count > 0;
	}

	public boolean stopTags(ActionBind k, Object tag) {
		if (k == null) {
			return true;
		}
		ActionElement eles = (ActionElement) actions.getValue(k);
		if (eles == null || tag == null) {
			return true;
		}
		TArray<ActionEvent> eves = eles.actions;
		if (eves == null || eves.size == 0) {
			return true;
		}
		int count = 0;
		for (int i = 0, size = eves.size; i < size; i++) {
			ActionEvent e = eves.get(i);
			if (e != null && (tag.equals(e.getTag()) || tag == e.getTag())) {
				e.kill();
				count++;
			}
		}
		return count > 0;
	}

	public boolean isCompleted(ActionBind k) {
		if (k == null) {
			return true;
		}
		ActionElement eles = (ActionElement) actions.getValue(k);
		if (eles == null) {
			return true;
		}
		TArray<ActionEvent> eves = eles.actions;
		if (eves == null || eves.size == 0) {
			return true;
		}
		int count = 0;
		for (int i = 0, size = eves.size; i < size; i++) {
			ActionEvent e = eves.get(i);
			if (e != null && e.isComplete()) {
				count++;
			}
		}
		return count == eves.size;
	}

	public boolean containsKey(ActionBind k) {
		if (k == null) {
			return false;
		}
		return actions.containsKey(k);
	}

	public boolean containsValue(ActionEvent v) {
		if (v == null) {
			return false;
		}
		return actions.containsValue(v);
	}

	public Actions addAction(ActionEvent action, ActionBind actObject, boolean paused) {
		ActionElement element = (ActionElement) actions.get(actObject);
		if (element == null) {
			element = new ActionElement(actObject, paused);
			actions.put(actObject, element);
		}
		element.actions.add(action);
		action.start(actObject);
		_started = true;
		return this;
	}

	private void deleteElement(ActionElement element) {
		if (element != null) {
			for (ActionEvent eve : element.actions) {
				if (eve != null) {
					eve.forceCompleted();
				}
			}
			element.actions.clear();
			actions.remove(element.key);
		}
	}

	public Actions removeAllActions(ActionBind actObject) {
		if (actObject == null) {
			return this;
		}
		Object o = actions.get(actObject);
		if (o != null) {
			ActionElement element = (ActionElement) o;
			if (element != null) {
				for (ActionEvent eve : element.actions) {
					if (eve != null) {
						eve.forceCompleted();
					}
				}
				element.actions.clear();
				deleteElement(element);
			}
		}
		checkStoped();
		return this;
	}

	private void removeAction(int index, ActionElement element) {
		ActionEvent eve = element.actions.removeIndex(index);
		if (eve != null) {
			eve.forceCompleted();
		}
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

	private void checkStoped() {
		if (_started && actions.size() == 0) {
			_stoped = true;
		}
	}

	public Actions removeAction(Object tag, ActionBind actObject) {
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
		checkStoped();
		return this;
	}

	public Actions removeAction(ActionEvent action) {
		if (action == null) {
			return this;
		}
		ActionElement element = (ActionElement) actions.get(action.getOriginal());
		if (element != null) {
			int i = element.actions.indexOf(action);
			if (i != -1) {
				removeAction(i, element);
			}
		}
		checkStoped();
		return this;
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
		final int size = actions.size();
		for (int i = size - 1; i > -1; --i) {
			final ActionElement currentTarget = (ActionElement) actions.get(i);
			if (currentTarget == null) {
				continue;
			}
			synchronized (currentTarget) {
				if (!currentTarget.paused) {
					for (currentTarget.actionIndex = 0; currentTarget.actionIndex < currentTarget.actions.size; currentTarget.actionIndex++) {
						currentTarget.currentAction = currentTarget.actions.get(currentTarget.actionIndex);
						if (currentTarget.currentAction == null) {
							continue;
						}
						if (!currentTarget.currentAction.isInit) {
							currentTarget.currentAction.isInit = true;
							currentTarget.currentAction.initAction();
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

	public Actions paused(boolean pause, ActionBind actObject) {
		ActionElement element = (ActionElement) actions.get(actObject);
		if (element != null) {
			element.paused = pause;
		}
		return this;
	}

	public Actions stop(ActionBind actObject) {
		ActionElement element = (ActionElement) actions.get(actObject);
		if (element != null) {
			element.paused = true;
		}
		_stoped = true;
		return this;
	}

	public Actions start(ActionBind actObject) {
		ActionElement element = (ActionElement) actions.get(actObject);
		if (element != null) {
			element.paused = false;
		}
		_started = true;
		return this;
	}

	public Actions start() {
		for (int i = 0; i < actions.size(); i++) {
			((ActionElement) actions.get(i)).paused = false;
		}
		_started = true;
		return this;
	}

	public Actions stop() {
		for (int i = 0; i < actions.size(); i++) {
			((ActionElement) actions.get(i)).paused = true;
		}
		_stoped = true;
		return this;
	}

	protected void resetAction() {
		_started = _stoped = false;
	}

	protected void stopAction() {
		_stoped = true;
	}

	public boolean isInited() {
		return _started && !_stoped;
	}

	public boolean isStarted() {
		return _started;
	}

	public boolean isStoped() {
		return _stoped;
	}

}
