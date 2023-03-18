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

import loon.LSystem;
import loon.utils.StringUtils;

public class Pair<T1, T2> implements IValueKey<T1>, IValueValue<T2>{

	private T1 o1;

	private T2 o2;

	private Pair(final T1 o1, final T2 o2) {
		this.o1 = o1;
		this.o2 = o2;
	}

	public T1 getLeft() {
		return o1;
	}

	public Pair<T1, T2> setLeft(T1 left) {
		this.o1 = left;
		return this;
	}

	public T2 getRight() {
		return o2;
	}

	public Pair<T1, T2> setRight(T2 right) {
		this.o2 = right;
		return this;
	}

	@Override
	public T1 getKey() {
		return o1;
	}
	
	@Override
	public T2 getValue() {
		return o2;
	}
	
	public final T1 get1() {
		return o1;
	}

	public final T2 get2() {
		return o2;
	}

	public final static <T1, T2> Pair<T1, T2> get(final T1 o1, final T2 o2) {
		return new Pair<>(o1, o2);
	}

	@Override
	public int hashCode() {
		if (o1 == null && o2 != null) {
			return super.hashCode() ^ o2.hashCode();
		}
		if (o2 == null && o1 != null) {
			return super.hashCode() ^ o1.hashCode();
		}
		if (o1 == null && o2 == null) {
			return super.hashCode();
		}
		return o1.hashCode() ^ o2.hashCode();
	}

	@Override
	public final boolean equals(final Object o) {
		if (!(o instanceof Pair)) {
			return false;
		}
		final Pair<?, ?> p = (Pair<?, ?>) o;
		return LSystem.equals(o1, p.o1) && LSystem.equals(o2, p.o2);
	}

	@Override
	public String toString() {
		return "[" + StringUtils.toString(o1) + ", " + StringUtils.toString(o2) + "]";
	}

}
