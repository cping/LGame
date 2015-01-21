/**
 * Copyright 2013 The Loon Authors
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
 */
package loon.foundation;

import loon.utils.collection.ArrayList;

public class NSMutableArray extends NSArray {

	public static NSMutableArray array() {
		return new NSMutableArray();
	}

	public static NSMutableArray arrayWithObject(NSObject o) {
		return new NSMutableArray(o);
	}

	public static NSMutableArray arrayWithObjects(NSObject... objects) {
		return new NSMutableArray(objects);
	}

	public static NSMutableArray arrayWithArray(NSArray array) {
		return new NSMutableArray(array);
	}

	public static NSMutableArray arrayWithCapacity(int capacity) {
		return new NSMutableArray(capacity);
	}

	public NSMutableArray(NSArray array) {
		this._list = array._list;
	}

	public NSMutableArray(NSObject... objects) {
		_list = new ArrayList(objects.length);
		for (NSObject o : objects) {
			_list.add(o);
		}
	}

	public NSMutableArray(NSObject o) {
		_list = new ArrayList(1);
		_list.add(o);
	}

	public NSMutableArray(int capacity) {
		_list = new ArrayList(capacity);
	}

	public NSMutableArray() {
		_list = new ArrayList();
	}

	public void removeAllObjects() {
		_list.clear();
	}

	public void addObjectsFromArray(NSArray a) {
		_list.addAll(a._list);
	}

	public void replaceObject(int index, NSObject o) {
		_list.set(index, o);
	}
}
