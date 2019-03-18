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
import loon.action.sprite.Entity;
import loon.action.sprite.Sprite;
import loon.canvas.Image;
import loon.canvas.NinePatchAbstract.Repeat;
import loon.canvas.TGA;
import loon.component.DefUI;
import loon.geom.Vector2f;
import loon.utils.ArrayByte;
import loon.utils.ArrayByteReader;
import loon.utils.GifDecoder;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.reply.GoFuture;

public abstract class BaseIO extends DefUI {

	public final static GoFuture<String> loadAsynText(String path) {
		final LGame base = LSystem._base;
		if (base != null) {
			try {
				return base.assets().getText(path);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	public final static String loadText(String path) {
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

	public final static LTexture newTexture(String path) {
		return LTextures.newTexture(path);
	}

	public final static LTexture newTexture(String path, Format config) {
		return LTextures.newTexture(path, config);
	}

	public final static LTexture loadTexture(String path) {
		return LTextures.loadTexture(path);
	}

	public final static LTexture loadTexture(String path, Format config) {
		return LTextures.loadTexture(path, config);
	}

	public final static LTexture loadNinePatchTexture(String path, int x, int y, int w, int h) {
		return LTextures.loadNinePatchTexture(path, x, y, w, h);
	}

	public final static LTexture loadNinePatchTexture(String path, Repeat repeat, int x, int y, int w, int h, Format config) {
		return LTextures.loadNinePatchTexture(path, repeat, x, y, w, h, config);
	}

	public final static Image loadImage(String path) {
		return loadImage(path, true);
	}

	public final static Image loadImage(String path, boolean syn) {
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
					throw LSystem.runThrow(e.getMessage());
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

	public final static GoFuture<byte[]> loadAsynBytes(String path) {
		final LGame base = LSystem._base;
		if (base != null) {
			try {
				return base.assets().getBytes(path);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	public final static byte[] loadBytes(String path) {
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

	public final static Sound loadSound(String path) {
		final LGame base = LSystem._base;
		if (base != null) {
			return base.assets().getSound(path);
		}
		return null;
	}

	public final static Sound loadMusic(String path) {
		final LGame base = LSystem._base;
		if (base != null) {
			return base.assets().getMusic(path);
		}
		return null;
	}

	public final static Image loadRemoteImage(String url) {
		return loadRemoteImage(url, 0, 0);
	}

	public final static Image loadRemoteImage(String url, int w, int h) {
		final LGame base = LSystem._base;
		if (base != null) {
			return base.assets().getRemoteImage(url, w, h);
		}
		return null;
	}

	public final static Sprite createSprite(String path) {
		return new Sprite(path);
	}

	public final static Sprite createSprite(LTexture tex2d) {
		return new Sprite(tex2d);
	}

	public final static Sprite createSprite(String path, float scale) {
		Sprite spr = new Sprite(path);
		spr.setScale(scale);
		return spr;
	}

	public final static Sprite createSprite(LTexture tex2d, float scale) {
		Sprite spr = new Sprite(tex2d);
		spr.setScale(scale);
		return spr;
	}

	public final static Sprite createSprite(String path, Vector2f pos) {
		Sprite spr = new Sprite(path);
		spr.setLocation(pos);
		return spr;
	}

	public final static Sprite createSprite(LTexture tex2d, Vector2f pos) {
		Sprite spr = new Sprite(tex2d);
		spr.setLocation(pos);
		return spr;
	}

	public final static Entity createEntity(String path) {
		return new Entity(path);
	}

	public final static Entity createEntity(LTexture tex2d) {
		return new Entity(tex2d);
	}

	public final static Entity createEntity(String path, float scale) {
		Entity spr = new Entity(path);
		spr.setScale(scale);
		return spr;
	}

	public final static Entity createEntity(LTexture tex2d, float scale) {
		Entity spr = new Entity(tex2d);
		spr.setScale(scale);
		return spr;
	}

	public final static Entity createEntity(String path, Vector2f pos) {
		Entity spr = new Entity(path);
		spr.setLocation(pos);
		return spr;
	}

	public final static Entity createEntity(LTexture tex2d, Vector2f pos) {
		Entity spr = new Entity(tex2d);
		spr.setLocation(pos);
		return spr;
	}

	public final static TArray<Sprite> createMultiSprite(String[] path, Vector2f[] pos) {
		return createMultiSprite(path, pos, 1f);
	}

	public final static TArray<Sprite> createMultiSprite(String[] path, Vector2f[] pos, float scale) {
		if (StringUtils.isEmpty(path)) {
			return new TArray<Sprite>();
		}
		final int size = path.length;
		TArray<Sprite> list = new TArray<Sprite>(path.length);
		for (int i = 0; i < size; i++) {
			Sprite sprite = createSprite(path[i], pos[i]);
			sprite.setScale(scale);
		}
		return list;
	}

	public final static TArray<Entity> createMultiEntity(String[] path, Vector2f[] pos) {
		return createMultiEntity(path, pos, 1f);
	}

	public final static TArray<Entity> createMultiEntity(String[] path, Vector2f[] pos, float scale) {
		if (StringUtils.isEmpty(path)) {
			return new TArray<Entity>();
		}
		final int size = path.length;
		TArray<Entity> list = new TArray<Entity>(path.length);
		for (int i = 0; i < size; i++) {
			Entity sprite = createEntity(path[i], pos[i]);
			sprite.setScale(scale);
		}
		return list;
	}
}
