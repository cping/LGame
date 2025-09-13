/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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

import static loon.opengl.GL20.GL_COLOR_ATTACHMENT0;
import static loon.opengl.GL20.GL_FRAMEBUFFER;
import static loon.opengl.GL20.GL_TEXTURE_2D;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Iterator;

import loon.LGame;
import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.Support;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.utils.GLUtils;
import loon.utils.TArray;

public abstract class GLFrameBuffer implements LRelease {

	public enum FrameBufferDepthFormat {
		DEPTHSTENCIL_NONE, DEPTH_16, STENCIL_8, DEPTHSTENCIL_24_8
	}

	public static class FrameBufferBuilder extends GLFrameBufferBuilder<FrameBuffer> {
		public FrameBufferBuilder(int width, int height, boolean created) {
			super(width, height, created);
		}

		@Override
		public FrameBuffer build() {
			return new FrameBuffer();
		}
	}

	protected static class FrameBufferTextureAttachmentSpec {
		int internalFormat, format, type;
		boolean isFloat, isGpuOnly;
		boolean isDepth;
		boolean isStencil;

		public FrameBufferTextureAttachmentSpec(int internalformat, int format, int type) {
			this.internalFormat = internalformat;
			this.format = format;
			this.type = type;
		}

		public boolean isColorTexture() {
			return !isDepth && !isStencil;
		}
	}

	protected static class FrameBufferRenderBufferAttachmentSpec {
		int internalFormat;

		public FrameBufferRenderBufferAttachmentSpec(int internalFormat) {
			this.internalFormat = internalFormat;
		}
	}

	protected static abstract class GLFrameBufferBuilder<U extends GLFrameBuffer> {
		protected int width, height;

		protected TArray<FrameBufferTextureAttachmentSpec> textureAttachmentSpecs = new TArray<FrameBufferTextureAttachmentSpec>();

		protected FrameBufferRenderBufferAttachmentSpec stencilRenderBufferSpec;
		protected FrameBufferRenderBufferAttachmentSpec depthRenderBufferSpec;
		protected FrameBufferRenderBufferAttachmentSpec packedStencilDepthRenderBufferSpec;

		protected boolean hasStencilRenderBuffer;
		protected boolean hasDepthRenderBuffer;
		protected boolean makeCacheTexture;

		public GLFrameBufferBuilder(int width, int height, boolean created) {
			this.width = width;
			this.height = height;
			this.makeCacheTexture = created;
		}

		public GLFrameBufferBuilder<U> addColorTextureAttachment(int internalFormat, int format, int type) {
			textureAttachmentSpecs.add(new FrameBufferTextureAttachmentSpec(internalFormat, format, type));
			return this;
		}

		public GLFrameBufferBuilder<U> addFloatAttachment(int internalFormat, int format, int type, boolean gpuOnly) {
			FrameBufferTextureAttachmentSpec spec = new FrameBufferTextureAttachmentSpec(internalFormat, format, type);
			spec.isFloat = true;
			spec.isGpuOnly = gpuOnly;
			textureAttachmentSpecs.add(spec);
			return this;
		}

		public GLFrameBufferBuilder<U> addBasicColorTextureAttachment(int glFormat, int glType) {
			return addColorTextureAttachment(glFormat, glFormat, glType);
		}

		public GLFrameBufferBuilder<U> addDepthTextureAttachment(int internalFormat, int type) {
			FrameBufferTextureAttachmentSpec spec = new FrameBufferTextureAttachmentSpec(internalFormat,
					GL20.GL_DEPTH_COMPONENT, type);
			spec.isDepth = true;
			textureAttachmentSpecs.add(spec);
			return this;
		}

		public GLFrameBufferBuilder<U> addStencilTextureAttachment(int internalFormat, int type) {
			FrameBufferTextureAttachmentSpec spec = new FrameBufferTextureAttachmentSpec(internalFormat,
					GL20.GL_STENCIL_ATTACHMENT, type);
			spec.isStencil = true;
			textureAttachmentSpecs.add(spec);
			return this;
		}

		public GLFrameBufferBuilder<U> addDepthRenderBuffer(int internalFormat) {
			depthRenderBufferSpec = new FrameBufferRenderBufferAttachmentSpec(internalFormat);
			hasDepthRenderBuffer = true;
			return this;
		}

		public GLFrameBufferBuilder<U> addStencilRenderBuffer(int internalFormat) {
			stencilRenderBufferSpec = new FrameBufferRenderBufferAttachmentSpec(internalFormat);
			hasStencilRenderBuffer = true;
			return this;
		}

		public GLFrameBufferBuilder<U> addBasicDepthRenderBuffer() {
			return addDepthRenderBuffer(GL20.GL_DEPTH_COMPONENT16);
		}

		public GLFrameBufferBuilder<U> addBasicStencilRenderBuffer() {
			return addStencilRenderBuffer(GL20.GL_STENCIL_INDEX8);
		}

		public abstract U build();
	}

	protected abstract LTexture createTexture(FrameBufferTextureAttachmentSpec attachmentSpec);

	protected abstract void disposeColorTexture(LTexture colorTexture);

	protected abstract void attachFrameBufferColorTexture(LTexture texture);

	protected final static int GL_DEPTH_COMPONENT = 0x1902;

	protected final static int GL_DEPTH_STENCIL_OES = 0x84F9;

	protected final static int GL_UNSIGNED_SHORT = 0x1403;

	protected final static int GL_UNSIGNED_INT = 0x1405;

	protected final static int GL_UNSIGNED_INT_24_8_OES = 0x84FA;

	protected final static int GL_DEPTH_COMPONENT16 = 0x81A5;

	protected final static int GL_DEPTH_COMPONENT32_OES = 0x81A7;

	protected final static int GL_DEPTH24_STENCIL8_OES = 0x88F0;

	protected boolean bufferLocked = false;

	protected GLFrameBuffer lastBoundFramebuffer = null;

	protected GLFrameBuffer currentBoundFramebuffer = null;

	protected TArray<LTexture> textureAttachments = new TArray<LTexture>();

	protected static int defaultFramebufferHandle;

	protected static boolean defaultFramebufferHandleInitialized = false;

	protected int framebufferHandle;

	protected int depthbufferHandle;

	protected int stencilbufferHandle;

	protected int depthStencilPackedBufferHandle;

	protected boolean hasDepthStencilPackedBuffer;

	protected GLFrameBufferBuilder<? extends GLFrameBuffer> bufferBuilder;

	GLFrameBuffer() {
	}

	protected GLFrameBuffer(GLFrameBufferBuilder<? extends GLFrameBuffer> bufferBuilder) {
		this.bufferBuilder = bufferBuilder;
		build();
	}

	public LTexture texture() {
		return textureAttachments.first();
	}

	public LTexture getColorBufferTexture() {
		return textureAttachments.first();
	}

	public TArray<LTexture> getTextureAttachments() {
		return textureAttachments;
	}

	public void clear(float r, float g, float b, float a) {
		this.clear(r, g, b, a, FrameBufferDepthFormat.DEPTHSTENCIL_NONE);
	}

	public void clear(float r, float g, float b, float a, FrameBufferDepthFormat depthStencilFormat) {
		GL20 gl = LSystem.base().graphics().gl;
		gl.glClearColor(r, g, b, a);
		int flag = GL20.GL_COLOR_BUFFER_BIT;
		switch (depthStencilFormat) {
		case DEPTHSTENCIL_NONE:
			flag |= GL20.GL_DEPTH_BUFFER_BIT;
			return;
		case DEPTH_16:
			flag |= GL20.GL_DEPTH_BUFFER_BIT;
			break;
		case STENCIL_8:
			flag |= GL20.GL_STENCIL_BUFFER_BIT;
			break;
		case DEPTHSTENCIL_24_8:
			flag |= GL20.GL_DEPTH_BUFFER_BIT;
			flag |= GL20.GL_STENCIL_BUFFER_BIT;
			break;
		}
		gl.glClear(flag);
	}

	public boolean isBound() {
		return currentBoundFramebuffer == this;
	}

	public LTexture getTextureData() {
		return getTextureData(true, true);
	}

	public LTexture getTextureData(boolean flip, boolean alpha) {
		return getImageData(0, flip, alpha).texture();
	}

	public Image getImageData(int index, boolean flip, boolean alpha) {
		GL20 gl = LSystem.base().graphics().gl;
		final int nfb = gl.glGenFramebuffer();
		if (nfb == 0) {
			throw new LSysException("Failed to gen framebuffer: " + gl.glGetError());
		}
		LTexture texture = getTextureAttachments().get(index);
		gl.glBindFramebuffer(GL_FRAMEBUFFER, nfb);
		gl.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getID(), 0);
		boolean canRead = GLUtils.isFrameBufferCompleted(gl);
		if (!canRead) {
			return null;
		}
		Image image = GLUtils.getFrameBuffeImage(gl, 0, 0, getWidth(), getHeight(), flip, alpha);
		gl.glBindFramebuffer(GL_FRAMEBUFFER, defaultFramebufferHandle);
		gl.glDeleteFramebuffer(nfb);
		return image;
	}

	protected static void checkIOSdefaultFramebufferHandle(GL20 gl) {
		if (!defaultFramebufferHandleInitialized) {
			defaultFramebufferHandleInitialized = true;
			if (LSystem.base() != null && LSystem.base().type() == LGame.Type.IOS) {
				IntBuffer intbuf = ByteBuffer.allocateDirect(16 * Integer.SIZE / 8).order(ByteOrder.nativeOrder())
						.asIntBuffer();
				gl.glGetIntegerv(GL20.GL_FRAMEBUFFER_BINDING, intbuf);
				defaultFramebufferHandle = intbuf.get(0);
			} else {
				defaultFramebufferHandle = 0;
			}
		}
	}

	protected void build() {
		build(LSystem.base().graphics().gl, true);
	}

	protected void reset() {
		build(LSystem.base().graphics().gl, false);
	}

	protected void build(GL20 gl, boolean putPool) {

		checkIOSdefaultFramebufferHandle(gl);
		framebufferHandle = gl.glGenFramebuffer();
		if (framebufferHandle == 0) {
			throw new LSysException("Failed to gen framebuffer: " + gl.glGetError());
		}

		gl.glBindFramebuffer(GL_FRAMEBUFFER, framebufferHandle);

		final int width = bufferBuilder.width;
		final int height = bufferBuilder.height;

		if (bufferBuilder.hasDepthRenderBuffer) {
			depthbufferHandle = gl.glGenRenderbuffer();
			gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, depthbufferHandle);
			gl.glRenderbufferStorage(GL20.GL_RENDERBUFFER, bufferBuilder.depthRenderBufferSpec.internalFormat, width,
					height);
		}

		if (bufferBuilder.hasStencilRenderBuffer) {
			stencilbufferHandle = gl.glGenRenderbuffer();
			gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, stencilbufferHandle);
			gl.glRenderbufferStorage(GL20.GL_RENDERBUFFER, bufferBuilder.stencilRenderBufferSpec.internalFormat, width,
					height);
		}

		if (bufferBuilder.makeCacheTexture) {
			textureAttachments.clear();
			final LTexture texture = createTexture(
					(FrameBufferTextureAttachmentSpec) bufferBuilder.textureAttachmentSpecs.first());
			textureAttachments.add(texture);
			GLUtils.bindTexture(gl, texture.getID());
			attachFrameBufferColorTexture(textureAttachments.first());
		}

		if (bufferBuilder.hasDepthRenderBuffer) {
			gl.glFramebufferRenderbuffer(GL20.GL_FRAMEBUFFER, GL20.GL_DEPTH_ATTACHMENT, GL20.GL_RENDERBUFFER,
					depthbufferHandle);
		}

		if (bufferBuilder.hasStencilRenderBuffer) {
			gl.glFramebufferRenderbuffer(GL20.GL_FRAMEBUFFER, GL20.GL_STENCIL_ATTACHMENT, GL20.GL_RENDERBUFFER,
					stencilbufferHandle);
		}

		final boolean skip = !(bufferBuilder.hasDepthRenderBuffer && bufferBuilder.hasStencilRenderBuffer
				&& bufferBuilder.makeCacheTexture);

		if (!skip) {
			gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, 0);
			for (LTexture tex : textureAttachments) {
				GLUtils.bindTexture(gl, tex);
			}

			int result = gl.glCheckFramebufferStatus(GL20.GL_FRAMEBUFFER);

			if (result == GL20.GL_FRAMEBUFFER_UNSUPPORTED && bufferBuilder.hasDepthRenderBuffer
					&& bufferBuilder.hasStencilRenderBuffer) {
				if (bufferBuilder.hasDepthRenderBuffer) {
					gl.glDeleteRenderbuffer(depthbufferHandle);
					depthbufferHandle = 0;
				}
				if (bufferBuilder.hasStencilRenderBuffer) {
					gl.glDeleteRenderbuffer(stencilbufferHandle);
					stencilbufferHandle = 0;
				}

				depthStencilPackedBufferHandle = gl.glGenRenderbuffer();
				hasDepthStencilPackedBuffer = true;
				gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, depthStencilPackedBufferHandle);
				gl.glRenderbufferStorage(GL20.GL_RENDERBUFFER, GL_DEPTH24_STENCIL8_OES, width, height);
				gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, 0);

				gl.glFramebufferRenderbuffer(GL20.GL_FRAMEBUFFER, GL20.GL_DEPTH_ATTACHMENT, GL20.GL_RENDERBUFFER,
						depthStencilPackedBufferHandle);
				gl.glFramebufferRenderbuffer(GL20.GL_FRAMEBUFFER, GL20.GL_STENCIL_ATTACHMENT, GL20.GL_RENDERBUFFER,
						depthStencilPackedBufferHandle);
				result = gl.glCheckFramebufferStatus(GL20.GL_FRAMEBUFFER);
			}

			gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, defaultFramebufferHandle);

			if (result != GL20.GL_FRAMEBUFFER_COMPLETE) {
				for (LTexture tex : textureAttachments) {
					disposeColorTexture(tex);
				}

				if (hasDepthStencilPackedBuffer) {
					gl.glDeleteBuffer(depthStencilPackedBufferHandle);
				} else {
					if (bufferBuilder.hasDepthRenderBuffer) {
						gl.glDeleteRenderbuffer(depthbufferHandle);
					}
					if (bufferBuilder.hasStencilRenderBuffer) {
						gl.glDeleteRenderbuffer(stencilbufferHandle);
					}
				}

				gl.glDeleteFramebuffer(framebufferHandle);

				if (result == GL20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT) {
					throw new LSysException("Frame buffer couldn't be constructed: incomplete attachment");
				}
				if (result == GL20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS) {
					throw new LSysException("Frame buffer couldn't be constructed: incomplete dimensions");
				}
				if (result == GL20.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT) {
					throw new LSysException("Frame buffer couldn't be constructed: missing attachment");
				}
				if (result == GL20.GL_FRAMEBUFFER_UNSUPPORTED) {
					throw new LSysException("Frame buffer couldn't be constructed: unsupported combination of formats");
				}
				throw new LSysException("Frame buffer couldn't be constructed: unknown error " + result);
			}
		}
		if (putPool) {
			addManagedFrameBuffer(this);
		}
	}

	@Override
	public void close() {
		if (LSystem.base() == null || LSystem.base().graphics() == null) {
			return;
		}
		final GL20 gl = LSystem.base().graphics().gl;
		for (Iterator<LTexture> it = textureAttachments.iterator(); it.hasNext();) {
			LTexture texture = it.next();
			if (texture != null) {
				disposeColorTexture(texture);
			}
		}
		if (hasDepthStencilPackedBuffer) {
			gl.glDeleteRenderbuffer(depthStencilPackedBufferHandle);
		} else {
			if (bufferBuilder.hasDepthRenderBuffer) {
				gl.glDeleteRenderbuffer(depthbufferHandle);
			}
			if (bufferBuilder.hasStencilRenderBuffer) {
				gl.glDeleteRenderbuffer(stencilbufferHandle);
			}
		}
		gl.glDeleteFramebuffer(framebufferHandle);
		LSystem.removeFrameBuffer(this);
	}

	public int[] readPixels(int x, int y, int width, int height) {
		return readPixels(LSystem.base().graphics().gl, x, y, width, height);
	}

	public int[] readPixels(GL20 gl, int x, int y, int width, int height) {
		return readPixels(gl, x, y, width, height, true, true);
	}

	public int[] readPixels(GL20 gl, int x, int y, int width, int height, boolean flipY, boolean alpha) {
		final Support support = LSystem.base().support();
		final int oldReadBuffer = gl.glGetInteger(GL20.GL_READ_FRAMEBUFFER_BINDING);
		gl.glBindFramebuffer(GL20.GL_READ_FRAMEBUFFER, framebufferHandle);
		final int bits = alpha ? 4 : 3;
		final int pixelSize = width * height;
		final int numBytes = pixelSize * bits;
		final ByteBuffer pixels = support.newByteBuffer(numBytes);
		gl.glPixelStorei(GL20.GL_PACK_ALIGNMENT, 1);
		gl.glReadPixels(x, y, width, height, alpha ? GL20.GL_RGBA : GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, pixels);
		final int[] intPixels = new int[pixelSize];
		int idx = 0;
		if (flipY) {
			final int offset = -width * 2;
			int rev = width * (height - 1);
			for (int j = 0; j < height; j++) {
				for (int i = 0; i < width; i++) {
					if (alpha) {
						int r = pixels.get(idx++) & 0xFF;
						int g = pixels.get(idx++) & 0xFF;
						int b = pixels.get(idx++) & 0xFF;
						int a = pixels.get(idx++) & 0xFF;
						intPixels[rev] = LColor.argb(a, r, g, b);
					} else {
						int r = pixels.get(idx++) & 0xFF;
						int g = pixels.get(idx++) & 0xFF;
						int b = pixels.get(idx++) & 0xFF;
						intPixels[rev] = LColor.rgb(r, g, b);
					}
					rev++;
				}
				rev += offset;
			}
		} else {
			int dst = 0;
			for (int y1 = 0; y1 < height; y1++) {
				for (int x1 = 0; x1 < width; x1++) {
					if (alpha) {
						int r = pixels.get(idx++) & 0xFF;
						int g = pixels.get(idx++) & 0xFF;
						int b = pixels.get(idx++) & 0xFF;
						int a = pixels.get(idx++) & 0xFF;
						intPixels[dst + x1] = LColor.argb(a, r, g, b);
					} else {
						int r = pixels.get(idx++) & 0xFF;
						int g = pixels.get(idx++) & 0xFF;
						int b = pixels.get(idx++) & 0xFF;
						intPixels[dst + x1] = LColor.rgb(r, g, b);
					}
				}
				dst += width;
			}
		}
		gl.glBindFramebuffer(GL20.GL_READ_FRAMEBUFFER, oldReadBuffer);
		return intPixels;
	}

	public void copyToTexture(int texid) {
		copyToTexture(LSystem.base().graphics().gl, texid);
	}

	public void copyToTexture(GL20 gl, int texid) {
		copyToTexture(gl, texid, getWidth(), getHeight());
	}

	public void copyToTexture(GL20 gl, int texid, int w, int h) {
		final int oldReadBuffer = gl.glGetInteger(GL20.GL_READ_FRAMEBUFFER_BINDING);
		gl.glBindFramebuffer(GL20.GL_READ_FRAMEBUFFER, framebufferHandle);
		gl.glBindTexture(GL_TEXTURE_2D, texid);
		gl.glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, w, h);
		gl.glBindFramebuffer(GL20.GL_READ_FRAMEBUFFER, oldReadBuffer);
	}

	public void bind() {
		bind(LSystem.base().graphics().gl);
	}

	public void unbind() {
		unbind(LSystem.base().graphics().gl);
	}

	public void bind(GL20 gl) {
		bind(gl, framebufferHandle);
	}

	public void unbind(GL20 gl) {
		unbind(gl, defaultFramebufferHandle);
	}

	public void bind(GL20 gl, int id) {
		gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, id);
	}

	public void unbind(GL20 gl, int id) {
		gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, id);
	}

	public void bind(GL20 gl, int w, int h) {
		bind(gl, framebufferHandle, w, h);
	}

	public void bind(GL20 gl, int id, int w, int h) {
		gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, id);
		gl.glViewport(0, 0, w, h);
	}

	public void unbind(GL20 gl, int w, int h) {
		unbind(gl, framebufferHandle, w, h);
	}

	public void unbind(GL20 gl, int id, int w, int h) {
		gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, id);
		gl.glViewport(0, 0, w, h);
	}

	public GLFrameBuffer lock() {
		this.bufferLocked = true;
		return this;
	}

	public boolean isLocked() {
		return this.bufferLocked;
	}

	public GLFrameBuffer unlock() {
		this.bufferLocked = false;
		return this;
	}

	public void begin() {
		if (bufferLocked) {
			return;
		}
		if (currentBoundFramebuffer == this) {
			throw new LSysException("Do not run begin !");
		}
		LSystem.mainFlushDraw();
		lastBoundFramebuffer = currentBoundFramebuffer;
		currentBoundFramebuffer = this;
		bind();
		setFrameBufferViewport();
	}

	public void end() {
		if (bufferLocked) {
			return;
		}
		LSystem.mainFlushDraw();
		if (lastBoundFramebuffer != null) {
			lastBoundFramebuffer.bind();
			lastBoundFramebuffer.setFrameBufferViewport();
		} else {
			unbind();
			LSystem.base().graphics().gl.glViewport(0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
		}
		currentBoundFramebuffer = lastBoundFramebuffer;
		lastBoundFramebuffer = null;
	}

	protected void setFrameBufferViewport() {
		LSystem.base().graphics().gl.glViewport(0, 0, bufferBuilder.width, bufferBuilder.height);
	}

	public void end(int x, int y, int width, int height) {
		unbind();
		LSystem.base().graphics().gl.glViewport(x, y, width, height);
	}

	public int getFramebufferID() {
		return getFramebufferHandle();
	}

	public int getFramebufferHandle() {
		return framebufferHandle;
	}

	public int getDepthBufferHandle() {
		return depthbufferHandle;
	}

	public int getStencilBufferHandle() {
		return stencilbufferHandle;
	}

	protected int getDepthStencilPackedBuffer() {
		return depthStencilPackedBufferHandle;
	}

	public int getHeight() {
		return bufferBuilder.height;
	}

	public int getWidth() {
		return bufferBuilder.width;
	}

	public final static int getSystemDefaultFramebufferHandle() {
		return defaultFramebufferHandle;
	}

	private void addManagedFrameBuffer(GLFrameBuffer frameBuffer) {
		LSystem.addFrameBuffer(frameBuffer);
	}

	public static void invalidate(LGame game) {
		if (game == null || game.graphics() == null || game.graphics().gl == null) {
			return;
		}
		final TArray<GLFrameBuffer> bufferArray = game.getFrameBufferAll();
		if (bufferArray == null || bufferArray.size == 0) {
			return;
		}
		for (Iterator<GLFrameBuffer> it = bufferArray.iterator(); it.hasNext();) {
			GLFrameBuffer buffer = it.next();
			if (buffer != null) {
				buffer.build(game.graphics().gl, false);
			}
		}
	}

	public void clearAllFrameBuffers() {
		LSystem.clearFramebuffer();
	}

}
