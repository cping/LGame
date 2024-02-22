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
			return texture.pixelWidth();
		}

		@Override
		public int height() {
			return texture.pixelHeight();
		}

		@Override
		public float xscale() {
			return texture.pixelWidth() / texture.width();
		}

		@Override
		public float yscale() {
			return texture.pixelHeight() / texture.height();
		}

		@Override
		public boolean flip() {
			return true;
		}

		@Override
		public LTexture texture() {
			return texture;
		}

	}

	public static RenderTarget create(Graphics gfx, final LTexture tex) {
		return new TextureRenderTarget(gfx, tex);
	}

	public final Graphics gfx;

	public final LTexture texture;

	private FrameBuffer frameBuffer;

	private boolean disposed;

	private boolean inited;

	public RenderTarget(Graphics gfx, LTexture texture) {
		this.gfx = gfx;
		this.texture = texture;
	}

	protected void checkFrameBufferInit() {
		if (!inited) {
			if (this.frameBuffer != null) {
				this.frameBuffer.close();
			}
			final LTexture tex = texture();
			if (tex == null) {
				this.frameBuffer = FrameBuffer.createEmptyFrameBuffer(width(), height());
			} else {
				this.frameBuffer = new FrameBuffer(width(), height());
				this.frameBuffer.attachFrameBufferColorTexture(tex);
			}
			this.inited = true;
		}
	}

	public int id() {
		return frameBuffer.getFramebufferHandle();
	}

	public abstract LTexture texture();

	public abstract int width();

	public abstract int height();

	public abstract float xscale();

	public abstract float yscale();

	public abstract boolean flip();

	public LTexture getTextureData() {
		checkFrameBufferInit();
		return frameBuffer.getTextureData();
	}

	public LTexture getTextureData(boolean flip, boolean alpha) {
		checkFrameBufferInit();
		return frameBuffer.getTextureData(flip, alpha);
	}

	public Image getImageData(int index, boolean flip, boolean alpha) {
		checkFrameBufferInit();
		return frameBuffer.getImageData(index, flip, alpha);
	}

	public int getDefaultFramebufferID() {
		checkFrameBufferInit();
		return frameBuffer.getFramebufferHandle();
	}

	public FrameBuffer getFrameBuffer() {
		checkFrameBufferInit();
		return frameBuffer;
	}

	public void bind() {
		checkFrameBufferInit();
		frameBuffer.bind(gfx.gl, id(), width(), height());
	}

	public void unbind() {
		if (inited) {
			frameBuffer.unbind(gfx.gl);
		}
	}

	@Override
	public String toString() {
		return "[id=" + id() + ", size=" + width() + "x" + height() + " @ " + xscale() + "x" + yscale() + ", flip="
				+ flip() + "]";
	}

	public boolean isClosed() {
		return disposed;
	}

	@Override
	public void close() {
		if (!disposed) {
			if (inited) {
				frameBuffer.close();
			}
			disposed = true;
			inited = false;
		}
	}

	@Override
	protected void finalize() {
		if (!disposed) {
			gfx.queueForDispose(this);
		}
	}

}
