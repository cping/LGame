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

public class DecoratorConditional<T> extends Decorator<T> implements IConditional<T> {

	protected IConditional<T> _conditional;

	protected boolean _shouldReevaluate;

	protected TaskStatus _conditionalStatus;

	public DecoratorConditional(IConditional<T> conditional, boolean shouldReevalute) {
		_conditional = conditional;
		_shouldReevaluate = shouldReevalute;
	}

	public DecoratorConditional(IConditional<T> conditional) {
		this(conditional, true);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		_conditionalStatus = TaskStatus.Invalid;
	}

	@Override
	public void onStart() {
		_conditionalStatus = TaskStatus.Invalid;
	}

	@Override
	public void onEnd() {

	}

	public TaskStatus update(T context) {
		_conditionalStatus = execute(context, false);
		if (_conditionalStatus == TaskStatus.Success) {
			return child.tick(context);
		}
		return TaskStatus.Failure;
	}

	public TaskStatus execute(T context, boolean forceUpdate) {
		if (forceUpdate || _shouldReevaluate || _conditionalStatus == TaskStatus.Invalid) {
			_conditionalStatus = _conditional.update(context);
		}
		return _conditionalStatus;
	}

}