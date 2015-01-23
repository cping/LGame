package loon;

import javax.microedition.khronos.opengles.GL11ExtensionPack;

import loon.core.event.Updateable;
import loon.core.graphics.opengl.FrameBuffer;
import loon.core.graphics.opengl.GL10;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTexture.Format;

public class AndroidFrameBuffer implements FrameBuffer {

	int[] framebuffers = new int[1];
	private LTexture texture;
	private int id;
	private int width, height;

	private boolean isLoaded;

	static boolean superFBO(GL10 gl, String extension) {
		final String extensions = " " + gl.glGetString(GL10.GL_EXTENSIONS)
				+ " ";
		return extensions.indexOf(" " + extension + " ") >= 0;
	}

	public AndroidFrameBuffer(final LTexture texture) {
		if (!isSupported()) {
			throw new RuntimeException(
					"FBO extension not supported in hardware");
		}
		this.texture = texture;
		this.width = texture.getWidth();
		this.height = texture.getHeight();
		Updateable update = new Updateable() {

			@Override
			public void action(Object a) {
				if (!texture.isLoaded()) {
					texture.loadTexture();
				}
				final GL11ExtensionPack gl11ep = (GL11ExtensionPack) ((AndroidGL10) GLEx.gl10).gl;
				gl11ep.glGenFramebuffersOES(1, framebuffers, 0);
				id = framebuffers[0];
				gl11ep.glBindFramebufferOES(
						GL11ExtensionPack.GL_FRAMEBUFFER_OES, id);
				int depthbuffer;
				int[] renderbuffers = new int[1];
				gl11ep.glGenRenderbuffersOES(1, renderbuffers, 0);
				depthbuffer = renderbuffers[0];
				gl11ep.glBindRenderbufferOES(
						GL11ExtensionPack.GL_RENDERBUFFER_OES, depthbuffer);
				gl11ep.glRenderbufferStorageOES(
						GL11ExtensionPack.GL_RENDERBUFFER_OES,
						GL11ExtensionPack.GL_DEPTH_COMPONENT16, width, height);
				gl11ep.glFramebufferRenderbufferOES(
						GL11ExtensionPack.GL_FRAMEBUFFER_OES,
						GL11ExtensionPack.GL_DEPTH_ATTACHMENT_OES,
						GL11ExtensionPack.GL_RENDERBUFFER_OES, depthbuffer);
				gl11ep.glFramebufferTexture2DOES(
						GL11ExtensionPack.GL_FRAMEBUFFER_OES,
						GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES,
						GL10.GL_TEXTURE_2D, texture.getTextureID(), 0);
				int status = gl11ep
						.glCheckFramebufferStatusOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES);
				if (status != GL11ExtensionPack.GL_FRAMEBUFFER_COMPLETE_OES) {
					throw new RuntimeException("Framebuffer is not complete: "
							+ Integer.toHexString(status));
				}
				gl11ep.glBindFramebufferOES(
						GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);
				isLoaded = true;
			}
		};
		LSystem.load(update);
	}

	public AndroidFrameBuffer(int width, int height, Format format) {
		this(new LTexture(width, height, format));
	}

	public AndroidFrameBuffer(int width, int height) {
		this(width, height, LTexture.Format.LINEAR);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public LTexture getTexture() {
		return texture;
	}

	@Override
	public boolean isLoaded() {
		return isLoaded;
	}

	@Override
	public boolean isSupported() {
		return superFBO(GLEx.gl10, "GL_OES_framebuffer_object");
	}

	public void bind() {
		if (!isLoaded) {
			return;
		}
		if (id == 0) {
			throw new IllegalStateException(
					"can't use FBO as it has been destroyed..");
		}
		final GL11ExtensionPack gl11ep = (GL11ExtensionPack) ((AndroidGL10) GLEx.gl10).gl;
		gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, id);
	}

	public void unbind() {
		if (!isLoaded) {
			return;
		}
		if (id == 0) {
			return;
		}
		final GL11ExtensionPack gl11ep = (GL11ExtensionPack) ((AndroidGL10) GLEx.gl10).gl;
		gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);
	}

	public void destroy() {
		if (isLoaded) {
			isLoaded = false;
			final GL11ExtensionPack gl11ep = (GL11ExtensionPack) ((AndroidGL10) GLEx.gl10).gl;
			gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);
			gl11ep.glDeleteFramebuffersOES(1, framebuffers, 0);
			id = 0;
		}
	}
}
