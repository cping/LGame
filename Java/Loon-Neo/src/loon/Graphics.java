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
	protected Scale scale = null;
	protected int viewPixelWidth, viewPixelHeight;

	private Display display = null;
	private Affine2f affine = null, lastAffine = null;
	private Matrix4 viewMatrix = null;
	private static Array<Matrix4> matrixsStack = new Array<Matrix4>();
	// 创建一个半永久的纹理，用以批量进行颜色渲染
	private static LTexture colorTex;

	// 用以提供GL渲染服务
	public final GL20 gl;

	private class DefaultRender extends RenderTarget {

		private final Graphics _graphics;

		public DefaultRender(Graphics gfx) {
			super(gfx);
			_graphics = gfx;
		}

		public int id() {
			return _graphics.defaultFramebuffer();
		}

		public int width() {
			return _graphics.viewPixelWidth;
		}

		public int height() {
			return _graphics.viewPixelHeight;
		}

		public float xscale() {
			return _graphics.game.setting.scaling() ? LSystem.getScaleWidth() : _graphics.scale.factor;
		}

		public float yscale() {
			return _graphics.game.setting.scaling() ? LSystem.getScaleHeight() : _graphics.scale.factor;
		}

		public boolean flip() {
			return true;
		}

	}

	public RenderTarget defaultRenderTarget = new DefaultRender(this);

	/**
	 * 返回一个缩放比例，用以让当前设备加载的资源按照此比例进行资源缩放
	 * 
	 * @return
	 */
	public Scale scale() {
		return scale;
	}

	public Matrix4 getViewMatrix() {
		display = game.display();
		Dimension view = LSystem.viewSize;
		if (viewMatrix == null) {
			viewMatrix = new Matrix4();
			viewMatrix.setToOrtho2D(0, 0, view.getWidth(), view.getHeight());
		} else if (display != null && display.GL() != null && !(affine = display.GL().tx()).equals(lastAffine)) {
			viewMatrix = affine.toViewMatrix4();
			lastAffine = affine;
		}
		return viewMatrix;
	}

	public void save() {
		if (viewMatrix != null) {
			matrixsStack.add(viewMatrix = viewMatrix.cpy());
		}
	}

	public void restore() {
		viewMatrix = matrixsStack.pop();
	}

	public abstract Dimension screenSize();

	public Canvas createCanvas(float width, float height) {
		return createCanvasImpl(scale, scale.scaledCeil(width), scale.scaledCeil(height));
	}

	public Canvas createCanvas(Dimension size) {
		return createCanvas(size.width, size.height);
	}

	public LTexture createTexture(float width, float height, LTexture.Format config) {
		int texWidth = config.toTexWidth(scale.scaledCeil(width));
		int texHeight = config.toTexHeight(scale.scaledCeil(height));
		if (texWidth <= 0 || texHeight <= 0) {
			throw new IllegalArgumentException("Invalid texture size: " + texWidth + "x" + texHeight);
		}
		int id = createTexture(config);
		gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texWidth, texHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
		return new LTexture(this, id, config, texWidth, texHeight, scale, width, height);
	}

	public LTexture createTexture(Dimension size, LTexture.Format config) {
		return createTexture(size.width, size.height, config);
	}

	public abstract TextLayout layoutText(String text, TextFormat format);

	public abstract TextLayout[] layoutText(String text, TextFormat format, TextWrap wrap);

	private class DisposePort extends UnitPort {

		private final LRelease _release;

		DisposePort(LRelease r) {
			this._release = r;
		}

		@Override
		public void onEmit() {
			_release.close();
		}

	}

	public void queueForDispose(final LRelease resource) {
		game.frame.connect(new DisposePort(resource)).once();
	}

	public LTexture finalColorTex() {
		if (colorTex == null) {
			Canvas canvas = createCanvas(1, 1);
			canvas.setFillColor(0xFFFFFFFF).fillRect(0, 0, canvas.width, canvas.height);
			colorTex = canvas.toTexture(LTexture.Format.NEAREST);
			colorTex.setDisabledTexture(true);
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

	protected abstract Canvas createCanvasImpl(Scale scale, int pixelWidth, int pixelHeight);

	protected void viewportChanged(Scale scale, int viewWidth, int viewHeight) {
		Display d = game.display();
		LSystem.viewSize.setSize((int) (viewWidth / LSystem.getScaleWidth()),
				(int) (viewHeight / LSystem.getScaleHeight()));
		if (viewMatrix != null) {
			LSystem.viewSize.getMatrix().mul(viewMatrix);
		}
		this.scale = scale;
		this.viewPixelWidth = viewWidth;
		this.viewPixelHeight = viewHeight;
		this.viewSizeM.width = game.setting.scaling() ? LSystem.invXScaled(viewPixelWidth)
				: scale.invScaled(viewPixelWidth);
		this.viewSizeM.height = game.setting.scaling() ? LSystem.invXScaled(viewPixelHeight)
				: scale.invScaled(viewPixelHeight);
		if (d != null) {
			d.resize(LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
		}
	}

	protected boolean isAllowResize(int viewWidth, int viewHeight) {
		if (game.setting.isCheckReisze) {
			Dimension size = this.screenSize();
			if (size == null || size.width <= 0 || size.height <= 0) {
				return true;
			}
			if (game.setting.landscape() && viewWidth > viewHeight) {
				return true;
			} else if (viewWidth < viewHeight) {
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

	public int createTexture(LTexture.Format config) {
		return createTexture(config, 0);
	}

	public int createTexture(LTexture.Format config, int count) {
		int id = gl.glGenTexture() + count;
		if (LTextures.contains(id)) {
			return createTexture(config, 1);
		}
		if (GLUtils.getCurrentHardwareTextureID() == id) {
			return createTexture(config, 1);
		}
		GLUtils.bindTexture(gl, id);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, config.magFilter);
		int minFilter = mipmapify(config.minFilter, config.mipmaps);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, config.repeatX ? GL_REPEAT : GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, config.repeatY ? GL_REPEAT : GL_CLAMP_TO_EDGE);
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
