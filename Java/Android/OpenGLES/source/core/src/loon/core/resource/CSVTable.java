package loon.core.resource;

import java.io.IOException;
import java.util.ArrayList;

import loon.utils.collection.ArrayMap;
import loon.utils.collection.ArrayMap.Entry;


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
public class CSVTable {

	public static class CSVItem extends ArrayMap {

		int index;

		public int getIndex() {
			return index;
		}

	}

	/**
	 * 载入CSV数据后转化为指定类的Object[]
	 * 
	 * @param fileName
	 * @param clazz
	 * @return
	 */
	final static public Object[] load(final String fileName,
			final Class<?> clazz) {
		Object[] obj = null;
		try {
			CSVItem[] properts = load(fileName);
			if (properts != null) {
				int size = properts.length - 1;
				obj = (Object[]) java.lang.reflect.Array.newInstance(clazz,
						size);
				for (int i = 0; i < size; i++) {
					CSVItem property = properts[i];
					if (property != null) {
						Entry[] entry = property.toEntrys();
						obj[i] = clazz.newInstance();
						for (int j = 0; j < entry.length; j++) {
							Entry e = entry[j];
							register(obj[i], (String) e.getKey(),
									(String) e.getValue());
						}
					}
				}
				properts = null;
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex + " " + fileName);
		}
		return obj;
	}

	/**
	 * 载入CSV数据到CSVItem数组
	 * 
	 * @param fileName
	 * @return
	 */
	final static public CSVItem[] load(final String fileName) {
		CSVItem[] items = null;
		try {
			CSVReader csv = new CSVReader(fileName);
			ArrayList<String> tables = csv.readLineAsList();
			int length = tables.size();
			if (length > 0) {
				items = new CSVItem[length];
				int count = 0;
				for (; csv.ready();) {
					items[count] = new CSVItem();
					String[] value = csv.readLineAsArray();
					for (int i = 0; i < length; i++) {
						items[count].put(tables.get(i), value[i]);
						items[count].index = i;
					}
					count++;
				}
			}
			if (csv != null) {
				csv.close();
				csv = null;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return items;
	}

	/**
	 * 注册指定对象中指定名称函数为指定数值
	 * 
	 * @param object
	 * @param beanProperty
	 * @param value
	 */
	private static void register(final Object object,
			final String beanProperty, final String value) {
		Object[] beanObject = bind(object.getClass(), beanProperty);
		Object[] cache = new Object[1];
		java.lang.reflect.Method getter = (java.lang.reflect.Method) beanObject[0];
		java.lang.reflect.Method setter = (java.lang.reflect.Method) beanObject[1];
		try {
			String methodType = getter.getReturnType().getName();
			if (methodType.equalsIgnoreCase("long")) {
				cache[0] = new Long(value);
				setter.invoke(object, cache);
			} else if (methodType.equalsIgnoreCase("int")
					|| methodType.equalsIgnoreCase("integer")) {
				cache[0] = new Integer(value);
				setter.invoke(object, cache);
			} else if (methodType.equalsIgnoreCase("short")) {
				cache[0] = new Short(value);
				setter.invoke(object, cache);
			} else if (methodType.equalsIgnoreCase("float")) {
				cache[0] = new Float(value);
				setter.invoke(object, cache);
			} else if (methodType.equalsIgnoreCase("double")) {
				cache[0] = new Double(value);
				setter.invoke(object, cache);
			} else if (methodType.equalsIgnoreCase("boolean")) {
				cache[0] = new Boolean(value);
				setter.invoke(object, cache);
			} else if (methodType.equalsIgnoreCase("java.lang.String")) {
				cache[0] = value;
				setter.invoke(object, cache);
			} else if (methodType.equalsIgnoreCase("java.io.InputStream")) {
			} else if (methodType.equalsIgnoreCase("char")) {
				cache[0] = (Character.valueOf(value.charAt(0)));
				setter.invoke(object, cache);
			}
		} catch (Exception ex) {
			throw new RuntimeException(beanProperty + " is " + ex.getMessage());
		}
	}

	/**
	 * 绑定指定类与方法
	 * 
	 * @param clazz
	 * @param beanProperty
	 * @return
	 */
	final static private Object[] bind(final Class<? extends Object> clazz,
			final String beanProperty) {
		Object[] result = new Object[2];
		byte[] array = beanProperty.toLowerCase().getBytes();
		array[0] = (byte) Character.toUpperCase((char) array[0]);
		String nowPropertyName = new String(array);
		String names[] = { ("set" + nowPropertyName).intern(),
				("get" + nowPropertyName).intern(),
				("is" + nowPropertyName).intern(),
				("write" + nowPropertyName).intern(),
				("read" + nowPropertyName).intern() };
		java.lang.reflect.Method getter = null;
		java.lang.reflect.Method setter = null;
		java.lang.reflect.Method methods[] = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			java.lang.reflect.Method method = methods[i];
			if (!java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
				continue;
			}
			String methodName = method.getName().intern();
			for (int j = 0; j < names.length; j++) {
				String name = names[j];
				if (!name.equals(methodName)) {
					continue;
				}
				if (methodName.startsWith("set")
						|| methodName.startsWith("read")) {
					setter = method;
				} else {
					getter = method;
				}
			}
		}
		result[0] = getter;
		result[1] = setter;
		return result;
	}
}
