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
package loon;

import loon.canvas.Canvas;
import loon.font.TextFormat;
import loon.font.TextLayout;
import loon.font.TextWrap;
import loon.geom.Affine2f;
import loon.geom.Dimension;
import loon.geom.Matrix4;
import loon.opengl.GL20;
import loon.opengl.RenderTarget;
import loon.utils.Array;
import loon.utils.GLUtils;
import loon.utils.Scale;
import loon.utils.reply.UnitPort;
import static loon.opengl.GL20.*;

public abstract class Graphics {

	protected final LGame game;
	protected final Dimension viewSizeM = new Dimension();
	protected Scale scale;
	protected int viewPixelWidth, viewPixelHeight;

	// 创建一个半永久的纹理，用以批量进行颜色渲染
	private static LTexture colorTex;

	// 用以提供GL渲染服务
	public final GL20 gl;

	public RenderTarget defaultRenderTarget = new RenderTarget(this) {
		public int id() {
			return defaultFramebuffer();
		}

		public int width() {
			return viewPixelWidth;
		}

		public int height() {
			return viewPixelHeight;
		}

		public float xscale() {
			return scale.factor;
		}

		public float yscale() {
			return scale.factor;
		}

		public boolean flip() {
			return true;
		}

		public void close() {
		}
	};

	private static Array<Matrix4> matrixsStack = new Array<Matrix4>();

	private Matrix4 transformMatrix;

	private Matrix4 projectionMatrix;

	public void setTransformMatrix(Matrix4 t) {
		this.transformMatrix = t;
	}

	public void setProjectionMatrix(Matrix4 t) {
		this.projectionMatrix = t;
	}

	public Matrix4 getTransformMatrix() {
		if (transformMatrix == null) {
			transformMatrix = new Matrix4();
		}
		return transformMatrix;
	}

	private Affine2f lastAffine = null;

	public Matrix4 getProjectionMatrix() {
		if (projectionMatrix == null) {
			matrixsStack.add(projectionMatrix = new Matrix4());
			projectionMatrix.setToOrtho2D(0, 0, LSystem.viewSize.width,
					LSystem.viewSize.height);
		} else if (LSystem.base().display() != null
				&& !LSystem.base().display().GL().tx().equals(lastAffine)) {
			lastAffine = LSystem.base().display().GL().tx().cpy();
			LSystem.viewSize.getMatrix().mul(projectionMatrix);
			projectionMatrix = projectionMatrix.newCombine(lastAffine);
		}
		return projectionMatrix;
	}

	public void save() {
		if (projectionMatrix == null) {
			return;
		}
		matrixsStack.add(projectionMatrix = projectionMatrix.cpy());
	}

	public void restore() {
		projectionMatrix = matrixsStack.pop();
	}

	/**
	 * 返回一个缩放比例，用以让当前设备按照此比例进行资源缩放
	 * 
	 * @return
	 */
	public Scale scale() {
		return scale;
	}

	public abstract Dimension screenSize();

	public Canvas createCanvas(float width, float height) {
		return createCanvasImpl(scale, scale.scaledCeil(width),
				scale.scaledCeil(height));
	}

	public Canvas createCanvas(Dimension size) {
		return createCanvas(size.width, size.height);
	}

	public LTexture createTexture(float width, float height,
			LTexture.Format config) {
		int texWidth = config.toTexWidth(scale.scaledCeil(width));
		int texHeight = config.toTexHeight(scale.scaledCeil(height));
		if (texWidth <= 0 || texHeight <= 0) {
			throw new IllegalArgumentException("Invalid texture size: "
					+ texWidth + "x" + texHeight);
		}
		int id = createTexture(config);
		gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texWidth, texHeight, 0,
				GL_RGBA, GL_UNSIGNED_BYTE, null);
		return LTextures.loadTexture(new LTexture(this, id, config, texWidth,
				texHeight, scale, width, height));
	}

	public LTexture createTexture(Dimension size, LTexture.Format config) {
		return createTexture(size.width, size.height, config);
	}

	public abstract TextLayout layoutText(String text, TextFormat format);

	public abstract TextLayout[] layoutText(String text, TextFormat format,
			TextWrap wrap);

	public void queueForDispose(final LRelease resource) {
		game.frame.connect(new UnitPort() {
			public void onEmit() {
				resource.close();
			}
		}).once();
	}

	public LTexture finalColorTex() {
		if (colorTex == null) {
			Canvas canvas = createCanvas(1, 1);
			canvas.setFillColor(0xFFFFFFFF).fillRect(0, 0, canvas.width,
					canvas.height);
			colorTex = canvas.toTexture(LTexture.Format.NEAREST);
		}
		return colorTex;
	}

	protected Graphics(LGame game, GL20 gl, Scale scale) {
		this.game = game;
		this.gl = gl;
		this.scale = scale;
	}

	protected int defaultFramebuffer() {
		return 0;
	}

	protected abstract Canvas createCanvasImpl(Scale scale, int pixelWidth,
			int pixelHeight);

	protected void viewportChanged(Scale scale, int viewWidth, int viewHeight) {
		if (!LSystem.LOCK_SCREEN) {
			LSystem.viewSize.setSize(viewWidth, viewHeight);
			this.scale = scale;
			this.viewPixelWidth = viewWidth;
			this.viewPixelHeight = viewHeight;
			this.viewSizeM.width = scale.invScaled(viewWidth);
			this.viewSizeM.height = scale.invScaled(viewHeight);
			Display d = game.display();
			if (d != null) {
				d.resize(scale, viewWidth, viewHeight);
			}
		}
	}

	public int createTexture(LTexture.Format config) {
		int id = gl.glGenTexture();
		GLUtils.bindTexture(gl, id);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER,
				config.magFilter);
		int minFilter = mipmapify(config.minFilter, config.mipmaps);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,
				config.repeatX ? GL_REPEAT : GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T,
				config.repeatY ? GL_REPEAT : GL_CLAMP_TO_EDGE);
		return id;
	}

	protected static int mipmapify(int filter, boolean mipmaps) {
		if (!mipmaps) {
			return filter;
		}
		switch (filter) {
		case GL_NEAREST:
			return GL_NEAREST_MIPMAP_NEAREST;
		case GL_LINEAR:
			return GL_LINEAR_MIPMAP_NEAREST;
		default:
			return filter;
		}
	}
}
