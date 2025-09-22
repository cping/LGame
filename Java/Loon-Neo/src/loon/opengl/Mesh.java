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

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import loon.LGame;
import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.geom.Affine2f;
import loon.geom.Matrix3;
import loon.geom.Matrix4;
import loon.geom.Vector3f;
import loon.opengl.VertexAttributes.Usage;
import loon.utils.TArray;

public final class Mesh implements LRelease {

	private static void addManagedMesh(Mesh mesh) {
		LSystem.addMesh(mesh);
	}

	public static void invalidate(LGame game) {
		if (game.graphics().gl == null) {
			return;
		}
		TArray<Mesh> meshesArray = game.getMeshAll();
		if (meshesArray == null) {
			return;
		}
		for (int i = 0; i < meshesArray.size; i++) {
			meshesArray.get(i).vertices.invalidate();
			meshesArray.get(i).indices.invalidate();
		}
	}

	public static void clearAllMeshes() {
		LSystem.clearMesh();
	}

	public static enum VertexDataType {
		VertexArray, VertexBufferObject, VertexBufferObjectSubData,
	}

	private final VertexData vertices;
	private final IndexData indices;
	private final boolean isVertexArray;

	private boolean autoBind = true;

	private boolean closed = false;

	protected Mesh(VertexData vertices, IndexData indices, boolean isVertexArray) {
		this.vertices = vertices;
		this.indices = indices;
		this.isVertexArray = isVertexArray;

		addManagedMesh(this);
	}

	public Mesh(boolean isStatic, int maxVertices, int maxIndices, VertexAttribute... attributes) {
		vertices = new VertexBufferObject(isStatic, maxVertices, attributes);
		indices = new IndexBufferObject(isStatic, maxIndices);
		isVertexArray = false;

		addManagedMesh(this);
	}

	public Mesh(boolean isStatic, int maxVertices, int maxIndices, VertexAttributes attributes) {
		vertices = new VertexBufferObject(isStatic, maxVertices, attributes);
		indices = new IndexBufferObject(isStatic, maxIndices);
		isVertexArray = false;

		addManagedMesh(this);
	}

	public Mesh(VertexDataType type, boolean isStatic, int maxVertices, int maxIndices, VertexAttribute... attributes) {
		if (type == VertexDataType.VertexBufferObject) {
			vertices = new VertexBufferObject(isStatic, maxVertices, attributes);
			indices = new IndexBufferObject(isStatic, maxIndices);
			isVertexArray = false;
		} else if (type == VertexDataType.VertexBufferObjectSubData) {
			vertices = new VertexBufferObjectSubData(isStatic, maxVertices, attributes);
			indices = new IndexBufferObjectSubData(isStatic, maxIndices);
			isVertexArray = false;
		} else {
			vertices = new VertexArray(maxVertices, attributes);
			indices = new IndexArray(maxIndices);
			isVertexArray = true;
		}
		addManagedMesh(this);
	}

	public Mesh setVertices(float[] vertices) {
		this.vertices.setVertices(vertices, 0, vertices.length);
		return this;
	}

	public Mesh setVertices(float[] vertices, int offset, int count) {
		this.vertices.setVertices(vertices, offset, count);
		return this;
	}

	public Mesh updateVertices(int targetOffset, float[] source) {
		return updateVertices(targetOffset, source, 0, source.length);
	}

	public Mesh updateVertices(int targetOffset, float[] source, int sourceOffset, int count) {
		this.vertices.updateVertices(targetOffset, source, sourceOffset, count);
		return this;
	}

	public float[] getVertices(float[] vertices) {
		return getVertices(0, -1, vertices);
	}

	public float[] getVertices(int srcOffset, float[] vertices) {
		return getVertices(srcOffset, -1, vertices);
	}

	public float[] getVertices(int srcOffset, int count, float[] vertices) {
		return getVertices(srcOffset, count, vertices, 0);
	}

	public float[] getVertices(int srcOffset, int count, float[] vertices, int destOffset) {
		final int max = getNumVertices() * getVertexSize() / 4;
		if (count == -1) {
			count = max - srcOffset;
			if (count > vertices.length - destOffset)
				count = vertices.length - destOffset;
		}
		if (srcOffset < 0 || count <= 0 || (srcOffset + count) > max || destOffset < 0
				|| destOffset >= vertices.length) {
			throw new LSysException("your offset size >= vertices length !");
		}
		if ((vertices.length - destOffset) < count) {
			throw new LSysException(
					"not enough room in vertices array, has " + vertices.length + " floats, needs " + count);
		}
		FloatBuffer verticesBuffer = getVerticesBuffer(false);
		int pos = verticesBuffer.position();
		((Buffer) verticesBuffer).position(srcOffset);
		verticesBuffer.get(vertices, destOffset, count);
		((Buffer) verticesBuffer).position(pos);
		return vertices;
	}

	public Mesh setIndices(short[] indices) {
		this.indices.setIndices(indices, 0, indices.length);
		return this;
	}

	public Mesh setIndices(short[] indices, int offset, int count) {
		this.indices.setIndices(indices, offset, count);
		return this;
	}

	public void getIndices(short[] indices) {
		getIndices(indices, 0);
	}

	public void getIndices(short[] indices, int destOffset) {
		getIndices(0, indices, destOffset);
	}

	public void getIndices(int srcOffset, short[] indices, int destOffset) {
		getIndices(srcOffset, -1, indices, destOffset);
	}

	public void getIndices(int srcOffset, int count, short[] indices, int destOffset) {
		int max = getNumIndices();
		if (count < 0)
			count = max - srcOffset;
		if (srcOffset < 0 || srcOffset >= max || srcOffset + count > max)
			throw new LSysException(
					"Invalid range specified, offset: " + srcOffset + ", count: " + count + ", max: " + max);
		if ((indices.length - destOffset) < count)
			throw new LSysException(
					"not enough room in indices array, has " + indices.length + " shorts, needs " + count);
		ShortBuffer indicesBuffer = getIndicesBuffer(false);
		int pos = indicesBuffer.position();
		((Buffer) indicesBuffer).position(srcOffset);
		indicesBuffer.get(indices, destOffset, count);
		((Buffer) indicesBuffer).position(pos);
	}

	public int getNumIndices() {
		return indices.getNumIndices();
	}

	public int getNumVertices() {
		return vertices.getNumVertices();
	}

	public int getMaxVertices() {
		return vertices.getNumMaxVertices();
	}

	public int getMaxIndices() {
		return indices.getNumMaxIndices();
	}

	public int getVertexSize() {
		return vertices.getAttributes().vertexSize;
	}

	public void setAutoBind(boolean autoBind) {
		this.autoBind = autoBind;
	}

	public void bind(final ShaderProgram shader) {
		bind(shader, null);
	}

	public void bind(final ShaderProgram shader, final int[] locations) {
		vertices.bind(shader, locations);
		if (indices.getNumIndices() > 0)
			indices.bind();
	}

	public void unbind(final ShaderProgram shader) {
		unbind(shader, null);
	}

	public void unbind(final ShaderProgram shader, final int[] locations) {
		vertices.unbind(shader, locations);
		if (indices.getNumIndices() > 0)
			indices.unbind();
	}

	public void render(ShaderProgram shader, int primitiveType) {
		render(shader, primitiveType, 0, indices.getNumMaxIndices() > 0 ? getNumIndices() : getNumVertices(), autoBind);
	}

	public void render(ShaderProgram shader, int primitiveType, int offset, int count) {
		render(shader, primitiveType, offset, count, autoBind);
	}

	public void render(ShaderProgram shader, int primitiveType, int offset, int count, boolean autoBind) {
		if (count == 0) {
			return;
		}
		if (autoBind) {
			bind(shader);
		}
		final GL20 gl = LSystem.base().graphics().gl;
		if (isVertexArray) {
			if (indices.getNumIndices() > 0) {
				final ShortBuffer buffer = indices.getBuffer(false);
				final int oldPosition = buffer.position();
				((Buffer) buffer).position(offset);
				gl.glDrawElements(primitiveType, count, GL20.GL_UNSIGNED_SHORT, buffer);
				((Buffer) buffer).position(oldPosition);
			} else {
				gl.glDrawArrays(primitiveType, offset, count);
			}
		} else {
			if (indices.getNumIndices() > 0) {
				gl.glDrawElements(primitiveType, count, GL20.GL_UNSIGNED_SHORT, offset * 2);
			} else {
				gl.glDrawArrays(primitiveType, offset, count);
			}
		}
		if (autoBind) {
			unbind(shader);
		}
	}

	public boolean isClosed() {
		return closed;
	}

	public VertexAttribute getVertexAttribute(int usage) {
		VertexAttributes attributes = vertices.getAttributes();
		int len = attributes.size();
		for (int i = 0; i < len; i++) {
			if (attributes.get(i).usage == usage) {
				return attributes.get(i);
			}
		}
		return null;
	}

	public VertexAttributes getVertexAttributes() {
		return vertices.getAttributes();
	}

	public FloatBuffer getVerticesBuffer(boolean dirty) {
		return vertices.getBuffer(dirty);
	}

	public ShortBuffer getIndicesBuffer(boolean dirty) {
		return indices.getBuffer(dirty);
	}

	public Mesh copy(boolean isStatic, boolean removeDuplicates, final int[] usage) {
		final int vertexSize = getVertexSize() / 4;
		int numVertices = getNumVertices();
		float[] vertices = new float[numVertices * vertexSize];
		getVertices(0, vertices.length, vertices);
		short[] checks = null;
		VertexAttribute[] attrs = null;
		int newVertexSize = 0;
		if (usage != null) {
			int size = 0;
			int as = 0;
			for (int i = 0; i < usage.length; i++)
				if (getVertexAttribute(usage[i]) != null) {
					size += getVertexAttribute(usage[i]).numComponents;
					as++;
				}
			if (size > 0) {
				attrs = new VertexAttribute[as];
				checks = new short[size];
				int idx = -1;
				int ai = -1;
				for (int i = 0; i < usage.length; i++) {
					VertexAttribute a = getVertexAttribute(usage[i]);
					if (a == null) {
						continue;
					}
					for (int j = 0; j < a.numComponents; j++) {
						checks[++idx] = (short) (a.offset + j);
					}
					attrs[++ai] = a.cpy();
					newVertexSize += a.numComponents;
				}
			}
		}
		if (checks == null) {
			checks = new short[vertexSize];
			for (short i = 0; i < vertexSize; i++) {
				checks[i] = i;
			}
			newVertexSize = vertexSize;
		}
		int numIndices = getNumIndices();
		short[] indices = null;
		if (numIndices > 0) {
			indices = new short[numIndices];
			getIndices(indices);
			if (removeDuplicates || newVertexSize != vertexSize) {
				float[] tmp = new float[vertices.length];
				int size = 0;
				for (int i = 0; i < numIndices; i++) {
					final int idx1 = indices[i] * vertexSize;
					short newIndex = -1;
					if (removeDuplicates) {
						for (short j = 0; j < size && newIndex < 0; j++) {
							final int idx2 = j * newVertexSize;
							boolean found = true;
							for (int k = 0; k < checks.length && found; k++) {
								if (tmp[idx2 + k] != vertices[idx1 + checks[k]]) {
									found = false;
								}
							}
							if (found) {
								newIndex = j;
							}
						}
					}
					if (newIndex > 0) {
						indices[i] = newIndex;
					} else {
						final int idx = size * newVertexSize;
						for (int j = 0; j < checks.length; j++) {
							tmp[idx + j] = vertices[idx1 + checks[j]];
						}
						indices[i] = (short) size;
						size++;
					}
				}
				vertices = tmp;
				numVertices = size;
			}
		}

		Mesh result;
		if (attrs == null) {
			result = new Mesh(isStatic, numVertices, indices == null ? 0 : indices.length, getVertexAttributes());
		} else {
			result = new Mesh(isStatic, numVertices, indices == null ? 0 : indices.length, attrs);
		}
		result.setVertices(vertices, 0, numVertices * newVertexSize);
		if (indices != null) {
			result.setIndices(indices);
		}
		return result;
	}

	public Mesh copy(boolean isStatic) {
		return copy(isStatic, false, null);
	}

	public Mesh cpy() {
		return copy(false);
	}

	public void scale(float scaleX, float scaleY, float scaleZ) {
		final VertexAttribute posAttr = getVertexAttribute(Usage.Position);
		final int offset = posAttr.offset / 4;
		final int numComponents = posAttr.numComponents;
		final int numVertices = getNumVertices();
		final int vertexSize = getVertexSize() / 4;

		final float[] vertices = new float[numVertices * vertexSize];
		getVertices(vertices);

		int idx = offset;
		switch (numComponents) {
		case 1:
			for (int i = 0; i < numVertices; i++) {
				vertices[idx] *= scaleX;
				idx += vertexSize;
			}
			break;
		case 2:
			for (int i = 0; i < numVertices; i++) {
				vertices[idx] *= scaleX;
				vertices[idx + 1] *= scaleY;
				idx += vertexSize;
			}
			break;
		case 3:
			for (int i = 0; i < numVertices; i++) {
				vertices[idx] *= scaleX;
				vertices[idx + 1] *= scaleY;
				vertices[idx + 2] *= scaleZ;
				idx += vertexSize;
			}
			break;
		}

		setVertices(vertices);
	}

	public void transform(final Matrix4 matrix) {
		transform(matrix, 0, getNumVertices());
	}

	public void transform(final Matrix4 matrix, final int start, final int count) {
		final VertexAttribute posAttr = getVertexAttribute(Usage.Position);
		final int posOffset = posAttr.offset / 4;
		final int stride = getVertexSize() / 4;
		final int numComponents = posAttr.numComponents;

		final float[] vertices = new float[count * stride];
		getVertices(start * stride, count * stride, vertices);

		transform(matrix, vertices, stride, posOffset, numComponents, 0, count);

		updateVertices(start * stride, vertices);
	}

	public static void transform(final Matrix4 matrix, final float[] vertices, int vertexSize, int offset,
			int dimensions, int start, int count) {
		if (offset < 0 || dimensions < 1 || (offset + dimensions) > vertexSize)
			throw new LSysException("offset > vertexSize !");
		if (start < 0 || count < 1 || ((start + count) * vertexSize) > vertices.length)
			throw new LSysException("start = " + start + ", count = " + count + ", vertexSize = " + vertexSize
					+ ", length = " + vertices.length);

		final Vector3f result = new Vector3f();

		int idx = offset + (start * vertexSize);
		switch (dimensions) {
		case 1:
			for (int i = 0; i < count; i++) {
				result.set(vertices[idx], 0, 0).mulSelf(matrix);
				vertices[idx] = result.x;
				idx += vertexSize;
			}
			break;
		case 2:
			for (int i = 0; i < count; i++) {
				result.set(vertices[idx], vertices[idx + 1], 0).mulSelf(matrix);
				vertices[idx] = result.x;
				vertices[idx + 1] = result.y;
				idx += vertexSize;
			}
			break;
		case 3:
			for (int i = 0; i < count; i++) {
				result.set(vertices[idx], vertices[idx + 1], vertices[idx + 2]).mulSelf(matrix);
				vertices[idx] = result.x;
				vertices[idx + 1] = result.y;
				vertices[idx + 2] = result.z;
				idx += vertexSize;
			}
			break;
		}
	}

	public void transformUV(final Affine2f matrix) {
		transformUV(matrix, 0, getNumVertices());
	}

	protected void transformUV(final Affine2f matrix, final int start, final int count) {
		final VertexAttribute posAttr = getVertexAttribute(Usage.TextureCoordinates);
		final int offset = posAttr.offset / 4;
		final int vertexSize = getVertexSize() / 4;
		final int numVertices = getNumVertices();
		final int size = numVertices * vertexSize;
		final float[] vertices = new float[size];
		getVertices(0, size, vertices);
		transformUV(matrix, vertices, vertexSize, offset, start, count);
		setVertices(vertices, 0, vertices.length);
	}

	public static void transformUV(final Affine2f matrix, final float[] vertices, int vertexSize, int offset, int start,
			int count) {
		if (start < 0 || count < 1 || ((start + count) * vertexSize) > vertices.length) {
			throw new LSysException("start = " + start + ", count = " + count + ", vertexSize = " + vertexSize
					+ ", length = " + vertices.length);
		}
		float newX = 0f;
		float newY = 0f;
		int idx = offset + (start * vertexSize);
		for (int i = 0; i < count; i++) {
			newX = vertices[idx];
			newY = vertices[idx + 1];
			float matX = newX * matrix.m00 + newY * matrix.m01 + matrix.tx;
			float matY = newX * matrix.m10 + newY * matrix.m11 + matrix.ty;
			vertices[idx] = matX;
			vertices[idx + 1] = matY;
			idx += vertexSize;
		}
	}

	public void transformUV(final Matrix3 matrix) {
		transformUV(matrix, 0, getNumVertices());
	}

	protected void transformUV(final Matrix3 matrix, final int start, final int count) {
		final VertexAttribute posAttr = getVertexAttribute(Usage.TextureCoordinates);
		final int offset = posAttr.offset / 4;
		final int vertexSize = getVertexSize() / 4;
		final int numVertices = getNumVertices();
		final int size = numVertices * vertexSize;
		final float[] vertices = new float[size];
		getVertices(0, size, vertices);
		transformUV(matrix, vertices, vertexSize, offset, start, count);
		setVertices(vertices, 0, vertices.length);
	}

	public static void transformUV(final Matrix3 matrix, final float[] vertices, int vertexSize, int offset, int start,
			int count) {
		if (start < 0 || count < 1 || ((start + count) * vertexSize) > vertices.length) {
			throw new LSysException("start = " + start + ", count = " + count + ", vertexSize = " + vertexSize
					+ ", length = " + vertices.length);
		}
		float newX = 0f;
		float newY = 0f;
		int idx = offset + (start * vertexSize);
		final float[] mat = matrix.val;
		for (int i = 0; i < count; i++) {
			newX = vertices[idx];
			newY = vertices[idx + 1];
			float matX = newX * mat[0] + newY * mat[3] + mat[6];
			float matY = newX * mat[1] + newY * mat[4] + mat[7];
			vertices[idx] = matX;
			vertices[idx + 1] = matY;
			idx += vertexSize;
		}
	}

	@Override
	public void close() {
		if (closed) {
			return;
		}
		synchronized (Mesh.class) {
			LSystem.removeMesh(this);
			this.vertices.close();
			this.indices.close();
			this.closed = true;

		}
	}
}
