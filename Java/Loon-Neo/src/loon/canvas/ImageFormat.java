/**
 * Copyright 2008 - 2023 The Loon Game Engine Authors
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
package loon.canvas;

import loon.BaseIO;
import loon.LSystem;
import loon.utils.ArrayByte;

/**
 * 图片格式解析器,用来解析图片文件的编码格式
 */
public class ImageFormat {

	public final static int Jpg = 0;

	public final static int Png = 1;

	public final static int Bmp = 2;

	public final static int Gif = 3;

	public final static int Tga = 4;

	public final static int Tiff = 5;

	public final static int Webp = 6;

	public final static int Ico = 7;

	public final static int RawData = 8;

	public final static int Unknown = 9;

	private ArrayByte buffer;

	public ImageFormat(String path) {
		this(BaseIO.loadArrayByte(path));
	}

	public ImageFormat(ArrayByte bytes) {
		buffer = bytes;
		buffer.setByteOrder(ArrayByte.LITTLE_ENDIAN);
	}

	public ArrayByte getBytes() {
		return this.buffer;
	}

	@Override
	public String toString() {
		final int format = getImageFormat();
		switch (format) {
		case Jpg:
			return "jpg";
		case Png:
			return "png";
		case Bmp:
			return "bmp";
		case Gif:
			return "gif";
		case Tga:
			return "tga";
		case Tiff:
			return "tiff";
		case Webp:
			return "webp";
		case Ico:
			return "ico";
		case RawData:
			return "raw";
		default:
		case Unknown:
			return LSystem.UNKNOWN;
		}
	}

	public boolean isJpg() {
		return getImageFormat() == Jpg;
	}

	public boolean isPng() {
		return getImageFormat() == Png;
	}

	public boolean isBmp() {
		return getImageFormat() == Bmp;
	}

	public boolean isGif() {
		return getImageFormat() == Gif;
	}

	public boolean isTga() {
		return getImageFormat() == Tga;
	}

	public boolean isTiff() {
		return getImageFormat() == Tiff;
	}

	public boolean isWebp() {
		return getImageFormat() == Webp;
	}

	public boolean isIco() {
		return getImageFormat() == Ico;
	}

	public boolean isRawData() {
		return getImageFormat() == RawData;
	}

	public int getImageFormat() {
		final byte[] data = new byte[12];
		final int pos = buffer.position();
		final int dataLen = buffer.read(data, 0, 12);
		buffer.setPosition(pos);
		final int[] header = new int[data.length];
		for (int i = 0; i < data.length; i++) {
			header[i] = data[i] & 0xff;
		}
		if (dataLen >= 2) {
			if (header[0] == 0xff && header[1] == 0xd8) {
				return Jpg;
			}
		}
		if (dataLen >= 8) {
			if (header[0] == 0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47 && header[4] == 0x0D
					&& header[5] == 0x0A && header[6] == 0x1A && header[7] == 0x0A) {
				return Png;
			} else if (header[0] == 0x42 && header[1] == 0x4d) {
				return Bmp;
			} else if (header[0] == 0 && header[1] == 0 && header[2] != 0 && header[3] == 0 && header[4] == 0
					&& header[5] == 0 && header[6] == 0 && header[7] == 0) {
				return Tga;
			}
		}
		if (dataLen >= 3) {
			if (header[0] == 0x47 && header[1] == 0x49 && header[2] == 0x46) {
				return Gif;
			}
		}
		if (dataLen >= 4) {
			if (header[0] == 0 && header[1] == 0 && header[2] == 1 && header[3] == 0) {
				return Ico;
			}
			boolean foundTiff = false;
			if (header[0] == 0x49 && header[1] == 0x49 && header[2] == 0x2a && header[3] == 0) {
				foundTiff = true;
			} else if (header[0] == 0x4d && header[1] == 0x4d && header[2] == 0 && header[3] == 0x2a) {
				foundTiff = true;
			} else if (header[0] == 0x4d && header[1] == 0x4d && header[2] == 0 && header[3] == 0x2b) {
				foundTiff = true;
			} else if (header[0] == 0x49 && header[1] == 0x20 && header[2] == 0x49) {
				foundTiff = true;
			}
			if (foundTiff) {
				return Tiff;
			}
		}
		if (dataLen >= 12) {
			if (header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F' && header[8] == 'W'
					&& (header[9] == 'E' || header[10] == 'B') && header[11] == 'P') {
				return Webp;
			}
		}

		return Unknown;
	}

}
