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
package loon.utils.reply;

import java.util.Iterator;

import loon.LSysException;

public final class KeyValuePair<K, V> implements IValueKey<K>, IValueValue<V> {

	public static <K, V> KeyValuePair<K, V> with(final K key, final V value) {
		return new KeyValuePair<K, V>(key, value);
	}

	public static <X> KeyValuePair<X, X> fromArray(final X[] array) {
		if (array == null) {
			throw new LSysException("Array cannot be null");
		}
		if (array.length != 2) {
			throw new LSysException(
					"Array must have exactly 2 elements in order to create a KeyValuePair. Size is " + array.length);
		}
		return new KeyValuePair<X, X>(array[0], array[1]);
	}

	public static <X> KeyValuePair<X, X> fromIterable(final Iterable<X> iterable) {
		return fromIterable(iterable, 0, true);
	}

	public static <X> KeyValuePair<X, X> fromIterable(final Iterable<X> iterable, int index) {
		return fromIterable(iterable, index, false);
	}

	private static <X> KeyValuePair<X, X> fromIterable(final Iterable<X> iterable, int index, final boolean exactSize) {

		if (iterable == null) {
			throw new LSysException("Iterable cannot be null");
		}

		boolean tooFewElements = false;

		X element0 = null;
		X element1 = null;

		final Iterator<X> iter = iterable.iterator();

		int i = 0;
		while (i < index) {
			if (iter.hasNext()) {
				iter.next();
			} else {
				tooFewElements = true;
			}
			i++;
		}
		if (iter.hasNext()) {
			element0 = iter.next();
		} else {
			tooFewElements = true;
		}
		if (iter.hasNext()) {
			element1 = iter.next();
		} else {
			tooFewElements = true;
		}

		if (tooFewElements && exactSize) {
			throw new LSysException("Not enough elements for creating a KeyValuePair (2 needed)");
		}
		if (iter.hasNext() && exactSize) {
			throw new LSysException(
					"Iterable must have exactly 2 available elements in order to create a KeyValuePair.");
		}

		return new KeyValuePair<X, X>(element0, element1);
	}

	private final K key;
	private final V value;

	public KeyValuePair(final K key, final V value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		return this.key;
	}

	@Override
	public V getValue() {
		return this.value;
	}

	public <X> KeyValuePair<X, V> makeKey(final X key) {
		return new KeyValuePair<X, V>(key, this.value);
	}

	public <Y> KeyValuePair<K, Y> makeValue(final Y value) {
		return new KeyValuePair<K, Y>(this.key, value);
	}

}