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
import loon.canvas.Canvas;
import loon.geom.Affine2f;
import loon.utils.MathUtils;

public class MeshBatch extends BaseBatch {

	private final MeshData meshData;

	private Mesh meshObject;

	private int _count = 0;

	private int _idx = 0;

	public void submit() {
		if (_idx == 0) {
			return;
		}
		try {
			bindTexture();
			meshData.texture = getCurrentTexture();
		} catch (Throwable ex) {
			LSystem.error("Batch submit() error", ex);
		}
	}

	public MeshBatch(Canvas gl) {
		this(gl, 256);
	}

	public MeshBatch(Canvas gl, int maxSize) {
		super(gl);
		this.meshData = new MeshData();
		this.init();
	}

	@Override
	public void init() {
		meshObject = LSystem.base().makeMesh(gl);
		meshData.blend = -1;
	}

	private float ubufWidth = 0;

	private float ubufHeight = 0;

	private boolean uflip = true;

	@Override
	public void begin(float fbufWidth, float fbufHeight, boolean flip) {
		if (this.ubufWidth != fbufWidth || this.ubufHeight != fbufHeight || this.uflip != flip) {
			this.ubufWidth = fbufWidth;
			this.ubufHeight = fbufHeight;
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
			}
		}
	}

	@Override
	public void flush() {
		super.flush();
		if (_idx > 0) {
			submit();
		}
	}

	@Override
	public void end() {
		super.end();
		this.meshData.blend = -1;
	}

	@Override
	public void close() {
		super.close();
		this.meshData.blend = -1;
	}

	@Override
	public BaseBatch setBlendMode(int b) {
		this.meshData.blend = b;
		return this;
	}

	@Override
	public int getBlendMode() {
		return meshData.blend;
	}

	@Override
	public String toString() {
		return "tris/" + _count;
	}

	@Override
	public void addQuad(int tint, float m00, float m01, float m10, float m11, float tx, float ty, float left, float top,
			float right, float bottom, float sl, float st, float sr, float sb) {
		if (meshData.blend != -1) {
			gl.setBlendMethod(meshData.blend);
		}
		meshData.texture = getCurrentTexture();
		meshObject.setMesh(meshData);
		meshObject.paint(tint, m00, m01, m10, m11, tx, ty, left, top, right, bottom, sl, st, sr, sb);
		_count++;
	}

}
