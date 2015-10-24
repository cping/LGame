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

import loon.LTexture;
import loon.geom.Affine2f;
import loon.opengl.ShaderProgram.Mini;
import static loon.opengl.GL20.*;

public class TrilateralBatch extends BaseBatch {

	protected static final int[] QUAD_INDICES = { 0, 1, 2, 1, 3, 2 };
	
	public static class Source extends LTextureBind.Source {

		public static final String VERT_UNIFS = "uniform vec2 u_HScreenSize;\n"
				+ "uniform float u_Flip;\n";

		public static final String VERT_ATTRS = "attribute vec4 a_Matrix;\n"
				+ "attribute vec2 a_Translation;\n"
				+ "attribute vec2 a_Color;\n";

		public static final String PER_VERT_ATTRS = "attribute vec2 a_Position;\n"
				+ "attribute vec2 a_TexCoord;\n";

		public static final String VERT_VARS = "varying vec2 v_TexCoord;\n"
				+ "varying vec4 v_Color;\n";

		public static final String VERT_SETPOS =

		"mat3 transform = mat3(\n"
				+ "  a_Matrix[0],      a_Matrix[1],      0,\n"
				+ "  a_Matrix[2],      a_Matrix[3],      0,\n"
				+ "  a_Translation[0], a_Translation[1], 1);\n"
				+ "gl_Position = vec4(transform * vec3(a_Position, 1.0), 1);\n"
				+
				"gl_Position.xy /= u_HScreenSize.xy;\n" +
				"gl_Position.xy -= 1.0;\n" +
				"gl_Position.y *= u_Flip;\n";

		public static final String VERT_SETTEX = "v_TexCoord = a_TexCoord;\n";

		public static final String VERT_SETCOLOR =
		"float red = mod(a_Color.x, 256.0);\n"
				+ "float alpha = (a_Color.x - red) / 256.0;\n"
				+ "float blue = mod(a_Color.y, 256.0);\n"
				+ "float green = (a_Color.y - blue) / 256.0;\n"
				+ "v_Color = vec4(red / 255.0, green / 255.0, blue / 255.0, alpha / 255.0);\n";

		public String vertex() {
			return (VERT_UNIFS + VERT_ATTRS + PER_VERT_ATTRS + VERT_VARS
					+ "void main(void) {\n" + VERT_SETPOS + VERT_SETTEX
					+ VERT_SETCOLOR + "}");
		}
	}

	private static final int START_VERTS = 16 * 4;
	private static final int EXPAND_VERTS = 16 * 4;
	private static final int START_ELEMS = 6 * START_VERTS / 4;
	private static final int EXPAND_ELEMS = 6 * EXPAND_VERTS / 4;
	private static final int FLOAT_SIZE_BYTES = 4;

	private boolean delayedBinding;

	protected Mini program;
	protected int uTexture;
	protected int uHScreenSize;
	protected int uFlip;
	protected int aMatrix, aTranslation, aColor; 
	protected int aPosition, aTexCoord;

	protected int verticesId, elementsId;
	protected float[] stableAttrs;
	protected float[] vertices;
	protected short[] elements;
	protected int vertPos, elemPos;

	private Source source;

	public TrilateralBatch(GL20 gl) {
		this(gl, new Source());
	}

	public TrilateralBatch(GL20 gl, Source source) {
		super(gl);
		this.source = source;
		this.init();
	}

	public void init() {
		delayedBinding = "Intel".equals(gl.glGetString(GL20.GL_VENDOR));

		program = new ShaderProgram.Mini(gl, source.vertex(), source.fragment());
		uTexture = program.getUniformLocation("u_Texture");
		uHScreenSize = program.getUniformLocation("u_HScreenSize");
		uFlip = program.getUniformLocation("u_Flip");
		aMatrix = program.getAttribLocation("a_Matrix");
		aTranslation = program.getAttribLocation("a_Translation");
		aColor = program.getAttribLocation("a_Color");
		aPosition = program.getAttribLocation("a_Position");
		aTexCoord = program.getAttribLocation("a_TexCoord");

		stableAttrs = new float[stableAttrsSize()];
		vertices = new float[START_VERTS * vertexSize()];
		elements = new short[START_ELEMS];

		int[] ids = new int[2];
		gl.glGenBuffers(2, ids, 0);
		verticesId = ids[0];
		elementsId = ids[1];
	}

	public void prepare(int tint, Affine2f xf) {
		prepare(tint, xf.m00, xf.m01, xf.m10, xf.m11, xf.tx, xf.ty);
	}

	public void prepare(int tint, float m00, float m01, float m10, float m11,
			float tx, float ty) {
		float[] stables = stableAttrs;
		stables[0] = m00;
		stables[1] = m01;
		stables[2] = m10;
		stables[3] = m11;
		stables[4] = tx;
		stables[5] = ty;
		stables[6] = (tint >> 16) & 0xFFFF; // ar
		stables[7] = (tint >> 0) & 0xFFFF; // gb
	}

	public void addTris(LTexture tex, int tint, Affine2f xf,
			float[] xys, int xysOffset, int xysLen, float tw, float th,
			int[] indices, int indicesOffset, int indicesLen, int indexBase) {
		setTexture(tex);
		prepare(tint, xf);
		addTris(xys, xysOffset, xysLen, tw, th, indices, indicesOffset,
				indicesLen, indexBase);
	}


	public void addTris(LTexture tex, int tint, Affine2f xf,
			float[] xys, float[] sxys, int xysOffset, int xysLen,
			int[] indices, int indicesOffset, int indicesLen, int indexBase) {
		setTexture(tex);
		prepare(tint, xf);
		addTris(xys, sxys, xysOffset, xysLen, indices, indicesOffset,
				indicesLen, indexBase);
	}

	public void addTris(float[] xys, int xysOffset, int xysLen, float tw,
			float th, int[] indices, int indicesOffset, int indicesLen,
			int indexBase) {
		int vertIdx = beginPrimitive(xysLen / 2, indicesLen), offset = vertPos;
		float[] verts = vertices, stables = stableAttrs;
		for (int ii = xysOffset, ll = ii + xysLen; ii < ll; ii += 2) {
			float x = xys[ii], y = xys[ii + 1];
			offset = add(verts, add(verts, offset, stables), x, y, x / tw, y
					/ th);
		}
		vertPos = offset;

		addElems(vertIdx, indices, indicesOffset, indicesLen, indexBase);
	}

	public void addTris(float[] xys, float[] sxys, int xysOffset, int xysLen,
			int[] indices, int indicesOffset, int indicesLen, int indexBase) {
		int vertIdx = beginPrimitive(xysLen / 2, indicesLen), offset = vertPos;
		float[] verts = vertices, stables = stableAttrs;
		for (int ii = xysOffset, ll = ii + xysLen; ii < ll; ii += 2) {
			offset = add(verts, add(verts, offset, stables), xys[ii],
					xys[ii + 1], sxys[ii], sxys[ii + 1]);
		}
		vertPos = offset;

		addElems(vertIdx, indices, indicesOffset, indicesLen, indexBase);
	}

	@Override
	public void addQuad(int tint, float m00, float m01, float m10, float m11,
			float tx, float ty, float x1, float y1, float sx1, float sy1,
			float x2, float y2, float sx2, float sy2, float x3, float y3,
			float sx3, float sy3, float x4, float y4, float sx4, float sy4) {
		prepare(tint, m00, m01, m10, m11, tx, ty);

		int vertIdx = beginPrimitive(4, 6);
		int offset = vertPos;
		float[] verts = vertices, stables = stableAttrs;
		offset = add(verts, add(verts, offset, stables), x1, y1, sx1, sy1);
		offset = add(verts, add(verts, offset, stables), x2, y2, sx2, sy2);
		offset = add(verts, add(verts, offset, stables), x3, y3, sx3, sy3);
		offset = add(verts, add(verts, offset, stables), x4, y4, sx4, sy4);
		vertPos = offset;

		addElems(vertIdx, QUAD_INDICES, 0, QUAD_INDICES.length, 0);
	}

	@Override
	public void begin(float fbufWidth, float fbufHeight, boolean flip) {
		super.begin(fbufWidth, fbufHeight, flip);
		program.activate();
		gl.glUniform2f(uHScreenSize, fbufWidth / 2f, fbufHeight / 2f);
		gl.glUniform1f(uFlip, flip ? -1 : 1);
		if (!delayedBinding) {
			bindAttribsBufs();
		}
	}

	private void bindAttribsBufs() {
		gl.glBindBuffer(GL_ARRAY_BUFFER, verticesId);
		int stride = vertexStride();
		glBindVertAttrib(aMatrix, 4, GL_FLOAT, stride, 0);
		glBindVertAttrib(aTranslation, 2, GL_FLOAT, stride, 16);
		glBindVertAttrib(aColor, 2, GL_FLOAT, stride, 24);
		int offset = stableAttrsSize() * FLOAT_SIZE_BYTES;
		glBindVertAttrib(aPosition, 2, GL_FLOAT, stride, offset);
		glBindVertAttrib(aTexCoord, 2, GL_FLOAT, stride, offset + 8);
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementsId);
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glUniform1i(uTexture, 0);

	}

	@Override
	public void flush() {
		super.flush();
		if (vertPos > 0) {
			bindTexture();
			if (delayedBinding) {
				bindAttribsBufs();
			}
			gl.bufs.setFloatBuffer(vertices, 0, vertPos);
			gl.glBufferData(GL_ARRAY_BUFFER, vertPos * 4, gl.bufs.floatBuffer,
					GL_STREAM_DRAW);
			gl.bufs.setShortBuffer(elements, 0, elemPos);
			gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, elemPos * 2,
					gl.bufs.shortBuffer, GL_STREAM_DRAW);
			gl.glDrawElements(GL_TRIANGLES, elemPos, GL_UNSIGNED_SHORT, 0);
			vertPos = 0;
			elemPos = 0;
		}
	}

	@Override
	public void end() {
		super.end();
		gl.glDisableVertexAttribArray(aMatrix);
		gl.glDisableVertexAttribArray(aTranslation);
		gl.glDisableVertexAttribArray(aColor);
		gl.glDisableVertexAttribArray(aPosition);
		gl.glDisableVertexAttribArray(aTexCoord);
	}

	public void freeBuffer() {
		gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	@Override
	public void close() {
		super.close();
		program.close();
		gl.glDeleteBuffers(2, new int[] { verticesId, elementsId }, 0);
	}

	@Override
	public String toString() {
		return "tris/" + (elements.length / QUAD_INDICES.length);
	}

	protected int stableAttrsSize() {
		return 8;
	}

	protected int vertexSize() {
		return stableAttrsSize() + 4;
	}

	protected int vertexStride() {
		return vertexSize() * FLOAT_SIZE_BYTES;
	}

	protected int beginPrimitive(int vertexCount, int elemCount) {
		int vertIdx = vertPos / vertexSize();
		int verts = vertIdx + vertexCount, elems = elemPos + elemCount;
		int availVerts = vertices.length / vertexSize(), availElems = elements.length;
		if (verts <= availVerts && elems <= availElems) {
			return vertIdx;
		}
		flush();
		if (verts > availVerts) {
			expandVerts(verts);
		}
		if (elems > availElems) {
			expandElems(elems);
		}
		return 0;
	}

	protected final void glBindVertAttrib(int loc, int size, int type,
			int stride, int offset) {
		gl.glEnableVertexAttribArray(loc);
		gl.glVertexAttribPointer(loc, size, type, false, stride, offset);
	}

	protected final void addElems(int vertIdx, int[] indices,
			int indicesOffset, int indicesLen, int indexBase) {
		short[] data = elements;
		int offset = elemPos;
		for (int ii = indicesOffset, ll = ii + indicesLen; ii < ll; ii++) {
			data[offset++] = (short) (vertIdx + indices[ii] - indexBase);
		}
		elemPos = offset;
	}

	private final void expandVerts(int vertCount) {
		int newVerts = vertices.length / vertexSize();
		while (newVerts < vertCount) {
			newVerts += EXPAND_VERTS;
		}
		vertices = new float[newVerts * vertexSize()];
	}

	private final void expandElems(int elemCount) {
		int newElems = elements.length;
		while (newElems < elemCount) {
			newElems += EXPAND_ELEMS;
		}
		elements = new short[newElems];
	}

	protected static int add(float[] into, int offset, float[] stables) {
		System.arraycopy(stables, 0, into, offset, stables.length);
		return offset + stables.length;
	}

	protected static int add(float[] into, int offset, float[] stables,
			int soff, int slen) {
		System.arraycopy(stables, soff, into, offset, slen);
		return offset + slen;
	}

	protected static int add(float[] into, int offset, float x, float y,
			float sx, float sy) {
		into[offset++] = x;
		into[offset++] = y;
		into[offset++] = sx;
		into[offset++] = sy;
		return offset;
	}

}
