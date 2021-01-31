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

public class Repeater<T> extends Decorator<T> {

	public int count;

	public boolean repeatForever;

	public boolean endOnFailure;

	protected int _iterationCount;

	public Repeater(int count) {
		this(count, false);
	}
	
	public Repeater(int count, boolean endOnFailure) {
		this.count = count;
		this.endOnFailure = endOnFailure;
	}

	public Repeater(boolean repeatForever, boolean endOnFailure) {
		this.repeatForever = repeatForever;
		this.endOnFailure = endOnFailure;
	}

	@Override
	public void onStart() {
		_iterationCount = 0;
	}

	@Override
	public TaskStatus update(T context) {
		if (!repeatForever && _iterationCount == count) {
			return TaskStatus.Success;
		}

		TaskStatus status = child.tick(context);
		_iterationCount++;

		if (endOnFailure && status == TaskStatus.Failure) {
			return TaskStatus.Success;
		}

		if (!repeatForever && _iterationCount == count) {
			return TaskStatus.Success;
		}

		return TaskStatus.Running;
	}

	@Override
	public void onEnd() {
		
	}
}