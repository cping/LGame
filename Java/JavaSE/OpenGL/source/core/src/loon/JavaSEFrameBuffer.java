package loon;

import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glBindFramebufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glCheckFramebufferStatusEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glDeleteFramebuffersEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glFramebufferTexture2DEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glGenFramebuffersEXT;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import loon.core.event.Updateable;
import loon.core.graphics.opengl.FrameBuffer;
import loon.core.graphics.opengl.GL;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTexture.Format;

import org.lwjgl.opengl.GLContext;

public class JavaSEFrameBuffer implements FrameBuffer {

	private LTexture texture;
	private int id;
	private int width, height;

	private boolean isLoaded;

	public JavaSEFrameBuffer(final LTexture texture) {
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
				id = glGenFramebuffersEXT();
				glBindFramebufferEXT(GL_FRAMEBUFFER, id);
				glFramebufferTexture2DEXT(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
						texture.hasAlpha() ? GL.GL_RGBA : GL.GL_RGB,
						texture.getTextureID(), 0);
				int result = glCheckFramebufferStatusEXT(GL_FRAMEBUFFER);
				if (result != GL_FRAMEBUFFER_COMPLETE) {
					glBindFramebufferEXT(GL_FRAMEBUFFER, 0);
					glDeleteFramebuffers(id);
					throw new RuntimeException("exception " + result
							+ " when checking JavaSEFBO status");
				}
				glBindFramebufferEXT(GL_FRAMEBUFFER, 0);
				isLoaded = true;
			}
		};
		LSystem.load(update);
	}

	public JavaSEFrameBuffer(int width, int height, Format format) {
		this(new LTexture(width, height, format));
	}

	public JavaSEFrameBuffer(int width, int height) {
		this(width, height, LTexture.Format.LINEAR);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getID() {
		return id;
	}

	public LTexture getTexture() {
		return texture;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public boolean isSupported() {
		return GLContext.getCapabilities().GL_EXT_framebuffer_object;
	}

	public void bind() {
		if (!isLoaded) {
			return;
		}
		if (id == 0) {
			throw new IllegalStateException(
					"can't use FBO as it has been destroyed..");
		}
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, id);
	}

	public void unbind() {
		if (!isLoaded) {
			return;
		}
		if (id == 0) {
			return;
		}
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	}

	public void destroy() {
		if (isLoaded) {
			isLoaded = false;
			glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
			glDeleteFramebuffersEXT(id);
			id = 0;
		}
	}
}
