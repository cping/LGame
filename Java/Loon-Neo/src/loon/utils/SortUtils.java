/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain srcArray copy of
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

	public static void msort(Object[] srcArray, Object[] dest, Comparator<Object> compar) {
		msort(srcArray, dest, 0, srcArray.length - 1, compar);
	}

	public static void msort(Object[] srcArray, Object[] dest, int low, int high, Comparator<Object> compar) {
		if (low < high) {
			int center = (low + high) / 2;
			msort(srcArray, dest, low, center, compar);
			msort(srcArray, dest, center + 1, high, compar);
			merge(srcArray, dest, low, center + 1, high, compar);
		}
	}

	private static void merge(Object[] srcArray, Object[] dest, int low, int middle, int high,
			Comparator<Object> compar) {
		int leftEnd = middle - 1;
		int pos = low;
		int numElements = high - low + 1;

		for (; low <= leftEnd && middle <= high;) {
			if (compar.compare(srcArray[low], srcArray[middle]) <= 0) {
				dest[pos++] = srcArray[low++];
			} else {
				dest[pos++] = srcArray[middle++];
			}
		}

		for (; low <= leftEnd;) {
			dest[pos++] = srcArray[low++];
		}

		for (; middle <= high;) {
			dest[pos++] = srcArray[middle++];
		}

		for (int i = 0; i < numElements; i++, high--) {
			srcArray[high] = dest[high];
		}
	}

	public static void quickSort(Object[] a, Comparator<Object> compar) {
		quickSort(a, 0, a.length - 1, compar);
	}

	public static void quickSort(Object[] a, int lo0, int hi0, Comparator<Object> compar) {
		if (hi0 <= lo0) {
			return;
		}
		Object t;
		if (hi0 - lo0 == 1) {
			if (compar.compare(a[hi0], a[lo0]) < 0) {
				t = a[lo0];
				a[lo0] = a[hi0];
				a[hi0] = t;
			}
			return;
		}
		Object mid = a[(lo0 + hi0) / 2];
		int lo = lo0 - 1, hi = hi0 + 1;
		for (;;) {
			for (; compar.compare(a[++lo], mid) < 0;)
				;
			for (; compar.compare(mid, a[--hi]) < 0;)
				;
			if (hi > lo) {
				t = a[lo];
				a[lo] = a[hi];
				a[hi] = t;
			} else {
				break;
			}
		}
		if (lo0 < lo - 1) {
			quickSort(a, lo0, lo - 1, compar);
		}
		if (hi + 1 < hi0) {
			quickSort(a, hi + 1, hi0, compar);
		}
	}

	public static void gnomeSort(Object[] srcArray, Comparator<Object> compar) {
		int pos = 1;
		int last = 0;
		int length = srcArray.length;
		for (; pos < length;) {
			if (compar.compare(srcArray[pos], srcArray[pos - 1]) >= 0) {
				if (last != 0) {
					pos = last;
					last = 0;
				}
				pos++;
			} else {
				Object tmp = srcArray[pos];
				srcArray[pos] = srcArray[pos - 1];
				srcArray[pos - 1] = tmp;
				if (pos > 1) {
					if (last == 0) {
						last = pos;
					}
					pos--;
				} else {
					pos++;
				}
			}
		}
	}

	public static void insert(Object obj) {
		_heap[(_num++)] = obj;
		int i = _num;
		int j = i / 2;
		for (; (i > 1) && (_comparator.compare(_heap[(i - 1)], _heap[(j - 1)]) < 0);) {
			Object tgt = _heap[(i - 1)];
			_heap[(i - 1)] = _heap[(j - 1)];
			_heap[(j - 1)] = tgt;
			i = j;
			j = i / 2;
		}
	}

	public static Object getResult() {
		Object res = _heap[0];
		_heap[0] = _heap[(--_num)];
		int i = 1;
		int j = i * 2;
		while (j <= _num) {
			if ((j + 1 <= _num) && (_comparator.compare(_heap[(j - 1)], _heap[j]) > 0))
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

	public static void sort(Object[] srcArray, Comparator<Object> compar) {
		_comparator = compar;
		_heap = new Object[srcArray.length];
		_num = 0;
		for (_target = 0; _target < srcArray.length; _target += 1) {
			insert(srcArray[_target]);
		}
		for (_target = 0; _num > 0; _target += 1) {
			srcArray[_target] = getResult();
		}
		_comparator = null;
		_heap = null;
	}
}
