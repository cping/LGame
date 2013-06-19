package loon.core.graphics.opengl;

import java.util.HashMap;

import loon.core.LSystem;
import loon.core.event.Updateable;
import loon.core.graphics.opengl.LTexture.Format;


/**
 * Copyright 2008 - 2011
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
 * @version 0.1
 */

public class LTextures {

	private static HashMap<String, LTexture> lazyTextures = new HashMap<String, LTexture>(
			100);

	public static int count() {
		return lazyTextures.size();
	}

	public static LTexture loadTexture(String path) {
		return loadTexture(path, Format.DEFAULT);
	}

	public static boolean containsValue(LTexture texture) {
		return lazyTextures.containsValue(texture);
	}

	public static int getRefCount(LTexture texture) {
		return getRefCount(texture.lazyName);
	}

	public static int getRefCount(String fileName) {
		String key = fileName.trim().toLowerCase();
		LTexture texture = lazyTextures.get(key);
		if (texture != null) {
			return texture.refCount;
		}
		return 0;
	}

	public static LTexture loadTexture(String fileName, Format format) {
		if (fileName == null) {
			return null;
		}
		synchronized (lazyTextures) {
			String key = fileName.trim().toLowerCase();
			LTexture texture = lazyTextures.get(key);
			if (texture != null && !texture.isClose) {
				texture.refCount++;
				return texture;
			}
			texture = new LTexture(fileName, format);
			texture.lazyName = fileName;
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
			String key = tex2d.lazyName == null ? String.valueOf(id)
					: tex2d.lazyName;
			LTexture texture = lazyTextures.get(key);
			if (texture != null && !texture.isClose) {
				texture.refCount++;
				return texture;
			}
			texture = tex2d;
			texture.lazyName = key;
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
				if (!texture.isClose) {
					Updateable u = new Updateable() {
						@Override
						public void action() {
							synchronized (texture) {
								if (texture.textureID > 0) {
									if (texture.parent == null) {
										GLEx.deleteTexture(texture.textureID);
									}
									texture.textureID = -1;
									GLEx.deleteBuffer(texture.bufferID);
									texture.bufferID = -1;
								}
								texture.isLoaded = false;
								texture.isClose = true;
								LTextureBatch.isBatchCacheDitry = true;
							}
						}
					};
					LSystem.load(u);
					if (texture.imageData != null && texture.parent == null) {
						if (texture.imageData.fileName == null) {
							texture.imageData.source = null;
							texture.imageData = null;
						}
					}
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
		return removeTexture(texture.lazyName, remove);
	}

	public static void reload() {
		synchronized (lazyTextures) {
			if (lazyTextures.size() > 0) {
				for (final LTexture texture : lazyTextures.values()) {
					if (texture != null) {
						texture.isLoaded = false;
						texture.reload = true;
						texture._hashCode = 1;
						if (texture.childs != null) {
							Updateable u = new Updateable() {
								@Override
								public void action() {
									texture.loadTexture();
									for (int i = 0; i < texture.childs.size(); i++) {
										LTexture child = texture.childs.get(i);
										if (child != null) {
											child.textureID = texture.textureID;
											child.isLoaded = texture.isLoaded;
											child.reload = texture.reload;
											if (GLEx.isVbo()) {
												child.bufferID = GLEx
														.createBufferID();
												GLEx.bufferDataARR(
														child.bufferID,
														texture.data,
														GL11.GL_STATIC_DRAW);
											}
										}
									}
									LTextureBatch.isBatchCacheDitry = true;
								}
							};
							LSystem.load(u);
						}
					}
				}
			}
			GLUtils.reload();
		}
	}

	public static void disposeAll() {
		if (lazyTextures.size() > 0) {
			for (LTexture tex2d : lazyTextures.values()) {
				if (tex2d != null && !tex2d.isClose) {
					tex2d.refCount = 0;
					tex2d.dispose(false);
					tex2d = null;
				}
			}
			lazyTextures.clear();
		}
		LSTRDictionary.dispose();
	}

	public static void destroyAll() {
		if (lazyTextures.size() > 0) {
			for (LTexture tex2d : lazyTextures.values()) {
				if (tex2d != null && !tex2d.isClose) {
					tex2d.refCount = 0;
					tex2d.destroy(false);
					tex2d = null;
				}
			}
			lazyTextures.clear();
		}
		LSTRDictionary.dispose();
	}
}
