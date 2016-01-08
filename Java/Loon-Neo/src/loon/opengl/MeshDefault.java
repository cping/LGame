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
import loon.opengl.Mesh.VertexDataType;
import loon.opengl.VertexAttributes.Usage;
import loon.utils.ObjectMap;

public class MeshDefault {

	private int type = GL20.GL_TRIANGLES;

	private boolean running = false;

	private boolean stop_main_readering = false;

	private final static ObjectMap<String, Mesh> meshLazy = new ObjectMap<String, Mesh>(
			10);

	public Mesh getMesh(String n, int size) {
		final String name = n + size;
		Mesh mesh = meshLazy.get(name);
		if (mesh == null) {
			mesh = new Mesh(VertexDataType.VertexArray, false, size * 4,
					size * 6, new VertexAttribute(Usage.Position, 2,
							ShaderProgram.POSITION_ATTRIBUTE),
					new VertexAttribute(Usage.ColorPacked, 4,
							ShaderProgram.COLOR_ATTRIBUTE),
					new VertexAttribute(Usage.TextureCoordinates, 2,
							ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
			resetIndices(size, mesh);
			meshLazy.put(name, mesh);
		}
		return mesh;
	}

	private void resetIndices(int size, Mesh mesh) {
		int len = size * 6;
		short[] indices = new short[len];
		short j = 0;
		for (int i = 0; i < len; i += 6, j += 4) {
			indices[i] = j;
			indices[i + 1] = (short) (j + 1);
			indices[i + 2] = (short) (j + 2);
			indices[i + 3] = (short) (j + 2);
			indices[i + 4] = (short) (j + 3);
			indices[i + 5] = j;
		}
		mesh.setIndices(indices);
	}

	public void setGLType(int type) {
		this.type = type;
	}

	public int getGLType() {
		return this.type;
	}

	public void setIndices(String name, int size, short[] indices) {
		Mesh mesh = getMesh(name, size);
		mesh.setIndices(indices);
	}

	public void resetIndices(String name, int size) {
		Mesh mesh = getMesh(name, size);
		resetIndices(size, mesh);
	}

	public void post(final String name, final int size, ShaderProgram shader,
			float[] vertices, int vertexIdx, int count) {
		// 防止与主画面渲染器GLEx冲突
		this.running = LSystem.mainDrawRunning();
		if (!running) {
			shader.glUseProgramBind();
		} else {
			LSystem.mainEndDraw();
			stop_main_readering = true;
		}
		Mesh mesh = getMesh(name, size);
		mesh.setVertices(vertices, 0, vertexIdx);
		mesh.getIndicesBuffer().position(0);
		mesh.getIndicesBuffer().limit(count);
		mesh.render(shader, type, 0, count);
		if (!running) {
			shader.glUseProgramUnBind();
		} else if (stop_main_readering) {
			LSystem.mainBeginDraw();
		}
	}

	public int size() {
		return meshLazy.size;
	}

	public void dispose(String name, int size) {
		final String key = name + size;
		Mesh mesh = meshLazy.remove(key);
		if (mesh != null) {
			mesh.close();
		}
	}

	public static void dispose() {
		for (Mesh mesh : meshLazy.values()) {
			if (mesh != null) {
				mesh.close();
			}
		}
		meshLazy.clear();
	}

}
