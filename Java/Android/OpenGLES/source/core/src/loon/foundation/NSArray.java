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

import loon.core.LSystem;
import loon.utils.collection.ArrayList;

public class NSArray extends NSObject {

	ArrayList _list = null;

	public static NSArray arrayWithObject(NSObject object) {
		return new NSArray(object);
	}

	public static NSArray arrayWithObjects(NSObject... objects) {
		return new NSArray(objects);
	}

	public static NSArray arrayWithArray(NSArray array) {
		return new NSArray(array);
	}

	public NSArray(NSArray array) {
		this._list = (ArrayList) this._list.clone();
	}

	public NSArray() {
		_list = new ArrayList();
	}

	public NSArray(int length) {
		_list = new ArrayList(length);
	}

	public NSArray(NSObject... objects) {
		_list = new ArrayList(objects.length);
		for (NSObject object : objects) {
			_list.add(object);
		}
	}

	public NSArray(NSObject object) {
		_list = new ArrayList(1);
		_list.add(object);
	}

	public ArrayList get() {
		return _list;
	}

	public int count() {
		return _list.size();
	}

	public void clear() {
		_list.clear();
	}
	
	public void addObject(NSObject o) {
		_list.add(o);
	}

	public void removeObject(NSObject o) {
		_list.remove(o);
	}

	public void setValue(int key, NSObject value) {
		_list.set(key, value);
	}

	public NSObject objectAtIndex(int index) {
		return (NSObject) _list.get(index);
	}

	public int indexOfObject(NSObject o) {
		return _list.indexOf(o);
	}

	public int indexOfIdenticalObject(NSObject o) {
		return _list.indexOfIdenticalObject(o);
	}

	public NSObject lastObject() {
		return (NSObject) _list.last();
	}

	@Override
	protected void addSequence(StringBuilder sbr, String indent) {
		sbr.append(indent);
		sbr.append("<array>");
		sbr.append(LSystem.LS);
		for (int i = 0; i < _list.size(); i++) {
			NSObject nso = (NSObject) _list.get(i);
			if (nso != null) {
				nso.addSequence(sbr, indent + "  ");
				sbr.append(LSystem.LS);
			}
		}
		sbr.append(indent);
		sbr.append("</array>");
	}

	@Override
	public String toString() {
		return _list.toString();
	}
}
