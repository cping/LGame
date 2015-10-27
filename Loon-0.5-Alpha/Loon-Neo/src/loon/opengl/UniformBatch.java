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

import static loon.opengl.GL20.*;
import loon.opengl.ShaderProgram.Mini;

public class UniformBatch extends BaseBatch {

	private static final int VERTICES_PER_QUAD = 4;
	private static final int ELEMENTS_PER_QUAD = 6;
	private static final int VERTEX_SIZE = 3; 
	private static final int BASE_VEC4S_PER_QUAD = 3; 
	
	public static class Source extends LTextureBind.Source {

		public static final String VERT_UNIFS = "uniform vec2 u_HScreenSize;\n"
				+ "uniform float u_Flip;\n"
				+ "uniform vec4 u_Data[_VEC4S_PER_QUAD_*_MAX_QUADS_];\n";

		public static final String VERT_ATTRS = "attribute vec3 a_Vertex;\n";

		public static final String VERT_VARS = "varying vec2 v_TexCoord;\n"
				+ "varying vec4 v_Color;\n";

		public static final String VERT_EXTRACTDATA = "int index = _VEC4S_PER_QUAD_*int(a_Vertex.z);\n"
				+ "vec4 mat = u_Data[index+0];\n"
				+ "vec4 txc = u_Data[index+1];\n"
				+ "vec4 tcs = u_Data[index+2];\n";

		public static final String VERT_SETPOS =

		"mat3 transform = mat3(\n"
				+ "  mat.x, mat.y, 0,\n"
				+ "  mat.z, mat.w, 0,\n"
				+ "  txc.x, txc.y, 1);\n"
				+ "gl_Position = vec4(transform * vec3(a_Vertex.xy, 1.0), 1.0);\n"
				+
				"gl_Position.xy /= u_HScreenSize.xy;\n" +
				"gl_Position.xy -= 1.0;\n" +
				"gl_Position.y *= u_Flip;\n";

		public static final String VERT_SETTEX = "v_TexCoord = a_Vertex.xy * tcs.xy + txc.zw;\n";

		public static final String VERT_SETCOLOR =
		
		"float red = mod(tcs.z, 256.0);\n"
				+ "float alpha = (tcs.z - red) / 256.0;\n"
				+ "float blue = mod(tcs.w, 256.0);\n"
				+ "float green = (tcs.w - blue) / 256.0;\n"
				+ "v_Color = vec4(red / 255.0, green / 255.0, blue / 255.0, alpha / 255.0);\n";

		public String vertex(UniformBatch batch) {
			return vertex().replace("_MAX_QUADS_", "" + batch.maxQuads)
					.replace("_VEC4S_PER_QUAD_", "" + BASE_VEC4S_PER_QUAD);
		}

		protected String vertex() {
			return (VERT_UNIFS + VERT_ATTRS + VERT_VARS + "void main(void) {\n"
					+ VERT_EXTRACTDATA + VERT_SETPOS + VERT_SETTEX
					+ VERT_SETCOLOR + "}");
		}
	}

	public static boolean isLikelyToPerform(GL20 gl) {
		int maxVecs = usableMaxUniformVectors(gl);
		return (maxVecs >= 16 * BASE_VEC4S_PER_QUAD);
	}

	protected int maxQuads;

	protected Mini program;
	protected int uTexture;
	protected int uHScreenSize;
	protected int uFlip;
	protected int uData;
	protected int aVertex;

	protected int verticesId, elementsId;
	protected float[] data;
	protected int quadCounter;
	private Source source;

	public UniformBatch(GL20 gl) {
		this(gl, new Source());
	}

	public UniformBatch(GL20 gl, Source source) {
		super(gl);
		this.source = source;
        this.init();   
	}

	public void init() {
		int maxVecs = usableMaxUniformVectors(gl);
		if (maxVecs < BASE_VEC4S_PER_QUAD){
			throw new RuntimeException(
					"GL_MAX_VERTEX_UNIFORM_VECTORS too low: have " + maxVecs
							+ ", need at least " + BASE_VEC4S_PER_QUAD);
		}
		maxQuads = maxVecs / BASE_VEC4S_PER_QUAD;
		program = new ShaderProgram.Mini(gl, source.vertex(this), source.fragment());
		uTexture = program.getUniformLocation("u_Texture");
		uHScreenSize = program.getUniformLocation("u_HScreenSize");
		uFlip = program.getUniformLocation("u_Flip");
		uData = program.getUniformLocation("u_Data");
		aVertex = program.getAttribLocation("a_Vertex");

		short[] verts = new short[maxQuads * VERTICES_PER_QUAD * VERTEX_SIZE];
		short[] elems = new short[maxQuads * ELEMENTS_PER_QUAD];
		int vv = 0, ee = 0;
		for (short ii = 0; ii < maxQuads; ii++) {
			verts[vv++] = 0;
			verts[vv++] = 0;
			verts[vv++] = ii;
			verts[vv++] = 1;
			verts[vv++] = 0;
			verts[vv++] = ii;
			verts[vv++] = 0;
			verts[vv++] = 1;
			verts[vv++] = ii;
			verts[vv++] = 1;
			verts[vv++] = 1;
			verts[vv++] = ii;
			short base = (short) (ii * VERTICES_PER_QUAD);
			short base0 = base, base1 = ++base, base2 = ++base, base3 = ++base;
			elems[ee++] = base0;
			elems[ee++] = base1;
			elems[ee++] = base2;
			elems[ee++] = base1;
			elems[ee++] = base3;
			elems[ee++] = base2;
		}

		data = new float[maxQuads * BASE_VEC4S_PER_QUAD * 4];

		int[] ids = new int[2];
		gl.glGenBuffers(2, ids, 0);
		verticesId = ids[0];
		elementsId = ids[1];

		gl.glBindBuffer(GL_ARRAY_BUFFER, verticesId);
		gl.bufs.setShortBuffer(verts, 0, verts.length);
		gl.glBufferData(GL_ARRAY_BUFFER, verts.length * 2, gl.bufs.shortBuffer,
				GL_STATIC_DRAW);

		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementsId);
		gl.bufs.setShortBuffer(elems, 0, elems.length);
		gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, elems.length * 2,
				gl.bufs.shortBuffer, GL_STATIC_DRAW);

	}

	@Override
	public void addQuad(int tint, float m00, float m01, float m10, float m11,
			float tx, float ty, float x1, float y1, float sx1, float sy1,
			float x2, float y2, float sx2, float sy2, float x3, float y3,
			float sx3, float sy3, float x4, float y4, float sx4, float sy4) {
		int pos = quadCounter * BASE_VEC4S_PER_QUAD * 4;
		float dw = x2 - x1, dh = y3 - y1;
		
		data[pos++] = m00 * dw;
		data[pos++] = m01 * dw;
		data[pos++] = m10 * dh;
		data[pos++] = m11 * dh;
		data[pos++] = tx + m00 * x1 + m10 * y1;
		data[pos++] = ty + m01 * x1 + m11 * y1;
		data[pos++] = sx1;
		data[pos++] = sy1;
		data[pos++] = sx2 - sx1;
		data[pos++] = sy3 - sy1;
		data[pos++] = (tint >> 16) & 0xFFFF;
		data[pos++] = tint & 0xFFFF;

		quadCounter++;

		if (quadCounter >= maxQuads) {
			flush();
		}
	}

	@Override
	public void begin(float fbufWidth, float fbufHeight, boolean flip) {
		super.begin(fbufWidth, fbufHeight, flip);
		program.activate();
		gl.glUniform2f(uHScreenSize, fbufWidth / 2f, fbufHeight / 2f);
		gl.glUniform1f(uFlip, flip ? -1 : 1);
		gl.glBindBuffer(GL_ARRAY_BUFFER, verticesId);
		gl.glEnableVertexAttribArray(aVertex);
		gl.glVertexAttribPointer(aVertex, VERTEX_SIZE, GL_SHORT, false, 0, 0);
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementsId);
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glUniform1i(uTexture, 0);
	}

	@Override
	public void flush() {
		super.flush();
		if (quadCounter > 0) {
			bindTexture();
			gl.glUniform4fv(uData, quadCounter * BASE_VEC4S_PER_QUAD, data, 0);
			gl.glDrawElements(GL_TRIANGLES, quadCounter * ELEMENTS_PER_QUAD,
					GL_UNSIGNED_SHORT, 0);
			quadCounter = 0;
		}
	}

	@Override
	public void end() {
		super.end();
		gl.glDisableVertexAttribArray(aVertex);
	}
	
	public void freeBuffer() {
		gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	private static int usableMaxUniformVectors(GL20 gl) {
		int maxVecs = gl.glGetInteger(GL_MAX_VERTEX_UNIFORM_VECTORS) - 3;
		return maxVecs;
	}

	@Override
	public String toString() {
		return "uquad/" + maxQuads;
	}

	@Override
	public void close() {
		super.close();
		program.close();
		gl.glDeleteBuffers(2, new int[] { verticesId, elementsId }, 0);
	}

}
