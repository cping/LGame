/**
 * Copyright 2008 - 2023 The Loon Game Engine Authors
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
package loon.action.sprite;

import java.util.Comparator;
import java.util.List;

import loon.ZIndex;
import loon.utils.InsertionSorter;
import loon.utils.LayerSorter;
import loon.utils.MathUtils;
import loon.utils.TArray;

public class SpriteSorter<T> extends InsertionSorter<T> {

	public static int calcLowToHigh(ISprite p1, ISprite p2, boolean y) {
		if (p1 == p2) {
			return 0;
		}
		int p2v = 0;
		int p1v = 0;
		if (y) {
			p1v = MathUtils.floor(p1.getY() + p1.getOffsetY());
			p2v = MathUtils.floor(p2.getY() + p2.getOffsetY());

		} else {
			p1v = MathUtils.floor(p1.getX() + p1.getOffsetX());
			p2v = MathUtils.floor(p2.getX() + p2.getOffsetX());
		}
		return p2v - p1v;
	}

	public static int calcHighToLow(ISprite p1, ISprite p2, boolean y) {
		if (p1 == p2) {
			return 0;
		}
		int p2v = 0;
		int p1v = 0;
		if (y) {
			p1v = MathUtils.floor(p1.getY() + p1.getOffsetY());
			p2v = MathUtils.floor(p2.getY() + p2.getOffsetY());
		} else {
			p1v = MathUtils.floor(p1.getX() + p1.getOffsetX());
			p2v = MathUtils.floor(p2.getX() + p2.getOffsetX());

		}
		return p1v - p2v;
	}

	private class SpriteComparator implements Comparator<T> {

		private SpriteSorter<T> _sorter;

		public SpriteComparator(SpriteSorter<T> sorter) {
			this._sorter = sorter;
		}

		@Override
		public int compare(final T ea, final T eb) {
			if (ea == eb) {
				return 0;
			}
			if (ea == null || eb == null) {
				return 0;
			}
			if (ea instanceof ISprite && eb instanceof ISprite) {
				ISprite src = ((ISprite) ea);
				ISprite des = ((ISprite) eb);
				return _sorter.calc(src, des);
			}
			return 0;
		}

	}

	private boolean positive = false;

	private boolean sortY = true;

	private final SpriteComparator spriteComparator;

	public SpriteSorter() {
		this(true, false);
	}

	public SpriteSorter(boolean y, boolean p) {
		this.sortY = y;
		this.positive = p;
		this.spriteComparator = new SpriteComparator(this);
	}

	public int calc(ISprite p1, ISprite p2) {
		if (p1.autoXYSort() && p2.autoXYSort()) {
			return positive ? calcLowToHigh(p1, p2, sortY) : calcHighToLow(p1, p2, sortY);
		} else {
			return positive ? LayerSorter.calcLowToHigh((ZIndex) p1, (ZIndex) p2)
					: LayerSorter.calcHighToLow((ZIndex) p1, (ZIndex) p2);
		}
	}

	public void sort(final T[] es) {
		this.sort(es, this.spriteComparator);
	}

	public void sort(final T[] es, final int s, final int e) {
		this.sort(es, s, e, this.spriteComparator);
	}

	public void sort(final List<T> es) {
		this.sort(es, this.spriteComparator);
	}

	public void sort(final List<T> es, final int s, final int e) {
		this.sort(es, s, e, this.spriteComparator);
	}

	public void sort(final TArray<T> es) {
		this.sort(es, this.spriteComparator);
	}

	public void sort(final TArray<T> es, final int s, final int e) {
		this.sort(es, s, e, this.spriteComparator);
	}

	public boolean isSortY() {
		return sortY;
	}

	public SpriteSorter<T> setSortY(boolean sortY) {
		this.sortY = sortY;
		return this;
	}
}