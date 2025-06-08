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

import java.util.Comparator;

import loon.LRelease;
import loon.action.ActionBind;
import loon.utils.BoolArray;
import loon.utils.FloatArray;
import loon.utils.IntArray;
import loon.utils.IntMap;
import loon.utils.IntMap.Entry;
import loon.utils.MathUtils;
import loon.utils.TArray;

public class Collisions implements Comparator<Integer>, LRelease {

	private final static int INIT_SIZE = 4;
	private final BoolArray overlaps = new BoolArray(INIT_SIZE);
	private final FloatArray tis = new FloatArray(INIT_SIZE);
	private final FloatArray moveXs = new FloatArray(INIT_SIZE);
	private final FloatArray moveYs = new FloatArray(INIT_SIZE);
	private final FloatArray normalXs = new FloatArray(INIT_SIZE);
	private final FloatArray normalYs = new FloatArray(INIT_SIZE);
	private final FloatArray touchXs = new FloatArray(INIT_SIZE);
	private final FloatArray touchYs = new FloatArray(INIT_SIZE);
	private final FloatArray x1s = new FloatArray(INIT_SIZE);
	private final FloatArray y1s = new FloatArray(INIT_SIZE);
	private final FloatArray w1s = new FloatArray(INIT_SIZE);
	private final FloatArray h1s = new FloatArray(INIT_SIZE);
	private final FloatArray x2s = new FloatArray(INIT_SIZE);
	private final FloatArray y2s = new FloatArray(INIT_SIZE);
	private final FloatArray w2s = new FloatArray(INIT_SIZE);
	private final FloatArray h2s = new FloatArray(INIT_SIZE);
	private final IntArray order = new IntArray(INIT_SIZE);
	private final IntMap<Integer> swapMap = new IntMap<Integer>(INIT_SIZE);
	public TArray<ActionBind> items = new TArray<ActionBind>(INIT_SIZE);
	public TArray<ActionBind> others = new TArray<ActionBind>(INIT_SIZE);
	public TArray<CollisionResult> types = new TArray<CollisionResult>(INIT_SIZE);
	private int size = 0;

	public void add(CollisionData col) {
		add(col.overlaps, col.ti, col.move.x, col.move.y, col.normal.x, col.normal.y, col.touch.x, col.touch.y,
				col.itemRect.x, col.itemRect.y, col.itemRect.width, col.itemRect.height, col.otherRect.x,
				col.otherRect.y, col.otherRect.width, col.otherRect.height, col.item, col.other, col.type);
	}

	public void add(boolean overlap, float ti, float moveX, float moveY, float normalX, float normalY, float touchX,
			float touchY, float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2,
			ActionBind item, ActionBind other, CollisionResult type) {
		size++;
		overlaps.add(overlap);
		tis.add(ti);
		moveXs.add(moveX);
		moveYs.add(moveY);
		normalXs.add(normalX);
		normalYs.add(normalY);
		touchXs.add(touchX);
		touchYs.add(touchY);
		x1s.add(x1);
		y1s.add(y1);
		w1s.add(w1);
		h1s.add(h1);
		x2s.add(x2);
		y2s.add(y2);
		w2s.add(w2);
		h2s.add(h2);
		items.add(item);
		others.add(other);
		types.add(type);
	}

	private final CollisionData collision = new CollisionData();

	public CollisionData get(int index) {
		if (index >= size) {
			return null;
		}
		collision.set(overlaps.get(index), tis.get(index), moveXs.get(index), moveYs.get(index), normalXs.get(index),
				normalYs.get(index), touchXs.get(index), touchYs.get(index), x1s.get(index), y1s.get(index),
				w1s.get(index), h1s.get(index), x2s.get(index), y2s.get(index), w2s.get(index), h2s.get(index));
		collision.item = items.get(index);
		collision.other = others.get(index);
		collision.type = types.get(index);
		return collision;
	}

	public void remove(int index) {
		if (index < size) {
			size--;
			overlaps.removeIndex(index);
			tis.removeIndex(index);
			moveXs.removeIndex(index);
			moveYs.removeIndex(index);
			normalXs.removeIndex(index);
			normalYs.removeIndex(index);
			touchXs.removeIndex(index);
			touchYs.removeIndex(index);
			x1s.removeIndex(index);
			y1s.removeIndex(index);
			w1s.removeIndex(index);
			h1s.removeIndex(index);
			x2s.removeIndex(index);
			y2s.removeIndex(index);
			w2s.removeIndex(index);
			h2s.removeIndex(index);
			items.removeIndex(index);
			others.removeIndex(index);
			types.removeIndex(index);
		}
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public void clear() {
		size = 0;
		overlaps.clear();
		tis.clear();
		moveXs.clear();
		moveYs.clear();
		normalXs.clear();
		normalYs.clear();
		touchXs.clear();
		touchYs.clear();
		x1s.clear();
		y1s.clear();
		w1s.clear();
		h1s.clear();
		x2s.clear();
		y2s.clear();
		w2s.clear();
		h2s.clear();
		items.clear();
		others.clear();
		types.clear();
	}

	public <T extends Comparable<T>> void keySort(final IntArray indices, TArray<ActionBind> list) {
		swapMap.clear();
		for (int i = 0; i < indices.size(); i++) {
			int k = indices.get(i);
			while (swapMap.containsKey(k)) {
				k = swapMap.get(k, Integer.valueOf(0));
			}
			swapMap.put(i, Integer.valueOf(k));
		}
		Entry<Integer>[] entrys = swapMap.getEntrys();
		for (int i = 0; i < entrys.length; i++) {
			swap(list, (int) entrys[i].key, entrys[i].value.intValue());
		}
	}

	public <T extends Comparable<T>> void keySortResult(final IntArray indices, TArray<CollisionResult> list) {
		swapMap.clear();
		for (int i = 0; i < indices.size(); i++) {
			int k = indices.get(i);
			while (swapMap.containsKey(k)) {
				k = swapMap.get(k, Integer.valueOf(0));
			}
			swapMap.put(i, Integer.valueOf(k));
		}
		Entry<Integer>[] entrys = swapMap.getEntrys();
		for (int i = 0; i < entrys.length; i++) {
			swapResult(list, (int) entrys[i].key, entrys[i].value.intValue());
		}
	}

	public static void swapResult(TArray<CollisionResult> list, int i, int j) {
		if (i >= list.size || j >= list.size) {
			return;
		}
		final TArray<CollisionResult> l = list;
		CollisionResult t = l.get(i);
		l.set(j, t);
		l.set(i, t);
	}

	public static void swap(TArray<ActionBind> list, int i, int j) {
		if (i >= list.size || j >= list.size) {
			return;
		}
		final TArray<ActionBind> l = list;
		ActionBind t = l.get(i);
		l.set(j, t);
		l.set(i, t);
	}

	public <T extends Comparable<T>> void keySort(final IntArray indices, FloatArray list) {
		swapMap.clear();
		for (int i = 0; i < indices.size(); i++) {
			int k = indices.get(i);
			while (swapMap.containsKey(k)) {
				k = swapMap.get(k, Integer.valueOf(0));
			}

			swapMap.put(i, Integer.valueOf(k));
		}
		Entry<Integer>[] entrys = swapMap.getEntrys();
		for (int i = 0; i < entrys.length; i++) {
			Entry<Integer> e = entrys[i];
			int key = (int) e.key;
			int value = e.value.intValue();
			if (key < list.length && value < list.length) {
				list.swap(key, value);
			}
		}
	}

	public <T extends Comparable<T>> void keySort(final IntArray indices, BoolArray list) {
		swapMap.clear();
		for (int i = 0; i < indices.size(); i++) {
			int k = indices.get(i);
			while (swapMap.containsKey(k)) {
				k = swapMap.get(k, Integer.valueOf(0));
			}
			swapMap.put(i, Integer.valueOf(k));
		}
		Entry<Integer>[] entrys = swapMap.getEntrys();
		for (int i = 0; i < entrys.length; i++) {
			Entry<Integer> e = entrys[i];
			int key = (int) e.key;
			int value = e.value.intValue();
			if (key < list.length && value < list.length) {
				list.swap(key, value);
			}
		}
	}

	public void sort() {
		order.clear();
		for (int i = 0; i < size; i++) {
			order.add(i);
		}
		order.sort();
		keySort(order, overlaps);
		keySort(order, tis);
		keySort(order, moveXs);
		keySort(order, moveYs);
		keySort(order, normalXs);
		keySort(order, normalYs);
		keySort(order, touchXs);
		keySort(order, touchYs);
		keySort(order, x1s);
		keySort(order, y1s);
		keySort(order, w1s);
		keySort(order, h1s);
		keySort(order, x2s);
		keySort(order, y2s);
		keySort(order, w2s);
		keySort(order, h2s);
		keySort(order, items);
		keySort(order, others);
		keySortResult(order, types);
	}

	@Override
	public int compare(Integer a, Integer b) {
		if (tis.get(a) == (tis.get(b))) {

			float ad = CollisionHelper.getSquareDistance(x1s.get(a), y1s.get(a), w1s.get(a), h1s.get(a), x2s.get(a),
					y2s.get(a), w2s.get(a), h2s.get(a));
			float bd = CollisionHelper.getSquareDistance(x1s.get(a), y1s.get(a), w1s.get(a), h1s.get(a), x2s.get(b),
					y2s.get(b), w2s.get(b), h2s.get(b));

			return MathUtils.compare(ad, bd);
		}
		return MathUtils.compare(tis.get(a), tis.get(b));
	}

	@Override
	public void close() {
		overlaps.clear();
		tis.clear();
		moveXs.clear();
		moveYs.clear();
		normalXs.clear();
		normalYs.clear();
		touchXs.clear();
		touchYs.clear();
		x1s.clear();
		y1s.clear();
		w1s.clear();
		h1s.clear();
		x2s.clear();
		y2s.clear();
		w2s.clear();
		h2s.clear();
		order.clear();
		swapMap.clear();
		items.clear();
		others.clear();
		types.clear();
	}

}
