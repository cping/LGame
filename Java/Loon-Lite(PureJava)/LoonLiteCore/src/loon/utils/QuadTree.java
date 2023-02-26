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
package loon.utils;

public class QuadTree<T> {

	private interface QuadTreeIFace<T> {

		public void set(final int x, final int y, final T value);

		public T get(final int x, final int y);
	}

	private static class Grid<T> implements QuadTreeIFace<T> {

		private final TArray<T> values;

		private final int dimension;

		public Grid(final int dimension) {
			this.dimension = dimension;
			values = new TArray<T>(dimension * dimension);
		}

		@Override
		public T get(int x, int y) {
			if (x >= 0 && y >= 0 && x < dimension && y < dimension) {
				return values.get(x * dimension + y);
			}
			return null;
		}

		@Override
		public void set(int x, int y, T value) {
			if (x >= 0 && y >= 0 && x < dimension && y < dimension) {
				values.set(x * dimension + y, value);
			}
		}
	}

	private static class Quad<T> implements QuadTreeIFace<T> {

		private final TArray<QuadTreeIFace<T>> quads;
		private final int size;

		protected Quad(int size) {
			this.size = size;
			quads = new TArray<QuadTreeIFace<T>>(size);
		}

		@Override
		public T get(int x, int y) {
			int quad = getIndex(x, y);
			if (quads.get(quad) == null) {
				return null;
			}
			int innerX;
			int innerY;
			if (quads.get(quad) instanceof Grid) {
				innerX = x + ((x < 0) ? size : 0);
				innerY = y + ((y < 0) ? size : 0);
			} else {
				innerX = x + ((x < 0) ? size : 0) - size / 2;
				innerY = y + ((y < 0) ? size : 0) - size / 2;
			}
			return quads.get(quad).get(innerX, innerY);
		}

		@Override
		public void set(final int x, final int y, final T value) {
			int quad = getIndex(x, y);
			if (quads.get(quad) == null) {
				if (size <= CollectionUtils.INITIAL_CAPACITY) {
					quads.set(quad, new Grid<T>(size));
				} else {
					quads.set(quad, new Quad<T>(size / 2));
				}
			}
			int innerX;
			int innerY;
			if (quads.get(quad) instanceof Grid) {
				innerX = x + ((x < 0) ? size : 0);
				innerY = y + ((y < 0) ? size : 0);
			} else {
				innerX = x + ((x < 0) ? size : 0) - size / 2;
				innerY = y + ((y < 0) ? size : 0) - size / 2;
			}
			quads.get(quad).set(innerX, innerY, value);
		}

		private static int getIndex(int signX, int signY) {
			int quad = ((signX < 0) ? 1 : 0) + ((signY < 0) ? 2 : 0);
			if (quad >= 0 && quad < 4) {
				return quad;
			}
			return -1;
		}

		protected static <T> Quad<T> superQuad(Quad<T> oldQuad) {
			Quad<T> superQuad = new Quad<T>(oldQuad.size * 2);
			for (int i = 0; i < 4; i++) {
				superQuad.quads.set(i, new Quad<T>(oldQuad.size));
			}
			((Quad<T>) superQuad.quads.get(getIndex(-1, -1))).quads.set(getIndex(+1, +1),
					oldQuad.quads.get(getIndex(-1, -1)));
			((Quad<T>) superQuad.quads.get(getIndex(-1, +1))).quads.set(getIndex(+1, -1),
					oldQuad.quads.get(getIndex(-1, +1)));
			((Quad<T>) superQuad.quads.get(getIndex(+1, -1))).quads.set(getIndex(-1, +1),
					oldQuad.quads.get(getIndex(+1, -1)));
			((Quad<T>) superQuad.quads.get(getIndex(+1, +1))).quads.set(getIndex(-1, -1),
					oldQuad.quads.get(getIndex(+1, +1)));
			return superQuad;
		}
	}

	private Quad<T> root;
	private int size;

	public QuadTree() {
		this(CollectionUtils.INITIAL_CAPACITY);
	}

	public QuadTree(int gridSize) {
		this.size = gridSize;
		root = new Quad<T>(size);
	}

	public QuadTree<T> set(int x, int y, T value) {
		while (x >= size || y >= size || x < -size || y < -size) {
			splitQuad();
		}
		root.set(x, y, value);
		return this;
	}

	public T get(int x, int y) {
		if (x >= size || y >= size || x < -size || y < -size) {
			return null;
		}
		return root.get(x, y);
	}

	private void splitQuad() {
		size = size * 2;
		Quad<T> newRoot = Quad.superQuad(root);
		root = newRoot;
	}
}
