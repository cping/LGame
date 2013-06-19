/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.core.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import loon.action.avg.drama.Expression;
import loon.action.map.Field2D;
import loon.core.LRelease;
import loon.core.LSystem;
import loon.utils.CollectionUtils;


//0.3.3新增类，用以充当配置器，其基本数据保存形式是键值对，即以类似ini文件的保存方式，
//通过key=value为变量赋值，不过也可以利用关键字begin同end之间的空隙保存大块数据。
public class ConfigReader implements Expression, LRelease {

	private final static HashMap<String, ConfigReader> pConfigReaders = new HashMap<String, ConfigReader>(
			CollectionUtils.INITIAL_CAPACITY);

	public static ConfigReader getInstance(String resName) {
		synchronized (pConfigReaders) {
			ConfigReader reader = pConfigReaders.get(resName);
			if (reader == null || reader.isClose) {
				try {
					reader = new ConfigReader(resName);
				} catch (IOException ex) {
					throw new RuntimeException(ex.getMessage());
				}
				pConfigReaders.put(resName, reader);
			}
			return reader;
		}
	}

	public static ConfigReader getInstance(final InputStream in) {
		try {
			return new ConfigReader(in);
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}

	private final HashMap<String, String> pConfigItems = new HashMap<String, String>(
			CollectionUtils.INITIAL_CAPACITY);

	private StringBuffer values = new StringBuffer();

	private boolean isClose;

	public HashMap<String, String> getContent() {
		return new HashMap<String, String>(pConfigItems);
	}

	public ConfigReader(final String resName) throws IOException {
		this(Resources.openResource(resName));
	}

	public ConfigReader(final InputStream in) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in,
					LSystem.encoding));
			String record = null;
			StringBuffer mapBuffer = new StringBuffer();
			boolean mapFlag = false;
			String mapName = null;
			for (; (record = reader.readLine()) != null;) {
				record = record.trim();
				if (record.length() > 0 && !record.startsWith(FLAG_L_TAG)
						&& !record.startsWith(FLAG_C_TAG)
						&& !record.startsWith(FLAG_I_TAG)) {
					if (record.startsWith("begin")) {
						mapBuffer.delete(0, mapBuffer.length());
						String mes = record.substring(5, record.length())
								.trim();
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
		} catch (Exception ex) {
			throw new IOException(ex.getMessage());
		} finally {
			LSystem.close(in);
			if (reader != null) {
				try {
					reader.close();
					reader = null;
				} catch (IOException e) {
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
		for (int i = 0; i < size; i++) {
			char flag = chars[i];
			switch (flag) {
			case '=':
				if (idx == 0) {
					key = sbr.toString();
					sbr.delete(0, sbr.length());
				}
				idx++;
				break;
			case '\'':
				break;
			case ' ':
				break;
			case '\"':
				break;
			default:
				sbr.append(flag);
				break;
			}
		}
		if (key != null) {
			value = sbr.toString();
			if (save) {
				pConfigItems.put(key, value);
			}
		}
		return value;
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

	public Field2D getField2D(String name, int width, int height) {
		return getField2D(name, width, height, null);
	}

	public Field2D getField2D(String name, int width, int height,
			Field2D fallback) {
		int[][] arrays = getArray2D(name,
				fallback == null ? null : fallback.getMap());
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
			ArrayList<int[]> records = new ArrayList<int[]>(
					CollectionUtils.INITIAL_CAPACITY);
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
			int col = records.size();
			int[][] result = new int[col][];
			for (int i = 0; i < col; i++) {
				result[i] = records.get(i);
			}
			return result;
		}
		return fallback;
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
		return "true".equalsIgnoreCase(v) || "yes".equalsIgnoreCase(v)
				|| "ok".equalsIgnoreCase(v);
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

	public boolean isClose() {
		return isClose;
	}

	@Override
	public void dispose() {
		isClose = true;
		if (pConfigItems != null) {
			pConfigItems.clear();
		}
	}

}
