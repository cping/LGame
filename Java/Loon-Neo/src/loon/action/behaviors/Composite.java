/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
package loon.action.behaviors;

import loon.utils.TArray;

public abstract class Composite<T> extends Behavior<T> {

	public AbortTypes abortType = AbortTypes.None;

	protected TArray<Behavior<T>> _children = new TArray<Behavior<T>>();

	protected boolean _hasLowerPriorityConditionalAbort;

	protected int _currentChildIndex = 0;

	@Override
	public void invalidate() {
		super.invalidate();
		for (Behavior<T> v : _children) {
			v.invalidate();
		}
	}

	public boolean isFirstChild() {
		return _children.get(0) instanceof IConditional;
	}

	protected boolean hasLowerPriorityInChild() {
		for (Behavior<T> v : _children) {
			if (v instanceof Composite) {
				Composite<T> composite = (Composite<T>) v;
				if (composite != null && composite.abortType.has(AbortTypes.LowerPriority)) {
					if (composite.isFirstChild()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public void onStart() {
		_hasLowerPriorityConditionalAbort = hasLowerPriorityInChild();
		_currentChildIndex = 0;
	}

	@Override
	public void onEnd() {
		for (Behavior<T> v : _children) {
			v.invalidate();
		}
	}

	public Composite<T> addChild(Behavior<T> child) {
		_children.add(child);
		return this;
	}

	protected void updateLowerPriorityAbort(T context, TaskStatus statusCheck) {
		int i = 0;
		for (Behavior<T> v : _children) {
			i++;
			if (v instanceof Composite) {
				Composite<T> composite = (Composite<T>) v;
				if (composite != null && composite.abortType.has(AbortTypes.LowerPriority)) {
					Behavior<T> child = composite._children.get(0);
					TaskStatus status = updateNode(context, child);
					if (status != statusCheck) {
						_currentChildIndex = i;
						for (int j = i; j < _children.size; j++) {
							_children.get(j).invalidate();
						}
						break;
					}
				}
			}
		}
	}

	protected void updateSelfAbort(T context, TaskStatus statusCheck) {
		int i = 0;
		for (Behavior<T> v : _children) {
			i++;
			Behavior<T> child = v;
			if (!(child instanceof IConditional)) {
				continue;
			}
			TaskStatus status = updateNode(context, child);
			if (status != statusCheck) {
				_currentChildIndex = i;
				for (int j = i; j < _children.size; j++) {
					_children.get(j).invalidate();
				}
				break;
			}
		}
	}

	public TaskStatus updateNode(T context, Behavior<T> node) {
		if (node instanceof DecoratorConditional) {
			return ((DecoratorConditional<T>) node).execute(context, true);
		} else {
			return node.update(context);
		}
	}

	public void handleAborts(T context, TaskStatus status) {
		if (_hasLowerPriorityConditionalAbort) {
			updateLowerPriorityAbort(context, status);
		}
		if (abortType.has(AbortTypes.Self)) {
			updateSelfAbort(context, status);
		}
	}
}