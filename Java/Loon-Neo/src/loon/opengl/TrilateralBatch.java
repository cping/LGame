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

import loon.canvas.LColor;
import loon.geom.Affine2f;
import loon.geom.Matrix4;
import loon.utils.GLUtils;
import loon.utils.MathUtils;
import loon.LSystem;

public class TrilateralBatch extends BaseBatch {

	public final static GlobalSource DEF_SOURCE = new GlobalSource();

	private final static String _batch_name = "trilbatch";

	private final Matrix4 viewMatrix;

	private final ExpandVertices expandVertices;

	private int idx = 0;

	private final LColor tmpColor = new LColor();

	private ShaderProgram shader;

	private int maxSpritesInBatch = 0;

	private boolean isLoaded;

	private boolean lockSubmit = false;

	private MeshDefault mesh;

	public int getSize() {
		return expandVertices.getSize();
	}

	public void setShaderUniformf(String name, LColor color) {
		if (shader != null) {
			shader.setUniformf(name, color);
		}
	}

	public void setShaderUniformf(int name, LColor color) {
		if (shader != null) {
			shader.setUniformf(name, color);
		}
	}

	public boolean isLockSubmit() {
		return lockSubmit;
	}

	public void setLockSubmit(boolean lockSubmit) {
		this.lockSubmit = lockSubmit;
	}

	public void submit() {
		if (idx == 0) {
			return;
		}
		try {
			int spritesInBatch = idx / 20;
			if (spritesInBatch > maxSpritesInBatch) {
				maxSpritesInBatch = spritesInBatch;
			}
			int count = spritesInBatch * 6;
			bindTexture();
			GL20 gl = LSystem.base().graphics().gl;
			int tmp = GLUtils.getBlendMode();
			if (tmpColor.a >= 0.98f) {
				GLUtils.setBlendMode(gl, LSystem.MODE_NORMAL);
			} else {
				GLUtils.setBlendMode(gl, LSystem.MODE_SPEED);
			}
			mesh.post(_batch_name, expandVertices.getSize(), shader, expandVertices.getVertices(), idx, count);
			GLUtils.setBlendMode(gl, tmp);
		} catch (Throwable ex) {
			LSystem.error(ex.getMessage(), ex);
		} finally {
			if (expandVertices.expand(this.idx)) {
				mesh.reset(_batch_name, expandVertices.length());
			}
			if (!lockSubmit) {
				idx = 0;
			}
		}
	}

	private void setupMatrices() {
		if (shader != null) {
			shader.setUniformMatrix("u_projTrans", viewMatrix);
			shader.setUniformi("u_texture", 0);
			_shader_source.setupShader(shader);
		}
	}

	public TrilateralBatch(GL20 gl) {
		this(gl, DEF_SOURCE);
	}

	public TrilateralBatch(GL20 gl, ShaderSource src) {
		this(gl, 512, src);
	}

	public TrilateralBatch(GL20 gl, int maxSize, ShaderSource src) {
		super(gl);
		this.expandVertices = new ExpandVertices(maxSize);
		this._shader_source = src;
		this.viewMatrix = new Matrix4();
		this.init();
	}

	@Override
	public void init() {
		this.mesh = new MeshDefault();
	}

	protected float addX(float m00, float m01, float m10, float m11, float x, float y, float sx, float sy, float tx,
			float ty) {
		return m00 * x + m10 * y + tx;
	}

	protected float addY(float m00, float m01, float m10, float m11, float x, float y, float sx, float sy, float tx,
			float ty) {
		return m01 * x + m11 * y + ty;
	}

	@Override
	public void addQuad(int tint, float m00, float m01, float m10, float m11, float tx, float ty, float x1, float y1,
			float sx1, float sy1, float x2, float y2, float sx2, float sy2, float x3, float y3, float sx3, float sy3,
			float x4, float y4, float sx4, float sy4) {

		if (lockSubmit) {
			return;
		}

		float colorFloat = tmpColor.setColor(tint).toFloatBits();

		int index = this.idx;

		expandVertices.setVertice(index++, addX(m00, m01, m10, m11, x1, y1, sx1, sy1, tx, ty));
		expandVertices.setVertice(index++, addY(m00, m01, m10, m11, x1, y1, sx1, sy1, tx, ty));
		expandVertices.setVertice(index++, colorFloat);
		expandVertices.setVertice(index++, sx1);
		expandVertices.setVertice(index++, sy1);

		expandVertices.setVertice(index++, addX(m00, m01, m10, m11, x2, y2, sx2, sy2, tx, ty));
		expandVertices.setVertice(index++, addY(m00, m01, m10, m11, x2, y2, sx2, sy2, tx, ty));
		expandVertices.setVertice(index++, colorFloat);
		expandVertices.setVertice(index++, sx2);
		expandVertices.setVertice(index++, sy2);

		expandVertices.setVertice(index++, addX(m00, m01, m10, m11, x4, y4, sx4, sy4, tx, ty));
		expandVertices.setVertice(index++, addY(m00, m01, m10, m11, x4, y4, sx4, sy4, tx, ty));
		expandVertices.setVertice(index++, colorFloat);
		expandVertices.setVertice(index++, sx4);
		expandVertices.setVertice(index++, sy4);

		expandVertices.setVertice(index++, addX(m00, m01, m10, m11, x3, y3, sx3, sy3, tx, ty));
		expandVertices.setVertice(index++, addY(m00, m01, m10, m11, x3, y3, sx3, sy3, tx, ty));
		expandVertices.setVertice(index++, colorFloat);
		expandVertices.setVertice(index++, sx3);
		expandVertices.setVertice(index++, sy3);

		this.idx = index;

		if (lastTexId != curTexId) {
			flush();
		}
	}

	private float ubufWidth = 0;

	private float ubufHeight = 0;

	private boolean uflip = true;

	@Override
	public void begin(float fbufWidth, float fbufHeight, boolean flip) {
		if (this.ubufWidth != fbufWidth || this.ubufHeight != fbufHeight || this.uflip != flip) {
			this.ubufWidth = fbufWidth;
			this.ubufHeight = fbufHeight;
			this.viewMatrix.setToOrtho2D(0, 0, ubufWidth, ubufHeight);
			this.uflip = flip;
			if (!flip) {
				Affine2f a2f = new Affine2f();
				float w = ubufWidth / 2;
				float h = ubufHeight / 2;
				a2f.translate(w, h);
				a2f.scale(-1, 1);
				a2f.translate(-w, -h);
				a2f.translate(w, h);
				a2f.rotate(MathUtils.PI);
				a2f.translate(-w, -h);
				this.viewMatrix.mul(a2f);
			}
		}
		if (!isLoaded || isShaderDirty()) {
			if (shader == null || isShaderDirty()) {
				if (shader != null) {
					shader.close();
					shader = null;
				}
				shader = LSystem.createShader(_shader_source.vertexShader(), _shader_source.fragmentShader());
				setShaderDirty(false);
			}
			isLoaded = true;
		}
		shader.begin();
		setupMatrices();
	}

	@Override
	public void flush() {
		super.flush();
		if (idx > 0) {
			submit();
		}
		shader.end();
	}

	protected int vertexSize() {
		return expandVertices.vertexSize();
	}

	@Override
	public void end() {
		super.end();
	}

	@Override
	public void close() {
		super.close();
		if (shader != null) {
			shader.close();
		}
	}

	@Override
	public String toString() {
		return "tris/" + expandVertices.length();
	}

}
