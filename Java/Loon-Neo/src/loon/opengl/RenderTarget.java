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

import loon.Graphics;
import loon.LRelease;
import loon.LSysException;
import loon.LTexture;
import loon.canvas.Image;
import loon.utils.GLUtils;

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

	private int defaultFramebufferID;

	private int frameBufferID;

	private boolean disposed;

	private boolean inited;

	public RenderTarget(Graphics gfx, LTexture texture) {
		this.gfx = gfx;
		this.texture = texture;
		this.frameBufferID = 0;
		this.defaultFramebufferID = GLFrameBuffer.defaultFramebufferHandle;
	}

	protected void checkInit() {
		if (!inited) {
			createFrameBuffer();
			inited = true;
		}
	}

	protected void createFrameBuffer() {
		GL20 gl = gfx.gl;
		GLFrameBuffer.checkIOSdefaultFramebufferHandle(gl);
		final int fb = gl.glGenFramebuffer();
		if (fb == 0) {
			throw new LSysException("Failed to gen framebuffer: " + gl.glGetError());
		}
		gl.glBindFramebuffer(GL_FRAMEBUFFER, fb);
		frameBufferID = fb;
		if (texture != null) {
			gl.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getID(), 0);
		}
		gl.checkError("RenderTarget.create");
	}

	public int id() {
		return frameBufferID;
	}

	public abstract LTexture texture();

	public abstract int width();

	public abstract int height();

	public abstract float xscale();

	public abstract float yscale();

	public abstract boolean flip();

	public LTexture getTextureData() {
		return getTextureData(true, true);
	}

	public LTexture getTextureData(boolean flip, boolean alpha) {
		return getImageData(0, flip, alpha).texture();
	}

	public Image getImageData(int index, boolean flip, boolean alpha) {
		if (texture() == null) {
			return null;
		}
		checkInit();
		final GL20 gl = gfx.gl;
		final int nfb = gl.glGenFramebuffer();
		if (nfb == 0) {
			throw new LSysException("Failed to gen framebuffer: " + gl.glGetError());
		}
		gl.glBindFramebuffer(GL_FRAMEBUFFER, nfb);
		gl.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture().getID(), 0);
		boolean canRead = GLUtils.isFrameBufferCompleted(gl);
		if (!canRead) {
			return null;
		}
		Image image = GLUtils.getFrameBuffeImage(gl, 0, 0, width(), height(), flip, alpha);
		gl.glBindFramebuffer(GL_FRAMEBUFFER, defaultFramebufferID);
		gl.glDeleteFramebuffer(nfb);
		return image;
	}

	public void bindTexture(LTexture texture) {
		if (texture() == null) {
			return;
		}
		checkInit();
		gfx.gl.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER, GL20.GL_COLOR_ATTACHMENT0, GL20.GL_TEXTURE_2D,
				texture().getID(), 0);
	}

	public void bind() {
		checkInit();
		final GL20 g = gfx.gl;
		g.glBindFramebuffer(GL_FRAMEBUFFER, id());
		g.glViewport(0, 0, width(), height());
	}

	public void unbind() {
		if (inited) {
			gfx.gl.glBindFramebuffer(GL_FRAMEBUFFER, defaultFramebufferID);
		}
	}

	public int getDefaultFramebufferID() {
		return defaultFramebufferID;
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
				gfx.gl.glDeleteFramebuffer(id());
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
