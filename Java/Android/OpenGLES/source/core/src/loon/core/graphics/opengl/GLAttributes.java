/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.core.graphics.opengl;

import loon.core.graphics.opengl.GLAttributes;

public class GLAttributes {

	public static class VertexAttribute {

		public final int usage;

		public final int numComponents;

		public int offset;

		public String alias;

		public VertexAttribute(int usage, int numComponents, String alias) {
			this.usage = usage;
			this.numComponents = numComponents;
			this.alias = alias;
		}

		@Override
		public boolean equals(final Object obj) {
			if (!(obj instanceof VertexAttribute)) {
				return false;
			}
			final VertexAttribute other = (VertexAttribute) obj;
			return this.usage == other.usage
					&& this.numComponents == other.numComponents
					&& this.alias.equals(other.alias);
		}
	}

	public static final class Usage {
		public static final int Position = 0;
		public static final int Color = 1;
		public static final int ColorPacked = 5;
		public static final int Normal = 2;
		public static final int TextureCoordinates = 3;
		public static final int Generic = 4;
	}

	final VertexAttribute[] attributes;

	public final int vertexSize;

	public GLAttributes(VertexAttribute... attributes) {
		if (attributes.length == 0)
			throw new IllegalArgumentException("attributes must be >= 1");

		VertexAttribute[] list = new VertexAttribute[attributes.length];
		for (int i = 0; i < attributes.length; i++)
			list[i] = attributes[i];

		this.attributes = list;

		checkValidity();
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
		for (int i = 0; i < len; i++) {
			if (get(i).usage == usage)
				return get(i);
		}
		return null;
	}

	private int calculateOffsets() {
		int count = 0;
		for (int i = 0; i < attributes.length; i++) {
			VertexAttribute attribute = attributes[i];
			attribute.offset = count;
			if (attribute.usage == Usage.ColorPacked)
				count += 4;
			else
				count += 4 * attribute.numComponents;
		}

		return count;
	}

	private void checkValidity() {
		boolean pos = false;
		boolean cols = false;
		boolean nors = false;

		for (int i = 0; i < attributes.length; i++) {
			VertexAttribute attribute = attributes[i];
			if (attribute.usage == Usage.Position) {
				if (pos)
					throw new IllegalArgumentException(
							"two position attributes were specified");
				pos = true;
			}

			if (attribute.usage == Usage.Normal) {
				if (nors)
					throw new IllegalArgumentException(
							"two normal attributes were specified");
			}

			if (attribute.usage == Usage.Color
					|| attribute.usage == Usage.ColorPacked) {
				if (attribute.numComponents != 4)
					throw new IllegalArgumentException(
							"color attribute must have 4 components");

				if (cols)
					throw new IllegalArgumentException(
							"two color attributes were specified");
				cols = true;
			}
		}

		if (pos == false)
			throw new IllegalArgumentException(
					"no position attribute was specified");
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

	public boolean equals(final Object obj) {
		if (!(obj instanceof GLAttributes)) {
			return false;
		}
		GLAttributes other = (GLAttributes) obj;
		if (this.attributes.length != other.size()) {
			return false;
		}
		for (int i = 0; i < attributes.length; i++) {
			if (!attributes[i].equals(other.attributes[i])) {
				return false;
			}
		}
		return true;
	}

}
