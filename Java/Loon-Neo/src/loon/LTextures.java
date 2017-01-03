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
import loon.utils.ObjectMap;
import loon.utils.TArray;

public class LTextures {

	private final static TArray<LTexture> textureList = new TArray<LTexture>(
			100);

	private final static ObjectMap<String, LTexture> lazyTextures = new ObjectMap<String, LTexture>(
			100);

	public static boolean contains(int id) {
		synchronized (textureList) {
			for (LTexture tex : LTextures.textureList) {
				if (tex.getID() == id) {
					return true;
				}
			}
			return false;
		}
	}

	static boolean delTexture(int id) {
		synchronized (textureList) {
			for (LTexture tex : LTextures.textureList) {
				if (tex.getID() == id) {
					return textureList.remove(tex);
				}
			}
		}
		return false;
	}

	static void putTexture(LTexture tex2d) {
		if (tex2d != null && !tex2d.isClose() && !tex2d.isChild()
				&& !textureList.contains(tex2d)) {
			synchronized (textureList) {
				textureList.add(tex2d);
			}
		}
	}

	public final static void reload() {
		TArray<LTexture> texs = null;
		synchronized (textureList) {
			texs = new TArray<LTexture>(textureList);
			textureList.clear();
		}
		for (LTexture tex : texs) {
			if (tex != null && !tex.isLoaded() && !tex.isClose()) {
				tex.reload();
			}
		}
		textureList.addAll(texs);
	}

	public final static int getMemSize() {
		int memTotal = 0;
		for (LTexture tex : textureList) {
			if (tex != null && !tex.isChild() && !tex.isClose()) {
				memTotal += tex.getMemSize();
			}
		}
		return memTotal;
	}

	public final static void close() {
		if (textureList.size > 0) {
			TArray<LTexture> tex2d = new TArray<LTexture>(textureList);
			for (LTexture tex : tex2d) {
				if (tex != null && !tex.isChild() && !tex.isClose()) {
					tex.close();
				}
			}
		}
		textureList.clear();
	}

	public static LTexture createTexture(int width, int height, Format config) {
		final LGame base = LSystem._base;
		if (base != null) {
			LTexture texture = base.graphics().createTexture(width, height,
					config);
			return texture;
		}
		return null;
	}

	public static LTexture newTexture(String path) {
		if (LSystem._base == null) {
			return null;
		}
		return BaseIO.loadImage(path).onHaveToClose(true).texture();
	}

	public static LTexture newTexture(String path, Format config) {
		if (LSystem._base == null) {
			return null;
		}
		return BaseIO.loadImage(path).onHaveToClose(true).createTexture(config);
	}

	public static int count() {
		return textureList.size;
	}

	public static boolean containsValue(LTexture texture) {
		return textureList.contains(texture);
	}

	public static int getRefCount(LTexture texture) {
		return texture.refCount;
	}

	public static int getRefCount(String fileName) {
		String key = fileName.trim().toLowerCase();
		LTexture texture = lazyTextures.get(key);
		if (texture != null) {
			return texture.refCount;
		}
		for (int i = 0, size = textureList.size; i < size; i++) {
			LTexture tex2d = textureList.get(i);
			if (tex2d != null) {
				if (key.equals(tex2d.getSource())
						|| key.equals(tex2d.getSource().toLowerCase())) {
					return tex2d.refCount;
				}
			}
		}
		return 0;
	}

	public static LTexture loadTexture(String fileName, Format config) {
		if (fileName == null) {
			return null;
		}
		synchronized (lazyTextures) {
			String key = fileName.trim().toLowerCase();
			LTexture texture = lazyTextures.get(key);
			if (texture != null && !texture.isClose()) {
				texture.refCount++;
				return texture;
			}
			texture = BaseIO.loadImage(fileName).onHaveToClose(true)
					.createTexture(config);
			texture.tmpLazy = fileName;
			lazyTextures.put(key, texture);
			return texture;
		}
	}

	public static LTexture loadTexture(String fileName) {
		return loadTexture(fileName, Format.LINEAR);
	}

	static LTexture removeTexture(LTexture tex) {
		String key = tex.src().trim().toLowerCase();
		LTexture tex2d = lazyTextures.remove(key);
		if (tex2d == null) {
			tex2d = lazyTextures.remove(tex.tmpLazy);
		}
		return tex2d;
	}

	static int removeTextureRef(String name, final boolean remove) {
		final LTexture texture = lazyTextures.get(name);
		if (texture != null) {
			return texture.refCount--;
		} else {
			for (int i = 0; i < textureList.size; i++) {
				LTexture tex = textureList.get(i);
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
		if (lazyTextures.size > 0) {
			TArray<LTexture> textures = new TArray<LTexture>(
					lazyTextures.values());
			for (int i = 0; i < textures.size; i++) {
				LTexture tex2d = textures.get(i);
				if (tex2d != null && !tex2d.isClose()
						&& tex2d.getSource() != null
						&& tex2d.getSource().indexOf("<canvas>") == -1) {
					tex2d.refCount = 0;
					tex2d.close(true);
					lazyTextures.remove(tex2d.tmpLazy);
					tex2d = null;
				}
			}
		}
		lazyTextures.clear();
	}

	public static void destroyAllCache() {
		if (lazyTextures.size > 0) {
			TArray<LTexture> textures = new TArray<LTexture>(
					lazyTextures.values());
			for (int i = 0; i < textures.size; i++) {
				LTexture tex2d = textures.get(i);
				if (tex2d != null && !tex2d.isClose()) {
					tex2d.refCount = 0;
					tex2d.close(true);
					lazyTextures.remove(tex2d.tmpLazy);
					tex2d = null;
				}
			}
		}
		lazyTextures.clear();
	}

	public static void dispose() {
		destroyAllCache();
		close();
	}
}
