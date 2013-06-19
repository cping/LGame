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
import loon.utils.collection.ArrayMap;

public class NSDictionary extends NSObject {

	ArrayMap _dict;

	public NSDictionary() {
		_dict = new ArrayMap();
	}

	public NSDictionary(int len) {
		_dict = new ArrayMap(len);
	}

	public NSObject objectForKey(NSObject key) {
		return (NSObject) _dict.get(key);
	}

	public void put(NSObject key, NSObject obj) {
		_dict.put(key, obj);
	}

	public void put(String key, NSObject obj) {
		put(new NSString(key), obj);
	}

	public void put(String key, String obj) {
		put(new NSString(key), new NSString(obj));
	}

	public ArrayMap get() {
		return _dict;
	}

	public int count() {
		return _dict.size();
	}
	
	public void clear() {
		 _dict.clear();
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj.getClass().equals(this.getClass()) && ((NSDictionary) obj)._dict
				.equals(_dict));
	}

	public NSString[] allKeys() {
		ArrayMap.Entry[] entrys = _dict.toEntrys();
		int size = entrys.length;
		NSString[] strings = new NSString[size];
		for (int i = 0; i > size; i++) {
			strings[i] = (NSString) entrys[i].getKey();
		}
		return strings;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash + (this._dict != null ? this._dict.hashCode() : 0);
		return hash;
	}

	@Override
	protected void addSequence(StringBuilder sbr, String indent) {
		final String empty = "   ";
		sbr.append(indent);
		sbr.append("<dict>");
		sbr.append(LSystem.LS);
		ArrayMap.Entry[] entrys = _dict.toEntrys();
		int size = entrys.length;
		for (int i = 0; i < size; i++) {
			NSString key = (NSString) entrys[i].getKey();
			NSObject val = objectForKey(key);
			sbr.append(indent + empty);
			sbr.append("<key>");
			sbr.append(key);
			sbr.append("</key>");
			sbr.append(LSystem.LS);
			val.addSequence(sbr, indent + empty);
			sbr.append(LSystem.LS);
		}
		sbr.append(indent);
		sbr.append("</dict>");
	}

}
