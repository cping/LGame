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
package loon.opengl;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class VertexAttributes implements Iterable<VertexAttribute> {

	public static final class Usage {
		public static final int Position = 1;
		public static final int Color = 2;
		public static final int ColorUnpacked = 2;
		public static final int ColorPacked = 4;
		public static final int Normal = 8;
		public static final int TextureCoordinates = 16;
		public static final int Generic = 32;
		public static final int BoneWeight = 64;
		public static final int Tangent = 128;
		public static final int BiNormal = 256;
	}

	private final VertexAttribute[] attributes;

	public final int vertexSize;

	private long mask = -1;

	private ReadonlyIterable<VertexAttribute> iterable;

	public VertexAttributes(VertexAttribute... attributes) {
		if (attributes.length == 0)
			throw new IllegalArgumentException("attributes must be >= 1");

		VertexAttribute[] list = new VertexAttribute[attributes.length];
		for (int i = 0; i < attributes.length; i++)
			list[i] = attributes[i];

		this.attributes = list;
		vertexSize = calculateOffsets();
	}

	public int getOffset(int usage) {
		VertexAttribute vertexAttribute = findByUsage(usage);
		if (vertexAttribute == null)
			return 0;
		return vertexAttribute.offset / 4;
	}

	public VertexAttribute findByUsage(int usage) {
		int len = size();
		for (int i = 0; i < len; i++)
			if (get(i).usage == usage)
				return get(i);
		return null;
	}

	private int calculateOffsets() {
		int count = 0;
		for (int i = 0; i < attributes.length; i++) {
			VertexAttribute attribute = attributes[i];
			attribute.offset = count;
			if (attribute.usage == VertexAttributes.Usage.ColorPacked)
				count += 4;
			else
				count += 4 * attribute.numComponents;
		}

		return count;
	}

	public int size() {
		return attributes.length;
	}

	public VertexAttribute get(int index) {
		return attributes[index];
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (int i = 0; i < attributes.length; i++) {
			builder.append("(");
			builder.append(attributes[i].alias);
			builder.append(", ");
			builder.append(attributes[i].usage);
			builder.append(", ");
			builder.append(attributes[i].numComponents);
			builder.append(", ");
			builder.append(attributes[i].offset);
			builder.append(")");
			builder.append("\n");
		}
		builder.append("]");
		return builder.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof VertexAttributes))
			return false;
		VertexAttributes other = (VertexAttributes) obj;
		if (this.attributes.length != other.size())
			return false;
		for (int i = 0; i < attributes.length; i++) {
			if (!attributes[i].equals(other.attributes[i]))
				return false;
		}
		return true;
	}

	public long getMask() {
		if (mask == -1) {
			long result = 0;
			for (int i = 0; i < attributes.length; i++) {
				result |= attributes[i].usage;
			}
			mask = result;
		}
		return mask;
	}

	@Override
	public Iterator<VertexAttribute> iterator() {
		if (iterable == null)
			iterable = new ReadonlyIterable<VertexAttribute>(attributes);
		return iterable.iterator();
	}

	static private class ReadonlyIterator<T> implements Iterator<T>,
			Iterable<T> {
		private final T[] array;
		int index;
		boolean valid = true;

		public ReadonlyIterator(T[] array) {
			this.array = array;
		}

		@Override
		public boolean hasNext() {
			if (!valid)
				throw new RuntimeException("#iterator() cannot be used nested.");
			return index < array.length;
		}

		@Override
		public T next() {
			if (index >= array.length)
				throw new NoSuchElementException(String.valueOf(index));
			if (!valid)
				throw new RuntimeException("#iterator() cannot be used nested.");
			return array[index++];
		}

		@Override
		public void remove() {
			throw new RuntimeException("Remove not allowed.");
		}

		@Override
		public Iterator<T> iterator() {
			return this;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static private class ReadonlyIterable<T> implements Iterable<T> {

		private final T[] array;

		private ReadonlyIterator iterator1, iterator2;

		public ReadonlyIterable(T[] array) {
			this.array = array;
		}

		@Override
		public Iterator<T> iterator() {
			if (iterator1 == null) {
				iterator1 = new ReadonlyIterator(array);
				iterator2 = new ReadonlyIterator(array);
			}
			if (!iterator1.valid) {
				iterator1.index = 0;
				iterator1.valid = true;
				iterator2.valid = false;
				return iterator1;
			}
			iterator2.index = 0;
			iterator2.valid = true;
			iterator1.valid = false;
			return iterator2;
		}
	}
}
