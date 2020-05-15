package loon.utils;

import java.util.Comparator;
import java.util.List;

import loon.ZIndex;

public class LayerSorter<T> extends InsertionSorter<T> {

	private boolean positive = false;

	private final Comparator<T> zIndexComparator = new Comparator<T>() {
		@Override
		public int compare(final T ea, final T eb) {
			if (ea != null && eb == null) {
				if (positive) {
					if (ea instanceof ZIndex) {
						return ((ZIndex) ea).getLayer();
					}
				}else{
					if (ea instanceof ZIndex) {
						return -((ZIndex) ea).getLayer();
					}
				}
			}
			if (ea == null && eb != null) {
				if (positive) {
					if (eb instanceof ZIndex) {
						return ((ZIndex) eb).getLayer();
					}
				}else{
					if (eb instanceof ZIndex) {
						return -((ZIndex) eb).getLayer();
					}
				}
			}
			if (ea instanceof ZIndex && eb instanceof ZIndex) {
				if (positive) {
					return ((ZIndex) ea).getLayer() - ((ZIndex) eb).getLayer();
				} else {
					return ((ZIndex) eb).getLayer() - ((ZIndex) ea).getLayer();
				}
			}
			return 0;
		}
	};

	public LayerSorter() {
		this(false);
	}

	public LayerSorter(boolean p) {
		this.positive = p;
	}

	public void sort(final T[] es) {
		this.sort(es, this.zIndexComparator);
	}

	public void sort(final T[] es, final int s, final int e) {
		this.sort(es, s, e, this.zIndexComparator);
	}

	public void sort(final List<T> es) {
		this.sort(es, this.zIndexComparator);
	}

	public void sort(final List<T> es, final int s, final int e) {
		this.sort(es, s, e, this.zIndexComparator);
	}

	public void sort(final TArray<T> es) {
		this.sort(es, this.zIndexComparator);
	}

	public void sort(final TArray<T> es, final int s, final int e) {
		this.sort(es, s, e, this.zIndexComparator);
	}
}