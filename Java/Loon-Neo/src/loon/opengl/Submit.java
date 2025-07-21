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

import loon.LRelease;
import loon.LSystem;

public class Submit implements LRelease {

	private int glType = GL20.GL_TRIANGLES;

	private boolean main_draw_running = false;

	private boolean need_stop_main_readering = false;

	public final Mesh getMesh(String n, int size) {
		return LSystem.getMeshPool(n, size);
	}

	public final Mesh getMesh(String n, int size, int trisize) {
		return LSystem.getMeshTrianglePool(n, size, trisize);
	}

	public final void reset(String n, int size) {
		LSystem.resetMeshPool(n, size);
		resetRunningState();
	}

	public final void reset(String n, int size, int trisize) {
		LSystem.resetMeshTrianglePool(n, size, trisize);
		resetRunningState();
	}

	public Submit setGLType(int type) {
		this.glType = type;
		return this;
	}

	public int getGLType() {
		return this.glType;
	}

	private void resetRunningState() {
		this.need_stop_main_readering = false;
	}

	public void setVertices(String name, int size, float[] vertices) {
		Mesh mesh = getMesh(name, size);
		mesh.setVertices(vertices);
		resetRunningState();
	}

	public void setIndices(String name, int size, short[] indices) {
		Mesh mesh = getMesh(name, size);
		mesh.setIndices(indices);
		resetRunningState();
	}

	public void resetIndices(String name, int size) {
		Mesh mesh = getMesh(name, size);
		LSystem.resetIndices(size, mesh);
		resetRunningState();
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

	public boolean isMainDrawRunning() {
		return this.main_draw_running;
	}

	public boolean isNeedStopMainReadering() {
		return this.need_stop_main_readering;
	}

	public void post(final String name, final int size, ShaderProgram shader, float[] vertices, int vertexIdx,
			int count) {
		// 防止与主画面渲染器GLEx冲突
		this.main_draw_running = LSystem.mainDrawRunning();
		if (!main_draw_running) {
			shader.glUseProgramBind();
		} else {
			need_stop_main_readering = true;
		}
		Mesh mesh = getMesh(name, size);
		if (mesh == null) {
			if (!main_draw_running) {
				shader.glUseProgramUnBind();
			}
			return;
		}
		mesh.setVertices(vertices, 0, vertexIdx);
		final ShortBuffer buffer = mesh.getIndicesBuffer(false);
		final int oldPosition = buffer.position();
		final int oldLimit = buffer.limit();
		final Buffer result = ((Buffer) buffer);
		result.position(0);
		result.limit(count);
		mesh.render(shader, glType, 0, count);
		result.position(oldPosition);
		result.limit(oldLimit);
		if (!main_draw_running) {
			shader.glUseProgramUnBind();
		}
	}

	public void post(final String name, final int size, final int trisize, ShaderProgram shader, short[] indices,
			int indicesIdx, float[] vertices, int vertexIdx, int countInBatch) {
		// 防止与主画面渲染器GLEx冲突
		this.main_draw_running = LSystem.mainDrawRunning();
		if (!main_draw_running) {
			shader.glUseProgramBind();
		} else {
			need_stop_main_readering = true;
		}
		Mesh mesh = getMesh(name, size, trisize);
		if (mesh == null) {
			if (!main_draw_running) {
				shader.glUseProgramUnBind();
			}
			return;
		}
		mesh.setVertices(vertices, 0, vertexIdx);
		mesh.setIndices(indices, 0, indicesIdx);
		final ShortBuffer buffer = mesh.getIndicesBuffer(false);
		final int oldPosition = buffer.position();
		final int oldLimit = buffer.limit();
		final Buffer result = ((Buffer) buffer);
		result.position(0);
		result.limit(countInBatch);
		mesh.render(shader, glType, 0, countInBatch);
		result.position(oldPosition);
		result.limit(oldLimit);
		if (!main_draw_running) {
			shader.glUseProgramUnBind();
		}
	}

	@Override
	public void close() {
		this.resetRunningState();
	}

}
