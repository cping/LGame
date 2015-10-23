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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import loon.LTexture.Format;
import loon.canvas.Image;

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
			if (syn) {
				return base.assets().getImageSync(path);
			} else {
				return base.assets().getImage(path);
			}
		}
		return null;
	}

	public static InputStream loadStream(String path) {
		return new ByteArrayInputStream(loadBytes(path));
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
