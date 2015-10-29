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

	private final ObjectMap<Integer, Mesh> meshLazy = new ObjectMap<Integer, Mesh>(
			10);

	public Mesh getMesh(final int size) {
		Mesh mesh = meshLazy.get(size);
		if (mesh == null) {
			mesh = new Mesh(VertexDataType.VertexArray, false, size * 4,
					size * 6, new VertexAttribute(Usage.Position, 2,
							ShaderProgram.POSITION_ATTRIBUTE),
					new VertexAttribute(Usage.ColorPacked, 4,
							ShaderProgram.COLOR_ATTRIBUTE),
					new VertexAttribute(Usage.TextureCoordinates, 2,
							ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
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
			meshLazy.put(size, mesh);
		}
		return mesh;
	}

	public void post(final int size, ShaderProgram shader, float[] vertices,
			int vertexIdx, int count) {
		boolean running = LSystem.mainDrawRunning();
		if (!running) {
			shader.glUseProgramBind();
		}
		Mesh mesh = getMesh(size);
		mesh.setVertices(vertices, 0, vertexIdx);
		mesh.getIndicesBuffer().position(0);
		mesh.getIndicesBuffer().limit(count);
		mesh.render(shader, GL20.GL_TRIANGLES, 0, count);
		if (!running) {
			shader.glUseProgramUnBind();
		}
	}
}
