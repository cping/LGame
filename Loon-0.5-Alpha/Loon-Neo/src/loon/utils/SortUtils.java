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
