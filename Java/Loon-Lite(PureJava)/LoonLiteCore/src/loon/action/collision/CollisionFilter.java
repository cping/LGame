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

	private static CollisionFilter _defaultFilter;

	public static CollisionFilter getDefault() {
		if (_defaultFilter == null) {
			synchronized (CollisionFilter.class) {
				if (_defaultFilter == null) {
					_defaultFilter = new CollisionFilter() {
						@Override
						public CollisionResult filter(ActionBind obj, ActionBind other) {
							return CollisionResult.newSlide();
						}
					};

				}
			}
		}
		return _defaultFilter;
	}

	private static CollisionFilter _crossFilter;

	public static CollisionFilter getCross() {
		if (_crossFilter == null) {
			synchronized (CollisionFilter.class) {
				_crossFilter = new CollisionFilter() {
					@Override
					public CollisionResult filter(ActionBind obj, ActionBind other) {
						return CollisionResult.newCross();
					}
				};
			}
		}
		return _crossFilter;
	}

	private static CollisionFilter _touchFilter;

	public static CollisionFilter getTouch() {
		if (_touchFilter == null) {
			synchronized (CollisionFilter.class) {
				if (_touchFilter == null) {
					_touchFilter = new CollisionFilter() {
						@Override
						public CollisionResult filter(ActionBind obj, ActionBind other) {
							return CollisionResult.newTouch();
						}
					};
				}
			}
		}
		return _touchFilter;
	}

	private static CollisionFilter _bulletFilter;

	public static CollisionFilter getBullet() {
		if (_bulletFilter == null) {
			synchronized (CollisionFilter.class) {
				if (_bulletFilter == null) {
					_bulletFilter = new CollisionFilter() {
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
		return _bulletFilter;
	}

	public static void freeStatic(){
		_defaultFilter = null;
		_crossFilter = null;
		_touchFilter = null;
		_bulletFilter = null;
	}

}
