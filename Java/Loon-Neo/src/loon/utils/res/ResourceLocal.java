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

	private ObjectMap<String, Object> _dataTable;

	private boolean _initFlag = false;

	private String _path = null;

	public ResourceLocal(String path) {
		this();
		_path = path;
	}

	public ResourceLocal() {
		_resourceTable = new ObjectMap<String, ResourceItem>();
		_dataTable = new ObjectMap<String, Object>();
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

	public TArray<String> getGroupKeys(String name) {
		return _groupTable.get(name);
	}

	public Keys<String> getGroupNames(String name) {
		return _groupTable.keys();
	}

	@Override
	public XMLDocument getXml(String name) {
		init();
		XMLDocument obj = null;
		ResourceItem item = getResItem(name, ResourceType.TYPE_XML);
		if (false == _dataTable.containsKey(name)) {
			String url = item.url();
			obj = XMLParser.parse(url);
			_dataTable.put(name, obj);
		} else {
			obj = (XMLDocument) _dataTable.get(name);
		}
		return obj;
	}

	@Override
	public Json.Object getJson(String name) {
		init();
		Json.Object obj = null;
		ResourceItem item = getResItem(name, ResourceType.TYPE_JSON);
		if (false == _dataTable.containsKey(name)) {
			String url = item.url();
			obj = LSystem.base().json().parse(BaseIO.loadText(url));
			_dataTable.put(name, obj);
		} else {
			obj = (Json.Object) _dataTable.get(name);
		}
		return obj;
	}

	@Override
	public Image getImage(String name) {
		init();
		Image image = null;
		ResourceItem item = getResItem(name, ResourceType.TYPE_IMAGE);
		if (false == _dataTable.containsKey(name)) {
			String url = item.url();
			image = Image.createImage(url);
			_dataTable.put(name, image);
		} else {
			image = (Image) _dataTable.get(name);
		}
		return image;
	}

	@Override
	public Pixmap getPixmap(String name) {
		init();
		Pixmap pixmap = null;
		ResourceItem item = getResItem(name, ResourceType.TYPE_PIXMAP);
		if (false == _dataTable.containsKey(name)) {
			String url = item.url();
			Image img = Image.createImage(url);
			pixmap = img.getPixmap();
			_dataTable.put(name, pixmap);
			img.close();
			img = null;
		} else {
			pixmap = (Pixmap) _dataTable.get(name);
		}
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
		if (false == _dataTable.containsKey(name)) {
			tex = new Texture(item.url());
			_dataTable.put(name, tex);

		} else {
			tex = (Texture) _dataTable.get(name);
		}
		return tex;
	}

	@Override
	public MovieSpriteSheet getSheet(String name) {
		init();
		MovieSpriteSheet sset = null;
		ResourceItem item = getResItem(name, ResourceType.TYPE_SHEET);
		if (false == _dataTable.containsKey(name)) {
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
			_dataTable.put(name, sset);
		} else {
			sset = (MovieSpriteSheet) _dataTable.get(name);
		}
		return sset;
	}

	@Override
	public BMFont getBMFont(String name) {
		init();
		BMFont fs = null;
		ResourceItem item = getResItem(name, ResourceType.TYPE_BMFNT);
		if (false == _dataTable.containsKey(name)) {
			try {
				fs = new BMFont(item.url());
			} catch (Exception e) {
				LSystem.error("BMFont " + item.url() + " not found!", e);
			}
			_dataTable.put(name, fs);
		} else {
			fs = (BMFont) _dataTable.get(name);
		}
		return fs;
	}

	@Override
	public FontSheet getFontSheet(String name) {
		init();
		FontSheet fs = null;
		ResourceItem item = getResItem(name, ResourceType.TYPE_FONT);
		if (false == _dataTable.containsKey(name)) {
			fs = new FontSheet(item.url());
			_dataTable.put(name, fs);
		} else {
			fs = (FontSheet) _dataTable.get(name);
		}
		return fs;
	}

	public Sound getSound(String name, String type) {
		init();
		Sound sound = null;
		ResourceItem item = getResItem(name, ResourceType.TYPE_SOUND);
		if (_dataTable.containsKey(name)) {
			sound = LSystem.base().assets().getSound(item.url());
		} else {
			sound = (Sound) _dataTable.get(name);
		}
		return sound;
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
