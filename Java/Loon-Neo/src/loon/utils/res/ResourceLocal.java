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
package loon.utils.res;

import loon.BaseIO;
import loon.Json;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.Sound;
import loon.canvas.Image;
import loon.canvas.Pixmap;
import loon.event.EventDispatcher;
import loon.event.IEventListener;
import loon.font.BMFont;
import loon.utils.ArrayMap;
import loon.utils.ObjectMap;
import loon.utils.ObjectMap.Keys;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.TimeUtils;
import loon.utils.xml.XMLDocument;
import loon.utils.xml.XMLParser;

public class ResourceLocal extends ResourceGetter implements IEventListener {

	private ObjectMap<String, TArray<String>> _groupTable;

	private ObjectMap<String, ResourceItem> _resourceTable;

	private ArrayMap _dataTable;

	private boolean _initFlag = false;

	private String _path = null;

	public ResourceLocal(String path) {
		this();
		_path = path;
	}

	public ResourceLocal() {
		_dataTable = new ArrayMap();
		_resourceTable = new ObjectMap<String, ResourceItem>();
		_groupTable = new ObjectMap<String, TArray<String>>();
	}

	private void init() {
		init(_path);
	}

	public void init(String path) {
		if ((!StringUtils.isEmpty(path) && !path.equals(_path)) || !_initFlag) {
			_path = path;
			String jsonText = BaseIO.loadText(path);
			if (jsonText == null && path.indexOf('.') == -1) {
				jsonText = BaseIO.loadText(path + ".json");
			}
			Json.Object jsonObj = LSystem.base().json().parse(jsonText);
			Json.Array groupsList = jsonObj.getArray("groups");

			if (groupsList != null && groupsList.length() > 0) {
				for (int i = 0; i < groupsList.length(); i++) {
					Json.Object group = groupsList.getObject(i);
					String groupName = group.getString("name");
					if (groupName == null) {
						groupName = "G" + TimeUtils.millis();
					}
					String keyStrings = group.getString("keys");
					String[] keys = StringUtils.split(keyStrings, ',');
					int size = keys.length;
					TArray<String> list = new TArray<String>(size);
					for (int j = 0; j < size; j++) {
						String result = keys[j];
						if (!StringUtils.isEmpty(result)) {
							list.add(result);
						}
					}
					_groupTable.put(groupName, list);
				}
			}
			Json.Array resList = jsonObj.getArray("resources");
			if (resList != null && resList.length() > 0) {
				for (int i = 0; i < resList.length(); i++) {
					Json.Object resItem = resList.getObject(i);
					String itemName = resItem.getString("name");
					String itemType = resItem.getString("type");
					String itemUrl = resItem.getString("url");
					ResourceItem item = new ResourceItem(itemName, itemType, itemUrl);
					if (itemType.equals("sheet")) {
						String subkeys = resItem.getString("subkeys");
						item.subkeys = subkeys;
					}
					_resourceTable.put(item.name(), item);
				}
			}
			_initFlag = true;
		}
	}

	@Override
	public TArray<String> getGroupKeys(String name) {
		return _groupTable.get(name);
	}

	@Override
	public Keys<String> getGroupNames(String name) {
		return _groupTable.keys();
	}

	@Override
	public XMLDocument getXml(String name) {
		init();
		XMLDocument obj = null;
		ResourceItem item = getResItem(name, ResourceType.TYPE_XML);
		if (!_dataTable.containsKey(name)) {
			obj = loadXML(item);
			_dataTable.put(name, obj);
		} else {
			obj = (XMLDocument) _dataTable.get(name);
			if (obj == null || obj.isClosed()) {
				obj = loadXML(item);
				_dataTable.put(name, obj);
			}
		}
		return obj;
	}
	
	protected XMLDocument loadXML(ResourceItem item){
		return XMLParser.parse(item.url());
	}

	@Override
	public String getText(String name) {
		init();
		String context = null;
		ResourceItem item = getResItem(name, ResourceType.TYPE_TXT);
		if (!_dataTable.containsKey(name)) {
			context = BaseIO.loadText(item.url());
			_dataTable.put(name, context);
		} else {
			context = (String) _dataTable.get(name);
			if (context == null) {
				context = BaseIO.loadText(item.url());
				_dataTable.put(name, context);
			}
		}
		return context;
	}

	@Override
	public Json.Object getJson(String name) {
		init();
		Json.Object obj = null;
		ResourceItem item = getResItem(name, ResourceType.TYPE_JSON);
		if (!_dataTable.containsKey(name)) {
			obj = loadJsonObject(item);
			_dataTable.put(name, obj);
		} else {
			obj = (Json.Object) _dataTable.get(name);
			if (obj == null) {
				obj = loadJsonObject(item);
				_dataTable.put(name, obj);
			}
		}
		return obj;
	}

	protected Json.Object loadJsonObject(ResourceItem item) {
		return LSystem.base().json().parse(BaseIO.loadText(item.url()));
	}

	@Override
	public Image getImage(String name) {
		init();
		Image image = null;
		ResourceItem item = getResItem(name, ResourceType.TYPE_IMAGE);
		if (!_dataTable.containsKey(name)) {
			image = loadImage(item);
			_dataTable.put(name, image);
		} else {
			image = (Image) _dataTable.get(name);
			if (image == null || image.isClosed()) {
				image = loadImage(item);
				_dataTable.put(name, image);
			}
		}
		return image;
	}

	protected Image loadImage(ResourceItem item) {
		return BaseIO.loadImage(item.url());
	}

	@Override
	public Pixmap getPixmap(String name) {
		init();
		Pixmap pixmap = null;
		ResourceItem item = getResItem(name, ResourceType.TYPE_PIXMAP);
		if (!_dataTable.containsKey(name)) {
			pixmap = loadPixmap(item);
			_dataTable.put(name, pixmap);
		} else {
			pixmap = (Pixmap) _dataTable.get(name);
			if (pixmap == null || pixmap.isClosed()) {
				pixmap = loadPixmap(item);
				_dataTable.put(name, pixmap);
			}
		}
		return pixmap;
	}

	protected Pixmap loadPixmap(ResourceItem item) {
		String url = item.url();
		Image img = Image.createImage(url);
		Pixmap pixmap = img.getPixmap();
		img.close();
		img = null;
		return pixmap;
	}

	public LTexture getLTexture(String name) {
		return getTexture(name).img();
	}

	@Override
	public Texture getTexture(String name) {
		init();
		Texture tex = null;
		ResourceItem item = getResItem(name, ResourceType.TYPE_TEXTURE);
		if (!_dataTable.containsKey(name)) {
			tex = loadTexture(item);
			_dataTable.put(name, tex);
		} else {
			tex = (Texture) _dataTable.get(name);
			if (tex == null || tex.isClosed()) {
				tex = loadTexture(item);
				_dataTable.put(name, tex);
			}
		}
		return tex;
	}

	protected Texture loadTexture(ResourceItem item) {
		return new Texture(item.url());
	}

	@Override
	public MovieSpriteSheet getSheet(String name) {
		init();
		MovieSpriteSheet sset = null;
		ResourceItem item = getResItem(name, ResourceType.TYPE_SHEET);
		if (!_dataTable.containsKey(name)) {
			sset = loadSpriteSheet(item);
			_dataTable.put(name, sset);
		} else {
			sset = (MovieSpriteSheet) _dataTable.get(name);
			if (sset == null || sset.isClosed()) {
				sset = loadSpriteSheet(item);
				_dataTable.put(name, sset);
			}
		}
		return sset;
	}

	protected MovieSpriteSheet loadSpriteSheet(ResourceItem item) {
		MovieSpriteSheet sset = null;
		String url = item.url();
		Json.Object jsonObj = LSystem.base().json().parse(BaseIO.loadText(url));
		String imagePath = url;
		String ext = LSystem.getExtension(imagePath);
		LTexture sheet = null;
		if (!LSystem.isImage(ext)) {
			sheet = LTextures.loadTexture(StringUtils.replaceIgnoreCase(imagePath, ext, "png"));
		} else if (StringUtils.isEmpty(ext)) {
			sheet = LTextures.loadTexture(imagePath + ".png");
		} else {
			sheet = LTextures.loadTexture(imagePath);
		}
		if (item.subkeys != null) {
			sset = new MovieSpriteSheet(jsonObj, StringUtils.split(item.subkeys, ','), sheet);
		} else {
			sset = new MovieSpriteSheet(jsonObj, sheet);
		}
		return sset;
	}

	@Override
	public BMFont getBMFont(String name) {
		init();
		BMFont fs = null;
		ResourceItem item = getResItem(name, ResourceType.TYPE_BMFNT);
		if (!_dataTable.containsKey(name)) {
			fs = loadBMFont(item);
			_dataTable.put(name, fs);
		} else {
			fs = (BMFont) _dataTable.get(name);
			if (fs == null || fs.isClosed()) {
				fs = loadBMFont(item);
				_dataTable.put(name, fs);
			}
		}
		return fs;
	}

	protected BMFont loadBMFont(ResourceItem item) {
		BMFont fs = null;
		try {
			fs = new BMFont(item.url());
		} catch (Exception e) {
			LSystem.error("BMFont " + item.url() + " not found!", e);
		}
		return fs;
	}

	@Override
	public FontSheet getFontSheet(String name) {
		init();
		FontSheet fs = null;
		ResourceItem item = getResItem(name, ResourceType.TYPE_FONT);
		if (!_dataTable.containsKey(name)) {
			fs = loadFontSheet(item);
			_dataTable.put(name, fs);
		} else {
			fs = (FontSheet) _dataTable.get(name);
			if (fs == null || fs.isClosed()) {
				fs = loadFontSheet(item);
				_dataTable.put(name, fs);
			}
		}
		return fs;
	}

	protected FontSheet loadFontSheet(ResourceItem item) {
		return new FontSheet(item.url());
	}

	public Sound getSound(String name, String type) {
		init();
		Sound sound = null;
		ResourceItem item = getResItem(name, ResourceType.TYPE_SOUND);
		if (!_dataTable.containsKey(name)) {
			sound = loadSound(item);
			_dataTable.put(name, sound);
		} else {
			sound = (Sound) _dataTable.get(name);
			if (sound == null) {
				sound = loadSound(item);
				_dataTable.put(name, sound);
			}
		}
		return sound;
	}

	protected Sound loadSound(ResourceItem item) {
		return LSystem.base().assets().getSound(item.url());
	}

	@Override
	public String getURL(String name) {
		init();
		String url;
		if (_resourceTable.containsKey(name)) {
			ResourceItem item = (ResourceItem) _resourceTable.get(name);
			url = item.url();
		} else {
			url = name;
		}
		return url;
	}

	@Override
	public void release(String name) {
		Object obj = _dataTable.remove(name);
		if (obj != null) {
			if (obj instanceof Texture) {
				((Texture) obj).close();
			} else if (obj instanceof Image) {
				((Image) obj).close();
			} else if (obj instanceof Pixmap) {
				((Pixmap) obj).close();
			} else if (obj instanceof BMFont) {
				((BMFont) obj).close();
			} else if (obj instanceof FontSheet) {
				((FontSheet) obj).close();
			} else if (obj instanceof MovieSpriteSheet) {
				((MovieSpriteSheet) obj).close();
			} else if (obj instanceof XMLDocument) {
				((XMLDocument) obj).close();
			} else if (obj instanceof Sound) {
				((Sound) obj).stop();
			}
		}
	}

	public void destroyRes(String name) {
		this.release(name);
	}

	private ResourceItem getResItem(String name, String type) {
		init();
		ResourceItem item = null;
		if (_resourceTable.containsKey(name)) {
			item = _resourceTable.get(name);
		} else {
			String url = name;
			item = new ResourceItem(name, type, url);
			_resourceTable.put(name, item);
		}
		return item;
	}

	@Override
	public void onReciveEvent(int type, EventDispatcher dispatcher, Object data) {
		this.dispatchEvent(type, data);
	}

}
