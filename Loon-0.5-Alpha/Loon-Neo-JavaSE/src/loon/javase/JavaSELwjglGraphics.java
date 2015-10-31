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
package loon.javase;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.util.Arrays;

import loon.*;
import loon.geom.Dimension;
import loon.javase.JavaSEGame.JavaSetting;
import loon.jni.NativeSupport;
import loon.opengl.GL20;
import loon.utils.GLUtils;
import loon.utils.Scale;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class JavaSELwjglGraphics extends JavaSEGraphics {

	private Dimension screenSize = new Dimension();

	public JavaSELwjglGraphics(JavaSEGame game) {
		super(game, new JavaSELwjglGL20(), Scale.ONE);
	}

	boolean isPowerOfTwo(int value) {
		return value != 0 && (value & value - 1) == 0;
	}

	void checkScaleFactor() {
		float scaleFactor = Display.getPixelScaleFactor();
		if (scaleFactor != scale.factor) {
			updateViewport(new Scale(scaleFactor), Display.getWidth(),
					Display.getHeight());
		}
	}

	@Override
	public Dimension screenSize() {
		DisplayMode mode = Display.getDesktopDisplayMode();
		screenSize.width = scale.invScaled(mode.getWidth());
		screenSize.height = scale.invScaled(mode.getHeight());
		return screenSize;
	}

	@Override
	public void setSize(int width, int height, boolean fullscreen) {
		setDisplayMode(width, height, fullscreen);
	}

	@Override
	protected void init() {
		if(game.setting instanceof JavaSetting){
			JavaSetting setting = (JavaSetting)game.setting;
			Display.setVSyncEnabled(setting.vSyncEnabled);
		}else{
			Display.setVSyncEnabled(true);
		}
		if (game.setting.width_zoom > 0 && game.setting.height_zoom > 0) {
			setDisplayMode(scale.scaledCeil(game.setting.width_zoom),
					scale.scaledCeil(game.setting.height_zoom),
					game.setting.fullscreen);
		} else {
			setDisplayMode(scale.scaledCeil(game.setting.width),
					scale.scaledCeil(game.setting.height),
					game.setting.fullscreen);
		}
		try {
			System.setProperty("org.lwjgl.opengl.Display.enableHighDPI", "true");
			Display.create();
			checkScaleFactor();
		} catch (LWJGLException e) {
			throw new RuntimeException(e);
		}
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
				ByteBuffer source = NativeSupport.getByteBuffer((byte[]) img
						.getRaster().getDataElements(0, 0, img.getWidth(),
								img.getHeight(), null));
				int srcPixelFormat = hasAlpha ? GL20.GL_RGBA : GL20.GL_RGB;
				gl.glBindTexture(GL11.GL_TEXTURE_2D, tex.getID());
				gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, srcPixelFormat,
						texWidth, texHeight, 0, srcPixelFormat,
						GL20.GL_UNSIGNED_BYTE, source);
				gl.checkError("updateTexture");

				return;
			}

			BufferedImage texImage = new BufferedImage(texWidth, texHeight,
					hasAlpha ? BufferedImage.TYPE_4BYTE_ABGR
							: BufferedImage.TYPE_3BYTE_BGR);

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

			ByteBuffer source = NativeSupport.getByteBuffer((byte[]) texImage
					.getRaster().getDataElements(0, 0, texImage.getWidth(),
							texImage.getHeight(), null));

			if (texImage != null) {
				texImage.flush();
				texImage = null;
			}
			int srcPixelFormat = hasAlpha ? GL20.GL_RGBA : GL20.GL_RGB;
			gl.glBindTexture(GL11.GL_TEXTURE_2D, tex.getID());
			gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, srcPixelFormat, texWidth,
					texHeight, 0, srcPixelFormat, GL20.GL_UNSIGNED_BYTE, source);
			gl.checkError("updateTexture");
			return;
		}

		gl.glBindTexture(GL11.GL_TEXTURE_2D, tex.getID());
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA,
				bitmap.getWidth(), bitmap.getHeight(), 0, format, type, bbuf);
		gl.checkError("updateTexture");
	}

	void copyArea(BufferedImage image, Graphics2D g, int x, int y, int width,
			int height, int dx, int dy) {
		BufferedImage tmp = image.getSubimage(x, y, width, height);
		g.drawImage(tmp, x + dx, y + dy, null);
		tmp.flush();
		tmp = null;
	}

	protected void setDisplayMode(int width, int height, boolean fullscreen) {
		try {
			DisplayMode mode = Display.getDisplayMode();
			if (fullscreen == Display.isFullscreen()
					&& mode.getWidth() == width && mode.getHeight() == height){
				return;
			}
			if (!fullscreen){
				mode = new DisplayMode(width, height);
			}
			else {
				DisplayMode matching = null;
				for (DisplayMode dm : Display.getAvailableDisplayModes()) {
					if (dm.getWidth() == width && dm.getHeight() == height
							&& dm.isFullscreenCapable()) {
						matching = dm;
					}
				}
				if (matching != null) {
					mode = matching;
				} else {
					game.log().info(
							"Could not find a matching fullscreen mode, available: "
									+ Arrays.asList(Display
											.getAvailableDisplayModes()));
				}
			}
			game.log().debug(
					"Loon display mode: " + mode + ", fullscreen: "
							+ fullscreen);
			Scale scale;
			if (fullscreen) {
				Display.setDisplayModeAndFullscreen(mode);
				scale = Scale.ONE;
			} else {
				Display.setDisplayMode(mode);
				scale = new Scale(Display.getPixelScaleFactor());
			}
			updateViewport(scale, mode.getWidth(), mode.getHeight());

		} catch (LWJGLException ex) {
			throw new RuntimeException(ex);
		}
	}
}
