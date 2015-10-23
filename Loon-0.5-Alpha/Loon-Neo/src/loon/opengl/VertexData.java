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

import loon.LRelease;

public interface VertexData extends LRelease {

	public int getNumVertices();

	public int getNumMaxVertices();

	public VertexAttributes getAttributes();

	public void setVertices(float[] vertices, int offset, int count);

	public void updateVertices(int targetOffset, float[] vertices,
			int sourceOffset, int count);

	public FloatBuffer getBuffer();

	public void bind(ShaderProgram shader);

	public void bind(ShaderProgram shader, int[] locations);

	public void unbind(ShaderProgram shader);

	public void unbind(ShaderProgram shader, int[] locations);

	public void invalidate();

	public void close();
}
