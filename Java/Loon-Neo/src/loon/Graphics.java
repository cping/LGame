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
import loon.geom.FloatValue;
import loon.geom.Matrix4;
import loon.opengl.GL20;
import loon.opengl.GLFrameBuffer;
import loon.opengl.RenderTarget;
import loon.utils.Array;
import loon.utils.GLUtils;
import loon.utils.MathUtils;
import loon.utils.Scale;
import loon.utils.reply.UnitPort;
import static loon.opengl.GL20.*;

public abstract class Graphics {

	protected final LGame game;

	protected final Dimension viewSizeM = new Dimension();

	protected final FloatValue viewDPISacle = new FloatValue(1f);

	protected Scale scale = null;

	protected int viewPixelWidth, viewPixelHeight;

	protected int lastViewWidth, lastViewHeight;

	private Display display = null;
	private Affine2f affine = null, lastAffine = null;
	private Matrix4 viewMatrix = null;
	private final Array<Matrix4> matrixsStack = new Array<Matrix4>();
	// 创建一个半永久的纹理，用以批量进行颜色渲染
	private LTexture colorTex;

	// 用以提供GL渲染服务
	public final GL20 gl;

	private static final class DefaultRender extends RenderTarget {

		private final Graphics _graphics;

		public DefaultRender(Graphics gfx) {
			super(gfx, null);
			_graphics = gfx;
		}

		@Override
		public int id() {
			return _graphics.defaultFramebuffer();
		}

		@Override
		public int width() {
			return _graphics.viewPixelWidth;
		}

		@Override
		public int height() {
			return _graphics.viewPixelHeight;
		}

		@Override
		public float xscale() {
			return _graphics.game.setting.scaling() ? LSystem.getScaleWidth() : _graphics.scale.factor;
		}

		@Override
		public float yscale() {
			return _graphics.game.setting.scaling() ? LSystem.getScaleHeight() : _graphics.scale.factor;
		}

		@Override
		public boolean flip() {
			return true;
		}

		@Override
		public LTexture texture() {
			return null;
		}

	}

	protected final RenderTarget defaultRenderTarget;

	/**
	 * 返回一个缩放比例，用以让当前设备加载的资源按照此比例进行资源缩放
	 * 
	 * @return
	 */
	public Scale scale() {
		return scale;
	}

	public Graphics setDPIScale(float v) {
		viewDPISacle.set(v);
		return this;
	}

	public float onDPI(float v) {
		return viewDPISacle.scaled(v);
	}

	public int width() {
		return screenSize().getWidth();
	}

	public int height() {
		return screenSize().getHeight();
	}

	public boolean isHidden() {
		return width() < 2 || height() < 2;
	}

	public final Matrix4 getViewMatrix() {
		display = game.display();
		final Dimension view = LSystem.viewSize;
		if (viewMatrix == null) {
			viewMatrix = new Matrix4();
			viewMatrix.setToOrtho2D(0, 0, view.getWidth(), view.getHeight());
		} else if (display != null && display.GL() != null && !(affine = display.GL().tx()).equals(lastAffine)) {
			viewMatrix = affine.toViewMatrix4();
			lastAffine = affine;
		}
		return viewMatrix;
	}

	public final Graphics save() {
		if (viewMatrix != null) {
			matrixsStack.add(viewMatrix = viewMatrix.cpy());
		}
		return this;
	}

	public final Graphics restore() {
		viewMatrix = matrixsStack.pop();
		return this;
	}

	public abstract Dimension screenSize();

	public final Canvas createCanvas(final float width, final float height) {
		return createCanvasImpl(scale, scale.scaledCeil(width), scale.scaledCeil(height));
	}

	public final Canvas createCanvas(final Dimension size) {
		return createCanvas(size.width, size.height);
	}

	public final LTexture createTexture(final float width, final float height, final LTexture.Format config) {
		int texWidth = config.toTexWidth(scale.scaledCeil(width));
		int texHeight = config.toTexHeight(scale.scaledCeil(height));
		if (texWidth <= 0 || texHeight <= 0) {
			throw new LSysException("Invalid texture size: " + texWidth + "x" + texHeight);
		}
		int id = createTexture(config);
		gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texWidth, texHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
		return new LTexture(this, id, config, texWidth, texHeight, scale, width, height);
	}

	public final LTexture createTexture(final Dimension size, final LTexture.Format config) {
		return createTexture(size.width, size.height, config);
	}

	public abstract TextLayout layoutText(String text, TextFormat format);

	public abstract TextLayout[] layoutText(String text, TextFormat format, TextWrap wrap);

	private final static class DisposePort extends UnitPort {

		private final LRelease _release;

		DisposePort(final LRelease r) {
			this._release = r;
		}

		@Override
		public void onEmit() {
			_release.close();
		}

	}

	public final void queueForDispose(final LRelease resource) {
		game.frame.connect(new DisposePort(resource)).once();
	}

	public final LTexture finalColorTex() {
		if (colorTex == null || colorTex.isClosed()) {
			final Canvas canvas = createCanvas(1, 1);
			canvas.setFillColor(0xFFFFFFFF).fillRect(0, 0, canvas.width, canvas.height);
			colorTex = canvas.toTexture(LTexture.Format.NEAREST);
			colorTex.setDisabledTexture(true);
		}
		return colorTex;
	}

	protected Graphics(final LGame game, final GL20 gl, final Scale scale) {
		this.game = game;
		this.gl = gl;
		this.scale = scale;
		this.defaultRenderTarget = new DefaultRender(this);
	}

	protected int defaultFramebuffer() {
		return GLFrameBuffer.getSystemDefaultFramebufferHandle();
	}

	protected abstract Canvas createCanvasImpl(final Scale scale, final int pixelWidth, final int pixelHeight);

	protected void viewportChanged(final Scale scale, final int viewWidth, final int viewHeight) {
		if (lastViewWidth == viewWidth && lastViewHeight == viewHeight) {
			return;
		}
		if (lastViewWidth <= 0 || lastViewHeight <= 0) {
			game.log().info("Updating size (" + game.setting.getShowWidth() + "x" + game.setting.getShowHeight() + " / "
					+ scale.factor + ") -> " + "(" + viewWidth + "x" + viewHeight + ")");
		} else {
			game.log().info("Updating size (" + lastViewWidth + "x" + lastViewHeight + " / " + scale.factor + ") -> "
					+ "(" + viewWidth + "x" + viewHeight + ")");
		}
		this.lastViewWidth = viewWidth;
		this.lastViewHeight = viewHeight;
		final Display d = game.display();
		final LSetting setting = game.setting;
		if (setting.isSimpleScaling) {
			final boolean A = setting.width == viewWidth && setting.height == viewHeight;
			final boolean B = setting.height == viewWidth && setting.width == viewHeight;
			if (!setting.scaling() && (A || B)) {
				LSystem.setSize(MathUtils.ceil(viewWidth / LSystem.getScaleWidth()),
						MathUtils.ceil(viewHeight / LSystem.getScaleHeight()));
			} else {
				if (!(A || B)) {
					setting.width_zoom = viewWidth;
					setting.height_zoom = viewHeight;
					setting.updateScale();
				} else {
					if (setting.scaling()) {
						if (A) {
							if (viewWidth != setting.width_zoom || viewHeight != setting.height_zoom) {
								setting.width_zoom = viewWidth;
								setting.height_zoom = viewHeight;
								setting.updateScale();
							} else {
								LSystem.setSize(MathUtils.ceil(viewWidth / LSystem.getScaleWidth()),
										MathUtils.ceil(viewHeight / LSystem.getScaleHeight()));
							}
						} else {
							setting.width_zoom = viewWidth;
							setting.height_zoom = viewHeight;
							setting.updateScale();
						}
					} else {
						LSystem.setSize(MathUtils.ceil(viewWidth / LSystem.getScaleWidth()),
								MathUtils.ceil(viewHeight / LSystem.getScaleHeight()));
					}
				}
			}
		} else {
			LSystem.setSize(MathUtils.ceil(viewWidth / LSystem.getScaleWidth()),
					MathUtils.ceil(viewHeight / LSystem.getScaleHeight()));
		}
		if (viewMatrix != null) {
			LSystem.viewSize.getMatrix().mul(viewMatrix);
		}
		this.scale = scale;
		this.viewPixelWidth = viewWidth;
		this.viewPixelHeight = viewHeight;
		this.viewSizeM.width = setting.scaling() ? LSystem.invXScaled(viewPixelWidth) : scale.invScaled(viewPixelWidth);
		this.viewSizeM.height = setting.scaling() ? LSystem.invXScaled(viewPixelHeight)
				: scale.invScaled(viewPixelHeight);
		if (d != null) {
			d.resize(LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
		}
	}

	protected boolean isAllowResize(final int viewWidth, final int viewHeight) {
		if (game.setting.isCheckResize) {
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

	public int createTexture(final LTexture.Format config) {
		return createTexture(config, 0);
	}

	public int createTexture(final LTexture.Format config, final int count) {
		int id = gl.glGenTexture() + count;
		if (LSystem.containsTexture(id)) {
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

	protected static int mipmapify(final int filter, final boolean mipmaps) {
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

	public LGame game() {
		return game;
	}

	public LSetting setting() {
		return game.setting;
	}

	public int getLastViewWidth() {
		return lastViewWidth == 0 ? game.setting.getShowWidth() : lastViewWidth;
	}

	public int getLastViewHeight() {
		return lastViewHeight == 0 ? game.setting.getShowHeight() : lastViewHeight;
	}

	public boolean landscape() {
		if (lastViewWidth == 0 || this.lastViewHeight == 0) {
			return game.setting.landscape();
		}
		return this.lastViewHeight < this.lastViewWidth;
	}

	public boolean portrait() {
		if (lastViewWidth == 0 || this.lastViewHeight == 0) {
			return game.setting.portrait();
		}
		return this.lastViewHeight >= this.lastViewWidth;
	}
}
