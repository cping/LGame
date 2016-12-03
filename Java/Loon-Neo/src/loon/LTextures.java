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
import loon.event.Updateable;
import loon.utils.ObjectMap;
import loon.utils.TArray;

public class LTextures {

	private static ObjectMap<String, LTexture> lazyTextures = new ObjectMap<String, LTexture>(
			100);

	public static LTexture newTexture(String path) {
		if (LSystem._base == null) {
			return null;
		}
		return BaseIO.loadImage(path).texture();
	}

	public static LTexture newTexture(String path, Format config) {
		if (LSystem._base == null) {
			return null;
		}
		return BaseIO.loadImage(path).createTexture(config);
	}

	public static int count() {
		return lazyTextures.size;
	}

	public static boolean containsValue(LTexture texture) {
		return lazyTextures.containsValue(texture);
	}

	public static int getRefCount(LTexture texture) {
		return getRefCount(texture.tmpLazy);
	}

	public static int getRefCount(String fileName) {
		String key = fileName.trim().toLowerCase();
		LTexture texture = lazyTextures.get(key);
		if (texture != null) {
			return texture.refCount;
		}
		return 0;
	}

	public static LTexture createTexture(int width, int height, Format config) {
		final LGame base = LSystem._base;
		if (base != null) {
			LTexture texture = base.graphics().createTexture(width, height,
					config);
			return loadTexture(texture);
		}
		return null;
	}

	public static LTexture loadTexture(String fileName, Format config) {
		if (fileName == null) {
			return null;
		}
		synchronized (lazyTextures) {
			String key = fileName.trim().toLowerCase();
			LTexture texture = lazyTextures.get(key);
			if (texture != null && !texture.disposed()) {
				texture.refCount++;
				return texture;
			}
			texture = BaseIO.loadImage(fileName).createTexture(config);
			texture.tmpLazy = fileName;
			lazyTextures.put(key, texture);
			return texture;
		}
	}

	public static LTexture loadTexture(String fileName) {
		if (fileName == null) {
			return null;
		}
		synchronized (lazyTextures) {
			String key = fileName.trim().toLowerCase();
			LTexture texture = lazyTextures.get(key);
			if (texture != null && !texture.disposed()) {
				texture.refCount++;
				return texture;
			}
			texture = BaseIO.loadImage(fileName).texture();
			texture.tmpLazy = fileName;
			lazyTextures.put(key, texture);
			return texture;
		}
	}

	public static LTexture loadTexture(LTexture texture) {
		return loadTexture(System.currentTimeMillis(), texture);
	}

	public static LTexture loadTexture(long id, LTexture tex2d) {
		if (tex2d == null) {
			return null;
		}
		synchronized (lazyTextures) {
			String key = tex2d.tmpLazy == null ? String.valueOf(id)
					: tex2d.tmpLazy;
			LTexture texture = lazyTextures.get(key);
			if (texture != null && !texture.disposed()) {
				texture.refCount++;
				return texture;
			}
			texture = tex2d;
			texture.tmpLazy = key;
			lazyTextures.put(key, texture);
			return texture;
		}
	}

	public static int removeTexture(String name, final boolean remove) {
		final LTexture texture = lazyTextures.get(name);
		if (texture != null) {
			if (texture.refCount <= 0) {
				if (remove) {
					synchronized (lazyTextures) {
						lazyTextures.remove(name);
					}
				}
				if (!texture._disposed) {
					Updateable u = new Updateable() {
						@Override
						public void action(Object a) {
							synchronized (texture) {
								texture.free();
								LTextureBatch.isBatchCacheDitry = true;
							}
						}
					};
					LSystem.load(u);
					if (texture.childs != null) {
						texture.childs.clear();
						texture.childs = null;
					}
				}
			} else {
				texture.refCount--;
			}
			return texture.refCount;
		}
		return -1;
	}

	public static int removeTexture(LTexture texture, final boolean remove) {
		return removeTexture(texture.tmpLazy, remove);
	}

	public static void destroySourceAll() {
		if (lazyTextures.size > 0) {
			TArray<LTexture> textures = new TArray<LTexture>(
					lazyTextures.values());
			for (int i = 0; i < textures.size; i++) {
				LTexture tex2d = textures.get(i);
				if (tex2d != null && !tex2d.disposed()
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

	public static void destroyAll() {
		if (lazyTextures.size > 0) {
			TArray<LTexture> textures = new TArray<LTexture>(
					lazyTextures.values());
			for (int i = 0; i < textures.size; i++) {
				LTexture tex2d = textures.get(i);
				if (tex2d != null && !tex2d.disposed()) {
					tex2d.refCount = 0;
					tex2d.close(true);
					lazyTextures.remove(tex2d.tmpLazy);
					tex2d = null;
				}
			}
		}
		lazyTextures.clear();
	}
}
