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
package loon.utils;

/**
 * 其实Array就应该是Stack,最早起名时瞎起名,改名牵扯类比较多,懒得改了,衍生个子类,特此声明……
 */
public class Stack<T> extends Array<T> {

	public boolean empty() {
		return this.isEmpty();
	}

	public int serach(T o) {
		int i = lastIndexOf(o);
		if (i >= 0) {
			return size() - i;
		}
		return -1;
	}

}
