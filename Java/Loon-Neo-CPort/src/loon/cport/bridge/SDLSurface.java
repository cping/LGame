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
package loon.cport.bridge;

import loon.LRelease;
import loon.geom.RangeI;
import loon.geom.RectI;

public final class SDLSurface implements LRelease {

	public final static SDLSurface createBMP(String path) {
		long handle = SDLCall.loadBMPHandle(path);
		return new SDLSurface(handle);
	}

	public final static SDLSurface create(String path) {
		long handle = STBCall.loadPathToSDLSurface(path);
		return new SDLSurface(handle);
	}

	public final static SDLSurface createRGB(int flags, int width, int height, int depth, int rmask, int gmask,
			int bmask, int amask) {
		long handle = SDLCall.createRGBSurface(flags, width, height, depth, rmask, gmask, bmask, amask);
		return new SDLSurface(handle);
	}

	public final static SDLSurface createRGBFrom(int[] pixels, int width, int height, int format) {
		long handle = SDLCall.createRGBSurfaceFrom(pixels, width, height, format);
		return new SDLSurface(handle);
	}

	private boolean _closed;

	private long _surfaceHandle;

	private SDLSurface(long handle) {
		_surfaceHandle = handle;
	}

	public void lock() {
		SDLCall.lockSurface(_surfaceHandle);
	}

	public void unlock() {
		SDLCall.unlockSurface(_surfaceHandle);
	}

	public boolean isMUSTLock() {
		return SDLCall.MUSTLockSurface(_surfaceHandle);
	}

	public SDLSurface convertFormat(int pixel_format, int flags) {
		long newHandle = SDLCall.convertSurfaceFormat(_surfaceHandle, pixel_format, flags);
		return new SDLSurface(newHandle);
	}

	public int[] getPixels(int x, int y, int w, int h) {
		return SDLCall.getSurfacePixels(_surfaceHandle, x, y, w, h);
	}

	public int[] getPixels32() {
		return getPixels32(0);
	}

	public int[] getPixels32(int order) {
		return SDLCall.getSurfacePixels32(_surfaceHandle, order);
	}

	public void setPixel(int x, int y, int pixel) {
		SDLCall.setSurfacePixel(_surfaceHandle, x, y, pixel);
	}

	public void setPixel32(int x, int y, int pixel) {
		SDLCall.setSurfacePixel32(_surfaceHandle, x, y, pixel);
	}

	public void setPixels32(int x, int y, int w, int h, int[] pixels) {
		SDLCall.setSurfacePixels32(_surfaceHandle, x, y, w, h, pixels);
	}

	public void setBlendMode(int blend) {
		SDLCall.setSurfaceBlendMode(_surfaceHandle, blend);
	}

	public int getBlendMode() {
		return SDLCall.getSurfaceBlendMode(_surfaceHandle);
	}

	public void setClipRect(int x, int y, int w, int h) {
		SDLCall.setSurfaceClipRect(_surfaceHandle, x, y, w, h);
	}

	public void setClipRect(RectI rect) {
		if (rect == null) {
			return;
		}
		SDLCall.setSurfaceClipRect(_surfaceHandle, rect.x, rect.y, rect.width, rect.height);
	}

	public void fillRect(int x, int y, int w, int h, int r, int g, int b, int a) {
		SDLCall.fillRectSurface(_surfaceHandle, x, y, w, h, r, g, b, a);
	}

	public RectI getClipRect() {
		final RectI rect = new RectI();
		int[] temp_rect = SDLCall.getSurfaceClipRect(_surfaceHandle);
		rect.set(temp_rect[0], temp_rect[1], temp_rect[2], temp_rect[3]);
		temp_rect = null;
		return rect;
	}

	public RangeI getSize() {
		int[] temp_rect = SDLCall.getSurfaceSize(_surfaceHandle);
		final RangeI size = new RangeI(temp_rect[0], temp_rect[1]);
		temp_rect = null;
		return size;
	}

	public int getFormat() {
		return SDLCall.getSurfaceFormat(_surfaceHandle);
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		if (_surfaceHandle != 0 && !_closed) {
			SDLCall.freeSurface(_surfaceHandle);
			_surfaceHandle = 0;
			_closed = true;
		}
	}

}
