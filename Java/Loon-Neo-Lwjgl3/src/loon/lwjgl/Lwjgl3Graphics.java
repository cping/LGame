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
package loon.lwjgl;

import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowFrameSize;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.nglfwGetFramebufferSize;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.system.MemoryUtil;

import loon.LTexture;
import loon.geom.Dimension;
import loon.jni.NativeSupport;
import loon.opengl.GL20;
import loon.utils.GLUtils;
import loon.utils.Scale;

public class Lwjgl3Graphics extends Lwjgl3ImplGraphics {

	private final GLFWFramebufferSizeCallback fbSizeCallback = new GLFWFramebufferSizeCallback() {
		@Override
		public void invoke(long windowId, int width, int height) {
			viewportAndScaleChanged(width, height);
		}
	};
	private Dimension screenSize = new Dimension();
	private final long windowId;

	public Lwjgl3Graphics(Lwjgl3Game game, long win) {
		super(game, new Lwjgl3GL20(), Scale.ONE);
		this.windowId = win;
		glfwSetFramebufferSizeCallback(windowId, fbSizeCallback);
	}

	boolean isPowerOfTwo(int value) {
		return value != 0 && (value & value - 1) == 0;
	}

	private void viewportAndScaleChanged(int fbWidth, int fbHeight) {
		if (!isAllowResize(fbWidth, fbHeight)) {
			return;
		}
		viewportChanged(scale(), fbWidth, fbHeight);
	}

	@Override
	public Dimension screenSize() {
		GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		screenSize.width = vidMode.width();
		screenSize.height = vidMode.height();
		return screenSize;
	}

	@Override
	public void setSize(int width, int height, boolean fullscreen) {
		setDisplayMode(width, height, fullscreen);
	}

	void shutdown() {
		fbSizeCallback.close();
	}

	void setTitle(String title) {
		if (windowId != 0L)
			glfwSetWindowTitle(windowId, title);
	}

	@Override
	protected void init() {
		setDisplayMode(scale.scaledCeil(game.setting.getShowWidth()), scale.scaledCeil(game.setting.getShowHeight()),
				game.setting.fullscreen);
		setTitle(game.setting.appName);
	}

	@Override
	protected void upload(BufferedImage img, LTexture tex) {
		if (img == null) {
			return;
		}

		BufferedImage bitmap = convertImage(img);

		DataBuffer dbuf = bitmap.getRaster().getDataBuffer();
		ByteBuffer bbuf;
		int format, type;

		if (bitmap.getType() == BufferedImage.TYPE_INT_ARGB_PRE) {
			DataBufferInt ibuf = (DataBufferInt) dbuf;
			int iSize = ibuf.getSize() * 4;
			bbuf = checkGetImageBuffer(iSize);
			bbuf.asIntBuffer().put(ibuf.getData());
			bbuf.position(bbuf.position() + iSize);
			bbuf.flip();
			format = GL12.GL_BGRA;
			type = GL12.GL_UNSIGNED_INT_8_8_8_8_REV;

		} else if (bitmap.getType() == BufferedImage.TYPE_4BYTE_ABGR) {
			DataBufferByte dbbuf = (DataBufferByte) dbuf;
			bbuf = checkGetImageBuffer(dbbuf.getSize());
			bbuf.put(dbbuf.getData());
			bbuf.flip();
			format = GL11.GL_RGBA;
			type = GL12.GL_UNSIGNED_INT_8_8_8_8;

		} else {
			int srcWidth = img.getWidth();
			int srcHeight = img.getHeight();

			int texWidth = GLUtils.powerOfTwo(srcWidth);
			int texHeight = GLUtils.powerOfTwo(srcHeight);

			int width = srcWidth;
			int height = srcHeight;

			boolean hasAlpha = img.getColorModel().hasAlpha();

			if (isPowerOfTwo(srcWidth) && isPowerOfTwo(srcHeight)) {
				width = srcWidth;
				height = srcHeight;
				texHeight = srcHeight;
				texWidth = srcWidth;
				ByteBuffer source = NativeSupport.getByteBuffer(
						(byte[]) img.getRaster().getDataElements(0, 0, img.getWidth(), img.getHeight(), null));
				int srcPixelFormat = hasAlpha ? GL20.GL_RGBA : GL20.GL_RGB;
				gl.glBindTexture(GL11.GL_TEXTURE_2D, tex.getID());
				gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, srcPixelFormat, texWidth, texHeight, 0, srcPixelFormat,
						GL20.GL_UNSIGNED_BYTE, source);
				gl.checkError("updateTexture");

				return;
			}

			BufferedImage texImage = new BufferedImage(texWidth, texHeight,
					hasAlpha ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR);

			Graphics2D g = texImage.createGraphics();

			g.drawImage(img, 0, 0, null);

			if (height < texHeight - 1) {
				copyArea(texImage, g, 0, 0, width, 1, 0, texHeight - 1);
				copyArea(texImage, g, 0, height - 1, width, 1, 0, 1);
			}
			if (width < texWidth - 1) {
				copyArea(texImage, g, 0, 0, 1, height, texWidth - 1, 0);
				copyArea(texImage, g, width - 1, 0, 1, height, 1, 0);
			}

			ByteBuffer source = NativeSupport.getByteBuffer((byte[]) texImage.getRaster().getDataElements(0, 0,
					texImage.getWidth(), texImage.getHeight(), null));

			if (texImage != null) {
				texImage.flush();
				texImage = null;
			}
			int srcPixelFormat = hasAlpha ? GL20.GL_RGBA : GL20.GL_RGB;
			gl.glBindTexture(GL11.GL_TEXTURE_2D, tex.getID());
			gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, srcPixelFormat, texWidth, texHeight, 0, srcPixelFormat,
					GL20.GL_UNSIGNED_BYTE, source);
			gl.checkError("updateTexture");
			return;
		}

		gl.glBindTexture(GL11.GL_TEXTURE_2D, tex.getID());
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, bitmap.getWidth(), bitmap.getHeight(), 0, format, type,
				bbuf);
		gl.checkError("updateTexture");
	}

	void copyArea(BufferedImage image, Graphics2D g, int x, int y, int width, int height, int dx, int dy) {
		BufferedImage tmp = image.getSubimage(x, y, width, height);
		g.drawImage(tmp, x + dx, y + dy, null);
		tmp.flush();
		tmp = null;
	}

	protected void setDisplayMode(int width, int height, boolean fullscreen) {
		if (game.setting.fullscreen != fullscreen) {
			game.log().warn("fullscreen cannot be changed via setSize, use config.fullscreen instead");
			return;
		}
		GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		if (width > vidMode.width()) {
			game.log().debug("Capping windowId width at desktop width: " + width + " -> " + vidMode.width());
			width = vidMode.width();
		}
		if (height > vidMode.height()) {
			game.log().debug("Capping windowId height at desktop height: " + height + " -> " + vidMode.height());
			height = vidMode.height();
		}

		glfwSetWindowSize(windowId, width, height);

		if (!game.setting.fullscreen) {
			IntBuffer frameXPos = BufferUtils.createIntBuffer(1);
			IntBuffer frameYPos = BufferUtils.createIntBuffer(1);
			IntBuffer frameWidth = BufferUtils.createIntBuffer(1);
			IntBuffer frameHeight = BufferUtils.createIntBuffer(1);

			glfwGetWindowFrameSize(windowId, frameXPos, frameYPos, frameWidth, frameHeight);

			int frameW = frameWidth.get(0) - frameXPos.get(0);
			int frameH = frameYPos.get(0) + frameHeight.get(0);
			glfwSetWindowPos(windowId, (vidMode.width() - width + frameW) / 2,
					(vidMode.height() - height + frameH) / 2);
		}

		viewSizeM.setSize(width, height);

		IntBuffer fbSize = BufferUtils.createIntBuffer(2);
		long addr = MemoryUtil.memAddress(fbSize);
		nglfwGetFramebufferSize(windowId, addr, addr + 4);
		viewportAndScaleChanged(fbSize.get(0), fbSize.get(1));
	}
}
