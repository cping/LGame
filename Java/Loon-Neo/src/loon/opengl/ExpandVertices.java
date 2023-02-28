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
package loon.opengl;

import loon.utils.CollectionUtils;

public class ExpandVertices {

	private static final int START_VERTS = 20;

	private static final int EXPAND_VERTS = 20;

	private float[] vertices;

	private int maxSize;

	public ExpandVertices(int size) {
		this.maxSize = size;
		this.init();
	}

	public void init() {
		vertices = new float[START_VERTS * maxSize];
	}

	public int vertexSize() {
		return START_VERTS;
	}

	public int length() {
		return vertices.length;
	}

	public int getSize() {
		return vertices.length / START_VERTS;
	}

	public boolean expand(int vertPos) {
		return expand(vertPos, START_VERTS);
	}

	public boolean expand(int vertPos, int vertexCount) {
		int vertIdx = vertPos / vertexSize();
		int verts = vertIdx + vertexCount;
		int availVerts = vertices.length / vertexSize();
		if (verts <= availVerts) {
			return false;
		}
		if (verts > availVerts) {
			expandVert(verts);
			return true;
		}
		return false;
	}

	private final void expandVert(int vertCount) {
		int newVerts = vertices.length / vertexSize();
		while (newVerts < vertCount) {
			newVerts += EXPAND_VERTS;
		}
		this.vertices = CollectionUtils.expand(this.vertices, newVerts * vertexSize());
	}

	public final void setVertice(int index, float v) {
		if (expand(index)) {
			maxSize = getSize();
		}
		this.vertices[index] = v;
	}

	public final float[] getVertices() {
		return this.vertices;
	}

}
