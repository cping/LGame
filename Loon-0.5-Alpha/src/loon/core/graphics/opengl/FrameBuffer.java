package loon.core.graphics.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import loon.LSystem;
import loon.core.LRelease;
import loon.core.graphics.opengl.LTexture.Format;
import loon.core.graphics.opengl.LTexture.TextureFilter;
import loon.core.graphics.opengl.LTexture.TextureWrap;
import loon.utils.collection.Array;

public class FrameBuffer implements LRelease {

	private final static Map<GLEx, Array<FrameBuffer>> buffers = new HashMap<GLEx, Array<FrameBuffer>>();

	protected LTexture colorTexture;

	private static int defaultFramebufferHandle;

	private static boolean defaultFramebufferHandleInitialized = false;

	private int framebufferHandle;

	private int depthbufferHandle;

	private int stencilbufferHandle;

	protected final int width;

	protected final int height;

	protected final boolean hasDepth;

	protected final boolean hasStencil;

	protected final Format format;

	public FrameBuffer(Format format, int width, int height, boolean hasDepth) {
		this(format, width, height, hasDepth, false);
	}

	public FrameBuffer(Format format, int width, int height, boolean hasDepth,
			boolean hasStencil) {
		this.width = width;
		this.height = height;
		this.format = format;
		this.hasDepth = hasDepth;
		this.hasStencil = hasStencil;
		build();

		addManagedFrameBuffer(GLEx.self, this);
	}

	protected void setupTexture() {
		colorTexture = new LTexture(width, height, format);
		colorTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		colorTexture.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
	}

	private void build() {
		GL20 gl = GLEx.gl;

		if (!defaultFramebufferHandleInitialized) {
			defaultFramebufferHandleInitialized = true;
			if (LSystem.ApplicationType.IOS == LSystem.type) {
				IntBuffer intbuf = ByteBuffer
						.allocateDirect(16 * Integer.SIZE / 8)
						.order(ByteOrder.nativeOrder()).asIntBuffer();
				gl.glGetIntegerv(GL20.GL_FRAMEBUFFER_BINDING, intbuf);
				defaultFramebufferHandle = intbuf.get(0);
			} else {
				defaultFramebufferHandle = 0;
			}
		}

		setupTexture();

		framebufferHandle = gl.glGenFramebuffer();

		if (hasDepth) {
			depthbufferHandle = gl.glGenRenderbuffer();
		}

		if (hasStencil) {
			stencilbufferHandle = gl.glGenRenderbuffer();
		}

		gl.glBindTexture(GL20.GL_TEXTURE_2D,
				colorTexture.getTextureObjectHandle());

		if (hasDepth) {
			gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, depthbufferHandle);
			gl.glRenderbufferStorage(GL20.GL_RENDERBUFFER,
					GL20.GL_DEPTH_COMPONENT16, colorTexture.getWidth(),
					colorTexture.getHeight());
		}

		if (hasStencil) {
			gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, stencilbufferHandle);
			gl.glRenderbufferStorage(GL20.GL_RENDERBUFFER,
					GL20.GL_STENCIL_INDEX8, colorTexture.getWidth(),
					colorTexture.getHeight());
		}

		gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, framebufferHandle);
		gl.glFramebufferTexture2D(GL20.GL_FRAMEBUFFER,
				GL20.GL_COLOR_ATTACHMENT0, GL20.GL_TEXTURE_2D,
				colorTexture.getTextureObjectHandle(), 0);
		if (hasDepth) {
			gl.glFramebufferRenderbuffer(GL20.GL_FRAMEBUFFER,
					GL20.GL_DEPTH_ATTACHMENT, GL20.GL_RENDERBUFFER,
					depthbufferHandle);
		}

		if (hasStencil) {
			gl.glFramebufferRenderbuffer(GL20.GL_FRAMEBUFFER,
					GL20.GL_STENCIL_ATTACHMENT, GL20.GL_RENDERBUFFER,
					stencilbufferHandle);
		}

		int result = gl.glCheckFramebufferStatus(GL20.GL_FRAMEBUFFER);

		gl.glBindRenderbuffer(GL20.GL_RENDERBUFFER, 0);
		gl.glBindTexture(GL20.GL_TEXTURE_2D, 0);
		gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, defaultFramebufferHandle);

		if (result != GL20.GL_FRAMEBUFFER_COMPLETE) {
			colorTexture.dispose();

			if (hasDepth)
				gl.glDeleteRenderbuffer(depthbufferHandle);

			if (hasStencil)
				gl.glDeleteRenderbuffer(stencilbufferHandle);

			gl.glDeleteFramebuffer(framebufferHandle);

			if (result == GL20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT)
				throw new IllegalStateException(
						"frame buffer couldn't be constructed: incomplete attachment");
			if (result == GL20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS)
				throw new IllegalStateException(
						"frame buffer couldn't be constructed: incomplete dimensions");
			if (result == GL20.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT)
				throw new IllegalStateException(
						"frame buffer couldn't be constructed: missing attachment");
			if (result == GL20.GL_FRAMEBUFFER_UNSUPPORTED)
				throw new IllegalStateException(
						"frame buffer couldn't be constructed: unsupported combination of formats");
			throw new IllegalStateException(
					"frame buffer couldn't be constructed: unknown error "
							+ result);
		}
	}

	public void dispose() {
		GL20 gl = GLEx.gl;
		colorTexture.dispose();
		if (hasDepth){
			gl.glDeleteRenderbuffer(depthbufferHandle);
		}
		if (hasStencil){
			gl.glDeleteRenderbuffer(stencilbufferHandle);
		}
		gl.glDeleteFramebuffer(framebufferHandle);
		if (buffers.get(GLEx.self) != null){
			buffers.get(GLEx.self).remove(this);
		}
	}

	public void bind() {
		GLEx.gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, framebufferHandle);
	}


	public static void unbind() {
		GLEx.gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER,
				defaultFramebufferHandle);
	}

	public void begin() {
		bind();
		setFrameBufferViewport();
	}

	protected void setFrameBufferViewport() {
		GLEx.gl.glViewport(0, 0, colorTexture.getWidth(),
				colorTexture.getHeight());
	}

	public void end() {
		unbind();
		setDefaultFrameBufferViewport();
	}

	protected void setDefaultFrameBufferViewport() {
		GLEx.gl.glViewport(0, 0, GLEx.width(),
				GLEx.height());
	}

	public void end(int x, int y, int width, int height) {
		unbind();
		GLEx.gl.glViewport(x, y, width, height);
	}

	public LTexture getColorBufferTexture() {
		return colorTexture;
	}

	public int getHeight() {
		return colorTexture.getHeight();
	}

	public int getWidth() {
		return colorTexture.getWidth();
	}

	private static void addManagedFrameBuffer(GLEx app,
			FrameBuffer frameBuffer) {
		Array<FrameBuffer> managedResources = buffers.get(app);
		if (managedResources == null){
			managedResources = new Array<FrameBuffer>();
		}
		managedResources.add(frameBuffer);
		buffers.put(app, managedResources);
	}

	public static void invalidateAllFrameBuffers(GLEx app) {
		if (GLEx.gl == null){
			return;
		}
		Array<FrameBuffer> bufferArray = buffers.get(app);
		if (bufferArray == null){
			return;
		}
		for (int i = 0; i < bufferArray.size(); i++) {
			bufferArray.get(i).build();
		}
	}

	public static void clearAllFrameBuffers(GLEx app) {
		buffers.remove(app);
	}

	public static StringBuilder getManagedStatus(final StringBuilder builder) {
		builder.append("Managed buffers/app: { ");
		for (GLEx app : buffers.keySet()) {
			builder.append(buffers.get(app).size());
			builder.append(" ");
		}
		builder.append("}");
		return builder;
	}

	public static String getManagedStatus() {
		return getManagedStatus(new StringBuilder()).toString();
	}
}
