package loon.utils;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import java.nio.ByteBuffer;

import loon.core.graphics.device.LImage;
import loon.core.graphics.opengl.GL20;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextureRegion;
import loon.jni.NativeSupport;

public final class ScreenUtils {

	final private static GraphicsEnvironment environment = GraphicsEnvironment
			.getLocalGraphicsEnvironment();

	final private static GraphicsDevice graphicsDevice = environment
			.getDefaultScreenDevice();

	/**
	 * 查询可用的屏幕设备
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public final static DisplayMode searchFullScreenModeDisplay(int width,
			int height) {
		return searchFullScreenModeDisplay(graphicsDevice, width, height);
	}

	/**
	 * 查询可用的屏幕设备
	 * 
	 * @param device
	 * @param width
	 * @param height
	 * @return
	 */
	public final static DisplayMode searchFullScreenModeDisplay(
			GraphicsDevice device, int width, int height) {
		DisplayMode displayModes[] = device.getDisplayModes();
		int currentDisplayPoint = 0;
		DisplayMode fullScreenMode = null;
		DisplayMode normalMode = device.getDisplayMode();
		DisplayMode adisplaymode[] = displayModes;
		int i = 0, length = adisplaymode.length;
		for (int j = length; i < j; i++) {
			DisplayMode mode = adisplaymode[i];
			if (mode.getWidth() == width && mode.getHeight() == height) {
				int point = 0;
				if (normalMode.getBitDepth() == mode.getBitDepth()) {
					point += 40;
				} else {
					point += mode.getBitDepth();
				}
				if (normalMode.getRefreshRate() == mode.getRefreshRate()) {
					point += 5;
				}
				if (currentDisplayPoint < point) {
					fullScreenMode = mode;
					currentDisplayPoint = point;
				}
			}
		}
		return fullScreenMode;
	}
	public static LTextureRegion getFrameBufferTexture() {
		final int w = GLEx.width();
		final int h = GLEx.height();
		return getFrameBufferTexture(0, 0, w, h);
	}

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

	public static byte[] getFrameBufferPixels(boolean flipY) {
		final int w = GLEx.width();
		final int h = GLEx.height();
		return getFrameBufferPixels(0, 0, w, h, flipY);
	}

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
