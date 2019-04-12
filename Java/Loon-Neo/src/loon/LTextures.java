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

import loon.LTexture.Format;
import loon.canvas.Image;
import loon.canvas.NinePatchAbstract.Repeat;
import loon.utils.ObjectMap;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class LTextures {

	private final static TArray<LTexture> TEXTURE_ALL_LIST = new TArray<LTexture>(100);

	private final static ObjectMap<String, LTexture> TEXTURE_LAZY_LIST = new ObjectMap<String, LTexture>(100);

	public static boolean contains(int id) {
		synchronized (TEXTURE_ALL_LIST) {
			for (LTexture tex : LTextures.TEXTURE_ALL_LIST) {
				if (tex.getID() == id) {
					return true;
				}
			}
			return false;
		}
	}

	static boolean delTexture(int id) {
		synchronized (TEXTURE_ALL_LIST) {
			for (LTexture tex : LTextures.TEXTURE_ALL_LIST) {
				if (tex.getID() == id) {
					return TEXTURE_ALL_LIST.remove(tex);
				}
			}
		}
		return false;
	}

	static void putTexture(LTexture tex2d) {
		if (tex2d != null && !tex2d.isClosed() && !tex2d.isChild() && !TEXTURE_ALL_LIST.contains(tex2d)) {
			synchronized (TEXTURE_ALL_LIST) {
				TEXTURE_ALL_LIST.add(tex2d);
			}
		}
	}

	public final static void reload() {
		TArray<LTexture> texs = null;
		synchronized (TEXTURE_ALL_LIST) {
			texs = new TArray<LTexture>(TEXTURE_ALL_LIST);
			TEXTURE_ALL_LIST.clear();
		}
		for (LTexture tex : texs) {
			if (tex != null && !tex.isLoaded() && !tex.isClosed()) {
				tex.reload();
			}
		}
		TEXTURE_ALL_LIST.addAll(texs);
	}

	public final static int getMemSize() {
		if (LSystem._base == null) {
			return 0;
		}
		int memTotal = 0;
		for (LTexture tex : TEXTURE_ALL_LIST) {
			if (tex != null && !tex.isChild() && !tex.isClosed()) {
				memTotal += tex.getMemSize();
			}
		}
		return memTotal;
	}

	public final static void close() {
		if (TEXTURE_ALL_LIST.size > 0) {
			TArray<LTexture> tex2d = new TArray<LTexture>(TEXTURE_ALL_LIST);
			for (LTexture tex : tex2d) {
				if (tex != null && !tex.isChild() && !tex.isClosed()) {
					tex.close();
				}
			}
		}
		TEXTURE_ALL_LIST.clear();
	}

	public static LTexture createTexture(int width, int height, Format config) {
		final LGame base = LSystem._base;
		if (base != null) {
			LTexture texture = base.graphics().createTexture(width, height, config);
			return texture;
		}
		return null;
	}

	public static LTexture newTexture(String path) {
		return newTexture(path, Format.LINEAR);
	}

	public static LTexture newTexture(String path, Format config) {
		if (StringUtils.isEmpty(path)) {
			return null;
		}
		if (LSystem._base == null) {
			return null;
		}
		LSystem.debug("Texture : New " + path + " Loaded");
		return BaseIO.loadImage(path).onHaveToClose(true).createTexture(config);
	}

	public static int count() {
		if (LSystem._base == null) {
			return 0;
		}
		return TEXTURE_ALL_LIST.size;
	}

	public static boolean containsValue(LTexture texture) {
		return TEXTURE_ALL_LIST.contains(texture);
	}

	public static int getRefCount(String fileName) {
		if (StringUtils.isEmpty(fileName)) {
			return 0;
		}
		String key = fileName.trim();
		LTexture texture = TEXTURE_LAZY_LIST.get(key);
		if (texture != null) {
			return texture.refCount;
		}
		for (int i = 0, size = TEXTURE_ALL_LIST.size; i < size; i++) {
			LTexture tex2d = TEXTURE_ALL_LIST.get(i);
			String source = tex2d.getSource();
			if (tex2d != null && source.indexOf("<canvas>") == -1) {
				if (key.equalsIgnoreCase(source) || key.equalsIgnoreCase(tex2d.tmpLazy)) {
					return tex2d.refCount;
				}
			}
		}
		return 0;
	}

	public static LTexture loadNinePatchTexture(String fileName, int x, int y, int w, int h) {
		return loadNinePatchTexture(fileName, null, x, y, w, h, Format.LINEAR);
	}

	public static LTexture loadNinePatchTexture(String fileName, Repeat repeat, int x, int y, int w, int h,
			Format config) {
		if (StringUtils.isEmpty(fileName)) {
			return null;
		}
		synchronized (TEXTURE_LAZY_LIST) {
			String key = fileName.trim().toLowerCase() + (repeat == null ? "" : repeat);
			ObjectMap<String, LTexture> texs = new ObjectMap<String, LTexture>(TEXTURE_LAZY_LIST);
			LTexture texture = texs.get(key);
			if (texture == null) {
				for (LTexture tex : texs.values()) {
					if (tex.tmpLazy != null && tex.tmpLazy.toLowerCase().equals(key.toLowerCase())) {
						texture = tex;
						break;
					}
				}
			}
			if (texture != null && !texture.disposed()) {
				texture.refCount++;
				return texture;
			}
			texture = Image.createImageNicePatch(fileName, x, y, w, h).onHaveToClose(true).createTexture(config);
			texture.tmpLazy = fileName;
			TEXTURE_LAZY_LIST.put(key, texture);
			LSystem.debug("Texture : " + fileName + " Loaded");
			return texture;
		}
	}

	public static LTexture loadTexture(String fileName, Format config) {
		if (StringUtils.isEmpty(fileName)) {
			return null;
		}
		synchronized (TEXTURE_LAZY_LIST) {
			String key = fileName.trim().toLowerCase();
			ObjectMap<String, LTexture> texs = new ObjectMap<String, LTexture>(TEXTURE_LAZY_LIST);
			LTexture texture = texs.get(key);
			if (texture == null) {
				for (LTexture tex : texs.values()) {
					if (tex.tmpLazy != null && tex.tmpLazy.toLowerCase().equals(key.toLowerCase())) {
						texture = tex;
						break;
					}
				}
			}
			if (texture != null && !texture.disposed()) {
				texture.refCount++;
				return texture;
			}
			texture = BaseIO.loadImage(fileName).onHaveToClose(true).createTexture(config);
			texture.tmpLazy = fileName;
			TEXTURE_LAZY_LIST.put(key, texture);

			LSystem.debug("Texture : " + fileName + " Loaded");
			return texture;
		}
	}

	public static LTexture loadTexture(String fileName) {
		return loadTexture(fileName, Format.LINEAR);
	}

	static LTexture removeTexture(LTexture tex) {
		String key = tex.src().trim().toLowerCase();
		LTexture tex2d = TEXTURE_LAZY_LIST.remove(key);
		if (tex2d == null) {
			tex2d = TEXTURE_LAZY_LIST.remove(tex.tmpLazy);
		}
		return tex2d;
	}

	static int removeTextureRef(String name, final boolean remove) {
		if (StringUtils.isEmpty(name)) {
			return 0;
		}
		final LTexture texture = TEXTURE_LAZY_LIST.get(name);
		if (texture != null) {
			return texture.refCount--;
		} else {
			for (int i = 0; i < TEXTURE_ALL_LIST.size; i++) {
				LTexture tex = TEXTURE_ALL_LIST.get(i);
				if (tex != null && tex.tmpLazy.equals(name)) {
					return tex.refCount--;
				}
			}
		}
		return -1;
	}

	static int removeTextureRef(LTexture texture, final boolean remove) {
		return removeTextureRef(texture.tmpLazy, remove);
	}

	public static void destroySourceAllCache() {
		if (TEXTURE_LAZY_LIST.size > 0) {
			TArray<LTexture> textures = new TArray<LTexture>(TEXTURE_LAZY_LIST.values());
			for (int i = 0; i < textures.size; i++) {
				LTexture tex2d = textures.get(i);
				if (tex2d != null && !tex2d.isClosed() && tex2d.getSource() != null
						&& tex2d.getSource().indexOf("<canvas>") == -1) {
					tex2d.refCount = 0;
					tex2d.close(true);
					tex2d = null;
				}
			}
		}
		TEXTURE_LAZY_LIST.clear();
	}

	public static void destroyAllCache() {
		if (TEXTURE_LAZY_LIST.size > 0) {
			TArray<LTexture> textures = new TArray<LTexture>(TEXTURE_LAZY_LIST.values());
			for (int i = 0; i < textures.size; i++) {
				LTexture tex2d = textures.get(i);
				if (tex2d != null && !tex2d.isClosed()) {
					tex2d.refCount = 0;
					tex2d.close(true);
					tex2d = null;
				}
			}
		}
		TEXTURE_LAZY_LIST.clear();
	}

	public static void dispose() {
		destroyAllCache();
		close();
	}
}
