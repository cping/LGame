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
package loon.utils.res.loaders;

import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.utils.LIterator;
import loon.utils.ObjectMap;
import loon.utils.ObjectMap.Values;
import loon.utils.PathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.xml.XMLListener;

public class PreloadAssets implements LRelease {

	private TArray<AssetLoader> _loads;

	private ObjectMap<PreloadItem, TArray<AssetLoader>> _preloadMap;

	public PreloadAssets() {
		this._loads = new TArray<AssetLoader>();
		this._preloadMap = new ObjectMap<PreloadItem, TArray<AssetLoader>>();
	}

	public PreloadAssets load(AssetLoader loader) {
		if (loader == null) {
			throw new LSysException("AssetLoader cannot be null");
		}
		if (!_loads.contains(loader)) {
			this._loads.add(loader);
		}
		return this;
	}

	private boolean checkPathRedundancy(PreloadItem item, String path) {
		if (path == null) {
			throw new LSysException("The Path cannot be empty");
		}
		for (AssetLoader loader : _loads) {
			if (loader != null) {
				if (loader.item().equals(item) && path.equals(loader.getPath())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkNickNameRedundancy(PreloadItem item, String nickname) {
		if (nickname == null) {
			throw new LSysException("The Nickname cannot be empty");
		}
		for (AssetLoader loader : _loads) {
			if (loader != null) {
				if (loader.item().equals(item) && nickname.equals(loader.getNickName())) {
					return true;
				}
			}
		}
		return false;
	}

	private void checkAssets(PreloadItem item, String path, String nickname) {
		if (checkPathRedundancy(item, path)) {
			throw new LSysException("This preload object [" + path + "] already exists");
		}
		if (nickname != null && nickname.length() > 0) {
			if (checkNickNameRedundancy(item, nickname)) {
				throw new LSysException("The preload object nickname [" + nickname + "] already exists");
			}
		}
	}

	public boolean isLoaded(PreloadItem item, String path) {
		TArray<AssetLoader> loaders = _preloadMap.get(item);
		if (loaders == null) {
			return false;
		}
		if (loaders != null) {
			for (AssetLoader loader : loaders) {
				if (loader != null) {
					final String pathName = loader.getPath();
					final String nickName = loader.getNickName();
					if (loader != null && (path.equals(pathName) || path.equals(nickName))) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public PreloadAssets load(String path) {
		return load(path, null);
	}

	public PreloadAssets load(String path, String nickname) {
		String ext = PathUtils.getExtension(path).trim().toLowerCase();
		if ("xml".equals(ext) || "tmx".equals(ext)) {
			return xml(path, nickname, null);
		} else if ("json".equals(ext) || "tmj".equals(ext)) {
			return json(path, nickname);
		} else if ("txt".equals(ext) || "chr".equals(ext)) {
			return text(path, nickname);
		} else if ("i18n".equals(ext)) {
			return I18N(path, nickname);
		} else if ("pack".equals(ext)) {
			return texturePack(path, nickname);
		} else if ("fnt".equals(ext)) {
			return bitmapFont(path, nickname);
		} else if ("dbf".equals(ext)) {
			return glyphFont(path, nickname);
		} else if ("bytes".equals(ext)) {
			return bytes(path, nickname);
		} else if ("cfg".equals(ext) || "config".equals(ext)) {
			return config(path, nickname);
		} else if (LSystem.isImage(ext)) {
			return texture(path, nickname);
		} else if (LSystem.isAudio(ext)) {
			return sound(path, nickname);
		} else {
			throw new LSysException("The file " + path + " cannot be recognized, so it loading failed.");
		}
	}

	public PreloadAssets text(String path) {
		return text(path, null);
	}

	public PreloadAssets text(String path, String nickname) {
		checkAssets(PreloadItem.Text, path, nickname);
		return load(new TextAssetLoader(path, nickname));
	}

	public PreloadAssets texturePack(String path) {
		return texturePack(path, null);
	}

	public PreloadAssets texturePack(String path, String nickname) {
		checkAssets(PreloadItem.TexturePack, path, nickname);
		return load(new TextureAssetLoader(path, nickname));
	}

	public PreloadAssets res(String path) {
		return res(path, null);
	}

	public PreloadAssets res(String path, String nickname) {
		checkAssets(PreloadItem.Res, path, nickname);
		return load(new ResAssetLoader(path, nickname));
	}

	public PreloadAssets bytes(String path) {
		return bytes(path, null);
	}

	public PreloadAssets bytes(String path, String nickname) {
		checkAssets(PreloadItem.Bytes, path, nickname);
		return load(new BytesAssetLoader(path, nickname));
	}

	public PreloadAssets glyphFont(String path) {
		return glyphFont(path, null, -1);
	}

	public PreloadAssets glyphFont(String path, int size) {
		return glyphFont(path, null, size);
	}

	public PreloadAssets glyphFont(String path, String nickname) {
		return glyphFont(path, nickname, -1);
	}

	public PreloadAssets glyphFont(String path, String nickname, int size) {
		checkAssets(PreloadItem.BitmapDistributionFont, path, nickname);
		return load(new BDFontAssetLoader(path, nickname, size));
	}

	public PreloadAssets bitmapFont(String path) {
		return bitmapFont(path, null);
	}

	public PreloadAssets bitmapFont(String path, String imgPath) {
		return bitmapFont(path, null, imgPath);
	}

	public PreloadAssets bitmapFont(String path, String nickname, String imgPath) {
		checkAssets(PreloadItem.BitmapFont, path, nickname);
		return load(new BMFontAssetLoader(path, nickname, imgPath));
	}

	public PreloadAssets config(String path) {
		return config(path, null);
	}

	public PreloadAssets config(String path, String nickname) {
		checkAssets(PreloadItem.Config, path, nickname);
		return load(new ConfigAssetLoader(path, nickname));
	}

	public PreloadAssets context(String text, String context) {
		return context(text, null, context);
	}

	public PreloadAssets context(String text, String nickname, String context) {
		checkAssets(PreloadItem.Context, text, nickname);
		return load(new ContextAssetLoader(text, nickname, context));
	}

	public PreloadAssets texture(String path) {
		return texture(path, null);
	}

	public PreloadAssets texture(String path, String nickname) {
		checkAssets(PreloadItem.Texture, path, nickname);
		return load(new TextureAssetLoader(path, nickname));
	}

	public PreloadAssets json(String path) {
		return json(path, null);
	}

	public PreloadAssets json(String path, String nickname) {
		checkAssets(PreloadItem.Json, path, nickname);
		return load(new JsonAssetLoader(path, nickname));
	}

	public PreloadAssets sound(String path) {
		return sound(path, null);
	}

	public PreloadAssets sound(String path, String nickname) {
		checkAssets(PreloadItem.Sound, path, nickname);
		return load(new SoundAssetLoader(path, nickname));
	}

	public PreloadAssets music(String path) {
		return music(path, null);
	}

	public PreloadAssets music(String path, String nickname) {
		checkAssets(PreloadItem.Music, path, nickname);
		return load(new MusicAssetLoader(path, nickname));
	}

	public PreloadAssets image(String path) {
		return image(path, null);
	}

	public PreloadAssets image(String path, String nickname) {
		checkAssets(PreloadItem.Image, path, nickname);
		return load(new ImageAssetLoader(path, nickname));
	}

	public PreloadAssets pixmap(String path) {
		return pixmap(path, null);
	}

	public PreloadAssets pixmap(String path, String nickname) {
		checkAssets(PreloadItem.Pixmap, path, nickname);
		return load(new PixmapAssetLoader(path, nickname));
	}

	public PreloadAssets xml(String path) {
		return xml(path, null, null);
	}

	public PreloadAssets xml(String path, String nickname) {
		return xml(path, nickname, null);
	}

	public PreloadAssets xml(String path, XMLListener listener) {
		return xml(path, null, listener);
	}

	public PreloadAssets xml(String path, String nickname, XMLListener listener) {
		checkAssets(PreloadItem.Xml, path, nickname);
		return load(new XmlAssetLoader(path, nickname, listener));
	}

	public PreloadAssets I18N(String path) {
		return I18N(path, null);
	}

	public PreloadAssets I18N(String path, String nickname) {
		checkAssets(PreloadItem.I18N, path, nickname);
		return load(new I18NAssetLoader(path, nickname));
	}

	private void loaderGetException(String path) {
		throw new LSysException("The resource [" + path + "] does not exist");
	}

	public TextAssetLoader getText(String path) {
		AssetLoader loader = getAssetLoader(PreloadItem.Text, path);
		if (loader != null && loader instanceof TextAssetLoader) {
			return (TextAssetLoader) loader;
		}
		loaderGetException(path);
		return null;
	}

	public BMFontAssetLoader getBitmapFont(String path) {
		AssetLoader loader = getAssetLoader(PreloadItem.BitmapFont, path);
		if (loader != null && loader instanceof BMFontAssetLoader) {
			return (BMFontAssetLoader) loader;
		}
		loaderGetException(path);
		return null;
	}

	public BDFontAssetLoader getBitmapDistributionFont(String path) {
		AssetLoader loader = getAssetLoader(PreloadItem.BitmapDistributionFont, path);
		if (loader != null && loader instanceof BDFontAssetLoader) {
			return (BDFontAssetLoader) loader;
		}
		loaderGetException(path);
		return null;
	}

	public ContextAssetLoader getContext(String text) {
		AssetLoader loader = getAssetLoader(PreloadItem.Context, text);
		if (loader != null && loader instanceof ContextAssetLoader) {
			return (ContextAssetLoader) loader;
		}
		loaderGetException(text);
		return null;
	}

	public BytesAssetLoader getBytes(String path) {
		AssetLoader loader = getAssetLoader(PreloadItem.Bytes, path);
		if (loader != null && loader instanceof BytesAssetLoader) {
			return (BytesAssetLoader) loader;
		}
		loaderGetException(path);
		return null;
	}

	public TextureAssetLoader getTexture(String path) {
		AssetLoader loader = getAssetLoader(PreloadItem.Texture, path);
		if (loader != null && loader instanceof TextureAssetLoader) {
			return (TextureAssetLoader) loader;
		}
		loaderGetException(path);
		return null;
	}

	public TexturePackAssetLoader getTexturePack(String path) {
		AssetLoader loader = getAssetLoader(PreloadItem.TexturePack, path);
		if (loader != null && loader instanceof TexturePackAssetLoader) {
			return (TexturePackAssetLoader) loader;
		}
		loaderGetException(path);
		return null;
	}

	public ConfigAssetLoader getConfig(String path) {
		AssetLoader loader = getAssetLoader(PreloadItem.Config, path);
		if (loader != null && loader instanceof ConfigAssetLoader) {
			return (ConfigAssetLoader) loader;
		}
		loaderGetException(path);
		return null;
	}

	public JsonAssetLoader getJson(String path) {
		AssetLoader loader = getAssetLoader(PreloadItem.Json, path);
		if (loader != null && loader instanceof JsonAssetLoader) {
			return (JsonAssetLoader) loader;
		}
		loaderGetException(path);
		return null;
	}

	public I18NAssetLoader getI18N(String path) {
		AssetLoader loader = getAssetLoader(PreloadItem.I18N, path);
		if (loader != null && loader instanceof I18NAssetLoader) {
			return (I18NAssetLoader) loader;
		}
		loaderGetException(path);
		return null;
	}

	public SoundAssetLoader getSound(String path) {
		AssetLoader loader = getAssetLoader(PreloadItem.Sound, path);
		if (loader != null && loader instanceof SoundAssetLoader) {
			return (SoundAssetLoader) loader;
		}
		loaderGetException(path);
		return null;
	}

	public MusicAssetLoader getMusic(String path) {
		AssetLoader loader = getAssetLoader(PreloadItem.Music, path);
		if (loader != null && loader instanceof MusicAssetLoader) {
			return (MusicAssetLoader) loader;
		}
		loaderGetException(path);
		return null;
	}

	public ImageAssetLoader getImage(String path) {
		AssetLoader loader = getAssetLoader(PreloadItem.Image, path);
		if (loader != null && loader instanceof ImageAssetLoader) {
			return (ImageAssetLoader) loader;
		}
		loaderGetException(path);
		return null;
	}

	public PixmapAssetLoader getPixmap(String path) {
		AssetLoader loader = getAssetLoader(PreloadItem.Pixmap, path);
		if (loader != null && loader instanceof PixmapAssetLoader) {
			return (PixmapAssetLoader) loader;
		}
		loaderGetException(path);
		return null;
	}

	public XmlAssetLoader getXml(String path) {
		AssetLoader loader = getAssetLoader(PreloadItem.Xml, path);
		if (loader != null && loader instanceof XmlAssetLoader) {
			return (XmlAssetLoader) loader;
		}
		loaderGetException(path);
		return null;
	}

	public ResAssetLoader getRes(String path) {
		AssetLoader loader = getAssetLoader(PreloadItem.Res, path);
		if (loader != null && loader instanceof ResAssetLoader) {
			return (ResAssetLoader) loader;
		}
		loaderGetException(path);
		return null;
	}

	public TArray<AssetLoader> getAssetLoaderList(PreloadItem item) {
		return _preloadMap.get(item);
	}

	public AssetLoader getAssetLoader(PreloadItem item, String path) {

		if (StringUtils.isEmpty(path)) {
			return null;
		}

		TArray<AssetLoader> loaders = _preloadMap.get(item);

		if (loaders != null) {
			for (AssetLoader loader : loaders) {
				if (loader != null) {
					final String pathName = loader.getPath();
					final String nickName = loader.getNickName();
					if (loader != null && (path.equals(pathName) || path.equals(nickName))) {
						return loader;
					}
				}
			}
		}

		return null;
	}

	public boolean detection() {

		if (_loads.size == 0) {
			return false;
		}

		boolean result = false;
		AssetLoader loader = this._loads.first();

		if (loader != null && (result = loader.completed())) {
			PreloadItem item = loader.item();
			TArray<AssetLoader> list = _preloadMap.get(item);
			if (list == null) {
				list = new TArray<AssetLoader>();
			}
			if (!list.contains(loader)) {
				list.add(loader);
			}
			_preloadMap.put(item, list);
			_loads.removeFirst();

			return result;
		}

		return false;
	}

	public boolean completed() {
		return _loads.isEmpty();
	}

	public int waiting() {
		return _loads.size;
	}

	@Override
	public void close() {
		Values<TArray<AssetLoader>> list = _preloadMap.values();
		for (LIterator<TArray<AssetLoader>> it = list.iterator(); it.hasNext();) {
			TArray<AssetLoader> loaders = it.next();
			if (loaders != null) {
				for (AssetLoader loader : loaders) {
					if (loader != null) {
						loader.close();
					}
				}
			}
		}
		_preloadMap.clear();
	}

}
