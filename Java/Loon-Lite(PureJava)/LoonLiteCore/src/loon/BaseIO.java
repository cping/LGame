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

import loon.canvas.Image;
import loon.canvas.TGA;
import loon.component.DefUI;
import loon.utils.ArrayByte;
import loon.utils.ArrayByteReader;
import loon.utils.GifDecoder;
import loon.utils.StringUtils;
import loon.utils.parse.StrTokenizer;
import loon.utils.reply.GoFuture;

/**
 * Loon的基础资源加载器
 */
public abstract class BaseIO extends DefUI {

	public final static GoFuture<String> loadAsynText(String path) {
		final LGame base = LSystem.base();
		if (base != null) {
			try {
				return base.assets().getText(path);
			} catch (Throwable e) {
				base.log().debug("The path [" + path + "] is null !");
				return null;
			}
		}
		return null;
	}

	public final static String loadText(String path) {
		final LGame base = LSystem.base();
		if (base != null) {
			try {
				return base.assets().getTextSync(path);
			} catch (Throwable e) {
				base.log().debug("The path [" + path + "] is null !");
				return null;
			}
		}
		return null;
	}

	public final static LTexture newTexture(String path) {
		return LSystem.newTexture(path);
	}

	public final static LTexture texture(String path) {
		return LSystem.loadTexture(path);
	}

	public final static LTexture loadTexture(String path) {
		return LSystem.loadTexture(path);
	}

	public final static Image image(String path) {
		return loadImage(path, true);
	}

	public final static Image image(String path, boolean syn) {
		return loadImage(path, syn);
	}

	public final static Image loadImage(String path) {
		return loadImage(path, true);
	}

	public final static Image loadImage(String path, boolean syn) {
		final LGame base = LSystem.base();
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
				} catch (Exception e) {
					throw new LSysException(e.getMessage());
				}
				return tmp;
			}
			// 发现有些手机机型对gif解码不全|||……
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

	public final static ArrayByteReader loadArrayByteReader(String path) {
		final byte[] buffer = loadBytes(path);
		if (buffer == null) {
			return new ArrayByteReader(new ArrayByte(1));
		}
		return new ArrayByteReader(new ArrayByte(buffer));
	}

	public final static ArrayByte loadArrayByte(String path) {
		final byte[] buffer = loadBytes(path);
		if (buffer == null) {
			return new ArrayByte(1);
		}
		return new ArrayByte(buffer);
	}

	public final static StrTokenizer loadStrTokenizer(String path) {
		return loadStrTokenizer(path, null);
	}

	public final static StrTokenizer loadStrTokenizer(String path, String delimiters) {
		if (StringUtils.isEmpty(path)) {
			return new StrTokenizer("");
		}
		String text = loadText(path);
		if (text == null) {
			return new StrTokenizer("");
		}
		if (delimiters == null) {
			return new StrTokenizer(text);
		} else {
			return new StrTokenizer(text, delimiters);
		}
	}

	public final static GoFuture<byte[]> loadAsynBytes(String path) {
		final LGame base = LSystem.base();
		if (base != null) {
			try {
				return base.assets().getBytes(path);
			} catch (Throwable e) {
				throw new LSysException("The file " + path + " not found !");
			}
		}
		return null;
	}

	public final static byte[] loadBytes(String path) {
		final LGame base = LSystem.base();
		if (base != null) {
			try {
				return base.assets().getBytesSync(path);
			} catch (Throwable e) {
				throw new LSysException("The file " + path + " not found !");
			}
		}
		return null;
	}

	public final static Sound loadSound(String path) {
		final LGame base = LSystem.base();
		if (base != null) {
			return base.assets().getSound(path);
		}
		return null;
	}

	public final static Sound loadMusic(String path) {
		final LGame base = LSystem.base();
		if (base != null) {
			return base.assets().getMusic(path);
		}
		return null;
	}

	public final static Image loadRemoteImage(String url) {
		return loadRemoteImage(url, 0, 0);
	}

	public final static Image loadRemoteImage(String url, int w, int h) {
		final LGame base = LSystem.base();
		if (base != null) {
			return base.assets().getRemoteImage(url, w, h);
		}
		return null;
	}

	public final static Object loadJsonObject(String path) {
		return loadJsonObjectContext(loadText(path));
	}

	public final static Object loadJsonObjectContext(String text) {
		if (StringUtils.isEmpty(text)) {
			return null;
		}
		final LGame base = LSystem.base();
		if (base != null) {
			try {
				return base.json().parse(text);
			} catch (Exception e) {
				try {
					return base.json().parseArray(text);
				} catch (Exception ex) {
					base.log().debug("This data cannot be converted to json !");
					return null;
				}
			}
		}
		return null;
	}

}
