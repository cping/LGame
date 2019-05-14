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

	private static CollisionFilter defaultFilter;

	public static CollisionFilter getDefault() {
		if (defaultFilter == null) {
			synchronized (CollisionFilter.class) {
				if (defaultFilter == null) {
					defaultFilter = new CollisionFilter() {
						@Override
						public CollisionResult filter(ActionBind obj, ActionBind other) {
							return CollisionResult.newSlide();
						}
					};

				}
			}
		}
		return defaultFilter;
	}

	private static CollisionFilter crossFilter;

	public static CollisionFilter getCross() {
		if (crossFilter == null) {
			synchronized (CollisionFilter.class) {
				crossFilter = new CollisionFilter() {
					@Override
					public CollisionResult filter(ActionBind obj, ActionBind other) {
						return CollisionResult.newCross();
					}
				};
			}
		}
		return crossFilter;
	}

	private static CollisionFilter touchFilter;

	public static CollisionFilter getTouch() {
		if (touchFilter == null) {
			synchronized (CollisionFilter.class) {
				if (touchFilter == null) {
					touchFilter = new CollisionFilter() {
						@Override
						public CollisionResult filter(ActionBind obj, ActionBind other) {
							return CollisionResult.newTouch();
						}
					};
				}
			}
		}
		return touchFilter;
	}

	private static CollisionFilter bulletFilter;

	public static CollisionFilter getBullet() {
		if (bulletFilter == null) {
			synchronized (CollisionFilter.class) {
				if (bulletFilter == null) {
					bulletFilter = new CollisionFilter() {
						@Override
						public CollisionResult filter(ActionBind obj, ActionBind other) {
							if (obj == null || other == null) {
								return CollisionResult.newSlide();
							}
							if (obj == other || obj.equals(other)) {
								return CollisionResult.newCross();
							} else {
								return CollisionResult.newTouch();
							}
						}
					};
				}
			}
		}
		return bulletFilter;
	}

}
