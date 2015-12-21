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

import loon.LRelease;
import loon.LSystem;
import loon.canvas.LColor;
import loon.geom.Affine2f;
import loon.opengl.VertexAttributes.Usage;
import loon.utils.TArray;

public class GLBatch implements LRelease {

	private int primitiveType;
	private int vertexIdx;
	private int numSetTexCoords;
	private final int maxVertices;
	private int numVertices;

	private Mesh mesh;
	private ShaderProgram shader;
	private boolean ownsShader;
	private int numTexCoords;
	private int vertexSize;
	private int normalOffset;
	private int colorOffset;
	private int texCoordOffset;
	private final Affine2f projModelView = new Affine2f();
	float[] vertices;
	private String[] shaderUniformNames;

	public GLBatch(boolean hasNormals, boolean hasColors, int numTexCoords) {
		this(5000, hasNormals, hasColors, numTexCoords, null);
		ownsShader = true;
	}

	public GLBatch(int maxVertices, boolean hasNormals, boolean hasColors,
			int numTexCoords) {
		this(maxVertices, hasNormals, hasColors, numTexCoords, null);
		ownsShader = true;
	}

	private boolean hasNormals, hasColors;

	public GLBatch(int maxVertices, boolean hasNormals, boolean hasColors,
			int numTexCoords, ShaderProgram shader) {
		this.maxVertices = maxVertices;
		this.numTexCoords = numTexCoords;
		this.shader = shader;
		this.hasNormals = hasNormals;
		this.hasColors = hasColors;

	}

	private VertexAttribute[] buildVertexAttributes(boolean hasNormals,
			boolean hasColor, int numTexCoords) {
		TArray<VertexAttribute> attribs = new TArray<VertexAttribute>(
				numTexCoords + 2);
		attribs.add(new VertexAttribute(Usage.Position, 3,
				ShaderProgram.POSITION_ATTRIBUTE));
		if (hasNormals) {
			attribs.add(new VertexAttribute(Usage.Normal, 3,
					ShaderProgram.NORMAL_ATTRIBUTE));
		}
		if (hasColor) {
			attribs.add(new VertexAttribute(Usage.ColorPacked, 4,
					ShaderProgram.COLOR_ATTRIBUTE));
		}
		for (int i = 0; i < numTexCoords; i++) {
			attribs.add(new VertexAttribute(Usage.TextureCoordinates, 2,
					ShaderProgram.TEXCOORD_ATTRIBUTE + i));
		}
		final int size = attribs.size;
		final VertexAttribute[] array = new VertexAttribute[size];
		for (int i = 0; i < size; i++) {
			array[i] = attribs.get(i);
		}
		return array;
	}

	public void setShader(ShaderProgram shader) {
		if (ownsShader) {
			this.shader.close();
		}
		this.shader = shader;
		ownsShader = false;
	}

	public void begin(Affine2f projModelView, int primitiveType) {
		if (shader == null) {
			VertexAttribute[] attribs = buildVertexAttributes(hasNormals,
					hasColors, numTexCoords);
			mesh = new Mesh(false, maxVertices, 0, attribs);
			vertices = new float[maxVertices
					* (mesh.getVertexAttributes().vertexSize / 4)];
			vertexSize = mesh.getVertexAttributes().vertexSize / 4;
			normalOffset = mesh.getVertexAttribute(Usage.Normal) != null ? mesh
					.getVertexAttribute(Usage.Normal).offset / 4 : 0;
			colorOffset = mesh.getVertexAttribute(Usage.ColorPacked) != null ? mesh
					.getVertexAttribute(Usage.ColorPacked).offset / 4 : 0;
			texCoordOffset = mesh.getVertexAttribute(Usage.TextureCoordinates) != null ? mesh
					.getVertexAttribute(Usage.TextureCoordinates).offset / 4
					: 0;

			shaderUniformNames = new String[numTexCoords];
			for (int i = 0; i < numTexCoords; i++) {
				shaderUniformNames[i] = "u_sampler" + i;
			}
			shader = createDefaultShader(hasNormals, hasColors, numTexCoords);
		}
		this.numSetTexCoords = 0;
		this.vertexIdx = 0;
		this.numVertices = 0;
		this.projModelView.set(projModelView);
		this.primitiveType = primitiveType;
	}

	public void color(LColor color) {
		vertices[vertexIdx + colorOffset] = color.toFloatBits();
	}

	public void color(float r, float g, float b, float a) {
		vertices[vertexIdx + colorOffset] = LColor.toFloatBits(r, g, b, a);
	}

	public void texCoord(float u, float v) {
		final int idx = vertexIdx + texCoordOffset;
		vertices[idx + numSetTexCoords] = u;
		vertices[idx + numSetTexCoords + 1] = v;
		numSetTexCoords += 2;
	}

	public void normal(float x, float y, float z) {
		final int idx = vertexIdx + normalOffset;
		vertices[idx] = x;
		vertices[idx + 1] = y;
		vertices[idx + 2] = z;
	}

	public void vertex(float x, float y) {
		vertex(x, y, 0);
	}

	public void vertex(float x, float y, float z) {
		final int idx = vertexIdx;
		vertices[idx] = x;
		vertices[idx + 1] = y;
		vertices[idx + 2] = z;

		numSetTexCoords = 0;
		vertexIdx += vertexSize;
		numVertices++;
	}

	public void flush() {
		if (numVertices == 0) {
			return;
		}
		shader.begin();
		shader.setUniformMatrix("u_projModelView",
				projModelView.toViewMatrix4());
		for (int i = 0; i < numTexCoords; i++) {
			shader.setUniformi(shaderUniformNames[i], i);
		}
		mesh.setVertices(vertices, 0, vertexIdx);
		mesh.render(shader, primitiveType);
		shader.end();
	}

	public void end() {
		flush();
	}

	public int getNumVertices() {
		return numVertices;
	}

	public int getMaxVertices() {
		return maxVertices;
	}

	public void close() {
		if (ownsShader && shader != null) {
			shader.close();
		}
		mesh.close();
	}

	static public ShaderProgram createDefaultShader(boolean hasNormals,
			boolean hasColors, int numTexCoords) {
		String vertexShader = LSystem.createVertexShader(hasNormals, hasColors,
				numTexCoords);
		String fragmentShader = LSystem.createFragmentShader(hasNormals,
				hasColors, numTexCoords);
		ShaderProgram program = new ShaderProgram(vertexShader, fragmentShader);
		return program;
	}
}
