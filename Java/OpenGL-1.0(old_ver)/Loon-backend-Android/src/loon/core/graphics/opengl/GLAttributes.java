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

	}

	@Override
	public int hashCode(){
		return super.hashCode();
	}
	
	public static final class Usage {
		public static final int Position = 0;
		public static final int Color = 1;
		public static final int ColorPacked = 5;
		public static final int Normal = 2;
		public static final int TextureCoordinates = 3;
		public static final int Generic = 4;
	}

    final VertexAttribute[] _attributes;

	public final int vertexSize;

	public GLAttributes(VertexAttribute... attributes) {
		if (attributes.length == 0) {
			throw new IllegalArgumentException("attributes must be >= 1");
		}
		int size = attributes.length;
		_attributes = new VertexAttribute[size];
		System.arraycopy(attributes, 0, _attributes, 0, size);
		checkValidity();
		vertexSize = calculateOffsets();
	}

	private int calculateOffsets() {
		int count = 0;
		for (int i = 0; i < _attributes.length; i++) {
			VertexAttribute attribute = _attributes[i];
			attribute.offset = count;
			if (attribute.usage == GLAttributes.Usage.ColorPacked) {
				count += 4;
			} else {
				count += 4 * attribute.numComponents;
			}
		}
		return count;
	}

	private void checkValidity() {
		boolean pos = false;
		boolean cols = false;
		boolean nors = false;
		for (int i = 0; i < _attributes.length; i++) {
			VertexAttribute attribute = _attributes[i];
			if (attribute.usage == Usage.Position) {
				if (pos) {
					throw new IllegalArgumentException(
							"two position attributes were specified !");
				}
				pos = true;
			}
			if (attribute.usage == Usage.Normal) {
				if (nors) {
					throw new IllegalArgumentException(
							"two normal attributes were specified !");
				}
			}
			if (attribute.usage == Usage.Color
					|| attribute.usage == Usage.ColorPacked) {
				if (attribute.numComponents != 4) {
					throw new IllegalArgumentException(
							"color attribute must have 4 components !");
				}
				if (cols) {
					throw new IllegalArgumentException(
							"two color attributes were specified !");
				}
				cols = true;
			}
		}
		if (!pos){
			throw new IllegalArgumentException(
					"no position attribute was specified !");
		}
	}

	public int size() {
		return _attributes.length;
	}

	public VertexAttribute get(int index) {
		return _attributes[index];
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof GLAttributes)) {
			return false;
		}
		GLAttributes other = (GLAttributes) obj;
		if (this._attributes.length != other.size()) {
			return false;
		}
		for (int i = 0; i < _attributes.length; i++) {
			if (!_attributes[i].equals(other._attributes[i])) {
				return false;
			}
		}
		return true;
	}

}
