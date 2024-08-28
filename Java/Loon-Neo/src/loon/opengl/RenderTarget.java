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

import loon.Graphics;
import loon.LRelease;
import loon.LTexture;
import loon.canvas.Image;

public abstract class RenderTarget implements LRelease {

	static class TextureRenderTarget extends RenderTarget {

		public TextureRenderTarget(Graphics gfx, LTexture texture) {
			super(gfx, texture);
		}

		@Override
		public int width() {
			return _texture.pixelWidth();
		}

		@Override
		public int height() {
			return _texture.pixelHeight();
		}

		@Override
		public float xscale() {
			return _texture.pixelWidth() / _texture.width();
		}

		@Override
		public float yscale() {
			return _texture.pixelHeight() / _texture.height();
		}

		@Override
		public boolean flip() {
			return true;
		}

		@Override
		public LTexture texture() {
			return _texture;
		}

	}

	public static RenderTarget create(Graphics gfx, final LTexture tex) {
		return new TextureRenderTarget(gfx, tex);
	}

	public final Graphics _gfx;

	public final LTexture _texture;

	private FrameBuffer _frameBuffer;

	private boolean _disposed;

	private boolean _inited;

	public RenderTarget(Graphics gfx, LTexture texture) {
		this._gfx = gfx;
		this._texture = texture;
	}

	protected void checkFrameBufferInit() {
		if (!_inited) {
			if (this._frameBuffer != null) {
				this._frameBuffer.close();
			}
			final LTexture tex = texture();
			if (tex == null) {
				this._frameBuffer = FrameBuffer.createEmptyFrameBuffer(width(), height());
			} else {
				this._frameBuffer = new FrameBuffer(width(), height());
				this._frameBuffer.attachFrameBufferColorTexture(tex);
			}
			this._inited = true;
		}
	}

	public abstract LTexture texture();

	public abstract int width();

	public abstract int height();

	public abstract float xscale();

	public abstract float yscale();

	public abstract boolean flip();

	public int id() {
		checkFrameBufferInit();
		return _frameBuffer.getFramebufferHandle();
	}

	public LTexture getTextureData() {
		checkFrameBufferInit();
		return _frameBuffer.getTextureData();
	}

	public LTexture getTextureData(boolean flip, boolean alpha) {
		checkFrameBufferInit();
		return _frameBuffer.getTextureData(flip, alpha);
	}

	public Image getImageData(int index, boolean flip, boolean alpha) {
		checkFrameBufferInit();
		return _frameBuffer.getImageData(index, flip, alpha);
	}

	public int getDefaultFramebufferID() {
		checkFrameBufferInit();
		return _frameBuffer.getFramebufferHandle();
	}

	public FrameBuffer getFrameBuffer() {
		checkFrameBufferInit();
		return _frameBuffer;
	}

	public void bind() {
		checkFrameBufferInit();
		_frameBuffer.bind(_gfx.gl, id(), width(), height());
	}

	public void unbind() {
		if (_inited) {
			_frameBuffer.unbind(_gfx.gl);
		}
	}

	@Override
	public String toString() {
		return "[id=" + id() + ", size=" + width() + "x" + height() + " @ " + xscale() + "x" + yscale() + ", flip="
				+ flip() + "]";
	}

	public boolean isClosed() {
		return _disposed;
	}

	@Override
	public void close() {
		if (!_disposed) {
			if (_inited) {
				_frameBuffer.close();
			}
			_disposed = true;
			_inited = false;
		}
	}

	@Override
	protected void finalize() {
		if (!_disposed) {
			_gfx.queueForDispose(this);
		}
	}

}
