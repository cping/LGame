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
package loon.utils;

import java.util.Comparator;

public class SortUtils {

	private static Comparator<Object> _comparator;
	private static Object[] _heap;
	private static int _num;
	private static int _target;

	public static void insert(Object obj) {
		_heap[(_num++)] = obj;
		int i = _num;
		int j = i / 2;
		for (; (i > 1)
				&& (_comparator.compare(_heap[(i - 1)], _heap[(j - 1)]) < 0);) {
			Object tgt = _heap[(i - 1)];
			_heap[(i - 1)] = _heap[(j - 1)];
			_heap[(j - 1)] = tgt;
			i = j;
			j = i / 2;
		}
	}

	public static Object deletemin() {
		Object res = _heap[0];
		_heap[0] = _heap[(--_num)];
		int i = 1;
		int j = i * 2;
		while (j <= _num) {
			if ((j + 1 <= _num)
					&& (_comparator.compare(_heap[(j - 1)], _heap[j]) > 0))
				j++;
			if (_comparator.compare(_heap[(i - 1)], _heap[(j - 1)]) > 0) {
				Object tgt = _heap[(i - 1)];
				_heap[(i - 1)] = _heap[(j - 1)];
				_heap[(j - 1)] = tgt;
			}
			i = j;
			j = i * 2;
		}
		return res;
	}

	public static void sort(Object[] srcArray, Comparator<Object> cmprtr) {
		_comparator = cmprtr;
		_heap = new Object[srcArray.length];
		_num = 0;
		for (_target = 0; _target < srcArray.length; _target += 1) {
			insert(srcArray[_target]);
		}
		for (_target = 0; _num > 0; _target += 1) {
			srcArray[_target] = deletemin();
		}
		_comparator = null;
		_heap = null;
	}
}
