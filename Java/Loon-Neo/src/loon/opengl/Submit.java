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
import java.nio.ShortBuffer;

import loon.LSystem;

public class Submit {

	private int glType = GL20.GL_TRIANGLES;

	private boolean running = false;

	private boolean stop_main_readering = false;

	public Mesh getMesh(String n, int size) {
		return LSystem.getMeshPool(n, size);
	}

	public Mesh getMesh(String n, int size, int trisize) {
		return LSystem.getMeshTrianglePool(n, size, trisize);
	}

	public void reset(String n, int size) {
		LSystem.resetMeshPool(n, size);
	}

	public void reset(String n, int size, int trisize) {
		LSystem.resetMeshTrianglePool(n, size, trisize);
	}

	public Submit setGLType(int type) {
		this.glType = type;
		return this;
	}

	public int getGLType() {
		return this.glType;
	}

	public void setVertices(String name, int size, float[] vertices) {
		Mesh mesh = getMesh(name, size);
		mesh.setVertices(vertices);
	}

	public void setIndices(String name, int size, short[] indices) {
		Mesh mesh = getMesh(name, size);
		mesh.setIndices(indices);
	}

	public void resetIndices(String name, int size) {
		Mesh mesh = getMesh(name, size);
		LSystem.resetIndices(size, mesh);
	}

	public int size() {
		return LSystem.getMeshPoolSize();
	}

	public void dispose(String name, int size) {
		LSystem.disposeMeshPool(name, size);
	}

	public static void dispose() {
		LSystem.disposeMeshPool();
	}

	public void post(final String name, final int size, ShaderProgram shader, float[] vertices, int vertexIdx,
			int count) {
		// 防止与主画面渲染器GLEx冲突
		this.running = LSystem.mainDrawRunning();
		if (!running) {
			shader.glUseProgramBind();
		} else {
			LSystem.mainEndDraw();
			stop_main_readering = true;
		}
		Mesh mesh = getMesh(name, size);
		if (mesh == null) {
			if (!running) {
				shader.glUseProgramUnBind();
			} else if (stop_main_readering) {
				LSystem.mainBeginDraw();
			}
			return;
		}
		mesh.setVertices(vertices, 0, vertexIdx);
		final ShortBuffer buffer = mesh.getIndicesBuffer(false);
		final int oldPosition = buffer.position();
		final int oldLimit = buffer.limit();
		((Buffer) buffer).position(0);
		((Buffer) buffer).limit(count);
		mesh.render(shader, glType, 0, count);
		((Buffer) buffer).position(oldPosition);
		((Buffer) buffer).limit(oldLimit);
		if (!running) {
			shader.glUseProgramUnBind();
		} else if (stop_main_readering) {
			LSystem.mainBeginDraw();
		}
	}

	public void post(final String name, final int size, final int trisize, ShaderProgram shader, short[] indices,
			int indicesIdx, float[] vertices, int vertexIdx, int countInBatch) {
		// 防止与主画面渲染器GLEx冲突
		this.running = LSystem.mainDrawRunning();
		if (!running) {
			shader.glUseProgramBind();
		} else {
			LSystem.mainEndDraw();
			stop_main_readering = true;
		}
		Mesh mesh = getMesh(name, size, trisize);
		if (mesh == null) {
			if (!running) {
				shader.glUseProgramUnBind();
			} else if (stop_main_readering) {
				LSystem.mainBeginDraw();
			}
			return;
		}
		mesh.setVertices(vertices, 0, vertexIdx);
		mesh.setIndices(indices, 0, indicesIdx);
		mesh.render(shader, glType, 0, countInBatch);
		if (!running) {
			shader.glUseProgramUnBind();
		} else if (stop_main_readering) {
			LSystem.mainBeginDraw();
		}
	}

}
