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
	
	public final static int RawData = 7;
	
	public final static int Unknown = 8;

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
		case RawData:
			return "raw";
		default:
		case Unknown:
			return LSystem.UNKNOWN;
		}
	}

	public int getImageFormat() {
		byte[] data = new byte[8];
		int pos = buffer.position();
		int dataLen = buffer.read(data, 0, 8);
		buffer.setPosition(pos);
		int[] header = new int[data.length];
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
			if (header[0] == 0x47 && header[1] == 0x49 && header[1] == 0x46) {
				return Gif;
			}
		}
		if (dataLen >= 2) {
			if ((header[0] == 0x49 && header[1] == 0x49) || (header[0] == 0x4d && header[1] == 0x4d)) {
				return Tiff;
			}
		}
		return Unknown;
	}

}
