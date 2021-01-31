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

public class ParallelSelector<T> extends Composite<T> {
	@Override
	public TaskStatus update(T context) {
		boolean flag = true;
		for (int i = 0; i < _children.size; i++) {
			Behavior<T> child = _children.get(i);
			child.tick(context);

			if (child.status == TaskStatus.Success) {
				return TaskStatus.Success;
			}
			if (child.status != TaskStatus.Failure) {
				flag = false;
			}
		}
		if (flag) {
			return TaskStatus.Failure;
		}
		return TaskStatus.Running;
	}
}