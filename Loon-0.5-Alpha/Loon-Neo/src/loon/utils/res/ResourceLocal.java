package loon.utils.res;

import loon.BaseIO;
import loon.Json;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.Sound;
import loon.canvas.Image;
import loon.event.EventDispatcher;
import loon.event.IEventListener;
import loon.utils.ObjectMap;
import loon.utils.StringUtils;

public class ResourceLocal extends ResourceGetter implements IEventListener {

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
	}

	private void init() {
		if (!_initFlag && _path != null) {
			init(_path);
			_initFlag = true;
		}
	}

	public void init(String path) {
		if (path != null) {
			_path = path;
			_initFlag = false;
			Json.Object jsonObj = LSystem.base().json()
					.parse(BaseIO.loadText(path));
			Json.Array resList = jsonObj.getArray("resources");
			for (int i = 0; i < resList.length(); i++) {
				Json.Object resItem = resList.getObject(i);
				String itemName = resItem.getString("name");
				String itemType = resItem.getString("type");
				String itemUrl = resItem.getString("url");
				ResourceItem item = new ResourceItem(itemName, itemType,
						itemUrl);
				if (itemType.equals("sheet")) {
					String subkeys = resItem.getString("subkeys");
					item.subkeys = subkeys;
				}
				_resourceTable.put(item.name(), item);
			}
		}
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
	public Texture getTexture(String name) {
		init();
		Texture tex = null;
		ResourceItem item = getResItem(name, ResourceType.TYPE_TEXTURE);
		if (false == _dataTable.containsKey(name)) {
			tex = new Texture(LTextures.loadTexture(item.url()));
			_dataTable.put(name, tex);

		} else {
			tex = (Texture) _dataTable.get(name);
		}
		return tex;
	}

	@Override
	public SpriteSheet getSheet(String name) {
		init();
		SpriteSheet sset = null;
		ResourceItem item = getResItem(name, ResourceType.TYPE_SHEET);
		if (false == _dataTable.containsKey(name)) {
			String url = item.url();
			Json.Object jsonObj = LSystem.base().json()
					.parse(BaseIO.loadText(url));
			String imagePath = url;
			LTexture sheet = LTextures.loadTexture(imagePath);
			sset = new SpriteSheet(jsonObj,
					StringUtils.split(item.subkeys, ','), sheet);
			_dataTable.put(name, sset);
		} else {
			sset = (SpriteSheet) _dataTable.get(name);
		}
		return sset;
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
		_dataTable.remove(name);
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
