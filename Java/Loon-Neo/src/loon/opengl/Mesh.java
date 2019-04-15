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

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import loon.LRelease;
import loon.LSystem;
import loon.LGame;
import loon.geom.BoundingBox;
import loon.geom.Matrix3;
import loon.geom.Matrix4;
import loon.geom.Vector2f;
import loon.geom.Vector3f;
import loon.opengl.VertexAttributes.Usage;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;

public class Mesh implements LRelease {

	public enum VertexDataType {
		VertexArray, VertexBufferObject, VertexBufferObjectSubData,
	}

	static final ObjectMap<LGame, TArray<Mesh>> gmeshes = new ObjectMap<LGame, TArray<Mesh>>();

	private final VertexData vertices;
	private final IndexData indices;
	private final boolean isVertexArray;

	private boolean autoBind = true;

	protected Mesh(VertexData vertices, IndexData indices, boolean isVertexArray) {
		this.vertices = vertices;
		this.indices = indices;
		this.isVertexArray = isVertexArray;

		addManagedMesh(LSystem.base(), this);
	}

	public Mesh(boolean isStatic, int maxVertices, int maxIndices, VertexAttribute... attributes) {
		vertices = new VertexBufferObject(isStatic, maxVertices, attributes);
		indices = new IndexBufferObject(isStatic, maxIndices);
		isVertexArray = false;

		addManagedMesh(LSystem.base(), this);
	}

	public Mesh(boolean isStatic, int maxVertices, int maxIndices, VertexAttributes attributes) {
		vertices = new VertexBufferObject(isStatic, maxVertices, attributes);
		indices = new IndexBufferObject(isStatic, maxIndices);
		isVertexArray = false;

		addManagedMesh(LSystem.base(), this);
	}

	public Mesh(boolean staticVertices, boolean staticIndices, int maxVertices, int maxIndices,
			VertexAttributes attributes) {
		vertices = new VertexBufferObject(staticVertices, maxVertices, attributes);
		indices = new IndexBufferObject(staticIndices, maxIndices);
		isVertexArray = false;

		addManagedMesh(LSystem.base(), this);
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
		addManagedMesh(LSystem.base(), this);
	}

	public static Mesh create(boolean isStatic, final Mesh base, final Matrix4[] transformations) {
		final VertexAttribute posAttr = base.getVertexAttribute(Usage.Position);
		final int offset = posAttr.offset / 4;
		final int numComponents = posAttr.numComponents;
		final int numVertices = base.getNumVertices();
		final int vertexSize = base.getVertexSize() / 4;
		final int baseSize = numVertices * vertexSize;
		final int numIndices = base.getNumIndices();

		final float vertices[] = new float[numVertices * vertexSize * transformations.length];
		final short indices[] = new short[numIndices * transformations.length];

		base.getIndices(indices);

		for (int i = 0; i < transformations.length; i++) {
			base.getVertices(0, baseSize, vertices, baseSize * i);
			transform(transformations[i], vertices, vertexSize, offset, numComponents, numVertices * i, numVertices);
			if (i > 0)
				for (int j = 0; j < numIndices; j++)
					indices[(numIndices * i) + j] = (short) (indices[j] + (numVertices * i));
		}

		final Mesh result = new Mesh(isStatic, vertices.length / vertexSize, indices.length,
				base.getVertexAttributes());
		result.setVertices(vertices);
		result.setIndices(indices);
		return result;
	}

	public static Mesh create(boolean isStatic, final Mesh[] meshes) {
		return create(isStatic, meshes, null);
	}

	public static Mesh create(boolean isStatic, final Mesh[] meshes, final Matrix4[] transformations) {
		if (transformations != null && transformations.length < meshes.length)
			throw LSystem.runThrow("Not enough transformations specified");
		final VertexAttributes attributes = meshes[0].getVertexAttributes();
		int vertCount = meshes[0].getNumVertices();
		int idxCount = meshes[0].getNumIndices();
		for (int i = 1; i < meshes.length; i++) {
			if (!meshes[i].getVertexAttributes().equals(attributes))
				throw LSystem.runThrow("Inconsistent VertexAttributes");
			vertCount += meshes[i].getNumVertices();
			idxCount += meshes[i].getNumIndices();
		}
		final VertexAttribute posAttr = meshes[0].getVertexAttribute(Usage.Position);
		final int offset = posAttr.offset / 4;
		final int numComponents = posAttr.numComponents;
		final int vertexSize = attributes.vertexSize / 4;

		final float vertices[] = new float[vertCount * vertexSize];
		final short indices[] = new short[idxCount];

		meshes[0].getVertices(vertices);
		meshes[0].getIndices(indices);
		int vcount = meshes[0].getNumVertices();
		if (transformations != null)
			transform(transformations[0], vertices, vertexSize, offset, numComponents, 0, vcount);
		int voffset = vcount;
		int ioffset = meshes[0].getNumIndices();
		for (int i = 1; i < meshes.length; i++) {
			final Mesh mesh = meshes[i];
			vcount = mesh.getNumVertices();
			final int isize = mesh.getNumIndices();
			mesh.getVertices(0, vcount * vertexSize, vertices, voffset * vertexSize);
			if (transformations != null)
				transform(transformations[i], vertices, vertexSize, offset, numComponents, voffset, vcount);
			mesh.getIndices(indices, ioffset);
			for (int j = 0; j < isize; j++)
				indices[ioffset + j] = (short) (indices[ioffset + j] + voffset);
			ioffset += isize;
			voffset += vcount;
		}

		final Mesh result = new Mesh(isStatic, vertices.length / vertexSize, indices.length, attributes);
		result.setVertices(vertices);
		result.setIndices(indices);
		return result;
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
			throw LSystem.runThrow("your offset size >= vertices length !");
		}
		if ((vertices.length - destOffset) < count) {
			throw LSystem.runThrow(
					"not enough room in vertices array, has " + vertices.length + " floats, needs " + count);
		}
		int pos = getVerticesBuffer().position();
		getVerticesBuffer().position(srcOffset);
		getVerticesBuffer().get(vertices, destOffset, count);
		getVerticesBuffer().position(pos);
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
			throw LSystem.runThrow(
					"Invalid range specified, offset: " + srcOffset + ", count: " + count + ", max: " + max);
		if ((indices.length - destOffset) < count)
			throw LSystem.runThrow(
					"not enough room in indices array, has " + indices.length + " shorts, needs " + count);
		int pos = getIndicesBuffer().position();
		getIndicesBuffer().position(srcOffset);
		getIndicesBuffer().get(indices, destOffset, count);
		getIndicesBuffer().position(pos);
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
		if (isVertexArray) {
			if (indices.getNumIndices() > 0) {
				ShortBuffer buffer = indices.getBuffer();
				int oldPosition = buffer.position();
				int oldLimit = buffer.limit();
				buffer.position(offset);
				buffer.limit(offset + count);
				LSystem.base().graphics().gl.glDrawElements(primitiveType, count, GL20.GL_UNSIGNED_SHORT, buffer);
				buffer.position(oldPosition);
				buffer.limit(oldLimit);
			} else {
				LSystem.base().graphics().gl.glDrawArrays(primitiveType, offset, count);
			}
		} else {
			if (indices.getNumIndices() > 0) {
				LSystem.base().graphics().gl.glDrawElements(primitiveType, count, GL20.GL_UNSIGNED_SHORT, offset * 2);
			} else {
				LSystem.base().graphics().gl.glDrawArrays(primitiveType, offset, count);
			}
		}

		if (autoBind) {
			unbind(shader);
		}
	}

	private boolean closed;

	public synchronized void close() {
		if (closed) {
			return;
		}
		TArray<Mesh> mesh = gmeshes.get(LSystem.base());
		if (mesh != null) {
			synchronized (Mesh.class) {
				mesh.removeValue(this);
			}
		}
		this.vertices.close();
		this.indices.close();
		this.closed = true;
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

	public FloatBuffer getVerticesBuffer() {
		return vertices.getBuffer();
	}

	public BoundingBox calculateBoundingBox() {
		BoundingBox bbox = new BoundingBox();
		calculateBoundingBox(bbox);
		return bbox;
	}

	public void calculateBoundingBox(BoundingBox bbox) {
		final int numVertices = getNumVertices();
		if (numVertices == 0) {
			throw LSystem.runThrow("No vertices defined");
		}

		final FloatBuffer verts = vertices.getBuffer();
		bbox.inf();
		final VertexAttribute posAttrib = getVertexAttribute(Usage.Position);
		final int offset = posAttrib.offset / 4;
		final int vertexSize = vertices.getAttributes().vertexSize / 4;
		int idx = offset;

		switch (posAttrib.numComponents) {
		case 1:
			for (int i = 0; i < numVertices; i++) {
				bbox.ext(verts.get(idx), 0, 0);
				idx += vertexSize;
			}
			break;
		case 2:
			for (int i = 0; i < numVertices; i++) {
				bbox.ext(verts.get(idx), verts.get(idx + 1), 0);
				idx += vertexSize;
			}
			break;
		case 3:
			for (int i = 0; i < numVertices; i++) {
				bbox.ext(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));
				idx += vertexSize;
			}
			break;
		}
	}

	public BoundingBox calculateBoundingBox(final BoundingBox out, int offset, int count) {
		return extendBoundingBox(out.inf(), offset, count);
	}

	public BoundingBox calculateBoundingBox(final BoundingBox out, int offset, int count, final Matrix4 transform) {
		return extendBoundingBox(out.inf(), offset, count, transform);
	}

	public BoundingBox extendBoundingBox(final BoundingBox out, int offset, int count) {
		return extendBoundingBox(out, offset, count, null);
	}

	private final Vector3f tmpV = new Vector3f();

	public BoundingBox extendBoundingBox(final BoundingBox out, int offset, int count, final Matrix4 transform) {
		int numIndices = getNumIndices();
		if (offset < 0 || count < 1 || offset + count > numIndices) {
			throw LSystem.runThrow(
					"Not enough indices ( offset=" + offset + ", count=" + count + ", max=" + numIndices + " )");
		}

		final FloatBuffer verts = vertices.getBuffer();
		final ShortBuffer index = indices.getBuffer();
		final VertexAttribute posAttrib = getVertexAttribute(Usage.Position);
		final int posoff = posAttrib.offset / 4;
		final int vertexSize = vertices.getAttributes().vertexSize / 4;
		final int end = offset + count;

		switch (posAttrib.numComponents) {
		case 1:
			for (int i = offset; i < end; i++) {
				final int idx = index.get(i) * vertexSize + posoff;
				tmpV.set(verts.get(idx), 0, 0);
				if (transform != null)
					tmpV.mulSelf(transform);
				out.ext(tmpV);
			}
			break;
		case 2:
			for (int i = offset; i < end; i++) {
				final int idx = index.get(i) * vertexSize + posoff;
				tmpV.set(verts.get(idx), verts.get(idx + 1), 0);
				if (transform != null)
					tmpV.mulSelf(transform);
				out.ext(tmpV);
			}
			break;
		case 3:
			for (int i = offset; i < end; i++) {
				final int idx = index.get(i) * vertexSize + posoff;
				tmpV.set(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));
				if (transform != null)
					tmpV.mulSelf(transform);
				out.ext(tmpV);
			}
			break;
		}
		return out;
	}

	public float calculateRadiusSquared(final float centerX, final float centerY, final float centerZ, int offset,
			int count, final Matrix4 transform) {
		int numIndices = getNumIndices();
		if (offset < 0 || count < 1 || offset + count > numIndices) {
			throw LSystem.runThrow("Not enough indices");
		}

		final FloatBuffer verts = vertices.getBuffer();
		final ShortBuffer index = indices.getBuffer();
		final VertexAttribute posAttrib = getVertexAttribute(Usage.Position);
		final int posoff = posAttrib.offset / 4;
		final int vertexSize = vertices.getAttributes().vertexSize / 4;
		final int end = offset + count;

		float result = 0;

		switch (posAttrib.numComponents) {
		case 1:
			for (int i = offset; i < end; i++) {
				final int idx = index.get(i) * vertexSize + posoff;
				tmpV.set(verts.get(idx), 0, 0);
				if (transform != null)
					tmpV.mulSelf(transform);
				final float r = tmpV.subtractSelf(centerX, centerY, centerZ).len2();
				if (r > result)
					result = r;
			}
			break;
		case 2:
			for (int i = offset; i < end; i++) {
				final int idx = index.get(i) * vertexSize + posoff;
				tmpV.set(verts.get(idx), verts.get(idx + 1), 0);
				if (transform != null)
					tmpV.mulSelf(transform);
				final float r = tmpV.subtractSelf(centerX, centerY, centerZ).len2();
				if (r > result)
					result = r;
			}
			break;
		case 3:
			for (int i = offset; i < end; i++) {
				final int idx = index.get(i) * vertexSize + posoff;
				tmpV.set(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));
				if (transform != null)
					tmpV.mulSelf(transform);
				final float r = tmpV.subtractSelf(centerX, centerY, centerZ).len2();
				if (r > result)
					result = r;
			}
			break;
		}
		return result;
	}

	public float calculateRadius(final float centerX, final float centerY, final float centerZ, int offset, int count,
			final Matrix4 transform) {
		return MathUtils.sqrt(calculateRadiusSquared(centerX, centerY, centerZ, offset, count, transform));
	}

	public float calculateRadius(final Vector3f center, int offset, int count, final Matrix4 transform) {
		return calculateRadius(center.x, center.y, center.z, offset, count, transform);
	}

	public float calculateRadius(final float centerX, final float centerY, final float centerZ, int offset, int count) {
		return calculateRadius(centerX, centerY, centerZ, offset, count, null);
	}

	public float calculateRadius(final Vector3f center, int offset, int count) {
		return calculateRadius(center.x, center.y, center.z, offset, count, null);
	}

	public float calculateRadius(final float centerX, final float centerY, final float centerZ) {
		return calculateRadius(centerX, centerY, centerZ, 0, getNumIndices(), null);
	}

	public float calculateRadius(final Vector3f center) {
		return calculateRadius(center.x, center.y, center.z, 0, getNumIndices(), null);
	}

	public ShortBuffer getIndicesBuffer() {
		return indices.getBuffer();
	}

	private static void addManagedMesh(LGame self, Mesh mesh) {
		TArray<Mesh> managedResources = gmeshes.get(self);
		if (managedResources == null) {
			managedResources = new TArray<Mesh>();
		}
		managedResources.add(mesh);
		gmeshes.put(self, managedResources);
	}

	public static void invalidateAllMeshes(LGame self) {
		if (gmeshes == null || gmeshes.size == 0) {
			return;
		}
		TArray<Mesh> meshesArray = gmeshes.get(self);
		if (meshesArray == null) {
			return;
		}
		for (int i = 0; i < meshesArray.size; i++) {
			meshesArray.get(i).vertices.invalidate();
			meshesArray.get(i).indices.invalidate();
		}
	}

	public static void clearAllMeshes(LGame self) {
		gmeshes.remove(self);
	}

	public static String getManagedStatus() {
		StringBuilder builder = new StringBuilder();
		builder.append("Managed meshes/self: { ");
		for (LGame self : gmeshes.keys()) {
			builder.append(gmeshes.get(self).size);
			builder.append(" ");
		}
		builder.append("}");
		return builder.toString();
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
			throw LSystem.runThrow("offset > vertexSize !");
		if (start < 0 || count < 1 || ((start + count) * vertexSize) > vertices.length)
			throw LSystem.runThrow("start = " + start + ", count = " + count + ", vertexSize = "
					+ vertexSize + ", length = " + vertices.length);

		final Vector3f tmp = new Vector3f();

		int idx = offset + (start * vertexSize);
		switch (dimensions) {
		case 1:
			for (int i = 0; i < count; i++) {
				tmp.set(vertices[idx], 0, 0).mulSelf(matrix);
				vertices[idx] = tmp.x;
				idx += vertexSize;
			}
			break;
		case 2:
			for (int i = 0; i < count; i++) {
				tmp.set(vertices[idx], vertices[idx + 1], 0).mulSelf(matrix);
				vertices[idx] = tmp.x;
				vertices[idx + 1] = tmp.y;
				idx += vertexSize;
			}
			break;
		case 3:
			for (int i = 0; i < count; i++) {
				tmp.set(vertices[idx], vertices[idx + 1], vertices[idx + 2]).mulSelf(matrix);
				vertices[idx] = tmp.x;
				vertices[idx + 1] = tmp.y;
				vertices[idx + 2] = tmp.z;
				idx += vertexSize;
			}
			break;
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

		final float[] vertices = new float[numVertices * vertexSize];
		getVertices(0, vertices.length, vertices);
		transformUV(matrix, vertices, vertexSize, offset, start, count);
		setVertices(vertices, 0, vertices.length);
	}

	final static Vector2f tmp = new Vector2f();

	public static void transformUV(final Matrix3 matrix, final float[] vertices, int vertexSize, int offset, int start,
			int count) {
		if (start < 0 || count < 1 || ((start + count) * vertexSize) > vertices.length) {
			throw LSystem.runThrow("start = " + start + ", count = " + count + ", vertexSize = "
					+ vertexSize + ", length = " + vertices.length);
		}
		int idx = offset + (start * vertexSize);
		for (int i = 0; i < count; i++) {
			tmp.set(vertices[idx], vertices[idx + 1]).mulSelf(matrix);
			vertices[idx] = tmp.x;
			vertices[idx + 1] = tmp.y;
			idx += vertexSize;
		}
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
					if (a == null)
						continue;
					for (int j = 0; j < a.numComponents; j++) {
						checks[++idx] = (short) (a.offset + j);
					}
					attrs[++ai] = new VertexAttribute(a.usage, a.numComponents, a.alias);
					newVertexSize += a.numComponents;
				}
			}
		}
		if (checks == null) {
			checks = new short[vertexSize];
			for (short i = 0; i < vertexSize; i++)
				checks[i] = i;
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
								if (tmp[idx2 + k] != vertices[idx1 + checks[k]])
									found = false;
							}
							if (found)
								newIndex = j;
						}
					}
					if (newIndex > 0)
						indices[i] = newIndex;
					else {
						final int idx = size * newVertexSize;
						for (int j = 0; j < checks.length; j++)
							tmp[idx + j] = vertices[idx1 + checks[j]];
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
		result.setIndices(indices);
		return result;

	}

	public Mesh copy(boolean isStatic) {
		return copy(isStatic, false, null);
	}

	@Override
	public String toString() {
		return super.toString() + " " + getManagedStatus();
	}
}
