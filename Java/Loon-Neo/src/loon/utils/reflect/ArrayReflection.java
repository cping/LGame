/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.utils.reflect;

public final class ArrayReflection {

	static public Object newInstance(Class<?> c, int size) {
		return java.lang.reflect.Array.newInstance(c, size);
	}

	static public int getLength(Object array) {
		return java.lang.reflect.Array.getLength(array);
	}

	static public Object get(Object array, int index) {
		return java.lang.reflect.Array.get(array, index);
	}

	static public void set(Object array, int index, Object value) {
		java.lang.reflect.Array.set(array, index, value);
	}

}
