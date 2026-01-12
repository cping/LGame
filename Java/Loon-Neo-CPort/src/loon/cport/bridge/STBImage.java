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
import loon.LSystem;
import loon.cport.bridge.STBFont.VFormat;

public final class STBImage implements LRelease {

	public final static STBImage createImage(String path) {
		long handle = STBCall.loadPathToImage(path);
		if (handle == -1) {
			LSystem.error(path + " not found !");
		}
		return new STBImage(handle);
	}

	public final static STBImage createImage(byte[] buffer) {
		return createImage(buffer, buffer.length);
	}

	public final static STBImage createImage(byte[] buffer, int len) {
		long handle = STBCall.loadBytesToImage(buffer, len);
		if (handle == -1) {
			LSystem.error("create Image " + buffer + " error !");
		}
		return new STBImage(handle);
	}

	private final VFormat _sizeformat = new VFormat();

	private final int[] _formatSize = new int[3];

	private boolean _closed;

	private long _imageHandle;

	private int _width, _height;

	private int _format;

	private STBImage(long handle) {
		_imageHandle = handle;
	}

	public VFormat getSizeFormat() {
		if (_sizeformat.width > 0 && _sizeformat.height > 0) {
			return _sizeformat;
		}
		STBCall.getSizeFormat(_imageHandle, _formatSize);
		_sizeformat.width = _formatSize[0];
		_sizeformat.height = _formatSize[1];
		_sizeformat.format = _formatSize[2];
		return _sizeformat;
	}

	public byte[] getPixels() {
		final VFormat format = getSizeFormat();
		final byte[] bytes = new byte[format.width * format.height];
		STBCall.getImagePixels(_imageHandle, bytes);
		return bytes;
	}

	public int[] getImagePixels32() {
		final VFormat format = getSizeFormat();
		final int[] pixels = new int[format.width * format.height];
		STBCall.getImagePixels32(_imageHandle, format.width, format.height, pixels);
		return pixels;
	}

	public int[] getImageFormatPixels32(int f) {
		final VFormat format = getSizeFormat();
		final int[] pixels = new int[format.width * format.height];
		STBCall.getImagePixels32(_imageHandle, f, pixels);
		return pixels;
	}

	public int getWidth() {
		if (_width > 0) {
			return _width;
		}
		return _width = STBCall.getImageWidth(_imageHandle);
	}

	public int getHeight() {
		if (_height > 0) {
			return _height;
		}
		return _height = STBCall.getImageHeight(_imageHandle);
	}

	public int getFormat() {
		if (_format > 0) {
			return _format;
		}
		return _format = STBCall.getImageFormat(_imageHandle);
	}

	public String getFailureReason() {
		return STBCall.getImageFailureReason();
	}

	public long getHandle() {
		return _imageHandle;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		if (_imageHandle != 0 && !_closed) {
			STBCall.freeImage(_imageHandle);
			_imageHandle = 0;
			_width = _height = _format = 0;
			_closed = true;
		}
	}

}
