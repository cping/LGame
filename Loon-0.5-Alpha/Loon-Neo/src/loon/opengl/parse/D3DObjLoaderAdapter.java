/**
 * 
 * Copyright 2014
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
 * @version 0.4.1
 */
package loon.opengl.parse;

import java.nio.CharBuffer;
import java.nio.FloatBuffer;

public class D3DObjLoaderAdapter implements D3DIObjLoaderAdapter {

	D3DMesh mesh = new D3DMesh();
	
	float scale = 0.01f;

	@Override
	public void setVertexNumber(int n) {
		mesh.mVertices = FloatBuffer.allocate(n * D3DMesh.nbFloatPerVertex);
	}

	@Override
	public void setFaceNumber(int n) {
		mesh.mIndices = CharBuffer.allocate(n * 3);
	}

	@Override
	public void addVertex(float x, float y, float z) {
		mesh.mVertices.put(x * scale);
		mesh.mVertices.put(y * scale);
		mesh.mVertices.put(z * scale);
	}

	@Override
	public void addNormal(float xn, float yn, float zn) {
		mesh.mVertices.put(xn);
		mesh.mVertices.put(yn);
		mesh.mVertices.put(zn);
	}

	@Override
	public void addTexCoords(float u, float v) {
		mesh.mVertices.put(u);
		mesh.mVertices.put(v);
	}

	@Override
	public void addFace(char a, char b, char c) {
		mesh.mIndices.put(a);
		mesh.mIndices.put(b);
		mesh.mIndices.put(c);
	}

	public D3DMesh getMesh() {
		mesh.mVertices.position(0);
		mesh.mIndices.position(0);
		return mesh;
	}
}
