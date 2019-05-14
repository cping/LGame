/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.action.collision;

import loon.action.ActionBind;

public abstract class CollisionFilter {

	public abstract CollisionResult filter(ActionBind obj, ActionBind other);

	public static CollisionFilter defaultFilter = new CollisionFilter() {
		@Override
		public CollisionResult filter(ActionBind obj, ActionBind other) {
			return CollisionResult.slide;
		}
	};

	public static CollisionFilter crossFilter = new CollisionFilter() {
		@Override
		public CollisionResult filter(ActionBind obj, ActionBind other) {
			return CollisionResult.cross;
		}
	};

	public static CollisionFilter bulletFilter = new CollisionFilter() {
		@Override
		public CollisionResult filter(ActionBind obj, ActionBind other) {
			if (obj == null || other == null) {
				return CollisionResult.slide;
			}
			if (obj == other || obj.equals(other)) {
				return CollisionResult.cross;
			} else {
				return CollisionResult.touch;
			}
		}
	};

}
