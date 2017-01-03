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
package loon;

import java.io.IOException;

import loon.LTexture.Format;
import loon.canvas.Image;
import loon.canvas.TGA;
import loon.utils.ArrayByte;
import loon.utils.ArrayByteReader;
import loon.utils.GifDecoder;

public abstract class BaseIO {

	public static String loadText(String path) {
		final LGame base = LSystem._base;
		if (base != null) {
			try {
				return base.assets().getTextSync(path);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	public static LTexture newTexture(String path) {
		return LTextures.newTexture(path);
	}

	public static LTexture newTexture(String path, Format config) {
		return LTextures.newTexture(path, config);
	}

	public static LTexture loadTexture(String path) {
		return LTextures.loadTexture(path);
	}

	public static LTexture loadTexture(String path, Format config) {
		return LTextures.loadTexture(path, config);
	}

	public static Image loadImage(String path) {
		return loadImage(path, true);
	}

	public static Image loadImage(String path, boolean syn) {
		final LGame base = LSystem._base;
		if (base != null) {
			String ext = LSystem.getExtension(path);
			if ("tga".equalsIgnoreCase(ext)) {
				Image tmp = null;
				try {
					TGA.State tga = TGA.load(path);
					if (tga != null) {
						tmp = Image.createImage(tga.width, tga.height);
						tmp.setPixels(tga.pixels, tga.width, tga.height);
						tga.close();
						tga = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				return tmp;
			}
			//发现有些手机机型对gif解码不全|||……
			if ("gif".equalsIgnoreCase(ext) && LSystem.isMobile()) {
				ArrayByte bytes = BaseIO.loadArrayByte(path);
				GifDecoder gif = new GifDecoder();
				gif.readStatus(bytes);
				if (gif.getFrameCount() > 0) {
					return gif.getImage();
				}
			}
			if (syn) {
				return base.assets().getImageSync(path);
			} else {
				return base.assets().getImage(path);
			}
		}
		return null;
	}

	public static ArrayByteReader loadArrayByteReader(String path) {
		final byte[] buffer = loadBytes(path);
		if (buffer == null) {
			return new ArrayByteReader(new ArrayByte(1));
		}
		return new ArrayByteReader(new ArrayByte(buffer));
	}

	public static ArrayByte loadArrayByte(String path) {
		final byte[] buffer = loadBytes(path);
		if (buffer == null) {
			return new ArrayByte(1);
		}
		return new ArrayByte(buffer);
	}

	public static byte[] loadBytes(String path) {
		final LGame base = LSystem._base;
		if (base != null) {
			try {
				return base.assets().getBytesSync(path);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	public static Sound loadSound(String path) {
		final LGame base = LSystem._base;
		if (base != null) {
			return base.assets().getSound(path);
		}
		return null;
	}

	public static Sound loadMusic(String path) {
		final LGame base = LSystem._base;
		if (base != null) {
			return base.assets().getMusic(path);
		}
		return null;
	}

	public static Image loadRemoteImage(String url) {
		return loadRemoteImage(url, 0, 0);
	}

	public static Image loadRemoteImage(String url, int w, int h) {
		final LGame base = LSystem._base;
		if (base != null) {
			return base.assets().getRemoteImage(url, w, h);
		}
		return null;
	}

}
