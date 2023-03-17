package loon.utils;

import java.util.Comparator;
import java.util.List;

import loon.ZIndex;

public class LayerSorter<T> extends InsertionSorter<T> {

	public static int calcLowToHigh(ZIndex p1, ZIndex p2) {
		if (p1 == p2) {
			return 0;
		}
		return p1.getLayer() - p2.getLayer();
	}

	public static int calcHighToLow(ZIndex p1, ZIndex p2) {
		if (p1 == p2) {
			return 0;
		}
		return p2.getLayer() - p1.getLayer();
	}

	private class ZIndexComparator implements Comparator<T> {

		private LayerSorter<T> _sorter;

		public ZIndexComparator(LayerSorter<T> sorter) {
			this._sorter = sorter;
		}

		@Override
		public int compare(final T ea, final T eb) {
			if ((ea == eb) || ea == null || eb == null) {
				return 0;
			}
			if (ea instanceof ZIndex && eb instanceof ZIndex) {
				ZIndex src = ((ZIndex) ea);
				ZIndex des = ((ZIndex) eb);
				return _sorter.calc(src, des);
			}
			return 0;
		}

	}

	private boolean positive = false;

	private final ZIndexComparator zindexComparator;

	public LayerSorter() {
		this(false);
	}

	public LayerSorter(boolean p) {
		this.positive = p;
		this.zindexComparator = new ZIndexComparator(this);
	}

	public int calc(ZIndex p1, ZIndex p2) {
		return positive ? calcLowToHigh(p1, p2) : calcHighToLow(p1, p2);
	}

	public void sort(final T[] es) {
		this.sort(es, this.zindexComparator);
	}

	public void sort(final T[] es, final int s, final int e) {
		this.sort(es, s, e, this.zindexComparator);
	}

	public void sort(final List<T> es) {
		this.sort(es, this.zindexComparator);
	}

	public void sort(final List<T> es, final int s, final int e) {
		this.sort(es, s, e, this.zindexComparator);
	}

	public void sort(final TArray<T> es) {
		this.sort(es, this.zindexComparator);
	}

	public void sort(final TArray<T> es, final int s, final int e) {
		this.sort(es, s, e, this.zindexComparator);
	}
}