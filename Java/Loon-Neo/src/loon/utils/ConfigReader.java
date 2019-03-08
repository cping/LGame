package loon.utils;

import java.util.StringTokenizer;

import loon.BaseIO;
import loon.LSystem;
import loon.action.avg.drama.Expression;
import loon.action.map.Field2D;

/**
 * 一个简单的多文本数据存储及读取用类,作用类似于ini文件
 * 
 * 存储格式为:
 * begin name = "key1"
 * value1
 * end
 * begin name = "key2"
 * value2
 * end
 * key3 = "567"
 * key4 = "780"
 */
public class ConfigReader implements Expression, Bundle<String> {

	private String FLAG_L_TAG = "//";

	private String FLAG_C_TAG = "#";

	private String FLAG_I_TAG = "'";

	private final static ObjectMap<String, ConfigReader> pConfigReaders = new ObjectMap<String, ConfigReader>();

	public static ConfigReader getInstance(final String path) {
		synchronized (pConfigReaders) {
			ConfigReader reader = pConfigReaders.get(path);
			if (reader == null || reader.isClose) {
				reader = new ConfigReader(path);
				pConfigReaders.put(path, reader);
			}
			return reader;
		}
	}

	private final ObjectMap<String, String> pConfigItems = new ObjectMap<String, String>();

	private StringBuffer values = new StringBuffer();

	private boolean isClose;

	public ObjectMap<String, String> getContent() {
		return new ObjectMap<String, String>(pConfigItems);
	}

	public ConfigReader(final String resName) {
		parse(BaseIO.loadText(resName));
	}

	public void parse(final String text) {
		StringTokenizer reader = new StringTokenizer(text, LSystem.NL);
		String record = null;
		StringBuffer mapBuffer = new StringBuffer();
		boolean mapFlag = false;
		String mapName = null;
		for (; reader.hasMoreTokens();) {
			record = reader.nextToken().trim();
			if (record.length() > 0 && !record.startsWith(FLAG_L_TAG) && !record.startsWith(FLAG_C_TAG)
					&& !record.startsWith(FLAG_I_TAG)) {
				if (record.startsWith("begin")) {
					mapBuffer.delete(0, mapBuffer.length());
					String mes = record.substring(5, record.length()).trim();
					if (mes.startsWith("name")) {
						mapName = loadItem(mes, false);
					}
					mapFlag = true;
				} else if (record.startsWith("end")) {
					mapFlag = false;
					if (mapName != null) {
						pConfigItems.put(mapName, mapBuffer.toString());
					}
				} else if (mapFlag) {
					mapBuffer.append(record);
				} else {
					loadItem(record, true);
				}
			}
		}
	}

	private final String loadItem(final String mes, final boolean save) {
		char[] chars = mes.toCharArray();
		int size = chars.length;
		StringBuffer sbr = values.delete(0, values.length());
		String key = null;
		String value = null;
		int idx = 0;
		int equals = 0;
		for (int i = 0; i < size; i++) {
			char flag = chars[i];
			switch (flag) {
			case '=':
				if (equals < 3) {
					equals++;
					if (idx == 0) {
						key = sbr.toString();
						sbr.delete(0, sbr.length());
					}
					idx++;
				}
				break;
			case '\'':
				if (equals > 1) {
					sbr.append(flag);
				}
				break;
			case '\"':
				equals++;
				break;
			default:
				sbr.append(flag);
				break;
			}
		}
		if (key != null) {
			value = sbr.toString();
			if (save) {
				pConfigItems.put(key.trim(), value.trim());
			}
		}
		return value.trim();
	}

	public void putItem(String key, String value) {
		synchronized (pConfigItems) {
			pConfigItems.put(key, value);
		}
	}

	public void removeItem(String key) {
		synchronized (pConfigItems) {
			pConfigItems.remove(key);
		}
	}

	public boolean getBoolValue(String name) {
		return getBoolValue(name, false);
	}

	public boolean getBoolValue(String name, boolean fallback) {
		String v = null;
		synchronized (pConfigItems) {
			v = pConfigItems.get(name);
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
		String v = null;
		synchronized (pConfigItems) {
			v = pConfigItems.get(name);
		}
		if (v == null) {
			return fallback;
		}
		return Integer.parseInt(v);
	}

	public float getFloatValue(String name) {
		return getFloatValue(name, 0f);
	}

	public float getFloatValue(String name, float fallback) {
		String v = null;
		synchronized (pConfigItems) {
			v = pConfigItems.get(name);
		}
		if (v == null) {
			return fallback;
		}
		return Float.parseFloat(v);
	}

	public String getValue(String name) {
		return getValue(name, null);
	}

	public String getValue(String name, String fallback) {
		String v = null;
		synchronized (pConfigItems) {
			v = pConfigItems.get(name);
		}
		if (v == null) {
			return fallback;
		}
		return v;
	}

	public String get(String name) {
		return getValue(name, null);
	}

	@Override
	public void put(String key, String value) {
		putItem(key, value);
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
		String v = null;
		synchronized (pConfigItems) {
			v = pConfigItems.get(name);
		}
		if (v != null) {
			boolean pFlag = false;
			char[] chars = v.toCharArray();
			int size = chars.length;
			StringBuffer sbr = new StringBuffer(128);
			TArray<int[]> records = new TArray<int[]>(CollectionUtils.INITIAL_CAPACITY);
			for (int i = 0; i < size; i++) {
				char pValue = chars[i];
				switch (pValue) {
				case '{':
					pFlag = true;
					break;
				case '}':
					pFlag = false;
					String row = sbr.toString();
					String[] strings = row.split(",");
					int length = strings.length;
					int[] arrays = new int[length];
					for (int j = 0; j < length; j++) {
						arrays[j] = Integer.parseInt(strings[j]);
					}
					records.add(arrays);
					sbr.delete(0, sbr.length());
					break;
				case ' ':
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

	public boolean isClose() {
		return isClose;
	}

	public void dispose() {
		isClose = true;
		if (pConfigItems != null) {
			pConfigItems.clear();
		}
	}

}
