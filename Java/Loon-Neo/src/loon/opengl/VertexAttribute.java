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

import loon.LSystem;
import loon.opengl.VertexAttributes.Usage;

public final class VertexAttribute {

	public final boolean normalized;

	public final int usage;

	public final int numComponents;

	public final int vertexType;

	public int offset;

	public int unit;

	public String alias;

	private final int usageIndex;

	public VertexAttribute(int usage, int numComponents, String alias) {
		this(usage, numComponents, alias, 0);
	}

	public VertexAttribute(int usage, int numComponents, String alias, int unit) {
		this(usage, numComponents, usage == Usage.ColorPacked ? GL20.GL_UNSIGNED_BYTE : GL20.GL_FLOAT,
				usage == Usage.ColorPacked, alias, unit);
	}

	public VertexAttribute(int usage, int numComponents, int vertexType, boolean normalized, String alias) {
		this(usage, numComponents, vertexType, normalized, alias, 0);
	}

	public VertexAttribute(int usage, int numComponents, int vertexType, boolean normalized, String alias, int unit) {
		this.usage = usage;
		this.numComponents = numComponents;
		this.vertexType = vertexType;
		this.normalized = normalized;
		this.alias = alias;
		this.unit = unit;
		this.usageIndex = Integer.numberOfTrailingZeros(usage);
	}

	public VertexAttribute cpy() {
		return new VertexAttribute(usage, numComponents, vertexType, normalized, alias, unit);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof VertexAttribute)) {
			return false;
		}
		return equals((VertexAttribute) obj);
	}

	public boolean equals(final VertexAttribute other) {
		return other != null && usage == other.usage && numComponents == other.numComponents
				&& vertexType == other.vertexType && normalized == other.normalized && alias.equals(other.alias)
				&& unit == other.unit;
	}

	public static VertexAttribute Position() {
		return new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE);
	}

	public static VertexAttribute TexCoords(int unit) {
		return new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + unit, unit);
	}

	public static VertexAttribute Normal() {
		return new VertexAttribute(Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE);
	}

	public static VertexAttribute ColorPacked() {
		return new VertexAttribute(Usage.ColorPacked, 4, GL20.GL_UNSIGNED_BYTE, true, ShaderProgram.COLOR_ATTRIBUTE);
	}

	public static VertexAttribute ColorUnpacked() {
		return new VertexAttribute(Usage.ColorUnpacked, 4, GL20.GL_FLOAT, false, ShaderProgram.COLOR_ATTRIBUTE);
	}

	public static VertexAttribute Tangent() {
		return new VertexAttribute(Usage.Tangent, 3, ShaderProgram.TANGENT_ATTRIBUTE);
	}

	public static VertexAttribute Binormal() {
		return new VertexAttribute(Usage.BiNormal, 3, ShaderProgram.BINORMAL_ATTRIBUTE);
	}

	public static VertexAttribute BoneWeight(int unit) {
		return new VertexAttribute(Usage.BoneWeight, 2, ShaderProgram.BONEWEIGHT_ATTRIBUTE + unit, unit);
	}

	public int getKey() {
		return (usageIndex << 8) + (unit & 0xFF);
	}

	public int getSizeInBytes() {
		switch (vertexType) {
		case GL20.GL_FLOAT:
		case GL20.GL_FIXED:
			return 4 * numComponents;
		case GL20.GL_UNSIGNED_BYTE:
		case GL20.GL_BYTE:
			return numComponents;
		case GL20.GL_UNSIGNED_SHORT:
		case GL20.GL_SHORT:
			return 2 * numComponents;
		}
		return 0;
	}

	@Override
	public int hashCode() {
		int result = getKey();
		result = LSystem.unite(result, normalized);
		result = LSystem.unite(result, alias.hashCode());
		return result;
	}
}
