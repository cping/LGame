package loon.utils;

import java.util.Iterator;

import loon.BaseIO;
import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.action.avg.drama.Expression;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.utils.ObjectMap.Keys;
import loon.utils.ObjectMap.Values;
import loon.utils.parse.StrTokenizer;
import loon.utils.res.TextResource;

/**
 * 一个简单的多文本数据存储及读取用类,作用类似于ini文件
 * <p>
 * 
 * 用它可以进行一些简单的键值对(key-value)模式数据设置,不想使用xml或json配置时的帮手
 * <p>
 * 
 * 完整存储格式为(可部分使用):
 * 
 * <pre>
 * test = "abc"
 * //设定子元素
 * [abc]
 * //获得子元素数值 get(abc.test)
 * test = "efg"
 * 
 * //($后是数据段名称，下面是数据，两个一组，换行符开始下一组，或者再次出现$也算一组新数据)
 * $bigdata1
 * begin name = "key1" 
 * value1 
 * end 
 * begin name = "key2" 
 * value2 
 * end 
 * key3 ="567" 
 * key4 = "780"
 * $bigdata2
 * ......
 * </pre>
 * 
 * or
 * 
 * <pre>
 * String test = "key1=ds;key2=12,43,56,67;key3=fs";
 * ConfigReader config = ConfigReader.parse(test);
 * System.out.println(config.get("key1"));
 * System.out.println(config.getFloatValues("key2")[1]);
 * </pre>
 */
public final class ConfigReader implements Expression, Bundle<String>, LRelease {

	private final static ObjectMap<String, ConfigReader> CONFIG_CACHE = new ObjectMap<String, ConfigReader>();

	public final static ConfigReader from(final String context) {
		return parse(context);
	}

	public final static ConfigReader parse(final String context) {
		final String defaultName = LSystem.getSystemAppName() + "_configpasertemp_" + CRC32.toHexString(context);
		synchronized (ConfigReader.class) {
			ConfigReader config = CONFIG_CACHE.get(defaultName);
			if (config == null || config._closed) {
				config = create();
				CONFIG_CACHE.put(defaultName, config);
			}
			config.parseMapContext(context);
			return config;
		}
	}

	public final static ConfigReader at(final String path) {
		return shared(path);
	}

	public final static ConfigReader file(final String path) {
		return shared(path);
	}

	public final static ConfigReader shared(final String path) {
		synchronized (ConfigReader.class) {
			ConfigReader reader = CONFIG_CACHE.get(path);
			if (reader == null || reader._closed) {
				reader = create(path);
				CONFIG_CACHE.put(filter(path), reader);
			}
			return reader;
		}
	}

	public final static ConfigReader create(final String path) {
		return new ConfigReader(path);
	}

	public final static ConfigReader create() {
		return new ConfigReader();
	}

	private final static String BEGIN_FLAG = "begin";

	private final static String END_FLAG = "end";

	private final static String NAME_FLAG = "name";

	private final ObjectMap<String, String> _configItems = new ObjectMap<String, String>();

	private final TArray<String> _tables = new TArray<String>();

	private final StrBuilder _template_values = new StrBuilder();

	private final String _path;

	private TArray<StringKeyValue> _bigContexts;

	private boolean _closed;

	ConfigReader() {
		this._path = LSystem.UNKNOWN;
	}

	public ConfigReader(final String resName) {
		if (StringUtils.isEmpty(resName)) {
			throw new LSysException("Resource path cannot be Empty!");
		}
		this._path = resName;
		this.parseMap(resName);
	}

	public ObjectMap<String, String> getContent() {
		return new ObjectMap<String, String>(_configItems);
	}

	public void parseMapContext(final String context) {
		if (StringUtils.isNullOrEmpty(context)) {
			throw new LSysException("The Resource context cannot be Empty !");
		}
		if (_bigContexts == null) {
			_bigContexts = new TArray<StringKeyValue>();
		} else {
			_bigContexts.clear();
		}
		if (_configItems != null) {
			_configItems.clear();
		}
		if (_tables != null) {
			_tables.clear();
		}
		if (_template_values != null) {
			_template_values.clear();
		}
		final StrTokenizer reader = new StrTokenizer(context, LSystem.NL + LSystem.BRANCH);
		String curTemplate = LSystem.EMPTY;
		StringKeyValue curBuffer = null;
		String result = null;
		try {
			for (; reader.hasMoreTokens();) {
				result = filter(reader.nextToken());
				if (StringUtils.isEmpty(result)) {
					continue;
				}
				if (result.indexOf(LSystem.BACKSLASH) == 0) {
					continue;
				}
				if (result.charAt(0) == '$') {
					if (!curTemplate.equals(LSystem.EMPTY) && curBuffer != null) {
						_bigContexts.add(curBuffer);
					}
					curTemplate = filter(result.substring(1));
					curBuffer = new StringKeyValue(curTemplate);
				} else {
					if (curBuffer != null) {
						curBuffer.addValue(result);
						curBuffer.addValue(LSystem.LS);
					}
				}
			}
			if (!curTemplate.equals(LSystem.EMPTY) && curBuffer != null) {
				_bigContexts.add(curBuffer);
			}
		} catch (Throwable ex) {
			throw new LSysException(ex.getMessage(), ex);
		}
		if (_bigContexts != null && _bigContexts.size > 0) {
			loadMapKey(_bigContexts.get(0).getKey());
		} else {
			parseData(context);
		}
	}

	public void parseMap(final String path) {
		final String context = TextResource.get().loadText(path);
		if (StringUtils.isEmpty(context)) {
			throw new LSysException("The loaded data does not exist !");
		}
		parseMapContext(context);
	}

	public void loadMapKey(final String name) {
		if (_bigContexts != null) {
			for (StringKeyValue v : _bigContexts) {
				if (v != null && v.getKey().equals(name)) {
					parseData(v.getValue());
					return;
				}
			}
		}
	}

	private final static String filter(String v) {
		if (StringUtils.isEmpty(v)) {
			return LSystem.EMPTY;
		}
		return v.trim();
	}

	public void parseData(final String text) {
		_configItems.clear();
		if (StringUtils.isEmpty(text)) {
			return;
		}
		StrTokenizer reader = new StrTokenizer(text, LSystem.NL + LSystem.BRANCH);
		String record = null;
		StrBuilder mapBuffer = new StrBuilder();
		boolean mapFlag = false;
		String mapName = null;
		String itemName = null;
		for (; reader.hasMoreTokens();) {
			record = filter(reader.nextToken());
			final int size = record.length();
			if (size > 0 && !record.startsWith(FLAG_L_TAG) && !record.startsWith(FLAG_C_TAG)
					&& !record.startsWith(FLAG_I_TAG)) {
				final int start = record.indexOf(LSystem.BRACKET_START);
				final int end = record.lastIndexOf(LSystem.BRACKET_END);
				if (start == 0 && end == size - 1) {
					itemName = record.substring(1, size - 1);
					putTable(itemName);
				} else if (StringUtils.isEmpty(itemName) && start == 0) {
					itemName = record.substring(1, size);
				} else if (!StringUtils.isEmpty(itemName) && end == size - 1) {
					itemName += record.substring(0, size - 1);
					putTable(itemName);
				} else if (record.startsWith(BEGIN_FLAG)) {
					mapBuffer.setLength(0);
					String mes = filter(record.substring(BEGIN_FLAG.length(), size));
					if (mes.startsWith(NAME_FLAG)) {
						mapName = loadItem(itemName, mes, false);
					}
					mapFlag = true;
				} else if (record.startsWith(END_FLAG)) {
					mapFlag = false;
					if (mapName != null) {
						_configItems.put(StringUtils.isEmpty(itemName) ? filter(mapName)
								: filter(itemName + LSystem.DOT + mapName), filter(mapBuffer.toString()));
					}
				} else if (mapFlag) {
					mapBuffer.append(record).append(LSystem.LF);
				} else {
					loadItem(itemName, record, true);
				}
			}
		}
	}

	protected final void putTable(final String itemName) {
		if (!_tables.contains(itemName) && !StringUtils.isEmpty(itemName)) {
			_tables.add(itemName);
		}
	}

	private final String loadItem(final String itemName, final String mes, final boolean save) {
		if (StringUtils.isEmpty(mes)) {
			return LSystem.EMPTY;
		}
		final char[] chars = mes.toCharArray();
		final int size = chars.length;
		final StrBuilder sbr = _template_values.setLength(0);
		String key = itemName;
		String vl = null;
		int idx = 0;
		int equals = 0;
		for (int i = 0; i < size; i++) {
			char flag = chars[i];
			switch (flag) {
			case LSystem.EQUAL:
				if (equals < 3) {
					equals++;
					if (idx == 0) {
						if (StringUtils.isEmpty(itemName)) {
							key = sbr.toString();
						} else {
							key = itemName + LSystem.DOT + sbr.toString();
						}
						sbr.setLength(0);
					}
					idx++;
				}
				break;
			case LSystem.SINGLE_QUOTE:
				if (equals > 1) {
					sbr.append(flag);
				}
				break;
			case LSystem.DOUBLE_QUOTES:
				equals++;
				break;
			default:
				sbr.append(flag);
				break;
			}
		}
		if (key != null) {
			vl = sbr.toString();
			if (save) {
				_configItems.put(filter(key), filter(vl));
			}
		}
		return filter(vl);
	}

	public String putItem(String key, String vl) {
		if (StringUtils.isEmpty(key) || StringUtils.isEmpty(vl)) {
			return null;
		}
		synchronized (_configItems) {
			return _configItems.put(filter(key), filter(vl));
		}
	}

	public String removeItem(String key) {
		if (StringUtils.isEmpty(key)) {
			return null;
		}
		synchronized (_configItems) {
			return _configItems.remove(key);
		}
	}

	public int getTableCount() {
		return _tables.size;
	}

	public TArray<String> getTables() {
		return new TArray<String>(_tables);
	}

	public TArray<String> getChildKeys(String key) {
		final TArray<String> list = new TArray<String>();
		for (Iterator<String> it = _configItems.keys(); it.hasNext();) {
			String name = it.next();
			if (name != null && name.startsWith(key + LSystem.DOT)) {
				list.add(name);
			}
		}
		return list;
	}

	public TArray<String> getChildValues(String key) {
		final TArray<String> list = new TArray<String>();
		for (Iterator<String> it = _configItems.keys(); it.hasNext();) {
			String name = it.next();
			if (name != null && name.startsWith(key + LSystem.DOT)) {
				list.add(_configItems.get(name));
			}
		}
		return list;
	}

	public Keys<String> getKeys() {
		return _configItems.keys();
	}

	public Values<String> getValues() {
		return _configItems.values();
	}

	public boolean getBoolValue(String name) {
		return getBoolValue(name, false);
	}

	public boolean getBoolValue(String name, boolean fallback) {
		if (StringUtils.isEmpty(name)) {
			return fallback;
		}
		String v = null;
		synchronized (_configItems) {
			v = _configItems.get(filter(name));
		}
		if (v == null) {
			return fallback;
		}
		return StringUtils.toBoolean(v);
	}

	public int getIntValue(String name) {
		return getIntValue(name, 0);
	}

	public int getIntValue(String name, int fallback) {
		if (StringUtils.isEmpty(name)) {
			return fallback;
		}
		String v = null;
		synchronized (_configItems) {
			v = _configItems.get(filter(name));
		}
		if (v == null) {
			return fallback;
		}
		return Integer.valueOf(v);
	}

	public float getFloatValue(String name) {
		return getFloatValue(name, 0f);
	}

	public float getFloatValue(String name, float fallback) {
		if (StringUtils.isEmpty(name)) {
			return fallback;
		}
		String v = null;
		synchronized (_configItems) {
			v = _configItems.get(filter(name));
		}
		if (v == null) {
			return fallback;
		}
		return Float.valueOf(v);
	}

	public float[] getFloatValues(String name) {
		return getFloatValues(name, null);
	}

	public float[] getFloatValues(String name, float[] fallback) {
		final String[] list = getValues(name);
		if (list != null) {
			final int size = list.length;
			final float[] v = new float[size];
			for (int i = 0; i < size; i++) {
				v[i] = Float.valueOf(list[i]);
			}
			return v;
		}
		return fallback;
	}

	public int[] getIntValues(String name) {
		return getIntValues(name, null);
	}

	public int[] getIntValues(String name, int[] fallback) {
		final String[] list = getValues(name);
		if (list != null) {
			final int size = list.length;
			final int[] v = new int[size];
			for (int i = 0; i < size; i++) {
				v[i] = Integer.valueOf(list[i]);
			}
			return v;
		}
		return fallback;
	}

	public LColor getColor(String name) {
		return getColor(name, LColor.white.cpy());
	}

	public LColor getColor(String name, LColor color) {
		String result = get(name);
		if (result != null) {
			return new LColor(result);
		} else {
			return color;
		}
	}

	public String[] getValues(String name) {
		return getValues(name, null);
	}

	public String[] getValues(String name, String[] fallback) {
		if (StringUtils.isEmpty(name)) {
			return fallback;
		}
		String[] list = null;
		synchronized (_configItems) {
			final String result = _configItems.get(filter(name));
			if (!StringUtils.isEmpty(result)) {
				list = StringUtils.split(result, LSystem.COMMA, LSystem.VERTICALLINE);
			}
		}
		if (list == null) {
			return fallback;
		}
		return list;
	}

	public String getValue(String name) {
		return getValue(name, null);
	}

	public String getValue(String name, String fallback) {
		if (StringUtils.isEmpty(name)) {
			return fallback;
		}
		String v = null;
		synchronized (_configItems) {
			v = _configItems.get(filter(name));
		}
		if (v == null) {
			v = fallback;
		}
		return v;
	}

	public Object getJson(String name, String fallback) {
		if (StringUtils.isEmpty(name)) {
			return BaseIO.loadJsonObjectContext(fallback);
		}
		String v = null;
		synchronized (_configItems) {
			v = _configItems.get(filter(name));
		}
		if (v == null) {
			v = fallback;
		}
		return BaseIO.loadJsonObjectContext(v);
	}

	@Override
	public String get(String name) {
		return getValue(name, null);
	}

	@Override
	public void put(String key, String vl) {
		putItem(key, vl);
	}

	@Override
	public String get(String key, String defaultValue) {
		return getValue(key, defaultValue);
	}

	@Override
	public String remove(String key) {
		String result = getValue(key);
		removeItem(key);
		return result;
	}

	@Override
	public String remove(String key, String defaultValue) {
		String result = getValue(key, defaultValue);
		removeItem(key);
		return result;
	}

	public String[] getNewlineList(String name) {
		return getNewlineList(name, LSystem.EMPTY);
	}

	public String[] getNewlineList(String name, String args) {
		final String result = getValue(name, args);
		return StringUtils.split(result, LSystem.LF);
	}

	public Field2D getField2D(String name, int width, int height) {
		return getField2D(name, width, height, null);
	}

	public Field2D getField2D(String name, int width, int height, Field2D fallback) {
		int[][] arrays = getArray2D(name, fallback == null ? null : fallback.getMap());
		if (arrays != null) {
			return new Field2D(arrays, width, height);
		}
		return null;
	}

	public int[][] getArray2D(String name) {
		return getArray2D(name, null);
	}

	public int[][] getArray2D(String name, int[][] fallback) {
		if (StringUtils.isEmpty(name)) {
			return fallback;
		}
		String v = null;
		synchronized (_configItems) {
			v = _configItems.get(filter(name));
		}
		if (v != null) {
			boolean pFlag = false;
			char[] chars = v.toCharArray();
			int size = chars.length;
			StrBuilder sbr = new StrBuilder(128);
			TArray<int[]> records = new TArray<int[]>(CollectionUtils.INITIAL_CAPACITY);
			for (int i = 0; i < size; i++) {
				char pValue = chars[i];
				switch (pValue) {
				case LSystem.DELIM_START:
					pFlag = true;
					break;
				case LSystem.DELIM_END:
					pFlag = false;
					String row = sbr.toString();
					String[] strings = StringUtils.split(row, LSystem.COMMA);
					int length = strings.length;
					int[] arrays = new int[length];
					for (int j = 0; j < length; j++) {
						arrays[j] = Integer.valueOf(strings[j]);
					}
					records.add(arrays);
					sbr.setLength(0);
					break;
				case LSystem.TAB:
				case LSystem.SPACE:
					break;
				default:
					if (pFlag) {
						sbr.append(pValue);
					}
					break;
				}
			}
			int col = records.size;
			int[][] result = new int[col][];
			for (int i = 0; i < col; i++) {
				result[i] = records.get(i);
			}
			return result;
		}
		return fallback;
	}

	public String getPath() {
		return _path;
	}

	@Override
	public int size() {
		return _configItems.size;
	}

	@Override
	public void clear() {
		_configItems.clear();
	}

	@Override
	public boolean isEmpty() {
		return _configItems.isEmpty();
	}

	@Override
	public boolean isNotEmpty() {
		return !isEmpty();
	}

	@Override
	public boolean hasKey(String key) {
		if (_configItems != null) {
			return _configItems.containsKey(filter(key));
		}
		return false;
	}

	public boolean isClosed() {
		return _closed;
	}

	public void dispose() {
		_closed = true;
		if (_bigContexts != null) {
			_bigContexts.clear();
		}
		if (_configItems != null) {
			_configItems.clear();
		}
	}

	@Override
	public void close() {
		dispose();
	}

}
