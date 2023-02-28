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
	private ShaderProgram customShader;
	private boolean ownsShader, closed;
	private int numTexCoords;
	private int vertexSize;
	private int normalOffset;
	private int colorOffset;
	private int texCoordOffset;
	private final Affine2f projModelView = new Affine2f();
	private ExpandVertices expandVertices;
	private String[] shaderUniformNames;

	public GLBatch(boolean hasNormals, boolean hasColors, int numTexCoords) {
		this(2048, hasNormals, hasColors, numTexCoords, null);
		ownsShader = true;
	}

	public GLBatch(int maxVertices, boolean hasNormals, boolean hasColors, int numTexCoords) {
		this(maxVertices, hasNormals, hasColors, numTexCoords, null);
		ownsShader = true;
	}

	private boolean hasNormals, hasColors;

	public GLBatch(int maxVertices, boolean hasNormals, boolean hasColors, int numTexCoords, ShaderProgram shader) {
		this.maxVertices = maxVertices;
		this.numTexCoords = numTexCoords;
		this.customShader = shader;
		this.hasNormals = hasNormals;
		this.hasColors = hasColors;
	}

	private VertexAttribute[] buildVertexAttributes(boolean hasNormals, boolean hasColor, int numTexCoords) {
		TArray<VertexAttribute> attribs = new TArray<VertexAttribute>(numTexCoords + 2);
		attribs.add(new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE));
		if (hasNormals) {
			attribs.add(new VertexAttribute(Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE));
		}
		if (hasColor) {
			attribs.add(new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE));
		}
		for (int i = 0; i < numTexCoords; i++) {
			attribs.add(new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + i));
		}
		final int size = attribs.size;
		final VertexAttribute[] array = new VertexAttribute[size];
		for (int i = 0; i < size; i++) {
			array[i] = attribs.get(i);
		}
		return array;
	}

	public void setShader(ShaderProgram shader) {
		if (shader == customShader) {
			return;
		}
		if (ownsShader) {
			this.customShader.close();
		}
		this.customShader = shader;
		ownsShader = false;
	}

	public void begin(Affine2f projModelView, int primitiveType) {
		begin(maxVertices, projModelView, primitiveType);
	}

	public void begin(int maxSize, Affine2f projModelView, int primitiveType) {
		if (customShader == null) {
			VertexAttribute[] attribs = buildVertexAttributes(hasNormals, hasColors, numTexCoords);
			mesh = new Mesh(false, maxSize, 0, attribs);
			expandVertices = new ExpandVertices(maxSize * (mesh.getVertexAttributes().vertexSize / 4));
			vertexSize = mesh.getVertexAttributes().vertexSize / 4;
			normalOffset = mesh.getVertexAttribute(Usage.Normal) != null
					? mesh.getVertexAttribute(Usage.Normal).offset / 4
					: 0;
			colorOffset = mesh.getVertexAttribute(Usage.ColorPacked) != null
					? mesh.getVertexAttribute(Usage.ColorPacked).offset / 4
					: 0;
			texCoordOffset = mesh.getVertexAttribute(Usage.TextureCoordinates) != null
					? mesh.getVertexAttribute(Usage.TextureCoordinates).offset / 4
					: 0;

			shaderUniformNames = new String[numTexCoords];
			for (int i = 0; i < numTexCoords; i++) {
				shaderUniformNames[i] = "u_sampler" + i;
			}
			customShader = createDefaultShader(hasNormals, hasColors, numTexCoords);
		}
		this.numSetTexCoords = 0;
		this.vertexIdx = 0;
		this.numVertices = 0;
		this.projModelView.set(projModelView);
		this.primitiveType = primitiveType;
	}

	public void reset(int verticesSize) {
		if (customShader != null) {
			customShader.close();
			customShader = null;
		}
		if (mesh != null) {
			mesh.close();
			mesh = null;
		}
		VertexAttribute[] attribs = buildVertexAttributes(hasNormals, hasColors, numTexCoords);
		mesh = new Mesh(false, verticesSize, 0, attribs);
		vertexSize = mesh.getVertexAttributes().vertexSize / 4;
		normalOffset = mesh.getVertexAttribute(Usage.Normal) != null ? mesh.getVertexAttribute(Usage.Normal).offset / 4
				: 0;
		colorOffset = mesh.getVertexAttribute(Usage.ColorPacked) != null
				? mesh.getVertexAttribute(Usage.ColorPacked).offset / 4
				: 0;
		texCoordOffset = mesh.getVertexAttribute(Usage.TextureCoordinates) != null
				? mesh.getVertexAttribute(Usage.TextureCoordinates).offset / 4
				: 0;
		shaderUniformNames = new String[numTexCoords];
		for (int i = 0; i < numTexCoords; i++) {
			shaderUniformNames[i] = "u_sampler" + i;
		}
		customShader = createDefaultShader(hasNormals, hasColors, numTexCoords);
		this.numSetTexCoords = 0;
		this.vertexIdx = 0;
		this.numVertices = 0;
	}

	public void color(float color) {
		expandVertices.setVertice(vertexIdx + colorOffset, color);
	}

	public void color(LColor color) {
		expandVertices.setVertice(vertexIdx + colorOffset, color.toFloatBits());
	}

	public void color(float r, float g, float b, float a) {
		expandVertices.setVertice(vertexIdx + colorOffset, LColor.toFloatBits(r, g, b, a));
	}

	public void texCoord(float u, float v) {
		final int idx = vertexIdx + texCoordOffset;
		expandVertices.setVertice(idx + numSetTexCoords, u);
		expandVertices.setVertice(idx + numSetTexCoords + 1, v);
		numSetTexCoords += 2;
	}

	public void normal(float x, float y, float z) {
		final int idx = vertexIdx + normalOffset;
		expandVertices.setVertice(idx, x);
		expandVertices.setVertice(idx + 1, y);
		expandVertices.setVertice(idx + 2, z);
	}

	public void vertex(float x, float y) {
		vertex(x, y, 0);
	}

	public void vertex(float x, float y, float z) {
		final int idx = vertexIdx;
		expandVertices.setVertice(idx, x);
		expandVertices.setVertice(idx + 1, y);
		expandVertices.setVertice(idx + 2, z);

		numSetTexCoords = 0;
		vertexIdx += vertexSize;
		numVertices++;
	}

	public void flush() {
		if (numVertices == 0) {
			return;
		}
		try {
			customShader.begin();
			customShader.setUniformMatrix("u_projModelView", projModelView.toViewMatrix4());
			for (int i = 0; i < numTexCoords; i++) {
				customShader.setUniformi(shaderUniformNames[i], i);
			}
			mesh.setVertices(expandVertices.getVertices(), 0, vertexIdx);
			mesh.render(customShader, primitiveType);
		} catch (Throwable ex) {
			LSystem.error("Batch error flush()", ex);
		} finally {
			customShader.end();
		}
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

	public static String createVertexShader(boolean hasNormals, boolean hasColors, int numTexCoords) {
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, hasNormals);
		hashCode = LSystem.unite(hashCode, hasColors);
		hashCode = LSystem.unite(hashCode, numTexCoords);
		ShaderCmd cmd = ShaderCmd.getCmd("dvertexshader" + hashCode);
		if (cmd.isCache()) {
			return cmd.getShader();
		} else {
			cmd.putAttributeVec4(ShaderProgram.POSITION_ATTRIBUTE);
			if (hasNormals) {
				cmd.putAttributeVec3(ShaderProgram.NORMAL_ATTRIBUTE);
			}
			if (hasColors) {
				cmd.putAttributeVec4(ShaderProgram.COLOR_ATTRIBUTE);
			}
			for (int i = 0; i < numTexCoords; i++) {
				cmd.putAttributeVec2(ShaderProgram.TEXCOORD_ATTRIBUTE + i);
			}
			cmd.putUniformMat4("u_projModelView");
			if (hasColors) {
				cmd.putVaryingVec4("v_col");
			}
			for (int i = 0; i < numTexCoords; i++) {
				cmd.putVaryingVec2("v_tex" + i);
			}
			String mainCmd = "   gl_Position = u_projModelView * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
					+ (hasColors ? "   v_col = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" : LSystem.EMPTY);
			for (int i = 0; i < numTexCoords; i++) {
				mainCmd += "   v_tex" + i + " = " + ShaderProgram.TEXCOORD_ATTRIBUTE + i + ";\n";
			}
			mainCmd += "   gl_PointSize = 1.0;";
			cmd.putMainCmd(mainCmd);
			return cmd.getShader();
		}

	}

	public static String createFragmentShader(boolean hasNormals, boolean hasColors, int numTexCoords) {
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, hasNormals);
		hashCode = LSystem.unite(hashCode, hasColors);
		hashCode = LSystem.unite(hashCode, numTexCoords);
		ShaderCmd cmd = ShaderCmd.getCmd("dfragmentshader" + hashCode);
		if (cmd.isCache()) {
			return cmd.getShader();
		} else {
			cmd.putDefine("#ifdef GL_ES\n" + "precision mediump float;\n" + "#endif\n");
			if (hasColors) {
				cmd.putVaryingVec4("v_col");
			}
			for (int i = 0; i < numTexCoords; i++) {
				cmd.putVaryingVec2("v_tex" + i);
				cmd.putUniform("sampler2D", "u_sampler" + i);
			}

			String mainCmd = "  gl_FragColor = " + (hasColors ? "v_col" : "vec4(1, 1, 1, 1)");
			if (numTexCoords > 0) {
				mainCmd += " * ";
			}
			for (int i = 0; i < numTexCoords; i++) {
				if (i == numTexCoords - 1) {
					mainCmd += " texture2D(u_sampler" + i + ",  v_tex" + i + ")";
				} else {
					mainCmd += " texture2D(u_sampler" + i + ",  v_tex" + i + ") *";
				}
			}
			mainCmd += ";";
			cmd.putMainCmd(mainCmd);
			return cmd.getShader();
		}
	}

	public static ShaderProgram createDefaultShader(boolean hasNormals, boolean hasColors, int numTexCoords) {
		String vertexShader = createVertexShader(hasNormals, hasColors, numTexCoords);
		String fragmentShader = createFragmentShader(hasNormals, hasColors, numTexCoords);
		ShaderProgram program = new ShaderProgram(vertexShader, fragmentShader);
		return program;
	}

	@Override
	public void close() {
		if (ownsShader && customShader != null) {
			customShader.close();
		}
		if (mesh != null) {
			mesh.close();
		}
		closed = true;
	}

	public boolean isClosed() {
		return closed;
	}

}
