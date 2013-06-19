/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.core.graphics.opengl;

import java.nio.FloatBuffer;

import loon.core.LRelease;
import loon.core.graphics.LColor;
import loon.jni.NativeSupport;

//0.3.3新增的顶点操作用类，视有无native支持而采取不同的操作方针。
public class GLBatch implements LRelease {

	private int primitiveType;

	private float[] vertexs;
	private FloatBuffer vertexsBuffer;

	private float[] colors;
	private FloatBuffer colorsBuffer;

	private float[] normals;
	private FloatBuffer normalsBuffer;

	private float[] texCoords;
	private FloatBuffer texCoordsBuffer;

	private int indexPos = 0;
	private int indexCols = 0;
	private int indexNors = 0;
	private int indexTexCoords = 0;

	private boolean hasCols;
	private boolean hasNors;
	private boolean hasTexCoords;

	private boolean nativeLibs;
	private boolean closed;

	private final int maxVertices;

	private int numVertices;

	private LColor color = new LColor(1f, 1f, 1f, 1f);

	public GLBatch() {
		this(3000);
	}

	public GLBatch(int maxVertices) {
		this.maxVertices = maxVertices;
		this.nativeLibs = NativeSupport.UseLoonNative();
		if (nativeLibs) {
			this.vertexs = new float[3 * maxVertices];
			this.vertexsBuffer = NativeSupport.newFloatBuffer(3 * maxVertices);
			this.colors = new float[4 * maxVertices];
			this.colorsBuffer = NativeSupport.newFloatBuffer(4 * maxVertices);
			this.normals = new float[3 * maxVertices];
			this.normalsBuffer = NativeSupport.newFloatBuffer(3 * maxVertices);
			this.texCoords = new float[2 * maxVertices];
			this.texCoordsBuffer = NativeSupport
					.newFloatBuffer(2 * maxVertices);
		} else {
			this.vertexsBuffer = NativeSupport.newFloatBuffer(3 * maxVertices);
			this.colorsBuffer = NativeSupport.newFloatBuffer(4 * maxVertices);
			this.normalsBuffer = NativeSupport.newFloatBuffer(3 * maxVertices);
			this.texCoordsBuffer = NativeSupport
					.newFloatBuffer(2 * maxVertices);
		}
	}

	public void begin(int p) {
		synchronized (GLBatch.class) {
			GLEx.self.glTex2DDisable();
			this.primitiveType = p;
			this.numVertices = 0;
			if (nativeLibs) {
				indexPos = 0;
				indexCols = 0;
				indexNors = 0;
				indexTexCoords = 0;
			} else {
				vertexsBuffer.rewind();
				vertexsBuffer.limit(vertexsBuffer.capacity());
				colorsBuffer.rewind();
				colorsBuffer.limit(colorsBuffer.capacity());
				normalsBuffer.rewind();
				normalsBuffer.limit(normalsBuffer.capacity());
				texCoordsBuffer.rewind();
				texCoordsBuffer.limit(texCoordsBuffer.capacity());
			}
			this.hasCols = false;
			this.hasNors = false;
			this.hasTexCoords = false;
		}
	}

	public void color(float r, float g, float b, float a) {
		if (nativeLibs) {
			colors[indexCols] = r;
			colors[indexCols + 1] = g;
			colors[indexCols + 2] = b;
			colors[indexCols + 3] = a;
		} else {
			colorsBuffer.put(r);
			colorsBuffer.put(g);
			colorsBuffer.put(b);
			colorsBuffer.put(a);
		}
		color.setColor(r, g, b, a);
		hasCols = true;
	}

	public void color(LColor c) {
		if (nativeLibs) {
			colors[indexCols] = c.r;
			colors[indexCols + 1] = c.g;
			colors[indexCols + 2] = c.b;
			colors[indexCols + 3] = c.a;
		} else {
			colorsBuffer.put(c.r);
			colorsBuffer.put(c.g);
			colorsBuffer.put(c.b);
			colorsBuffer.put(c.a);
		}
		color.setColor(c);
		hasCols = true;
	}

	public LColor getColor() {
		return color;
	}

	public void normal(float x, float y, float z) {
		if (nativeLibs) {
			normals[indexNors] = x;
			normals[indexNors + 1] = y;
			normals[indexNors + 2] = z;
		} else {
			normalsBuffer.put(x);
			normalsBuffer.put(y);
			normalsBuffer.put(z);
		}
		hasNors = true;
	}

	public void texCoord(float u, float v) {
		if (nativeLibs) {
			texCoords[indexTexCoords] = u;
			texCoords[indexTexCoords + 1] = v;
		} else {
			texCoordsBuffer.put(u);
			texCoordsBuffer.put(v);
		}
		hasTexCoords = true;
	}

	public void vertex(float x, float y) {
		vertex(x, y, 0);
	}

	public void vertex(float x, float y, float z) {
		if (nativeLibs) {
			vertexs[indexPos++] = x;
			vertexs[indexPos++] = y;
			vertexs[indexPos++] = z;
			if (hasCols) {
				indexCols += 4;
			}
			if (hasNors) {
				indexNors += 3;
			}
			if (hasTexCoords) {
				indexTexCoords += 2;
			}
		} else {
			vertexsBuffer.put(x);
			vertexsBuffer.put(y);
			vertexsBuffer.put(z);
		}
		numVertices++;
	}

	public int getNumVertices() {
		return numVertices;
	}

	public int getMaxVertices() {
		return maxVertices;
	}

	public synchronized void end() {
		if (numVertices == 0) {
			return;
		}
		synchronized (GLBatch.class) {
			GL10 gl = GLEx.gl10;
			if (nativeLibs) {
				gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
				vertexsBuffer.clear();
				NativeSupport.copy(vertexs, vertexsBuffer, 0, indexPos);
				gl.glVertexPointer(3, GL.GL_FLOAT, 0, vertexsBuffer);
				if (hasCols) {
					gl.glEnableClientState(GL.GL_COLOR_ARRAY);
					colorsBuffer.clear();
					NativeSupport.copy(colors, colorsBuffer, 0, indexCols);
					gl.glColorPointer(4, GL.GL_FLOAT, 0, colorsBuffer);
				}
				if (hasNors) {
					gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
					normalsBuffer.clear();
					NativeSupport.copy(normals, normalsBuffer, 0, indexNors);
					gl.glNormalPointer(GL.GL_FLOAT, 0, normalsBuffer);
				}
				if (hasTexCoords) {
					gl.glClientActiveTexture(GL.GL_TEXTURE0);
					gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
					texCoordsBuffer.clear();
					NativeSupport.copy(texCoords, texCoordsBuffer, 0,
							indexTexCoords);
					gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, texCoordsBuffer);
				}
			} else {
				gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
				vertexsBuffer.flip();
				gl.glVertexPointer(3, GL.GL_FLOAT, 0, vertexsBuffer);
				if (hasCols) {
					gl.glEnableClientState(GL.GL_COLOR_ARRAY);
					colorsBuffer.flip();
					gl.glColorPointer(4, GL.GL_FLOAT, 0, colorsBuffer);
				}
				if (hasNors) {
					gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
					normalsBuffer.flip();
					gl.glNormalPointer(GL.GL_FLOAT, 0, normalsBuffer);
				}
				if (hasTexCoords) {
					gl.glClientActiveTexture(GL.GL_TEXTURE0);
					gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
					texCoordsBuffer.flip();
					gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, texCoordsBuffer);
				}
			}
			gl.glDrawArrays(primitiveType, 0, numVertices);
			if (hasCols) {
				gl.glDisableClientState(GL.GL_COLOR_ARRAY);
			}
			if (hasNors) {
				gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
			}
			if (hasTexCoords) {
				gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
			}
			GLEx.self.glTex2DEnable();
		}
	}

	public boolean isClose() {
		return closed;
	}

	@Override
	public void dispose() {
		closed = true;
		vertexs = null;
		vertexsBuffer = null;
		colors = null;
		colorsBuffer = null;
		normals = null;
		normalsBuffer = null;
		texCoords = null;
		texCoordsBuffer = null;
	}

}
