/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package loon.utils;

import java.nio.ByteBuffer;

import loon.core.graphics.device.LImage;
import loon.core.graphics.opengl.GL20;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextureRegion;
import loon.jni.NativeSupport;


/**
 * Class with static helper methods that provide access to the default OpenGL
 * FrameBuffer. These methods can be used to get the entire screen content or a
 * portion thereof.
 * 
 * @author espitz
 */
public final class ScreenUtils {

	/**
	 * Returns the default framebuffer contents as a {@link LTextureRegion} with
	 * a width and height equal to the current screen size. The base
	 * {@link Texture} always has {@link MathUtils#nextPowerOfTwo} dimensions
	 * and RGBA8888 {@link Format}. It can be accessed via
	 * {@link LTextureRegion#getTexture}. The texture is not managed and has to
	 * be reloaded manually on a context loss. The returned TextureRegion is
	 * flipped along the Y axis by default.
	 */
	public static LTextureRegion getFrameBufferTexture() {
		final int w = GLEx.width();
		final int h = GLEx.height();
		return getFrameBufferTexture(0, 0, w, h);
	}

	/**
	 * Returns a portion of the default framebuffer contents specified by x, y,
	 * width and height as a {@link LTextureRegion} with the same dimensions.
	 * The base {@link Texture} always has {@link MathUtils#nextPowerOfTwo}
	 * dimensions and RGBA8888 {@link Format}. It can be accessed via
	 * {@link LTextureRegion#getTexture}. This texture is not managed and has to
	 * be reloaded manually on a context loss. If the width and height specified
	 * are larger than the framebuffer dimensions, the Texture will be padded
	 * accordingly. Pixels that fall outside of the current screen will have
	 * RGBA values of 0.
	 * 
	 * @param x
	 *            the x position of the framebuffer contents to capture
	 * @param y
	 *            the y position of the framebuffer contents to capture
	 * @param w
	 *            the width of the framebuffer contents to capture
	 * @param h
	 *            the height of the framebuffer contents to capture
	 */
	public static LTextureRegion getFrameBufferTexture(int x, int y, int w,
			int h) {
		final int potW = MathUtils.nextPowerOfTwo(w);
		final int potH = MathUtils.nextPowerOfTwo(h);

		final LImage pixmap = getFrameBufferPixmap(x, y, w, h);
		final LImage potPixmap = new LImage(potW, potH, true);
		potPixmap.getLGraphics().drawImage(pixmap, 0, 0);
		LTexture texture = new LTexture(potPixmap);
		potPixmap.getLGraphics().dispose();
		LTextureRegion textureRegion = new LTextureRegion(texture, 0, h, w, -h);
		potPixmap.dispose();
		pixmap.dispose();

		return textureRegion;
	}

	public static LImage getFrameBufferPixmap(int x, int y, int w, int h) {
		GLEx.gl.glPixelStorei(GL20.GL_PACK_ALIGNMENT, 1);
		final LImage pixmap = new LImage(w, h, true);
		ByteBuffer pixels = (ByteBuffer) pixmap.getByteBuffer();
		GLEx.gl.glReadPixels(x, y, w, h, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE,
				pixels);
		return pixmap;
	}

	/**
	 * Returns the default framebuffer contents as a byte[] array with a length
	 * equal to screen width * height * 4. The byte[] will always contain
	 * RGBA8888 data. Because of differences in screen and image origins the
	 * framebuffer contents should be flipped along the Y axis if you intend
	 * save them to disk as a bitmap. Flipping is not a cheap operation, so use
	 * this functionality wisely.
	 * 
	 * @param flipY
	 *            whether to flip pixels along Y axis
	 */
	public static byte[] getFrameBufferPixels(boolean flipY) {
		final int w = GLEx.width();
		final int h = GLEx.height();
		return getFrameBufferPixels(0, 0, w, h, flipY);
	}

	/**
	 * Returns a portion of the default framebuffer contents specified by x, y,
	 * width and height, as a byte[] array with a length equal to the specified
	 * width * height * 4. The byte[] will always contain RGBA8888 data. If the
	 * width and height specified are larger than the framebuffer dimensions,
	 * the Texture will be padded accordingly. Pixels that fall outside of the
	 * current screen will have RGBA values of 0. Because of differences in
	 * screen and image origins the framebuffer contents should be flipped along
	 * the Y axis if you intend save them to disk as a bitmap. Flipping is not
	 * cheap operation, so use this functionality wisely.
	 * 
	 * @param flipY
	 *            whether to flip pixels along Y axis
	 */
	public static byte[] getFrameBufferPixels(int x, int y, int w, int h,
			boolean flipY) {
		GLEx.gl.glPixelStorei(GL20.GL_PACK_ALIGNMENT, 1);
		final ByteBuffer pixels = NativeSupport.newByteBuffer(w * h * 4);
		GLEx.gl.glReadPixels(x, y, w, h, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE,
				pixels);
		final int numBytes = w * h * 4;
		byte[] lines = new byte[numBytes];
		if (flipY) {
			final int numBytesPerLine = w * 4;
			for (int i = 0; i < h; i++) {
				pixels.position((h - i - 1) * numBytesPerLine);
				pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
			}
		} else {
			pixels.clear();
			pixels.get(lines);
		}
		return lines;

	}
}
